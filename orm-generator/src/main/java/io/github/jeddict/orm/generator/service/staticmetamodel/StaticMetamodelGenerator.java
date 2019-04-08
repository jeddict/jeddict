/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.orm.generator.service.staticmetamodel;

import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.ManyToMany;
import io.github.jeddict.jpa.spec.OneToMany;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.BaseAttribute;
import io.github.jeddict.jpa.spec.extend.CompositionAttribute;
import io.github.jeddict.jpa.spec.extend.IAttributes;
import io.github.jeddict.jpa.spec.extend.IPersistenceAttributes;
import io.github.jeddict.jpa.spec.extend.IPrimaryKeyAttributes;
import io.github.jeddict.jpa.spec.extend.PersistenceBaseAttribute;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import io.github.jeddict.orm.generator.service.ClassGenerator;
import io.github.jeddict.orm.generator.util.ClassHelper;
import java.util.ArrayList;

public class StaticMetamodelGenerator extends ClassGenerator<StaticMetamodelClassDefSnippet> {

    private final ManagedClass managedClass;
    private final String entityPackageName;

    public StaticMetamodelGenerator(ManagedClass managedClass, String entityPackageName, String packageName) {
        super(new StaticMetamodelClassDefSnippet());
        this.managedClass = managedClass;
        this.rootPackageName = packageName;
        this.packageName = managedClass.getAbsolutePackage(rootPackageName);
        this.entityPackageName = entityPackageName;
    }

    @Override
    protected MetamodelVariableDefSnippet getVariableDef(Attribute attr) {
        MetamodelVariableDefSnippet variableDef = (MetamodelVariableDefSnippet) variables.get(attr.getName());//GG_REMOVE hard cast
        if (variableDef == null) {
            variableDef = new MetamodelVariableDefSnippet(attr);
            variableDef.setName(attr.getName());
//            variableDef.setAnnotation(attr.getAnnotation());
            variables.put(attr.getName(), variableDef);
        }
        return variableDef;
    }

    @Override
    protected MetamodelVariableDefSnippet processVariable(Attribute attr) {
        if (attr instanceof BaseAttribute) {
            return processBase((BaseAttribute) attr);
        } else if (attr instanceof RelationAttribute) {
            return processRelation((RelationAttribute) attr);
        } else {
            throw new IllegalStateException("Invalid Attribute Type");
        }
    }

    private MetamodelVariableDefSnippet processBase(BaseAttribute parsedBaseAttribute) {
        MetamodelVariableDefSnippet variableDef = getVariableDef(parsedBaseAttribute);
        if (parsedBaseAttribute instanceof CompositionAttribute) {
            if (parsedBaseAttribute instanceof ElementCollection) {
                ElementCollection elementCollection = (ElementCollection) parsedBaseAttribute;
                variableDef.setType(parsedBaseAttribute.getAttributeType());
                variableDef.setAttributeType(MetamodelAttributeType.getInstance(elementCollection.getCollectionType()));
            } else {
                variableDef.setType(parsedBaseAttribute.getAttributeType());
                variableDef.setAttributeType(MetamodelAttributeType.SINGULAR);
            }
        } else if (parsedBaseAttribute instanceof PersistenceBaseAttribute) {
            variableDef.setType(parsedBaseAttribute.getAttributeType());
            variableDef.setAttributeType(MetamodelAttributeType.SINGULAR);
        }
        return variableDef;
    }

    private MetamodelVariableDefSnippet processRelation(RelationAttribute parsedRelation) {
        MetamodelVariableDefSnippet variableDef = getVariableDef(parsedRelation);
        variableDef.setType(parsedRelation.getTargetEntity());
        if (parsedRelation instanceof ManyToMany) {
            ManyToMany manyToMany = (ManyToMany) parsedRelation;
            variableDef.setAttributeType(MetamodelAttributeType.getInstance(manyToMany.getCollectionType()));
        } else if (parsedRelation instanceof OneToMany) {
            OneToMany oneToMany = (OneToMany) parsedRelation;
            variableDef.setAttributeType(MetamodelAttributeType.getInstance(oneToMany.getCollectionType()));
        } else {
            variableDef.setAttributeType(MetamodelAttributeType.SINGULAR);
        }
        return variableDef;
    }

    @Override
    public StaticMetamodelClassDefSnippet getClassDef() {//#ATTRIBUTE_SEQUENCE_FLOW#

        //Attributes -- Method level annotations
        IAttributes parsedAttributes = managedClass.getAttributes();
        if (parsedAttributes != null) {
            if (parsedAttributes instanceof IPersistenceAttributes) {
                IPersistenceAttributes persistenceAttributes = (IPersistenceAttributes) parsedAttributes;
                if (parsedAttributes instanceof IPrimaryKeyAttributes) {
                    IPrimaryKeyAttributes primaryKeyAttributes = (IPrimaryKeyAttributes) parsedAttributes;
                    if (primaryKeyAttributes.getEmbeddedId() == null) {
                        primaryKeyAttributes.getId().forEach(this::processBase);
                    } else {
                        processBase(primaryKeyAttributes.getEmbeddedId());
                    }
                    primaryKeyAttributes.getVersion().forEach(this::processBase);
                }

                persistenceAttributes.getBasic().forEach(this::processBase);
                persistenceAttributes.getElementCollection().forEach(this::processBase);
                persistenceAttributes.getEmbedded().forEach(this::processBase);
                persistenceAttributes.getOneToOne().forEach(this::processRelation);
                persistenceAttributes.getManyToOne().forEach(this::processRelation);
                persistenceAttributes.getOneToMany().forEach(this::processRelation);
                persistenceAttributes.getManyToMany().forEach(this::processRelation);
            }
        }

        // Classlevel annotations
        //Class decorations
        ClassHelper classHelper = new ClassHelper(managedClass.getClazz() + "_"); //For each managed class X in package p, a metamodel class X_ in package p is created.
        classHelper.setPackageName(packageName);
        //The name of the metamodel class is derived from the name of the managed class by appending "_" to the name of the managed class.
        if (managedClass.getSuperclass() != null) {
            ClassHelper superClassHelper = new ClassHelper(managedClass.getSuperclass().getClazz() + "_");//If class X extends another class S, where S is the most derived managed class (i.e., entity or mapped superclass) extended by X, then class X_ must extend class S_, where S_ is the metamodel class created for S.
            superClassHelper.setPackageName(packageName);
            classDef.setSuperClassName(superClassHelper.getFQClassName());
        }

        classDef.setVariableDefs(new ArrayList<>(variables.values()));
        classDef.setClassName(classHelper.getFQClassName());

        classDef.setPackageName(classHelper.getPackageName());

        classDef.getEntityClassHelper().setClassName(managedClass.getClazz());
        classDef.getEntityClassHelper().setPackageName(entityPackageName);
//        classDef.setStaticMetamodel(true);

        classDef.setValue(managedClass.getClazz());//@StaticMetamodel( Person.class )

        return classDef;
    }

    public ManagedClass getManagedClass() {
        return managedClass;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.managedClass.getId() != null ? this.managedClass.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StaticMetamodelGenerator other = (StaticMetamodelGenerator) obj;
        if (this.managedClass.getId() != other.managedClass.getId() && (this.managedClass.getId() == null || !this.managedClass.getId().equals(other.managedClass.getId()))) {
            return false;
        }
        return true;
    }

}
