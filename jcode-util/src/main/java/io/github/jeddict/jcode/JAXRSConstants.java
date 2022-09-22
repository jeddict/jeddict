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

/**
 *
 * @author Gaurav Gupta
 */
public interface JAXRSConstants {

    String REST_API_PACKAGE = "jakarta.ws.rs.";
    String BEAN_PARAM = REST_API_PACKAGE + "BeanParam";
    String RESPONSE = "jakarta.ws.rs.core.Response";
    String RESPONSE_UNQF = "Response";
    String GET_PROPERTIES = "getProperties";
    String PRE_MATCHING = "jakarta.ws.rs.container.PreMatching";
    String CONTAINER_REQUEST = "jakarta.ws.rs.container.ContainerRequestFilter";
    String CONTAINER_RESPONSE = "jakarta.ws.rs.container.ContainerResponseFilter";
    String CLIENT_REQUEST = "jakarta.ws.rs.client.ClientRequestFilter";
    String CLIENT_RESPONSE = "jakarta.ws.rs.client.ClientResponseFilter";
    String FORM_PARAM = "jakarta.ws.rs.FormParam";
    String SINGLETON_METHOD = "getSingletons";
    String PATH_ANNOTATION = "Path";
    String PATH_PARAM_ANNOTATION = "PathParam";
    String QUERY_PARAM_ANNOTATION = "QueryParam";
    String DEFAULT_VALUE_ANNOTATION = "DefaultValue";
    String GET_ANNOTATION = "GET";
    String POST_ANNOTATION = "POST";
    String PUT_ANNOTATION = "PUT";
    String DELETE_ANNOTATION = "DELETE";
    String PRODUCE_MIME_ANNOTATION = "Produces";
    String CONSUME_MIME_ANNOTATION = "Consumes";
    String GET = REST_API_PACKAGE + GET_ANNOTATION;
    String POST = REST_API_PACKAGE + POST_ANNOTATION;
    String PUT = REST_API_PACKAGE + PUT_ANNOTATION;
    String DELETE = REST_API_PACKAGE + DELETE_ANNOTATION;
    String PRODUCE_MIME = REST_API_PACKAGE + PRODUCE_MIME_ANNOTATION;
    String CONSUME_MIME = REST_API_PACKAGE + CONSUME_MIME_ANNOTATION;
    String PATH_PARAM = REST_API_PACKAGE + PATH_PARAM_ANNOTATION;
    String QUERY_PARAM = REST_API_PACKAGE + QUERY_PARAM_ANNOTATION;
    String DEFAULT_VALUE = REST_API_PACKAGE + DEFAULT_VALUE_ANNOTATION;
    String PATH = REST_API_PACKAGE + PATH_ANNOTATION;

    String JAX_RS_APPLICATION_CLASS = "jakarta.ws.rs.core.Application";
    String GET_CLASSES = "getClasses";
    String GET_REST_RESOURCE_CLASSES2 = "addRestResourceClasses";
}
