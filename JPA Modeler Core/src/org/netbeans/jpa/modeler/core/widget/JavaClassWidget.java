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
package org.netbeans.jpa.modeler.core.widget;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import org.netbeans.jpa.modeler.rules.entity.EntityValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.IColorScheme;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.netbeans.modules.j2ee.persistence.dd.JavaPersistenceQLKeywords;
import org.openide.util.NbBundle;

public abstract class JavaClassWidget<E extends JavaClass> extends FlowNodeWidget<E,JPAModelerScene> {

    private GeneralizationFlowWidget outgoingGeneralizationFlowWidget;
    private final List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = new ArrayList<>();

    public JavaClassWidget(JPAModelerScene scene, NodeWidgetInfo node) {
        super(scene, node);
        this.addPropertyChangeListener("class", (PropertyChangeListener<String>) (String value) -> {
            if (value == null || value.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, NbBundle.getMessage(EntityValidator.class, EntityValidator.EMPTY_CLASS_NAME));
                setName(JavaClassWidget.this.getLabel());//rollback
            } else {
                setName(value);
                setLabel(value);
            }
        });

        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) (String tableName) -> {
            if (tableName != null && !tableName.trim().isEmpty()) {
                if (SQLKeywords.isSQL99ReservedKeyword(tableName)) {
                    this.getErrorHandler().throwError(EntityValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                } else {
                    this.getErrorHandler().clearError(EntityValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                }
            } else {
                this.getErrorHandler().clearError(EntityValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
            }
        });

        this.setImage(this.getNodeWidgetInfo().getModelerDocument().getImage());
    }

    
     
    public abstract void deleteAttribute(AttributeWidget attributeWidget);

    public abstract void sortAttributes();

    @Override
    public void setName(String name) {

        if (name != null && !name.trim().isEmpty()) {
            this.name = name.replaceAll("\\s+", "");
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                getBaseElementSpec().setClazz(this.name);
            }
            if (JavaPersistenceQLKeywords.isKeyword(JavaClassWidget.this.getName())) {
                getErrorHandler().throwError(EntityValidator.CLASS_NAME_WITH_JPQL_KEYWORD);
            } else {
                getErrorHandler().clearError(EntityValidator.CLASS_NAME_WITH_JPQL_KEYWORD);
            }
            EntityMappings entityMapping = JavaClassWidget.this.getModelerScene().getBaseElementSpec();
            if (entityMapping.findAllEntity(JavaClassWidget.this.getName()).size() > 1) {
                getErrorHandler().throwError(EntityValidator.NON_UNIQUE_ENTITY_NAME);
            } else {
                getErrorHandler().clearError(EntityValidator.NON_UNIQUE_ENTITY_NAME);
            }
        }

    }

    @Override
    public void setLabel(String label) {
        if (label != null && !label.trim().isEmpty()) {
            this.setNodeName(label.replaceAll("\\s+", ""));
        }
    }

    public JavaClassWidget getSuperclassWidget() {
        if (outgoingGeneralizationFlowWidget != null) {
            return outgoingGeneralizationFlowWidget.getSuperclassWidget();
        }
        return null;
    }

    public List<JavaClassWidget> getAllSuperclassWidget() {
        List<JavaClassWidget> superclassWidgetList = new LinkedList<>();
        boolean exist = false;
        GeneralizationFlowWidget generalizationFlowWidget_TMP = this.outgoingGeneralizationFlowWidget;
        if (generalizationFlowWidget_TMP != null) {
            exist = true;
        }
        while (exist) {
            JavaClassWidget superclassWidget_Nest = generalizationFlowWidget_TMP.getSuperclassWidget();
            superclassWidgetList.add(superclassWidget_Nest);
            generalizationFlowWidget_TMP = superclassWidget_Nest.getOutgoingGeneralizationFlowWidget();
            if (generalizationFlowWidget_TMP == null) {
                exist = false;
            }
        }
        return superclassWidgetList;
    }

    public List<JavaClassWidget> getSubclassWidgets() {
        List<JavaClassWidget> subclassWidgetList = new LinkedList<>();
        for (GeneralizationFlowWidget generalizationFlowWidget_TMP : this.incomingGeneralizationFlowWidgets) {
            JavaClassWidget subclassWidget_Nest = generalizationFlowWidget_TMP.getSubclassWidget();
            subclassWidgetList.add(subclassWidget_Nest);
        }
        return subclassWidgetList;
    }

    public List<JavaClassWidget> getAllSubclassWidgets() {
        List<JavaClassWidget> subclassWidgetList = new LinkedList<>();
        for (GeneralizationFlowWidget generalizationFlowWidget_TMP : this.incomingGeneralizationFlowWidgets) {
            JavaClassWidget subclassWidget_Nest = generalizationFlowWidget_TMP.getSubclassWidget();
            subclassWidgetList.add(subclassWidget_Nest);
            subclassWidgetList.addAll(subclassWidget_Nest.getAllSubclassWidgets());
        }
        return subclassWidgetList;
    }

//    public List<JavaClassWidget> getAllSubclassWidget() {
//        List<JavaClassWidget> subclassWidgetList = new LinkedList<JavaClassWidget>();
//        boolean exist = true;
//        for (GeneralizationFlowWidget generalizationFlowWidget_TMP : this.incomingGeneralizationFlowWidgets) {
////        ;List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = new ArrayList<GeneralizationFlowWidget>()
////        if (generalizationFlowWidget_TMP != null) {
////            exist = true;
////        }
//            while (exist) {
//                JavaClassWidget subclassWidget_Nest = generalizationFlowWidget_TMP.getSubclassWidget();
//                subclassWidgetList.add(subclassWidget_Nest);
//                generalizationFlowWidget_TMP = subclassWidget_Nest.getOutgoingGeneralizationFlowWidget();
//                if (generalizationFlowWidget_TMP == null) {
//                    exist = false;
//                }
//            }
//        }
//        return subclassWidgetList;
//    }
    /**
     * @return the outgoingGeneralizationFlowWidget
     */
    public GeneralizationFlowWidget getOutgoingGeneralizationFlowWidget() {
        return outgoingGeneralizationFlowWidget;
    }

    /**
     * @param outgoingGeneralizationFlowWidget the
     * outgoingGeneralizationFlowWidget to set
     */
    public void setOutgoingGeneralizationFlowWidget(GeneralizationFlowWidget outgoingGeneralizationFlowWidget) {
        this.outgoingGeneralizationFlowWidget = outgoingGeneralizationFlowWidget;
    }

    /**
     * @return the incomingGeneralizationFlowWidgets
     */
    public List<GeneralizationFlowWidget> getIncomingGeneralizationFlowWidgets() {
        return incomingGeneralizationFlowWidgets;
    }

    public void addIncomingGeneralizationFlowWidget(GeneralizationFlowWidget generalizationFlowWidget) {
        incomingGeneralizationFlowWidgets.add(generalizationFlowWidget);
    }

    public void removeIncomingGeneralizationFlowWidget(GeneralizationFlowWidget generalizationFlowWidget) {
        incomingGeneralizationFlowWidgets.remove(generalizationFlowWidget);
    }

    public abstract InheritenceStateType getInheritenceState();

////    private static final Border WIDGET_BORDER = new ShadowBorder(new Color(255, 25, 25) ,2, new Color(255, 25, 25), new Color(255, 255, 255), new Color(255, 25, 25), new Color(255, 255, 255), new Color(255, 25, 25));
    public void showInheritencePath() {
        IColorScheme colorScheme =  this.getModelerScene().getColorScheme();
        colorScheme.highlightUI(this);
        this.setHighlightStatus(true);
//        this.setBorder(colorScheme.);
        if (this.getOutgoingGeneralizationFlowWidget() != null) {
            this.getOutgoingGeneralizationFlowWidget().setHighlightStatus(true);
            colorScheme.highlightUI(this.getOutgoingGeneralizationFlowWidget());
//            this.getOutgoingGeneralizationFlowWidget().setForeground(Color.red);
            this.getOutgoingGeneralizationFlowWidget().getSuperclassWidget().showInheritencePath();
        }
    }

    public void hideInheritencePath() {
        IColorScheme colorScheme =  this.getModelerScene().getColorScheme();
        this.setHighlightStatus(false);
        colorScheme.updateUI(this, this.getState(), this.getState());
        if (this.getOutgoingGeneralizationFlowWidget() != null) {
            this.getOutgoingGeneralizationFlowWidget().setHighlightStatus(false);
            colorScheme.updateUI(this.getOutgoingGeneralizationFlowWidget(), this.getOutgoingGeneralizationFlowWidget().getState(), this.getOutgoingGeneralizationFlowWidget().getState());
            this.getOutgoingGeneralizationFlowWidget().getSuperclassWidget().hideInheritencePath();
        }
    }

    public void showCompositionPath() {
        IColorScheme colorScheme =  this.getModelerScene().getColorScheme();
        colorScheme.highlightUI(this);
        this.setHighlightStatus(true);
    }

    public void hideCompositionPath() {
        IColorScheme colorScheme =  this.getModelerScene().getColorScheme();
        this.setHighlightStatus(false);
        colorScheme.updateUI(this, this.getState(), this.getState());
    }

    


}
