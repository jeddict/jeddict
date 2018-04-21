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
package io.github.jeddict.db.accessor;

import static java.util.stream.Collectors.toList;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedIdAccessor;
import io.github.jeddict.jpa.spec.EmbeddedId;
import io.github.jeddict.jpa.spec.Inheritance;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.validator.override.AttributeValidator;

/**
 *
 * @author Gaurav Gupta
 */
public class EmbeddedIdSpecAccessor extends EmbeddedIdAccessor {

    private final EmbeddedId embeddedId;
    private boolean inherit;

    private EmbeddedIdSpecAccessor(EmbeddedId embeddedId) {
        this.embeddedId = embeddedId;
    }

    public static EmbeddedIdSpecAccessor getInstance(EmbeddedId embeddedId, boolean inherit) {
        EmbeddedIdSpecAccessor accessor = new EmbeddedIdSpecAccessor(embeddedId);
        accessor.inherit = inherit;
        accessor.setName(embeddedId.getName());
        accessor.setAttributeType(embeddedId.getAttributeType());
        AttributeValidator.filter(embeddedId);
        accessor.setAttributeOverrides(embeddedId.getAttributeOverride().stream().map(AttributeOverrideSpecMetadata::getInstance).collect(toList()));
        return accessor;

    }

    @Override
    public void process() {
        super.process();
        getMapping().setProperty(Attribute.class, embeddedId);
        getMapping().setProperty(Inheritance.class, inherit);//Remove inherit functionality , once eclipse support dynamic mapped super class
    }

}
