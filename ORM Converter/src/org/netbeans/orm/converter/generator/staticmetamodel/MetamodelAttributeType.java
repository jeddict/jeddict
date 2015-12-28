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
package org.netbeans.orm.converter.generator.staticmetamodel;

public enum MetamodelAttributeType {

    SINGULAR("SingularAttribute"),COLLECTION("CollectionAttribute"),SET("SetAttribute"),LIST("ListAttribute"),MAP("MapAttribute") ;
    private String type;

    MetamodelAttributeType(String type) {
        this.type = type;
    }

    public static MetamodelAttributeType getInstance(String collectionType) {
        if ("java.util.Collection".equals(collectionType)) {
            return COLLECTION;
        } else if ("java.util.List".equals(collectionType)) {
            return LIST;
        } else if ("java.util.Set".equals(collectionType)) {
            return SET;
        } else if ("java.util.Map".equals(collectionType)) {
            return MAP;
        }
        return SINGULAR;
    }

    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
    
    
}
