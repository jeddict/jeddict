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
package io.github.jeddict.orm.generator.compiler;

import io.github.jeddict.orm.generator.compiler.def.VariableDefSnippet;
import io.github.jeddict.snippet.AttributeSnippet;
import io.github.jeddict.snippet.AttributeSnippetLocationType;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.PRE_SETTER;
import static io.github.jeddict.snippet.AttributeSnippetLocationType.SETTER_THROWS;
import io.github.jeddict.snippet.ClassSnippet;
import static io.github.jeddict.snippet.ClassSnippetLocationType.AFTER_FIELD;
import static io.github.jeddict.snippet.ClassSnippetLocationType.AFTER_METHOD;
import static io.github.jeddict.snippet.ClassSnippetLocationType.IMPORT;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 * @author jGauravGupta
 */
public class VetoPropertyRuntimeSnippet {

    public List<ClassSnippet> getClassSnippet(boolean propertyChangeSupport, boolean vetoableChangeSupport) {
        List<ClassSnippet> classSnippets = new ArrayList<>();

        if (propertyChangeSupport) {
            classSnippets.addAll(new ArrayList<>(asList(
                    new ClassSnippet(
                            "private final transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);",
                            AFTER_FIELD),
                    new ClassSnippet(
                            "   /**\n"
                            + "     * Add PropertyChangeListener.\n"
                            + "     *\n"
                            + "     * @param listener\n"
                            + "     */"
                            + "    public void addPropertyChangeListener(PropertyChangeListener listener) {"
                            + "        propertyChangeSupport.addPropertyChangeListener(listener);"
                            + "    }",
                            AFTER_METHOD),
                    new ClassSnippet(
                            "   /**\n"
                            + "     * Remove PropertyChangeListener.\n"
                            + "     *\n"
                            + "     * @param listener\n"
                            + "     */"
                            + "    public void removePropertyChangeListener(PropertyChangeListener listener) {"
                            + "        propertyChangeSupport.removePropertyChangeListener(listener);"
                            + "    }",
                            AFTER_METHOD),
                    new ClassSnippet("java.beans.PropertyChangeListener", IMPORT),
                    new ClassSnippet("java.beans.PropertyChangeSupport", IMPORT)
            )));
        }
        if (vetoableChangeSupport) {
            classSnippets.addAll(new ArrayList<>(asList(
                    new ClassSnippet(
                            "private final transient VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);",
                            AFTER_FIELD),
                    new ClassSnippet(
                            "   /**\n"
                            + "     * Add VetoableChangeListener.\n"
                            + "     *\n"
                            + "     * @param listener\n"
                            + "     */"
                            + "    public void addVetoableChangeListener(VetoableChangeListener listener) {"
                            + "        vetoableChangeSupport.addVetoableChangeListener(listener);"
                            + "    }",
                            AFTER_METHOD),
                    new ClassSnippet(
                            "   /**\n"
                            + "     * Remove VetoableChangeListener.\n"
                            + "     *\n"
                            + "     * @param listener\n"
                            + "     */"
                            + "    public void removeVetoableChangeListener(VetoableChangeListener listener) {"
                            + "        vetoableChangeSupport.removeVetoableChangeListener(listener);"
                            + "    }",
                            AFTER_METHOD),
                    new ClassSnippet("java.beans.VetoableChangeListener", IMPORT),
                    new ClassSnippet("java.beans.VetoableChangeSupport", IMPORT)
            )));
        }
        return classSnippets;
    }

    public List<ClassSnippet> getClassSnippet(VariableDefSnippet variableDef) {
        List<ClassSnippet> classSnippets = new ArrayList<>();
        if (variableDef.isPropertyChangeSupport() || variableDef.isVetoableChangeSupport()) {
            String prop = "PROP_" + variableDef.getName().toUpperCase();
            classSnippets.add(new ClassSnippet(
                    String.format("public static final String %s = \"%s\";", prop, variableDef.getName()),
                    AFTER_FIELD
            ));
        }
        return classSnippets;
    }

    public List<AttributeSnippet> getAttributeSnippet(VariableDefSnippet variableDef) {
        List<AttributeSnippet> attributeSnippets = new ArrayList<>();
        if (variableDef.isPropertyChangeSupport() || variableDef.isVetoableChangeSupport()) {

            String name = variableDef.getName();
            String prop = "PROP_" + name.toUpperCase();
            String oldProp = "old" + variableDef.getMethodName();
            if (variableDef.isPropertyChangeSupport() || variableDef.isVetoableChangeSupport()) {
                //dynamic AttributeSnippet as type is not evaluated during registration
                attributeSnippets.add(new AttributeSnippet() {
                    Supplier<String> template = () -> String.format("%s %s = %s;", variableDef.getType(), oldProp, name);

                    @Override
                    public String getValue() {
                        return template.get();
                    }

                    @Override
                    public AttributeSnippetLocationType getLocationType() {
                        return PRE_SETTER;
                    }
                });
            }
            if (variableDef.isVetoableChangeSupport()) {
                attributeSnippets.add(new AttributeSnippet(
                        "java.beans.PropertyVetoException",
                        AttributeSnippetLocationType.IMPORT
                ));
                attributeSnippets.add(new AttributeSnippet(
                        "PropertyVetoException",
                        SETTER_THROWS
                ));
                attributeSnippets.add(new AttributeSnippet(
                        String.format("vetoableChangeSupport.fireVetoableChange(%s, %s, %s);", prop, oldProp, name),
                        PRE_SETTER
                ));
            }
            if (variableDef.isPropertyChangeSupport()) {
                attributeSnippets.add(new AttributeSnippet(
                        String.format("propertyChangeSupport.firePropertyChange(%s, %s, %s);", prop, oldProp, name),
                        PRE_SETTER
                ));
            }
//            if (variableDef.isPropertyChangeSupport() || variableDef.isVetoableChangeSupport()) {
//                attributeSnippets.add(new AttributeSnippet(
//                        String.format("this.%s = %s;", name, name),
//                        SETTER
//                ));
//            }
        }
        return attributeSnippets;
    }

}
