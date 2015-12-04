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

import org.netbeans.orm.converter.util.ORMConverterUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractRelationDefSnippet implements RelationDefSnippet {

    private static final String CASCADE_PREFIX = "CascadeType.";

    private String fetchType = null;
    private String targetEntity = null;

    private List<String> cascadeTypes = Collections.EMPTY_LIST;

    public List<String> getCascadeTypes() {

        if (cascadeTypes.isEmpty()) {
            return cascadeTypes;
        }

        return processedCascadeTypes();
    }

    public void setCascadeTypes(List<String> cascadeTypes) {
        if (cascadeTypes != null) {
            this.cascadeTypes = cascadeTypes;
        }
    }

    public String getFetchType() {
        if (fetchType != null) {
            return "FetchType." + fetchType;
        }

        return null;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    public String getTargetEntity() {

        if (targetEntity == null
                || targetEntity.endsWith(ORMConverterUtil.CLASS_SUFFIX)) {
            return targetEntity;
        }

        return targetEntity + ORMConverterUtil.CLASS_SUFFIX;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    private List<String> processedCascadeTypes() {
        List<String> processedCascadeTypes = new ArrayList<String>();

        for (String cascadeType : cascadeTypes) {

            if (!cascadeType.startsWith(CASCADE_PREFIX)) {
                processedCascadeTypes.add(CASCADE_PREFIX + cascadeType);
            } else {
                processedCascadeTypes.add(cascadeType);
            }
        }

        return processedCascadeTypes;
    }
}
