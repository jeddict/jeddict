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
package org.netbeans.orm.converter.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.netbeans.orm.converter.util.ClassHelper;

public class TypeIdentifierSnippet implements Snippet {

    private String type = null;

    private VariableDefSnippet variableDef = null;

    private Collection<String> importSnippets = Collections.EMPTY_LIST;

    public TypeIdentifierSnippet(VariableDefSnippet variableDef) {
        this.variableDef = variableDef;
    }

    public String getVariableType() {
        processVariableType();
        return type;
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {
        processVariableType();
        return importSnippets;
    }

    public String getSnippet() throws InvalidDataException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void setType(String type) {
        this.type = type;
        processVariableType();
    }

    private ClassHelper getClassHelper(String targetEntity) {

        CompilerConfigManager compilerConfigManager
                = CompilerConfigManager.getInstance();

        String defaultPkgName
                = compilerConfigManager.getCompilerConfig().getDefaultPkgName();

        ClassHelper classHelper = new ClassHelper(targetEntity);

        classHelper.setPackageName(defaultPkgName);

        return classHelper;
    }

    private void processVariableType() {

        if (type != null) {
            return;
        }

        if (variableDef.getRelationDef() != null) {

            RelationDefSnippet relationDef = variableDef.getRelationDef();

            if (relationDef instanceof OneToManySnippet
                    || relationDef instanceof ManyToManySnippet) {

                importSnippets = new ArrayList<String>();

                ClassHelper collectionTypeClassHelper = null;
                if (relationDef instanceof OneToManySnippet) {
                    collectionTypeClassHelper = getClassHelper(((OneToManySnippet) relationDef).getCollectionType());
                } else if (relationDef instanceof ManyToManySnippet) {
                    collectionTypeClassHelper = getClassHelper(((ManyToManySnippet) relationDef).getCollectionType());
                }

                ClassHelper classHelper = getClassHelper(relationDef.getTargetEntity());
                type = collectionTypeClassHelper.getClassName() + "<" + classHelper.getClassName() + ">";
                importSnippets.add(collectionTypeClassHelper.getFQClassName());
                importSnippets.add(classHelper.getFQClassName());
                return;
            } else {
                ClassHelper classHelper = getClassHelper(relationDef.getTargetEntity());
                type = classHelper.getClassName();
                importSnippets = Collections.singletonList(classHelper.getFQClassName());
                return;
            }
        }
        if (variableDef.getElementCollection() != null) {

            ElementCollectionSnippet elementCollection = variableDef.getElementCollection();
            importSnippets = new ArrayList<String>();

            ClassHelper collectionTypeClassHelper = getClassHelper(elementCollection.getCollectionType());
            ClassHelper classHelper = getClassHelper(elementCollection.getTargetClass());
            type = collectionTypeClassHelper.getClassName() + "<" + classHelper.getClassName() + ">";
            importSnippets.add(collectionTypeClassHelper.getFQClassName());
            importSnippets.add(classHelper.getFQClassName());
            return;
        }

        if (variableDef.isLob()) {
            type = "byte[]";
            return;
        }

        if (variableDef.isTemporal()) {
            importSnippets = Collections.singletonList("java.util.Calendar");
            type = "Calendar";
            return;
        }

        ColumnDefSnippet columnDef = variableDef.getColumnDef();

        if (columnDef != null) {

            if (columnDef.getPrecision() != 0
                    && columnDef.getScale() != 0) {

                type = "double";
                return;
            } else if (columnDef.getPrecision() != 0
                    && columnDef.getScale() == 0) {

                type = "long";
                return;
            }
        }

        type = "String";
    }
}
