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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.WorkingCopy;
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
import static io.github.jeddict.jcode.util.JavaSourceHelper.getSimpleClassName;
import io.github.jeddict.jcode.util.StringHelper;
import static io.github.jeddict.jcode.jpa.JPAConstants.BASIC_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.COLUMN_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.ELEMENT_COLLECTION_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.EMBEDDABLE_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.EMBEDDED_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.EMBEDDED_ID_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.ENTITY_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.GENERATED_VALUE_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.ID_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.JOIN_COLUMNS_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.JOIN_COLUMN_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.MANY_TO_MANY_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.MANY_TO_ONE_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.MAPPED_SUPERCLASS_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.ONE_TO_MANY_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.ONE_TO_ONE_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.PERSISTENCE_PACKAGE;
import static io.github.jeddict.jcode.jpa.JPAConstants.TRANSIENT_FQN;
import static io.github.jeddict.jcode.jpa.JPAConstants.VERSION_FQN;
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.extend.annotation.Annotation;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.Utilities;

/**
 *
 * @author Gaurav Gupta
 */
public class JavaSourceParserUtil {
    
    private static final String CONSTANT_VAR = "^[A-Z_$][A-Z_$0-9]*$";

    public static String simpleClassName(String fqn) {
        int lastDot = fqn.lastIndexOf('.');
        return lastDot > 0 ? fqn.substring(lastDot + 1) : fqn;
    }

    public static String readResource(InputStream is, String encoding) throws IOException {
        // read the config from resource first
        StringBuilder sbuffer = new StringBuilder();
        String lineSep = System.getProperty("line.separator");//NOI18N
        BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
        String line = br.readLine();
        while (line != null) {
            sbuffer.append(line);
            sbuffer.append(lineSep);
            line = br.readLine();
        }
        br.close();
        return sbuffer.toString();
    }

    public static void createFile(FileObject target, String content, String encoding) throws IOException {
        FileLock lock = target.lock();
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(target.getOutputStream(lock), encoding));
            bw.write(content);
            bw.close();

        } finally {
            lock.releaseLock();
        }
    }

    public static boolean isFieldAccess(TypeElement clazz) {
        boolean fieldAccess = false;
        boolean accessTypeDetected = false;
        TypeElement typeElement = clazz;
        Name qualifiedName = typeElement.getQualifiedName();
        whileloop:
        while (typeElement != null) {
            if (isAnnotatedWith(typeElement, ENTITY_FQN) || isAnnotatedWith(typeElement, MAPPED_SUPERCLASS_FQN)) { // NOI18N
                for (Element element : typeElement.getEnclosedElements()) {
                    if (isAnnotatedWith(element, ID_FQN) || isAnnotatedWith(element, EMBEDDED_ID_FQN) || isAnnotatedWith(element, EMBEDDED_FQN)
                            || isAnnotatedWith(element, BASIC_FQN) || isAnnotatedWith(element, TRANSIENT_FQN)
                            || isAnnotatedWith(element, VERSION_FQN) || isAnnotatedWith(element, ELEMENT_COLLECTION_FQN)
                            || isAnnotatedWith(element, ONE_TO_MANY_FQN) || isAnnotatedWith(element, MANY_TO_MANY_FQN)
                            || isAnnotatedWith(element, ONE_TO_ONE_FQN) || isAnnotatedWith(element, MANY_TO_ONE_FQN)) {
                        if (ElementKind.FIELD == element.getKind()) {
                            fieldAccess = true;
                        }
                        accessTypeDetected = true;
                        break whileloop;
                    }
                }
            }
            typeElement = getSuperclassTypeElement(typeElement);
        }
        if (!accessTypeDetected) {
            Logger.getLogger(JavaSourceParserUtil.class.getName()).log(Level.WARNING, "Failed to detect correct access type for class: {0}", qualifiedName); // NOI18N
        }
        return fieldAccess;
    }

    public static boolean isAnnotatedWith(Element element, String annotationFqn) {
        return findAnnotation(element, annotationFqn) != null;
    }

    public static AnnotationMirror getAnnotation(Element element, String annotationFqn) {
        return findAnnotation(element, annotationFqn);
    }

    //"javax.persistence|javax.xml.bind.annotation"
    private static final Pattern JPA_PACKAGE_PATTERN = Pattern.compile(PERSISTENCE_PACKAGE);
    private static final Class[] BEAN_VALIDATION_REVENG_CLASS_LIST = new Class[]{
        AssertFalse.class, AssertTrue.class, 
        Null.class, NotNull.class, NotEmpty.class, NotBlank.class,
        Size.class, Pattern.class, Email.class,
        DecimalMax.class, DecimalMin.class,
        Max.class, Min.class, Digits.class, 
        Positive.class, PositiveOrZero.class, Negative.class, NegativeOrZero.class,
        Future.class, Past.class, FutureOrPresent.class, PastOrPresent.class,};
    private static final Map<String, Class<? extends Constraint>> SUPPORTED_BV_REVENG_CLASS_SET = new HashMap<>();

    static {
        for (Class<? extends Constraint> bvClass : BEAN_VALIDATION_REVENG_CLASS_LIST) {
            SUPPORTED_BV_REVENG_CLASS_SET.put(io.github.jeddict.jcode.bv.BeanVaildationConstants.BEAN_VAILDATION_PACKAGE + "." + bvClass.getSimpleName(), bvClass);
        }
    }

    public static Set<Constraint> getBeanValidation(Element element) {
        Set<Constraint> constraints = io.github.jeddict.jpa.spec.extend.Attribute.CONSTRAINTS_SUPPLIER.get();
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            String annotationQualifiedName = getAnnotationQualifiedName(annotationMirror);
            Class<? extends Constraint> bvClass = SUPPORTED_BV_REVENG_CLASS_SET.get(annotationQualifiedName);
            if (bvClass != null) {
                Constraint constraint = null;
                try {
                    constraint = bvClass.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    ex.printStackTrace();
                    // Ignore
                }
                if (constraint != null) {
                    constraint.load(annotationMirror);
                    constraints.add(constraint);
                }
            }
        }
        return constraints;
    }

    public static <T extends Annotation> List<T> getNonEEAnnotation(Element element, Class<? extends T> type) {
        List<T> annotations = new ArrayList<>();
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            String annotationQualifiedName = getAnnotationQualifiedName(annotationMirror);
            Matcher matcher = JPA_PACKAGE_PATTERN.matcher(annotationQualifiedName);
            if (!matcher.find()) {
                if (SUPPORTED_BV_REVENG_CLASS_SET.containsKey(annotationQualifiedName)) {
                    continue;//skip this annotation , already reveng in getBeanValidation()
                }
                T annotation;
                
                try {
                    annotation = type.newInstance();
                } catch(Exception e){
                    throw new IllegalStateException(e);
                }
                
//TODO parse annotation
//        Iterator itr = annotationMirror.getElementValues().entrySet().iterator();
//        while(itr.hasNext()){
//            Entry entry = (Entry)itr.next();
//            ExecutableElement executableElement = (ExecutableElement)entry.getKey();
//            AnnotationValue annotationValue = (AnnotationValue)entry.getValue();
//            AnnotationElement annotationElement = new AnnotationElement();
//            annotationElement.setName(executableElement.getSimpleName().toString());
//            annotationElement.setValue(annotationValue.getValue());
//            annotation.addAnnotationElement(annotationElement);
//        }
//        annotation.setName(annotationMirror.getAnnotationType().toString());
//        
                annotation.setName(annotationMirror.toString());
                annotations.add(annotation);
            }
        }
        return annotations;
    }

    public static AnnotationMirror findAnnotation(Element element, String annotationFqn) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            String annotationQualifiedName = getAnnotationQualifiedName(annotationMirror);
            if (annotationQualifiedName.equals(annotationFqn)) {
                return annotationMirror;
            }
        }
        return null;
    }

    public static String getAnnotationQualifiedName(AnnotationMirror annotationMirror) {
//     Iterator<Entry<? extends ExecutableElement, ? extends AnnotationValue>> elementValuesItr =  annotationMirror.getElementValues().entrySet().iterator();
        DeclaredType annotationDeclaredType = annotationMirror.getAnnotationType();
        TypeElement annotationTypeElement = (TypeElement) annotationDeclaredType.asElement();
        Name name = annotationTypeElement.getQualifiedName();
        return name.toString();
    }

    public static TypeElement getSuperclassTypeElement(TypeElement typeElement) {
        TypeElement superclass = null;
        TypeMirror superclassMirror = typeElement.getSuperclass();
        if (superclassMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType superclassDeclaredType = (DeclaredType) superclassMirror;
            Element superclassElement = superclassDeclaredType.asElement();
            if (superclassElement.getKind() == ElementKind.CLASS && (superclassElement instanceof TypeElement)) {
                superclass = (TypeElement) superclassElement;
            }
        }
        return superclass;
    }

    public static TypeElement getAttributeTypeElement(VariableElement variableElement) {
        TypeElement attribute = null;
        TypeMirror attributeMirror = variableElement.asType();
        if (attributeMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType attributeDeclaredType = (DeclaredType) attributeMirror;
            Element attributeElement = attributeDeclaredType.asElement();
            if (attributeElement.getKind() == ElementKind.CLASS && (attributeElement instanceof TypeElement)) {
                attribute = (TypeElement) attributeElement;
            }
        }
        return attribute;
    }

    public static TypeElement getCollectionTypeElement(VariableElement variableElement) {
        TypeElement attribute = null;
        TypeMirror attributeMirror = variableElement.asType();
        if (attributeMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType attributeDeclaredType = (DeclaredType) attributeMirror; //java.util.List<Address>
            DeclaredType attributeGenericType = (DeclaredType) attributeDeclaredType.getTypeArguments().get(0);//Address
            Element attributeElement = attributeGenericType.asElement();
            if (attributeElement.getKind() == ElementKind.CLASS && (attributeElement instanceof TypeElement)) {
                attribute = (TypeElement) attributeElement;
            }
        }
        return attribute;
    }
    
    public static TypeElement getTypeElement(DeclaredType attributeGenericType) {
        TypeElement attribute = null;
        Element attributeElement = attributeGenericType.asElement();
        if (attributeElement.getKind() == ElementKind.CLASS && (attributeElement instanceof TypeElement)) {
            attribute = (TypeElement) attributeElement;
        }
        return attribute;
    }

    public static String findAnnotationValueAsString(AnnotationMirror annotation, String annotationKey) {
        String value = null;
        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationMap = annotation.getElementValues();
        for (ExecutableElement key : annotationMap.keySet()) {
            if (annotationKey.equals(key.getSimpleName().toString())) {
                AnnotationValue annotationValue = annotationMap.get(key);
                value = annotationValue.getValue().toString();
                break;
            }
        }
        return value;
    }

    public static Object findAnnotationValue(AnnotationMirror annotation, String annotationKey) {
        Object value = null;
        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationMap = annotation.getElementValues();
        for (ExecutableElement key : annotationMap.keySet()) {
            if (annotationKey.equals(key.getSimpleName().toString())) {
                AnnotationValue annotationValue = annotationMap.get(key);
                value = annotationValue.getValue();
                break;
            }
        }
        return value;
    }

    public static List<AnnotationMirror> findNestedAnnotations(AnnotationMirror annotationMirror, String annotationFqn) {
        List<AnnotationMirror> result = new ArrayList<>();
        findNestedAnnotationsInternal(annotationMirror, annotationFqn, result);
        return result;
    }

    private static void findNestedAnnotationsInternal(Object object, String annotationFqn, List<AnnotationMirror> result) {
        Collection<? extends AnnotationValue> annotationValueCollection = null;
        if (object instanceof AnnotationMirror) {
            AnnotationMirror annotationMirror = (AnnotationMirror) object;
            String annotationQualifiedName = getAnnotationQualifiedName(annotationMirror);
            if (annotationQualifiedName.equals(annotationFqn)) {
                result.add(annotationMirror);
            } else {
                //prepare to recurse
                Map<? extends ExecutableElement, ? extends AnnotationValue> annotationMap = annotationMirror.getElementValues();
                annotationValueCollection = annotationMap.values();
            }
        } else if (object instanceof List) {
            //prepare to recurse
            annotationValueCollection = (Collection<? extends AnnotationValue>) object;
        }

        //recurse
        if (annotationValueCollection != null) {
            for (AnnotationValue annotationValue : annotationValueCollection) {
                Object value = annotationValue.getValue();
                findNestedAnnotationsInternal(value, annotationFqn, result);
            }
        }
    }

    public static String fieldFromClassName(String className) {
        boolean makeFirstLower = className.length() == 1 || (!Character.isUpperCase(className.charAt(1)));
        String candidate = makeFirstLower ? className.substring(0, 1).toLowerCase() + className.substring(1) : className;
        if (!Utilities.isJavaIdentifier(candidate)) {
            candidate += "1"; //NOI18N
        }
        return candidate;
    }

    public static String getPropNameFromMethod(String name) {
        //getABcd should be converted to ABcd, getFooBar should become fooBar
        //getA1 is "a1", getA_ is a_, getAB is AB
        //in case method doesn't start with "get" return name with brackets
        if (!name.startsWith("get") && !name.startsWith("set")) {  //NOI18N
            return name + "()";   //NOI18n
        }
        boolean makeFirstLower = name.length() < 5 || (!Character.isUpperCase(name.charAt(4)));
        return makeFirstLower ? name.substring(3, 4).toLowerCase() + name.substring(4) : name.substring(3);
    }
    
    public static List<Element> getElements(TypeElement typeElement, boolean fieldAccess){
        List<Element> elements = new ArrayList<>();
        if(!isLombokSupport(typeElement)){
            for (ExecutableElement method : JavaSourceParserUtil.getMethods(typeElement)) {
                try {
                    String methodName = method.getSimpleName().toString();
                    if (methodName.startsWith("get") || methodName.startsWith("is")) {
                        Element element;
                        VariableElement variableElement = JavaSourceParserUtil.guessField(method);
                        // skip processing if the method is not joined with field
                        // might be transient method or method implementation from some interface
                        if (variableElement == null) {
                            continue;
                        }
                        if (fieldAccess) {
                            element = variableElement;
                        } else {
                            element = method;
                        }
                        elements.add(element);
                    }
                } catch (TypeNotPresentException ex) {
                    //Ignore Erroneous variable Type : ClassA have relation with List<ClassB>. And ClassB does not exist on classpath 
                    //LOG TODO access to IO
                }
            }
        } else {
            //if no method available then add var element e.g in case of lombok
            for (VariableElement variableElement : JavaSourceParserUtil.getFields(typeElement)) {
                if(variableElement.toString().matches(CONSTANT_VAR)){//skip constant variable
                    continue;
                }
                elements.add(variableElement);
            }
        }
        return elements;
    }
    
    private static boolean isLombokSupport(TypeElement typeElement){
        return typeElement.getAnnotationMirrors()
                .stream()
                .map(Object::toString)
                .anyMatch(annot -> annot.contains("@lombok.Getter"));
    }

    public static boolean isEmbeddableClass(Element typeElement) {//TypeElement
        if (JavaSourceParserUtil.isAnnotatedWith(typeElement, EMBEDDABLE_FQN)) {
            return true;
        }
        return false;
    }
    
    public static Embeddable loadEmbeddableClass(EntityMappings entityMappings, Element element, VariableElement variableElement, DeclaredType embeddableClass) {
        Embeddable embeddableClassSpec;
        Optional<Embeddable> embeddableClassSpecOpt = entityMappings.findEmbeddable(getSimpleClassName(embeddableClass.toString()));
        if (!embeddableClassSpecOpt.isPresent()) {
            boolean fieldAccess = element == variableElement;
            embeddableClassSpec = new Embeddable();
            TypeElement embeddableTypeElement = getTypeElement(embeddableClass); 
            embeddableClassSpec.load(entityMappings, embeddableTypeElement, fieldAccess);
            entityMappings.addEmbeddable(embeddableClassSpec);
        } else {
            embeddableClassSpec = embeddableClassSpecOpt.get();
        }
        return embeddableClassSpec;
    }

    public static boolean isMappedSuperClass(Element typeElement) {//TypeElement
        if (JavaSourceParserUtil.isAnnotatedWith(typeElement, MAPPED_SUPERCLASS_FQN)) {
            return true;
        }
        return false;
    }

    public static boolean isEntityClass(Element typeElement) {//TypeElement
        if (JavaSourceParserUtil.isAnnotatedWith(typeElement, ENTITY_FQN)) {
            return true;
        }
        return false;
    }
    
    public static Entity loadEntityClass(EntityMappings entityMappings, Element element, VariableElement variableElement, DeclaredType entityClass) {
        Entity entityClassSpec;
        Optional<Entity> entityClassSpecOpt = entityMappings.findEntity(getSimpleClassName(entityClass.toString()));
        if (!entityClassSpecOpt.isPresent()) {
            boolean fieldAccess = element == variableElement;
            entityClassSpec = new Entity();
            TypeElement embeddableTypeElement = getTypeElement(entityClass);
            entityClassSpec.load(entityMappings, embeddableTypeElement, fieldAccess);
            entityMappings.addEntity(entityClassSpec);
        } else {
            entityClassSpec = entityClassSpecOpt.get();
        }
        return entityClassSpec;
    }

    public static boolean isNonEntityClass(TypeElement typeElement) {
        return !isEntityClass(typeElement) && !isMappedSuperClass(typeElement) && !isEmbeddableClass(typeElement);
    }

    public static int isRelationship(ExecutableElement method, boolean isFieldAccess) {
        Element element = isFieldAccess ? JavaSourceParserUtil.guessField(method) : method;
        if (element != null) {
            if (JavaSourceParserUtil.isAnnotatedWith(element, ONE_TO_ONE_FQN) || JavaSourceParserUtil.isAnnotatedWith(element, MANY_TO_ONE_FQN)) {
                return REL_TO_ONE;
            }
            if (JavaSourceParserUtil.isAnnotatedWith(element, ONE_TO_MANY_FQN) || JavaSourceParserUtil.isAnnotatedWith(element, MANY_TO_MANY_FQN)) {
                return REL_TO_MANY;
            }
        }
        return REL_NONE;
    }

    public static ExecutableElement getOtherSideOfRelation(CompilationController controller, ExecutableElement executableElement, boolean isFieldAccess) {
        TypeMirror passedReturnType = executableElement.getReturnType();
        if (TypeKind.DECLARED != passedReturnType.getKind() || !(passedReturnType instanceof DeclaredType)) {
            return null;
        }
        Types types = controller.getTypes();
        TypeMirror passedReturnTypeStripped = stripCollection((DeclaredType) passedReturnType, types);
        if (passedReturnTypeStripped == null) {
            return null;
        }
        TypeElement passedReturnTypeStrippedElement = (TypeElement) types.asElement(passedReturnTypeStripped);

        //try to find a mappedBy annotation element on the possiblyAnnotatedElement
        Element possiblyAnnotatedElement = isFieldAccess ? JavaSourceParserUtil.guessField(executableElement) : executableElement;
        String mappedBy = null;
        AnnotationMirror persistenceAnnotation = JavaSourceParserUtil.findAnnotation(possiblyAnnotatedElement, ONE_TO_ONE_FQN);  //NOI18N"
        if (persistenceAnnotation == null) {
            persistenceAnnotation = JavaSourceParserUtil.findAnnotation(possiblyAnnotatedElement, ONE_TO_MANY_FQN);  //NOI18N"
        }
        if (persistenceAnnotation == null) {
            persistenceAnnotation = JavaSourceParserUtil.findAnnotation(possiblyAnnotatedElement, MANY_TO_ONE_FQN);  //NOI18N"
        }
        if (persistenceAnnotation == null) {
            persistenceAnnotation = JavaSourceParserUtil.findAnnotation(possiblyAnnotatedElement, MANY_TO_MANY_FQN);  //NOI18N"
        }
        if (persistenceAnnotation != null) {
            mappedBy = JavaSourceParserUtil.findAnnotationValueAsString(persistenceAnnotation, "mappedBy");  //NOI18N
        }
        for (ExecutableElement method : JavaSourceParserUtil.getMethods(passedReturnTypeStrippedElement)) {
            if (mappedBy != null && mappedBy.length() > 0) {
                String tail = mappedBy.length() > 1 ? mappedBy.substring(1) : "";
                String getterName = "get" + mappedBy.substring(0, 1).toUpperCase() + tail;
                if (getterName.equals(method.getSimpleName().toString())) {
                    return method;
                }
            } else {
                TypeMirror iteratedReturnType = method.getReturnType();
                iteratedReturnType = stripCollection(iteratedReturnType, types);
                TypeMirror executableElementEnclosingType = executableElement.getEnclosingElement().asType();
                if (types.isSameType(executableElementEnclosingType, iteratedReturnType)) {
                    return method;
                }
            }
        }
        return null;
    }

    public static final int REL_NONE = 0;
    public static final int REL_TO_ONE = 1;
    public static final int REL_TO_MANY = 2;

    public static TypeMirror stripCollection(TypeMirror passedType, Types types) {
        if (TypeKind.DECLARED != passedType.getKind() || !(passedType instanceof DeclaredType)) {
            return passedType;
        }
        TypeElement passedTypeElement = (TypeElement) types.asElement(passedType);
        String passedTypeQualifiedName = passedTypeElement.getQualifiedName().toString();   //does not include type parameter info
        Class passedTypeClass = null;
        try {
            passedTypeClass = Class.forName(passedTypeQualifiedName);
        } catch (ClassNotFoundException e) {
            //just let passedTypeClass be null
        }
        if (passedTypeClass != null && Collection.class.isAssignableFrom(passedTypeClass)) {
            List<? extends TypeMirror> passedTypeArgs = ((DeclaredType) passedType).getTypeArguments();
            if (passedTypeArgs.isEmpty()) {
                return passedType;
            }
            return passedTypeArgs.get(0);
        }
        return passedType;
    }

    public static boolean isFieldOptionalAndNullable(ExecutableElement method, boolean fieldAccess) {
        boolean isFieldOptional = true;
        Boolean isFieldNullable;
        Element fieldElement = fieldAccess ? JavaSourceParserUtil.guessField(method) : method;
        if (fieldElement == null) {
            fieldElement = method;
        }
        String[] fieldAnnotationFqns = {MANY_TO_ONE_FQN, ONE_TO_ONE_FQN, BASIC_FQN};
        Boolean isFieldOptionalBoolean = findAnnotationValueAsBoolean(fieldElement, fieldAnnotationFqns, "optional");
        if (isFieldOptionalBoolean != null) {
            isFieldOptional = isFieldOptionalBoolean;
        }
        if (!isFieldOptional) {
            return false;
        }
        //field is optional
        fieldAnnotationFqns = new String[]{COLUMN_FQN, JOIN_COLUMN_FQN};
        isFieldNullable = findAnnotationValueAsBoolean(fieldElement, fieldAnnotationFqns, "nullable");
        if (isFieldNullable != null) {
            return isFieldNullable;
        }
        boolean result = true;
        AnnotationMirror fieldAnnotation = JavaSourceParserUtil.findAnnotation(fieldElement, JOIN_COLUMNS_FQN); //NOI18N
        if (fieldAnnotation != null) {
            //all joinColumn annotations must indicate nullable = false to return a false result
            List<AnnotationMirror> joinColumnAnnotations = JavaSourceParserUtil.findNestedAnnotations(fieldAnnotation, JOIN_COLUMN_FQN);
            for (AnnotationMirror joinColumnAnnotation : joinColumnAnnotations) {
                String columnNullableValue = JavaSourceParserUtil.findAnnotationValueAsString(joinColumnAnnotation, "nullable"); //NOI18N
                if (columnNullableValue != null) {
                    result = Boolean.parseBoolean(columnNullableValue);
                    if (result) {
                        break;  //one of the joinColumn annotations is nullable, so return true
                    }
                } else {
                    result = true;
                    break;  //one of the joinColumn annotations is nullable, so return true
                }
            }
        }
        return result;
    }

    private static Boolean findAnnotationValueAsBoolean(Element fieldElement, String[] fieldAnnotationFqns, String annotationKey) {
        Boolean isFieldXable = null;
        for (String fieldAnnotationFqn : fieldAnnotationFqns) {
            AnnotationMirror fieldAnnotation = JavaSourceParserUtil.findAnnotation(fieldElement, fieldAnnotationFqn); //NOI18N
            if (fieldAnnotation != null) {
                String annotationValueString = JavaSourceParserUtil.findAnnotationValueAsString(fieldAnnotation, annotationKey); //NOI18N
                if (annotationValueString != null) {
                    isFieldXable = Boolean.valueOf(annotationValueString);
                } else {
                    isFieldXable = Boolean.TRUE;
                }
                break;
            }
        }
        return isFieldXable;
    }

    /**
     * check if there is id in the entity
     *
     * @param typeElement
     * @return true if id is present
     */
    public static boolean haveId(final TypeElement clazz) {
        boolean idDetected = false;
        TypeElement typeElement = clazz;
        while (typeElement != null && !idDetected) {
            if (isAnnotatedWith(typeElement, ENTITY_FQN) || isAnnotatedWith(typeElement, MAPPED_SUPERCLASS_FQN)) { // NOI18N
                for (Element element : typeElement.getEnclosedElements()) {
                    if (isAnnotatedWith(element, ID_FQN) || isAnnotatedWith(element, EMBEDDED_ID_FQN)) {
                        idDetected = true;
                    }
                }
            }
            typeElement = getSuperclassTypeElement(typeElement);
        }
        if (!idDetected) {
            return false;//
        } else {
            return true;
        }
    }

    public static ExecutableElement getIdGetter(final boolean isFieldAccess, final TypeElement typeElement) {
        List<ExecutableElement> methods = JavaSourceParserUtil.getMethods(typeElement);
        for (ExecutableElement method : methods) {
            String methodName = method.getSimpleName().toString();
            if (methodName.startsWith("get")) { //only getter (for auto-gen pk)
                Element element = isFieldAccess ? JavaSourceParserUtil.guessField(method) : method;
                if (element != null) {
                    if (JavaSourceParserUtil.isAnnotatedWith(element, ID_FQN) || JavaSourceParserUtil.isAnnotatedWith(element, EMBEDDED_ID_FQN)) {
                        return method;
                    }
                }
            }
        }
        Logger.getLogger(JavaSourceParserUtil.class.getName()).log(Level.WARNING, "Cannot find ID getter in class: {0}", typeElement.getQualifiedName());
        return null;
    }

    public static boolean isGenerated(ExecutableElement method, boolean isFieldAccess) {
        Element element = isFieldAccess ? JavaSourceParserUtil.guessField(method) : method;
        if (element != null) {
            if (JavaSourceParserUtil.isAnnotatedWith(element, GENERATED_VALUE_FQN)) {
                return true;
            }
        }
        return false;
    }

    public static boolean exceptionsThrownIncludes(WorkingCopy workingCopy, String fqClass, String methodName, List<String> formalParamFqTypes, String exceptionFqClassMaybeIncluded) {
        List<String> exceptionsThrown = getExceptionsThrown(workingCopy, fqClass, methodName, formalParamFqTypes);
        for (String exception : exceptionsThrown) {
            if (exceptionFqClassMaybeIncluded.equals(exception)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getExceptionsThrown(WorkingCopy workingCopy, String fqClass, String methodName, List<String> formalParamFqTypes) {
        if (formalParamFqTypes == null) {
            formalParamFqTypes = Collections.<String>emptyList();
        }
        ExecutableElement desiredMethodElement = null;
        TypeElement suppliedTypeElement = workingCopy.getElements().getTypeElement(fqClass);
        TypeElement typeElement = suppliedTypeElement;
        whileloop:
        while (typeElement != null) {
            for (ExecutableElement methodElement : ElementFilter.methodsIn(typeElement.getEnclosedElements())) {
                if (methodElement.getSimpleName().contentEquals(methodName)) {
                    List<? extends VariableElement> formalParamElements = methodElement.getParameters();
                    //for now, just check sizes
                    if (formalParamElements.size() == formalParamFqTypes.size()) {
                        desiredMethodElement = methodElement;
                        break whileloop;
                    }
                }
            }
            typeElement = getSuperclassTypeElement(typeElement);
        }
        if (desiredMethodElement == null) {
            throw new IllegalArgumentException("Could not find " + methodName + " in " + fqClass);
        }
        List<String> result = new ArrayList<>();
        List<? extends TypeMirror> thrownTypes = desiredMethodElement.getThrownTypes();
        for (TypeMirror thrownType : thrownTypes) {
            if (thrownType.getKind() == TypeKind.DECLARED) {
                DeclaredType thrownDeclaredType = (DeclaredType) thrownType;
                TypeElement thrownElement = (TypeElement) thrownDeclaredType.asElement();
                String thrownFqClass = thrownElement.getQualifiedName().toString();
                result.add(thrownFqClass);
            } else {
                result.add(null);
            }
        }
        return result;
    }

    public static List<ExecutableElement> getMethods(TypeElement typeElement) {
        List<ExecutableElement> result = new LinkedList<>();
        result.addAll(ElementFilter.methodsIn(typeElement.getEnclosedElements()));
        return result;
    }

    /**
     * #5977 FIX fixed serialVersionUID in output reversed model (class)
     *
     * @author georgeeb <georgeeb@java.net>
     * @since Thu, 17 Apr 2014 15:04:13 +0000
     */
    public static List<VariableElement> getFields(TypeElement typeElement) {
        if(typeElement==null){
            return Collections.<VariableElement>emptyList();
        }
        List<VariableElement> result = new LinkedList<>();
        final List<VariableElement> fieldsIn = ElementFilter.fieldsIn(typeElement.getEnclosedElements());
        result.addAll(removeSerialVersionUid(fieldsIn));
        return result;
    }

    private static List<VariableElement> removeSerialVersionUid(List<VariableElement> fieldsIn) {
        List<VariableElement> result = new LinkedList<>();
        for (VariableElement variableElement : fieldsIn) {
            if (!variableElement.getSimpleName().toString().equals("serialVersionUID")) {
                result.add(variableElement);
            }
        }
        return result;
    }
    // Issue Fix #5977 End

    public static VariableElement guessField(ExecutableElement getter) {
        String name;
        String methodName = getter.getSimpleName().toString();
        if (methodName.startsWith("get")) {
            name = getter.getSimpleName().toString().substring(3);
        } else if (methodName.startsWith("is")) {
            name = getter.getSimpleName().toString().substring(2);
        } else {
            return null;
        }
        String guessFieldName = StringHelper.firstLower(name);
        TypeElement typeElement = (TypeElement) getter.getEnclosingElement();
        for (VariableElement variableElement : ElementFilter.fieldsIn(typeElement.getEnclosedElements())) {
            //BUG : handling of field name for reserved sql keyword e.g : _size
            if (variableElement.getSimpleName().charAt(0) == '_' && variableElement.getSimpleName().contentEquals('_' + guessFieldName)) {
                return variableElement;
            } else if (variableElement.getSimpleName().contentEquals(guessFieldName)) {
                return variableElement;
            }
        }
        Logger.getLogger(JavaSourceParserUtil.class.getName()).log(Level.WARNING, "Cannot detect the field associated with property: {0}", guessFieldName);
        return null;
    }

    /**
     * guess getter from var
     *
     * @param variableElement
     * @return
     */
    public static ExecutableElement guessGetter(VariableElement variableElement) {
        String name = variableElement.getSimpleName().toString();
        String guessGetterName = StringHelper.firstUpper(name);
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
        for (ExecutableElement executableElement : ElementFilter.methodsIn(typeElement.getEnclosedElements())) {
            if (executableElement.getSimpleName().contentEquals("get" + guessGetterName) || executableElement.getSimpleName().contentEquals("is" + guessGetterName)) {
                return executableElement;
            }
        }
        Logger.getLogger(JavaSourceParserUtil.class.getName()).log(Level.INFO, "Cannot detect getter associated with var: {0}", guessGetterName);
        return null;
    }

    public static class TypeInfo {

        private String rawType;
        private TypeInfo[] declaredTypeParameters;

        public String getRawType() {
            return rawType;
        }

        public TypeInfo[] getDeclaredTypeParameters() {
            return declaredTypeParameters;
        }

        public TypeInfo(String rawType) {
            if (rawType == null) {
                throw new IllegalArgumentException();
            }
            this.rawType = rawType;
        }

        public TypeInfo(String rawType, TypeInfo[] declaredTypeParameters) {
            if (rawType == null) {
                throw new IllegalArgumentException();
            }
            this.rawType = rawType;
            if (declaredTypeParameters == null || declaredTypeParameters.length == 0) {
                return;
            }
            this.declaredTypeParameters = declaredTypeParameters;
        }

        public TypeInfo(String rawType, String[] declaredTypeParamStrings) {
            if (rawType == null) {
                throw new IllegalArgumentException();
            }
            this.rawType = rawType;
            if (declaredTypeParamStrings == null || declaredTypeParamStrings.length == 0) {
                return;
            }
            this.declaredTypeParameters = TypeInfo.fromStrings(declaredTypeParamStrings);
        }

        public static TypeInfo[] fromStrings(String[] strings) {
            if (strings == null || strings.length == 0) {
                return null;
            }
            TypeInfo[] typeInfos = new TypeInfo[strings.length];
            for (int i = 0; i < strings.length; i++) {
                typeInfos[i] = new TypeInfo(strings[i]);
            }
            return typeInfos;
        }
    }

//    private static String getPersistenceVersion(Project project) throws IOException {
//        String version = Persistence.VERSION_1_0;
//        PersistenceScope persistenceScopes[] = PersistenceUtils.getPersistenceScopes(project);
//        if (persistenceScopes.length > 0) {
//            FileObject persXml = persistenceScopes[0].getPersistenceXml();
//            if (persXml != null) {
//                Persistence persistence = PersistenceMetadata.getDefault().getRoot(persXml);
//                version = persistence.getVersion();
//            }
//        }
//        return version;
//    }
//
//    static boolean isId(ExecutableElement method, boolean isFieldAccess) {
//        Element element = isFieldAccess ? JavaSourceParserUtil.guessField(method) : method;
//        if (element != null) {
//            if (JavaSourceParserUtil.isAnnotatedWith(element, ID_FQN) || JavaSourceParserUtil.isAnnotatedWith(element, EMBEDDED_ID_FQN)) { // NOI18N
//                return true;
//            }
//        }
//        return false;
//    }
}
