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

import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLASS_SUFFIX;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractRelationDefSnippet implements RelationDefSnippet {

    private static final String CASCADE_PREFIX = "CascadeType.";

    private String fetchType;
    private String targetEntity;
    private String targetEntityPackage;
    private String targetField;

    private List<String> cascadeTypes = Collections.<String>emptyList();

    @Override
    public List<String> getCascadeTypes() {
        return cascadeTypes;
    }

    @Override
    public void setCascadeTypes(List<String> cascadeTypes) {
        if (cascadeTypes != null) {
            this.cascadeTypes = processedCascadeTypes(cascadeTypes);
        }
    }

    @Override
    public String getFetchType() {
        if (fetchType != null) {
            return "FetchType." + fetchType;
        }

        return null;
    }

    @Override
    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    @Override
    public String getTargetEntity() {

        if (targetEntity == null
                || targetEntity.endsWith(CLASS_SUFFIX)) {
            return targetEntity;
        }

        return targetEntity + CLASS_SUFFIX;
    }
    
    @Override
    public String getTargetEntityName() {

        if (targetEntity.endsWith(CLASS_SUFFIX)) {
            return targetEntity.substring(0, targetEntity.lastIndexOf(CLASS_SUFFIX));
        }

        return targetEntity;
    }

    @Override
    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    private List<String> processedCascadeTypes(List<String> cascadeTypes) {
        List<String> processedCascadeTypes = new ArrayList<>();
        for (String cascadeType : cascadeTypes) {
            if (!cascadeType.startsWith(CASCADE_PREFIX)) {
                processedCascadeTypes.add(CASCADE_PREFIX + cascadeType);
            } else {
                processedCascadeTypes.add(cascadeType);
            }
        }

        return processedCascadeTypes;
    }

    /**
     * @return the targetEntityPackage
     */
    @Override
    public String getTargetEntityPackage() {
        return targetEntityPackage;
    }

    /**
     * @param targetEntityPackage the targetEntityPackage to set
     */
    @Override
    public void setTargetEntityPackage(String targetEntityPackage) {
        this.targetEntityPackage = targetEntityPackage;
    }

    /**
     * @return the targetField
     */
    @Override
    public String getTargetField() {
        return targetField;
    }

    /**
     * @param targetField the targetField to set
     */
    @Override
    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }
}
