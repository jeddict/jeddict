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
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class BaseAttributeWidget<E extends BaseAttribute> extends AttributeWidget<E> {

    public BaseAttributeWidget(JPAModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("attributeType", (PropertyChangeListener<String>) (String attributeType) -> {
            setAttributeTooltip();
        });
    }

    @Override
    protected void setAttributeTooltip() {
        if (getBaseElementSpec() instanceof ElementCollection) {
            ElementCollection elementCollection = (ElementCollection) getBaseElementSpec();
            StringBuilder writer = new StringBuilder();
            writer.append(elementCollection.getCollectionType().substring(elementCollection.getCollectionType().lastIndexOf('.') + 1));
            writer.append('<').append(elementCollection.getAttributeType()).append('>');
            this.setToolTipText(writer.toString());
        } else {
            this.setToolTipText(this.getBaseElementSpec().getAttributeType());//TODO init called before initialization of connectedClass for CompositionAttribute
        }
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);

        if (this.getBaseElementSpec() instanceof PersistenceBaseAttribute) {
            PersistenceBaseAttribute persistenceBaseAttribute = (PersistenceBaseAttribute) this.getBaseElementSpec();
            if (persistenceBaseAttribute.getColumn() == null) {
                persistenceBaseAttribute.setColumn(new Column());
            }

            set.createPropertySet(this, persistenceBaseAttribute.getColumn(), getPropertyChangeListeners(), getPropertyVisibilityHandlers());
        } else if (this instanceof BasicCollectionAttributeWidget) {
            ElementCollection elementCollection = (ElementCollection) this.getBaseElementSpec();
            if (elementCollection.getColumn() == null) {
                elementCollection.setColumn(new Column());
            }

            set.createPropertySet(this, elementCollection.getColumn(), getPropertyChangeListeners(), getPropertyVisibilityHandlers());
        }
//        BasicCollectionAttributeWidget => ElementCollection [Column allowed]
//        MultiValueEmbeddedAttributeWidget => ElementCollection [Column not allowed]
//        set.put("BASIC_PROP", getValidationProperty());
        
        createBeanValidationPropertySet(set);
    }
    
    public void createBeanValidationPropertySet(ElementPropertySet set){
        set.deleteGroup("CONSTRAINTS");
        this.getBaseElementSpec().getNewConstraints().stream().forEach((constraint) -> {
            set.createPropertySet(this, constraint, getPropertyChangeListeners(), getPropertyVisibilityHandlers());
        });
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
