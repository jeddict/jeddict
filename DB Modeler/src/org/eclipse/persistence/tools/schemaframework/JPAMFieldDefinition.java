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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.netbeans.db.modeler.spec.DBColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedAssociationInverseJoinColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedAssociationJoinColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedAttributeColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedAttributeJoinColumn;
import org.netbeans.db.modeler.spec.DBInverseJoinColumn;
import org.netbeans.db.modeler.spec.DBJoinColumn;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.modeler.core.NBModelerUtil;

public class JPAMFieldDefinition extends FieldDefinition {

    private final LinkedList<Attribute> intrinsicAttribute = new LinkedList<>();
    private final Attribute managedAttribute;
    private final boolean inverse;
    private final boolean foriegnKey;
    private final boolean relationTable; 
    
    

    public JPAMFieldDefinition(LinkedList<Attribute> intrinsicAttribute, Attribute managedAttribute, boolean inverse, boolean foriegnKey, boolean relationTable) {
        intrinsicAttribute.stream().forEach((attr) -> {
            if(attr!=null && attr.getOrignalObject()!=null){
                this.intrinsicAttribute.add((Attribute)attr.getOrignalObject());
            } else {
                this.intrinsicAttribute.add(attr);
            }
        });
        this.managedAttribute = managedAttribute!=null&&managedAttribute.getOrignalObject()!=null ? (Attribute)managedAttribute.getOrignalObject(): managedAttribute;
        this.inverse = inverse;
        this.foriegnKey = foriegnKey;
        this.relationTable=relationTable;
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
        if (intrinsicAttribute.size() == 1) {
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
            } else {
                column = new DBColumn(name, managedAttribute);
            }
        } else if (intrinsicAttribute.size() > 1) {
            List<Embedded> embeddedList = new ArrayList<>();
            for (int i = 0; i < intrinsicAttribute.size()-1; i++) {
                embeddedList.add((Embedded)intrinsicAttribute.get(i));
            }
            
            if (managedAttribute instanceof RelationAttribute) {
                if (inverse) {
                    column = new DBEmbeddedAssociationInverseJoinColumn(name, embeddedList, (RelationAttribute)managedAttribute, relationTable);
                } else {
                    column = new DBEmbeddedAssociationJoinColumn(name, embeddedList, (RelationAttribute)managedAttribute, relationTable);
                }
            } else if (foriegnKey) {
                column = new DBEmbeddedAttributeJoinColumn(name, embeddedList, managedAttribute);
            } else {
                column = new DBEmbeddedAttributeColumn(name, embeddedList, managedAttribute);
            }
        } 

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
                // NULL
            } else {
                //NOT NULL
            }
        }
        column.setPrimaryKey(isPrimaryKey && session.getPlatform().supportsPrimaryKeyConstraint());

        table.addColumn(column);

    }

}
