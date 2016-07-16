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
package org.netbeans.jpa.modeler.spec.extend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.StringUtils;
import static org.netbeans.jcode.core.util.AttributeType.BIGDECIMAL;
import static org.netbeans.jcode.core.util.AttributeType.STRING;
import static org.netbeans.jcode.core.util.AttributeType.STRING_FQN;
import static org.netbeans.jcode.core.util.JavaSourceHelper.getSimpleClassName;
import static org.netbeans.jcode.jpa.JPAConstants.MAP_KEY_COLUMN_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.MAP_KEY_ENUMERATED_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.MAP_KEY_TEMPORAL_FQN;
import org.netbeans.jpa.modeler.spec.AttributeOverride;
import org.netbeans.jpa.modeler.spec.Convert;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.EnumType;
import org.netbeans.jpa.modeler.spec.ForeignKey;
import org.netbeans.jpa.modeler.spec.MapKey;
import org.netbeans.jpa.modeler.spec.MapKeyClass;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.OrderColumn;
import org.netbeans.jpa.modeler.spec.TemporalType;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;
import org.netbeans.jpa.source.JavaSourceParserUtil;
import static org.netbeans.jpa.source.JavaSourceParserUtil.isEmbeddableClass;
import static org.netbeans.jpa.source.JavaSourceParserUtil.isEntityClass;
import static org.netbeans.jpa.source.JavaSourceParserUtil.loadEmbeddableClass;
import static org.netbeans.jpa.source.JavaSourceParserUtil.loadEntityClass;

/**
 *
 * @author Gaurav Gupta
 */
@XmlType(propOrder = {
    "orderBy",
    "orderColumn",
    "mapKeyTemporal",
    "mapKeyEnumerated",
    "mapKeyAttributeOverride",
    "mapKeyConvert",
    "mapKeyColumn",
    "mapKeyJoinColumn",
    "mapKeyForeignKey"
})
public abstract class MultiRelationAttribute extends RelationAttribute implements CollectionTypeHandler, MapKeyHandler{

    @XmlElement(name = "order-by")
    protected String orderBy;
    @XmlElement(name = "order-column")
    protected OrderColumn orderColumn;//REVENG PENDING
    @XmlAttribute(name = "own")
    private Boolean owner;
    @XmlTransient//(name = "mapped-by")
    protected String mappedBy;
    @XmlAttribute(name = "collection-type")
    private String collectionType;//custom added

    @XmlElement(name = "map-key-convert")
    protected List<Convert> mapKeyConvert;//REVENG PENDING

    @XmlAttribute(name = "mkt")
    private MapKeyType mapKeyType;
    
    //Existing MapKeyType
    @XmlAttribute(name = "mkat-ref")//attribute-ref
    @XmlIDREF
    private Attribute mapKeyAttribute;
    @XmlTransient//@XmlElement(name = "map-key")//Not required
    protected MapKey mapKey;//only required in rev-eng and stored to mapKeyAttribute
     //@XmlElement(name = "map-key-class")//Not required
     //protected MapKeyClass mapKeyClass; //rev-eng done and stored to mapKeyAttributeType

    
    //New MapKeyType - Basic
    @XmlAttribute(name="mkat")
    private String mapKeyAttributeType; //e.g String, int, Enum, Date    applicable for basic,enumerated,temporal
    @XmlElement(name = "mkc")
    protected Column mapKeyColumn;
    @XmlElement(name = "mktemp")
    protected TemporalType mapKeyTemporal;
    @XmlElement(name = "mkenum")
    protected EnumType mapKeyEnumerated;
    
    //New MapKeyType - Entity
    @XmlAttribute(name = "mken-ref")//entity-ref
    @XmlIDREF
    private Entity mapKeyEntity;
    @XmlElement(name = "mkjc")
    protected List<JoinColumn> mapKeyJoinColumn;
    @XmlElement(name = "mkfk")
    protected ForeignKey mapKeyForeignKey;
            
    //New MapKeyType - Embeddable
    @XmlAttribute(name = "mkem-ref")
    @XmlIDREF
    private Embeddable mapKeyEmbeddable;
    @XmlElement(name = "mkao")
    protected Set<AttributeOverride> mapKeyAttributeOverride; 
    

    @Override
    public void loadAttribute(EntityMappings entityMappings, Element element, VariableElement variableElement,AnnotationMirror relationAnnotationMirror) {
        super.loadAttribute(entityMappings, element, variableElement, relationAnnotationMirror);

        AnnotationMirror orderByMirror = JavaSourceParserUtil.findAnnotation(element, "javax.persistence.OrderBy");
        if (orderByMirror != null) {
            Object value = JavaSourceParserUtil.findAnnotationValue(orderByMirror, "value");
            this.orderBy = value == null ? StringUtils.EMPTY : value.toString();
        }

        this.mappedBy = (String) JavaSourceParserUtil.findAnnotationValue(relationAnnotationMirror, "mappedBy");
        this.collectionType = ((DeclaredType) variableElement.asType()).asElement().toString();
        Class collectionTypeClass = null;
        try {
            collectionTypeClass = Class.forName(this.collectionType);
        } catch (ClassNotFoundException ex) {
        }
        boolean mapKeyExist = collectionTypeClass!=null && Map.class.isAssignableFrom(collectionTypeClass);

        DeclaredType declaredType = (DeclaredType) JavaSourceParserUtil.findAnnotationValue(relationAnnotationMirror, "targetEntity");
        if (declaredType == null) {
            if (variableElement.asType() instanceof ErrorType) { //variable => "<any>"
                throw new TypeNotPresentException(this.name + " type not found", null);
            }
            declaredType = (DeclaredType) ((DeclaredType) variableElement.asType()).getTypeArguments().get(mapKeyExist?1:0);
        }
        this.targetEntity = declaredType.asElement().getSimpleName().toString();
        
        if (mapKeyExist) {
            this.mapKey = new MapKey().load(element, null);
            this.mapKeyType = this.mapKey!=null?MapKeyType.EXT:MapKeyType.NEW;
            
            DeclaredType keyDeclaredType = MapKeyClass.getDeclaredType(element);
            if (keyDeclaredType == null) {
                keyDeclaredType = (DeclaredType) ((DeclaredType) variableElement.asType()).getTypeArguments().get(0);
            }
            if (isEmbeddableClass(keyDeclaredType.asElement())) {
                loadEmbeddableClass(entityMappings, element, variableElement, keyDeclaredType);
                this.mapKeyAttributeType = getSimpleClassName(keyDeclaredType.toString());
            } else if (isEntityClass(keyDeclaredType.asElement())) {
                loadEntityClass(entityMappings, element, variableElement, keyDeclaredType);
                this.mapKeyAttributeType = getSimpleClassName(keyDeclaredType.toString());
            } else {
                 this.mapKeyAttributeType = keyDeclaredType.toString();
            }
            
            this.mapKeyColumn = new Column().load(element, JavaSourceParserUtil.findAnnotation(element, MAP_KEY_COLUMN_FQN));
            this.mapKeyTemporal = TemporalType.load(element, JavaSourceParserUtil.findAnnotation(element, MAP_KEY_TEMPORAL_FQN));
            this.mapKeyEnumerated = EnumType.load(element, JavaSourceParserUtil.findAnnotation(element, MAP_KEY_ENUMERATED_FQN));
            
            AnnotationMirror joinColumnsAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, "javax.persistence.MapKeyJoinColumns");
            if (joinColumnsAnnotationMirror != null) {
                List joinColumnsAnnot = (List) JavaSourceParserUtil.findAnnotationValue(joinColumnsAnnotationMirror, "value");
                if (joinColumnsAnnot != null) {
                    for (Object joinColumnObj : joinColumnsAnnot) {
                        this.getMapKeyJoinColumn().add(new JoinColumn().load(element, (AnnotationMirror) joinColumnObj));
                    }
                }
            } else {
                AnnotationMirror joinColumnAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, "javax.persistence.MapKeyJoinColumn");
                if (joinColumnAnnotationMirror != null) {
                    this.getMapKeyJoinColumn().add(new JoinColumn().load(element, joinColumnAnnotationMirror));
                }
            }
            
            this.mapKeyForeignKey = ForeignKey.load(element, null);
            this.getMapKeyAttributeOverride().addAll(AttributeOverride.load(element));
        
        }
    }

    /**
     * Gets the value of the orderBy property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * Sets the value of the orderBy property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setOrderBy(String value) {
        this.orderBy = value;
    }

    /**
     * Gets the value of the orderColumn property.
     *
     * @return possible object is {@link OrderColumn }
     *
     */
    public OrderColumn getOrderColumn() {
        return orderColumn;
    }

    /**
     * Sets the value of the orderColumn property.
     *
     * @param value allowed object is {@link OrderColumn }
     *
     */
    public void setOrderColumn(OrderColumn value) {
        this.orderColumn = value;
    }

    /**
     * Gets the value of the mapKey property.
     *
     * @return possible object is {@link MapKey }
     *
     */
    public MapKey getMapKey() {
        return mapKey;
    }

    /**
     * Sets the value of the mapKey property.
     *
     * @param value allowed object is {@link MapKey }
     *
     */
    public void setMapKey(MapKey value) {
        this.mapKey = value;
    }


    /**
     * Gets the value of the mapKeyTemporal property.
     *
     * @return possible object is {@link TemporalType }
     *
     */
    public TemporalType getMapKeyTemporal() {
        return mapKeyTemporal;
    }

    /**
     * Sets the value of the mapKeyTemporal property.
     *
     * @param value allowed object is {@link TemporalType }
     *
     */
    public void setMapKeyTemporal(TemporalType value) {
        this.mapKeyTemporal = value;
    }

    /**
     * Gets the value of the mapKeyEnumerated property.
     *
     * @return possible object is {@link EnumType }
     *
     */
    public EnumType getMapKeyEnumerated() {
        return mapKeyEnumerated;
    }

    /**
     * Sets the value of the mapKeyEnumerated property.
     *
     * @param value allowed object is {@link EnumType }
     *
     */
    public void setMapKeyEnumerated(EnumType value) {
        this.mapKeyEnumerated = value;
    }

    /**
     * Gets the value of the mapKeyAttributeOverride property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the mapKeyAttributeOverride property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMapKeyAttributeOverride().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttributeOverride }
     *
     *
     * @return
     */
    public Set<AttributeOverride> getMapKeyAttributeOverride() {
        if (mapKeyAttributeOverride == null) {
            mapKeyAttributeOverride = new TreeSet<>();
        }
        return this.mapKeyAttributeOverride;
    }

    /**
     * Gets the value of the mapKeyConvert property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the mapKeyConvert property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMapKeyConvert().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Convert }
     *
     *
     */
    public List<Convert> getMapKeyConvert() {
        if (mapKeyConvert == null) {
            mapKeyConvert = new ArrayList<Convert>();
        }
        return this.mapKeyConvert;
    }

    /**
     * Gets the value of the mapKeyColumn property.
     *
     * @return possible object is {@link MapKeyColumn }
     *
     */
    public Column getMapKeyColumn() {
        if(mapKeyColumn==null){
            mapKeyColumn = new Column();
        }
        return mapKeyColumn;
    }

    /**
     * Sets the value of the mapKeyColumn property.
     *
     * @param value allowed object is {@link MapKeyColumn }
     *
     */
    public void setMapKeyColumn(Column value) {
        this.mapKeyColumn = value;
    }

    /**
     * Gets the value of the mapKeyJoinColumn property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the mapKeyJoinColumn property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMapKeyJoinColumn().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MapKeyJoinColumn }
     *
     *
     */
    public List<JoinColumn> getMapKeyJoinColumn() {
        if (mapKeyJoinColumn == null) {
            mapKeyJoinColumn = new ArrayList<>();
        }
        return this.mapKeyJoinColumn;
    }

    /**
     * Gets the value of the mapKeyForeignKey property.
     *
     * @return possible object is {@link ForeignKey }
     *
     */
    public ForeignKey getMapKeyForeignKey() {
        return mapKeyForeignKey;
    }

    /**
     * Sets the value of the mapKeyForeignKey property.
     *
     * @param value allowed object is {@link ForeignKey }
     *
     */
    public void setMapKeyForeignKey(ForeignKey value) {
        this.mapKeyForeignKey = value;
    }

    /**
     * Gets the value of the mappedBy property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getMappedBy() {
        if (Boolean.FALSE.equals(isOwner())) {
            if (mappedBy != null) {
                return mappedBy;
            }
            if (getConnectedAttribute() == null) {
                return null;
            }
            return getConnectedAttribute().getName();
        } else {
            return null;
        }
    }

    /**
     * Sets the value of the mappedBy property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setMappedBy(String value) {
        this.mappedBy = value;
        this.owner = StringUtils.isBlank(mappedBy);
    }

    /**
     * @return the collectionType
     */
    public String getCollectionType() {
        if (collectionType == null) {
            collectionType = "java.util.List";
        }
        return collectionType;
    }

    /**
     * @param collectionType the collectionType to set
     */
    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    @Override
    public List<JaxbVariableType> getJaxbVariableList() {
        if (mappedBy != null && !mappedBy.trim().isEmpty()) {
            return null;
        } else {
            return super.getJaxbVariableList();
        }
    }

    /**
     * @return the owner
     */
    public boolean isOwner() {
        if (owner == null) {
            return Boolean.FALSE;
        }
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(boolean owner) {
        this.owner = owner;
        if (owner) {
            mappedBy = null;
        }

    }

    @Override
    public String getDataTypeLabel() {
        if (getValidatedMapKeyType() == null) {
            return String.format("%s<%s>", getCollectionType(), getTargetEntity());
        } else {
            return String.format("%s<%s, %s>", getCollectionType(), getMapKeyDataTypeLabel(), getTargetEntity());
        }
    }

        /**
     * @return the mapKeyAttribute
     */
    public Attribute getMapKeyAttribute() {
        return mapKeyAttribute;
    }

    /**
     * @param mapKeyAttribute the mapKeyAttribute to set
     */
    public void setMapKeyAttribute(Attribute mapKeyAttribute) {
        resetMapAttributeExceptExisting();
        this.mapKeyAttribute = mapKeyAttribute;
    }
    
    public MapKeyType getValidatedMapKeyType() {
        if(mapKeyAttribute != null){
            return MapKeyType.EXT;
        } else if(mapKeyAttributeType != null || mapKeyEmbeddable != null || mapKeyEntity != null){
            return MapKeyType.NEW;
        }
        return null;
    }
    
    /**
     * @return the mapKeyType
     */
    public MapKeyType getMapKeyType() {
        if(mapKeyType==null){
            return MapKeyType.EXT;
        }
        return mapKeyType;
    }

    @Override
    public void setMapKeyType(MapKeyType mapKeyType) {
        this.mapKeyType=mapKeyType;
    }
    
    /**
     * @return the mapKeyAttributeType
     */
    public String getMapKeyAttributeType() {
        return mapKeyAttributeType;
    }

    /**
     * @param mapKeyAttributeType the mapKeyAttributeType to set
     */
    public void setMapKeyAttributeType(String mapKeyAttributeType) {
        this.mapKeyAttributeType = mapKeyAttributeType;
    }

    /**
     * @return the mapKeyEntity
     */
    public Entity getMapKeyEntity() {
        return mapKeyEntity;
    }

    /**
     * @param mapKeyEntity the mapKeyEntity to set
     */
    public void setMapKeyEntity(Entity mapKeyEntity) {
        resetMapAttributeExceptEntity();
        this.mapKeyEntity = mapKeyEntity;
    }

    /**
     * @return the mapKeyEmbeddable
     */
    public Embeddable getMapKeyEmbeddable() {
        return mapKeyEmbeddable;
    }

    /**
     * @param mapKeyEmbeddable the mapKeyEmbeddable to set
     */
    public void setMapKeyEmbeddable(Embeddable mapKeyEmbeddable) {
        resetMapAttributeExceptEmbeddable();
        this.mapKeyEmbeddable = mapKeyEmbeddable;
    }
    
    @Override
    public void resetMapAttribute(){
        this.mapKeyAttribute=null;
        
        this.mapKeyEntity=null;
        this.mapKeyEmbeddable=null;
        this.mapKeyEnumerated=null;
        this.mapKeyTemporal=null;
        this.mapKeyAttributeType=null;
        
        this.mapKeyColumn=null;
        this.mapKeyJoinColumn=null;
        this.mapKeyForeignKey=null;
        this.mapKeyAttributeOverride=null;
    }
    
    public void resetMapAttributeExceptExisting(){
        this.mapKeyEntity=null;
        this.mapKeyEmbeddable=null;
        this.mapKeyEnumerated=null;
        this.mapKeyTemporal=null;
        this.mapKeyAttributeType=null;
        
        this.mapKeyColumn=null;
        this.mapKeyJoinColumn=null;
        this.mapKeyForeignKey=null;
        this.mapKeyAttributeOverride=null;
    }
    
    public void resetMapAttributeExceptBasic(){
        this.mapKeyEnumerated=null;
        this.mapKeyTemporal=null;
        this.mapKeyAttributeType=null;
        this.mapKeyColumn=null;
    }
    
    public void resetMapAttributeExceptEmbeddable(){
        this.mapKeyAttribute=null;
        
        this.mapKeyEntity=null;
        this.mapKeyEnumerated=null;
        this.mapKeyTemporal=null;
        this.mapKeyAttributeType=null;
        
        this.mapKeyColumn=null;
        this.mapKeyJoinColumn=null;
        this.mapKeyForeignKey=null;
    }
    
    public void resetMapAttributeExceptEntity(){
        this.mapKeyAttribute=null;
        
        this.mapKeyEmbeddable=null;
        this.mapKeyEnumerated=null;
        this.mapKeyTemporal=null;
        this.mapKeyAttributeType=null;
        
        this.mapKeyColumn=null;
        this.mapKeyAttributeOverride=null;
    }
    
        @Override
    public String getMapKeyDataTypeLabel(){
        if(mapKeyType == MapKeyType.EXT && mapKeyAttribute!=null){
            return mapKeyAttribute.getDataTypeLabel();
        } else {
            if(mapKeyEntity!=null){
                return mapKeyEntity.getClazz();
            } else if(mapKeyEmbeddable!=null){
                return mapKeyEmbeddable.getClazz();
            } else if(mapKeyAttributeType!=null){
                return mapKeyAttributeType;
            }
        }
        return null;
    }
    
    public boolean isTextMapKeyAttributeType() {
        if (STRING.equals(getMapKeyAttributeType()) || STRING_FQN.equals(getMapKeyAttributeType())) {
            return true;
        }
        return false;
    }
    
    public boolean isPrecisionpMapKeyAttributeType() {
        if (BIGDECIMAL.equals(getMapKeyAttributeType())) {
            return true;
        }
        return false;
    }

    public boolean isScaleMapKeyAttributeType() {
        if (BIGDECIMAL.equals(getMapKeyAttributeType())) {
            return true;
        }
        return false;
    }


}
