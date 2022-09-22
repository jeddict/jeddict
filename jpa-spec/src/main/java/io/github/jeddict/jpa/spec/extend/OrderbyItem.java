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
package io.github.jeddict.jpa.spec.extend;

import io.github.jeddict.jpa.spec.OrderType;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class OrderbyItem {
    
    @XmlAttribute(name="c")
    private String property;//column, attribute
    @XmlAttribute(name="o")
    private OrderType orderType;

    public static Set<OrderbyItem> process(String value) {
        Set<OrderbyItem> processedList = new LinkedHashSet<>();
        String[] valueParts = value.trim().split(",");
        if (valueParts.length > 0) {
            for (String valuePart : valueParts) {
                String[] valueSubPart = valuePart.trim().split(" ");
                if (valueSubPart.length == 1) {
                    processedList.add(new OrderbyItem(valueSubPart[0], null));
                } else if (valueSubPart.length == 2) {
                    processedList.add(new OrderbyItem(valueSubPart[0], OrderType.valueOf(valueSubPart[1])));
                }
            }
        }
        return processedList;
    }

    public OrderbyItem() {
    }

    public OrderbyItem(String property) {
        this.property = property;
    }

    public OrderbyItem(String property, OrderType orderType) {
        this.property = property;
        this.orderType = orderType;
    }

    /**
     * @return the property
     */
    public String getProperty() {
        return property;
    }

    /**
     * @param property the property to set
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * @return the orderType
     */
    public OrderType getOrderType() {
        return orderType;
    }

    /**
     * @param orderType the orderType to set
     */
    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.property);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OrderbyItem other = (OrderbyItem) obj;
        if (!Objects.equals(this.property, other.property)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (orderType != null) {
            return property + " " + orderType.name();
        } else {
            return property;
        }
    }
    
}
