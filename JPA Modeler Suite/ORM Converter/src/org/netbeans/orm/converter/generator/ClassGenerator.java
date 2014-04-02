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
package org.netbeans.orm.converter.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.AttributeOverride;
import org.netbeans.jpa.modeler.spec.Attributes;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.CascadeType;
import org.netbeans.jpa.modeler.spec.CollectionTable;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.ColumnResult;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.EmptyType;
import org.netbeans.jpa.modeler.spec.EntityListener;
import org.netbeans.jpa.modeler.spec.EntityListeners;
import org.netbeans.jpa.modeler.spec.EntityResult;
import org.netbeans.jpa.modeler.spec.EnumType;
import org.netbeans.jpa.modeler.spec.FetchType;
import org.netbeans.jpa.modeler.spec.FieldResult;
import org.netbeans.jpa.modeler.spec.GeneratedValue;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.IdClass;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.JoinTable;
import org.netbeans.jpa.modeler.spec.Lob;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.NamedNativeQuery;
import org.netbeans.jpa.modeler.spec.NamedQuery;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.PrimaryKeyJoinColumn;
import org.netbeans.jpa.modeler.spec.QueryHint;
import org.netbeans.jpa.modeler.spec.SecondaryTable;
import org.netbeans.jpa.modeler.spec.SequenceGenerator;
import org.netbeans.jpa.modeler.spec.SqlResultSetMapping;
import org.netbeans.jpa.modeler.spec.Table;
import org.netbeans.jpa.modeler.spec.TableGenerator;
import org.netbeans.jpa.modeler.spec.TemporalType;
import org.netbeans.jpa.modeler.spec.Transient;
import org.netbeans.jpa.modeler.spec.UniqueConstraint;
import org.netbeans.jpa.modeler.spec.Version;
import org.netbeans.orm.converter.compiler.AssociationOverrideSnippet;
import org.netbeans.orm.converter.compiler.AssociationOverridesSnippet;
import org.netbeans.orm.converter.compiler.AttributeOverrideSnippet;
import org.netbeans.orm.converter.compiler.AttributeOverridesSnippet;
import org.netbeans.orm.converter.compiler.BasicSnippet;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.compiler.CollectionTableSnippet;
import org.netbeans.orm.converter.compiler.ColumnDefSnippet;
import org.netbeans.orm.converter.compiler.ColumnResultSnippet;
import org.netbeans.orm.converter.compiler.ElementCollectionSnippet;
import org.netbeans.orm.converter.compiler.EntityListenerSnippet;
import org.netbeans.orm.converter.compiler.EntityListenersSnippet;
import org.netbeans.orm.converter.compiler.EntityResultSnippet;
import org.netbeans.orm.converter.compiler.EnumeratedSnippet;
import org.netbeans.orm.converter.compiler.FieldResultSnippet;
import org.netbeans.orm.converter.compiler.GeneratedValueSnippet;
import org.netbeans.orm.converter.compiler.IdClassSnippet;
import org.netbeans.orm.converter.compiler.JoinColumnSnippet;
import org.netbeans.orm.converter.compiler.JoinColumnsSnippet;
import org.netbeans.orm.converter.compiler.JoinTableSnippet;
import org.netbeans.orm.converter.compiler.ManyToManySnippet;
import org.netbeans.orm.converter.compiler.ManyToOneSnippet;
import org.netbeans.orm.converter.compiler.NamedNativeQueriesSnippet;
import org.netbeans.orm.converter.compiler.NamedNativeQuerySnippet;
import org.netbeans.orm.converter.compiler.NamedQueriesSnippet;
import org.netbeans.orm.converter.compiler.NamedQueryDefSnippet;
import org.netbeans.orm.converter.compiler.OneToManySnippet;
import org.netbeans.orm.converter.compiler.OneToOneSnippet;
import org.netbeans.orm.converter.compiler.PrimaryKeyJoinColumnSnippet;
import org.netbeans.orm.converter.compiler.PrimaryKeyJoinColumnsSnippet;
import org.netbeans.orm.converter.compiler.QueryHintSnippet;
import org.netbeans.orm.converter.compiler.SQLResultSetMappingSnippet;
import org.netbeans.orm.converter.compiler.SQLResultSetMappingsSnippet;
import org.netbeans.orm.converter.compiler.SecondaryTableSnippet;
import org.netbeans.orm.converter.compiler.SecondaryTablesSnippet;
import org.netbeans.orm.converter.compiler.SequenceGeneratorSnippet;
import org.netbeans.orm.converter.compiler.TableDefSnippet;
import org.netbeans.orm.converter.compiler.TableGeneratorSnippet;
import org.netbeans.orm.converter.compiler.UniqueConstraintSnippet;
import org.netbeans.orm.converter.compiler.VariableDefSnippet;
import org.netbeans.orm.converter.util.ORMConvLogger;

public abstract class ClassGenerator {

    private static final String TEMPORAL_TYPE_PREFIX = "TemporalType.";

    private static Logger logger = ORMConvLogger.getLogger(ClassGenerator.class);

    protected String packageName = null;

    protected ClassDefSnippet classDef = new ClassDefSnippet();

    protected Map<String, VariableDefSnippet> variables
            = new HashMap<String, VariableDefSnippet>();

    public abstract ClassDefSnippet getClassDef();

    protected ColumnDefSnippet getColumnDef(Column column) {

        if (column == null) {
            return null;
        }

        ColumnDefSnippet columnDef = new ColumnDefSnippet();

        columnDef.setColumnDefinition(column.getColumnDefinition());
        columnDef.setName(column.getName());
        columnDef.setTable(column.getTable());
        columnDef.setInsertable(column.getInsertable()); // line added by gaurav gupta
        columnDef.setNullable(column.getNullable());// line added by gaurav gupta
        columnDef.setUnique(column.getUnique());// line added by gaurav gupta
        columnDef.setUpdatable(column.getUpdatable());// line added by gaurav gupta

        if (column.getLength() != null) {
            columnDef.setLength(column.getLength());
        }

        if (column.getPrecision() != null) {
            columnDef.setPrecision(column.getPrecision());
        }

        if (column.getScale() != null) {
            columnDef.setScale(column.getScale());
        }
        if (columnDef.isEmptyObject()) {
            columnDef = null;
        }

        return columnDef;
    }

    protected VariableDefSnippet getVariableDef(String name) {
        VariableDefSnippet variableDef = variables.get(name);

        if (variableDef == null) {
            variableDef = new VariableDefSnippet();
            variableDef.setName(name);
            variables.put(name, variableDef);
        }

        return variableDef;
    }

    protected void processBasic(List<Basic> parsedBasics) {
        if (parsedBasics == null) {
            return;
        }
        for (Basic parsedBasic : parsedBasics) {
            ColumnDefSnippet columnDef = getColumnDef(parsedBasic.getColumn());

            EnumType parsedEnumType = parsedBasic.getEnumerated();

            EnumeratedSnippet enumerated = null;

            if (parsedEnumType != null) {
                enumerated = new EnumeratedSnippet();

                if (parsedEnumType.equals(EnumType.ORDINAL)) {
                    enumerated.setValue(EnumeratedSnippet.TYPE_ORDINAL);
                } else {
                    enumerated.setValue(EnumeratedSnippet.TYPE_STRING);
                }
            }

            FetchType parsedFetchType = parsedBasic.getFetch();
            BasicSnippet basic = new BasicSnippet();

            if (parsedFetchType != null) {
                basic.setFetchType(parsedFetchType.value());
            }
            if (parsedBasic.getOptional() != null) {
                basic.setOptional(parsedBasic.getOptional());
            }

            TemporalType parsedTemporal = parsedBasic.getTemporal();
            Lob parsedLob = parsedBasic.getLob();

            VariableDefSnippet variableDef = getVariableDef(parsedBasic.getName());

            variableDef.setBasic(basic);
            variableDef.setColumnDef(columnDef);
            variableDef.setEnumerated(enumerated);
            variableDef.setType(parsedBasic.getAttributeType());

            if (parsedTemporal != null) {
                variableDef.setTemporal(true);
                variableDef.setTemporalType(
                        TEMPORAL_TYPE_PREFIX + parsedTemporal.value());
            }

            if (parsedLob != null) {
                variableDef.setLob(true);
            }
        }
    }

    protected void processElementCollection(List<ElementCollection> parsedElementCollections) {
        if (parsedElementCollections == null) {
            return;
        }
        for (ElementCollection parsedElementCollection : parsedElementCollections) {
            ColumnDefSnippet columnDef = getColumnDef(parsedElementCollection.getColumn());
            EnumType enumType = parsedElementCollection.getEnumerated();

            CollectionTableSnippet collectionTable = getCollectionTable(parsedElementCollection.getCollectionTable());

            EnumeratedSnippet enumerated = null;

            if (enumType != null) {
                enumerated = new EnumeratedSnippet();

                if (enumType.equals(EnumType.ORDINAL)) {
                    enumerated.setValue(EnumeratedSnippet.TYPE_ORDINAL);
                } else {
                    enumerated.setValue(EnumeratedSnippet.TYPE_STRING);
                }
            }
            FetchType parsedFetchType = parsedElementCollection.getFetch();
            ElementCollectionSnippet elementCollection = new ElementCollectionSnippet();
            elementCollection.setCollectionType(parsedElementCollection.getCollectionType());
            elementCollection.setTargetClass(parsedElementCollection.getTargetClass());
            if (parsedFetchType != null) {
                elementCollection.setFetchType(parsedFetchType.value());
            }
            TemporalType parsedTemporal = parsedElementCollection.getTemporal();
            Lob parsedLob = parsedElementCollection.getLob();

            VariableDefSnippet variableDef = getVariableDef(parsedElementCollection.getName());
            variableDef.setElementCollection(elementCollection);
            variableDef.setCollectionTable(collectionTable);
            variableDef.setColumnDef(columnDef);
            variableDef.setEnumerated(enumerated);

            if (parsedTemporal != null) {
                variableDef.setTemporal(true);
                variableDef.setTemporalType(
                        TEMPORAL_TYPE_PREFIX + parsedTemporal.value());
            }

            if (parsedLob != null) {
                variableDef.setLob(true);
            }
            List<AttributeOverride> attributedOverrrides
                    = parsedElementCollection.getAttributeOverride();

            if (attributedOverrrides != null
                    && !attributedOverrrides.isEmpty()
                    && classDef.getAttributeOverrides() == null) {

                classDef.setAttributeOverrides(new AttributeOverridesSnippet());
            }

            for (AttributeOverride parsedAttributeOverride : attributedOverrrides) {
                AttributeOverrideSnippet attributeOverride = new AttributeOverrideSnippet();

                ColumnDefSnippet columnDefAttributeOverride = getColumnDef(
                        parsedAttributeOverride.getColumn());

                attributeOverride.setColumnDef(columnDefAttributeOverride);
                attributeOverride.setName(parsedAttributeOverride.getName());

                classDef.getAttributeOverrides().addAttributeOverrides(
                        attributeOverride);
            }
        }
    }

    protected void processTransient(
            List<Transient> parsedTransients) {

        for (Transient parsedTransient : parsedTransients) {
            VariableDefSnippet variableDef = getVariableDef(
                    parsedTransient.getName());
            variableDef.setType(parsedTransient.getAttributeType());
            variableDef.setTranzient(true);
        }
    }

    protected List<String> getCascadeTypes(
            CascadeType cascadeType) {

        if (cascadeType == null) {
            return Collections.EMPTY_LIST;
        }

        List<String> cascadeTypes = new ArrayList<String>();

        EmptyType cascadeAll = cascadeType.getCascadeAll();
        EmptyType cascadeMerge = cascadeType.getCascadeMerge();
        EmptyType cascadePersist = cascadeType.getCascadePersist();
        EmptyType cascadeRefresh = cascadeType.getCascadeRefresh();
        EmptyType cascadeRemove = cascadeType.getCascadeRemove();

        if (cascadeAll != null) {
            cascadeTypes.add(ManyToManySnippet.CASCADE_ALL);
        }

        if (cascadeMerge != null) {
            cascadeTypes.add(ManyToManySnippet.CASCADE_MERGE);
        }

        if (cascadePersist != null) {
            cascadeTypes.add(ManyToManySnippet.CASCADE_PERSIST);
        }

        if (cascadeRefresh != null) {
            cascadeTypes.add(ManyToManySnippet.CASCADE_REFRESH);
        }

        if (cascadeRemove != null) {
            cascadeTypes.add(ManyToManySnippet.CASCADE_REMOVE);
        }

        return cascadeTypes;
    }

    protected List<ColumnResultSnippet> getColumnResults(
            List<ColumnResult> parsedColumnResults) {

        if (parsedColumnResults == null || parsedColumnResults.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<ColumnResultSnippet> columnResults = new ArrayList<ColumnResultSnippet>();

        for (ColumnResult parsedColumnResult : parsedColumnResults) {
            ColumnResultSnippet columnResult = new ColumnResultSnippet();

            columnResult.setName(parsedColumnResult.getName());
            columnResults.add(columnResult);
        }

        return columnResults;
    }

    protected List<EntityResultSnippet> getEntityResults(
            List<EntityResult> parsedEntityResults) {

        if (parsedEntityResults == null || parsedEntityResults.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<EntityResultSnippet> entityResults = new ArrayList<EntityResultSnippet>();

        for (EntityResult parsedEntityResult : parsedEntityResults) {

            List<FieldResultSnippet> fieldResults = getFieldResults(
                    parsedEntityResult.getFieldResult());

            EntityResultSnippet entityResult = new EntityResultSnippet();

            entityResult.setDiscriminatorColumn(
                    parsedEntityResult.getDiscriminatorColumn());
            entityResult.setEntityClass(
                    parsedEntityResult.getEntityClass());
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

        List<FieldResultSnippet> fieldResults = new ArrayList<FieldResultSnippet>();

        for (FieldResult parsedFieldResult : parsedFieldResults) {
            FieldResultSnippet fieldResult = new FieldResultSnippet();

            fieldResult.setColumn(parsedFieldResult.getColumn());
            fieldResult.setName(parsedFieldResult.getName());

            fieldResults.add(fieldResult);
        }
        return fieldResults;
    }

    protected List<JoinColumnSnippet> getJoinColumns(
            List<JoinColumn> parsedJoinColumns) {

        List<JoinColumnSnippet> joinColumns = new ArrayList<JoinColumnSnippet>();

        for (JoinColumn parsedJoinColumn : parsedJoinColumns) {

            JoinColumnSnippet joinColumn = new JoinColumnSnippet();

            joinColumn.setColumnDefinition(
                    parsedJoinColumn.getColumnDefinition());

            if (parsedJoinColumn.isInsertable() != null) {
                joinColumn.setInsertable(parsedJoinColumn.isInsertable());
            }

            if (parsedJoinColumn.isUnique() != null) {
                joinColumn.setUnique(parsedJoinColumn.isUnique());
            }

            if (parsedJoinColumn.isNullable() != null) {
                joinColumn.setNullable(parsedJoinColumn.isNullable());
            }

            if (parsedJoinColumn.isUpdatable() != null) {
                joinColumn.setUpdatable(parsedJoinColumn.isUpdatable());
            }

            joinColumn.setName(parsedJoinColumn.getName());
            joinColumn.setReferencedColumnName(
                    parsedJoinColumn.getReferencedColumnName());
            joinColumn.setTable(parsedJoinColumn.getTable());

            joinColumns.add(joinColumn);
        }

        return joinColumns;
    }

    protected CollectionTableSnippet getCollectionTable(CollectionTable parsedCollectionTable) {
        if (parsedCollectionTable == null) {
            return null;
        }

        List<JoinColumnSnippet> joinColumns = getJoinColumns(
                parsedCollectionTable.getJoinColumn());

        List<UniqueConstraint> parsedUniqueConstraints
                = parsedCollectionTable.getUniqueConstraint();

        UniqueConstraintSnippet uniqueConstraints = getUniqueConstraint(
                parsedUniqueConstraints);

        CollectionTableSnippet collectionTable = new CollectionTableSnippet();

        collectionTable.setCatalog(parsedCollectionTable.getCatalog());
        collectionTable.setName(parsedCollectionTable.getName());
        collectionTable.setSchema(parsedCollectionTable.getSchema());
        collectionTable.setJoinColumns(joinColumns);
        collectionTable.setUniqueConstraints(uniqueConstraints);

        return collectionTable;
    }

    protected JoinTableSnippet getJoinTable(JoinTable parsedJoinTable) {
        if (parsedJoinTable == null) {
            return null;
        }

        List<JoinColumnSnippet> inverseJoinColumns = getJoinColumns(
                parsedJoinTable.getInverseJoinColumn());

        List<JoinColumnSnippet> joinColumns = getJoinColumns(
                parsedJoinTable.getJoinColumn());

        List<UniqueConstraint> parsedUniqueConstraints
                = parsedJoinTable.getUniqueConstraint();

        UniqueConstraintSnippet uniqueConstraints = getUniqueConstraint(
                parsedUniqueConstraints);

        JoinTableSnippet joinTable = new JoinTableSnippet();

        joinTable.setCatalog(parsedJoinTable.getCatalog());
        joinTable.setName(parsedJoinTable.getName());
        joinTable.setSchema(parsedJoinTable.getSchema());
        joinTable.setJoinColumns(joinColumns);
        joinTable.setInverseJoinColumns(inverseJoinColumns);
        joinTable.setUniqueConstraints(uniqueConstraints);

        return joinTable;
    }

    protected List<PrimaryKeyJoinColumnSnippet> getPrimaryKeyJoinColumns(
            List<PrimaryKeyJoinColumn> parsedPrimaryKeyJoinColumns) {

        if (parsedPrimaryKeyJoinColumns == null || parsedPrimaryKeyJoinColumns.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns
                = new ArrayList<PrimaryKeyJoinColumnSnippet>();

        for (PrimaryKeyJoinColumn parsedPrimaryKeyJoinColumn : parsedPrimaryKeyJoinColumns) {

            PrimaryKeyJoinColumnSnippet primaryKeyJoinColumn = new PrimaryKeyJoinColumnSnippet();

            primaryKeyJoinColumn.setColumnDefinition(
                    parsedPrimaryKeyJoinColumn.getColumnDefinition());
            primaryKeyJoinColumn.setName(parsedPrimaryKeyJoinColumn.getName());
            primaryKeyJoinColumn.setReferencedColumnName(
                    parsedPrimaryKeyJoinColumn.getReferencedColumnName());

            primaryKeyJoinColumns.add(primaryKeyJoinColumn);
        }

        return primaryKeyJoinColumns;
    }

    protected List<QueryHintSnippet> getQueryHints(
            List<QueryHint> parsedQueryHints) {

        if (parsedQueryHints == null || parsedQueryHints.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<QueryHintSnippet> queryHints = new ArrayList<QueryHintSnippet>();

        for (QueryHint parsedQueryHint : parsedQueryHints) {
            QueryHintSnippet queryHint = new QueryHintSnippet();

            queryHint.setName(parsedQueryHint.getName());
            queryHint.setValue(parsedQueryHint.getValue());

            queryHints.add(queryHint);
        }
        return queryHints;
    }

    protected UniqueConstraintSnippet getUniqueConstraint(
            List<UniqueConstraint> parsedUniqueConstraints) {

        if (parsedUniqueConstraints == null || parsedUniqueConstraints.isEmpty()) {
            return null;
        }

        UniqueConstraintSnippet uniqueConstraints = new UniqueConstraintSnippet();

        for (UniqueConstraint parsedUniqueConstraint : parsedUniqueConstraints) {
            uniqueConstraints.getUniqueConstraints().addAll(
                    parsedUniqueConstraint.getColumnName());
        }

        return uniqueConstraints;
    }

    protected List<UniqueConstraintSnippet> getUniqueConstraints(
            List<UniqueConstraint> parsedUniqueConstraints) {

        if (parsedUniqueConstraints == null || parsedUniqueConstraints.isEmpty()) {
            return null;
        }

        List<UniqueConstraintSnippet> uniqueConstraints
                = new ArrayList<UniqueConstraintSnippet>();

        for (UniqueConstraint parsedUniqueConstraint : parsedUniqueConstraints) {
            UniqueConstraintSnippet uniqueConstraint = new UniqueConstraintSnippet();

            uniqueConstraint.setUniqueConstraints(
                    parsedUniqueConstraint.getColumnName());
            uniqueConstraints.add(uniqueConstraint);
        }

        return uniqueConstraints;
    }

    protected void processAssociationOverrides(
            List<AssociationOverride> parsedAssociationOverrides) {

        if (parsedAssociationOverrides == null
                || parsedAssociationOverrides.isEmpty()) {
            return;
        }

        classDef.setAssociationOverrides(new AssociationOverridesSnippet());

        for (AssociationOverride parsedAssociationOverride : parsedAssociationOverrides) {

            List<JoinColumnSnippet> joinColumnsList = getJoinColumns(
                    parsedAssociationOverride.getJoinColumn());

            AssociationOverrideSnippet associationOverride = new AssociationOverrideSnippet();

            associationOverride.setName(parsedAssociationOverride.getName());
            associationOverride.setJoinColumns(joinColumnsList);

            classDef.getAssociationOverrides().addAssociationOverride(
                    associationOverride);
        }
    }

    protected void processAttributeOverrides(
            List<AttributeOverride> parsedAttributeOverrides) {

        if (parsedAttributeOverrides == null || parsedAttributeOverrides.isEmpty()) {
            return;
        }

        classDef.setAttributeOverrides(new AttributeOverridesSnippet());

        for (AttributeOverride parsedAttributeOverride : parsedAttributeOverrides) {

            ColumnDefSnippet columnDef = getColumnDef(
                    parsedAttributeOverride.getColumn());

            AttributeOverrideSnippet attributeOverride = new AttributeOverrideSnippet();

            attributeOverride.setColumnDef(columnDef);
            attributeOverride.setName(parsedAttributeOverride.getName());

            classDef.getAttributeOverrides().addAttributeOverrides(
                    attributeOverride);
        }
    }

    protected void processEntityListeners(EntityListeners parsedEntityListeners) {

        if (parsedEntityListeners == null) {
            return;
        }

        List<EntityListener> parsedEntityListenersList
                = parsedEntityListeners.getEntityListener();

        List<EntityListenerSnippet> entityListeners
                = GeneratorUtil.processEntityListeners(
                        parsedEntityListenersList, packageName);

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

    protected void processIdClass(IdClass parsedIdClass) {

        if (parsedIdClass == null) {
            return;
        }

        IdClassSnippet idClass = new IdClassSnippet();

        idClass.setValue(parsedIdClass.getClazz());
        idClass.setPackageName(packageName);

        classDef.setIdClass(idClass);
    }

    protected void processNamedNativeQueries(
            List<NamedNativeQuery> parsedNamedNativeQueries) {

        if (parsedNamedNativeQueries == null
                || parsedNamedNativeQueries.isEmpty()) {
            return;
        }

        NamedNativeQueriesSnippet namedNativeQueries = new NamedNativeQueriesSnippet();

        classDef.setNamedNativeQueries(namedNativeQueries);

        for (NamedNativeQuery parsedNamedNativeQuery : parsedNamedNativeQueries) {

            List<QueryHintSnippet> queryHints = getQueryHints(
                    parsedNamedNativeQuery.getHint());

            NamedNativeQuerySnippet namedNativeQuery = new NamedNativeQuerySnippet();

            namedNativeQuery.setName(parsedNamedNativeQuery.getName());
            namedNativeQuery.setQuery(parsedNamedNativeQuery.getQuery());
            namedNativeQuery.setResultClass(
                    parsedNamedNativeQuery.getResultClass());
            namedNativeQuery.setPackageName(packageName);
            namedNativeQuery.setResultSetMapping(
                    parsedNamedNativeQuery.getResultSetMapping());
            namedNativeQuery.setQueryHints(queryHints);
            namedNativeQuery.setAttributeType(parsedNamedNativeQuery.getAttributeType());

            classDef.getNamedNativeQueries().addNamedQuery(namedNativeQuery);
        }
    }

    protected void processNamedQueries(
            List<NamedQuery> parsedNamedQueries) {

        if (parsedNamedQueries == null || parsedNamedQueries.isEmpty()) {
            return;
        }

        NamedQueriesSnippet namedQueries = new NamedQueriesSnippet();

        classDef.setNamedQueries(namedQueries);

        for (NamedQuery parsedNamedQuery : parsedNamedQueries) {

            List<QueryHintSnippet> queryHints = getQueryHints(
                    parsedNamedQuery.getHint());

            NamedQueryDefSnippet namedQuery = new NamedQueryDefSnippet();

            namedQuery.setName(parsedNamedQuery.getName());
            namedQuery.setQuery(parsedNamedQuery.getQuery());
            namedQuery.setAttributeType(parsedNamedQuery.getAttributeType());
            namedQuery.setQueryHints(queryHints);

            classDef.getNamedQueries().addNamedQuery(namedQuery);
        }
    }

    protected void processEmbedded(List<Embedded> parsedEmbeddeds) {

        if (parsedEmbeddeds == null) {
            return;
        }
//
//        List<ParsedEmbedded> parsedEmbeddeds = parsedAttributes.getEmbedded();
        for (Embedded parsedEmbeded : parsedEmbeddeds) {
            VariableDefSnippet variableDef = getVariableDef(parsedEmbeded.getName());

            variableDef.setEmbedded(true);
            variableDef.setType(parsedEmbeded.getAttributeType());

            List<AttributeOverride> attributedOverrrides
                    = parsedEmbeded.getAttributeOverride();

            if (attributedOverrrides != null
                    && !attributedOverrrides.isEmpty()
                    && classDef.getAttributeOverrides() == null) {

                classDef.setAttributeOverrides(new AttributeOverridesSnippet());
            }

            for (AttributeOverride parsedAttributeOverride : attributedOverrrides) {
                AttributeOverrideSnippet attributeOverride = new AttributeOverrideSnippet();

                ColumnDefSnippet columnDef = getColumnDef(
                        parsedAttributeOverride.getColumn());

                attributeOverride.setColumnDef(columnDef);
                attributeOverride.setName(parsedAttributeOverride.getName());

                classDef.getAttributeOverrides().addAttributeOverrides(
                        attributeOverride);
            }
        }
    }

    protected void processEmbeddedId(EmbeddedId parsedEmbeddedId) {
        if (parsedEmbeddedId == null) {
            return;
        }

        VariableDefSnippet variableDef = getVariableDef(parsedEmbeddedId.getName());
        variableDef.setEmbeddedId(true);
        variableDef.setType(parsedEmbeddedId.getAttributeType());

        List<AttributeOverride> attributedOverrrides
                = parsedEmbeddedId.getAttributeOverride();

        if (attributedOverrrides != null
                && !attributedOverrrides.isEmpty()
                && classDef.getAttributeOverrides() == null) {

            classDef.setAttributeOverrides(new AttributeOverridesSnippet());
        }

        for (AttributeOverride parsedAttributeOverride : attributedOverrrides) {

            AttributeOverrideSnippet attributeOverride = new AttributeOverrideSnippet();

            ColumnDefSnippet columnDef = getColumnDef(parsedAttributeOverride.getColumn());

            attributeOverride.setColumnDef(columnDef);
            attributeOverride.setName(parsedAttributeOverride.getName());

            classDef.getAttributeOverrides().addAttributeOverrides(
                    attributeOverride);
        }
    }

    protected void processId(List<Id> parsedIds) {

//        if (parsedAttributes == null) {
//            return;
//        }
//
//        List<ParsedId> parsedIds = parsedAttributes.getId();
        for (Id parsedId : parsedIds) {
            VariableDefSnippet variableDef = getVariableDef(parsedId.getName());
            variableDef.setType(parsedId.getAttributeType());

            variableDef.setPrimaryKey(true);

            Column parsedColumn = parsedId.getColumn();

            if (parsedColumn != null) {
                ColumnDefSnippet columnDef = getColumnDef(parsedColumn);
                variableDef.setColumnDef(columnDef);
            }

            GeneratedValue parsedGeneratedValue
                    = parsedId.getGeneratedValue();

            if (parsedGeneratedValue != null && parsedGeneratedValue.getStrategy() != null) {
                GeneratedValueSnippet generatedValue = new GeneratedValueSnippet();

                generatedValue.setGenerator(parsedGeneratedValue.getGenerator());
                generatedValue.setStrategy("GenerationType." + parsedGeneratedValue.getStrategy().value());

                variableDef.setGeneratedValue(generatedValue);

                SequenceGenerator parsedSequenceGenerator
                        = parsedId.getSequenceGenerator();

                if (parsedSequenceGenerator != null) {
                    SequenceGeneratorSnippet sequenceGenerator = new SequenceGeneratorSnippet();
                    sequenceGenerator.setAllocationSize(
                            parsedSequenceGenerator.getAllocationSize());
                    sequenceGenerator.setInitialValue(
                            parsedSequenceGenerator.getInitialValue());
                    sequenceGenerator.setName(parsedSequenceGenerator.getName());
                    sequenceGenerator.setSequenceName(
                            parsedSequenceGenerator.getSequenceName());
                    sequenceGenerator.setCatalog(parsedSequenceGenerator.getCatalog());
                    sequenceGenerator.setSchema(parsedSequenceGenerator.getSchema());
                    variableDef.setSequenceGenerator(sequenceGenerator);
                }

                TableGenerator parsedTableGenerator
                        = parsedId.getTableGenerator();

                if (parsedTableGenerator != null) {
                    TableGeneratorSnippet tableGenerator = new TableGeneratorSnippet();

                    tableGenerator.setAllocationSize(
                            parsedTableGenerator.getAllocationSize());
                    tableGenerator.setCatalog(parsedTableGenerator.getCatalog());
                    tableGenerator.setInitialValue(
                            parsedTableGenerator.getInitialValue());
                    tableGenerator.setName(parsedTableGenerator.getName());
                    tableGenerator.setPkColumnName(
                            parsedTableGenerator.getPkColumnName());
                    tableGenerator.setPkColumnValue(
                            parsedTableGenerator.getPkColumnValue());
                    tableGenerator.setSchema(parsedTableGenerator.getSchema());
                    tableGenerator.setTable(parsedTableGenerator.getTable());
                    tableGenerator.setValueColumnName(
                            parsedTableGenerator.getValueColumnName());

                    List<UniqueConstraint> parsedUniqueConstraints
                            = parsedTableGenerator.getUniqueConstraint();

                    List<UniqueConstraintSnippet> uniqueConstraints
                            = new ArrayList<UniqueConstraintSnippet>();

                    for (UniqueConstraint parsedUniqueConstraint : parsedUniqueConstraints) {
                        UniqueConstraintSnippet uniqueConstraint = new UniqueConstraintSnippet();

                        uniqueConstraint.setUniqueConstraints(
                                parsedUniqueConstraint.getColumnName());
                    }

                    tableGenerator.setUniqueConstraints(uniqueConstraints);

                    variableDef.setTableGenerator(tableGenerator);
                }
            }

            TemporalType parsedTemporal = parsedId.getTemporal();

            if (parsedTemporal != null) {
                variableDef.setTemporal(true);
                variableDef.setTemporalType(TEMPORAL_TYPE_PREFIX + parsedTemporal.value());
            }
        }
    }

    protected void processManyToMany(List<ManyToMany> parsedManyToManys) {

        if (parsedManyToManys == null) {
            return;
        }
//
//        List<ParsedManyToMany> parsedManyToManys
//                = parsedAttributes.getManyToMany();
        for (ManyToMany parsedManyToMany : parsedManyToManys) {
            List<String> cascadeTypes = getCascadeTypes(
                    parsedManyToMany.getCascade());

            JoinTableSnippet joinTable = getJoinTable(parsedManyToMany.getJoinTable());

            ManyToManySnippet manyToMany = new ManyToManySnippet();

            manyToMany.setCollectionType(parsedManyToMany.getCollectionType());
            manyToMany.setMappedBy(parsedManyToMany.getMappedBy());
            manyToMany.setTargetEntity(parsedManyToMany.getTargetEntity());
            manyToMany.setCascadeTypes(cascadeTypes);

            if (parsedManyToMany.getFetch() != null) {
                manyToMany.setFetchType(parsedManyToMany.getFetch().value());
            }
            //TODO: Checked this error - The ORM.xsd has this but NOT the
            //http://www.oracle.com/technology/products/ias/toplink/jpa/resources/toplink-jpa-annotations.html
            //manyToMany.setOrderBy(parsedManyToMany.getOrderBy());

            VariableDefSnippet variableDef = getVariableDef(
                    parsedManyToMany.getName());

            variableDef.setRelationDef(manyToMany);
            variableDef.setJoinTable(joinTable);
//            variableDef.setType(parsedManyToMany.getAttributeType());

            if (parsedManyToMany.getMapKey() != null) {
                variableDef.setMapKey(parsedManyToMany.getMapKey().getName());
            }
        }
    }

    protected void processManyToOne(List<ManyToOne> parsedManyToOnes) {

        if (parsedManyToOnes == null) {
            return;
        }
//
//        List<ParsedManyToOne> parsedManyToOnes
//                = parsedAttributes.getManyToOne();
        for (ManyToOne parsedManyToOne : parsedManyToOnes) {
            List<String> cascadeTypes = getCascadeTypes(
                    parsedManyToOne.getCascade());

            JoinTableSnippet joinTable = getJoinTable(parsedManyToOne.getJoinTable());

            List<JoinColumnSnippet> joinColumnsList = getJoinColumns(
                    parsedManyToOne.getJoinColumn());

            JoinColumnsSnippet joinColumns = null;

            if (!joinColumnsList.isEmpty()) {
                joinColumns = new JoinColumnsSnippet();
                joinColumns.setJoinColumns(joinColumnsList);
            }

            ManyToOneSnippet manyToOne = new ManyToOneSnippet();

            manyToOne.setTargetEntity(parsedManyToOne.getTargetEntity());
            manyToOne.setCascadeTypes(cascadeTypes);

            if (parsedManyToOne.getOptional() != null) {
                manyToOne.setOptional(parsedManyToOne.getOptional());
            }

            if (parsedManyToOne.getFetch() != null) {
                manyToOne.setFetchType(parsedManyToOne.getFetch().value());
            }

            VariableDefSnippet variableDef = getVariableDef(parsedManyToOne.getName());

            variableDef.setRelationDef(manyToOne);
            variableDef.setJoinTable(joinTable);
            variableDef.setJoinColumns(joinColumns);
//            variableDef.setType(parsedManyToOne.getAttributeType());
        }
    }

    protected void processOneToMany(List<OneToMany> parsedOneToManys) {

        if (parsedOneToManys == null) {
            return;
        }
//
//        List<ParsedOneToMany> parsedOneToManys = parsedAttributes.getOneToMany();
        for (OneToMany parsedOneToMany : parsedOneToManys) {
            List<String> cascadeTypes = getCascadeTypes(
                    parsedOneToMany.getCascade());

            JoinTableSnippet joinTable = getJoinTable(parsedOneToMany.getJoinTable());

            List<JoinColumnSnippet> joinColumnsList = getJoinColumns(
                    parsedOneToMany.getJoinColumn());

            JoinColumnsSnippet joinColumns = null;

            if (!joinColumnsList.isEmpty()) {
                joinColumns = new JoinColumnsSnippet();
                joinColumns.setJoinColumns(joinColumnsList);
            }

            OneToManySnippet oneToMany = new OneToManySnippet();

            oneToMany.setCascadeTypes(cascadeTypes);
            oneToMany.setTargetEntity(parsedOneToMany.getTargetEntity());
            oneToMany.setMappedBy(parsedOneToMany.getMappedBy());
            oneToMany.setCollectionType(parsedOneToMany.getCollectionType());

            if (parsedOneToMany.getFetch() != null) {
                oneToMany.setFetchType(parsedOneToMany.getFetch().value());
            }

            VariableDefSnippet variableDef = getVariableDef(parsedOneToMany.getName());

            variableDef.setRelationDef(oneToMany);
            variableDef.setJoinTable(joinTable);
            variableDef.setJoinColumns(joinColumns);
//            variableDef.setType(parsedOneToMany.getAttributeType());

            if (parsedOneToMany.getMapKey() != null) {
                variableDef.setMapKey(parsedOneToMany.getMapKey().getName());
            }
        }
    }

    protected void processOneToOne(List<OneToOne> parsedOneToOnes) {

        if (parsedOneToOnes == null) {
            return;
        }
//
//        List<ParsedOneToOne> parsedOneToOnes = parsedAttributes.getOneToOne();
        for (OneToOne parsedOneToOne : parsedOneToOnes) {
            List<String> cascadeTypes = getCascadeTypes(
                    parsedOneToOne.getCascade());

            JoinTableSnippet joinTable = getJoinTable(parsedOneToOne.getJoinTable());

            List<JoinColumnSnippet> joinColumnsList = getJoinColumns(
                    parsedOneToOne.getJoinColumn());

            JoinColumnsSnippet joinColumns = null;

            if (!joinColumnsList.isEmpty()) {
                joinColumns = new JoinColumnsSnippet();
                joinColumns.setJoinColumns(joinColumnsList);
            }

            OneToOneSnippet oneToOne = new OneToOneSnippet();

            oneToOne.setCascadeTypes(cascadeTypes);
            oneToOne.setTargetEntity(parsedOneToOne.getTargetEntity());
            oneToOne.setMappedBy(parsedOneToOne.getMappedBy());

            if (parsedOneToOne.getFetch() != null) {
                oneToOne.setFetchType(parsedOneToOne.getFetch().value());
            }

            VariableDefSnippet variableDef = getVariableDef(parsedOneToOne.getName());

            variableDef.setRelationDef(oneToOne);
            variableDef.setJoinTable(joinTable);
            variableDef.setJoinColumns(joinColumns);
//            variableDef.setType(parsedOneToOne.getAttributeType());
        }
    }

    protected void processVersion(List<Version> parsedVersions) {

//        if (parsedAttributes == null) {
//            return;
//        }
//
//        List<ParsedVersion> parsedVersions = parsedAttributes.getVersion();
        for (Version parsedVersion : parsedVersions) {
            VariableDefSnippet variableDef = getVariableDef(parsedVersion.getName());

            ColumnDefSnippet columnDef = getColumnDef(parsedVersion.getColumn());
            variableDef.setType(parsedVersion.getAttributeType());

            variableDef.setVersion(true);
            variableDef.setColumnDef(columnDef);

            if (parsedVersion.getTemporal() != null) {
                variableDef.setTemporal(true);
                variableDef.setTemporalType(TEMPORAL_TYPE_PREFIX
                        + parsedVersion.getTemporal().value());
            }
        }
    }

    protected void processPrimaryKeyJoinColumns(
            List<PrimaryKeyJoinColumn> parsedPrimaryKeyJoinColumns) {

        if (parsedPrimaryKeyJoinColumns == null
                || parsedPrimaryKeyJoinColumns.isEmpty()) {
            return;
        }

        classDef.setPrimaryKeyJoinColumns(new PrimaryKeyJoinColumnsSnippet());

        for (PrimaryKeyJoinColumn parsedPrimaryKeyJoinColumn : parsedPrimaryKeyJoinColumns) {
            PrimaryKeyJoinColumnSnippet primaryKeyJoinColumn = new PrimaryKeyJoinColumnSnippet();

            primaryKeyJoinColumn.setColumnDefinition(
                    parsedPrimaryKeyJoinColumn.getColumnDefinition());
            primaryKeyJoinColumn.setName(parsedPrimaryKeyJoinColumn.getName());
            primaryKeyJoinColumn.setReferencedColumnName(
                    parsedPrimaryKeyJoinColumn.getReferencedColumnName());

            classDef.getPrimaryKeyJoinColumns().addPrimaryKeyJoinColumn(
                    primaryKeyJoinColumn);
        }
    }

    protected void processSecondaryTable(
            List<SecondaryTable> parsedSecondaryTables) {

        if (parsedSecondaryTables == null || parsedSecondaryTables.isEmpty()) {
            return;
        }

        classDef.setSecondaryTables(new SecondaryTablesSnippet());

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
            secondaryTable.setPrimaryKeyJoinColumns(primaryKeyJoinColumns);

            classDef.getSecondaryTables().addSecondaryTable(secondaryTable);
        }
    }

    protected void processSequenceGenerator(
            SequenceGenerator parsedSequenceGenerator) {

        if (parsedSequenceGenerator == null) {
            return;
        }

        SequenceGeneratorSnippet sequenceGenerator = new SequenceGeneratorSnippet();

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

    protected void processSqlResultSetMapping(
            List<SqlResultSetMapping> parsedSqlResultSetMappings) {

        if (parsedSqlResultSetMappings == null
                || parsedSqlResultSetMappings.isEmpty()) {
            return;
        }

        classDef.setSQLResultSetMappings(new SQLResultSetMappingsSnippet());

        for (SqlResultSetMapping parsedSqlResultSetMapping : parsedSqlResultSetMappings) {
            SQLResultSetMappingSnippet sqlResultSetMapping = new SQLResultSetMappingSnippet();

            List<ColumnResultSnippet> columnResults = getColumnResults(
                    parsedSqlResultSetMapping.getColumnResult());

            List<EntityResultSnippet> entityResults = getEntityResults(
                    parsedSqlResultSetMapping.getEntityResult());

            sqlResultSetMapping.setColumnResults(columnResults);
            sqlResultSetMapping.setEntityResults(entityResults);
            sqlResultSetMapping.setName(parsedSqlResultSetMapping.getName());

            classDef.getSQLResultSetMappings().addSQLResultSetMapping(
                    sqlResultSetMapping);
        }
    }

    protected void processTable(Table parsedTable) {

        if (parsedTable == null) {
            return;
        }

        TableDefSnippet table = new TableDefSnippet();

        table.setCatalog(parsedTable.getCatalog());
        table.setName(parsedTable.getName());
        table.setSchema(parsedTable.getSchema());
        table.setUniqueConstraint(
                getUniqueConstraint(parsedTable.getUniqueConstraint()));

        classDef.setTableDef(table);
    }

    protected void processTableGenerator(
            TableGenerator parsedTableGenerator) {

        if (parsedTableGenerator == null) {
            return;
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
            logger.log(Level.WARNING, "Ignoring : Cannot find variable for "
                    + "Table generator :" + tableGenerator.getName());
        }
    }
}
