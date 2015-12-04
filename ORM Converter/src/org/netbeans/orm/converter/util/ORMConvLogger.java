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

import java.util.logging.Logger;

public class ORMConvLogger {

    public static Logger getLogger(Class cls) {
        return Logger.getLogger(getName(cls));
    }

    private static String getName(Class cls) {
        Package pkg = cls.getPackage();
        return (pkg == null) ? getPkgName(cls) : pkg.getName();
    }

    private static String getPkgName(Class cls) {
        String className = cls.getName();
        String pkgName = className;

        int index = -1;
        if (className != null) {
            index = className.lastIndexOf(".");
        }

        if (index != -1) {
            pkgName = className.substring(0, index);
        }

        return pkgName;
    }
}
