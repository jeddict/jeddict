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

import java.util.Collection;
import java.util.Collections;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class SequenceGeneratorSnippet implements Snippet {

    private int allocationSize = 50;
    private int initialValue = 1;

    private String name = null;
    private String sequenceName = null;
    private String catalog;//added by gaurav gupta
    private String schema;//added by gaurav gupta

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

    public String getSnippet() throws InvalidDataException {

        if (name == null) {
            throw new InvalidDataException("Name is required");
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@SequenceGenerator(name=\"");
        builder.append(name);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        if (sequenceName == null && allocationSize == 50 && initialValue == 1) {

            return builder.substring(0, builder.length() - 1)
                    + ORMConverterUtil.CLOSE_PARANTHESES;
        }

        if (sequenceName != null && !sequenceName.isEmpty()) {
            builder.append("sequenceName=\"");
            builder.append(sequenceName);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (allocationSize != 50) {
            builder.append("allocationSize=");
            builder.append(allocationSize);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (initialValue != 1) {
            builder.append("initialValue=");
            builder.append(initialValue);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (catalog != null && !catalog.isEmpty()) {
            builder.append("catalog=\"");
            builder.append(catalog);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (schema != null && !schema.isEmpty()) {
            builder.append("schema=\"");
            builder.append(schema);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.singleton("javax.persistence.SequenceGenerator");
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
