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
package org.netbeans.jpa.modeler.spec.extend;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.xml.bind.annotation.XmlElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;
import org.netbeans.jpa.modeler.db.accessor.BasicSpecAccessor;
import org.netbeans.jpa.modeler.db.accessor.ElementCollectionSpecAccessor;
import org.netbeans.jpa.modeler.db.accessor.EmbeddedSpecAccessor;
import org.netbeans.jpa.modeler.db.accessor.TransientSpecAccessor;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.Transient;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
//@XmlType(propOrder = {
//    "basic",
//    "manyToOne",
//    "oneToMany",
//    "oneToOne",
//    "manyToMany",
//    "elementCollection",
//    "embedded",
//    "_transient"
//})
public abstract class BaseAttributes implements IAttributes {

    @XmlElement(name = "basic")
    protected List<Basic> basic;
    @XmlElement(name = "many-to-one")
    protected List<ManyToOne> manyToOne;
    @XmlElement(name = "one-to-many")
    protected List<OneToMany> oneToMany;
    @XmlElement(name = "one-to-one")
    protected List<OneToOne> oneToOne;
    @XmlElement(name = "many-to-many")
    protected List<ManyToMany> manyToMany;
    @XmlElement(name = "element-collection")
    protected List<ElementCollection> elementCollection;
    @XmlElement(name = "embedded")
    protected List<Embedded> embedded;
    @XmlElement(name = "transient")
    protected List<Transient> _transient;

    /**
     * Gets the value of the basic property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the basic property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBasic().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Basic }
     *
     *
     */
    @Override
    public List<Basic> getBasic() {
        if (basic == null) {
            basic = new ArrayList<Basic>();
        }
        return this.basic;
    }

    @Override
    public void addBasic(Basic basic) {
        this.getBasic().add(basic);
        notifyListeners(basic, "addAttribute", null, null);
    }

    @Override
    public void removeBasic(Basic basic) {
        this.getBasic().remove(basic);
        notifyListeners(basic, "removeAttribute", null, null);
    }

    /**
     * Gets the value of the manyToOne property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the manyToOne property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getManyToOne().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManyToOne }
     *
     *
     */
    @Override
    public List<ManyToOne> getManyToOne() {
        if (manyToOne == null) {
            manyToOne = new ArrayList<ManyToOne>();
        }
        return this.manyToOne;
    }

    /**
     * Gets the value of the oneToMany property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the oneToMany property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOneToMany().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OneToMany }
     *
     *
     */
    @Override
    public List<OneToMany> getOneToMany() {
        if (oneToMany == null) {
            oneToMany = new ArrayList<OneToMany>();
        }
        return this.oneToMany;
    }

    /**
     * Gets the value of the oneToOne property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the oneToOne property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOneToOne().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OneToOne }
     *
     *
     */
    @Override
    public List<OneToOne> getOneToOne() {
        if (oneToOne == null) {
            oneToOne = new ArrayList<OneToOne>();
        }
        return this.oneToOne;
    }

    /**
     * Gets the value of the manyToMany property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the manyToMany property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getManyToMany().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManyToMany }
     *
     *
     */
    @Override
    public List<ManyToMany> getManyToMany() {
        if (manyToMany == null) {
            manyToMany = new ArrayList<ManyToMany>();
        }
        return this.manyToMany;
    }

    /**
     * Gets the value of the elementCollection property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the elementCollection property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getElementCollection().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ElementCollection }
     *
     *
     */
    @Override
    public List<ElementCollection> getElementCollection() {
        if (elementCollection == null) {
            elementCollection = new ArrayList<ElementCollection>();
        }
        return this.elementCollection;
    }

    @Override
    public void addElementCollection(ElementCollection elementCollection) {
        this.getElementCollection().add(elementCollection);
        notifyListeners(elementCollection, "addAttribute", null, null);
    }

    @Override
    public void removeElementCollection(ElementCollection elementCollection) {
        this.getElementCollection().remove(elementCollection);
        notifyListeners(elementCollection, "removeAttribute", null, null);
    }

    /**
     * Gets the value of the embedded property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the embedded property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEmbedded().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Embedded }
     *
     *
     */
    @Override
    public List<Embedded> getEmbedded() {
        if (embedded == null) {
            embedded = new ArrayList<Embedded>();
        }
        return this.embedded;
    }

    @Override
    public void addEmbedded(Embedded embedded) {
        this.getEmbedded().add(embedded);
        notifyListeners(embedded, "addAttribute", null, null);
    }

    @Override
    public void removeEmbedded(Embedded embedded) {
        this.getEmbedded().remove(embedded);
        notifyListeners(embedded, "removeAttribute", null, null);
    }

    /**
     * Gets the value of the transient property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the transient property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransient().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Transient }
     *
     *
     */
    @Override
    public List<Transient> getTransient() {
        if (_transient == null) {
            _transient = new ArrayList<Transient>();
        }
        return this._transient;
    }

    @Override
    public void addTransient(Transient _transient) {
        this.getTransient().add(_transient);
        notifyListeners(_transient, "addAttribute", null, null);
    }

    @Override
    public void removeTransient(Transient _transient) {
        this.getTransient().remove(_transient);
        notifyListeners(_transient, "removeAttribute", null, null);
    }

    public List<RelationAttribute> getRelationAttributes() {
        List<RelationAttribute> relationAttributes = new ArrayList<RelationAttribute>(this.getOneToOne());
        relationAttributes.addAll(this.getOneToMany());
        relationAttributes.addAll(this.getManyToOne());
        relationAttributes.addAll(this.getManyToMany());
        return relationAttributes;
    }

    //does not need to extends BaseElement (id field hide)
    private transient List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();

    @Override
    public void notifyListeners(Object object, String property, String oldValue, String newValue) {
        for (PropertyChangeListener propertyChangeListener : listener) {
            propertyChangeListener.propertyChange(new PropertyChangeEvent(object, property, oldValue, newValue));
        }
    }

    @Override
    public void addChangeListener(PropertyChangeListener newListener) {
        listener.add(newListener);
    }

    @Override
    public void removeChangeListener(PropertyChangeListener newListener) {
        listener.remove(newListener);
    }

    @Override
    public void removeRelationAttribute(RelationAttribute relationAttribute) {
        if (relationAttribute instanceof ManyToMany) {
            this.getManyToMany().remove((ManyToMany) relationAttribute);
            notifyListeners(relationAttribute, "removeAttribute", null, null);
        } else if (relationAttribute instanceof OneToMany) {
            this.getOneToMany().remove((OneToMany) relationAttribute);
            notifyListeners(relationAttribute, "removeAttribute", null, null);
        } else if (relationAttribute instanceof ManyToOne) {
            this.getManyToOne().remove((ManyToOne) relationAttribute);
            notifyListeners(relationAttribute, "removeAttribute", null, null);
        } else if (relationAttribute instanceof OneToOne) {
            this.getOneToOne().remove((OneToOne) relationAttribute);
            notifyListeners(relationAttribute, "removeAttribute", null, null);
        } else {
            throw new IllegalStateException("Invalid Type Relation Attribute");
        }
    }

    @Override
    public void addRelationAttribute(RelationAttribute relationAttribute) {
        if (relationAttribute instanceof ManyToMany) {
            this.getManyToMany().add((ManyToMany) relationAttribute);
            notifyListeners(relationAttribute, "addAttribute", null, null);
        } else if (relationAttribute instanceof OneToMany) {
            this.getOneToMany().add((OneToMany) relationAttribute);
            notifyListeners(relationAttribute, "addAttribute", null, null);
        } else if (relationAttribute instanceof ManyToOne) {
            this.getManyToOne().add((ManyToOne) relationAttribute);
            notifyListeners(relationAttribute, "addAttribute", null, null);
        } else if (relationAttribute instanceof OneToOne) {
            this.getOneToOne().add((OneToOne) relationAttribute);
            notifyListeners(relationAttribute, "addAttribute", null, null);
        } else {
            throw new IllegalStateException("Invalid Type Relation Attribute");
        }
    }

    public List<Attribute> getAllAttribute() {
        List<Attribute> attributes = new ArrayList<Attribute>();
        attributes.addAll(this.getBasic());
        attributes.addAll(this.getEmbedded());
        attributes.addAll(this.getElementCollection());
        attributes.addAll(this.getRelationAttributes());
        attributes.addAll(this.getTransient());
        return attributes;
    }

    @Override
    public boolean isAttributeExist(String name) {

        if (basic != null) {
            for (Basic basic_TMP : basic) {
                if (basic_TMP.getName() != null && basic_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }
        if (_transient != null) {
            for (Transient transient_TMP : _transient) {
                if (transient_TMP.getName() != null && transient_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }
        if (elementCollection != null) {
            for (ElementCollection elementCollection_TMP : elementCollection) {
                if (elementCollection_TMP.getName() != null && elementCollection_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }
        if (embedded != null) {
            for (Embedded embedded_TMP : embedded) {
                if (embedded_TMP.getName() != null && embedded_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }

        if (oneToOne != null) {
            for (OneToOne oneToOne_TMP : oneToOne) {
                if (oneToOne_TMP.getName() != null && oneToOne_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }
        if (oneToMany != null) {
            for (OneToMany oneToMany_TMP : oneToMany) {
                if (oneToMany_TMP.getName() != null && oneToMany_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }
        if (manyToOne != null) {
            for (ManyToOne manyToOne_TMP : manyToOne) {
                if (manyToOne_TMP.getName() != null && manyToOne_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }
        if (manyToMany != null) {
            for (ManyToMany manyToMany_TMP : manyToMany) {
                if (manyToMany_TMP.getName() != null && manyToMany_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<Attribute> findAllAttribute(String name) {
        List<Attribute> attributes = new ArrayList<Attribute>();

        if (basic != null) {
            for (Basic basic_TMP : basic) {
                if (basic_TMP.getName() != null && basic_TMP.getName().equals(name)) {
                    attributes.add(basic_TMP);
                }
            }
        }
        if (elementCollection != null) {
            for (ElementCollection elementCollection_TMP : elementCollection) {
                if (elementCollection_TMP.getName() != null && elementCollection_TMP.getName().equals(name)) {
                    attributes.add(elementCollection_TMP);
                }
            }
        }

        if (_transient != null) {
            for (Transient transient_TMP : _transient) {
                if (transient_TMP.getName() != null && transient_TMP.getName().equals(name)) {
                    attributes.add(transient_TMP);
                }
            }
        }
        if (oneToOne != null) {
            for (OneToOne oneToOne_TMP : oneToOne) {
                if (oneToOne_TMP.getName() != null && oneToOne_TMP.getName().equals(name)) {
                    attributes.add(oneToOne_TMP);
                }
            }
        }
        if (oneToMany != null) {
            for (OneToMany oneToMany_TMP : oneToMany) {
                if (oneToMany_TMP.getName() != null && oneToMany_TMP.getName().equals(name)) {
                    attributes.add(oneToMany_TMP);
                }
            }
        }
        if (manyToOne != null) {
            for (ManyToOne manyToOne_TMP : manyToOne) {
                if (manyToOne_TMP.getName() != null && manyToOne_TMP.getName().equals(name)) {
                    attributes.add(manyToOne_TMP);
                }
            }
        }
        if (manyToMany != null) {
            for (ManyToMany manyToMany_TMP : manyToMany) {
                if (manyToMany_TMP.getName() != null && manyToMany_TMP.getName().equals(name)) {
                    attributes.add(manyToMany_TMP);
                }
            }
        }
        if (embedded != null) {
            for (Embedded embedded_TMP : embedded) {
                if (embedded_TMP.getName() != null && embedded_TMP.getName().equals(name)) {
                    attributes.add(embedded_TMP);
                }
            }
        }

        return attributes;
    }

    
    public XMLAttributes getAccessor() {
        XMLAttributes attr = new XMLAttributes();
        attr.setBasicCollections(new ArrayList<>());
        attr.setBasicMaps(new ArrayList<>());
        attr.setTransformations(new ArrayList<>());
        attr.setVariableOneToOnes(new ArrayList<>());
        attr.setStructures(new ArrayList<>());
        attr.setArrays(new ArrayList<>());
        
        attr.setBasics(new ArrayList<>());
        attr.setElementCollections(new ArrayList<>());
        attr.setEmbeddeds(new ArrayList<>());
        attr.setTransients(new ArrayList<>());
        attr.setManyToManys(new ArrayList<>());
        attr.setManyToOnes(new ArrayList<>());
        attr.setOneToManys(new ArrayList<>());
        attr.setOneToOnes(new ArrayList<>());
        return attr;
//        return updateAccessor(attr);
    }
    
    public XMLAttributes updateAccessor(XMLAttributes attr) {
        attr.getBasics().addAll(getBasic().stream().map(BasicSpecAccessor::getInstance).collect(toList()));
        attr.getElementCollections().addAll(getElementCollection().stream().map(ElementCollectionSpecAccessor::getInstance).collect(toList()));
        attr.getEmbeddeds().addAll(getEmbedded().stream().map(EmbeddedSpecAccessor::getInstance).collect(toList()));
        attr.getTransients().addAll(getTransient().stream().map(TransientSpecAccessor::getInstance).collect(toList()));
        attr.getManyToManys().addAll(getManyToMany().stream().map(ManyToMany::getAccessor).collect(toList()));
        attr.getManyToOnes().addAll(getManyToOne().stream().map(ManyToOne::getAccessor).collect(toList()));
        attr.getOneToManys().addAll(getOneToMany().stream().map(OneToMany::getAccessor).collect(toList()));
        attr.getOneToOnes().addAll(getOneToOne().stream().map(OneToOne::getAccessor).collect(toList()));
        return attr;
    }

}
