/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.orm.generator.compiler.def;

import io.github.jeddict.jaxb.spec.JaxbMetadata;
import io.github.jeddict.jaxb.spec.JaxbVariableType;
import static io.github.jeddict.jcode.JAXBConstants.JAXB_XML_ATTRIBUTE;
import static io.github.jeddict.jcode.JAXBConstants.JAXB_XML_ELEMENT;
import static io.github.jeddict.jcode.JAXBConstants.JAXB_XML_ELEMENT_REF;
import static io.github.jeddict.jcode.JAXBConstants.JAXB_XML_ELEMENT_WRAPPER;
import static io.github.jeddict.jcode.JAXBConstants.JAXB_XML_INVERSE_REFERENCE;
import static io.github.jeddict.jcode.JAXBConstants.JAXB_XML_LIST;
import static io.github.jeddict.jcode.JAXBConstants.JAXB_XML_TRANSIENT;
import static io.github.jeddict.jcode.JAXBConstants.JAXB_XML_VALUE;
import static io.github.jeddict.jcode.JPAConstants.ELEMENT_COLLECTION_FQN;
import static io.github.jeddict.jcode.JPAConstants.EMBEDDED_FQN;
import static io.github.jeddict.jcode.JPAConstants.EMBEDDED_ID_FQN;
import static io.github.jeddict.jcode.JPAConstants.ID_FQN;
import static io.github.jeddict.jcode.JPAConstants.LOB_FQN;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_FQN;
import static io.github.jeddict.jcode.JPAConstants.TRANSIENT_FQN;
import static io.github.jeddict.jcode.JPAConstants.VERSION_FQN;
import static io.github.jeddict.jcode.util.AttributeType.getArrayType;
import static io.github.jeddict.jcode.util.AttributeType.getWrapperType;
import static io.github.jeddict.jcode.util.AttributeType.isArray;
import io.github.jeddict.jcode.util.Inflector;
import static io.github.jeddict.jcode.util.JavaSourceHelper.getSimpleClassName;
import io.github.jeddict.jcode.util.JavaUtil;
import static io.github.jeddict.jcode.util.StringHelper.firstLower;
import static io.github.jeddict.jcode.util.StringHelper.firstUpper;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.ManyToMany;
import io.github.jeddict.jpa.spec.OneToMany;
import io.github.jeddict.jpa.spec.extend.AccessModifierType;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.AttributeAnnotationLocationType;
import io.github.jeddict.jpa.spec.extend.CollectionTypeHandler;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.spec.extend.MultiRelationAttribute;
import io.github.jeddict.orm.generator.compiler.AnnotationSnippet;
import io.github.jeddict.orm.generator.compiler.AssociationOverridesHandler;
import io.github.jeddict.orm.generator.compiler.AssociationOverridesSnippet;
import io.github.jeddict.orm.generator.compiler.AttributeOverridesHandler;
import io.github.jeddict.orm.generator.compiler.AttributeOverridesSnippet;
import io.github.jeddict.orm.generator.compiler.BasicSnippet;
import io.github.jeddict.orm.generator.compiler.CollectionTableSnippet;
import io.github.jeddict.orm.generator.compiler.ColumnDefSnippet;
import io.github.jeddict.orm.generator.compiler.ConvertsSnippet;
import io.github.jeddict.orm.generator.compiler.ElementCollectionSnippet;
import io.github.jeddict.orm.generator.compiler.EnumeratedSnippet;
import io.github.jeddict.orm.generator.compiler.GeneratedValueSnippet;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.compiler.JoinColumnsSnippet;
import io.github.jeddict.orm.generator.compiler.JoinTableSnippet;
import io.github.jeddict.orm.generator.compiler.ManyToManySnippet;
import io.github.jeddict.orm.generator.compiler.ManyToOneSnippet;
import io.github.jeddict.orm.generator.compiler.OneToManySnippet;
import io.github.jeddict.orm.generator.compiler.OneToOneSnippet;
import io.github.jeddict.orm.generator.compiler.OrderBySnippet;
import io.github.jeddict.orm.generator.compiler.OrderColumnSnippet;
import io.github.jeddict.orm.generator.compiler.RelationDefSnippet;
import io.github.jeddict.orm.generator.compiler.SequenceGeneratorSnippet;
import io.github.jeddict.orm.generator.compiler.SingleRelationAttributeSnippet;
import io.github.jeddict.orm.generator.compiler.Snippet;
import io.github.jeddict.orm.generator.compiler.TableGeneratorSnippet;
import io.github.jeddict.orm.generator.compiler.TemporalSnippet;
import io.github.jeddict.orm.generator.compiler.TypeIdentifierSnippet;
import io.github.jeddict.orm.generator.compiler.constraints.ConstraintSnippet;
import io.github.jeddict.orm.generator.util.ClassHelper;
import io.github.jeddict.orm.generator.util.ImportSet;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.NEW_LINE;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.TAB;
import io.github.jeddict.settings.code.CodePanel;
import io.github.jeddict.snippet.AttributeSnippetLocationType;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.GETTER_JAVADOC;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.GETTER_THROWS;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.IMPORT;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.PROPERTY_JAVADOC;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.SETTER_JAVADOC;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.SETTER_THROWS;
import java.util.ArrayList;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.apache.commons.lang.StringUtils;
import org.openide.util.Exceptions;

public class VariableDefSnippet implements Snippet, AttributeOverridesHandler, AssociationOverridesHandler {

    private List<ConstraintSnippet> attributeConstraints = new ArrayList<>();
    private List<ConstraintSnippet> keyConstraints = new ArrayList<>();
    private List<ConstraintSnippet> valueConstraints = new ArrayList<>();

    private List<Snippet> attributeJSONBSnippets = new ArrayList<>();
    private boolean functionalType;

    private JaxbVariableType jaxbVariableType;
    private JaxbMetadata jaxbWrapperMetadata;
    private JaxbMetadata jaxbMetadata;

    private boolean embedded;
    private boolean embeddedId;
    private boolean lob;
    private boolean primaryKey;
    private boolean tranzient;
    private boolean version;

    private AccessModifierType accessModifier;
    private String name;
    private String defaultValue;
    private String description;
    private boolean propertyChangeSupport;
    private boolean vetoableChangeSupport;
    private ClassHelper classHelper = new ClassHelper();
    private String mapKey;
    private BasicSnippet basic;
    private ElementCollectionSnippet elementCollection;
    private ColumnDefSnippet columnDef;
    private RelationDefSnippet relationDef;
    private OrderBySnippet orderBy;
    private OrderColumnSnippet orderColumn;
    private JoinColumnsSnippet joinColumns;
    private JoinTableSnippet joinTable;
    private CollectionTableSnippet collectionTable;
    private GeneratedValueSnippet generatedValue;
    private TableGeneratorSnippet tableGenerator;
    private SequenceGeneratorSnippet sequenceGenerator;
    private EnumeratedSnippet enumerated;
    private TemporalSnippet temporal;
    private AssociationOverridesSnippet associationOverrides;
    private AttributeOverridesSnippet attributeOverrides;
    private TypeIdentifierSnippet typeIdentifier;
    private Attribute attribute;
    private Map<AttributeSnippetLocationType, List<String>> customSnippet;
    private Map<AttributeAnnotationLocationType, List<AnnotationSnippet>> annotation;
    private ConvertsSnippet converts;
    
    private String collectionType;
    private String collectionImplType;

    public VariableDefSnippet() {
    }

    public VariableDefSnippet(Attribute attribute) {
        this.attribute = attribute;
    }

    public BasicSnippet getBasic() {
        return basic;
    }

    public void setBasic(BasicSnippet basic) {
        this.basic = basic;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public boolean isEmbeddedId() {
        return embeddedId;
    }

    public void setEmbeddedId(boolean embeddedId) {
        this.embeddedId = embeddedId;
    }

    public boolean isLob() {
        return lob;
    }

    public void setLob(boolean lob) {
        this.lob = lob;
    }

    public String getAccessModifier() {
        if (accessModifier == null) {
            return AccessModifierType.PRIVATE.getValue();
        }
        return accessModifier.getValue();
    }

    public void setAccessModifier(AccessModifierType accessModifier) {
        this.accessModifier = accessModifier;
    }

    public String getConstraintType() {
        String type = null;
        if (this.getTypeIdentifier() != null) {
            type = this.getTypeIdentifier().getConstraintVariableType();
        } else if (isArray(classHelper.getClassName())) {
            String constraint = null;
            try {
                constraint = getInlineValueConstraint();
            } catch (InvalidDataException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (constraint != null) {
                type = getArrayType(classHelper.getClassName()) + " " + constraint + "[]";
            }
        }

        if (type == null) {
            type = classHelper.getClassName();
        }

        return type;
    }

    public String getType() {
        String type;
        if (this.getTypeIdentifier() != null) {
            type = this.getTypeIdentifier().getVariableType();
        } else {
            type = classHelper.getClassName();
        }

        return type;
    }

    public String getReturnType() {//Modified : Collection => Collection<Entity>
        String type;
        if (this.getTypeIdentifier() != null) { //Collection<Entity> , Collection<String>
            type = this.getTypeIdentifier().getVariableType();
        } else {
            type = classHelper.getClassName();
        }

        if ((this.getTypeIdentifier() == null
                || getRelationDef() instanceof SingleRelationAttributeSnippet)
                && functionalType) {
            if (isArray(type)) {
                type = "Optional<" + type + '>';
            } else {
                type = "Optional<" + getWrapperType(type) + '>';
            }
        }
        return type;
    }

    public String getReturnValue() {
        String value = getName();
        if ((this.getTypeIdentifier() == null || getRelationDef() instanceof SingleRelationAttributeSnippet) && functionalType) {
            value = "Optional.ofNullable(" + value + ')';
        }
        return value;
    }

    public String getImplementationType() {
        if (this.getTypeIdentifier() != null) {
            return this.getTypeIdentifier().getImplementationType();
        } else {
            return null;
        }
    }


     public ClassHelper getClassHelper(){
        return classHelper;
    }
    
    public void setType(String type) {
        classHelper.setClassName(type);
    }
    
    
    public void setType(String rootPackage, JavaClass javaClass) {
        classHelper.setClassName(javaClass.getClazz());
        classHelper.setPackageName(javaClass.getAbsolutePackage(rootPackage));
    }

    public String getName() {
        return name;
    }

    public String getFluentMethodName() {
        if (StringUtils.isNotBlank(CodePanel.getFluentAPIPrefix())) {
            return CodePanel.getFluentAPIPrefix() + firstUpper(getName());
        } else {
            return getName();
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColumnDefSnippet getColumnDef() {
        return columnDef;
    }

    public void setColumnDef(ColumnDefSnippet columnDef) {
        this.columnDef = columnDef;
    }

    public boolean isTranzient() {
        return tranzient;
    }

    public void setTranzient(boolean tranzient) {
        this.tranzient = tranzient;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * @return the a derived methodName from variableName Eg nickname -->
     * getNickname()
     */
    public String getMethodName() {
        return JavaUtil.getMethodName(null, name);
    }

    public RelationDefSnippet getRelationDef() {
        return relationDef;
    }

    public void setRelationDef(RelationDefSnippet relationType) {
        this.relationDef = relationType;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    public OrderBySnippet getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBySnippet orderBy) {
        this.orderBy = orderBy;
    }

    public JoinColumnsSnippet getJoinColumns() {
        return joinColumns;
    }

    public void setJoinColumns(JoinColumnsSnippet joinColumns) {
        this.joinColumns = joinColumns;
    }

    public JoinTableSnippet getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(JoinTableSnippet joinTable) {
        this.joinTable = joinTable;
    }

    public GeneratedValueSnippet getGeneratedValue() {
        return generatedValue;
    }

    public void setGeneratedValue(GeneratedValueSnippet generatedValue) {
        this.generatedValue = generatedValue;
    }

    public TableGeneratorSnippet getTableGenerator() {
        return tableGenerator;
    }

    public void setTableGenerator(TableGeneratorSnippet tableGenerator) {
        this.tableGenerator = tableGenerator;
    }

    public SequenceGeneratorSnippet getSequenceGenerator() {
        return sequenceGenerator;
    }

    public void setSequenceGenerator(SequenceGeneratorSnippet sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    public EnumeratedSnippet getEnumerated() {
        return enumerated;
    }

    public void setEnumerated(EnumeratedSnippet enumerated) {
        this.enumerated = enumerated;
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey;
    }

    public String getMapKeyString() {

        if (mapKey == null) {
            return "@" + MAP_KEY;
        }

        return "@" + MAP_KEY + "(name=\"" + mapKey + ORMConverterUtil.QUOTE + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public TypeIdentifierSnippet getTypeIdentifier() { //in case of collection type
        return typeIdentifier;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<String> getTypeImportSnippets() throws InvalidDataException {
        Collection<String> imports = new HashSet<>();
        if (attribute instanceof CollectionTypeHandler) {
            typeIdentifier = new TypeIdentifierSnippet(this);
            classHelper = new ClassHelper();
            classHelper.setClassName(typeIdentifier.getVariableType());
            imports.addAll(typeIdentifier.getImportSnippets());
        } else if (classHelper.getPackageName() != null) {
            imports.add(classHelper.getFQClassName());
        }
        return imports;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        ImportSet importSnippets = new ImportSet();
        importSnippets.addAll(getTypeImportSnippets());
        if (functionalType) {
            importSnippets.add(Optional.class.getCanonicalName());
        }

        if (basic != null) {
            importSnippets.addAll(basic.getImportSnippets());
        }
        if (elementCollection != null) {
            importSnippets.addAll(elementCollection.getImportSnippets());
        }

        if (columnDef != null) {
            importSnippets.addAll(columnDef.getImportSnippets());
        }

        if (relationDef != null) {
            importSnippets.addAll(relationDef.getImportSnippets());
        }

        if (orderBy != null) {
            importSnippets.addAll(orderBy.getImportSnippets());
        }

        if (orderColumn != null) {
            importSnippets.addAll(orderColumn.getImportSnippets());
        }

        if (joinColumns != null) {
            importSnippets.addAll(joinColumns.getImportSnippets());
        }

        if (joinTable != null) {
            importSnippets.addAll(joinTable.getImportSnippets());
        }

        if (collectionTable != null) {
            importSnippets.addAll(collectionTable.getImportSnippets());
        }

        if (generatedValue != null) {
            importSnippets.addAll(generatedValue.getImportSnippets());
        }

        if (tableGenerator != null) {
            importSnippets.addAll(tableGenerator.getImportSnippets());
        }

        if (sequenceGenerator != null) {
            importSnippets.addAll(sequenceGenerator.getImportSnippets());
        }

        if (enumerated != null) {
            importSnippets.addAll(enumerated.getImportSnippets());
        }

        if (temporal != null) {
            importSnippets.addAll(temporal.getImportSnippets());
        }

        if (mapKey != null) {
            importSnippets.add(MAP_KEY_FQN);
        }

        if (elementCollection != null) {
            importSnippets.add(ELEMENT_COLLECTION_FQN);
        }
        if (embedded) {
            importSnippets.add(EMBEDDED_FQN);
        }

        if (embeddedId) {
            importSnippets.add(EMBEDDED_ID_FQN);
        }

        if (lob) {
            importSnippets.add(LOB_FQN);
        }

        if (primaryKey) {
            importSnippets.add(ID_FQN);
        }

        if (tranzient) {
            importSnippets.add(TRANSIENT_FQN);
        }

        if (version) {
            importSnippets.add(VERSION_FQN);
        }

        if (converts != null) {
            importSnippets.addAll(converts.getImportSnippets());
        }

        if (attributeOverrides != null) {
            importSnippets.addAll(attributeOverrides.getImportSnippets());
        }
        if (associationOverrides != null) {
            importSnippets.addAll(associationOverrides.getImportSnippets());
        }

        for (AnnotationSnippet snippet : this.getAnnotation()
                .values()
                .stream()
                .flatMap(annot -> annot.stream())
                .collect(toList())) {
            importSnippets.addAll(snippet.getImportSnippets());
        }

        for (ConstraintSnippet snippet : this.getAttributeConstraints()) {
            importSnippets.addAll(snippet.getImportSnippets());
        }

        for (ConstraintSnippet snippet : this.getKeyConstraints()) {
            importSnippets.addAll(snippet.getImportSnippets());
        }

        for (ConstraintSnippet snippet : this.getValueConstraints()) {
            importSnippets.addAll(snippet.getImportSnippets());
        }

        for (Snippet snippet : this.getJSONBSnippets()) {
            importSnippets.addAll(snippet.getImportSnippets());
        }

        if (getJaxbVariableType() == JaxbVariableType.XML_INVERSE_REFERENCE && getRelationDef() != null) {
            importSnippets.add("org.eclipse.persistence.oxm.annotations.XmlInverseReference");
        }

        List<String> customImportSnippets = getCustomSnippet().get(IMPORT);
        if (customImportSnippets != null) {
            importSnippets.addAll(
                    customImportSnippets
                            .stream()
                            .filter(snippet -> !snippet.startsWith("import"))
                            .filter(snippet -> !snippet.startsWith(";"))
                            .collect(toSet())
            );
        }


        return importSnippets;
    }

    /**
     * @return the elementCollection
     */
    public ElementCollectionSnippet getElementCollection() {
        return elementCollection;
    }

    /**
     * @param elementCollection the elementCollection to set
     */
    public void setElementCollection(ElementCollectionSnippet elementCollection) {
        this.elementCollection = elementCollection;
    }

    /**
     * @return the collectionTable
     */
    public CollectionTableSnippet getCollectionTable() {
        return collectionTable;
    }

    /**
     * @param collectionTable the collectionTable to set
     */
    public void setCollectionTable(CollectionTableSnippet collectionTable) {
        this.collectionTable = collectionTable;
    }

    /**
     * @return the associationOverrides
     */
    @Override
    public AssociationOverridesSnippet getAssociationOverrides() {
        return associationOverrides;
    }

    /**
     * @param associationOverrides the associationOverrides to set
     */
    @Override
    public void setAssociationOverrides(AssociationOverridesSnippet associationOverrides) {
        this.associationOverrides = associationOverrides;
    }

    /**
     * @return the attributeOverrides
     */
    @Override
    public AttributeOverridesSnippet getAttributeOverrides() {
        return attributeOverrides;
    }

    /**
     * @param attributeOverrides the attributeOverrides to set
     */
    @Override
    public void setAttributeOverrides(AttributeOverridesSnippet attributeOverrides) {
        this.attributeOverrides = attributeOverrides;
    }

    /**
     * @return the annotation
     */
    public Map<AttributeAnnotationLocationType, List<AnnotationSnippet>> getAnnotation() {
        return annotation;
    }

    public List<AnnotationSnippet> getAnnotation(String locationType) {
        return annotation.get(AttributeAnnotationLocationType.valueOf(locationType));
    }

    public String getInlineKeyAnnotation() throws InvalidDataException {
        StringBuilder sb = new StringBuilder();
        List<AnnotationSnippet> snippets = annotation.get(AttributeAnnotationLocationType.KEY);
        if (snippets != null) {
            for (AnnotationSnippet snippet : snippets) {
                sb.append(snippet.getSnippet()).append(" ");
            }
        }
        return sb.toString();
    }

    public String getInlineValueAnnotation() throws InvalidDataException {
        StringBuilder sb = new StringBuilder();
        List<AnnotationSnippet> snippets = annotation.get(AttributeAnnotationLocationType.TYPE);
        if (snippets != null) {
            for (AnnotationSnippet snippet : snippets) {
                sb.append(snippet.getSnippet()).append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * @param annotation the annotation to set
     */
    public void setAnnotation(Map<AttributeAnnotationLocationType, List<AnnotationSnippet>> annotation) {
        this.annotation = annotation;
    }

    /**
     * @return the attributeConstraints
     */
    public List<ConstraintSnippet> getAttributeConstraints() {
        return attributeConstraints;
    }

    /**
     * @param attributeConstraints the attributeConstraints to set
     */
    public void setAttributeConstraints(List<ConstraintSnippet> attributeConstraints) {
        this.attributeConstraints = attributeConstraints;
    }

    /**
     * @return the keyConstraints
     */
    public List<ConstraintSnippet> getKeyConstraints() {
        return keyConstraints;
    }

    public String getInlineKeyConstraint() throws InvalidDataException {
        StringBuilder sb = new StringBuilder();
        for (ConstraintSnippet keyConstraint : keyConstraints) {
            sb.append(keyConstraint.getSnippet()).append(" ");
        }
        return sb.toString();
    }

    /**
     * @param keyConstraints the keyConstraints to set
     */
    public void setKeyConstraints(List<ConstraintSnippet> keyConstraints) {
        this.keyConstraints = keyConstraints;
    }

    /**
     * @return the valueConstraints
     */
    public List<ConstraintSnippet> getValueConstraints() {
        return valueConstraints;
    }

    public String getInlineValueConstraint() throws InvalidDataException {
        StringBuilder sb = new StringBuilder();
        for (ConstraintSnippet valueConstraint : valueConstraints) {
            sb.append(valueConstraint.getSnippet()).append(" ");
        }
        return sb.toString();
    }

    /**
     * @param valueConstraints the valueConstraints to set
     */
    public void setValueConstraints(List<ConstraintSnippet> valueConstraints) {
        this.valueConstraints = valueConstraints;
    }

    /**
     * @return the jaxbVariableType
     */
    public JaxbVariableType getJaxbVariableType() {
        return jaxbVariableType;
    }

    /**
     * @param jaxbVariableType the jaxbVariableType to set
     */
    public void setJaxbVariableType(JaxbVariableType jaxbVariableType) {
        this.jaxbVariableType = jaxbVariableType;
    }

    /**
     * @return the jaxbWrapperMetadata
     */
    public JaxbMetadata getJaxbWrapperMetadata() {
        return jaxbWrapperMetadata;
    }

    /**
     * @param jaxbWrapperMetadata the jaxbWrapperMetadata to set
     */
    public void setJaxbWrapperMetadata(JaxbMetadata jaxbWrapperMetadata) {
        this.jaxbWrapperMetadata = jaxbWrapperMetadata;
    }

    /**
     * @return the jaxbXmlAttribute
     */
    public JaxbMetadata getJaxbMetadata() {
        return jaxbMetadata;
    }

    /**
     * @param jaxbMetadata the jaxbMetadata to set
     */
    public void setJaxbMetadata(JaxbMetadata jaxbMetadata) {
        this.jaxbMetadata = jaxbMetadata;
    }

    public String getJaxbAnnotationSnippet() {

        StringBuilder snippet = new StringBuilder();

        if (getJaxbVariableType() == JaxbVariableType.XML_ATTRIBUTE || getJaxbVariableType() == JaxbVariableType.XML_LIST_ATTRIBUTE) {
            if (getJaxbVariableType() == JaxbVariableType.XML_LIST_ATTRIBUTE) {
                snippet.append("@" + JAXB_XML_LIST).append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
            }
            snippet.append("@" + JAXB_XML_ATTRIBUTE);
            JaxbMetadata md = this.getJaxbMetadata();
            if (StringUtils.isNotBlank(md.getName())
                    || StringUtils.isNotBlank(md.getNamespace())
                    || md.getRequired()) {
                snippet.append("(");
                if (StringUtils.isNotBlank(md.getName())) {
                    snippet.append("name = \"").append(md.getName()).append("\", ");
                }
                if (StringUtils.isNotBlank(md.getNamespace())) {
                    snippet.append("namespace = \"").append(md.getNamespace()).append("\", ");
                }
                if (md.getRequired()) {
                    snippet.append("required = ").append(md.getRequired()).append(", ");
                }
                snippet.setLength(snippet.length() - 2);
                snippet.append(")");
            }
        } else if (getJaxbVariableType() == JaxbVariableType.XML_ELEMENT
                || getJaxbVariableType() == JaxbVariableType.XML_LIST_ELEMENT
                || getJaxbVariableType() == JaxbVariableType.XML_ELEMENT_WRAPPER) {
            if (getJaxbVariableType() == JaxbVariableType.XML_LIST_ELEMENT) {
                snippet.append("@" + JAXB_XML_LIST).append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
            } else if (getJaxbVariableType() == JaxbVariableType.XML_ELEMENT_WRAPPER) {
                snippet.append("@" + JAXB_XML_ELEMENT_WRAPPER);
                JaxbMetadata wmd = this.getJaxbWrapperMetadata();
                if (StringUtils.isNotBlank(wmd.getName())
                        || StringUtils.isNotBlank(wmd.getNamespace())
                        || wmd.getRequired()
                        || wmd.getNillable()) {
                    snippet.append("(");
                    if (wmd.getName() != null && !wmd.getName().isEmpty()) {
                        snippet.append("name = \"").append(wmd.getName()).append("\", ");
                    }
                    if (StringUtils.isNotBlank(wmd.getNamespace())) {
                        snippet.append("namespace = \"").append(wmd.getNamespace()).append("\", ");
                    }
                    if (wmd.getRequired()) {
                        snippet.append("required = ").append(wmd.getRequired()).append(", ");
                    }
                    if (wmd.getNillable()) {
                        snippet.append("nillable = ").append(wmd.getNillable()).append(", ");
                    }
                    snippet.setLength(snippet.length() - 2);
                    snippet.append(")");
                }
                snippet.append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
            }
            snippet.append("@" + JAXB_XML_ELEMENT);
            JaxbMetadata md = this.getJaxbMetadata();
            if (StringUtils.isNotBlank(md.getName())
                    || StringUtils.isNotBlank(md.getNamespace())
                    || md.getRequired()
                    || md.getNillable()) {
                snippet.append("(");
                if (md.getName() != null && !md.getName().isEmpty()) {
                    snippet.append("name = \"").append(md.getName()).append("\", ");
                }
                if (StringUtils.isNotBlank(md.getNamespace())) {
                    snippet.append("namespace = \"").append(md.getNamespace()).append("\", ");
                }
                if (md.getRequired()) {
                    snippet.append("required = ").append(md.getRequired()).append(", ");
                }
                if (md.getNillable()) {
                    snippet.append("nillable = ").append(md.getNillable()).append(", ");
                }
                snippet.setLength(snippet.length() - 2);
                snippet.append(")");
            }
        } else if (getJaxbVariableType() == JaxbVariableType.XML_VALUE || getJaxbVariableType() == JaxbVariableType.XML_LIST_VALUE) {
            if (getJaxbVariableType() == JaxbVariableType.XML_LIST_VALUE) {
                snippet.append("@" + JAXB_XML_LIST).append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
            }
            snippet.append("@" + JAXB_XML_VALUE);
        } else if (getJaxbVariableType() == JaxbVariableType.XML_TRANSIENT) {
            snippet.append("@" + JAXB_XML_TRANSIENT);
        } else if (getJaxbVariableType() == JaxbVariableType.XML_INVERSE_REFERENCE && getRelationDef() != null) {
            String mappedBy = getRelationDef().getTargetField();//both side are applicable so targetField is used instead of mappedBy
            if (mappedBy != null) {
                snippet.append(String.format("@" + JAXB_XML_INVERSE_REFERENCE + "(mappedBy=\"%s\")", mappedBy));
            }
        } else if (getJaxbVariableType() == JaxbVariableType.XML_ELEMENT_REF) {
            snippet.append("@" + JAXB_XML_ELEMENT_REF);
            JaxbMetadata md = this.getJaxbMetadata();
            if (StringUtils.isNotBlank(md.getName())
                    || StringUtils.isNotBlank(md.getNamespace())
                    || md.getRequired()) {
                snippet.append("(");
                if (StringUtils.isNotBlank(md.getName())) {
                    snippet.append("name = \"").append(md.getName()).append("\", ");
                }
                if (StringUtils.isNotBlank(md.getNamespace())) {
                    snippet.append("namespace = \"").append(md.getNamespace()).append("\", ");
                }
                if (md.getRequired()) {
                    snippet.append("required = ").append(md.getRequired()).append(", ");
                }
                snippet.setLength(snippet.length() - 2);
                snippet.append(")");
            }
        } else {
            if (isPrimaryKey()) {
//            snippet.append("@XmlID").append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
            } else if (getRelationDef() != null) {
                if (getRelationDef() instanceof OneToOneSnippet) {
                    OneToOneSnippet otoSnippet = (OneToOneSnippet) getRelationDef();
                    if (otoSnippet.getMappedBy() != null && !otoSnippet.getMappedBy().trim().isEmpty()) {
                        snippet.append("@" + JAXB_XML_TRANSIENT);
                    } else {
//                      snippet.append("@XmlIDREF").append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
                    }
                } else if (getRelationDef() instanceof OneToManySnippet) {
                    OneToManySnippet otmSnippet = (OneToManySnippet) getRelationDef();
                    if (otmSnippet.getMappedBy() != null && !otmSnippet.getMappedBy().trim().isEmpty()) {
                        snippet.append("@" + JAXB_XML_TRANSIENT);
                    } else {
//                      snippet.append("@XmlIDREF").append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
                    }
                } else if (getRelationDef() instanceof ManyToOneSnippet) {
//                   snippet.append("@XmlIDREF").append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
                } else if (getRelationDef() instanceof ManyToManySnippet) {
                    ManyToManySnippet mtmSnippet = (ManyToManySnippet) getRelationDef();
                    if (mtmSnippet.getMappedBy() != null && !mtmSnippet.getMappedBy().trim().isEmpty()) {
                        snippet.append("@" + JAXB_XML_TRANSIENT);
                    } else {
//                      snippet.append("@XmlIDREF").append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
                    }
                }
            }
        }
        int snippetLength = snippet.length(); //Remove NEW_LINE and TAB
        if (snippetLength > 6 && snippet.charAt(snippetLength - 5) == '\n') {
            snippet.setLength(snippetLength - 5);
        }
        return snippet.toString();
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

    public StringBuilder getJavaDoc(String prefix) {
        StringBuilder doc = new StringBuilder();
        doc.append(TAB).append("/**").append(NEW_LINE);
        int count = 0;
        for (String line : description.split("\\r\\n|\\n|\\r")) {
            count++;
            doc.append(TAB).append(" * ");
            if (count == 1 && StringUtils.isNotBlank(prefix)) {
                doc.append(prefix);
                line = firstLower(line);
            }
            doc.append(line).append(NEW_LINE);
        }
        return doc;
    }

    public String getPropertyJavaDoc() {
        if (getCustomSnippet(PROPERTY_JAVADOC.name()) != null) {
            return getCustomSnippet(PROPERTY_JAVADOC.name())
                    .stream()
                    .collect(joining("\n"));
        } else {
            StringBuilder doc = getJavaDoc(null);
            doc.append(TAB).append(" */");
            return doc.toString();
        }
    }

    public String getGetterJavaDoc() {
        if (getCustomSnippet(GETTER_JAVADOC.name()) != null) {
            return getCustomSnippet(GETTER_JAVADOC.name())
                    .stream()
                    .collect(joining("\n"));
        } else {
            StringBuilder doc = getJavaDoc("Get ");
            doc.append(TAB).append(" * ").append(NEW_LINE);
            doc.append(TAB).append(" * ").append(String.format("@return {@link #%s}", getName())).append(NEW_LINE);
            doc.append(TAB).append(" */");
            return doc.toString();
        }
    }

    public String getSetterJavaDoc() {
        if (getCustomSnippet(SETTER_JAVADOC.name()) != null) {
            return getCustomSnippet(SETTER_JAVADOC.name())
                    .stream()
                    .collect(joining("\n"));
        } else {
            StringBuilder doc = getJavaDoc("Set ");
            doc.append(TAB).append(" * ").append(NEW_LINE);
            doc.append(TAB).append(" * ").append(String.format("@param %s {@link #%s}", getName(), getName())).append(NEW_LINE);
            doc.append(TAB).append(" */");
            return doc.toString();
        }
    }

    public String getFluentJavaDoc() {
        StringBuilder doc = getJavaDoc("Set ");
        doc.append(TAB).append(" * ").append(NEW_LINE);
        doc.append(TAB).append(" * ").append(String.format("@param %s {@link #%s}", getName(), getName())).append(NEW_LINE);
        doc.append(TAB).append(" * ").append(String.format("@return {@link #%s}", attribute.getJavaClass().getClazz())).append(NEW_LINE);
        doc.append(TAB).append(" */");
        return doc.toString();
    }

    public boolean isJavaDocExist() {
        return StringUtils.isNotBlank(description);
    }

    public boolean isPropertyJavaDocExist() {
        return (isJavaDocExist() && CodePanel.isPropertyJavaDoc())
                || getCustomSnippet(PROPERTY_JAVADOC.name()) != null;
    }

    public boolean isGetterJavaDocExist() {
        return (isJavaDocExist() && CodePanel.isGetterJavaDoc())
                || getCustomSnippet(GETTER_JAVADOC.name()) != null;
    }

    public boolean isSetterJavaDocExist() {
        return (isJavaDocExist() && CodePanel.isSetterJavaDoc())
                || getCustomSnippet(SETTER_JAVADOC.name()) != null;
    }

    public boolean isFluentJavaDocExist() {
        return isJavaDocExist() && CodePanel.isFluentAPIJavaDoc();
    }

    /**
     * @return the temporal
     */
    public TemporalSnippet getTemporal() {
        return temporal;
    }

    /**
     * @param temporal the temporal to set
     */
    public void setTemporal(TemporalSnippet temporal) {
        this.temporal = temporal;
    }

    /**
     * @return the functionalType
     */
    public boolean isFunctionalType() {
        return functionalType;
    }

    /**
     * @param functionalType the functionalType to set
     */
    public void setFunctionalType(boolean functionalType) {
        this.functionalType = functionalType;
    }

    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
        return attribute;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the customSnippet
     */
    public Map<AttributeSnippetLocationType, List<String>> getCustomSnippet() {
        return customSnippet;
    }

    public List<String> getCustomSnippet(String type) {
        AttributeSnippetLocationType locationType = AttributeSnippetLocationType.valueOf(type);
        List<String> customSnippets = customSnippet.containsKey(locationType)
                ? customSnippet.get(locationType) : emptyList();
        if (locationType == IMPORT) {
            customSnippets
                    = customSnippets
                            .stream()
                            .filter(snippet -> snippet.startsWith("import"))
                            .filter(snippet -> snippet.startsWith(";"))
                            .collect(toList());
        }
        return customSnippets;
    }

    public boolean isGetterThrows() {
        return !getCustomSnippet(GETTER_THROWS.name()).isEmpty();
    }

    public String getGetterThrowsSnippet() {
        return "throws " + getCustomSnippet(GETTER_THROWS.name()).stream().collect(joining(", "));
    }

    public boolean isSetterThrows() {
        return !getCustomSnippet(SETTER_THROWS.name()).isEmpty();
    }

    public String getSetterThrowsSnippet() {
        return "throws " + getCustomSnippet(SETTER_THROWS.name()).stream().collect(joining(", "));
    }

    /**
     * @param customSnippet the customSnippet to set
     */
    public void setCustomSnippet(Map<AttributeSnippetLocationType, List<String>> customSnippet) {
        this.customSnippet = customSnippet;
    }

    /**
     * @return the orderColumn
     */
    public OrderColumnSnippet getOrderColumn() {
        return orderColumn;
    }

    /**
     * @param orderColumn the orderColumn to set
     */
    public void setOrderColumn(OrderColumnSnippet orderColumn) {
        this.orderColumn = orderColumn;
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
     * @return the convertsSnippet
     */
    public ConvertsSnippet getConverts() {
        return converts;
    }

    /**
     * @param convertsSnippet the convertsSnippet to set
     */
    public void setConverts(ConvertsSnippet convertsSnippet) {
        this.converts = convertsSnippet;
    }

        /**
     * @return the collectionType
     */
    public String getCollectionType() {
        return collectionType;
    }

    /**
     * @param collectionType the collectionType to set
     */
    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }
            
    /**
     * @return the collectionImplType
     */
    public String getCollectionImplType() {
        return collectionImplType;
    }

    /**
     * @param collectionImplType the collectionImplType to set
     */
    public void setCollectionImplType(String collectionImplType) {
        this.collectionImplType = collectionImplType;
    }
    /**
     * @return the attributeJSONBSnippets
     */
    public List<Snippet> getJSONBSnippets() {
        return attributeJSONBSnippets;
    }

    /**
     * @param attributeJSONBSnippets the attributeJSONBSnippets to set
     */
    public void setJSONBSnippets(List<Snippet> attributeJSONBSnippets) {
        this.attributeJSONBSnippets = attributeJSONBSnippets;
    }

    public String getSingularName() {
        return Inflector.getInstance().singularize(name);
    }

    public String helperMethodName() {
        return Inflector.getInstance().singularize(getMethodName());
    }

    public String getHelperMethodSnippet() {
        String singularName = getSingularName();
        String methodName = getMethodName();
        String helperMethodName = helperMethodName();
        String connectedMethodName = null;

        StringBuilder sb = new StringBuilder();

        String type = null;
        if (attribute instanceof ElementCollection) {
            type = ((ElementCollection) attribute).getAttributeType();
        } else if (attribute instanceof MultiRelationAttribute) {
            type = ((MultiRelationAttribute) attribute).getConnectedEntity().getClazz();
        }

        if (type == null) {
            return "";
        }

        type = getSimpleClassName(type);

        //add
        sb.append(String.format("public void add%s(%s %s) {",
                helperMethodName, type, singularName)).append(NEW_LINE);
        sb.append(String.format("get%s().add(%s);", methodName, singularName)).append(NEW_LINE);
        if (attribute instanceof OneToMany && !((OneToMany) attribute).isOwner()) {
            OneToMany otm = (OneToMany) attribute;
            if (otm.getConnectedAttributeName() != null) {
                connectedMethodName = JavaUtil.getMethodName("set", otm.getConnectedAttributeName());
                sb.append(String.format("%s.%s(this);", singularName, connectedMethodName)).append(NEW_LINE);
            }
        } else if (attribute instanceof ManyToMany && ((ManyToMany) attribute).isOwner()) {
            ManyToMany mtm = (ManyToMany) attribute;
            if (mtm.getConnectedAttributeName() != null) {
                connectedMethodName = JavaUtil.getMethodName("get", mtm.getConnectedAttributeName());
                sb.append(String.format("%s.%s().add(this);", singularName, connectedMethodName)).append(NEW_LINE);
            }
        }
        sb.append("}").append(NEW_LINE).append(NEW_LINE);

        //remove
        sb.append(String.format("public void remove%s(%s %s) {",
                helperMethodName, type, getSingularName())).append(NEW_LINE);
        sb.append(String.format("get%s().remove(%s);", methodName, singularName)).append(NEW_LINE);
        if (attribute instanceof OneToMany && !((OneToMany) attribute).isOwner()) {
            OneToMany otm = (OneToMany) attribute;
            if (otm.getConnectedAttributeName() != null) {
                sb.append(String.format("%s.set%s(null);", singularName, connectedMethodName)).append(NEW_LINE);
            }
        } else if (attribute instanceof ManyToMany && ((ManyToMany) attribute).isOwner()) {
            ManyToMany mtm = (ManyToMany) attribute;
            if (mtm.getConnectedAttributeName() != null) {
                sb.append(String.format("%s.get%s().remove(this);", singularName, connectedMethodName)).append(NEW_LINE);
            }
        }
        sb.append("}").append(NEW_LINE).append(NEW_LINE);

        return sb.toString();
    }
}
