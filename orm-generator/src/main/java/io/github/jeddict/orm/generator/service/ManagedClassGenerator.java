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
import io.github.jeddict.orm.generator.compiler.TransientSnippet;
import io.github.jeddict.orm.generator.compiler.AssociationOverrideSnippet;
import io.github.jeddict.orm.generator.compiler.AssociationOverridesHandler;
import io.github.jeddict.orm.generator.compiler.AssociationOverridesSnippet;
import io.github.jeddict.orm.generator.compiler.AttributeOverrideSnippet;
import io.github.jeddict.orm.generator.compiler.AttributeOverridesHandler;
import io.github.jeddict.orm.generator.compiler.AttributeOverridesSnippet;
import io.github.jeddict.orm.generator.compiler.BasicSnippet;
import io.github.jeddict.orm.generator.compiler.CollectionTableSnippet;
import io.github.jeddict.orm.generator.compiler.ColumnSnippet;
import io.github.jeddict.orm.generator.compiler.ConvertSnippet;
import io.github.jeddict.orm.generator.compiler.ConvertsSnippet;
import io.github.jeddict.orm.generator.compiler.ElementCollectionSnippet;
import io.github.jeddict.orm.generator.compiler.EnumeratedSnippet;
import io.github.jeddict.orm.generator.compiler.ForeignKeySnippet;
import io.github.jeddict.orm.generator.compiler.IndexSnippet;
import io.github.jeddict.orm.generator.compiler.JoinColumnSnippet;
import io.github.jeddict.orm.generator.compiler.JoinColumnsSnippet;
import io.github.jeddict.orm.generator.compiler.JoinTableSnippet;
import io.github.jeddict.orm.generator.compiler.LobSnippet;
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

    protected VariableDefSnippet processBasic(Basic basic) {
        VariableDefSnippet variableDef = getVariableDef(basic);
        variableDef.setType(basic.getAttributeType());

        ColumnSnippet columnSnippet = ManagedClassGenerator.this.processColumn(basic.getColumn());
        variableDef.setColumn(columnSnippet);

        variableDef.setConverts(processConverts(singletonList(basic.getConvert())));

        if (!classDef.isNoSQL()) {
            EnumType enumType = basic.getEnumerated();
            if (enumType != null) {
                EnumeratedSnippet enumerated = new EnumeratedSnippet();
                enumerated.setValue(enumType);
                variableDef.setEnumerated(enumerated);
            }

            TemporalType temporalType = basic.getTemporal();
            if (temporalType != null) {
                TemporalSnippet temporal = new TemporalSnippet();
                temporal.setValue(temporalType);
                variableDef.setTemporal(temporal);
            }

            BasicSnippet basicSnippet = new BasicSnippet();
            if (basic.getFetch() != null) {
                basicSnippet.setFetchType(basic.getFetch().value());
            }
            if (basic.getOptional() != null) {
                basicSnippet.setOptional(basic.getOptional());
            }
            variableDef.setBasic(basicSnippet);

            if (basic.getLob() != null) {
                variableDef.setLob(new LobSnippet());
            }

        }

        return variableDef;
    }

    protected VariableDefSnippet processTransient(Transient tranzient) {
        VariableDefSnippet variableDef = getVariableDef(tranzient);
        variableDef.setType(tranzient.getAttributeType());
        if (!classDef.isNoSQL()) {
            TransientSnippet transientSnippet = new TransientSnippet();
            variableDef.setTranzient(transientSnippet);
        }
        return variableDef;
    }

    protected VariableDefSnippet processEmbedded(Embedded embedded) {
        VariableDefSnippet variableDef = getVariableDef(embedded);
        variableDef.setType(rootPackageName, embedded.getConnectedClass());
        variableDef.setConverts(processConverts(embedded.getConverts()));
        if (!classDef.isNoSQL()) {
            variableDef.setEmbedded(true);
            processInternalAttributeOverride(variableDef, embedded.getAttributeOverride());
            processInternalAssociationOverride(variableDef, embedded.getAssociationOverride());
        }
        return variableDef;
    }

    protected VariableDefSnippet processElementCollection(ElementCollection elementCollection) {
        VariableDefSnippet variableDef = getVariableDef(elementCollection);
        if (elementCollection.getConnectedClass() != null) {
            variableDef.setType(rootPackageName, elementCollection.getConnectedClass());
        } else {
            variableDef.setType(elementCollection.getAttributeType());
        }
        variableDef.setCollectionType(elementCollection.getCollectionType());
        variableDef.setCollectionImplType(elementCollection.getCollectionImplType());
        List<Convert> converts = new ArrayList<>();

        if (!classDef.isNoSQL()) {
            FetchType fetchType = elementCollection.getFetch();
            ElementCollectionSnippet elementCollectionSnippet = new ElementCollectionSnippet();
            elementCollectionSnippet.setMapKeySnippet(updateMapKeyAttributeSnippet(elementCollection));
            elementCollectionSnippet.setTargetClass(elementCollection.getAttributeType());
            if (elementCollection.getConnectedClass() != null) {
                elementCollectionSnippet.setTargetClassPackage(elementCollection.getConnectedClass().getAbsolutePackage(rootPackageName));
            }
            if (fetchType != null) {
                elementCollectionSnippet.setFetchType(fetchType.value());
            }
            variableDef.setElementCollection(elementCollectionSnippet);

            CollectionTableSnippet collectionTable = processCollectionTable(elementCollection.getCollectionTable());
            variableDef.setCollectionTable(collectionTable);

            if (elementCollection.getOrderBy() != null) {
                variableDef.setOrderBy(new OrderBySnippet(elementCollection.getOrderBy()));
            } else if (elementCollection.getOrderColumn() != null) {
                variableDef.setOrderColumn(new OrderColumnSnippet(elementCollection.getOrderColumn()));
            }

            if (elementCollection.getLob() != null) {
                variableDef.setLob(new LobSnippet());
            }

            if (elementCollection.getConnectedClass() == null) {//if not embeddable
                EnumType enumType = elementCollection.getEnumerated();
                EnumeratedSnippet enumerated = null;
                if (enumType != null) {
                    enumerated = new EnumeratedSnippet();
                    enumerated.setValue(enumType);
                }

                TemporalType temporalType = elementCollection.getTemporal();
                TemporalSnippet temporal = null;
                if (temporalType != null) {
                    temporal = new TemporalSnippet();
                    temporal.setValue(temporalType);
                }
                variableDef.setEnumerated(enumerated);
                variableDef.setTemporal(temporal);
                variableDef.setColumn(ManagedClassGenerator.this.processColumn(elementCollection.getColumn()));
            } else {
                processInternalAttributeOverride(variableDef, elementCollection.getAttributeOverride());
                processInternalAssociationOverride(variableDef, elementCollection.getAssociationOverride());
            }

            converts.addAll(elementCollection.getMapKeyConverts());
        }

        converts.addAll(elementCollection.getConverts());
        variableDef.setConverts(processConverts(converts));

        return variableDef;
    }

    protected List<PrimaryKeyJoinColumnSnippet> getPrimaryKeyJoinColumns(
            List<PrimaryKeyJoinColumn> primaryKeyJoinColumns) {

        if (primaryKeyJoinColumns == null || primaryKeyJoinColumns.isEmpty()) {
            return Collections.<PrimaryKeyJoinColumnSnippet>emptyList();
        }

        List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumnSnippets = new ArrayList<>();

        for (PrimaryKeyJoinColumn primaryKeyJoinColumn : primaryKeyJoinColumns) {
            PrimaryKeyJoinColumnSnippet primaryKeyJoinColumnSnippet = new PrimaryKeyJoinColumnSnippet();
            primaryKeyJoinColumnSnippet.setColumnDefinition(primaryKeyJoinColumn.getColumnDefinition());
            primaryKeyJoinColumnSnippet.setName(primaryKeyJoinColumn.getName());
            primaryKeyJoinColumnSnippet.setReferencedColumnName(primaryKeyJoinColumn.getReferencedColumnName());
            primaryKeyJoinColumnSnippet.setForeignKey(processForeignKey(primaryKeyJoinColumn.getForeignKey()));
            primaryKeyJoinColumnSnippets.add(primaryKeyJoinColumnSnippet);
        }

        return primaryKeyJoinColumnSnippets;
    }

    protected VariableDefSnippet processOneToOne(OneToOne oneToOne) {
        VariableDefSnippet variableDef = getVariableDef(oneToOne);
        variableDef.setType(rootPackageName, oneToOne.getConnectedEntity());

        if (!classDef.isNoSQL()) {
            List<String> cascadeTypes = getCascadeTypes(oneToOne.getCascade());

            JoinTableSnippet joinTable = getJoinTable(oneToOne.getJoinTable());

            OneToOneSnippet oneToOneSnippet = new OneToOneSnippet();

            oneToOneSnippet.setCascadeTypes(cascadeTypes);
            oneToOneSnippet.setTargetEntity(oneToOne.getTargetEntity());
            oneToOneSnippet.setTargetEntityPackage(oneToOne.getConnectedEntity().getAbsolutePackage(rootPackageName));
            oneToOneSnippet.setTargetField(oneToOne.getConnectedAttributeName());
            oneToOneSnippet.setMappedBy(oneToOne.getMappedBy());
            if (oneToOne.getOptional() != null) {
                oneToOneSnippet.setOptional(oneToOne.getOptional());
            }

            if (oneToOne.getFetch() != null) {
                oneToOneSnippet.setFetchType(oneToOne.getFetch().value());
            } else if (ClassDiagramSettings.isLazyDefaultTypeForSingleAssociation()) {
                oneToOneSnippet.setFetchType(FetchType.LAZY.value());
            }

            oneToOneSnippet.setOrphanRemoval(oneToOne.getOrphanRemoval());

            oneToOneSnippet.setPrimaryKey(oneToOne.isPrimaryKey());
            oneToOneSnippet.setMapsId(oneToOne.getMapsId());

            variableDef.setRelation(oneToOneSnippet);
            variableDef.setJoinTable(joinTable);
            variableDef.setJoinColumns(getJoinColumnsSnippet(oneToOne, false));
            variableDef.setPrimaryKeyJoinColumns(processPrimaryKeyJoinColumns(oneToOne));
        }
        return variableDef;
    }

    protected VariableDefSnippet processManyToOne(ManyToOne manyToOne) {
        VariableDefSnippet variableDef = getVariableDef(manyToOne);
        variableDef.setType(rootPackageName, manyToOne.getConnectedEntity());

        if (!classDef.isNoSQL()) {
            List<String> cascadeTypes = getCascadeTypes(manyToOne.getCascade());

            JoinTableSnippet joinTable = getJoinTable(manyToOne.getJoinTable());

            ManyToOneSnippet manyToOneSnippet = new ManyToOneSnippet();

            manyToOneSnippet.setTargetEntity(manyToOne.getTargetEntity());
            manyToOneSnippet.setTargetEntityPackage(manyToOne.getConnectedEntity().getAbsolutePackage(rootPackageName));
            manyToOneSnippet.setTargetField(manyToOne.getConnectedAttributeName());
            manyToOneSnippet.setCascadeTypes(cascadeTypes);

            if (manyToOne.getOptional() != null) {
                manyToOneSnippet.setOptional(manyToOne.getOptional());
            }

            if (manyToOne.getFetch() != null) {
                manyToOneSnippet.setFetchType(manyToOne.getFetch().value());
            } else if (ClassDiagramSettings.isLazyDefaultTypeForSingleAssociation()) {
                manyToOneSnippet.setFetchType(FetchType.LAZY.value());
            }

            manyToOneSnippet.setPrimaryKey(manyToOne.isPrimaryKey());
            manyToOneSnippet.setMapsId(manyToOne.getMapsId());

            variableDef.setRelation(manyToOneSnippet);
            variableDef.setJoinTable(joinTable);
            variableDef.setJoinColumns(getJoinColumnsSnippet(manyToOne, false));
            variableDef.setPrimaryKeyJoinColumns(processPrimaryKeyJoinColumns(manyToOne));
        }
        return variableDef;
    }

    private PrimaryKeyJoinColumnsSnippet processPrimaryKeyJoinColumns(SingleRelationAttribute singleRelationAttribute) {
        PrimaryKeyJoinColumnsSnippet primaryKeyJoinColumnsSnippet = null;
        List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns = getPrimaryKeyJoinColumns(singleRelationAttribute.getPrimaryKeyJoinColumn());
        ForeignKeySnippet primaryKeyForeignKey = processForeignKey(singleRelationAttribute.getPrimaryKeyForeignKey());
        if (primaryKeyJoinColumns != null && !primaryKeyJoinColumns.isEmpty()) {
            primaryKeyJoinColumnsSnippet = new PrimaryKeyJoinColumnsSnippet(repeatable);
            primaryKeyJoinColumnsSnippet.setPrimaryKeyJoinColumns(primaryKeyJoinColumns);
            primaryKeyJoinColumnsSnippet.setForeignKey(primaryKeyForeignKey);
        }
        return primaryKeyJoinColumnsSnippet;
    }

    protected VariableDefSnippet processOneToMany(OneToMany oneToMany) {
        VariableDefSnippet variableDef = getVariableDef(oneToMany);
        variableDef.setType(rootPackageName, oneToMany.getConnectedEntity());
        variableDef.setCollectionType(oneToMany.getCollectionType());
        variableDef.setCollectionImplType(oneToMany.getCollectionImplType());

        if (!classDef.isNoSQL()) {
            List<String> cascadeTypes = getCascadeTypes(oneToMany.getCascade());

            JoinTableSnippet joinTable = getJoinTable(oneToMany.getJoinTable());

            OneToManySnippet oneToManySnippet = new OneToManySnippet();

            oneToManySnippet.setCascadeTypes(cascadeTypes);
            oneToManySnippet.setTargetEntity(oneToMany.getTargetEntity());
            oneToManySnippet.setTargetEntityPackage(oneToMany.getConnectedEntity().getAbsolutePackage(rootPackageName));
            oneToManySnippet.setTargetField(oneToMany.getConnectedAttributeName());
            oneToManySnippet.setMappedBy(oneToMany.getMappedBy());
            oneToManySnippet.setMapKeySnippet(updateMapKeyAttributeSnippet(oneToMany));
            if (oneToMany.getFetch() != null) {
                oneToManySnippet.setFetchType(oneToMany.getFetch().value());
            }
            oneToManySnippet.setOrphanRemoval(oneToMany.getOrphanRemoval());

            variableDef.setRelation(oneToManySnippet);
            variableDef.setJoinTable(joinTable);
            variableDef.setJoinColumns(getJoinColumnsSnippet(oneToMany, false));
            if (oneToMany.getOrderBy() != null) {
                variableDef.setOrderBy(new OrderBySnippet(oneToMany.getOrderBy()));
            } else if (oneToMany.getOrderColumn() != null) {
                variableDef.setOrderColumn(new OrderColumnSnippet(oneToMany.getOrderColumn()));
            }

            if (oneToMany.getMapKey() != null) {
                variableDef.setMapKey(oneToMany.getMapKey().getName());
            }
            variableDef.setConverts(processConverts(oneToMany.getMapKeyConverts()));
        }
        return variableDef;
    }

    protected VariableDefSnippet processManyToMany(ManyToMany manyToMany) {
        VariableDefSnippet variableDef = getVariableDef(manyToMany);
        variableDef.setType(rootPackageName, manyToMany.getConnectedEntity());
        variableDef.setCollectionType(manyToMany.getCollectionType());
        variableDef.setCollectionImplType(manyToMany.getCollectionImplType());

        if (!classDef.isNoSQL()) {
            List<String> cascadeTypes = getCascadeTypes(manyToMany.getCascade());

            JoinTableSnippet joinTable = getJoinTable(manyToMany.getJoinTable());

            ManyToManySnippet manyToManySnippet = new ManyToManySnippet();
            manyToManySnippet.setMapKeySnippet(updateMapKeyAttributeSnippet(manyToMany));
            manyToManySnippet.setMappedBy(manyToMany.getMappedBy());
            manyToManySnippet.setTargetEntity(manyToMany.getTargetEntity());
            manyToManySnippet.setTargetEntityPackage(manyToMany.getConnectedEntity().getAbsolutePackage(rootPackageName));
            manyToManySnippet.setTargetField(manyToMany.getConnectedAttributeName());
            manyToManySnippet.setCascadeTypes(cascadeTypes);

            if (manyToMany.getFetch() != null) {
                manyToManySnippet.setFetchType(manyToMany.getFetch().value());
            }
            //TODO: Checked this error - The ORM.xsd has this but NOT the
            //http://www.oracle.com/technology/products/ias/toplink/jpa/resources/toplink-jpa-annotations.html
//            manyToManySnippet.setOrderBy(manyToMany.getOrderBy());

            variableDef.setRelation(manyToManySnippet);
            variableDef.setJoinTable(joinTable);
            if (manyToMany.getOrderBy() != null) {
                variableDef.setOrderBy(new OrderBySnippet(manyToMany.getOrderBy()));
            } else if (manyToMany.getOrderColumn() != null) {
                variableDef.setOrderColumn(new OrderColumnSnippet(manyToMany.getOrderColumn()));
            }
            if (manyToMany.getMapKey() != null) {
                variableDef.setMapKey(manyToMany.getMapKey().getName());
            }
            variableDef.setConverts(processConverts(manyToMany.getMapKeyConverts()));
        }
        return variableDef;
    }

    protected ConvertsSnippet processConverts(List<Convert> converts) {

        if (converts == null || converts.isEmpty()) {
            return null;
        }

        ConvertsSnippet convertsSnippet = new ConvertsSnippet(repeatable);

        for (Convert convert : converts) {
            if (convert != null && ConvertValidator.isNotEmpty(convert)) {
                ConvertSnippet convertSnippet = new ConvertSnippet(convert);
                convertsSnippet.add(convertSnippet);
            }

        }
        if (!convertsSnippet.get().isEmpty()) {
            return convertsSnippet;
        }

        return null;
    }

    protected ColumnSnippet processColumn(Column column) {
        return processColumn(column, false);
    }

    protected ColumnSnippet processColumn(Column column, boolean mapKey) {

        if (column == null) {
            return null;
        }

        ColumnSnippet columnSnippet = new ColumnSnippet(mapKey);

        columnSnippet.setNoSQL(classDef.isNoSQL());
        columnSnippet.setColumnDefinition(column.getColumnDefinition());
        columnSnippet.setName(column.getName());
        columnSnippet.setTable(column.getTable());
        columnSnippet.setInsertable(column.getInsertable());
        columnSnippet.setNullable(column.getNullable());
        columnSnippet.setUnique(column.getUnique());
        columnSnippet.setUpdatable(column.getUpdatable());

        if (column.getLength() != null) {
            columnSnippet.setLength(column.getLength());
        }

        if (column.getPrecision() != null) {
            columnSnippet.setPrecision(column.getPrecision());
        }

        if (column.getScale() != null) {
            columnSnippet.setScale(column.getScale());
        }
        if (columnSnippet.isEmptyObject()) {
            columnSnippet = null;
        }

        return columnSnippet;
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
            ColumnSnippet columnDef = ManagedClassGenerator.this.processColumn(attributeOverride.getColumn());
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

            List<JoinColumnSnippet> joinColumnsList = processJoinColumns(associationOverride.getJoinColumn(), false);
            JoinTableSnippet joinTable = getJoinTable(associationOverride.getJoinTable());

            if (joinTable.isEmpty() && joinColumnsList.isEmpty()) {
                continue;
            }
            AssociationOverrideSnippet associationOverrideSnippet = new AssociationOverrideSnippet();
            associationOverrideSnippet.setName(associationOverride.getName());
            associationOverrideSnippet.setJoinColumns(joinColumnsList);
            associationOverrideSnippet.setJoinTable(joinTable);

            associationOverrideSnippet.setForeignKey(processForeignKey(associationOverride.getForeignKey()));

            assoHandler.getAssociationOverrides().add(associationOverrideSnippet);
        }
        if (assoHandler.getAssociationOverrides() != null && assoHandler.getAssociationOverrides().get().isEmpty()) {
            assoHandler.setAssociationOverrides(null);
        }
    }

    protected List<JoinColumnSnippet> processJoinColumns(List<? extends JoinColumn> joinColumns, boolean mapKey) {

        return joinColumns.stream()
                .filter(JoinColumnValidator::isNotEmpty)
                .map(joinColumn -> {

                    JoinColumnSnippet joinColumnSnippet = new JoinColumnSnippet(mapKey);
                    joinColumnSnippet.setName(joinColumn.getName());
                    joinColumnSnippet.setReferencedColumnName(joinColumn.getReferencedColumnName());
                    joinColumnSnippet.setTable(joinColumn.getTable());
                    joinColumnSnippet.setForeignKey(processForeignKey(joinColumn.getForeignKey()));
                    joinColumnSnippet.setColumnDefinition(joinColumn.getColumnDefinition());

                    if (joinColumn.getInsertable() != null) {
                        joinColumnSnippet.setInsertable(joinColumn.getInsertable());
                    }

                    if (joinColumn.getUnique() != null) {
                        joinColumnSnippet.setUnique(joinColumn.getUnique());
                    }

                    if (joinColumn.getNullable() != null) {
                        joinColumnSnippet.setNullable(joinColumn.getNullable());
                    }

                    if (joinColumn.getUpdatable() != null) {
                        joinColumnSnippet.setUpdatable(joinColumn.getUpdatable());
                    }
                    return joinColumnSnippet;
                })
                .collect(toList());

    }

    protected JoinTableSnippet getJoinTable(JoinTable joinTable) {
        if (joinTable == null || JoinTableValidator.isEmpty(joinTable)) {
            return null;
        }

        List<JoinColumnSnippet> inverseJoinColumnSnippets 
                = processJoinColumns(joinTable.getInverseJoinColumn(), false);

        List<JoinColumnSnippet> joinColumnSnippets 
                = processJoinColumns(joinTable.getJoinColumn(), false);

        List<UniqueConstraintSnippet> uniqueConstraintSnippets 
                = processUniqueConstraints(joinTable.getUniqueConstraint());

        JoinTableSnippet joinTableSnippet = new JoinTableSnippet();

        joinTableSnippet.setCatalog(joinTable.getCatalog());
        joinTableSnippet.setName(joinTable.getName());
        joinTableSnippet.setSchema(joinTable.getSchema());
        joinTableSnippet.setJoinColumns(joinColumnSnippets);
        joinTableSnippet.setInverseJoinColumns(inverseJoinColumnSnippets);
        joinTableSnippet.setUniqueConstraints(uniqueConstraintSnippets);
        joinTableSnippet.setIndices(processIndexes(joinTable.getIndex()));

        joinTableSnippet.setForeignKey(processForeignKey(joinTable.getForeignKey()));
        joinTableSnippet.setInverseForeignKey(processForeignKey(joinTable.getInverseForeignKey()));

        return joinTableSnippet;
    }

    protected ForeignKeySnippet processForeignKey(ForeignKey foreignKey) {
        if (foreignKey == null || ForeignKeyValidator.isEmpty(foreignKey)) {
            return null;
        }

        ForeignKeySnippet foreignKeySnippet = new ForeignKeySnippet();

        foreignKeySnippet.setName(foreignKey.getName());
        foreignKeySnippet.setDescription(foreignKey.getDescription());
        foreignKeySnippet.setForeignKeyDefinition(foreignKey.getForeignKeyDefinition());
        if (foreignKey.getConstraintMode() != null) {
            foreignKeySnippet.setConstraintMode(foreignKey.getConstraintMode().name());
        }

        return foreignKeySnippet;
    }

    protected List<IndexSnippet> processIndexes(List<Index> indexs) {
        if (indexs == null || indexs.isEmpty()) {
            return Collections.<IndexSnippet>emptyList();
        }
        return indexs.stream()
                .filter(index -> !index.getColumnList().isEmpty())
                .map(index -> new IndexSnippet(index))
                .collect(toList());
    }

    protected List<UniqueConstraintSnippet> processUniqueConstraints(Set<UniqueConstraint> uniqueConstraints) {
        if (uniqueConstraints == null || uniqueConstraints.isEmpty()) {
            return Collections.<UniqueConstraintSnippet>emptyList();
        }
        return uniqueConstraints.stream()
                .map(c -> new UniqueConstraintSnippet(c))
                .collect(toList());
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
            List<JoinColumnSnippet> joinColumnsList = processJoinColumns(mapKeyHandler.getMapKeyJoinColumn(), true);
            JoinColumnsSnippet joinColumns = null;
            if (!joinColumnsList.isEmpty()) {
                joinColumns = new JoinColumnsSnippet(repeatable, true);
                joinColumns.setJoinColumns(joinColumnsList);
                joinColumns.setForeignKey(processForeignKey(mapKeyHandler.getMapKeyForeignKey()));
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
            snippet.setColumnSnippet(processColumn(mapKeyHandler.getMapKeyColumn(), true));
            snippet.setMapKeyAttributeType(mapKeyHandler.getMapKeyAttributeType());
        }
        return snippet;
    }

    private JoinColumnsSnippet getJoinColumnsSnippet(JoinColumnHandler joinColumnHandler, boolean mapKey) {
        List<JoinColumnSnippet> joinColumnsList = processJoinColumns(joinColumnHandler.getJoinColumn(), mapKey);
        JoinColumnsSnippet joinColumns = null;
        if (!joinColumnsList.isEmpty()) {
            joinColumns = new JoinColumnsSnippet(repeatable, mapKey);
            joinColumns.setJoinColumns(joinColumnsList);
            joinColumns.setForeignKey(processForeignKey(joinColumnHandler.getForeignKey()));
        }
        return joinColumns;
    }

    protected CollectionTableSnippet processCollectionTable(CollectionTable collectionTable) {
        if (collectionTable == null || CollectionTableValidator.isEmpty(collectionTable)) {
            return null;
        }

        List<JoinColumnSnippet> joinColumns 
                = processJoinColumns(collectionTable.getJoinColumn(), false);

        List<UniqueConstraintSnippet> uniqueConstraintSnippets 
                = processUniqueConstraints(collectionTable.getUniqueConstraint());

        CollectionTableSnippet collectionTableSnippet = new CollectionTableSnippet();
        collectionTableSnippet.setName(collectionTable.getName());
        collectionTableSnippet.setCatalog(collectionTable.getCatalog());
        collectionTableSnippet.setSchema(collectionTable.getSchema());
        collectionTableSnippet.setJoinColumns(joinColumns);
        collectionTableSnippet.setUniqueConstraints(uniqueConstraintSnippets);
        collectionTableSnippet.setIndices(processIndexes(collectionTable.getIndex()));
        collectionTableSnippet.setForeignKey(processForeignKey(collectionTable.getForeignKey()));

        return collectionTableSnippet;
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

            ColumnSnippet columnSnippet = ManagedClassGenerator.this.processColumn(attributeOverride.getColumn());
            if (columnSnippet == null) {
                continue;
            }
            AttributeOverrideSnippet attributeOverrideSnippet = new AttributeOverrideSnippet();

            attributeOverrideSnippet.setColumnDef(columnSnippet);
            attributeOverrideSnippet.setName(attributeOverride.getName());

            attributeOverridesSnippet.add(attributeOverrideSnippet);
        }

        if (attributeOverridesSnippet.get().isEmpty()) {
            return null;
        }

        return attributeOverridesSnippet;
    }

}
