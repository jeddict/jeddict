/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.orm.converter.compiler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.netbeans.jpa.modeler.spec.extend.AttributeType;

public class JavaHashcodeEqualsUtil {

    private enum KindOfType {

        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        LONG,
        CHAR,
        FLOAT,
        DOUBLE,
        ENUM,
        PRIMITIVE_ARRAY,
        ARRAY,
        STRING,
        OTHER;
    }

    private static final String VAR_EXPRESSION = "{VAR}";
    private static final Map<KindOfType, String> EQUALS_PATTERNS;
    private static final Map<KindOfType, String> HASH_CODE_PATTERNS;

    static {
        EQUALS_PATTERNS = new LinkedHashMap<>();

        String NOT_EQUAL_COMP = "this.{VAR} != other.{VAR}";
        String EQUALS_METHOD = "!java.util.Objects.equals(this.{VAR}, other.{VAR})";////this.{VAR} != other.{VAR} && (this.{VAR} == null || !this.{VAR}.equals(other.{VAR}))");
        EQUALS_PATTERNS.put(KindOfType.BOOLEAN, NOT_EQUAL_COMP);
        EQUALS_PATTERNS.put(KindOfType.BYTE, NOT_EQUAL_COMP);
        EQUALS_PATTERNS.put(KindOfType.SHORT, NOT_EQUAL_COMP);
        EQUALS_PATTERNS.put(KindOfType.INT, NOT_EQUAL_COMP);
        EQUALS_PATTERNS.put(KindOfType.LONG, NOT_EQUAL_COMP);
        EQUALS_PATTERNS.put(KindOfType.CHAR, NOT_EQUAL_COMP);
        EQUALS_PATTERNS.put(KindOfType.FLOAT, "java.lang.Float.floatToIntBits(this.{VAR}) != java.lang.Float.floatToIntBits(other.{VAR})");
        EQUALS_PATTERNS.put(KindOfType.DOUBLE, "java.lang.Double.doubleToLongBits(this.{VAR}) != java.lang.Double.doubleToLongBits(other.{VAR})");
        EQUALS_PATTERNS.put(KindOfType.PRIMITIVE_ARRAY, "! java.util.Arrays.equals(this.{VAR}, other.{VAR}");
        EQUALS_PATTERNS.put(KindOfType.ARRAY, "! java.util.Arrays.deepEquals(this.{VAR}, other.{VAR}");
        EQUALS_PATTERNS.put(KindOfType.ENUM, "this.{VAR} != other.{VAR}");
        EQUALS_PATTERNS.put(KindOfType.STRING, EQUALS_METHOD);
        EQUALS_PATTERNS.put(KindOfType.OTHER, EQUALS_METHOD);

        HASH_CODE_PATTERNS = new LinkedHashMap<>();

        String DEFAULT_HASHCODE = "this.{VAR}";
        String HASHCODE_METHOD = "(this.{VAR} != null ? this.{VAR}.hashCode() : 0)";
        HASH_CODE_PATTERNS.put(KindOfType.BYTE, DEFAULT_HASHCODE);
        HASH_CODE_PATTERNS.put(KindOfType.SHORT, DEFAULT_HASHCODE);
        HASH_CODE_PATTERNS.put(KindOfType.INT, DEFAULT_HASHCODE);
        HASH_CODE_PATTERNS.put(KindOfType.CHAR, DEFAULT_HASHCODE);
        HASH_CODE_PATTERNS.put(KindOfType.LONG, "(int) (this.{VAR} ^ (this.{VAR} >>> 32))");
        HASH_CODE_PATTERNS.put(KindOfType.FLOAT, "java.lang.Float.floatToIntBits(this.{VAR})");
        HASH_CODE_PATTERNS.put(KindOfType.DOUBLE, "(int) (Double.doubleToLongBits(this.{VAR}) ^ (Double.doubleToLongBits(this.{VAR}) >>> 32))");
        HASH_CODE_PATTERNS.put(KindOfType.BOOLEAN, "(this.{VAR} ? 1 : 0)");
        HASH_CODE_PATTERNS.put(KindOfType.PRIMITIVE_ARRAY, "java.util.Arrays.hashCode(this.{VAR})");
        HASH_CODE_PATTERNS.put(KindOfType.ARRAY, "java.util.Arrays.deepHashCode(this.{VAR})");
        HASH_CODE_PATTERNS.put(KindOfType.ENUM, HASHCODE_METHOD);
        HASH_CODE_PATTERNS.put(KindOfType.STRING, HASHCODE_METHOD);
        HASH_CODE_PATTERNS.put(KindOfType.OTHER, HASHCODE_METHOD);
    }

    public static String getEqualExpression(String dataType, String attributeName) {
        return EQUALS_PATTERNS.get(detectKind(dataType)).replace(VAR_EXPRESSION, attributeName);
    }
    
    public static String getEqualExpression(String attributeName) {
        return EQUALS_PATTERNS.get(KindOfType.OTHER).replace(VAR_EXPRESSION, attributeName);
    }
    
    public static String getHashcodeExpression(String dataType, String attributeName) {
        return HASH_CODE_PATTERNS.get(detectKind(dataType)).replace(VAR_EXPRESSION, attributeName);
    }
    
    public static String getHashcodeExpression(String attributeName) {
        return HASH_CODE_PATTERNS.get(KindOfType.OTHER).replace(VAR_EXPRESSION, attributeName);
    }
    
    private static KindOfType detectKind(String dataType){ //enum not detected
        KindOfType kindOfType;
        AttributeType.Type type = AttributeType.getType(dataType);
        if (type == AttributeType.Type.STRING) {
            kindOfType = KindOfType.STRING;
        } else if (type == AttributeType.Type.PREMITIVE) {
            kindOfType = KindOfType.valueOf(dataType);
        } else if (type == AttributeType.Type.WRAPPER) {
            kindOfType = KindOfType.valueOf(AttributeType.getPrimitiveType(dataType));
        } else if (type == AttributeType.Type.ARRAY) {
            kindOfType = KindOfType.ARRAY;
        } else if (type == AttributeType.Type.PRIMITIVE_ARRAY) {
            kindOfType = KindOfType.PRIMITIVE_ARRAY;
        } else {
            kindOfType = KindOfType.OTHER;
        }
        return kindOfType;
    }
    
    

}
