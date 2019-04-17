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
package io.github.jeddict.jpa.spec.sync;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import static io.github.jeddict.jcode.BeanVaildationConstants.BV_ANNOTATIONS;
import static io.github.jeddict.jcode.BeanVaildationConstants.BV_CONSTRAINTS_PACKAGE;
import static io.github.jeddict.jcode.JAXBConstants.JAXB_ANNOTATIONS;
import static io.github.jeddict.jcode.JAXBConstants.JAXB_PACKAGE;
import static io.github.jeddict.jcode.JPAConstants.JNOSQL_ANNOTATIONS;
import static io.github.jeddict.jcode.JPAConstants.JPA_ANNOTATIONS;
import static io.github.jeddict.jcode.JPAConstants.NOSQL_PACKAGE;
import static io.github.jeddict.jcode.JPAConstants.PERSISTENCE_PACKAGE;
import static io.github.jeddict.jcode.JSONBConstants.JSONB_ANNOTATIONS;
import static io.github.jeddict.jcode.JSONBConstants.JSONB_PACKAGE;
import static io.github.jeddict.jcode.util.JavaIdentifiers.isFQN;
import static io.github.jeddict.jcode.util.JavaIdentifiers.unqualify;
import static io.github.jeddict.jcode.util.JavaUtil.getFieldName;
import static io.github.jeddict.jcode.util.JavaUtil.getFieldNameFromDelegatorMethod;
import static io.github.jeddict.jcode.util.JavaUtil.isBeanMethod;
import static io.github.jeddict.jcode.util.JavaUtil.isHelperMethod;
import io.github.jeddict.jcode.util.StringHelper;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.ClassAnnotation;
import io.github.jeddict.jpa.spec.extend.ClassAnnotationLocationType;
import static io.github.jeddict.jpa.spec.extend.ClassAnnotationLocationType.TYPE;
import io.github.jeddict.jpa.spec.extend.CollectionTypeHandler;
import io.github.jeddict.jpa.spec.extend.Constructor;
import io.github.jeddict.jpa.spec.extend.IAttributes;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.spec.extend.ReferenceClass;
import static io.github.jeddict.settings.generate.GenerateSettings.isGenerateFluentAPI;
import io.github.jeddict.snippet.AttributeSnippet;
import io.github.jeddict.snippet.AttributeSnippetLocationType;
import io.github.jeddict.snippet.ClassSnippet;
import io.github.jeddict.snippet.ClassSnippetLocationType;
import static io.github.jeddict.snippet.ClassSnippetLocationType.AFTER_CLASS;
import static io.github.jeddict.snippet.ClassSnippetLocationType.AFTER_FIELD;
import static io.github.jeddict.snippet.ClassSnippetLocationType.AFTER_METHOD;
import static io.github.jeddict.snippet.ClassSnippetLocationType.BEFORE_FIELD;
import static io.github.jeddict.snippet.ClassSnippetLocationType.BEFORE_PACKAGE;
import static io.github.jeddict.snippet.ClassSnippetLocationType.IMPORT;
import static io.github.jeddict.snippet.ClassSnippetLocationType.TYPE_JAVADOC;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import static java.util.Objects.nonNull;
import java.util.Set;
import java.util.TreeMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static io.github.jeddict.util.StringUtils.isNotBlank;
import static java.util.Objects.isNull;

/**
 *
 * @author jGauravGupta
 */
public class JavaClassSyncHandler {

    private final JavaClass<IAttributes> javaClass;

    private JavaClassSyncHandler(JavaClass<IAttributes> javaClass) {
        this.javaClass = javaClass;
    }

    public static JavaClassSyncHandler getInstance(JavaClass<IAttributes> javaClass) {
        return new JavaClassSyncHandler(javaClass);
    }

    private Map<String, Attribute> getPreviousAttributes() {
        return javaClass.getAttributes()
                .getAllAttribute()
                .stream()
                .filter(attr -> nonNull(attr.getPreviousName()))
                .collect(toMap(Attribute::getPreviousName, identity(), (a1, a2) -> a1));
    }

    private Map<String, ImportDeclaration> getImports(CompilationUnit cu) {
        return cu.getImports()
                .stream()
                .collect(toMap(importDec -> unqualify(importDec.getNameAsString()), identity(), (i1, i2) -> i1));
    }

    public void syncExistingSnippet(CompilationUnit existingSource) {
        Map<String, Attribute> attributes = javaClass.getAttributes().getAllAttributeMap();
        Map<String, Attribute> previousAttributes = getPreviousAttributes();
        Map<String, ImportDeclaration> imports = getImports(existingSource);
        NodeList<TypeDeclaration<?>> types = existingSource.getTypes();

        for (TypeDeclaration<?> type : types) {
            if (type.getNameAsString().equals(javaClass.getPreviousClass())
                    || type.getNameAsString().equals(javaClass.getClazz())) {
                ClassOrInterfaceDeclaration rootClass = (ClassOrInterfaceDeclaration) type;

                syncHeaderJavaDoc(type);
                syncTypeParameters(rootClass.getTypeParameters(), imports);
                syncExtendedTypes(rootClass.getExtendedTypes(), imports);
                syncImplementedTypes(rootClass.getImplementedTypes(), imports);
                syncAnnotations(rootClass.getAnnotations(), TYPE, imports);

                NodeList<BodyDeclaration<?>> members = rootClass.getMembers();

                BodyDeclaration<?> lastScannedMember = null;
                Attribute lastScannedAttribute = null;
                for (BodyDeclaration<?> member : members) {
                    if (member instanceof MethodDeclaration) {
                        MethodDeclaration method = (MethodDeclaration) member;
                        String methodName = method.getNameAsString();
                        if (isBeanMethod(methodName)) {
                            String attributeName = getFieldName(methodName);

                            if (javaClass.getRemovedAttributes().contains(attributeName)) {
                                continue; //ignore deleted attribute
                            }

                            Attribute previousAttribute = previousAttributes.get(attributeName);
                            Attribute attribute = attributes.get(attributeName);
                            if (previousAttribute != null) { //renamed
                                lastScannedMember = method;
                                lastScannedAttribute = previousAttribute;
                                AttributeSyncHandler
                                        .getInstance(previousAttribute)
                                        .syncExistingSnippet(attributeName, method, imports);
                            } else if (attribute != null) { // new or non-modified
                                lastScannedMember = method;
                                lastScannedAttribute = attribute;
                                AttributeSyncHandler
                                        .getInstance(attribute)
                                        .syncExistingSnippet(attributeName, method, imports);
                            } else {
                                syncMethodSnippet(lastScannedMember, lastScannedAttribute, method, imports);
                            }
                        } else if (methodName.equals("toString")
                                && method.getParameters().isEmpty()) {
                            if (javaClass.getToStringMethod().getAttributes().isEmpty()) {
                                syncMethodSnippet(method, imports);
                            }
                        } else if (methodName.equals("hashCode")
                                && method.getParameters().isEmpty()) {
                            if (javaClass.getHashCodeMethod().getAttributes().isEmpty()) {
                                syncMethodSnippet(method, imports);
                            }
                        } else if (methodName.equals("equals")
                                && method.getParameters().size() == 1
                                && method.getParameters().get(0).getTypeAsString().equals("Object")) {
                            if (javaClass.getEqualsMethod().getAttributes().isEmpty()) {
                                syncMethodSnippet(method, imports);
                            }
                        } else if (isHelperMethod(methodName)
                                && (method.getParameters().size() == 1 || method.getParameters().size() == 2)) { // delegator/helper method
                            String attributeName = getFieldNameFromDelegatorMethod(methodName);
                            String attributePluralName = StringHelper.pluralize(attributeName);

                            if (javaClass.getRemovedAttributes().contains(attributePluralName)
                                    || nonNull(previousAttributes.get(attributePluralName))
                                    || nonNull(attributes.get(attributePluralName))) {
                                attributeName = attributePluralName;
                            }

                            if (javaClass.getRemovedAttributes().contains(attributeName)) {
                                continue;
                            }
                            Attribute previousAttribute = previousAttributes.get(attributeName);
                            Attribute attribute = attributes.get(attributeName);

                            if (previousAttribute == null && attribute == null) { // if helper method field name is not plural
                                if (javaClass.getRemovedAttributes().contains(attributeName)) {
                                    continue;
                                }
                                previousAttribute = previousAttributes.get(attributeName);
                                attribute = attributes.get(attributeName);
                            }

                            if (previousAttribute != null
                                    && previousAttribute instanceof CollectionTypeHandler
                                    && isNotBlank(((CollectionTypeHandler) previousAttribute).getCollectionImplType())) { //renamed
                                lastScannedMember = method;
                                lastScannedAttribute = previousAttribute;
                                AttributeSyncHandler
                                        .getInstance(previousAttribute)
                                        .syncExistingSnippet(attributeName, method, imports);
                            } else if (attribute != null
                                    && attribute instanceof CollectionTypeHandler
                                    && isNotBlank(((CollectionTypeHandler) attribute).getCollectionImplType())) { // new or non-modified
                                lastScannedMember = method;
                                lastScannedAttribute = attribute;
                                AttributeSyncHandler
                                        .getInstance(attribute)
                                        .syncExistingSnippet(attributeName, method, imports);
                            } else {
                                syncMethodSnippet(lastScannedMember, lastScannedAttribute, method, imports);
                            }
                        } else if (isFluentMethod(method)) {
                            String attributeName = method.getParameter(0).getNameAsString();
                            Attribute previousAttribute = previousAttributes.get(attributeName);
                            Attribute attribute = attributes.get(attributeName);
                            if (previousAttribute == null && attribute == null) {
                                syncMethodSnippet(lastScannedMember, lastScannedAttribute, method, imports);
                            }
                        } else {
                            syncMethodSnippet(lastScannedMember, lastScannedAttribute, method, imports);
                        }
                    } else if (member instanceof FieldDeclaration) {
                        FieldDeclaration field = (FieldDeclaration) member;
                        String attributeName = field.getVariable(0).getNameAsString();

                        if (javaClass.getRemovedAttributes().contains(attributeName)) {
                            continue; //ignore deleted attribute
                        }

                        Attribute previousAttribute = previousAttributes.get(attributeName);
                        Attribute attribute = attributes.get(attributeName);
                        if (previousAttribute != null) { //renamed
                            lastScannedMember = field;
                            lastScannedAttribute = previousAttribute;
                            AttributeSyncHandler
                                    .getInstance(previousAttribute)
                                    .syncExistingSnippet(attributeName, field, imports);
                        } else if (attribute != null) { // new or non-modified
                            lastScannedMember = field;
                            lastScannedAttribute = attribute;
                            AttributeSyncHandler
                                    .getInstance(attribute)
                                    .syncExistingSnippet(attributeName, field, imports);
                        } else {
                            syncFieldSnippet(lastScannedMember, lastScannedAttribute, field, imports);
                        }
                    } else if (member instanceof ClassOrInterfaceDeclaration || member instanceof EnumDeclaration) {
                        syncInnerClassOrInterfaceOrEnumSnippet(lastScannedMember, lastScannedAttribute, member, imports);
                    } else if (member instanceof InitializerDeclaration) {
                        syncInitializationBlockSnippet(lastScannedMember, lastScannedAttribute, (InitializerDeclaration) member, imports);
                    } else if (member instanceof ConstructorDeclaration) {
                        syncConstructorSnippet((ConstructorDeclaration) member, imports);
                    } else {
                        System.out.println("member not supported " + type);
                    }
                }
            } else if (type instanceof ClassOrInterfaceDeclaration || type instanceof EnumDeclaration) {
                syncClassOrInterfaceOrEnumSnippet(type, imports);
            } else {
                System.out.println("member not supported " + type);
            }
        }
    }

    public boolean isFluentMethod(MethodDeclaration method) {
        return isGenerateFluentAPI()
                && method.getParameters().size() == 1
                && (method.getTypeAsString().equals(javaClass.getPreviousClass()) || method.getTypeAsString().equals(javaClass.getClazz()))
                && method.getNameAsString().toLowerCase().endsWith(method.getParameter(0).getNameAsString().toLowerCase());
    }

    private void syncHeader(Comment comment) {
        if (!javaClass.getSnippets(BEFORE_PACKAGE).isEmpty()) {
            return;
        }
        String value = comment.toString();
        javaClass.addRuntimeSnippet(new ClassSnippet(value, BEFORE_PACKAGE));
    }

    private void syncJavadoc(Comment comment) {
        if (!javaClass.getSnippets(TYPE_JAVADOC).isEmpty()) {
            return;
        }
        String value = comment.toString();
        if (javaClass.getDescription() == null || !value.contains(javaClass.getDescription())) {
            javaClass.addRuntimeSnippet(new ClassSnippet(value, TYPE_JAVADOC));
        }
    }

    private void syncHeaderJavaDoc(TypeDeclaration<?> type) {
        TreeMap<Integer, Comment> comments = new TreeMap<>();
        int packagePosition = 1;
        if (type.getParentNode().isPresent()) {
            Node parentNode = type.getParentNode().get();
            parentNode.getComment().ifPresent(comment -> comments.put(comment.getBegin().get().line, comment));
            for (Node node : parentNode.getChildNodes()) {
                if (node instanceof PackageDeclaration) {
                    PackageDeclaration packageDeclaration = (PackageDeclaration) node;
                    if (packageDeclaration.getBegin().isPresent()) {
                        packagePosition = packageDeclaration.getBegin().get().line;
                    }
                    if (packageDeclaration.getComment().isPresent()) {
                        Comment comment = packageDeclaration.getComment().get();
                        comments.put(comment.getBegin().get().line, comment);
                    }
                } else if (node instanceof Comment) {
                    Comment comment = (Comment) node;
                    comments.put(comment.getBegin().get().line, comment);
                }
            }
        }
        type.getComment().ifPresent(comment -> comments.put(comment.getBegin().get().line, comment));
        comments.headMap(packagePosition).values().forEach(this::syncHeader);
        comments.tailMap(packagePosition).values().forEach(this::syncJavadoc);
    }

    private void syncTypeParameters(List<TypeParameter> typeParameters, Map<String, ImportDeclaration> imports) {
        for (TypeParameter typeParameter : typeParameters) {
            String value = typeParameter.toString();
            javaClass.addRuntimeTypeParameter(value);
            syncImportSnippet(value, imports);;
        }
    }

    private void syncExtendedTypes(List<ClassOrInterfaceType> extendedTypes, Map<String, ImportDeclaration> imports) {
        if (extendedTypes.size() != 1) {
            return; // single extends is valid for entity
        }
        ClassOrInterfaceType extendedType = extendedTypes.get(0);
        String value = extendedType.toString();
        if (javaClass.getSuperclassRef() == null && javaClass.getSuperclass() == null) {
            javaClass.setRuntimeSuperclassRef(new ReferenceClass(value));
            syncImportSnippet(value, imports);;
        }
    }

    private void syncImplementedTypes(List<ClassOrInterfaceType> implementedTypes, Map<String, ImportDeclaration> imports) {
        Set<ReferenceClass> allInterfaces = new LinkedHashSet<>(javaClass.getRootElement().getInterfaces());
        allInterfaces.addAll(javaClass.getInterfaces());

        for (ClassOrInterfaceType implementedType : implementedTypes) {
            String implementedExprName = implementedType.getNameAsString();
            String implementedName;
            if (isFQN(implementedExprName)) {
                implementedName = unqualify(implementedExprName);
            } else {
                implementedName = implementedExprName;
            }

            String value = implementedType.toString();
            if (!allInterfaces
                    .stream()
                    .filter(inter -> inter.isEnable())
                    .filter(inter -> inter.getName().contains(implementedName))
                    .findAny()
                    .isPresent()) {
                javaClass.addRuntimeInterface(new ReferenceClass(value));
                syncImportSnippet(value, imports);;
            }
        }
    }

    private void syncAnnotations(List<AnnotationExpr> annotationExprs, ClassAnnotationLocationType locationType, Map<String, ImportDeclaration> imports) {
        for (AnnotationExpr annotationExpr : annotationExprs) {
            String annotationExprName = annotationExpr.getNameAsString();
            String annotationName;
            String annotationFQN;
            //TODO calculate using resolve type or find solution for static import ??
            if (isFQN(annotationExprName)) {
                annotationFQN = annotationExprName;
                annotationName = unqualify(annotationExprName);
            } else {
                annotationFQN = imports.containsKey(annotationExprName)
                        ? imports.get(annotationExprName).getNameAsString() : annotationExprName;
                annotationName = annotationExprName;
            }

            if (!annotationFQN.startsWith(PERSISTENCE_PACKAGE)
                    && !annotationFQN.startsWith(NOSQL_PACKAGE)
                    && !annotationFQN.startsWith(BV_CONSTRAINTS_PACKAGE)
                    && !annotationFQN.startsWith(JSONB_PACKAGE)
                    && !annotationFQN.startsWith(JAXB_PACKAGE)
                    && !JPA_ANNOTATIONS.contains(annotationFQN)
                    && !JNOSQL_ANNOTATIONS.contains(annotationFQN)
                    && !BV_ANNOTATIONS.contains(annotationFQN)
                    && !JSONB_ANNOTATIONS.contains(annotationFQN)
                    && !JAXB_ANNOTATIONS.contains(annotationFQN)) {

                String value = annotationExpr.toString();
                if (!javaClass.getAnnotation()
                        .stream()
                        .filter(anot -> anot.getLocationType() == locationType)
                        .filter(anot -> anot.getName().contains(annotationName))
                        .findAny()
                        .isPresent()) {
                    javaClass.addRuntimeAnnotation(new ClassAnnotation(value, locationType));
                    syncImportSnippet(value, imports);;
                }
            }

        }
    }

    private void syncFieldSnippet(FieldDeclaration field, Map<String, ImportDeclaration> imports) {
        if (isClassMemberSnippetExist()) {
            return;
        }
        syncClassSnippet(field.isStatic() ? BEFORE_FIELD : AFTER_FIELD, field.toString(), imports);
    }

    private void syncFieldSnippet(BodyDeclaration<?> lastScannedMember, Attribute lastScannedAttribute, FieldDeclaration field, Map<String, ImportDeclaration> imports) {
        if (lastScannedAttribute == null) {
            syncFieldSnippet(field, imports);
        } else {
            if (lastScannedMember instanceof MethodDeclaration) {
                syncAttributeSnippet(lastScannedAttribute, AttributeSnippetLocationType.AFTER_METHOD, field.toString(), imports);
            } else if (lastScannedMember instanceof FieldDeclaration) {
                syncAttributeSnippet(lastScannedAttribute, AttributeSnippetLocationType.AFTER_FIELD, field.toString(), imports);
            } else {
                syncFieldSnippet(field, imports);
            }
        }
    }

    private void syncInitializationBlockSnippet(InitializerDeclaration initializationBlock, Map<String, ImportDeclaration> imports) {
        if (isClassMemberSnippetExist()) {
            return;
        }
        syncClassSnippet(AFTER_FIELD, initializationBlock.toString(), imports);
    }

    private void syncInitializationBlockSnippet(BodyDeclaration<?> lastScannedMember, Attribute lastScannedAttribute, InitializerDeclaration initializationBlock, Map<String, ImportDeclaration> imports) {
        if (lastScannedAttribute == null) {
            syncInitializationBlockSnippet(initializationBlock, imports);
        } else {
            if (lastScannedMember instanceof MethodDeclaration) {
                syncAttributeSnippet(lastScannedAttribute, AttributeSnippetLocationType.AFTER_METHOD, initializationBlock.toString(), imports);
            } else if (lastScannedMember instanceof FieldDeclaration) {
                syncAttributeSnippet(lastScannedAttribute, AttributeSnippetLocationType.AFTER_FIELD, initializationBlock.toString(), imports);
            } else {
                syncInitializationBlockSnippet(initializationBlock, imports);
            }
        }
    }

    private void syncConstructorSnippet(ConstructorDeclaration constructor, Map<String, ImportDeclaration> imports) {
        String signature
                = constructor.getParameters()
                        .stream()
                        .map(Parameter::getTypeAsString)
                        .collect(joining(", "));
        if (!javaClass.getConstructors()
                .stream()
                .filter(Constructor::isEnable)
                .filter(cot -> cot.getSignature().equals(signature))
                .findAny()
                .isPresent()) {
            syncClassSnippet(AFTER_FIELD, constructor.toString(), imports);
        }
    }

    private void syncMethodSnippet(MethodDeclaration method, Map<String, ImportDeclaration> imports) {
        if (isClassMemberSnippetExist()) {
            return;
        }
        syncClassSnippet(AFTER_METHOD, method.toString(), imports);
    }

    private void syncMethodSnippet(BodyDeclaration<?> lastScannedMember, Attribute lastScannedAttribute, MethodDeclaration method, Map<String, ImportDeclaration> imports) {
        if (lastScannedAttribute == null) {
            syncMethodSnippet(method, imports);
        } else {
            if (lastScannedMember instanceof MethodDeclaration) {
                syncAttributeSnippet(lastScannedAttribute, AttributeSnippetLocationType.AFTER_METHOD, method.toString(), imports);
            } else if (lastScannedMember instanceof FieldDeclaration) {
                syncAttributeSnippet(lastScannedAttribute, AttributeSnippetLocationType.AFTER_FIELD, method.toString(), imports);
            } else {
                syncMethodSnippet(method, imports);
            }
        }
    }

    private void syncInnerClassOrInterfaceOrEnumSnippet(BodyDeclaration<?> member, Map<String, ImportDeclaration> imports) {
        if (isClassMemberSnippetExist()) {
            return;
        }
        syncClassSnippet(AFTER_METHOD, member.toString(), imports);
    }

    private void syncInnerClassOrInterfaceOrEnumSnippet(BodyDeclaration<?> lastScannedMember, Attribute lastScannedAttribute, BodyDeclaration<?> member, Map<String, ImportDeclaration> imports) {
        if (lastScannedAttribute == null) {
            syncInnerClassOrInterfaceOrEnumSnippet(member, imports);
        } else {
            if (lastScannedMember instanceof MethodDeclaration) {
                syncAttributeSnippet(lastScannedAttribute, AttributeSnippetLocationType.AFTER_METHOD, member.toString(), imports);
            } else if (lastScannedMember instanceof FieldDeclaration) {
                syncAttributeSnippet(lastScannedAttribute, AttributeSnippetLocationType.AFTER_FIELD, member.toString(), imports);
            } else {
                syncInnerClassOrInterfaceOrEnumSnippet(member, imports);
            }
        }
    }

    private void syncClassOrInterfaceOrEnumSnippet(TypeDeclaration<?> type, Map<String, ImportDeclaration> imports) {
        if (!javaClass.getSnippets(AFTER_CLASS).isEmpty()) {
            return;
        }
        syncClassSnippet(AFTER_CLASS, type.toString(), imports);
    }

    private void syncClassSnippet(ClassSnippetLocationType locationType, String snippet, Map<String, ImportDeclaration> imports) {
        syncImportSnippet(snippet, imports);
        javaClass.addRuntimeSnippet(new ClassSnippet(snippet, locationType));
    }

    private void syncAttributeSnippet(Attribute attribute, AttributeSnippetLocationType locationType, String snippet, Map<String, ImportDeclaration> imports) {
        syncImportSnippet(snippet, imports);
        attribute.addRuntimeSnippet(new AttributeSnippet(snippet, locationType));
    }

    private void syncImportSnippet(String snippet, Map<String, ImportDeclaration> imports) {
        imports.keySet()
                .stream()
                .filter(snippet::contains)
                .map(imports::get)
                .map(importClass -> new ClassSnippet(importClass.getNameAsString(), IMPORT))
                .forEach(javaClass::addRuntimeSnippet);
    }

    private boolean isClassMemberSnippetExist() {
        return !javaClass.getSnippets(ClassSnippetLocationType.DEFAULT).isEmpty()
                || !javaClass.getSnippets(ClassSnippetLocationType.BEFORE_FIELD).isEmpty()
                || !javaClass.getSnippets(ClassSnippetLocationType.AFTER_FIELD).isEmpty()
                || !javaClass.getSnippets(ClassSnippetLocationType.BEFORE_METHOD).isEmpty()
                || !javaClass.getSnippets(ClassSnippetLocationType.AFTER_METHOD).isEmpty()
                || !javaClass.getAttributeSnippets(AttributeSnippetLocationType.BEFORE_FIELD).isEmpty()
                || !javaClass.getAttributeSnippets(AttributeSnippetLocationType.AFTER_FIELD).isEmpty()
                || !javaClass.getAttributeSnippets(AttributeSnippetLocationType.BEFORE_METHOD).isEmpty()
                || !javaClass.getAttributeSnippets(AttributeSnippetLocationType.AFTER_METHOD).isEmpty();
    }
}
