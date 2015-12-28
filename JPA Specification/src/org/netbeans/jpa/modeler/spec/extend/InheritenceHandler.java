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

import org.netbeans.jpa.modeler.spec.DiscriminatorColumn;
import org.netbeans.jpa.modeler.spec.Inheritance;

/**
 *
 * @author Gaurav Gupta
 */
public interface InheritenceHandler {

    /**
     * Gets the value of the inheritance property.
     *
     * @return possible object is {@link Inheritance }
     *
     */
    public Inheritance getInheritance();

    /**
     * Sets the value of the inheritance property.
     *
     * @param value allowed object is {@link Inheritance }
     *
     */
    public void setInheritance(Inheritance value);

    /**
     * Gets the value of the discriminatorValue property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDiscriminatorValue();

    /**
     * Sets the value of the discriminatorValue property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDiscriminatorValue(String value);

    /**
     * Gets the value of the discriminatorColumn property.
     *
     * @return possible object is {@link DiscriminatorColumn }
     *
     */
    public DiscriminatorColumn getDiscriminatorColumn();

    /**
     * Sets the value of the discriminatorColumn property.
     *
     * @param value allowed object is {@link DiscriminatorColumn }
     *
     */
    public void setDiscriminatorColumn(DiscriminatorColumn value);
}
