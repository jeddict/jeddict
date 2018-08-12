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

import static io.github.jeddict.jcode.JPAConstants.NAMED_STORED_PROCEDURE_QUERY;
import static io.github.jeddict.jcode.JPAConstants.NAMED_STORED_PROCEDURE_QUERY_FQN;
import io.github.jeddict.orm.generator.util.ClassHelper;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class NamedStoredProcedureQuerySnippet implements Snippet {

    private String name = null;
    private String procedureName = null;
    private List<QueryHintSnippet> queryHints = Collections.<QueryHintSnippet>emptyList();

    private List<ClassHelper> resultClasses = Collections.<ClassHelper>emptyList();
    private List<String> resultSetMappings = Collections.<String>emptyList();

    private List<StoredProcedureParameterSnippet> parameters = Collections.<StoredProcedureParameterSnippet>emptyList();

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the queryHints
     */
    public List<QueryHintSnippet> getQueryHints() {
        return queryHints;
    }

    /**
     * @param queryHints the queryHints to set
     */
    public void setQueryHints(List<QueryHintSnippet> queryHints) {
        this.queryHints = queryHints;
    }

    public void addQueryHint(QueryHintSnippet queryHint) {
        if (this.queryHints.isEmpty()) {
            this.queryHints = new ArrayList<>();
        }
        this.queryHints.add(queryHint);
    }

    /**
     * @return the resultClass
     */
    public List<String> getResultClasses() {
        List<String> resultClassesText = new ArrayList<>();
        for (ClassHelper resultClass : resultClasses) {
            resultClassesText.add(resultClass.getClassNameWithClassSuffix());
        }
        return resultClassesText;
    }

    /**
     * @param resultClasses the resultClass to set
     */
    public void setResultClasses(List<String> resultClasses) {
        for (String resultClass : resultClasses) {
            addResultClasses(resultClass);
        }
    }

    public void addResultClasses(String resultClass) {
        if (this.resultClasses.isEmpty()) {
            this.resultClasses = new ArrayList<>();
        }
        ClassHelper classHelper = new ClassHelper();
        classHelper.setClassName(resultClass);
        this.resultClasses.add(classHelper);
    }

    /**
     * @return the resultSetMapping
     */
    public List<String> getResultSetMappings() {
        return resultSetMappings;
    }

    /**
     * @param resultSetMapping the resultSetMapping to set
     */
    public void setResultSetMappings(List<String> resultSetMapping) {
        this.resultSetMappings = resultSetMapping;
    }

    public void addResultSetMapping(String resultSetMapping) {
        if (this.resultSetMappings.isEmpty()) {
            this.resultSetMappings = new ArrayList<>();
        }
        this.resultSetMappings.add(resultSetMapping);
    }

    /**
     * @return the parameter
     */
    public List<StoredProcedureParameterSnippet> getParameters() {
        return parameters;
    }

    /**
     * @param parameter the parameter to set
     */
    public void setParameters(List<StoredProcedureParameterSnippet> parameter) {
        this.parameters = parameter;
    }

    public void addParameter(StoredProcedureParameterSnippet parameter) {
        if (this.parameters.isEmpty()) {
            this.parameters = new ArrayList<>();
        }
        this.parameters.add(parameter);
    }

    /**
     * @return the procedureName
     */
    public String getProcedureName() {
        return procedureName;
    }

    /**
     * @param procedureName the procedureName to set
     */
    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (name == null || procedureName == null) {
            throw new InvalidDataException("Name and ProcedureName required");
        }

        StringBuilder builder = new StringBuilder(AT);
        builder.append(NAMED_STORED_PROCEDURE_QUERY)
                .append(OPEN_PARANTHESES)
                .append(buildString("name", name))
                .append(buildString("procedureName", procedureName))
                .append(buildStrings("resultSetMappings", resultSetMappings));

        if (!resultClasses.isEmpty()) {
            builder.append("resultClasses=").append(OPEN_BRACES);
            for (ClassHelper resultClass : resultClasses) {
                builder.append(resultClass.getClassNameWithClassSuffix());
                builder.append(COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(CLOSE_BRACES);
            builder.append(COMMA);
        }

        builder.append(buildSnippets("parameters", parameters))
                .append(buildSnippets("hints", queryHints));

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        imports.add(NAMED_STORED_PROCEDURE_QUERY_FQN);

        for (StoredProcedureParameterSnippet parameter : parameters) {
            imports.addAll(parameter.getImportSnippets());
        }

        for (ClassHelper resultClass : resultClasses) {
            imports.add(resultClass.getFQClassName());
        }

        for (QueryHintSnippet queryHint : queryHints) {
            imports.addAll(queryHint.getImportSnippets());
        }
        return imports;
    }
}
