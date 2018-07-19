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
package io.github.jeddict.jaxb.spec;

import java.util.List;

/**
 *
 * @author Gaurav Gupta
 */
public interface JaxbVariableTypeHandler {

    /**
     * @return the jaxbVariableType
     */
    public JaxbVariableType getJaxbVariableType();
    
    public List<JaxbVariableType> getJaxbVariableList();

    /**
     * @param jaxbVariableType the jaxbVariableType to set
     */
    public void setJaxbVariableType(JaxbVariableType jaxbVariableType);

    /**
     * @return the jaxbMetadata
     */
    public JaxbMetadata getJaxbMetadata();

    /**
     * @param jaxbMetadata the jaxbMetadata to set
     */
    public void setJaxbMetadata(JaxbMetadata jaxbMetadata);

}
