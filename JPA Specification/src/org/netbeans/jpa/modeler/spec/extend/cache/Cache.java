/**
 * Copyright [2013] Gaurav Gupta
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
package org.netbeans.jpa.modeler.spec.extend.cache;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Cache {

    @XmlElement(name = "ct")
    private Queue<String> collectionClass;
    private final static int COLLECTION_SIZE = 5;

    @XmlElement(name = "db")
    private DatabaseConnectionCache databaseConnection;

    /**
     * @return the collectionType
     */
    public Queue<String> getCollectionClasses() {
        if (collectionClass == null) {
            collectionClass = new LinkedList<>();
            collectionClass.add(List.class.getName());
            collectionClass.add(Set.class.getName());
            collectionClass.add(Collection.class.getName());
        }
        return collectionClass;
    }

    public void addCollectionClass(String _class) {
        LinkedList<String> collection = (LinkedList) getCollectionClasses();
        if (collection.contains(_class)) {
            collection.remove(_class);
        }
        while (COLLECTION_SIZE < collection.size()) {
            collection.removeLast();
        }

        collection.addFirst(_class);

    }

    /**
     * @return the databaseConnection
     */
    public DatabaseConnectionCache getDatabaseConnectionCache() {
        return databaseConnection;
    }

    /**
     * @param databaseConnection the databaseConnection to set
     */
    public void setDatabaseConnection(DatabaseConnectionCache databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

}
