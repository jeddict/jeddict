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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.netbeans.jpa.modeler.spec.ForeignKey;
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
    protected List<JoinColumn> joinColumn;
    @XmlElement(name = "foreign-key")
    protected ForeignKey foreignKey;//REVENG PENDING

    @XmlAttribute(name = "optional")
    protected Boolean optional;
    @XmlAttribute
    private Boolean primaryKey;//id=>primaryKey changed to prevent BaseElement.id field hiding//REVENG PENDING

    @Override
    public void load(AnnotationMirror relationAnnotationMirror, Element element, VariableElement variableElement) {
        super.load(relationAnnotationMirror, element, variableElement);
        if (JavaSourceParserUtil.isAnnotatedWith(element, "javax.persistence.Id")) {
            this.setPrimaryKey(Boolean.TRUE);
        }

        AnnotationMirror joinColumnsAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, "javax.persistence.JoinColumns");
        if (joinColumnsAnnotationMirror != null) {
            List joinColumnsAnnot = (List) JavaSourceParserUtil.findAnnotationValue(joinColumnsAnnotationMirror, "value");
            if (joinColumnsAnnot != null) {
                for (Object joinColumnObj : joinColumnsAnnot) {
                    this.getJoinColumn().add(JoinColumn.load(element, (AnnotationMirror) joinColumnObj));
                }
            }
        } else {
            AnnotationMirror joinColumnAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, "javax.persistence.JoinColumn");
            if (joinColumnAnnotationMirror != null) {
                this.getJoinColumn().add(JoinColumn.load(element, joinColumnAnnotationMirror));
            }
        }

        this.optional = (Boolean) JavaSourceParserUtil.findAnnotationValue(relationAnnotationMirror, "optional");
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

}
