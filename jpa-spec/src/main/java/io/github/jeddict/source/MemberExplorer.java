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

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LiteralStringValueExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.utils.Pair;
import io.github.jeddict.bv.constraints.Constraint;
import static io.github.jeddict.jcode.util.JavaIdentifiers.unqualify;
import io.github.jeddict.jpa.spec.EntityMappings;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.joining;

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

    public Set<Constraint> getTypeArgumentBeanValidationConstraints(int index) {
        NodeWithAnnotations<? extends Annotation> nodeWithAnnotations = null;

        if (index < 0) {
            throw new IllegalStateException("index value must be positive");
        }
        index = index + 1;
        List<Node> childNodes = field.getElementType().getChildNodes();
        if (!childNodes.isEmpty() && childNodes.size() >= index
                && childNodes.get(index) instanceof NodeWithAnnotations) {
            nodeWithAnnotations = (NodeWithAnnotations) childNodes.get(index);
        }

        Set<Constraint> constraints = Collections.emptySet();
        if (nodeWithAnnotations != null) {
            constraints = getBeanValidationConstraints(
                    nodeWithAnnotations.getAnnotations()
                            .stream()
                            .map(AnnotationExplorer::new)
            );
        }

        return constraints;
    }

    public List<String> getTypeArguments() {
        List<String> args = new ArrayList<>();
        for (Pair<ResolvedTypeParameterDeclaration, ResolvedType> pair : getReferenceType().getTypeParametersMap()) {
            if (pair.b.isReferenceType()) {
                args.add(pair.b.asReferenceType().getQualifiedName());
            }
        }
        return args;
    }

    public Optional<ResolvedReferenceTypeDeclaration> getTypeArgumentDeclaration(int index) {
        if (index < 0) {
            throw new IllegalStateException("index value must be positive");
        }
        List<ResolvedReferenceTypeDeclaration> declarations = getTypeArgumentDeclarations();
        if (!declarations.isEmpty() && declarations.size() >= index) {
            return Optional.of(declarations.get(index));
        }
        return Optional.empty();
    }

    public List<ResolvedReferenceTypeDeclaration> getTypeArgumentDeclarations() {
        List<ResolvedReferenceTypeDeclaration> declarations = new ArrayList<>();
        for (Pair<ResolvedTypeParameterDeclaration, ResolvedType> pair : getReferenceType().getTypeParametersMap()) {
            if (pair.b.isReferenceType()) {
                declarations.add(pair.b.asReferenceType().getTypeDeclaration());
            } // isTypeVariable()asTypeParameter()
        }
        return declarations;
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
        if (field.getVariables().get(0).getChildNodes().size() == 3) {
            Node node = field.getVariables().get(0).getChildNodes().get(2);
            if (node instanceof Expression) { //FieldAccessExpr, MethodCallExpr, ObjectCreationExpr
                defaultValue = node.toString();
                Map<String, ImportDeclaration> imports = clazz.getImports();
                 String importList = imports.keySet()
                         .stream()
                        .filter(defaultValue::contains)
                        .map(imports::get)
                        .map(ImportDeclaration::getNameAsString)
                        .collect(joining(" ,\n"));
                defaultValue = importList.isEmpty() ? defaultValue : "[\n" + importList + "\n]\n" + defaultValue;
            } else if (node instanceof NodeWithSimpleName) {
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
