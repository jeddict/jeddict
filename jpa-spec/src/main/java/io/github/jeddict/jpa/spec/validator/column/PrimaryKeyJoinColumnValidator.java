/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import java.util.List;
import io.github.jeddict.util.StringUtils;
import io.github.jeddict.jpa.spec.PrimaryKeyJoinColumn;
import io.github.jeddict.jpa.spec.validator.MarshalValidator;

public class PrimaryKeyJoinColumnValidator extends MarshalValidator<PrimaryKeyJoinColumn> {

    @Override
    public PrimaryKeyJoinColumn marshal(PrimaryKeyJoinColumn column) throws Exception {
        if (column != null && isEmpty(column)) {
            return null;
        }
        return column;
    }

    public static boolean isEmpty(PrimaryKeyJoinColumn column) {
        boolean empty = false;
        if (StringUtils.isBlank(column.getName()) && StringUtils.isBlank(column.getReferencedColumnName())
                && StringUtils.isBlank(column.getColumnDefinition())
                && ForeignKeyValidator.isEmpty(column.getForeignKey())) {
            empty = true;
        }
        if(!empty && StringUtils.isBlank(column.getName()) &&  StringUtils.isNotBlank(column.getImplicitName())){
            column.setName(column.getImplicitName());
            column.setImplicitName(null);
        }
        if(!empty && StringUtils.isBlank(column.getName())){
            empty = true;
        }
        return empty;
    }

    public static boolean isNotEmpty(PrimaryKeyJoinColumn column) {
        return !isEmpty(column);
    }

    /**
     * Remove empty/invalid PrimaryKeyJoinColumn
     *
     * @param columns
     */
    public static void filter(List<PrimaryKeyJoinColumn> columns) {
        columns.removeIf(PrimaryKeyJoinColumnValidator::isEmpty);
    }

}
