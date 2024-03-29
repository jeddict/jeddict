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

import java.util.List;
import io.github.jeddict.jpa.spec.EmbeddedId;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.Version;
import java.util.Optional;

/**
 *
 * @author Gaurav_Gupta
 */
public interface IPrimaryKeyAttributes extends IPersistenceAttributes {

    /**
     * Gets the value of the description property.
     *
     * @return possible object is {@link String }
     *
     */
    String getDescription();

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is {@link String }
     *
     */
    void setDescription(String value);

    /**
     * Gets the value of the embeddedId property.
     *
     * @return possible object is {@link EmbeddedId }
     *
     */
    EmbeddedId getEmbeddedId();

    /**
     * Sets the value of the embeddedId property.
     *
     * @param value allowed object is {@link EmbeddedId }
     *
     */
    void setEmbeddedId(EmbeddedId value);

    /**
     * Gets the value of the id property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the id property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getId().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Id }
     *
     *
     */
    List<Id> getId();

    /**
     * Gets the value of the version property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the version property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVersion().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Version }
     *
     *
     */
    List<Version> getVersion();

    void addVersion(Version version);

    void removeVersion(Version version);

    Optional<Id> findId(String name);

    void addId(Id id);

    void removeId(Id id);

    Attribute getIdField();
    
    List<Id> getSuperId();
    
    List<Attribute> getPrimaryKeyAttributes();
    
    List<Version> getSuperVersion();
}
