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

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.spec.Column;
import static org.netbeans.jpa.source.Package.LANG_PACKAGE;
import org.netbeans.modeler.core.NBModelerUtil;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class PersistenceBaseAttribute extends BaseAttribute implements ColumnHandler{

    @XmlAttribute(name = "attribute-type", required = true)
    private String attributeType;

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

    public boolean isPrecisionAttributeType() {
//        if (attributeType.equals("byte") || attributeType.equals("Byte")) {
//            return true;
//        } else if (attributeType.equals("short") || attributeType.equals("Short")) {
//            return true;
//        } else if (attributeType.equals("int") || attributeType.equals("Integer")) {
//            return true;
//        } else if (attributeType.equals("long") || attributeType.equals("Long")) {
//            return true;
//        } else if (attributeType.equals("float") || attributeType.equals("Float")) {
//            return true;
//        } else if (attributeType.equals("double") || attributeType.equals("Double")) {
//            return true;
//        } else if (attributeType.equals("java.math.BigInteger") || attributeType.equals("java.math.BigDecimal")) {
//            return true;
//        }

        if ("java.math.BigDecimal".equals(attributeType)) {
            return true;
        }
        return false;
    }

    public boolean isScaleAttributeType() {
//        if (attributeType.equals("float") || attributeType.equals("Float")) {
//            return true;
//        } else if (attributeType.equals("double") || attributeType.equals("Double")) {
//            return true;
//        } else if (attributeType.equals("java.math.BigDecimal")) {
//            return true;
//        }
        if ("java.math.BigDecimal".equals(attributeType)) {
            return true;
        }
        return false;
    }

    public boolean isTextAttributeType() {
        if (attributeType.equals(String.class.getName())) {
            return true;
        }
        return false;
    }

    void beforeMarshal(Marshaller marshaller) {
        if (NBModelerUtil.isEmptyObject(getColumn())) {
            setColumn(null);
        }
    }

    /**
     * Gets the value of the column property.
     *
     * @return possible object is {@link Column }
     *
     */
    public abstract Column getColumn();

    /**
     * Sets the value of the column property.
     *
     * @param value allowed object is {@link Column }
     *
     */
    public abstract void setColumn(Column value);

    public String getDefaultColumnName() {
        return this.getName().toUpperCase();
    }

    public String getColumnName() {
        if (this.getColumn() != null && StringUtils.isNotBlank(this.getColumn().getName())) {
            return getColumn().getName();
        } else {
            return getDefaultColumnName();
        }
    }
}
