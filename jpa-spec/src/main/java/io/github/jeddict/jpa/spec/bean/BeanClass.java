/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.spec.bean;

import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.spec.extend.ReferenceClass;
import io.github.jeddict.source.ClassExplorer;
import java.util.Optional;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "attributes"
})
public class BeanClass extends JavaClass<BeanAttributes> {

    @XmlElement(name = "attributes")
    private BeanAttributes attributes;

    @Override
    public void load(ClassExplorer clazz) {
        super.load(clazz);
        this.getAttributes().load(clazz);

        Optional<ResolvedReferenceTypeDeclaration> superClassTypeOpt = clazz.getSuperClass();
        if (superClassTypeOpt.isPresent()) {
            ResolvedReferenceTypeDeclaration superClassType = superClassTypeOpt.get();
            Optional<BeanClass> superClassOpt = clazz.getSource().findBeanClass(superClassType);
            if (superClassOpt.isPresent()) {
                super.addSuperclass(superClassOpt.get());
            } else {
                this.setSuperclassRef(new ReferenceClass(superClassType.getQualifiedName()));
            }
        }
    }

    public BeanClass() {
    }

    public BeanClass(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public BeanAttributes getAttributes() {
        if (attributes == null) {
            attributes = new BeanAttributes();
            attributes.setJavaClass(this);
        }
        return attributes;
    }

    @Override
    public void setAttributes(BeanAttributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return clazz;
    }

    @Override
    public void setName(String name) {
        setClazz(name);
    }

}
