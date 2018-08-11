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
package io.github.jeddict.jcode.util;

import static io.github.jeddict.jcode.util.Constants.JAVA_EXT;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
public class JavaUtil {

    public static boolean isJava9() {
        return getJavaVersion() >= 9;
    }

    public static double getJavaVersion() {
        return Double.parseDouble(ManagementFactory.getRuntimeMXBean().getSpecVersion());
    }

    public static String getUniqueClassName(String candidateName, FileObject targetFolder) {
        return org.openide.filesystems.FileUtil.findFreeFileName(targetFolder, candidateName, JAVA_EXT); //NOI18N
    }

    public static Class<?> getPrimitiveType(String typeName) {
        return Lazy.primitiveTypes.get(typeName);
    }

    private static class Lazy {

        private static final Map<String, Class<?>> primitiveTypes = new HashMap<String, Class<?>>();

        static {
            primitiveTypes.put("int", Integer.class);
            primitiveTypes.put("int[]", int[].class);
            primitiveTypes.put("java.lang.Integer[]", Integer[].class);
            primitiveTypes.put("boolean", Boolean.class);
            primitiveTypes.put("boolean[]", boolean[].class);
            primitiveTypes.put("java.lang.Boolean[]", Boolean[].class);
            primitiveTypes.put("byte", Byte.class);
            primitiveTypes.put("byte[]", byte[].class);
            primitiveTypes.put("java.lang.Byte[]", Byte[].class);
            primitiveTypes.put("char", Character.class);
            primitiveTypes.put("char[]", char[].class);
            primitiveTypes.put("java.lang.Character[]", Character[].class);
            primitiveTypes.put("double", Double.class);
            primitiveTypes.put("double[]", double[].class);
            primitiveTypes.put("java.lang.Double[]", Double[].class);
            primitiveTypes.put("float", Float.class);
            primitiveTypes.put("float[]", float[].class);
            primitiveTypes.put("java.lang.Float[]", Float[].class);
            primitiveTypes.put("long", Long.class);
            primitiveTypes.put("long[]", long[].class);
            primitiveTypes.put("java.lang.Long[]", Long[].class);
            primitiveTypes.put("short", Short.class);
            primitiveTypes.put("short[]", short[].class);
            primitiveTypes.put("java.lang.Short[]", Short[].class);
        }
    }

    public static boolean isMap(String _className) {
        boolean valid = false;
        try {
            if (_className != null && !_className.trim().isEmpty()) {
                if (java.util.Map.class.isAssignableFrom(Class.forName(_className.trim()))) {
                    valid = true;
                }
            }
        } catch (ClassNotFoundException ex) {
            //skip allow = false;
        }
        return valid;
    }

    public static boolean isGetterMethod(String methodName) {
        return methodName.startsWith("get") || methodName.startsWith("is");
    }

    public static boolean isSetterMethod(String methodName) {
        return methodName.startsWith("set") || methodName.startsWith("is");
    }

    public static boolean isBeanMethod(String methodName) {
        return isGetterMethod(methodName) || isSetterMethod(methodName);
    }

    /**
     * a derived methodName from variableName Eg nickname -> getNickname /
     * setNickname
     */
    public static String getMethodName(String type, String fieldName) {
        String methodName;
        if (fieldName.charAt(0) == '_') {
            char ch = Character.toUpperCase(fieldName.charAt(1));
            methodName = Character.toString(ch) + fieldName.substring(2);
        } else {
            char ch = Character.toUpperCase(fieldName.charAt(0));
            methodName = Character.toString(ch) + fieldName.substring(1);
        }
        if (type != null) {
            methodName = type + methodName;
        }
        return methodName;
    }

    public static String getFieldName(String methodName) {
        String fieldName;
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            fieldName = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            fieldName = methodName.substring(2);
        } else {
            return null;
        }
        fieldName = StringHelper.firstLower(fieldName);
        return fieldName;
    }

    public static String removeBeanMethodPrefix(String methodName) {
        if (methodName.startsWith("get")) {
            methodName = methodName.replaceFirst("get", EMPTY);
        }
        if (methodName.startsWith("set")) {
            methodName = methodName.replaceFirst("set", EMPTY);
        }
        if (methodName.startsWith("is")) {
            methodName = methodName.replaceFirst("is", EMPTY);
        }
        return methodName;
    }

    public static String mergePackage(String package1, String package2) {
        if (isNotBlank(package1) && isNotBlank(package2)) {
            return package1 + '.' + package2;
        } else if (isNotBlank(package1)) {
            return package1;
        } else if (isNotBlank(package2)) {
            return package2;
        }
        return EMPTY;
    }
}
