/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.orm.generator.compiler;

import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_TEMPORAL;
import static io.github.jeddict.jcode.JPAConstants.MAP_KEY_TEMPORAL_FQN;
import static io.github.jeddict.jcode.JPAConstants.TEMPORAL;
import static io.github.jeddict.jcode.JPAConstants.TEMPORAL_DATE;
import static io.github.jeddict.jcode.JPAConstants.TEMPORAL_FQN;
import static io.github.jeddict.jcode.JPAConstants.TEMPORAL_TIME;
import static io.github.jeddict.jcode.JPAConstants.TEMPORAL_TIMESTAMP;
import static io.github.jeddict.jcode.JPAConstants.TEMPORAL_TYPE_FQN;
import io.github.jeddict.jpa.spec.TemporalType;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static io.github.jeddict.util.StringUtils.isNotBlank;

public class TemporalSnippet implements Snippet {

    private static final List<String> TEMPORAL_TYPES = asList(
            TEMPORAL_DATE,
            TEMPORAL_TIME,
            TEMPORAL_TIMESTAMP
    );

    private boolean mapKey;

    private String value;

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
        switch (parsedTemporalType) {
            case DATE:
                this.setValue(TEMPORAL_DATE);
                break;
            case TIME:
                this.setValue(TEMPORAL_TIME);
                break;
            case TIMESTAMP:
                this.setValue(TEMPORAL_TIMESTAMP);
                break;
            default:
                break;
        }
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder(AT);
        if (mapKey) {
            builder.append(MAP_KEY_TEMPORAL);
        } else {
            builder.append(TEMPORAL);
        }
        if (isNotBlank(value)) {
            builder.append(OPEN_PARANTHESES).append(value).append(CLOSE_PARANTHESES);
        }
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        if (mapKey) {
            imports.add(MAP_KEY_TEMPORAL_FQN);
        } else {
            imports.add(TEMPORAL_FQN);
        }
        if (isNotBlank(value)) {
            imports.add(TEMPORAL_TYPE_FQN);
        }
        return imports;
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
