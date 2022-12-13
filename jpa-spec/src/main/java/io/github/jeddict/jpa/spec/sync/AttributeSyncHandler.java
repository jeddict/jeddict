/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ReferenceType;
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
import static io.github.jeddict.jcode.util.AttributeType.isBoolean;
import static io.github.jeddict.jcode.util.JavaIdentifiers.isFQN;
import static io.github.jeddict.jcode.util.JavaIdentifiers.unqualify;
import static io.github.jeddict.jcode.util.JavaUtil.getMethodName;
import static io.github.jeddict.jcode.util.JavaUtil.isAddMethod;
import static io.github.jeddict.jcode.util.JavaUtil.isBeanMethod;
import static io.github.jeddict.jcode.util.JavaUtil.isGetterMethod;
import static io.github.jeddict.jcode.util.JavaUtil.isHelperMethod;
import io.github.jeddict.jcode.util.StringHelper;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.AttributeAnnotation;
import io.github.jeddict.jpa.spec.extend.AttributeAnnotationLocationType;
import static io.github.jeddict.jpa.spec.extend.AttributeAnnotationLocationType.PROPERTY;
import io.github.jeddict.jpa.spec.extend.CollectionTypeHandler;
import static io.github.jeddict.settings.generate.GenerateSettings.getIntrospectionPrefix;
import io.github.jeddict.snippet.AttributeSnippet;
import io.github.jeddict.snippet.AttributeSnippetLocationType;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.ADD_HELPER;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.GETTER;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.GETTER_JAVADOC;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.GETTER_THROWS;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.IMPORT;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.POST_GETTER;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.POST_SETTER;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.PRE_ADD_HELPER;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.PRE_GETTER;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.PRE_REMOVE_HELPER;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.PRE_SETTER;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.PROPERTY_JAVADOC;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.REMOVE_HELPER;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.SETTER;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.SETTER_JAVADOC;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.SETTER_THROWS;
import io.github.jeddict.snippet.ClassSnippet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static io.github.jeddict.util.StringUtils.deleteWhitespace;
import static io.github.jeddict.util.StringUtils.isNotBlank;

/**
 *
 * @author jGauravGupta
 */
public class AttributeSyncHandler {

    private final Attribute attribute;

    private AttributeSyncHandler(Attribute attribute) {
        this.attribute = attribute;
    }

    public static AttributeSyncHandler getInstance(Attribute attribute) {
        return new AttributeSyncHandler(attribute);
    }

    public void syncExistingSnippet(String name, FieldDeclaration field, Map<String, ImportDeclaration> imports) {
        syncJavadoc(field.getComment(), PROPERTY_JAVADOC);
        syncAnnotation(field.getAnnotations(), PROPERTY, imports);
    }

    public void syncExistingSnippet(String name, MethodDeclaration method, Map<String, ImportDeclaration> imports) {
        String methodName = method.getNameAsString();
        if (isBeanMethod(methodName)) {
            boolean getterMethod = isGetterMethod(methodName);
            syncJavadoc(method.getComment(), getterMethod ? GETTER_JAVADOC : SETTER_JAVADOC);
            syncAnnotation(method.getAnnotations(), getterMethod ? AttributeAnnotationLocationType.GETTER : AttributeAnnotationLocationType.SETTER, imports);
            syncThrows(method, getterMethod, imports);
            syncMethodBody(name, method, getterMethod, imports);
        } else if (isHelperMethod(methodName)) {
            syncHelperMethodBody(name, method, isAddMethod(methodName), imports);
        }
    }

    private void syncJavadoc(Optional<Comment> commentOpt, AttributeSnippetLocationType locationType) {
        if (commentOpt.isPresent()
                && attribute.getSnippets(locationType).isEmpty()
                && attribute.getJavaClass().getSnippets().isEmpty()) {
            Comment comment = commentOpt.get();
            AttributeSnippet attributeSnippet = new AttributeSnippet();
            attributeSnippet.setLocationType(locationType);
            attributeSnippet.setValue(String.format("%" + comment.getBegin().get().column + "s%s", "", comment.toString()));
            if (attribute.getDescription() == null
                    || !attributeSnippet.getValue().contains(attribute.getDescription())) {
                attribute.addRuntimeSnippet(attributeSnippet);
            }
        }
    }

    private void syncThrows(MethodDeclaration method, boolean getterMethod, Map<String, ImportDeclaration> imports) {
        if (attribute.getSnippets(getterMethod ? GETTER_THROWS : SETTER_THROWS).isEmpty()) {
            for (ReferenceType thrownException : method.getThrownExceptions()) {
                String value = thrownException.toString();
                AttributeSnippet attributeSnippet = new AttributeSnippet(value, getterMethod ? GETTER_THROWS : SETTER_THROWS);
                attribute.addRuntimeSnippet(attributeSnippet);
                addImportSnippet(value, imports);
            }
        }
    }

    private void syncAnnotation(List<AnnotationExpr> annotationExprs, AttributeAnnotationLocationType locationType, Map<String, ImportDeclaration> imports) {
        for (AnnotationExpr annotationExpr : annotationExprs) {
            String annotationExprName = annotationExpr.getNameAsString();
            String annotationName;
            String annotationFQN;
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
                if (!attribute.getAnnotation()
                        .stream()
                        .filter(anot -> anot.getLocationType() == locationType)
                        .filter(anot -> anot.getName().contains(annotationName))
                        .findAny()
                        .isPresent()) {
                    attribute.addRuntimeAnnotation(new AttributeAnnotation(value, locationType));
                    addImportSnippet(value, imports);
                }
            }

        }
    }

    private void syncMethodBody(String name, MethodDeclaration method, boolean getterMethod, Map<String, ImportDeclaration> imports) {
        if (!method.getBody().isPresent()) {
            return;
        }
        List<AttributeSnippet> preSnippets = attribute.getSnippets(getterMethod ? PRE_GETTER : PRE_SETTER);
        List<AttributeSnippet> bodySnippets = attribute.getSnippets(getterMethod ? GETTER : SETTER);
        List<AttributeSnippet> postSnippets = attribute.getSnippets(getterMethod ? POST_GETTER : POST_SETTER);
        if (!preSnippets.isEmpty() || !bodySnippets.isEmpty() || !postSnippets.isEmpty()) {
            return;
        }

        boolean bodyMatched = false;
        BlockStmt block = method.getBody().get();
        AttributeSnippetLocationType locationType = getterMethod ? PRE_GETTER : PRE_SETTER;

        String intializerLines = null; // collection default impl type null check & intializer
        if (attribute instanceof CollectionTypeHandler
                && isNotBlank(((CollectionTypeHandler) attribute).getCollectionImplType())) {
            intializerLines = String.format("if\\(%s==null\\)\\{%s=new([A-Za-z]+)<>\\(\\);\\}", name, name);
        }

        List<String> bridgeLines = new ArrayList<>();
        if (getterMethod) {
            bridgeLines.add(deleteWhitespace(String.format("return %s;", name)));
            bridgeLines.add(deleteWhitespace(String.format("return this.%s;", name)));
        } else {
            bridgeLines.add(deleteWhitespace(String.format("this.%s = %s;", name, name)));
        }
        for (Node node : block.getChildNodes()) {
            if (locationType == PRE_GETTER
                    && intializerLines != null
                    && deleteWhitespace(node.toString()).matches(intializerLines)) {
                intializerLines = null;
                continue;
            }

            String[] statements = node.toString().split("\n");
            for (String statement : statements) {
                if (bridgeLines.contains(deleteWhitespace(statement))) {
                    locationType = getterMethod ? POST_GETTER : POST_SETTER;
                    bodyMatched = true;
                } else {
                    attribute.addRuntimeSnippet(new AttributeSnippet(statement, locationType));
                    addImportSnippet(statement, imports);
                }
            }
        }
        if(!bodyMatched) {
            attribute.addRuntimeSnippet(new AttributeSnippet(null, getterMethod ? GETTER : SETTER));
        }
    }

    private void syncHelperMethodBody(String name, MethodDeclaration method, boolean addMethod, Map<String, ImportDeclaration> imports) {
        if (!method.getBody().isPresent()) {
            return;
        }
        List<AttributeSnippet> preSnippets = attribute.getSnippets(addMethod ? PRE_ADD_HELPER : PRE_REMOVE_HELPER);
        List<AttributeSnippet> bodySnippets = attribute.getSnippets(addMethod ? ADD_HELPER : REMOVE_HELPER);
        if (!preSnippets.isEmpty() || !bodySnippets.isEmpty()) {
            return;
        }

        boolean bodyMatched = false;
        BlockStmt block = method.getBody().get();
        AttributeSnippetLocationType locationType = addMethod ? PRE_ADD_HELPER : PRE_REMOVE_HELPER;

        String bridgeLine = deleteWhitespace(String.format("%s().%s(%s);",
                getMethodName(getIntrospectionPrefix(isBoolean(attribute.getDataTypeLabel())), name),
                addMethod ? "add" : "remove",
                StringHelper.singularize(name)
        ));

        for (Node node : block.getChildNodes()) {
            String[] statements = node.toString().split("\n");
            for (String statement : statements) {
                if (bridgeLine.equals(deleteWhitespace(statement))) {
                    locationType = null;
                    bodyMatched = true;
                } else if (locationType != null) {
                    attribute.addRuntimeSnippet(new AttributeSnippet(statement, locationType));
                    addImportSnippet(statement, imports);
                }
            }
        }
        if(!bodyMatched) {
            attribute.addRuntimeSnippet(new AttributeSnippet(null, addMethod ? ADD_HELPER : REMOVE_HELPER));
        }
    }

    private void addImportSnippet(String snippet, Map<String, ImportDeclaration> imports) {
        imports.keySet()
                .stream()
                .filter(snippet::contains)
                .map(imports::get)
                .map(importClass -> new AttributeSnippet(importClass.getNameAsString(), IMPORT))
                .forEach(attribute::addRuntimeSnippet);
    }

}
