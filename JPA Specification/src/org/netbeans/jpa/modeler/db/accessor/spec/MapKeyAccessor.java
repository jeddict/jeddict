/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.jpa.modeler.db.accessor.spec;

import java.util.List;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappedKeyMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.EnumeratedMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TemporalMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.MapKeyMetadata;

public interface MapKeyAccessor extends MappedKeyMapAccessor {

    void setMapKeyEnumerated(EnumeratedMetadata mapKeyEnumerated);

    void setMapKeyClassName(String mapKeyClassName);

//    void setTargetClassName(String targetClassName);

    void setMapKeyTemporal(TemporalMetadata mapKeyTemporal);

    void setMapKeyColumn(ColumnMetadata mapKeyColumn);

    void setMapKeyAttributeOverrides(List<AttributeOverrideMetadata> mapKeyAttributeOverrides);

    void setMapKeyJoinColumns(List<JoinColumnMetadata> mapKeyJoinColumns);

    void setMapKey(MapKeyMetadata mapKey);
    
    void setAttributeType(String attributeType);
    
}
