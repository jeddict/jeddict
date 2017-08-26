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
package org.netbeans.db.modeler.classloader;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modeler.core.ModelerFile;

/**
 * This custom ClassLoader provides support for dynamically generating classes
 * and JDBC Driver class
 *
 * @author Gaurav Gupta
 * @since Jeddict 1.3
 */
public class DynamicDriverClassLoader extends DynamicClassLoader {

    private ChildClassLoader baseClassLoader;
    private ChildClassLoader driverClassLoader;
    private ChildClassLoader projectClassLoader;

    public DynamicDriverClassLoader(ModelerFile file) {
        super(Thread.currentThread().getContextClassLoader());
        ClassPath classPath = ClassPath.getClassPath(file.getFileObject(), ClassPath.EXECUTE);
        projectClassLoader = classPath != null ? new ChildClassLoader(classPath.getClassLoader(true), new DetectClass(this.getParent())) : null;
        baseClassLoader = new ChildClassLoader(this.getClass().getClassLoader(), new DetectClass(this.getParent()));
    }

    public DynamicDriverClassLoader(ModelerFile file, Class loadClass) {
        this(file);
        driverClassLoader = new ChildClassLoader(loadClass.getClassLoader(), new DetectClass(this.getParent()));
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> _class;
        try {
            _class = baseClassLoader.searchClass(name);// To load class Object,Long,String etc.
        } catch (ClassNotFoundException e) {
                _class = checkDriverClassLoader(name, resolve);
        }
        return _class;
    }
    
    //   BaseClassLoader > DriverClassLoader > DynamicClassLoader > ProjectClassLoader

    private Class<?> checkDriverClassLoader(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> _class;
        if (driverClassLoader != null) {
            try {
                _class = driverClassLoader.searchClass(name);// To load driver class e.g : Oracle, MySql etc
            } catch (ClassNotFoundException ex) {
                _class = checkDynamicClassLoader(name, resolve);
            }
        } else {
            _class = checkDynamicClassLoader(name, resolve);
        }
        return _class;
    }

    private Class<?> checkDynamicClassLoader(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> _class;
        try {
            _class = super.loadClass(name, resolve);//To Load DynamicEntity e.g : Entity1, Entity2 etc
        } catch (ClassNotFoundException ex) {
            _class = checkProjectClassLoader(name);
        }
        return _class;
    }

    private Class<?> checkProjectClassLoader(String name) throws ClassNotFoundException {
        Class<?> _class = null;
        if (projectClassLoader != null) {
            _class = projectClassLoader.searchClass(name);// To load client/user library
        }
        return _class;
    }

    private static class ChildClassLoader extends ClassLoader {

        private final DetectClass realParent;

        public ChildClassLoader(ClassLoader driverClassLoader, DetectClass realParent) {
            super(driverClassLoader);
            this.realParent = realParent;
        }

        public Class<?> searchClass(String name) throws ClassNotFoundException {
            try {
                Class<?> loaded = super.findLoadedClass(name);
                if (loaded != null) {
                    return loaded;
                }
                return super.loadClass(name);//findClass
            } catch (ClassNotFoundException e) {
                return realParent.loadClass(name);
            }
        }
    }

    private static class DetectClass extends ClassLoader {

        public DetectClass(ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            return super.findClass(name);
        }
    }

}
