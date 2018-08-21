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

import static io.github.jeddict.jcode.util.AttributeType.Type.OTHER;
import static io.github.jeddict.jcode.util.AttributeType.getArrayType;
import static io.github.jeddict.jcode.util.AttributeType.getType;
import static io.github.jeddict.jcode.util.AttributeType.isArray;
import static io.github.jeddict.jcode.util.AttributeType.isJavaType;
import io.github.jeddict.jcode.util.JavaIdentifiers;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.Transient;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.Attributes;
import io.github.jeddict.jpa.spec.extend.BaseAttribute;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.source.ClassExplorer;
import io.github.jeddict.source.MemberExplorer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;
import javax.lang.model.element.TypeElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang.StringUtils;

@XmlAccessorType(XmlAccessType.FIELD)
public class BeanAttributes extends Attributes<BeanClass> {

    @XmlElement(name = "basic")
    private List<BeanAttribute> basic;
    @XmlElement(name = "element-collection")
    private List<BeanCollectionAttribute> elementCollection;
    @XmlElement(name = "transient")
    private List<Transient> _transient;

    @XmlElement(name = "many-to-one-assoc")
    private List<ManyToOneAssociation> manyToOne;
    @XmlElement(name = "one-to-many-assoc")
    private List<OneToManyAssociation> oneToMany;
    @XmlElement(name = "one-to-one-assoc")
    private List<OneToOneAssociation> oneToOne;
    @XmlElement(name = "many-to-many-assoc")
    private List<ManyToManyAssociation> manyToMany;

    @Override
    public void load(EntityMappings entityMappings, TypeElement typeElement, boolean fieldAccess) {
    }

    @Override
    public void load(ClassExplorer clazz) {
        Collection<MemberExplorer> members = clazz.getMembers();

        for (MemberExplorer member : members) {
            boolean collectionType = member.isCollectionType();
            boolean mapType = member.isMapType();
            String type = member.getType();

            boolean elementCollectionType = false;
            boolean relationType = false;
            String typeArgument = null;
            if (collectionType || mapType) {
                if (!member.getTypeArguments().isEmpty()) {
                    typeArgument = member.getTypeArguments().get(mapType ? 1 : 0);
                    if (isJavaType(typeArgument)) {
                        elementCollectionType = true;
                    } else {
                        relationType = true;
                    }
                } else {
                    relationType = true;
                }
            } else if (!isJavaType(type)) {
                relationType = true;
            }


            if (member.isTransient()) {
                this.addTransient(Transient.load(member));
            } else if (elementCollectionType) {
                this.addElementCollection(BeanCollectionAttribute.load(member, typeArgument));
            } else if (relationType) {
                if (collectionType || mapType) {
//                    member.getTypeDeclaration()
//                      ManyToManyAssociation
//                      ManyToOneAssociation
                } else {
//                      OneToManyAssociation
//                      OneToOneAssociation
                }
                System.out.println("");
//                this.addElementCollection(BeanCollectionAttribute.load(member, typeArgument));
            } else {
                this.addBasic(BeanAttribute.load(member));
            }
        }

    }

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
        return this.basic;
    }

    public void setBasic(List<BeanAttribute> attributes) {
        this.basic = attributes;
    }

    public Optional<BeanAttribute> getBasic(String id) {
        return findById(basic, id);
    }

    public void addBasic(BeanAttribute attribute) {
        getBasic().add(attribute);
        attribute.setAttributes(this);
        notifyListeners(basic, ADD_ATTRIBUTE_PROPERTY, null, null);
    }

    public void removeBasic(BeanAttribute attribute) {
        getBasic().remove(attribute);
        attribute.setAttributes(null);
        notifyListeners(basic, REMOVE_ATTRIBUTE_PROPERTY, null, null);
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
        return findById(elementCollection, id);
    }

    public void addElementCollection(BeanCollectionAttribute attribute) {
        getElementCollection().add(attribute);
        attribute.setAttributes(this);
        notifyListeners(attribute, ADD_ATTRIBUTE_PROPERTY, null, null);
    }

    public void removeElementCollection(BeanCollectionAttribute attribute) {
        getElementCollection().remove(attribute);
        attribute.setAttributes(null);
        notifyListeners(attribute, REMOVE_ATTRIBUTE_PROPERTY, null, null);
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
        return findById(_transient, id);
    }

    public void addTransient(Transient _transient) {
        getTransient().add(_transient);
        _transient.setAttributes(this);
        notifyListeners(_transient, ADD_ATTRIBUTE_PROPERTY, null, null);
    }

    public void removeTransient(Transient _transient) {
        getTransient().remove(_transient);
        _transient.setAttributes(null);
        notifyListeners(_transient, REMOVE_ATTRIBUTE_PROPERTY, null, null);
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
        return findById(manyToOne, id);
    }

    public void addManyToOne(ManyToOneAssociation manyToOne) {
        getManyToOne().add(manyToOne);
        manyToOne.setAttributes(this);
        notifyListeners(manyToOne, ADD_ATTRIBUTE_PROPERTY, null, null);
    }

    public void removeManyToOne(ManyToOneAssociation manyToOne) {
        getManyToOne().remove(manyToOne);
        manyToOne.setAttributes(null);
        notifyListeners(manyToOne, REMOVE_ATTRIBUTE_PROPERTY, null, null);
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
        return findById(oneToMany, id);
    }

    public void addOneToMany(OneToManyAssociation oneToMany) {
        getOneToMany().add(oneToMany);
        oneToMany.setAttributes(this);
        notifyListeners(oneToMany, ADD_ATTRIBUTE_PROPERTY, null, null);
    }

    public void removeOneToMany(OneToManyAssociation oneToMany) {
        getOneToMany().remove(oneToMany);
        oneToMany.setAttributes(null);
        notifyListeners(oneToMany, REMOVE_ATTRIBUTE_PROPERTY, null, null);
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
        return findById(oneToOne, id);
    }

    public void addOneToOne(OneToOneAssociation oneToOne) {
        getOneToOne().add(oneToOne);
        oneToOne.setAttributes(this);
        notifyListeners(oneToOne, ADD_ATTRIBUTE_PROPERTY, null, null);
    }

    public void removeOneToOne(OneToOneAssociation oneToOne) {
        getOneToOne().remove(oneToOne);
        oneToOne.setAttributes(null);
        notifyListeners(oneToOne, REMOVE_ATTRIBUTE_PROPERTY, null, null);
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
        return findById(manyToMany, id);
    }

    public void addManyToMany(ManyToManyAssociation manyToMany) {
        getManyToMany().add(manyToMany);
        manyToMany.setAttributes(this);
        notifyListeners(manyToMany, ADD_ATTRIBUTE_PROPERTY, null, null);
    }

    public void removeManyToMany(ManyToManyAssociation manyToMany) {
        getManyToMany().remove(manyToMany);
        manyToMany.setAttributes(null);
        notifyListeners(manyToMany, REMOVE_ATTRIBUTE_PROPERTY, null, null);
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

    public Set<String> getBasicConnectedClass(Set<String> javaClasses) {
        List<String> basicClasses = getBasic().stream()
                .map(BaseAttribute::getDataTypeLabel)
                .filter(StringUtils::isNotEmpty)
                .filter(dataType -> {
                    dataType = isArray(dataType) ? getArrayType(dataType) : dataType;
                    if (getType(dataType) == OTHER) {
                        return !JavaIdentifiers.getPackageName(dataType).startsWith("java");
                    }
                    return false;
                })
                .distinct()
                .collect(toList());
        javaClasses.addAll(basicClasses);
        return javaClasses;
    }

    public Optional<AssociationAttribute> getAssociationAttribute(String id) {
        return findById(getAssociationAttributes(), id);
    }

    public void removeAssociationAttribute(AssociationAttribute associationAttribute) {
        if (associationAttribute instanceof ManyToManyAssociation) {
            this.getManyToMany().remove((ManyToManyAssociation) associationAttribute);
        } else if (associationAttribute instanceof OneToManyAssociation) {
            this.getOneToMany().remove((OneToManyAssociation) associationAttribute);
        } else if (associationAttribute instanceof ManyToOneAssociation) {
            this.getManyToOne().remove((ManyToOneAssociation) associationAttribute);
        } else if (associationAttribute instanceof OneToOneAssociation) {
            this.getOneToOne().remove((OneToOneAssociation) associationAttribute);
        } else {
            throw new IllegalStateException("Invalid Type Association Attribute");
        }
        notifyListeners(associationAttribute, REMOVE_ATTRIBUTE_PROPERTY, null, null);
    }

    public void addAssociationAttribute(AssociationAttribute associationAttribute) {
        if (associationAttribute instanceof ManyToManyAssociation) {
            this.addManyToMany((ManyToManyAssociation) associationAttribute);
        } else if (associationAttribute instanceof OneToManyAssociation) {
            this.addOneToMany((OneToManyAssociation) associationAttribute);
        } else if (associationAttribute instanceof ManyToOneAssociation) {
            this.addManyToOne((ManyToOneAssociation) associationAttribute);
        } else if (associationAttribute instanceof OneToOneAssociation) {
            this.addOneToOne((OneToOneAssociation) associationAttribute);
        } else {
            throw new IllegalStateException("Invalid Type Association Attribute");
        }
        notifyListeners(associationAttribute, ADD_ATTRIBUTE_PROPERTY, null, null);
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

}
