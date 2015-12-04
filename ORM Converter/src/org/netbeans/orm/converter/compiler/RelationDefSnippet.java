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

import java.util.List;

public interface RelationDefSnippet extends Snippet {

    public static final String CASCADE_ALL = "CascadeType.ALL";
    public static final String CASCADE_MERGE = "CascadeType.MERGE";
    public static final String CASCADE_PERSIST = "CascadeType.PERSIST";
    public static final String CASCADE_REFRESH = "CascadeType.REFRESH";
    public static final String CASCADE_REMOVE = "CascadeType.REMOVE";

    public static final String FETCH_EAGER = "FetchType.EAGER";
    public static final String FETCH_LAZY = "FetchType.LAZY";

    public List<String> getCascadeTypes();

    public void setCascadeTypes(List<String> cascadeTypes);

    public String getFetchType();

    public void setFetchType(String fetchType);

    public String getTargetEntity();

    public void setTargetEntity(String targetEntity);
}
