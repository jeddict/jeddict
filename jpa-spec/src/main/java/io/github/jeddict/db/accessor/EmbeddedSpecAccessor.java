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
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import io.github.jeddict.jpa.spec.Convert;
import io.github.jeddict.jpa.spec.Embedded;
import io.github.jeddict.jpa.spec.Inheritance;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.validator.override.AssociationValidator;
import io.github.jeddict.jpa.spec.validator.override.AttributeValidator;

/**
 *
 * @author Gaurav Gupta
 */
public class EmbeddedSpecAccessor extends EmbeddedAccessor {

    private final Embedded embedded;
    private boolean inherit;

    private EmbeddedSpecAccessor(Embedded embedded) {
        this.embedded = embedded;
    }

    public static EmbeddedSpecAccessor getInstance(Embedded embedded, boolean inherit) {
        EmbeddedSpecAccessor accessor = new EmbeddedSpecAccessor(embedded);
        accessor.inherit = inherit;
        accessor.setName(embedded.getName());
        accessor.setAttributeType(embedded.getAttributeType());
        AttributeValidator.filter(embedded);
        accessor.setAttributeOverrides(embedded.getAttributeOverride().stream().map(AttributeOverrideSpecMetadata::getInstance).collect(toList()));
        AssociationValidator.filter(embedded);
        accessor.setAssociationOverrides(embedded.getAssociationOverride().stream().map(AssociationOverrideSpecMetadata::getInstance).collect(toList()));
        accessor.setConverts(embedded.getConverts().stream().map(Convert::getAccessor).collect(toList()));
        return accessor;
    }

    @Override
    public void process() {
        super.process();
        getMapping().setProperty(Attribute.class, embedded);
        getMapping().setProperty(Inheritance.class, inherit);//Remove inherit functionality , once eclipse support dynamic mapped super class
    }

}
