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
package io.github.jeddict.jcode.rest;

//import com.sun.source.tree.AnnotationTree;
//import com.sun.source.tree.ExpressionTree;
//import com.sun.source.tree.MethodTree;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import io.github.jeddict.jcode.util.JavaSourceHelper;
import io.github.jeddict.jcode.util.SourceGroupSupport;
import org.netbeans.modules.j2ee.deployment.common.api.Datasource;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleProvider;
import org.netbeans.modules.websvc.rest.model.api.RestConstants;
import org.netbeans.modules.websvc.rest.model.api.RestServicesModel;
import org.netbeans.modules.websvc.rest.spi.MiscUtilities;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * REST support utility
 *
 */
public class RestUtil {

    public static RestSupport getRestSupport(Project project) {
        return project.getLookup().lookup(RestSupport.class);
    }

    public static RestServicesModel getRestServicesMetadataModel(Project project) {
        RestSupport support = getRestSupport(project);
        if (support != null) {
            return support.getRestServicesModel();
        }
        return null;
    }

    public static void disableRestServicesChangeListner(Project project) {
        final RestServicesModel wsModel = RestUtil.getRestServicesMetadataModel(project);
        if (wsModel == null) {
            return;
        }
        wsModel.disablePropertyChangeListener();
    }

    public static void enableRestServicesChangeListner(Project project) {
        final RestServicesModel wsModel = RestUtil.getRestServicesMetadataModel(project);
        if (wsModel == null) {
            return;
        }
        wsModel.enablePropertyChangeListener();
    }

    public static boolean hasJTASupport(Project project) {
        RestSupport support = getRestSupport(project);

        if (support != null) {
            return support.hasJTASupport();
        }

        return false;
    }

    public static FileObject getDeploymentDescriptor(Project p) {
        WebModuleProvider wmp = p.getLookup().lookup(WebModuleProvider.class);
        if (wmp != null) {
            return wmp.findWebModule(p.getProjectDirectory()).getDeploymentDescriptor();
        }
        return null;
    }

    public static boolean hasSpringSupport(Project project) {
        RestSupport support = getRestSupport(project);

        if (support != null) {
            return support.hasSpringSupport();
        }

        return false;
    }

    public static boolean isServerTomcat(Project project) {
        RestSupport support = getRestSupport(project);

        if (support != null) {
            return support.isServerTomcat();
        }

        return false;
    }

    public static boolean isServerGFV3(Project project) {
        RestSupport support = getRestSupport(project);

        if (support != null) {
            return support.isServerGFV3();
        }

        return false;
    }

    public static boolean isServerGFV2(Project project) {
        RestSupport support = getRestSupport(project);

        if (support != null) {
            return support.isServerGFV2();
        }

        return false;
    }

    public static Datasource getDatasource(Project project, String jndiName) {
        return MiscUtilities.getDatasource(project, jndiName);
    }

    //
    // TODO: The following methods don't belong here. Some of them should go into
    // JavaSourceHelper and the XML/DOM related methods should go into
    // their own utility class.
    //        
    public static String getAttributeValue(Node n, String nodePath, String attrName) throws XPathExpressionException {
        String attrValue = null;
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr3 = xpath.compile(nodePath + "/@" + attrName);
        Object result3 = expr3.evaluate(n, XPathConstants.NODESET);
        NodeList nodes3 = (NodeList) result3;
        for (int i = 0; i < nodes3.getLength(); i++) {
            attrValue = nodes3.item(i).getNodeValue();
            break;
        }
        return attrValue;
    }

    public static String findUri(JavaSource rSrc) {
        String path = null;
        List<? extends AnnotationMirror> annotations = JavaSourceHelper.getClassAnnotations(rSrc);
        for (AnnotationMirror annotation : annotations) {
            String cAnonType = annotation.getAnnotationType().toString();
            if (RestConstants.PATH.equals(cAnonType)) {
                path = getValueFromAnnotation(annotation);
            }
        }
        return path;
    }

    public static boolean isStaticResource(JavaSource src) {
        List<? extends AnnotationMirror> annotations = JavaSourceHelper.getClassAnnotations(src);
        if (annotations != null && annotations.size() > 0) {
            for (AnnotationMirror annotation : annotations) {
                String classAnonType = annotation.getAnnotationType().toString();
                if (RestConstants.PATH.equals(classAnonType)) {
                    return true;
                }
            }
        }
        return false;
    }

//    public static boolean isDynamicResource(JavaSource src) {
//        List<MethodTree> trees = JavaSourceHelper.getAllMethods(src);
//        for (MethodTree tree : trees) {
//            List<? extends AnnotationTree> mAnons = tree.getModifiers().getAnnotations();
//            if (mAnons != null && mAnons.size() > 0) {
//                for (AnnotationTree mAnon : mAnons) {
//                    String mAnonType = mAnon.getAnnotationType().toString();
//                    if (RestConstants.PATH_ANNOTATION.equals(mAnonType) || RestConstants.PATH.equals(mAnonType)) {
//                        return true;
//                    } else if (RestConstants.GET_ANNOTATION.equals(mAnonType) || RestConstants.GET.equals(mAnonType)) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }

//    public static String findElementName(MethodTree tree) {
//        String eName = "";
//        List<? extends AnnotationTree> mAnons = tree.getModifiers().getAnnotations();
//        if (mAnons != null && mAnons.size() > 0) {
//            for (AnnotationTree mAnon : mAnons) {
//                eName = mAnon.toString();
//                if (eName.indexOf("\"") != -1) {
//                    eName = getValueFromAnnotation(mAnon);
//                } else {
//                    eName = getNameFromMethod(tree);
//                }
//            }
//        }
//        return eName.substring(0, 1).toLowerCase() + eName.substring(1);
//    }
//
//    public static String getNameFromMethod(MethodTree tree) {
//        String attrName = tree.getName().toString();
//        attrName = attrName.substring(attrName.indexOf("get") + 3);
//        attrName = attrName.substring(0, 1).toLowerCase() + attrName.substring(1);
//        return attrName;
//    }

    public static String getValueFromAnnotation(AnnotationMirror annotation) {
        return getValueFromAnnotation(annotation.getElementValues().values().toString());
    }

//    public static String getValueFromAnnotation(AnnotationTree mAnon) {
//        return getValueFromAnnotation(mAnon.toString());
//    }
//
//    public static String getValueFromAnnotation(ExpressionTree eAnon) {
//        return getValueFromAnnotation(eAnon.toString());
//    }

    public static String getValueFromAnnotation(String value) {
        if (value.indexOf("\"") != -1) {
            value = value.substring(value.indexOf("\"") + 1, value.lastIndexOf("\""));
        }
        return value;
    }

//    public static List<String> getMimeAnnotationValue(AnnotationTree ant) {
//        List<? extends ExpressionTree> ets = ant.getArguments();
//        if (ets.size() > 0) {
//            String value = getValueFromAnnotation(ets.get(0));
//            value = value.replace("\"", "");
//            return Arrays.asList(value.split(","));
//        }
//        return Collections.emptyList();
//    }

    public static boolean hasClass(Project project, String fqn) throws IOException {
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        if (wm != null) {
            SourceGroup[] sourceGroups = SourceGroupSupport.getJavaSourceGroups(project);
            if (sourceGroups.length > 0) {
                ClassPath cp = ClassPath.getClassPath(sourceGroups[0].getRootFolder(),
                        ClassPath.COMPILE);
                if (cp != null && cp.findResource(fqn) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String createBodyForGetClassesMethod(RestSupport restSupport, List<String> provideClasses) {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        builder.append("Set<Class<?>> resources = new java.util.HashSet<>();");// NOI18N
        if (provideClasses != null) {
            for (String _class : provideClasses) {
                builder.append("resources.add(").append(_class).append(".class);");
            }
        }
        builder.append(RestConstants.GET_REST_RESOURCE_CLASSES2+"(resources);");
        builder.append("return resources;}");
        return builder.toString();
    }


    public static boolean hasProfile(Project project, Profile... profiles) {
        WebModule webModule = WebModule.getWebModule(project.getProjectDirectory());
        if (webModule != null) {
            Profile projectProfile = webModule.getJ2eeProfile();
            for (Profile profile : profiles) {
                if (projectProfile == profile) {
                    return true;
                }
            }
        }
        return false;
    }

}
