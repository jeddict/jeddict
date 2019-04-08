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

import static io.github.jeddict.jcode.JPAConstants.NAMED_NATIVE_QUERY;
import static io.github.jeddict.jcode.JPAConstants.NAMED_NATIVE_QUERY_FQN;
import io.github.jeddict.orm.generator.util.ClassHelper;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NamedNativeQuerySnippet extends NamedQuerySnippet implements Snippet {

    private ClassHelper classHelper = new ClassHelper();
    private String resultSetMapping = null;

    public String getResultClass() {
        return classHelper.getClassNameWithClassSuffix();
    }

    public void setResultClass(String resultClass) {
        classHelper.setClassName(resultClass);
    }

    public void setPackageName(String packageName) {
        classHelper.setPackageName(packageName);
    }

    public String getResultSetMapping() {
        return resultSetMapping;
    }

    public void setResultSetMapping(String resultSetMapping) {
        this.resultSetMapping = resultSetMapping;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        if (name == null || query == null) {
            throw new InvalidDataException("Query data missing, Name:" + name + " Query: " + query);
        }

        return annotate(
                NAMED_NATIVE_QUERY,
                attribute("name", name),
                attribute("query", query.replaceAll("\\n", " ").replaceAll("\\t", " ")),
                attributeExp("resultClass", getResultClass(), val -> classHelper.getClassName() != null),
                attribute("resultSetMapping", resultSetMapping),
                attributes("hints", queryHints)
        );
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        imports.add(NAMED_NATIVE_QUERY_FQN);
        if (classHelper.getClassName() != null) {
            imports.add(classHelper.getFQClassName());
        }
        if (!queryHints.isEmpty()) {
            imports.addAll(queryHints.get(0).getImportSnippets());
        }
        return imports;
    }
}
