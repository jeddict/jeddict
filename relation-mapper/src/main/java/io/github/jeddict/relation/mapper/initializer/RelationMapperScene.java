/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.relation.mapper.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import static java.util.stream.Collectors.toList;
import javax.swing.JMenuItem;
import io.github.jeddict.relation.mapper.widget.column.ColumnWidget;
import io.github.jeddict.relation.mapper.widget.column.ForeignKeyWidget;
import io.github.jeddict.relation.mapper.widget.api.IPrimaryKeyWidget;
import io.github.jeddict.relation.mapper.widget.flow.ReferenceFlowWidget;
import io.github.jeddict.relation.mapper.widget.table.TableWidget;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.VIEW_SQL;
import io.github.jeddict.relation.mapper.spec.DBColumn;
import io.github.jeddict.relation.mapper.spec.DBForeignKey;
import io.github.jeddict.relation.mapper.spec.DBMapping;
import io.github.jeddict.relation.mapper.spec.DBTable;
import io.github.jeddict.relation.mapper.event.DBEventListener;
import io.github.jeddict.relation.mapper.theme.DBColorScheme;
import io.github.jeddict.jpa.modeler.widget.FlowNodeWidget;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.PrimaryKeyJoinColumn;
import io.github.jeddict.jpa.spec.extend.IJoinColumn;
import io.github.jeddict.jpa.spec.validator.column.JoinColumnValidator;
import io.github.jeddict.jpa.spec.validator.column.PrimaryKeyJoinColumnValidator;
import io.github.jeddict.jpa.spec.validator.override.AssociationValidator;
import io.github.jeddict.jpa.spec.validator.override.AttributeValidator;
import org.netbeans.modeler.actions.IEventListener;
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.core.scene.vmd.DefaultPModelerScene;
import org.netbeans.modeler.specification.model.document.IColorScheme;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.widget.edge.vmd.PEdgeWidget;
import org.netbeans.modeler.widget.node.vmd.internal.*;

public class RelationMapperScene extends DefaultPModelerScene<DBMapping> {

    @Override
    public void deleteBaseElement(IBaseElementWidget baseElementWidget) {
        DBMapping entityMappingsSpec = this.getBaseElementSpec();
        if (baseElementWidget instanceof IFlowElementWidget) {
            if (baseElementWidget instanceof FlowNodeWidget) { //reverse ref
                FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                IBaseElement baseElementSpec = flowNodeWidget.getBaseElementSpec();
                if (baseElementWidget instanceof TableWidget) {
                    TableWidget<DBTable> tableWidget = (TableWidget) baseElementWidget;
                    tableWidget.setLocked(true); //this method is used to prevent from reverse call( Recursion call) //  Source-flow-target any of deletion will delete each other so as deletion procced each element locked
                    for (ForeignKeyWidget foreignKeyWidget : new CopyOnWriteArrayList<>(tableWidget.getForeignKeyWidgets())) {
                        foreignKeyWidget.getReferenceFlowWidget().forEach(w -> {
                            ((ReferenceFlowWidget) w).remove();
                        });
                    }
                    for (IPrimaryKeyWidget primaryKeyWidget : new CopyOnWriteArrayList<>(tableWidget.getPrimaryKeyWidgets())) {
                        primaryKeyWidget.getReferenceFlowWidget().forEach(w -> {
                            ((ReferenceFlowWidget) w).remove();
                        });
                    }

                    tableWidget.setLocked(false);
                }
                entityMappingsSpec.removeBaseElement(baseElementSpec);
                flowNodeWidget.setFlowElementsContainer(null);
                this.removeBaseElement(flowNodeWidget);
            } else if (baseElementWidget instanceof IFlowEdgeWidget) {
                if (baseElementWidget instanceof ReferenceFlowWidget) {
                    ReferenceFlowWidget referenceFlowWidget = (ReferenceFlowWidget) baseElementWidget;
                    referenceFlowWidget.setLocked(true);
                    ForeignKeyWidget foreignKeyWidget = referenceFlowWidget.getSourceWidget();
                    foreignKeyWidget.remove();
                    foreignKeyWidget.getTableWidget().removeForeignKeyWidget(foreignKeyWidget);
                    ColumnWidget columnWidget = (ColumnWidget) referenceFlowWidget.getTargetWidget();
                    columnWidget.remove();
                    columnWidget.getTableWidget().removeColumnWidget(columnWidget);
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
        String baseElementId;
        Boolean isExist = false;
        if (baseElementWidget instanceof IFlowElementWidget) {
            this.addBaseElement(baseElementWidget);
            if (baseElementWidget instanceof IFlowNodeWidget) { //reverse ref
                ((FlowNodeWidget) baseElementWidget).setFlowElementsContainer(this);
                baseElementId = ((FlowNodeWidget) baseElementWidget).getId();
                isExist = ((FlowNodeWidget) baseElementWidget).getNodeWidgetInfo().isExist();
            } else if (baseElementWidget instanceof IFlowEdgeWidget) { //reverse ref
                ((IFlowEdgeWidget) baseElementWidget).setFlowElementsContainer(this);
                baseElementId = ((IFlowEdgeWidget) baseElementWidget).getId();
                isExist = ((PEdgeWidget) baseElementWidget).getEdgeWidgetInfo().isExist();
            } else {
                throw new InvalidElmentException("Invalid JPA FlowElement : " + baseElementWidget);
            }
        } else {
            throw new InvalidElmentException("Invalid JPA Element");
        }

        if (!isExist) {

//            IRootElement rootElement = this.getBaseElementSpec();
//            IBaseElement baseElement = null;
//            if (baseElementWidget instanceof IFlowElementWidget) {
//                if (baseElementWidget instanceof IFlowNodeWidget) {
//                    if (baseElementWidget instanceof TableWidget) {
//                        baseElement = new Table(null);
//                    } else {
//                        throw new InvalidElmentException("Invalid JPA Task Element : " + baseElement);
//                    }
//                } else if (baseElementWidget instanceof IFlowEdgeWidget) {
//                    // skip don't need to create spec RelationFlowWidget, GeneralizationFlowWidget,EmbeddableFlowWidget
//                } else {
//                    throw new InvalidElmentException("Invalid JPA Element");
//                }
//            } else {
//                throw new InvalidElmentException("Invalid JPA Element");
//            }
//            if (baseElement != null) {
//                baseElementWidget.setBaseElementSpec(baseElement);
//                baseElement.setId(baseElementId);
//                rootElement.addBaseElement(baseElement);
//                ElementConfigFactory elementConfigFactory = this.getModelerFile().getVendorSpecification().getElementConfigFactory();
//                elementConfigFactory.initializeObjectValue(baseElement);
//            }
        } else if (baseElementWidget instanceof IFlowElementWidget) {
            if (baseElementWidget instanceof FlowNodeWidget) {
                FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                flowNodeWidget.setBaseElementSpec(flowNodeWidget.getNodeWidgetInfo().getBaseElementSpec());
            } else {
                throw new InvalidElmentException("Invalid JPA Element");
            }
        } else {
            throw new InvalidElmentException("Invalid JPA Element");
        }

    }

    @Override
    public IColorScheme getColorScheme(String defaultTheme) {
        EntityMappings entityMappings = (EntityMappings) this.getModelerFile().getParentFile().getDefinitionElement();
        String theme = entityMappings.getDbTheme() == null ? defaultTheme : entityMappings.getDbTheme();
        if (PFactory.getDarkScheme().getSimpleName().equals(theme)) {
            return PFactory.getColorScheme(PFactory.getDarkScheme());
        } else if (PFactory.getLightScheme().getSimpleName().equals(theme)) {
            return PFactory.getColorScheme(PFactory.getLightScheme());
        } else {
            return PFactory.getColorScheme(DBColorScheme.class);
        }
    }

    @Override
    public void setColorScheme(Class<? extends IColorScheme> scheme) {
        EntityMappings entityMappings = (EntityMappings) this.getModelerFile().getParentFile().getDefinitionElement();
        entityMappings.setDbTheme(scheme.getSimpleName());
    }

    @Override
    public Map<String, Class<? extends IColorScheme>> getColorSchemes() {
        Map<String, Class<? extends IColorScheme>> colorSchemes = new HashMap<>();
        colorSchemes.put("Default", DBColorScheme.class);
        colorSchemes.put("Dark", PDarkColorScheme.class);
        colorSchemes.put("Light", PLightColorScheme.class);
        return colorSchemes;
    }


    
    @Override
    public void destroy() {
        try {
            if (this.getModelerFile().isLoaded() && this.getBaseElementSpec() != null) {
                this.getBaseElementSpec().getTables().stream().map(t -> t.getEntity()).forEach(e -> {
                    AttributeValidator.filter(e);
                    AssociationValidator.filter(e);
                });
                this.getBaseElementSpec().getTables().stream().flatMap(t -> t.getColumns().stream())
                        .filter(c -> c instanceof DBForeignKey).collect(toList())
                        .forEach((DBColumn column) -> {
                            List<IJoinColumn> joinColumns;
                            IJoinColumn joinColumn;
                            joinColumn = ((DBForeignKey) column).getJoinColumn();
                            joinColumns = ((DBForeignKey) column).getJoinColumns();
                            if (joinColumn != null) {
                                if (joinColumn instanceof JoinColumn && JoinColumnValidator.isEmpty((JoinColumn) joinColumn)) {
                                    joinColumns.remove(joinColumn);
                                } else if (joinColumn instanceof PrimaryKeyJoinColumn && PrimaryKeyJoinColumnValidator.isEmpty((PrimaryKeyJoinColumn) joinColumn)) {
                                    joinColumns.remove(joinColumn);
                                }
                            }
                        });
            }
        } catch (Exception ex) {
            this.getModelerFile().handleException(ex);
        }
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = new ArrayList<>();
        JMenuItem openSQLEditor = new JMenuItem("View SQL", VIEW_SQL);
        openSQLEditor.addActionListener(e -> SQLEditorUtil.openEditor(RelationMapperScene.this.getModelerFile(), RelationMapperScene.this.getBaseElementSpec().getSQL()));

        menuList.add(openSQLEditor);
        menuList.add(getThemeMenu());
        menuList.add(getContainerMenu());
        menuList.add(getPropertyMenu());
        return menuList;
    }

    @Override
    protected IEventListener getEventListener() {
        return new DBEventListener();
    }
}
