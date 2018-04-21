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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import io.github.jeddict.jpa.spec.OrderType;

@XmlAccessorType(XmlAccessType.FIELD)
public class OrderbyItem {
    
    @XmlAttribute(name="c")
    private String property;//column, attribute
    @XmlAttribute(name="o")
    private OrderType orderType;

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
