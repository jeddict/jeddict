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
import java.util.List;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_BRACES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.COMMA;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.QUOTE;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.SINGLE_QUOTE;

public class ToStringMethodSnippet implements Snippet {

    private final String className;
    private List<String> attributes = Collections.<String>emptyList();

    public ToStringMethodSnippet(String className) {
        this.className = className;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        String classTemplate = "\"%s{\" + ";
        builder.append(String.format(classTemplate, className));

        String attrTemplate = " %s=\" + %s + ";
        for (int i = 0; i < getAttributes().size(); i++) {
            String attribute = getAttributes().get(i);
            builder.append(QUOTE);
            if (i != 0) {
                builder.append(COMMA);
            }
            builder.append(String.format(attrTemplate, attribute, attribute));
        }
        builder.append(SINGLE_QUOTE).append(CLOSE_BRACES).append(SINGLE_QUOTE);
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.<String>emptyList();
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the attributes
     */
    public List<String> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }
}
