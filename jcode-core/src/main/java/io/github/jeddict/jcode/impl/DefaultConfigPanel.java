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
package io.github.jeddict.jcode.impl;

import io.github.jeddict.jcode.impl.DefaultLayerConfigData;
import io.github.jeddict.jcode.LayerConfigPanel;
import javax.swing.event.ChangeEvent;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;

/**
 *
 * @author Gaurav Gupta
 */
public class DefaultConfigPanel extends LayerConfigPanel<DefaultLayerConfigData> {

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }

    @Override
    public void store() {
    }

    @Override
    public void read() {
    }

    @Override
    public void init(String _package, Project project, SourceGroup sourceGroup) {
    }

}
