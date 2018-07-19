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
package io.github.jeddict.db.modeler.exception;

import org.eclipse.persistence.exceptions.ValidationException;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.JavaClass;

public class DBValidationException extends ValidationException {

    private ValidationException validationException;
    private Attribute attribute;
    private JavaClass javaClass;
    
    /**
     * Creates a new instance of <code>DBValidationException</code> without
     * detail message.
     * @param exception
     */
    public DBValidationException(ValidationException exception) {
        this.validationException=exception;
    }

    /**
     * Constructs an instance of <code>DBValidationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DBValidationException(String msg) {
        super(msg);
    }

    /**
     * @return the validationException
     */
    public ValidationException getValidationException() {
        return validationException;
    }
    
    @Override
    public int getErrorCode(){
        return validationException!=null?validationException.getErrorCode():0;
    }

    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public synchronized Throwable getCause() {
        return validationException;
    }

    @Override
    public String getLocalizedMessage() {
        return validationException.getLocalizedMessage();
    }
    
    @Override
    public String getMessage() {
        return validationException.getMessage();
    }
    
    
    
    /**
     * @return the javaClass
     */
    public JavaClass getJavaClass() {
        return javaClass;
    }

    /**
     * @param javaClass the javaClass to set
     */
    public void setJavaClass(JavaClass javaClass) {
        this.javaClass = javaClass;
    }
}
