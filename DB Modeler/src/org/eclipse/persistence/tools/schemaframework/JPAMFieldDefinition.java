/**
 * Copyright [2016] Gaurav Gupta
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
package org.eclipse.persistence.tools.schemaframework;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.netbeans.db.modeler.spec.DBColumn;
import org.netbeans.db.modeler.spec.DBDiscriminatorColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedAssociationInverseJoinColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedAssociationJoinColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedAttributeColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedAttributeJoinColumn;
import org.netbeans.db.modeler.spec.*;
import org.netbeans.db.modeler.spec.DBInverseJoinColumn;
import org.netbeans.db.modeler.spec.DBJoinColumn;
import org.netbeans.db.modeler.spec.DBParentAssociationInverseJoinColumn;
import org.netbeans.db.modeler.spec.DBParentAssociationJoinColumn;
import org.netbeans.db.modeler.spec.DBParentAttributeColumn;
import org.netbeans.db.modeler.spec.DBPrimaryKeyJoinColumn;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.jpa.modeler.spec.DefaultAttribute;
import org.netbeans.jpa.modeler.spec.DiscriminatorColumn;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.modeler.core.NBModelerUtil;

public class JPAMFieldDefinition extends FieldDefinition {

    private Entity intrinsicClass;
    private LinkedList<Attribute> intrinsicAttribute = new LinkedList<>();
    private Attribute managedAttribute;
    private boolean inverse;
    private boolean foriegnKey;
    private boolean relationTable;
    private boolean inherited;
    private DiscriminatorColumn discriminatorColumn;

    public JPAMFieldDefinition(LinkedList<Attribute> intrinsicAttribute, Attribute managedAttribute, boolean inverse, boolean foriegnKey, boolean relationTable) {
        if (intrinsicAttribute != null) {
            intrinsicAttribute.stream().forEach((attr) -> {
                if (attr != null && attr.getOrignalObject() != null) {
                    this.intrinsicAttribute.add((Attribute) attr.getOrignalObject());
                } else {
                    this.intrinsicAttribute.add(attr);
                }
            });
        }
//       if(managedAttribute!=null){
        this.managedAttribute = managedAttribute != null && managedAttribute.getOrignalObject() != null ? (Attribute) managedAttribute.getOrignalObject() : managedAttribute;
//       } else {
//           this.managedAttribute = null;
//       }
        this.inverse = inverse;
        this.foriegnKey = foriegnKey;
        this.relationTable = relationTable;
        this.inherited = false;
        this.intrinsicClass = null;
    }

    public JPAMFieldDefinition(Entity intrinsicClass) {
        this.intrinsicClass = intrinsicClass != null && intrinsicClass.getOrignalObject() != null ? (Entity) intrinsicClass.getOrignalObject() : intrinsicClass;
        if (intrinsicClass.getDiscriminatorColumn() == null) {
            intrinsicClass.setDiscriminatorColumn(new DiscriminatorColumn());
        }
        this.discriminatorColumn = intrinsicClass.getDiscriminatorColumn();
    }

    public JPAMFieldDefinition(Entity intrinsicClass, boolean inverse, boolean foriegnKey) {
        this.intrinsicClass = intrinsicClass != null && intrinsicClass.getOrignalObject() != null ? (Entity) intrinsicClass.getOrignalObject() : intrinsicClass;
        this.inverse = inverse;
        this.foriegnKey = foriegnKey;
    }

    public JPAMFieldDefinition(Entity intrinsicClass, Attribute managedAttribute, boolean inverse, boolean foriegnKey, boolean relationTable) {
//        if(intrinsicAttribute!=null){
        this.intrinsicClass = intrinsicClass != null && intrinsicClass.getOrignalObject() != null ? (Entity) intrinsicClass.getOrignalObject() : intrinsicClass;
//        }else {
//           this.intrinsicClass = null;
//       }
//        if(managedAttribute!=null){
        this.managedAttribute = managedAttribute != null && managedAttribute.getOrignalObject() != null ? (Attribute) managedAttribute.getOrignalObject() : managedAttribute;
//        } else {
//           this.managedAttribute = null;
//       }
        this.inverse = inverse;
        this.foriegnKey = foriegnKey;
        this.relationTable = relationTable;
        this.inherited = true;
    }

    /**
     * INTERNAL: Append the database field definition string to the table
     * creation statement.
     *
     * @param writer Target writer where to write field definition string.
     * @param session Current session context.
     * @param table Database table being processed.
     * @throws ValidationException When invalid or inconsistent data were found.
     */
    public void buildDBColumn(final DBTable table, final AbstractSession session,
            final JPAMTableDefinition tableDef) throws ValidationException {
        DBColumn column = null;
        if (discriminatorColumn != null) {
            column = new DBDiscriminatorColumn(name, discriminatorColumn);
        } else if (inherited) {
            if (managedAttribute instanceof RelationAttribute) {
                if (inverse) {
                    column = new DBParentAssociationInverseJoinColumn(name, intrinsicClass, (RelationAttribute) managedAttribute, relationTable);
                } else {
                    column = new DBParentAssociationJoinColumn(name, intrinsicClass, (RelationAttribute) managedAttribute, relationTable);
                }
//            } else if (foriegnKey) {
//                column = new DBEmbeddedAttributeJoinColumn(name, embeddedList, managedAttribute);
            } else {
                column = new DBParentAttributeColumn(name, intrinsicClass, managedAttribute);
            }
        } else if (foriegnKey && inverse && (intrinsicAttribute == null || intrinsicAttribute.isEmpty())) {//intrinsicAttribute will be null in case of JoinTableStrategy primary key mapping to children
            column = new DBPrimaryKeyJoinColumn(name, (Id) managedAttribute);
        } else if (intrinsicAttribute.size() == 1) {
            if (intrinsicAttribute.peek() instanceof RelationAttribute) {
                if (inverse) {
                    column = new DBInverseJoinColumn(name, (RelationAttribute) managedAttribute, relationTable);
                } else {
                    column = new DBJoinColumn(name, managedAttribute, relationTable);
                }
            } else if (intrinsicAttribute.peek() instanceof ElementCollection) {
                if (foriegnKey) {
                    column = new DBJoinColumn(name, managedAttribute, relationTable);
                } else {
                    column = new DBColumn(name, managedAttribute);
                }
            } else if (foriegnKey && inverse && intrinsicAttribute.peek() instanceof Id) {//PrimaryKeyJoinColumn
                column = new DBPrimaryKeyJoinColumn(name, (Id) managedAttribute);
            } else {
                column = new DBColumn(name, managedAttribute);
            }
        } else if (intrinsicAttribute.size() > 1) {
            if (intrinsicAttribute.get(0) instanceof RelationAttribute) { // nested derived Identity / shared relationship IdClass
                //Ex 3.a
                if (inverse) {
                    column = new DBInverseJoinColumn(name, (RelationAttribute) managedAttribute, relationTable);
                } else {
                    column = new DBJoinColumn(name, managedAttribute, relationTable);
                }
            } else if (intrinsicAttribute.get(0) instanceof Embedded) {
                List<Embedded> embeddedList = new ArrayList<>();
                for (int i = 0; i < intrinsicAttribute.size() - 1; i++) {
                    embeddedList.add((Embedded) intrinsicAttribute.get(i));
                }

                if (managedAttribute instanceof RelationAttribute) {
                    if (inverse) {
                        column = new DBEmbeddedAssociationInverseJoinColumn(name, embeddedList, (RelationAttribute) managedAttribute, relationTable);
                    } else {
                        column = new DBEmbeddedAssociationJoinColumn(name, embeddedList, (RelationAttribute) managedAttribute, relationTable);
                    }
                } else if (foriegnKey) {
                    column = new DBEmbeddedAttributeJoinColumn(name, embeddedList, managedAttribute);
                } else {
                    column = new DBEmbeddedAttributeColumn(name, embeddedList, managedAttribute);
                }
            } else if (intrinsicAttribute.get(0) instanceof EmbeddedId) {
                EmbeddedId embeddedId = (EmbeddedId) intrinsicAttribute.get(0);
                if (intrinsicAttribute.size() > 2 && ((DefaultAttribute) intrinsicAttribute.get(1)).isDerived() && ((DefaultAttribute) intrinsicAttribute.get(1)).getConnectedAttribute() instanceof RelationAttribute) {
                        column = new DBInverseJoinColumn(name, (RelationAttribute) ((DefaultAttribute) intrinsicAttribute.get(1)).getConnectedAttribute(), relationTable);//2.4.1.3 Example 2:b firstName, lastName
//                    column = new DBEmbeddedIdDerivedColumn(name, embeddedId, managedAttribute);
                } else {
                    if (((DefaultAttribute) managedAttribute).getConnectedAttribute() instanceof RelationAttribute) {
                        column = new DBInverseJoinColumn(name, (RelationAttribute) ((DefaultAttribute) managedAttribute).getConnectedAttribute(), relationTable);//2.4.1.3 Example 1:b empPK
                    } else if (((EmbeddedId) intrinsicAttribute.get(0)).getConnectedAttribute() instanceof RelationAttribute) {
                        column = new DBInverseJoinColumn(name, (RelationAttribute) ((EmbeddedId) intrinsicAttribute.get(0)).getConnectedAttribute(), relationTable);//2.4.1.3 Example 5:b id(firstName,lastName)
                    } else {
                        column = new DBColumn(name, managedAttribute);
//                        column = new DBEmbeddedIdAttributeColumn(name, embeddedId, managedAttribute);
                    }
                }
            }
        } 
        
        if(column == null) {
            column = new DBColumn(name, managedAttribute);
        }
        
        System.out.println("Name : "+ name + " - " + column.getClass());

        column.setId(NBModelerUtil.getAutoGeneratedStringId());

        if (getTypeDefinition() != null) { //apply user-defined complete type definition
            //TODO
        } else {
            final FieldTypeDefinition fieldType = type != null ? session.getPlatform().getFieldTypeDefinition(type) : new FieldTypeDefinition(typeName);
            if (fieldType == null) {
                throw ValidationException.javaTypeIsNotAValidDatabaseType(type);
            }
            column.setDataType(fieldType.getName());

            if ((fieldType.isSizeAllowed()) && ((this.getSize() != 0) || (fieldType.isSizeRequired()))) {
                if (this.getSize() == 0) {
                    column.setSize(fieldType.getDefaultSize());
                } else {
                    column.setSize(this.getSize());
                }
                if (this.getSubSize() != 0) {
                    column.setSubSize(this.getSubSize());
                } else if (fieldType.getDefaultSubSize() != 0) {
                    column.setSubSize(fieldType.getDefaultSubSize());
                }
            }

            if (shouldAllowNull && fieldType.shouldAllowNull()) {
                column.setAllowNull(true);
            } else {
                column.setAllowNull(false);
            }
        }

        column.setUniqueKey(isUnique());
        column.setPrimaryKey(isPrimaryKey() && session.getPlatform().supportsPrimaryKeyConstraint());

        table.addColumn(column);

    }

}
