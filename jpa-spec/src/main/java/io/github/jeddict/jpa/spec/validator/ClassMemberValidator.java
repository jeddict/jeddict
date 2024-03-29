/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import io.github.jeddict.util.StringUtils;
import io.github.jeddict.jpa.spec.extend.ClassMembers;

public class ClassMemberValidator extends MarshalValidator<ClassMembers> {

    @Override
    public ClassMembers marshal(ClassMembers classMembers) throws Exception {
        if (classMembers != null && isEmpty(classMembers)) {
            return null;
        }
        return classMembers;
    }

    public static boolean isEmpty(ClassMembers classMembers) {
        boolean empty = false;
        if (classMembers.getAttributes().isEmpty()
                && StringUtils.isBlank(classMembers.getPreCode()) 
                && StringUtils.isBlank(classMembers.getPostCode())) {
            empty = true;
        }
        return empty;
    }

    public static boolean isNotEmpty(ClassMembers classMembers) {
        return !isEmpty(classMembers);
    }

}
