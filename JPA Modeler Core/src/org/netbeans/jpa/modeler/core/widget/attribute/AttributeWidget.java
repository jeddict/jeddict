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

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.netbeans.jpa.modeler.core.widget.FlowPinWidget;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.properties.PropertiesHandler;
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
import org.netbeans.jpa.modeler.spec.extend.CollectionTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableTypeHandler;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.properties.embedded.EmbeddedDataListener;
import org.netbeans.modeler.properties.embedded.EmbeddedPropertySupport;
import org.netbeans.modeler.properties.embedded.GenericEmbedded;
import org.netbeans.modeler.resource.toolbar.ImageUtil;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.generic.ElementCustomPropertySupport;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.netbeans.modules.j2ee.persistence.dd.JavaPersistenceQLKeywords;
import org.netbeans.orm.converter.util.ClassHelper;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 * @param <E>
 */
public abstract class AttributeWidget<E extends Attribute> extends FlowPinWidget<E,JPAModelerScene> {

//    private boolean selectedView;
    public AttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("name", (PropertyChangeListener<String>) (String value) -> {
            if (value == null || value.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, NbBundle.getMessage(AttributeValidator.class, AttributeValidator.EMPTY_ATTRIBUTE_NAME));
                setName(AttributeWidget.this.getLabel());//rollback
            } else {
                setName(value);
                setLabel(value);
            }
        });

        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) (String tableName) -> {
            if (tableName != null && !tableName.trim().isEmpty()) {
                if (SQLKeywords.isSQL99ReservedKeyword(tableName)) {
                    errorHandler.throwError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                } else {
                    errorHandler.clearError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                }
            } else {
                errorHandler.clearError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
            }
        });
        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (String columnName) -> {
            if (columnName != null && !columnName.trim().isEmpty()) {
                if (SQLKeywords.isSQL99ReservedKeyword(columnName)) {
                    errorHandler.throwError(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
                } else {
                    errorHandler.clearError(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
                }
            } else {
                errorHandler.clearError(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
            }
        });
        
         this.addPropertyChangeListener("collectionType", (PropertyChangeListener<String>) (String collectionType) -> {
            Attribute attribute = getBaseElementSpec();
            boolean valid = false;
            try {
                if (collectionType != null && !collectionType.trim().isEmpty()) {
                    if (java.util.Collection.class.isAssignableFrom(Class.forName(collectionType.trim()))) {
                        valid = true;
                    }
                }
            } catch (ClassNotFoundException ex) {
                //skip allow = false;
            }
            if (!valid) {
                collectionType = java.util.Collection.class.getName();
            }
            
                ((CollectionTypeHandler) attribute).setCollectionType(collectionType.trim());
                                setAttributeTooltip();

        });
        
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
            } catch (NoSuchMethodException | NoSuchFieldException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
          PropertiesHandler.getJaxbVarTypeProperty(set , this, (JaxbVariableTypeHandler) this.getBaseElementSpec());
    }

    private EmbeddedPropertySupport getFieldTypeProperty() {

        GenericEmbedded entity = new GenericEmbedded("fieldType", "Field Type", "");
        if (this.getBaseElementSpec() instanceof BaseAttribute) {
            if (this.getBaseElementSpec() instanceof ElementCollection && ((ElementCollection) this.getBaseElementSpec()).getConnectedClass() != null) {//SingleValueEmbeddableFlowWidget
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
                    persistenceClassWidget = (PersistenceClassWidget) AttributeWidget.this.getModelerScene().getBaseElement(((RelationAttribute) attribute).getConnectedEntity().getId());
                } else if (attribute instanceof ElementCollection && ((ElementCollection) attribute).getConnectedClass() != null) { //Embedded Collection
                    persistenceClassWidget = (PersistenceClassWidget) AttributeWidget.this.getModelerScene().getBaseElement(((ElementCollection) attribute).getConnectedClass().getId());
                } else if (attribute instanceof Embedded) {
                    persistenceClassWidget = (PersistenceClassWidget) AttributeWidget.this.getModelerScene().getBaseElement(((Embedded) attribute).getConnectedClass().getId());
                }
            }

            @Override
            public Attribute getData() {
                return attribute;
            }

            @Override
            public void setData(Attribute baseAttribute) {
                AttributeWidget.this.setBaseElementSpec((E)baseAttribute);
            }

            @Override
            public String getDisplay() {
                if (attribute instanceof BaseAttribute) {
                    if (attribute instanceof ElementCollection) {
                        String collectionType = ((ElementCollection) attribute).getCollectionType();
                        if (((ElementCollection) attribute).getConnectedClass() == null) { //Basic
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

    public static PinWidgetInfo create(String id, String name, IBaseElement baseElement) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id,baseElement);
        pinWidgetInfo.setName(name);
        return pinWidgetInfo;
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = super.getPopupMenuItemList();

        JMenuItem delete;
        delete = new JMenuItem("Delete");
        delete.setIcon(ImageUtil.getInstance().getIcon("delete.png"));
        delete.addActionListener((ActionEvent e) -> {
            AttributeWidget.this.remove(true);
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
             getBaseElementSpec().setName(this.name);
        }
        if (JavaPersistenceQLKeywords.isKeyword(this.getName())) {
            errorHandler.throwError(AttributeValidator.ATTRIBUTE_NAME_WITH_JPQL_KEYWORD);
        } else {
            errorHandler.clearError(AttributeValidator.ATTRIBUTE_NAME_WITH_JPQL_KEYWORD);
        }

        JavaClass javaClass = (JavaClass) this.getClassWidget().getBaseElementSpec();
        if (javaClass.getAttributes().findAllAttribute(this.getName()).size() > 1) {
            errorHandler.throwError(AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
        } else {
            errorHandler.clearError(AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
        }

    }

    @Override
    public void setLabel(String label) {
        if (label != null && !label.trim().isEmpty()) {
            this.setPinName(label.replaceAll("\\s+", ""));
        }
    }

    @Override
    public void createVisualPropertySet(ElementPropertySet elementPropertySet) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    protected abstract void setAttributeTooltip();
    
    @Override
    public void init() {
        setAttributeTooltip();
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

//    /**
//     * @return the selectedView
//     */
//    public boolean isSelectedView() {
//        return selectedView;
//    }
//
//    /**
//     * @param selectedView the selectedView to set
//     */
//    public void setSelectedView(boolean selectedView) {
//        this.selectedView = selectedView;
//    }
}
