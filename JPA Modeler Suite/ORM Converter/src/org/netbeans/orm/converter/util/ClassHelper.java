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
package org.netbeans.orm.converter.util;

import java.io.File;

public class ClassHelper {

    private String className = null;
    private String packageName = null;

    //Named Queries return only List, Hence ignoring generics of type A<B,C>
    private ClassHelper genericClass = null;

    public ClassHelper() {
    }

    public ClassHelper(String className) {
        this.className = className;
        parseClassName();
    }

    public String getClassDeclarationWithGeneric() {

        if (genericClass == null) {
            return getClassName();
        }

        return getClassName() + ORMConverterUtil.LESS_THAN
                + genericClass.getClassName() + ORMConverterUtil.GREATER_THAN;
    }

    public String getClassDeclarationWithFQGeneric() {

        if (genericClass == null) {
            return getClassName();
        }

        return getClassName() + ORMConverterUtil.LESS_THAN
                + genericClass.getFQClassName() + ORMConverterUtil.GREATER_THAN;
    }

    public String getClassName() {
        return className;
    }

    public String getClassNameWithClassSuffix() {

        if (className == null) {
            return null;
        }

        return className + ORMConverterUtil.CLASS_SUFFIX;
    }

    public String getClassNameWithSourceSuffix() {

        if (className == null) {
            return null;
        }

        return className + ORMConverterUtil.SOURCE_SUFFIX;
    }

    public String getFQClassDeclarationWithGeneric() {

        if (genericClass == null) {
            return getClassName();
        }

        return getFQClassName() + ORMConverterUtil.LESS_THAN
                + genericClass.getClassName() + ORMConverterUtil.GREATER_THAN;
    }

    public String getFQClassDeclarationWithFQGeneric() {

        if (genericClass == null) {
            return getClassName();
        }

        return getFQClassName() + ORMConverterUtil.LESS_THAN
                + genericClass.getFQClassName() + ORMConverterUtil.GREATER_THAN;
    }

    public String getFQClassName() {

        if (className == null) {
            return null;
        }

        if (packageName == null) {
            return className;
        }

        return packageName + ORMConverterUtil.DOT + className;
    }

    public String getFQClassNameWithClassSuffix() {

        if (className == null) {
            return null;
        }

        return getFQClassName() + ORMConverterUtil.CLASS_SUFFIX;
    }

    public String getFQSourceName() {

        if (className == null) {
            return null;
        }

        if (packageName == null) {
            return getClassNameWithSourceSuffix();
        }

        return packageName + ORMConverterUtil.DOT + getClassNameWithSourceSuffix();
    }

    public String getPackageName() {

        if (className == null) {
            return null;
        }

        return packageName;
    }

    public String getSourcePath() {

        if (className == null) {
            return null;
        }

        if (packageName == null) {
            return null;
        }

        return packageName.replace(ORMConverterUtil.DOT, File.separator);
    }

    public void setClassName(String className) {
        this.className = className;
        parseClassName();
    }

    public void setPackageName(String packageName) {
        // Package specified as the part of class takes precedence.
        if (this.packageName != null) {
            return;
        }

        this.packageName = packageName;
    }

    private void parseClassName() {

        if (className == null) {
            return;
        }

        //remove .class suffix
        int classSuffixIndex = className.indexOf(ORMConverterUtil.CLASS_SUFFIX);

        if (classSuffixIndex != -1) {
            className = className.substring(0, classSuffixIndex);
        }

        //If generics is present remove generic type
        int genericsIndex = className.indexOf(ORMConverterUtil.LESS_THAN);

        if (genericsIndex != -1) {
            int endGenericsIndex = className.lastIndexOf(ORMConverterUtil.GREATER_THAN);

            String genericClassName = className.substring(
                    genericsIndex + 1, endGenericsIndex);

            genericClass = new ClassHelper(genericClassName);

            className = className.substring(0, genericsIndex);
        }

        //if FQ className split packageName and className
        int dotIndex = className.lastIndexOf(ORMConverterUtil.DOT);

        if (dotIndex != -1) {
            packageName = className.substring(0, dotIndex);
            className = className.substring(dotIndex + 1);
        }
    }

    public static String getSimpleClassName(String fullClassName) {
        //if FQ className split packageName and className
        int dotIndex = fullClassName.lastIndexOf(ORMConverterUtil.DOT);

        if (dotIndex != -1) {
            fullClassName = fullClassName.substring(dotIndex + 1);
        }
        return fullClassName;
    }
}
