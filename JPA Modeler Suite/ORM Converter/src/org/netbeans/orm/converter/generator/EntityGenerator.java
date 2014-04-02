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
import java.util.logging.Logger;
import org.netbeans.jpa.modeler.spec.Attributes;
import org.netbeans.jpa.modeler.spec.DiscriminatorColumn;
import org.netbeans.jpa.modeler.spec.DiscriminatorType;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.Inheritance;
import org.netbeans.jpa.modeler.spec.InheritanceType;
import static org.netbeans.jpa.modeler.spec.InheritanceType.JOINED;
import static org.netbeans.jpa.modeler.spec.InheritanceType.TABLE_PER_CLASS;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.compiler.DiscriminatorColumnSnippet;
import org.netbeans.orm.converter.compiler.DiscriminatorValueSnippet;
import org.netbeans.orm.converter.compiler.InheritanceSnippet;
import org.netbeans.orm.converter.compiler.InheritanceSnippet.Type;
import org.netbeans.orm.converter.compiler.VariableDefSnippet;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConvLogger;

public class EntityGenerator extends ClassGenerator {

    private static Logger logger = ORMConvLogger.getLogger(EntityGenerator.class);

    private Entity entity = null;

    public EntityGenerator(Entity parsedEntity, String packageName) {
        this.entity = parsedEntity;
        this.packageName = packageName;
    }

    public ClassDefSnippet getClassDef() {

        //--BEGIN ---- TODOs:
        /*
         * Commented -- revist for Phase 3
         ParsedAccessType accessType = parsedEntity.getAccess();

         */
        //----END TODO ---------
        //Classlevel annotations
        processDiscriminatorColumn();
        processDiscriminatorValue();
        processInheritance();

        processIdClass(entity.getIdClass());
        processAssociationOverrides(entity.getAssociationOverride());
        processAttributeOverrides(entity.getAttributeOverride());

        processSecondaryTable(entity.getSecondaryTable());
        processPrimaryKeyJoinColumns(entity.getPrimaryKeyJoinColumn());
        processSqlResultSetMapping(entity.getSqlResultSetMapping());
        processEntityListeners(entity.getEntityListeners());

        processDefaultExcludeListeners(
                entity.getExcludeDefaultListeners());
        processExcludeSuperclassListeners(
                entity.getExcludeSuperclassListeners());
        //Table
        processTable(entity.getTable());

        //Queries
        processNamedQueries(entity.getNamedQuery());
        processNamedNativeQueries(entity.getNamedNativeQuery());

        //Attributes -- Method level annotations
        Attributes parsedAttributes = entity.getAttributes();

        if (parsedAttributes != null) {
            processEmbeddedId(parsedAttributes.getEmbeddedId());
            if (parsedAttributes.getEmbeddedId() == null) {
                processId(parsedAttributes.getId());
            }
            processBasic(parsedAttributes.getBasic());
            processTransient(parsedAttributes.getTransient());
            processElementCollection(parsedAttributes.getElementCollection());

            processEmbedded(parsedAttributes.getEmbedded());

            processManyToMany(parsedAttributes.getManyToMany());
            processManyToOne(parsedAttributes.getManyToOne());
            processOneToMany(parsedAttributes.getOneToMany());
            processOneToOne(parsedAttributes.getOneToOne());
            processVersion(parsedAttributes.getVersion());
        }

        // Classlevel annotations - Special case
        // processTableGenerator() && processSequenceGenerator()
        //depends on @GeneratedValue annotation - So process it after @GeneratedValue
        processTableGenerator(entity.getTableGenerator());
        processSequenceGenerator(entity.getSequenceGenerator());

        //Class decorations
        ClassHelper classHelper = new ClassHelper(entity.getClazz());
        ClassHelper superClassHelper = new ClassHelper(entity.getSuperclass());

        classHelper.setPackageName(packageName);
        superClassHelper.setPackageName(packageName);

        classDef.setVariableDefs(new ArrayList<VariableDefSnippet>(variables.values()));
        classDef.setClassName(classHelper.getFQClassName());
        classDef.setSuperClassName(superClassHelper.getFQClassName());
        classDef.setPackageName(classHelper.getPackageName());

        if (entity.getTable() != null) {
            classDef.setEntityName(entity.getName()); //modified by gaurav gupta //.getTable().getName()
        }
        classDef.setEntity(true);

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
