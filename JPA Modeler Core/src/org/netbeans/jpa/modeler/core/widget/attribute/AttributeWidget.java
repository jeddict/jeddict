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

import org.netbeans.jpa.modeler.settings.view.AttributeViewAs;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import javax.lang.model.SourceVersion;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import org.atteo.evo.inflector.English;
import static org.netbeans.jcode.core.util.AttributeType.STRING;
import org.netbeans.jcode.core.util.JavaSourceHelper;
import org.netbeans.jpa.modeler.core.widget.FlowPinWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.properties.PropertiesHandler;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getFieldTypeProperty;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.jpa.modeler.settings.view.ViewPanel;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.IdentifiableClass;
import org.netbeans.jpa.modeler.spec.NamedNativeQuery;
import org.netbeans.jpa.modeler.spec.NamedQuery;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.CollectionTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyType;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableTypeHandler;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.resource.toolbar.ImageUtil;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.generic.ElementCustomPropertySupport;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.netbeans.modeler.widget.properties.handler.PropertyVisibilityHandler;
import org.netbeans.modules.j2ee.persistence.dd.JavaPersistenceQLKeywords;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

/**
 *
 * @author Gaurav Gupta
 * @param <E>
 */
public abstract class AttributeWidget<E extends Attribute> extends FlowPinWidget<E, JPAModelerScene> {

    public AttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }
    
    public void visualizeDataType() {
        AttributeViewAs viewAs = ViewPanel.getDataType();
        
        String dataType = ((Attribute) this.getBaseElementSpec()).getDataTypeLabel();
        if (viewAs == AttributeViewAs.SIMPLE_CLASS_NAME) {
            dataType = JavaSourceHelper.getSimpleClassName(dataType);
        } else if (viewAs == AttributeViewAs.SHORT_CLASS_NAME) {
            dataType = JavaSourceHelper.getSimpleClassName(dataType);
            final int SHORT_LENGTH = 3;
            if (dataType.length() > SHORT_LENGTH) {
                dataType = dataType.substring(0, SHORT_LENGTH);
            }
        } else if (viewAs == AttributeViewAs.NONE) {
            return;
        }
        visualizeDataType(dataType);
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        if (!(this.getBaseElementSpec() instanceof EmbeddedId)) {//to hide property
            set.put("BASIC_PROP", getFieldTypeProperty("fieldType", "Field Type", "", false, this));
        } else {
            try {//add "custom manual editable class type property" instead of "Field Type Panel" for EmbeddedId
                set.put("BASIC_PROP", new ElementCustomPropertySupport(set.getModelerFile(), this.getClassWidget().getBaseElementSpec(), String.class,
                       "compositePrimaryKeyClass", "compositePrimaryKeyClass", "Field Type", "", null));
            } catch (NoSuchMethodException | NoSuchFieldException ex) {
                this.getModelerScene().getModelerFile().handleException(ex);;
            }
        }
        PropertiesHandler.getJaxbVarTypeProperty(set, this, (JaxbVariableTypeHandler) this.getBaseElementSpec());
        set.put("BASIC_PROP", PropertiesHandler.getCustomAnnoation(this.getModelerScene(), this.getBaseElementSpec().getAnnotation()));

        this.addPropertyChangeListener("name", (PropertyChangeListener<String>) (String value) -> {
            if (StringUtils.isBlank(value)) {
                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), NbBundle.getMessage(AttributeValidator.class, AttributeValidator.EMPTY_ATTRIBUTE_NAME));
                setName(AttributeWidget.this.getLabel());//rollback
            } else {
                setName(value);
                setLabel(value);
            }
        });

        this.addPropertyChangeListener("table_name", (PropertyChangeListener<String>) (String tableName) -> {
            if (tableName != null && !tableName.trim().isEmpty()) {
                if (SQLKeywords.isSQL99ReservedKeyword(tableName)) {
                    warningHandler.throwSignal(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                } else {
                    warningHandler.clearSignal(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                }
            } else {
                warningHandler.clearSignal(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
            }
        });
        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (String columnName) -> {
            if (columnName != null && !columnName.trim().isEmpty()) {
                if (SQLKeywords.isSQL99ReservedKeyword(columnName)) {
                    warningHandler.throwSignal(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
                } else {
                    warningHandler.clearSignal(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
                }
            } else {
                warningHandler.clearSignal(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
            }
        });
        
    }

   
    public static <T> T getInstance(IPNodeWidget nodeWidget, String name, IBaseElement baseElement, Class documentId) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(baseElement.getId(), baseElement);
        pinWidgetInfo.setName(name);
        pinWidgetInfo.setDocumentId(documentId.getSimpleName());
        return (T)nodeWidget.createPinWidget(pinWidgetInfo);
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
    public void init() {
        setAttributeTooltip();
        this.getClassWidget().scanDuplicateAttributes(null, this.name);
        validateName(null, this.getName());
        visualizeDataType();
    }

    @Override
    public void destroy() {
        this.getClassWidget().scanDuplicateAttributes(this.name, null);
    }
    private void validateName(String previousName,String name){
        if (JavaPersistenceQLKeywords.isKeyword(name)) {
            errorHandler.throwSignal(AttributeValidator.ATTRIBUTE_NAME_WITH_JPQL_KEYWORD);
        } else {
            errorHandler.clearSignal(AttributeValidator.ATTRIBUTE_NAME_WITH_JPQL_KEYWORD);
        }
        if(SourceVersion.isName(name)){
            errorHandler.clearSignal(AttributeValidator.INVALID_ATTRIBUTE_NAME);
        } else {
            errorHandler.throwSignal(AttributeValidator.INVALID_ATTRIBUTE_NAME);
        }
        this.getClassWidget().scanDuplicateAttributes(previousName, name);

    }
    @Override
    public void setName(String name) {
        String previousName = this.name;
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.replaceAll("\\s+", "");
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                getBaseElementSpec().setName(this.name);
                refractorReference(previousName, this.name);
            }
        }
       validateName(previousName,this.getName());
    }
    
    private void refractorReference(String previousName, String newName) {
        if (previousName == null) {
            return;
        }
        ModelerFile modelerFile = this.getModelerScene().getModelerFile();
        RequestProcessor.getDefault().post(() -> {
            try {

                String singularPreName = previousName;
                String pluralPreName = English.plural(singularPreName);
                String singularNewName = newName;
                String pluralNewName = English.plural(singularNewName);
       

           
                //Refractor NamedQuery, NamedNativeQuery
                if (this.getClassWidget().getBaseElementSpec() instanceof IdentifiableClass) {
                    ((IdentifiableClass)this.getClassWidget().getBaseElementSpec()).getNamedQuery().stream().forEach((NamedQuery obj) -> {
                        obj.refractorName(singularPreName, singularNewName);
                        obj.refractorName(pluralPreName, pluralNewName);
                        obj.refractorQuery(singularPreName, singularNewName);
                        obj.refractorQuery(pluralPreName, pluralNewName);
                    }); 
                    ((IdentifiableClass)this.getClassWidget().getBaseElementSpec()).getNamedNativeQuery().stream().forEach((NamedNativeQuery obj) -> {
                        obj.refractorName(singularPreName, singularNewName);
                        obj.refractorName(pluralPreName, pluralNewName);
                        obj.refractorQuery(singularPreName, singularNewName);
                        obj.refractorQuery(pluralPreName, pluralNewName);
                    }); 
                }

               
            } catch (Throwable t) {
                modelerFile.handleException(t);
            }
        });
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

    public void setAttributeTooltip() {
        this.setToolTipText(this.getBaseElementSpec().getDataTypeLabel());
    }

    /**
     * @return the classWidget
     */
    public PersistenceClassWidget getClassWidget() {
        return (PersistenceClassWidget) this.getPNodeWidget();
    }

    
    protected void createMapKeyPropertySet(ElementPropertySet set){
        Attribute attribute = this.getBaseElementSpec();
        if(!(attribute instanceof MapKeyHandler)){
            throw new IllegalStateException("BaseElementSpec does not implements MapKeyHandler");
        }
        MapKeyHandler mapKeyHandler = (MapKeyHandler)attribute;
         PropertyVisibilityHandler mapKeyVisibilityHandler = () -> {
            if(attribute instanceof CollectionTypeHandler){
                String classname = ((CollectionTypeHandler)attribute).getCollectionType();
                    try {
                        return Map.class.isAssignableFrom(Class.forName(classname));
                    } catch (ClassNotFoundException ex) { }
            }
            return false;
        };      
        
        set.put("BASIC_PROP", PropertiesHandler.getMapKeyProperty(this, mapKeyHandler, mapKeyVisibilityHandler));
        set.put("BASIC_PROP", PropertiesHandler.getFieldTypeProperty("mapKeyFieldType", "Map Key", "", true, this));
        set.put("COLLECTION_TABLE_PROP", PropertiesHandler.getJoinColumnsProperty("mapKeyJoinColumns", "MapKey Join Columns", "", this.getModelerScene(), mapKeyHandler.getMapKeyJoinColumn()));
        
        
        this.addPropertyChangeListener("mapKeyType",(val) -> {
            mapKeyHandler.resetMapAttribute(); 
            AttributeValidator.scanMapKeyHandlerError(this);
            visualizeDataType();
        });
        this.addPropertyVisibilityHandler("mapKeyType", mapKeyVisibilityHandler);
        this.addPropertyVisibilityHandler("mapKeyFieldType", () -> mapKeyVisibilityHandler.isVisible() && mapKeyHandler.getMapKeyType() == MapKeyType.NEW);
        this.addPropertyVisibilityHandler("mapKey", () -> mapKeyVisibilityHandler.isVisible() && mapKeyHandler.getMapKeyType() == MapKeyType.EXT);
        this.addPropertyVisibilityHandler("mapKeyJoinColumns", () -> {
               return mapKeyVisibilityHandler.isVisible() && mapKeyHandler.getMapKeyType() == MapKeyType.NEW && mapKeyHandler.getMapKeyEntity()!=null;
                        }
        );
    }

}
