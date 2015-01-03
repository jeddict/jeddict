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
package org.netbeans.orm.converter.generator.staticmetamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.CompositionAttribute;
import org.netbeans.jpa.modeler.spec.extend.IAttributes;
import org.netbeans.jpa.modeler.spec.extend.IPersistenceAttributes;
import org.netbeans.jpa.modeler.spec.extend.PersistenceBaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.orm.converter.compiler.VariableDefSnippet;
import org.netbeans.orm.converter.generator.ClassGenerator;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConvLogger;

public class StaticMetamodelGenerator extends ClassGenerator<StaticMetamodelClassDefSnippet> {

    private static Logger logger = ORMConvLogger.getLogger(StaticMetamodelGenerator.class);

    private ManagedClass managedClass = null;



    public StaticMetamodelGenerator(ManagedClass managedClass, String packageName) {
        super(new StaticMetamodelClassDefSnippet());
        this.managedClass = managedClass;
        this.packageName = packageName;
    }

    
   protected MetamodelVariableDefSnippet getVariableDef(Attribute attr) {
        MetamodelVariableDefSnippet variableDef = (MetamodelVariableDefSnippet)variables.get(attr.getName());//GG_REMOVE hard cast
        if (variableDef == null) {
            variableDef = new MetamodelVariableDefSnippet();
            variableDef.setName(attr.getName());
            variableDef.setAnnotation(attr.getAnnotation());
            variables.put(attr.getName(), variableDef);
        }
        return variableDef;
    }
    
       private void processBase(BaseAttribute parsedBaseAttribute) {
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
       }
    private void processBase(List<? extends BaseAttribute> parsedBaseAttributes) {
        if (parsedBaseAttributes == null) {
            return;
        }
        for (BaseAttribute parsedBaseAttribute : parsedBaseAttributes) {
           processBase(parsedBaseAttribute);
        }
    }

    private void processRelation(List<? extends RelationAttribute> parsedRelations) {
        if (parsedRelations == null) {
            return;
        }
        for (RelationAttribute parsedRelation : parsedRelations) {
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
            
        }
    }
    
    
    @Override
    public StaticMetamodelClassDefSnippet getClassDef() {
      
        //Attributes -- Method level annotations
        IAttributes parsedAttributes = managedClass.getAttributes();
        if (parsedAttributes != null) {
            if (parsedAttributes instanceof IPersistenceAttributes) {
                IPersistenceAttributes persistenceAttributes = (IPersistenceAttributes) parsedAttributes;
                if (persistenceAttributes.getEmbeddedId() == null) {
                    processBase(persistenceAttributes.getId());
                } else {
                    processBase(persistenceAttributes.getEmbeddedId());
                }
                processBase(persistenceAttributes.getVersion());
            }
            
            processBase(parsedAttributes.getBasic());
//            processTransient(parsedAttributes.getTransient());
            processBase(parsedAttributes.getElementCollection());
            processBase(parsedAttributes.getEmbedded());
            processRelation(parsedAttributes.getManyToMany());
            processRelation(parsedAttributes.getManyToOne());
            processRelation(parsedAttributes.getOneToMany());
            processRelation(parsedAttributes.getOneToOne());
        }

        // Classlevel annotations 
       
        //Class decorations
        ClassHelper classHelper = new ClassHelper(managedClass.getClazz() + "_"); //For each managed class X in package p, a metamodel class X_ in package p is created.
        classHelper.setPackageName(packageName);
//The name of the metamodel class is derived from the name of the managed class by appending "_" to the name of the managed class.
       if(managedClass.getSuperclass()!=null){
        ClassHelper superClassHelper = new ClassHelper(managedClass.getSuperclass().getClazz() + "_");//If class X extends another class S, where S is the most derived managed class (i.e., entity or mapped superclass) extended by X, then class X_ must extend class S_, where S_ is the metamodel class created for S.
        superClassHelper.setPackageName(packageName);
        classDef.setSuperClassName(superClassHelper.getFQClassName());
       }
        

        classDef.setVariableDefs(new ArrayList<VariableDefSnippet>(variables.values()));
        classDef.setClassName(classHelper.getFQClassName());
       
        classDef.setPackageName(classHelper.getPackageName());
//        classDef.setStaticMetamodel(true);
        
        classDef.setValue(managedClass.getClazz());//@StaticMetamodel( Person.class )

        return classDef;
    }
    
//    public boolean isManagedSuperClassExist(){
//        return managedClass.getSuperclass()!=null;
//    }
//    public String getManagedSuperClass(){
//        return managedClass.getSuperclass().getClazz();
//    }
    public ManagedClass getManagedClass(){
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
    
    

//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final BaseElement other = (BaseElement) obj;
//        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
//            return false;
//        }
//        return true;
//    }
  
}
