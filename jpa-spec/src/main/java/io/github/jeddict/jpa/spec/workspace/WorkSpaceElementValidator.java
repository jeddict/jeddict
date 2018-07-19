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
package io.github.jeddict.jpa.spec.workspace;

import io.github.jeddict.jpa.spec.validator.MarshalValidator;

/**
 *
 * @author jGauravGupta
 */
public class WorkSpaceElementValidator  extends MarshalValidator<WorkSpaceElement> {

    @Override
    public WorkSpaceElement marshal(WorkSpaceElement element) throws Exception {
        if (element != null && isEmpty(element)) {
            return null;
        }
        return element;
    }

    public static boolean isEmpty(WorkSpaceElement element) {
        boolean empty = false;
        if (element.getAttribute()== null || 
                (!element.getTextDesign().isChanged() && !element.getJsonbTextDesign().isChanged())){
            empty = true;
        }
        return empty;
    }

    public static boolean isNotEmpty(WorkSpaceElement item) {
        return !isEmpty(item);
    }

}
