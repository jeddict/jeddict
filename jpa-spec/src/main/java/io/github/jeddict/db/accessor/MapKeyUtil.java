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
import org.apache.commons.lang3.StringUtils;
import static io.github.jeddict.jcode.util.Constants.LIST_TYPE;
import io.github.jeddict.db.accessor.spec.MapKeyAccessor;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.extend.MapKeyHandler;
import io.github.jeddict.jpa.spec.extend.MapKeyType;
import io.github.jeddict.jpa.spec.validator.override.AttributeValidator;

/**
 *
 * @author Gaurav Gupta
 */
public class MapKeyUtil {

    public static void load(MapKeyAccessor accessor, MapKeyHandler mapKeyHandler) {
        MapKeyType mapKeyType = mapKeyHandler.getValidatedMapKeyType();
        if (mapKeyType != null) {
            if (mapKeyType == MapKeyType.NEW) {
                if (StringUtils.isNotBlank(mapKeyHandler.getMapKeyAttributeType())) {
                    AccessorUtil.setEnumerated(accessor, mapKeyHandler.getMapKeyEnumerated());
                    AccessorUtil.setTemporal(accessor, mapKeyHandler.getMapKeyTemporal());
                    accessor.setMapKeyClassName(mapKeyHandler.getMapKeyAttributeType());
                    if (mapKeyHandler.getMapKeyColumn() != null) {
                        accessor.setMapKeyColumn(mapKeyHandler.getMapKeyColumn().getAccessor());
                    }
                } else if (mapKeyHandler.getMapKeyEntity() != null) {
                    accessor.setMapKeyClassName(mapKeyHandler.getMapKeyEntity().getClazz());
                    accessor.setMapKeyJoinColumns(mapKeyHandler.getMapKeyJoinColumn().stream().map(JoinColumn::getAccessor).collect(toList()));
                } else if (mapKeyHandler.getMapKeyEmbeddable() != null) {
                    accessor.setMapKeyClassName(mapKeyHandler.getMapKeyEmbeddable().getClazz());
                    AttributeValidator.filterMapKey(mapKeyHandler);
                    accessor.setMapKeyAttributeOverrides(mapKeyHandler.getMapKeyAttributeOverride().stream().map(AttributeOverrideSpecMetadata::getInstance).collect(toList()));
                }
            } else {
//                MapKeyMetadata mapKeyMetadata = new MapKeyMetadata();
//                mapKeyMetadata.setName(mapKeyHandler.getMapKeyAttribute().getName());
//                accessor.setMapKey(mapKeyMetadata);
                accessor.setAttributeType(LIST_TYPE);//ignore @MapKey(<Key>) and set List type
            }
        }
    }

}
