/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jpa.modeler.properties.rootmember.nodes;

import org.netbeans.jpa.modeler.navigator.nodes.CheckableAttributeNode;
import org.netbeans.jpa.modeler.navigator.nodes.LeafNode;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import org.netbeans.modeler.specification.model.document.IModelerScene;

public class EMLeafNode extends LeafNode<EntityMappings> {

    private final JavaClass javaClass;

    public EMLeafNode(JavaClass javaClass, IModelerScene scene, EntityMappings entityMappings, CheckableAttributeNode checkableNode) {
        super(scene, entityMappings, checkableNode);
        this.javaClass = javaClass;
    }

    @Override
    public void init() {
        getCheckableNode().setEnableWithParent(true);
        
        this.setIconBaseWithExtension(JPAModelerUtil.getBaseElementIcon(getJavaClass().getClass()));
//        JavaClass javaClass = (JavaClass) leafClass.getBaseElementSpec();
        this.setShortDescription(getJavaClass().getClazz());
    }

    private String htmlDisplayName;
    @Override
    public String getHtmlDisplayName() {
        if (htmlDisplayName == null) {
//            JavaClass javaClass = (JavaClass) leafClass.getBaseElementSpec();
            htmlDisplayName = getJavaClass().getClazz();
        }
        if (getCheckableNode() != null && !getCheckableNode().isSelected()) {
            return String.format("<font color=\"#969696\">%s</font>", htmlDisplayName); //NOI18N
        } else {
            return String.format("<font color=\"#0000E6\">%s</font>", htmlDisplayName); //NOI18N
        }
    }

    /**
     * @return the javaClass
     */
    public JavaClass getJavaClass() {
        return javaClass;
    }
    
}
