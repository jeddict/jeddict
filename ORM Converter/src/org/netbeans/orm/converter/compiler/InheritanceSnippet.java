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
import java.util.List;

public class InheritanceSnippet implements Snippet {

    public static enum Type {

        SINGLE_TABLE, JOINED, TABLE_PER_CLASS
    };

    private Type statergy = Type.SINGLE_TABLE;

    public Type getStatergy() {
        return statergy;
    }

    public void setStatergy(Type statergy) {
        this.statergy = statergy;
    }

    public String getSnippet() throws InvalidDataException {
        return "@Inheritance(strategy=InheritanceType." + statergy + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.Inheritance");
        importSnippets.add("javax.persistence.InheritanceType");

        return importSnippets;
    }
}
