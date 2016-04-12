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
package org.netbeans.jpa.modeler.spec.validation.constraints;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gaurav Gupta
 */
@XmlRootElement(name="di")
public class Digits extends Constraints {

    @XmlAttribute(name="f")
    private Integer fraction ;

    @XmlAttribute(name="i")
    private Integer integer;

    /**
     * @return the fraction
     */
    public Integer getFraction() {
        return fraction;
    }

    /**
     * @param fraction the fraction to set
     */
    public void setFraction(Integer fraction) {
        this.fraction = fraction;
    }

    /**
     * @return the integer
     */
    public Integer getInteger() {
        return integer;
    }

    /**
     * @param integer the integer to set
     */
    public void setInteger(Integer integer) {
        this.integer = integer;
    }

}