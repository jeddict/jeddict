package io.github.jeddict.jpa.spec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.ColumnHandler;

@XmlAccessorType(XmlAccessType.FIELD)
public class DefaultAttribute extends Attribute {

    @XmlAttribute(name = "attribute-type")
    private String attributeType;
    
    @XmlTransient
    private Attribute connectedAttribute;
    
    @XmlAttribute(name = "derived")
    private boolean derived;
    
    private Column column;

    public DefaultAttribute() {//to represent IdClass
    }

    public DefaultAttribute(Attribute connectedAttribute) {
        this.connectedAttribute = connectedAttribute;
        if(connectedAttribute instanceof ColumnHandler){ // to load embeddable attribute @Column
            column = ((ColumnHandler)connectedAttribute).getColumn();
        }
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

    @Override
    public String getDataTypeLabel() {
        return getAttributeType();
    }
    
    @Override
    public boolean isOptionalReturnType() {
        return false;
    }

    /**
     * @return the column
     */
    public Column getColumn() {
        if (column == null) {
            column = new Column();
        }
        return column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(Column column) {
        this.column = column;
    }
    
}
