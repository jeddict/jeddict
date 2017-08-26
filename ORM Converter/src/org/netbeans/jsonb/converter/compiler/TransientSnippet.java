/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jsonb.converter.compiler;

import java.util.Collection;
import java.util.Collections;
import static org.netbeans.jcode.jsonb.JSONBConstants.JSONB_TRANSIENT;
import static org.netbeans.jcode.jsonb.JSONBConstants.JSONB_TRANSIENT_FQN;
import org.netbeans.orm.converter.compiler.*;

public class TransientSnippet implements Snippet {

    @Override
    public String getSnippet() throws InvalidDataException {
        return "@" + JSONB_TRANSIENT;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        return Collections.singletonList(JSONB_TRANSIENT_FQN);
    }
}
