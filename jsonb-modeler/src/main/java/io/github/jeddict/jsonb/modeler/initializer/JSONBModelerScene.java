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
package io.github.jeddict.jsonb.modeler.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import static javax.json.bind.config.PropertyNamingStrategy.IDENTITY;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.eclipse.yasson.internal.model.customization.naming.DefaultNamingStrategies;
import io.github.jeddict.jsonb.modeler.widget.BranchNodeWidget;
import io.github.jeddict.jsonb.modeler.widget.DocumentWidget;
import io.github.jeddict.jsonb.modeler.widget.GeneralizationFlowWidget;
import io.github.jeddict.jsonb.modeler.widget.LeafNodeWidget;
import io.github.jeddict.jsonb.modeler.widget.ReferenceFlowWidget;
import static io.github.jeddict.jsonb.modeler.properties.PropertiesHandler.getJsonbVisibility;
import io.github.jeddict.jsonb.modeler.spec.JSONBMapping;
import io.github.jeddict.jsonb.modeler.event.JSONBEventListener;
import io.github.jeddict.jpa.modeler.widget.FlowNodeWidget;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jsonb.spec.PropertyNamingStrategy;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import io.github.jeddict.jpa.modeler.initializer.JSONBUtil;
import org.netbeans.modeler.actions.IEventListener;
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.core.scene.vmd.DefaultPModelerScene;
import org.netbeans.modeler.properties.spec.ComboBoxValue;
import org.netbeans.modeler.specification.model.document.IColorScheme;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.edge.vmd.PEdgeWidget;
import org.netbeans.modeler.widget.node.vmd.internal.*;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class JSONBModelerScene extends DefaultPModelerScene<JSONBMapping> {

    @Override
    public void deleteBaseElement(IBaseElementWidget baseElementWidget) {
        JSONBMapping entityMappingsSpec = this.getBaseElementSpec();
        if (baseElementWidget instanceof IFlowElementWidget) {
            if (baseElementWidget instanceof FlowNodeWidget) { //reverse ref
                FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                IBaseElement baseElementSpec = flowNodeWidget.getBaseElementSpec();
                if (baseElementWidget instanceof DocumentWidget) {
                    DocumentWidget documentWidget = (DocumentWidget) baseElementWidget;
                    documentWidget.setLocked(true);
                    if (documentWidget.getOutgoingGeneralizationFlowWidget() != null) {
                        documentWidget.getOutgoingGeneralizationFlowWidget().remove();
                    }
                    for (GeneralizationFlowWidget generalizationFlowWidget : new CopyOnWriteArrayList<>(documentWidget.getIncomingGeneralizationFlowWidgets())) {
                        generalizationFlowWidget.remove();
                    }

                    for (BranchNodeWidget branchNodeWidget : new CopyOnWriteArrayList<>(documentWidget.getBranchNodeWidgets())) {
                        branchNodeWidget.getReferenceFlowWidget().forEach(ReferenceFlowWidget::remove);
                    }
                    for (LeafNodeWidget leafNodeWidget : new CopyOnWriteArrayList<>(documentWidget.getLeafNodeWidgets())) {
                        leafNodeWidget.getReferenceFlowWidget().forEach(ReferenceFlowWidget::remove);
                    }

                    documentWidget.setLocked(false);
                }
                entityMappingsSpec.removeBaseElement(baseElementSpec);
                flowNodeWidget.setFlowElementsContainer(null);
                this.removeBaseElement(flowNodeWidget);
            } else if (baseElementWidget instanceof IFlowEdgeWidget) {
                if (baseElementWidget instanceof ReferenceFlowWidget) {
                    ReferenceFlowWidget referenceFlowWidget = (ReferenceFlowWidget) baseElementWidget;
                    referenceFlowWidget.setLocked(true);
                    BranchNodeWidget sourceBranchNodeWidget = referenceFlowWidget.getSourceWidget();
                    sourceBranchNodeWidget.remove();
                    referenceFlowWidget.setLocked(false);
                    referenceFlowWidget.setFlowElementsContainer(null);
                    this.removeBaseElement(referenceFlowWidget);
                } else {
                    throw new InvalidElmentException("Invalid JPA Element");
                }
            } else {
                throw new InvalidElmentException("Invalid JPA Element");
            }
        }
    }

    @Override
    public void createBaseElement(IBaseElementWidget baseElementWidget) {
        Boolean isExist = false;
        if (baseElementWidget instanceof IFlowElementWidget) {
            this.addBaseElement(baseElementWidget);
            if (baseElementWidget instanceof IFlowNodeWidget) { //reverse ref
                ((FlowNodeWidget) baseElementWidget).setFlowElementsContainer(this);
                isExist = ((FlowNodeWidget) baseElementWidget).getNodeWidgetInfo().isExist();
            } else if (baseElementWidget instanceof IFlowEdgeWidget) { //reverse ref
                ((IFlowEdgeWidget) baseElementWidget).setFlowElementsContainer(this);
                isExist = ((PEdgeWidget) baseElementWidget).getEdgeWidgetInfo().isExist();
            } else {
                throw new InvalidElmentException("Invalid JSONB FlowElement : " + baseElementWidget);
            }
        } else {
            throw new InvalidElmentException("Invalid JSONB Element");
        }

        if (!isExist) {
            //skip
        } else if (baseElementWidget instanceof IFlowElementWidget) {
            if (baseElementWidget instanceof FlowNodeWidget) {
                FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                flowNodeWidget.setBaseElementSpec(flowNodeWidget.getNodeWidgetInfo().getBaseElementSpec());
            } else {
                throw new InvalidElmentException("Invalid JSONB Element");
            }
        } else {
            throw new InvalidElmentException("Invalid JSONB Element");
        }

    }

    @Override
    public IColorScheme getColorScheme(String defaultTheme) {
        EntityMappings entityMappings = (EntityMappings) this.getModelerFile().getParentFile().getDefinitionElement();
        String theme = entityMappings.getJBTheme() == null ? defaultTheme : entityMappings.getJBTheme();
        if (PFactory.getDarkScheme().getSimpleName().equals(theme)) {
            return PFactory.getColorScheme(PFactory.getDarkScheme());
        } else if (PFactory.getLightScheme().getSimpleName().equals(theme)) {
            return PFactory.getColorScheme(PFactory.getLightScheme());
        } else {
            return PFactory.getColorScheme(PFactory.getLightScheme());
        }
    }

    @Override
    public void setColorScheme(Class<? extends IColorScheme> scheme) {
        EntityMappings entityMappings = (EntityMappings) this.getModelerFile().getParentFile().getDefinitionElement();
        entityMappings.setJBTheme(scheme.getSimpleName());
    }

    @Override
    public Map<String, Class<? extends IColorScheme>> getColorSchemes() {
        Map<String, Class<? extends IColorScheme>> colorSchemes = new HashMap<>();
        colorSchemes.put("Default", PLightColorScheme.class);
        colorSchemes.put("Dark", PDarkColorScheme.class);
        colorSchemes.put("Light", PLightColorScheme.class);
        return colorSchemes;
    }

    @Override
    public void destroy() {
    }

    @NbBundle.Messages({
        "RELOAD_DIAGRAM_TITLE=Reload Diagram",
        "RELOAD_DIAGRAM_CONTENT=Are you sure you want to reload Diagram ?"
    })
    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        EntityMappings entityMappings = this.getBaseElementSpec().getEntityMappings();
        JPAModelerScene parentScene = (JPAModelerScene) this.getModelerScene().getModelerFile().getParentFile().getModelerScene();
        set.put("JSONB_PROP", getJsonbVisibility(entityMappings, this, parentScene));

        this.addPropertyChangeListener("jsonbPropertyNamingStrategy", (PropertyChangeListener<ComboBoxValue>) (oldValue, newValue) -> {
            int option = JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                    Bundle.RELOAD_DIAGRAM_CONTENT(), Bundle.RELOAD_DIAGRAM_TITLE(), JOptionPane.YES_NO_OPTION);
            if (option == javax.swing.JOptionPane.OK_OPTION) {
                this.getModelerScene().getModelerPanelTopComponent().changePersistenceState(false);
                JSONBUtil.openJSONBViewer(this.getModelerFile().getParentFile());
            }
        });

    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = new ArrayList<>();
        menuList.add(getThemeMenu());
        menuList.add(getContainerMenu());
        menuList.add(getPropertyMenu());
        return menuList;
    }

    @Override
    protected IEventListener getEventListener() {
        return new JSONBEventListener();
    }

    public String transferPropertyName(String name) {
        PropertyNamingStrategy namingStrategy = getBaseElementSpec().getEntityMappings().getJsonbPropertyNamingStrategy();
        javax.json.bind.config.PropertyNamingStrategy namingStrategyInstance = DefaultNamingStrategies.getStrategy(namingStrategy != null ? namingStrategy.name() : IDENTITY);
        return namingStrategyInstance.translateName(name);
    }
}
