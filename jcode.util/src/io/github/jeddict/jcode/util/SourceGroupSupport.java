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
package io.github.jeddict.jcode.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.queries.UnitTestForSourceQuery;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.SourceGroupModifier;
import org.netbeans.api.project.Sources;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT_SUFFIX;
import org.netbeans.modules.websvc.rest.spi.MiscUtilities;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

public class SourceGroupSupport {

    private SourceGroupSupport() {
    }

    public static SourceGroup[] getJavaSourceGroups(Project project) {
        Parameters.notNull("project", project); //NOI18N
        SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        Set<SourceGroup> testGroups = getTestSourceGroups(sourceGroups);
        List<SourceGroup> result = new ArrayList<>();
        for (SourceGroup sourceGroup : sourceGroups) {
            if (!testGroups.contains(sourceGroup)) {
                result.add(sourceGroup);
            }
        }
        return result.toArray(new SourceGroup[result.size()]);
    }

    public static Set<SourceGroup> getTestSourceGroups(Project project) {
        return getTestSourceGroups(project, true);
    }

    public static Set<SourceGroup> getTestSourceGroups(Project project, boolean create) {
        SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        Set<SourceGroup> testGroups = getTestSourceGroups(sourceGroups);
        if (testGroups.isEmpty() && create) {
            if (SourceGroupModifier.createSourceGroup(project, JavaProjectConstants.SOURCES_TYPE_JAVA, JavaProjectConstants.SOURCES_HINT_TEST) != null) {
                return getTestSourceGroups(project, false);
            } else {
                throw new IllegalStateException("Test Source group creation failed");
            }
        }
        return testGroups;
    }

    public static SourceGroup getTestSourceGroup(Project project) {
        return getTestSourceGroups(project, true).stream().findAny().get();
    }

//    public static Collection<Object> getTestTargets(final boolean sourceGroupsOnly) {
//
//        /*
//         * Idea:
//         * 1) Get all SourceGroups
//         * 2) For each SourceGroup, ask UnitTestForSourceQuery for its related
//         *    test SourceGroups
//         *
//         * Union of all SourceGroups returned by UnitTestForSourceQuery
//         * are the test SourceGroups.
//         */
//
//        /* .) get all SourceGroups: */
//        final SourceGroup[] sourceGroups = getJavaSourceGroups();
//        if (sourceGroups.length == 0) {
//            return Collections.<Object>emptyList();
//        }
//
//        /* .) */
//        createFoldersToSourceGroupsMap(sourceGroups);
//        Object testTargetsUnion[] = new Object[sourceGroups.length];
//        int size = 0;
//        for (int i = 0; i < sourceGroups.length; i++) {
//            Object[] testTargets = getTestTargets(sourceGroups[i],
//                                                  sourceGroupsOnly);
//            size = merge(testTargets, testTargetsUnion, size);
//        }
//
//        if (size != testTargetsUnion.length) {
//            testTargetsUnion = JUnitTestUtil.skipNulls(testTargetsUnion, new Object[0]);
//        }
//
//        return Collections.unmodifiableCollection(
//                      Arrays.asList(testTargetsUnion));
//    }
//   
    /**
     * Checks whether the folder identified by the given
     * <code>packageName</code> is writable or is in a writable parent directory
     * but does not exist yet.
     *
     * @param sourceGroup the source group of the folder; must not be null.
     * @param packageName the package to check; must not be null.
     * @return true if the folder is writable or can be created (i.e. the parent
     * folder, or the root folder of the given <code>sourceGroup</code> if there
     * is no other parent for the folder, is writable), false otherwise.
     */
    public static boolean isFolderWritable(SourceGroup sourceGroup, String packageName) {
        Parameters.notNull("sourceGroup", sourceGroup); //NOI18N
        Parameters.notNull("packageName", packageName); //NOI18N
        FileObject fo = getFolderForPackage(sourceGroup, packageName, false);

        while ((fo == null) && (packageName.lastIndexOf('.') != -1)) {
            packageName = packageName.substring(0, packageName.lastIndexOf('.'));
            fo = getFolderForPackage(sourceGroup, packageName, false);
        }
        return fo == null ? sourceGroup.getRootFolder().canWrite() : fo.canWrite();
    }

    public static SourceGroup findSourceGroupForFile(Project project, FileObject folder) {
        return findSourceGroupForFile(getJavaSourceGroups(project), folder);
    }

    /**
     * Gets the {@link SourceGroup} of the given <code>folder</code>.
     *
     * @param sourceGroups the source groups to search; must not be null.
     * @param folder the folder whose source group is to be get; must not be
     * null.
     * @return the source group containing the given <code>folder</code> or null
     * if not found.
     */
    public static SourceGroup findSourceGroupForFile(SourceGroup[] sourceGroups, FileObject folder) {
        for (int i = 0; i < sourceGroups.length; i++) {
            if (FileUtil.isParentOf(sourceGroups[i].getRootFolder(), folder)
                    || sourceGroups[i].getRootFolder().equals(folder)) {
                return sourceGroups[i];
            }
        }
        return null;
    }

    public static SourceGroup findSourceGroupForFile(FileObject file) {
        Parameters.notNull("file", file); //NOI18N
        Project project = FileOwnerQuery.getOwner(file);
        for (SourceGroup sourceGroup : getJavaSourceGroups(project)) {
            if (FileUtil.isParentOf(sourceGroup.getRootFolder(), file)) {
                return sourceGroup;
            }
        }
        return null;
    }

    /**
     * Converts the path of the given <code>folder</code> to a package name.
     *
     * @param sourceGroup the source group for the folder; must not be null.
     * @param folder the folder to convert; must not be null.
     * @return the package name of the given <code>folder</code>.
     */
    public static String getPackageForFolder(SourceGroup sourceGroup, FileObject folder) {
        Parameters.notNull("sourceGroup", sourceGroup); //NOI18N
        Parameters.notNull("folder", folder); //NOI18N

        String relative = FileUtil.getRelativePath(sourceGroup.getRootFolder(), folder);
        if (relative != null) {
            return relative.replace('/', '.'); // NOI18N
        } else {
            return ""; // NOI18N
        }
    }

    public static String getPackageForFolder(FileObject folder) {
        Project project = FileOwnerQuery.getOwner(folder);
        SourceGroup[] sources = ProjectUtils.getSources(project).getSourceGroups(
                JavaProjectConstants.SOURCES_TYPE_JAVA);
        SourceGroup sg = findSourceGroupForFile(sources, folder);
        if (sg != null) {
            return getPackageForFolder(sg, folder);
        } else {
            return "";          //NOI18N
        }
    }

    /**
     * Gets the folder representing the given <code>packageName</code>. If the
     * folder does not exists, it will be created.
     *
     * @param sourceGroup the source group of the package.
     * @param packageName the name of the package.
     * @return the folder representing the given package.
     */
    public static FileObject getFolderForPackage(SourceGroup sourceGroup, String packageName) throws IOException {
        return getFolderForPackage(sourceGroup, packageName, false);
    }

    /**
     * Gets the folder representing the given <code>packageName</code>.
     *
     * @param sourceGroup the source group of the package; must not be null.
     * @param packageName the name of the package; must not be null.
     * @param create specifies whether the folder should be created if it does
     * not exist.
     * @return the folder representing the given package or null if it was not
     * found.
     */
    public static FileObject getFolderForPackage(SourceGroup sourceGroup, String packageName, boolean create) {
        Parameters.notNull("sourceGroup", sourceGroup);
        return getFolderForPackage(sourceGroup.getRootFolder(), packageName, create);
    }

    public static FileObject getFolderForPackage(FileObject rootFile, String packageName, boolean create) {
        Parameters.notEmpty("packageName", packageName);
        Parameters.notNull("rootFile", rootFile);

        String relativePkgName = packageName.replace('.', '/');
        FileObject folder = rootFile.getFileObject(relativePkgName);
        if (folder != null) {
            return folder;
        } else if (create) {
            try {
                return FileUtil.createFolder(rootFile, relativePkgName);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    private static Map<FileObject, SourceGroup> createFoldersToSourceGroupsMap(final SourceGroup[] sourceGroups) {
        Map result;
        if (sourceGroups.length == 0) {
            result = Collections.EMPTY_MAP;
        } else {
            result = new HashMap(2 * sourceGroups.length, .5f);
            for (int i = 0; i < sourceGroups.length; i++) {
                SourceGroup sourceGroup = sourceGroups[i];
                result.put(sourceGroup.getRootFolder(), sourceGroup);
            }
        }
        return result;
    }

    private static Set<SourceGroup> getTestSourceGroups(SourceGroup[] sourceGroups) {
        Map<FileObject, SourceGroup> foldersToSourceGroupsMap = createFoldersToSourceGroupsMap(sourceGroups);
        Set<SourceGroup> testGroups = new HashSet<>();
        for (int i = 0; i < sourceGroups.length; i++) {
            testGroups.addAll(getTestTargets(sourceGroups[i], foldersToSourceGroupsMap));
        }
        return testGroups;
    }

    private static List<SourceGroup> getTestTargets(SourceGroup sourceGroup, Map<FileObject, SourceGroup> foldersToSourceGroupsMap) {
        final URL[] rootURLs = UnitTestForSourceQuery.findUnitTests(sourceGroup.getRootFolder());
        if (rootURLs.length == 0) {
            return Collections.emptyList();
        }
        List<SourceGroup> result = new ArrayList<>();
        List<FileObject> sourceRoots = getFileObjects(rootURLs);
        for (FileObject sourceRoot : sourceRoots) {
            SourceGroup srcGroup = foldersToSourceGroupsMap.get(sourceRoot);
            if (srcGroup != null) {
                result.add(srcGroup);
            }
        }
        return result;
    }

    private static List<FileObject> getFileObjects(URL[] urls) {
        List<FileObject> result = new ArrayList<>();
        for (URL url : urls) {
            FileObject sourceRoot = URLMapper.findFileObject(url);
            if (sourceRoot != null) {
                result.add(sourceRoot);
            } else {
                Logger.getLogger(SourceGroup.class.getName()).log(Level.INFO, "No FileObject found for the following URL: " + url);
            }
        }
        return result;
    }

    public static String getPackageName(String qualifiedClassName) {
        int i = qualifiedClassName.lastIndexOf('.');
        return i > 0 ? qualifiedClassName.substring(0, i) : null;
    }

    public static String getClassName(String qualifiedClassName) {
        return qualifiedClassName.substring(qualifiedClassName.lastIndexOf('.') + 1);
    }

    public static JavaSource getJavaSource(Project project, String fqClassName) throws IOException {
        FileObject fo = getJavaFileObject(project, fqClassName);
        if (fo != null) {
            return JavaSource.forFileObject(fo);
        } else {
            return null;
        }
    }

    public static JavaSource getJavaSource(SourceGroup sourceGroup, String fqClassName) {
        FileObject sourceClass = SourceGroupSupport.getJavaFileObject(sourceGroup, fqClassName);
        return JavaSource.forFileObject(sourceClass);
    }

    public static FileObject getJavaFileObject(SourceGroup sourceGroup, String fqClassName) {
        return sourceGroup.getRootFolder().getFileObject(fqClassName.replaceAll("\\.", Matcher.quoteReplacement("/")) + JAVA_EXT_SUFFIX);//File.seprator not supported => "/"
    }

    public static ElementHandle<TypeElement> getHandleClassName(String qualifiedClassName,
            Project project) throws IOException {
        FileObject root = MiscUtilities.findSourceRoot(project);
        ClassPathProvider provider = project.getLookup().lookup(
                ClassPathProvider.class);
        ClassPath sourceCp = provider.findClassPath(root, ClassPath.SOURCE);
        ClassPath compileCp = provider.findClassPath(root, ClassPath.COMPILE);
        ClassPath bootCp = provider.findClassPath(root, ClassPath.BOOT);
        ClasspathInfo cpInfo = ClasspathInfo.create(bootCp, compileCp, sourceCp);
        ClassIndex ci = cpInfo.getClassIndex();
        int beginIndex = qualifiedClassName.lastIndexOf('.') + 1;
        String simple = qualifiedClassName.substring(beginIndex);
        Set<ElementHandle<TypeElement>> handles = ci.getDeclaredTypes(
                simple, ClassIndex.NameKind.SIMPLE_NAME,
                EnumSet.of(ClassIndex.SearchScope.SOURCE,
                        ClassIndex.SearchScope.DEPENDENCIES));
        for (final ElementHandle<TypeElement> handle : handles) {
            if (qualifiedClassName.equals(handle.getQualifiedName())) {
                return handle;
            }
        }
        return null;
    }

    public static ElementHandle<TypeElement> getHandleClassName(String qualifiedClassName,
            SourceGroup sourceGroup) throws IOException {
        ClassPath rcp = ClassPathSupport.createClassPath(sourceGroup.getRootFolder());
        ClasspathInfo cpInfo = ClasspathInfo.create(ClassPath.EMPTY, ClassPath.EMPTY, rcp);
        ClassIndex ci = cpInfo.getClassIndex();
        int beginIndex = qualifiedClassName.lastIndexOf('.') + 1;
        String simple = qualifiedClassName.substring(beginIndex);

        Set<ElementHandle<TypeElement>> handles = ci.getDeclaredTypes(
                simple, ClassIndex.NameKind.SIMPLE_NAME,
                EnumSet.of(ClassIndex.SearchScope.SOURCE));
        for (final ElementHandle<TypeElement> handle : handles) {
            if (qualifiedClassName.equals(handle.getQualifiedName())) {
                return handle;
            }
        }

        return null;
    }

    public static FileObject getJavaFileObject(Project project, String fqClassName) {
        try {
            final ElementHandle<TypeElement> handle = getHandleClassName(fqClassName, project);
            if (handle == null) {
                return null;
            }
            ClassPathProvider provider = project.getLookup().lookup(ClassPathProvider.class);
            FileObject root = MiscUtilities.findSourceRoot(project);
            ClassPath sourceCp = provider.findClassPath(root, ClassPath.SOURCE);
            final ClassPath compileCp = provider.findClassPath(root, ClassPath.COMPILE);
            ClassPath bootCp = provider.findClassPath(root, ClassPath.BOOT);
            ClasspathInfo cpInfo = ClasspathInfo.create(bootCp, compileCp, sourceCp);
            if (fqClassName.equals(handle.getQualifiedName())) {
                FileObject fo = SourceUtils.getFile(handle, cpInfo);
                if (fo != null) {
                    return fo;
                }
                JavaSource javaSource = JavaSource.create(cpInfo);
                final FileObject classFo[] = new FileObject[1];
                javaSource.runUserActionTask(controller -> {
                    TypeElement element = handle.resolve(controller);
                    if (element == null) {
                        return;
                    }
                    PackageElement pack = controller.getElements().getPackageOf(element);
                    if (pack == null) {
                        return;
                    }
                    String packageName = pack.getQualifiedName().toString();
                    String fqn = ElementUtilities.getBinaryName(element);
                    String className = fqn.substring(packageName.length());
                    if (className.length() > 0 && className.charAt(0) == '.') {
                        className = className.substring(1);
                    } else {
                        return;
                    }
                    int dotIndex = className.indexOf('.');
                    if (dotIndex != -1) {
                        className = className.substring(0, dotIndex);
                    }

                    String path = packageName.replace('.', '/') + '/'
                            + className + ".class"; // NOI18N
                    classFo[0] = compileCp.findResource(path);
                }, true);
                return classFo[0];
            }
            return null;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public static FileObject findJavaSourceFile(Project project, String name) {
        for (SourceGroup group : getJavaSourceGroups(project)) {
            Enumeration<? extends FileObject> files = group.getRootFolder().getChildren(true);
            while (files.hasMoreElements()) {
                FileObject fo = files.nextElement();
                if (JAVA_EXT.equals(fo.getExt())) { //NOI18N
                    if (name.equals(fo.getName())) {
                        return fo;
                    }
                }
            }
        }
        return null;
    }

    public static List<ClassPath> gerClassPath(Project project) {
        List<ClassPath> paths = new ArrayList<>();
        List<SourceGroup> groups = new ArrayList<>();
        groups.addAll(Arrays.asList(ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)));
        ClassPathProvider cpp = project.getLookup().lookup(ClassPathProvider.class);
        for (SourceGroup group : groups) {
            ClassPath cp = cpp.findClassPath(group.getRootFolder(), ClassPath.COMPILE);
            if (cp != null) {
                paths.add(cp);
            }
            cp = cpp.findClassPath(group.getRootFolder(), ClassPath.SOURCE);
            if (cp != null) {
                paths.add(cp);
            }
        }
        return paths;
    }
    public static final String TYPE_DOC_ROOT = "doc_root"; //NOI18N

    public static SourceGroup[] getSourceGroups(Project project) {
        SourceGroup[] sourceGroups = null;

        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] docRoot = sources.getSourceGroups(TYPE_DOC_ROOT);
        SourceGroup[] srcRoots = SourceGroupSupport.getJavaSourceGroups(project);

        if (docRoot != null && srcRoots != null) {
            sourceGroups = new SourceGroup[docRoot.length + srcRoots.length];
            System.arraycopy(docRoot, 0, sourceGroups, 0, docRoot.length);
            System.arraycopy(srcRoots, 0, sourceGroups, docRoot.length, srcRoots.length);
        }

        if (sourceGroups == null || sourceGroups.length == 0) {
            sourceGroups = sources.getSourceGroups(Sources.TYPE_GENERIC);
        }
        return sourceGroups;
    }

    public static Class getType(Project project, String typeName) {
        List<ClassPath> classPaths = SourceGroupSupport.gerClassPath(project);

        for (ClassPath cp : classPaths) {
            try {
                Class ret = JavaUtil.getPrimitiveType(typeName);
                if (ret != null) {
                    return ret;
                }
                ClassLoader cl = cp.getClassLoader(true);
                ret = getGenericRawType(typeName, cl);
                if (ret != null) {
                    return ret;
                }
                if (cl != null) {
                    return cl.loadClass(typeName);
                }
            } catch (ClassNotFoundException ex) {
                //Logger.global.log(Level.INFO, ex.getLocalizedMessage(), ex);
            }
        }
        return null;
    }

    public static Class getGenericRawType(String typeName, ClassLoader loader) {
        int i = typeName.indexOf('<');
        if (i < 1) {
            return null;
        }
        String raw = typeName.substring(0, i);
        try {
            return loader.loadClass(raw);
        } catch (ClassNotFoundException ex) {
            Logger.global.log(Level.INFO, "", ex);
            return null;
        }
    }

    /**
     * Gets the {@link SourceGroup} of the given <code>project</code> which
     * contains the given <code>fqClassName</code>.
     *
     * @param project the project; must not be null.
     * @param fqClassName the fully qualified name of the class whose source
     * group to get; must not be empty or null.
     * @return the source group containing the given <code>fqClassName</code> or
     * <code>null</code> if the class was not found in the source groups of the
     * project.
     */
    public static SourceGroup getClassSourceGroup(Project project, String fqClassName) {
        Parameters.notNull("project", project); //NOI18N
        Parameters.notEmpty("fqClassName", fqClassName); //NOI18N

        String classFile = fqClassName.replace('.', '/') + JAVA_EXT_SUFFIX; // NOI18N
        SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

        for (SourceGroup sourceGroup : sourceGroups) {
            FileObject classFO = sourceGroup.getRootFolder().getFileObject(classFile);
            if (classFO != null) {
                return sourceGroup;
            }
        }
        return null;
    }

    /**
     * Gets the {@link SourceGroup} of the given <code>folder</code>.
     *
     * @param sourceGroups the source groups to search; must not be null.
     * @param folder the folder whose source group is to be get; must not be
     * null.
     * @return the source group containing the given <code>folder</code> or null
     * if not found.
     */
    public static SourceGroup getFolderSourceGroup(SourceGroup[] sourceGroups, FileObject folder) {
        Parameters.notNull("sourceGroups", sourceGroups); //NOI18N
        Parameters.notNull("folder", folder); //NOI18N
        for (int i = 0; i < sourceGroups.length; i++) {
            if (FileUtil.isParentOf(sourceGroups[i].getRootFolder(), folder)) {
                return sourceGroups[i];
            }
        }
        return null;
    }

    public static SourceGroup getFolderSourceGroup(FileObject folder) {
        Project project = FileOwnerQuery.getOwner(folder);
        return getFolderSourceGroup(ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA), folder);
    }
}
