/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.db.modeler.widget;

import io.github.jeddict.db.modeler.spec.DBColumn;
import io.github.jeddict.db.modeler.spec.DBTable;
import io.github.jeddict.db.modeler.initializer.DBModelerScene;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.COLUMN;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.COLUMN_ICON_PATH;
import io.github.jeddict.relation.mapper.widget.api.IColumnWidget;
import java.awt.Image;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JMenuItem;
import io.github.jeddict.util.StringUtils;
import io.github.jeddict.jpa.modeler.widget.FlowPinWidget;
import io.github.jeddict.jpa.modeler.rules.attribute.AttributeValidator;
import io.github.jeddict.jpa.modeler.rules.entity.SQLKeywords;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import static org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType.ERROR;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class ColumnWidget<E extends DBColumn> extends FlowPinWidget<E, DBModelerScene> implements IColumnWidget {

    private final List<ReferenceFlowWidget> referenceFlowWidget = new ArrayList<>();

    public ColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setImage(getIcon());
        getPinNameWidget().getActions().removeAction(editAction);
    }

    public void setDatatypeTooltip() {
        DBColumn column = this.getBaseElementSpec();
        StringBuilder writer = new StringBuilder();
        writer.append(column.getTypeName());
        if (column.getLength() != 0) {
            writer.append('(').append(column.getLength()).append(')');
        }
        this.setToolTipText(writer.toString());
    }

    @Override
    public void setLabel(String label) {
        if (label != null && !label.trim().isEmpty()) {
            this.setPinName(label.replaceAll("\\s+", ""));
        }
    }

    @Override
    public void init() {
        validateName(this.getName());
        setDatatypeTooltip();
    }

    @Override
    public void destroy() {
    }

    public TableWidget getTableWidget() {
        return (TableWidget) this.getPNodeWidget();
    }

    public boolean addReferenceFlowWidget(ReferenceFlowWidget flowWidget) {
        return getReferenceFlowWidget().add(flowWidget);
    }

    public boolean removeReferenceFlowWidget(ReferenceFlowWidget flowWidget) {
        return getReferenceFlowWidget().remove(flowWidget);
    }

    @Override
    public ContextPaletteModel getContextPaletteModel() {
        return null;
    }

    /**
     * @return the referenceFlowWidget
     */
    public List<ReferenceFlowWidget> getReferenceFlowWidget() {
        return referenceFlowWidget;
    }

    @Override
    public void setName(String name) {
        if (StringUtils.isNotBlank(name)) {
            this.name = name.replaceAll("\\s+", "");
            updateName(this.name);
        } else {
            setDefaultName();
        }
        validateName(this.name);
    }

    /**
     * Called when developer delete value
     */
    protected void setDefaultName() {
        this.name = evaluateName();
        updateName(null);
        setLabel(name);
    }

    private String evaluateName() {
        return ((DBColumn) this.getBaseElementSpec()).getColumn().getName();
    }

    private void updateName(String newName) {
        if (this.getModelerScene().getModelerFile().isLoaded()) {
            //todo  
        }
    }

    protected void validateName(String name) {
        if (SQLKeywords.isSQL99ReservedKeyword(name)) {
            getSignalManager().fire(ERROR, AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
        } else {
            getSignalManager().clear(ERROR, AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
        }

        DBTable tableSpec = this.getTableWidget().getBaseElementSpec();
        if (tableSpec.findColumns(name).size() > 1) {
            getSignalManager().fire(ERROR, AttributeValidator.NON_UNIQUE_COLUMN_NAME);
        } else {
            getSignalManager().clear(ERROR, AttributeValidator.NON_UNIQUE_COLUMN_NAME);
        }
    }

    protected void validateTableName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            if (SQLKeywords.isSQL99ReservedKeyword(name)) {
                getSignalManager().fire(ERROR, AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
            } else {
                getSignalManager().clear(ERROR, AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
            }
        } else {
            getSignalManager().clear(ERROR, AttributeValidator.ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD);
        }
    }

    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuItemList = new LinkedList<>();
        menuItemList.add(getPropertyMenu());
        return menuItemList;
    }

    @Override
    public String getIconPath() {
        return COLUMN_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        return COLUMN;
    }

}
