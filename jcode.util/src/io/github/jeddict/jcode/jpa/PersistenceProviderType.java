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
package io.github.jeddict.jcode.jpa;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import org.netbeans.modeler.properties.type.Enumy;

/**
 *
 * @author jGauravGupta
 */
@XmlEnum(String.class)
public enum PersistenceProviderType implements Enumy {
    
    @XmlEnumValue("E") ECLIPSELINK("EclipseLink", "org.eclipse.persistence.jpa.PersistenceProvider"),
    @XmlEnumValue("H") HIBERNATE("Hibernate", "org.hibernate.jpa.HibernatePersistenceProvider");

    private final String persistenceProvider;
    private final String title;
    

    private PersistenceProviderType(String title, String persistenceProvider) {
        this.persistenceProvider = persistenceProvider;
        this.title = title;
    }

    /**
     * @return the persistenceProvider
     */
    public String getProviderClass() {
        return persistenceProvider;
    }

    @Override
    public String toString() {
        return title;
    }
    
    @Override
    public String getDisplay() {
        return title;
    }

    @Override
    public Enumy getDefault() {
        return null;
    }

}
