/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.generator.repository;

import io.github.jeddict.jcode.ApplicationConfigData;
import io.github.jeddict.jcode.Generator;
import io.github.jeddict.jcode.annotation.ConfigData;
import io.github.jeddict.jcode.annotation.Technology;
import static io.github.jeddict.jcode.annotation.Technology.Type.REPOSITORY;
import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.FG_RED;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import io.github.jeddict.jpa.spec.Entity;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT;
import static io.github.jeddict.jcode.util.FileUtil.expandTemplate;
import static io.github.jeddict.jcode.util.StringHelper.firstLower;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import io.github.jeddict.jpa.spec.EntityMappings;
import org.netbeans.modules.websvc.saas.codegen.java.support.SourceGroupSupport;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 * Generates sample repository classes.
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(
        type = REPOSITORY,
        label = "Sample Repository Generator",
        panel = SampleRepositoryConfigPanel.class
)
public final class SampleRepositoryGenerator implements Generator {

    private static final String TEMPLATE = "io/github/jeddict/generator/repository/template/";
    private static final String FACADE_ABSTRACT = "Abstract"; //NOI18N

    /**
     * Panel data
     */
    @ConfigData
    private SampleRepositoryConfigData beanData;

    /**
     * Contains information about classes designed in modeler
     */
    @ConfigData
    private EntityMappings entityMapping;

    /**
     * Prints log in console and shows progress
     */
    @ConfigData
    private ProgressHandler handler;

    /**
     * Contains information about project path and type
     */
    @ConfigData
    private ApplicationConfigData appConfigData;

    @Override
    public void execute() throws IOException {
        FileObject targetFolder = SourceGroupSupport.getFolderForPackage(
                appConfigData.getTargetSourceGroup(), beanData.getPackage(), true);

        handler.progress(Console.wrap("Generating Sample Repository", FG_RED));

        //create the abstract facade class
        String abstractFileName = beanData.getPrefixName() + FACADE_ABSTRACT + beanData.getSuffixName();
        handler.progress(abstractFileName);

        Map<String, Object> params = new HashMap<>();
        params.put("AbstractFacade", abstractFileName);
        params.put("package", beanData.getPackage());
        expandTemplate(
                TEMPLATE + "AbstractFacade.java.ftl", // freemarker template path
                targetFolder, // target folder to be generated
                abstractFileName + '.' + JAVA_EXT, // target file name to be generated
                params // parameters for template
        );

        for (Entity entity : entityMapping.getEntity()) {
            String facadeFileName = beanData.getPrefixName() + entity.getClazz() + beanData.getSuffixName();
            handler.progress(facadeFileName);

            params.put("EntityFacade", facadeFileName);
            params.put("EntityClass", entity.getClazz());
            params.put("EntityClass_FQN", entity.getFQN());
            params.put("entityInstance", firstLower(entity.getClazz()));
            params.put("PU", entityMapping.getPersistenceUnitName());
            params.put("package", entity.getAbsolutePackage(beanData.getPackage()));

            expandTemplate(
                    TEMPLATE + "EntityFacade.java.ftl", // freemarker template path
                    targetFolder, // target folder to be generated
                    facadeFileName + '.' + JAVA_EXT, // target file name to be generated
                    params // parameters for template
            );
        }
    }

}
