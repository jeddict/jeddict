/**
 * Copyright [2018] Gaurav Gupta
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
package org.netbeans.orm.converter.compiler.def;

import org.netbeans.orm.converter.compiler.InvalidDataException;
import org.netbeans.orm.converter.util.ImportSet;

public class DefaultClassDefSnippet extends ClassDefSnippet {
    
    private static final String DEFAULT_TEMPLATE_FILENAME = "classtemplate.vm";

    @Override
    protected String getTemplateName() {
        return DEFAULT_TEMPLATE_FILENAME;
    }

    @Override
    public ImportSet getImportSet() throws InvalidDataException {
        ImportSet importSnippets = super.getImportSet();
        return importSnippets;
    }
}
