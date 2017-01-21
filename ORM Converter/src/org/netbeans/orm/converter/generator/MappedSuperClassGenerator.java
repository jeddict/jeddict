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

import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.spec.Attributes;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.orm.converter.generator.managed.ManagedClassDefSnippet;

public class MappedSuperClassGenerator extends ClassGenerator<ManagedClassDefSnippet> {

    private MappedSuperclass mappedSuperclass;

    public MappedSuperClassGenerator(MappedSuperclass parsedMappedSuperclass, String packageName) {
        super(new ManagedClassDefSnippet());
        this.mappedSuperclass = parsedMappedSuperclass;
        this.rootPackageName = packageName;
        this.packageName = mappedSuperclass.getAbsolutePackage(rootPackageName);
    }

    @Override
    public ManagedClassDefSnippet getClassDef() {

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
        Attributes parsedAttributes = mappedSuperclass.getAttributes();

        if (parsedAttributes != null) {//#ATTRIBUTE_SEQUENCE_FLOW#
            processEmbeddedId(mappedSuperclass, parsedAttributes.getEmbeddedId());
            if (parsedAttributes.getEmbeddedId() == null) {
                processId(parsedAttributes.getId());
            }
            processBasic(parsedAttributes.getBasic());
            processElementCollection(parsedAttributes.getElementCollection());
            processEmbedded(parsedAttributes.getEmbedded());

            processOneToOne(parsedAttributes.getOneToOne());
            processManyToOne(parsedAttributes.getManyToOne());
            processOneToMany(parsedAttributes.getOneToMany());
            processManyToMany(parsedAttributes.getManyToMany());
            processVersion(parsedAttributes.getVersion());
            processTransient(parsedAttributes.getTransient());
        }

        //Class decorations
        classDef = initClassDef(packageName,mappedSuperclass);
        if (StringUtils.isNotBlank(mappedSuperclass.getDescription())) {
            classDef.setDescription(mappedSuperclass.getDescription());
        }
        classDef.setAuthor(mappedSuperclass.getAuthor());
        classDef.setMappedSuperClass(true);
        classDef.setXmlRootElement(mappedSuperclass.getXmlRootElement());
        return classDef;
    }
}
