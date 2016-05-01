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
package org.netbeans.jpa.modeler.db.accessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.netbeans.jpa.modeler.spec.DefaultClass;

/**
 *
 * @author Gaurav Gupta
 */
public class DefaultClassSpecAccessor extends EmbeddableAccessor {

    private final DefaultClass defaultClass;

    private DefaultClassSpecAccessor(DefaultClass defaultClass) {
        this.defaultClass = defaultClass;
    }

    public static DefaultClassSpecAccessor getInstance(DefaultClass defaultClass) {
        DefaultClassSpecAccessor accessor = new DefaultClassSpecAccessor(defaultClass);
        accessor.setClassName(defaultClass.getClazz());
        accessor.setAccess("VIRTUAL");
        accessor.setAttributes(defaultClass.getAccessor());
        if (defaultClass.getSuperclass() != null) {
            accessor.setParentClassName(defaultClass.getSuperclass().getClazz());
        }
        return accessor;

    }

    /**
     * @return the defaultClass
     */
    public DefaultClass getDefaultClass() {
        return defaultClass;
    }

}
