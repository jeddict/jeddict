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
import java.util.Map;
import org.netbeans.jcode.core.util.AttributeType;
import org.netbeans.jcode.core.util.JavaSourceHelper;
import static org.netbeans.jcode.core.util.AttributeType.BYTE_ARRAY;
import static org.netbeans.jcode.core.util.AttributeType.CALENDAR;
import static org.netbeans.jcode.core.util.AttributeType.DOUBLE;
import static org.netbeans.jcode.core.util.AttributeType.LONG;
import static org.netbeans.jcode.core.util.AttributeType.STRING;
import org.netbeans.orm.converter.util.ClassHelper;

public class TypeIdentifierSnippet implements Snippet {

    private static final String SELECT_MAP_KEY = "Map_Key_Missing";
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

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        processVariableType();
        return importSnippets;
    }

    @Override
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
    
    private String wrap(String dataType) {
        return AttributeType.Type.PRIMITIVE ==  AttributeType.getType(dataType) ?
                AttributeType.getWrapperType(dataType) : dataType;
    }

    private void processVariableType() {

        if (type != null) {
            return;
        }

        if (variableDef.getRelationDef() != null) {

            RelationDefSnippet relationDef = variableDef.getRelationDef();

            if (relationDef instanceof MultiRelationAttributeSnippet) {
                MultiRelationAttributeSnippet multiRelationAttributeSnippet = (MultiRelationAttributeSnippet) relationDef;

                importSnippets = new ArrayList<>();

                ClassHelper collectionTypeClassHelper = getClassHelper(((MultiRelationAttributeSnippet) relationDef).getCollectionType());
                
                ClassHelper classHelper = getClassHelper(relationDef.getTargetEntity());
                ClassHelper mapKeyClassHelper = null;
                Class _class = collectionTypeClassHelper.getClazz();
                  
                if (_class != null && Map.class.isAssignableFrom(_class)) {
                    if (multiRelationAttributeSnippet.getMapKeySnippet() != null) {
                        mapKeyClassHelper = multiRelationAttributeSnippet.getMapKeySnippet().getMapKeyAttributeType();
                        type = collectionTypeClassHelper.getClassName() + "<" + wrap(mapKeyClassHelper.getClassName()) + "," + wrap(classHelper.getClassName()) + ">";
                    } else {
                        type = collectionTypeClassHelper.getClassName() + "<" + SELECT_MAP_KEY + "," + wrap(classHelper.getClassName()) + ">";
                    }
                } else {
                    type = collectionTypeClassHelper.getClassName() + "<" + wrap(classHelper.getClassName()) + ">";
                }

                importSnippets.add(collectionTypeClassHelper.getFQClassName());
                if (_class != null && Map.class.isAssignableFrom(_class) && mapKeyClassHelper!=null) {
                    importSnippets.add(mapKeyClassHelper.getFQClassName());
                }
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
            importSnippets = new ArrayList<>();

            ClassHelper collectionTypeClassHelper = getClassHelper(elementCollection.getCollectionType());
            ClassHelper classHelper = getClassHelper(elementCollection.getTargetClass());
            ClassHelper mapKeyClassHelper = null;
            Class _class = collectionTypeClassHelper.getClazz();
                if (_class != null && Map.class.isAssignableFrom(_class)) {
                    if (elementCollection.getMapKeySnippet() != null) {
                        mapKeyClassHelper = elementCollection.getMapKeySnippet().getMapKeyAttributeType();
                        type = collectionTypeClassHelper.getClassName() + "<" + wrap(mapKeyClassHelper.getClassName()) + "," + wrap(classHelper.getClassName()) + ">";
                    } else {
                        type = collectionTypeClassHelper.getClassName() + "<" + SELECT_MAP_KEY + "," + wrap(classHelper.getClassName()) + ">";
                    }
                } else {
                    type = collectionTypeClassHelper.getClassName() + "<" + wrap(classHelper.getClassName()) + ">";
                }
            
            importSnippets.add(collectionTypeClassHelper.getFQClassName());
            if (_class != null && Map.class.isAssignableFrom(_class) && mapKeyClassHelper!=null) {
                    importSnippets.add(mapKeyClassHelper.getFQClassName());
            }
            importSnippets.add(classHelper.getFQClassName());
            return;
        }

        if (variableDef.isLob()) {
            type = BYTE_ARRAY;
            return;
        }

        if (variableDef.getTemporal()!=null) {
            importSnippets = Collections.singletonList(CALENDAR);
            type = JavaSourceHelper.getSimpleClassName(CALENDAR);
            return;
        }

        ColumnDefSnippet columnDef = variableDef.getColumnDef();

        if (columnDef != null) {

            if (columnDef.getPrecision() != 0
                    && columnDef.getScale() != 0) {

                type = DOUBLE;
                return;
            } else if (columnDef.getPrecision() != 0
                    && columnDef.getScale() == 0) {

                type = LONG;
                return;
            }
        }

        type = STRING;
    }
}
