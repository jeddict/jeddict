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
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import static org.netbeans.jcode.core.util.JavaSourceHelper.getSimpleClassName;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.ForeignKey;
import org.netbeans.jpa.modeler.spec.IdClass;
import org.netbeans.jpa.modeler.spec.IdentifiableClass;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.source.JavaSourceParserUtil;

/**
 *
 * @author Gaurav Gupta
 */
@XmlType(propOrder = {
    "joinColumn",
    "foreignKey"
})
public abstract class SingleRelationAttribute extends RelationAttribute implements JoinColumnHandler {

    @XmlElement(name = "join-column")
    private List<JoinColumn> joinColumn;
    @XmlElement(name = "fk")
    protected ForeignKey foreignKey;//joinColumns foreignKey

    @XmlAttribute(name = "optional")
    protected Boolean optional;
    @XmlAttribute
    private Boolean primaryKey;//id=>primaryKey changed to prevent BaseElement.id field hiding
    @XmlAttribute(name = "maps-id")
    private String mapsId;//used in case of EmbeddedId
    

    @Override
    public void loadAttribute(EntityMappings entityMappings, Element element, VariableElement variableElement, AnnotationMirror annotationMirror) {
        super.loadAttribute(entityMappings, element, variableElement, annotationMirror);
        if (JavaSourceParserUtil.isAnnotatedWith(element, "javax.persistence.Id")) {
            this.setPrimaryKey(Boolean.TRUE);
        }

        AnnotationMirror joinColumnsAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, "javax.persistence.JoinColumns");
        if (joinColumnsAnnotationMirror != null) {
            List joinColumnsAnnot = (List) JavaSourceParserUtil.findAnnotationValue(joinColumnsAnnotationMirror, "value");
            if (joinColumnsAnnot != null) {
                for (Object joinColumnObj : joinColumnsAnnot) {
                    this.getJoinColumn().add(new JoinColumn().load(element, (AnnotationMirror) joinColumnObj));
                }
            }
        } else {
            AnnotationMirror joinColumnAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, "javax.persistence.JoinColumn");
            if (joinColumnAnnotationMirror != null) {
                this.getJoinColumn().add(new JoinColumn().load(element, joinColumnAnnotationMirror));
            }
        }

        this.optional = (Boolean) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "optional");
        
        AnnotationMirror mapsIdAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, "javax.persistence.MapsId");
        AnnotationMirror idAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, "javax.persistence.Id");

        this.primaryKey = mapsIdAnnotationMirror != null || idAnnotationMirror != null;
        if (mapsIdAnnotationMirror != null) {
            this.mapsId = (String) JavaSourceParserUtil.findAnnotationValue(mapsIdAnnotationMirror, "value");
        }
        
        AnnotationMirror foreignKeyValue = (AnnotationMirror) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "foreignKey");
        if (foreignKeyValue != null) {
            this.foreignKey = ForeignKey.load(element, foreignKeyValue);
        }
        
        DeclaredType declaredType = (DeclaredType) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "targetEntity");
        if (declaredType == null) { 
            if (variableElement.asType() instanceof ErrorType) { //variable => "<any>"
                throw new TypeNotPresentException(this.name + " type not found", null);
            }
           this.targetEntity = getSimpleClassName(variableElement.asType().toString());
        } else {
            this.targetEntity = declaredType.asElement().getSimpleName().toString();
        }
        
    }

    /**
     * Gets the value of the joinColumn property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the joinColumn property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getJoinColumn().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JoinColumn }
     *
     *
     */
    @Override
    public List<JoinColumn> getJoinColumn() {
        if (joinColumn == null) {
            joinColumn = new ArrayList<JoinColumn>();
        }
        return this.joinColumn;
    }

    @Override
    public void addJoinColumn(JoinColumn joinColumn_In) {
        if (joinColumn == null) {
            joinColumn = new ArrayList<JoinColumn>();
        }
        joinColumn.add(joinColumn_In);
    }

    @Override
    public void removeJoinColumn(JoinColumn joinColumn_In) {
        if (joinColumn == null) {
            joinColumn = new ArrayList<JoinColumn>();
        }
        joinColumn.remove(joinColumn_In);
    }

    /**
     * Gets the value of the foreignKey property.
     *
     * @return possible object is {@link ForeignKey }
     *
     */
    public ForeignKey getForeignKey() {
        if(foreignKey==null){
            foreignKey = new ForeignKey();
        }
        return foreignKey;
    }

    /**
     * Sets the value of the foreignKey property.
     *
     * @param value allowed object is {@link ForeignKey }
     *
     */
    public void setForeignKey(ForeignKey value) {
        this.foreignKey = value;
    }

    /**
     * Gets the value of the optional property.
     *
     * @return possible object is {@link Boolean }
     *
     */
    public Boolean getOptional() {//isOptional()
        if (optional == null) {
            optional = true;
        }
        return optional;
    }

    /**
     * Sets the value of the optional property.
     *
     * @param value allowed object is {@link Boolean }
     *
     */
    public void setOptional(Boolean value) {
        this.optional = value;
    }

    /**
     * @return the primaryKey
     */
    public Boolean isPrimaryKey() {
        if (primaryKey == null) {
            primaryKey = Boolean.FALSE;
        }
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }
    
    public IdClass getIdClass(){
        if (isPrimaryKey()) {
            JavaClass javaClass = this.getJavaClass();
            if (javaClass instanceof IdentifiableClass) {
                IdentifiableClass identifiableClass = (IdentifiableClass) javaClass;
                return identifiableClass.getIdClass();
            }
        }
        return null;
    }
    
    
    /**
     * Gets the value of the mapsId property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getMapsId() {
        return mapsId;
    }

    /**
     * Sets the value of the mapsId property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setMapsId(String value) {
        this.mapsId = value;
    }

}
