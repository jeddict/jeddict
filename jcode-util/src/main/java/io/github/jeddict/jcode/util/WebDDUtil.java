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

import static io.github.jeddict.jcode.util.FileUtil.expandTemplate;
import static io.github.jeddict.jcode.util.ProjectHelper.getProjectWebInf;
import java.io.IOException;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.common.dd.DDHelper;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.dd.api.web.WelcomeFileList;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import java.util.function.Predicate;

/**
 *
 * @author Gaurav Gupta
 */
public class WebDDUtil {

    public static final String DD_NAME = "web.xml";

    public static boolean setWelcomeFiles(Project project, String text) throws IOException {
        FileObject webXml = null;
        // Create web.xml
        WebModule wm1 = WebModule.getWebModule(project.getProjectDirectory());
        if (wm1 != null && wm1.getDocumentBase() != null) {
            FileObject webInf = wm1.getWebInf();
            if (webInf == null) {
                webInf = FileUtil.createFolder(wm1.getDocumentBase(), "WEB-INF");    //NOI18N
            }
            webXml = wm1.getDeploymentDescriptor();
            if (webXml == null) {
                webXml = DDHelper.createWebXml(wm1.getJ2eeProfile(), webInf);
            }
        }

        if (webXml != null) {
            WebApp webApp = DDProvider.getDefault().getDDRoot(webXml);

            if (text.length() == 0) {
                webApp.setWelcomeFileList(null);
            } else {
                java.util.List<String> wfList = new java.util.ArrayList<>();
                java.util.StringTokenizer tok = new java.util.StringTokenizer(text, ",");
                while (tok.hasMoreTokens()) {
                    String wf = tok.nextToken().trim();
                    if (wf.length() > 0 && !wfList.contains(wf)) {
                        wfList.add(wf);
                    }
                }

                WelcomeFileList welcomeFileList = webApp.getSingleWelcomeFileList();
                if (welcomeFileList == null) {
                    try {
                        welcomeFileList = (WelcomeFileList) webApp.createBean("WelcomeFileList"); //NOI18N
                        webApp.setWelcomeFileList(welcomeFileList);
                        if (welcomeFileList == null) {
                            return false;
                        }
                    } catch (ClassNotFoundException ex) {
                    }
                }
//                if (wfList.size() == 1) {
//                    welcomeFileList.addWelcomeFile(wfList.get(0));
//                } else {
                String[] welcomeFiles = new String[wfList.size()];
                wfList.toArray(welcomeFiles);
                welcomeFileList.setWelcomeFile(welcomeFiles);
//                }
            }

            webApp.write(webXml);
        }
        return true;
    }
    
    public static FileObject createDD(
            Project project,
            String ddTemplatePath,
            Map<String, Object> params,
            Predicate<FileObject> condition) {
        FileObject fileObject = null;
        try {
            FileObject webInf = getProjectWebInf(project);
            if (webInf != null && condition.test(webInf)) {
                fileObject = expandTemplate(ddTemplatePath, webInf, DD_NAME, params);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return fileObject;
    }
        
    public static FileObject createTestDD(
            Project project,
            String ddTemplatePath,
            Map<String, Object> params,
            Predicate<FileObject> condition) {
        FileObject fileObject = null;
        try {
            FileObject testResourceRoot = ProjectHelper.getTestResourceDirectory(project);
            if (condition.test(testResourceRoot)) {
                fileObject = expandTemplate(ddTemplatePath, testResourceRoot, DD_NAME, params);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return fileObject;
    }

}
