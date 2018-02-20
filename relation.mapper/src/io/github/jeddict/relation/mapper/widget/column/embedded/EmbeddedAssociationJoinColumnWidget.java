/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.relation.mapper.widget.column.embedded;

import java.util.List;
import io.github.jeddict.relation.mapper.spec.DBEmbeddedAssociationJoinColumn;
import io.github.jeddict.relation.mapper.spec.DBTable;
import io.github.jeddict.relation.mapper.initializer.RelationMapperScene;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.OneToMany;
import io.github.jeddict.jpa.spec.extend.Attribute;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;

public class EmbeddedAssociationJoinColumnWidget extends EmbeddedAssociationColumnWidget<DBEmbeddedAssociationJoinColumn> {

    public EmbeddedAssociationJoinColumnWidget(RelationMapperScene scene, IPNodeWidget nodeWidget, PinWidgetInfo pinWidgetInfo) {
        super(scene, nodeWidget, pinWidgetInfo);
    }

    public static PinWidgetInfo create(String id, String name, IBaseElement baseElement) {
        PinWidgetInfo pinWidgetInfo = new PinWidgetInfo(id, baseElement);
        pinWidgetInfo.setName(name);
        pinWidgetInfo.setDocumentId(EmbeddedAssociationJoinColumnWidget.class.getSimpleName());
        return pinWidgetInfo;
    }

    @Override
    protected boolean prePersistName() {
        Attribute attribute = this.getBaseElementSpec().getAttribute();
        if (attribute instanceof OneToMany && !this.getBaseElementSpec().isRelationTableExist()) {
            return false;//OneToMany by default creates JoinTable
        }
        return true;
    }

    @Override
    protected String evaluateName() {
        DBTable table = (DBTable) this.getTableWidget().getBaseElementSpec();
        Entity entity = table.getEntity();
        List<Id> id = (List<Id>) entity.getAttributes().getId();
        return entity.getDefaultTableName().toUpperCase() + "_" + id.get(0).getName().toUpperCase();
    }

//        @Override
//    protected List<JMenuItem> getPopupMenuItemList() {
//        List<JMenuItem> menuList = super.getPopupMenuItemList();
//        if (this.getTableWidget() instanceof BaseTableWidget) {
//            JMenuItem joinTable = new JMenuItem("Create Join Table");//, MICRO_DB);
//            joinTable.addActionListener((ActionEvent e) -> {
//                String joinTableName = JOptionPane.showInputDialog((Component)EmbeddedAssociationJoinColumnWidget.this.getModelerScene().getModelerPanelTopComponent(), "Please enter join table name");
//                convertToJoinTable(joinTableName);
//                ModelerFile parentFile = EmbeddedAssociationJoinColumnWidget.this.getModelerScene().getModelerFile().getParentFile();
//                JPAModelerUtil.openDBViewer(parentFile, (EntityMappings) parentFile.getModelerScene().getBaseElementSpec());
//            });
//            menuList.add(0, joinTable);
//        }
//        return menuList;
//    }
//
//    void convertToJoinTable(String name) {
//        DBEmbeddedAssociationJoinColumn joinColumn = this.getBaseElementSpec();
//        if (joinColumn.getAttribute() instanceof RelationAttribute) {
//            joinColumn.getJoinColumns().clear();
//            ((RelationAttribute) joinColumn.getAttribute()).getJoinTable().setName(name);
//        }
//    }
}
