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
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.workspace.WorkSpace;

/**
 *
 * @author Gaurav Gupta
 */
public class EmbeddableSpecAccessor extends EmbeddableAccessor {

    private final Embeddable embeddable;

    private EmbeddableSpecAccessor(Embeddable embeddable) {
        this.embeddable = embeddable;
    }

    public static EmbeddableSpecAccessor getInstance(WorkSpace workSpace, Embeddable embeddable) {
        EmbeddableSpecAccessor accessor = new EmbeddableSpecAccessor(embeddable);
        accessor.setClassName(embeddable.getClazz());
        accessor.setAccess("VIRTUAL");
        accessor.setAttributes(embeddable.getAttributes().getAccessor(workSpace));
        if (embeddable.getSuperclass() != null) {
            accessor.setParentClassName(embeddable.getSuperclass().getClazz());
        }
        return accessor;

    }

    /**
     * @return the embeddable
     */
    public Embeddable getEmbeddable() {
        return embeddable;
    }

    @Override
    public void process() {
        try {
            super.process();
        } catch (ValidationException ex) {
            DBValidationException exception = new DBValidationException(ex);
            exception.setJavaClass(embeddable);
            throw exception;
        }
    }

    @Override
    protected void processVirtualClass() {
        try {
            super.processVirtualClass();
        } catch (ValidationException ex) {
            DBValidationException exception = new DBValidationException(ex);
            exception.setJavaClass(embeddable);
            throw exception;
        }
    }
}
