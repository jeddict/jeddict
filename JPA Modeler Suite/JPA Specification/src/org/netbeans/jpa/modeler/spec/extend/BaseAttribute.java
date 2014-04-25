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
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class BaseAttribute extends Attribute {

    @XmlAttribute(name = "attribute-type", required = true)
    private String attributeType;

    /**
     * @return the attributeType
     */
    public String getAttributeType() {
        return attributeType;
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

        if (attributeType.equals("java.math.BigDecimal")) {
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
        if (attributeType.equals("java.math.BigDecimal")) {
            return true;
        }
        return false;
    }

    public boolean isTextAttributeType() {
        if (attributeType.equals("String")) {
            return true;
        }
        return false;
    }

    /**
     * @param attributeType the attributeType to set
     */
    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

}
