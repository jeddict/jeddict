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
package io.github.jeddict.db.accessor;

import static java.lang.Boolean.TRUE;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.SecondaryTableMetadata;
import io.github.jeddict.db.modeler.exception.DBValidationException;
import io.github.jeddict.jpa.spec.Convert;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.Inheritance;
import io.github.jeddict.jpa.spec.MappedSuperclass;
import io.github.jeddict.jpa.spec.PrimaryKeyJoinColumn;
import io.github.jeddict.jpa.spec.SecondaryTable;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.spec.validator.column.PrimaryKeyJoinColumnValidator;
import io.github.jeddict.jpa.spec.validator.override.AssociationValidator;
import io.github.jeddict.jpa.spec.validator.override.AttributeValidator;
import io.github.jeddict.jpa.spec.workspace.WorkSpace;

/**
 *
 * @author Gaurav Gupta
 */
public class EntitySpecAccessor extends EntityAccessor {

    private final Entity entity;

    private EntitySpecAccessor(Entity entity) {
        this.entity = entity;
    }

    public static EntitySpecAccessor getInstance(WorkSpace workSpace, Entity entity) {
        EntitySpecAccessor accessor = new EntitySpecAccessor(entity);
        accessor.setName(entity.getName());
        accessor.setClassName(entity.getClazz());
        accessor.setAccess("VIRTUAL");
        if(TRUE.equals(entity.getAbstract())){//set abstract
            MetadataClass metadataClass = new MetadataClass(null, entity.getClazz());//accessor.getMetadataFactory()
            metadataClass.setModifiers(1024);
            accessor.setAccessibleObject(metadataClass);//Test : Modifier.isAbstract(accessor.getJavaClass().getModifiers());
        }
        
        accessor.setAttributes(entity.getAttributes().getAccessor(workSpace));
        if (entity.getTable() != null) {
            accessor.setTable(entity.getTable().getAccessor());
        }
        if(!entity.getSecondaryTable().isEmpty()){
            List<SecondaryTableMetadata> secondaryTableMetadata = new ArrayList<>();
            for(SecondaryTable secondaryTable : entity.getSecondaryTable()){
               secondaryTableMetadata.add(secondaryTable.getAccessor());
            }
            accessor.setSecondaryTables(secondaryTableMetadata);
        }
        processSuperClass(workSpace, entity, accessor);
        if (entity.getInheritance() != null) {
            accessor.setInheritance(entity.getInheritance().getAccessor());
        } else if(!entity.getSubclassList().isEmpty()) { //if Inheritance null and ROOT/BRANCH then set default
            accessor.setInheritance(Inheritance.getDefaultAccessor());
        }
        
        if (entity.getIdClass() != null) {
            accessor.setIdClassName(entity.getIdClass().getClazz());
        }
        
        if (entity.getDiscriminatorColumn() != null) {
            accessor.setDiscriminatorColumn(entity.getDiscriminatorColumn().getAccessor());
        }
        accessor.setDiscriminatorValue(entity.getDiscriminatorValue());

        AttributeValidator.filter(entity);
        accessor.setAttributeOverrides(entity.getAttributeOverride()
                .stream()
                .map(AttributeOverrideSpecMetadata::getInstance)
                .collect(toList()));
        AssociationValidator.filter(entity);
        accessor.setAssociationOverrides(entity.getAssociationOverride()
                .stream()
                .map(AssociationOverrideSpecMetadata::getInstance)
                .collect(toList()));

        PrimaryKeyJoinColumnValidator.filter(entity.getPrimaryKeyJoinColumn());
        accessor.setPrimaryKeyJoinColumns(entity.getPrimaryKeyJoinColumn()
                .stream()
                .map(PrimaryKeyJoinColumn::getAccessor)
                .collect(toList()));
        
        accessor.setConverts(entity.getConverts()
                .stream()
                .map(Convert::getAccessor)
                .collect(toList()));
        
        if (entity.getSequenceGenerator()!= null) {
            accessor.setSequenceGenerator(entity.getSequenceGenerator().getAccessor());
        }  
        if (entity.getTableGenerator() != null) {
            accessor.setTableGenerator(entity.getTableGenerator().getAccessor());
        }  
        
        return accessor;

    }

    private static void processSuperClass(WorkSpace workSpace, JavaClass _class, EntityAccessor accessor) {
        if (_class.getSuperclass() != null) {
            if (_class.getSuperclass() instanceof MappedSuperclass) {
                MappedSuperclass superclass = (MappedSuperclass) _class.getSuperclass();
                superclass.getAttributes().updateAccessor(workSpace, accessor.getAttributes(), true);
                processSuperClass(workSpace, superclass, accessor);
            } else {
                accessor.setParentClassName(_class.getSuperclass().getClazz());
            }
        }
    }

    /**
     * @return the entity
     */
    public Entity getEntity() {
        return entity;
    }
    
    @Override
     protected void addMultipleTableKeyFields(List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns, DatabaseTable targetTable, String PK_CTX, String FK_CTX) {
        try {
            super.addMultipleTableKeyFields(primaryKeyJoinColumns, targetTable, PK_CTX, FK_CTX);
        } catch (ValidationException ex) {// to handle @PrimaryKeyJoinColumn exception
            DBValidationException exception = new DBValidationException(ex);
            exception.setJavaClass(entity);
            throw exception;
        }
    }
     
    @Override
    public void process() {
        try {
            super.process();
        } catch (ValidationException ex) {
            DBValidationException exception = new DBValidationException(ex);
            exception.setJavaClass(entity);
            throw exception;
        }
    }
    
    @Override
    protected void processVirtualClass() {
      try {
            super.processVirtualClass();
        } catch (ValidationException ex) {
            DBValidationException exception = new DBValidationException(ex);
            exception.setJavaClass(entity);
            throw exception;
        }
    }
}
