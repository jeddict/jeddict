/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.relation.mapper.persistence.internal.jpa.metadata;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsReader;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.Archive;

public class JPAMMetadataProcessor extends MetadataProcessor {

    public JPAMMetadataProcessor(AbstractSession session, ClassLoader loader, boolean weaveLazy, boolean weaveEager, boolean weaveFetchGroups, boolean multitenantSharedEmf, boolean multitenantSharedCache, Map predeployProperties, MetadataProcessor compositeProcessor) {
        m_loader = loader;
        m_session = session;
        m_project = new JPAMMetadataProject(session, weaveLazy, weaveEager, weaveFetchGroups, multitenantSharedEmf, multitenantSharedCache);
        m_predeployProperties = predeployProperties;
        m_compositeProcessor = compositeProcessor;
        if (m_compositeProcessor != null) {
            m_compositeProcessor.addCompositeMemberProcessor(this);
            m_project.setCompositeProcessor(m_compositeProcessor);
        }
    }

    @Override
    protected void initPersistenceUnitClasses() {
        // 1 - Iterate through the classes that are defined in the <mapping>
        // files and add them to the map. This will merge the accessors where
        // necessary.
        HashMap<String, EntityAccessor> entities = new HashMap<>();
        HashMap<String, EmbeddableAccessor> embeddables = new HashMap<>();

        for (XMLEntityMappings entityMappings : m_project.getEntityMappings()) {
            entityMappings.initPersistenceUnitClasses(entities, embeddables);
        }

        // 2 - Iterate through all the XML entities and add them to the project 
        // and apply any persistence unit defaults.
        for (EntityAccessor entity : entities.values()) {
            // This will apply global persistence unit defaults.
            m_project.addEntityAccessor(entity);

            // This will override any global settings.
            entity.getEntityMappings().processEntityMappingsDefaults(entity);
        }

        // 3 - Iterate though all the XML embeddables and add them to the 
        // project and apply any persistence unit defaults.
        for (EmbeddableAccessor embeddable : embeddables.values()) {
            // This will apply global persistence unit defaults.
            m_project.addEmbeddableAccessor(embeddable);

            // This will override any global settings.
            embeddable.getEntityMappings().processEntityMappingsDefaults(embeddable);
        }

    }

    @Override
    protected void loadStandardMappingFiles(String ormXMLFile) {

        URL rootURL = m_loader.getResource("");
        URL ormURL;

        Archive par = null;
        try {
            par = PersistenceUnitProcessor.getArchiveFactory(m_loader).createArchive(rootURL, null);

            if (par != null) {
                ormURL = par.getEntryAsURL(ormXMLFile);

                if (ormURL != null) {

                    // Read the document through OX and add it to the project., pass persistence unit properties for any orm properties set there
                    XMLEntityMappings entityMappings = XMLEntityMappingsReader.read(ormURL, m_loader, m_project.getPersistenceUnitInfo().getProperties());
                    entityMappings.setIsEclipseLinkORMFile(ormXMLFile.equals(MetadataHelper.ECLIPSELINK_ORM_FILE));
                    m_project.addEntityMappings(entityMappings);
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        } finally {
            if (par != null) {
                par.close();
            }
        }

    }

}
