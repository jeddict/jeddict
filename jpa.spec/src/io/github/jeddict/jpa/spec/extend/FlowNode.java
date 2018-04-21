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
package io.github.jeddict.jpa.spec.extend;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import org.netbeans.modeler.specification.model.document.core.IFlowNode;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class FlowNode extends BaseElement implements IFlowNode {

    @XmlAttribute(name = "m")
    private boolean minimized;

    @Override
    public List<String> getIncoming() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getOutgoing() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the visible
     */
    public boolean isMinimized() {
        return minimized;
    }

    /**
     * @param visible the visible to set
     */
    public void setMinimized(boolean visible) {
        this.minimized = visible;
    }

}
