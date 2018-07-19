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
package io.github.jeddict.jpa.spec.validator;

import org.apache.commons.lang3.StringUtils;
import io.github.jeddict.jpa.spec.Convert;

public class ConvertValidator extends MarshalValidator<Convert> {

    @Override
    public Convert marshal(Convert convert) throws Exception {
        if (convert != null && isEmpty(convert)) {
            return null;
        }
        return convert;
    }

    public static boolean isEmpty(Convert convert) {
        boolean empty = false;
        if (StringUtils.isBlank(convert.getConverter()) && !convert.isDisableConversion()){
            empty = true;
        }
        return empty;
    }

    public static boolean isNotEmpty(Convert convert) {
        return !isEmpty(convert);
    }

}
