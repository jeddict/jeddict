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
package io.github.jeddict.docs;

import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.reveng.doc.DocWizardDescriptor;
import io.github.jeddict.test.BaseModelTest;
import io.github.jeddict.test.ProjectBuilder;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.netbeans.api.project.Project;

/**
 *
 * @author jGauravGupta
 */
public class DocumentReverseEngineeringTest extends BaseModelTest {

    @Test
    void testXmlReveng() throws Exception {
        validateEntity(parseDocument("sample.xml", true, true, true));
        validateClass(parseDocument("sample.xml", false, false, false));
    }

    @Test
    void testJsonReveng() throws Exception {
        validateEntity(parseDocument("sample.json", true, true, true));
        validateClass(parseDocument("sample.json", false, false, false));
    }

    @Test
    void testYmlReveng() throws Exception {
        validateEntity(parseDocument("sample.yml", true, true, true));
        validateClass(parseDocument("sample.yml", false, false, false));
    }

    private EntityMappings parseDocument(String name, boolean jpaSupport, boolean jsonbSupport, boolean jaxbSupport) throws Exception {
        Project project = new ProjectBuilder("document-reverse-engineering-test").get();
        EntityMappings entityMappings = createEntityMappings();

        String docFile = DocumentReverseEngineeringTest.class.getResource(name).toURI().getPath();
        DocWizardDescriptor docWizardDescriptor = new DocWizardDescriptor(
                project,
                docFile,
                jpaSupport,
                jsonbSupport,
                jaxbSupport
        );
        docWizardDescriptor.generate(getProgressReporter(), entityMappings);
        return entityMappings;
    }

    private void validateEntity(EntityMappings entityMappings) {
        assertTrue(entityMappings.findEntity("CatalogItem").isPresent());
        assertTrue(entityMappings.findEntity("ColorSwatch").isPresent());
        assertTrue(entityMappings.findEntity("Product").isPresent());
        assertTrue(entityMappings.findEntity("Size").isPresent());

        assertTrue(entityMappings.findEntity("ColorSwatch").get()
                .getAttributes()
                .findBasic("image")
                .isPresent());

        assertTrue(entityMappings.findEntity("CatalogItem").get()
                .getAttributes()
                .findBasic("gender")
                .isPresent());

        assertTrue(entityMappings.findEntity("CatalogItem").get()
                .getAttributes()
                .findBasic("itemNumber")
                .isPresent());

        assertTrue(entityMappings.findEntity("CatalogItem").get()
                .getAttributes()
                .findBasic("price")
                .isPresent());

        assertTrue(entityMappings.findEntity("Product").get()
                .getAttributes()
                .findBasic("productImage")
                .isPresent());

        assertTrue(entityMappings.findEntity("Product").get()
                .getAttributes()
                .findBasic("description")
                .isPresent());

        assertTrue(entityMappings.findEntity("Product").get()
                .getAttributes()
                .findOneToMany("catalogItem")
                .isPresent());

    }
    
    private void validateClass(EntityMappings entityMappings) {
        assertTrue(entityMappings.findBeanClass("CatalogItem").isPresent());
        assertTrue(entityMappings.findBeanClass("ColorSwatch").isPresent());
        assertTrue(entityMappings.findBeanClass("Product").isPresent());
        assertTrue(entityMappings.findBeanClass("Size").isPresent());

        assertTrue(entityMappings.findBeanClass("ColorSwatch").get()
                .getAttributes()
                .findBasic("image")
                .isPresent());

        assertTrue(entityMappings.findBeanClass("CatalogItem").get()
                .getAttributes()
                .findBasic("gender")
                .isPresent());

        assertTrue(entityMappings.findBeanClass("CatalogItem").get()
                .getAttributes()
                .findBasic("itemNumber")
                .isPresent());

        assertTrue(entityMappings.findBeanClass("CatalogItem").get()
                .getAttributes()
                .findBasic("price")
                .isPresent());

        assertTrue(entityMappings.findBeanClass("Product").get()
                .getAttributes()
                .findBasic("productImage")
                .isPresent());

        assertTrue(entityMappings.findBeanClass("Product").get()
                .getAttributes()
                .findBasic("description")
                .isPresent());

        assertTrue(entityMappings.findBeanClass("Product").get()
                .getAttributes()
                .findOneToMany("catalogItem")
                .isPresent());

    }
}
