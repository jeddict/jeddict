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
package io.github.jeddict.jpa.spec.extend;

import java.util.List;
import java.util.Set;
import io.github.jeddict.jpa.spec.AttributeOverride;
import io.github.jeddict.jpa.spec.Column;
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EnumType;
import io.github.jeddict.jpa.spec.ForeignKey;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.MapKey;
import io.github.jeddict.jpa.spec.TemporalType;

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
    
    String getDefaultMapKeyColumnName();
    
        //used in db modeler element-config.xml expression
    boolean isTextMapKeyAttributeType();

    boolean isPrecisionpMapKeyAttributeType();

    boolean isScaleMapKeyAttributeType();
}
