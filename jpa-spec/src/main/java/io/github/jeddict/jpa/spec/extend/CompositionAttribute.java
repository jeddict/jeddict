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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import io.github.jeddict.jaxb.spec.JaxbVariableType;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class CompositionAttribute<T extends JavaClass> extends BaseAttribute implements AccessTypeHandler, AttributeOverrideHandler {

    @XmlAttribute(name = "connected-class-id", required = true)
    @XmlIDREF
    private T connectedClass;

    @Override
    public String getAttributeType() {
        if (connectedClass != null) {
            return connectedClass.getClazz();
        } else {
            return null;//TODO init called before initialization of connectedClass
        }
    }

    /**
     * @return the connectedClass
     */
    public T getConnectedClass() {
        return connectedClass;
    }

    /**
     * @param connectedClass the connectedClass to set
     */
    public void setConnectedClass(T connectedClass) {
        this.connectedClass = connectedClass;
    }

    @Override
    public List<JaxbVariableType> getJaxbVariableList() {
        List<JaxbVariableType> jaxbVariableTypeList = new ArrayList<>();
        jaxbVariableTypeList.add(JaxbVariableType.XML_DEFAULT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT_WRAPPER);
        jaxbVariableTypeList.add(JaxbVariableType.XML_TRANSIENT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT_REF);
        return jaxbVariableTypeList;
    }
}
