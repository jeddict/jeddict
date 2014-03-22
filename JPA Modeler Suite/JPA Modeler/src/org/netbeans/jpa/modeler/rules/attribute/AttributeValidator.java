/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.rules.attribute;

public class AttributeValidator {

    public final static String EMPTY_ATTRIBUTE_NAME = "MSG_EmptyAttributeName";
    public final static String NON_UNIQUE_ATTRIBUTE_NAME = "MSG_NonUniqueAttributeName";
    public final static String ATTRIBUTE_NAME_WITH_JPQL_KEYWORD = "MSG_AttrNamedWithJavaPersistenceQLKeyword";
    public final static String ATTRIBUTE_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD = "MSG_AttrTableNamedWithReservedSQLKeyword";
    public final static String ATTRIBUTE_COLUMN_NAME_WITH_RESERVED_SQL_KEYWORD = "MSG_AttrColumnNamedWithReservedSQLKeyword";
    public final static String PRIMARYKEY_INVALID_LOCATION = "MSG_PrimaryKeyInvalidLocation";
}
