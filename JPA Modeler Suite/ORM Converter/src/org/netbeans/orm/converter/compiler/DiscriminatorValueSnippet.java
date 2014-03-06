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

import java.util.Collection;
import java.util.Collections;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class DiscriminatorValueSnippet implements Snippet {

    private String value = null;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isDefault() {
        if ((value == null || value.isEmpty())) {
            return true;
        }
        return false;
    }

    public String getSnippet() throws InvalidDataException {
        if (value == null) {
            throw new InvalidDataException("Value cannot be null");
        }

        return "@DiscriminatorValue(\"" + value
                + ORMConverterUtil.QUOTE + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList(
                "javax.persistence.DiscriminatorValue");
    }
}
