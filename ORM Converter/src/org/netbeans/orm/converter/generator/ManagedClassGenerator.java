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
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.jcode.infra.JavaEEVersion;
import org.netbeans.jpa.modeler.settings.code.CodePanel;
import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.AttributeOverride;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.CascadeType;
import org.netbeans.jpa.modeler.spec.CollectionTable;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.Convert;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.EmptyType;
import org.netbeans.jpa.modeler.spec.EnumType;
import org.netbeans.jpa.modeler.spec.FetchType;
import org.netbeans.jpa.modeler.spec.ForeignKey;
import org.netbeans.jpa.modeler.spec.Index;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.JoinTable;
import org.netbeans.jpa.modeler.spec.Lob;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.TemporalType;
import org.netbeans.jpa.modeler.spec.Transient;
import org.netbeans.jpa.modeler.spec.UniqueConstraint;
import org.netbeans.jpa.modeler.spec.extend.JoinColumnHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyType;
import org.netbeans.jpa.modeler.spec.validator.ConvertValidator;
import org.netbeans.jpa.modeler.spec.validator.column.ForeignKeyValidator;
import org.netbeans.jpa.modeler.spec.validator.column.JoinColumnValidator;
import org.netbeans.jpa.modeler.spec.validator.table.CollectionTableValidator;
import org.netbeans.jpa.modeler.spec.validator.table.JoinTableValidator;
import org.netbeans.orm.converter.compiler.AssociationOverrideSnippet;
import org.netbeans.orm.converter.compiler.AssociationOverridesHandler;
import org.netbeans.orm.converter.compiler.AssociationOverridesSnippet;
import org.netbeans.orm.converter.compiler.AttributeOverrideSnippet;
import org.netbeans.orm.converter.compiler.AttributeOverridesHandler;
import org.netbeans.orm.converter.compiler.AttributeOverridesSnippet;
import org.netbeans.orm.converter.compiler.BasicSnippet;
import org.netbeans.orm.converter.compiler.CollectionTableSnippet;
import org.netbeans.orm.converter.compiler.ColumnDefSnippet;
import org.netbeans.orm.converter.compiler.ConvertSnippet;
import org.netbeans.orm.converter.compiler.ConvertsSnippet;
import org.netbeans.orm.converter.compiler.ElementCollectionSnippet;
import org.netbeans.orm.converter.compiler.EnumeratedSnippet;
import org.netbeans.orm.converter.compiler.ForeignKeySnippet;
import org.netbeans.orm.converter.compiler.IndexSnippet;
import org.netbeans.orm.converter.compiler.JoinColumnSnippet;
import org.netbeans.orm.converter.compiler.JoinColumnsSnippet;
import org.netbeans.orm.converter.compiler.JoinTableSnippet;
import org.netbeans.orm.converter.compiler.ManyToManySnippet;
import org.netbeans.orm.converter.compiler.ManyToOneSnippet;
import org.netbeans.orm.converter.compiler.MapKeySnippet;
import org.netbeans.orm.converter.compiler.OneToManySnippet;
import org.netbeans.orm.converter.compiler.OneToOneSnippet;
import org.netbeans.orm.converter.compiler.OrderBySnippet;
import org.netbeans.orm.converter.compiler.OrderColumnSnippet;
import org.netbeans.orm.converter.compiler.TemporalSnippet;
import org.netbeans.orm.converter.compiler.UniqueConstraintSnippet;
import org.netbeans.orm.converter.compiler.def.VariableDefSnippet;
import org.netbeans.orm.converter.compiler.def.ManagedClassDefSnippet;

public abstract class ManagedClassGenerator<T extends ManagedClassDefSnippet> extends ClassGenerator<T> {

    public ManagedClassGenerator(T classDef, JavaEEVersion javaEEVersion) {
        super(classDef, javaEEVersion);
    }

    protected void processTransient(List<Transient> parsedTransients) {
        for (Transient parsedTransient : parsedTransients) {
            VariableDefSnippet variableDef = getVariableDef(parsedTransient);
            variableDef.setType(parsedTransient.getAttributeType());
            variableDef.setTranzient(true);
            variableDef.setFunctionalType(parsedTransient.isOptionalReturnType());
        }
    }

    protected void processManyToMany(List<ManyToMany> parsedManyToManys) {

        if (parsedManyToManys == null) {
            return;
        }
        for (ManyToMany parsedManyToMany : parsedManyToManys) {
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
            } else if (CodePanel.isLazyDefaultTypeForSingleAssociation()) {
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
            variableDef.setFunctionalType(parsedOneToOne.isOptionalReturnType());
        }
    }

    protected void processManyToOne(List<ManyToOne> parsedManyToOnes) {
        if (parsedManyToOnes == null) {
            return;
        }

        for (ManyToOne parsedManyToOne : parsedManyToOnes) {
            List<String> cascadeTypes = getCascadeTypes(
                    parsedManyToOne.getCascade());

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
            } else if (CodePanel.isLazyDefaultTypeForSingleAssociation()) {
                manyToOne.setFetchType(FetchType.LAZY.value());
            }

            manyToOne.setPrimaryKey(parsedManyToOne.isPrimaryKey());
            manyToOne.setMapsId(parsedManyToOne.getMapsId());

            VariableDefSnippet variableDef = getVariableDef(parsedManyToOne);
            variableDef.setType(rootPackageName, parsedManyToOne.getConnectedEntity());

            variableDef.setRelationDef(manyToOne);
            variableDef.setJoinTable(joinTable);
            variableDef.setJoinColumns(getJoinColumnsSnippet(parsedManyToOne, false));
            variableDef.setFunctionalType(parsedManyToOne.isOptionalReturnType());
        }
    }

    protected void processEmbedded(List<Embedded> parsedEmbeddeds) {

        if (parsedEmbeddeds == null) {
            return;
        }
        for (Embedded parsedEmbeded : parsedEmbeddeds) {
            VariableDefSnippet variableDef = getVariableDef(parsedEmbeded);
            variableDef.setType(rootPackageName, parsedEmbeded.getConnectedClass());

            variableDef.setEmbedded(true);
            variableDef.setFunctionalType(parsedEmbeded.isOptionalReturnType());
            variableDef.setConverts(processConverts(parsedEmbeded.getConverts()));
            processInternalAttributeOverride(variableDef, parsedEmbeded.getAttributeOverride());
            processInternalAssociationOverride(variableDef, parsedEmbeded.getAssociationOverride());
        }
    }

    protected void processElementCollection(List<ElementCollection> parsedElementCollections) {
        if (parsedElementCollections == null) {
            return;
        }
        for (ElementCollection parsedElementCollection : parsedElementCollections) {

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
        }
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

            variableDef.setBasic(basic);
            variableDef.setColumnDef(columnDef);
            variableDef.setEnumerated(enumerated);
            variableDef.setTemporal(temporal);
            variableDef.setType(parsedBasic.getAttributeType());
            variableDef.setFunctionalType(parsedBasic.isOptionalReturnType());
            variableDef.setConverts(processConverts(Collections.singletonList(parsedBasic.getConvert())));
            variableDef.setLob(parsedLob != null);
        }
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
        for (AttributeOverride parsedAttributeOverride : attributeOverrrides) {
            AttributeOverrideSnippet attributeOverride = new AttributeOverrideSnippet();
            ColumnDefSnippet columnDef = getColumnDef(parsedAttributeOverride.getColumn());
            if (columnDef == null) {
                continue;
            }
            attributeOverride.setColumnDef(columnDef);
            attributeOverride.setName(parsedAttributeOverride.getName());
            attrHandler.getAttributeOverrides().add(attributeOverride);
        }
        if (attrHandler.getAttributeOverrides() != null && attrHandler.getAttributeOverrides().get().isEmpty()) {
            attrHandler.setAttributeOverrides(null);
        }
    }

    protected void processInternalAssociationOverride(AssociationOverridesHandler assoHandler, Set<AssociationOverride> associationOverrrides) {

        if (associationOverrrides != null && !associationOverrrides.isEmpty()
                && assoHandler.getAssociationOverrides() == null) {
            assoHandler.setAssociationOverrides(new AssociationOverridesSnippet(repeatable));
        }

        for (AssociationOverride parsedAssociationOverride : associationOverrrides) {

            List<JoinColumnSnippet> joinColumnsList = getJoinColumns(parsedAssociationOverride.getJoinColumn(), false);
            JoinTableSnippet joinTable = getJoinTable(parsedAssociationOverride.getJoinTable());

            if (joinTable.isEmpty() && joinColumnsList.isEmpty()) {
                continue;
            }
            AssociationOverrideSnippet associationOverride = new AssociationOverrideSnippet();
            associationOverride.setName(parsedAssociationOverride.getName());
            associationOverride.setJoinColumns(joinColumnsList);
            associationOverride.setJoinTable(joinTable);

            associationOverride.setForeignKey(getForeignKey(parsedAssociationOverride.getForeignKey()));

            assoHandler.getAssociationOverrides().add(associationOverride);
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
            return Collections.EMPTY_LIST;
        }
        return parsedIndexes.stream().filter(index -> !index.getColumnList().isEmpty())
                .map(index -> new IndexSnippet(index)).collect(toList());
    }
    protected List<UniqueConstraintSnippet> getUniqueConstraints(Set<UniqueConstraint> parsedUniqueConstraints) {
        if (parsedUniqueConstraints == null || parsedUniqueConstraints.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return parsedUniqueConstraints.stream().map(c -> new UniqueConstraintSnippet(c)).collect(toList());
    }    private MapKeySnippet updateMapKeyAttributeSnippet(MapKeyHandler mapKeyHandler) {
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
            return Collections.EMPTY_LIST;
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
            Set<AttributeOverride> parsedAttributeOverrides) {

        if (parsedAttributeOverrides == null || parsedAttributeOverrides.isEmpty()) {
            return null;
        }
        AttributeOverridesSnippet attributeOverridesSnippet = new AttributeOverridesSnippet(repeatable);

        for (AttributeOverride parsedAttributeOverride : parsedAttributeOverrides) {

            ColumnDefSnippet columnDef = getColumnDef(parsedAttributeOverride.getColumn());
            if (columnDef == null) {
                continue;
            }
            AttributeOverrideSnippet attributeOverride = new AttributeOverrideSnippet();

            attributeOverride.setColumnDef(columnDef);
            attributeOverride.setName(parsedAttributeOverride.getName());

            attributeOverridesSnippet.add(attributeOverride);
        }

        if (attributeOverridesSnippet.get().isEmpty()) {
            return null;
        }

        return attributeOverridesSnippet;
    }


}
