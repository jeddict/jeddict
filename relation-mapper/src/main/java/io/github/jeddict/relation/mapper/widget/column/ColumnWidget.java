/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.relation.mapper.widget.column;

import io.github.jeddict.jpa.modeler.rules.attribute.AttributeValidator;
import io.github.jeddict.jpa.modeler.rules.entity.SQLKeywords;
import io.github.jeddict.jpa.modeler.widget.FlowPinWidget;
import io.github.jeddict.relation.mapper.initializer.RelationMapperScene;
import io.github.jeddict.relation.mapper.initializer.RelationMapperUtil;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.COLUMN;
import static io.github.jeddict.relation.mapper.initializer.RelationMapperUtil.COLUMN_ICON_PATH;
import io.github.jeddict.relation.mapper.spec.DBColumn;
import io.github.jeddict.relation.mapper.spec.DBTable;
import io.github.jeddict.relation.mapper.widget.api.IColumnWidget;
import io.github.jeddict.relation.mapper.widget.flow.ReferenceFlowWidget;
import io.github.jeddict.relation.mapper.widget.table.TableWidget;
import io.github.jeddict.settings.diagram.ClassDiagramSettings;
import io.github.jeddict.settings.view.AttributeViewAs;
import java.awt.Image;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JMenuItem;
import io.github.jeddict.util.StringUtils;
import org.netbeans.modeler.widget.context.ContextPaletteModel;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import static org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType.ERROR;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class ColumnWidget<E extends DBColumn> extends FlowPinWidget<E, RelationMapperScene> implements IColumnWidget {

    private final List<ReferenceFlowWidget> referenceFlowWidget = new ArrayList<>();

    public ColumnWidget(RelationMapperScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setImage(RelationMapperUtil.COLUMN);
    }

    public void visualizeDataType() {
        AttributeViewAs viewAs = ClassDiagramSettings.getDataType();
        DBColumn column = this.getBaseElementSpec();
        
        String dataType = column.getDataType();
        if (null != viewAs) switch (viewAs) {
            case CLASS_FQN:
            case SIMPLE_CLASS_NAME:
                dataType = dataType + "(" + column.getSize()+ ")";
                break;
        //skip
            case SHORT_CLASS_NAME:
                break;
            case NONE:
                return;
            default:
                break;
        }
        
        visualizeDataType(dataType);
    }

    public void setDatatypeTooltip() {
        DBColumn column = this.getBaseElementSpec();
        StringBuilder writer = new StringBuilder();
        writer.append(column.getDataType());
        if (column.getSize() != 0) {
            writer.append('(').append(column.getSize()).append(')');
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
        visualizeDataType();
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
            if (this.getModelerScene().getModelerFile().isLoaded()) {
                updateName(this.name);
            }
        } else {
            setDefaultName();
        }
        validateName(this.name);
    }

    /**
     * Called when developer delete value
     */
    protected void setDefaultName() {
        if (!prePersistName()) {
            return;
        }
        this.name = evaluateName();
        if (this.getModelerScene().getModelerFile().isLoaded()) {
            updateName(null);
        }
        setLabel(name);
    }

    abstract protected String evaluateName();

    abstract protected void updateName(String newName);

    /**
     * Listener called before persistence event of the name, useful in case to
     * skip process
     */
    protected boolean prePersistName() {
        return true;
    }

    /**
     * Called when value changed by property panel Override it if multiple name
     * property available
     */
    protected void setPropertyName(String name) {
        if (!prePersistName()) {
            return;
        }
        this.name = name;
        validateName(name);
        setLabel(name);
    }

    protected void setMultiPropertyName(String name) {
        if (!prePersistName()) {
            return;
        }
        this.name = evaluateName();
        validateName(name);
        setLabel(name);
    }

    protected void validateName(String name) {
        if (SQLKeywords.isSQL99ReservedKeyword(name)) {
            getSignalManager().fire(ERROR, AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
        } else {
            getSignalManager().clear(ERROR, AttributeValidator.ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD);
        }

        DBTable tableSpec = (DBTable) this.getTableWidget().getBaseElementSpec();
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
