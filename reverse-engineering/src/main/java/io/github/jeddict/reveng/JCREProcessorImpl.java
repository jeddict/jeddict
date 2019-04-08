/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.jeddict.reveng;

import java.util.Collections;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT_SUFFIX;
import static io.github.jeddict.jcode.util.ProjectHelper.findSourceGroupForFile;
import static io.github.jeddict.jcode.util.ProjectHelper.getFolderSourceGroup;
import io.github.jeddict.jpa.modeler.initializer.JPAFileActionListener;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.reveng.database.DBImportWizardDescriptor;
import io.github.jeddict.reveng.database.GenerateTablesImpl;
import io.github.jeddict.reveng.database.ImportHelper;
import io.github.jeddict.reveng.doc.DocOptions;
import io.github.jeddict.reveng.doc.DocWizardDescriptor;
import io.github.jeddict.reveng.klass.ClassWizardDescriptor;
import static io.github.jeddict.reveng.settings.RevengPanel.isIncludeReferencedClasses;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import static java.util.Objects.isNull;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showInputDialog;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.DatabaseMetaDataTransfer.Table;
import static org.netbeans.api.java.classpath.ClassPath.EMPTY;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modules.dbschema.SchemaElement;
import org.netbeans.modules.j2ee.persistence.api.EntityClassScope;
import org.netbeans.modules.j2ee.persistence.entitygenerator.EntityMember;
import org.netbeans.modules.j2ee.persistence.wizard.EntityClosure;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.DBSchemaManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

/**
 *
 * @author jGauravGupta
 */
@ServiceProvider(service = JCREProcessor.class)
public class JCREProcessorImpl implements JCREProcessor {

    /**
     * Update the complete existing diagram with existing class
     *
     * @param modelerFile
     */
    @Override
    public void syncExistingDiagram(ModelerFile modelerFile) {
        Project project = modelerFile.getProject();
        ClassWizardDescriptor classWizardDescriptor = new ClassWizardDescriptor(project);
        try {
            EntityMappings entityMappings = (EntityMappings) modelerFile.getDefinitionElement();
            FileObject targetFilePath = modelerFile.getFileObject().getParent();
            FileObject sourcePackage = getFolderSourceGroup(targetFilePath).getRootFolder();
            String targetFileName = modelerFile.getFileObject().getName();
            Set<String> entities = entityMappings.getEntity()
                    .stream()
                    .map(JavaClass::getFQN)
                    .collect(toSet());

            EntityClassScope entityClassScope = EntityClassScope.getEntityClassScope(project.getProjectDirectory());
            EntityClosure entityClosure = EntityClosure.create(entityClassScope, project);
            entityClosure.setClosureEnabled(true);
            entityClosure.addEntities(entities);
            if (entityClosure.getSelectedEntities().size() > entities.size()) {
                showInputDialog("Entity closure have more entity");
            }

            FileObject backupFile = null;
            try {
                String backupFileName = targetFileName + "_backup";
                backupFile = FileUtil.copyFile(modelerFile.getFileObject(), targetFilePath, backupFileName);
            } catch (IOException ex) {
            }
            EntityMappings newEntityMappings = EntityMappings.getNewInstance(JPAModelerUtil.getModelerFileVersion());
            newEntityMappings.setGenerated();
            classWizardDescriptor.instantiateJCREProcess(
                    "Updating Design", newEntityMappings,
                    sourcePackage, entities,
                    targetFilePath, targetFileName,
                    isIncludeReferencedClasses(), false,
                    null
            );
            classWizardDescriptor.notifyBackupCreation(modelerFile, backupFile);
        } catch (IOException ex) {
            modelerFile.handleException(ex);
        }
    }

    /**
     * Drop classes in existing diagram
     *
     * @param modelerFile
     * @param javaClasses
     */
    @Override
    public void processDropedClasses(ModelerFile modelerFile, List<File> javaClasses) {
        if (!consent("java classes")) {
            return;
        }

        Project project = modelerFile.getProject();
        ClassWizardDescriptor classWizardDescriptor = new ClassWizardDescriptor(project);
        List<FileObject> fileObjects
                = javaClasses
                        .stream()
                        .map(FileUtil::toFileObject)
                        .collect(toList());

        Map<SourceGroup, Set<String>> sourceGroups = new HashMap<>();
        for (FileObject fileObject : fileObjects) {
            SourceGroup sourceGroup = findSourceGroupForFile(fileObject);
            if (isNull(sourceGroup)) {
                continue;
            }
            if (!sourceGroups.containsKey(sourceGroup)) {
                sourceGroups.put(sourceGroup, new HashSet<>());
            }
            String entity = fileObject.getPath()
                    .substring(sourceGroup.getRootFolder().getPath().length() + 1, fileObject.getPath().lastIndexOf(JAVA_EXT_SUFFIX))
                    .replace('/', '.');
            sourceGroups.get(sourceGroup).add(entity);
        }

        try {
            EntityMappings entityMappings = (EntityMappings) modelerFile.getModelerScene().getBaseElementSpec();
            int totalSource = sourceGroups.keySet().size();
            int sourceIndex = 0;
            for (SourceGroup sourceGroup : sourceGroups.keySet()) {
                int currentSource = ++sourceIndex;
                Set<String> entities = sourceGroups.get(sourceGroup);
                classWizardDescriptor.instantiateJCREProcess(
                        "Importing classes",
                        entityMappings,
                        sourceGroup.getRootFolder(), entities,
                        null, null,
                        isIncludeReferencedClasses(), false,
                        () -> {
                            if (totalSource == currentSource) {
                                modelerFile.save(true);
                                modelerFile.close();
                                JPAFileActionListener.open(modelerFile);
                            }
                        }
                );
            }
        } catch (IOException ex) {
            modelerFile.handleException(ex);
        }
    }


    @Override
    public void processDropedDocument(ModelerFile modelerFile, List<File> docFiles) {
        try {
            Project project = modelerFile.getProject();
            
            DocOptions docOptions = new DocOptions(docFiles.get(0).getName(), modelerFile.getFile().getName());
            docOptions.setVisible(true);
            if (docOptions.getDialogResult() != javax.swing.JOptionPane.OK_OPTION) {
                return;
            }

            DocWizardDescriptor docWizardDescriptor = new DocWizardDescriptor(
                    project,
                    docFiles.get(0).getAbsolutePath(),
                    docOptions.isJPASupport(),
                    docOptions.isJSONBSupport(),
                    docOptions.isJAXBSupport()
            );
            EntityMappings entityMappings = (EntityMappings) modelerFile.getModelerScene().getBaseElementSpec();
            docWizardDescriptor.instantiateProcess(
                    entityMappings,
                    () -> {
                        modelerFile.save(true);
                        modelerFile.close();
                        JPAFileActionListener.open(modelerFile);
                    });
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }



    @Override
    public void processDropedTables(ModelerFile modelerFile, List<String> tables, DatabaseConnection databaseConnection, Optional<JavaClass> javaClass) {
        try {
            if (!consent("db tables"
                    + (javaClass.isPresent() ? " in " + javaClass.get().getClazz() + " Class" : EMPTY))) {
                return;
            }

            EntityMappings entityMappings = (EntityMappings) modelerFile.getModelerScene().getBaseElementSpec();
            Project project = modelerFile.getProject();
            SourceGroup sourceGroup = findSourceGroupForFile(modelerFile.getFileObject());
            String packageName = "sample";
            DBSchemaManager dbschemaManager = new DBSchemaManager();

            SchemaElement schemaElement = null;
            GenerateTablesImpl genTables = new GenerateTablesImpl();
            for (String table : tables) {
                schemaElement = dbschemaManager.getSchemaElement(databaseConnection);
//              TableElement tableElement = schemaElement.getTable(DBIdentifier.create(table.getTableName()));

                genTables.addTable(
                        schemaElement.getCatalog().getName(),
                        schemaElement.getSchema().getName(),
                        table,
                        sourceGroup.getRootFolder(),
                        packageName,
                        EntityMember.makeClassName(table),
                        Collections.emptySet()
                );
            }

            DBImportWizardDescriptor descriptor = new DBImportWizardDescriptor();
            descriptor.initialize(project, entityMappings, javaClass);
            ImportHelper importHelper = descriptor.getHelper();
            importHelper.setLocation(sourceGroup);
            importHelper.setPackageName(packageName);
            importHelper.setTableSource(schemaElement, databaseConnection, "");
            importHelper.buildBeans(genTables);
            descriptor.instantiateProcess(() -> {
                modelerFile.save(true);
                modelerFile.close();
                JPAFileActionListener.open(modelerFile);
            });


        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static boolean consent(String param) {
        return YES_OPTION == showConfirmDialog(
                WindowManager.getDefault().getMainWindow(),
                String.format("Would You like to import %s ?", param),
                "Import file",
                YES_NO_OPTION
        );
    }

}
