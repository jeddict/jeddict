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
package org.netbeans.orm.converter.compiler;

import org.netbeans.orm.converter.util.ORMConverterUtil;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public class PersistenceXMLUnitSnippet implements Snippet {

    private static final String TEMPLATE_FILENAME = "persistence.vm";
    private static final String DEFAULT_PU_NAME = "DEFAULT_PU";

    private String name = DEFAULT_PU_NAME;
    private List<String> classNames = new ArrayList<String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public List<String> getClassNames() {
        return classNames;
    }

    public void setClassNames(List<String> classes) {
        this.classNames = classes;
    }

    public String getSnippet() throws InvalidDataException {
        try {

            Template template = ORMConverterUtil.getTemplate(TEMPLATE_FILENAME);

            VelocityContext context = new VelocityContext();
            context.put("pu", this);

            ByteArrayOutputStream generatedXML = new ByteArrayOutputStream();

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(generatedXML));

            if (template != null) {
                template.merge(context, writer);
            }

            writer.flush();
            writer.close();

            return generatedXML.toString();

        } catch (Exception e) {
            throw new InvalidDataException(e);
        }
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
