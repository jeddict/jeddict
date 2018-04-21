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
package io.github.jeddict.jcode.impl;

import io.github.jeddict.jcode.Generator;
import io.github.jeddict.jcode.annotation.Technology;
import static io.github.jeddict.jcode.annotation.Technology.Type.CONTROLLER;
import java.io.IOException;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author jGauravGupta <gaurav.gupta.jc@gmail.com>
 */
@ServiceProvider(service=Generator.class)
@Technology(type=CONTROLLER, microservice = true, listIndex = 0)
public class DefaultControllerLayer implements Generator {

    @Override
    public void execute() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
