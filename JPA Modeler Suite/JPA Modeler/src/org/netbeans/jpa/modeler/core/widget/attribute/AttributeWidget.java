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
package org.netbeans.jpa.modeler.core.widget.attribute;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.netbeans.jpa.modeler.core.widget.FlowPinWidget;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.properties.fieldtype.FieldTypePanel;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.FlowPin;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.modeler.properties.embedded.EmbeddedDataListener;
import org.netbeans.modeler.properties.embedded.EmbeddedPropertySupport;
import org.netbeans.modeler.properties.embedded.GenericEmbedded;
import org.netbeans.modeler.resource.toolbar.ImageUtil;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.generic.ElementCustomPropertySupport;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.netbeans.modules.j2ee.persistence.dd.JavaPersistenceQLKeywords;
import org.netbeans.orm.converter.util.ClassHelper;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
public class AttributeWidget extends FlowPinWidget {

    public AttributeWidget(IModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("name", new PropertyChangeListener<String>() {
            @Override
            public void changePerformed(String value) {
                if (value == null || value.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, NbBundle.getMessage(AttributeValidator.class, AttributeValidator.EMPTY_ATTRIBUTE_NAME));
                    setName(AttributeWidget.this.getLabel());//rollback
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
                        throwError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                    } else {
                        clearError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                    }
                } else {
                    clearError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                }
            }
        });
        this.addPropertyChangeListener("column_name", new PropertyChangeListener<String>() {
            @Override
            public void changePerformed(String tableName) {
                if (tableName != null && !tableName.trim().isEmpty()) {
                    if (SQLKeywords.isSQL99ReservedKeyword(tableName)) {
                        throwError(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
                    } else {
                        clearError(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
                    }
                } else {
                    clearError(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
                }
            }
        });

    }

//    private static SelectProvider selectProvider = new SelectProvider() {
//
//        @Override
//        public boolean isAimingAllowed(Widget widget, Point localLocation, boolean invertSelection) {
//            return false;
//        }
//
//        public boolean isSelectionAllowed(Widget widget, Point localLocation, boolean invertSelection) {
//            return true;
//        }
//
//        @Override
//        public void select(Widget widget, Point localLocation, boolean invertSelection) {
//            ImageWidget imageWidget = (ImageWidget) widget;
//                  NotifyDescriptor d = new NotifyDescriptor.Confirmation("Java Class name can not be empty", "Java Class name", NotifyDescriptor.ERROR_MESSAGE);
//                    DialogDisplayer.getDefault().notify(d);
//
//        }
//    };
    private Image icon;
    private static Image errorIcon = ImageUtilities.loadImage("org/netbeans/jpa/modeler/resource/image/error_small_icon.gif");

    protected void setIcon(Image image) {
        this.icon = image;
        this.setPinImage(image);
    }

    private final java.util.Map<String, String> errorList = new HashMap<String, String>();

    public void throwError(String key) {
        errorList.put(key, NbBundle.getMessage(AttributeValidator.class, key));
        printError();
    }

    public void throwError(String key, String message) {
        errorList.put(key, message);
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
            this.setPinImage(getErrorIcon());
        } else {
            this.setToolTipText(null);
            this.setPinImage(icon);
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

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        if (!(this.getBaseElementSpec() instanceof EmbeddedId)) {//to hide property
            set.put("BASIC_PROP", getFieldTypeProperty());
        } else {
            try {//add "custom manual editable class type property" instead of "Field Type Panel" for EmbeddedId
                set.put("BASIC_PROP", new ElementCustomPropertySupport(set.getModelerFile(), this.getClassWidget().getBaseElementSpec(), String.class,
                        "compositePrimaryKeyClass", "Field Type", "", null));
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            } catch (NoSuchFieldException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private EmbeddedPropertySupport getFieldTypeProperty() {

        GenericEmbedded entity = new GenericEmbedded("fieldType", "Field Type", "");
        if (this.getBaseElementSpec() instanceof BaseAttribute) {
            if (this.getBaseElementSpec() instanceof ElementCollection && ((ElementCollection) this.getBaseElementSpec()).getConnectedClassId() != null) {//SingleValueEmbeddableFlowWidget
                entity.setEntityEditor(null);
            } else if (this.getBaseElementSpec() instanceof Embedded) {//to Disable it
                entity.setEntityEditor(null);
            } else {
                entity.setEntityEditor(new FieldTypePanel(this.getModelerScene().getModelerFile()));
            }

        } else if (this.getBaseElementSpec() instanceof RelationAttribute) {
            entity.setEntityEditor(null);
        }
        entity.setDataListener(new EmbeddedDataListener<Attribute>() {
            private Attribute attribute;
            private String displayName = null;
            private PersistenceClassWidget persistenceClassWidget = null;

            @Override
            public void init() {
                attribute = (Attribute) AttributeWidget.this.getBaseElementSpec();
                if (attribute instanceof RelationAttribute) {
                    persistenceClassWidget = (PersistenceClassWidget) AttributeWidget.this.getModelerScene().findBaseElement(((RelationAttribute) attribute).getConnectedEntityId());
                } else if (attribute instanceof ElementCollection && ((ElementCollection) attribute).getConnectedClassId() != null) { //Embedded Collection
                    persistenceClassWidget = (PersistenceClassWidget) AttributeWidget.this.getModelerScene().findBaseElement(((ElementCollection) attribute).getConnectedClassId());
                } else if (attribute instanceof Embedded) {
                    persistenceClassWidget = (PersistenceClassWidget) AttributeWidget.this.getModelerScene().findBaseElement(((Embedded) attribute).getConnectedClassId());
                }
            }

            @Override
            public Attribute getData() {
                return attribute;
            }

            @Override
            public void setData(Attribute baseAttribute) {
                AttributeWidget.this.setBaseElementSpec(baseAttribute);
            }

            @Override
            public String getDisplay() {
                if (attribute instanceof BaseAttribute) {
                    if (attribute instanceof ElementCollection) {
                        String collectionType = ((ElementCollection) attribute).getCollectionType();
                        if (((ElementCollection) attribute).getConnectedClassId() == null) { //Basic
                            displayName = ClassHelper.getSimpleClassName(collectionType) + "<" + ((ElementCollection) attribute).getTargetClass() + ">";
                        } else { //Embedded
                            displayName = ClassHelper.getSimpleClassName(collectionType) + "<" + persistenceClassWidget.getName() + ">";
                        }
                    } else if (attribute instanceof Embedded) {
                        displayName = persistenceClassWidget.getName();
                    } else {
                        displayName = ((BaseAttribute) attribute).getAttributeType();
                    }
                } else if (attribute instanceof RelationAttribute) {
                    // Issue Fix #5851 Start
                    if (attribute instanceof OneToMany || attribute instanceof ManyToMany) {
                        String collectionType = null;
                        if (attribute instanceof OneToMany) {
                            collectionType = ((OneToMany) attribute).getCollectionType();
                        } else if (attribute instanceof ManyToMany) {
                            collectionType = ((ManyToMany) attribute).getCollectionType();
                        }
                        displayName = ClassHelper.getSimpleClassName(collectionType) + "<" + persistenceClassWidget.getName() + ">";
                    } else {
                        displayName = persistenceClassWidget.getName();
                    }
                    // Issue Fix #5851 End

                }
                return displayName;

            }

        });
        return new EmbeddedPropertySupport(this.getModelerScene().getModelerFile(), entity);
    }

    public static PinWidgetInfo create(String id, String name) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id);
        pinWidgetInfo.setName(name);
        return pinWidgetInfo;
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = super.getPopupMenuItemList();

        JMenuItem delete;
        delete = new JMenuItem("Delete");
        delete.setIcon(ImageUtil.getInstance().getIcon("delete.png"));
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AttributeWidget.this.remove(true);
            }
        });

        menuList.add(0, delete);

        return menuList;
    }

    @Override
    public boolean remove() {
        return remove(false);
    }

    @Override
    public boolean remove(boolean notification) {
        // Issue Fix #5855 Start
        if (super.remove(notification)) {
            getClassWidget().deleteAttribute(AttributeWidget.this);
            return true;
        }
        // Issue Fix #5855 End
        return false;
    }

    @Override
    public void setName(String name) {

        if (name != null && !name.trim().isEmpty()) {
            this.name = name.replaceAll("\\s+", "");
            ((FlowPin) getBaseElementSpec()).setName(this.name);
        }
        if (JavaPersistenceQLKeywords.isKeyword(this.getName())) {
            throwError(AttributeValidator.ATTRIBUTE_NAME_WITH_JPQL_KEYWORD);
        } else {
            clearError(AttributeValidator.ATTRIBUTE_NAME_WITH_JPQL_KEYWORD);
        }

        JavaClass javaClass = (JavaClass) this.getClassWidget().getBaseElementSpec();
        if (javaClass.getAttributes().findAllAttribute(this.getName()).size() > 1) {
            throwError(AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
        } else {
            clearError(AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
        }

    }

    public void setLabel(String label) {
        if (label != null && !label.trim().isEmpty()) {
            this.setPinName(label.replaceAll("\\s+", ""));
        }
    }

    @Override
    public void createVisualPropertySet(ElementPropertySet elementPropertySet
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private IBaseElement baseElementSpec;

    /**
     * @return the baseElementSpec
     */
    @Override
    public IBaseElement getBaseElementSpec() {
        return baseElementSpec;
    }

    /**
     * @param baseElementSpec the baseElementSpec to set
     */
    @Override
    public void setBaseElementSpec(IBaseElement baseElementSpec) {
        this.baseElementSpec = baseElementSpec;
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    /**
     * @return the classWidget
     */
    public JavaClassWidget getClassWidget() {
        return (JavaClassWidget) this.getPNodeWidget();
    }

}
