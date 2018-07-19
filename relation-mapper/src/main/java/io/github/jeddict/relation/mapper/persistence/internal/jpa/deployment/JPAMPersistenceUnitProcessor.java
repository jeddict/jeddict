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
package io.github.jeddict.relation.mapper.persistence.internal.jpa.deployment;

import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor.Mode;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import io.github.jeddict.relation.mapper.persistence.internal.jpa.metadata.JPAMMetadataProcessor;

/**
 * INTERNAL: Utility Class that deals with persistence archives for EJB 3.0
 * Provides functions like searching for persistence archives, processing
 * persistence.xml and searching for Entities in a Persistence archive
 */
public class JPAMPersistenceUnitProcessor {

    /**
     * Process the Object/relational metadata from XML and annotations
     */
    public static void processORMetadata(XMLEntityMappings mapping, JPAMMetadataProcessor processor, boolean throwExceptionOnFail, Mode mode) {
        if (mode == Mode.ALL || mode == Mode.COMPOSITE_MEMBER_INITIAL) {
            // DO NOT CHANGE the order of invocation of various methods.

            // 1 - Load the list of mapping files for the persistence unit. Need to 
            // do this before we start processing entities as the list of entity 
            // classes depend on metadata read from mapping files.
            processor.getProject().addEntityMappings(mapping);
        }

        // 2 - Process each XML entity mappings file metadata (except for
        // the actual classes themselves). This method is also responsible
        // for handling any XML merging.
        processor.processEntityMappings(mode);

        // 3 - Process the persistence unit classes (from XML and annotations)
        // and their metadata now.
        processor.processORMMetadata(mode);
    }

}
