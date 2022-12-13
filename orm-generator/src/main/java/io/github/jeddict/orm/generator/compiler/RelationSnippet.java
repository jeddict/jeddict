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
package io.github.jeddict.orm.generator.compiler;

import java.util.List;

public interface RelationSnippet extends Snippet {

    public List<String> getCascadeTypes();

    public void setCascadeTypes(List<String> cascadeTypes);

    public String getFetchType();

    public void setFetchType(String fetchType);
    
    public String getTargetEntityName();

    public String getTargetEntity();

    public void setTargetEntity(String targetEntity);

    public String getTargetEntityPackage();

    public void setTargetEntityPackage(String targetEntityPackage);
    
    public String getTargetField();
    
    public void setTargetField(String targetField);

}
