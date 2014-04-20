/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.source.generator.adaptor.orm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.editor.BaseDocument;
import org.netbeans.jpa.modeler.spec.DefaultClass;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.modeler.task.ITaskSupervisor;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.compiler.CompilerConfig;
import org.netbeans.orm.converter.compiler.CompilerConfigManager;
import org.netbeans.orm.converter.compiler.InvalidDataException;
import org.netbeans.orm.converter.compiler.LifecycleListenerSnippet;
import org.netbeans.orm.converter.compiler.WritableSnippet;
import org.netbeans.orm.converter.generator.DefaultClassGenerator;
import org.netbeans.orm.converter.generator.EmbeddableGenerator;
import org.netbeans.orm.converter.generator.EmbeddableIdClassGenerator;
import org.netbeans.orm.converter.generator.EntityGenerator;
import org.netbeans.orm.converter.generator.LifecycleCallbackGenerator;
import org.netbeans.orm.converter.generator.PersistenceXMLGenerator;
import org.netbeans.orm.converter.generator.SuperClassGenerator;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ClassType;
import org.netbeans.orm.converter.util.ClassesRepository;
import org.netbeans.orm.converter.util.ORMConverterUtil;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author Gaurav Gupta
 */
public class ORM2Java {

//    private boolean generateSerializer = false;
    private String packageName = null;
    private ClassesRepository classesRepository = ClassesRepository.getInstance();
    private EntityMappings parsedEntityMappings = null;
    private File destDir;
    private ITaskSupervisor task;
    private Project project;
    private SourceGroup sourceGroup;

    public void generateSource(ITaskSupervisor task, Project project, SourceGroup sourceGroup, EntityMappings parsedEntityMappings) {
        try {
            this.task = task;
            this.project = project;
            this.sourceGroup = sourceGroup;
            destDir = FileUtil.toFile(sourceGroup.getRootFolder());

            this.parsedEntityMappings = parsedEntityMappings;// parser.parseContent(ormFile);
            this.packageName = parsedEntityMappings.getPackage();

            CompilerConfig compilerConfig = new CompilerConfig(packageName);

            CompilerConfigManager.getInstance().initialize(compilerConfig);

            classesRepository.clear();

//            for (ParsedEntity entity : parsedEntityMappings.getEntity()) {
//                if (entity.getIdClass() != null) {
//                    ParsedDefaultClass _class = new ParsedDefaultClass();
//                    ParsedIdClass idClass = entity.getIdClass();
//                    _class.setClazz(idClass.getClazz());
//                    for (ParsedId idSpec : entity.getAttributes().getId()) {
//                        ParsedDefaultAttribute attribute = new ParsedDefaultAttribute();
//                        attribute.setAttributeType(idSpec.getAttributeType());
//                        attribute.setName(idSpec.getName());
//                        _class.addAttribute(attribute);
//                    }
//                    parsedEntityMappings.addIdclass(_class);
//                }
//            }
//            for (ParsedMappedSuperclass mappedSuperclass : parsedEntityMappings.getMappedSuperclass()) {
//                if (mappedSuperclass.getIdClass() != null) {
//                    ParsedDefaultClass _class = new ParsedDefaultClass();
//                    ParsedIdClass idClass = mappedSuperclass.getIdClass();
//                    _class.setClazz(idClass.getClazz());
//                    for (ParsedId idSpec : mappedSuperclass.getAttributes().getId()) {
//                        ParsedDefaultAttribute attribute = new ParsedDefaultAttribute();
//                        attribute.setAttributeType(idSpec.getAttributeType());
//                        attribute.setName(idSpec.getName());
//                        _class.addAttribute(attribute);
//                    }
//                    parsedEntityMappings.addIdclass(_class);
//                }
//            }
            for (DefaultClass defaultClass : parsedEntityMappings.getDefaultClass()) {
                if (defaultClass.isEmbeddable()) {
                    generateEmbededIdClasses(defaultClass);
                } else {
                    generateIdClasses(defaultClass);
                }
            }
            generateSuperClasses();
            generateEntityClasses();
            generateEmbededClasses();
            generateLifeCycleClasses();

//            generateJavaSingleton();
            generatePersistenceXML();
//            generateSerializers();
//        } catch (ORMConvParserException ex) {
//            Logger.getLogger(ORM2Java.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidDataException ex) {
            Logger.getLogger(ORM2Java.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ORM2Java.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void generateEmbededClasses()
            throws InvalidDataException, IOException {

        List<Embeddable> parsedEmbeddables
                = parsedEntityMappings.getEmbeddable();

        for (Embeddable parsedEmbeddable : parsedEmbeddables) {
            task.log("Generating Embeddable Class : " + parsedEmbeddable.getClazz(), true);

            ClassDefSnippet classDef = new EmbeddableGenerator(
                    parsedEmbeddable, packageName).getClassDef();

            classesRepository.addWritableSnippet(
                    ClassType.EMBEDED_CLASS, classDef);

            writeSnippet(classDef);
        }
    }

    private void generateEntityClasses()
            throws InvalidDataException, IOException {

        List<Entity> parsedEntities = parsedEntityMappings.getEntity();

        for (Entity parsedEntity : parsedEntities) {
            task.log("Generating Entity Class : " + parsedEntity.getClazz(), true);
            ClassDefSnippet classDef = new EntityGenerator(
                    parsedEntity, packageName).getClassDef();

            classesRepository.addWritableSnippet(
                    ClassType.ENTITY_CLASS, classDef);

            writeSnippet(classDef);
        }
    }

//    //Generate A abstract class for global named queries,
//    //sqlresultsets etc
//    private void generateJavaSingleton()
//            throws InvalidDataException, IOException {
//
//        ORMConvSingletonGenerator singletonGenerator
//                = new ORMConvSingletonGenerator(parsedEntityMappings);
//
//        ClassDefSnippet abstractSingleton = singletonGenerator.getClassDef();
//
//        writeSnippet(abstractSingleton);
//
//        classesRepository.addWritableSnippet(
//                ClassType.JAVA_SINGLETON, abstractSingleton);
//    }
    private void generateLifeCycleClasses()
            throws InvalidDataException, IOException {

        List<ClassDefSnippet> classDefs = getPUXMLEntries();

        //Generate lifecycle events processors
        LifecycleCallbackGenerator callbackGenerator
                = new LifecycleCallbackGenerator(
                        parsedEntityMappings, classDefs, packageName);

        Collection<LifecycleListenerSnippet> lifecycleListeners
                = callbackGenerator.getLifecycleListeners();

        for (LifecycleListenerSnippet lifecycleListener : lifecycleListeners) {
            classesRepository.addWritableSnippet(
                    ClassType.LISTENER_CLASS, lifecycleListener);

            writeSnippet(lifecycleListener);
        }
    }

    private void generatePersistenceXML() {
        List<ClassDefSnippet> classDefs = getPUXMLEntries();

        //Generate persistence.xml
        PersistenceXMLGenerator persistenceXMLGenerator
                = new PersistenceXMLGenerator(classDefs);
        persistenceXMLGenerator.setPUName(parsedEntityMappings.getPersistenceUnitName());

        persistenceXMLGenerator.generatePersistenceXML(project, sourceGroup);

    }

//    private void generateSerializers()
//            throws InvalidDataException, IOException {
//
//        if (!generateSerializer) {
//            return;
//        }
//
//        List<WritableSnippet> entitiesClassDefs
//                = classesRepository.getWritableSnippets(
//                        ClassType.ENTITY_CLASS);
//
//        ClassDefSnippet javaSingleton = (ClassDefSnippet) classesRepository.getWritableSnippets(
//                ClassType.JAVA_SINGLETON).get(0);
//
//        entitiesClassDefs.add(javaSingleton);
//
//        for (WritableSnippet writableSnippet : entitiesClassDefs) {
//
//            ClassDefSnippet classDef = (ClassDefSnippet) writableSnippet;
//
//            SerializerGenerator serializerGenerator
//                    = new SerializerGenerator(classDef, packageName);
//
//            classesRepository.addWritableSnippet(
//                    ClassType.SERIALIZER_CLASS, serializerGenerator);
//
//            writeSnippet(serializerGenerator);
//
//            SerializerUtilGenerator serializerUtilGenerator
//                    = new SerializerUtilGenerator(classDef, packageName);
//
//            if (!isAlreadyGenerated(serializerUtilGenerator.getClassHelper())) {
//
//                classesRepository.addWritableSnippet(
//                        ClassType.SERIALIZER_UTIL_CLASS, serializerUtilGenerator);
//
//                writeSnippet(serializerUtilGenerator);
//            }
//        }
//    }
    private void generateEmbededIdClasses(DefaultClass defaultClass) throws InvalidDataException, IOException {
        task.log("Generating EmbeddedId Class : " + defaultClass.getClazz(), true);
        ClassDefSnippet classDef = new EmbeddableIdClassGenerator(defaultClass, packageName).getClassDef();
        classesRepository.addWritableSnippet(ClassType.EMBEDED_CLASS, classDef);
        writeSnippet(classDef);
    }

    private void generateIdClasses(DefaultClass defaultClass) throws InvalidDataException, IOException {
        task.log("Generating IdClass Class : " + defaultClass.getClazz(), true);
        ClassDefSnippet classDef = new DefaultClassGenerator(defaultClass, packageName).getClassDef();
        classesRepository.addWritableSnippet(ClassType.SERIALIZER_CLASS, classDef);
        writeSnippet(classDef);
    }

    private void generateSuperClasses()
            throws InvalidDataException, IOException {

        List<MappedSuperclass> parsedMappedSuperclasses
                = parsedEntityMappings.getMappedSuperclass();

        for (MappedSuperclass parsedMappedSuperclass : parsedMappedSuperclasses) {
            task.log("Generating MappedSuperclass Class : " + parsedMappedSuperclass.getClazz(), true);
            ClassDefSnippet classDef = new SuperClassGenerator(
                    parsedMappedSuperclass, packageName).getClassDef();

            classesRepository.addWritableSnippet(
                    ClassType.SUPER_CLASS, classDef);

            writeSnippet(classDef);
        }
    }

    private List<ClassDefSnippet> getPUXMLEntries() {

//        List<WritableSnippet> results = new ArrayList<WritableSnippet>();
        List<WritableSnippet> entitySnippets
                = classesRepository.getWritableSnippets(ClassType.ENTITY_CLASS);

//        List<WritableSnippet> superClassSnippets
//                = classesRepository.getWritableSnippets(ClassType.SUPER_CLASS);
//
//        List<WritableSnippet> embededSnippets
//                = classesRepository.getWritableSnippets(ClassType.EMBEDED_CLASS);
//        results.addAll(entitySnippets);
//        results.addAll(superClassSnippets);
//        results.addAll(embededSnippets);
        List<ClassDefSnippet> classDefs = new ArrayList<ClassDefSnippet>();

        for (WritableSnippet writableSnippet : entitySnippets) {
            classDefs.add((ClassDefSnippet) writableSnippet);
        }

        return classDefs;
    }

    private boolean isAlreadyGenerated(ClassHelper classHelper) {

        WritableSnippet writableSnippet = classesRepository.getWritableSnippet(
                classHelper);

        if (writableSnippet == null) {
            return false;
        }

        return true;
    }

    private void writeSnippet(WritableSnippet writableSnippet)
            throws InvalidDataException, IOException {

        String content = writableSnippet.getSnippet();

        File sourceFile = ORMConverterUtil.createFile(
                destDir.getAbsolutePath(),
                writableSnippet.getClassHelper().getSourcePath(),
                writableSnippet.getClassHelper().getClassNameWithSourceSuffix());

        ORMConverterUtil.writeContent(content, sourceFile);
        formatFile(sourceFile);

        System.out.println(
                "Java: Generated file :" + sourceFile.getAbsolutePath());
    }

    private void formatFile(File file) {
        final FileObject fo = FileUtil.toFileObject(file);
        try {
            DataObject dobj = DataObject.find(fo);
            EditorCookie ec = dobj.getLookup().lookup(EditorCookie.class);
            if (ec == null) {
                return;
            }
            ec.close();
            StyledDocument document = ec.getDocument();
            if (document instanceof BaseDocument) {
                final BaseDocument doc = (BaseDocument) document;
                final Reformat f = Reformat.get(doc);
                f.lock();
                try {
                    doc.runAtomic(new Runnable() {
                        public void run() {
                            try {
                                f.reformat(0, doc.getLength());
                            } catch (BadLocationException ex) {
                                Exceptions.attachMessage(ex, "Failure while formatting " + FileUtil.getFileDisplayName(fo));
                                Exceptions.printStackTrace(ex);
                            }

                        }
                    });
                } finally {
                    f.unlock();
                }
                try {
                    ec.saveDocument();
//                    SaveCookie save = dobj.getLookup().lookup(SaveCookie.class);
//                    if (save != null) {
//                        save.save();
//                    }
                } catch (IOException ex) {
                    Exceptions.attachMessage(ex, "Failure while formatting and saving " + FileUtil.getFileDisplayName(fo));
                    Exceptions.printStackTrace(ex);
                }
            }
        } catch (DataObjectNotFoundException ex) {
            Exceptions.attachMessage(ex, "Failure while formatting " + FileUtil.getFileDisplayName(fo));
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.attachMessage(ex, "Failure while formatting " + FileUtil.getFileDisplayName(fo));
            Exceptions.printStackTrace(ex);
        }
    }

}
