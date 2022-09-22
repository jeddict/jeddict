/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jcode;

import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Gaurav Gupta
 */
public class JSONBConstants {

    public static final String JSONB_PACKAGE = "jakarta.json.bind";
    public static final String JSONB_ANNOTATION_PACKAGE = JSONB_PACKAGE + ".annotation";
    public static final String JSONB_ANNOTATION_PACKAGE_PREFIX = JSONB_ANNOTATION_PACKAGE + '.';

    public static final String JSONB_CONFIG_PACKAGE = JSONB_PACKAGE + ".config";
    public static final String JSONB_CONFIG_PACKAGE_PREFIX = JSONB_CONFIG_PACKAGE + '.';

    public static final String JSONB_PROPERTY_ORDER = "JsonbPropertyOrder";
    public static final String JSONB_PROPERTY_ORDER_FQN = JSONB_ANNOTATION_PACKAGE_PREFIX + JSONB_PROPERTY_ORDER;
    public static final String JSONB_CREATOR = "JsonbCreator";
    public static final String JSONB_CREATOR_FQN = JSONB_ANNOTATION_PACKAGE_PREFIX + JSONB_CREATOR;
    public static final String JSONB_PROPERTY = "JsonbProperty";
    public static final String JSONB_PROPERTY_FQN = JSONB_ANNOTATION_PACKAGE_PREFIX + JSONB_PROPERTY;
    public static final String JSONB_DATE_FORMAT = "JsonbDateFormat";
    public static final String JSONB_DATE_FORMAT_FQN = JSONB_ANNOTATION_PACKAGE_PREFIX + JSONB_DATE_FORMAT;
    public static final String JSONB_NUMBER_FORMAT = "JsonbNumberFormat";
    public static final String JSONB_NUMBER_FORMAT_FQN = JSONB_ANNOTATION_PACKAGE_PREFIX + JSONB_NUMBER_FORMAT;
    public static final String JSONB_TRANSIENT = "JsonbTransient";
    public static final String JSONB_TRANSIENT_FQN = JSONB_ANNOTATION_PACKAGE_PREFIX + JSONB_TRANSIENT;
    public static final String JSONB_TYPE_ADAPTER = "JsonbTypeAdapter";
    public static final String JSONB_TYPE_ADAPTER_FQN = JSONB_ANNOTATION_PACKAGE_PREFIX + JSONB_TYPE_ADAPTER;
    public static final String JSONB_TYPE_DESERIALIZER = "JsonbTypeDeserializer";
    public static final String JSONB_TYPE_DESERIALIZER_FQN = JSONB_ANNOTATION_PACKAGE_PREFIX + JSONB_TYPE_DESERIALIZER;
    public static final String JSONB_TYPE_SERIALIZER = "JsonbTypeSerializer";
    public static final String JSONB_TYPE_SERIALIZER_FQN = JSONB_ANNOTATION_PACKAGE_PREFIX + JSONB_TYPE_SERIALIZER;
    public static final String JSONB_VISIBILITY = "JsonbVisibility";
    public static final String JSONB_VISIBILITY_FQN = JSONB_ANNOTATION_PACKAGE_PREFIX + JSONB_VISIBILITY;
    public static final String JSONB_NILLABLE = "JsonbNillable";
    public static final String JSONB_NILLABLE_FQN = JSONB_ANNOTATION_PACKAGE_PREFIX + JSONB_NILLABLE;

    public static final Set<String> JSONB_ANNOTATIONS = new HashSet<>(asList(
            JSONB_PROPERTY_ORDER,
            JSONB_CREATOR,
            JSONB_PROPERTY,
            JSONB_DATE_FORMAT,
            JSONB_NUMBER_FORMAT,
            JSONB_TRANSIENT,
            JSONB_TYPE_ADAPTER,
            JSONB_TYPE_DESERIALIZER,
            JSONB_TYPE_SERIALIZER,
            JSONB_VISIBILITY,
            JSONB_NILLABLE
    ));

}
