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
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ui.OpenProjects;
import static io.github.jeddict.jcode.util.SourceGroupSupport.getFolderForPackage;
import static io.github.jeddict.jcode.util.SourceGroupSupport.getTestSourceGroup;
import org.netbeans.modules.j2ee.common.J2eeProjectCapabilities;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.api.webmodule.WebProjectConstants;
import org.netbeans.modules.websvc.rest.spi.MiscUtilities;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Gaurav Gupta
 */
public class ProjectHelper {

    public static boolean isCDIEnabled(FileObject fileObject) {
        Project project = FileOwnerQuery.getOwner(fileObject);
        if (project != null) {
            return isCDIEnabled(project);
        }
        return false;
    }

    public static boolean isCDIEnabled(Project project) {
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        if (wm != null) {
            if (!MiscUtilities.isJavaEE6AndHigher(project)) {
                return false;
            }
            FileObject confRoot = wm.getWebInf();
            if (confRoot != null && confRoot.getFileObject("beans.xml") != null) {  //NOI18N
                return true;
            }
        }
        return false;
    }

    public static List<Project> getJavaProjects() {
        List<Project> list = new ArrayList<>();
        Project[] openProjects = OpenProjects.getDefault().getOpenProjects();

        if (openProjects != null) {
            for (Project prj : openProjects) {
                Sources sources = ProjectUtils.getSources(prj);
                if (sources == null) {
                    continue;
                }
                SourceGroup[] srcGrps = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
                if (srcGrps != null && srcGrps.length > 0) {
                    list.add(prj);
                }
            }
        }
        return list;
    }
    
    public static FileObject getProjectWebRoot(Project project) {
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup sourceGroups[] = sources.getSourceGroups(WebProjectConstants.TYPE_DOC_ROOT);
        if (sourceGroups.length > 0) {
            return sourceGroups[0].getRootFolder();
        } else {
            return null;
        }
    }
    
    
    /**
     * when ever there is need for non-java files creation or lookup,
     * use this method to get the right location for all projects. 
     * Eg. maven places resources not next to the java files.
     * Please note that the method should not be used for 
     * checking file existence. There can be multiple resource roots, the returned one
     * is just the first in line. Use <code>getResource</code> instead in that case.
     * @param project
     * @return 
     */ 
    public static FileObject getResourceDirectory(Project project) {
        Sources srcs = ProjectUtils.getSources(project);
        SourceGroup[] grps = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_RESOURCES);
        if (grps != null && grps.length > 0) {
            return grps[0].getRootFolder();
        }
        try {
            return project.getProjectDirectory()
                    .getFileObject("src/main")
                    .createFolder("resources");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
    
    public static FileObject getTestResourceDirectory(Project prj) {
        
        SourceGroup sourceGroup = getTestSourceGroup(prj);
        FileObject fileObject = getFolderForPackage(sourceGroup.getRootFolder().getParent(), "resources", true);
    //if(POMManager.isMavenProject(project)){
        return fileObject;
    }

    public static FileObject getDockerDirectory(SourceGroup sourceGroup) {
        return getFolderForPackage(sourceGroup.getRootFolder().getParent(), "docker", true);
    }
      
   
    /**
     * check if resource of given path exists in the current project resources.
     * 
     * @param prj
     * @param path as in <code>FileObject.getFileObject(path)</code>
     * @return FileObject or null if not found.
     * @since 1.57
     */
    public static FileObject getResource(Project prj, String path) {
        Sources srcs = ProjectUtils.getSources(prj);
        SourceGroup[] grps = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_RESOURCES);
        if (grps != null && grps.length > 0) {
            for (SourceGroup sg : grps) {
                FileObject fo = sg.getRootFolder().getFileObject(path);
                if (fo != null) {
                    return fo;
                }
            }
        }
        return null;
    }

    
     public static boolean isJavaEE6AndHigher(Project project) {
        return MiscUtilities.isJavaEE6AndHigher(project);
    }
     
    public static Map<String, ?> getTemplateProperties() {
        FileObject dir = org.openide.filesystems.FileUtil.getConfigFile("Templates/Properties");
        if (dir == null) {
            return Collections.emptyMap();
        }
        Charset set;
        InputStream is;
        
        Map<String, Object> ret = new HashMap<>();
        for (Enumeration<? extends FileObject> en = dir.getChildren(true); en.hasMoreElements(); ) {
            try {
                FileObject fo = en.nextElement();
                Properties p = new Properties();
                is = fo.getInputStream();
                p.load(is);
                is.close();
                for (Map.Entry<Object, Object> entry : p.entrySet()) {
                    if (entry.getKey() instanceof String) {
                        String key = (String) entry.getKey();
                        if (!ret.containsKey(key)) {
                            ret.put(key, entry.getValue());
                        }
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return ret;
    }

    public static ProjectType getProjectType(Project project){
        ProjectType projectType = ProjectType.JAR;
        if (!ProjectHelper.isJavaEE6AndHigher(project)) { //removed for gradle project type support
        J2eeProjectCapabilities capabilities = J2eeProjectCapabilities.forProject(project);
            if (capabilities != null && capabilities.isEjb31Supported()) {
                projectType = ProjectType.EJB;
            }
        } else {
            projectType = ProjectType.WEB;
        }
        return projectType;
    }
    
    public static String getProjectDisplayName(Project project) {
        return ProjectUtils.getInformation(project).getDisplayName();
    }
}
