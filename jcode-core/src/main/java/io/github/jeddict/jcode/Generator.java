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

import io.github.jeddict.jcode.annotation.Technology;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.openide.util.Lookup;

/**
 *
 * @author Gaurav Gupta
 */
public interface Generator {

    default void preExecute() {
    }
    default void postExecute() {
    }
    void execute() throws IOException;

    static TechContext get(String className) {
        TechContext context = null;
        for (Generator codeGenerator : Lookup.getDefault().lookupAll(Generator.class)) {
            if (codeGenerator.getClass().getSimpleName().equals(className)) {
                context = new TechContext((Class<Generator>)codeGenerator.getClass());
            }
        }
        return context;
    }

//    static List<TechContext> getBusinessService() {
//        return getTechContexts(null, Technology.Type.BUSINESS);
//    }
//
//    static List<TechContext> getController(TechContext parentCodeGenerator) {
//        return getTechContexts(parentCodeGenerator, Technology.Type.CONTROLLER);
//    }
//
//    static List<TechContext> getViewer(TechContext parentCodeGenerator) {
//        return getTechContexts(parentCodeGenerator, Technology.Type.VIEWER);
//    }

        
    static List<TechContext> getBusinessService(boolean microservices) {
        return getTechContexts(null, Technology.Type.BUSINESS, microservices);
    }

    static List<TechContext> getController(TechContext parentCodeGenerator, boolean microservices) {
        return getTechContexts(parentCodeGenerator, Technology.Type.CONTROLLER, microservices);
    }

    static List<TechContext> getViewer(TechContext parentCodeGenerator, boolean microservices) {
        return getTechContexts(parentCodeGenerator, Technology.Type.VIEWER, microservices);
    }

    static List<TechContext> getSiblingTechContexts(TechContext rootTechContext) {
        Class<?> rootCodeGeneratorClass = rootTechContext.getGeneratorClass();
        Technology rootTechnology = rootCodeGeneratorClass.getAnnotation(Technology.class);
        Set<Class<? extends Generator>> rootCodeGeneratorSibling = Arrays.stream(rootTechnology.sibling()).collect(toSet());
        Set<TechContext> siblingCodeGenerators = new LinkedHashSet<>();
        Lookup.getDefault().lookupAll(Generator.class).forEach(codeGenerator -> {
            Technology technology = codeGenerator.getClass().getAnnotation(Technology.class);
            //if direct lookup || reverse lookup
            if (technology != null 
                    && technology.type() == Technology.Type.NONE 
                    && !rootTechContext.isRootTechContext(codeGenerator.getClass())
                    && (rootCodeGeneratorSibling.contains(codeGenerator.getClass())
                    || Arrays.stream(technology.sibling()).filter(sibling -> sibling == rootCodeGeneratorClass).findAny().isPresent())) {
                siblingCodeGenerators.add(new TechContext(rootTechContext, (Class<Generator>)codeGenerator.getClass()));
            }
        });
        return new ArrayList<>(siblingCodeGenerators);
    }

    static List<TechContext> getTechContexts(TechContext parentCodeGenerator, Technology.Type type, boolean microservices) {
        List<TechContext> codeGenerators = new ArrayList<>();//default <none> type //LayerConfigPanel
        List<TechContext> customCodeGenerators = new ArrayList<>();

        Lookup.getDefault().lookupAll(Generator.class)
                .stream()
                .forEach(codeGenerator -> {
                    Technology technology = codeGenerator.getClass().getAnnotation(Technology.class);
                    if(microservices && !technology.microservice()){
                        return;
                    }
                    if (technology.type() == type) {
                        if (technology.panel() == LayerConfigPanel.class) {
                            codeGenerators.add(new TechContext((Class<Generator>) codeGenerator.getClass()));
                        } else {
                            if (parentCodeGenerator != null) {
                                for (Class<? extends Generator> genClass : technology.parents()) {
                                    if (genClass == parentCodeGenerator.getGeneratorClass()) {
                                        customCodeGenerators.add(new TechContext((Class<Generator>) codeGenerator.getClass()));
                                        break;
                                    }
                                }
                                for (Class<? extends Generator> genClass : parentCodeGenerator.getTechnology().children()) {
                                    if (genClass == codeGenerator.getClass()) {
                                        customCodeGenerators.add(new TechContext((Class<Generator>) codeGenerator.getClass()));
                                        break;
                                    }
                                }
                            } else {
                                customCodeGenerators.add(new TechContext((Class<Generator>) codeGenerator.getClass()));
                            }
                        }
                    }
                });
        codeGenerators.addAll(customCodeGenerators);
        return codeGenerators.stream()
                .sorted((t1,t2) -> Integer.compare(t1.getTechnology().listIndex() , t2.getTechnology().listIndex()))
                .collect(toList());
    }
}
