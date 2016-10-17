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
import java.util.Optional;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import static org.netbeans.jcode.core.util.AttributeType.getWrapperType;
import org.netbeans.jcode.core.util.StringHelper;
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
import org.netbeans.jpa.modeler.spec.extend.Attribute;

public class VariableDefSnippet implements Snippet, AttributeOverridesHandler, AssociationOverridesHandler {

    private List<AnnotationSnippet> annotation = new ArrayList<>();
    private List<ConstraintSnippet> constraints = new ArrayList<>();
    private boolean functionalType;

    private JaxbVariableType jaxbVariableType;
    private JaxbXmlAttribute jaxbXmlAttribute;
    private JaxbXmlElement jaxbXmlElement;
    private List<JaxbXmlElement> jaxbXmlElementList;

    private boolean autoGenerate = false;
    private boolean embedded = false;
    private boolean embeddedId = false;
    private boolean lob = false;
    private boolean primaryKey = false;
    private boolean tranzient = false;
    private boolean version = false;

    private String name;
    private String description;
    private final ClassHelper classHelper = new ClassHelper();
    //TODO: See if these 2 can be as a class
    private String mapKey = null;

    private BasicSnippet basic = null;
    private ElementCollectionSnippet elementCollection = null;

    private ColumnDefSnippet columnDef = null;
    private RelationDefSnippet relationDef = null;
    private OrderBySnippet orderBy = null;
    private JoinColumnsSnippet joinColumns = null;
    private JoinTableSnippet joinTable = null;
    private CollectionTableSnippet collectionTable = null;
    private GeneratedValueSnippet generatedValue = null;
    private TableGeneratorSnippet tableGenerator = null;
    private SequenceGeneratorSnippet sequenceGenerator = null;
    private EnumeratedSnippet enumerated = null;
    private TemporalSnippet temporal = null;
    private AssociationOverridesSnippet associationOverrides = null;
    private AttributeOverridesSnippet attributeOverrides = null;

    private TypeIdentifierSnippet typeIdentifier = null;

    private Attribute attribute;

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

    public String getType() {//Modified : Collection => Collection<Entity>
        String type;
        if (this.getTypeIdentifier() != null) { //Collection<Entity> , Collection<String>
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
            type = "Optional<" + getWrapperType(type) + '>';
        }
        return type;
    }
    
    public String getReturnValue() {
        String value = "this."+getName();
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

        return "@"+ MAP_KEY + "(name=\"" + mapKey + ORMConverterUtil.QUOTE + ORMConverterUtil.CLOSE_PARANTHESES;
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

        Collection<String> importSnippets = new ArrayList<>();

        if (classHelper.getClassName() == null) {
            typeIdentifier = new TypeIdentifierSnippet(this);
            classHelper.setClassName(typeIdentifier.getVariableType());
            importSnippets.addAll(typeIdentifier.getImportSnippets());
        } else if (classHelper.getPackageName() != null) {
            importSnippets.add(classHelper.getFQClassName());
            
            if (functionalType) {
                importSnippets.add(Optional.class.getCanonicalName());
            }
        } else {
           if (functionalType) {
                importSnippets.add(Optional.class.getCanonicalName());
            } 
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

        if (this.getAttributeOverrides() != null) {
            importSnippets.addAll(this.getAttributeOverrides().getImportSnippets());
        }
        if (this.getAssociationOverrides() != null) {
            importSnippets.addAll(this.getAssociationOverrides().getImportSnippets());
        }

        for (AnnotationSnippet snippet : this.getAnnotation()) {
            importSnippets.addAll(snippet.getImportSnippets());
        }
        
        for (ConstraintSnippet snippet : this.getConstraints()) {
            importSnippets.addAll(snippet.getImportSnippets());
        }

//        if (importSnippets.contains("java.lang.Integer")) {  //BUG : remove String
//            importSnippets.remove("java.lang.Integer");
//        }
//        if(getJaxbVariableType().equals("Attribute")){
//             importSnippets.add("javax.xml.bind.annotation.XmlAttribute");
//        } else if(getJaxbVariableType().equals("Element")){
//             importSnippets.add("javax.xml.bind.annotation.XmlElement");
//        } else if(getJaxbVariableType().equals("Value")){
//             importSnippets.add("javax.xml.bind.annotation.XmlValue");
//        }
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

    public boolean isArray(String type) {
        int length = type.length();
        return type.charAt(length - 2) == '[' && type.charAt(length - 1) == ']';
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
    public List<AnnotationSnippet> getAnnotation() {
        return annotation;
    }

    /**
     * @param annotation the annotation to set
     */
    public void setAnnotation(List<AnnotationSnippet> annotation) {
        this.annotation = annotation;
    }

    /**
     * @return the constraints
     */
    public List<ConstraintSnippet> getConstraints() {
        return constraints;
    }

    /**
     * @param constraints the constraints to set
     */
    public void setConstraints(List<ConstraintSnippet> constraints) {
        this.constraints = constraints;
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
        } else if (getJaxbVariableType() == JaxbVariableType.XML_TRANSIENT && getRelationDef() == null) {
            snippet.append("@XmlTransient");
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
    
    public String getJavaDoc() {
        StringBuilder doc = new StringBuilder();
        doc.append(TAB).append("/**").append(NEW_LINE);
//        if (StringUtils.isNotBlank(description)) {
            for (String line : description.split("\\r\\n|\\n|\\r")) {
                doc.append(" * ").append(line).append(NEW_LINE);
            }
//        }
        doc.append(" */");
        return doc.toString();
    }
    
    public boolean isJavaDocExist(){
        return StringUtils.isNotBlank(description);
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
}
