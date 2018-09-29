/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jeddict.jcode;

import static io.github.jeddict.jcode.util.POMManager.addNBActionMappingGoal;
import static io.github.jeddict.jcode.util.POMManager.addNBActionMappingProfile;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.EMPTY;
import org.netbeans.api.project.Project;

/**
 *
 * @author jGauravGupta
 */
public class Environment {

    private final String name;

    private final Set<String> preCommands = new LinkedHashSet<>();
    private final Set<String> postCommands = new LinkedHashSet<>();
    private final Set<String> profiles = new LinkedHashSet<>();
    private final Set<String> goals = new LinkedHashSet<>();
    private final Set<String> buildProperties = new LinkedHashSet<>();
    private final Map<String, String> config = new HashMap<>();

    private static final String COMMAND_SEPARATOR = " && ";

    public Environment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Environment addPreCommand(String preCommand) {
        preCommands.add(preCommand);
        return this;
    }

    public Environment removePreCommand(String preCommand) {
        preCommands.remove(preCommand);
        return this;
    }

    public String getPreCommands() {
        String plainCommand = String.join(COMMAND_SEPARATOR, preCommands);
        if (!plainCommand.isEmpty()) {
            return plainCommand + COMMAND_SEPARATOR;
        }
        return EMPTY;
    }

    public Environment addPostCommand(String postCommand) {
        postCommands.add(postCommand);
        return this;
    }

    public Environment removePostCommand(String postCommand) {
        postCommands.remove(postCommand);
        return this;
    }

    public String getPostCommands() {
        String plainCommand = String.join(COMMAND_SEPARATOR, postCommands);
        if (!plainCommand.isEmpty()) {
            return plainCommand + COMMAND_SEPARATOR;
        }
        return EMPTY;
    }

    public Environment addProfile(String profile) {
        profiles.add(profile);
        return this;
    }

    public Environment removeProfile(String profile) {
        profiles.remove(profile);
        return this;
    }

    public String getProfiles() {
        return String.join(",", profiles);
    }

    public Environment addProfileAndActivate(String profile, Project project) {
        addProfile(profile);

        Map<String, String> properties = new HashMap<>();

        addNBActionMappingProfile("build", project,
                asList("install"),
                asList(profile),
                properties);

        addNBActionMappingProfile("rebuild", project,
                asList("clean", "install"),
                asList(profile),
                properties);

        properties.put("netbeans.deploy", "true");
        addNBActionMappingProfile("run", project,
                asList("package"),
                asList(profile),
                properties);

        properties.put("netbeans.deploy.clientUrlPart", "${webpagePath}");
        addNBActionMappingProfile("run.single.deploy", project,
                asList("package"),
                asList(profile),
                properties);

        properties = new HashMap<>();
        properties.put("netbeans.deploy", "true");
        properties.put("netbeans.deploy.debugmode", "true");
        addNBActionMappingProfile("debug", project,
                asList("package"),
                asList(profile),
                properties);

        properties = new HashMap<>();
        properties.put("netbeans.deploy", "true");
        properties.put("netbeans.deploy.debugmode", "true");
        properties.put("netbeans.deploy.clientUrlPart", "${webpagePath}");
        addNBActionMappingProfile("debug.single.deploy", project,
                asList("package"),
                asList(profile),
                properties);
        return this;
    }

    public Environment addGoal(String goal) {
        goals.add(goal);
        return this;
    }

    public Environment removeGoal(String goal) {
        goals.remove(goal);
        return this;
    }

    public String getGoals() {
        return String.join(" ", goals);
    }

    public Environment addGoalAndActivate(String goal, Project project) {
        addGoal(goal);
        addNBActionMappingGoal("run", project, asList(goal));
        addNBActionMappingGoal("run.single.deploy", project, asList(goal));
        addNBActionMappingGoal("debug", project, asList(goal));
        addNBActionMappingGoal("debug.single.deploy", project, asList(goal));
        return this;
    }

    public Environment addBuildProperty(String propertyName, String propertyValue) {
        String buildProperty = "-D" + propertyName + '=' + propertyValue;
        buildProperties.add(buildProperty);
        return this;
    }

    public Environment removeBuildProperty(String propertyName) {
        buildProperties.removeIf(element -> element.startsWith("-D" + propertyName + '='));
        return this;
    }

    public String getBuildProperties() {
        return String.join(" ", buildProperties);
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public Environment addConfig(String key, String value) {
        config.put(key, value);
        return this;
    }

    public Environment removeConfig(String key) {
        config.remove(key);
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Environment other = (Environment) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "Environment{" + name + '}';
    }

}
