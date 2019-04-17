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
package io.github.jeddict.jpa.spec;

import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.OrderbyItem;
import io.github.jeddict.source.AnnotationExplorer;
import io.github.jeddict.source.MemberExplorer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import io.github.jeddict.util.StringUtils;

@XmlAccessorType(XmlAccessType.FIELD)
public class OrderBy {

    @XmlElement(name = "a")
    private List<OrderbyItem> attributes;

    public static OrderBy load(MemberExplorer member) {
        Optional<AnnotationExplorer> orderByOpt = member.getAnnotation(javax.persistence.OrderBy.class);
        if (orderByOpt.isPresent()) {
            OrderBy orderBy = new OrderBy();
            orderByOpt.get()
                    .getString("value")
                    .ifPresent(value -> orderBy.getAttributes().addAll(OrderbyItem.process(value)));
            return orderBy;
        }
        return null;
    }

    /**
     * @return the attributeList
     */
    public List<OrderbyItem> getAttributes() {
        if(attributes == null){
            attributes = new ArrayList<>();
        }
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(List<OrderbyItem> attributes) {
        this.attributes = attributes;
    }
    public void addAttribute(OrderbyItem orderbyItem){
        getAttributes().add(orderbyItem);
    }
    public void addAttribute(Attribute attribute, OrderType orderType){
        getAttributes().add(new OrderbyItem(attribute.getName(), orderType));
    }
    public void addAttribute(Attribute attribute){
        getAttributes().add(new OrderbyItem(attribute.getName(), null));
    }
    public boolean isExist(Attribute attribute) {
        return getAttributes().stream().filter(a -> StringUtils.equals(a.getProperty(),attribute.getName())).findAny().isPresent();
    }
    
    public boolean isExist(String prefix) {
        return getAttributes().stream().filter(a -> a.getProperty().startsWith(prefix + '.') || a.getProperty().equals(prefix)).findAny().isPresent();
    }
    
    public Optional<OrderType> getOrderType(String property) {
        return getAttributes().stream().filter(a -> a.getProperty().equals(property)).findAny().map(OrderbyItem::getOrderType);
    }
}
