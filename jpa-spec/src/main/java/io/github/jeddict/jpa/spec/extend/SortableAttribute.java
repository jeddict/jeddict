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

import io.github.jeddict.jpa.spec.OrderBy;
import io.github.jeddict.jpa.spec.OrderColumn;

/**
 *
 * @author jGauravGupta
 */
public interface SortableAttribute {

    /**
     * Gets the value of the orderBy property.
     *
     * @return possible object is {@link String }
     *
     */
    public OrderBy getOrderBy();

    /**
     * Sets the value of the orderBy property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setOrderBy(OrderBy value);

    /**
     * Gets the value of the orderColumn property.
     *
     * @return possible object is {@link OrderColumn }
     *
     */
    public OrderColumn getOrderColumn();

    /**
     * Sets the value of the orderColumn property.
     *
     * @param value allowed object is {@link OrderColumn }
     *
     */
    public void setOrderColumn(OrderColumn value);
}
