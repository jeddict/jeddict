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
package org.netbeans.jpa.modeler.widget.connection.relation;

import java.util.List;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.MappedSuperclassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.modeler.widget.connection.relation.IRelationProxy;
import org.netbeans.modeler.widget.connection.relation.IRelationValidator;

/**
 * RelationValidator manages the dispatch of relation validation events, while
 * also having the meta layer validate the proposed relationship against rules.
 */
public class RelationValidator implements IRelationValidator {

    /**
     *
     */
    /**
     *
     * Called to validate the proposed relationship. Calling this method will
     * result in the firing of the IRelationValidatorEventsSink methods.
     *
     * @param proxy[in] The proxy to validate
     *
     * @return HRESULT
     *
     */
    @Override
    public boolean validateRelation(IRelationProxy proxy) {
        boolean valid = true;
//        if (proxy.getTarget() == proxy.getSource()) {
//            valid = false;
//        } else

        if (proxy.getEdgeType().contains("RELATION") && (proxy.getEdgeType().charAt(0) == 'U' || proxy.getEdgeType().charAt(0) == 'B')) {
            if (proxy.getTarget() instanceof MappedSuperclassWidget) {
                return false;
            } else if (proxy.getTarget() instanceof EmbeddableWidget && proxy.getEdgeType().contains("RELATION")) {
                return false;
            }
        } else if (proxy.getEdgeType().equals("GENERALIZATION")) {
            if (!(proxy.getSource() instanceof JavaClassWidget) || !(proxy.getTarget() instanceof JavaClassWidget)) {
                return false;

            }
            //Prevent Cyclic inheritence
            JavaClassWidget sourceJavaClassWidget = (JavaClassWidget) proxy.getSource();
            JavaClassWidget targetJavaClassWidget = (JavaClassWidget) proxy.getTarget();
            List<JavaClassWidget> targetSuperclassWidgetList = targetJavaClassWidget.getAllSuperclassWidget();
            if (sourceJavaClassWidget.getOutgoingGeneralizationFlowWidget() != null) {
                return false;
            } else if (targetSuperclassWidgetList.size() > 0 && targetSuperclassWidgetList.contains(sourceJavaClassWidget)) {
                return false;
            }
        } else if (proxy.getEdgeType().equals("SINGLE_EMBEDDABLE_RELATION") || proxy.getEdgeType().equals("MULTI_EMBEDDABLE_RELATION")) {
            if (!(proxy.getSource() instanceof PersistenceClassWidget) || !(proxy.getTarget() instanceof EmbeddableWidget)) {
                return false;
            }
//            //Prevent Cyclic inheritence
//            JavaClassWidget sourceJavaClassWidget = (JavaClassWidget) proxy.getSource();
//            EmbeddableWidget targetEmbeddableWidget = (EmbeddableWidget) proxy.getTarget();
//            List<JavaClassWidget> targetContainerclassWidgetList = null ;//= targetEmbeddableWidget.getAllContainerclassWidget();
//            if (sourceJavaClassWidget.getOutgoingGeneralizationFlowWidget() != null) {
//                valid = false;
//            } else if (targetContainerclassWidgetList.size() > 0 && targetContainerclassWidgetList.contains(sourceJavaClassWidget)) {
//                valid = false;
//            }

        }

        return valid;
    }
}
