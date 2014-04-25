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
//        super.createPropertySet(set);
        if (this.getBaseElementSpec() instanceof PersistenceBaseAttribute) {
            PersistenceBaseAttribute persistenceBaseAttribute = (PersistenceBaseAttribute) this.getBaseElementSpec();
            if (persistenceBaseAttribute.getColumn() == null) {
                persistenceBaseAttribute.setColumn(new Column());
            }
            ElementConfigFactory elementConfigFactory = this.getModelerScene().getModelerFile().getVendorSpecification().getElementConfigFactory();
            elementConfigFactory.createPropertySet(set, persistenceBaseAttribute.getColumn(), getPropertyChangeListeners(), this.getPropertyVisibilityHandlers());
        }
//        set.put("BASIC_PROP", getValidationProperty());

    }

//    public PropertySupport getValidationProperty() {
//
//        final List<JoinColumn> joinColumnsSpec = null;
//
//        final NAttributeEntity attributeEntity = new NAttributeEntity("VALIDATION_PROPERTY", "Validation", "");
//        attributeEntity.setCountDisplay(new String[]{"No JoinColumns exist", "One JoinColumn exist", "JoinColumns exist"});
//
//        List<org.netbeans.modeler.properties.nentity.Column> columns = new ArrayList<org.netbeans.modeler.properties.nentity.Column>();
//        columns.add(new org.netbeans.modeler.properties.nentity.Column("OBJECT", false, true, Object.class));
//        columns.add(new org.netbeans.modeler.properties.nentity.Column("Column Name", false, String.class));
//        columns.add(new org.netbeans.modeler.properties.nentity.Column("Referenced Column Name", false, String.class));
//        attributeEntity.setColumns(columns);
//        attributeEntity.setCustomDialog(new JoinColumnPanel());
//
//        attributeEntity.setTableDataListener(new NEntityDataListener() {
//            List<Object[]> data;
//            int count;
//
//            @Override
//            public void initCount() {
//                count = joinColumnsSpec.size();
//            }
//
//            @Override
//            public int getCount() {
//                return count;
//            }
//
//            @Override
//            public void initData() {
//                List<JoinColumn> joinColumns = joinColumnsSpec;
//                List<Object[]> data_local = new LinkedList<Object[]>();
//                Iterator<JoinColumn> itr = joinColumns.iterator();
//                while (itr.hasNext()) {
//                    JoinColumn joinColumn = itr.next();
//                    Object[] row = new Object[attributeEntity.getColumns().size()];
//                    row[0] = joinColumn;
//                    row[1] = joinColumn.getName();
//                    row[2] = joinColumn.getReferencedColumnName();
//                    data_local.add(row);
//                }
//                this.data = data_local;
//            }
//
//            @Override
//            public List<Object[]> getData() {
//                return data;
//            }
//
//            @Override
//            public void setData(List data) {
//                joinColumnsSpec.clear();
//                for (Object[] row : (List<Object[]>) data) {
//                    joinColumnsSpec.add((JoinColumn) row[0]);
//                }
//                this.data = data;
//            }
//
//        });
//
//        return new NEntityPropertySupport(this.getModelerScene().getModelerFile(), attributeEntity);
//    }
}
