/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.orm.converter.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public class ElementCollectionSnippet implements Snippet {

    private String collectionType;
    private String targetClass;
    private String fetchType = null;
//    private String accessType = null;

    public String getFetchType() {
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    public String getSnippet() throws InvalidDataException {
        if (fetchType == null) {
            return "@ElementCollection";
        }

        return "@ElementCollection(fetch=FetchType." + fetchType + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {

        if (fetchType == null) {
            return Collections.singletonList("javax.persistence.ElementCollection");
        }

        List<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.ElementCollection");
        importSnippets.add("javax.persistence.FetchType");

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
}
