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
package io.github.jeddict.jpa.spec.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.lang.model.element.TypeElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.StringUtils;
import io.github.jeddict.jcode.util.AttributeType;
import static io.github.jeddict.jcode.util.AttributeType.Type.OTHER;
import static io.github.jeddict.jcode.util.AttributeType.getArrayType;
import static io.github.jeddict.jcode.util.AttributeType.getType;
import static io.github.jeddict.jcode.util.AttributeType.isArray;
import io.github.jeddict.jcode.util.JavaIdentifiers;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.Transient;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.Attributes;
import io.github.jeddict.jpa.spec.extend.BaseAttribute;
import io.github.jeddict.jpa.spec.extend.JavaClass;

@XmlAccessorType(XmlAccessType.FIELD)
public class BeanAttributes extends Attributes<BeanClass> {

    @XmlElement(name = "basic")
    private List<BeanAttribute> basic;
    @XmlElement(name = "element-collection")
    private List<BeanCollectionAttribute> elementCollection;
    @XmlElement(name = "transient")
    private List<Transient> _transient;
    
    @XmlElement(name = "many-to-one")
    private List<ManyToOneAssociation> manyToOne;
    @XmlElement(name = "one-to-many")
    private List<OneToManyAssociation> oneToMany;
    @XmlElement(name = "one-to-one")
    private List<OneToOneAssociation> oneToOne;
    @XmlElement(name = "many-to-many")
    private List<ManyToManyAssociation> manyToMany;

    @Override
    public List<Attribute> getAllAttribute(boolean includeParentClassAttibute) {
        List<Attribute> attributes = super.getAllAttribute(includeParentClassAttibute);
        attributes.addAll(this.getBasic());
        attributes.addAll(this.getElementCollection());
        attributes.addAll(this.getAssociationAttributes());
        attributes.addAll(this.getTransient());
        return attributes;
    }
    
    public List<BeanAttribute> getBasic() {
        if (this.basic == null) {
            this.basic = new ArrayList<>();
        }
        return basic;
    }

    public void setBasic(List<BeanAttribute> attributes) {
        this.basic = attributes;
    }

    public Optional<BeanAttribute> getBasic(String id) {
        if (basic != null) {
            return basic.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }
    
    public void addBasic(BeanAttribute attribute) {
        getBasic().add(attribute);
        attribute.setAttributes(this);
    }

    public void removeBasic(BeanAttribute attribute) {
        getBasic().remove(attribute);
        attribute.setAttributes(null);
    }

    public List<BeanCollectionAttribute> getElementCollection() {
        if (this.elementCollection == null) {
            this.elementCollection = new ArrayList<>();
        }
        return elementCollection;
    }

    public void setElementCollection(List<BeanCollectionAttribute> attributes) {
        this.elementCollection = attributes;
    }

    public Optional<BeanCollectionAttribute> getElementCollection(String id) {
        if (elementCollection != null) {
            return elementCollection.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }
    
    public void addElementCollection(BeanCollectionAttribute attribute) {
        getElementCollection().add(attribute);
        attribute.setAttributes(this);
    }

    public void removeElementCollection(BeanCollectionAttribute attribute) {
        getElementCollection().remove(attribute);
        attribute.setAttributes(null);
    }

    public List<Transient> getTransient() {
        if (this._transient == null) {
            this._transient = new ArrayList<>();
        }
        return _transient;
    }

    public void setTransient(List<Transient> attributes) {
        this._transient = attributes;
    }

    
    public Optional<Transient> getTransient(String id) {
        if (_transient != null) {
            return _transient.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }
    
    public void addTransient(Transient attribute) {
        getTransient().add(attribute);
        attribute.setAttributes(this);
    }

    public void removeTransient(Transient attribute) {
        getTransient().remove(attribute);
        attribute.setAttributes(null);
    }

    
    public List<ManyToOneAssociation> getManyToOne() {
        if (manyToOne == null) {
            this.manyToOne = new ArrayList<>();
        }
        return this.manyToOne;
    }

    public void setManyToOne(List<ManyToOneAssociation> manyToOne) {
        this.manyToOne = manyToOne;
    }

    public Optional<ManyToOneAssociation> getManyToOne(String id) {
        if (manyToOne != null) {
            return manyToOne.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }

    public void addManyToOne(ManyToOneAssociation manyToOne) {
        getManyToOne().add(manyToOne);
        manyToOne.setAttributes(this);
    }

    public void removeManyToOne(ManyToOneAssociation manyToOne) {
        getManyToOne().remove(manyToOne);
        manyToOne.setAttributes(null);
    }

    public List<OneToManyAssociation> getOneToMany() {
        if (oneToMany == null) {
            this.oneToMany = new ArrayList<>();
        }
        return this.oneToMany;
    }

    public void setOneToMany(List<OneToManyAssociation> oneToMany) {
        this.oneToMany = oneToMany;
    }

    public Optional<OneToManyAssociation> getOneToMany(String id) {
        if (oneToMany != null) {
            return oneToMany.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }

    public void addOneToMany(OneToManyAssociation oneToMany) {
        getOneToMany().add(oneToMany);
        oneToMany.setAttributes(this);
    }

    public void removeOneToMany(OneToManyAssociation oneToMany) {
        getOneToMany().remove(oneToMany);
        oneToMany.setAttributes(null);
    }

    public List<OneToOneAssociation> getOneToOne() {
        if (oneToOne == null) {
            this.oneToOne = new ArrayList<>();
        }
        return this.oneToOne;
    }

    public void setOneToOne(List<OneToOneAssociation> oneToOne) {
        this.oneToOne = oneToOne;
    }

    public Optional<OneToOneAssociation> getOneToOne(String id) {
        if (oneToOne != null) {
            return oneToOne.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }

    public void addOneToOne(OneToOneAssociation oneToOne) {
        getOneToOne().add(oneToOne);
        oneToOne.setAttributes(this);
    }

    public void removeOneToOne(OneToOneAssociation oneToOne) {
        getOneToOne().remove(oneToOne);
        oneToOne.setAttributes(null);
    }

    public List<ManyToManyAssociation> getManyToMany() {
        if (manyToMany == null) {
            this.manyToMany = new ArrayList<>();
        }
        return this.manyToMany;
    }

    public void setManyToMany(List<ManyToManyAssociation> manyToMany) {
        this.manyToMany = manyToMany;
    }

    public Optional<ManyToManyAssociation> getManyToMany(String id) {
        if (manyToMany != null) {
            return manyToMany.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }

    public void addManyToMany(ManyToManyAssociation manyToMany) {
        getManyToMany().add(manyToMany);
        manyToMany.setAttributes(this);
    }

    public void removeManyToMany(ManyToManyAssociation manyToMany) {
        getManyToMany().remove(manyToMany);
        manyToMany.setAttributes(null);
    }

    public List<AssociationAttribute> getAssociationAttributes() {
        List<AssociationAttribute> associationAttributes = new ArrayList<>(this.getOneToOne());
        associationAttributes.addAll(this.getManyToOne());
        associationAttributes.addAll(this.getOneToMany());
        associationAttributes.addAll(this.getManyToMany());
        return associationAttributes;
    }

    @Override
    public Set<String> getConnectedClass(Set<String> javaClasses) {
        javaClasses.add(getJavaClass().getFQN());
        if (getJavaClass().getSuperclass() != null && getJavaClass().getSuperclass() instanceof ManagedClass) {
            javaClasses.addAll(((ManagedClass) getJavaClass().getSuperclass()).getAttributes().getConnectedClass(javaClasses));
        }
        javaClasses.addAll(getBasicConnectedClass(javaClasses));
//        javaClasses.addAll(getAssociationConnectedClass(javaClasses));
//        javaClasses.addAll(getElementCollectionConnectedClass(javaClasses));
        return javaClasses;
    }

//    public Set<String> getAssociationConnectedClass(Set<String> javaClasses) {
//        Map<ManagedClass, String> releationClasses = getAssociationAttributes().stream()
//                .map(AssociationAttribute::getConnectedEntity)
//                .distinct()
//                .filter(jc -> !javaClasses.contains(jc.getFQN()))
//                .collect(toMap(identity(), JavaClass::getFQN));
//        javaClasses.addAll(releationClasses.values());
//        for (ManagedClass releationClass : releationClasses.keySet()) {
//            javaClasses.addAll(releationClass.getAttributes().getConnectedClass(javaClasses));
//        }
//        return javaClasses;
//    }

//    public Set<Entity> getAssociationConnectedClassRef() {
//        Set<Entity> javaClasses = getAssociationAttributes().stream()
//                .map(AssociationAttribute::getConnectedEntity)
//                .collect(toSet());
//        javaClasses.addAll(getEmbedded().stream()
//                .map(Embedded::getConnectedClass)
//                .flatMap(c -> c.getAttributes().getAssociationConnectedClassRef().stream())
//                .collect(toSet()));
//        return javaClasses;
//    }

//    public Set<String> getElementCollectionConnectedClass(Set<String> javaClasses) {
//        Map<ManagedClass, String> elementCollectionClasses = getElementCollection().stream()
//                .filter(ec -> ec.getConnectedClass() != null)
//                .map(BeanCollectionAttribute::getConnectedClass)
//                .distinct()
//                .filter(jc -> !javaClasses.contains(jc.getFQN()))
//                .collect(toMap(identity(), JavaClass::getFQN));
//        javaClasses.addAll(elementCollectionClasses.values());
//        for (ManagedClass elementCollectionClass : elementCollectionClasses.keySet()) {
//            javaClasses.addAll(elementCollectionClass.getAttributes().getConnectedClass(javaClasses));
//        }
//        return javaClasses;
//    }

    public Set<String> getBasicConnectedClass(Set<String> javaClasses) {
        List<String> basicClasses = getBasic().stream()
                .map(BaseAttribute::getDataTypeLabel)
                .filter(dataType -> {
                    if (StringUtils.isNotEmpty(dataType)) {
                        dataType = isArray(dataType) ? getArrayType(dataType) : dataType;
                        AttributeType.Type type = getType(dataType);
                        if (type == OTHER) {
                            return !JavaIdentifiers.getPackageName(dataType).startsWith("java");
                        }
                    }
                    return false;
                })
                .distinct()
                .collect(Collectors.toList());
        javaClasses.addAll(basicClasses);
        return javaClasses;
    }

    public Optional<AssociationAttribute> getAssociationAttribute(String id) {
        return getAssociationAttributes().stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    public void removeAssociationAttribute(AssociationAttribute associationAttribute) {
        if (associationAttribute instanceof ManyToManyAssociation) {
            this.getManyToMany().remove((ManyToManyAssociation) associationAttribute);
            notifyListeners(associationAttribute, "removeAttribute", null, null);
        } else if (associationAttribute instanceof OneToManyAssociation) {
            this.getOneToMany().remove((OneToManyAssociation) associationAttribute);
            notifyListeners(associationAttribute, "removeAttribute", null, null);
        } else if (associationAttribute instanceof ManyToOneAssociation) {
            this.getManyToOne().remove((ManyToOneAssociation) associationAttribute);
            notifyListeners(associationAttribute, "removeAttribute", null, null);
        } else if (associationAttribute instanceof OneToOneAssociation) {
            this.getOneToOne().remove((OneToOneAssociation) associationAttribute);
            notifyListeners(associationAttribute, "removeAttribute", null, null);
        } else {
            throw new IllegalStateException("Invalid Type Association Attribute");
        }
    }

    public void addAssociationAttribute(AssociationAttribute associationAttribute) {
        if (associationAttribute instanceof ManyToManyAssociation) {
            this.addManyToMany((ManyToManyAssociation) associationAttribute);
            notifyListeners(associationAttribute, "addAttribute", null, null);
        } else if (associationAttribute instanceof OneToManyAssociation) {
            this.addOneToMany((OneToManyAssociation) associationAttribute);
            notifyListeners(associationAttribute, "addAttribute", null, null);
        } else if (associationAttribute instanceof ManyToOneAssociation) {
            this.addManyToOne((ManyToOneAssociation) associationAttribute);
            notifyListeners(associationAttribute, "addAttribute", null, null);
        } else if (associationAttribute instanceof OneToOneAssociation) {
            this.addOneToOne((OneToOneAssociation) associationAttribute);
            notifyListeners(associationAttribute, "addAttribute", null, null);
        } else {
            throw new IllegalStateException("Invalid Type Association Attribute");
        }
    }


    public List<Attribute> getNonAssociationAttributes() {
        List<Attribute> attributes = new ArrayList<>(this.getBasic());
        attributes.addAll(this.getElementCollection());
        attributes.addAll(this.getTransient());
        return attributes;
    }

    public void removeNonOwnerAttribute(Set<JavaClass> filterJavaClasses) {
        Predicate<AssociationAttribute> filterOwner = attr -> attr.isOwner()
                || (attr.getConnectedAttribute() != null && filterJavaClasses.contains(attr.getConnectedClass()));//either owner or contains in specified class set

        setOneToOne(
                getOneToOne()
                        .stream()
                        .filter(filterOwner)
                        .collect(toList())
        );
        setOneToMany(
                getOneToMany()
                        .stream()
                        .filter(filterOwner)
                        .collect(toList())
        );
        setManyToOne(
                getManyToOne()
                        .stream()
                        .filter(filterOwner)
                        .collect(toList())
        );
        setManyToMany(
                getManyToMany()
                        .stream()
                        .filter(filterOwner)
                        .collect(toList())
        );
    }

    @Override
    public void load(EntityMappings entityMappings, TypeElement element, boolean fieldAccess) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
