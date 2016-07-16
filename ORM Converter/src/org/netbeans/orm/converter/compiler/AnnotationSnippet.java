/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.orm.converter.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class AnnotationSnippet implements Snippet {

    private String name;
    private String _import;//TODO multi import with sub element
    

    @Override
    public String getSnippet() throws InvalidDataException {
        
        return name;
    }
    
    private boolean haveElements(String name){
        return name.charAt(name.length()-1) == ')';
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<>();
        if(StringUtils.isNotBlank(_import)){
            importSnippets.add(_import);
        }
        return importSnippets;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        if(haveElements(name)){
            int startChild = name.indexOf('(');
            _import = name.substring(1, startChild);
        } else {
             _import = name.substring(1);
        }
        this.name = '@' + name.substring(_import.lastIndexOf('.')+2);
    }
}
