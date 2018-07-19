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
package io.github.jeddict.orm.generator.compiler;

import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import static io.github.jeddict.jcode.jpa.JPAConstants.UNIQUE_CONSTRAINT;
import static io.github.jeddict.jcode.jpa.JPAConstants.UNIQUE_CONSTRAINT_FQN;
import io.github.jeddict.jpa.spec.UniqueConstraint;
import io.github.jeddict.orm.generator.util.ORMConverterUtil;

public class UniqueConstraintSnippet implements Snippet {

    private final UniqueConstraint constraint;

    public UniqueConstraintSnippet(UniqueConstraint constraint) {
        this.constraint = constraint;
    }

    @Override
    public String getSnippet() throws InvalidDataException {

        

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(UNIQUE_CONSTRAINT).append("(");

        if (StringUtils.isNotBlank(constraint.getName())) {
            builder.append("name=\"");
            builder.append(constraint.getName());
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }
                
        builder.append("columnNames={");
        if (!constraint.getColumnName().isEmpty()) {
            for (String columnName : constraint.getColumnName()) {
                builder.append(ORMConverterUtil.QUOTE);
                builder.append(columnName);
                builder.append(ORMConverterUtil.QUOTE);
                builder.append(ORMConverterUtil.COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append(ORMConverterUtil.CLOSE_BRACES);

        
        
        builder.append(ORMConverterUtil.CLOSE_PARANTHESES);
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList(UNIQUE_CONSTRAINT_FQN);

    }
}
