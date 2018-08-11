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
package io.github.jeddict.source;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.LiteralStringValueExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import static io.github.jeddict.jcode.util.JavaIdentifiers.unqualify;
import io.github.jeddict.jpa.spec.EntityMappings;
import java.lang.annotation.Annotation;
import java.util.List;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author jGauravGupta
 */
public class MemberExplorer extends AnnotatedMember {

    private BodyDeclaration<? extends Annotation> annotatedMember;
    private MethodDeclaration getter;
    private FieldDeclaration field;

    private final ClassExplorer clazz;

    public MemberExplorer(ClassExplorer clazz) {
        this.clazz = clazz;
    }

    public EntityMappings getEntityMapping() {
        return clazz.getEntityMappings();
    }

    public boolean isIncludeReference() {
        return clazz.isIncludeReference();
    }

    public SourceExplorer getSource() {
        return clazz.getSource();
    }

    public boolean isTransient() {
        return field.isTransient();
    }

    public String getFieldName() {
        return field.getVariable(0).getNameAsString();
    }

    public List<String> getTypeArguments() {
        return getReferenceType().getTypeParametersMap()
                .stream()
                .map(param -> param.b.asReferenceType().getQualifiedName())
                .collect(toList());
    }

    public List<ResolvedReferenceTypeDeclaration> getTypeArgumentDeclarations() {
        return getReferenceType().getTypeParametersMap()
                .stream()
                .map(param -> param.b.asReferenceType().getTypeDeclaration())
                .collect(toList());
    }

    public ResolvedReferenceTypeDeclaration getTypeDeclaration() {
        return getReferenceType().getTypeDeclaration();
    }

    public String getType() {
        String type;
        ResolvedType resolvedType = null;
        try {
            resolvedType = field.getElementType().resolve();
        } catch (UnsupportedOperationException ex) {
            System.out.println("UnsupportedOperationException : " + field);
        }
        if (resolvedType != null && resolvedType.isReferenceType()) {
            type = resolvedType.asReferenceType().getQualifiedName();
        } else if (resolvedType != null && resolvedType.isPrimitive()) {
            type = resolvedType.asPrimitive().describe();
        } else {
            type = field.getElementType().toString();
        }
        if (field.getVariable(0).getType().isArrayType()) {
            type = type + "[]";
        }
        return type;
    }

    public String getSimpleType() {
        return unqualify(getType());
    }

    public boolean isCollectionType() {
        Class classType = null;
        try {
            classType = Class.forName(getType());
        } catch (ClassNotFoundException ex) {
        }
        return classType != null && java.util.Collection.class.isAssignableFrom(classType);
    }

    public boolean isMapType() {
        Class classType = null;
        try {
            classType = Class.forName(getType());
        } catch (ClassNotFoundException ex) {
        }
        return classType != null && java.util.Map.class.isAssignableFrom(classType);
    }

    private ResolvedReferenceType getReferenceType() {
        return field.getElementType().resolve().asReferenceType();
    }

    public String getDefaultValue() {
        String defaultValue = null;
        // String(ClassOrInterfaceType) var() = value(NameExp);
        if (field.getVariables().get(0).getChildNodes().size() == 3) {
            Node node = field.getVariables().get(0).getChildNodes().get(2);
            if (node instanceof NodeWithSimpleName) {
                defaultValue = ((NodeWithSimpleName) node).getNameAsString();
            } else if (node instanceof LiteralStringValueExpr) {
                defaultValue = "'" + ((LiteralStringValueExpr) node).getValue() + "'";
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return defaultValue;
    }
    
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return annotatedMember.isAnnotationPresent(annotationClass);
    }

    @Override
    protected BodyDeclaration getAnnotatedMember() {
        return annotatedMember;
    }

    public void setAnnotatedMember(BodyDeclaration annotationMember) {
        this.annotatedMember = annotationMember;
    }

    public MethodDeclaration getGetter() {
        return getter;
    }

    public void setGetter(MethodDeclaration getter) {
        this.getter = getter;
    }

    public FieldDeclaration getField() {
        return field;
    }

    public void setField(FieldDeclaration field) {
        this.field = field;
    }

}
