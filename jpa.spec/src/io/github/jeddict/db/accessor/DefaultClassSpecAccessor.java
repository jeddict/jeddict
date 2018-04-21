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
package io.github.jeddict.db.accessor;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import io.github.jeddict.db.modeler.exception.DBValidationException;
import io.github.jeddict.jpa.spec.DefaultClass;

/**
 *
 * @author Gaurav Gupta
 */
public class DefaultClassSpecAccessor extends EmbeddableAccessor {

    private final DefaultClass defaultClass;

    private DefaultClassSpecAccessor(DefaultClass defaultClass) {
        this.defaultClass = defaultClass;
    }

    public static DefaultClassSpecAccessor getInstance(DefaultClass defaultClass) {
        DefaultClassSpecAccessor accessor = new DefaultClassSpecAccessor(defaultClass);
        accessor.setClassName(defaultClass.getClazz());
        accessor.setAccess("VIRTUAL");
        accessor.setAttributes(defaultClass.getAccessor());
        if (defaultClass.getSuperclass() != null) {
            accessor.setParentClassName(defaultClass.getSuperclass().getClazz());
        }
        return accessor;

    }

    /**
     * @return the defaultClass
     */
    public DefaultClass getDefaultClass() {
        return defaultClass;
    }
    
        @Override
    public void process() {
        try {
            super.process();
        } catch (ValidationException ex) {
            DBValidationException exception = new DBValidationException(ex);
            exception.setJavaClass(defaultClass);
            throw exception;
        }
    }
    
    @Override
    protected void processVirtualClass() {
      try {
            super.processVirtualClass();
        } catch (ValidationException ex) {
            DBValidationException exception = new DBValidationException(ex);
            exception.setJavaClass(defaultClass);
            throw exception;
        }
    }

}
