/**
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
package org.netbeans.orm.converter.compiler;

import java.util.ArrayList;
import java.util.List;
import static org.netbeans.jcode.jpa.JPAConstants.CASCADE_TYPE;
import static org.netbeans.jcode.jpa.JPAConstants.FETCH_TYPE;
import static org.netbeans.jcode.jpa.JPAConstants.PERSISTENCE_PACKAGE_PREFIX;
import org.netbeans.jpa.modeler.spec.extend.CollectionTypeHandler;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public abstract class MultiRelationAttributeSnippet extends AbstractRelationDefSnippet implements CollectionTypeHandler {

    protected String collectionType;
    protected String mappedBy = null;
    protected MapKeySnippet mapKeySnippet;

    public String getMappedBy() {
        return mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

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

    public abstract String getType();

    @Override
    public String getSnippet() throws InvalidDataException {

        if (mappedBy == null && getTargetEntity() == null && getFetchType() == null
                && getCascadeTypes().isEmpty() && (mapKeySnippet==null || mapKeySnippet.isEmpty())) {
            return "@" + getType();
        }

        StringBuilder builder = new StringBuilder();
        if (mapKeySnippet != null && !mapKeySnippet.isEmpty()) {
            builder.append(mapKeySnippet.getSnippet());
        }

        builder.append("@").append(getType()).append("(");

        if (!getCascadeTypes().isEmpty()) {
            builder.append("cascade={");
            String encodedString = ORMConverterUtil.getCommaSeparatedString(getCascadeTypes());
            builder.append(encodedString);
            builder.append("},");
        }

        if (getFetchType() != null) {
            builder.append("fetch = ");
            builder.append(getFetchType());
            builder.append(ORMConverterUtil.COMMA);
        }

        if (getTargetEntity() != null) {
            builder.append("targetEntity = ");
            builder.append(getTargetEntity());
            builder.append(ORMConverterUtil.COMMA);
        }

        if (mappedBy != null) {
            builder.append("mappedBy = ");
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(mappedBy);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    @Override
    public List<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        importSnippets.add(PERSISTENCE_PACKAGE_PREFIX + getType());
        if (getFetchType() != null) {
            importSnippets.add(FETCH_TYPE);
        }
        if (getCascadeTypes() != null && !getCascadeTypes().isEmpty()) {
            importSnippets.add(CASCADE_TYPE);
        }
        if(mapKeySnippet != null && !mapKeySnippet.isEmpty()) {
            importSnippets.addAll(mapKeySnippet.getImportSnippets());
        }
//        if (getTargetEntityPackage()!= null) {
//            importSnippets.add(getTargetEntityPackage() + ORMConverterUtil.DOT + getTargetEntityName());
//        }
        return importSnippets;
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
}
