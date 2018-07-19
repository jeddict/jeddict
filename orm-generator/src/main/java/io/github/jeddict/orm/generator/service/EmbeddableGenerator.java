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

import org.apache.commons.lang3.StringUtils;
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.EmbeddableAttributes;
import io.github.jeddict.orm.generator.compiler.def.EmbeddableDefSnippet;

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

        //Attributes -- Method level annotations
        EmbeddableAttributes parsedEmbeddableAttributes = embeddable.getAttributes();

        if (parsedEmbeddableAttributes != null) {//#ATTRIBUTE_SEQUENCE_FLOW#
            processBasic(parsedEmbeddableAttributes.getBasic());
            processElementCollection(parsedEmbeddableAttributes.getElementCollection());
            processEmbedded(parsedEmbeddableAttributes.getEmbedded());
            processOneToOne(parsedEmbeddableAttributes.getOneToOne());
            processManyToOne(parsedEmbeddableAttributes.getManyToOne());
            processOneToMany(parsedEmbeddableAttributes.getOneToMany());
            processManyToMany(parsedEmbeddableAttributes.getManyToMany());
            processTransient(parsedEmbeddableAttributes.getTransient());
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
