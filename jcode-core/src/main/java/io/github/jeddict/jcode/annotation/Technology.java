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
package io.github.jeddict.jcode.annotation;

import io.github.jeddict.jcode.Generator;
import io.github.jeddict.jcode.LayerConfigPanel;
import static io.github.jeddict.jcode.annotation.Technology.Type.NONE;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author jGauravGupta <gaurav.gupta.jc@gmail.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Technology {
    
    String NONE_LABEL = "< none >";
    
    Type type() default NONE;
    
    Class<? extends LayerConfigPanel> panel() default LayerConfigPanel.class;
    
    Class<? extends Generator>[] parents() default {};
    
    Class<? extends Generator>[] children() default {};
    
    Class<? extends Generator>[] sibling() default {};
    
    int tabIndex() default -1;
    
    int listIndex() default 100;

    String label() default NONE_LABEL;
    
    String description() default "";
    
    boolean entityGenerator() default true;
    
    boolean microservice() default false;
    
    enum Type {
        BUSINESS("Repository"), CONTROLLER("Controller"), VIEWER("Viewer"), NONE("Other");

        private final String displayLabel;

        Type(String displayLabel) {
            this.displayLabel = displayLabel;
        }

        public String getDisplayLabel() {
            return displayLabel;
        }

    }

}
