/**
 * Copyright [2013] Gaurav Gupta
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
package org.netbeans.jpa.modeler.spec.extend;

import org.netbeans.jpa.modeler.spec.IdClass;

public interface PrimaryKeyContainer {

    public IdClass getIdClass();

    public void setIdClass(IdClass value);

    public CompositePrimaryKeyType getCompositePrimaryKeyType();

    public void setCompositePrimaryKeyType(CompositePrimaryKeyType compositePrimaryKeyType);

    public String getCompositePrimaryKeyClass();

    public void setCompositePrimaryKeyClass(String compositePrimaryKeyClass);

    public void manageCompositePrimaryKeyClass();

}
