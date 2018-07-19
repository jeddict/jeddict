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
package io.github.jeddict.db.accessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.TransientAccessor;
import io.github.jeddict.jpa.spec.Transient;

/**
 *
 * @author Gaurav Gupta
 */
public class TransientSpecAccessor extends TransientAccessor {

    private Transient _transient;

    private TransientSpecAccessor(Transient _transient) {
        this._transient = _transient;
    }

    public static TransientSpecAccessor getInstance(Transient _transient) {
        TransientSpecAccessor accessor = new TransientSpecAccessor(_transient);
        accessor.setName(_transient.getName());
        accessor.setAttributeType(_transient.getAttributeType());
        return accessor;

    }

}
