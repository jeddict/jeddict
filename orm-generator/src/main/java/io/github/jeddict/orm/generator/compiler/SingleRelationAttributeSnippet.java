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
package io.github.jeddict.orm.generator.compiler;

/**
 *
 * @author jGauravGupta
 */
public abstract class SingleRelationAttributeSnippet extends AbstractRelationDefSnippet {

    protected boolean optional = false;
    protected String mapsId;
    protected boolean primaryKey;

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * @return the mapsId
     */
    public String getMapsId() {
        return mapsId;
    }

    /**
     * @param mapsId the mapsId to set
     */
    public void setMapsId(String mapsId) {
        this.mapsId = mapsId;
    }

    /**
     * @return the primaryKey
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }
}
