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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import org.netbeans.jpa.modeler.rules.entity.EntityValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.modeler.core.scene.vmd.PModelerScene;
import org.netbeans.modeler.specification.model.document.IColorScheme;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.netbeans.modules.j2ee.persistence.dd.JavaPersistenceQLKeywords;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

public abstract class JavaClassWidget extends FlowNodeWidget {

    private GeneralizationFlowWidget outgoingGeneralizationFlowWidget;
    private final List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = new ArrayList<GeneralizationFlowWidget>();

    public JavaClassWidget(IModelerScene scene, NodeWidgetInfo node) {
        super(scene, node);
        this.addPropertyChangeListener("class", new PropertyChangeListener<String>() {
            @Override
            public void changePerformed(String value) {
                if (value == null || value.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, NbBundle.getMessage(EntityValidator.class, EntityValidator.EMPTY_CLASS_NAME));
                    setName(JavaClassWidget.this.getLabel());//rollback
                } else {
                    setName(value);
                    setLabel(value);
                }

            }
        });

        this.addPropertyChangeListener("table_name", new PropertyChangeListener<String>() {
            @Override
            public void changePerformed(String tableName) {
                if (tableName != null && !tableName.trim().isEmpty()) {
                    if (SQLKeywords.isSQL99ReservedKeyword(tableName)) {
                        throwError(EntityValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                    } else {
                        clearError(EntityValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                    }
                } else {
                    clearError(EntityValidator.CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                }
            }
        });

        this.icon = this.getNodeWidgetInfo().getModelerDocument().getImage();
    }

    private Image icon;
    private static Image errorIcon = ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/error_small_icon.gif");

    private final java.util.Map<String, String> errorList = new HashMap<String, String>();

    public void throwError(String key) {
        errorList.put(key, NbBundle.getMessage(EntityValidator.class, key));
        printError();
    }

    public void clearError(String key) {
        errorList.remove(key);
        printError();
    }

    private void printError() {
        StringBuilder errorMessage = new StringBuilder();
        for (String errorKey : errorList.keySet()) {
            errorMessage.append(errorList.get(errorKey));
        }
        if (errorMessage.length() != 0) {
            this.setToolTipText(errorMessage.toString());
            this.setNodeImage(getErrorIcon());
        } else {
            this.setToolTipText(null);
            this.setNodeImage(icon);
        }
    }

    private Image getErrorIcon() {
        int iconWidth = (int) ((BufferedImage) icon).getWidth() + 3;
        int iconHeight = (int) ((BufferedImage) icon).getHeight() + 3;
        int errorIconWidth = (int) ((BufferedImage) errorIcon).getWidth();
        int errorIconHeight = (int) ((BufferedImage) errorIcon).getHeight();

        BufferedImage combined = new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();
        g.drawImage(icon, 0, 0, null);
        g.drawImage(errorIcon, iconWidth - errorIconWidth, iconHeight - errorIconHeight, null);
        return combined;
    }

    public abstract void deleteAttribute(AttributeWidget attributeWidget);

    public abstract void sortAttributes();

    @Override
    public void setName(String name) {

        if (name != null && !name.trim().isEmpty()) {
            this.name = name.replaceAll("\\s+", "");
            ((JavaClass) getBaseElementSpec()).setClazz(this.name);
        }
        if (JavaPersistenceQLKeywords.isKeyword(JavaClassWidget.this.getName())) {
            throwError(EntityValidator.CLASS_NAME_WITH_JPQL_KEYWORD);
        } else {
            clearError(EntityValidator.CLASS_NAME_WITH_JPQL_KEYWORD);
        }
        EntityMappings entityMapping = (EntityMappings) JavaClassWidget.this.getModelerScene().getBaseElementSpec();
        if (entityMapping.findAllEntity(JavaClassWidget.this.getName()).size() > 1) {
            throwError(EntityValidator.NON_UNIQUE_ENTITY_NAME);
        } else {
            clearError(EntityValidator.NON_UNIQUE_ENTITY_NAME);
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
        List<JavaClassWidget> superclassWidgetList = new LinkedList<JavaClassWidget>();
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
        List<JavaClassWidget> subclassWidgetList = new LinkedList<JavaClassWidget>();
        for (GeneralizationFlowWidget generalizationFlowWidget_TMP : this.incomingGeneralizationFlowWidgets) {
            JavaClassWidget subclassWidget_Nest = generalizationFlowWidget_TMP.getSubclassWidget();
            subclassWidgetList.add(subclassWidget_Nest);
        }
        return subclassWidgetList;
    }

    public List<JavaClassWidget> getAllSubclassWidgets() {
        List<JavaClassWidget> subclassWidgetList = new LinkedList<JavaClassWidget>();
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

    public abstract String getInheritenceState();

////    private static final Border WIDGET_BORDER = new ShadowBorder(new Color(255, 25, 25) ,2, new Color(255, 25, 25), new Color(255, 255, 255), new Color(255, 25, 25), new Color(255, 255, 255), new Color(255, 25, 25));
    public void showInheritencePath() {
//        Border WIDGET_BORDER = new ShadowBorder(new Color(255, 25, 25), 1, new Color(255, 225, 225), new Color(255, 255, 255), new Color(255, 225, 225), new Color(255, 255, 255), new Color(255, 225, 225));
        IColorScheme colorScheme = ((PModelerScene) this.getModelerScene()).getColorScheme();
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
        IColorScheme colorScheme = ((PModelerScene) this.getModelerScene()).getColorScheme();
        this.setHighlightStatus(false);
        colorScheme.updateUI(this, this.getState(), this.getState());
        if (this.getOutgoingGeneralizationFlowWidget() != null) {
            this.getOutgoingGeneralizationFlowWidget().setHighlightStatus(false);
            colorScheme.updateUI(this.getOutgoingGeneralizationFlowWidget(), this.getOutgoingGeneralizationFlowWidget().getState(), this.getOutgoingGeneralizationFlowWidget().getState());
            this.getOutgoingGeneralizationFlowWidget().getSuperclassWidget().hideInheritencePath();
        }
    }

    public void showCompositionPath() {
        IColorScheme colorScheme = ((PModelerScene) this.getModelerScene()).getColorScheme();
        colorScheme.highlightUI(this);
        this.setHighlightStatus(true);
//        if (this.getEmbeddableFlowWidget() != null) {
//            this.getEmbeddableFlowWidget().setHighlightStatus(true);
//            colorScheme.highlightUI(this.getEmbeddableFlowWidget());
//            this.getEmbeddableFlowWidget().getTargetEmbeddableWidget().showCompositionPath();
//        }
    }

    public void hideCompositionPath() {
        IColorScheme colorScheme = ((PModelerScene) this.getModelerScene()).getColorScheme();
        this.setHighlightStatus(false);
        colorScheme.updateUI(this, this.getState(), this.getState());
//        if (this.getEmbeddableFlowWidget() != null) {
//            this.getEmbeddableFlowWidget().setHighlightStatus(false);
//            colorScheme.updateUI(this.getEmbeddableFlowWidget(), this.getEmbeddableFlowWidget().getState(), this.getEmbeddableFlowWidget().getState());
//            this.getEmbeddableFlowWidget().getTargetEmbeddableWidget().hideCompositionPath();
//        }
    }
//    private LayerWidget preLayerWidget;
//
//    public void showInheritencePath() {
//        JPAModelerScene modelerScene = (JPAModelerScene) this.getModelerScene();
//        preLayerWidget = (LayerWidget) this.getParentWidget();
//        this.removeFromParent();
//        modelerScene.getWidgetHighlightLayer().addChild(this);
//        if (this.getOutgoingGeneralizationFlowWidget() != null) {
//            this.getOutgoingGeneralizationFlowWidget().setPreLayerWidget((LayerWidget) this.getOutgoingGeneralizationFlowWidget().getParentWidget());
//            this.getOutgoingGeneralizationFlowWidget().removeFromParent();
//            modelerScene.getBoundaryWidgetLayer().addChild(this.getOutgoingGeneralizationFlowWidget());
//            this.getOutgoingGeneralizationFlowWidget().getSuperclassWidget().showInheritencePath();
//        }
//    }
//
//    public void hideInheritencePath() {
//        this.removeFromParent();
//        preLayerWidget.addChild(this);
//        if (this.getOutgoingGeneralizationFlowWidget() != null) {
//            this.getOutgoingGeneralizationFlowWidget().removeFromParent();
//            this.getOutgoingGeneralizationFlowWidget().getPreLayerWidget().addChild(this.getOutgoingGeneralizationFlowWidget());
//            this.getOutgoingGeneralizationFlowWidget().getSuperclassWidget().hideInheritencePath();
//        }
//    }
//
//    /**
//     * @return the preLayerWidget
//     */
//    public LayerWidget getPreLayerWidget() {
//        return preLayerWidget;
//    }
//
//    /**
//     * @param preLayerWidget the preLayerWidget to set
//     */
//    public void setPreLayerWidget(LayerWidget preLayerWidget) {
//        this.preLayerWidget = preLayerWidget;
//    }
}
