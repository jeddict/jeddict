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
package org.netbeans.db.modeler.core.widget;

import org.apache.commons.lang.StringUtils;
import org.netbeans.db.modeler.spec.DBEmbeddedAssociationColumn;
import org.netbeans.db.modeler.spec.DBEmbeddedColumn;
import org.netbeans.db.modeler.spec.DBTable;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;

public class EmbeddedAssociationColumnWidget<E extends DBEmbeddedAssociationColumn> extends ForeignKeyWidget<E> {

    public EmbeddedAssociationColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.addPropertyChangeListener("column_name", (PropertyChangeListener<String>) (String value) -> {
            setDefaultName();
        });

        this.addPropertyChangeListener("attr_override_column_name", (PropertyChangeListener<String>) (String value) -> {
            setDefaultName();
        });

        PropertyChangeListener propertyChangeListener = (PropertyChangeListener<String>) (String tableName) -> {
            if (tableName != null && !tableName.trim().isEmpty()) {
                if (SQLKeywords.isSQL99ReservedKeyword(tableName)) {
                    errorHandler.throwError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                } else {
                    errorHandler.clearError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
                }
            } else {
                errorHandler.clearError(AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
            }
        };
        this.addPropertyChangeListener("table_name", propertyChangeListener);
        this.addPropertyChangeListener("attr_override_table_name", propertyChangeListener);
    }

    public static PinWidgetInfo create(String id, String name, IBaseElement baseElement) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id, baseElement);
        pinWidgetInfo.setName(name);
        pinWidgetInfo.setDocumentId(EmbeddedAssociationColumnWidget.class.getSimpleName());
        return pinWidgetInfo;
    }

    private String evaluateLabel() {
        AssociationOverride associationOverride = this.getBaseElementSpec().getAssociationOverride();
        Column embeddableColumn = null;
        Attribute refAttribute = ((DBEmbeddedColumn) this.getBaseElementSpec()).getAttribute();
        PersistenceBaseAttribute baseRefAttribute = null;
        if (refAttribute instanceof PersistenceBaseAttribute) {
            baseRefAttribute = (PersistenceBaseAttribute) refAttribute;
            embeddableColumn = baseRefAttribute.getColumn();
        }

//        if (StringUtils.isNotBlank(associationOverride.getColumn().getName())) {
//            return associationOverride.getColumn().getName();
//        } else if (StringUtils.isNotBlank(embeddableColumn.getName())) {
//            return embeddableColumn.getName();
//        } else {
            return baseRefAttribute.getDefaultColumnName();
//        }

    }

    private void setDefaultName() {
        this.name = evaluateLabel();

        if (SQLKeywords.isSQL99ReservedKeyword(EmbeddedAssociationColumnWidget.this.getName())) {
            this.getErrorHandler().throwError(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
        } else {
            this.getErrorHandler().clearError(AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
        }

        DBTable tableSpec = (DBTable) this.getTableWidget().getBaseElementSpec();
        if (tableSpec.findColumns(this.getName()).size() > 1) {
            errorHandler.throwError(AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
        } else {
            errorHandler.clearError(AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
        }
        setLabel(name);
    }

    @Override
    public void setName(String name) {
        if (StringUtils.isNotBlank(name)) {
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                AssociationOverride associationOverride = this.getBaseElementSpec().getAssociationOverride();
//                attributeOverride.getColumn().setName(this.name);
            }
        } else {
            AssociationOverride associationOverride = this.getBaseElementSpec().getAssociationOverride();
//            attributeOverride.getColumn().setName(null);
        }
        setDefaultName();
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        Attribute refAttribute = ((DBEmbeddedColumn) this.getBaseElementSpec()).getAttribute();
        if (refAttribute instanceof PersistenceBaseAttribute) {
            PersistenceBaseAttribute baseRefAttribute = (PersistenceBaseAttribute) refAttribute;
            if (baseRefAttribute.getColumn() == null) {
                baseRefAttribute.setColumn(new Column());
            }
//            set.createPropertySet("EMBEDDABLE_COLUMN", this, baseRefAttribute.getColumn(), getPropertyChangeListeners());
        }
//        AssociationOverride associationOverride = this.getBaseElementSpec().getAssociationOverride();
//        set.createPropertySet("ATTRIBUTE_OVERRIDE", "ATTR_OVERRIDE", this, attributeOverride.getColumn(), getPropertyChangeListeners());
    }


}
