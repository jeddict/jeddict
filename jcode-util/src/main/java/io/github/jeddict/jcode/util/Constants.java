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
package io.github.jeddict.jcode.util;

import javax.lang.model.element.Modifier;

public class Constants {

    public static final String WEB_INF = "WEB-INF";
    public static final String META_INF = "META-INF";
    public static final String LOGGER = "java.util.logging.Logger";
    public static final String POST_CONSTRUCT = "javax.annotation.PostConstruct";

    public static final String NAMED = "javax.inject.Named";
    public static final String RESOURCE_SUFFIX = "Resource";

    public static final String XML_TRANSIENT_ANNOTATION = "XmlTransient";
    public static final String XML_ROOT_ELEMENT_ANNOTATION = "XmlRootElement";
    public static final String XML_ELEMENT_ANNOTATION = "XmlElement";
    public static final String XML_ATTRIBUTE_ANNOTATION = "XmlAttribute";
    public static final String URI_TYPE = "java.net.URI";
    public static final String XML_ANNOTATION_PACKAGE = "javax.xml.bind.annotation.";
    public static final String XML_ROOT_ELEMENT = XML_ANNOTATION_PACKAGE + XML_ROOT_ELEMENT_ANNOTATION;
    public static final String XML_ELEMENT = XML_ANNOTATION_PACKAGE + XML_ELEMENT_ANNOTATION;
    public static final String XML_ATTRIBUTE = XML_ANNOTATION_PACKAGE + XML_ATTRIBUTE_ANNOTATION;
    public static final String XML_TRANSIENT = XML_ANNOTATION_PACKAGE + XML_TRANSIENT_ANNOTATION;

    public static final String VOID = "void";

    public static final String COLLECTION = "Collection";
    public static final String COLLECTION_TYPE = "java.util.Collection";
    public static final String COLLECTIONS_TYPE = "java.util.Collections";
    public static final String LIST_TYPE = "java.util.List";
    public static final String SET_TYPE = "java.util.Set";
    public static final String ARRAY_LIST_TYPE = "java.util.ArrayList";
    public static final String HASH_SET_TYPE = "java.util.HashSet";

    public static final String REQUEST_SCOPE = "javax.enterprise.context.RequestScoped";

    public static final Modifier[] PUBLIC = new Modifier[]{Modifier.PUBLIC};
    public static final Modifier[] PRIVATE = new Modifier[]{Modifier.PRIVATE};
    public static final Modifier[] PUBLIC_STATIC_FINAL = new Modifier[]{Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL};

    
    public static final String LANG_PACKAGE = "java.lang";
    public static final String JAVA_EXT = "java";
    public static final String JAVA_EXT_SUFFIX = ".java";

}
