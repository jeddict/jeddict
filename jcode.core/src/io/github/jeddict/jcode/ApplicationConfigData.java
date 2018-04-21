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
package io.github.jeddict.jcode;

import static io.github.jeddict.jcode.RegistryType.CONSUL;
import static io.github.jeddict.jcode.util.POMManager.updateNBActionMapping;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.extend.ProjectType;
import java.io.Serializable;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;

/**
 *
 * @author Gaurav Gupta
 */
public class ApplicationConfigData implements Serializable {

    private ProjectType projectType;

    private Project targetProject;
    private SourceGroup targetSourceGroup;
    private String targetPackage;
    private String targetArtifactId;

    private Project gatewayProject;
    private SourceGroup gatewaySourceGroup;
    private String gatewayPackage;
    private String gatewayArtifactId;

    private boolean completeApplication;
    private EntityMappings entityMappings;

    private TechContext bussinesTechContext;
    private TechContext controllerTechContext;
    private TechContext viewerTechContext;

    private final Map<Project, StringBuilder> webDescriptorContent = new HashMap<>();
    private final Map<Project, StringBuilder> webDescriptorTestContent = new HashMap<>();

    private final Set<String> profiles = new LinkedHashSet<>();
    private final Set<String> goals = new LinkedHashSet<>();
    private final List<String> buildProperties = new ArrayList<>();
    
    private RegistryType registryType = RegistryType.CONSUL;

    public void addWebDescriptorContent(String content, Project project) {
        StringBuilder sb = webDescriptorContent.get(project);
        if (sb == null) {
            sb = new StringBuilder();
            webDescriptorContent.put(project, sb);
        }
        sb.append("\n").append(content);
    }

    public Map<Project, StringBuilder> getWebDescriptorContent() {
        return webDescriptorContent;
    }

    public void addWebDescriptorTestContent(String content, Project project) {
        StringBuilder sb = webDescriptorTestContent.get(project);
        if (sb == null) {
            sb = new StringBuilder();
            webDescriptorTestContent.put(project, sb);
        }
        sb.append("\n").append(content);
    }

    public Map<Project, StringBuilder> getWebDescriptorTestContent() {
        return webDescriptorTestContent;
    }

    public void addProfile(String profile) {
        profiles.add(profile);
    }

    public void removeProfile(String profile) {
        profiles.remove(profile);
    }

    public String getProfiles() {
        return String.join(",", profiles);
    }
    
    public void addProfileAndActivate(Project project, String profile) {
        addProfile(profile);
        updateNBActionMapping("run", project, asList(profile));
        updateNBActionMapping("run.single.deploy", project, asList(profile));
        updateNBActionMapping("debug", project, asList(profile));
        updateNBActionMapping("debug.single.deploy", project, asList(profile));
    }
    
    public void addGoal(String goal) {
        goals.add(goal);
    }

    public void removeGoal(String goal) {
        goals.remove(goal);
    }

    public String getGoals() {
        return String.join(" ", goals);
    }
    
    public void addBuildProperty(String propertyName, String propertyValue) {
        String buildProperty = "-D" + propertyName + '=' + propertyValue;
        buildProperties.add(buildProperty);
    }

    public void removeBuildProperty(String propertyName) {
        buildProperties.removeIf(element -> element.startsWith("-D" + propertyName + '='));
    }

    public String getBuildProperties() {
        return String.join(" ", buildProperties);
    }

    public TechContext getBussinesTechContext() {
        return bussinesTechContext;
    }

    public void setBussinesTechContext(TechContext bussinesTechContext) {
        this.bussinesTechContext = bussinesTechContext;
    }

    public TechContext getControllerTechContext() {
        return controllerTechContext;
    }

    public void setControllerTechContext(TechContext controllerTechContext) {
        this.controllerTechContext = controllerTechContext;
    }

    public TechContext getViewerTechContext() {
        return viewerTechContext;
    }

    public void setViewerTechContext(TechContext viewerLayerTechContext) {
        this.viewerTechContext = viewerLayerTechContext;
    }

    /**
     * @return the targetProject
     */
    public Project getTargetProject() {
        return targetProject;
    }

    /**
     * @param targetProject the targetProject to set
     */
    public void setTargetProject(Project targetProject) {
        this.targetProject = targetProject;
    }

    /**
     * @return the targetSourceGroup
     */
    public SourceGroup getTargetSourceGroup() {
        return targetSourceGroup;
    }

    /**
     * @param targetSourceGroup the targetSourceGroup to set
     */
    public void setTargetSourceGroup(SourceGroup targetSourceGroup) {
        this.targetSourceGroup = targetSourceGroup;
    }

    /**
     * @return the targetPackage
     */
    public String getTargetPackage() {
        return targetPackage;
    }

    /**
     * @param targetPackage the targetPackage to set
     */
    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    /**
     * @return the targetContextPath
     */
    public String getTargetContextPath() {
        return targetArtifactId.toLowerCase();
    }

    /**
     * @return the targetArtifactId
     */
    public String getTargetArtifactId() {
        return targetArtifactId;
    }

    /**
     * @param targetArtifactId the targetArtifactId to set
     */
    public void setTargetArtifactId(String targetArtifactId) {
        this.targetArtifactId = targetArtifactId;
    }

    /**
     * @return the gatewayProject
     */
    public Project getGatewayProject() {
        return gatewayProject;
    }

    /**
     * @param gatewayProject the gatewayProject to set
     */
    public void setGatewayProject(Project gatewayProject) {
        this.gatewayProject = gatewayProject;
    }

    /**
     * @return the gatewaySourceGroup
     */
    public SourceGroup getGatewaySourceGroup() {
        return gatewaySourceGroup;
    }

    /**
     * @param gatewaySourceGroup the gatewaySourceGroup to set
     */
    public void setGatewaySourceGroup(SourceGroup gatewaySourceGroup) {
        this.gatewaySourceGroup = gatewaySourceGroup;
    }

    /**
     * @return the gatewayPackage
     */
    public String getGatewayPackage() {
        return gatewayPackage;
    }

    /**
     * @param gatewayPackage the gatewayPackage to set
     */
    public void setGatewayPackage(String gatewayPackage) {
        this.gatewayPackage = gatewayPackage;
    }

    /**
     * @return the gatewayContextPath
     */
    public String getGatewayContextPath() {
        return gatewayArtifactId;
    }

    /**
     * @return the gatewayArtifactId
     */
    public String getGatewayArtifactId() {
        return gatewayArtifactId;
    }

    /**
     * @param gatewayArtifactId the gatewayArtifactId to set
     */
    public void setGatewayArtifactId(String gatewayArtifactId) {
        this.gatewayArtifactId = gatewayArtifactId;
    }

    public boolean isMonolith() {
        return projectType == ProjectType.MONOLITH;
    }

    public boolean isMicroservice() {
        return projectType == ProjectType.MICROSERVICE;
    }

    public boolean isGateway() {
        return projectType == ProjectType.GATEWAY;
    }

    /**
     * @return the projectType
     */
    public ProjectType getProjectType() {
        return projectType;
    }

    /**
     * @param projectType the projectType to set
     */
    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    /**
     * @return the entityMappings
     */
    public EntityMappings getEntityMappings() {
        return entityMappings;
    }

    /**
     * @param entityMappings the entityMappings to set
     */
    public void setEntityMappings(EntityMappings entityMappings) {
        this.entityMappings = entityMappings;
    }

    /**
     * @return the completeApplication
     */
    public boolean isCompleteApplication() {
        return completeApplication;
    }

    /**
     * @param completeApplication the completeApplication to set
     */
    public void setCompleteApplication(boolean completeApplication) {
        this.completeApplication = completeApplication;
    }

    /**
     * @return the registryType
     */
    public RegistryType getRegistryType() {
        return registryType;
    }

    /**
     * @param registryType the registryType to set
     */
    public void setRegistryType(RegistryType registryType) {
        this.registryType = registryType;
    }

}
