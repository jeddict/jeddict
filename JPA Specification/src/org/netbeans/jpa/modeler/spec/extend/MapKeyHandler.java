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
package org.netbeans.jpa.modeler.spec.extend;

import java.util.List;
import java.util.Set;
import org.netbeans.jpa.modeler.spec.AttributeOverride;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EnumType;
import org.netbeans.jpa.modeler.spec.ForeignKey;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.MapKey;
import org.netbeans.jpa.modeler.spec.TemporalType;

/**
 *
 * @author Gaurav Gupta
 */
public interface MapKeyHandler {

    Attribute getMapKeyAttribute();//MapKeyType.EXT is default type so always perform null check or getValidatedMapKeyType() == EXT

    void setMapKeyAttribute(Attribute mapKeyAttribute);

    String getMapKeyDataTypeLabel();
    
    MapKeyType getValidatedMapKeyType();

    MapKeyType getMapKeyType();

    void setMapKeyType(MapKeyType mapKeyType);

    String getMapKeyAttributeType();

    void setMapKeyAttributeType(String mapKeyAttributeType);

    Entity getMapKeyEntity();

    void setMapKeyEntity(Entity mapKeyEntity);

    Embeddable getMapKeyEmbeddable();

    void setMapKeyEmbeddable(Embeddable mapKeyEmbeddable);

    TemporalType getMapKeyTemporal();

    void setMapKeyTemporal(TemporalType value);

    EnumType getMapKeyEnumerated();

    void setMapKeyEnumerated(EnumType value);

    Column getMapKeyColumn();

    void setMapKeyColumn(Column value);

    List<JoinColumn> getMapKeyJoinColumn();

    ForeignKey getMapKeyForeignKey();

    void setMapKeyForeignKey(ForeignKey value);

    Set<AttributeOverride> getMapKeyAttributeOverride();

    public void resetMapAttribute();

    MapKey getMapKey();
            
    void setMapKey(MapKey value);
}
