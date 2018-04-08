/**
 * Copyright [2014-2018] Gaurav Gupta
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
package io.github.jeddict.orm.generator.service;

import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.BOLD;
import static io.github.jeddict.jcode.console.Console.FG_DARK_RED;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import io.github.jeddict.jcode.jpa.PersistenceProviderType;
import io.github.jeddict.jcode.task.ITaskSupervisor;
import static io.github.jeddict.jpa.util.PersistenceHelper.JTA_VALUE;
import static io.github.jeddict.jpa.util.PersistenceHelper.RESOURCE_LOCAL_VALUE;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.orm.generator.IPersistenceXMLGenerator;
import org.netbeans.modules.j2ee.persistence.dd.common.Persistence;
import org.netbeans.modules.j2ee.persistence.dd.common.PersistenceUnit;
import org.netbeans.modules.j2ee.persistence.dd.common.Properties;
import org.netbeans.modules.j2ee.persistence.dd.common.Property;
import static org.netbeans.modules.j2ee.persistence.provider.Provider.TABLE_GENERATION_CREATE;
import org.netbeans.modules.j2ee.persistence.provider.ProviderUtil;
import org.netbeans.modules.j2ee.persistence.unit.PUDataObject;
import org.netbeans.modules.j2ee.persistence.wizard.Util;
import io.github.jeddict.orm.generator.util.ORMConvLogger;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IPersistenceXMLGenerator.class)
public class PersistenceXMLGenerator implements IPersistenceXMLGenerator{

    private static final Logger LOGGER = ORMConvLogger.getLogger(PersistenceXMLGenerator.class);

    //Reference : org.netbeans.modules.j2ee.persistence.wizard.unit.PersistenceUnitWizard.instantiateWProgress
    @Override
    public void generatePersistenceXML(
            ITaskSupervisor task,
            Project project, 
            SourceGroup sourceGroup, 
            EntityMappings entityMappings, 
            List<String> classNames) {
        String puName = entityMappings.getPersistenceUnitName();
        String puProvider = entityMappings.getPersistenceProviderType()!=null?entityMappings.getPersistenceProviderType().getProviderClass():PersistenceProviderType.ECLIPSELINK.getProviderClass();
        
        if (!entityMappings.getGeneratePersistenceUnit()) {
            return;
        }

        try {
            // Issue Fix #5915 Start
            // String version = PersistenceUtils.getJPAVersion(project);
            PUDataObject pud = ProviderUtil.getPUDataObject(project);
            String version = pud.getPersistence().getVersion();
            boolean existFile = false;
            PersistenceUnit punit = null;
            if (pud.getPersistence().sizePersistenceUnit() != 0) {
                for (PersistenceUnit persistenceUnit_In : pud.getPersistence().getPersistenceUnit()) {
                    if (persistenceUnit_In.getName().equalsIgnoreCase(puName)) {
                        punit = persistenceUnit_In;
                        existFile = true;
                        break;
                    }
                }
            }
            if (!existFile) {
                if (Persistence.VERSION_2_1.equals(version)) {
                    punit = (PersistenceUnit) new org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_1.PersistenceUnit();
                } else if (Persistence.VERSION_2_0.equals(version)) {
                    punit = (PersistenceUnit) new org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit();
                } else {//currently default 1.0
                    punit = (PersistenceUnit) new org.netbeans.modules.j2ee.persistence.dd.persistence.model_1_0.PersistenceUnit();
                }
                Properties properties = punit.newProperties();
                punit.setProperties(properties);

                if (!Util.isJavaSE(project)) {// if (Util.isContainerManaged(project)) {
                    punit.setTransactionType(JTA_VALUE);
                    punit.setExcludeUnlistedClasses(false);

                    Property property = properties.newProperty();
                    property.setName("javax.persistence.schema-generation.database.action");
                    property.setValue("drop-and-create");
                    properties.addProperty2(property);
//                    punit.setJtaDataSource("jdbc/sample"); // custom gui will be added in future release for DataSource , JTA
                } else {
                    punit.setTransactionType(RESOURCE_LOCAL_VALUE);

                    Property property = properties.newProperty();
                    property.setName("javax.persistence.jdbc.url");
                    property.setValue("jdbc:derby://localhost:1527/sample");
                    properties.addProperty2(property);

                    property = properties.newProperty();
                    property.setName("javax.persistence.jdbc.password");
                    property.setValue("app");
                    properties.addProperty2(property);

                    property = properties.newProperty();
                    property.setName("javax.persistence.jdbc.driver");
                    property.setValue("org.apache.derby.jdbc.ClientDriver");
                    properties.addProperty2(property);

                    property = properties.newProperty();
                    property.setName("javax.persistence.jdbc.user");
                    property.setValue("app");
                    properties.addProperty2(property);
                }

                punit.setName(puName);
                punit.setProvider(puProvider);
                ProviderUtil.setTableGeneration(punit, TABLE_GENERATION_CREATE, project);
                pud.addPersistenceUnit(punit);

            }
            for (String entityClass : classNames) { // run for both exist & non-exist-persistence
                pud.addClass(punit, entityClass, false);
            }
            pud.save();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "compiler_error", ex);
        }
    }

}
