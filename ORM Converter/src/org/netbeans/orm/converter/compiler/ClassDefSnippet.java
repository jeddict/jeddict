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
package org.netbeans.orm.converter.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang.StringUtils;
import static org.netbeans.jcode.core.util.JavaIdentifiers.getGenericType;
import static org.netbeans.jcode.core.util.JavaIdentifiers.unqualifyGeneric;
import org.netbeans.jcode.core.util.JavaSourceHelper;
import static org.netbeans.jcode.jpa.JPAConstants.EMBEDDABLE;
import static org.netbeans.jcode.jpa.JPAConstants.EMBEDDABLE_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.ENTITY;
import static org.netbeans.jcode.jpa.JPAConstants.ENTITY_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.EXCLUDE_DEFAULT_LISTENERS_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.EXCLUDE_SUPERCLASS_LISTENERS_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.GENERATED_VALUE_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.ID_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.MAPPED_SUPERCLASS;
import static org.netbeans.jcode.jpa.JPAConstants.MAPPED_SUPERCLASS_FQN;
import org.netbeans.jpa.modeler.settings.code.CodePanel;
import org.netbeans.orm.converter.compiler.extend.AssociationOverridesHandler;
import org.netbeans.orm.converter.compiler.extend.AttributeOverridesHandler;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConverterUtil;
import static org.netbeans.orm.converter.util.ORMConverterUtil.NEW_LINE;
import static org.netbeans.jcode.jpa.JPAConstants.GENERATION_TYPE_FQN;
import org.netbeans.jpa.modeler.spec.extend.ClassSnippetLocationType;
import org.netbeans.orm.converter.util.ImportSet;

public class ClassDefSnippet implements WritableSnippet, AttributeOverridesHandler, AssociationOverridesHandler {

    private static final String JPA_TEMPLATE_FILENAME = "jpatemplate.vm";
    private static final String DEFAULT_TEMPLATE_FILENAME = "classtemplate.vm";

    private static final VariableDefSnippet AUTO_GENERATE = new VariableDefSnippet();
    private List<ConstructorSnippet> constructorSnippets;
    private HashcodeMethodSnippet hashcodeMethodSnippet;
    private EqualsMethodSnippet equalsMethodSnippet;
    private ToStringMethodSnippet toStringMethodSnippet;

    private List<AnnotationSnippet> annotation;
    private Map<ClassSnippetLocationType, List<String>> customSnippet;

    static {
        AUTO_GENERATE.setName("id");
        AUTO_GENERATE.setType("String");
        AUTO_GENERATE.setAutoGenerate(true);
        AUTO_GENERATE.setPrimaryKey(true);
    }

    private boolean embeddable = false;
    private boolean generateId = false;
    private boolean excludeDefaultListener = false;
    private boolean excludeSuperClassListener = false;
    private boolean mappedSuperClass = false;
    private boolean entity = false;
    private boolean defaultClass = false;
    private boolean _abstract = false;
    private List<String> interfaces;

    private final ClassHelper classHelper = new ClassHelper();
    private final ClassHelper superClassHelper = new ClassHelper();
    private String description;
    private String author;
    private String entityName;

    private boolean propertyChangeSupport;
    private boolean vetoableChangeSupport;

    private TableDefSnippet tableDef;
    private CacheableDefSnippet cacheableDef;
    private PrimaryKeyJoinColumnsSnippet primaryKeyJoinColumns;
    private SecondaryTablesSnippet secondaryTables;
    private IdClassSnippet idClass;
    private AssociationOverridesSnippet associationOverrides;
    private AttributeOverridesSnippet attributeOverrides;
    private DiscriminatorColumnSnippet discriminatorColumn;
    private DiscriminatorValueSnippet discriminatorValue;

    private EntityListenersSnippet entityListeners;
    private ConvertsSnippet converts;
    private InheritanceSnippet inheritance;
    private NamedQueriesSnippet namedQueries;
    private NamedNativeQueriesSnippet namedNativeQueries;
    private SQLResultSetMappingsSnippet sqlResultSetMappings;
    private NamedEntityGraphsSnippet namedEntityGraphs;
    private NamedStoredProcedureQueriesSnippet namedStoredProcedureQueries;

    private List<VariableDefSnippet> variableDefs = new ArrayList<>();

    public boolean isEmbeddable() {
        return embeddable;
    }

    public void setEmbeddable(boolean embeddable) {
        this.embeddable = embeddable;
    }

    public boolean isDefaultExcludeListener() {
        return excludeDefaultListener;
    }

    public void setDefaultExcludeListener(boolean excludeDefaultListener) {
        this.excludeDefaultListener = excludeDefaultListener;
    }

    public boolean isExcludeSuperClassListener() {
        return excludeSuperClassListener;
    }

    public void setExcludeSuperClassListener(
            boolean excludeSuperClassListener) {
        this.excludeSuperClassListener = excludeSuperClassListener;
    }

    public boolean isGenerateId() {
        return generateId;
    }

    public void setGenerateId(boolean generateId) {
        this.generateId = generateId;
    }

    public boolean isMappedSuperClass() {
        return mappedSuperClass;
    }

    public void setMappedSuperClass(boolean mappedSuperClass) {
        this.mappedSuperClass = mappedSuperClass;
    }

    public String getPackageName() {
        return classHelper.getPackageName();
    }

    public void setPackageName(String packageName) {
        this.classHelper.setPackageName(packageName);
    }

    public String getClassName() {
        return classHelper.getClassName();
    }

    @Override
    public ClassHelper getClassHelper() {
        return classHelper;
    }

    public ClassHelper getSuperClassHelper() {
        return superClassHelper;
    }

    public void setClassName(String className) {
        classHelper.setClassName(className);
    }

    public String getSuperClassName() {
        return superClassHelper.getClassDeclarationWithFQGeneric();
    }

    public void setSuperClassName(String className) {
        superClassHelper.setClassName(className);
    }

    public TableDefSnippet getTableDef() {
        return tableDef;
    }

    public void setTableDef(TableDefSnippet tableDef) {
        this.tableDef = tableDef;
    }

    public PrimaryKeyJoinColumnsSnippet getPrimaryKeyJoinColumns() {
        return primaryKeyJoinColumns;
    }

    public void setPrimaryKeyJoinColumns(
            PrimaryKeyJoinColumnsSnippet primaryKeyJoinColumns) {

        this.primaryKeyJoinColumns = primaryKeyJoinColumns;
    }

    public SecondaryTablesSnippet getSecondaryTables() {
        return secondaryTables;
    }

    public void setSecondaryTables(SecondaryTablesSnippet secondaryTables) {
        this.secondaryTables = secondaryTables;
    }

    @Override
    public AssociationOverridesSnippet getAssociationOverrides() {
        return associationOverrides;
    }

    @Override
    public void setAssociationOverrides(
            AssociationOverridesSnippet associationOverrides) {

        this.associationOverrides = associationOverrides;
    }

    @Override
    public AttributeOverridesSnippet getAttributeOverrides() {
        return attributeOverrides;
    }

    @Override
    public void setAttributeOverrides(AttributeOverridesSnippet attributeOverrides) {
        this.attributeOverrides = attributeOverrides;
    }

    public DiscriminatorColumnSnippet getDiscriminatorColumn() {
        return discriminatorColumn;
    }

    public void setDiscriminatorColumn(DiscriminatorColumnSnippet discriminatorColumn) {
        this.discriminatorColumn = discriminatorColumn;
    }

    public DiscriminatorValueSnippet getDiscriminatorValue() {
        return discriminatorValue;
    }

    public void setDiscriminatorValue(DiscriminatorValueSnippet discriminatorValue) {
        this.discriminatorValue = discriminatorValue;
    }

    public IdClassSnippet getIdClass() {
        return idClass;
    }

    public void setIdClass(IdClassSnippet idClass) {
        this.idClass = idClass;
    }

    public InheritanceSnippet getInheritance() {
        return inheritance;
    }

    public void setInheritance(InheritanceSnippet inheritance) {
        this.inheritance = inheritance;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityString() {

        if (mappedSuperClass) {
            return "@" + MAPPED_SUPERCLASS;
        }

        if (embeddable) {
            return "@" + EMBEDDABLE;
        }

        if (entity) {
            if (entityName == null || entityName.isEmpty()) {
                return "@" + ENTITY;
            } else {
                return "@" + ENTITY + "(name=\"" + entityName + "\")";
            }

        }
        return "";
    }

    public void setVariableDefs(List<VariableDefSnippet> variableTypes) {
        this.variableDefs = variableTypes;
    }

    public NamedQueriesSnippet getNamedQueries() {
        return namedQueries;
    }

    public void setNamedQueries(NamedQueriesSnippet namedQueries) {
        this.namedQueries = namedQueries;
    }

    public EntityListenersSnippet getEntityListeners() {
        return entityListeners;
    }

    public void setEntityListeners(EntityListenersSnippet entityListeners) {
        this.entityListeners = entityListeners;
    }

    public NamedNativeQueriesSnippet getNamedNativeQueries() {
        return namedNativeQueries;
    }

    public void setNamedNativeQueries(NamedNativeQueriesSnippet namedNativeQueries) {
        this.namedNativeQueries = namedNativeQueries;
    }

    public SQLResultSetMappingsSnippet getSQLResultSetMappings() {
        return sqlResultSetMappings;
    }

    public void setSQLResultSetMappings(SQLResultSetMappingsSnippet sqlResultSetMappings) {
        this.sqlResultSetMappings = sqlResultSetMappings;
    }

    public List<VariableDefSnippet> getVariableDefs() {

        if (generateId && !variableDefs.contains(AUTO_GENERATE)) {
            variableDefs.add(AUTO_GENERATE);
        }

        return variableDefs;
    }

    protected String getTemplateName() {
        if (defaultClass) {
            return DEFAULT_TEMPLATE_FILENAME;
        } else {
            return JPA_TEMPLATE_FILENAME;
        }
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        try {

            Map velocityContext = new HashMap();
            velocityContext.put("classDef", this);
            velocityContext.put("n", NEW_LINE);
            velocityContext.put("fluentAPI", CodePanel.isGenerateFluentAPI());

            return ORMConverterUtil.writeToTemplate(getTemplateName(), velocityContext);

        } catch (Exception e) {
            throw new InvalidDataException("Class name : " + classHelper.getFQClassName(), e);
        }
    }

    public Collection<String> getImports() throws InvalidDataException {

        //Sort and eliminate duplicates
        ImportSet importSnippets = new ImportSet();

        if (mappedSuperClass) {
            importSnippets.add(MAPPED_SUPERCLASS_FQN);
        } else if (embeddable) {
            importSnippets.add(EMBEDDABLE_FQN);
        } else if (entity) {
            importSnippets.add(ENTITY_FQN);
        }

        if (superClassHelper.getPackageName() != null) {
            importSnippets.add(superClassHelper.getFQClassName());
        }

        if (tableDef != null) {
            importSnippets.addAll(tableDef.getImportSnippets());
        }

        if (cacheableDef != null) {
            importSnippets.addAll(cacheableDef.getImportSnippets());
        }

        if (primaryKeyJoinColumns != null) {
            importSnippets.addAll(primaryKeyJoinColumns.getImportSnippets());
        }

        if (secondaryTables != null) {
            importSnippets.addAll(secondaryTables.getImportSnippets());
        }

        if (associationOverrides != null) {
            importSnippets.addAll(associationOverrides.getImportSnippets());
        }

        if (attributeOverrides != null) {
            importSnippets.addAll(attributeOverrides.getImportSnippets());
        }

        if (idClass != null) {
            importSnippets.addAll(idClass.getImportSnippets());
        }

        if (namedQueries != null) {
            importSnippets.addAll(namedQueries.getImportSnippets());
        }

        if (namedNativeQueries != null) {
            importSnippets.addAll(namedNativeQueries.getImportSnippets());
        }

        if (namedEntityGraphs != null) {
            importSnippets.addAll(namedEntityGraphs.getImportSnippets());
        }

        if (namedStoredProcedureQueries != null) {
            importSnippets.addAll(namedStoredProcedureQueries.getImportSnippets());
        }

        if (sqlResultSetMappings != null) {
            importSnippets.addAll(sqlResultSetMappings.getImportSnippets());
        }

        if (entityListeners != null) {
            importSnippets.addAll(entityListeners.getImportSnippets());
        }

        if (converts != null) {
            importSnippets.addAll(converts.getImportSnippets());
        }

        if (discriminatorColumn != null) {
            importSnippets.addAll(discriminatorColumn.getImportSnippets());
        }

        if (discriminatorValue != null) {
            importSnippets.addAll(discriminatorValue.getImportSnippets());
        }

        if (inheritance != null) {
            importSnippets.addAll(inheritance.getImportSnippets());
        }

        if (!variableDefs.isEmpty()) {
            for (VariableDefSnippet variableDef : variableDefs) {
                importSnippets.addAll(variableDef.getImportSnippets());
            }
        }

        if (generateId) {
            importSnippets.add(ID_FQN);
            importSnippets.add(GENERATION_TYPE_FQN);
            importSnippets.add(GENERATED_VALUE_FQN);
        }

        if (excludeDefaultListener) {
            importSnippets.add(EXCLUDE_DEFAULT_LISTENERS_FQN);
        }

        if (excludeSuperClassListener) {
            importSnippets.add(EXCLUDE_SUPERCLASS_LISTENERS_FQN);
        }

        for (AnnotationSnippet snippet : this.getAnnotation()) {
            importSnippets.addAll(snippet.getImportSnippets());
        }

        importSnippets.addAll(this.getInterfaces().stream().collect(toList()));

        return importSnippets;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Collection<String> importSnippets = getImports();
        importSnippets = ORMConverterUtil.eliminateSamePkgImports(
                classHelper.getPackageName(), importSnippets);

        return ORMConverterUtil.processedImportStatements(importSnippets);
    }

    /**
     * @return the entity
     */
    public boolean isEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(boolean entity) {
        this.entity = entity;
    }

    /**
     * @return the defaultClass
     */
    public boolean isDefaultClass() {
        return defaultClass;
    }

    /**
     * @param defaultClass the defaultClass to set
     */
    public void setDefaultClass(boolean defaultClass) {
        this.defaultClass = defaultClass;
    }

    /**
     * @return the annotation
     */
    public List<AnnotationSnippet> getAnnotation() {
        if (annotation == null) {
            annotation = new ArrayList<>();
        }
        return annotation;
    }

    /**
     * @param annotation the annotation to set
     */
    public void setAnnotation(List<AnnotationSnippet> annotation) {
        this.annotation = annotation;
    }

    /**
     * @return the abstractClass
     */
    public boolean isAbstractClass() {
        return _abstract;
    }

    /**
     * @param abstractClass the abstractClass to set
     */
    public void setAbstractClass(boolean abstractClass) {
        this._abstract = abstractClass;
    }

    /**
     * @return the interfaces
     */
    public List<String> getInterfaces() {
        if (interfaces == null) {
            interfaces = new ArrayList<>();
        }
        return interfaces;
    }

    /**
     * @param interfaces the interfaces to set
     */
    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

    public boolean isInterfaceExist() {
        return interfaces != null && !interfaces.isEmpty();
    }

    public String getUnqualifiedInterfaceList() {
        return interfaces.stream().map(fqn -> unqualifyGeneric(fqn) + getGenericType(fqn)).collect(joining(", "));
    }

    /**
     * @return the namedEntityGraphs
     */
    public NamedEntityGraphsSnippet getNamedEntityGraphs() {
        return namedEntityGraphs;
    }

    /**
     * @param namedEntityGraphs the namedEntityGraphs to set
     */
    public void setNamedEntityGraphs(NamedEntityGraphsSnippet namedEntityGraphs) {
        this.namedEntityGraphs = namedEntityGraphs;
    }

    /**
     * @return the namedStoredProcedureQueries
     */
    public NamedStoredProcedureQueriesSnippet getNamedStoredProcedureQueries() {
        return namedStoredProcedureQueries;
    }

    /**
     * @param namedStoredProcedureQueries the namedStoredProcedureQueries to set
     */
    public void setNamedStoredProcedureQueries(NamedStoredProcedureQueriesSnippet namedStoredProcedureQueries) {
        this.namedStoredProcedureQueries = namedStoredProcedureQueries;
    }

    /**
     * @return the cacheableDef
     */
    public CacheableDefSnippet getCacheableDef() {
        return cacheableDef;
    }

    /**
     * @param cacheableDef the cacheableDef to set
     */
    public void setCacheableDef(CacheableDefSnippet cacheableDef) {
        this.cacheableDef = cacheableDef;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getJavaDoc() {
        StringBuilder doc = new StringBuilder();
        if (StringUtils.isNotBlank(description) || StringUtils.isNotBlank(author)) {
            doc.append(NEW_LINE).append("/**").append(NEW_LINE);
            if (StringUtils.isNotBlank(description)) {
                for (String line : description.split("\\r\\n|\\n|\\r")) {
                    doc.append(" * ").append(line).append(NEW_LINE);
                }
            }
            if (StringUtils.isNotBlank(author)) {
                doc.append(" * @author ").append(author).append(NEW_LINE);
            }
            doc.append(" */");
        }
        return doc.toString();
    }

    public boolean isJavaDocExist() {
        return StringUtils.isNotBlank(description) || StringUtils.isNotBlank(JavaSourceHelper.getAuthor());
    }

    /**
     * @return the toStringMethodSnippet
     */
    public ToStringMethodSnippet getToStringMethod() {
        return toStringMethodSnippet;
    }

    /**
     * @param toStringMethodSnippet the toStringMethodSnippet to set
     */
    public void setToStringMethod(ToStringMethodSnippet toStringMethodSnippet) {
        this.toStringMethodSnippet = toStringMethodSnippet;
    }

    /**
     * @return the hashcodeMethodSnippet
     */
    public HashcodeMethodSnippet getHashcodeMethod() {
        return hashcodeMethodSnippet;
    }

    /**
     * @param hashcodeMethodSnippet the hashcodeMethodSnippet to set
     */
    public void setHashcodeMethod(HashcodeMethodSnippet hashcodeMethodSnippet) {
        this.hashcodeMethodSnippet = hashcodeMethodSnippet;
    }

    /**
     * @return the equalsMethodSnippet
     */
    public EqualsMethodSnippet getEqualsMethod() {
        return equalsMethodSnippet;
    }

    /**
     * @param equalsMethodSnippet the equalsMethodSnippet to set
     */
    public void setEqualsMethod(EqualsMethodSnippet equalsMethodSnippet) {
        this.equalsMethodSnippet = equalsMethodSnippet;
    }

    /**
     * @return the constructorSnippets
     */
    public List<ConstructorSnippet> getConstructors() {
        if (constructorSnippets == null) {
            constructorSnippets = new ArrayList<>();
        }
        return constructorSnippets;
    }

    /**
     * @param constructorSnippets the constructorSnippets to set
     */
    public void setConstructors(List<ConstructorSnippet> constructorSnippets) {
        this.constructorSnippets = constructorSnippets;
    }

    public boolean addConstructor(ConstructorSnippet constructorSnippet) {
        return getConstructors().add(constructorSnippet);
    }

    public boolean removeConstructor(ConstructorSnippet constructorSnippet) {
        return getConstructors().remove(constructorSnippet);
    }

    /**
     * @return the customSnippet
     */
    public Map<ClassSnippetLocationType, List<String>> getCustomSnippet() {
        return customSnippet;
    }

    public List<String> getCustomSnippet(String type) {
        return customSnippet.get(ClassSnippetLocationType.valueOf(type));
    }

    /**
     * @param customSnippet the customSnippet to set
     */
    public void setCustomSnippet(Map<ClassSnippetLocationType, List<String>> customSnippet) {
        this.customSnippet = customSnippet;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the propertyChangeSupport
     */
    public boolean isPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    /**
     * @param propertyChangeSupport the propertyChangeSupport to set
     */
    public void setPropertyChangeSupport(boolean propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
    }

    /**
     * @return the vetoableChangeSupport
     */
    public boolean isVetoableChangeSupport() {
        return vetoableChangeSupport;
    }

    /**
     * @param vetoableChangeSupport the vetoableChangeSupport to set
     */
    public void setVetoableChangeSupport(boolean vetoableChangeSupport) {
        this.vetoableChangeSupport = vetoableChangeSupport;
    }

    /**
     * @return the converts
     */
    public ConvertsSnippet getConverts() {
        return converts;
    }

    /**
     * @param converts the converts to set
     */
    public void setConverts(ConvertsSnippet converts) {
        this.converts = converts;
    }
}
