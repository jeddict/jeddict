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
package org.netbeans.jpa.modeler.rules.entity;

public class EntityValidator {

    public final static String NO_PRIMARYKEY_EXIST = "MSG_NoIdDefinedInHierarchy";
    public final static String EMPTY_CLASS_NAME = "MSG_EmptyClassName";
    public final static String CLASS_TABLE_NAME_WITH_RESERVED_SQL_KEYWORD = "MSG_ClassNamedWithReservedSQLKeyword";
    public final static String CLASS_NAME_WITH_JPQL_KEYWORD = "MSG_ClassNamedWithJavaPersistenceQLKeyword";
    public final static String NON_UNIQUE_ENTITY_NAME = "MSG_NonUniqueEntityName";

}
