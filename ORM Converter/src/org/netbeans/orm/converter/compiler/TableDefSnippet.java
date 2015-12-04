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

import org.netbeans.orm.converter.util.ORMConverterUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class TableDefSnippet implements Snippet {

    private String catalog = null;
    private String name = null;
    private String schema = null;

    private UniqueConstraintSnippet uniqueConstraint = null;

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public UniqueConstraintSnippet getUniqueConstraints() {
        return uniqueConstraint;
    }

    public void setUniqueConstraint(UniqueConstraintSnippet uniqueConstraint) {
        this.uniqueConstraint = uniqueConstraint;
    }

    public String getSnippet() throws InvalidDataException {

        if (name == null
                && catalog == null
                && schema == null
                && uniqueConstraint == null) {
            return "@Table";
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@Table(");

        if (name != null) {
            builder.append("name=\"");
            builder.append(name);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (schema != null) {
            builder.append("schema=\"");
            builder.append(schema);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (catalog != null) {
            builder.append("catalog=\"");
            builder.append(catalog);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (uniqueConstraint != null) {
            builder.append("uniqueConstraints=");

            builder.append(uniqueConstraint.getSnippet());

            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;

    }

    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (uniqueConstraint == null) {
            return Collections.singletonList("javax.persistence.Table");
        }

        Collection<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.Table");
        importSnippets.addAll(uniqueConstraint.getImportSnippets());

        return importSnippets;
    }
}
