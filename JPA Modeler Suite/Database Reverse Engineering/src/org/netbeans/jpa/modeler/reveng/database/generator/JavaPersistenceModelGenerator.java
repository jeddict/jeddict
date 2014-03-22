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
package org.netbeans.jpa.modeler.reveng.database.generator;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.commons.io.FileUtils;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.Project;
import org.netbeans.jpa.modeler.reveng.database.ImportHelper;
import org.netbeans.jpa.modeler.spec.GenerationType;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.JoinTable;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.design.Diagram;
import org.netbeans.jpa.modeler.spec.design.Plane;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.specification.model.file.JPAFileDataObject;
import org.netbeans.jpa.modeler.specification.model.file.action.JPAFileActionListener;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelAction;
import org.netbeans.modules.j2ee.persistence.api.EntityClassScope;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.Entity;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.EntityMappingsMetadata;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.Table;
import org.netbeans.modules.j2ee.persistence.dd.PersistenceUtils;
import org.netbeans.modules.j2ee.persistence.entitygenerator.CMPMappingModel;
import org.netbeans.modules.j2ee.persistence.entitygenerator.CMPMappingModel.ColumnData;
import org.netbeans.modules.j2ee.persistence.entitygenerator.EntityClass;
import org.netbeans.modules.j2ee.persistence.entitygenerator.EntityMember;
import org.netbeans.modules.j2ee.persistence.entitygenerator.RelationshipRole;
import org.netbeans.modules.j2ee.persistence.util.MetadataModelReadHelper;
import org.netbeans.modules.j2ee.persistence.util.MetadataModelReadHelper.State;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.ProgressPanel;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public class JavaPersistenceModelGenerator implements IPersistenceModelGenerator {

    private final Map<String, String> entityName2TableName = new HashMap<String, String>();
    private Project initProject;

    private Set<FileObject> result;

    @Override
    public void generateModel(final ProgressPanel progressPanel,
            final ImportHelper helper,
            final FileObject dbSchemaFile,
            final ProgressContributor handle) throws IOException {
//        PUDataObject pudo;
//        try {
//            pudo = ProviderUtil.getPUDataObject(helper.getProject());
//            pudo.getPersistence().getPersistenceUnit();
//        } catch (InvalidPersistenceXmlException ex) {
//            Exceptions.printStackTrace(ex);
//        }

        generateModal(helper.getFileName(), helper.getBeans(),
                helper.isFullyQualifiedTableNames(), helper.isRegenTablesAttrs(),
                helper.isUseDefaults(),
                handle, progressPanel, helper.getProject());
    }

    private void generateModal(String fileName, EntityClass[] entityClasses,
            boolean fullyQualifiedTableNames, boolean regenTablesAttrs,
            boolean useDefaults,
            ProgressContributor progressContributor, ProgressPanel panel, Project prj) throws IOException {

        int progressMax = entityClasses.length * 3;
        progressContributor.start(progressMax);

        result = new Generator(fileName, entityClasses,
                fullyQualifiedTableNames, regenTablesAttrs,
                useDefaults,
                progressContributor, panel, this).run();
        progressContributor.progress(progressMax);

        PersistenceUtils.logUsage(JavaPersistenceModelGenerator.class, "USG_PERSISTENCE_ENTITY_DB_CREATED", new Integer[]{entityClasses.length});
    }

    @Override
    public void init(WizardDescriptor wiz) {
        // get the table names for all entities in the project
        initProject = Templates.getProject(wiz);
        final MetadataModelReadHelper<EntityMappingsMetadata, Set<Entity>> readHelper;
        EntityClassScope entityClassScope = EntityClassScope.getEntityClassScope(initProject.getProjectDirectory());
        if (entityClassScope == null) {
            return;
        }
        MetadataModel<EntityMappingsMetadata> entityMappingsModel = entityClassScope.getEntityMappingsModel(true);
        readHelper = MetadataModelReadHelper.create(entityMappingsModel, new MetadataModelAction<EntityMappingsMetadata, Set<Entity>>() {
            @Override
            public Set<Entity> run(EntityMappingsMetadata metadata) {
                Set<Entity> result = new HashSet<Entity>();
                result.addAll(Arrays.asList(metadata.getRoot().getEntity()));
                return result;
            }
        });

        readHelper.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (readHelper.getState() == State.FINISHED) {
                    try {
                        processEntities(readHelper.getResult());
                    } catch (ExecutionException ex) {
                        Logger.getLogger(JavaPersistenceModelGenerator.class.getName()).log(Level.FINE, "Failed to get entity classes: ", ex); //NOI18N
                    }
                }
            }
        });
        readHelper.start();
    }

    private void processEntities(Set<Entity> entityClasses) {
        for (Entity entity : entityClasses) {
            Table entityTable = entity.getTable();
            if (entityTable != null) {
                entityName2TableName.put(entityTable.getName(), entity.getClass2());
            }
        }
    }

    @Override
    public void uninit() {
        initProject = null;
    }

    @Override
    public String getFQClassName(String tableName) {
        return entityName2TableName.get(tableName);
    }

    @Override
    public String generateEntityName(String name) {
        return name;
    }

    @Override
    public Set<FileObject> createdObjects() {
        return result;
    }

    /**
     * Encapsulates the whole entity modal generation process.
     */
    private static final class Generator {

        private final ProgressPanel progressPanel;
        private final ProgressContributor progressContributor;
        private final Map<String, EntityClass> beanMap = new HashMap<String, EntityClass>();
        private final EntityClass[] entityClasses;
        private final boolean fullyQualifiedTableNames;
        private final boolean regenTablesAttrs;
        private final Set<FileObject> generatedEntityFOs;
        private final Set<FileObject> generatedFOs;
        private final boolean useDefaults;
        private final String fileName;

        public Generator(String fileName, EntityClass[] entityClasses,
                boolean fullyQualifiedTableNames, boolean regenTablesAttrs,
                boolean useDefaults,
                ProgressContributor progressContributor, ProgressPanel progressPanel,
                IPersistenceModelGenerator persistenceGen) {
            this.entityClasses = entityClasses;
            this.fullyQualifiedTableNames = fullyQualifiedTableNames;
            this.useDefaults = useDefaults;
            this.regenTablesAttrs = regenTablesAttrs;
            this.fileName = fileName;
            this.progressContributor = progressContributor;
            this.progressPanel = progressPanel;
            generatedFOs = new HashSet<FileObject>();
            generatedEntityFOs = new HashSet<FileObject>();
        }

        public Set<FileObject> run() throws IOException {
            try {
                runImpl();
            } catch (IOException e) {
                Logger.getLogger(JavaPersistenceModelGenerator.class.getName()).log(Level.INFO, "IOException, remove generated."); //NOI18N
                for (FileObject generatedFO : generatedFOs) {
                    generatedFO.delete();
                }
                throw e;
            } finally {
            }
            return generatedEntityFOs;
        }

        String getMemberType(EntityMember m) {
            String memberType = m.getMemberType();
            if ("java.sql.Date".equals(memberType)) { //NOI18N
                memberType = "java.util.Date";
            } else if ("java.sql.Time".equals(memberType)) { //NOI18N
                memberType = "java.util.Date";
            } else if ("java.sql.Timestamp".equals(memberType)) { //NOI18N
                memberType = "java.util.Date";
            }
            return memberType;
        }

        public void runImpl() throws IOException {

            final org.netbeans.jpa.modeler.spec.EntityMappings entityMappingsSpec = new org.netbeans.jpa.modeler.spec.EntityMappings();
            entityMappingsSpec.setId(NBModelerUtil.getAutoGeneratedStringId());
            entityMappingsSpec.setGenerated();
            Diagram diagram = new Diagram();
            diagram.setId(NBModelerUtil.getAutoGeneratedStringId());
            Plane plane = new Plane();
            plane.setId(NBModelerUtil.getAutoGeneratedStringId() + "_p");
            diagram.setJPAPlane(plane);
            plane.setElementRef(entityMappingsSpec.getId());
            entityMappingsSpec.setJPADiagram(diagram);

            // first generate empty entity modal -- this is needed as
            // in the field generation it will be necessary to resolve
            // their types (e.g. entity A has a field of type Collection<B>, thus
            // while generating entity A we must be able to resolve type B).
            beanMap.clear();
            for (int i = 0; i < entityClasses.length; i++) {
                final EntityClass entityClass = entityClasses[i];
                String entityClassName = entityClass.getClassName();
                beanMap.put(entityClassName, entityClass);

                /*-------JPA Modeler Start-------*/
                org.netbeans.jpa.modeler.spec.Entity entitySpec = new org.netbeans.jpa.modeler.spec.Entity();
                entitySpec.setId(NBModelerUtil.getAutoGeneratedStringId());
                entitySpec.setClazz(entityClassName);
                entityMappingsSpec.addEntity(entitySpec);
//                entitySpecMap.put(entityClassName, entitySpec);
                /*--------JPA Modeler End--------*/

                String progressMsg = NbBundle.getMessage(JavaPersistenceModelGenerator.class, "TXT_GeneratingClass", entityClassName);
                progressContributor.progress(progressMsg, i);
                if (progressPanel != null) {
                    progressPanel.setText(progressMsg);
                }
            }

            for (int i = 0; i < entityClasses.length; i++) {
                final EntityClass entityClass = entityClasses[i];
                String entityClassName = entityClass.getClassName();

                String progressMsg = NbBundle.getMessage(JavaPersistenceModelGenerator.class, "TXT_GeneratingClass", entityClassName);
                progressContributor.progress(progressMsg, 2 * entityClasses.length + i);
                if (progressPanel != null) {
                    progressPanel.setText(progressMsg);
                }
                EntityModalGenerator entityModalGenerator = new EntityModalGenerator(entityMappingsSpec, entityClass);
                entityModalGenerator.run();
            }

            for (org.netbeans.jpa.modeler.spec.Entity entity : entityMappingsSpec.getEntity()) {
                for (ManyToMany manyToMany : new ArrayList<ManyToMany>(entity.getAttributes().getManyToMany())) {
                    if (manyToMany.getMappedBy() == null) {
                        entityMappingsSpec.manageSiblingAttribute(entity, manyToMany);
                    }
                }
                for (OneToMany oneToMany : new ArrayList<OneToMany>(entity.getAttributes().getOneToMany())) {
                    if (oneToMany.getMappedBy() == null) {
                        entityMappingsSpec.manageSiblingAttribute(entity, oneToMany);
                    }
                }
                for (ManyToOne manyToOne : new ArrayList<ManyToOne>(entity.getAttributes().getManyToOne())) {
                    entityMappingsSpec.manageSiblingAttribute(entity, manyToOne);
                }
                for (OneToOne oneToOne : new ArrayList<OneToOne>(entity.getAttributes().getOneToOne())) {
                    if (oneToOne.getMappedBy() == null) {
                        entityMappingsSpec.manageSiblingAttribute(entity, oneToOne);
                    }
                }
            }
            // manageSiblingAttribute for MappedSuperClass and Embeddable is not required because it not generated DBRE CASE

            FileObject parentFileObject = entityClasses[0].getPackageFileObject();
            File jpaFile = new File(parentFileObject.getPath() + File.separator + getFileName(fileName, null, parentFileObject) + ".jpa");
            if (!jpaFile.exists()) {
                jpaFile.createNewFile();
            }

            JAXBContext context;
            try {
                context = JAXBContext.newInstance(new Class<?>[]{org.netbeans.jpa.modeler.spec.EntityMappings.class});
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://java.sun.com/xml/ns/persistence/orm orm_2_1.xsd");

                StringWriter sw = new StringWriter();
                marshaller.marshal(entityMappingsSpec, sw);
                FileUtils.writeStringToFile(jpaFile, sw.toString());
                FileObject jpaFileObject = FileUtil.toFileObject(jpaFile);
                JPAFileActionListener actionListener = new JPAFileActionListener((JPAFileDataObject) DataObject.find(jpaFileObject));
                actionListener.actionPerformed(null);

            } catch (JAXBException ex) {
                Exceptions.printStackTrace(ex);
            }

        }

        private String getFileName(String fileName, Integer index, FileObject parentFileObject) {
            File jpaFile;
            if (index == null) {
                jpaFile = new File(parentFileObject.getPath() + File.separator + fileName + ".jpa");
            } else {
                jpaFile = new File(parentFileObject.getPath() + File.separator + fileName + index + ".jpa");
            }
            if (jpaFile.exists()) {
                if (index == null) {
                    index = 0;
                }
                return getFileName(fileName, ++index, parentFileObject);
            } else {
                if (index == null) {
                    return fileName;
                } else {
                    return fileName + index;
                }

            }
        }

        private abstract class ModalGenerator {

            // the entity modal we are generating
            protected final EntityClass entityClass;
            // the mapping of the entity class to the database
            protected final CMPMappingModel dbMappings;
            private final org.netbeans.jpa.modeler.spec.EntityMappings entityMappings;

            public ModalGenerator(org.netbeans.jpa.modeler.spec.EntityMappings entityMappings, EntityClass entityClass) throws IOException {
                this.entityMappings = entityMappings;
                this.entityClass = entityClass;
                dbMappings = entityClass.getCMPMapping();
//                needsPKClass = entityClass.isForTable() && !entityClass.isUsePkField();
//                pkClassName = needsPKClass ? createPKClassName(entityClass.getClassName()) : null;
//                pkFQClassName = entityClass.getPackage() + "." + pkClassName;
            }

            protected String createFieldName(String capitalizedFieldName) {
                return createFieldNameImpl(capitalizedFieldName, false);
            }

            protected String createCapitalizedFieldName(String fieldName) {
                return createFieldNameImpl(fieldName, true);
            }

            private String createFieldNameImpl(String fieldName, boolean capitalized) {
                StringBuilder sb = new StringBuilder(fieldName);
                char firstChar = sb.charAt(0);
                sb.setCharAt(0, capitalized ? Character.toUpperCase(firstChar) : Character.toLowerCase(firstChar));
                return sb.toString();
            }

            String getMemberType(EntityMember m) {
                String memberType = m.getMemberType();
                if ("java.sql.Date".equals(memberType)) { //NOI18N
                    memberType = "java.util.Date";
                } else if ("java.sql.Time".equals(memberType)) { //NOI18N
                    memberType = "java.util.Date";
                } else if ("java.sql.Timestamp".equals(memberType)) { //NOI18N
                    memberType = "java.util.Date";
                }
                return memberType;
            }

            protected boolean isCharacterType(String type) {
                if ("java.lang.String".equals(type)) { // NOI18N
                    // XXX also need to check for char[] and Character[]
                    // (better to use TypeMirror)
                    return true;
                }
                return false;
            }

            protected boolean isDecimalType(String type) {
                if ("java.lang.Double".equals(type) || // NOI18N
                        "java.lang.Float".equals(type) || // NOI18N
                        "java.math.BigDecimal".equals(type)) { // NOI18N
                    return true;
                }
                return false;
            }

            protected String getMemberTemporalType(EntityMember m) {
                String memberType = m.getMemberType();
                String temporalType = null;
                if ("java.sql.Date".equals(memberType)) { //NOI18N
                    temporalType = "DATE";
                } else if ("java.sql.Time".equals(memberType)) { //NOI18N
                    temporalType = "TIME";
                } else if ("java.sql.Timestamp".equals(memberType)) { //NOI18N
                    temporalType = "TIMESTAMP";
                }
                return temporalType;
            }

            public void run() throws IOException {
                initialize();
                org.netbeans.jpa.modeler.spec.Attributes attributes = new org.netbeans.jpa.modeler.spec.Attributes();
                this.getEntityMappings().findEntity(entityClass.getClassName()).setAttributes(attributes);
                for (Object object : entityClass.getFields()) {
                    generateMember(attributes, (EntityMember) object);
                }
                for (RelationshipRole roleObject : entityClass.getRoles()) {
                    generateRelationship(attributes, roleObject);
                }
                finish();

            }

            /**
             * Called at the beginning of the generation process.
             */
            protected abstract void initialize() throws IOException;

            /**
             * Called for each entity class member.
             */
            protected abstract void generateMember(org.netbeans.jpa.modeler.spec.Attributes attributes, EntityMember m) throws IOException;

            /**
             * Called for each relationship.
             */
            protected abstract void generateRelationship(org.netbeans.jpa.modeler.spec.Attributes attributes, RelationshipRole role) throws IOException;

            /**
             * Called at the end of the generation process.
             */
            protected abstract void finish() throws IOException;

            /**
             * @return the entityMappings
             */
            public org.netbeans.jpa.modeler.spec.EntityMappings getEntityMappings() {
                return entityMappings;
            }

        }

        /**
         * An implementation of ModalGenerator which generates entity modal.
         */
        private final class EntityModalGenerator extends ModalGenerator {

            // the simple name of the entity class
            private final String entityClassName;

            private final List<String> pkColumnNames = new ArrayList<String>();

            public EntityModalGenerator(org.netbeans.jpa.modeler.spec.EntityMappings entityMappings, EntityClass entityClass) throws IOException {
                super(entityMappings, entityClass);
                entityClassName = entityClass.getClassName();
//                assert typeElement.getSimpleName().contentEquals(entityClassName);
//                entityFQClassName = entityClass.getPackage() + "." + entityClassName;
//                this.useDefaults = entityClass.getUseDefaults();
            }

            @Override
            protected void initialize() throws IOException {
                /*-------JPA Modeler Start-------*/
                org.netbeans.jpa.modeler.spec.Table tableSpec = new org.netbeans.jpa.modeler.spec.Table();
                tableSpec.setName(entityClass.getTableName());
                if (fullyQualifiedTableNames) {
                    tableSpec.setSchema(entityClass.getSchemaName());
                    tableSpec.setCatalog(entityClass.getCatalogName());
                }
                // UniqueConstraint annotations for the table
                if (entityClass.getUniqueConstraints() != null && !entityClass.getUniqueConstraints().isEmpty()) {
                    for (List<String> constraintCols : entityClass.getUniqueConstraints()) {
                        org.netbeans.jpa.modeler.spec.UniqueConstraint uniqueConstraint = new org.netbeans.jpa.modeler.spec.UniqueConstraint();
                        for (String colName : constraintCols) {
                            uniqueConstraint.getColumnName().add(colName);
                        }
                        tableSpec.getUniqueConstraint().add(uniqueConstraint);
                    }
                }
                this.getEntityMappings().findEntity(entityClassName).setTable(tableSpec);
                /*--------JPA Modeler End--------*/
            }

            @Override
            protected void generateMember(org.netbeans.jpa.modeler.spec.Attributes attributes, EntityMember m) throws IOException {
                /*-------JPA Modeler Start-------*/
                org.netbeans.jpa.modeler.spec.Column column = new org.netbeans.jpa.modeler.spec.Column();
                column.setTable(m.getTableName());
                String memberName = m.getMemberName();
                String columnName = (String) dbMappings.getCMPFieldMapping().get(memberName);
                if (!useDefaults || !memberName.equalsIgnoreCase(columnName)) {
                    column.setName(columnName);
                }

                String memberType = getMemberType(m);
                column.setNullable(m.isNullable());

                Integer length = m.getLength();
                Integer precision = m.getPrecision();
                Integer scale = m.getScale();

                if (length != null && isCharacterType(memberType)) {
                    column.setLength(length);
                }
                if (precision != null && isDecimalType(memberType)) {
                    column.setPrecision(precision); // NOI18N
                }
                if (scale != null && isDecimalType(memberType)) {
                    column.setScale(scale); // NOI18N
                }

                if (m.isPrimaryKey()) {
                    org.netbeans.jpa.modeler.spec.Id idSpec = new org.netbeans.jpa.modeler.spec.Id();
                    idSpec.setId(NBModelerUtil.getAutoGeneratedStringId());
                    idSpec.setColumn(column);
                    idSpec.setAttributeType(memberType);
                    idSpec.setName(memberName);

                    String temporalType = getMemberTemporalType(m);
                    if (temporalType != null) {
                        idSpec.setTemporal(org.netbeans.jpa.modeler.spec.TemporalType.fromValue(temporalType));
                    }

                    if (m.isAutoIncrement()) {
                        org.netbeans.jpa.modeler.spec.GeneratedValue generatedValue = new org.netbeans.jpa.modeler.spec.GeneratedValue();
                        generatedValue.setStrategy(GenerationType.AUTO);
                        idSpec.setGeneratedValue(generatedValue);
                    }

                    attributes.getId().add(idSpec);
                    String pkColumnName = (String) dbMappings.getCMPFieldMapping().get(memberName);
                    pkColumnNames.add(pkColumnName);
                } else {
                    org.netbeans.jpa.modeler.spec.Basic basicSpec = new org.netbeans.jpa.modeler.spec.Basic();
                    basicSpec.setId(NBModelerUtil.getAutoGeneratedStringId());
                    basicSpec.setColumn(column);
                    basicSpec.setAttributeType(memberType);
                    basicSpec.setName(memberName);

                    boolean isLobType = m.isLobType();
                    if (isLobType) {
                        basicSpec.setLob(new org.netbeans.jpa.modeler.spec.Lob());
                    }
                    String temporalType = getMemberTemporalType(m);
                    if (temporalType != null) {
                        basicSpec.setTemporal(org.netbeans.jpa.modeler.spec.TemporalType.fromValue(temporalType));
                    }
                    if (m.isNullable()) {
                        basicSpec.setOptional(true);
                    } else {
                        basicSpec.setOptional(false);
                    }
                    attributes.getBasic().add(basicSpec);

//                    m.isNullable()
                }

            }

            @Override
            protected void generateRelationship(org.netbeans.jpa.modeler.spec.Attributes attributes, RelationshipRole role) throws IOException {
                /*-------JPA Modeler Start-------*/
                RelationAttribute relationAttribute;
                if (role.isToMany()) {
                    //  collectionType.className();// Use the collection type the user wants
                }

//                role.getEntityName()
//                role.getParent().getRoleB().isToMany()
                if (role.isMany() && role.isToMany()) {
                    ManyToMany manyToMany = new ManyToMany();
                    attributes.getManyToMany().add(manyToMany);
                    relationAttribute = manyToMany;
                } else if (role.isMany()) {
                    ManyToOne manyToOne = new ManyToOne();
                    attributes.getManyToOne().add(manyToOne);
                    relationAttribute = manyToOne;
                } else if (role.isToMany()) {
                    OneToMany oneToMany = new OneToMany();
                    attributes.getOneToMany().add(oneToMany);
                    relationAttribute = oneToMany;
                } else {
                    OneToOne oneToOne = new OneToOne();
                    attributes.getOneToOne().add(oneToOne);
                    relationAttribute = oneToOne;
                }
                relationAttribute.setId(NBModelerUtil.getAutoGeneratedStringId());
                relationAttribute.setName(role.getFieldName());
                if (role.equals(role.getParent().getRoleB())) { // Role B
                    RelationshipRole roleA = role.getParent().getRoleA();//.getFieldName();
                    relationAttribute.setTargetEntity(roleA.getEntityName());
                    if (relationAttribute instanceof OneToOne) {
                        ((OneToOne) relationAttribute).setMappedBy(roleA.getFieldName());
                    } else if (relationAttribute instanceof OneToMany) {
                        ((OneToMany) relationAttribute).setMappedBy(roleA.getFieldName());
                    } else if (relationAttribute instanceof ManyToMany) {
                        ((ManyToMany) relationAttribute).setMappedBy(roleA.getFieldName());
                    }
                } else {  // Role A
                    RelationshipRole roleB = role.getParent().getRoleB();
                    relationAttribute.setTargetEntity(roleB.getEntityName());
                    if (role.isMany() && role.isToMany()) { // ManyToMany
                        String jTN = (String) dbMappings.getJoinTableMapping().get(role.getFieldName());
                        JoinTable joinTable = new JoinTable();
                        joinTable.setName(jTN);
                        ((ManyToMany) relationAttribute).setJoinTable(joinTable);

                        CMPMappingModel.JoinTableColumnMapping joinColumnMap = dbMappings.getJoinTableColumnMppings().get(role.getFieldName());

                        ColumnData[] columns = joinColumnMap.getColumns();
                        ColumnData[] refColumns = joinColumnMap.getReferencedColumns();
                        for (int colIndex = 0; colIndex < columns.length; colIndex++) {
                            JoinColumn joinColumn = new JoinColumn();
                            joinColumn.setName(columns[colIndex].getColumnName());
                            joinColumn.setReferencedColumnName(refColumns[colIndex].getColumnName());
                            if (regenTablesAttrs && !columns[colIndex].isNullable()) {
                                joinColumn.setNullable(false);
                            }
                            joinTable.getJoinColumn().add(joinColumn);
                        }

                        ColumnData[] invColumns = joinColumnMap.getInverseColumns();
                        ColumnData[] refInvColumns = joinColumnMap.getReferencedInverseColumns();
                        for (int colIndex = 0; colIndex < invColumns.length; colIndex++) {
                            JoinColumn joinColumn = new JoinColumn();
                            joinColumn.setName(invColumns[colIndex].getColumnName());
                            joinColumn.setReferencedColumnName(refInvColumns[colIndex].getColumnName());
                            if (regenTablesAttrs && !invColumns[colIndex].isNullable()) {
                                joinColumn.setNullable(false);
                            }
                            joinTable.getInverseJoinColumn().add(joinColumn);
                        }
                        //joinTable schema , catalog remaing ?
                    } else { // ManyToOne, OneToMany, OneToOne
                        ColumnData[] columns = (ColumnData[]) dbMappings.getCmrFieldMapping().get(role.getFieldName());
                        CMPMappingModel relatedMappings = beanMap.get(role.getParent().getRoleB().getEntityName()).getCMPMapping();
                        ColumnData[] invColumns = (ColumnData[]) relatedMappings.getCmrFieldMapping().get(role.getParent().getRoleB().getFieldName());
                        if (columns.length == 1) {
                            JoinColumn joinColumn = new JoinColumn();
                            joinColumn.setName(columns[0].getColumnName());
                            joinColumn.setReferencedColumnName(invColumns[0].getColumnName());
                            if (regenTablesAttrs && !columns[0].isNullable()) {
                                joinColumn.setNullable(false);
                            }
                            if (pkColumnNames.contains(columns[0].getColumnName())) {
                                joinColumn.setInsertable(false);
                                joinColumn.setUpdatable(false);
                            }
                            if (relationAttribute instanceof OneToOne) {
                                ((OneToOne) relationAttribute).getJoinColumn().add(joinColumn);
                            } else if (relationAttribute instanceof OneToMany) {
                                ((OneToMany) relationAttribute).getJoinColumn().add(joinColumn);
                            } else if (relationAttribute instanceof ManyToOne) {
                                ((ManyToOne) relationAttribute).getJoinColumn().add(joinColumn);
                            }
                        } else {

                            for (int colIndex = 0; colIndex < columns.length; colIndex++) {
                                JoinColumn joinColumn = new JoinColumn();
                                joinColumn.setName(columns[colIndex].getColumnName());
                                joinColumn.setReferencedColumnName(invColumns[colIndex].getColumnName());
                                if (regenTablesAttrs && !columns[colIndex].isNullable()) {
                                    joinColumn.setNullable(false);
                                }
                                if (pkColumnNames.contains(columns[0].getColumnName())) {
                                    joinColumn.setInsertable(false);
                                    joinColumn.setUpdatable(false);
                                }
                                if (relationAttribute instanceof OneToOne) {
                                    ((OneToOne) relationAttribute).getJoinColumn().add(joinColumn);
                                } else if (relationAttribute instanceof OneToMany) {
                                    ((OneToMany) relationAttribute).getJoinColumn().add(joinColumn);
                                } else if (relationAttribute instanceof ManyToOne) {
                                    ((ManyToOne) relationAttribute).getJoinColumn().add(joinColumn);
                                }

                            }

                        }
                    }
                }

                if (!role.isToMany()) { // meaning ManyToOne or OneToOne
                    // Add optional=false on @ManyToOne or the owning side of @OneToOne
                    // if the relationship is non-optional (or non-nuallable in other words)
                    if (!role.isOptional() && (role.isMany() || role.equals(role.getParent().getRoleA()))) {
                        if (relationAttribute instanceof OneToOne) {
                            ((OneToOne) relationAttribute).setOptional(false);
                        } else if (relationAttribute instanceof ManyToOne) {
                            ((ManyToOne) relationAttribute).setOptional(false);
                        }
                    }
                }
            }

            @Override
            protected void finish() {

            }

//            private String getRelationshipFieldType(RelationshipRole role, String pkg) {
//                RelationshipRole rA = role.getParent().getRoleA();
//                RelationshipRole rB = role.getParent().getRoleB();
//                RelationshipRole otherRole = role.equals(rA) ? rB : rA;
//
//                // To address issue 139804
//                // First, check if the entity package name is set in the role.
//                // If yes, then that's the package
//                // If no, then default to the passed in pkg
//                if (role.getEntityPkgName() != null) {
//                    return otherRole.getEntityPkgName() + "." + otherRole.getEntityName(); // NOI18N
//                } else {
//                    return pkg.length() == 0 ? otherRole.getEntityName() : pkg + "." + otherRole.getEntityName(); // NOI18N
//                }
//            }
        }

    }
}
