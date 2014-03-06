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
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.EmbeddableAttributes;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.compiler.VariableDefSnippet;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConvLogger;

public class EmbeddableGenerator extends ClassGenerator {

    private static Logger logger = ORMConvLogger.getLogger(
            EmbeddableGenerator.class);

    private Embeddable embeddable = null;

    public EmbeddableGenerator(
            Embeddable parsedEmbeddable, String packageName) {

        this.packageName = packageName;
        this.embeddable = parsedEmbeddable;
    }

    public ClassDefSnippet getClassDef() {

        //Commented -- revist for Phase 3
        //ParsedAccessType accessType = parsedEmbeddable.getAccess();
        embeddable.getAttributes();

        //Attributes -- Method level annotations
        EmbeddableAttributes parsedEmbeddableAttributes
                = embeddable.getAttributes();

        if (parsedEmbeddableAttributes != null) {
            processBasic(parsedEmbeddableAttributes.getBasic());
            processTransient(parsedEmbeddableAttributes.getTransient());
            processElementCollection(parsedEmbeddableAttributes.getElementCollection());
            processEmbedded(parsedEmbeddableAttributes.getEmbedded());
            processManyToMany(parsedEmbeddableAttributes.getManyToMany());
            processManyToOne(parsedEmbeddableAttributes.getManyToOne());
            processOneToMany(parsedEmbeddableAttributes.getOneToMany());
            processOneToOne(parsedEmbeddableAttributes.getOneToOne());

        }

        //Class decorations
        ClassHelper classHelper = new ClassHelper(embeddable.getClazz());
        classHelper.setPackageName(packageName);

        classDef.setVariableDefs(
                new ArrayList<VariableDefSnippet>(variables.values()));
        classDef.setClassName(classHelper.getFQClassName());
        classDef.setPackageName(classHelper.getPackageName());
        classDef.setEmbeddable(true);

        return classDef;
    }
}
