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
import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import io.github.jeddict.jpa.spec.AssociationOverride;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.validator.column.JoinColumnValidator;
import io.github.jeddict.jpa.spec.validator.table.JoinTableValidator;

/**
 *
 * @author Gaurav Gupta
 */
public class AssociationOverrideSpecMetadata extends AssociationOverrideMetadata {

    private AssociationOverrideSpecMetadata() {
    }

    public static AssociationOverrideSpecMetadata getInstance(AssociationOverride associationOverride) {
        AssociationOverrideSpecMetadata accessor = new AssociationOverrideSpecMetadata();
        accessor.setName(associationOverride.getName());
        if (!JoinTableValidator.isEmpty(associationOverride.getJoinTable())) {
            accessor.setJoinTable(associationOverride.getJoinTable().getAccessor());
        }
        JoinColumnValidator.filter(associationOverride.getJoinColumn());
        accessor.setJoinColumns(associationOverride.getJoinColumn().stream().map(JoinColumn::getAccessor).collect(toList()));

        return accessor;
    }

}
