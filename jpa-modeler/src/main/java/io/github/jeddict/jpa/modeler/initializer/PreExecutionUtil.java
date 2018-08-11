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
package io.github.jeddict.jpa.modeler.initializer;

import io.github.jeddict.jpa.spec.extend.SingleRelationAttribute;
import io.github.jeddict.jpa.spec.extend.InheritanceHandler;
import io.github.jeddict.jpa.spec.extend.FlowNode;
import io.github.jeddict.jpa.spec.InheritanceType;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import io.github.jeddict.jpa.spec.IdentifiableClass;
import io.github.jeddict.jpa.spec.extend.CompositePrimaryKeyType;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.DefaultClass;
import static java.lang.Boolean.TRUE;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import io.github.jeddict.jpa.modeler.widget.CompositePKProperty;
import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.modeler.widget.FlowNodeWidget;
import io.github.jeddict.jpa.modeler.widget.InheritanceStateType;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.BRANCH;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.ROOT;
import static io.github.jeddict.jpa.modeler.widget.InheritanceStateType.SINGLETON;
import io.github.jeddict.jpa.modeler.widget.JavaClassWidget;
import io.github.jeddict.jpa.modeler.widget.PersistenceClassWidget;
import io.github.jeddict.jpa.modeler.widget.PrimaryKeyContainerWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.RelationAttributeWidget;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;

/**
 *
 * @author jGauravGupta
 */
public class PreExecutionUtil {

    public static void preExecution(ModelerFile file) {
        JPAModelerScene scene = (JPAModelerScene) file.getModelerScene();
        EntityMappings entityMappings = (EntityMappings) file.getDefinitionElement();

        entityMappings.getDefaultClass().clear();
        for (IBaseElementWidget baseElementWidget : scene.getBaseElements()) {
            if (baseElementWidget instanceof FlowNodeWidget) {
                FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                FlowNode flowNode = (FlowNode) flowNodeWidget.getBaseElementSpec();
                flowNode.setMinimized(flowNodeWidget.isMinimized());
                if (baseElementWidget instanceof JavaClassWidget) {
                    if (baseElementWidget instanceof PersistenceClassWidget) {
                        PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget = (PersistenceClassWidget) baseElementWidget;
                         boolean isAbstract = TRUE.equals(persistenceClassWidget.getBaseElementSpec().getAbstract()); 
                        if (persistenceClassWidget instanceof EntityWidget) {
                            EntityWidget entityWidget = (EntityWidget) persistenceClassWidget;
                            InheritanceHandler classSpec = (InheritanceHandler) entityWidget.getBaseElementSpec();
                            InheritanceStateType inheritanceState = entityWidget.getInheritanceState();
                            switch (inheritanceState) {
                                case LEAF:
                                    classSpec.setDiscriminatorColumn(null);
                                    classSpec.setInheritance(null);
                                    break;
                                case SINGLETON :
                                    classSpec.setDiscriminatorColumn(null);
                                    classSpec.setInheritance(null);
                                    classSpec.setDiscriminatorValue(null);
                                    break;
                            }
                            if (isAbstract) {
                                classSpec.setDiscriminatorValue(null);
                            }
                        }

                    }
                }
            }

        }

        executeCompositePrimaryKeyEvaluation(scene.getBaseElements(), entityMappings);
        clearInheritanceData(file);

    }

    private static void executeCompositePrimaryKeyEvaluation(List<IBaseElementWidget> baseElements, EntityMappings entityMappings) {
        List<IBaseElementWidget> baseElementWidgetPending = new ArrayList<>();
        for (IBaseElementWidget baseElementWidget : baseElements) {
            if (baseElementWidget instanceof PrimaryKeyContainerWidget) {
                PrimaryKeyContainerWidget<? extends IdentifiableClass> primaryKeyContainerWidget = (PrimaryKeyContainerWidget) baseElementWidget;
                if (!manageCompositePrimaryKey(primaryKeyContainerWidget)) {
                    baseElementWidgetPending.add(primaryKeyContainerWidget);
                }
            }
        }
        if (!baseElementWidgetPending.isEmpty()) {
            executeCompositePrimaryKeyEvaluation(baseElementWidgetPending, entityMappings);
        }
    }

    private static boolean manageCompositePrimaryKey(PrimaryKeyContainerWidget<? extends IdentifiableClass> primaryKeyContainerWidget) {
        IdentifiableClass identifiableClass = primaryKeyContainerWidget.getBaseElementSpec();
        EntityMappings entityMappings = identifiableClass.getRootElement();
        CompositePKProperty compositePKProperty = primaryKeyContainerWidget.isCompositePKPropertyAllow();

        if (compositePKProperty == CompositePKProperty.NONE) {
            identifiableClass.clearCompositePrimaryKey();
        } else {
            if (identifiableClass.getCompositePrimaryKeyType() == null) {
                identifiableClass.setCompositePrimaryKeyType(CompositePrimaryKeyType.DEFAULT);
            }
            primaryKeyContainerWidget.onCompositePrimaryKeyTypeChange(identifiableClass.getCompositePrimaryKeyType());//if global config change [Default(IdClass) -> Default(EmbeddedId)]

            if (compositePKProperty == CompositePKProperty.AUTO_CLASS) {
                RelationAttributeWidget<RelationAttribute> relationAttributeWidget = primaryKeyContainerWidget.getDerivedRelationAttributeWidgets().get(0);
                RelationAttribute relationAttribute = relationAttributeWidget.getBaseElementSpec();
                IFlowElementWidget targetElementWidget = relationAttributeWidget.getRelationFlowWidget().getTargetWidget();
                EntityWidget targetEntityWidget = null;
                if (targetElementWidget instanceof EntityWidget) {
                    targetEntityWidget = (EntityWidget) targetElementWidget;
                } else if (targetElementWidget instanceof RelationAttributeWidget) {
                    RelationAttributeWidget targetRelationAttributeWidget = (RelationAttributeWidget) targetElementWidget;
                    targetEntityWidget = (EntityWidget) targetRelationAttributeWidget.getClassWidget();//target can be only Entity
                }
                Entity targetPKConatinerSpec = targetEntityWidget.getBaseElementSpec();
                if (StringUtils.isBlank(targetPKConatinerSpec.getCompositePrimaryKeyClass())) {
                    return false;//send to next execution, its parents are required to evalaute first
                }
                if (identifiableClass.isEmbeddedIdType() && (targetPKConatinerSpec.isIdClassType() || targetPKConatinerSpec.isEmbeddedIdType())) {
                    // when Enity E1 class use IdClass IC1 and
                    //another Enity E2 class use EmbeddedId is also IC1
                    //then register IdClass name here to append @Embeddable annotation
                    DefaultClass _class = entityMappings.addDefaultClass(targetPKConatinerSpec.getPackage(), targetPKConatinerSpec.getCompositePrimaryKeyClass());
                    _class.setGenerateSourceCode(identifiableClass.getGenerateSourceCode());
                    if (identifiableClass.isEmbeddedIdType()) {
                        _class.setEmbeddable(true);
                        primaryKeyContainerWidget.getEmbeddedIdAttributeWidget().getBaseElementSpec().setConnectedClass(_class);
                        primaryKeyContainerWidget.getEmbeddedIdAttributeWidget().getBaseElementSpec().setConnectedAttribute(relationAttribute);// Ex.5.b derived identity
//                                    _class.setAttributes(null);//attribute will be added in parent Entity DefaultClass creation process
                    }
                    if (relationAttribute instanceof SingleRelationAttribute) {
                        ((SingleRelationAttribute) relationAttribute).setMapsId("");
                    }
                } else if (identifiableClass.isIdClassType() && targetPKConatinerSpec.isEmbeddedIdType()) {
                    if (relationAttribute instanceof SingleRelationAttribute) {
                        ((SingleRelationAttribute) relationAttribute).setMapsId(null);
                    }
                }

                //set derived entity IdClass/EmbeddedId class type same as of parent entity IdClass/EmbeddedId class type
                identifiableClass.setCompositePrimaryKeyClass(targetPKConatinerSpec.getCompositePrimaryKeyClass());
            } else { //not CompositePKProperty.NONE
                if (identifiableClass.getCompositePrimaryKeyClass() == null) {
                    identifiableClass.setCompositePrimaryKeyClass(primaryKeyContainerWidget.getName() + "PK");
                }
                DefaultClass _class = identifiableClass.getDefaultClass();
                _class.setGenerateSourceCode(identifiableClass.getGenerateSourceCode());
                if (identifiableClass.isEmbeddedIdType()) {
                    primaryKeyContainerWidget.getEmbeddedIdAttributeWidget().getBaseElementSpec().setConnectedClass(_class);
                }
            }

        }
        identifiableClass.manageCompositePrimaryKey();
        return true;
    }

    public static void clearInheritanceData(ModelerFile file) {
        JPAModelerScene scene = (JPAModelerScene) file.getModelerScene();
        scene.getBaseElements().stream().filter((baseElementWidget) -> (baseElementWidget instanceof EntityWidget)).map((baseElementWidget) -> (EntityWidget) baseElementWidget).forEach((entityWidget) -> {
            Entity entity = entityWidget.getBaseElementSpec();
            InheritanceStateType type = entityWidget.getInheritanceState();
            //clear @Table and @PrimaryKeyJoinColumn
            if ((type == ROOT || type == BRANCH) && (entity.getInheritance() == null || entity.getInheritance().getStrategy() == InheritanceType.SINGLE_TABLE)) {
                entity.getSubclassList().stream().filter(subclass -> subclass instanceof Entity).forEach(subclass -> {
                    Entity subEntity = (Entity) subclass;
                    subEntity.setTable(null);
                    subEntity.setPrimaryKeyForeignKey(null);
                    subEntity.setPrimaryKeyJoinColumn(null);
                });
            }
        });
    }
}
