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
package io.github.jeddict.jsonb.modeler.spec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toMap;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.extend.BaseElement;
import io.github.jeddict.jpa.spec.workspace.WorkSpace;
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.specification.model.document.IDefinitionElement;
import org.netbeans.modeler.specification.model.document.IRootElement;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;

/**
 *
 * @author jGauravGupta
 */
public class JSONBMapping extends BaseElement implements IDefinitionElement, IRootElement {

    private String name;
    private EntityMappings entityMappings;
    private final Map<String, JSONBDocument> tables = new HashMap<>();

    public JSONBMapping(EntityMappings entityMapping, WorkSpace workSpace) {
        this.entityMappings = entityMapping;
        tables.putAll(entityMapping.getJavaClass().stream()
                .filter(clazz -> workSpace==null || workSpace.hasItem(clazz))
                .map(clazz -> new JSONBDocument(clazz, workSpace))
                .collect(toMap(doc -> doc.getName(), doc -> doc, (doc1,doc2) -> doc1))); 
        tables.values().forEach(doc -> doc.loadAttribute());
    }

   
    /**
     * @return the tables
     */
    public Collection<JSONBDocument> getDocuments() {
        return tables.values();
    }

    /**
     * @param tables the tables to set
     */
    public void setDocuments(List<JSONBDocument> tables) {
        tables.forEach(t -> addDocument(t));
    }

    public JSONBDocument getDocument(String name) {
        return this.tables.get(name);
    }

    public void addDocument(JSONBDocument table) {
        this.tables.put(table.getName(), table);
    }

    public void removeDocument(JSONBDocument table) {
        this.tables.remove(table.getName());
    }

    public List<JSONBDocument> findAllDocument(String tableName) {
        List<JSONBDocument> tablesResult = new ArrayList<>();
        for (JSONBDocument table : tables.values()) {
            if (tableName.equals(table.getName())) {
                tablesResult.add(table);
            }
        }
        return tablesResult;
    }

    @Override
    public void removeBaseElement(IBaseElement baseElement_In) {
        if (baseElement_In instanceof JSONBDocument) {
            removeDocument((JSONBDocument) baseElement_In);
        } else {
            throw new InvalidElmentException("Invalid Element");
        }
    }

    @Override
    public void addBaseElement(IBaseElement baseElement_In) {
        if (baseElement_In instanceof JSONBDocument) {
            addDocument((JSONBDocument) baseElement_In);
        } else {
            throw new InvalidElmentException("Invalid Element");
        }

    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the entityMapping
     */
    public EntityMappings getEntityMappings() {
        return entityMappings;
    }

    /**
     * @param entityMapping the entityMapping to set
     */
    public void setEntityMappings(EntityMappings entityMapping) {
        this.entityMappings = entityMapping;
    }

}
