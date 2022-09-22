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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import static io.github.jeddict.jcode.util.JavaIdentifiers.unqualify;
import static io.github.jeddict.jcode.util.JavaUtil.getFieldName;
import static io.github.jeddict.jcode.util.JavaUtil.isGetterMethod;
import static io.github.jeddict.jcode.util.JavaUtil.isSetterMethod;
import io.github.jeddict.jpa.spec.EntityMappings;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import java.util.Optional;
import java.util.function.Function;
import static java.util.function.Function.identity;
import static java.util.logging.Level.WARNING;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toMap;
import org.openide.util.Exceptions;

/**
 *
 * @author jGauravGupta
 */
public class ClassExplorer extends AnnotatedMember {

    private final TypeDeclaration type;
    private final Map<String, ImportDeclaration> imports;
    private Boolean fieldAccess;
    private final SourceExplorer source;
    private static final Logger LOG = Logger.getLogger(ClassExplorer.class.getName());

    public ClassExplorer(SourceExplorer source, CompilationUnit compilationUnit) {
        this.type = compilationUnit.getPrimaryType().orElseThrow(IllegalArgumentException::new);
        this.imports = compilationUnit.getImports().stream()
                .collect(toMap(importDec -> unqualify(importDec.getNameAsString()), identity()));
        this.source = source;
    }

    public ClassExplorer(SourceExplorer source, ClassOrInterfaceDeclaration clazz) {
        this.type = clazz;
        this.imports = emptyMap();
        this.source = source;
    }

    public EntityMappings getEntityMappings() {
        return source.getEntityMappings();
    }

    public boolean isIncludeReference() {
        return source.isIncludeReference();
    }

    public SourceExplorer getSource() {
        return source;
    }

    public String getName() {
        return type.getNameAsString();
    }

    public Optional<ResolvedReferenceTypeDeclaration> getSuperClass() {
        if (type instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) type;
            if (!clazz.getExtendedTypes().isEmpty()) {
                return clazz.getExtendedTypes().get(0).resolve().asReferenceType().getTypeDeclaration();
            }
        }
        return Optional.empty();
    }

    public Boolean isFieldAccess() {
        if (fieldAccess == null) {
            fieldAccess = findFieldAccess();
        }
        return fieldAccess;
    }

    public NodeList<Modifier> getModifiers() {
        return type.getModifiers();
    }

    public boolean isAbstract() {
        return type.getModifiers().contains(Modifier.abstractModifier());
    }

    public Collection<MemberExplorer> getMembers() {
        Map<String, MemberExplorer> members = new LinkedHashMap<>();
        Function<String, MemberExplorer> memberValue = (attributeName) -> {
            MemberExplorer classMember = members.get(attributeName);
            if (classMember == null) {
                classMember = new MemberExplorer(this);
                members.put(attributeName, classMember);
            }
            return classMember;
        };
        if (type instanceof ClassOrInterfaceDeclaration) {
            for (BodyDeclaration<?> member : ((ClassOrInterfaceDeclaration) type).getMembers()) {
                if (member instanceof FieldDeclaration) { //isLombokSupport ??
                    FieldDeclaration field = (FieldDeclaration) member;
                    String attributeName = field.getVariable(0).getNameAsString();
                    MemberExplorer classMember = memberValue.apply(attributeName);
                    if(field.getModifiers().contains(Modifier.staticModifier())) {
                        continue;
                    }
                    classMember.setField(field);
                    if (isFieldAccess() || classMember.getAnnotatedMember() == null) {
                        classMember.setAnnotatedMember(field);
                    }
                } else if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    String methodName = method.getNameAsString();
                    if (isGetterMethod(methodName)) {
                        String attributeName = getFieldName(methodName);
                        MemberExplorer classMember = memberValue.apply(attributeName);
                        classMember.setGetter(method);
                        if (!isFieldAccess()) {
                            classMember.setAnnotatedMember(method);
                        }
                    } else if (isSetterMethod(methodName)) {
                        String attributeName = getFieldName(methodName);
                        MemberExplorer classMember = memberValue.apply(attributeName);
                        classMember.setSetter(method);
                    }
                }
            }
        }

        List<MemberExplorer> memberExplorers = new ArrayList<>();
        for (MemberExplorer member : members.values()) {
            if (nonNull(member.getField()) && nonNull(member.getAnnotatedMember())) {
                memberExplorers.add(member);
            } else if (nonNull(member.getGetter()) && nonNull(member.getSetter())) {
                if (isNull(member.getAnnotatedMember())) {
                    member.setAnnotatedMember(member.getGetter());
                }
                memberExplorers.add(member);
            }
        }
        return memberExplorers;
    }

    private boolean findFieldAccess() {
        boolean fieldAccessValue = false;
        boolean accessTypeDetected = false;
        NodeList<BodyDeclaration<?>> members = type.getMembers();

        if (isEntity() || isMappedSuperclass() || isEmbeddable()) {
            for (BodyDeclaration<?> member : members) {
                if (member.isAnnotationPresent(jakarta.persistence.Id.class)
                        || member.isAnnotationPresent(jakarta.persistence.Basic.class)
                        || member.isAnnotationPresent(jakarta.persistence.Transient.class)
                        || member.isAnnotationPresent(jakarta.persistence.Version.class)
                        || member.isAnnotationPresent(jakarta.persistence.ElementCollection.class)
                        || member.isAnnotationPresent(jakarta.persistence.Embedded.class)
                        || member.isAnnotationPresent(jakarta.persistence.EmbeddedId.class)
                        || member.isAnnotationPresent(jakarta.persistence.OneToMany.class)
                        || member.isAnnotationPresent(jakarta.persistence.OneToOne.class)
                        || member.isAnnotationPresent(jakarta.persistence.ManyToMany.class)
                        || member.isAnnotationPresent(jakarta.persistence.ManyToOne.class)
                        || member.isAnnotationPresent(jakarta.persistence.Column.class)) {
                    if (member instanceof FieldDeclaration) {
                        fieldAccessValue = true;
                    }
                    accessTypeDetected = true;
                }
                if (accessTypeDetected) {
                    break;
                }
            }
        } else {
            if (type instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) type;
                if (!clazz.getExtendedTypes().isEmpty()) {
                    ClassOrInterfaceType parentClassType = clazz.getExtendedTypes().get(0);
                    String parentClassQualifiedName = parentClassType.resolve().asReferenceType().getQualifiedName();
                    try {
                        fieldAccessValue = getSource().createClass(parentClassQualifiedName)
                                .map(ClassExplorer::isFieldAccess)
                                .orElse(true);
                    } catch (FileNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                        fieldAccessValue = true;
                    }
                } else {
                    fieldAccessValue = true;
                }
            }
        }
        if (!accessTypeDetected) {
            LOG.log(WARNING, "Failed to detect correct access type for class: {0}", type.getName());
        }
        return fieldAccessValue;
    }

    public boolean isManagedClass() {
        return isEntity() || isMappedSuperclass() || isEmbeddable();
    }

    public boolean isMappedSuperclass() {
        return type.isAnnotationPresent(jakarta.persistence.MappedSuperclass.class);
    }

    public boolean isEntity() {
        return type.isAnnotationPresent(jakarta.persistence.Entity.class);
    }

    public boolean isEmbeddable() {
        return type.isAnnotationPresent(jakarta.persistence.Embeddable.class);
    }

    public boolean isClass() {
        return type.isClassOrInterfaceDeclaration()
                && !type.asClassOrInterfaceDeclaration().isInterface();
    }

    public boolean isInterface() {
        return type.isClassOrInterfaceDeclaration()
                && type.asClassOrInterfaceDeclaration().isInterface();
    }

    public boolean isEnum() {
        return type.isEnumDeclaration();
    }

    public Map<String, ImportDeclaration> getImports() {
        return imports;
    }

    @Override
    protected NodeWithAnnotations getAnnotatedMember() {
        return type;
    }

}
