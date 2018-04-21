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
package io.github.jeddict.jpa.spec.extend;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import io.github.jeddict.jpa.spec.Basic;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.Embedded;
import io.github.jeddict.jpa.spec.EmbeddedId;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.ManyToMany;
import io.github.jeddict.jpa.spec.ManyToOne;
import io.github.jeddict.jpa.spec.OneToMany;
import io.github.jeddict.jpa.spec.OneToOne;
import io.github.jeddict.jpa.spec.Transient;
import io.github.jeddict.jpa.spec.Version;
import io.github.jeddict.jpa.spec.bean.BeanAttribute;
import io.github.jeddict.jpa.spec.bean.BeanCollectionAttribute;
import io.github.jeddict.jpa.spec.bean.ManyToManyAssociation;
import io.github.jeddict.jpa.spec.bean.ManyToOneAssociation;
import io.github.jeddict.jpa.spec.bean.OneToManyAssociation;
import io.github.jeddict.jpa.spec.bean.OneToOneAssociation;

/**
 *
 * @author jGauravGupta
 */
public class AttributeLocationComparator implements Comparator<Attribute>{
 
    private final static Map<Class<? extends Attribute>, Function<Attribute, Integer>> index = new HashMap<>();
    
    static {
        index.put(EmbeddedId.class, attr -> 1);
        index.put(Id.class, attr -> 2);
        index.put(Basic.class, attr -> 3);
        index.put(ElementCollection.class, attr -> ((ElementCollection)attr).getConnectedClass()==null?4:5);
        index.put(Embedded.class, attr -> 6);
        index.put(OneToOne.class, attr -> 7);
        index.put(ManyToOne.class, attr -> 8);
        index.put(OneToMany.class, attr -> 9);
        index.put(ManyToMany.class, attr -> 10);
        
        index.put(BeanAttribute.class, attr -> 11);
        index.put(BeanCollectionAttribute.class, attr -> 12);
        index.put(OneToOneAssociation.class, attr -> 13);
        index.put(ManyToOneAssociation.class, attr -> 14);
        index.put(OneToManyAssociation.class, attr -> 15);
        index.put(ManyToManyAssociation.class, attr -> 16);
        
        index.put(Transient.class, attr -> 17);
        index.put(Version.class, attr -> 18);
    }
    
    @Override
    public int compare(Attribute a1, Attribute a2){
        return index.get(a1.getClass()).apply(a1) - index.get(a2.getClass()).apply(a2);
    }
}
