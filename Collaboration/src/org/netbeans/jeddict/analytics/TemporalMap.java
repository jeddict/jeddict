/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jeddict.analytics;

import java.util.concurrent.ConcurrentHashMap;   
import java.util.concurrent.ConcurrentMap;   
import java.util.concurrent.Executors;   
import java.util.concurrent.ScheduledExecutorService;   
import java.util.concurrent.TimeUnit;   
import java.util.function.BiConsumer;
    
public class TemporalMap<K, V> {   
    private final static ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();   
    
    private final ConcurrentMap<K, V> map = new ConcurrentHashMap<>();   
    private final long delay;
    private final TimeUnit unit;
    private final BiConsumer<K,V> consumer;
 
    public TemporalMap(long delay, TimeUnit unit, BiConsumer<K,V> consumer)  {
        this.delay = delay;
        this.unit = unit;
        this.consumer = consumer;
    }
    
    public void save(final K key, V value) { 
        System.out.println("Saving key : " + key + ", value " + value);
        map.put(key, value);   
        EXECUTOR_SERVICE.schedule(() -> consumer.accept(key, map.remove(key)), delay, unit);   
    }
    
    public void update(final K key, V value) {   
        System.out.println("Updating key : " + key + ", value " + value);
        map.put(key, value);   
    }
    
    public V get(K key) {   
        return map.get(key);   
    }
 
}