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
import java.util.List;

public class BasicSnippet implements Snippet {

    private String fetchType = null;
    private boolean optional = true;

    public String getFetchType() {
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getSnippet() throws InvalidDataException {
        if (fetchType == null) {
            return "@Basic";
        }

        return "@Basic(fetch=FetchType." + fetchType + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (fetchType == null) {
            return Collections.singletonList("javax.persistence.Basic");
        }

        List<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.Basic");
        importSnippets.add("javax.persistence.FetchType");

        return importSnippets;
    }
}
