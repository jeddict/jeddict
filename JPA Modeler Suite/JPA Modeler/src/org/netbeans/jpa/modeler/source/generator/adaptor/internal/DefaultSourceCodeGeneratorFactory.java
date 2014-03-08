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
package org.netbeans.jpa.modeler.source.generator.adaptor.internal;

import org.netbeans.jpa.modeler.source.generator.adaptor.ISourceCodeGenerator;
import org.netbeans.jpa.modeler.source.generator.adaptor.ISourceCodeGeneratorFactory;
import org.netbeans.jpa.modeler.source.generator.adaptor.SourceCodeGeneratorType;

/**
 *
 * @author Gaurav Gupta
 */
@org.openide.util.lookup.ServiceProvider(service = org.netbeans.jpa.modeler.source.generator.adaptor.ISourceCodeGeneratorFactory.class)
public class DefaultSourceCodeGeneratorFactory implements ISourceCodeGeneratorFactory {

    @Override
    public ISourceCodeGenerator getSourceGenerator(SourceCodeGeneratorType sourceGeneratorFactoryType) {
        if (sourceGeneratorFactoryType == SourceCodeGeneratorType.JPA) {
            return new JPASourceCodeGenerator();
        } else {
            return null;
        }
    }

}
