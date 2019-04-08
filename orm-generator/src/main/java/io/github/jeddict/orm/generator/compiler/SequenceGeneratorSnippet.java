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
package io.github.jeddict.orm.generator.compiler;

import static io.github.jeddict.jcode.JPAConstants.SEQUENCE_GENERATOR;
import static io.github.jeddict.jcode.JPAConstants.SEQUENCE_GENERATOR_FQN;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateDefaultValue;
import java.util.Collection;
import static java.util.Collections.singleton;
import static io.github.jeddict.util.StringUtils.isBlank;

public class SequenceGeneratorSnippet implements Snippet {

    private int allocationSize = 50;
    private int initialValue = 1;

    private String name = null;
    private String sequenceName = null;
    private String catalog;
    private String schema;

    public int getAllocationSize() {
        return allocationSize;
    }

    public void setAllocationSize(int allocationSize) {
        this.allocationSize = allocationSize;
    }

    public int getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(int initialValue) {
        this.initialValue = initialValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (isBlank(name)) {
            throw new InvalidDataException("Name is required");
        }

        return annotate(
                SEQUENCE_GENERATOR,
                attribute("name", name),
                attribute("sequenceName", sequenceName),
                attribute("allocationSize", allocationSize, val -> isGenerateDefaultValue() || val != 50),
                attribute("initialValue", initialValue, val -> isGenerateDefaultValue() || val != 1),
                attribute("catalog", catalog),
                attribute("schema", schema)
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return singleton(SEQUENCE_GENERATOR_FQN);
    }

    /**
     * @return the catalog
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * @param catalog the catalog to set
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    /**
     * @return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }
}
