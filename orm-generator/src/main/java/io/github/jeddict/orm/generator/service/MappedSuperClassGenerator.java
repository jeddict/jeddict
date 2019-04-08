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
package io.github.jeddict.orm.generator.service;

import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.MappedSuperclass;
import io.github.jeddict.jpa.spec.extend.IPrimaryKeyAttributes;
import io.github.jeddict.orm.generator.compiler.def.MappedSuperClassDefSnippet;
import static java.util.Objects.nonNull;
import static io.github.jeddict.util.StringUtils.isNotBlank;

public class MappedSuperClassGenerator extends IdentifiableClassGenerator<MappedSuperClassDefSnippet> {

    private final MappedSuperclass mappedSuperclass;

    public MappedSuperClassGenerator(MappedSuperclass parsedMappedSuperclass, String packageName) {
        super(new MappedSuperClassDefSnippet());
        this.mappedSuperclass = parsedMappedSuperclass;
        this.rootPackageName = packageName;
        this.packageName = mappedSuperclass.getAbsolutePackage(rootPackageName);
    }

    @Override
    public MappedSuperClassDefSnippet getClassDef() {
        classDef.setNoSQL(mappedSuperclass.getNoSQL());

        if (!classDef.isNoSQL()) {
            //Classlevel annotations
            processIdClass(mappedSuperclass.getIdClass());
            processEntityListeners(mappedSuperclass.getEntityListeners());

            processDefaultExcludeListeners(
                    mappedSuperclass.getExcludeDefaultListeners());
            processExcludeSuperclassListeners(
                    mappedSuperclass.getExcludeSuperclassListeners());

            //Queries
            processNamedQueries(mappedSuperclass.getNamedQuery());
            processNamedNativeQueries(mappedSuperclass.getNamedNativeQuery());

            //StoredProcedures
            processNamedStoredProcedureQueries((EntityMappings) mappedSuperclass.getRootElement(), mappedSuperclass.getNamedStoredProcedureQuery());
        }

        //Attributes -- Method level annotations
        IPrimaryKeyAttributes parsedAttributes = mappedSuperclass.getAttributes();

        if (nonNull(parsedAttributes)) {//#ATTRIBUTE_SEQUENCE_FLOW#
            processEmbeddedId(mappedSuperclass, parsedAttributes.getEmbeddedId());
            if (parsedAttributes.getEmbeddedId() == null) {
                parsedAttributes.getId().forEach(this::processId);
            }
            parsedAttributes.getBasic().forEach(this::processBasic);
            parsedAttributes.getElementCollection().forEach(this::processElementCollection);
            parsedAttributes.getEmbedded().forEach(this::processEmbedded);
            parsedAttributes.getOneToOne().forEach(this::processOneToOne);
            parsedAttributes.getManyToOne().forEach(this::processManyToOne);
            parsedAttributes.getOneToMany().forEach(this::processOneToMany);
            parsedAttributes.getManyToMany().forEach(this::processManyToMany);
            parsedAttributes.getVersion().forEach(this::processVersion);
            parsedAttributes.getTransient().forEach(this::processTransient);
        }

        //Class decorations
        classDef = initClassDef(packageName,mappedSuperclass);
        if (isNotBlank(mappedSuperclass.getDescription())) {
            classDef.setDescription(mappedSuperclass.getDescription());
        }
        classDef.setAuthor(mappedSuperclass.getAuthor());
        classDef.setXmlRootElement(mappedSuperclass.getXmlRootElement());
        return classDef;
    }
}
