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

import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.EmbeddableAttributes;
import io.github.jeddict.orm.generator.compiler.def.EmbeddableDefSnippet;
import io.github.jeddict.util.StringUtils;

public class EmbeddableGenerator extends ManagedClassGenerator<EmbeddableDefSnippet> {

    private final Embeddable embeddable;

    public EmbeddableGenerator(Embeddable parsedEmbeddable, String packageName) {
        super(new EmbeddableDefSnippet());
        this.embeddable = parsedEmbeddable;
        this.rootPackageName = packageName;
        this.packageName = embeddable.getAbsolutePackage(rootPackageName);
    }

    @Override
    public EmbeddableDefSnippet getClassDef() {
        classDef.setNoSQL(embeddable.getNoSQL());

        //Attributes -- Method level annotations
        EmbeddableAttributes parsedEmbeddableAttributes = embeddable.getAttributes();

        if (parsedEmbeddableAttributes != null) {//#ATTRIBUTE_SEQUENCE_FLOW#
            parsedEmbeddableAttributes.getBasic().forEach(this::processBasic);
            parsedEmbeddableAttributes.getElementCollection().forEach(this::processElementCollection);
            parsedEmbeddableAttributes.getEmbedded().forEach(this::processEmbedded);
            parsedEmbeddableAttributes.getOneToOne().forEach(this::processOneToOne);
            parsedEmbeddableAttributes.getManyToOne().forEach(this::processManyToOne);
            parsedEmbeddableAttributes.getOneToMany().forEach(this::processOneToMany);
            parsedEmbeddableAttributes.getManyToMany().forEach(this::processManyToMany);
            parsedEmbeddableAttributes.getTransient().forEach(this::processTransient);
        }

        //Class decorations
        classDef = initClassDef(packageName,embeddable);
        if (StringUtils.isNotBlank(embeddable.getDescription())) {
            classDef.setDescription(embeddable.getDescription());
        }
        classDef.setAuthor(embeddable.getAuthor());
        classDef.setXmlRootElement(embeddable.getXmlRootElement());
        return classDef;
    }
}
