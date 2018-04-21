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
package io.github.jeddict.jpa.spec.extend;

import java.util.List;
import io.github.jeddict.jpa.spec.ForeignKey;
import io.github.jeddict.jpa.spec.JoinColumn;

/**
 *
 * @author gaurav
 */
public interface JoinColumnHandler extends JoinTableHandler {

    public List<JoinColumn> getJoinColumn();

    public void addJoinColumn(JoinColumn joinColumn);

    public void removeJoinColumn(JoinColumn joinColumn);
    
    public ForeignKey getForeignKey();

    public void setForeignKey(ForeignKey value);

}
