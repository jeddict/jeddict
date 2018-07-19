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
package io.github.jeddict.jpa.spec;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;
import io.github.jeddict.db.accessor.DefaultAttributeSpecAccessor;
import io.github.jeddict.db.accessor.DefaultEmbeddedAttributeSpecAccessor;
import io.github.jeddict.jpa.spec.extend.DefaultAttributes;
import io.github.jeddict.jpa.spec.extend.JavaClass;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "class", propOrder = {
    "embeddable",
    "description",
    "attributes"
})
public class DefaultClass extends JavaClass<DefaultAttributes> {

    private boolean embeddable;

    @XmlElement(name = "attrs")
    private DefaultAttributes attributes;

    public DefaultClass() {
    }

    public DefaultClass(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public DefaultAttributes getAttributes() {
        if (attributes == null) {
            attributes = new DefaultAttributes();
            attributes.setJavaClass(this);
        }
        return attributes;
    }

    @Override
    public void setAttributes(DefaultAttributes attributes) {
        this.attributes = attributes;
    }

    /**
     * @return the embeddable
     */
    public boolean isEmbeddable() {
        return embeddable;
    }

    /**
     * @param embeddable the embeddable to set
     */
    public void setEmbeddable(boolean embeddable) {
        this.embeddable = embeddable;
    }

    @Override
    public String getName() {
        return clazz;
    }

    @Override
    public void setName(String name) {
        this.clazz = clazz;
    }

    public XMLAttributes getAccessor() {
        XMLAttributes attr = new XMLAttributes();
        attr.setBasicCollections(new ArrayList<>());
        attr.setBasicMaps(new ArrayList<>());
        attr.setTransformations(new ArrayList<>());
        attr.setVariableOneToOnes(new ArrayList<>());
        attr.setStructures(new ArrayList<>());
        attr.setArrays(new ArrayList<>());
        attr.setBasics(new ArrayList<>());
        attr.setElementCollections(new ArrayList<>());
        attr.setEmbeddeds(new ArrayList<>());
        attr.setTransients(new ArrayList<>());
        attr.setManyToManys(new ArrayList<>());
        attr.setManyToOnes(new ArrayList<>());
        attr.setOneToManys(new ArrayList<>());
        attr.setOneToOnes(new ArrayList<>());
        attr.setIds(new ArrayList<>());
        attr.setVersions(new ArrayList<>());
        return updateAccessor(attr);
    }

    public XMLAttributes updateAccessor(XMLAttributes attr) {
        for (DefaultAttribute attribute : getAttributes().getDefaultAttributes()) {
            if (attribute.isDerived()) {
                attr.getEmbeddeds().add(DefaultEmbeddedAttributeSpecAccessor.getInstance(attribute, false));
            } else {
                attr.getBasics().add(DefaultAttributeSpecAccessor.getInstance(attribute, false));
            }
        }
        return attr;
    }

}
