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

import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.extend.ProjectType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
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

    private TechContext repositoryTechContext;
    private TechContext controllerTechContext;
    private TechContext viewerTechContext;

    private final Map<Project, StringBuilder> webDescriptorContent = new HashMap<>();
    private final Map<Project, StringBuilder> webDescriptorTestContent = new HashMap<>();

    private final Set<Environment> environments = new LinkedHashSet<>();

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

    public TechContext getRepositoryTechContext() {
        return repositoryTechContext;
    }

    public void setRepositoryTechContext(TechContext bussinesTechContext) {
        this.repositoryTechContext = bussinesTechContext;
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

    public Environment getEnvironment(String name) {
        return environments.stream()
                .filter(env -> env.getName().equals(name))
                .findAny()
                .orElseGet(() -> {
            Environment environment = new Environment(name);
            environments.add(environment);
            return environment;
        });
    }

    public Set<Environment> getEnvironments() {
        return environments;
    }

}
