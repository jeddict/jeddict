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
package org.netbeans.db.modeler.spec;

import java.util.List;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.extend.Attribute;

public abstract class DBEmbeddedColumn<E extends Attribute> extends DBColumn<E> {

    private final List<Embedded> embeddedList;
    private String keyName;

    public DBEmbeddedColumn(String name, List<Embedded> embedded, E managedAttribute) {
        super(name, managedAttribute);
        this.embeddedList = embedded;
    }

    /**
     * @return the embedded
     */
    public List<Embedded> getEmbeddedList() {
        return embeddedList;
    }

    protected String getKeyName() {
        if (keyName != null) {
            return keyName;
        }
        StringBuilder keyNameBuilder = new StringBuilder();
        boolean skipFirst = true;
        for (Embedded next : embeddedList) {
            if (skipFirst) {
                skipFirst = false;
                continue;
            }
            keyNameBuilder.append(next.getName()).append('.');
        }
        keyNameBuilder.append(getAttribute().getName());
        keyName = keyNameBuilder.toString();
        return keyName;
    }
}
