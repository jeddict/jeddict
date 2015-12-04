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

import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConverterUtil;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public class LifecycleListenerSnippet implements WritableSnippet {

    private static final String TEMPLATE_FILENAME = "lifecyclelistener.vm";

    private ClassHelper classHelper = new ClassHelper();

    private List<MethodDefSnippet> methodDefs = Collections.EMPTY_LIST;

    public void addMethodDef(MethodDefSnippet methodDef) {

        if (methodDefs.isEmpty()) {
            methodDefs = new ArrayList<MethodDefSnippet>();
        }

        methodDefs.add(methodDef);
    }

    public List<MethodDefSnippet> getMethodDefs() {
        return methodDefs;
    }

    public void setMethodDefs(List<MethodDefSnippet> methodDefs) {
        if (methodDefs != null) {
            this.methodDefs = methodDefs;
        }
    }

    public String getClassName() {
        return classHelper.getClassName();
    }

    public void setClassName(String className) {
        classHelper.setClassName(className);
    }

    public String getPackageName() {
        return classHelper.getPackageName();
    }

    public void setPackageName(String packageName) {
        this.classHelper.setPackageName(packageName);
    }

    public ClassHelper getClassHelper() {
        return classHelper;
    }

    public String getSnippet() throws InvalidDataException {
        try {

            Template template = ORMConverterUtil.getTemplate(TEMPLATE_FILENAME);

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("lifeCycleListener", this);

            ByteArrayOutputStream generatedClass = new ByteArrayOutputStream();

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(generatedClass));

            if (template != null) {
                template.merge(velocityContext, writer);
            }

            writer.flush();
            writer.close();

            return generatedClass.toString();

        } catch (Exception e) {
            throw new InvalidDataException(e);
        }
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {
        //Sort and eliminate duplicates
        Collection<String> importSnippets = new TreeSet<String>();

        for (MethodDefSnippet methodDef : methodDefs) {
            importSnippets.addAll(methodDef.getImportSnippets());
        }

        return ORMConverterUtil.processedImportStatements(importSnippets);
    }
}
