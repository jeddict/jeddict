/* 
 * Copyright [2016] Gaurav Gupta
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

package org.netbeans.jpa.modeler.external.jpqleditor;

import org.netbeans.modules.j2ee.persistence.jpqleditor.JPQLResult;

public class CustomJPQLResult extends JPQLResult {
     private String queryProblems;

    public void setQueryProblems(String queryProblems) {
        this.queryProblems = queryProblems;
    }
    
     @Override
    public String getQueryProblems(){
        return queryProblems;
    }
}
