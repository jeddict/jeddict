/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.orm.generator.service.staticmetamodel;

import io.github.jeddict.orm.generator.compiler.InvalidDataException;
import io.github.jeddict.orm.generator.compiler.def.ClassDefSnippet;
import io.github.jeddict.orm.generator.util.ClassHelper;
import io.github.jeddict.orm.generator.util.ImportSet;
import io.github.jeddict.util.StringUtils;
import static io.github.jeddict.util.StringUtils.isNotBlank;

/**
 *
 * The StaticMetamodel annotation specifies that the class is a metamodel class
 * that represents the entity, mapped superclass, or embeddable class designated
 * by the value element.
 */
public class StaticMetamodelClassDefSnippet extends ClassDefSnippet {

    private static final String STATIC_METAMODEL_TEMPLATE_FILENAME = "staticmetamodel.ftl";

    private String value;//i.e: @StaticMetamodel( Person.class )   Class being modelled by the annotated class. //entity, mapped superclass, or embeddable class
    private final ClassHelper entityClassHelper = new ClassHelper();

    @Override
    protected String getTemplateName() {
        return STATIC_METAMODEL_TEMPLATE_FILENAME;
    }
    
    @Override
    public ImportSet getImportSet() throws InvalidDataException {
        ImportSet imports = super.getImportSet();
        if (isNotBlank(getEntityPackageName())
                && !StringUtils.equals(getPackageName(), getEntityPackageName())) {
            imports.add(entityClassHelper.getFQClassName());
        }
        return imports;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    public String getEntityPackageName() {
        return entityClassHelper.getPackageName();
    }

    public void setEntityPackageName(String packageName) {
        this.entityClassHelper.setPackageName(packageName);
    }

    public String getEntityClassName() {
        return entityClassHelper.getClassName();
    }

    public ClassHelper getEntityClassHelper() {
        return entityClassHelper;
    }

    public void setEntityClassName(String className) {
        entityClassHelper.setClassName(className);
    }
}
