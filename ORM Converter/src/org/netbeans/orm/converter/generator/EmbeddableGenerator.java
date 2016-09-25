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
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.EmbeddableAttributes;
import org.netbeans.orm.converter.generator.managed.ManagedClassDefSnippet;

public class EmbeddableGenerator extends ClassGenerator<ManagedClassDefSnippet> {

    private Embeddable embeddable;

    public EmbeddableGenerator(Embeddable parsedEmbeddable, String packageName) {
        super(new ManagedClassDefSnippet());
        this.embeddable = parsedEmbeddable;
        this.rootPackageName = packageName;
        this.packageName = embeddable.getPackage(rootPackageName);
    }

    @Override
    public ManagedClassDefSnippet getClassDef() {

        //Commented -- revist for Phase 3
        //ParsedAccessType accessType = parsedEmbeddable.getAccess();

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
        classDef.setEmbeddable(true);
        return classDef;
    }
}
