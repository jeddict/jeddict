//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.01.21 at 01:52:19 PM IST
//
package io.github.jeddict.jpa.spec;

import io.github.jeddict.source.AnnotationExplorer;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 *
 *
 * @Target({}) @Retention(RUNTIME) public @interface EntityResult { Class
 * entityClass(); FieldResult[] fields() default {}; String
 * discriminatorColumn() default ""; }
 *
 *
 *
 * <p>
 * Java class for entity-result complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="entity-result">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="field-result" type="{http://java.sun.com/xml/ns/persistence/orm}field-result" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="entity-class" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="discriminator-column" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entity-result", propOrder = {
    "fieldResult"
})
public class EntityResult {

    @XmlElement(name = "fr")//(name = "field-result")
    protected List<FieldResult> fieldResult;
    @XmlAttribute(name = "ec", required = true)//(name = "entity-class", required = true)
    protected String entityClass;
    @XmlAttribute(name = "dcl")//(name = "discriminator-column")
    protected String discriminatorColumn;

    public static EntityResult load(AnnotationExplorer annotation) {
        EntityResult entityResult = new EntityResult();
        annotation.getClassName("entityClass").ifPresent(entityResult::setEntityClass);
        annotation.getString("discriminatorColumn").ifPresent(entityResult::setDiscriminatorColumn);
        entityResult.fieldResult
                = annotation.getAnnotationList("fields")
                        .map(FieldResult::load)
                        .collect(toList());
        return entityResult;
    }

    /**
     * Gets the value of the fieldResult property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the fieldResult property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFieldResult().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldResult }
     *
     *
     */
    public List<FieldResult> getFieldResult() {
        if (fieldResult == null) {
            fieldResult = new ArrayList<>();
        }
        return this.fieldResult;
    }

    /**
     * Gets the value of the entityClass property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getEntityClass() {
        return entityClass;
    }

    /**
     * Sets the value of the entityClass property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setEntityClass(String value) {
        this.entityClass = value;
    }

    /**
     * Gets the value of the discriminatorColumn property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDiscriminatorColumn() {
        return discriminatorColumn;
    }

    /**
     * Sets the value of the discriminatorColumn property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDiscriminatorColumn(String value) {
        this.discriminatorColumn = value;
    }

}
