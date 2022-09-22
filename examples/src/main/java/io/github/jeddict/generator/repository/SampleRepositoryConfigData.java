/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.generator.repository;

import io.github.jeddict.jcode.LayerConfigData;
import java.util.Collections;
import java.util.List;
import jakarta.json.bind.annotation.JsonbProperty;
import io.github.jeddict.util.StringUtils;

/**
 *
 * @author Gaurav Gupta
 */
public class SampleRepositoryConfigData extends LayerConfigData {

    private String prefixName;

    private String suffixName;

    @JsonbProperty("package")
    private String _package;

    /**
     * @return the _package
     */
    public String getPackage() {
        return _package;
    }

    /**
     * @param _package the _package to set
     */
    public void setPackage(String _package) {
        this._package = _package;
    }
    
    /**
     * @return the suffixName
     */
    public String getSuffixName() {
        if(StringUtils.isBlank(suffixName)){
            suffixName = "Facade";
        }
        return suffixName;
    }

    /**
     * @param suffixName the suffixName to set
     */
    public void setSuffixName(String suffixName) {
        this.suffixName = suffixName;
    }

    /**
     * @return the prefixName
     */
    public String getPrefixName() {
        return prefixName;
    }

    /**
     * @param prefixName the prefixName to set
     */
    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }
    
    @Override
    public List<String> getUsageDetails(){
        return Collections.EMPTY_LIST;
    }
}
