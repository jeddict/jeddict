/**
 * Copyright [2013] Gaurav Gupta
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
package org.netbeans.jpa.modeler.core.widget;

import java.awt.Image;
import org.netbeans.jpa.modeler.spec.PrimaryKeyAttributes;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;

/**
 *
 * @author Gaurav_Gupta
 */
public class MappedSuperclassWidget extends PrimaryKeyContainerWidget<MappedSuperclass> {

    public MappedSuperclassWidget(JPAModelerScene scene, NodeWidgetInfo nodeWidgetInfo) {
        super(scene, nodeWidgetInfo);
    }

    @Override
    public void init() {
        super.init();
        MappedSuperclass mappedSuperclass = this.getBaseElementSpec();
        if (mappedSuperclass.getAttributes() == null) {
            mappedSuperclass.setAttributes(new PrimaryKeyAttributes());
        }
        if (mappedSuperclass.getClazz() == null || mappedSuperclass.getClazz().isEmpty()) {
            mappedSuperclass.setClazz(this.getModelerScene().getNextClassName("MappedSuperclass_"));
        }
        setName(mappedSuperclass.getClazz());
        setLabel(mappedSuperclass.getClazz());
        validateName(null, this.getName());

    }
    
    @Override
    public InheritanceStateType getInheritanceState() {
        return getInheritanceState(false);
    }
    
    @Override
    public InheritanceStateType getInheritanceState(boolean includeAllClass){
        return InheritanceStateType.NONE;
    }
    

    @Override
    public String getIconPath() {
        return JPAModelerUtil.MAPPED_SUPER_CLASS_ICON_PATH;
    }

    @Override
    public Image getIcon() {
        return JPAModelerUtil.MAPPED_SUPER_CLASS;
    }
}
