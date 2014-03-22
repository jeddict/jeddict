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
package org.netbeans.jpa.modeler.core.widget.attribute.relation;

import javax.swing.JOptionPane;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.properties.cascade.CascadeTypePanel;
import org.netbeans.jpa.modeler.spec.CascadeType;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.extend.JoinColumnHandler;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.config.element.ElementConfigFactory;
import org.netbeans.modeler.properties.embedded.EmbeddedDataListener;
import org.netbeans.modeler.properties.embedded.EmbeddedPropertySupport;
import org.netbeans.modeler.properties.embedded.GenericEmbedded;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class RelationAttributeWidget extends AttributeWidget {

    private boolean owner = false;

    public RelationAttributeWidget(IModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("collectionType", new PropertyChangeListener<String>() {
            @Override
            public void changePerformed(String collectionType) {
                RelationAttribute relationAttribute = (RelationAttribute) RelationAttributeWidget.this.getBaseElementSpec();
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
                if (relationAttribute instanceof OneToMany) {
                    ((OneToMany) relationAttribute).setCollectionType(collectionType.trim());
                } else if (relationAttribute instanceof ManyToMany) {
                    ((ManyToMany) relationAttribute).setCollectionType(collectionType.trim());
                }

            }
        });

    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        set.put("BASIC_PROP", getCascadeProperty());
        RelationAttribute relationAttributeSpec = (RelationAttribute) this.getBaseElementSpec();

        if (owner) {

            if (this.getBaseElementSpec() instanceof JoinColumnHandler) {
                JoinColumnHandler joinColumnHandlerSpec = (JoinColumnHandler) this.getBaseElementSpec();
                set.put("JOIN_COLUMN_PROP", JPAModelerUtil.getJoinColumnsProperty("JoinColumns", "Join Columns", "", this.getModelerScene(), joinColumnHandlerSpec.getJoinColumn()));

            }
            ElementConfigFactory elementConfigFactory = this.getModelerScene().getModelerFile().getVendorSpecification().getElementConfigFactory();
            elementConfigFactory.createPropertySet(set, relationAttributeSpec.getJoinTable());

            set.put("JOIN_TABLE_PROP", JPAModelerUtil.getJoinColumnsProperty("JoinTable_JoinColumns", "Join Columns", "", this.getModelerScene(), relationAttributeSpec.getJoinTable().getJoinColumn()));
            set.put("JOIN_TABLE_PROP", JPAModelerUtil.getJoinColumnsProperty("JoinTable_InverseJoinColumns", "Inverse Join Columns", "", this.getModelerScene(), relationAttributeSpec.getJoinTable().getInverseJoinColumn()));

//            set.put("JOIN_TABLE_PROP", getJoinTableColumnProperty());
//            set.put("JOIN_TABLE_PROP", getJoinTableInverseColumnProperty());
        }

    }

//    private PropertySupport getJoinTableInverseColumnProperty() {
//        final RelationAttribute relationAttributeSpec = (RelationAttribute) this.getBaseElementSpec();
//        final NAttributeEntity attributeEntity = new NAttributeEntity("JoinTable_InverseJoinColumns", "Inverse Join Columns", "");
//        attributeEntity.setCountDisplay(new String[]{"No JoinColumns exist", "One JoinColumn exist", "JoinColumns exist"});
//
//        List<Column> columns = new ArrayList<Column>();
//        columns.add(new Column("OBJECT", false, true, Object.class));
//        columns.add(new Column("Column Name", false, String.class));
//        columns.add(new Column("Referenced Column Name", false, String.class));
//        attributeEntity.setColumns(columns);
//        attributeEntity.setCustomDialog(new JoinColumnPanel());
//
//        attributeEntity.setTableDataListener(new NEntityDataListener() {
//            List<Object[]> data;
//            int count;
//
//            @Override
//            public void initCount() {
//                count = relationAttributeSpec.getJoinTable().getInverseJoinColumn().size();
//            }
//
//            @Override
//            public int getCount() {
//                return count;
//            }
//
//            @Override
//            public void initData() {
//                List<JoinColumn> joinColumns = relationAttributeSpec.getJoinTable().getInverseJoinColumn();
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
//                relationAttributeSpec.getJoinTable().getInverseJoinColumn().clear();
//                for (Object[] row : (List<Object[]>) data) {
//                    relationAttributeSpec.getJoinTable().getInverseJoinColumn().add((JoinColumn) row[0]);
//                }
//                this.data = data;
//            }
//
//        });
//
//        return new NEntityPropertySupport(this.getModelerScene().getModelerFile(), attributeEntity);
//    }
//    private PropertySupport getJoinTableColumnProperty() {
//        final RelationAttribute relationAttributeSpec = (RelationAttribute) this.getBaseElementSpec();
//        final NAttributeEntity attributeEntity = new NAttributeEntity("JoinTable_JoinColumns", "Join Columns", "");
//        attributeEntity.setCountDisplay(new String[]{"No JoinColumns exist", "One JoinColumn exist", "JoinColumns exist"});
//
//        List<Column> columns = new ArrayList<Column>();
//        columns.add(new Column("OBJECT", false, true, Object.class));
//        columns.add(new Column("Column Name", false, String.class));
//        columns.add(new Column("Referenced Column Name", false, String.class));
//        attributeEntity.setColumns(columns);
//        attributeEntity.setCustomDialog(new JoinColumnPanel());
//
//        attributeEntity.setTableDataListener(new NEntityDataListener() {
//            List<Object[]> data;
//            int count;
//
//            @Override
//            public void initCount() {
//                count = relationAttributeSpec.getJoinTable().getJoinColumn().size();
//            }
//
//            @Override
//            public int getCount() {
//                return count;
//            }
//
//            @Override
//            public void initData() {
//                List<JoinColumn> joinColumns = relationAttributeSpec.getJoinTable().getJoinColumn();
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
//                relationAttributeSpec.getJoinTable().getJoinColumn().clear();
//                for (Object[] row : (List<Object[]>) data) {
//                    relationAttributeSpec.getJoinTable().getJoinColumn().add((JoinColumn) row[0]);
//                }
//                this.data = data;
//            }
//
//        });
//
//        return new NEntityPropertySupport(this.getModelerScene().getModelerFile(), attributeEntity);
//    }
//    private PropertySupport getJoinColumnsProperty() {
//        final JoinColumnHandler joinColumnHandlerSpec = (JoinColumnHandler) this.getBaseElementSpec();
//        final NAttributeEntity attributeEntity = new NAttributeEntity("JoinColumns", "Join Columns", "");
//        attributeEntity.setCountDisplay(new String[]{"No JoinColumns exist", "One JoinColumn exist", "JoinColumns exist"});
//
//        List<Column> columns = new ArrayList<Column>();
//        columns.add(new Column("OBJECT", false, true, Object.class));
//        columns.add(new Column("Column Name", false, String.class));
//        columns.add(new Column("Referenced Column Name", false, String.class));
//        attributeEntity.setColumns(columns);
//        attributeEntity.setCustomDialog(new JoinColumnPanel());
//
//        attributeEntity.setTableDataListener(new NEntityDataListener/*<TProperty>*/() {
//                    List<Object[]> data;
//                    int count;
//
//                    @Override
//                    public void initCount() {
//                        count = joinColumnHandlerSpec.getJoinColumn().size();
//                    }
//
//                    @Override
//                    public int getCount() {
//                        return count;
//                    }
//
//                    @Override
//                    public void initData() {
//                        List<JoinColumn> joinColumns = joinColumnHandlerSpec.getJoinColumn();
//                        List<Object[]> data_local = new LinkedList<Object[]>();
//                        Iterator<JoinColumn> itr = joinColumns.iterator();
//                        while (itr.hasNext()) {
//                            JoinColumn joinColumn = itr.next();
//                            Object[] row = new Object[attributeEntity.getColumns().size()];
//                            row[0] = joinColumn;
//                            row[1] = joinColumn.getName();
//                            row[2] = joinColumn.getReferencedColumnName();
//                            data_local.add(row);
//                        }
//                        this.data = data_local;
//                    }
//
//                    @Override
//                    public List<Object[]> getData() {
//                        return data;
//                    }
//
//                    @Override
//                    public void setData(List data) {
//                        joinColumnHandlerSpec.getJoinColumn().clear();
//                        for (Object[] row : (List<Object[]>) data) {
//                            joinColumnHandlerSpec.addJoinColumn((JoinColumn) row[0]);
//                        }
//                        this.data = data;
//                    }
//
//                });
//
//        return new NEntityPropertySupport(this.getModelerScene().getModelerFile(), attributeEntity);
//    }
    private EmbeddedPropertySupport getCascadeProperty() {

        GenericEmbedded entity = new GenericEmbedded("cascadeType", "Cascade Type", "");
        entity.setEntityEditor(new CascadeTypePanel(this.getModelerScene().getModelerFile()));

        entity.setDataListener(new EmbeddedDataListener<CascadeType>() {
            private RelationAttribute relationAttribute;

            @Override
            public void init() {
                relationAttribute = (RelationAttribute) RelationAttributeWidget.this.getBaseElementSpec();
            }

            @Override
            public CascadeType getData() {
                return relationAttribute.getCascade();
            }

            @Override
            public void setData(CascadeType cascadeType) {
                relationAttribute.setCascade(cascadeType);
            }

            @Override
            public String getDisplay() {
                StringBuilder display = new StringBuilder();
                CascadeType cascadeType = relationAttribute.getCascade();
                if (cascadeType == null) {
                    display.append("None");
                } else {
                    if (cascadeType.getCascadeAll() != null) {
                        display.append("All");
                    } else {
                        if (cascadeType.getCascadeDetach() != null) {
                            display.append("Detach,");
                        }
                        if (cascadeType.getCascadeMerge() != null) {
                            display.append("Merge,");
                        }
                        if (cascadeType.getCascadePersist() != null) {
                            display.append("Persist,");
                        }
                        if (cascadeType.getCascadeRefresh() != null) {
                            display.append("Refresh,");
                        }
                        if (cascadeType.getCascadeRemove() != null) {
                            display.append("Remove,");
                        }
                        if (display.length() != 0) {
                            display.setLength(display.length() - 1);
                        }
                    }
                }

                return display.toString();
            }

        });
        return new EmbeddedPropertySupport(this.getModelerScene().getModelerFile(), entity);
    }

    /**
     * @return the owner
     */
    public boolean isOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public void setConnectedSibling(JavaClassWidget classWidget) {
        RelationAttribute relationAttribute = (RelationAttribute) this.getBaseElementSpec();
        relationAttribute.setConnectedEntityId(classWidget.getId());
    }

    public void setConnectedSibling(JavaClassWidget classWidget, AttributeWidget attributeWidget) {
        RelationAttribute relationAttribute = (RelationAttribute) this.getBaseElementSpec();
        relationAttribute.setConnectedEntityId(classWidget.getId());
        relationAttribute.setConnectedAttributeId(attributeWidget.getId());

    }

    public abstract RelationFlowWidget getRelationFlowWidget();
}
