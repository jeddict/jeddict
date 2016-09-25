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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.orm.converter.util.ORMConverterUtil;
import static org.netbeans.orm.converter.util.ORMConverterUtil.TAB;

public class ElementCollectionSnippet implements Snippet {

    private String collectionType;
    private String targetClass;
    private String targetClassPackage;
    
    private String fetchType = null;
    private MapKeySnippet mapKeySnippet;
//    private String accessType = null;

    public String getFetchType() {
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder();
        builder.append("@ElementCollection");
        if (fetchType != null) {
            builder.append("(fetch=FetchType.").append(fetchType).append(ORMConverterUtil.CLOSE_PARANTHESES);
        }
        if (mapKeySnippet != null && !mapKeySnippet.isEmpty()) {
            builder.append(mapKeySnippet.getSnippet()).append(TAB);
        }
        return builder.toString();
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        importSnippets.add("javax.persistence.ElementCollection");
        if (fetchType != null) {
            importSnippets.add("javax.persistence.FetchType");
        }
        if (mapKeySnippet != null && !mapKeySnippet.isEmpty()) {
            importSnippets.addAll(mapKeySnippet.getImportSnippets());
        }
        return importSnippets;
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

//    /**
//     * @return the accessType
//     */
//    public String getAccessType() {
//        return accessType;
//    }
//
//    /**
//     * @param accessType the accessType to set
//     */
//    public void setAccessType(String accessType) {
//        this.accessType = accessType;
//    }
    /**
     * @return the collectionType
     */
    public String getCollectionType() {
        return collectionType;
    }

    /**
     * @param collectionType the collectionType to set
     */
    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
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
