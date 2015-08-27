//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.01.21 at 01:52:19 PM IST
//
package org.netbeans.jpa.modeler.spec;

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.spec.extend.JoinColumnHandler;
import org.netbeans.jpa.modeler.spec.extend.MultiRelationAttribute;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;
import org.netbeans.jpa.source.JavaSourceParserUtil;
import org.netbeans.modeler.core.NBModelerUtil;

/**
 *
 *
 * @Target({METHOD, FIELD}) @Retention(RUNTIME) public @interface OneToMany {
 * Class targetEntity() default void.class; CascadeType[] cascade() default {};
 * FetchType fetch() default LAZY; String mappedBy() default ""; }
 *
 *
 *
 * <p>
 * Java class for one-to-many complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="one-to-many">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="order-by" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}order-by" minOccurs="0"/>
 *           &lt;element name="order-column" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}order-column" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element name="map-key" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}map-key" minOccurs="0"/>
 *           &lt;sequence>
 *             &lt;element name="map-key-class" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}map-key-class" minOccurs="0"/>
 *             &lt;choice>
 *               &lt;element name="map-key-temporal" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}temporal" minOccurs="0"/>
 *               &lt;element name="map-key-enumerated" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}enumerated" minOccurs="0"/>
 *               &lt;sequence>
 *                 &lt;element name="map-key-attribute-override" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}attribute-override" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;element name="map-key-convert" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}convert" maxOccurs="unbounded" minOccurs="0"/>
 *               &lt;/sequence>
 *             &lt;/choice>
 *             &lt;choice>
 *               &lt;element name="map-key-column" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}map-key-column" minOccurs="0"/>
 *               &lt;sequence>
 *                 &lt;element name="map-key-join-column" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}map-key-join-column" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;element name="map-key-foreign-key" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}foreign-key" minOccurs="0"/>
 *               &lt;/sequence>
 *             &lt;/choice>
 *           &lt;/sequence>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element name="join-table" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}join-table" minOccurs="0"/>
 *           &lt;sequence>
 *             &lt;element name="join-column" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}join-column" maxOccurs="unbounded" minOccurs="0"/>
 *             &lt;element name="foreign-key" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}foreign-key" minOccurs="0"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *         &lt;element name="cascade" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}cascade-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="target-entity" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fetch" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}fetch-type" />
 *       &lt;attribute name="access" type="{http://xmlns.jcp.org/xml/ns/persistence/orm}access-type" />
 *       &lt;attribute name="mapped-by" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="orphan-removal" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "one-to-many", propOrder = {
    "joinColumn",
    "foreignKey"
})
public class OneToMany extends MultiRelationAttribute implements JoinColumnHandler {

    @XmlElement(name = "join-column")
    protected List<JoinColumn> joinColumn;
    @XmlElement(name = "foreign-key")
    protected ForeignKey foreignKey;
    @XmlAttribute(name = "orphan-removal")
    protected Boolean orphanRemoval;

    public void load(Element element, VariableElement variableElement) {
        AnnotationMirror relationAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, "javax.persistence.OneToMany");
        super.load(relationAnnotationMirror, element, variableElement);
        
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
        this.orphanRemoval = (Boolean) JavaSourceParserUtil.findAnnotationValue(relationAnnotationMirror, "orphanRemoval");
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
    public List<JoinColumn> getJoinColumn() {
        if (joinColumn == null) {
            joinColumn = new ArrayList<JoinColumn>();
        }
        return this.joinColumn;
    }

   /**
     * Gets the value of the foreignKey property.
     * 
     * @return
     *     possible object is
     *     {@link ForeignKey }
     *     
     */
    public ForeignKey getForeignKey() {
        return foreignKey;
    }

    /**
     * Sets the value of the foreignKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link ForeignKey }
     *     
     */
    public void setForeignKey(ForeignKey value) {
        this.foreignKey = value;
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
     * Gets the value of the orphanRemoval property.
     *
     * @return possible object is {@link Boolean }
     *
     */
    public Boolean isOrphanRemoval() {
        return orphanRemoval;
    }

    /**
     * Sets the value of the orphanRemoval property.
     *
     * @param value allowed object is {@link Boolean }
     *
     */
    public void setOrphanRemoval(Boolean value) {
        this.orphanRemoval = value;
    }

}
