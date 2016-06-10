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
package org.netbeans.jpa.modeler.spec.extend;

import java.util.HashMap;
import java.util.Map;
import static org.netbeans.jpa.modeler.spec.extend.AttributeType.Type.ARRAY;
import static org.netbeans.jpa.modeler.spec.extend.AttributeType.Type.OTHER;
import static org.netbeans.jpa.modeler.spec.extend.AttributeType.Type.PREMITIVE;
import static org.netbeans.jpa.modeler.spec.extend.AttributeType.Type.PRIMITIVE_ARRAY;
import static org.netbeans.jpa.modeler.spec.extend.AttributeType.Type.WRAPPER;

/**
 *
 * @author Gaurav Gupta
 */
public class AttributeType {

    public static final String BIGDECIMAL = "java.math.BigDecimal";
    public static final String BIGINTEGER = "java.math.BigInteger";
    public static final String BYTE = "byte";
    public static final String SHORT = "short";
    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String BOOLEAN = "boolean";
    public static final String CHAR = "char";
    public static final String STRING = "String";

    public static final String BYTE_WRAPPER = "Byte";
    public static final String SHORT_WRAPPER = "Short";
    public static final String INT_WRAPPER = "Integer";
    public static final String LONG_WRAPPER = "Long";
    public static final String FLOAT_WRAPPER = "Float";
    public static final String DOUBLE_WRAPPER = "Double";
    public static final String BOOLEAN_WRAPPER = "Boolean";
    public static final String CHAR_WRAPPER = "Character";

    public static final String CALENDAR = "java.util.Calendar";
    public static final String DATE = "java.util.Date";
    public static final String SQL_DATE = "java.sql.Date";
    public static final String SQL_TIME = "java.sql.Time";
    public static final String SQL_TIMESTAMP = "java.sql.Timestamp";
    public static final String BYTE_ARRAY = "byte[]";
    public static final String BYTE_WRAPPER_ARRAY = "Byte[]";
    public static final String CHAR_ARRAY = "char[]";
    public static final String CHAR_WRAPPER_ARRAY = "Character[]";

    private static final Map<String, String> wrapperDataTypes = new HashMap<>();

    static {
        wrapperDataTypes.put(BYTE_WRAPPER, BYTE);
        wrapperDataTypes.put(SHORT_WRAPPER, SHORT);
        wrapperDataTypes.put(INT_WRAPPER, INT);
        wrapperDataTypes.put(LONG_WRAPPER, LONG);
        wrapperDataTypes.put(FLOAT_WRAPPER, FLOAT);
        wrapperDataTypes.put(DOUBLE_WRAPPER, DOUBLE);
        wrapperDataTypes.put(BOOLEAN_WRAPPER, BOOLEAN);
        wrapperDataTypes.put(CHAR_WRAPPER, CHAR);
    }

    public static String getPrimitiveType(String wrapperType) {
        return wrapperDataTypes.get(wrapperType);
    }

    private static final Map<String, String> primitiveDataTypes = new HashMap<>();

    static {
        wrapperDataTypes.entrySet().stream().forEach(e -> primitiveDataTypes.put(e.getValue(), e.getKey()));
    }

    public static String getWrapperType(String wrapperType) {
        return primitiveDataTypes.get(wrapperType);
    }

    public static enum Type {

        PREMITIVE, WRAPPER, ARRAY, PRIMITIVE_ARRAY, STRING, OTHER;
    }

    public static Type getType(String type) {
        if (wrapperDataTypes.containsKey(type)) {
            return WRAPPER;
        } else if (primitiveDataTypes.containsKey(type)) {
            return PREMITIVE;
        } else if (isArray(type)) {
            if (isPrimitiveArray(type)) {
                return PRIMITIVE_ARRAY;
            } else {
               return ARRAY;
            }
        } else if (String.class.getSimpleName().equals(type)) {
            return Type.STRING;
        } else {
            return OTHER;
        }

    }

    private static boolean isPrimitiveArray(String type) {
        String primitiveType = type.substring(0, type.length() - 2);
        return primitiveDataTypes.containsKey(primitiveType) && type.charAt(type.length() - 2) == '[' && type.charAt(type.length() - 1) == ']';
    }

    private static boolean isArray(String type) {
        return type.charAt(type.length() - 2) == '[' && type.charAt(type.length() - 1) == ']';
    }

}
