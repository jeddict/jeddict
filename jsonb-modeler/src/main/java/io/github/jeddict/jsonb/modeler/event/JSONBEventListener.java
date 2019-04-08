/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jsonb.modeler.event;

import javax.swing.JComponent;
import static io.github.jeddict.jpa.modeler.specification.model.event.JPAEventListener.registerDBViewerEvent;
import static io.github.jeddict.jpa.modeler.specification.model.event.JPAEventListener.registerGenerateSourceEvent;
import org.netbeans.modeler.actions.EventListener;
import org.netbeans.modeler.core.ModelerFile;

/**
 * @author Gaurav Gupta
 */
public class JSONBEventListener extends EventListener {

    @Override
    public void registerEvent(JComponent component, final ModelerFile modelerFile) {
        super.registerEvent(component, modelerFile);
        registerDBViewerEvent(component, modelerFile.getParentFile());
        registerGenerateSourceEvent(component, modelerFile.getParentFile());;
    }
}
