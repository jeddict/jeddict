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
package io.github.jeddict.jcode.generator;

import io.github.jeddict.jcode.AbstractGenerator;
import io.github.jeddict.jcode.ApplicationConfigData;
import io.github.jeddict.jcode.LayerConfigData;
import io.github.jeddict.jcode.TechContext;
import io.github.jeddict.jcode.annotation.ConfigData;
import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.BOLD;
import io.github.jeddict.jcode.jpa.PersistenceHelper;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import io.github.jeddict.jcode.util.BuildManager;
import io.github.jeddict.jcode.util.FileUtil;
import static io.github.jeddict.jcode.util.FileUtil.expandTemplateContent;
import io.github.jeddict.jcode.util.WebDDUtil;
import static io.github.jeddict.jcode.util.WebDDUtil.DD_NAME;
import io.github.jeddict.jpa.spec.EntityMappings;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import static java.util.Collections.singletonMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.openide.util.Exceptions;

/**
 *
 * @author Gaurav Gupta
 */
public class ApplicationGenerator extends AbstractGenerator {

    private ApplicationConfigData appConfigData;
    private ProgressHandler handler;
    private Map<Class<? extends LayerConfigData>, LayerConfigData> layerConfigData;

    private Project targetProject;

    private Project gatewayProject;

    private static final String WEB_XML_TEMPLATE = "/io/github/jeddict/template/web/descriptor/_web.xml.ftl";

    @Override
    public void initialize(ApplicationConfigData applicationConfigData, ProgressHandler progressHandler) {
        this.appConfigData = applicationConfigData;
        this.handler = progressHandler;
        targetProject = appConfigData.getTargetProject();
        gatewayProject = appConfigData.getGatewayProject();
        injectData();
    }

    @Override
    public void preGeneration() {
        TechContext bussinesLayerConfig = appConfigData.getRepositoryTechContext();
        if (bussinesLayerConfig == null) {
            return;
        }
        bussinesLayerConfig.getGenerator().preExecute();
        for (TechContext context : bussinesLayerConfig.getSiblingTechContext()) {
            context.getGenerator().preExecute();
        }

        TechContext controllerLayerConfig = appConfigData.getControllerTechContext();
        if (controllerLayerConfig == null) {
            return;
        }
        controllerLayerConfig.getGenerator().preExecute();
        for (TechContext context : controllerLayerConfig.getSiblingTechContext()) {
            context.getGenerator().preExecute();
        }

        TechContext viewerLayerConfig = appConfigData.getViewerTechContext();
        if (viewerLayerConfig == null) {
            return;
        }
        viewerLayerConfig.getGenerator().preExecute();
        for (TechContext context : viewerLayerConfig.getSiblingTechContext()) {
            context.getGenerator().preExecute();
        }
    }

    @Override
    public void generate() {
        try {
            if (handler != null) {
                initProgressReporting(handler);
            }
            EntityMappings entityMappings = appConfigData.getEntityMappings();
            Set<String> entities = entityMappings.getFQEntity().collect(toSet());
            //Make necessary changes to the persistence.xml
            if (appConfigData.isMonolith() || appConfigData.isMicroservice()) {
                new PersistenceHelper(targetProject).configure(entities);
            }
            if (appConfigData.isGateway()) {
                new PersistenceHelper(gatewayProject).configure(entities);
            }

            generateCRUD();
            appConfigData.getWebDescriptorContent().forEach((project, content) -> {
                WebDDUtil.createDD(
                        project,
                        WEB_XML_TEMPLATE,
                        singletonMap("content", content),
                        targetDir -> content.length() > 0
                );
            });
            appConfigData.getWebDescriptorTestContent().forEach((project, content) -> {
                WebDDUtil.createTestDD(
                        project,
                        WEB_XML_TEMPLATE,
                        singletonMap("content", content),
                        testResourceRoot -> content.length() > 0
                );
            });

            Project project = appConfigData.isGateway() ? appConfigData.getGatewayProject() : appConfigData.getTargetProject();
            BuildManager.reload(project);
            WebDDUtil.createDD(
                    project,
                    WEB_XML_TEMPLATE,
                    singletonMap("content", ""),
                    targetDir -> targetDir.getFileObject(DD_NAME) == null
            );
            WebDDUtil.createTestDD(
                    project,
                    WEB_XML_TEMPLATE,
                    singletonMap("content", ""),
                    testResourceRoot -> testResourceRoot.getFileObject(DD_NAME) == null
            );
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void postGeneration() {

        try {
            TechContext bussinesLayerConfig = appConfigData.getRepositoryTechContext();
            if (bussinesLayerConfig == null) {
                return;
            }
            bussinesLayerConfig.getGenerator().postExecute();
            bussinesLayerConfig.getSiblingTechContext().forEach(context -> context.getGenerator().postExecute());

            TechContext controllerLayerConfig = appConfigData.getControllerTechContext();
            if (controllerLayerConfig == null) {
                return;
            }
            controllerLayerConfig.getGenerator().postExecute();
            controllerLayerConfig.getSiblingTechContext().forEach(context -> context.getGenerator().postExecute());

            TechContext viewerLayerConfig = appConfigData.getViewerTechContext();
            if (viewerLayerConfig == null) {
                return;
            }
            viewerLayerConfig.getGenerator().postExecute();
            viewerLayerConfig.getSiblingTechContext()
                    .forEach(context -> context.getGenerator().postExecute());

        } finally {

            if (appConfigData.isCompleteApplication()) {
                String titleTemplate = "Maven '${env}' build";
                String commandTemplate = "mvn clean install ${profiles} ${buildProperties} ${goals}";

                appConfigData.getEnvironments()
                        .forEach(env -> {
                            Map<String, Object> params = new HashMap<>();
                            String profiles = env.getProfiles();
                            String goals = env.getGoals();
                            String properties = env.getBuildProperties();
                            params.put("profiles", profiles.isEmpty() ? "" : "-P " + profiles);
                            params.put("buildProperties", properties.isEmpty() ? "" : properties);
                            params.put("goals", goals.isEmpty() ? "" : goals);
                            params.put("env", env.getName());

                            handler.info(
                                    expandTemplateContent(titleTemplate, params),
                                    Console.wrap(
                                            env.getPreCommands()
                                            + expandTemplateContent(commandTemplate, params)
                                            + env.getPostCommands(),
                                            BOLD
                                    )
                            );
                        });
            }

            finishProgressReporting();
        }
    }

    private void injectData() {
        layerConfigData = new HashMap<>();
        TechContext bussinesLayerConfig = appConfigData.getRepositoryTechContext();
        if (bussinesLayerConfig == null) {
            return;
        }
        storeLayerConfigData(bussinesLayerConfig);

        TechContext controllerLayerConfig = appConfigData.getControllerTechContext();
        TechContext viewerLayerConfig = null;
        if (controllerLayerConfig != null) {
            controllerLayerConfig.getConfigData().setParentLayerConfigData(bussinesLayerConfig.getConfigData());
            storeLayerConfigData(controllerLayerConfig);

            viewerLayerConfig = appConfigData.getViewerTechContext();
            if (viewerLayerConfig != null) {
                viewerLayerConfig.getConfigData().setParentLayerConfigData(controllerLayerConfig.getConfigData());
                storeLayerConfigData(viewerLayerConfig);
            }
        }

        inject(bussinesLayerConfig);
        if (controllerLayerConfig != null) {
            inject(controllerLayerConfig);
        }
        if (viewerLayerConfig != null) {
            inject(viewerLayerConfig);
        }
    }

    private void storeLayerConfigData(TechContext rootTechContext) {
        layerConfigData.put(rootTechContext.getConfigData().getClass(), rootTechContext.getConfigData());
        for (TechContext context : rootTechContext.getSiblingTechContext()) {
            storeLayerConfigData(context);
        }
    }

    private void inject(TechContext rootTechContext) {
        inject(rootTechContext.getGenerator(), appConfigData, layerConfigData, handler, null, null);
        for (TechContext context : rootTechContext.getSiblingTechContext()) {
            inject(context);
        }
    }

    private void execute(TechContext rootTechContext) throws IOException {
        rootTechContext.getGenerator().execute();
        for (TechContext siblingTechContext : rootTechContext.getSiblingTechContext()) {
            execute(siblingTechContext);
        }
    }

    private void generateCRUD() throws IOException {
        TechContext bussinesLayerConfig = appConfigData.getRepositoryTechContext();
        if (bussinesLayerConfig == null) {
            return;
        }
        execute(bussinesLayerConfig);

        TechContext controllerLayerConfig = appConfigData.getControllerTechContext();
        if (controllerLayerConfig == null) {
            return;
        }
        execute(controllerLayerConfig);

        TechContext viewerLayerConfig = appConfigData.getViewerTechContext();
        if (viewerLayerConfig == null) {
            return;
        }
        execute(viewerLayerConfig);

//        PersistenceUtil.getPersistenceUnit(getProject(), applicationConfigData.getEntityMappings().getPersistenceUnitName()).ifPresent(pud -> {
//            try {
//                ProviderUtil.getPUDataObject(getProject()).save();
//            } catch (InvalidPersistenceXmlException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//        });
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != Object.class) {
            fields = getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

    public static void inject(Object instance,
            ApplicationConfigData applicationConfigData,
            Map<Class<? extends LayerConfigData>, LayerConfigData> layerConfigData,
            ProgressHandler handler,
            Project project,
            SourceGroup sourceGroup) {
        List<Field> fields = getAllFields(new LinkedList<>(), instance.getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigData.class)) {
                field.setAccessible(true);
                try {
                    if (field.getGenericType() == ApplicationConfigData.class) {
                        field.set(instance, applicationConfigData);
                    } else if (field.getGenericType() == EntityMappings.class) {
                        field.set(instance, applicationConfigData.getEntityMappings());
                    } else if (field.getType().isAssignableFrom(handler.getClass())) {
                        field.set(instance, handler);
                    } else if (LayerConfigData.class.isAssignableFrom(field.getType())) {
                        field.set(instance, layerConfigData.get(field.getType()));
                    } else if (field.getGenericType() == Project.class && project != null) {
                        field.set(instance, project);
                    } else if (field.getGenericType() == SourceGroup.class && sourceGroup != null) {
                        field.set(instance, sourceGroup);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected int getTotalWorkUnits() {
        float unit = 1.5f;
        float webUnit = 5f;
        float count = appConfigData.getEntityMappings().getGeneratedEntity().count();
        if (appConfigData.getRepositoryTechContext() != null) {
            count = count + count * unit;
        }
        if (appConfigData.getControllerTechContext() != null) {
            count = count + count * unit;
        }
        if (appConfigData.getViewerTechContext() != null) {
            count = count + count * webUnit;
        }
        return (int) count;
    }

}
