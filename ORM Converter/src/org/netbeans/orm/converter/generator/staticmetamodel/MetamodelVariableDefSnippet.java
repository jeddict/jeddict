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
package org.netbeans.orm.converter.generator.staticmetamodel;

import java.util.Collection;
import static org.netbeans.jcode.core.util.AttributeType.isArray;
import static org.netbeans.jcode.jpa.JPAConstants.PERSISTENCE_METAMODEL_PACKAGE;
import org.netbeans.jpa.modeler.collaborate.issues.ExceptionUtils;
import org.netbeans.orm.converter.compiler.InvalidDataException;
import org.netbeans.orm.converter.compiler.def.VariableDefSnippet;
import org.netbeans.orm.converter.util.ImportSet;

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
            try {
                return this.getWrapper(type).getSimpleName();
            } catch (ClassNotFoundException ex) {
                ExceptionUtils.printStackTrace(ex);
                throw new RuntimeException("No Wrapper Class found for " + type + " : " + ex.getMessage());
            }
        } else {
            return type;
        }
    }

}
