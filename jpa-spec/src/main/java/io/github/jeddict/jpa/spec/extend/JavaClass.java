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
package io.github.jeddict.jpa.spec.extend;

import static io.github.jeddict.jcode.JSONBConstants.JSONB_NILLABLE_FQN;
import static io.github.jeddict.jcode.JSONBConstants.JSONB_TYPE_ADAPTER_FQN;
import static io.github.jeddict.jcode.JSONBConstants.JSONB_TYPE_DESERIALIZER_FQN;
import static io.github.jeddict.jcode.JSONBConstants.JSONB_TYPE_SERIALIZER_FQN;
import static io.github.jeddict.jcode.JSONBConstants.JSONB_VISIBILITY_FQN;
import static io.github.jeddict.jcode.util.JavaUtil.mergePackage;
import static io.github.jeddict.jcode.util.JavaUtil.not;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.IdentifiableClass;
import io.github.jeddict.jsonb.spec.JsonbDateFormat;
import io.github.jeddict.jsonb.spec.JsonbNumberFormat;
import io.github.jeddict.jsonb.spec.JsonbTypeHandler;
import io.github.jeddict.jsonb.spec.JsonbVisibilityHandler;
import io.github.jeddict.snippet.ClassSnippet;
import io.github.jeddict.source.ClassExplorer;
import io.github.jeddict.source.JCRELoader;
import io.github.jeddict.source.JavaSourceParserUtil;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import static java.util.Objects.nonNull;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.type.Embedded;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 * @param <T>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class JavaClass<T extends IAttributes> extends FlowNode
        implements JCRELoader, JsonbTypeHandler, JsonbVisibilityHandler, Embedded {

    @XmlElement(name = "ts")
    private ClassMembers toStringMethod;

    @XmlElement(name = "hc")
    private ClassMembers hashCodeMethod;

    @XmlElement(name = "eq")
    private ClassMembers equalsMethod;

    @XmlElement(name = "con")
    private List<Constructor> constructors;

    @XmlAttribute(name = "abs")
    protected Boolean _abstract = false;

    @XmlAttribute(name = "class", required = true)
    protected String clazz;

    @XmlAttribute(name = "pclass")
    private String previousClass;

    @XmlTransient
    private String superclassId;

    @XmlAttribute(name = "superclassId")
    @XmlIDREF
    private JavaClass superclass;

    @XmlTransient
    private Set<JavaClass> subclassList;

    @XmlAttribute(name = "v")
    private boolean visible = true;

    @XmlElement(name = "ext")
    private ReferenceClass superclassRef; // if refered from classpath

    @XmlElement(name = "inf")
    private Set<ReferenceClass> interfaces;

    private List<ClassAnnotation> annotation;

    @XmlElement(name = "snp")
    private List<ClassSnippet> snippets;

    @XmlTransient
    private ReferenceClass runtimeSuperclassRef;

    @XmlTransient
    private Set<ReferenceClass> runtimeInterfaces;

    @XmlTransient
    private List<ClassAnnotation> runtimeAnnotation;

    @XmlTransient
    private List<ClassSnippet> runtimeSnippets;

    @XmlTransient
    private List<String> runtimeTypeParameters;

    @XmlTransient
    private FileObject fileObject;

    @XmlAttribute(name = "gen")
    private Boolean generateSourceCode;

    @XmlAttribute(name = "pkg")
    private String _package;

    @XmlElement(name = "des")
    protected String description;

    @XmlElement(name = "ath")
    private String author;

    //Jsonb support start
    @XmlAttribute(name = "jbn")
    private Boolean jsonbNillable;

    @XmlElement(name = "jbta")
    private ReferenceClass jsonbTypeAdapter;

    @XmlElement(name = "jbdf")
    private JsonbDateFormat jsonbDateFormat;

    @XmlElement(name = "jbnf")
    private JsonbNumberFormat jsonbNumberFormat;

    @XmlElement(name = "jbtd")
    private ReferenceClass jsonbTypeDeserializer;

    @XmlElement(name = "jbts")
    private ReferenceClass jsonbTypeSerializer;

    @XmlElement(name = "jbv")
    private ReferenceClass jsonbVisibility;

    @XmlElementWrapper(name = "jbpo")
    @XmlElement(name = "i")
    @XmlIDREF
    private List<Attribute> jsonbPropertyOrder; //REVENG pending
    //Jsonb support end

    @XmlAttribute(name = "xre")
    private Boolean xmlRootElement = false;

    @XmlElementWrapper(name = "removedAttributes")
    @XmlElement(name = "i")
    private Set<String> removedAttributes;

    @Override
    @Deprecated
    public void load(EntityMappings entityMappings, TypeElement element, boolean fieldAccess) {
        this.setId(NBModelerUtil.getAutoGeneratedStringId());

        this.clazz = element.getSimpleName().toString();
        if (element.getModifiers().contains(Modifier.ABSTRACT)) {
            this.setAbstract(true);
        }
        for (TypeMirror mirror : element.getInterfaces()) {
            this.addInterface(new ReferenceClass(mirror.toString()));
        }
        this.setAnnotation(JavaSourceParserUtil.getNonEEAnnotation(element, ClassAnnotation.class));

        AnnotationMirror nillableAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, JSONB_NILLABLE_FQN);
        if (nillableAnnotationMirror != null) {
            this.jsonbNillable = (Boolean) JavaSourceParserUtil.findAnnotationValue(nillableAnnotationMirror, "value");
        }

        this.jsonbDateFormat = JsonbDateFormat.load(element);
        this.jsonbNumberFormat = JsonbNumberFormat.load(element);

        AnnotationMirror typeAdapterAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, JSONB_TYPE_ADAPTER_FQN);
        if (typeAdapterAnnotationMirror != null) {
            DeclaredType classType = (DeclaredType) JavaSourceParserUtil.findAnnotationValue(typeAdapterAnnotationMirror, "value");
            if (classType != null) {
                this.jsonbTypeAdapter = new ReferenceClass(classType.toString());
            }
        }

        AnnotationMirror typeDeserializerAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, JSONB_TYPE_DESERIALIZER_FQN);
        if (typeDeserializerAnnotationMirror != null) {
            DeclaredType classType = (DeclaredType) JavaSourceParserUtil.findAnnotationValue(typeDeserializerAnnotationMirror, "value");
            if (classType != null) {
                this.jsonbTypeDeserializer = new ReferenceClass(classType.toString());
            }
        }

        AnnotationMirror typeSerializerAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, JSONB_TYPE_SERIALIZER_FQN);
        if (typeSerializerAnnotationMirror != null) {
            DeclaredType classType = (DeclaredType) JavaSourceParserUtil.findAnnotationValue(typeSerializerAnnotationMirror, "value");
            if (classType != null) {
                this.jsonbTypeSerializer = new ReferenceClass(classType.toString());
            }
        }

        AnnotationMirror visibilityAnnotationMirror = JavaSourceParserUtil.findAnnotation(element, JSONB_VISIBILITY_FQN);
        if (visibilityAnnotationMirror != null) {
            DeclaredType classType = (DeclaredType) JavaSourceParserUtil.findAnnotationValue(visibilityAnnotationMirror, "value");
            if (classType != null) {
                this.jsonbVisibility = new ReferenceClass(classType.toString());
            }
        }

    }

    public void load(ClassExplorer clazz) {
        this.setId(NBModelerUtil.getAutoGeneratedStringId());

        this.clazz = clazz.getName();
        this.setAbstract(clazz.isAbstract());

        // ignore intrface and non-ee annotation
//        for (ClassOrInterfaceType interfaceType : clazz.getImplementedTypes()) {
//            this.addInterface(new ReferenceClass(interfaceType.getNameAsString())); //fqn ??
//        }
//        this.setAnnotation(JavaSourceParserUtil.getNonEEAnnotation(element, ClassAnnotation.class));

        this.jsonbDateFormat = JsonbDateFormat.load(clazz);

        this.jsonbNumberFormat = JsonbNumberFormat.load(clazz);

        clazz.getBooleanAttribute(javax.json.bind.annotation.JsonbNillable.class, "value")
                .ifPresent(this::setJsonbNillable);

        clazz.getReferenceClassAttribute(javax.json.bind.annotation.JsonbTypeAdapter.class, "value")
                .ifPresent(this::setJsonbTypeAdapter);

        clazz.getReferenceClassAttribute(javax.json.bind.annotation.JsonbTypeSerializer.class, "value")
                .ifPresent(this::setJsonbTypeSerializer);

        clazz.getReferenceClassAttribute(javax.json.bind.annotation.JsonbTypeDeserializer.class, "value")
                .ifPresent(this::setJsonbTypeDeserializer);

        clazz.getReferenceClassAttribute(javax.json.bind.annotation.JsonbVisibility.class, "value")
                .ifPresent(this::setJsonbVisibility);

    }

    public void beforeMarshal(Marshaller marshaller) {
        if (jsonbPropertyOrder != null && jsonbPropertyOrder.isEmpty()) {
            jsonbPropertyOrder = null;
        }
    }

    public abstract T getAttributes();

    public abstract void setAttributes(T attributes);

    /**
     * @return the annotation
     */
    public List<ClassAnnotation> getAnnotation() {
        if (annotation == null) {
            annotation = new ArrayList<>();
        }
        return annotation;
    }

    /**
     * @param annotation the annotation to set
     */
    public void setAnnotation(List<ClassAnnotation> annotation) {
        this.annotation = annotation;
    }

    public void addAnnotation(ClassAnnotation annotation_In) {
        getAnnotation().add(annotation_In);
    }

    public void removeAnnotation(ClassAnnotation annotation_In) {
        getAnnotation().remove(annotation_In);
    }

    /**
     * @return the runtimeAnnotation
     */
    public List<ClassAnnotation> getRuntimeAnnotation() {
        if (runtimeAnnotation == null) {
            runtimeAnnotation = new ArrayList<>();
        }
        return runtimeAnnotation;
    }

    /**
     * @param runtimeAnnotation the runtimeAnnotation to set
     */
    public void setRuntimeAnnotation(List<ClassAnnotation> runtimeAnnotation) {
        this.runtimeAnnotation = runtimeAnnotation;
    }

    public void addRuntimeAnnotation(ClassAnnotation runtimeAnnotation) {
        getRuntimeAnnotation().add(runtimeAnnotation);
    }

    public void removeRuntimeAnnotation(ClassAnnotation runtimeAnnotation) {
        getRuntimeAnnotation().remove(runtimeAnnotation);
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the superclass
     */
    public JavaClass<? extends IAttributes> getSuperclass() {
        return superclass;
    }

    public List<JavaClass<? extends IAttributes>> getAllSuperclass() {
        List<JavaClass<? extends IAttributes>> superclassList = new LinkedList<>();
        JavaClass<? extends IAttributes> parentClass = this.getSuperclass();
        while (true) {
            if (parentClass == null) {
                break;
            }
            superclassList.add(parentClass);
            parentClass = parentClass.getSuperclass();
        }
        return superclassList;
    }

    public List<Attribute> getSuperclassAttributes() {
        List<Attribute> attributes = Collections.<Attribute>emptyList();
        if (superclass != null && superclass instanceof IdentifiableClass) {
            attributes = ((IdentifiableClass) superclass).getAttributes().getAllAttribute();
            attributes.addAll(superclass.getSuperclassAttributes());
        }
        return attributes;
    }

    /**
     * @param superclass the superclass to set
     */
    public void addSuperclass(JavaClass superclass) {
        if (this.superclass == superclass) {
            return;
        }
        if (this.superclass != null) {
            throw new RuntimeException("JavaClass.addSuperclass > superclass is already exist [remove it first to add the new one]");
        }
        this.superclass = superclass;
        if (this.superclass != null) {
            this.superclassId = this.superclass.getId();
            this.superclass.addSubclass(this);
        } else {
            throw new RuntimeException("JavaClass.addSuperclass > superclass is null");
        }
    }

    public void removeSuperclass(JavaClass superclass) {

        if (superclass != null) {
            superclass.removeSubclass(this);
        } else {
            throw new RuntimeException("JavaClass.removeSuperclass > superclass is null");
        }
        this.superclassId = null;
        this.superclass = null;
    }

    /**
     * @return the subclassList
     */
    public Set<JavaClass> getSubclassList() {
        if (this.subclassList == null) {
            this.subclassList = new HashSet<>();
        }
        return subclassList;
    }

    /**
     * @param subclassList the subclassList to set
     */
    public void setSubclassList(Set<JavaClass> subclassList) {
        if (this.subclassList == null) {
            this.subclassList = new HashSet<>();
        }
        this.subclassList = subclassList;
    }

    public void addSubclass(JavaClass subclass) {
        if (this.subclassList == null) {
            this.subclassList = new HashSet<>();
        }
        this.subclassList.add(subclass);
    }

    public void removeSubclass(JavaClass subclass) {
        if (this.subclassList == null) {
            this.subclassList = new HashSet<>();
        }
        this.subclassList.remove(subclass);
    }

    /**
     * @return the superclassId
     */
    public String getSuperclassId() {
        return superclassId;
    }

    /**
     * @return the _abstract
     */
    public Boolean getAbstract() {
        return _abstract;
    }

    /**
     * @param _abstract the _abstract to set
     */
    public void setAbstract(Boolean _abstract) {
        this._abstract = _abstract;
    }

    public void addInterface(ReferenceClass _interface) {
        this.getInterfaces().add(_interface);
    }

    public void removeInterface(ReferenceClass _interface) {
        this.getInterfaces().remove(_interface);
    }

    /**
     * @return the interfaces
     */
    public Set<ReferenceClass> getInterfaces() {
        if (this.interfaces == null) {
            this.interfaces = new LinkedHashSet<>();
        }
        return interfaces;
    }

    /**
     * @param interfaces the interfaces to set
     */
    public void setInterfaces(Set<ReferenceClass> interfaces) {
        this.interfaces = interfaces;
    }

    public void addRuntimeInterface(ReferenceClass runtimeInterfaces) {
        this.getRuntimeInterfaces().add(runtimeInterfaces);
    }

    public void removeRuntimeInterface(ReferenceClass runtimeInterfaces) {
        this.getRuntimeInterfaces().remove(runtimeInterfaces);
    }

    /**
     * @return the runtimeInterfaces
     */
    public Set<ReferenceClass> getRuntimeInterfaces() {
        if (this.runtimeInterfaces == null) {
            this.runtimeInterfaces = new LinkedHashSet<>();
        }
        return runtimeInterfaces;
    }

    /**
     * @param runtimeInterfaces the runtimeInterfaces to set
     */
    public void setRuntimeInterfaces(Set<ReferenceClass> runtimeInterfaces) {
        this.runtimeInterfaces = runtimeInterfaces;
    }

    public String getPreviousClass() {
        return previousClass;
    }

    private void setPreviousClass(String previousClass) {
        this.previousClass = previousClass;
    }

    public void resetPreviousClass() {
        this.previousClass = null;
    }

    /**
     * Gets the value of the clazz property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setClazz(String value) {
        if (getPreviousClass() == null && getClazz() != null) {
            setPreviousClass(getClazz());
        }
        this.clazz = value;
    }

    /**
     * @return the fileObject
     */
    public FileObject getFileObject() {
        return fileObject;
    }

    public JavaSource getJavaSource() {
        if (fileObject == null || !fileObject.canWrite()) {
            return null;
        }
        JavaSource javaSource = JavaSource.forFileObject(fileObject);
        if (javaSource == null) {
            return null;
        }
        return javaSource;
    }

    /**
     * @param fileObject the fileObject to set
     */
    public void setFileObject(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    /**
     * @return the toStringMethod
     */
    public ClassMembers getToStringMethod() {
        if (toStringMethod == null) {
            toStringMethod = new ClassMembers();
        }
        return toStringMethod;
    }

    /**
     * @param toStringMethod the toStringMethod to set
     */
    public void setToStringMethod(ClassMembers toStringMethod) {
        this.toStringMethod = toStringMethod;
    }

    /**
     * @return the hashCodeMethod
     */
    public ClassMembers getHashCodeMethod() {
        if (hashCodeMethod == null) {
            hashCodeMethod = new ClassMembers();
        }
        return hashCodeMethod;
    }

    /**
     * @param hashCodeMethod the hashCodeMethod to set
     */
    public void setHashCodeMethod(ClassMembers hashCodeMethod) {
        this.hashCodeMethod = hashCodeMethod;
    }

    /**
     * @return the equalsMethod
     */
    public ClassMembers getEqualsMethod() {
        if (equalsMethod == null) {
            equalsMethod = new ClassMembers();
        }
        return equalsMethod;
    }

    /**
     * @param equalsMethod the equalsMethod to set
     */
    public void setEqualsMethod(ClassMembers equalsMethod) {
        this.equalsMethod = equalsMethod;
    }

    /**
     * @return the constructors
     */
    public List<Constructor> getConstructors() {
        if (constructors == null) {
            constructors = new ArrayList<>();
        }
        return constructors;
    }

    /**
     * @param constructors the constructors to set
     */
    public void setConstructors(List<Constructor> constructors) {
        this.constructors = constructors;
    }

    /**
     * @return the generateSourceCode
     */
    public Boolean getGenerateSourceCode() {
        if (generateSourceCode == null) {
            return true;
        }
        return generateSourceCode;
    }

    /**
     * @param generateSourceCode the generateSourceCode to set
     */
    public void setGenerateSourceCode(Boolean generateSourceCode) {
        if (generateSourceCode != false) {
            this.generateSourceCode = null;
        } else {
            this.generateSourceCode = false;
        }
        // default value will be true, store only for false
    }

    /**
     * @return the snippets
     */
    public List<ClassSnippet> getSnippets() {
        if (snippets == null) {
            snippets = new ArrayList<>();
        }
        return snippets;
    }

    /**
     * @param snippets the snippets to set
     */
    public void setSnippets(List<ClassSnippet> snippets) {
        this.snippets = snippets;
    }

    public boolean addSnippet(ClassSnippet snippet) {
        return getSnippets().add(snippet);
    }

    public boolean removeSnippet(ClassSnippet snippet) {
        return getSnippets().remove(snippet);
    }

    /**
     * @return the runtimeSnippets
     */
    public List<ClassSnippet> getRuntimeSnippets() {
        if (runtimeSnippets == null) {
            runtimeSnippets = new ArrayList<>();
        }
        return runtimeSnippets;
    }

    /**
     * @param runtimeSnippets the runtimeSnippets to set
     */
    public void setRuntimeSnippets(List<ClassSnippet> runtimeSnippets) {
        this.runtimeSnippets = runtimeSnippets;
    }

    public boolean addRuntimeSnippet(ClassSnippet snippet) {
        return getRuntimeSnippets().add(snippet);
    }

    public boolean removeRuntimeSnippet(ClassSnippet snippet) {
        return getRuntimeSnippets().remove(snippet);
    }

    /**
     * @return the runtimeTypeParameters
     */
    public List<String> getRuntimeTypeParameters() {
        if (runtimeTypeParameters == null) {
            runtimeTypeParameters = new ArrayList<>();
        }
        return runtimeTypeParameters;
    }

    /**
     * @param runtimeTypeParameters the runtimeTypeParameters to set
     */
    public void setRuntimeTypeParameters(List<String> runtimeTypeParameters) {
        this.runtimeTypeParameters = runtimeTypeParameters;
    }

    public boolean addRuntimeTypeParameter(String runtimeTypeParameter) {
        return getRuntimeTypeParameters().add(runtimeTypeParameter);
    }

    public boolean removeRuntimeTypeParameter(String runtimeTypeParameter) {
        return getRuntimeTypeParameters().remove(runtimeTypeParameter);
    }

    /**
     * @return the _package
     */
    public String getPackage() {
        return _package;
    }

    /**
     * @param rootPackage
     * @return the complete _package
     */
    public String getAbsolutePackage(String rootPackage) { // rootPackage.class_pkg
        return mergePackage(rootPackage, _package);
    }

    public String getRootPackage() { // project_pkg.entity_pkg
        return getAbsolutePackage(this.getRootElement().getPackage());
    }

    public String getRelativeRootPackage() { // entity_pkg
        return getAbsolutePackage(this.getRootElement().getEntityPackage());
    }

    public String getFQN() { // project_pkg.entity_pkg.class_pkg
        return mergePackage(getAbsolutePackage(this.getRootElement().getPackage()), getClazz());
    }

    public String getRelativeFQN() { // entity_pkg.class_pkg
        return mergePackage(getAbsolutePackage(this.getRootElement().getEntityPackage()), getClazz());
    }

    /**
     * @param _package the _package to set
     */
    public void setPackage(String _package) {
        this._package = _package;
    }

    /**
     * @return the superclassRef
     */
    public ReferenceClass getSuperclassRef() {
        return superclassRef;
    }

    /**
     * @param superclassRef the superclassRef to set
     */
    public void setSuperclassRef(ReferenceClass superclassRef) {
        this.superclassRef = superclassRef;
    }

    /**
     * @return the runtimeSuperclassRef
     */
    public ReferenceClass getRuntimeSuperclassRef() {
        return runtimeSuperclassRef;
    }

    /**
     * @param runtimeSuperclassRef the runtimeSuperclassRef to set
     */
    public void setRuntimeSuperclassRef(ReferenceClass runtimeSuperclassRef) {
        this.runtimeSuperclassRef = runtimeSuperclassRef;
    }


    /**
     * Gets the value of the description property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    public void updateArtifact(Attribute removedAttribute) {
        //constructor gc
        if (constructors != null) {
            Iterator<Constructor> itr = constructors.iterator();
            while (itr.hasNext()) {
                Constructor constructor = itr.next();
                if (constructor.getAttributes().size() > 0) {
                    constructor.removeAttribute(removedAttribute);
                    if (constructor.getAttributes().isEmpty()) {
                        itr.remove();
                    }
                }
            }
        }
        if (equalsMethod != null) {
            equalsMethod.removeAttribute(removedAttribute);
        }
        if (hashCodeMethod != null) {
            hashCodeMethod.removeAttribute(removedAttribute);
        }
        if (toStringMethod != null) {
            toStringMethod.removeAttribute(removedAttribute);
        }
    }

    /**
     * @return the jsonbNillable
     */
    public Boolean getJsonbNillable() {
        if (jsonbNillable == null) {
            jsonbNillable = false;
        }
        return jsonbNillable;
    }

    /**
     * @param jsonbNillable the jsonbNillable to set
     */
    public void setJsonbNillable(Boolean jsonbNillable) {
        this.jsonbNillable = jsonbNillable;
    }

    /**
     * @return the jsonbTypeAdapter
     */
    @Override
    public ReferenceClass getJsonbTypeAdapter() {
        return jsonbTypeAdapter;
    }

    /**
     * @param jsonbTypeAdapter the jsonbTypeAdapter to set
     */
    @Override
    public void setJsonbTypeAdapter(ReferenceClass jsonbTypeAdapter) {
        this.jsonbTypeAdapter = jsonbTypeAdapter;
    }

    /**
     * @return the jsonbDateFormat
     */
    public JsonbDateFormat getJsonbDateFormat() {
        if (jsonbDateFormat == null) {
            jsonbDateFormat = new JsonbDateFormat();
        }
        return jsonbDateFormat;
    }

    /**
     * @param jsonbDateFormat the jsonbDateFormat to set
     */
    public void setJsonbDateFormat(JsonbDateFormat jsonbDateFormat) {
        this.jsonbDateFormat = jsonbDateFormat;
    }

    /**
     * @return the jsonbNumberFormat
     */
    public JsonbNumberFormat getJsonbNumberFormat() {
        if (jsonbNumberFormat == null) {
            jsonbNumberFormat = new JsonbNumberFormat();
        }
        return jsonbNumberFormat;
    }

    /**
     * @param jsonbNumberFormat the jsonbNumberFormat to set
     */
    public void setJsonbNumberFormat(JsonbNumberFormat jsonbNumberFormat) {
        this.jsonbNumberFormat = jsonbNumberFormat;
    }

    /**
     * @return the jsonbTypeDeserializer
     */
    @Override
    public ReferenceClass getJsonbTypeDeserializer() {
        return jsonbTypeDeserializer;
    }

    /**
     * @param jsonbTypeDeserializer the jsonbTypeDeserializer to set
     */
    @Override
    public void setJsonbTypeDeserializer(ReferenceClass jsonbTypeDeserializer) {
        this.jsonbTypeDeserializer = jsonbTypeDeserializer;
    }

    /**
     * @return the jsonbTypeSerializer
     */
    @Override
    public ReferenceClass getJsonbTypeSerializer() {
        return jsonbTypeSerializer;
    }

    /**
     * @param jsonbTypeSerializer the jsonbTypeSerializer to set
     */
    @Override
    public void setJsonbTypeSerializer(ReferenceClass jsonbTypeSerializer) {
        this.jsonbTypeSerializer = jsonbTypeSerializer;
    }

    /**
     * @return the jsonbVisibility
     */
    @Override
    public ReferenceClass getJsonbVisibility() {
        return jsonbVisibility;
    }

    /**
     * @param jsonbVisibility the jsonbVisibility to set
     */
    @Override
    public void setJsonbVisibility(ReferenceClass jsonbVisibility) {
        this.jsonbVisibility = jsonbVisibility;
    }

    /**
     * @return the jsonbPropertyOrder
     */
    public List<Attribute> getJsonbPropertyOrder() {
        if (jsonbPropertyOrder == null) {
            jsonbPropertyOrder = new ArrayList<>();
        }
        return jsonbPropertyOrder;
    }

    /**
     * Removes all the deleted attributes
     *
     * @return
     */
    public List<Attribute> evalJsonbPropertyOrder() {
        Set<Attribute> attributesSet = new HashSet<>(getAttributes().getAllAttribute());
        List<Attribute> propertyOrder = getJsonbPropertyOrder();
        propertyOrder.removeIf(not(attributesSet::contains));
        return propertyOrder;
    }

    /**
     * Gets the manually added attribute to JsonbPropertyOrder + all remaining
     * attributes
     *
     * @return
     */
    public List<Attribute> getAllJsonbPropertyOrder() {
        List<Attribute> attributes = getAttributes().getAllAttribute();
        List<Attribute> propertyOrder = new ArrayList<>(evalJsonbPropertyOrder());
        attributes.removeAll(propertyOrder);
        propertyOrder.addAll(attributes);
        return propertyOrder;
    }

    /**
     * @param jsonbPropertyOrder the jsonbPropertyOrder to set
     */
    public void setJsonbPropertyOrder(List<Attribute> jsonbPropertyOrder) {
        this.jsonbPropertyOrder = jsonbPropertyOrder;
    }

    /**
     * @return the xmlRootElement
     */
    public Boolean getXmlRootElement() {
        return xmlRootElement;
    }

    /**
     * @param xmlRootElement the xmlRootElement to set
     */
    public void setXmlRootElement(Boolean xmlRootElement) {
        this.xmlRootElement = xmlRootElement;
    }

    public Set<String> getRemovedAttributes() {
        if (removedAttributes == null) {
            removedAttributes = new HashSet<>();
        }
        return removedAttributes;
    }

    public void removedAttribute(Attribute attribute) {
        getRemovedAttributes().add(
                nonNull(attribute.getPreviousName()) ? attribute.getPreviousName() : attribute.getName()
        );
    }

    public void resetRemovedAttributes() {
        this.removedAttributes = null;
    }

    private final static Set<String> AUTO_GEN_ENITY = new HashSet<>(asList(
            "User",
            "Authority",
            "AbstractAuditingEntity",
            "AuditListner"
    ));

    public static boolean isAutoGenerated(String entity) {
        return AUTO_GEN_ENITY.contains(entity);
    }
}
