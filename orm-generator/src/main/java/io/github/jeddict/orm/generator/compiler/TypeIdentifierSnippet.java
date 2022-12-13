/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.orm.generator.compiler;

import io.github.jeddict.jcode.util.AttributeType;
import io.github.jeddict.orm.generator.compiler.def.VariableDefSnippet;
import io.github.jeddict.orm.generator.util.ClassHelper;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import io.github.jeddict.util.StringUtils;
import org.openide.util.Exceptions;

public class TypeIdentifierSnippet implements Snippet {

    private static final String SELECT_MAP_KEY = "Map_Key_Missing";
    private String type,
            implementationType, //ArrayList, HashSet etc
            constraintType;//bv, custom annotation etc

    private VariableDefSnippet variableDef = null;

    private final Collection<String> importSnippets = new ArrayList<>();

    public TypeIdentifierSnippet(VariableDefSnippet variableDef) {
        this.variableDef = variableDef;
        processVariableType();
    }

    public String getVariableType() {
        return type;
    }

    public String getConstraintVariableType() {
        return constraintType == null ? type : constraintType;
    }

    public String getImplementationType() {
        return implementationType;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return importSnippets;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private ClassHelper getClassHelper(String clazz) {
        ClassHelper classHelper = null;
        if (StringUtils.isNotBlank(clazz)) {
            classHelper = new ClassHelper(clazz);
            int count = clazz.endsWith(ORMConverterUtil.CLASS_SUFFIX) ? 2 : 1;
            if (clazz.split("\\.").length <= count) {
                CompilerConfigManager compilerConfigManager = CompilerConfigManager.getInstance();
                String defaultPkgName = compilerConfigManager.getCompilerConfig().getDefaultPkgName();
                classHelper.setPackageName(defaultPkgName);
            }
        }
        return classHelper;
    }

    private String wrap(String dataType) {
        if (dataType == null) {
            return null;
        }
        return AttributeType.Type.PRIMITIVE == AttributeType.getType(dataType)
                ? AttributeType.getWrapperType(dataType) : dataType;
    }

    private void processVariableType() {
        try {
            ClassHelper variable = variableDef.getClassHelper();
            ClassHelper collectionType = getClassHelper(variableDef.getCollectionType());
            ClassHelper collectionImplType = getClassHelper(variableDef.getCollectionImplType());
            if (collectionImplType != null) {
                implementationType = collectionImplType.getClassName();
                importSnippets.add(collectionImplType.getFQClassName());
            }
            if (collectionType != null && collectionType.getClazz() != null) {
                if (!Map.class.isAssignableFrom(collectionType.getClazz())) {
                    constraintType = collectionType.getClassName() + "<" + variableDef.getInlineValueAnnotation() + variableDef.getInlineValueConstraint() + wrap(variable.getClassName()) + ">";
                    type = collectionType.getClassName() + "<" + wrap(variable.getClassName()) + ">";
                }
                importSnippets.add(collectionType.getFQClassName());
            }

            if (variableDef.getRelation() != null && variableDef.getRelation() instanceof MultiRelationAttributeSnippet) {
                MultiRelationAttributeSnippet multiRelationSnippet = (MultiRelationAttributeSnippet) variableDef.getRelation();

                ClassHelper mapKeyClassHelper = null;
                Class _class = collectionType.getClazz();

                if (_class != null && Map.class.isAssignableFrom(_class)) {
                    if (multiRelationSnippet.getMapKeySnippet() != null && multiRelationSnippet.getMapKeySnippet().getMapKeyAttributeType().getClassName() != null) {
                        mapKeyClassHelper = multiRelationSnippet.getMapKeySnippet().getMapKeyAttributeType();
                        constraintType = collectionType.getClassName()
                                + "<" + variableDef.getInlineKeyAnnotation() + variableDef.getInlineKeyConstraint() + wrap(mapKeyClassHelper.getClassName())
                                + "," + variableDef.getInlineValueAnnotation() + variableDef.getInlineValueConstraint() + wrap(variable.getClassName())
                                + ">";
                        type = collectionType.getClassName()
                                + "<" + wrap(mapKeyClassHelper.getClassName())
                                + "," + wrap(variable.getClassName())
                                + ">";
                    } else {
                        constraintType = collectionType.getClassName()
                                + "<" + variableDef.getInlineKeyAnnotation() + variableDef.getInlineKeyConstraint() + SELECT_MAP_KEY
                                + "," + variableDef.getInlineValueAnnotation() + variableDef.getInlineValueConstraint() + wrap(variable.getClassName())
                                + ">";
                        type = collectionType.getClassName()
                                + "<" + SELECT_MAP_KEY
                                + "," + wrap(variable.getClassName())
                                + ">";
                    }
                }
                if (_class != null && Map.class.isAssignableFrom(_class) && mapKeyClassHelper != null) {
                    importSnippets.add(mapKeyClassHelper.getFQClassName());
                }
                importSnippets.add(variable.getFQClassName());
            } else if (variableDef.getElementCollection() != null) {
                ElementCollectionSnippet elementCollection = variableDef.getElementCollection();
                ClassHelper mapKeyClassHelper = null;
                Class _class = collectionType.getClazz();
                if (_class != null && Map.class.isAssignableFrom(_class)) {
                    if (elementCollection.getMapKeySnippet() != null && elementCollection.getMapKeySnippet().getMapKeyAttributeType().getClassName() != null) {
                        mapKeyClassHelper = elementCollection.getMapKeySnippet().getMapKeyAttributeType();
                        constraintType = collectionType.getClassName() + "<" + variableDef.getInlineKeyAnnotation() + variableDef.getInlineKeyConstraint() + wrap(mapKeyClassHelper.getClassName()) + "," + variableDef.getInlineValueAnnotation() + variableDef.getInlineValueConstraint() + wrap(variable.getClassName()) + ">";
                        type = collectionType.getClassName() + "<" + wrap(mapKeyClassHelper.getClassName()) + "," + wrap(variable.getClassName()) + ">";
                    } else {
                        constraintType = collectionType.getClassName() + "<" + variableDef.getInlineKeyAnnotation() + variableDef.getInlineKeyConstraint() + SELECT_MAP_KEY + "," + variableDef.getInlineValueAnnotation() + variableDef.getInlineValueConstraint() + wrap(variable.getClassName()) + ">";
                        type = collectionType.getClassName() + "<" + SELECT_MAP_KEY + "," + wrap(variable.getClassName()) + ">";
                    }
                }
                if (_class != null && Map.class.isAssignableFrom(_class) && mapKeyClassHelper != null) {
                    importSnippets.add(mapKeyClassHelper.getFQClassName());
                }
                importSnippets.add(variable.getFQClassName());
            } else {
                importSnippets.add(variable.getFQClassName());
            }

//            if (variableDef.getColumnDef() != null) {
//                ColumnDefSnippet columnDef = variableDef.getColumnDef();
//                if (columnDef.getPrecision() != 0 && columnDef.getScale() != 0) {
//                    type = DOUBLE;
//                    return;
//                } else if (columnDef.getPrecision() != 0 && columnDef.getScale() == 0) {
//                    type = LONG;
//                    return;
//                }
//            }
//
//            type = STRING;
        } catch (InvalidDataException ex) {
            Exceptions.printStackTrace(ex);
            throw new RuntimeException(ex);
        }

    }
}
