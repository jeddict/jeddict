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

import io.github.jeddict.jpa.spec.DiscriminatorColumn;
import io.github.jeddict.jpa.spec.DiscriminatorType;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.Inheritance;
import io.github.jeddict.jpa.spec.InheritanceType;
import static io.github.jeddict.jpa.spec.InheritanceType.JOINED;
import static io.github.jeddict.jpa.spec.InheritanceType.TABLE_PER_CLASS;
import io.github.jeddict.jpa.spec.extend.IPrimaryKeyAttributes;
import io.github.jeddict.orm.generator.compiler.DiscriminatorColumnSnippet;
import io.github.jeddict.orm.generator.compiler.DiscriminatorValueSnippet;
import io.github.jeddict.orm.generator.compiler.InheritanceSnippet;
import io.github.jeddict.orm.generator.compiler.InheritanceSnippet.Type;
import io.github.jeddict.orm.generator.compiler.def.EntityDefSnippet;
import org.apache.commons.lang.StringUtils;

public class EntityGenerator extends IdentifiableClassGenerator<EntityDefSnippet> {

    private final Entity entity;

    public EntityGenerator(Entity parsedEntity, String packageName) {
        super(new EntityDefSnippet());
        this.entity = parsedEntity;
        this.rootPackageName = packageName;
        this.packageName = entity.getAbsolutePackage(rootPackageName);
    }

    @Override
    public EntityDefSnippet getClassDef() {

        //Classlevel annotations
        processDiscriminatorColumn();
        processDiscriminatorValue();
        processInheritance();

        processIdClass(entity.getIdClass());
        processAssociationOverrides(entity.getAssociationOverride());
        classDef.setAttributeOverrides(processAttributeOverrides(entity.getAttributeOverride()));

        processSecondaryTable(entity.getSecondaryTable());
        processPrimaryKeyJoinColumns(getPrimaryKeyJoinColumns(entity.getPrimaryKeyJoinColumn()), getForeignKey(entity.getPrimaryKeyForeignKey()));
        
        processSqlResultSetMapping(entity.getSqlResultSetMapping());
        processEntityListeners(entity.getEntityListeners());

        processDefaultExcludeListeners(
                entity.getExcludeDefaultListeners());
        processExcludeSuperclassListeners(
                entity.getExcludeSuperclassListeners());
        
        classDef.setConverts(processConverts(entity.getConverts()));
        
        //Table
        processTable(entity.getTable());
        
        processCacheable(entity.getCacheable());

        //Queries
        processNamedQueries(entity.getNamedQuery());
        processNamedNativeQueries(entity.getNamedNativeQuery());

        //EntityGraphs
        processNamedEntityGraphs(entity.getNamedEntityGraph());

        //StoredProcedures
        processNamedStoredProcedureQueries((EntityMappings) entity.getRootElement(), entity.getNamedStoredProcedureQuery());

        //Attributes -- Method level annotations
        IPrimaryKeyAttributes parsedAttributes = entity.getAttributes();

        if (parsedAttributes != null) {//#ATTRIBUTE_SEQUENCE_FLOW#
            processEmbeddedId(entity, parsedAttributes.getEmbeddedId());
            if (!entity.isEmbeddedIdType()) {
                processId(parsedAttributes.getId());
            }
            processBasic(parsedAttributes.getBasic());
            processElementCollection(parsedAttributes.getElementCollection());//todo embedded collection should be generate after embedded
            processEmbedded(parsedAttributes.getEmbedded());

            processOneToOne(parsedAttributes.getOneToOne());
            processManyToOne(parsedAttributes.getManyToOne());
            processOneToMany(parsedAttributes.getOneToMany());
            processManyToMany(parsedAttributes.getManyToMany());

            processVersion(parsedAttributes.getVersion());
            processTransient(parsedAttributes.getTransient());
        }

        /**
         * processTableGeneratorEntity() && processSequenceGeneratorEntity()
         * depends on @GeneratedValue annotation - So process it after
         * @GeneratedValue
         */
        processTableGeneratorEntity(entity.getTableGenerator());
        processSequenceGeneratorEntity(entity.getSequenceGenerator());

        //Class decorations
        classDef = initClassDef(packageName,entity);
        if (StringUtils.isNotBlank(entity.getDescription())) {
            classDef.setDescription(entity.getDescription());
        }
        if (StringUtils.isNotBlank(entity.getEntityName())) {
            classDef.setEntityName(entity.getEntityName()); 
        }
        classDef.setAuthor(entity.getAuthor());
        classDef.setXmlRootElement(entity.getXmlRootElement());
        
        return classDef;
    }

    private void processDiscriminatorColumn() {
        DiscriminatorColumn parsedDiscriminatorColumn = entity.getDiscriminatorColumn();

        if (parsedDiscriminatorColumn == null) {
            return;
        }

        DiscriminatorType parsedDiscriminatorType = parsedDiscriminatorColumn.getDiscriminatorType();

        DiscriminatorColumnSnippet discriminatorColumn = new DiscriminatorColumnSnippet();

        discriminatorColumn.setName(parsedDiscriminatorColumn.getName());
        discriminatorColumn.setColumnDefinition(parsedDiscriminatorColumn.getColumnDefinition());

        if (parsedDiscriminatorColumn.getLength() != null) {
            discriminatorColumn.setLength(parsedDiscriminatorColumn.getLength());
        }
        if (parsedDiscriminatorType != null) {
            DiscriminatorType discriminatorType = DiscriminatorType.valueOf(parsedDiscriminatorType.value());
            discriminatorColumn.setDiscriminatorType(discriminatorType);
        }
        if (!discriminatorColumn.isDefault()) {
            classDef.setDiscriminatorColumn(discriminatorColumn);
        }
    }

    private void processDiscriminatorValue() {
        String parsedDiscriminatorValue = entity.getDiscriminatorValue();

        if (parsedDiscriminatorValue == null) {
            return;
        }

        DiscriminatorValueSnippet discriminatorValue = new DiscriminatorValueSnippet();
        discriminatorValue.setValue(parsedDiscriminatorValue);

        if (!discriminatorValue.isDefault()) {
            classDef.setDiscriminatorValue(discriminatorValue);
        }
    }

    private void processInheritance() {
        Inheritance parsedInheritance = entity.getInheritance();

        if (parsedInheritance == null) {
            return;
        }

        InheritanceSnippet inheritance = new InheritanceSnippet();

        InheritanceType inheritanceType
                = parsedInheritance.getStrategy();

        if (inheritanceType != null) {

            switch (inheritanceType) {
                case JOINED:
                    inheritance.setStatergy(Type.JOINED);
                    break;
                case TABLE_PER_CLASS:
                    inheritance.setStatergy(Type.TABLE_PER_CLASS);
                    break;
                default:
                    inheritance.setStatergy(Type.SINGLE_TABLE);
            }
        }

        classDef.setInheritance(inheritance);
    }
}
