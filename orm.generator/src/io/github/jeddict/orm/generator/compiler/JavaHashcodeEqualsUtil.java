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
package io.github.jeddict.orm.generator.compiler;

import java.util.LinkedHashMap;
import java.util.Map;
import io.github.jeddict.jcode.util.AttributeType;
import io.github.jeddict.jcode.util.StringHelper;

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
        EQUALS_PATTERNS.put(KindOfType.FLOAT, "Float.floatToIntBits(this.{VAR}) != Float.floatToIntBits(other.{VAR})");
        EQUALS_PATTERNS.put(KindOfType.DOUBLE, "Double.doubleToLongBits(this.{VAR}) != Double.doubleToLongBits(other.{VAR})");
        EQUALS_PATTERNS.put(KindOfType.PRIMITIVE_ARRAY, "! java.util.Arrays.equals(this.{VAR}, other.{VAR})");
        EQUALS_PATTERNS.put(KindOfType.ARRAY, "! java.util.Arrays.deepEquals(this.{VAR}, other.{VAR})");
        EQUALS_PATTERNS.put(KindOfType.ENUM, "this.{VAR} != other.{VAR}");
        EQUALS_PATTERNS.put(KindOfType.STRING, EQUALS_METHOD);
        EQUALS_PATTERNS.put(KindOfType.OTHER, EQUALS_METHOD);

        HASH_CODE_PATTERNS = new LinkedHashMap<>();

        String DEFAULT_HASHCODE = "this.{VAR}";
        String HASHCODE_METHOD = "Objects.hashCode(this.{VAR})";
        HASH_CODE_PATTERNS.put(KindOfType.BYTE, DEFAULT_HASHCODE);
        HASH_CODE_PATTERNS.put(KindOfType.SHORT, DEFAULT_HASHCODE);
        HASH_CODE_PATTERNS.put(KindOfType.INT, DEFAULT_HASHCODE);
        HASH_CODE_PATTERNS.put(KindOfType.CHAR, DEFAULT_HASHCODE);
        HASH_CODE_PATTERNS.put(KindOfType.LONG, "(int) (this.{VAR} ^ (this.{VAR} >>> 32))");
        HASH_CODE_PATTERNS.put(KindOfType.FLOAT, "Float.floatToIntBits(this.{VAR})");
        HASH_CODE_PATTERNS.put(KindOfType.DOUBLE, "(int) (Double.doubleToLongBits(this.{VAR}) ^ (Double.doubleToLongBits(this.{VAR}) >>> 32))");
        HASH_CODE_PATTERNS.put(KindOfType.BOOLEAN, "(this.{VAR} ? 1 : 0)");
        HASH_CODE_PATTERNS.put(KindOfType.PRIMITIVE_ARRAY, "java.util.Arrays.hashCode(this.{VAR})");
        HASH_CODE_PATTERNS.put(KindOfType.ARRAY, "java.util.Arrays.deepHashCode(this.{VAR})");
        HASH_CODE_PATTERNS.put(KindOfType.ENUM, HASHCODE_METHOD);
        HASH_CODE_PATTERNS.put(KindOfType.STRING, HASHCODE_METHOD);
        HASH_CODE_PATTERNS.put(KindOfType.OTHER, HASHCODE_METHOD);
    }

    public static String getEqualExpression(String dataType, String attributeName, boolean optionalType) {
        KindOfType type = detectKind(dataType);
        String attributeFunction = (type==KindOfType.BOOLEAN?"is" : "get") + StringHelper.getMethodName(attributeName) + (optionalType?"().orElse(null)":"()");
        return EQUALS_PATTERNS.get(type).replace(VAR_EXPRESSION, attributeFunction);
    }
    
    public static String getEqualExpression(String attributeName, boolean optionalType) {
        String attributeFunction =  "get" + StringHelper.getMethodName(attributeName) + (optionalType?"().orElse(null)":"()");
        return EQUALS_PATTERNS.get(KindOfType.OTHER).replace(VAR_EXPRESSION, attributeFunction);
    }
    
    public static String getHashcodeExpression(String dataType, String attributeName, boolean optionalType) {
        KindOfType type = detectKind(dataType);
        String attributeFunction = (type==KindOfType.BOOLEAN?"is" : "get") + StringHelper.getMethodName(attributeName) + (optionalType?"().orElse(null)":"()");
        return HASH_CODE_PATTERNS.get(type).replace(VAR_EXPRESSION, attributeFunction);
    }
    
    public static String getHashcodeExpression(String attributeName, boolean optionalType) {
        String attributeFunction =  "get" + StringHelper.getMethodName(attributeName) + (optionalType?"().orElse(null)":"()");
        return HASH_CODE_PATTERNS.get(KindOfType.OTHER).replace(VAR_EXPRESSION, attributeFunction);
    }
    
    private static KindOfType detectKind(String dataType){ //enum not detected
        KindOfType kindOfType = null;
        AttributeType.Type type = AttributeType.getType(dataType);
        if (null != type) switch (type) {
            case STRING:
                kindOfType = KindOfType.STRING;
                break;
            case PRIMITIVE:
                kindOfType = KindOfType.valueOf(dataType.toUpperCase());
                break;
            case WRAPPER:
                kindOfType = KindOfType.OTHER;//valueOf(AttributeType.getPrimitiveType(dataType).toUpperCase());
                break;
            case ARRAY:
                kindOfType = KindOfType.ARRAY;
                break;
            case PRIMITIVE_ARRAY:
                kindOfType = KindOfType.PRIMITIVE_ARRAY;
                break;
            default:
                kindOfType = KindOfType.OTHER;
                break;
        }
        return kindOfType;
    }
    
    

}
