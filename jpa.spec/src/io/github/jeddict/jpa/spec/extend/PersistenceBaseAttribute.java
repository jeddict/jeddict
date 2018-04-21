/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.spec.extend;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.xml.bind.annotation.XmlAttribute;
import org.apache.commons.lang3.StringUtils;
import static io.github.jeddict.jcode.util.Constants.LANG_PACKAGE;
import io.github.jeddict.jpa.spec.AccessType;
import io.github.jeddict.jpa.spec.Column;
import io.github.jeddict.jpa.spec.TemporalType;
import io.github.jeddict.source.JavaSourceParserUtil;

/**
 *
 * @author Gaurav Gupta
 */
//@XmlType(propOrder = {
//    "column",
//    "temporal"
//})
public abstract class PersistenceBaseAttribute extends BaseAttribute implements ColumnHandler, TemporalTypeHandler, AccessTypeHandler {

    @XmlAttribute(name = "attribute-type", required = true)
    private String attributeType;
    protected TemporalType temporal;
    protected Column column;
    @XmlAttribute(name = "access")
    protected AccessType access;

    @Override
    protected void loadAttribute(Element element, VariableElement variableElement, ExecutableElement getterElement) {
        super.loadAttribute(element, variableElement, getterElement);
        this.column = new Column().load(element, null);
        this.access = AccessType.load(element);
        this.temporal = TemporalType.load(element, null);
        this.setAttributeType(variableElement.asType().toString());
        this.setAttributeConstraints(JavaSourceParserUtil.getBeanValidation(element));
    }

    @Override
    public String getAttributeType() {
        return attributeType;
    }

    /**
     * @param attributeType the attributeType to set
     */
    public void setAttributeType(String attributeType) {
        if (attributeType.indexOf(LANG_PACKAGE) == 0) {
            this.attributeType = attributeType.substring(LANG_PACKAGE.length() + 1);
        } else {
            this.attributeType = attributeType;
        }
    }
    
    @Override
    public String getDefaultColumnName() {
        return this.getName().toUpperCase();
    }

    @Override
    public String getColumnName() {
        if (this.getColumn() != null && StringUtils.isNotBlank(this.getColumn().getName())) {
            return getColumn().getName();
        } else {
            return getDefaultColumnName();
        }
    }

    /**
     * Gets the value of the temporal property.
     *
     * @return possible object is {@link TemporalType }
     *
     */
    @Override
    public TemporalType getTemporal() {
        return temporal;
    }

    /**
     * Sets the value of the temporal property.
     *
     * @param value allowed object is {@link TemporalType }
     *
     */
    @Override
    public void setTemporal(TemporalType value) {
        this.temporal = value;
    }

    /**
     * Gets the value of the access property.
     *
     * @return possible object is {@link AccessType }
     *
     */
    @Override
    public AccessType getAccess() {
        return access;
    }

    /**
     * Sets the value of the access property.
     *
     * @param value allowed object is {@link AccessType }
     *
     */
    @Override
    public void setAccess(AccessType value) {
        this.access = value;
    }

    /**
     * Gets the value of the column property.
     *
     * @return possible object is {@link Column }
     *
     */
    @Override
    public Column getColumn() {
        if (column == null) {
            column = new Column();
        }
        return column;
    }

    /**
     * Sets the value of the column property.
     *
     * @param value allowed object is {@link Column }
     *
     */
    @Override
    public void setColumn(Column value) {
        this.column = value;
    }
    
}
