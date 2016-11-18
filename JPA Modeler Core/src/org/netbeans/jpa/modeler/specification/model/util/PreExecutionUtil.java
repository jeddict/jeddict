/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.jpa.modeler.specification.model.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.core.widget.CompositePKProperty;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.FlowNodeWidget;
import org.netbeans.jpa.modeler.core.widget.InheritanceStateType;
import static org.netbeans.jpa.modeler.core.widget.InheritanceStateType.BRANCH;
import static org.netbeans.jpa.modeler.core.widget.InheritanceStateType.ROOT;
import static org.netbeans.jpa.modeler.core.widget.InheritanceStateType.SINGLETON;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.SingleRelationAttributeWidget;
import org.netbeans.jpa.modeler.spec.DefaultAttribute;
import org.netbeans.jpa.modeler.spec.DefaultClass;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.*;
import org.netbeans.jpa.modeler.spec.extend.CompositePrimaryKeyType;
import org.netbeans.jpa.modeler.spec.extend.*;
import org.netbeans.jpa.modeler.spec.extend.PrimaryKeyContainer;
import org.netbeans.jpa.modeler.spec.extend.SingleRelationAttribute;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.extend.InheritanceHandler;

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
                        if (persistenceClassWidget instanceof EntityWidget) {
                            EntityWidget entityWidget = (EntityWidget) persistenceClassWidget;
                            InheritanceHandler classSpec = (InheritanceHandler) entityWidget.getBaseElementSpec();
                            InheritanceStateType inheritanceState = entityWidget.getInheritanceState();
                            switch (inheritanceState) {
                                case LEAF:
                                case SINGLETON:
                                    classSpec.setDiscriminatorColumn(null);
                                    classSpec.setInheritance(null);
                                    break;
                                case ROOT:
                                    classSpec.setDiscriminatorValue(null);
                                    break;
                            }
                        }

                    }
                }
            }

        }

        executeCompositePrimaryKeyEvaluation(scene.getBaseElements(), entityMappings);
//        addDefaultJoinColumnForCompositePK(scene.getBaseElements(), entityMappings);

        clearInheritanceData(file);

    }

    private static void executeCompositePrimaryKeyEvaluation(List<IBaseElementWidget> baseElements, EntityMappings entityMappings) {
        List<IBaseElementWidget> baseElementWidgetPending = new ArrayList<>();
        for (IBaseElementWidget baseElementWidget : baseElements) {
            if (baseElementWidget instanceof PersistenceClassWidget) {
                PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget = (PersistenceClassWidget) baseElementWidget;
                if (!manageCompositePrimaryKey(persistenceClassWidget, entityMappings)) {
                    baseElementWidgetPending.add(persistenceClassWidget);
                }
            }
        }
        if (!baseElementWidgetPending.isEmpty()) {
            executeCompositePrimaryKeyEvaluation(baseElementWidgetPending, entityMappings);
        }
    }

    private static boolean manageCompositePrimaryKey(PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget, EntityMappings entityMappings) {
           //Start : IDCLASS,EMBEDDEDID //((Entity) persistenceClassWidget.getBaseElementSpec()).getClazz()
     if (persistenceClassWidget.getBaseElementSpec() instanceof PrimaryKeyContainer) {
            ManagedClass managedClass = persistenceClassWidget.getBaseElementSpec();
            PrimaryKeyContainer pkContainerSpec = (PrimaryKeyContainer) managedClass;
            CompositePKProperty compositePKProperty = persistenceClassWidget.isCompositePKPropertyAllow();

            if (compositePKProperty == CompositePKProperty.NONE) {
                pkContainerSpec.clearCompositePrimaryKey();
            } else {
                persistenceClassWidget.onCompositePrimaryKeyTypeChange(pkContainerSpec.getCompositePrimaryKeyType());//if global config change [Default(IdClass) -> Default(EmbeddedId)]
                if (compositePKProperty == CompositePKProperty.AUTO_CLASS) {
                    RelationAttributeWidget relationAttributeWidget = persistenceClassWidget.getDerivedRelationAttributeWidgets().get(0);
                    RelationAttribute relationAttribute = (RelationAttribute) relationAttributeWidget.getBaseElementSpec();
                    IFlowElementWidget targetElementWidget = (EntityWidget) relationAttributeWidget.getRelationFlowWidget().getTargetWidget();
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
                    if (pkContainerSpec.isEmbeddedIdType() && (targetPKConatinerSpec.isIdClassType() || targetPKConatinerSpec.isEmbeddedIdType())) {
                        // when Enity E1 class use IdClass IC1 and
                        //another Enity E2 class use EmbeddedId is also IC1
                        //then register IdClass name here to append @Embeddable annotation
                        DefaultClass _class = entityMappings.addDefaultClass(targetPKConatinerSpec.getPackage(), targetPKConatinerSpec.getCompositePrimaryKeyClass());
                        _class.setGenerateSourceCode(persistenceClassWidget.getBaseElementSpec().getGenerateSourceCode());
                        if (pkContainerSpec.isEmbeddedIdType()) {
                            _class.setEmbeddable(true);
                            persistenceClassWidget.getEmbeddedIdAttributeWidget().getBaseElementSpec().setConnectedClass(_class);
                            persistenceClassWidget.getEmbeddedIdAttributeWidget().getBaseElementSpec().setConnectedAttribute(relationAttribute);// Ex.5.b derived identity
//                                    _class.setAttributes(null);//attribute will be added in parent Entity DefaultClass creation process
                        }
                        if (relationAttribute instanceof SingleRelationAttribute) {
                            ((SingleRelationAttribute) relationAttribute).setMapsId("");
                        }
                    } else if (pkContainerSpec.isIdClassType() && targetPKConatinerSpec.isEmbeddedIdType()) {
                        if (relationAttribute instanceof SingleRelationAttribute) {
                            ((SingleRelationAttribute) relationAttribute).setMapsId(null);
                        }
                    }

                    //set derived entity IdClass/EmbeddedId class type same as of parent entity IdClass/EmbeddedId class type
                    pkContainerSpec.setCompositePrimaryKeyClass(targetPKConatinerSpec.getCompositePrimaryKeyClass());
                } else { //not CompositePKProperty.NONE
                    List<IdAttributeWidget> idAttributeWidgets = null;
                    if (pkContainerSpec.getCompositePrimaryKeyClass() == null) {
                        pkContainerSpec.setCompositePrimaryKeyClass(persistenceClassWidget.getName() + "PK");
                    }
                    DefaultClass _class = entityMappings.addDefaultClass(managedClass.getPackage(), pkContainerSpec.getCompositePrimaryKeyClass());
                    _class.setGenerateSourceCode(persistenceClassWidget.getBaseElementSpec().getGenerateSourceCode());
                    if (pkContainerSpec.isEmbeddedIdType()) {
                        idAttributeWidgets = persistenceClassWidget.getIdAttributeWidgets();
                        _class.setEmbeddable(true);
                        persistenceClassWidget.getEmbeddedIdAttributeWidget().getBaseElementSpec().setConnectedClass(_class);
                    } else if (pkContainerSpec.isIdClassType()) {
                        idAttributeWidgets = persistenceClassWidget.getAllIdAttributeWidgets();
                    }

                    for (IdAttributeWidget idAttributeWidget : idAttributeWidgets) {
                        Id idSpec = idAttributeWidget.getBaseElementSpec();
                        DefaultAttribute attribute = new DefaultAttribute(idSpec);
                        attribute.setAttributeType(idSpec.getAttributeType());
                        attribute.setName(idSpec.getName());
                        _class.addAttribute(attribute);
                    }
                    for (SingleRelationAttributeWidget<SingleRelationAttribute> relationAttributeWidget : persistenceClassWidget.getDerivedRelationAttributeWidgets()) {
                        SingleRelationAttribute relationAttributeSpec = relationAttributeWidget.getBaseElementSpec();
                        Entity targetEntitySpec;
                        IFlowElementWidget targetElementWidget = relationAttributeWidget.getRelationFlowWidget().getTargetWidget();
                        EntityWidget targetEntityWidget = null;
                        if (targetElementWidget instanceof EntityWidget) {
                            targetEntityWidget = (EntityWidget) targetElementWidget;
                        } else if (targetElementWidget instanceof RelationAttributeWidget) {
                            RelationAttributeWidget targetRelationAttributeWidget = (RelationAttributeWidget) targetElementWidget;
                            targetEntityWidget = (EntityWidget) targetRelationAttributeWidget.getClassWidget();//target can be only Entity
                        }
                        targetEntitySpec = targetEntityWidget.getBaseElementSpec();
                        List<AttributeWidget> targetIdAttributeWidgets = targetEntityWidget.getPrimaryKeyAttributeWidgets();
                        DefaultAttribute attribute = new DefaultAttribute(relationAttributeSpec);
                        if (targetIdAttributeWidgets.size() == 1) {
                            if (targetIdAttributeWidgets.get(0) instanceof IdAttributeWidget) { //if only @Id exist
                                Id idSpec = ((IdAttributeWidget) targetIdAttributeWidgets.get(0)).getBaseElementSpec();
                                attribute.setAttributeType(idSpec.getAttributeType());
                                attribute.setName(relationAttributeSpec.getName());// matches name of @Id Relation attribute
                            } else {// if only @Id @Relation exist
                                //never execute , handled by above AUTO_CLASS condition
                                throw new IllegalStateException("Handled by Auto Class case");
                            }
                        } else {// if @Id and @Id @Relation exist
                            attribute.setAttributeType(targetEntitySpec.getCompositePrimaryKeyClass());
                            attribute.setName(relationAttributeSpec.getName());// matches name of @Id Relation attribute//PK
                            attribute.setDerived(true);
                        }
                        _class.addAttribute(attribute);
                        //Start : if dependent class is Embedded that add @MapsId to Derived PK
                        if (pkContainerSpec.isIdClassType()) {
                            if (relationAttributeSpec instanceof OneToOne) {
                                ((OneToOne) relationAttributeSpec).setMapsId(null);
                            } else if (relationAttributeSpec instanceof ManyToOne) {
                                ((ManyToOne) relationAttributeSpec).setMapsId(null);
                            }
                        } else if (pkContainerSpec.isEmbeddedIdType()) {
                            if (relationAttributeSpec instanceof OneToOne) {
                                ((OneToOne) relationAttributeSpec).setMapsId(attribute.getName());
                            } else if (relationAttributeSpec instanceof ManyToOne) {
                                ((ManyToOne) relationAttributeSpec).setMapsId(attribute.getName());
                            }
                        }
                        //End : if dependent class is Embedded that add @MapsId to Derived PK

                    }
                }

            }
            pkContainerSpec.manageCompositePrimaryKey();

        }
        return true;
//End : IDCLASS,EMBEDDEDID
    }
    
//    private static void manageConstructor(JavaClass javaClass){
//        for(Constructor constructor : javaClass.getConstructors()){
//            Map<JavaClass, List<Attribute>> transientConstructors = new HashMap<>();
//            for(Attribute attribute : constructor.getAttributes()){
//                
//            }
//            
//            
//            for(JavaClass parentJavaClass : transientConstructors.keySet()){
//                List<Attribute> parentAttributes
//            }
//            
//            
//        }
//    }

    
    public static void clearInheritanceData(ModelerFile file) {
        JPAModelerScene scene = (JPAModelerScene) file.getModelerScene();
        scene.getBaseElements().stream().filter((baseElementWidget) -> (baseElementWidget instanceof EntityWidget)).map((baseElementWidget) -> (EntityWidget)baseElementWidget).forEach((entityWidget) -> {
            Entity entity = entityWidget.getBaseElementSpec();
            InheritanceStateType type = entityWidget.getInheritanceState();
            //clear @Table and @PrimaryKeyJoinColumn
            if ((type == ROOT || type == BRANCH) && (entity.getInheritance()==null || entity.getInheritance().getStrategy() == InheritanceType.SINGLE_TABLE)) {
                entity.getSubclassList().stream().filter(subclass -> subclass instanceof Entity).forEach(subclass -> {
                    Entity subEntity = (Entity)subclass;
                    subEntity.setTable(null);
                    subEntity.setPrimaryKeyForeignKey(null);
                    subEntity.setPrimaryKeyJoinColumn(null);
                });
            }
        });
    }
}
