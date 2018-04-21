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
package io.github.jeddict.jcode.jpa;

/**
 *
 * @author Gaurav Gupta
 */
public class JPAConstants {

//Misc
    public static final String PERSISTENCE_PACKAGE = "javax.persistence";
    public static final String PERSISTENCE_PACKAGE_PREFIX = PERSISTENCE_PACKAGE + '.';
    public static final String PERSISTENCE_METAMODEL_PACKAGE = PERSISTENCE_PACKAGE_PREFIX + "metamodel.";

    public static final String QUERY_TYPE = "Query";
    public static final String ENTITY_MANAGER_TYPE = "EntityManager";
    public static final String ENTITY_MANAGER_FACTORY = "EntityManagerFactory";
    public static final String ENTITY_TRANSACTION = "EntityTransaction";
    public static final String PERSISTENCE = "Persistence";
    public static final String PERSISTENCE_CONTEXT_ANNOTATION = "PersistenceContext";
    public static final String PERSISTENCE_CONTEXT = PERSISTENCE_PACKAGE_PREFIX + PERSISTENCE_CONTEXT_ANNOTATION;

    public static final String EXCLUDE_DEFAULT_LISTENERS = "ExcludeDefaultListeners";
    public static final String EXCLUDE_DEFAULT_LISTENERS_FQN = PERSISTENCE_PACKAGE_PREFIX + EXCLUDE_DEFAULT_LISTENERS;
    public static final String EXCLUDE_SUPERCLASS_LISTENERS = "ExcludeSuperclassListeners";
    public static final String EXCLUDE_SUPERCLASS_LISTENERS_FQN = PERSISTENCE_PACKAGE_PREFIX + EXCLUDE_SUPERCLASS_LISTENERS;
    public static final String ENTITY_LISTENERS = "EntityListeners";
    public static final String ENTITY_LISTENERS_FQN = PERSISTENCE_PACKAGE_PREFIX + ENTITY_LISTENERS;
    public static final String CONVERTS = "Converts";
    public static final String CONVERTS_FQN = PERSISTENCE_PACKAGE_PREFIX + CONVERTS;
    public static final String CONVERT = "Convert";
    public static final String CONVERT_FQN = PERSISTENCE_PACKAGE_PREFIX + CONVERT;

    public static final String NO_RESULT_EXCEPTION = "NoResultException";

//Class 
    public static final String ENTITY = "Entity";
    public static final String ENTITY_FQN = PERSISTENCE_PACKAGE_PREFIX + ENTITY;

    public static final String EMBEDDABLE = "Embeddable";
    public static final String EMBEDDABLE_FQN = PERSISTENCE_PACKAGE_PREFIX + EMBEDDABLE;

    public static final String MAPPED_SUPERCLASS = "MappedSuperclass";
    public static final String MAPPED_SUPERCLASS_FQN = PERSISTENCE_PACKAGE_PREFIX + MAPPED_SUPERCLASS;

    public static final String TABLE = "Table";
    public static final String TABLE_FQN = PERSISTENCE_PACKAGE_PREFIX + TABLE;

    public static final String ID_CLASS = "IdClass";
    public static final String ID_CLASS_FQN = PERSISTENCE_PACKAGE_PREFIX + ID_CLASS;

    public static final String CACHEABLE = "Cacheable";
    public static final String CACHEABLE_FQN = PERSISTENCE_PACKAGE_PREFIX + CACHEABLE;

    public static final String ACCESS = "Access";
    public static final String ACCESS_FQN = PERSISTENCE_PACKAGE_PREFIX + ACCESS;
    
    public static final String UNIQUE_CONSTRAINT = "UniqueConstraint";
    public static final String UNIQUE_CONSTRAINT_FQN = PERSISTENCE_PACKAGE_PREFIX + UNIQUE_CONSTRAINT;

    public static final String SECONDARY_TABLE = "SecondaryTable";
    public static final String SECONDARY_TABLE_FQN = PERSISTENCE_PACKAGE_PREFIX + SECONDARY_TABLE;
    public static final String SECONDARY_TABLES = "SecondaryTables";
    public static final String SECONDARY_TABLES_FQN = PERSISTENCE_PACKAGE_PREFIX + SECONDARY_TABLES;

    public static final String DISCRIMINATOR_COLUMN = "DiscriminatorColumn";
    public static final String DISCRIMINATOR_COLUMN_FQN = PERSISTENCE_PACKAGE_PREFIX + DISCRIMINATOR_COLUMN;
    public static final String DISCRIMINATOR_TYPE = "DiscriminatorType";
    public static final String DISCRIMINATOR_TYPE_FQN = PERSISTENCE_PACKAGE_PREFIX + DISCRIMINATOR_TYPE;
    public static final String DISCRIMINATOR_VALUE = "DiscriminatorValue";
    public static final String DISCRIMINATOR_VALUE_FQN = PERSISTENCE_PACKAGE_PREFIX + DISCRIMINATOR_VALUE;

    public static final String INHERITANCE = "Inheritance";
    public static final String INHERITANCE_FQN = PERSISTENCE_PACKAGE_PREFIX + INHERITANCE;
    public static final String INHERITANCE_TYPE = "InheritanceType";
    public static final String INHERITANCE_TYPE_FQN = PERSISTENCE_PACKAGE_PREFIX + INHERITANCE_TYPE;

//Primary Key
    public static final String ID = "Id";
    public static final String ID_FQN = PERSISTENCE_PACKAGE_PREFIX + ID;

    public static final String EMBEDDED_ID = "EmbeddedId";
    public static final String EMBEDDED_ID_FQN = PERSISTENCE_PACKAGE_PREFIX + EMBEDDED_ID;

    public static final String GENERATED_VALUE = "GeneratedValue";
    public static final String GENERATED_VALUE_FQN = PERSISTENCE_PACKAGE_PREFIX + GENERATED_VALUE;
    public static final String GENERATION_TYPE = "GenerationType";
    public static final String GENERATION_TYPE_FQN = PERSISTENCE_PACKAGE_PREFIX + GENERATION_TYPE;

    public static final String TABLE_GENERATOR = "TableGenerator";
    public static final String TABLE_GENERATOR_FQN = PERSISTENCE_PACKAGE_PREFIX + TABLE_GENERATOR;
    public static final String SEQUENCE_GENERATOR = "SequenceGenerator";
    public static final String SEQUENCE_GENERATOR_FQN = PERSISTENCE_PACKAGE_PREFIX + SEQUENCE_GENERATOR;

    public static final String PRIMARY_KEY_JOIN_COLUMNS = "PrimaryKeyJoinColumns";
    public static final String PRIMARY_KEY_JOIN_COLUMNS_FQN = PERSISTENCE_PACKAGE_PREFIX + PRIMARY_KEY_JOIN_COLUMNS;
    public static final String PRIMARY_KEY_JOIN_COLUMN = "PrimaryKeyJoinColumn";
    public static final String PRIMARY_KEY_JOIN_COLUMN_FQN = PERSISTENCE_PACKAGE_PREFIX + PRIMARY_KEY_JOIN_COLUMN;

//Map
    public static final String MAPS_ID = "MapsId";
    public static final String MAPS_ID_FQN = PERSISTENCE_PACKAGE_PREFIX + MAPS_ID;

    public static final String MAP_KEY = "MapKey";
    public static final String MAP_KEY_FQN = PERSISTENCE_PACKAGE_PREFIX + MAP_KEY;
    public static final String MAP_KEY_CLASS = "MapKeyClass";
    public static final String MAP_KEY_CLASS_FQN = PERSISTENCE_PACKAGE_PREFIX + MAP_KEY_CLASS;

    public static final String MAP_KEY_TEMPORAL = "MapKeyTemporal";
    public static final String MAP_KEY_TEMPORAL_FQN = PERSISTENCE_PACKAGE_PREFIX + MAP_KEY_TEMPORAL;
    public static final String MAP_KEY_ENUMERATED = "MapKeyEnumerated";
    public static final String MAP_KEY_ENUMERATED_FQN = PERSISTENCE_PACKAGE_PREFIX + MAP_KEY_ENUMERATED;

    public static final String MAP_KEY_ATTRIBUTE_OVERRIDE = "MapKeyAttributeOverride";
    public static final String MAP_KEY_COLUMN = "MapKeyColumn";
    public static final String MAP_KEY_COLUMN_FQN = PERSISTENCE_PACKAGE_PREFIX + MAP_KEY_COLUMN;
    public static final String MAP_KEY_JOIN_COLUMNS = "MapKeyJoinColumns";
    public static final String MAP_KEY_JOIN_COLUMNS_FQN = PERSISTENCE_PACKAGE_PREFIX + MAP_KEY_JOIN_COLUMNS;
    public static final String MAP_KEY_JOIN_COLUMN = "MapKeyJoinColumn";
    public static final String MAP_KEY_JOIN_COLUMN_FQN = PERSISTENCE_PACKAGE_PREFIX + MAP_KEY_JOIN_COLUMN;

//Attribute
    public static final String BASIC = "Basic";
    public static final String BASIC_FQN = PERSISTENCE_PACKAGE_PREFIX + BASIC;

    public static final String COLLECTION_TABLE = "CollectionTable";
    public static final String COLLECTION_TABLE_FQN = PERSISTENCE_PACKAGE_PREFIX + COLLECTION_TABLE;

    public static final String ORDER_BY = "OrderBy";
    public static final String ORDER_BY_FQN = PERSISTENCE_PACKAGE_PREFIX + ORDER_BY;
    public static final String ORDER_COLUMN = "OrderColumn";
    public static final String ORDER_COLUMN_FQN = PERSISTENCE_PACKAGE_PREFIX + ORDER_COLUMN;

    public static final String ELEMENT_COLLECTION = "ElementCollection";
    public static final String ELEMENT_COLLECTION_FQN = PERSISTENCE_PACKAGE_PREFIX + ELEMENT_COLLECTION;

    public static final String COLUMN = "Column";
    public static final String COLUMN_FQN = PERSISTENCE_PACKAGE_PREFIX + COLUMN;
    public static final String ENUMERATED = "Enumerated";
    public static final String ENUMERATED_FQN = PERSISTENCE_PACKAGE_PREFIX + ENUMERATED;
    public static final String ENUM_TYPE = "EnumType";
    public static final String ENUM_TYPE_FQN = PERSISTENCE_PACKAGE_PREFIX + ENUM_TYPE;
    public static final String ENUM_TYPE_ORDINAL = "EnumType.ORDINAL";
    public static final String ENUM_TYPE_STRING = "EnumType.STRING";

    public static final String FETCH_TYPE = "FetchType";
    public static final String FETCH_TYPE_FQN = PERSISTENCE_PACKAGE_PREFIX + FETCH_TYPE;
    public static final String CASCADE_TYPE = "CascadeType";
    public static final String CASCADE_TYPE_FQN = PERSISTENCE_PACKAGE_PREFIX + CASCADE_TYPE;

    public static final String TEMPORAL = "Temporal";
    public static final String TEMPORAL_FQN = PERSISTENCE_PACKAGE_PREFIX + TEMPORAL;
    public static final String TEMPORAL_TYPE = "TemporalType";
    public static final String TEMPORAL_TYPE_FQN = PERSISTENCE_PACKAGE_PREFIX + TEMPORAL_TYPE;
    public static final String TEMPORAL_DATE = "TemporalType.DATE";
    public static final String TEMPORAL_TIME = "TemporalType.TIME";
    public static final String TEMPORAL_TIMESTAMP = "TemporalType.TIMESTAMP";

    public static final String LOB = "Lob";
    public static final String LOB_FQN = PERSISTENCE_PACKAGE_PREFIX + LOB;
    public static final String TRANSIENT = "Transient";
    public static final String TRANSIENT_FQN = PERSISTENCE_PACKAGE_PREFIX + TRANSIENT;
    public static final String VERSION = "Version";
    public static final String VERSION_FQN = PERSISTENCE_PACKAGE_PREFIX + VERSION;

//Ref Attribute
    public static final String JOIN_TABLE = "JoinTable";
    public static final String JOIN_TABLE_FQN = PERSISTENCE_PACKAGE_PREFIX + JOIN_TABLE;

    public static final String MANY_TO_ONE = "ManyToOne";
    public static final String MANY_TO_ONE_FQN = PERSISTENCE_PACKAGE_PREFIX + MANY_TO_ONE;
    public static final String ONE_TO_ONE = "OneToOne";
    public static final String ONE_TO_ONE_FQN = PERSISTENCE_PACKAGE_PREFIX + ONE_TO_ONE;
    public static final String MANY_TO_MANY = "ManyToMany";
    public static final String MANY_TO_MANY_FQN = PERSISTENCE_PACKAGE_PREFIX + MANY_TO_MANY;
    public static final String ONE_TO_MANY = "OneToMany";
    public static final String ONE_TO_MANY_FQN = PERSISTENCE_PACKAGE_PREFIX + ONE_TO_MANY;

    public static final String JOIN_COLUMNS = "JoinColumns";
    public static final String JOIN_COLUMNS_FQN = PERSISTENCE_PACKAGE_PREFIX + JOIN_COLUMNS;
    public static final String JOIN_COLUMN = "JoinColumn";
    public static final String JOIN_COLUMN_FQN = PERSISTENCE_PACKAGE_PREFIX + JOIN_COLUMN;

    public static final String INDEX = "Index";
    public static final String INDEX_FQN = PERSISTENCE_PACKAGE_PREFIX + INDEX;
    public static final String FOREIGN_KEY = "ForeignKey";
    public static final String FOREIGN_KEY_FQN = PERSISTENCE_PACKAGE_PREFIX + FOREIGN_KEY;
    public static final String CONSTRAINT_MODE = "ConstraintMode";
    public static final String CONSTRAINT_MODE_FQN = PERSISTENCE_PACKAGE_PREFIX + CONSTRAINT_MODE;

    public static final String EMBEDDED = "Embedded";
    public static final String EMBEDDED_FQN = PERSISTENCE_PACKAGE_PREFIX + EMBEDDED;

    public static final String ATTRIBUTE_OVERRIDE = "AttributeOverride";
    public static final String ATTRIBUTE_OVERRIDE_FQN = PERSISTENCE_PACKAGE_PREFIX + ATTRIBUTE_OVERRIDE;
    public static final String ATTRIBUTE_OVERRIDES = "AttributeOverrides";
    public static final String ATTRIBUTE_OVERRIDES_FQN = PERSISTENCE_PACKAGE_PREFIX + ATTRIBUTE_OVERRIDES;

    public static final String ASSOCIATION_OVERRIDE = "AssociationOverride";
    public static final String ASSOCIATION_OVERRIDE_FQN = PERSISTENCE_PACKAGE_PREFIX + ASSOCIATION_OVERRIDE;
    public static final String ASSOCIATION_OVERRIDES = "AssociationOverrides";
    public static final String ASSOCIATION_OVERRIDES_FQN = PERSISTENCE_PACKAGE_PREFIX + ASSOCIATION_OVERRIDES;

//Query
    public static final String NAMED_ENTITY_GRAPH = "NamedEntityGraph";
    public static final String NAMED_ENTITY_GRAPH_FQN = PERSISTENCE_PACKAGE_PREFIX + NAMED_ENTITY_GRAPH;
    public static final String NAMED_ENTITY_GRAPHS = "NamedEntityGraphs";
    public static final String NAMED_ENTITY_GRAPHS_FQN = PERSISTENCE_PACKAGE_PREFIX + NAMED_ENTITY_GRAPHS;
    public static final String NAMED_SUBGRAPH = "NamedSubgraph";
    public static final String NAMED_SUBGRAPH_FQN = PERSISTENCE_PACKAGE_PREFIX + NAMED_SUBGRAPH;
    public static final String NAMED_ATTRIBUTE_NODE = "NamedAttributeNode";
    public static final String NAMED_ATTRIBUTE_NODE_FQN = PERSISTENCE_PACKAGE_PREFIX + NAMED_ATTRIBUTE_NODE;

    public static final String NAMED_NATIVE_QUERY = "NamedNativeQuery";
    public static final String NAMED_NATIVE_QUERY_FQN = PERSISTENCE_PACKAGE_PREFIX + NAMED_NATIVE_QUERY;
    public static final String NAMED_NATIVE_QUERIES = "NamedNativeQueries";
    public static final String NAMED_NATIVE_QUERIES_FQN = PERSISTENCE_PACKAGE_PREFIX + NAMED_NATIVE_QUERIES;

    public static final String NAMED_QUERY = "NamedQuery";
    public static final String NAMED_QUERY_FQN = PERSISTENCE_PACKAGE_PREFIX + NAMED_QUERY;
    public static final String NAMED_QUERIES = "NamedQueries";
    public static final String NAMED_QUERIES_FQN = PERSISTENCE_PACKAGE_PREFIX + NAMED_QUERIES;

    public static final String QUERY_HINT = "QueryHint";
    public static final String QUERY_HINT_FQN = PERSISTENCE_PACKAGE_PREFIX + QUERY_HINT;

    public static final String SQL_RESULTSET_MAPPING = "SqlResultSetMapping";
    public static final String SQL_RESULTSET_MAPPING_FQN = PERSISTENCE_PACKAGE_PREFIX + SQL_RESULTSET_MAPPING;
    public static final String SQL_RESULTSET_MAPPINGS = "SqlResultSetMappings";
    public static final String SQL_RESULTSET_MAPPINGS_FQN = PERSISTENCE_PACKAGE_PREFIX + SQL_RESULTSET_MAPPINGS;

    public static final String NAMED_STORED_PROCEDURE_QUERY = "NamedStoredProcedureQuery";
    public static final String NAMED_STORED_PROCEDURE_QUERY_FQN = PERSISTENCE_PACKAGE_PREFIX + NAMED_STORED_PROCEDURE_QUERY;
    public static final String NAMED_STORED_PROCEDURE_QUERIES = "NamedStoredProcedureQueries";
    public static final String NAMED_STORED_PROCEDURE_QUERIES_FQN = PERSISTENCE_PACKAGE_PREFIX + NAMED_STORED_PROCEDURE_QUERIES;
    public static final String STORED_PROCEDURE_PARAMETER = "StoredProcedureParameter";
    public static final String STORED_PROCEDURE_PARAMETER_FQN = PERSISTENCE_PACKAGE_PREFIX + STORED_PROCEDURE_PARAMETER;
    public static final String PARAMETER_MODE = "ParameterMode";
    public static final String PARAMETER_MODE_FQN = PERSISTENCE_PACKAGE_PREFIX + PARAMETER_MODE;

    public static final String FIELD_RESULT = "FieldResult";
    public static final String FIELD_RESULT_FQN = PERSISTENCE_PACKAGE_PREFIX + FIELD_RESULT;
    public static final String ENTITY_RESULT = "EntityResult";
    public static final String ENTITY_RESULT_FQN = PERSISTENCE_PACKAGE_PREFIX + ENTITY_RESULT;
    public static final String COLUMN_RESULT = "ColumnResult";
    public static final String COLUMN_RESULT_FQN = PERSISTENCE_PACKAGE_PREFIX + COLUMN_RESULT;
    public static final String CONSTRUCTOR_RESULT = "ConstructorResult";
    public static final String CONSTRUCTOR_RESULT_FQN = PERSISTENCE_PACKAGE_PREFIX + CONSTRUCTOR_RESULT;

    public static final String LOCK_MODE_TYPE = "LockModeType";
    public static final String LOCK_MODE_TYPE_FQN = PERSISTENCE_PACKAGE_PREFIX + LOCK_MODE_TYPE;

    //Persistence.xml
    public static final String JDBC_URL = "javax.persistence.jdbc.url";
    public static final String JDBC_PASSWORD = "javax.persistence.jdbc.password";
    public static final String JDBC_DRIVER = "javax.persistence.jdbc.driver";
    public static final String JDBC_USER = "javax.persistence.jdbc.user";
    public static final String JAVA_DATASOURCE_PREFIX = "java:/";
    public static final String JAVA_GLOBAL_DATASOURCE_PREFIX = "java:global/";

}
