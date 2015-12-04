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
package org.netbeans.jpa.modeler.core.widget;

public enum CompositePKProperty {

    ALL, //both properties CompositePrimaryKeyClass ,CompositePrimaryKeyType are visible
    NONE, //both properties CompositePrimaryKeyClass ,CompositePrimaryKeyType are not visible
    TYPE, //only property compositePrimaryKeyType is visible
    CLASS,//only property CompositePrimaryKeyClass is visible
    AUTO_CLASS,// property CompositePrimaryKeyClass is disabled

}
/*AUTO_CLASS define the case  JPA JSR Ref : 2.4.1.3   Example 5: Case (a)
 *means dependent class contains 1 derived pk and (derived pk) parent class contains composite pk
 *so dependent class IdClass/EmbeddedId class is same as parent entity class (Auto assigned)
 */
