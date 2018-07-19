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
package io.github.jeddict.jsonb.spec;

import javax.xml.bind.annotation.XmlEnum;
import org.netbeans.modeler.properties.type.Enumy;

/**
 *
 * @author jGauravGupta
 */
@XmlEnum
public enum PropertyOrderStrategy implements Enumy {

    LEXICOGRAPHICAL,
    ANY,
    REVERSE;

    @Override
    public String getDisplay() {
        return name();
    }

    @Override
    public Enumy getDefault() {
        return null;
    }
}
