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
package io.github.jeddict.orm.generator.service.packageinfo;

import org.apache.commons.lang.StringUtils;
import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.compiler.Snippet;
import io.github.jeddict.orm.generator.compiler.def.ClassDefSnippet;
import io.github.jeddict.orm.generator.util.ImportSet;

public class PackageInfoClassDefSnippet extends ClassDefSnippet {

    private static final String JAXB_PACKAGE_INFO_TEMPLATE_FILENAME = "package-info.vm";

    private String namespace;//ex : @XmlSchema(namespace = "http://www.example.org/customer", elementFormDefault = XmlNsForm.QUALIFIED)

    
    @Override
    protected String getTemplateName() {
        return JAXB_PACKAGE_INFO_TEMPLATE_FILENAME;
    }
    
    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public boolean isJaxbMetadataExist() {
        return StringUtils.isNotBlank(getNamespace());
    }

    @Override
    public ImportSet getImportSet() throws InvalidDataException {
        ImportSet importSnippets = new ImportSet();
        if (isJaxbMetadataExist()) {
            importSnippets.add("javax.xml.bind.annotation.*");
        }
        for (Snippet snippet : getJSONBSnippets()) {
            importSnippets.addAll(snippet.getImportSnippets());
        }
        return importSnippets;
    }
}
