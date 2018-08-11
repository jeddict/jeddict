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
package io.github.jeddict.jcode;

import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Gaurav Gupta
 */
public class JAXBConstants {

    public static final String JAXB_PACKAGE = "javax.xml.bind";
    public static final String JAXB_ANNOTATION_PACKAGE = JAXB_PACKAGE + ".annotation";
    public static final String JAXB_ANNOTATION_PACKAGE_PREFIX = JAXB_ANNOTATION_PACKAGE + '.';

    public static final String JAXB_XML_ELEMENT = "XmlElement";
    public static final String JAXB_XML_LIST = "XmlList";
    public static final String JAXB_XML_VALUE = "XmlValue";
    public static final String JAXB_XML_TRANSIENT = "XmlTransient";
    public static final String JAXB_XML_ELEMENT_REF = "XmlElementRef";
    public static final String JAXB_XML_ELEMENT_WRAPPER = "XmlElementWrapper";
    public static final String JAXB_XML_ATTRIBUTE = "XmlAttribute";
    public static final String JAXB_XML_INVERSE_REFERENCE = "XmlInverseReference";
    public static final String JAXB_XML_SCHEMA = "XmlSchema";
    public static final String JAXB_XML_ACCESSOR_TYPE = "XmlAccessorType";
    public static final String JAXB_XML_ROOT_ELEMENT = "XmlRootElement";

    public static final Set<String> JAXB_ANNOTATIONS = new HashSet<>(asList(
            JAXB_XML_ELEMENT,
            JAXB_XML_LIST,
            JAXB_XML_VALUE,
            JAXB_XML_TRANSIENT,
            JAXB_XML_ELEMENT_REF,
            JAXB_XML_ELEMENT_WRAPPER,
            JAXB_XML_ATTRIBUTE,
            JAXB_XML_INVERSE_REFERENCE,
            JAXB_XML_SCHEMA,
            JAXB_XML_ACCESSOR_TYPE,
            JAXB_XML_ROOT_ELEMENT
    ));

}
