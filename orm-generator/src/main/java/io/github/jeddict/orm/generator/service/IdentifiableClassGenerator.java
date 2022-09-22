/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.orm.generator.service;

import io.github.jeddict.jpa.spec.AssociationOverride;
import io.github.jeddict.jpa.spec.Column;
import io.github.jeddict.jpa.spec.ColumnResult;
import io.github.jeddict.jpa.spec.ConstructorResult;
import io.github.jeddict.jpa.spec.EmbeddedId;
import io.github.jeddict.jpa.spec.EmptyType;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityListeners;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.EntityResult;
import io.github.jeddict.jpa.spec.FieldResult;
import io.github.jeddict.jpa.spec.GeneratedValue;
import io.github.jeddict.jpa.spec.GenerationType;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.IdClass;
import io.github.jeddict.jpa.spec.IdentifiableClass;
import io.github.jeddict.jpa.spec.NamedAttributeNode;
import io.github.jeddict.jpa.spec.NamedEntityGraph;
import io.github.jeddict.jpa.spec.NamedNativeQuery;
import io.github.jeddict.jpa.spec.NamedQuery;
import io.github.jeddict.jpa.spec.NamedStoredProcedureQuery;
import io.github.jeddict.jpa.spec.NamedSubgraph;
import io.github.jeddict.jpa.spec.QueryHint;
import io.github.jeddict.jpa.spec.SecondaryTable;
import io.github.jeddict.jpa.spec.SequenceGenerator;
import io.github.jeddict.jpa.spec.SqlResultSetMapping;
import io.github.jeddict.jpa.spec.StoredProcedureParameter;
import io.github.jeddict.jpa.spec.Table;
import io.github.jeddict.jpa.spec.TableGenerator;
import io.github.jeddict.jpa.spec.TemporalType;
import io.github.jeddict.jpa.spec.Version;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.ReferenceClass;
import io.github.jeddict.jpa.spec.validator.SequenceGeneratorValidator;
import io.github.jeddict.jpa.spec.validator.TableGeneratorValidator;
import io.github.jeddict.jpa.spec.validator.table.TableValidator;
import io.github.jeddict.orm.generator.compiler.AssociationOverrideSnippet;
import io.github.jeddict.orm.generator.compiler.AssociationOverridesSnippet;
import io.github.jeddict.orm.generator.compiler.CacheableSnippet;
import io.github.jeddict.orm.generator.compiler.ColumnSnippet;
import io.github.jeddict.orm.generator.compiler.ColumnResultSnippet;
import io.github.jeddict.orm.generator.compiler.ConstructorResultSnippet;
import io.github.jeddict.orm.generator.compiler.EntityListenerSnippet;
import io.github.jeddict.orm.generator.compiler.EntityListenersSnippet;
import io.github.jeddict.orm.generator.compiler.EntityResultSnippet;
import io.github.jeddict.orm.generator.compiler.FieldResultSnippet;
import io.github.jeddict.orm.generator.compiler.ForeignKeySnippet;
import io.github.jeddict.orm.generator.compiler.GeneratedValueSnippet;
import io.github.jeddict.orm.generator.compiler.IdClassSnippet;
import io.github.jeddict.orm.generator.compiler.IdSnippet;
import io.github.jeddict.orm.generator.compiler.JoinColumnSnippet;
import io.github.jeddict.orm.generator.compiler.JoinTableSnippet;
import io.github.jeddict.orm.generator.compiler.NamedAttributeNodeSnippet;
import io.github.jeddict.orm.generator.compiler.NamedEntityGraphSnippet;
import io.github.jeddict.orm.generator.compiler.NamedEntityGraphsSnippet;
import io.github.jeddict.orm.generator.compiler.NamedNativeQueriesSnippet;
import io.github.jeddict.orm.generator.compiler.NamedNativeQuerySnippet;
import io.github.jeddict.orm.generator.compiler.NamedQueriesSnippet;
import io.github.jeddict.orm.generator.compiler.NamedQuerySnippet;
import io.github.jeddict.orm.generator.compiler.NamedStoredProcedureQueriesSnippet;
import io.github.jeddict.orm.generator.compiler.NamedStoredProcedureQuerySnippet;
import io.github.jeddict.orm.generator.compiler.NamedSubgraphSnippet;
import io.github.jeddict.orm.generator.compiler.PrimaryKeyJoinColumnSnippet;
import io.github.jeddict.orm.generator.compiler.PrimaryKeyJoinColumnsSnippet;
import io.github.jeddict.orm.generator.compiler.QueryHintSnippet;
import io.github.jeddict.orm.generator.compiler.SQLResultSetMappingSnippet;
import io.github.jeddict.orm.generator.compiler.SQLResultSetMappingsSnippet;
import io.github.jeddict.orm.generator.compiler.SecondaryTableSnippet;
import io.github.jeddict.orm.generator.compiler.SecondaryTablesSnippet;
import io.github.jeddict.orm.generator.compiler.SequenceGeneratorSnippet;
import io.github.jeddict.orm.generator.compiler.StoredProcedureParameterSnippet;
import io.github.jeddict.orm.generator.compiler.TableSnippet;
import io.github.jeddict.orm.generator.compiler.TableGeneratorSnippet;
import io.github.jeddict.orm.generator.compiler.TemporalSnippet;
import io.github.jeddict.orm.generator.compiler.UniqueConstraintSnippet;
import io.github.jeddict.orm.generator.compiler.VersionSnippet;
import io.github.jeddict.orm.generator.compiler.def.IdentifiableClassDefSnippet;
import io.github.jeddict.orm.generator.compiler.def.VariableDefSnippet;
import static io.github.jeddict.orm.generator.service.ClassGenerator.logger;
import io.github.jeddict.orm.generator.util.GeneratorUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import static java.util.logging.Level.WARNING;

/**
 *
 * @author gaura
 */
public abstract class IdentifiableClassGenerator<T extends IdentifiableClassDefSnippet> extends ManagedClassGenerator<T> {

    public IdentifiableClassGenerator(T classDef) {
        super(classDef);
    }

    @Override
    protected VariableDefSnippet processVariable(Attribute attr) {
        if (attr instanceof Id) {
            return processId((Id) attr);
        } else if (attr instanceof Version) {
            return processVersion((Version) attr);
        } else if (attr instanceof EmbeddedId
                && attr.getJavaClass() instanceof IdentifiableClass) {
            return processEmbeddedId((IdentifiableClass) attr.getJavaClass(), (EmbeddedId) attr);
        } else {
            return super.processVariable(attr);
        }
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
            return Collections.<StoredProcedureParameterSnippet>emptyList();
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
            return Collections.<EntityResultSnippet>emptyList();
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
            return Collections.<FieldResultSnippet>emptyList();
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

    protected List<NamedAttributeNodeSnippet> getNamedAttributeNodes(
            List<NamedAttributeNode> parsedNamedAttributeNodes) {

        if (parsedNamedAttributeNodes == null || parsedNamedAttributeNodes.isEmpty()) {
            return Collections.<NamedAttributeNodeSnippet>emptyList();
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
            return Collections.<NamedSubgraphSnippet>emptyList();
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
            return Collections.<QueryHintSnippet>emptyList();
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

    protected void processAssociationOverrides(Set<AssociationOverride> associationOverrides) {

        if (associationOverrides == null
                || associationOverrides.isEmpty()) {
            return;
        }

        classDef.setAssociationOverrides(new AssociationOverridesSnippet(repeatable));

        Set<AssociationOverride> sortedAssociationOverrrides
                = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
        sortedAssociationOverrrides.addAll(associationOverrides);
        for (AssociationOverride associationOverride : sortedAssociationOverrrides) {

            List<JoinColumnSnippet> joinColumnsList = processJoinColumns(associationOverride.getJoinColumn(), false);
            JoinTableSnippet joinTable = getJoinTable(associationOverride.getJoinTable());

            if ((joinTable == null || joinTable.isEmpty()) && joinColumnsList.isEmpty()) {
                continue;
            }
            AssociationOverrideSnippet associationOverrideSnippet = new AssociationOverrideSnippet();
            associationOverrideSnippet.setName(associationOverride.getName());
            associationOverrideSnippet.setJoinColumns(joinColumnsList);
            associationOverrideSnippet.setJoinTable(joinTable);

            classDef.getAssociationOverrides().add(associationOverrideSnippet);
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

    protected VariableDefSnippet processEmbeddedId(IdentifiableClass identifiableClass, EmbeddedId parsedEmbeddedId) {
        if (parsedEmbeddedId == null || !identifiableClass.isEmbeddedIdType()) {
            return null;
        }

        VariableDefSnippet variableDef = getVariableDef(parsedEmbeddedId);
        /**
         * Filter if Embeddable class is used in case of derived entities. Refer
         * : JPA Spec 2.4.1.3 Example 5(b)
         */
        if (identifiableClass.isEmbeddedIdType() && parsedEmbeddedId.getConnectedClass() == null) {
            variableDef.setType(identifiableClass.getCompositePrimaryKeyClass());
        } else {
            variableDef.setType(parsedEmbeddedId.getAttributeType());
        }

        if (!classDef.isNoSQL()) {
            variableDef.setEmbeddedId(true);
            processInternalAttributeOverride(variableDef, parsedEmbeddedId.getAttributeOverride());
        }
        return variableDef;
    }

    protected VariableDefSnippet processId(Id id) {
        VariableDefSnippet variableDef = getVariableDef(id);
        variableDef.setType(id.getAttributeType());
        IdSnippet idSnippet = new IdSnippet();
        idSnippet.setNoSQL(classDef.isNoSQL());
        variableDef.setId(idSnippet);
        Column column = id.getColumn();

        if (!classDef.isNoSQL()) {

            if (column != null) {
                ColumnSnippet columnSnippet = processColumn(column);
                variableDef.setColumn(columnSnippet);
            }
            GeneratedValue parsedGeneratedValue = id.getGeneratedValue();
            if (parsedGeneratedValue != null && parsedGeneratedValue.getStrategy() != null) {

                GeneratedValueSnippet generatedValue = new GeneratedValueSnippet();
                generatedValue.setGenerator(parsedGeneratedValue.getGenerator());
                if (parsedGeneratedValue.getStrategy() != GenerationType.DEFAULT) {
                    generatedValue.setStrategy("GenerationType." + parsedGeneratedValue.getStrategy().value());
                }
                variableDef.setGeneratedValue(generatedValue);

                SequenceGenerator parsedSequenceGenerator = id.getSequenceGenerator();
                if (parsedSequenceGenerator != null) {
                    SequenceGeneratorSnippet sequenceGenerator = processSequenceGenerator(parsedSequenceGenerator);
                    variableDef.setSequenceGenerator(sequenceGenerator);
                }

                TableGenerator parsedTableGenerator = id.getTableGenerator();
                if (parsedTableGenerator != null) {
                    variableDef.setTableGenerator(processTableGenerator(parsedTableGenerator));
                }
            }

            TemporalType parsedTemporalType = id.getTemporal();
            TemporalSnippet temporal = null;
            if (parsedTemporalType != null) {
                temporal = new TemporalSnippet();
                temporal.setValue(parsedTemporalType);
            }
            variableDef.setTemporal(temporal);
        } else {
            if (column != null) {
                idSnippet.setName(column.getName());
            }
        }
        return variableDef;
    }

    protected VariableDefSnippet processVersion(Version parsedVersion) {
        VariableDefSnippet variableDef = getVariableDef(parsedVersion);
        variableDef.setType(parsedVersion.getAttributeType());
        
        if (!classDef.isNoSQL()) {
            VersionSnippet version = new VersionSnippet();
            variableDef.setVersion(version);
            ColumnSnippet columnDef = processColumn(parsedVersion.getColumn());
            variableDef.setColumn(columnDef);

            TemporalType parsedTemporalType = parsedVersion.getTemporal();
            TemporalSnippet temporal = null;
            if (parsedTemporalType != null) {
                temporal = new TemporalSnippet();
                temporal.setValue(parsedTemporalType);
            }
            variableDef.setTemporal(temporal);
        }
        return variableDef;
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

            List<UniqueConstraintSnippet> uniqueConstraints = processUniqueConstraints(
                    parsedSecondaryTable.getUniqueConstraint());

            SecondaryTableSnippet secondaryTable = new SecondaryTableSnippet();
            secondaryTable.setCatalog(parsedSecondaryTable.getCatalog());
            secondaryTable.setName(parsedSecondaryTable.getName());
            secondaryTable.setSchema(parsedSecondaryTable.getSchema());
            secondaryTable.setUniqueConstraints(uniqueConstraints);
            secondaryTable.setIndices(processIndexes(parsedSecondaryTable.getIndex()));
            secondaryTable.setPrimaryKeyJoinColumns(primaryKeyJoinColumns);
            secondaryTable.setForeignKey(processForeignKey(parsedSecondaryTable.getForeignKey()));

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
            logger.log(WARNING, "Ignoring : Cannot find variable for Sequence generator :{0}", sequenceGenerator.getName());
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

        TableSnippet table = new TableSnippet();

        table.setCatalog(parsedTable.getCatalog());
        table.setName(parsedTable.getName());
        table.setSchema(parsedTable.getSchema());
        table.setUniqueConstraints(processUniqueConstraints(parsedTable.getUniqueConstraint()));
        table.setIndices(processIndexes(parsedTable.getIndex()));

        classDef.setTableDef(table);
    }

    protected void processCacheable(Boolean cacheable) {

        if (cacheable == null) { //Implicit Disable (!Force Disable)
            return;
        }
        CacheableSnippet snippet = new CacheableSnippet(cacheable);
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
        tableGenerator.setUniqueConstraints(processUniqueConstraints(
                parsedTableGenerator.getUniqueConstraint()));
        tableGenerator.setIndices(processIndexes(parsedTableGenerator.getIndex()));

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
            logger.log(WARNING, "Ignoring : Cannot find variable for Table generator :{0}", tableGenerator.getName());
        }
    }

    protected List<ColumnResultSnippet> getColumnResults(
            List<ColumnResult> parsedColumnResults) {

        if (parsedColumnResults == null || parsedColumnResults.isEmpty()) {
            return Collections.<ColumnResultSnippet>emptyList();
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
            return Collections.<ConstructorResultSnippet>emptyList();
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
