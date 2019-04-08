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
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.emptySet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import static java.util.stream.Collectors.toSet;
import javax.swing.SwingUtilities;
import javax.xml.namespace.QName;
import io.github.jeddict.util.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.ModelUtils;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.api.customizer.ModelHandle2;
import org.netbeans.modules.maven.configurations.M2ConfigProvider;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.execute.model.NetbeansActionMapping;
import org.netbeans.modules.maven.model.ModelOperation;
import org.netbeans.modules.maven.model.Utilities;
import org.netbeans.modules.maven.model.pom.Activation;
import org.netbeans.modules.maven.model.pom.ActivationProperty;
import org.netbeans.modules.maven.model.pom.BuildBase;
import org.netbeans.modules.maven.model.pom.Configuration;
import org.netbeans.modules.maven.model.pom.Dependency;
import org.netbeans.modules.maven.model.pom.DependencyContainer;
import org.netbeans.modules.maven.model.pom.DependencyManagement;
import org.netbeans.modules.maven.model.pom.Exclusion;
import org.netbeans.modules.maven.model.pom.POMComponent;
import org.netbeans.modules.maven.model.pom.POMExtensibilityElement;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.POMModelFactory;
import org.netbeans.modules.maven.model.pom.Plugin;
import org.netbeans.modules.maven.model.pom.PluginExecution;
import org.netbeans.modules.maven.model.pom.Profile;
import org.netbeans.modules.maven.model.pom.Properties;
import org.netbeans.modules.maven.model.pom.Repository;
import org.netbeans.modules.maven.model.pom.RepositoryPolicy;
import org.netbeans.modules.maven.model.pom.Resource;
import org.netbeans.modules.maven.model.pom.spi.POMExtensibilityElementBase;
import org.openide.filesystems.FileObject;
import static org.openide.filesystems.FileUtil.toFileObject;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author jGauravGupta
 */
public class POMManager extends BuildManager {

    private Project project;
    
    private FileObject pomFileObject;
    
    private POMModel pomModel;
    
    private NbMavenProjectImpl mavenProject;

    private List<Model> sourceModels = new ArrayList<>();
    
    private List<ModelOperation<POMModel>> operations;
    
    private static final RequestProcessor RP = new RequestProcessor("Maven loading");
    
    private BiFunction<Xpp3Dom, POMExtensibilityElement, Boolean> extensionOverrideFilter;

    public POMManager(Project project){
        this(project, false);
    }
    
    public POMManager(Project project, boolean readonly) {
        //target    
        this.project = project;
        mavenProject = project.getLookup().lookup(NbMavenProjectImpl.class);
        pomFileObject = toFileObject(mavenProject.getPOMFile());
        pomModel = POMModelFactory.getDefault().createFreshModel(Utilities.createModelSource(pomFileObject));
        operations = new ArrayList<>();
        if(!readonly)pomModel.startTransaction();
    }

    public POMManager(Project project, String... inputResources) {
        this(project, false);
        copy(inputResources);
    }
    
    public POMManager(Project project, Reader... inputResources) {
        this(project, false);
        copy(inputResources);
    }
    
    @Override
    public POMManager copy(String... inputResources) {
        Map<String, ?> properties = Collections.singletonMap(ModelReader.IS_STRICT, false);
        ModelReader reader = EmbedderFactory.getProjectEmbedder().lookupComponent(ModelReader.class);
        for (String inputResource : inputResources) {
            try {
                //source
                sourceModels.add(reader.read(FileUtil.loadResource(inputResource), properties));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return this;
    }
        
    @Override
    public POMManager copy(Reader... inputResources) {
        Map<String, ?> properties = Collections.singletonMap(ModelReader.IS_STRICT, false);
        ModelReader reader = EmbedderFactory.getProjectEmbedder().lookupComponent(ModelReader.class);
        for (Reader inputResource : inputResources) {
            try {
                //source
                sourceModels.add(reader.read(inputResource, properties));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return this;
    }
    
    public void fixDistributionProperties() {
        org.netbeans.modules.maven.model.pom.Project pomProject = getPOMProject();
        if (pomProject.getGroupId() != null) {
            pomProject.setGroupId(pomProject.getGroupId().toLowerCase());
        }
        if (pomProject.getArtifactId() != null) {
            pomProject.setArtifactId(pomProject.getArtifactId().toLowerCase());
        }
        if (pomProject.getVersion() != null) {
            pomProject.setVersion(pomProject.getVersion().toLowerCase());
        }
    }

    private org.netbeans.modules.maven.model.pom.Project getPOMProject() {
        return pomModel.getProject();
    }

    public String getGroupId() {
        return getPOMProject().getGroupId();
    }

    public String getArtifactId() {
        return getPOMProject().getArtifactId();
    }

    public String getVersion() {
        return getPOMProject().getVersion();
    }

    @Override
    public POMManager addDefaultProperties(java.util.Properties prop) {
        addDefaultProperties(null, prop);
        return this;
    }

    @Override
    public POMManager addDefaultProperties(String profile, java.util.Properties prop) {
        addProperties(profile, prop, true);
        return this;
    }

    @Override
    public POMManager addProperties(java.util.Properties prop) {
        addProperties(null, prop);
        return this;
    }

    @Override
    public POMManager addProperties(String profile, java.util.Properties prop) {
        addProperties(profile, prop, false);
        return this;
    }

    private void addProperties(String profile, java.util.Properties prop, boolean defaultValue) {
        boolean tx = pomModel.isIntransaction();
        try {
            if (!tx) {
                pomModel.startTransaction();
            }
            org.netbeans.modules.maven.model.pom.Project pomProject = getPOMProject();
            if (profile != null) {
                Profile targetProfile = pomProject.findProfileById(profile);
                if (targetProfile == null) {
                    throw new IllegalArgumentException(String.format("Profile[%s] not exist", profile));
                }
                if (targetProfile.getProperties() == null) {
                        targetProfile.setProperties(pomModel.getFactory().createProperties());
                    }
                registerProperties(prop, targetProfile.getProperties(), defaultValue);
            } else {
                registerProperties(prop, pomProject.getProperties(), defaultValue);
            }
        } finally {
            if (!tx) {
                pomModel.endTransaction();
            }
        }
    }

    @Override
    public POMManager setExtensionOverrideFilter(BiFunction<Xpp3Dom, POMExtensibilityElement, Boolean> extensionOverrideFilter) {
        this.extensionOverrideFilter = extensionOverrideFilter;
        return this;
    }

    public static boolean isMavenProject(Project project) {
        return project.getLookup().lookup(NbMavenProjectImpl.class) != null;
    }

    private void execute() {
        for (Model sourceModel : sourceModels) {
            if (sourceModel != null) {
                org.netbeans.modules.maven.model.pom.Project pomProject = getPOMProject();
                if (pomProject.getProperties() == null) {
                    pomProject.setProperties(pomModel.getFactory().createProperties());
                }
                registerProperties(sourceModel.getProperties(), pomProject.getProperties(), false);
                pomProject.setDependencyManagement(registerDependencyManagement(sourceModel.getDependencyManagement(), pomProject.getDependencyManagement()));
                registerDependency(sourceModel.getDependencies(), pomProject);
                registerRepository(sourceModel.getRepositories());
                registerBuild(sourceModel.getBuild());
                registerProfile(sourceModel.getProfiles());
            }
        }
    }

    private Properties registerProperties(java.util.Properties source, Properties target, boolean defaultValue) {
        if (source != null && !source.isEmpty()) {
            if (target == null) {
                target = pomModel.getFactory().createProperties();
            }
            for (String sourceKey : source.stringPropertyNames()) {
                String sourceValue = source.getProperty(sourceKey);
                String targetValue = target.getProperty(sourceKey);
                if (targetValue == null || !defaultValue) {
                    target.setProperty(sourceKey, sourceValue);
                }
            }
        }
        return target;
    }

    private void registerBuild(org.apache.maven.model.Build sourceBuild) {
        if (sourceBuild != null) {
            registerBuildBase(sourceBuild, getPOMProject().getBuild());
        }
    }

    private BuildBase registerBuildBase(org.apache.maven.model.BuildBase sourceBuild, BuildBase targetBuild) {
        if (sourceBuild == null) {
            return targetBuild;
        }
        if (targetBuild == null) {
            targetBuild = pomModel.getFactory().createBuild();
        }
        if (sourceBuild.getFinalName() != null) {
            targetBuild.setFinalName(sourceBuild.getFinalName());
        }
        if (sourceBuild.getResources()!= null && !sourceBuild.getResources().isEmpty()) {
            for (org.apache.maven.model.Resource sourceResource : sourceBuild.getResources()) {

                Resource targetResource = null;
                if (targetBuild.getResources() != null) {
                    for (Resource resource : targetBuild.getResources()) {
                        if (resource.getDirectory().equals(sourceResource.getDirectory())) {
                            targetResource = resource;
                        }
                    }
                }
                if (targetResource == null) {
                    targetResource = pomModel.getFactory().createResource();
                    targetResource.setDirectory(sourceResource.getDirectory());
                    targetBuild.addResource(targetResource);
                }

                targetResource.setFiltering(Boolean.parseBoolean(sourceResource.getFiltering()));

                Set<String> existingExcludes = new HashSet<>(targetResource.getExcludes() != null ? targetResource.getExcludes() : emptySet());
                for (String exclude : sourceResource.getExcludes()) {
                    if (!existingExcludes.contains(exclude)) {
                        targetResource.addExclude(exclude);
                    }
                }

                Set<String> existingIncludes = new HashSet<>(targetResource.getIncludes() != null ? targetResource.getIncludes() : emptySet());
                for (String include : sourceResource.getIncludes()) {
                    if (!existingIncludes.contains(include)) {
                        targetResource.addInclude(include);
                    }
                }

            }
        }

        if (sourceBuild.getPlugins() != null && !sourceBuild.getPlugins().isEmpty()) {
            for (org.apache.maven.model.Plugin sourcePlugin : sourceBuild.getPlugins()) {
                Plugin targetPlugin = targetBuild.findPluginById(sourcePlugin.getGroupId(), sourcePlugin.getArtifactId());
                if (targetPlugin == null) {
                    targetPlugin = pomModel.getFactory().createPlugin();
                    targetPlugin.setGroupId(sourcePlugin.getGroupId());
                    targetPlugin.setArtifactId(sourcePlugin.getArtifactId());
                    targetBuild.addPlugin(targetPlugin);
                }
                if (sourcePlugin.getExtensions() != null) {
                    targetPlugin.setExtensions(Boolean.TRUE);
                }
                registerDependency(sourcePlugin.getDependencies(), targetPlugin);
                targetPlugin.setConfiguration(registerConfiguration(sourcePlugin.getConfiguration(), targetPlugin.getConfiguration()));
                if (sourcePlugin.getExecutions() != null && !sourcePlugin.getExecutions().isEmpty()) {
                    for (org.apache.maven.model.PluginExecution sourceExecution : sourcePlugin.getExecutions()) {
                        PluginExecution targetExecution = targetPlugin.findExecutionById(sourceExecution.getId());
                        if (targetExecution == null) {
                            targetExecution = pomModel.getFactory().createExecution();
                            targetExecution.setId(sourceExecution.getId());
                            sourceExecution.getGoals().forEach(targetExecution::addGoal);
                            targetPlugin.addExecution(targetExecution);
                        }
                        targetExecution.setPhase(sourceExecution.getPhase());
                        targetExecution.setConfiguration(registerConfiguration(sourceExecution.getConfiguration(), targetExecution.getConfiguration()));

                    }
                }
                targetPlugin.setVersion(sourcePlugin.getVersion());
            }
        }
        return targetBuild;
    }

    private Configuration registerConfiguration(Object sourceConfig, Configuration targetConfig) {
        if (sourceConfig != null) {
            Xpp3Dom parentDOM = (Xpp3Dom) sourceConfig;
            if (targetConfig == null) {
                targetConfig = pomModel.getFactory().createConfiguration();
            }
            loadDom(parentDOM, targetConfig);
        }
        return targetConfig;
    }

    private void loadDom(Xpp3Dom source, POMComponent target) {

        for (Xpp3Dom childDOM : source.getChildren()) {
            if (childDOM.getValue() != null) {
                if (target instanceof Configuration) {
                    ((Configuration) target).setSimpleParameter(childDOM.getName(), childDOM.getValue());
                } else if (target instanceof POMExtensibilityElementBase) {
                    Optional<POMComponent> targetComponentOptioal = ((POMExtensibilityElementBase) target).getChildren()
                            .stream()
                            .filter(targetElement -> {
                                String nodeName = targetElement.getPeer().getNodeName();
                                nodeName = nodeName.substring(nodeName.lastIndexOf(':') + 1);
                                String nodeValue = targetElement.getPeer().getChildNodes().item(0).getNodeValue();
                                return StringUtils.equals(nodeName, childDOM.getName())
                                        && StringUtils.equals(nodeValue, childDOM.getValue());
                            })
                            .findAny();
                    if (!targetComponentOptioal.isPresent()) {
//                        target.setChildElementText(childDOM.getValue(), childDOM.getValue(), new QName(childDOM.getName()));
                        POMExtensibilityElement element = pomModel.getFactory().createPOMExtensibilityElement(new QName(childDOM.getName()));
                        element.setElementText(childDOM.getValue());
                        target.addExtensibilityElement(element);
                    }
                } else {
                    target.setChildElementText("propertyName", childDOM.getValue(), new QName(childDOM.getName()));
                }
            } else {
                Optional<POMExtensibilityElement> targetElementOptioal = target.getExtensibilityElements()
                        .stream()
                        .filter(targetElement -> StringUtils.equals(targetElement.getQName().getLocalPart(), childDOM.getName()))
                        .filter(targetElement -> extensionOverrideFilter == null || extensionOverrideFilter.apply(childDOM, targetElement))
                        .findAny();

                POMExtensibilityElement element;
                if (targetElementOptioal.isPresent()) {
                    element = targetElementOptioal.get();
                } else {
                    element = pomModel.getFactory().createPOMExtensibilityElement(new QName(childDOM.getName()));
                    for (String key : childDOM.getAttributeNames()) {
                        String value = childDOM.getAttribute(key);
                        element.setAttribute(key, value);
                    }
                    target.addExtensibilityElement(element);
                }
                
                loadDom(childDOM, element);
            }
        }
    }

    private void registerProfile(List<org.apache.maven.model.Profile> sourceProfiles) {
        if (!sourceProfiles.isEmpty()) {
            org.netbeans.modules.maven.model.pom.Project pomProject = getPOMProject();
            for (org.apache.maven.model.Profile sourceProfile : sourceProfiles) {
                Profile targetProfile = pomProject.findProfileById(sourceProfile.getId());
                if (targetProfile == null) {
                    targetProfile = pomModel.getFactory().createProfile();
                    pomProject.addProfile(targetProfile);
                    targetProfile.setId(sourceProfile.getId());
                }
                targetProfile.setProperties(registerProperties(sourceProfile.getProperties(), targetProfile.getProperties(), false));
                if (sourceProfile.getActivation() != null) {
                    Activation activation = pomModel.getFactory().createActivation();
                    targetProfile.setActivation(activation);
                    if (sourceProfile.getActivation().getProperty() != null) {
                        org.apache.maven.model.ActivationProperty sourceProperty = sourceProfile.getActivation().getProperty();
                        ActivationProperty targetProperty = pomModel.getFactory().createActivationProperty();
                        activation.setActivationProperty(targetProperty);
                        targetProperty.setName(sourceProperty.getName());
                        targetProperty.setValue(sourceProperty.getValue());
                    } else {
                        activation.setChildElementText("activeByDefault", Boolean.toString(sourceProfile.getActivation().isActiveByDefault()), new QName("activeByDefault"));
                    }
                }
                targetProfile.setDependencyManagement(registerDependencyManagement(sourceProfile.getDependencyManagement(), targetProfile.getDependencyManagement()));
                registerDependency(sourceProfile.getDependencies(), targetProfile);
                targetProfile.setBuildBase(registerBuildBase(sourceProfile.getBuild(), targetProfile.getBuildBase()));
            }
        }
    }

    private DependencyManagement registerDependencyManagement(org.apache.maven.model.DependencyManagement source, DependencyManagement target) {
        if (source != null) {
            if (target == null) {
                target = pomModel.getFactory().createDependencyManagement();
            }
            registerDependency(source.getDependencies(), target);
        }
        return target;
    }

    private void registerDependency(List<org.apache.maven.model.Dependency> source, DependencyContainer target) {
        source.forEach((sourceDependency) -> {
            org.netbeans.modules.maven.model.pom.Dependency targetDependency = target.findDependencyById(sourceDependency.getGroupId(), sourceDependency.getArtifactId(), null);
            if (targetDependency == null) {
                targetDependency = createDependency(sourceDependency);
                target.addDependency(targetDependency);
            }
            updateDependency(sourceDependency, targetDependency);
        });
    }

    public void registerDependency(String groupId, String artifactId, String version, String type, String classifier, String scope) {
        org.apache.maven.model.Dependency dependency = new org.apache.maven.model.Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        dependency.setType(type);
        dependency.setClassifier(classifier);
        dependency.setScope(scope);
        registerDependency(Collections.singletonList(dependency), getPOMProject());
    }
    
    public String getDependencyVersion(String groupId, String artifactId){
        Dependency dependency = getPOMProject().findDependencyById(groupId, artifactId, null);
        if(dependency != null){
            return dependency.getVersion();
        }
        return null;
    }
    
    public void setDependencyVersion(String groupId, String artifactId, String version) {
        Dependency dependency = getPOMProject().findDependencyById(groupId, artifactId, null);
        if (dependency != null) {
            dependency.setVersion(version);
        }
    }
    
    public void setPluginConfiguration(String groupId, String artifactId, Map<String, String> configs) {
        if (getPOMProject().getBuild() != null) {
            Plugin plugin = getPOMProject().getBuild().findPluginById(groupId, artifactId);
            if(plugin != null && plugin.getConfiguration() != null) {
                Configuration configuration = plugin.getConfiguration();
                configs.forEach((key, value) -> configuration.setSimpleParameter(key, value));
            }
        }
    }

    private Dependency createDependency(org.apache.maven.model.Dependency source) {
        Dependency target = pomModel.getFactory().createDependency();
        target.setGroupId(source.getGroupId());
        target.setArtifactId(source.getArtifactId());
        target.setClassifier(source.getClassifier());
        if (!"jar".equals(source.getType())) {
            target.setType(source.getType());
        }
        target.setScope(source.getScope());
        return target;
    }

    private void updateDependency(org.apache.maven.model.Dependency source, Dependency target) {
        target.setVersion(source.getVersion());
        if (source.getExclusions() != null && !source.getExclusions().isEmpty()) {
            for (org.apache.maven.model.Exclusion sourceExclusion : source.getExclusions()) {
                Exclusion targetExclusion = target.findExclusionById(sourceExclusion.getGroupId(), sourceExclusion.getArtifactId());
                if (targetExclusion == null) {
                    targetExclusion = pomModel.getFactory().createExclusion();
                    targetExclusion.setGroupId(sourceExclusion.getGroupId());
                    targetExclusion.setArtifactId(sourceExclusion.getArtifactId());
                    target.addExclusion(targetExclusion);
                }
            }
        }
    }

    private void registerRepository(List<org.apache.maven.model.Repository> sourceRepositories) {
        if (sourceRepositories.size() > 0) {
            operations.add(pomModel -> {
                Set<String> existingRepositories = getPOMProject().getRepositories() != null ? 
                        getPOMProject().getRepositories()
                                .stream()
                                .map(Repository::getId)
                                .collect(toSet()) 
                        : Collections.EMPTY_SET;
                for (org.apache.maven.model.Repository sourceRepository : sourceRepositories) {
                    if (!existingRepositories.contains(sourceRepository.getId())) {
                        Repository repo = pomModel.getFactory().createRepository();
                        repo.setId(sourceRepository.getId());//isSnapshot ? MavenNbModuleImpl.NETBEANS_SNAPSHOT_REPO_ID : MavenNbModuleImpl.NETBEANS_REPO_ID);
                        repo.setName(sourceRepository.getName());
                        repo.setLayout(sourceRepository.getLayout());
                        repo.setUrl(sourceRepository.getUrl());
                        if (sourceRepository.getSnapshots() != null) {
                            RepositoryPolicy policy = pomModel.getFactory().createReleaseRepositoryPolicy();
                            policy.setEnabled(Boolean.valueOf(sourceRepository.getSnapshots().getEnabled()));
                            repo.setReleases(policy);
                        }
                        getPOMProject().addRepository(repo);
                    }
                }
            });
        }
    }

    public POMManager setSourceVersion(final String version) {
        operations.add(pomModel -> ModelUtils.setSourceLevel(pomModel, version));
        return this;
    }

//    private void downloadDependency() {
//        RequestProcessor.getDefault().post(() -> {
//            project.getLookup().lookup(NbMavenProject.class).triggerDependencyDownload();
//            NbMavenProject.fireMavenProjectReload(mavenProject);
//        });
//    }
    
    @Override
    public POMManager commit() {
        execute();
        if (operations.size() > 0) {
            Utilities.performPOMModelOperations(pomFileObject, operations);
        }
        pomModel.endTransaction();
        return this;
    }
    
    @Override
    public POMManager reload() {
        reload(project);
        return this;
    }
    
    public static void reload(Project project) {
        NbMavenProjectImpl mavenProject = project.getLookup().lookup(NbMavenProjectImpl.class);
        try {
            FileObject pomFileObject = toFileObject(mavenProject.getPOMFile());
            POMModel model = POMModelFactory.getDefault().getModel(Utilities.createModelSource(pomFileObject));
            Utilities.saveChanges(model);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        RP.post(() -> {
            project.getLookup().lookup(NbMavenProject.class).triggerDependencyDownload();
            NbMavenProject.fireMavenProjectReload(mavenProject);
        });

        SwingUtilities.invokeLater(() -> NbMavenProject.fireMavenProjectReload(mavenProject));
    }
//    
//    public static void reloadProject(Project project){
//        NbMavenProject mavenProject = project.getLookup().lookup(NbMavenProject.class);
//            RequestProcessor.getDefault().post(() -> {
//                if(mavenProject.isMavenProjectLoaded()){
//                    downloadDependency(project);
//                }
//            });
//    }
    
    public static void addNBActionMappingProfile(String actionName,
            Project project,
            List<String> goals,
            List<String> profiles,
            Map<String, String> properties) {
        try {
            M2ConfigProvider configProvider = project.getLookup().lookup(M2ConfigProvider.class);
            NetbeansActionMapping actionMapping = ModelHandle2.getMapping(actionName, project, configProvider.getActiveConfiguration());
            if (actionMapping == null) {
                actionMapping = ModelHandle2.getDefaultMapping(actionName, project);
            }
            if (actionMapping == null) {
                actionMapping = new NetbeansActionMapping();
                actionMapping.setActionName(actionName);
            }
            Set<String> existingGoals = new HashSet<>(actionMapping.getGoals());
            for (String goal : goals) {
                if (!existingGoals.contains(goal)) {
                    actionMapping.addGoal(goal);
                }
            }
            Set<String> existingProfiles = new HashSet<>(actionMapping.getActivatedProfiles());
            for (String profile : profiles) {
                if (!existingProfiles.contains(profile)) {
                    actionMapping.addActivatedProfile(profile);
                }
            }
            Map<String, String> existingProperties = actionMapping.getProperties();
            for (String key : properties.keySet()) {
                if (!existingProperties.containsKey(key)) {
                    actionMapping.addProperty(key, properties.get(key));
                }
            }
            
            ModelHandle2.putMapping(actionMapping, project, configProvider.getActiveConfiguration());
        } catch (Exception e) {
            Exceptions.attachMessage(e, "Cannot persist action configuration.");
            Exceptions.printStackTrace(e);
        }
    }
    
    public static void addNBActionMappingGoal(String actionName, Project project, List<String> goals) {
        try {
            M2ConfigProvider configProvider = project.getLookup().lookup(M2ConfigProvider.class);
            NetbeansActionMapping actionMapping = ModelHandle2.getMapping(actionName, project, configProvider.getActiveConfiguration());
            if (actionMapping == null) {
                actionMapping = ModelHandle2.getDefaultMapping(actionName, project);
            }
            if (actionMapping == null) {
                actionMapping = new NetbeansActionMapping();
                actionMapping.setActionName(actionName);
            }
            Set<String> existingGoals = new HashSet<>(actionMapping.getGoals());
            for (String goal : goals) {
                if (!existingGoals.contains(goal)) {
                    actionMapping.addGoal(goal);
                }
            }
            ModelHandle2.putMapping(actionMapping, project, configProvider.getActiveConfiguration());
        } catch (Exception e) {
            Exceptions.attachMessage(e, "Cannot persist action configuration.");
            Exceptions.printStackTrace(e);
        }
    }
}
