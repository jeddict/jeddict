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
import java.util.Collection;
import static java.util.Collections.singletonMap;
import java.util.List;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class PersistenceXMLUnitSnippet implements Snippet {

    private static final String TEMPLATE_FILENAME = "persistence.vm";

    private String name;
    private List<String> classNames = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public List<String> getClassNames() {
        return classNames;
    }

    public void setClassNames(List<String> classes) {
        this.classNames = classes;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        try {
            return ORMConverterUtil.writeToTemplate(TEMPLATE_FILENAME,singletonMap("pu", this));
        } catch (Exception e) {
            throw new InvalidDataException(e);
        }
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
