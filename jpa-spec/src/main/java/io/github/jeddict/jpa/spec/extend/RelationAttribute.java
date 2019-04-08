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
package io.github.jeddict.jpa.spec.extend;

import io.github.jeddict.jaxb.spec.JaxbVariableType;
import static io.github.jeddict.jcode.JPAConstants.CASCADE_TYPE_FQN;
import static io.github.jeddict.jcode.util.JavaUtil.mergePackage;
import io.github.jeddict.jpa.spec.AccessType;
import io.github.jeddict.jpa.spec.CascadeType;
import io.github.jeddict.jpa.spec.EmptyType;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.FetchType;
import io.github.jeddict.jpa.spec.JoinTable;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.source.AnnotationExplorer;
import io.github.jeddict.source.JavaSourceParserUtil;
import io.github.jeddict.source.MemberExplorer;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import io.github.jeddict.util.StringUtils;

/**
 *
 * @author Gaurav Gupta
 */
@XmlType(propOrder = {
    "joinTable",
    "cascade"
})
public abstract class RelationAttribute extends Attribute implements AccessTypeHandler, FetchTypeHandler, JoinTableHandler {

    @XmlAttribute(name = "connected-entity-id", required = true)
    @XmlIDREF
    private Entity connectedEntity;

    @XmlAttribute(name = "connected-attribute-id", required = true)
    @XmlIDREF
    private RelationAttribute connectedAttribute;

    @XmlElement(name = "join-table")
    protected JoinTable joinTable;
    protected CascadeType cascade;
    @XmlTransient
    protected String targetEntityPackage;
    @XmlTransient
    protected String targetEntity;
    @XmlAttribute(name = "fetch")
    protected FetchType fetch;

    @XmlAttribute(name = "access")
    protected AccessType access;

    @Deprecated
    protected void loadAttribute(EntityMappings entityMappings, Element element, VariableElement variableElement, ExecutableElement getterElement, AnnotationMirror relationAnnotationMirror) {
        super.loadAttribute(element, variableElement, getterElement);
        this.joinTable = JoinTable.load(element);
        if (StringUtils.isNotBlank((String) JavaSourceParserUtil.findAnnotationValue(relationAnnotationMirror, "mappedBy"))) {
            setOwner(false);
        } else {
            setOwner(true);
        }
        List cascadeList = (List) JavaSourceParserUtil.findAnnotationValue(relationAnnotationMirror, "cascade");
        if (cascadeList != null) {
            CascadeType cascadeType = new CascadeType();
            this.cascade = cascadeType;
            for (Object cascadeObj : cascadeList) {
                switch (cascadeObj.toString()) {
                    case CASCADE_TYPE_FQN + ".ALL":
                        cascadeType.setCascadeAll(new EmptyType());
                        break;
                    case CASCADE_TYPE_FQN + ".PERSIST":
                        cascadeType.setCascadePersist(new EmptyType());
                        break;
                    case CASCADE_TYPE_FQN + ".MERGE":
                        cascadeType.setCascadeMerge(new EmptyType());
                        break;
                    case CASCADE_TYPE_FQN + ".REMOVE":
                        cascadeType.setCascadeRemove(new EmptyType());
                        break;
                    case CASCADE_TYPE_FQN + ".REFRESH":
                        cascadeType.setCascadeRefresh(new EmptyType());
                        break;
                    case CASCADE_TYPE_FQN + ".DETACH":
                        cascadeType.setCascadeDetach(new EmptyType());
                        break;
                    default:
                        throw new IllegalStateException("Unknown Cascade Type : " + cascadeObj.toString());
                }
            }
        }
        this.fetch = FetchType.load(element, relationAnnotationMirror);
        this.access = AccessType.load(element);
    }

    protected void loadAttribute(MemberExplorer member, AnnotationExplorer annotation) {
        super.loadAttribute(member);
        this.joinTable = JoinTable.load(member);
        setOwner(!annotation.getString("mappedBy").isPresent());

        List<String> cascadeList = annotation.getEnumList("cascade");

        if (cascadeList != null) {
            CascadeType cascadeType = new CascadeType();
            this.cascade = cascadeType;
            for (String cascadeValue : cascadeList) {
                switch (cascadeValue) {
                    case "ALL":
                        cascadeType.setCascadeAll(new EmptyType());
                        break;
                    case "PERSIST":
                        cascadeType.setCascadePersist(new EmptyType());
                        break;
                    case "MERGE":
                        cascadeType.setCascadeMerge(new EmptyType());
                        break;
                    case "REMOVE":
                        cascadeType.setCascadeRemove(new EmptyType());
                        break;
                    case "REFRESH":
                        cascadeType.setCascadeRefresh(new EmptyType());
                        break;
                    case "DETACH":
                        cascadeType.setCascadeDetach(new EmptyType());
                        break;
                    default:
                        throw new IllegalStateException("Unknown Cascade Type : " + cascadeValue);
                }
            }
        }
        this.fetch = FetchType.load(annotation);
        this.access = AccessType.load(member);
    }

    /**
     * Gets the value of the joinTable property.
     *
     * @return possible object is {@link JoinTable }
     *
     */
    @Override
    public JoinTable getJoinTable() {
        if (joinTable == null) {
            joinTable = new JoinTable();
        }
        return joinTable;
    }

    /**
     * Sets the value of the joinTable property.
     *
     * @param value allowed object is {@link JoinTable }
     *
     */
    @Override
    public void setJoinTable(JoinTable value) {
        this.joinTable = value;
    }

    /**
     * Gets the value of the cascade property.
     *
     * @return possible object is {@link CascadeType }
     *
     */
    public CascadeType getCascade() {
        return cascade;
    }

    /**
     * Sets the value of the cascade property.
     *
     * @param value allowed object is {@link CascadeType }
     *
     */
    public void setCascade(CascadeType value) {
        this.cascade = value;
    }

    public String getTargetEntityFQN() {
        return mergePackage(targetEntityPackage, getTargetEntity());
    }

    /**
     * Gets the value of the targetEntity property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getTargetEntity() {
        if (targetEntity != null) {
            return targetEntity;
        }
        if (connectedEntity != null) { //TODO bug : called by tooltip from init
            return connectedEntity.getClazz();
        } else {
            return null;
        }
    }

    /**
     * Sets the value of the targetEntity property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setTargetEntity(String value) {
        this.targetEntity = value;
    }

    /**
     * Gets the value of the fetch property.
     *
     * @return possible object is {@link FetchType }
     *
     */
    @Override
    public FetchType getFetch() {
        return fetch;
    }

    /**
     * Sets the value of the fetch property.
     *
     * @param value allowed object is {@link FetchType }
     *
     */
    @Override
    public void setFetch(FetchType value) {
        this.fetch = value;
    }

    /**
     * Gets the value of the access property.
     *
     * @return possible object is {@link AccessType }
     *
     */
    @Override
    public AccessType getAccess() {
        return access;
    }

    /**
     * Sets the value of the access property.
     *
     * @param value allowed object is {@link AccessType }
     *
     */
    @Override
    public void setAccess(AccessType value) {
        this.access = value;
    }

    /**
     * @return the connectedEntityId
     */
    public Entity getConnectedEntity() {
        return connectedEntity;
    }

    /**
     * @param connectedEntity the connectedEntity to set
     */
    public void setConnectedEntity(Entity connectedEntity) {
        this.connectedEntity = connectedEntity;
    }

    /**
     * @return the connectedAttribute
     */
    public RelationAttribute getConnectedAttribute() {
        return connectedAttribute;
    }

    /**
     * @param connectedAttribute the connectedAttribute to set
     */
    public void setConnectedAttribute(RelationAttribute connectedAttribute) {
        this.connectedAttribute = connectedAttribute;
    }

    public String getConnectedAttributeName() {
        return connectedAttribute!=null?connectedAttribute.getName():null;
    }

    @Override
    public List<JaxbVariableType> getJaxbVariableList() {
        List<JaxbVariableType> jaxbVariableTypeList = new ArrayList<>();
        jaxbVariableTypeList.add(JaxbVariableType.XML_DEFAULT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT_WRAPPER);
        jaxbVariableTypeList.add(JaxbVariableType.XML_TRANSIENT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_INVERSE_REFERENCE);//both side are applicable
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT_REF);
        return jaxbVariableTypeList;
    }

    /**
     * @return the owner
     */
    public abstract boolean isOwner();

    /**
     * @param owner the owner to set
     */
    public abstract void setOwner(boolean owner);

    @Override
    public String getDataTypeLabel() {
        return getTargetEntity();
    }
    
    public boolean isNoSQL() {
        return ((ManagedClass) this.getJavaClass()).getNoSQL();
    }

}
