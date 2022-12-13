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
package io.github.jeddict.orm.generator.compiler.def;

import static io.github.jeddict.jcode.JPAConstants.EMBEDDABLE;
import static io.github.jeddict.jcode.JPAConstants.EMBEDDABLE_FQN;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.util.ImportSet;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;

public class EmbeddableIdDefSnippet extends ClassDefSnippet {

    private static final String DEFAULT_TEMPLATE_FILENAME = "classtemplate.ftl";

    @Override
    protected String getTemplateName() {
        return DEFAULT_TEMPLATE_FILENAME;
    }

    @Override
    public String getManagedType() {
        return AT + EMBEDDABLE;
    }

    @Override
    public ImportSet getImportSet() throws InvalidDataException {
        ImportSet imports = super.getImportSet();
        imports.add(EMBEDDABLE_FQN);
        return imports;
    }
}
