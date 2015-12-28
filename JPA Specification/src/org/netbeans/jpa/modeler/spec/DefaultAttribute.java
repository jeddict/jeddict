package org.netbeans.jpa.modeler.spec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import org.netbeans.jpa.modeler.spec.extend.Attribute;

//created by gaurav gupta
@XmlAccessorType(XmlAccessType.FIELD)
public class DefaultAttribute extends Attribute {

    @XmlAttribute(name = "attribute-type")
    private String attributeType;

    /**
     * @return the attributeType
     */
    public String getAttributeType() {
        return attributeType;
    }

    /**
     * @param attributeType the attributeType to set
     */
    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

}
