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

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import io.github.jeddict.jpa.spec.extend.ReferenceClass;
import static io.github.jeddict.source.AnnotatedMember.*;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author jGauravGupta
 */
public class AnnotationExplorer {

    AnnotationExpr annotationExpr;

    public AnnotationExplorer(AnnotationExpr annotationExpr) {
        this.annotationExpr = annotationExpr;

    }

    public String getName() {
        return annotationExpr.getNameAsString();
    }

    public Stream<AnnotationExplorer> getAnnotationList(String attributeName) {
        return getAnnotationAttributes(annotationExpr, attributeName);
    }

    public Optional<AnnotationExplorer> getAnnotation(String attributeName) {
        return getAnnotationAttribute(annotationExpr, attributeName);
    }

    public Optional<String> getString(String attributeName) {
        return getStringAttribute(annotationExpr, attributeName);
    }

    public List<String> getStringList(String attributeName) {
        return getStringAttributes(annotationExpr, attributeName);
    }

    public Optional<Boolean> getBoolean(String attributeName) {
        return getBooleanAttribute(annotationExpr, attributeName);
    }

    public Optional<Integer> getInt(String attributeName) {
        return getIntAttribute(annotationExpr, attributeName);
    }

    public Optional<Long> getLong(String attributeName) {
        return getLongAttribute(annotationExpr, attributeName);
    }

    public Optional<ResolvedReferenceTypeDeclaration> getResolvedClass(String attributeName) {
        return getResolvedClassAttribute(annotationExpr, attributeName);
    }

    public Optional<String> getClassName(String attributeName) {
        try {
            return getResolvedClassAttribute(annotationExpr, attributeName)
                    .map(ResolvedTypeDeclaration::getQualifiedName);
        } catch (Exception e) {
            return getTypeClassAttribute(annotationExpr, attributeName)
                    .map(Type::toString);
        }
    }

    public Optional<ReferenceClass> getReferenceClass(String attributeName) {
        return getClassName(attributeName)
                .map(ReferenceClass::new);
    }

    public Stream<ReferenceClass> getClassList(String attributeName) {
        return getReferenceClassAttributes(annotationExpr, attributeName);
    }

    public List<String> getClassNameList(String attributeName) {
        try {
            return getClassNameAttributes(annotationExpr, attributeName);
        } catch (IllegalStateException ex) {
            if (ex.getMessage().contains("not an ArrayInitializerExpr")) {
                return singletonList(getClassName(attributeName).get());
            } else {
                throw ex;
            }
        }
    }

    public Optional<String> getEnum(String attributeName) {
        return getEnumAttribute(annotationExpr, attributeName);
    }

    public List<String> getEnumList(String attributeName) {
        return getEnumAttributes(annotationExpr, attributeName);
    }

    @Override
    public String toString() {
        return annotationExpr.toString();
    }

}
