/**
 * Copyright [2013] Gaurav Gupta
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
package org.netbeans.jpa.modeler.core.widget;

import java.util.List;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import org.netbeans.jpa.modeler.properties.inheritence.InheritencePanel;
import org.netbeans.jpa.modeler.rules.entity.EntityValidator;
import org.netbeans.jpa.modeler.spec.Attributes;
import org.netbeans.jpa.modeler.spec.DiscriminatorColumn;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.Inheritance;
import org.netbeans.jpa.modeler.spec.InheritanceType;
import org.netbeans.jpa.modeler.spec.Table;
import org.netbeans.jpa.modeler.spec.extend.InheritenceHandler;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.config.element.ElementConfigFactory;
import org.netbeans.modeler.properties.embedded.EmbeddedDataListener;
import org.netbeans.modeler.properties.embedded.EmbeddedPropertySupport;
import org.netbeans.modeler.properties.embedded.GenericEmbedded;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyVisibilityHandler;

public class EntityWidget extends PrimaryKeyContainerWidget {

    public EntityWidget(IModelerScene scene, NodeWidgetInfo nodeWidgetInfo) {
        super(scene, nodeWidgetInfo);
        this.addPropertyVisibilityHandler("inheritence", new PropertyVisibilityHandler<String>() {
            @Override
            public boolean isVisible() {
                GeneralizationFlowWidget outgoingGeneralizationFlowWidget = EntityWidget.this.getOutgoingGeneralizationFlowWidget();
                List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = EntityWidget.this.getIncomingGeneralizationFlowWidgets();
                if (outgoingGeneralizationFlowWidget != null && !(outgoingGeneralizationFlowWidget.getSuperclassWidget() instanceof EntityWidget)) {
                    outgoingGeneralizationFlowWidget = null;
                }
                if (outgoingGeneralizationFlowWidget != null || !incomingGeneralizationFlowWidgets.isEmpty()) {
                    return true;
                }
                return false;
            }

        });

    }

    @Override
    public void init() {
        Entity entity = (Entity) this.getBaseElementSpec();
        if (entity.getAttributes() == null) {
            entity.setAttributes(new Attributes());
            addNewIdAttribute("id");
            sortAttributes();
        }

        if (entity.getClazz() == null || entity.getClazz().isEmpty()) {
            entity.setClazz(((JPAModelerScene) this.getModelerScene()).getNextClassName("Entity_"));
        }
        setName(entity.getClazz());
        setLabel(entity.getClazz());

        scanPrimaryKeyError();
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        Entity entity = (Entity) this.getBaseElementSpec();
        if (entity.getTable() == null) {
            entity.setTable(new Table());
        }
        ElementConfigFactory elementConfigFactory = this.getModelerScene().getModelerFile().getVendorSpecification().getElementConfigFactory();
        elementConfigFactory.createPropertySet(set, entity.getTable(), getPropertyChangeListeners());

        if (entity instanceof InheritenceHandler) {
            set.put("BASIC_PROP", getInheritenceProperty());
        }
        set.put("BASIC_PROP", JPAModelerUtil.getNamedQueryProperty("NamedQueries", "Named Queries", "", this.getModelerScene(), entity.getNamedQuery()));

    }

    private EmbeddedPropertySupport getInheritenceProperty() {

        GenericEmbedded entity = new GenericEmbedded("inheritence", "Inheritence", "");
        try {
            entity.setEntityEditor(new InheritencePanel(this.getModelerScene().getModelerFile(), EntityWidget.this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        entity.setDataListener(new EmbeddedDataListener<InheritenceHandler>() {
            private InheritenceHandler classSpec;
            private String displayName = null;

            @Override
            public void init() {
                classSpec = (InheritenceHandler) EntityWidget.this.getBaseElementSpec();
            }

            @Override
            public InheritenceHandler getData() {
                if (classSpec.getInheritance() == null) {
                    classSpec.setInheritance(new Inheritance());
                }
                if (classSpec.getDiscriminatorColumn() == null) {
                    classSpec.setDiscriminatorColumn(new DiscriminatorColumn());
                }
                return classSpec;
            }

            @Override
            public void setData(InheritenceHandler classSpec) {
                EntityWidget.this.setBaseElementSpec((IBaseElement) classSpec);
            }

            @Override
            public String getDisplay() {

                GeneralizationFlowWidget outgoingGeneralizationFlowWidget = EntityWidget.this.getOutgoingGeneralizationFlowWidget();
                List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = EntityWidget.this.getIncomingGeneralizationFlowWidgets();

                if (outgoingGeneralizationFlowWidget != null && !(outgoingGeneralizationFlowWidget.getSuperclassWidget() instanceof EntityWidget)) {
                    outgoingGeneralizationFlowWidget = null;
                }
//                String type;
//                if (outgoingGeneralizationFlowWidget == null && incomingGeneralizationFlowWidgets.isEmpty()) {
//                    type = "SINGLETON";
//                } else
                if (outgoingGeneralizationFlowWidget != null && incomingGeneralizationFlowWidgets.isEmpty()) {
//                    type = "LEAF";
                    EntityWidget superEntityWidget = (EntityWidget) EntityWidget.this.getOutgoingGeneralizationFlowWidget().getSuperclassWidget();
                    InheritenceHandler superClassSpec = (InheritenceHandler) superEntityWidget.getBaseElementSpec();
                    if (superClassSpec.getInheritance() != null && superClassSpec.getInheritance().getStrategy() != null) {
                        return superClassSpec.getInheritance().getStrategy().toString();
                    } else {
                        return InheritanceType.SINGLE_TABLE.toString();
                    }
                } else if (outgoingGeneralizationFlowWidget == null && !incomingGeneralizationFlowWidgets.isEmpty()) {
//                    type = "ROOT";
                    if (classSpec.getInheritance() != null && classSpec.getInheritance().getStrategy() != null) {
                        return classSpec.getInheritance().getStrategy().toString();
                    } else {
                        return InheritanceType.SINGLE_TABLE.toString();
                    }
                } else if (outgoingGeneralizationFlowWidget != null && !incomingGeneralizationFlowWidgets.isEmpty()) {
//                    type = "BRANCH";
                    if (classSpec.getInheritance() != null && classSpec.getInheritance().getStrategy() != null) {
                        return classSpec.getInheritance().getStrategy().toString();
                    } else {
                        return InheritanceType.SINGLE_TABLE.toString();
                    }
                } else {
                    return "";
                }
            }

        });
        return new EmbeddedPropertySupport(this.getModelerScene().getModelerFile(), entity);
    }

    @Override
    public String getInheritenceState() {
        GeneralizationFlowWidget outgoingGeneralizationFlowWidget = EntityWidget.this.getOutgoingGeneralizationFlowWidget();
        List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = EntityWidget.this.getIncomingGeneralizationFlowWidgets();
        if (outgoingGeneralizationFlowWidget != null && outgoingGeneralizationFlowWidget.getSuperclassWidget() != null && !(outgoingGeneralizationFlowWidget.getSuperclassWidget() instanceof EntityWidget)) {
            outgoingGeneralizationFlowWidget = null;
        }
        String type;
        if (outgoingGeneralizationFlowWidget == null && incomingGeneralizationFlowWidgets.isEmpty()) {
            type = "SINGLETON";
        } else if (outgoingGeneralizationFlowWidget != null && incomingGeneralizationFlowWidgets.isEmpty()) {
            type = "LEAF";
        } else if (outgoingGeneralizationFlowWidget == null && !incomingGeneralizationFlowWidgets.isEmpty()) {
            type = "ROOT";
        } else if (outgoingGeneralizationFlowWidget != null && !incomingGeneralizationFlowWidgets.isEmpty()) {
            type = "BRANCH";
        } else {
            type = "";
        }
        return type;
    }

//    @Override
    public void scanPrimaryKeyError() {
        String inheritenceState = this.getInheritenceState();
        if ("SINGLETON".equals(inheritenceState) || "ROOT".equals(inheritenceState)) {
            // Issue Fix #6041 Start
            if (this.getAllIdAttributeWidgets().isEmpty()) {
                throwError(EntityValidator.NO_PRIMARYKEY_EXIST);
            } else {
                clearError(EntityValidator.NO_PRIMARYKEY_EXIST);
            }
            // Issue Fix #6041 End
        } else {
            clearError(EntityValidator.NO_PRIMARYKEY_EXIST);
        }
    }

}
