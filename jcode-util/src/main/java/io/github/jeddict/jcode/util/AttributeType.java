/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jcode.util;

import static io.github.jeddict.jcode.util.AttributeType.Type.ARRAY;
import static io.github.jeddict.jcode.util.AttributeType.Type.OTHER;
import static io.github.jeddict.jcode.util.AttributeType.Type.PRIMITIVE;
import static io.github.jeddict.jcode.util.AttributeType.Type.PRIMITIVE_ARRAY;
import static io.github.jeddict.jcode.util.AttributeType.Type.WRAPPER;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import io.github.jeddict.util.StringUtils;
import static java.util.stream.Collectors.toMap;

/**
 *
 * @author Gaurav Gupta
 */
public class AttributeType {

    public static final String BYTE = "byte";
    public static final String SHORT = "short";
    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String BOOLEAN = "boolean";
    public static final String CHAR = "char";

    public static final String BYTE_WRAPPER = "Byte";
    public static final String SHORT_WRAPPER = "Short";
    public static final String INT_WRAPPER = "Integer";
    public static final String LONG_WRAPPER = "Long";
    public static final String FLOAT_WRAPPER = "Float";
    public static final String DOUBLE_WRAPPER = "Double";
    public static final String BOOLEAN_WRAPPER = "Boolean";
    public static final String CHAR_WRAPPER = "Character";

    public static final String BOOLEAN_WRAPPER_FQN = "java.lang.Boolean";

    public static final String BIGDECIMAL = "java.math.BigDecimal";
    public static final String BIGINTEGER = "java.math.BigInteger";
    public static final String STRING = "String";
    public static final String STRING_FQN = "java.lang.String";
    public static final String CALENDAR = "java.util.Calendar";
    public static final String DATE = "java.util.Date";
    public static final String GREGORIAN_CALENDAR = "java.util.GregorianCalendar";
    public static final String TIME_ZONE = "java.util.TimeZone";
    public static final String SIMPLE_TIME_ZONE = "java.util.SimpleTimeZone";
    public static final String SQL_DATE = "java.sql.Date";
    public static final String SQL_TIME = "java.sql.Time";
    public static final String SQL_TIMESTAMP = "java.sql.Timestamp";
    public static final String BYTE_ARRAY = "byte[]";
    public static final String BYTE_WRAPPER_ARRAY = "Byte[]";
    public static final String CHAR_ARRAY = "char[]";
    public static final String CHAR_WRAPPER_ARRAY = "Character[]";
    public static final String UUID = "java.util.UUID";
    public static final String URL = "java.net.URL";
    public static final String URI = "java.net.URI";  
    public static final String BYTE_BUFFER = "java.nio.ByteBuffer";  
    
    public static final String INSTANT = "java.time.Instant";
    public static final String DURATION = "java.time.Duration";
    public static final String PERIOD = "java.time.Period";
    public static final String LOCAL_DATE = "java.time.LocalDate";
    public static final String LOCAL_DATE_TIME = "java.time.LocalDateTime";
    public static final String LOCAL_TIME = "java.time.LocalTime";
    public static final String MONTH_DAY = "java.time.MonthDay";
    public static final String OFFSET_DATE_TIME = "java.time.OffsetDateTime";
    public static final String OFFSET_TIME = "java.time.OffsetTime";
    public static final String YEAR = "java.time.Year";
    public static final String YEAR_MONTH = "java.time.YearMonth";
    public static final String ZONED_DATE_TIME = "java.time.ZonedDateTime";
    public static final String ZONE_ID = "java.time.ZoneId";
    public static final String ZONE_OFFSET = "java.time.ZoneOffset";
    public static final String HIJRAH_DATE = "java.time.chrono.HijrahDate";
    public static final String JAPANESE_DATE = "java.time.chrono.JapaneseDate";
    public static final String MINGUO_DATE = "java.time.chrono.MinguoDate";
    public static final String THAI_BUDDHIST_DATE = "java.time.chrono.ThaiBuddhistDate";

    private static final Map<String, String> WRAPPER_DATA_TYPES = new HashMap<>();
        
    static {
        WRAPPER_DATA_TYPES.put(BYTE_WRAPPER, BYTE);
        WRAPPER_DATA_TYPES.put(SHORT_WRAPPER, SHORT);
        WRAPPER_DATA_TYPES.put(INT_WRAPPER, INT);
        WRAPPER_DATA_TYPES.put(LONG_WRAPPER, LONG);
        WRAPPER_DATA_TYPES.put(FLOAT_WRAPPER, FLOAT);
        WRAPPER_DATA_TYPES.put(DOUBLE_WRAPPER, DOUBLE);
        WRAPPER_DATA_TYPES.put(BOOLEAN_WRAPPER, BOOLEAN);
        WRAPPER_DATA_TYPES.put(CHAR_WRAPPER, CHAR);
    }
    
    public static String getPrimitiveType(String wrapperType) {
        boolean array = false;
        if (isArray(wrapperType)) {
            array = true;
            wrapperType = wrapperType.substring(0, wrapperType.length() - 2);
        }
        String primitiveType = WRAPPER_DATA_TYPES.get(wrapperType);
        if (primitiveType != null) {
            return primitiveType + (array?"[]":"");
        } else {
            return wrapperType + (array?"[]":"");
        }
    }

    private static final Map<String, String> PRIMITIVE_DATA_TYPES
            = WRAPPER_DATA_TYPES.entrySet()
                    .stream()
                    .collect(toMap(Entry::getValue, Entry::getKey));

    public static String getWrapperType(String primitiveType) {
        boolean array = false;
        if (isArray(primitiveType)) {
            array = true;
            primitiveType = primitiveType.substring(0, primitiveType.length() - 2);
        }
        String wrapperType = PRIMITIVE_DATA_TYPES.get(primitiveType);
        if (wrapperType != null) {
            return wrapperType + (array?"[]":"");
        } else {
            return primitiveType + (array?"[]":"");
        }
    }

    public static enum Type {
        PRIMITIVE, WRAPPER, ARRAY, PRIMITIVE_ARRAY, STRING, OTHER;
    }

    public static boolean isJavaType(String type) {
        if (isArray(type)) {
            type = getArrayType(type);
        }
        return STRING.equals(type)
                || isPrimitive(type)
                || isWrapper(type)
                || type.startsWith("java.lang")
                || type.startsWith("java.math")
                || type.startsWith("java.net")
                || type.startsWith("java.nio")
                || type.startsWith("java.util")
                || type.startsWith("java.sql")
                || type.startsWith("java.time");
    }

    public static boolean isGenericType(String type) {
        return type.length() == 1 
                && Character.isUpperCase(type.charAt(0));
    }

    public static Type getType(String type) {
        if (isWrapper(type)) {
            return WRAPPER;
        } else if (isPrimitive(type)) {
            return PRIMITIVE;
        } else if (isArray(type)) {
            if (isPrimitiveArray(type)) {
                return PRIMITIVE_ARRAY;
            } else {
                return ARRAY;
            }
        } else if (String.class.getSimpleName().equals(type) || String.class.getCanonicalName().equals(type)) {
            return Type.STRING;
        } else {
            return OTHER;
        }

    }

    public static boolean isBoolean(String type) {
        return BOOLEAN.equals(type) || BOOLEAN_WRAPPER.equals(type) || BOOLEAN_WRAPPER_FQN.equals(type);
    }
    
    public static boolean isText(String type) {
        return STRING.equals(type) 
                || STRING_FQN.equals(type)
                || CHAR_WRAPPER.equals(type)
                || CHAR.equals(type)
                || URL.equals(type)
                || URI.equals(type);
    }
    
    
    public static boolean isNumber(String type) {
        return BYTE.equals(type) 
                || BYTE_WRAPPER.equals(type) 
                || SHORT.equals(type) 
                || SHORT_WRAPPER.equals(type) 
                || INT.equals(type) 
                || INT_WRAPPER.equals(type) 
                || LONG.equals(type) 
                || LONG_WRAPPER.equals(type) 
                || FLOAT.equals(type) 
                || FLOAT_WRAPPER.equals(type) 
                || DOUBLE.equals(type) 
                || DOUBLE_WRAPPER.equals(type) 
                || BIGDECIMAL.equals(type) 
                || BIGINTEGER.equals(type);
    }

    public static boolean isDate(String type) {
        return CALENDAR.equals(type) 
                || DATE.equals(type)
                || GREGORIAN_CALENDAR.equals(type) 
                || TIME_ZONE.equals(type) 
                || SIMPLE_TIME_ZONE.equals(type) 
                || SQL_DATE.equals(type)
                || PERIOD.equals(type) 
                || LOCAL_DATE.equals(type)
                || LOCAL_DATE_TIME.equals(type)
                || ZONE_ID.equals(type)
                || ZONE_OFFSET.equals(type);
    }

    public static boolean isDateTime(String type) {
        return LOCAL_DATE_TIME.equals(type) 
                || OFFSET_DATE_TIME.equals(type)
                || ZONED_DATE_TIME.equals(type)
                || SQL_TIMESTAMP.equals(type);
    }

    public static boolean isTime(String type) {
        return SQL_TIME.equals(type) 
                || INSTANT.equals(type) 
                || DURATION.equals(type)
                || LOCAL_TIME.equals(type)
                || OFFSET_TIME.equals(type);
    }

    public static boolean isPrimitive(String type) {
        return PRIMITIVE_DATA_TYPES.containsKey(type);
    }

    public static boolean isWrapper(String type) {
        return WRAPPER_DATA_TYPES.containsKey(type);
    }

    public static boolean isPrimitiveArray(String type) {
        int length = type.length();
        if (isArray(type)) {
            String premitiveType = type.substring(0, length - 2);
            return isPrimitive(premitiveType);
        } else {
            return false;
        }
    }

    public static boolean isWrapperArray(String type) {
        return WRAPPER_DATA_TYPES.containsKey(getArrayType(type)) && isArray(type);
    }

    public static String getArrayType(String type) {
        return type.substring(0, type.length() - 2).trim(); 
    }

    public static boolean isArray(String type) {
        if (StringUtils.isEmpty(type) || type.length() < 3) {
            return false;
        }
        return type.charAt(type.length() - 2) == '[' && type.charAt(type.length() - 1) == ']';
    }


    public static boolean isPrecision(String type) {
        return isDouble(type) || isFloat(type);
    }

    public static boolean isDouble(String type) {
        return DOUBLE_WRAPPER.equals(type) || DOUBLE.equals(type);
    }

    public static boolean isFloat(String type) {
        return FLOAT_WRAPPER.equals(type) || FLOAT.equals(type);
    }

}
