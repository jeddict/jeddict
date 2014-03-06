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

import java.util.ArrayList;
import java.util.logging.Logger;
import org.netbeans.jpa.modeler.spec.Attributes;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.compiler.VariableDefSnippet;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConvLogger;

public class SuperClassGenerator extends ClassGenerator {

    private static Logger logger = ORMConvLogger.getLogger(
            SuperClassGenerator.class);

    private MappedSuperclass mappedSuperclass = null;

    public SuperClassGenerator(
            MappedSuperclass parsedMappedSuperclass,
            String packageName) {

        this.mappedSuperclass = parsedMappedSuperclass;
        this.packageName = packageName;
    }

    public ClassDefSnippet getClassDef() {

        //--BEGIN ---- TODOs:
        /*
         * Commented -- revist for Phase 3
         *
         ParsedAccessType accessType = parsedMappedSuperclass..getAccess();
         * parsedMappedSuperclass.isMetaDataComplete()

         */
        //----END TODO ---------
        //Classlevel annotations
        processIdClass(mappedSuperclass.getIdClass());
        processEntityListeners(mappedSuperclass.getEntityListeners());

        processDefaultExcludeListeners(
                mappedSuperclass.getExcludeDefaultListeners());
        processExcludeSuperclassListeners(
                mappedSuperclass.getExcludeSuperclassListeners());

        //Attributes -- Method level annotations
        Attributes attributes = mappedSuperclass.getAttributes();

        if (attributes != null) {
            processBasic(attributes.getBasic());
            processTransient(attributes.getTransient());

            processElementCollection(attributes.getElementCollection());

            processEmbedded(attributes.getEmbedded());
            processId(attributes.getId());
            processManyToMany(attributes.getManyToMany());
            processManyToOne(attributes.getManyToOne());
            processOneToMany(attributes.getOneToMany());
            processOneToOne(attributes.getOneToOne());
            processVersion(attributes.getVersion());
        }

        processEmbeddedId(attributes);

        //Class decorations
        ClassHelper classHelper = new ClassHelper(mappedSuperclass.getClazz());
        ClassHelper superClassHelper = new ClassHelper(
                mappedSuperclass.getSuperclass());
        classHelper.setPackageName(packageName);

        classDef.setVariableDefs(new ArrayList<VariableDefSnippet>(variables.values()));
        classDef.setClassName(classHelper.getFQClassName());
        classDef.setSuperClassName(superClassHelper.getFQClassName());
        classDef.setPackageName(classHelper.getPackageName());

        classDef.setMappedSuperClass(true);

        return classDef;
    }
}
