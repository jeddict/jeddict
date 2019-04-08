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
package io.github.jeddict.jcode.util;

import io.github.jeddict.jcode.task.AbstractTask;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT;
import static io.github.jeddict.jcode.util.ProjectHelper.getJavaSource;
import static io.github.jeddict.jcode.util.ProjectHelper.getJavaSourceGroups;
import static io.github.jeddict.jcode.util.ProjectHelper.getTemplateProperties;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import io.github.jeddict.util.StringUtils;
import static io.github.jeddict.util.StringUtils.EMPTY;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.netbeans.modules.websvc.rest.model.api.RestConstants;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

/**
 *
 * @author PeterLiu
 */
public class JavaSourceHelper {

    static final String CLASS_TEMPLATE = "Templates/Classes/Class.java"; // NOI18N
    static final String INTERFACE_TEMPLATE = "Templates/Classes/Interface.java"; // NOI18N

    public static List<JavaSource> getJavaSources(Project project) {
        List<JavaSource> result = new ArrayList<>();
        SourceGroup[] groups = getJavaSourceGroups(project);

        for (SourceGroup group : groups) {
            FileObject root = group.getRootFolder();
            Enumeration<? extends FileObject> files = root.getData(true);

            while (files.hasMoreElements()) {
                FileObject fobj = files.nextElement();

                if (fobj.getExt().equals(JAVA_EXT)) {
                    JavaSource source = JavaSource.forFileObject(fobj);
                    result.add(source);
                }
            }
        }

        return result;
    }

    public static void saveSource(FileObject[] files) throws IOException {
        for (FileObject f : files) {
            try {
                DataObject dobj = DataObject.find(f);
                SaveCookie sc = dobj.getCookie(SaveCookie.class);
                if (sc != null) {
                    sc.save();
                }
            } catch (DataObjectNotFoundException dex) {
                // something really wrong but continue trying to save others
            }
        }
    }

    public static boolean isOfAnnotationType(AnnotationMirror am, String annotationType) {
        return annotationType.equals(am.toString().substring(1));
    }

    public static AnnotationMirror findAnnotation(List<? extends AnnotationMirror> anmirs, String annotationString) {
        for (AnnotationMirror am : anmirs) {
            if (isOfAnnotationType(am, annotationString)) {
                return am;
            }
        }
        return null;
    }

    public static boolean annotationHasAttributeValue(AnnotationMirror am, String attr, String value) {
        return value.equals(am.getElementValues().get(attr).getValue());
    }

    public static boolean annotationHasAttributeValue(AnnotationMirror am, String value) {
        if (am != null) {
            for (AnnotationValue av : am.getElementValues().values()) {
                if (value.equals(av.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static TypeElement getXmlRepresentationClass(TypeElement typeElement, String defaultSuffix) {
        List<ExecutableElement> methods = ElementFilter.methodsIn(typeElement.getEnclosedElements());
        for (ExecutableElement method : methods) {
            List<? extends AnnotationMirror> anmirs = method.getAnnotationMirrors();

            AnnotationMirror mirrorHttpMethod = findAnnotation(anmirs, RestConstants.GET);
            if (mirrorHttpMethod != null) {
                TypeMirror tm = method.getReturnType();
                if (tm != null && tm.getKind() == TypeKind.DECLARED) {
                    TypeElement returnType = (TypeElement) ((DeclaredType) tm).asElement();
                    if (returnType.getSimpleName().toString().endsWith(defaultSuffix)) {
                        return returnType;
                    }
                }
            }
        }
        return null;
    }

    public static String getSelectedPackageName(FileObject targetFolder) {
        Project project = FileOwnerQuery.getOwner(targetFolder);
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        String packageName = null;
        for (int i = 0; i < groups.length && packageName == null; i++) {
            packageName = FileUtil.getRelativePath(groups[i].getRootFolder(), targetFolder);
        }
        if (packageName != null) {
            packageName = packageName.replaceAll("/", ".");
        }
        return packageName + "";
    }

    public static FileObject getPackageFileObject(SourceGroup location, String pkgName, Project project) {
        String relativePkgName = pkgName.replace('.', '/');
        FileObject fileObject;
        fileObject = location.getRootFolder().getFileObject(relativePkgName);
        if (fileObject != null) {
            return fileObject;
        } else {
            File rootFile = FileUtil.toFile(location.getRootFolder());
            File pkg = new File(rootFile, relativePkgName);
            pkg.mkdirs();
            fileObject = location.getRootFolder().getFileObject(relativePkgName);
        }
        return fileObject;
    }

    public static void reformat(DataObject dob) {
        try {
            EditorCookie ec = dob.getLookup().lookup(EditorCookie.class);
            if (ec == null) {
                return;
            }

            final StyledDocument doc = ec.openDocument();
            final Reformat reformat = Reformat.get(doc);

            reformat.lock();
            try {
                NbDocument.runAtomicAsUser(doc, () -> {
                    try {
                        reformat.reformat(0, doc.getLength());
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                });
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                reformat.unlock();
                ec.saveDocument();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Returns the simple class for the passed fully-qualified class name.
     *
     * @param fqClassName full qualified class name
     * @return uqfClassName
     */
    public static String getSimpleClassName(String fqClassName) {
        if (fqClassName == null) {
            return EMPTY;
        }

        if (fqClassName.endsWith(".class")) {
            fqClassName = fqClassName.substring(0, fqClassName.lastIndexOf(".class"));
        }

        if (fqClassName.indexOf('.') == -1) {
            return fqClassName;
        }
        int genericComp = fqClassName.indexOf('<');
        if (genericComp != -1) {
            StringBuilder sb = new StringBuilder(JavaIdentifiers.unqualify(fqClassName.substring(0, genericComp).trim()));
            sb.append('<');
            String[] genericElements = fqClassName.substring(genericComp + 1, fqClassName.length() - 1).split(",");
            for (String genericElement : genericElements) {
                genericElement = genericElement.trim();
                if (genericElement.indexOf('.') != -1) {
                    genericElement = JavaIdentifiers.unqualify(genericElement);
                }
                sb.append(genericElement).append(',');
            }
            sb.setCharAt(sb.length() - 1, '>');
            return sb.toString();
        } else {
            return JavaIdentifiers.unqualify(fqClassName);
        }
    }

    /**
     * Returns the package name of the passed fully-qualified class name.
     *
     * @param fqClassName
     */
    public static String getPackageName(String fqClassName) {
        int dot = fqClassName.lastIndexOf("."); // NOI18N
        if (dot >= 0 && dot < fqClassName.length() - 1) {
            return fqClassName.substring(0, dot);
        } else {
            return ""; // NOI18N
        }
    }

    public static boolean isValidPackageName(String packageName) {
        return StringUtils.isNotBlank(packageName) && JavaIdentifiers.isValidPackageName(packageName);
    }

    private static Map<String, ?> TEMPLATE_PROPERTIES;

    public static String getAuthor() {
        if (TEMPLATE_PROPERTIES == null) {
            TEMPLATE_PROPERTIES = getTemplateProperties();
        }
        String author = (String) TEMPLATE_PROPERTIES.get("user");
        if (StringUtils.isBlank(author)) {
            author = System.getProperty("user.name");
        }
        return author;
    }

    public static ClassTree getTopLevelClassTree(CompilationController controller) {
        String className = controller.getFileObject().getName();

        CompilationUnitTree cu = controller.getCompilationUnit();
        if (cu != null) {
            List<? extends Tree> decls = cu.getTypeDecls();
            for (Tree decl : decls) {
                if (!TreeUtilities.CLASS_TREE_KINDS.contains(decl.getKind())) {
                    continue;
                }

                ClassTree classTree = (ClassTree) decl;

                if (classTree.getSimpleName().contentEquals(className) && classTree.getModifiers().getFlags().contains(Modifier.PUBLIC)) {
                    return classTree;
                }
            }
        }
        return null;
    }

    public static TypeElement getTopLevelClassElement(CompilationController controller) {
        ClassTree classTree = getTopLevelClassTree(controller);
        if (classTree == null) {
            return null;
        }
        Trees trees = controller.getTrees();
        TreePath path = trees.getPath(controller.getCompilationUnit(), classTree);

        return (TypeElement) trees.getElement(path);
    }

    public static List<VariableElement> getEnumVariableElements(Project project, String fqClassName) throws IOException {
        JavaSource javaSource = getJavaSource(project, fqClassName);
        if (javaSource == null) {
            throw new IOException();
        }
        TypeElement typeElement = getTypeElement(javaSource);
        return ElementFilter.fieldsIn(typeElement.getEnclosedElements());
    }

    public static TypeElement getTypeElement(JavaSource source) throws IOException {
        final TypeElement[] results = new TypeElement[1];

        source.runUserActionTask(new AbstractTask<CompilationController>() {

            public void run(CompilationController controller) throws IOException {
                controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                results[0] = getTopLevelClassElement(controller);
            }
        }, true);

        return results[0];
    }

    public static String getClassNameQuietly(JavaSource source) {
        try {
            return getClassName(source);
        } catch (IOException ioe) {
            Logger.getLogger(JavaSourceHelper.class.getName()).log(Level.WARNING, ioe.getLocalizedMessage());
        }
        return null;
    }

    public static String getClassName(JavaSource source) throws IOException {
        TypeElement te = getTypeElement(source);
        if (te != null) {
            return te.getSimpleName().toString();
        } else {
            return null;
        }
    }

    public static String getClassType(JavaSource source) throws IOException {
        return getTypeElement(source).getQualifiedName().toString();
    }

}
