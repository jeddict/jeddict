/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import io.github.jeddict.source.MemberExplorer;
import java.util.Optional;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "many-to-one-assoc")
@XmlRootElement
public class ManyToOneAssociation extends SingleAssociationAttribute {

    public static ManyToOneAssociation load(MemberExplorer member) {
        ManyToOneAssociation attribute = new ManyToOneAssociation();
        attribute.loadAttribute(member);

        if (!member.getTypeDeclaration().isPresent()) {
            return null;
        }
        Optional<BeanClass> beanClassOpt = member.getSource().findBeanClass(member.getTypeDeclaration().get());
        if (!beanClassOpt.isPresent()) {
            return null;
        }
        attribute.setConnectedClass(beanClassOpt.get());
        return attribute;
    }

}
