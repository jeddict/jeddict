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
package org.netbeans.jpa.modeler.core.widget.attribute.base;

import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.modeler.config.element.ElementConfigFactory;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

/**
 *
 * @author Gaurav Gupta
 */
public class BaseAttributeWidget extends AttributeWidget {

    public BaseAttributeWidget(IModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("collectionType", new PropertyChangeListener<String>() {
            @Override
            public void changePerformed(String collectionType) { //Point here should be for only ElementCollection
                if (BaseAttributeWidget.this.getBaseElementSpec() instanceof ElementCollection) {
                    ElementCollection elementCollection = (ElementCollection) BaseAttributeWidget.this.getBaseElementSpec();
                    boolean valid = false;
                    try {
                        if (collectionType != null || !collectionType.trim().isEmpty()) {
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
                    elementCollection.setCollectionType(collectionType.trim());
                }
            }
        });

    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        super.createPropertySet(set);
        if (this.getBaseElementSpec() instanceof PersistenceBaseAttribute) {
            PersistenceBaseAttribute persistenceBaseAttribute = (PersistenceBaseAttribute) this.getBaseElementSpec();
            if (persistenceBaseAttribute.getColumn() == null) {
                persistenceBaseAttribute.setColumn(new Column());
            }
            ElementConfigFactory elementConfigFactory = this.getModelerScene().getModelerFile().getVendorSpecification().getElementConfigFactory();
            elementConfigFactory.createPropertySet(set, persistenceBaseAttribute.getColumn(), getPropertyChangeListeners(), this.getPropertyVisibilityHandlers());
        }
    }

//    protected ComboBoxPropertySupport getTemporalTypeProperty() {
//        final BaseAttributeWidget attributeWidget = this;
//        final Basic basicSpec = (Basic) attributeWidget.getBaseElementSpec();
//        ComboBoxListener comboBoxListener = new ComboBoxListener() {
//            @Override
//            public void setItem(ComboBoxValue value) {
//                basicSpec.setTemporal((TemporalType) value.getValue());
//            }
//
//            @Override
//            public ComboBoxValue getItem() {
//                return new ComboBoxValue(basicSpec.getTemporal(), basicSpec.getTemporal().value());
//            }
//
//            @Override
//            public List<ComboBoxValue> getItemList() {
//                ComboBoxValue[] values = new ComboBoxValue[]{
//                    new ComboBoxValue(TemporalType.DATE, "Date"),
//                    new ComboBoxValue(TemporalType.TIME, "Time"),
//                    new ComboBoxValue(TemporalType.TIMESTAMP, "TimeStamp")};
//                return Arrays.asList(values);
//            }
//
//            @Override
//            public String getDefaultText() {
//                return "Date";
//            }
//
//            @Override
//            public ActionHandler getActionHandler() {
//                return null;
//            }
//        };
//        return new ComboBoxPropertySupport(this.getModelerScene().getModelerFile(), "temporalType", "Temporal Type", "", comboBoxListener);
//    }
//
//    protected ComboBoxPropertySupport getEnumTypeProperty() {
//        final BaseAttributeWidget attributeWidget = this;
//        final Basic basicSpec = (Basic) attributeWidget.getBaseElementSpec();
//        ComboBoxListener comboBoxListener = new ComboBoxListener() {
//            @Override
//            public void setItem(ComboBoxValue value) {
//                basicSpec.setEnumerated((EnumType) value.getValue());
//            }
//
//            @Override
//            public ComboBoxValue getItem() {
//                return new ComboBoxValue(basicSpec.getEnumerated(), basicSpec.getEnumerated() != null ? basicSpec.getEnumerated().value() : null);
//            }
//
//            @Override
//            public List<ComboBoxValue> getItemList() {
//                ComboBoxValue[] values = new ComboBoxValue[]{
//                    new ComboBoxValue(null, null),
//                    new ComboBoxValue(EnumType.ORDINAL, "Ordinal"),
//                    new ComboBoxValue(EnumType.STRING, "String")};
//                return Arrays.asList(values);
//            }
//
//            @Override
//            public String getDefaultText() {
//                return "Ordinal";
//            }
//
//            @Override
//            public ActionHandler getActionHandler() {
//                return null;
//            }
//        };
//        return new ComboBoxPropertySupport(this.getModelerScene().getModelerFile(), "enumType", "Enum Type", "", comboBoxListener);
//    }
}
