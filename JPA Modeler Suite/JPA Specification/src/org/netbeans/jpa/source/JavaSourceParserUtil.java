/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.Utilities;

/**
 *
 * @author Gaurav Gupta
 */
public class JavaSourceParserUtil {

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
            if (isAnnotatedWith(typeElement, "javax.persistence.Entity") || isAnnotatedWith(typeElement, "javax.persistence.MappedSuperclass")) { // NOI18N
                for (Element element : typeElement.getEnclosedElements()) {
                    if (isAnnotatedWith(element, "javax.persistence.Id") || isAnnotatedWith(element, "javax.persistence.EmbeddedId") || isAnnotatedWith(element, "javax.persistence.Embedded")
                            || isAnnotatedWith(element, "javax.persistence.Basic") || isAnnotatedWith(element, "javax.persistence.Transient")
                            || isAnnotatedWith(element, "javax.persistence.Version") || isAnnotatedWith(element, "javax.persistence.ElementCollection")
                            || isAnnotatedWith(element, "javax.persistence.OneToMany") || isAnnotatedWith(element, "javax.persistence.ManyToMany")
                            || isAnnotatedWith(element, "javax.persistence.OneToOne") || isAnnotatedWith(element, "javax.persistence.ManyToOne")) {
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

    public static AnnotationMirror getAnnotation(Element element, String annotationFqn) {//temp replica
        return findAnnotation(element, annotationFqn);
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
        List<AnnotationMirror> result = new ArrayList<AnnotationMirror>();
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

    public static boolean isEmbeddableClass(Element typeElement) {//TypeElement
        if (JavaSourceParserUtil.isAnnotatedWith(typeElement, "javax.persistence.Embeddable")) {
            return true;
        }
        return false;
    }

    public static boolean isMappedSuperClass(Element typeElement) {//TypeElement
        if (JavaSourceParserUtil.isAnnotatedWith(typeElement, "javax.persistence.MappedSuperclass")) {
            return true;
        }
        return false;
    }

    public static boolean isEntityClass(Element typeElement) {//TypeElement
        if (JavaSourceParserUtil.isAnnotatedWith(typeElement, "javax.persistence.Entity")) {
            return true;
        }
        return false;
    }

    public static boolean isNonEntityClass(TypeElement typeElement) {
        if (!isEntityClass(typeElement) && !isMappedSuperClass(typeElement) && !isEmbeddableClass(typeElement)) {
            return true;
        }
        return false;
    }

    public static int isRelationship(ExecutableElement method, boolean isFieldAccess) {
        Element element = isFieldAccess ? JavaSourceParserUtil.guessField(method) : method;
        if (element != null) {
            if (JavaSourceParserUtil.isAnnotatedWith(element, "javax.persistence.OneToOne") || JavaSourceParserUtil.isAnnotatedWith(element, "javax.persistence.ManyToOne")) {
                return REL_TO_ONE;
            }
            if (JavaSourceParserUtil.isAnnotatedWith(element, "javax.persistence.OneToMany") || JavaSourceParserUtil.isAnnotatedWith(element, "javax.persistence.ManyToMany")) {
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
        AnnotationMirror persistenceAnnotation = JavaSourceParserUtil.findAnnotation(possiblyAnnotatedElement, "javax.persistence.OneToOne");  //NOI18N"
        if (persistenceAnnotation == null) {
            persistenceAnnotation = JavaSourceParserUtil.findAnnotation(possiblyAnnotatedElement, "javax.persistence.OneToMany");  //NOI18N"
        }
        if (persistenceAnnotation == null) {
            persistenceAnnotation = JavaSourceParserUtil.findAnnotation(possiblyAnnotatedElement, "javax.persistence.ManyToOne");  //NOI18N"
        }
        if (persistenceAnnotation == null) {
            persistenceAnnotation = JavaSourceParserUtil.findAnnotation(possiblyAnnotatedElement, "javax.persistence.ManyToMany");  //NOI18N"
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
        String[] fieldAnnotationFqns = {"javax.persistence.ManyToOne", "javax.persistence.OneToOne", "javax.persistence.Basic"};
        Boolean isFieldOptionalBoolean = findAnnotationValueAsBoolean(fieldElement, fieldAnnotationFqns, "optional");
        if (isFieldOptionalBoolean != null) {
            isFieldOptional = isFieldOptionalBoolean.booleanValue();
        }
        if (!isFieldOptional) {
            return false;
        }
        //field is optional
        fieldAnnotationFqns = new String[]{"javax.persistence.Column", "javax.persistence.JoinColumn"};
        isFieldNullable = findAnnotationValueAsBoolean(fieldElement, fieldAnnotationFqns, "nullable");
        if (isFieldNullable != null) {
            return isFieldNullable.booleanValue();
        }
        //new ballgame
        boolean result = true;
        AnnotationMirror fieldAnnotation = JavaSourceParserUtil.findAnnotation(fieldElement, "javax.persistence.JoinColumns"); //NOI18N
        if (fieldAnnotation != null) {
            //all joinColumn annotations must indicate nullable = false to return a false result
            List<AnnotationMirror> joinColumnAnnotations = JavaSourceParserUtil.findNestedAnnotations(fieldAnnotation, "javax.persistence.JoinColumn");
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
        for (int i = 0; i < fieldAnnotationFqns.length; i++) {
            String fieldAnnotationFqn = fieldAnnotationFqns[i];
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
            if (isAnnotatedWith(typeElement, "javax.persistence.Entity") || isAnnotatedWith(typeElement, "javax.persistence.MappedSuperclass")) { // NOI18N
                for (Element element : typeElement.getEnclosedElements()) {
                    if (isAnnotatedWith(element, "javax.persistence.Id") || isAnnotatedWith(element, "javax.persistence.EmbeddedId")) {
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
        ExecutableElement[] methods = JavaSourceParserUtil.getMethods(typeElement);
        for (ExecutableElement method : methods) {
            String methodName = method.getSimpleName().toString();
            if (methodName.startsWith("get")) {
                Element element = isFieldAccess ? JavaSourceParserUtil.guessField(method) : method;
                if (element != null) {
                    if (JavaSourceParserUtil.isAnnotatedWith(element, "javax.persistence.Id") || JavaSourceParserUtil.isAnnotatedWith(element, "javax.persistence.EmbeddedId")) {
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
            if (JavaSourceParserUtil.isAnnotatedWith(element, "javax.persistence.GeneratedValue")) { // NOI18N
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
        List<String> result = new ArrayList<String>();
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

    /**
     * Returns all methods in class and its super classes which are entity
     * classes or mapped superclasses.
     */
//    public static ExecutableElement[] getEntityMethods(TypeElement entityTypeElement) {
//        List<ExecutableElement> result = new LinkedList<ExecutableElement>();
//        TypeElement typeElement = entityTypeElement;
//        while (typeElement != null) {
//            if (isAnnotatedWith(typeElement, "javax.persistence.Entity") || isAnnotatedWith(typeElement, "javax.persistence.MappedSuperclass")) { // NOI18N
//                result.addAll(ElementFilter.methodsIn(typeElement.getEnclosedElements()));
//            }
//            typeElement = getSuperclassTypeElement(typeElement);
//        }
//        return result.toArray(new ExecutableElement[result.size()]);
//    }
    public static ExecutableElement[] getMethods(TypeElement typeElement) {
        List<ExecutableElement> result = new LinkedList<ExecutableElement>();
        result.addAll(ElementFilter.methodsIn(typeElement.getEnclosedElements()));
        return result.toArray(new ExecutableElement[result.size()]);
    }

    public static VariableElement[] getFields(TypeElement typeElement) {
        List<VariableElement> result = new LinkedList<VariableElement>();
        result.addAll(ElementFilter.fieldsIn(typeElement.getEnclosedElements()));
        return result.toArray(new VariableElement[result.size()]);
    }

    public static VariableElement guessField(ExecutableElement getter) {
        String name = getter.getSimpleName().toString().substring(3);
        String guessFieldName = name.substring(0, 1).toLowerCase() + name.substring(1);
        TypeElement typeElement = (TypeElement) getter.getEnclosingElement();
        for (VariableElement variableElement : ElementFilter.fieldsIn(typeElement.getEnclosedElements())) {
            if (variableElement.getSimpleName().contentEquals(guessFieldName)) {
                return variableElement;
            }
        }
        Logger.getLogger(JavaSourceParserUtil.class.getName()).log(Level.WARNING, "Cannot detect the field associated with property: {0}", guessFieldName);
        return null;
    }

    /**
     * TODO: actually it's guess setter from setter, need to review if it's a
     * problem of expected
     *
     * @param setter
     * @return
     */
    public static VariableElement guessGetter(ExecutableElement setter) {
        String name = setter.getSimpleName().toString().substring(3);
        String guessGetterName = "set" + name;
        TypeElement typeElement = (TypeElement) setter.getEnclosingElement();
        for (VariableElement variableElement : ElementFilter.fieldsIn(typeElement.getEnclosedElements())) {
            if (variableElement.getSimpleName().contentEquals(guessGetterName)) {
                return variableElement;
            }
        }
        Logger.getLogger(JavaSourceParserUtil.class.getName()).log(Level.INFO, "Cannot detect setter associated with getter: {0}", guessGetterName);
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
//            if (JavaSourceParserUtil.isAnnotatedWith(element, "javax.persistence.Id") || JavaSourceParserUtil.isAnnotatedWith(element, "javax.persistence.EmbeddedId")) { // NOI18N
//                return true;
//            }
//        }
//        return false;
//    }
}
