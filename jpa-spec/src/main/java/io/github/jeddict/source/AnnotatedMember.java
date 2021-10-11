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
package io.github.jeddict.source;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import io.github.jeddict.bv.constraints.AssertFalse;
import io.github.jeddict.bv.constraints.AssertTrue;
import io.github.jeddict.bv.constraints.Constraint;
import io.github.jeddict.bv.constraints.DecimalMax;
import io.github.jeddict.bv.constraints.DecimalMin;
import io.github.jeddict.bv.constraints.Digits;
import io.github.jeddict.bv.constraints.Email;
import io.github.jeddict.bv.constraints.Future;
import io.github.jeddict.bv.constraints.FutureOrPresent;
import io.github.jeddict.bv.constraints.Max;
import io.github.jeddict.bv.constraints.Min;
import io.github.jeddict.bv.constraints.Negative;
import io.github.jeddict.bv.constraints.NegativeOrZero;
import io.github.jeddict.bv.constraints.NotBlank;
import io.github.jeddict.bv.constraints.NotEmpty;
import io.github.jeddict.bv.constraints.NotNull;
import io.github.jeddict.bv.constraints.Null;
import io.github.jeddict.bv.constraints.Past;
import io.github.jeddict.bv.constraints.PastOrPresent;
import io.github.jeddict.bv.constraints.Positive;
import io.github.jeddict.bv.constraints.PositiveOrZero;
import io.github.jeddict.bv.constraints.Size;
import io.github.jeddict.bv.constraints.Valid;
import static io.github.jeddict.jcode.BeanVaildationConstants.BV_CONSTRAINTS_PACKAGE;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.ReferenceClass;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 *
 * @author jGauravGupta
 */
public abstract class AnnotatedMember {

    protected static final Map<String, Class<? extends Constraint>> SUPPORTED_BV_REVENG_CLASS_SET = new HashMap<>();
    protected static final Map<String, Class<? extends Constraint>> SUPPORTED_BV_REVENG_SIMPLE_CLASS_SET = new HashMap<>();

    public static final Class[] BEAN_VALIDATION_REVENG_CLASS_LIST = new Class[]{
        Valid.class,
        AssertFalse.class, AssertTrue.class,
        Null.class, NotNull.class,
        NotEmpty.class, NotBlank.class,
        Size.class, Pattern.class, Email.class,
        DecimalMax.class, DecimalMin.class,
        Max.class, Min.class, Digits.class,
        Positive.class, PositiveOrZero.class,
        Negative.class, NegativeOrZero.class,
        Past.class, PastOrPresent.class,
        Future.class, FutureOrPresent.class
    };

    static {
        for (Class<? extends Constraint> bvClass : BEAN_VALIDATION_REVENG_CLASS_LIST) {
            SUPPORTED_BV_REVENG_CLASS_SET.put(BV_CONSTRAINTS_PACKAGE + "." + bvClass.getSimpleName(), bvClass);
            SUPPORTED_BV_REVENG_SIMPLE_CLASS_SET.put(bvClass.getSimpleName(), bvClass);
        }
    }

    public Set<Constraint> getBeanValidationConstraints() {
        return getBeanValidationConstraints(getAnnotations());
    }

    protected Set<Constraint> getBeanValidationConstraints(Stream<AnnotationExplorer> annotations) {
        Set<Constraint> constraints = Attribute.CONSTRAINTS_SUPPLIER.get();
        annotations.forEach(annotation -> {
            String annotationName = annotation.getName(); // ?? annotationQualifiedName
            Class<? extends Constraint> bvClass = SUPPORTED_BV_REVENG_SIMPLE_CLASS_SET.get(annotationName);
            if (bvClass != null) {
                Constraint constraint = null;
                try {
                    constraint = bvClass.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    ex.printStackTrace();
                    // Ignore
                }
                if (constraint != null) {
                    constraint.load(annotation);
                    constraints.add(constraint);
                }
            }
        });
        return constraints;
    }

    static Optional<Expression> getAttribute(AnnotationExpr annotationExpr, String attributeName) {
        for (Node node : annotationExpr.getChildNodes()) {
            if (node instanceof MemberValuePair) {
                MemberValuePair valuePair = (MemberValuePair) node;
                if (valuePair.getNameAsString().equals(attributeName)) {
                    return Optional.of(valuePair.getValue());
                }
            } else if (node instanceof Expression) {
                Expression valuePair = (Expression) node;
                if ("value".equals(attributeName)) {
                    return Optional.of(valuePair);
                }
            }
        }
        return Optional.empty();
    }

    public Stream<AnnotationExplorer> getAnnotations() {
        return getAnnotatedMember()
                .getAnnotations()
                .stream()
                .map(AnnotationExplorer::new);
    }

    public Optional<AnnotationExplorer> getAnnotation(Class<? extends Annotation> annotationClass) {
        return getAnnotatedMember()
                .getAnnotationByClass(annotationClass)
                .map(AnnotationExplorer::new);
    }

    public Stream<AnnotationExplorer> getRepeatableAnnotations(Class<? extends Annotation> annotationClass) {
        return getAnnotatedMember()
                .getAnnotations()
                .stream() // .getAnnotationByClass(annotationClass) ??
                .filter(a -> a.getName().getIdentifier().equals(annotationClass.getSimpleName()))
                .map(AnnotationExplorer::new);
    }

    // String type
    public Optional<String> getStringAttribute(Class<? extends Annotation> annotationClass, String attributeName) {
        return getAnnotatedMember()
                .getAnnotationByClass(annotationClass)
                .flatMap(exp -> getStringAttribute(exp, attributeName));
    }

    static Optional<String> getStringAttribute(AnnotationExpr annotationExpr, String attributeName) {
        Optional<Expression> expressionOpt = getAttribute(annotationExpr, attributeName);
        if (expressionOpt.isPresent()) {
            Expression expression = expressionOpt.get();
            if (expression.isStringLiteralExpr()) {
                return expression.toStringLiteralExpr()
                        .map(StringLiteralExpr::getValue);
            } else if (expression.isNameExpr()) {
                return expression.toNameExpr()
                        .map(NameExpr::getNameAsString);
            } else if (expression.isIntegerLiteralExpr()) {
                return expression.toIntegerLiteralExpr()
                        .map(IntegerLiteralExpr::asInt)
                        .map(String::valueOf);
            } else if (expression.isFieldAccessExpr() || expression.isBinaryExpr()) {
                return expressionOpt
                        .map(Expression::toString);
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return Optional.empty();
    }

    static List<String> getStringAttributes(AnnotationExpr annotationExpr, String attributeName) {
        List<String> values = new ArrayList<>();
        Optional<Expression> expOptional = getAttribute(annotationExpr, attributeName);
        if (expOptional.isPresent()) {
            Expression expression = expOptional.get();
            if (expression.isStringLiteralExpr()) {
                values.add(expression.asStringLiteralExpr().getValue());
            } else if (expression.isArrayInitializerExpr()) {
                List<Node> nodes = expression.asArrayInitializerExpr().getChildNodes();
                for (Node node : nodes) {
                    if (node instanceof StringLiteralExpr) {
                        values.add(((StringLiteralExpr) node).getValue());
                    } else {
                        values.add(node.toString());
                    }
                }
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return values;
    }

    // Boolean type
    public Optional<Boolean> getBooleanAttribute(Class<? extends Annotation> annotationClass, String attributeName) {
        return getAnnotatedMember()
                .getAnnotationByClass(annotationClass)
                .flatMap(exp -> getBooleanAttribute(exp, attributeName));
    }

    static Optional<Boolean> getBooleanAttribute(AnnotationExpr annotationExpr, String attributeName) {
        return getAttribute(annotationExpr, attributeName)
                .flatMap(exp -> exp.toBooleanLiteralExpr())
                .map(exp -> exp.getValue());
    }

    // Integer type
    public Optional<Integer> getIntAttribute(Class<? extends Annotation> annotationClass, String attributeName) {
        return getAnnotatedMember()
                .getAnnotationByClass(annotationClass)
                .flatMap(exp -> getIntAttribute(exp, attributeName));
    }

    static Optional<Integer> getIntAttribute(AnnotationExpr annotationExpr, String attributeName) {
        return getAttribute(annotationExpr, attributeName)
                .flatMap(exp -> exp.toIntegerLiteralExpr())
                .map(exp -> exp.asInt());
    }

    static Optional<Long> getLongAttribute(AnnotationExpr annotationExpr, String attributeName) {
        Optional<Expression> expressionOpt = getAttribute(annotationExpr, attributeName);
        if (expressionOpt.isPresent()) {
            Expression expression = expressionOpt.get();
            if (expression.isLongLiteralExpr()) {
                return expressionOpt.flatMap(exp -> exp.toLongLiteralExpr())
                        .map(exp -> exp.asLong());
            } else if (expression.isIntegerLiteralExpr()) {
                return expressionOpt.flatMap(exp -> exp.toIntegerLiteralExpr())
                        .map(exp -> exp.asInt())
                        .map(Long::valueOf);
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return Optional.empty();
    }

    // Enum type
    public Optional<String> getEnumAttribute(Class<? extends Annotation> annotationClass, String attributeName) {
        return getAnnotatedMember()
                .getAnnotationByClass(annotationClass)
                .flatMap(exp -> getEnumAttribute(exp, attributeName));
    }

    static Optional<String> getEnumAttribute(AnnotationExpr annotationExpr, String attributeName) {
        return getAttribute(annotationExpr, attributeName)
                .map(exp -> exp.isFieldAccessExpr() ? exp.asFieldAccessExpr() : exp.asNameExpr())
                .map(exp -> exp.getNameAsString());
    }

    static List<String> getEnumAttributes(AnnotationExpr annotationExpr, String attributeName) {
        List<String> values = emptyList();
        Optional<Expression> expressionOpt = getAttribute(annotationExpr, attributeName);
        if (expressionOpt.isPresent()) {
            Expression expression = expressionOpt.get();
            if (expression.isFieldAccessExpr()) {
                values = expressionOpt
                        .map(exp -> exp.asFieldAccessExpr())
                        .map(exp -> exp.getNameAsString())
                        .map(exp -> singletonList(exp))
                        .orElse(emptyList());
            } else if (expression.isNameExpr()) {
                values = expressionOpt
                        .map(exp -> exp.asNameExpr())
                        .map(exp -> exp.getNameAsString())
                        .map(exp -> singletonList(exp))
                        .orElse(emptyList());
            } else if (expression.isArrayInitializerExpr()) {
                values = new ArrayList<>();
                List<Node> nodes = expression.asArrayInitializerExpr().getChildNodes();
                for (Node node : nodes) {
                    if (node instanceof NodeWithSimpleName) {
                        values.add(((NodeWithSimpleName) node).getNameAsString());
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
            } else {
                throw new UnsupportedOperationException();
            }

        }
        return values;
    }

    // Class type
    public Optional<Type> getTypeClassAttribute(Class<? extends Annotation> annotationClass, String attributeName) {
        return getAnnotatedMember()
                .getAnnotationByClass(annotationClass)
                .flatMap(exp -> getTypeClassAttribute(exp, attributeName));
    }

    static Optional<Type> getTypeClassAttribute(AnnotationExpr annotationExpr, String attributeName) {
        return getAttribute(annotationExpr, attributeName)
                .map(Expression::asClassExpr)
                .map(ClassExpr::getType);
    }

    public Optional<ResolvedReferenceTypeDeclaration> getResolvedClassAttribute(Class<? extends Annotation> annotationClass, String attributeName) {
        return getAnnotatedMember()
                .getAnnotationByClass(annotationClass)
                .flatMap(exp -> getResolvedClassAttribute(exp, attributeName));
    }

    static Optional<ResolvedReferenceTypeDeclaration> getResolvedClassAttribute(AnnotationExpr annotationExpr, String attributeName) {
        return getTypeClassAttribute(annotationExpr, attributeName)
                .map(Type::resolve)
                .map(ResolvedType::asReferenceType)
                .flatMap(ResolvedReferenceType::getTypeDeclaration);
    }

    public Optional<String> getClassNameAttribute(Class<? extends Annotation> annotationClass, String attributeName) {
        return getAnnotatedMember()
                .getAnnotationByClass(annotationClass)
                .flatMap(exp -> getClassNameAttribute(exp, attributeName));
    }

    static Optional<String> getClassNameAttribute(AnnotationExpr annotationExpr, String attributeName) {
        try {
            return getResolvedClassAttribute(annotationExpr, attributeName)
                    .map(ResolvedTypeDeclaration::getQualifiedName);
        } catch (Exception e) {
            return getTypeClassAttribute(annotationExpr, attributeName)
                    .map(Type::toString);
        }
    }

    static List<String> getClassNameAttributes(AnnotationExpr annotationExpr, String attributeName) {
        List<String> values = new ArrayList<>();
        Optional<Expression> expOptional = getAttribute(annotationExpr, attributeName);
        if (expOptional.isPresent()) {
            Expression expression = expOptional.get();
            if (expression.isClassExpr()) {
                values.add(expression.asClassExpr().getTypeAsString());
            } else if (expression.isArrayInitializerExpr()) {
                for (Node node : expression.asArrayInitializerExpr().getChildNodes()) {
                    if (node instanceof ClassExpr) {
                        values.add(((ClassExpr) node).getTypeAsString());
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return values;
    }

    public Optional<ReferenceClass> getReferenceClassAttribute(Class<? extends Annotation> annotationClass, String attributeName) {
        return getAnnotatedMember()
                .getAnnotationByClass(annotationClass)
                .flatMap(exp -> getReferenceClassAttribute(exp, attributeName));
    }

    static Optional<ReferenceClass> getReferenceClassAttribute(AnnotationExpr annotationExpr, String attributeName) {
        return getClassNameAttribute(annotationExpr, attributeName)
                .map(ReferenceClass::new);
    }

    static Stream<ReferenceClass> getReferenceClassAttributes(AnnotationExpr annotationExpr, String attributeName) { // use getClassNameAttributes
        return getClassNameAttributes(annotationExpr, attributeName)
                .stream()
                .map(ReferenceClass::new);
    }

    // Annotation type
    static Optional<AnnotationExplorer> getAnnotationAttribute(AnnotationExpr annotationExpr, String attributeName) {
        return getAttribute(annotationExpr, attributeName)
                .map(exp -> exp.asAnnotationExpr())
                .map(AnnotationExplorer::new);
    }

    static Stream<AnnotationExplorer> getAnnotationAttributes(AnnotationExpr annotationExpr, String attributeName) {
        Stream<AnnotationExplorer> annotations = Stream.of();
        Optional<Expression> expressionOpt = getAttribute(annotationExpr, attributeName);
        if (expressionOpt.isPresent()) {
            Expression expression = expressionOpt.get();
            if (expression.isAnnotationExpr()) {
                annotations = expressionOpt
                        .map(exp -> exp.asAnnotationExpr())
                        .map(AnnotationExplorer::new)
                        .map(exp -> Stream.of(exp))
                        .orElse(Stream.of());
            } else if (expression.isArrayInitializerExpr()) {
                List<AnnotationExplorer> values = new ArrayList<>();
                List<Node> nodes = expression.asArrayInitializerExpr().getChildNodes();
                for (Node node : nodes) {
                    if (node instanceof AnnotationExpr) {
                        AnnotationExplorer annotation = new AnnotationExplorer((AnnotationExpr) node);
                        values.add(annotation);
                    } else if (node instanceof Comment) {
                        //ignore
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
                annotations = values.stream();
            } else {
                throw new UnsupportedOperationException();
            }

        }
        return annotations;
    }

    protected abstract NodeWithAnnotations<? extends Annotation> getAnnotatedMember();

    @Override
    public String toString() {
        return getAnnotatedMember().toString();
    }
}
