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
import java.util.Collections;
import java.util.List;
import static org.netbeans.jcode.jpa.JPAConstants.NAMED_STORED_PROCEDURE_QUERY;
import static org.netbeans.jcode.jpa.JPAConstants.NAMED_STORED_PROCEDURE_QUERY_FQN;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConverterUtil;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
public class NamedStoredProcedureQuerySnippet implements Snippet {

    private String name = null;
    private String procedureName = null;
    private List<QueryHintSnippet> queryHints = Collections.EMPTY_LIST;

    private List<ClassHelper> resultClasses = Collections.EMPTY_LIST;
    private List<String> resultSetMappings = Collections.EMPTY_LIST;

    private List<StoredProcedureParameterSnippet> parameters = Collections.EMPTY_LIST;

    @Override
    public String getSnippet() throws InvalidDataException {

        if (name == null || procedureName == null) {
            throw new InvalidDataException("Name and ProcedureName required");
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@").append(NAMED_STORED_PROCEDURE_QUERY).append("(");

        builder.append("name=\"");
        builder.append(name);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        builder.append("procedureName=\"");
        builder.append(procedureName);
        builder.append(ORMConverterUtil.QUOTE);
        builder.append(ORMConverterUtil.COMMA);

        if (!resultSetMappings.isEmpty()) {
            builder.append("resultSetMappings={");
            for (String resultSetMapping : resultSetMappings) {
                builder.append(ORMConverterUtil.QUOTE);
                builder.append(resultSetMapping);
                builder.append(ORMConverterUtil.QUOTE);
                builder.append(ORMConverterUtil.COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (!resultClasses.isEmpty()) {
            builder.append("resultClasses={");
            for (ClassHelper resultClass : resultClasses) {
                builder.append(resultClass.getClassNameWithClassSuffix());
                builder.append(ORMConverterUtil.COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (!parameters.isEmpty()) {
            builder.append("parameters={");
            for (StoredProcedureParameterSnippet parameter : parameters) {
                builder.append(parameter.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);

        }

        if (!queryHints.isEmpty()) {
            builder.append("hints={");

            for (QueryHintSnippet queryHint : queryHints) {
                builder.append(queryHint.getSnippet());
                builder.append(ORMConverterUtil.COMMA);
            }

            builder.deleteCharAt(builder.length() - 1);

            builder.append(ORMConverterUtil.CLOSE_BRACES);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public List<String> getImportSnippets() throws InvalidDataException {

        List<String> importSnippets = new ArrayList<>();

        importSnippets.add(NAMED_STORED_PROCEDURE_QUERY_FQN);

        for (StoredProcedureParameterSnippet parameter : parameters) {
            importSnippets.addAll(parameter.getImportSnippets());
        }

        for (ClassHelper resultClass : resultClasses) {
            importSnippets.add(resultClass.getFQClassName());
        }

        for (QueryHintSnippet queryHint : queryHints) {
            importSnippets.addAll(queryHint.getImportSnippets());
        }
        return importSnippets;
    }

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
            this.queryHints = new ArrayList<QueryHintSnippet>();
        }
        this.queryHints.add(queryHint);
    }

    /**
     * @return the resultClass
     */
    public List<String> getResultClasses() {
        List<String> resultClassesText = new ArrayList<String>();
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
            this.resultClasses = new ArrayList<ClassHelper>();
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
            this.resultSetMappings = new ArrayList<String>();
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
            this.parameters = new ArrayList<StoredProcedureParameterSnippet>();
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

}
