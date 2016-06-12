/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.orm.converter.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.netbeans.jcode.jpa.JPAConstants.MAP_KEY_TEMPORAL;
import static org.netbeans.jcode.jpa.JPAConstants.MAP_KEY_TEMPORAL_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.TEMPORAL;
import static org.netbeans.jcode.jpa.JPAConstants.TEMPORAL_DATE;
import static org.netbeans.jcode.jpa.JPAConstants.TEMPORAL_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.TEMPORAL_TIME;
import static org.netbeans.jcode.jpa.JPAConstants.TEMPORAL_TIMESTAMP;
import static org.netbeans.jcode.jpa.JPAConstants.TEMPORAL_TYPE_FQN;
import org.netbeans.jpa.modeler.spec.TemporalType;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class TemporalSnippet implements Snippet {

    private static final List<String> TEMPORAL_TYPES = getTemporalTypes();
    private boolean mapKey;

    private String value = null;

    public TemporalSnippet(boolean mapKey) {
        this.mapKey = mapKey;
    }

    public TemporalSnippet() {
    }

    public TemporalSnippet(String value) {
        setValue(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (!TEMPORAL_TYPES.contains(value)) {
            throw new IllegalArgumentException("Invalid Temporal Type, Supported types" + TEMPORAL_TYPES);
        }
        this.value = value;
    }

    public void setValue(TemporalType parsedTemporalType) {
        if (parsedTemporalType.equals(TemporalType.DATE)) {
            this.setValue(TEMPORAL_DATE);
        } else if (parsedTemporalType.equals(TemporalType.TIME)) {
            this.setValue(TEMPORAL_TIME);
        } else if (parsedTemporalType.equals(TemporalType.TIMESTAMP)) {
            this.setValue(TEMPORAL_TIMESTAMP);
        }
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        builder.append('@');
        if (mapKey) {
            builder.append(MAP_KEY_TEMPORAL);
        } else {
            builder.append(TEMPORAL);
        }
        if (value != null) {
            builder.append(ORMConverterUtil.OPEN_PARANTHESES).append(value).append(ORMConverterUtil.CLOSE_PARANTHESES);
        }
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        if (mapKey) {
            importSnippets.add(MAP_KEY_TEMPORAL_FQN);
        } else {
            importSnippets.add(TEMPORAL_FQN);
        }
        if (value != null) {
            importSnippets.add(TEMPORAL_TYPE_FQN);
        }
        return importSnippets;
    }

    private static List<String> getTemporalTypes() {
        List<String> temporalTypesList = new ArrayList<>();
        temporalTypesList.add(TEMPORAL_DATE);
        temporalTypesList.add(TEMPORAL_TIME);
        temporalTypesList.add(TEMPORAL_TIMESTAMP);
        return temporalTypesList;
    }

    /**
     * @return the mapKey
     */
    public boolean isMapKey() {
        return mapKey;
    }

    /**
     * @param mapKey the mapKey to set
     */
    public void setMapKey(boolean mapKey) {
        this.mapKey = mapKey;
    }
}
