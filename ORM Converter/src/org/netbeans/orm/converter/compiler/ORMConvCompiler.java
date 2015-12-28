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
import java.util.Arrays;
import java.util.List;

public class ORMConvCompiler {

    public static void main(String[] args) throws InvalidDataException {
        ORMConvCompiler entityGenerator = new ORMConvCompiler();

        ClassDefSnippet sampleClass = entityGenerator.getSampleClassDef();

        System.out.println(sampleClass.getSnippet());
    }

    private ClassDefSnippet getSampleClassDef() {

        List<VariableDefSnippet> variableDefs = new ArrayList<VariableDefSnippet>();

        //Simple & Version
        VariableDefSnippet variableDef = new VariableDefSnippet();
        variableDef.setName("middlename");
        variableDef.setType("String");
        variableDef.setVersion(true);
        variableDefs.add(variableDef);

        //Relation
        variableDef = new VariableDefSnippet();
        variableDef.setName("lastname");
        variableDef.setType("String");

        OneToOneSnippet oneToOne = new OneToOneSnippet();

        String[] relationsArray = {RelationDefSnippet.CASCADE_ALL, RelationDefSnippet.CASCADE_MERGE};

        oneToOne.setCascadeTypes(Arrays.asList(relationsArray));
        oneToOne.setMappedBy("mappedBy");
        oneToOne.setOptional(true);
        oneToOne.setTargetEntity("targetEntity");
        oneToOne.setFetchType(RelationDefSnippet.FETCH_EAGER);

        variableDef.setRelationDef(oneToOne);
        variableDefs.add(variableDef);

        //Temporal
        variableDef = new VariableDefSnippet();
        variableDef.setName("firstname");
        variableDef.setType("String");
        variableDef.setTemporal(true);
        variableDef.setTemporalType(VariableDefSnippet.TEMPORAL_TIME);
        variableDefs.add(variableDef);

        //Column
        ColumnDefSnippet columnDef = new ColumnDefSnippet();
        columnDef.setName("MY_COLUMN");

        variableDef = new VariableDefSnippet();
        variableDef.setName("nickname");
        variableDef.setType("String");
        variableDef.setColumnDef(columnDef);
        variableDefs.add(variableDef);

        //transient
        variableDef = new VariableDefSnippet();
        variableDef.setName("male");
        variableDef.setType("Boolean");
        variableDef.setTranzient(true);
        variableDefs.add(variableDef);

        //Query
        NamedQueriesSnippet namedQueries = new NamedQueriesSnippet();

        NamedQueryDefSnippet namedQuery = new NamedQueryDefSnippet();

        namedQuery.setName("findByLastname");
        namedQuery.setQuery("SELECT user FROM UserEntity user WHERE"
                + " user.lastname LIKE ?lastname ORDER BY user.email ASC");

        namedQueries.addNamedQuery(namedQuery);
        namedQueries.addNamedQuery(namedQuery);

        //SQLResultSetMapping
        ColumnResultSnippet columnResult = new ColumnResultSnippet();
        EntityResultSnippet entityResult = new EntityResultSnippet();
        FieldResultSnippet fieldResult = new FieldResultSnippet();

        fieldResult.setColumn("FieldColumn");
        fieldResult.setName("FieldResultName");

        columnResult.setName("columnName");
        entityResult.setEntityClass("EntityClass.class");
        entityResult.addFieldResult(fieldResult);

        SQLResultSetMappingSnippet sqlResultSetMapping = new SQLResultSetMappingSnippet();

        sqlResultSetMapping.setName("sampleSQLSet");
        sqlResultSetMapping.addColumnResult(columnResult);
        sqlResultSetMapping.addEntityResult(entityResult);

        SQLResultSetMappingsSnippet sqlResultSetMappings = new SQLResultSetMappingsSnippet();
        sqlResultSetMappings.addSQLResultSetMapping(sqlResultSetMapping);

        ClassDefSnippet sampleClassDef = new ClassDefSnippet();

        sampleClassDef.setVariableDefs(variableDefs);
        sampleClassDef.setGenerateId(true);
        sampleClassDef.setPackageName("com.jpac.sample");
        sampleClassDef.setClassName("UserEntity");
        sampleClassDef.setNamedQueries(namedQueries);
        sampleClassDef.setEntityName("UserEntity");
        sampleClassDef.setSQLResultSetMappings(sqlResultSetMappings);

        TableDefSnippet tableDef = new TableDefSnippet();

        UniqueConstraintSnippet uniqueConstriant = new UniqueConstraintSnippet();
        uniqueConstriant.setUniqueConstraints(Arrays.asList(relationsArray));

        tableDef.setName("TableName");
        tableDef.setCatalog("my_catalog");
        tableDef.setSchema("my_schema");
        tableDef.setUniqueConstraint(uniqueConstriant);

        sampleClassDef.setTableDef(tableDef);

        return sampleClassDef;
    }
}
