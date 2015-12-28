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
package org.netbeans.orm.converter.generator;

import org.netbeans.orm.converter.compiler.CallbackSnippet;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.compiler.EntityListenerSnippet;
import org.netbeans.orm.converter.compiler.EntityListenersSnippet;
import org.netbeans.orm.converter.compiler.LifecycleListenerSnippet;
import org.netbeans.orm.converter.compiler.MethodDefSnippet;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.jpa.modeler.spec.EntityListener;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.PersistenceUnitDefaults;
import org.netbeans.jpa.modeler.spec.PersistenceUnitMetadata;

/*
 * Takes a list of generated entity classes, Finds the list of EntityListeners
 * for each entity listener creates a LifecycleListener which is a source
 * generator for the listener class
 */
public class LifecycleCallbackGenerator {

    private static final String DEFAULT_METHOD_NAME = "defaultCallback";

    private String packageName = null;

    private EntityMappings entityMappings = null;
    private List<ClassDefSnippet> classDefs = null;

    private Map<String, LifecycleListenerSnippet> lifecycleListeners
            = new HashMap<String, LifecycleListenerSnippet>();

    public LifecycleCallbackGenerator(
            EntityMappings parsedEntityMappings,
            List<ClassDefSnippet> classDefs,
            String packageName) {

        this.classDefs = classDefs;
        this.packageName = packageName;
        this.entityMappings = parsedEntityMappings;

        processGlobalEntityListeners();
        processClassLevelEntityListeners();
    }

    public Collection<LifecycleListenerSnippet> getLifecycleListeners() {
        return lifecycleListeners.values();
    }

    private LifecycleListenerSnippet getLifecycleListener(String className) {

        LifecycleListenerSnippet lifecycleListener
                = lifecycleListeners.get(className);

        if (lifecycleListener == null) {
            lifecycleListener = new LifecycleListenerSnippet();

            lifecycleListener.setClassName(className);
            lifecycleListener.setPackageName(packageName);

            lifecycleListeners.put(className, lifecycleListener);
        }

        return lifecycleListener;
    }

    private MethodDefSnippet getMethodDef(
            String methodName, LifecycleListenerSnippet lifecycleListener) {

        List<MethodDefSnippet> methodDefs = lifecycleListener.getMethodDefs();

        if (methodName == null) {
            throw new IllegalArgumentException("Method name cannot be null");
        }

        if (methodName.trim().length() == 0) {
            methodName = DEFAULT_METHOD_NAME;
        }

        for (MethodDefSnippet methodDef : methodDefs) {

            if (methodDef.getMethodName().equals(methodName)) {
                return methodDef;
            }
        }

        MethodDefSnippet methodDef = new MethodDefSnippet();
        methodDef.setMethodName(methodName);

        lifecycleListener.addMethodDef(methodDef);

        return methodDef;
    }

    private void processClassLevelEntityListeners() {
        if (classDefs.isEmpty()) {
            return;
        }

        for (ClassDefSnippet classDef : classDefs) {
            EntityListenersSnippet entityListeners = classDef.getEntityListeners();

            if (entityListeners != null) {
                populateLifecycleListeners(entityListeners);
            }
        }
    }

    private void populateLifecycleListeners(EntityListenersSnippet entityListeners) {

        List<EntityListenerSnippet> entityListenersList
                = entityListeners.getEntityListeners();

        for (EntityListenerSnippet entityListener : entityListenersList) {

            String className = entityListener.getClassHelper().getFQClassName();
            List<CallbackSnippet> callbacks = entityListener.getCallbacks();

            LifecycleListenerSnippet lifecycleListener
                    = getLifecycleListener(className);

            for (CallbackSnippet callback : callbacks) {

                MethodDefSnippet methodDef = getMethodDef(
                        callback.getMethodName(),
                        lifecycleListener);

                methodDef.addCallback(callback);
            }
        }
    }

    private void processGlobalEntityListeners() {

        PersistenceUnitMetadata persistenceUnitMetadata
                = entityMappings.getPersistenceUnitMetadata();

        if (persistenceUnitMetadata == null) {
            return;
        }

        PersistenceUnitDefaults persistenceUnitDefaults
                = persistenceUnitMetadata.getPersistenceUnitDefaults();

        if (persistenceUnitDefaults == null
                || persistenceUnitDefaults.getEntityListeners() == null) {

            return;
        }

        List<EntityListener> parsedEntityListenersList
                = persistenceUnitDefaults.getEntityListeners().getEntityListener();

        List<EntityListenerSnippet> entityListenerList
                = GeneratorUtil.processEntityListeners(
                        parsedEntityListenersList, packageName);

        if (entityListenerList.isEmpty()) {
            return;
        }

        EntityListenersSnippet entityListeners = new EntityListenersSnippet();
        entityListeners.setEntityListeners(entityListenerList);

        populateLifecycleListeners(entityListeners);
    }
}
