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
package io.github.jeddict.orm.generator.service.staticmetamodel;

import static io.github.jeddict.jcode.JPAConstants.PERSISTENCE_METAMODEL_PACKAGE;
import static io.github.jeddict.jcode.util.AttributeType.getWrapperType;
import static io.github.jeddict.jcode.util.AttributeType.isArray;
import static io.github.jeddict.jcode.util.AttributeType.isPrimitive;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.compiler.def.VariableDefSnippet;
import io.github.jeddict.orm.generator.util.ImportSet;
import java.util.Collection;

public class MetamodelVariableDefSnippet extends VariableDefSnippet {

    private MetamodelAttributeType attributeType;

    /**
     * @return the attributeType
     */
    public MetamodelAttributeType getAttributeType() {
        return attributeType;
    }

    /**
     * @param attributeType the attributeType to set
     */
    public void setAttributeType(MetamodelAttributeType attributeType) {
        this.attributeType = attributeType;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        ImportSet importSnippets = new ImportSet();
        importSnippets.addAll(super.getImportSnippets());
        importSnippets.add(PERSISTENCE_METAMODEL_PACKAGE + attributeType.getType());
        return importSnippets;
    }

    @Override
    public String getType() {
        String type = super.getType();
        if (isArray(type)) {
            int length = type.length();
            type = type.substring(0, length - 2);
        }
        if (isPrimitive(type)) {
            return getWrapperType(type);
        } else {
            return type;
        }
    }

}
