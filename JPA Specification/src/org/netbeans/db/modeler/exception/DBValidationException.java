/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.db.modeler.exception;

import org.eclipse.persistence.exceptions.ValidationException;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;

public class DBValidationException extends ValidationException {

    private ValidationException validationException;
    private Attribute attribute;
    private JavaClass javaClass;
    /**
     * Creates a new instance of <code>DBConnectionNotFound</code> without
     * detail message.
     */
    public DBValidationException(ValidationException exception) {
        this.validationException=exception;
    }

    /**
     * Constructs an instance of <code>DBConnectionNotFound</code> with the
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
