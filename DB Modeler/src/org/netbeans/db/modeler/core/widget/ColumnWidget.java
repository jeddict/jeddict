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

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.netbeans.db.modeler.spec.Column;
import org.netbeans.db.modeler.specification.model.scene.DBModelerScene;
import org.netbeans.db.modeler.specification.model.util.DBModelerUtil;
import org.netbeans.jpa.modeler.core.widget.FlowPinWidget;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
public class ColumnWidget extends FlowPinWidget<Column, DBModelerScene> {

    private final List<ReferenceFlowWidget> referenceFlowWidget = new ArrayList<>();

//    private boolean selectedView;
    public ColumnWidget(DBModelerScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
        this.setImage(DBModelerUtil.COLUMN);
        this.addPropertyChangeListener("name", (PropertyChangeListener<String>) (String value) -> {
            if (value == null || value.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, NbBundle.getMessage(AttributeValidator.class, AttributeValidator.EMPTY_ATTRIBUTE_NAME));
                setName(ColumnWidget.this.getLabel());//rollback
            } else {
                setName(value);
                setLabel(value);
            }
        });

    }
    
    public void setDatatypeTooltip(){
        Column column = this.getBaseElementSpec();
        StringBuilder writer = new StringBuilder();
        writer.append(column.getDataType());
        if(column.getSize()!=0){writer.append('(').append(column.getSize()).append(')');}
        this.setToolTipText(writer.toString());
    }

    public static PinWidgetInfo create(String id, String name, IBaseElement baseElement) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id, baseElement);
        pinWidgetInfo.setName(name);
        pinWidgetInfo.setDocumentId(ColumnWidget.class.getSimpleName());
        return pinWidgetInfo;
    }

    @Override
    public boolean remove() {
        return remove(false);
    }

    @Override
    public boolean remove(boolean notification) {
        // Issue Fix #5855 Start
//        if (super.remove(notification)) {
//            getClassWidget().deleteAttribute(ColumnWidget.this);
//            return true;
//        }
        // Issue Fix #5855 End
        return false;
    }

    @Override
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.replaceAll("\\s+", "");
            getBaseElementSpec().setName(this.name);
        }
    }

    @Override
    public void setLabel(String label) {
        if (label != null && !label.trim().isEmpty()) {
            this.setPinName(label.replaceAll("\\s+", ""));
        }
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void destroy() {
    }

    public TableWidget getTableWidget() {
        return (TableWidget) this.getPNodeWidget();
    }

    public boolean addReferenceFlowWidget(ReferenceFlowWidget flowWidget) {
        return referenceFlowWidget.add(flowWidget);
    }

    public boolean removeReferenceFlowWidget(ReferenceFlowWidget flowWidget) {
        return referenceFlowWidget.remove(flowWidget);
    }
}
