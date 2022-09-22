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
package io.github.jeddict.orm.generator.util;

import static io.github.jeddict.jcode.util.Constants.LANG_PACKAGE;
import java.util.Collection;
import java.util.TreeSet;
import static java.util.stream.Collectors.toSet;
import static io.github.jeddict.util.StringUtils.isNotBlank;

public class ImportSet extends TreeSet<String> {
    
    @Override
    public boolean add(String fqn){
        if(valid(fqn)){
            return super.add(fqn);
        }
        return false;
    }
    
    @Override
    public boolean addAll(Collection<? extends String> fqns){
       return super.addAll(fqns.stream().filter(this::valid).collect(toSet()));
    }
    
    private boolean valid(String fqn){
        if (isNotBlank(fqn)) {
            return !fqn.startsWith(LANG_PACKAGE);
        }
        return false;
    }
}
