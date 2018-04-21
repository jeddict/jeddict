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
package io.github.jeddict.jpa.spec.validator.column;

import org.apache.commons.lang3.StringUtils;
import io.github.jeddict.jpa.spec.Column;
import io.github.jeddict.jpa.spec.validator.MarshalValidator;

public class ColumnValidator<E extends Column> extends MarshalValidator<E> {

    @Override
    public E marshal(E column) throws Exception {
        if (column != null && isEmpty(column)) {
            return null;
        }
        return column;
    }

    public static boolean isEmpty(Column column) {
        return StringUtils.isBlank(column.getName()) 
                && StringUtils.isBlank(column.getColumnDefinition()) 
                && StringUtils.isBlank(column.getTable())
                && column.getNullable() 
                && column.getInsertable() 
                && column.getUpdatable() 
                && !column.getUnique()
                && (column.getLength() == null || column.getLength() == 255) 
                && (column.getScale() == null || column.getScale() == 0 )
                && (column.getPrecision() == null || column.getPrecision() == 0);
    }

}
