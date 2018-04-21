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
package io.github.jeddict.jcode;

import io.github.jeddict.jcode.annotation.ConfigData;
import io.github.jeddict.jcode.annotation.Technology;
import io.github.jeddict.jcode.impl.DefaultConfigPanel;
import io.github.jeddict.jcode.util.PreferenceUtils;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.openide.util.Exceptions;

/**
 *
 * @author jGauravGupta <gaurav.gupta.jc@gmail.com>
 */
public class TechContext {

    private Generator generator;
    private Technology technology;
    private LayerConfigPanel panel;
    private List<TechContext> siblingTechContext;
    private Class<? extends Generator> generatorClass;
    private TechContext parentTechContext;

    public TechContext(Class<? extends Generator> generatorClass) {
        this(null, generatorClass);
    }

    public TechContext(TechContext parentTechContext, Class<? extends Generator> generatorClass) {
        this.parentTechContext = parentTechContext;
        this.generatorClass = generatorClass;
        this.technology = generatorClass.getAnnotation(Technology.class);
        this.siblingTechContext = Generator.getSiblingTechContexts(this);
    }

    public LayerConfigPanel createPanel(Project project, SourceGroup sourceGroup, String _package) {
        if (panel == null) {
            if (isValid()) {
                try {
                    panel = getTechnology().panel().newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                panel = new DefaultConfigPanel();
            }
            
            Class<LayerConfigData> configDataClass = panel.getConfigDataClass();
            panel.setConfigData(PreferenceUtils.get(project, configDataClass));
            
            Map<Class<? extends LayerConfigPanel>, LayerConfigPanel> siblingData = new HashMap<>();
            Map<Class<? extends LayerConfigPanel>, LayerConfigPanel> cacheSiblingData = new HashMap<>();
            for(TechContext siblingContext : getSiblingTechContext()){
               LayerConfigPanel siblingPanel = siblingContext.createPanel(project, sourceGroup, _package);
               siblingData.put(siblingPanel.getClass(), siblingPanel);
            }
            
            cacheSiblingData.putAll(siblingData);
            cacheSiblingData.put(panel.getClass(), panel);
            Iterator<Entry<Class<? extends LayerConfigPanel>, LayerConfigPanel>> iterator = siblingData.entrySet().iterator();
            while(iterator.hasNext()){
                Entry<Class<? extends LayerConfigPanel>, LayerConfigPanel> entry = iterator.next();
                LayerConfigPanel instance = entry.getValue();
                List<Field> fields = Arrays.asList(instance.getClass().getDeclaredFields());
                for (Field field : fields) {
                    if (field.isAnnotationPresent(ConfigData.class)) {
                        field.setAccessible(true);
                        try {
                            if (LayerConfigPanel.class.isAssignableFrom(field.getType())) {
                                field.set(instance, cacheSiblingData.get(field.getType()));
                            }
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                instance.init(_package, project, sourceGroup);
                instance.read();
            }
               
            if (parentTechContext == null) { //if sibling context then first inject field and then init
                panel.init(_package, project, sourceGroup);
                panel.read();
            }
        }
        return panel;
    }

    public LayerConfigPanel getPanel() {
        return panel;
    }
    
    public void resetPanel() {
        this.panel = null;
        for (TechContext siblingContext : getSiblingTechContext()) {
            siblingContext.resetPanel();
        }
    }

    /**
     * @return the generator
     */
    public Generator getGenerator() {
        if(generator == null){
            try {
                generator = getGeneratorClass().newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return generator;
    }

    /**
     * @return the technology
     */
    public Technology getTechnology() {
        return technology;
    }

    /**
     * @param technology the technology to set
     */
    public void setTechnology(Technology technology) {
        this.technology = technology;
    }

    @Override
    public String toString() {
        return technology.label();
    }

    public boolean isValid() {
        return getTechnology().panel() != null && getTechnology().panel() != LayerConfigPanel.class;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.technology.label());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TechContext other = (TechContext) obj;
        if (this.getGeneratorClass() != other.getGeneratorClass()) {
            return false;
        }
        return true;
    }

    public List<TechContext> getSiblingTechContext() {
        if (siblingTechContext == null) {
            siblingTechContext = new ArrayList<>();
        }
        return siblingTechContext;
    }

    public void setSiblingTechContext(List<TechContext> siblingTechContext) {
        this.siblingTechContext = siblingTechContext;
    }

    public boolean addSiblingTechContext(TechContext e) {
        return getSiblingTechContext().add(e);
    }

    public boolean removeSiblingTechContext(TechContext o) {
        return getSiblingTechContext().remove(o);
    }

    /**
     * @return the generatorClass
     */
    public Class<? extends Generator> getGeneratorClass() {
        return generatorClass;
    }

    /**
     * @return the parentTechContext
     */
    public TechContext getParentTechContext() {
        return parentTechContext;
    }
    
    public boolean isRootTechContext(Class<? extends Generator> childGenerator) {
        if(parentTechContext==null){
            return false;
        } else if(parentTechContext.getGeneratorClass() == childGenerator){
            return true;
        } else {
           return parentTechContext.isRootTechContext(childGenerator); 
        }
    }

    /**
     * @param parentTechContext the parentTechContext to set
     */
    public void setParentTechContext(TechContext parentTechContext) {
        this.parentTechContext = parentTechContext;
    }

}
