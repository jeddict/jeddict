/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.modeler.widget.flow.relation;

import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.DirectionalRelation;

/**
 *
 * @author Gaurav Gupta
 */
public interface UnidirectionalRelation extends DirectionalRelation {

    /**
     * @return the targetEntityWidget
     */
    public EntityWidget getTargetEntityWidget();

    /**
     * @param targetEntityWidget the targetEntityWidget to set
     */
    public void setTargetEntityWidget(EntityWidget targetEntityWidget);

}
