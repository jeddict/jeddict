/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.db.modeler.persistence.internal.jpa.metadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 *
 * @author Gaurav Gupta
 */
public class JPAMMetadataProject extends MetadataProject {

    public JPAMMetadataProject(AbstractSession session, boolean weaveLazy, boolean weaveEager, boolean weaveFetchGroups, boolean multitenantSharedEmf, boolean multitenantSharedCache) {
        super(null, session, weaveLazy, weaveEager, weaveFetchGroups, multitenantSharedEmf, multitenantSharedCache);
    }

    @Override
    public boolean isSharedCacheModeAll() {
        return false;
    }

}
