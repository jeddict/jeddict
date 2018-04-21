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

import io.github.jeddict.jpa.spec.ForeignKey;

/**
 *
 * @author jGauravGupta
 */
public interface IJoinColumn {
      /**
     * Gets the value of the columnDefinition property.
     *
     * @return possible object is {@link String }
     *
     */
    String getColumnDefinition();

    /**
     * @return the foreignKey
     */
    ForeignKey getForeignKey();

    /**
     * @return the implicitName
     */
    String getImplicitName();

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    String getName();

    /**
     * Gets the value of the referencedColumnName property.
     *
     * @return possible object is {@link String }
     *
     */
    String getReferencedColumnName();

    /**
     * Sets the value of the columnDefinition property.
     *
     * @param value allowed object is {@link String }
     *
     */
    void setColumnDefinition(String value);

    /**
     * @param foreignKey the foreignKey to set
     */
    void setForeignKey(ForeignKey foreignKey);

    /**
     * @param implicitName the implicitName to set
     */
    void setImplicitName(String implicitName);

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     *
     */
    void setName(String value);

    /**
     * Sets the value of the referencedColumnName property.
     *
     * @param value allowed object is {@link String }
     *
     */
    void setReferencedColumnName(String value);
    
}
