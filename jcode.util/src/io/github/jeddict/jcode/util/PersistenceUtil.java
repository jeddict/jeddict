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
package io.github.jeddict.jcode.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.maven.wagon.providers.http.commons.lang.StringUtils;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.persistence.dd.common.PersistenceUnit;
import org.netbeans.modules.j2ee.persistence.dd.common.Properties;
import org.netbeans.modules.j2ee.persistence.dd.common.Property;
import org.netbeans.modules.j2ee.persistence.provider.InvalidPersistenceXmlException;
import org.netbeans.modules.j2ee.persistence.provider.ProviderUtil;
import org.netbeans.modules.j2ee.persistence.unit.PUDataObject;
import org.openide.util.Exceptions;

/**
 *
 * @author jGauravGupta
 */
public class PersistenceUtil {

    public static Optional<PersistenceUnit> getPersistenceUnit(Project project, String puName) {
        PUDataObject pud;
        try {
            pud = ProviderUtil.getPUDataObject(project);
            return Arrays.stream(pud.getPersistence().getPersistenceUnit()).filter(persistenceUnit_In -> persistenceUnit_In.getName().equalsIgnoreCase(puName)).findFirst();
        } catch (InvalidPersistenceXmlException ex) {
            Exceptions.printStackTrace(ex);
            return Optional.empty();
        }
    }

    public static void removeProperty(PersistenceUnit punit, String key) {
        if (punit.getProperties() != null || punit.getProperties().getProperty2() != null) {
            Arrays.stream(punit.getProperties().getProperty2()).filter(p1 -> StringUtils.equals(p1.getName(), key))
                    .findAny().ifPresent(p1 -> punit.getProperties().removeProperty2(p1));
        }
    }

    public static void addProperty(PersistenceUnit punit, String key, String value) {
        Properties properties = punit.getProperties();
        if (properties == null) {
            properties = punit.newProperties();
            punit.setProperties(properties);
        }
        Property property = properties.newProperty();
        property.setName(key);
        property.setValue(value);

        Property existing = getProperty(properties.getProperty2(), key);

        if (existing != null) {
            existing.setValue(property.getValue());
        } else {
            properties.addProperty2(property);
        }
        System.out.println(Arrays.stream(punit.getProperties().getProperty2()).map(p -> p.getName() + '.' + p.getValue()).collect(Collectors.joining("|", "<", ">")));
    }

    /**
     * @return the property from the given properties whose name matches the
     * given propertyName or null if the given properties didn't contain
     * property with a matching name.
     */
    public static Property getProperty(Property[] properties, String propertyName) {

        if (null == properties) {
            return null;
        }

        for (int i = 0; i < properties.length; i++) {
            Property each = properties[i];
            if (each.getName() != null && each.getName().equals(propertyName)) {
                return each;
            }
        }

        return null;
    }

    public static void updatePersistenceUnit(Project project, PersistenceUnit persistenceUnit) {
        PUDataObject pud;
        try {
            pud = ProviderUtil.getPUDataObject(project);
            if (!Arrays.stream(pud.getPersistence().getPersistenceUnit()).filter(pu -> Objects.equals(pu, persistenceUnit)).findAny().isPresent()) {
                pud.addPersistenceUnit(persistenceUnit);
            }
            pud.modelUpdated();
            pud.save();
        } catch (InvalidPersistenceXmlException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static void addClasses(Project project, PersistenceUnit persistenceUnit, List<String> classNames) {
        try {
            PUDataObject pud = ProviderUtil.getPUDataObject(project);
            classNames.forEach((entityClass) -> {
                pud.addClass(persistenceUnit, entityClass, false);
            });
        } catch (InvalidPersistenceXmlException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
