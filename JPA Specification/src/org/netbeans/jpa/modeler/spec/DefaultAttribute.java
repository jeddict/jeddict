package org.netbeans.jpa.modeler.spec;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import org.netbeans.jpa.modeler.spec.extend.Attribute;

//created by gaurav gupta
@XmlAccessorType(XmlAccessType.FIELD)
public class DefaultAttribute extends Attribute {

    @XmlAttribute(name = "attribute-type")
    private String attributeType;
    
    @XmlTransient
    private Attribute connectedAttribute;
    
    @XmlAttribute(name = "derived")
    private boolean derived;

    @XmlTransient
    private DefaultClass _class;

    public DefaultAttribute(Attribute connectedAttribute) {
        this.connectedAttribute = connectedAttribute;
    }
    
    public DefaultClass getJavaClass() {
        return _class;
    }

    public void setJavaClass(DefaultClass _class) {
        this._class = _class;
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        setJavaClass((DefaultClass) parent);
    }

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

    /**
     * @return the derived
     */
    public boolean isDerived() {
        return derived;
    }

    /**
     * @param derived the derived to set
     */
    public void setDerived(boolean derived) {
        this.derived = derived;
    }

    /**
     * @return the connectedAttribute
     */
    public Attribute getConnectedAttribute() {
        return connectedAttribute;
    }
    
}
