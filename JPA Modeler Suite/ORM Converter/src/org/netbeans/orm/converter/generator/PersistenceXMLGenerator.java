/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.orm.converter.generator;

import org.netbeans.orm.converter.compiler.PersistenceXMLUnitSnippet;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.util.ORMConvLogger;
import org.netbeans.orm.converter.util.ORMConverterUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersistenceXMLGenerator {

    private static final String PERSISTENCE_XML_FILE_NAME = "persistence.xml";
    private static final String META_INF_DIR = "META-INF";

    private static Logger logger = ORMConvLogger.getLogger(
            PersistenceXMLGenerator.class);

    private String puName = null;

    private Collection<ClassDefSnippet> classDefs = null;

    public PersistenceXMLGenerator(Collection<ClassDefSnippet> classDefs) {
        this.classDefs = classDefs;
    }

    public String getPUName() {
        return puName;
    }

    public void setPUName(String puName) {
        this.puName = puName;
    }

    public void generatePersistenceXML(String destDir) {

        List<String> classNames = new ArrayList<String>();

        /*
         * Classes to scan for annotations.  It should be annotated
         * with either @Entity, @Embeddable or @MappedSuperclass.         *
         */
        for (ClassDefSnippet classDef : classDefs) {
            classNames.add(classDef.getClassHelper().getFQClassName());
        }

        PersistenceXMLUnitSnippet persistenceXMLUnit = new PersistenceXMLUnitSnippet();

        persistenceXMLUnit.setName(puName);
        persistenceXMLUnit.setClassNames(classNames);

        try {
            String content = persistenceXMLUnit.getSnippet();

            writeContent(destDir, content);

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "compiler_error", ex);
        }
    }

    private void writeContent(String destDir, String content)
            throws IOException {

        File xmlFile = ORMConverterUtil.createFile(destDir, META_INF_DIR,
                PERSISTENCE_XML_FILE_NAME);

        ORMConverterUtil.writeContent(content, xmlFile);

        System.out.println(
                "Java: Generated file :" + xmlFile.getAbsolutePath());
    }
}
