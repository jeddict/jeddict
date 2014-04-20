/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.spec.extend;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class JavaClass extends FlowNode {

    @XmlAttribute
    private String superclass;
    @XmlAttribute
    private String superclassId;

    public abstract String getClazz();

    public abstract void setClazz(String value);

    public abstract IAttributes getAttributes();

    public abstract void setAttributes(IAttributes attributes);

    /**
     * @return the superclass
     */
    public String getSuperclass() {
        return superclass;
    }

    /**
     * @param superclass the superclass to set
     */
    public void setSuperclass(String superclass) {
        this.superclass = superclass;
    }

    /**
     * @return the superclassId
     */
    public String getSuperclassId() {
        return superclassId;
    }

    /**
     * @param superclassId the superclassId to set
     */
    public void setSuperclassId(String superclassId) {
        this.superclassId = superclassId;
    }

}
