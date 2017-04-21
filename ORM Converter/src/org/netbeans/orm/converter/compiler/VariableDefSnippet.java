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

import org.netbeans.orm.converter.compiler.validation.constraints.ConstraintSnippet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import static org.netbeans.jcode.core.util.AttributeType.getArrayType;
import static org.netbeans.jcode.core.util.AttributeType.getWrapperType;
import static org.netbeans.jcode.core.util.AttributeType.isArray;
import org.netbeans.jcode.core.util.StringHelper;
import static org.netbeans.jcode.core.util.StringHelper.firstLower;
import static org.netbeans.jcode.core.util.StringHelper.firstUpper;
import static org.netbeans.jcode.jpa.JPAConstants.ELEMENT_COLLECTION_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.EMBEDDED_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.EMBEDDED_ID_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.GENERATED_VALUE_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.ID_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.LOB_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.MAP_KEY;
import static org.netbeans.jcode.jpa.JPAConstants.MAP_KEY_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.TRANSIENT_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.VERSION_FQN;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlAttribute;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlElement;
import org.netbeans.orm.converter.compiler.extend.AssociationOverridesHandler;
import org.netbeans.orm.converter.compiler.extend.AttributeOverridesHandler;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConverterUtil;
import static org.netbeans.orm.converter.util.ORMConverterUtil.NEW_LINE;
import static org.netbeans.orm.converter.util.ORMConverterUtil.TAB;
import static org.netbeans.jcode.jpa.JPAConstants.GENERATION_TYPE_FQN;
import org.netbeans.jpa.modeler.settings.code.CodePanel;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.AttributeAnnotationLocationType;
import org.netbeans.jpa.modeler.spec.extend.AttributeSnippetLocationType;
import org.netbeans.orm.converter.util.ImportSet;
import org.openide.util.Exceptions;

public class VariableDefSnippet implements Snippet, AttributeOverridesHandler, AssociationOverridesHandler {

    private List<ConstraintSnippet> attributeConstraints = new ArrayList<>();
    private List<ConstraintSnippet> keyConstraints = new ArrayList<>();
    private List<ConstraintSnippet> valueConstraints = new ArrayList<>();
    private boolean functionalType;

    private JaxbVariableType jaxbVariableType;
    private JaxbXmlAttribute jaxbXmlAttribute;
    private JaxbXmlElement jaxbXmlElement;
    private List<JaxbXmlElement> jaxbXmlElementList;

    private boolean autoGenerate;
    private boolean embedded;
    private boolean embeddedId;
    private boolean lob;
    private boolean primaryKey;
    private boolean tranzient;
    private boolean version;

    private String name;
    private String defaultValue;
    private String description;
    private boolean propertyChangeSupport;
    private boolean vetoableChangeSupport;
    private final ClassHelper classHelper = new ClassHelper();
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

    public String getConstraintType() {
        String type = null;
        if (this.getTypeIdentifier() != null) { 
            type = this.getTypeIdentifier().getConstraintVariableType();
        } else if(isArray(classHelper.getClassName())) {
            String constraint= null;
            try {
                constraint = getInlineValueConstraint();
            } catch (InvalidDataException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (constraint != null) {
                type = getArrayType(classHelper.getClassName()) + " " + constraint + "[]" ;
            }
        } 
        
        if(type == null){
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

        if ((this.getTypeIdentifier() == null || getRelationDef() instanceof SingleRelationAttributeSnippet) && functionalType) {
            if (isArray(type)) {
                type = "Optional<" + type + '>';
            } else {
                type = "Optional<" + getWrapperType(type) + '>';
            }
        }
        return type;
    }

    public String getReturnValue() {
        String value = "this." + getName();
        if ((this.getTypeIdentifier() == null || getRelationDef() instanceof SingleRelationAttributeSnippet) && functionalType) {
            value = "Optional.ofNullable(" + value + ')';
        }
        return value;
    }

    public void setType(String type) {
        classHelper.setClassName(type);
    }

    public String getName() {
        return name;
    }
    
    public String getFluentMethodName() {
        if(StringUtils.isNotBlank(CodePanel.getFluentAPIPrefix())){
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

    public boolean isAutoGenerate() {
        return autoGenerate;
    }

    public void setAutoGenerate(boolean autoGenerate) {
        this.autoGenerate = autoGenerate;
    }

    /**
     * @return the a derived methodName from variableName Eg nickname -->
     * getNickname()
     */
    public String getMethodName() {
        return StringHelper.getMethodName(name);
    }

    public String getPropName() {
        return "PROP_" + name.toUpperCase();
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

    public TypeIdentifierSnippet getTypeIdentifier() {
        return typeIdentifier;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {

        ImportSet importSnippets = new ImportSet();

        if (classHelper.getClassName() == null) {
            typeIdentifier = new TypeIdentifierSnippet(this);
            classHelper.setClassName(typeIdentifier.getVariableType());
            importSnippets.addAll(typeIdentifier.getImportSnippets());
        } else if (classHelper.getPackageName() != null) {
            importSnippets.add(classHelper.getFQClassName());
        }
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

        if (autoGenerate) {
            importSnippets.add(GENERATION_TYPE_FQN);
            importSnippets.add(GENERATED_VALUE_FQN);
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

        for (AnnotationSnippet snippet : this.getAnnotation().values().stream().flatMap(annot -> annot.stream()).collect(toList())) {
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

        if (getJaxbVariableType() == JaxbVariableType.XML_INVERSE_REFERENCE && getRelationDef() != null){
             importSnippets.add("org.eclipse.persistence.oxm.annotations.XmlInverseReference");
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

    public boolean isPrimitive(String type) {
        return "boolean".equals(type) || "byte".equals(type)
                || "short".equals(type) || "char".equals(type)
                || "int".equals(type) || "long".equals(type)
                || "float".equals(type) || "double".equals(type);
    }

    public boolean isPrimitiveArray(String type) {
        int length = type.length();
        if (isArray(type)) {
            String premitiveType = type.substring(0, length - 2);
            return isPrimitive(premitiveType);
        } else {
            return false;
        }
    }

    public Class getWrapper(String premitive) throws ClassNotFoundException {
        return ClassUtils.primitiveToWrapper(ClassUtils.getClass(premitive));
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
//        System.out.println("jaxbVariableType==null?\"\":jaxbVariableType.getType() : " + jaxbVariableType==null?"":jaxbVariableType.getType());
        return jaxbVariableType;//==null?"":jaxbVariableType.getType();
    }

    /**
     * @param jaxbVariableType the jaxbVariableType to set
     */
    public void setJaxbVariableType(JaxbVariableType jaxbVariableType) {
        this.jaxbVariableType = jaxbVariableType;
    }

    /**
     * @return the jaxbXmlAttribute
     */
    public JaxbXmlAttribute getJaxbXmlAttribute() {
        return jaxbXmlAttribute;
    }

    /**
     * @param jaxbXmlAttribute the jaxbXmlAttribute to set
     */
    public void setJaxbXmlAttribute(JaxbXmlAttribute jaxbXmlAttribute) {
        this.jaxbXmlAttribute = jaxbXmlAttribute;
    }

    /**
     * @return the jaxbXmlElement
     */
    public JaxbXmlElement getJaxbXmlElement() {
        return jaxbXmlElement;
    }

    /**
     * @param jaxbXmlElement the jaxbXmlElement to set
     */
    public void setJaxbXmlElement(JaxbXmlElement jaxbXmlElement) {
        this.jaxbXmlElement = jaxbXmlElement;
    }

    /**
     * @return the jaxbXmlElementList
     */
    public List<JaxbXmlElement> getJaxbXmlElementList() {
        return jaxbXmlElementList;
    }

    /**
     * @param jaxbXmlElementList the jaxbXmlElementList to set
     */
    public void setJaxbXmlElementList(List<JaxbXmlElement> jaxbXmlElementList) {
        this.jaxbXmlElementList = jaxbXmlElementList;
    }

    public String getJaxbAnnotationSnippet() {

        StringBuilder snippet = new StringBuilder();

        if (getJaxbVariableType() == JaxbVariableType.XML_ATTRIBUTE || getJaxbVariableType() == JaxbVariableType.XML_LIST_ATTRIBUTE) {
            if (getJaxbVariableType() == JaxbVariableType.XML_LIST_ATTRIBUTE) {
                snippet.append("@XmlList").append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
            }
            snippet.append("@XmlAttribute");
            JaxbXmlAttribute attr = this.getJaxbXmlAttribute();
            if ((attr.getName() != null && !attr.getName().isEmpty()) || (attr.getRequired() != null && attr.getRequired())) {
                snippet.append("(");
                if (attr.getName() != null && !attr.getName().isEmpty()) {
                    snippet.append("name = \"").append(attr.getName()).append("\", ");
                }
                if (attr.getRequired() != null) {
                    snippet.append("required = ").append(attr.getRequired()).append(", ");
                }
                snippet.setLength(snippet.length() - 2);
                snippet.append(")");
            }
        } else if (getJaxbVariableType() == JaxbVariableType.XML_ELEMENT || getJaxbVariableType() == JaxbVariableType.XML_LIST_ELEMENT) {
            if (getJaxbVariableType() == JaxbVariableType.XML_LIST_ELEMENT) {
                snippet.append("@XmlList").append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
            }
            snippet.append("@XmlElement");
            JaxbXmlElement ele = this.getJaxbXmlElement();
            if ((ele.getName() != null && !ele.getName().isEmpty()) || (ele.getRequired() != null && ele.getRequired())) {
                snippet.append("(");
                if (ele.getName() != null && !ele.getName().isEmpty()) {
                    snippet.append("name = \"").append(ele.getName()).append("\", ");
                }
                if (ele.getRequired() != null) {
                    snippet.append("required = ").append(ele.getRequired()).append(", ");
                }
                snippet.setLength(snippet.length() - 2);
                snippet.append(")");
            }
        } else if (getJaxbVariableType() == JaxbVariableType.XML_ELEMENTS) { //pending
            snippet.append("@XmlElements");
        } else if (getJaxbVariableType() == JaxbVariableType.XML_VALUE || getJaxbVariableType() == JaxbVariableType.XML_LIST_VALUE) {
            if (getJaxbVariableType() == JaxbVariableType.XML_LIST_VALUE) {
                snippet.append("@XmlList").append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
            }
            snippet.append("@XmlValue");
        } else if (getJaxbVariableType() == JaxbVariableType.XML_TRANSIENT) {
            snippet.append("@XmlTransient");
        }  else if (getJaxbVariableType() == JaxbVariableType.XML_INVERSE_REFERENCE && getRelationDef() != null) {
            String mappedBy = getRelationDef().getTargetField();//both side are applicable so targetField is used instead of mappedBy
            if (mappedBy != null) {
                snippet.append(String.format("@XmlInverseReference(mappedBy=\"%s\")", mappedBy));
            } 
        } else if (getJaxbVariableType() == JaxbVariableType.XML_ELEMENT_REF) {
            snippet.append("@XmlElementRef");
        } else {
            if (isPrimaryKey()) {
//            snippet.append("@XmlID").append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
            } else if (getRelationDef() != null) {
                if (getRelationDef() instanceof OneToOneSnippet) {
                    OneToOneSnippet otoSnippet = (OneToOneSnippet) getRelationDef();
                    if (otoSnippet.getMappedBy() != null && !otoSnippet.getMappedBy().trim().isEmpty()) {
                        snippet.append("@XmlTransient");
                    } else {
//                      snippet.append("@XmlIDREF").append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
                    }
                } else if (getRelationDef() instanceof OneToManySnippet) {
                    OneToManySnippet otmSnippet = (OneToManySnippet) getRelationDef();
                    if (otmSnippet.getMappedBy() != null && !otmSnippet.getMappedBy().trim().isEmpty()) {
                        snippet.append("@XmlTransient");
                    } else {
//                      snippet.append("@XmlIDREF").append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
                    }
                } else if (getRelationDef() instanceof ManyToOneSnippet) {
//                   snippet.append("@XmlIDREF").append(ORMConverterUtil.NEW_LINE).append(ORMConverterUtil.TAB);
                } else if (getRelationDef() instanceof ManyToManySnippet) {
                    ManyToManySnippet mtmSnippet = (ManyToManySnippet) getRelationDef();
                    if (mtmSnippet.getMappedBy() != null && !mtmSnippet.getMappedBy().trim().isEmpty()) {
                        snippet.append("@XmlTransient");
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
        StringBuilder doc = getJavaDoc(null);
        doc.append(TAB).append(" */");
        return doc.toString();
    }
    
    public String getGetterJavaDoc() {
        StringBuilder doc = getJavaDoc("Get ");
        doc.append(TAB).append(" * ").append(NEW_LINE);
        doc.append(TAB).append(" * ").append(String.format("@return {@link #%s}",getName())).append(NEW_LINE);
        doc.append(TAB).append(" */");
        return doc.toString();
    }
        
    public String getSetterJavaDoc() {
        StringBuilder doc = getJavaDoc("Set ");
        doc.append(TAB).append(" * ").append(NEW_LINE);
        doc.append(TAB).append(" * ").append(String.format("@param %s {@link #%s}",getName(),getName())).append(NEW_LINE);
        doc.append(TAB).append(" */");
        return doc.toString();
    }
            
    public String getFluentJavaDoc() {
        StringBuilder doc = getJavaDoc("Set ");
        doc.append(TAB).append(" * ").append(NEW_LINE);
        doc.append(TAB).append(" * ").append(String.format("@param %s {@link #%s}",getName(),getName())).append(NEW_LINE);
        doc.append(TAB).append(" * ").append(String.format("@return {@link #%s}",attribute.getJavaClass().getClazz())).append(NEW_LINE);
        doc.append(TAB).append(" */");
        return doc.toString();
    }
    
    
    
    public boolean isJavaDocExist() {
        return StringUtils.isNotBlank(description);
    }
    
    public boolean isPropertyJavaDocExist() {
        return isJavaDocExist() && CodePanel.isPropertyJavaDoc();
    }
    
    public boolean isGetterJavaDocExist() {
        return isJavaDocExist() && CodePanel.isGetterJavaDoc();
    }
    
    public boolean isSetterJavaDocExist() {
        return isJavaDocExist() && CodePanel.isSetterJavaDoc();
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
        return customSnippet.get(AttributeSnippetLocationType.valueOf(type));
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
}
