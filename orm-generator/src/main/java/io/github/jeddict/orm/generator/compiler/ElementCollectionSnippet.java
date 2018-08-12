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

import static io.github.jeddict.jcode.JPAConstants.ELEMENT_COLLECTION;
import static io.github.jeddict.jcode.JPAConstants.ELEMENT_COLLECTION_FQN;
import static io.github.jeddict.jcode.JPAConstants.FETCH_TYPE_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.NEW_LINE;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.TAB;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ElementCollectionSnippet implements Snippet {

    private String targetClass;
    private String targetClassPackage;
    
    private String fetchType = null;
    private MapKeySnippet mapKeySnippet;

    public String getFetchType() {
        if (fetchType != null) {
            return "FetchType." + fetchType;
        }
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        if (mapKeySnippet != null && !mapKeySnippet.isEmpty()) {
            builder.append(mapKeySnippet.getSnippet())
                    .append(NEW_LINE)
                    .append(TAB);
        }

        builder.append(AT)
                .append(ELEMENT_COLLECTION);

        if (isNotBlank(getFetchType())) {
            builder.append(OPEN_PARANTHESES)
                    .append("fetch=")
                    .append(getFetchType())
                    .append(CLOSE_PARANTHESES);
        }
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        imports.add(ELEMENT_COLLECTION_FQN);
        if (isNotBlank(getFetchType())) {
            imports.add(FETCH_TYPE_FQN);
        }
        if (mapKeySnippet != null && !mapKeySnippet.isEmpty()) {
            imports.addAll(mapKeySnippet.getImportSnippets());
        }
        return imports;
    }

    /**
     * @return the targetClass
     */
    public String getTargetClass() {
        return targetClass;
    }

    /**
     * @param targetClass the targetClass to set
     */
    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }



    /**
     * @return the mapKeySnippet
     */
    public MapKeySnippet getMapKeySnippet() {
        return mapKeySnippet;
    }

    /**
     * @param mapKeySnippet the mapKeySnippet to set
     */
    public void setMapKeySnippet(MapKeySnippet mapKeySnippet) {
        this.mapKeySnippet = mapKeySnippet;
    }

    /**
     * @return the targetClassPackage
     */
    public String getTargetClassPackage() {
        return targetClassPackage;
    }

    /**
     * @param targetClassPackage the targetClassPackage to set
     */
    public void setTargetClassPackage(String targetClassPackage) {
        this.targetClassPackage = targetClassPackage;
    }
    
}
