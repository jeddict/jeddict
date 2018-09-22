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
package io.github.jeddict.orm.generator.service;

import io.github.jeddict.jpa.spec.AssociationOverride;
import io.github.jeddict.jpa.spec.AttributeOverride;
import io.github.jeddict.jpa.spec.Basic;
import io.github.jeddict.jpa.spec.CascadeType;
import io.github.jeddict.jpa.spec.CollectionTable;
import io.github.jeddict.jpa.spec.Column;
import io.github.jeddict.jpa.spec.Convert;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.Embedded;
import io.github.jeddict.jpa.spec.EmptyType;
import io.github.jeddict.jpa.spec.EnumType;
import io.github.jeddict.jpa.spec.FetchType;
import io.github.jeddict.jpa.spec.ForeignKey;
import io.github.jeddict.jpa.spec.Index;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.JoinTable;
import io.github.jeddict.jpa.spec.Lob;
import io.github.jeddict.jpa.spec.ManyToMany;
import io.github.jeddict.jpa.spec.ManyToOne;
import io.github.jeddict.jpa.spec.OneToMany;
import io.github.jeddict.jpa.spec.OneToOne;
import io.github.jeddict.jpa.spec.PrimaryKeyJoinColumn;
import io.github.jeddict.jpa.spec.TemporalType;
import io.github.jeddict.jpa.spec.Transient;
import io.github.jeddict.jpa.spec.UniqueConstraint;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.JoinColumnHandler;
import io.github.jeddict.jpa.spec.extend.MapKeyHandler;
import io.github.jeddict.jpa.spec.extend.MapKeyType;
import io.github.jeddict.jpa.spec.extend.SingleRelationAttribute;
import io.github.jeddict.jpa.spec.validator.ConvertValidator;
import io.github.jeddict.jpa.spec.validator.column.ForeignKeyValidator;
import io.github.jeddict.jpa.spec.validator.column.JoinColumnValidator;
import io.github.jeddict.jpa.spec.validator.table.CollectionTableValidator;
import io.github.jeddict.jpa.spec.validator.table.JoinTableValidator;
import io.github.jeddict.orm.generator.compiler.AssociationOverrideSnippet;
import io.github.jeddict.orm.generator.compiler.AssociationOverridesHandler;
import io.github.jeddict.orm.generator.compiler.AssociationOverridesSnippet;
import io.github.jeddict.orm.generator.compiler.AttributeOverrideSnippet;
import io.github.jeddict.orm.generator.compiler.AttributeOverridesHandler;
import io.github.jeddict.orm.generator.compiler.AttributeOverridesSnippet;
import io.github.jeddict.orm.generator.compiler.BasicSnippet;
import io.github.jeddict.orm.generator.compiler.CollectionTableSnippet;
import io.github.jeddict.orm.generator.compiler.ColumnDefSnippet;
import io.github.jeddict.orm.generator.compiler.ConvertSnippet;
import io.github.jeddict.orm.generator.compiler.ConvertsSnippet;
import io.github.jeddict.orm.generator.compiler.ElementCollectionSnippet;
import io.github.jeddict.orm.generator.compiler.EnumeratedSnippet;
import io.github.jeddict.orm.generator.compiler.ForeignKeySnippet;
import io.github.jeddict.orm.generator.compiler.IndexSnippet;
import io.github.jeddict.orm.generator.compiler.JoinColumnSnippet;
import io.github.jeddict.orm.generator.compiler.JoinColumnsSnippet;
import io.github.jeddict.orm.generator.compiler.JoinTableSnippet;
import io.github.jeddict.orm.generator.compiler.ManyToManySnippet;
import io.github.jeddict.orm.generator.compiler.ManyToOneSnippet;
import io.github.jeddict.orm.generator.compiler.MapKeySnippet;
import io.github.jeddict.orm.generator.compiler.OneToManySnippet;
import io.github.jeddict.orm.generator.compiler.OneToOneSnippet;
import io.github.jeddict.orm.generator.compiler.OrderBySnippet;
import io.github.jeddict.orm.generator.compiler.OrderColumnSnippet;
import io.github.jeddict.orm.generator.compiler.PrimaryKeyJoinColumnSnippet;
import io.github.jeddict.orm.generator.compiler.PrimaryKeyJoinColumnsSnippet;
import io.github.jeddict.orm.generator.compiler.TemporalSnippet;
import io.github.jeddict.orm.generator.compiler.UniqueConstraintSnippet;
import io.github.jeddict.orm.generator.compiler.def.ManagedClassDefSnippet;
import io.github.jeddict.orm.generator.compiler.def.VariableDefSnippet;
import io.github.jeddict.settings.diagram.ClassDiagramSettings;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.singletonList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import static java.util.stream.Collectors.toList;

public abstract class ManagedClassGenerator<T extends ManagedClassDefSnippet> extends ClassGenerator<T> {

    public ManagedClassGenerator(T classDef) {
        super(classDef);
    }

    @Override
    protected VariableDefSnippet processVariable(Attribute attr) {
        if (attr instanceof Basic) {
            return processBasic((Basic) attr);
        } else if (attr instanceof Transient) {
            return processTransient((Transient) attr);
        } else if (attr instanceof Embedded) {
            return processEmbedded((Embedded) attr);
        } else if (attr instanceof ElementCollection) {
            return processElementCollection((ElementCollection) attr);
        } else if (attr instanceof OneToOne) {
            return processOneToOne((OneToOne) attr);
        } else if (attr instanceof ManyToOne) {
            return processManyToOne((ManyToOne) attr);
        } else if (attr instanceof OneToMany) {
            return processOneToMany((OneToMany) attr);
        } else if (attr instanceof ManyToMany) {
            return processManyToMany((ManyToMany) attr);
        } else {
            throw new IllegalStateException("Invalid Attribute Type");
        }
    }

    protected VariableDefSnippet processBasic(Basic parsedBasic) {
        ColumnDefSnippet columnDef = getColumnDef(parsedBasic.getColumn());

        EnumType parsedEnumType = parsedBasic.getEnumerated();
        EnumeratedSnippet enumerated = null;
        if (parsedEnumType != null) {
            enumerated = new EnumeratedSnippet();
            enumerated.setValue(parsedEnumType);
        }

        TemporalType parsedTemporalType = parsedBasic.getTemporal();
        TemporalSnippet temporal = null;
        if (parsedTemporalType != null) {
            temporal = new TemporalSnippet();
            temporal.setValue(parsedTemporalType);
        }

        FetchType parsedFetchType = parsedBasic.getFetch();
        BasicSnippet basic = new BasicSnippet();

        if (parsedFetchType != null) {
            basic.setFetchType(parsedFetchType.value());
        }
        if (parsedBasic.getOptional() != null) {
            basic.setOptional(parsedBasic.getOptional());
        }

        Lob parsedLob = parsedBasic.getLob();

        VariableDefSnippet variableDef = getVariableDef(parsedBasic);
        variableDef.setType(parsedBasic.getAttributeType());
        variableDef.setBasic(basic);
        variableDef.setColumnDef(columnDef);
        variableDef.setEnumerated(enumerated);
        variableDef.setTemporal(temporal);
        variableDef.setConverts(processConverts(singletonList(parsedBasic.getConvert())));
        variableDef.setLob(parsedLob != null);
        return variableDef;
    }

    protected VariableDefSnippet processTransient(Transient parsedTransient) {
        VariableDefSnippet variableDef = getVariableDef(parsedTransient);
        variableDef.setType(parsedTransient.getAttributeType());
        variableDef.setTranzient(true);
        return variableDef;
    }

    protected VariableDefSnippet processEmbedded(Embedded parsedEmbeded) {
        VariableDefSnippet variableDef = getVariableDef(parsedEmbeded);
        variableDef.setType(rootPackageName, parsedEmbeded.getConnectedClass());
        variableDef.setEmbedded(true);
        variableDef.setConverts(processConverts(parsedEmbeded.getConverts()));
        processInternalAttributeOverride(variableDef, parsedEmbeded.getAttributeOverride());
        processInternalAssociationOverride(variableDef, parsedEmbeded.getAssociationOverride());
        return variableDef;
    }

    protected VariableDefSnippet processElementCollection(ElementCollection parsedElementCollection) {
        CollectionTableSnippet collectionTable = getCollectionTable(parsedElementCollection.getCollectionTable());

        FetchType parsedFetchType = parsedElementCollection.getFetch();
        ElementCollectionSnippet elementCollection = new ElementCollectionSnippet();
        elementCollection.setMapKeySnippet(updateMapKeyAttributeSnippet(parsedElementCollection));
        elementCollection.setTargetClass(parsedElementCollection.getAttributeType());
        if (parsedElementCollection.getConnectedClass() != null) {
            elementCollection.setTargetClassPackage(parsedElementCollection.getConnectedClass().getAbsolutePackage(rootPackageName));
        }

        if (parsedFetchType != null) {
            elementCollection.setFetchType(parsedFetchType.value());
        }
        Lob parsedLob = parsedElementCollection.getLob();

        VariableDefSnippet variableDef = getVariableDef(parsedElementCollection);
        if (parsedElementCollection.getConnectedClass() != null) {
            variableDef.setType(rootPackageName, parsedElementCollection.getConnectedClass());
        } else {
            variableDef.setType(parsedElementCollection.getAttributeType());
        }
        variableDef.setCollectionType(parsedElementCollection.getCollectionType());
        variableDef.setCollectionImplType(parsedElementCollection.getCollectionImplType());
        variableDef.setElementCollection(elementCollection);
        variableDef.setCollectionTable(collectionTable);

        if (parsedElementCollection.getOrderBy() != null) {
            variableDef.setOrderBy(new OrderBySnippet(parsedElementCollection.getOrderBy()));
        } else if (parsedElementCollection.getOrderColumn() != null) {
            variableDef.setOrderColumn(new OrderColumnSnippet(parsedElementCollection.getOrderColumn()));
        }

        if (parsedLob != null) {
            variableDef.setLob(true);
        }

        if (parsedElementCollection.getConnectedClass() == null) {//if not embeddable
            EnumType parsedEnumType = parsedElementCollection.getEnumerated();
            EnumeratedSnippet enumerated = null;
            if (parsedEnumType != null) {
                enumerated = new EnumeratedSnippet();
                enumerated.setValue(parsedEnumType);
            }

            TemporalType parsedTemporalType = parsedElementCollection.getTemporal();
            TemporalSnippet temporal = null;
            if (parsedTemporalType != null) {
                temporal = new TemporalSnippet();
                temporal.setValue(parsedTemporalType);
            }
            variableDef.setEnumerated(enumerated);
            variableDef.setTemporal(temporal);
            variableDef.setColumnDef(getColumnDef(parsedElementCollection.getColumn()));
        } else {
            processInternalAttributeOverride(variableDef, parsedElementCollection.getAttributeOverride());
            processInternalAssociationOverride(variableDef, parsedElementCollection.getAssociationOverride());
        }

        List<Convert> converts = new ArrayList<>();
        converts.addAll(parsedElementCollection.getMapKeyConverts());
        converts.addAll(parsedElementCollection.getConverts());
        variableDef.setConverts(processConverts(converts));
        return variableDef;
    }

    protected List<PrimaryKeyJoinColumnSnippet> getPrimaryKeyJoinColumns(
            List<PrimaryKeyJoinColumn> parsedPrimaryKeyJoinColumns) {

        if (parsedPrimaryKeyJoinColumns == null || parsedPrimaryKeyJoinColumns.isEmpty()) {
            return Collections.<PrimaryKeyJoinColumnSnippet>emptyList();
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

    protected VariableDefSnippet processOneToOne(OneToOne parsedOneToOne) {
        List<String> cascadeTypes = getCascadeTypes(parsedOneToOne.getCascade());

        JoinTableSnippet joinTable = getJoinTable(parsedOneToOne.getJoinTable());

        OneToOneSnippet oneToOne = new OneToOneSnippet();

        oneToOne.setCascadeTypes(cascadeTypes);
        oneToOne.setTargetEntity(parsedOneToOne.getTargetEntity());
        oneToOne.setTargetEntityPackage(parsedOneToOne.getConnectedEntity().getAbsolutePackage(rootPackageName));
        oneToOne.setTargetField(parsedOneToOne.getConnectedAttributeName());
        oneToOne.setMappedBy(parsedOneToOne.getMappedBy());
        if (parsedOneToOne.getOptional() != null) {
            oneToOne.setOptional(parsedOneToOne.getOptional());
        }

        if (parsedOneToOne.getFetch() != null) {
            oneToOne.setFetchType(parsedOneToOne.getFetch().value());
        } else if (ClassDiagramSettings.isLazyDefaultTypeForSingleAssociation()) {
            oneToOne.setFetchType(FetchType.LAZY.value());
        }

        oneToOne.setOrphanRemoval(parsedOneToOne.getOrphanRemoval());

        oneToOne.setPrimaryKey(parsedOneToOne.isPrimaryKey());
        oneToOne.setMapsId(parsedOneToOne.getMapsId());

        VariableDefSnippet variableDef = getVariableDef(parsedOneToOne);
        variableDef.setType(rootPackageName, parsedOneToOne.getConnectedEntity());

        variableDef.setRelationDef(oneToOne);
        variableDef.setJoinTable(joinTable);
        variableDef.setJoinColumns(getJoinColumnsSnippet(parsedOneToOne, false));
        variableDef.setPrimaryKeyJoinColumns(processPrimaryKeyJoinColumns(parsedOneToOne));
        return variableDef;
    }

    protected VariableDefSnippet processManyToOne(ManyToOne parsedManyToOne) {
        List<String> cascadeTypes = getCascadeTypes(parsedManyToOne.getCascade());

        JoinTableSnippet joinTable = getJoinTable(parsedManyToOne.getJoinTable());

        ManyToOneSnippet manyToOne = new ManyToOneSnippet();

        manyToOne.setTargetEntity(parsedManyToOne.getTargetEntity());
        manyToOne.setTargetEntityPackage(parsedManyToOne.getConnectedEntity().getAbsolutePackage(rootPackageName));
        manyToOne.setTargetField(parsedManyToOne.getConnectedAttributeName());
        manyToOne.setCascadeTypes(cascadeTypes);

        if (parsedManyToOne.getOptional() != null) {
            manyToOne.setOptional(parsedManyToOne.getOptional());
        }

        if (parsedManyToOne.getFetch() != null) {
            manyToOne.setFetchType(parsedManyToOne.getFetch().value());
        } else if (ClassDiagramSettings.isLazyDefaultTypeForSingleAssociation()) {
            manyToOne.setFetchType(FetchType.LAZY.value());
        }

        manyToOne.setPrimaryKey(parsedManyToOne.isPrimaryKey());
        manyToOne.setMapsId(parsedManyToOne.getMapsId());

        VariableDefSnippet variableDef = getVariableDef(parsedManyToOne);
        variableDef.setType(rootPackageName, parsedManyToOne.getConnectedEntity());

        variableDef.setRelationDef(manyToOne);
        variableDef.setJoinTable(joinTable);
        variableDef.setJoinColumns(getJoinColumnsSnippet(parsedManyToOne, false));
        variableDef.setPrimaryKeyJoinColumns(processPrimaryKeyJoinColumns(parsedManyToOne));
        return variableDef;
    }

    private PrimaryKeyJoinColumnsSnippet processPrimaryKeyJoinColumns(SingleRelationAttribute singleRelationAttribute) {
        PrimaryKeyJoinColumnsSnippet primaryKeyJoinColumnsSnippet = null;
        List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns = getPrimaryKeyJoinColumns(singleRelationAttribute.getPrimaryKeyJoinColumn());
        ForeignKeySnippet primaryKeyForeignKey = getForeignKey(singleRelationAttribute.getPrimaryKeyForeignKey());
        if (primaryKeyJoinColumns != null && !primaryKeyJoinColumns.isEmpty()) {
            primaryKeyJoinColumnsSnippet = new PrimaryKeyJoinColumnsSnippet(repeatable);
            primaryKeyJoinColumnsSnippet.setPrimaryKeyJoinColumns(primaryKeyJoinColumns);
            primaryKeyJoinColumnsSnippet.setForeignKey(primaryKeyForeignKey);
        }
        return primaryKeyJoinColumnsSnippet;
    }

    protected VariableDefSnippet processOneToMany(OneToMany parsedOneToMany) {
        List<String> cascadeTypes = getCascadeTypes(
                parsedOneToMany.getCascade());

        JoinTableSnippet joinTable = getJoinTable(parsedOneToMany.getJoinTable());

        OneToManySnippet oneToMany = new OneToManySnippet();

        oneToMany.setCascadeTypes(cascadeTypes);
        oneToMany.setTargetEntity(parsedOneToMany.getTargetEntity());
        oneToMany.setTargetEntityPackage(parsedOneToMany.getConnectedEntity().getAbsolutePackage(rootPackageName));
        oneToMany.setTargetField(parsedOneToMany.getConnectedAttributeName());
        oneToMany.setMappedBy(parsedOneToMany.getMappedBy());
        oneToMany.setMapKeySnippet(updateMapKeyAttributeSnippet(parsedOneToMany));
        if (parsedOneToMany.getFetch() != null) {
            oneToMany.setFetchType(parsedOneToMany.getFetch().value());
        }
        oneToMany.setOrphanRemoval(parsedOneToMany.getOrphanRemoval());

        VariableDefSnippet variableDef = getVariableDef(parsedOneToMany);
        variableDef.setType(rootPackageName, parsedOneToMany.getConnectedEntity());
        variableDef.setCollectionType(parsedOneToMany.getCollectionType());
        variableDef.setCollectionImplType(parsedOneToMany.getCollectionImplType());
        variableDef.setRelationDef(oneToMany);
        variableDef.setJoinTable(joinTable);
        variableDef.setJoinColumns(getJoinColumnsSnippet(parsedOneToMany, false));
        if (parsedOneToMany.getOrderBy() != null) {
            variableDef.setOrderBy(new OrderBySnippet(parsedOneToMany.getOrderBy()));
        } else if (parsedOneToMany.getOrderColumn() != null) {
            variableDef.setOrderColumn(new OrderColumnSnippet(parsedOneToMany.getOrderColumn()));
        }

        if (parsedOneToMany.getMapKey() != null) {
            variableDef.setMapKey(parsedOneToMany.getMapKey().getName());
        }
        variableDef.setConverts(processConverts(parsedOneToMany.getMapKeyConverts()));
        return variableDef;
    }

    protected VariableDefSnippet processManyToMany(ManyToMany parsedManyToMany) {
        List<String> cascadeTypes = getCascadeTypes(parsedManyToMany.getCascade());

        JoinTableSnippet joinTable = getJoinTable(parsedManyToMany.getJoinTable());

        ManyToManySnippet manyToMany = new ManyToManySnippet();
        manyToMany.setMapKeySnippet(updateMapKeyAttributeSnippet(parsedManyToMany));
        manyToMany.setMappedBy(parsedManyToMany.getMappedBy());
        manyToMany.setTargetEntity(parsedManyToMany.getTargetEntity());
        manyToMany.setTargetEntityPackage(parsedManyToMany.getConnectedEntity().getAbsolutePackage(rootPackageName));
        manyToMany.setTargetField(parsedManyToMany.getConnectedAttributeName());
        manyToMany.setCascadeTypes(cascadeTypes);

        if (parsedManyToMany.getFetch() != null) {
            manyToMany.setFetchType(parsedManyToMany.getFetch().value());
        }
        //TODO: Checked this error - The ORM.xsd has this but NOT the
        //http://www.oracle.com/technology/products/ias/toplink/jpa/resources/toplink-jpa-annotations.html
//            manyToMany.setOrderBy(parsedManyToMany.getOrderBy());

        VariableDefSnippet variableDef = getVariableDef(parsedManyToMany);
        variableDef.setType(rootPackageName, parsedManyToMany.getConnectedEntity());
        variableDef.setCollectionType(parsedManyToMany.getCollectionType());
        variableDef.setCollectionImplType(parsedManyToMany.getCollectionImplType());

        variableDef.setRelationDef(manyToMany);
        variableDef.setJoinTable(joinTable);
        if (parsedManyToMany.getOrderBy() != null) {
            variableDef.setOrderBy(new OrderBySnippet(parsedManyToMany.getOrderBy()));
        } else if (parsedManyToMany.getOrderColumn() != null) {
            variableDef.setOrderColumn(new OrderColumnSnippet(parsedManyToMany.getOrderColumn()));
        }
        if (parsedManyToMany.getMapKey() != null) {
            variableDef.setMapKey(parsedManyToMany.getMapKey().getName());
        }
        variableDef.setConverts(processConverts(parsedManyToMany.getMapKeyConverts()));
        return variableDef;
    }

    protected ConvertsSnippet processConverts(List<Convert> parsedConverts) {

        if (parsedConverts == null || parsedConverts.isEmpty()) {
            return null;
        }

        ConvertsSnippet convertsSnippet = new ConvertsSnippet(repeatable);

        for (Convert parsedConvert : parsedConverts) {
            if (parsedConvert != null && ConvertValidator.isNotEmpty(parsedConvert)) {
                ConvertSnippet convertSnippet = new ConvertSnippet(parsedConvert);
                convertsSnippet.add(convertSnippet);
            }

        }
        if (!convertsSnippet.get().isEmpty()) {
            return convertsSnippet;
        }

        return null;
    }

    protected ColumnDefSnippet getColumnDef(Column column) {
        return getColumnDef(column, false);
    }

    protected ColumnDefSnippet getColumnDef(Column column, boolean mapKey) {

        if (column == null) {
            return null;
        }

        ColumnDefSnippet columnDef = new ColumnDefSnippet(mapKey);

        columnDef.setColumnDefinition(column.getColumnDefinition());
        columnDef.setName(column.getName());
        columnDef.setTable(column.getTable());
        columnDef.setInsertable(column.getInsertable());
        columnDef.setNullable(column.getNullable());
        columnDef.setUnique(column.getUnique());
        columnDef.setUpdatable(column.getUpdatable());

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

    protected void processInternalAttributeOverride(AttributeOverridesHandler attrHandler, Set<AttributeOverride> attributeOverrrides) {
        if (attributeOverrrides != null && !attributeOverrrides.isEmpty()
                && attrHandler.getAttributeOverrides() == null) {
            attrHandler.setAttributeOverrides(new AttributeOverridesSnippet(repeatable));
        }
        Set<AttributeOverride> sortedAttributeOverrides
                = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
        sortedAttributeOverrides.addAll(attributeOverrrides);
        for (AttributeOverride attributeOverride : sortedAttributeOverrides) {
            AttributeOverrideSnippet attributeOverrideSnippet = new AttributeOverrideSnippet();
            ColumnDefSnippet columnDef = getColumnDef(attributeOverride.getColumn());
            if (columnDef == null) {
                continue;
            }
            attributeOverrideSnippet.setColumnDef(columnDef);
            attributeOverrideSnippet.setName(attributeOverride.getName());
            attrHandler.getAttributeOverrides().add(attributeOverrideSnippet);
        }
        if (attrHandler.getAttributeOverrides() != null && attrHandler.getAttributeOverrides().get().isEmpty()) {
            attrHandler.setAttributeOverrides(null);
        }
    }

    protected void processInternalAssociationOverride(AssociationOverridesHandler assoHandler, Set<AssociationOverride> associationOverrides) {

        if (associationOverrides != null && !associationOverrides.isEmpty()
                && assoHandler.getAssociationOverrides() == null) {
            assoHandler.setAssociationOverrides(new AssociationOverridesSnippet(repeatable));
        }

        Set<AssociationOverride> sortedAssociationOverrrides
                = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
        sortedAssociationOverrrides.addAll(associationOverrides);
        for (AssociationOverride associationOverride : sortedAssociationOverrrides) {

            List<JoinColumnSnippet> joinColumnsList = getJoinColumns(associationOverride.getJoinColumn(), false);
            JoinTableSnippet joinTable = getJoinTable(associationOverride.getJoinTable());

            if (joinTable.isEmpty() && joinColumnsList.isEmpty()) {
                continue;
            }
            AssociationOverrideSnippet associationOverrideSnippet = new AssociationOverrideSnippet();
            associationOverrideSnippet.setName(associationOverride.getName());
            associationOverrideSnippet.setJoinColumns(joinColumnsList);
            associationOverrideSnippet.setJoinTable(joinTable);

            associationOverrideSnippet.setForeignKey(getForeignKey(associationOverride.getForeignKey()));

            assoHandler.getAssociationOverrides().add(associationOverrideSnippet);
        }
        if (assoHandler.getAssociationOverrides() != null && assoHandler.getAssociationOverrides().get().isEmpty()) {
            assoHandler.setAssociationOverrides(null);
        }
    }

    protected List<JoinColumnSnippet> getJoinColumns(List<? extends JoinColumn> parsedJoinColumns, boolean mapKey) {

        List<JoinColumnSnippet> joinColumns = new ArrayList<>();

        parsedJoinColumns.stream().filter(JoinColumnValidator::isNotEmpty).forEach(parsedJoinColumn -> {

            JoinColumnSnippet joinColumn = new JoinColumnSnippet(mapKey);

            joinColumn.setColumnDefinition(
                    parsedJoinColumn.getColumnDefinition());

            if (parsedJoinColumn.getInsertable() != null) {
                joinColumn.setInsertable(parsedJoinColumn.getInsertable());
            }

            if (parsedJoinColumn.getUnique() != null) {
                joinColumn.setUnique(parsedJoinColumn.getUnique());
            }

            if (parsedJoinColumn.getNullable() != null) {
                joinColumn.setNullable(parsedJoinColumn.getNullable());
            }

            if (parsedJoinColumn.getUpdatable() != null) {
                joinColumn.setUpdatable(parsedJoinColumn.getUpdatable());
            }

            joinColumn.setName(parsedJoinColumn.getName());
            joinColumn.setReferencedColumnName(parsedJoinColumn.getReferencedColumnName());
            joinColumn.setTable(parsedJoinColumn.getTable());
            joinColumn.setForeignKey(getForeignKey(parsedJoinColumn.getForeignKey()));

            joinColumns.add(joinColumn);
        });

        return joinColumns;
    }

    protected JoinTableSnippet getJoinTable(JoinTable parsedJoinTable) {
        if (parsedJoinTable == null || JoinTableValidator.isEmpty(parsedJoinTable)) {
            return null;
        }

        List<JoinColumnSnippet> inverseJoinColumns = getJoinColumns(
                parsedJoinTable.getInverseJoinColumn(), false);

        List<JoinColumnSnippet> joinColumns = getJoinColumns(
                parsedJoinTable.getJoinColumn(), false);

        Set<UniqueConstraint> parsedUniqueConstraints
                = parsedJoinTable.getUniqueConstraint();

        List<UniqueConstraintSnippet> uniqueConstraints = getUniqueConstraints(parsedUniqueConstraints);

        JoinTableSnippet joinTable = new JoinTableSnippet();

        joinTable.setCatalog(parsedJoinTable.getCatalog());
        joinTable.setName(parsedJoinTable.getName());
        joinTable.setSchema(parsedJoinTable.getSchema());
        joinTable.setJoinColumns(joinColumns);
        joinTable.setInverseJoinColumns(inverseJoinColumns);
        joinTable.setUniqueConstraints(uniqueConstraints);
        joinTable.setIndices(getIndexes(parsedJoinTable.getIndex()));

        joinTable.setForeignKey(getForeignKey(parsedJoinTable.getForeignKey()));
        joinTable.setInverseForeignKey(getForeignKey(parsedJoinTable.getInverseForeignKey()));

        return joinTable;
    }

    protected ForeignKeySnippet getForeignKey(ForeignKey parsedForeignKey) {
        if (parsedForeignKey == null || ForeignKeyValidator.isEmpty(parsedForeignKey)) {
            return null;
        }

        ForeignKeySnippet foreignKey = new ForeignKeySnippet();

        foreignKey.setName(parsedForeignKey.getName());
        foreignKey.setDescription(parsedForeignKey.getDescription());
        foreignKey.setForeignKeyDefinition(parsedForeignKey.getForeignKeyDefinition());
        if (parsedForeignKey.getConstraintMode() != null) {
            foreignKey.setConstraintMode(parsedForeignKey.getConstraintMode().name());
        }

        return foreignKey;
    }

    protected List<IndexSnippet> getIndexes(List<Index> parsedIndexes) {
        if (parsedIndexes == null || parsedIndexes.isEmpty()) {
            return Collections.<IndexSnippet>emptyList();
        }
        return parsedIndexes.stream().filter(index -> !index.getColumnList().isEmpty())
                .map(index -> new IndexSnippet(index)).collect(toList());
    }

    protected List<UniqueConstraintSnippet> getUniqueConstraints(Set<UniqueConstraint> parsedUniqueConstraints) {
        if (parsedUniqueConstraints == null || parsedUniqueConstraints.isEmpty()) {
            return Collections.<UniqueConstraintSnippet>emptyList();
        }
        return parsedUniqueConstraints.stream().map(c -> new UniqueConstraintSnippet(c)).collect(toList());
    }

    private MapKeySnippet updateMapKeyAttributeSnippet(MapKeyHandler mapKeyHandler) {
        if (mapKeyHandler.getMapKeyType() == null || mapKeyHandler.getValidatedMapKeyType() == null) {
            return null;
        }
        MapKeySnippet snippet = new MapKeySnippet();
        if (mapKeyHandler.getMapKeyType() == MapKeyType.EXT && mapKeyHandler.getValidatedMapKeyType() == MapKeyType.EXT) {
            snippet.setMapKeyAttribute(mapKeyHandler.getMapKeyAttribute());
            snippet.setMapKeyAttributeType(mapKeyHandler.getMapKeyAttribute().getDataTypeLabel());
        } else if (mapKeyHandler.getMapKeyEntity() != null) {
            List<JoinColumnSnippet> joinColumnsList = getJoinColumns(mapKeyHandler.getMapKeyJoinColumn(), true);
            JoinColumnsSnippet joinColumns = null;
            if (!joinColumnsList.isEmpty()) {
                joinColumns = new JoinColumnsSnippet(repeatable, true);
                joinColumns.setJoinColumns(joinColumnsList);
                joinColumns.setForeignKey(getForeignKey(mapKeyHandler.getMapKeyForeignKey()));
            }
            snippet.setJoinColumnsSnippet(joinColumns);
            snippet.setMapKeyAttributeType(mapKeyHandler.getMapKeyEntity().getClazz());
        } else if (mapKeyHandler.getMapKeyEmbeddable() != null) {//TODO attr override
            snippet.setAttributeOverrideSnippet(processAttributeOverrides(mapKeyHandler.getMapKeyAttributeOverride()));
            snippet.setMapKeyAttributeType(mapKeyHandler.getMapKeyEmbeddable().getClazz());
        } else {
            if (mapKeyHandler.getMapKeyEnumerated() != null) {
                EnumeratedSnippet enumeratedSnippet = new EnumeratedSnippet(true);
                enumeratedSnippet.setValue(mapKeyHandler.getMapKeyEnumerated());
                snippet.setEnumeratedSnippet(enumeratedSnippet);
            } else if (mapKeyHandler.getMapKeyTemporal() != null) {
                TemporalSnippet temporalSnippet = new TemporalSnippet(true);
                temporalSnippet.setValue(mapKeyHandler.getMapKeyTemporal());
                snippet.setTemporalSnippet(temporalSnippet);
            }
            snippet.setColumnSnippet(getColumnDef(mapKeyHandler.getMapKeyColumn(), true));
            snippet.setMapKeyAttributeType(mapKeyHandler.getMapKeyAttributeType());
        }
        return snippet;
    }

    private JoinColumnsSnippet getJoinColumnsSnippet(JoinColumnHandler joinColumnHandler, boolean mapKey) {
        List<JoinColumnSnippet> joinColumnsList = getJoinColumns(joinColumnHandler.getJoinColumn(), mapKey);
        JoinColumnsSnippet joinColumns = null;
        if (!joinColumnsList.isEmpty()) {
            joinColumns = new JoinColumnsSnippet(repeatable, mapKey);
            joinColumns.setJoinColumns(joinColumnsList);
            joinColumns.setForeignKey(getForeignKey(joinColumnHandler.getForeignKey()));
        }
        return joinColumns;
    }

    protected CollectionTableSnippet getCollectionTable(CollectionTable parsedCollectionTable) {
        if (parsedCollectionTable == null || CollectionTableValidator.isEmpty(parsedCollectionTable)) {
            return null;
        }

        List<JoinColumnSnippet> joinColumns = getJoinColumns(parsedCollectionTable.getJoinColumn(), false);

        Set<UniqueConstraint> parsedUniqueConstraints = parsedCollectionTable.getUniqueConstraint();

        List<UniqueConstraintSnippet> uniqueConstraints = getUniqueConstraints(parsedUniqueConstraints);

        CollectionTableSnippet collectionTable = new CollectionTableSnippet();

        collectionTable.setCatalog(parsedCollectionTable.getCatalog());
        collectionTable.setName(parsedCollectionTable.getName());
        collectionTable.setSchema(parsedCollectionTable.getSchema());
        collectionTable.setJoinColumns(joinColumns);
        collectionTable.setUniqueConstraints(uniqueConstraints);
        collectionTable.setIndices(getIndexes(parsedCollectionTable.getIndex()));

        collectionTable.setForeignKey(getForeignKey(parsedCollectionTable.getForeignKey()));

        return collectionTable;
    }

    protected List<String> getCascadeTypes(CascadeType cascadeType) {

        if (cascadeType == null) {
            return Collections.<String>emptyList();
        }

        List<String> cascadeTypes = new ArrayList<>();

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

    protected AttributeOverridesSnippet processAttributeOverrides(
            Set<AttributeOverride> attributeOverrides) {

        if (attributeOverrides == null || attributeOverrides.isEmpty()) {
            return null;
        }
        AttributeOverridesSnippet attributeOverridesSnippet = new AttributeOverridesSnippet(repeatable);

        Set<AttributeOverride> sortedAttributeOverrides
                = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
        sortedAttributeOverrides.addAll(attributeOverrides);
        for (AttributeOverride attributeOverride : sortedAttributeOverrides) {

            ColumnDefSnippet columnDef = getColumnDef(attributeOverride.getColumn());
            if (columnDef == null) {
                continue;
            }
            AttributeOverrideSnippet attributeOverrideSnippet = new AttributeOverrideSnippet();

            attributeOverrideSnippet.setColumnDef(columnDef);
            attributeOverrideSnippet.setName(attributeOverride.getName());

            attributeOverridesSnippet.add(attributeOverrideSnippet);
        }

        if (attributeOverridesSnippet.get().isEmpty()) {
            return null;
        }

        return attributeOverridesSnippet;
    }

}
