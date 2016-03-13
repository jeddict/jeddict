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
import org.eclipse.persistence.dynamic.DynamicClassWriter;

/**
 * This custom ClassLoader provides support for dynamically generating classes
 * and JDBC Driver class
 *
 * @author Gaurav Gupta
 * @since JPA Modeler 1.3
 */
public class DynamicDriverClassLoader extends DynamicClassLoader {

    private ChildClassLoader childClassLoader;
    private ChildClassLoader driverClassLoader;

    /**
     * Create a DynamicClassLoader providing the delegate loader and leaving the
     * defaultWriter as {@link DynamicClassWriter}
     */
    public DynamicDriverClassLoader(Class loadClass) {
        super(Thread.currentThread().getContextClassLoader());
        childClassLoader = new ChildClassLoader(this.getClass().getClassLoader(), new DetectClass(this.getParent()));
        driverClassLoader = new ChildClassLoader(loadClass.getClassLoader(), new DetectClass(this.getParent()));
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> _class;
        try {
            _class = childClassLoader.searchClass(name);// To load class Object,Long,String etc.
        } catch (ClassNotFoundException e) {
            if (driverClassLoader != null) {
                try {
                    _class = driverClassLoader.searchClass(name);// To load driver class e.g : Oracle, MySql etc
                } catch (ClassNotFoundException e2) {
                    _class = super.loadClass(name, resolve);
                }
            } else {
                _class = super.loadClass(name, resolve);//To Load DynamicEntity e.g : Entity1, Entity2 etc
            }
        }
        return _class;
    }

    public DynamicDriverClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
        childClassLoader = new ChildClassLoader(this.getClass().getClassLoader(), new DetectClass(this.getParent()));
    }

    /**
     * Create a DynamicClassLoader providing the delegate loader and a default
     * {@link DynamicClassWriter}.
     */
    public DynamicDriverClassLoader(DynamicClassWriter writer) {
        this();
        this.defaultWriter = writer;
    }

    private static class ChildClassLoader extends ClassLoader {

        private DetectClass realParent;

        public ChildClassLoader(ClassLoader driverClassLoader, DetectClass realParent) {
            super(driverClassLoader);
            this.realParent = realParent;
        }

//        @Override
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
