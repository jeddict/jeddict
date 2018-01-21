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

import static org.netbeans.jcode.jpa.JPAConstants.EMBEDDABLE;
import static org.netbeans.jcode.jpa.JPAConstants.EMBEDDABLE_FQN;
import org.netbeans.orm.converter.compiler.InvalidDataException;
import org.netbeans.orm.converter.util.ImportSet;

public class EmbeddableDefSnippet extends IdentifiableClassDefSnippet {

    @Override
    public String getManagedType() {
        return "@" + EMBEDDABLE;
    }

    @Override
    public ImportSet getImportSet() throws InvalidDataException {
        ImportSet importSnippets = super.getImportSet();

        importSnippets.add(EMBEDDABLE_FQN);

        return importSnippets;
    }
}
