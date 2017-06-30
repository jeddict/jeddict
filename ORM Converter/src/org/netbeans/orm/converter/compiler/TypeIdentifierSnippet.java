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
import org.apache.commons.lang.StringUtils;
import org.netbeans.jcode.core.util.AttributeType;
import org.netbeans.jcode.core.util.JavaSourceHelper;
import static org.netbeans.jcode.core.util.AttributeType.BYTE_ARRAY;
import static org.netbeans.jcode.core.util.AttributeType.CALENDAR;
import static org.netbeans.jcode.core.util.AttributeType.DOUBLE;
import static org.netbeans.jcode.core.util.AttributeType.LONG;
import static org.netbeans.jcode.core.util.AttributeType.STRING;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConverterUtil;
import org.openide.util.Exceptions;

public class TypeIdentifierSnippet implements Snippet {

    private static final String SELECT_MAP_KEY = "Map_Key_Missing";
    private String type, 
            implementationType, //ArrayList, HashSet etc
            constraintType;//bv, custom annotation etc

    private VariableDefSnippet variableDef = null;

    private Collection<String> importSnippets = Collections.EMPTY_LIST;

    public TypeIdentifierSnippet(VariableDefSnippet variableDef) {
        this.variableDef = variableDef;
    }

    public String getVariableType() {
        processVariableType();
        return type;
    }
    
    public String getConstraintVariableType() {
        processVariableType();
        return constraintType==null?type:constraintType;
    }

    public String getImplementationType(){
        return implementationType;
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

        ClassHelper classHelper = null;
        if (StringUtils.isNotBlank(targetEntity)) {
            classHelper = new ClassHelper(targetEntity);
            int count = targetEntity.endsWith(ORMConverterUtil.CLASS_SUFFIX) ? 2 : 1;
            if (targetEntity.split("\\.").length <= count) {
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

        if (type != null) {
            return;
        }
        try {
            if (variableDef.getRelationDef() != null) {

                RelationDefSnippet relationDef = variableDef.getRelationDef();

                if (relationDef instanceof MultiRelationAttributeSnippet) {
                    MultiRelationAttributeSnippet multiRelationAttributeSnippet = (MultiRelationAttributeSnippet) relationDef;

                    importSnippets = new ArrayList<>();
                    ClassHelper collectionTypeClassHelper = getClassHelper(multiRelationAttributeSnippet.getCollectionType());
                    ClassHelper collectionImplTypeClassHelper = getClassHelper(multiRelationAttributeSnippet.getCollectionImplType());

                    ClassHelper classHelper = getClassHelper(relationDef.getTargetEntity());
                    classHelper.setPackageName(relationDef.getTargetEntityPackage());

                    ClassHelper mapKeyClassHelper = null;
                    Class _class = collectionTypeClassHelper.getClazz();

                    if (_class != null && Map.class.isAssignableFrom(_class)) {
                        if (multiRelationAttributeSnippet.getMapKeySnippet() != null && multiRelationAttributeSnippet.getMapKeySnippet().getMapKeyAttributeType().getClassName() != null) {
                            mapKeyClassHelper = multiRelationAttributeSnippet.getMapKeySnippet().getMapKeyAttributeType();
                            constraintType = collectionTypeClassHelper.getClassName() 
                                    + "<" + variableDef.getInlineKeyAnnotation() + variableDef.getInlineKeyConstraint() + wrap(mapKeyClassHelper.getClassName()) 
                                    + "," + variableDef.getInlineValueAnnotation() + variableDef.getInlineValueConstraint() + wrap(classHelper.getClassName()) 
                                    + ">";
                            type = collectionTypeClassHelper.getClassName() 
                                    + "<" + wrap(mapKeyClassHelper.getClassName()) 
                                    + "," + wrap(classHelper.getClassName()) 
                                    + ">";
                        } else {
                            constraintType = collectionTypeClassHelper.getClassName() 
                                    + "<" + variableDef.getInlineKeyAnnotation() + variableDef.getInlineKeyConstraint() + SELECT_MAP_KEY 
                                    + "," + variableDef.getInlineValueAnnotation() + variableDef.getInlineValueConstraint() + wrap(classHelper.getClassName()) 
                                    + ">";
                            type = collectionTypeClassHelper.getClassName() 
                                    + "<" + SELECT_MAP_KEY 
                                    + "," + wrap(classHelper.getClassName()) 
                                    + ">";
                        }
                    } else {
                        constraintType = collectionTypeClassHelper.getClassName() 
                                + "<" + variableDef.getInlineValueAnnotation() + variableDef.getInlineValueConstraint() + wrap(classHelper.getClassName()) 
                                + ">";
                        type = collectionTypeClassHelper.getClassName() 
                                + "<" + wrap(classHelper.getClassName()) 
                                + ">";
                    }

                    importSnippets.add(collectionTypeClassHelper.getFQClassName());
                    if (_class != null && Map.class.isAssignableFrom(_class) && mapKeyClassHelper != null) {
                        importSnippets.add(mapKeyClassHelper.getFQClassName());
                    }
                    importSnippets.add(classHelper.getFQClassName());
                    if (collectionImplTypeClassHelper != null) {
                        implementationType = collectionImplTypeClassHelper.getClassName();
                        importSnippets.add(collectionImplTypeClassHelper.getFQClassName());
                    }
                    return;
                } else {
                    ClassHelper classHelper = getClassHelper(relationDef.getTargetEntity());
                    classHelper.setPackageName(relationDef.getTargetEntityPackage());
                    type = classHelper.getClassName();
                    importSnippets = Collections.singletonList(classHelper.getFQClassName());
                    return;
                }
            } else if (variableDef.getElementCollection() != null) {

                ElementCollectionSnippet elementCollection = variableDef.getElementCollection();
                importSnippets = new ArrayList<>();

                ClassHelper collectionTypeClassHelper = getClassHelper(elementCollection.getCollectionType());
                ClassHelper collectionImplTypeClassHelper = getClassHelper(elementCollection.getCollectionImplType());
                ClassHelper classHelper = getClassHelper(elementCollection.getTargetClass());
                if (elementCollection.getTargetClassPackage() != null) {
                    classHelper.setPackageName(elementCollection.getTargetClassPackage());
                }
                ClassHelper mapKeyClassHelper = null;
                Class _class = collectionTypeClassHelper.getClazz();
                if (_class != null && Map.class.isAssignableFrom(_class)) {
                    if (elementCollection.getMapKeySnippet() != null && elementCollection.getMapKeySnippet().getMapKeyAttributeType().getClassName() != null) {
                        mapKeyClassHelper = elementCollection.getMapKeySnippet().getMapKeyAttributeType();
                        constraintType = collectionTypeClassHelper.getClassName() + "<" + variableDef.getInlineKeyAnnotation() + variableDef.getInlineKeyConstraint() + wrap(mapKeyClassHelper.getClassName()) + "," + variableDef.getInlineValueAnnotation() + variableDef.getInlineValueConstraint() + wrap(classHelper.getClassName()) + ">";
                        type = collectionTypeClassHelper.getClassName() + "<" + wrap(mapKeyClassHelper.getClassName()) + "," + wrap(classHelper.getClassName()) + ">";
                    } else {
                        constraintType = collectionTypeClassHelper.getClassName() + "<" + variableDef.getInlineKeyAnnotation() + variableDef.getInlineKeyConstraint() + SELECT_MAP_KEY + "," + variableDef.getInlineValueAnnotation() + variableDef.getInlineValueConstraint() + wrap(classHelper.getClassName()) + ">";
                        type = collectionTypeClassHelper.getClassName() + "<" + SELECT_MAP_KEY + "," + wrap(classHelper.getClassName()) + ">";
                    }
                } else {
                    constraintType = collectionTypeClassHelper.getClassName() + "<" + variableDef.getInlineValueAnnotation() + variableDef.getInlineValueConstraint() + wrap(classHelper.getClassName()) + ">";
                    type = collectionTypeClassHelper.getClassName() + "<" + wrap(classHelper.getClassName()) + ">";
                }

                importSnippets.add(collectionTypeClassHelper.getFQClassName());
                if (_class != null && Map.class.isAssignableFrom(_class) && mapKeyClassHelper != null) {
                    importSnippets.add(mapKeyClassHelper.getFQClassName());
                }
                importSnippets.add(classHelper.getFQClassName());
                if (collectionImplTypeClassHelper != null) {
                        implementationType = collectionImplTypeClassHelper.getClassName();
                        importSnippets.add(collectionImplTypeClassHelper.getFQClassName());
                }
                return;
            }

            if (variableDef.isLob()) {
                type = BYTE_ARRAY;
                return;
            }

            if (variableDef.getTemporal() != null) {
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
        } catch (InvalidDataException ex) {
            Exceptions.printStackTrace(ex);
            throw new RuntimeException(ex);
        }
        
    }
}
