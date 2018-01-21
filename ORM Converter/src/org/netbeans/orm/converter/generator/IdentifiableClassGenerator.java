/**
 * Copyright [2018] Gaurav Gupta
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
package org.netbeans.orm.converter.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.jcode.infra.JavaEEVersion;
import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.ColumnResult;
import org.netbeans.jpa.modeler.spec.ConstructorResult;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.EmptyType;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityListeners;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.EntityResult;
import org.netbeans.jpa.modeler.spec.FieldResult;
import org.netbeans.jpa.modeler.spec.GeneratedValue;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.IdClass;
import org.netbeans.jpa.modeler.spec.IdentifiableClass;
import org.netbeans.jpa.modeler.spec.NamedAttributeNode;
import org.netbeans.jpa.modeler.spec.NamedEntityGraph;
import org.netbeans.jpa.modeler.spec.NamedNativeQuery;
import org.netbeans.jpa.modeler.spec.NamedQuery;
import org.netbeans.jpa.modeler.spec.NamedStoredProcedureQuery;
import org.netbeans.jpa.modeler.spec.NamedSubgraph;
import org.netbeans.jpa.modeler.spec.PrimaryKeyJoinColumn;
import org.netbeans.jpa.modeler.spec.QueryHint;
import org.netbeans.jpa.modeler.spec.SecondaryTable;
import org.netbeans.jpa.modeler.spec.SequenceGenerator;
import org.netbeans.jpa.modeler.spec.SqlResultSetMapping;
import org.netbeans.jpa.modeler.spec.StoredProcedureParameter;
import org.netbeans.jpa.modeler.spec.Table;
import org.netbeans.jpa.modeler.spec.TableGenerator;
import org.netbeans.jpa.modeler.spec.TemporalType;
import org.netbeans.jpa.modeler.spec.Version;
import org.netbeans.jpa.modeler.spec.extend.ReferenceClass;
import org.netbeans.jpa.modeler.spec.validator.SequenceGeneratorValidator;
import org.netbeans.jpa.modeler.spec.validator.TableGeneratorValidator;
import org.netbeans.jpa.modeler.spec.validator.table.TableValidator;
import org.netbeans.orm.converter.compiler.AssociationOverrideSnippet;
import org.netbeans.orm.converter.compiler.AssociationOverridesSnippet;
import org.netbeans.orm.converter.compiler.CacheableDefSnippet;
import org.netbeans.orm.converter.compiler.ColumnDefSnippet;
import org.netbeans.orm.converter.compiler.ColumnResultSnippet;
import org.netbeans.orm.converter.compiler.ConstructorResultSnippet;
import org.netbeans.orm.converter.compiler.EntityListenerSnippet;
import org.netbeans.orm.converter.compiler.EntityListenersSnippet;
import org.netbeans.orm.converter.compiler.EntityResultSnippet;
import org.netbeans.orm.converter.compiler.FieldResultSnippet;
import org.netbeans.orm.converter.compiler.ForeignKeySnippet;
import org.netbeans.orm.converter.compiler.GeneratedValueSnippet;
import org.netbeans.orm.converter.compiler.IdClassSnippet;
import org.netbeans.orm.converter.compiler.JoinColumnSnippet;
import org.netbeans.orm.converter.compiler.JoinTableSnippet;
import org.netbeans.orm.converter.compiler.NamedAttributeNodeSnippet;
import org.netbeans.orm.converter.compiler.NamedEntityGraphSnippet;
import org.netbeans.orm.converter.compiler.NamedEntityGraphsSnippet;
import org.netbeans.orm.converter.compiler.NamedNativeQueriesSnippet;
import org.netbeans.orm.converter.compiler.NamedNativeQuerySnippet;
import org.netbeans.orm.converter.compiler.NamedQueriesSnippet;
import org.netbeans.orm.converter.compiler.NamedQuerySnippet;
import org.netbeans.orm.converter.compiler.NamedStoredProcedureQueriesSnippet;
import org.netbeans.orm.converter.compiler.NamedStoredProcedureQuerySnippet;
import org.netbeans.orm.converter.compiler.NamedSubgraphSnippet;
import org.netbeans.orm.converter.compiler.PrimaryKeyJoinColumnSnippet;
import org.netbeans.orm.converter.compiler.PrimaryKeyJoinColumnsSnippet;
import org.netbeans.orm.converter.compiler.QueryHintSnippet;
import org.netbeans.orm.converter.compiler.SQLResultSetMappingSnippet;
import org.netbeans.orm.converter.compiler.SQLResultSetMappingsSnippet;
import org.netbeans.orm.converter.compiler.SecondaryTableSnippet;
import org.netbeans.orm.converter.compiler.SecondaryTablesSnippet;
import org.netbeans.orm.converter.compiler.SequenceGeneratorSnippet;
import org.netbeans.orm.converter.compiler.StoredProcedureParameterSnippet;
import org.netbeans.orm.converter.compiler.TableDefSnippet;
import org.netbeans.orm.converter.compiler.TableGeneratorSnippet;
import org.netbeans.orm.converter.compiler.TemporalSnippet;
import org.netbeans.orm.converter.compiler.UniqueConstraintSnippet;
import org.netbeans.orm.converter.compiler.def.VariableDefSnippet;
import static org.netbeans.orm.converter.generator.ClassGenerator.logger;
import org.netbeans.orm.converter.compiler.def.IdentifiableClassDefSnippet;
import org.netbeans.orm.converter.util.GeneratorUtil;

/**
 *
 * @author gaura
 */
public abstract class IdentifiableClassGenerator<T extends IdentifiableClassDefSnippet> extends ManagedClassGenerator<T> {

    public IdentifiableClassGenerator(T classDef, JavaEEVersion javaEEVersion) {
        super(classDef, javaEEVersion);
    }

    protected void processIdClass(IdClass parsedIdClass) {

        if (parsedIdClass == null) {
            return;
        }

        IdClassSnippet idClass = new IdClassSnippet();

        idClass.setValue(parsedIdClass.getClazz());
        idClass.setPackageName(packageName);

        classDef.setIdClass(idClass);
    }

    protected void processNamedEntityGraphs(List<NamedEntityGraph> parsedNamedEntityGraphs) {

        if (parsedNamedEntityGraphs == null || parsedNamedEntityGraphs.isEmpty()) {
            return;
        }

        NamedEntityGraphsSnippet namedEntityGraphs = new NamedEntityGraphsSnippet(repeatable);

        for (NamedEntityGraph parsedNamedEntityGraph : parsedNamedEntityGraphs) {
            if (parsedNamedEntityGraph.isEnable()) {
                NamedEntityGraphSnippet namedEntityGraph = new NamedEntityGraphSnippet();
                namedEntityGraph.setName(parsedNamedEntityGraph.getName());
                namedEntityGraph.setIncludeAllAttributes(parsedNamedEntityGraph.isIncludeAllAttributes());
                namedEntityGraph.setNamedAttributeNodes(getNamedAttributeNodes(parsedNamedEntityGraph.getNamedAttributeNode()));
                namedEntityGraph.setSubgraphs(getNamedSubgraphs(parsedNamedEntityGraph.getSubgraph()));
                namedEntityGraph.setSubclassSubgraphs(getNamedSubgraphs(parsedNamedEntityGraph.getSubclassSubgraph()));

                namedEntityGraphs.add(namedEntityGraph);
            }

        }
        if (!namedEntityGraphs.isEmpty()) {
            classDef.setNamedEntityGraphs(namedEntityGraphs);
        }
    }

    protected void processNamedStoredProcedureQueries(EntityMappings entityMappings, List<NamedStoredProcedureQuery> parsedNamedStoredProcedureQueries) {

        if (parsedNamedStoredProcedureQueries == null || parsedNamedStoredProcedureQueries.isEmpty()) {
            return;
        }

        NamedStoredProcedureQueriesSnippet namedStoredProcedureQueries = new NamedStoredProcedureQueriesSnippet(repeatable);

        for (NamedStoredProcedureQuery parsedNamedStoredProcedureQuery : parsedNamedStoredProcedureQueries) {
            if (parsedNamedStoredProcedureQuery.isEnable()) {
                NamedStoredProcedureQuerySnippet namedStoredProcedureQuery = new NamedStoredProcedureQuerySnippet();
                namedStoredProcedureQuery.setName(parsedNamedStoredProcedureQuery.getName());
                namedStoredProcedureQuery.setProcedureName(parsedNamedStoredProcedureQuery.getProcedureName());
                namedStoredProcedureQuery.setQueryHints(getQueryHints(parsedNamedStoredProcedureQuery.getHint()));
                namedStoredProcedureQuery.setResultClasses(getResultClasses(entityMappings, parsedNamedStoredProcedureQuery.getResultClass()));
                namedStoredProcedureQuery.setResultSetMappings(parsedNamedStoredProcedureQuery.getResultSetMapping());
                namedStoredProcedureQuery.setParameters(getStoredProcedureParameters(parsedNamedStoredProcedureQuery.getParameter()));

                namedStoredProcedureQueries.add(namedStoredProcedureQuery);
            }
        }
        if (!namedStoredProcedureQueries.isEmpty()) {
            classDef.setNamedStoredProcedureQueries(namedStoredProcedureQueries);
        }
    }

    protected List<String> getResultClasses(EntityMappings entityMappings, List<String> parsedgetResultClasses) {
        List<String> newParsedgetResultClasses = new ArrayList<>();

        for (String resultClass : parsedgetResultClasses) {
            if (resultClass.charAt(0) == '{' && resultClass.charAt(resultClass.length() - 1) == '}') {
                String id = resultClass.substring(1, resultClass.length() - 1);
                Entity entity = entityMappings.getEntity(id);
                newParsedgetResultClasses.add(packageName + "." + entity.getClazz());
            } else {
                newParsedgetResultClasses.add(resultClass);
            }
        }

        return newParsedgetResultClasses;
    }

    protected List<StoredProcedureParameterSnippet> getStoredProcedureParameters(List<StoredProcedureParameter> parsedStoredProcedureParameters) {

        if (parsedStoredProcedureParameters == null || parsedStoredProcedureParameters.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<StoredProcedureParameterSnippet> storedProcedureParameters = new ArrayList<>();

        for (StoredProcedureParameter parsedStoredProcedureParameter : parsedStoredProcedureParameters) {
            StoredProcedureParameterSnippet storedProcedureParameter = new StoredProcedureParameterSnippet();
            storedProcedureParameter.setName(parsedStoredProcedureParameter.getName());
            storedProcedureParameter.setType(parsedStoredProcedureParameter.getClazz());
            if (parsedStoredProcedureParameter.getMode() != null) {
                storedProcedureParameter.setMode(parsedStoredProcedureParameter.getMode().value());
            }
            storedProcedureParameters.add(storedProcedureParameter);
        }
        return storedProcedureParameters;
    }

    protected void processNamedNativeQueries(List<NamedNativeQuery> parsedNamedNativeQueries) {

        if (parsedNamedNativeQueries == null || parsedNamedNativeQueries.isEmpty()) {
            return;
        }

        NamedNativeQueriesSnippet namedNativeQueries = new NamedNativeQueriesSnippet(repeatable);

        for (NamedNativeQuery parsedNamedNativeQuery : parsedNamedNativeQueries) {
            if (parsedNamedNativeQuery.isEnable()) {
                List<QueryHintSnippet> queryHints = getQueryHints(parsedNamedNativeQuery.getHint());

                NamedNativeQuerySnippet namedNativeQuery = new NamedNativeQuerySnippet();
                namedNativeQuery.setName(parsedNamedNativeQuery.getName());
                namedNativeQuery.setQuery(parsedNamedNativeQuery.getQuery());
                namedNativeQuery.setResultClass(parsedNamedNativeQuery.getResultClass());
                namedNativeQuery.setPackageName(packageName);
                namedNativeQuery.setResultSetMapping(parsedNamedNativeQuery.getResultSetMapping());
                namedNativeQuery.setQueryHints(queryHints);
//            namedNativeQuery.setAttributeType(parsedNamedNativeQuery.getAttributeType());
                namedNativeQueries.add(namedNativeQuery);
            }
        }

        if (!namedNativeQueries.isEmpty()) {
            classDef.setNamedNativeQueries(namedNativeQueries);
        }
    }

    protected void processNamedQueries(List<NamedQuery> parsedNamedQueries) {

        if (parsedNamedQueries == null || parsedNamedQueries.isEmpty()) {
            return;
        }

        NamedQueriesSnippet namedQueries = new NamedQueriesSnippet(repeatable);

        for (NamedQuery parsedNamedQuery : parsedNamedQueries) {

            if (parsedNamedQuery.isEnable()) {
                List<QueryHintSnippet> queryHints = getQueryHints(parsedNamedQuery.getHint());

                NamedQuerySnippet namedQuery = new NamedQuerySnippet();
                namedQuery.setName(parsedNamedQuery.getName());
                namedQuery.setQuery(parsedNamedQuery.getQuery());
                //  namedQuery.setAttributeType(parsedNamedQuery.getAttributeType());
                namedQuery.setQueryHints(queryHints);
                namedQuery.setLockMode(parsedNamedQuery.getLockMode());

                namedQueries.add(namedQuery);
            }
        }

        if (!namedQueries.isEmpty()) {
            classDef.setNamedQueries(namedQueries);
        }
    }

    protected List<EntityResultSnippet> getEntityResults(List<EntityResult> parsedEntityResults) {

        if (parsedEntityResults == null || parsedEntityResults.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<EntityResultSnippet> entityResults = new ArrayList<>();

        for (EntityResult parsedEntityResult : parsedEntityResults) {

            List<FieldResultSnippet> fieldResults = getFieldResults(parsedEntityResult.getFieldResult());

            EntityResultSnippet entityResult = new EntityResultSnippet();
            entityResult.setDiscriminatorColumn(parsedEntityResult.getDiscriminatorColumn());
            entityResult.setEntityClass(parsedEntityResult.getEntityClass());
            entityResult.setPackageName(packageName);
            entityResult.setFieldResults(fieldResults);
            entityResults.add(entityResult);
        }

        return entityResults;
    }

    protected List<FieldResultSnippet> getFieldResults(
            List<FieldResult> parsedFieldResults) {

        if (parsedFieldResults == null || parsedFieldResults.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<FieldResultSnippet> fieldResults = new ArrayList<>();

        for (FieldResult parsedFieldResult : parsedFieldResults) {
            FieldResultSnippet fieldResult = new FieldResultSnippet();

            fieldResult.setColumn(parsedFieldResult.getColumn());
            fieldResult.setName(parsedFieldResult.getName());

            fieldResults.add(fieldResult);
        }
        return fieldResults;
    }

  

    protected List<PrimaryKeyJoinColumnSnippet> getPrimaryKeyJoinColumns(
            List<PrimaryKeyJoinColumn> parsedPrimaryKeyJoinColumns) {

        if (parsedPrimaryKeyJoinColumns == null || parsedPrimaryKeyJoinColumns.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns = new ArrayList<>();

        for (PrimaryKeyJoinColumn parsedPrimaryKeyJoinColumn : parsedPrimaryKeyJoinColumns) {
            PrimaryKeyJoinColumnSnippet primaryKeyJoinColumn = new PrimaryKeyJoinColumnSnippet();
            primaryKeyJoinColumn.setColumnDefinition(parsedPrimaryKeyJoinColumn.getColumnDefinition());
            primaryKeyJoinColumn.setName(parsedPrimaryKeyJoinColumn.getName());
            primaryKeyJoinColumn.setReferencedColumnName(parsedPrimaryKeyJoinColumn.getReferencedColumnName());
            primaryKeyJoinColumn.setForeignKey(getForeignKey(parsedPrimaryKeyJoinColumn.getForeignKey()));
            primaryKeyJoinColumns.add(primaryKeyJoinColumn);
        }

        return primaryKeyJoinColumns;
    }

    protected List<NamedAttributeNodeSnippet> getNamedAttributeNodes(
            List<NamedAttributeNode> parsedNamedAttributeNodes) {

        if (parsedNamedAttributeNodes == null || parsedNamedAttributeNodes.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<NamedAttributeNodeSnippet> namedAttributeNodes = new ArrayList<>();
        for (NamedAttributeNode parsedNamedAttributeNode : parsedNamedAttributeNodes) {
            NamedAttributeNodeSnippet namedAttributeNode = new NamedAttributeNodeSnippet();
            namedAttributeNode.setName(parsedNamedAttributeNode.getName());
            namedAttributeNode.setSubgraph(parsedNamedAttributeNode.getSubgraph());
            namedAttributeNode.setKeySubgraph(parsedNamedAttributeNode.getKeySubgraph());
            namedAttributeNodes.add(namedAttributeNode);
        }
        return namedAttributeNodes;
    }

    protected List<NamedSubgraphSnippet> getNamedSubgraphs(
            List<NamedSubgraph> parsedNamedSubgraphs) {

        if (parsedNamedSubgraphs == null || parsedNamedSubgraphs.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<NamedSubgraphSnippet> namedSubgraphs = new ArrayList<>();
        for (NamedSubgraph parsedNamedSubgraph : parsedNamedSubgraphs) {
            NamedSubgraphSnippet namedSubgraph = new NamedSubgraphSnippet();
            namedSubgraph.setName(parsedNamedSubgraph.getName());
            namedSubgraph.setNamedAttributeNode(getNamedAttributeNodes(parsedNamedSubgraph.getNamedAttributeNode()));
            namedSubgraph.setType(parsedNamedSubgraph.getClazz());
            namedSubgraphs.add(namedSubgraph);
        }
        return namedSubgraphs;
    }

    protected List<QueryHintSnippet> getQueryHints(
            List<QueryHint> parsedQueryHints) {

        if (parsedQueryHints == null || parsedQueryHints.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<QueryHintSnippet> queryHints = new ArrayList<>();

        for (QueryHint parsedQueryHint : parsedQueryHints) {
            QueryHintSnippet queryHint = new QueryHintSnippet();

            queryHint.setName(parsedQueryHint.getName());
            queryHint.setValue(parsedQueryHint.getValue());

            queryHints.add(queryHint);
        }
        return queryHints;
    }

    protected void processAssociationOverrides(Set<AssociationOverride> parsedAssociationOverrides) {

        if (parsedAssociationOverrides == null
                || parsedAssociationOverrides.isEmpty()) {
            return;
        }

        classDef.setAssociationOverrides(new AssociationOverridesSnippet(repeatable));

        for (AssociationOverride parsedAssociationOverride : parsedAssociationOverrides) {

            List<JoinColumnSnippet> joinColumnsList = getJoinColumns(parsedAssociationOverride.getJoinColumn(), false);
            JoinTableSnippet joinTable = getJoinTable(parsedAssociationOverride.getJoinTable());

            if ((joinTable == null || joinTable.isEmpty()) && joinColumnsList.isEmpty()) {
                continue;
            }
            AssociationOverrideSnippet associationOverride = new AssociationOverrideSnippet();
            associationOverride.setName(parsedAssociationOverride.getName());
            associationOverride.setJoinColumns(joinColumnsList);
            associationOverride.setJoinTable(joinTable);

            classDef.getAssociationOverrides().add(associationOverride);
        }
        if (classDef.getAssociationOverrides() != null && classDef.getAssociationOverrides().get().isEmpty()) {
            classDef.setAssociationOverrides(null);
        }
    }

    protected void processEntityListeners(EntityListeners parsedEntityListeners) {

        if (parsedEntityListeners == null) {
            return;
        }

        Set<ReferenceClass> parsedEntityListenersList = parsedEntityListeners.getEntityListener();

        List<EntityListenerSnippet> entityListeners = GeneratorUtil.processEntityListeners(parsedEntityListenersList, packageName);

        if (entityListeners.isEmpty()) {
            return;
        }

        classDef.setEntityListeners(new EntityListenersSnippet());
        classDef.getEntityListeners().setEntityListeners(entityListeners);
    }


    protected void processDefaultExcludeListeners(
            EmptyType parsedEmptyType) {

        if (parsedEmptyType != null) {
            classDef.setDefaultExcludeListener(true);
        }
    }

    protected void processExcludeSuperclassListeners(
            EmptyType parsedEmptyType) {

        if (parsedEmptyType != null) {
            classDef.setExcludeSuperClassListener(true);
        }
    }


    protected void processEmbeddedId(IdentifiableClass identifiableClass, EmbeddedId parsedEmbeddedId) {
        if (parsedEmbeddedId == null || !identifiableClass.isEmbeddedIdType()) {
            return;
        }

        VariableDefSnippet variableDef = getVariableDef(parsedEmbeddedId);
        variableDef.setEmbeddedId(true);
        /**
         * Filter if Embeddable class is used in case of derived entities. Refer
         * : JPA Spec 2.4.1.3 Example 5(b)
         */
        if (identifiableClass.isEmbeddedIdType() && parsedEmbeddedId.getConnectedClass() == null) {
            variableDef.setType(identifiableClass.getCompositePrimaryKeyClass());
        } else {
            variableDef.setType(parsedEmbeddedId.getAttributeType());
        }

        processInternalAttributeOverride(variableDef, parsedEmbeddedId.getAttributeOverride());
    }

    protected void processId(List<Id> parsedIds) {

//        if (parsedAttributes == null) {
//            return;
//        }
//
//        List<ParsedId> parsedIds = parsedAttributes.getId();
        for (Id parsedId : parsedIds) {
            VariableDefSnippet variableDef = getVariableDef(parsedId);
            variableDef.setType(parsedId.getAttributeType());
            variableDef.setFunctionalType(parsedId.isOptionalReturnType());
            variableDef.setPrimaryKey(true);

            Column parsedColumn = parsedId.getColumn();

            if (parsedColumn != null) {
                ColumnDefSnippet columnDef = getColumnDef(parsedColumn);
                variableDef.setColumnDef(columnDef);
            }
            GeneratedValue parsedGeneratedValue = parsedId.getGeneratedValue();
            if (parsedGeneratedValue != null && parsedGeneratedValue.getStrategy() != null) {
                GeneratedValueSnippet generatedValue = new GeneratedValueSnippet();

                generatedValue.setGenerator(parsedGeneratedValue.getGenerator());
                generatedValue.setStrategy("GenerationType." + parsedGeneratedValue.getStrategy().value());

                variableDef.setGeneratedValue(generatedValue);

                SequenceGenerator parsedSequenceGenerator
                        = parsedId.getSequenceGenerator();

                if (parsedSequenceGenerator != null) {
                    SequenceGeneratorSnippet sequenceGenerator = processSequenceGenerator(parsedSequenceGenerator);
                    variableDef.setSequenceGenerator(sequenceGenerator);
                }

                TableGenerator parsedTableGenerator = parsedId.getTableGenerator();
                if (parsedTableGenerator != null) {
                    variableDef.setTableGenerator(processTableGenerator(parsedTableGenerator));
                }
            }

            TemporalType parsedTemporalType = parsedId.getTemporal();
            TemporalSnippet temporal = null;
            if (parsedTemporalType != null) {
                temporal = new TemporalSnippet();
                temporal.setValue(parsedTemporalType);
            }
            variableDef.setTemporal(temporal);
        }
    }


    protected void processVersion(List<Version> parsedVersions) {
        if (parsedVersions == null) {
            return;
        }
        for (Version parsedVersion : parsedVersions) {
            VariableDefSnippet variableDef = getVariableDef(parsedVersion);

            ColumnDefSnippet columnDef = getColumnDef(parsedVersion.getColumn());
            variableDef.setType(parsedVersion.getAttributeType());
            variableDef.setFunctionalType(parsedVersion.isOptionalReturnType());
            variableDef.setVersion(true);
            variableDef.setColumnDef(columnDef);

            TemporalType parsedTemporalType = parsedVersion.getTemporal();
            TemporalSnippet temporal = null;
            if (parsedTemporalType != null) {
                temporal = new TemporalSnippet();
                temporal.setValue(parsedTemporalType);
            }
            variableDef.setTemporal(temporal);
        }
    }

    protected void processPrimaryKeyJoinColumns(List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns, ForeignKeySnippet primaryKeyForeignKey) {
        if (primaryKeyJoinColumns == null || primaryKeyJoinColumns.isEmpty()) {
            return;
        }
        classDef.setPrimaryKeyJoinColumns(new PrimaryKeyJoinColumnsSnippet(repeatable));
        classDef.getPrimaryKeyJoinColumns().setPrimaryKeyJoinColumns(primaryKeyJoinColumns);
        classDef.getPrimaryKeyJoinColumns().setForeignKey(primaryKeyForeignKey);
    }

    protected void processSecondaryTable(List<SecondaryTable> parsedSecondaryTables) {

        if (parsedSecondaryTables == null || parsedSecondaryTables.isEmpty()) {
            return;
        }

        classDef.setSecondaryTables(new SecondaryTablesSnippet(repeatable));

        for (SecondaryTable parsedSecondaryTable : parsedSecondaryTables) {
            List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns
                    = getPrimaryKeyJoinColumns(parsedSecondaryTable.getPrimaryKeyJoinColumn());

            List<UniqueConstraintSnippet> uniqueConstraints = getUniqueConstraints(
                    parsedSecondaryTable.getUniqueConstraint());

            SecondaryTableSnippet secondaryTable = new SecondaryTableSnippet();
            secondaryTable.setCatalog(parsedSecondaryTable.getCatalog());
            secondaryTable.setName(parsedSecondaryTable.getName());
            secondaryTable.setSchema(parsedSecondaryTable.getSchema());
            secondaryTable.setUniqueConstraints(uniqueConstraints);
            secondaryTable.setIndices(getIndexes(parsedSecondaryTable.getIndex()));
            secondaryTable.setPrimaryKeyJoinColumns(primaryKeyJoinColumns);
            secondaryTable.setForeignKey(getForeignKey(parsedSecondaryTable.getForeignKey()));

            classDef.getSecondaryTables().add(secondaryTable);
        }
    }

    protected SequenceGeneratorSnippet processSequenceGenerator(SequenceGenerator parsedSequenceGenerator) {

        if (parsedSequenceGenerator == null || SequenceGeneratorValidator.isEmpty(parsedSequenceGenerator)) {
            return null;
        }

        SequenceGeneratorSnippet sequenceGenerator = new SequenceGeneratorSnippet();

        sequenceGenerator.setCatalog(parsedSequenceGenerator.getCatalog());
        sequenceGenerator.setSchema(parsedSequenceGenerator.getSchema());
        sequenceGenerator.setName(parsedSequenceGenerator.getName());
        sequenceGenerator.setSequenceName(parsedSequenceGenerator.getSequenceName());

        if (parsedSequenceGenerator.getAllocationSize() != null) {
            sequenceGenerator.setAllocationSize(
                    parsedSequenceGenerator.getAllocationSize());
        }

        if (parsedSequenceGenerator.getInitialValue() != null) {
            sequenceGenerator.setInitialValue(
                    parsedSequenceGenerator.getInitialValue());
        }

        return sequenceGenerator;
    }

    protected void processSequenceGeneratorEntity(SequenceGenerator parsedSequenceGenerator) {

        SequenceGeneratorSnippet sequenceGenerator = processSequenceGenerator(parsedSequenceGenerator);

        if (sequenceGenerator == null) {
            return;
        }

        VariableDefSnippet variableDef = null;
        boolean found = false;
        //The name of the SequenceGenerator must match the generator name in a
        //GeneratedValue with its strategy set to SEQUENCE.
        for (Map.Entry<String, VariableDefSnippet> entry : variables.entrySet()) {
            variableDef = entry.getValue();

            if (variableDef.getGeneratedValue() != null
                    && variableDef.getGeneratedValue().getGenerator().equals(
                            sequenceGenerator.getName())) {

                found = true;
                break;
            }
        }
        if (found) {
            variableDef.setSequenceGenerator(sequenceGenerator);
        } else {
            logger.log(Level.WARNING, "Ignoring : Cannot find variable for "
                    + "Sequence generator :" + sequenceGenerator.getName());
        }
    }

    protected void processSqlResultSetMapping(Set<SqlResultSetMapping> parsedSqlResultSetMappings) {

        if (parsedSqlResultSetMappings == null
                || parsedSqlResultSetMappings.isEmpty()) {
            return;
        }

        classDef.setSQLResultSetMappings(new SQLResultSetMappingsSnippet(repeatable));

        for (SqlResultSetMapping parsedSqlResultSetMapping : parsedSqlResultSetMappings) {
            SQLResultSetMappingSnippet sqlResultSetMapping = new SQLResultSetMappingSnippet();

            List<ColumnResultSnippet> columnResults = getColumnResults(
                    parsedSqlResultSetMapping.getColumnResult());

            List<EntityResultSnippet> entityResults = getEntityResults(
                    parsedSqlResultSetMapping.getEntityResult());

            List<ConstructorResultSnippet> constructorResults = getConstructorResults(
                    parsedSqlResultSetMapping.getConstructorResult());

            sqlResultSetMapping.setColumnResults(columnResults);
            sqlResultSetMapping.setEntityResults(entityResults);
            sqlResultSetMapping.setConstructorResults(constructorResults);
            sqlResultSetMapping.setName(parsedSqlResultSetMapping.getName());

            classDef.getSQLResultSetMappings().add(sqlResultSetMapping);
        }
    }

    protected void processTable(Table parsedTable) {

        if (parsedTable == null || TableValidator.isEmpty(parsedTable)) {
            return;
        }

        TableDefSnippet table = new TableDefSnippet();

        table.setCatalog(parsedTable.getCatalog());
        table.setName(parsedTable.getName());
        table.setSchema(parsedTable.getSchema());
        table.setUniqueConstraints(getUniqueConstraints(parsedTable.getUniqueConstraint()));
        table.setIndices(getIndexes(parsedTable.getIndex()));

        classDef.setTableDef(table);
    }

    protected void processCacheable(Boolean cacheable) {

        if (cacheable == null) { //Implicit Disable (!Force Disable)
            return;
        }
        CacheableDefSnippet snippet = new CacheableDefSnippet(cacheable);
        classDef.setCacheableDef(snippet);
    }

    protected TableGeneratorSnippet processTableGenerator(TableGenerator parsedTableGenerator) {

        if (parsedTableGenerator == null || TableGeneratorValidator.isEmpty(parsedTableGenerator)) {
            return null;
        }

        TableGeneratorSnippet tableGenerator = new TableGeneratorSnippet();

        if (parsedTableGenerator.getAllocationSize() != null) {
            tableGenerator.setAllocationSize(
                    parsedTableGenerator.getAllocationSize());
        }

        if (parsedTableGenerator.getInitialValue() != null) {
            tableGenerator.setInitialValue(
                    parsedTableGenerator.getInitialValue());
        }

        tableGenerator.setCatalog(parsedTableGenerator.getCatalog());
        tableGenerator.setName(parsedTableGenerator.getName());
        tableGenerator.setPkColumnName(parsedTableGenerator.getPkColumnName());
        tableGenerator.setPkColumnValue(
                parsedTableGenerator.getPkColumnValue());
        tableGenerator.setSchema(parsedTableGenerator.getSchema());
        tableGenerator.setTable(parsedTableGenerator.getTable());
        tableGenerator.setValueColumnName(
                parsedTableGenerator.getValueColumnName());
        tableGenerator.setUniqueConstraints(getUniqueConstraints(
                parsedTableGenerator.getUniqueConstraint()));
        tableGenerator.setIndices(getIndexes(parsedTableGenerator.getIndex()));

        return tableGenerator;
    }

    protected void processTableGeneratorEntity(TableGenerator parsedTableGenerator) {

        TableGeneratorSnippet tableGenerator = processTableGenerator(parsedTableGenerator);
        if (tableGenerator == null) {
            return;
        }

        VariableDefSnippet variableDef = null;
        boolean found = false;
        //The name of the TableGenerator must match the generator name in a
        //GeneratedValue with its strategy set to TABLE. The scope of the
        //generator name is global to the persistence unit
        //(across all generator types).
        for (Map.Entry<String, VariableDefSnippet> entry : variables.entrySet()) {
            variableDef = entry.getValue();

            if (variableDef.getGeneratedValue() != null
                    && variableDef.getGeneratedValue().getGenerator().equals(
                            parsedTableGenerator.getName())) {
                found = true;
                break;
            }
        }

        if (found) {
            variableDef.setTableGenerator(tableGenerator);
        } else {
            logger.log(Level.WARNING, "Ignoring : Cannot find variable for Table generator :{0}", tableGenerator.getName());
        }
    }



    protected List<ColumnResultSnippet> getColumnResults(
            List<ColumnResult> parsedColumnResults) {

        if (parsedColumnResults == null || parsedColumnResults.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<ColumnResultSnippet> columnResults = new ArrayList<>();

        for (ColumnResult parsedColumnResult : parsedColumnResults) {
            ColumnResultSnippet columnResult = new ColumnResultSnippet();

            columnResult.setName(parsedColumnResult.getName());
            columnResult.setType(parsedColumnResult.getClazz());
            columnResults.add(columnResult);
        }

        return columnResults;
    }

    protected List<ConstructorResultSnippet> getConstructorResults(
            List<ConstructorResult> parsedConstructorResults) {

        if (parsedConstructorResults == null || parsedConstructorResults.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<ConstructorResultSnippet> constructorResults = new ArrayList<>();

        for (ConstructorResult parsedConstructorResult : parsedConstructorResults) {
            ConstructorResultSnippet constructorResult = new ConstructorResultSnippet();
            List<ColumnResultSnippet> columnResults = getColumnResults(parsedConstructorResult.getColumn());
            constructorResult.setColumnResults(columnResults);
            constructorResult.setTargetClass(parsedConstructorResult.getTargetClass());
            constructorResult.setPackageName(packageName);
            constructorResults.add(constructorResult);
        }

        return constructorResults;
    }

}
