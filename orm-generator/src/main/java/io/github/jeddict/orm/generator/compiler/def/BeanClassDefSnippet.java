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
package io.github.jeddict.orm.generator.compiler.def;

import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.util.ImportSet;

public class BeanClassDefSnippet extends ClassDefSnippet {

    private static final String BEAN_CLASS_TEMPLATE_FILENAME = "beanclasstemplate.ftl";

    @Override
    protected String getTemplateName() {
        return BEAN_CLASS_TEMPLATE_FILENAME;
    }

    @Override
    public ImportSet getImportSet() throws InvalidDataException {
        ImportSet imports = super.getImportSet();
        return imports;
    }
}
