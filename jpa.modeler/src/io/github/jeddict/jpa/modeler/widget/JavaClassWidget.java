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
package io.github.jeddict.jpa.modeler.widget;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.lang.model.SourceVersion;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.StringUtils;
import org.atteo.evo.inflector.English;
import org.netbeans.api.visual.widget.Widget;
import io.github.jeddict.jcode.util.SourceGroupSupport;
import io.github.jeddict.jcode.util.StringHelper;
import static io.github.jeddict.jcode.util.StringHelper.firstUpper;
import static io.github.jeddict.jcode.util.StringHelper.getNext;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.widget.flow.GeneralizationFlowWidget;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getClassAnnoation;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getClassSnippet;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getConstructorProperties;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getCustomArtifact;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getCustomParentClass;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getToStringProperty;
import io.github.jeddict.jpa.modeler.rules.attribute.AttributeValidator;
import io.github.jeddict.jpa.modeler.rules.entity.ClassValidator;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.IdentifiableClass;
import io.github.jeddict.jpa.spec.extend.AttributeLocationComparator;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.modeler.initializer.JPAModelerScene;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.model.document.IColorScheme;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import static org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType.ERROR;
import static org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType.WARNING;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.netbeans.modeler.widget.properties.handler.PropertyChangeListener;
import org.netbeans.modules.j2ee.persistence.dd.JavaPersistenceQLKeywords;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getEqualsHashcodeProperty;

public abstract class JavaClassWidget<E extends JavaClass> extends FlowNodeWidget<E, JPAModelerScene> {

    private GeneralizationFlowWidget outgoingGeneralizationFlowWidget;
    private final List<GeneralizationFlowWidget> incomingGeneralizationFlowWidgets = new ArrayList<>();

    public JavaClassWidget(JPAModelerScene scene, NodeWidgetInfo node) {
        super(scene, node);
        this.addPropertyChangeListener("class", (PropertyChangeListener<String>) (oldValue, clazz) -> {
            if (clazz == null || clazz.trim().isEmpty()) {
                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), NbBundle.getMessage(ClassValidator.class, ClassValidator.EMPTY_CLASS_NAME));
                setName(JavaClassWidget.this.getLabel());//rollback
            } else {
                clazz = StringHelper.firstUpper(clazz);
                setName(clazz);
                setLabel(clazz);
            }
        });
        this.setImage(this.getNodeWidgetInfo().getModelerDocument().getImage());
    }

    @Override
    public void init() {
        super.init();
        addOpenSourceCodeAction();
    }

    protected void addOpenSourceCodeAction() {
        this.getImageWidget().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.getImageWidget().getActions().addAction(
                new OpenSourceCodeAction(
                        () -> getFileObject(),
                        this.getBaseElementSpec(),
                        this.getModelerScene().getModelerFile()
                )
        );
    }

    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        JavaClass javaClass = this.getBaseElementSpec();

        set.put("CLASS_STRUCTURE", getClassAnnoation(this.getModelerScene(), javaClass.getAnnotation()));
        set.put("CLASS_STRUCTURE", getCustomParentClass(this));
        set.put("CLASS_STRUCTURE", getCustomArtifact(this.getModelerScene(), javaClass.getInterfaces(), "Interface"));
        set.put("CLASS_STRUCTURE", getClassSnippet(this.getModelerScene(), javaClass.getSnippets()));
        set.put("CLASS_STRUCTURE", getConstructorProperties(this));
        set.put("CLASS_STRUCTURE", getEqualsHashcodeProperty(this));
        set.put("CLASS_STRUCTURE", getToStringProperty(this));
    }

    public FileObject getFileObject() {
        JavaClass javaClass = (JavaClass) this.getBaseElementSpec();
        ModelerFile modelerFile = this.getModelerScene().getModelerFile();
        return getFileObject(javaClass, modelerFile);
    }

    public static FileObject getFileObject(JavaClass javaClass, ModelerFile modelerFile) {
        if (javaClass.getFileObject() == null) {
            javaClass.setFileObject(SourceGroupSupport.getJavaFileObject(modelerFile.getSourceGroup(), javaClass.getFQN()));
        }
        if (javaClass.getFileObject() == null) {
            javaClass.setFileObject(SourceGroupSupport.getJavaFileObject(modelerFile.getProject(), javaClass.getFQN()));
        }
        return javaClass.getFileObject();
    }

    public abstract void deleteAttribute(AttributeWidget attributeWidget);

    @Override
    public void deletePinWidget(IPinWidget pinWidget) {
        super.deletePinWidget(pinWidget);
        deleteAttribute((AttributeWidget) pinWidget);//  Issue Fix #5855
    }

    public void sortAttributes() {
        sortPins(getAttributeCategories());
    }

    public abstract Map<String, List<Widget>> getAttributeCategories();

    protected void validateName(String previousName, String name) {
        if (JavaPersistenceQLKeywords.isKeyword(JavaClassWidget.this.getName())) {
            getSignalManager().fire(ERROR, ClassValidator.CLASS_NAME_WITH_JPQL_KEYWORD);
        } else {
            getSignalManager().clear(ERROR, ClassValidator.CLASS_NAME_WITH_JPQL_KEYWORD);
        }
        if (JavaClass.isAutoGenerated(JavaClassWidget.this.getName())) {
            getSignalManager().fire(WARNING, ClassValidator.CLASS_NAME_WITH_AUTO_GEN_ENITY);
        } else {
            getSignalManager().clear(WARNING, ClassValidator.CLASS_NAME_WITH_AUTO_GEN_ENITY);
        }
        if (SourceVersion.isName(name)) {
            getSignalManager().clear(ERROR, ClassValidator.INVALID_CLASS_NAME);
        } else {
            getSignalManager().fire(ERROR, ClassValidator.INVALID_CLASS_NAME);
        }
        scanDuplicateClass(previousName, name);
        scanReservedDefaultClass(previousName, name);
    }

    public void scanDuplicateClass(String previousName, String newName) {
        int previousNameCount = 0, newNameCount = 0;
        List<JavaClassWidget> javaClassList = this.getModelerScene().getJavaClassWidges();
        EntityMappings entityMappings = this.getModelerScene().getBaseElementSpec();

        List<JavaClass> hiddenJavaClasses = new ArrayList<>(entityMappings.getJavaClass());
        hiddenJavaClasses.removeAll(
                javaClassList.stream()
                        .map(JavaClassWidget::getBaseElementSpec)
                        .collect(toList())
        );
        for (JavaClass javaClass : hiddenJavaClasses) {
            if (javaClass.getClazz().equals(previousName)) {
                ++previousNameCount;
            }
            if (javaClass.getClazz().equals(newName)) {
                ++newNameCount;
            }
        }

        for (JavaClassWidget<JavaClass> javaClassWidget : javaClassList) {
            JavaClass javaClass = javaClassWidget.getBaseElementSpec();

            if (javaClass.getClazz().equals(previousName)) {
                if (++previousNameCount > 1) {
                    javaClassWidget.getSignalManager().fire(ERROR, ClassValidator.NON_UNIQUE_JAVA_CLASS);
                } else if (!javaClassWidget.getSignalManager().getSignalList(ERROR).isEmpty()) {
                    javaClassWidget.getSignalManager().clear(ERROR, ClassValidator.NON_UNIQUE_JAVA_CLASS);
                }
            }

            if (javaClass.getClazz().equals(newName)) {
                if (++newNameCount > 1) {
                    javaClassWidget.getSignalManager().fire(ERROR, ClassValidator.NON_UNIQUE_JAVA_CLASS);
                } else if (!javaClassWidget.getSignalManager().getSignalList(ERROR).isEmpty()) {
                    javaClassWidget.getSignalManager().clear(ERROR, ClassValidator.NON_UNIQUE_JAVA_CLASS);
                }
            }
        }
    }

    //to fix class name
    protected String filterName(String name) {
        if (StringUtils.isNotBlank(name)) {
            name = firstUpper(name.replaceAll("\\s+", ""));
        }
        return name;
    }

    @Override
    public void setLabel(String label) {
        if (StringUtils.isNotBlank(label)) {
            this.setNodeName(filterName(label));
        }
    }
    
    public JavaClassWidget getSuperclassWidget() {
        if (outgoingGeneralizationFlowWidget != null) {
            return outgoingGeneralizationFlowWidget.getSuperclassWidget();
        }
        return null;
    }

    public List<JavaClassWidget> getAllSuperclassWidget() {
        List<JavaClassWidget> superclassWidgetList = new LinkedList<>();
        boolean exist = false;
        GeneralizationFlowWidget generalizationFlowWidget_TMP = this.outgoingGeneralizationFlowWidget;
        if (generalizationFlowWidget_TMP != null) {
            exist = true;
        }
        while (exist) {
            JavaClassWidget superclassWidget_Next = generalizationFlowWidget_TMP.getSuperclassWidget();
            superclassWidgetList.add(superclassWidget_Next);
            generalizationFlowWidget_TMP = superclassWidget_Next.getOutgoingGeneralizationFlowWidget();
            if (generalizationFlowWidget_TMP == null) {
                exist = false;
            }
        }
        return superclassWidgetList;
    }

    public List<JavaClassWidget> getSubclassWidgets() {
        List<JavaClassWidget> subclassWidgetList = new LinkedList<>();
        for (GeneralizationFlowWidget generalizationFlowWidget_TMP : this.incomingGeneralizationFlowWidgets) {
            JavaClassWidget subclassWidget_Nest = generalizationFlowWidget_TMP.getSubclassWidget();
            subclassWidgetList.add(subclassWidget_Nest);
        }
        return subclassWidgetList;
    }

    public List<JavaClassWidget> getAllSubclassWidgets() {
        List<JavaClassWidget> subclassWidgetList = new LinkedList<>();
        for (GeneralizationFlowWidget generalizationFlowWidget_TMP : this.incomingGeneralizationFlowWidgets) {
            JavaClassWidget subclassWidget_Nest = generalizationFlowWidget_TMP.getSubclassWidget();
            subclassWidgetList.add(subclassWidget_Nest);
            subclassWidgetList.addAll(subclassWidget_Nest.getAllSubclassWidgets());
        }
        return subclassWidgetList;
    }

    /**
     * @return the outgoingGeneralizationFlowWidget
     */
    public GeneralizationFlowWidget getOutgoingGeneralizationFlowWidget() {
        return outgoingGeneralizationFlowWidget;
    }

    /**
     * @param outgoingGeneralizationFlowWidget the
     * outgoingGeneralizationFlowWidget to set
     */
    public void setOutgoingGeneralizationFlowWidget(GeneralizationFlowWidget outgoingGeneralizationFlowWidget) {
        this.outgoingGeneralizationFlowWidget = outgoingGeneralizationFlowWidget;
    }

    /**
     * @return the incomingGeneralizationFlowWidgets
     */
    public List<GeneralizationFlowWidget> getIncomingGeneralizationFlowWidgets() {
        return incomingGeneralizationFlowWidgets;
    }

    public void addIncomingGeneralizationFlowWidget(GeneralizationFlowWidget generalizationFlowWidget) {
        incomingGeneralizationFlowWidgets.add(generalizationFlowWidget);
    }

    public void removeIncomingGeneralizationFlowWidget(GeneralizationFlowWidget generalizationFlowWidget) {
        incomingGeneralizationFlowWidgets.remove(generalizationFlowWidget);
    }

    public abstract InheritanceStateType getInheritanceState();

    public abstract InheritanceStateType getInheritanceState(boolean includeAllClass);

////    private static final Border WIDGET_BORDER = new ShadowBorder(new Color(255, 25, 25) ,2, new Color(255, 25, 25), new Color(255, 255, 255), new Color(255, 25, 25), new Color(255, 255, 255), new Color(255, 25, 25));
    public void showInheritancePath() {
        IColorScheme colorScheme = this.getModelerScene().getColorScheme();
        colorScheme.highlightUI(this);
        this.setHighlightStatus(true);
//        this.setBorder(colorScheme.);
        if (this.getOutgoingGeneralizationFlowWidget() != null) {
            this.getOutgoingGeneralizationFlowWidget().setHighlightStatus(true);
            colorScheme.highlightUI(this.getOutgoingGeneralizationFlowWidget());
//            this.getOutgoingGeneralizationFlowWidget().setForeground(Color.red);
            this.getOutgoingGeneralizationFlowWidget().getSuperclassWidget().showInheritancePath();
        }
    }

    public void hideInheritancePath() {
        IColorScheme colorScheme = this.getModelerScene().getColorScheme();
        this.setHighlightStatus(false);
        colorScheme.updateUI(this, this.getState(), this.getState());
        if (this.getOutgoingGeneralizationFlowWidget() != null) {
            this.getOutgoingGeneralizationFlowWidget().setHighlightStatus(false);
            colorScheme.updateUI(this.getOutgoingGeneralizationFlowWidget(), this.getOutgoingGeneralizationFlowWidget().getState(), this.getOutgoingGeneralizationFlowWidget().getState());
            this.getOutgoingGeneralizationFlowWidget().getSuperclassWidget().hideInheritancePath();
        }
    }

    public void showCompositionPath() {
        IColorScheme colorScheme = this.getModelerScene().getColorScheme();
        colorScheme.highlightUI(this);
        this.setHighlightStatus(true);
    }

    public void hideCompositionPath() {
        IColorScheme colorScheme = this.getModelerScene().getColorScheme();
        this.setHighlightStatus(false);
        colorScheme.updateUI(this, this.getState(), this.getState());
    }

    /**
     * To reserve DefaultClass name should not be used by any visual artifact
     * e.g Embeddable etc.
     *
     * @param previousName
     * @param newName
     */
    public void scanReservedDefaultClass(String previousName, String newName) {
        int previousNameCount = 0, newNameCount = 0;
        List<String> previousNameClasses = new ArrayList<>(), newNameClasses = new ArrayList<>();

        List<JavaClassWidget> javaClassList = this.getModelerScene().getJavaClassWidges();
        EntityMappings entityMappings = this.getModelerScene().getBaseElementSpec();
        for (JavaClass javaClass : entityMappings.getJavaClass()) {
            if (javaClass instanceof IdentifiableClass) {
                IdentifiableClass ic = (IdentifiableClass) javaClass;
                if (ic.getCompositePrimaryKeyType() != null && ic.getCompositePrimaryKeyClass() != null) {
                    if (ic.getCompositePrimaryKeyClass().equals(previousName)) {
                        ++previousNameCount;
                        previousNameClasses.add(ic.getClazz() + ".<" + ic.getAttributes().getEmbeddedId().getName() + '>');
                    }
                    if (ic.getCompositePrimaryKeyClass().equals(newName)) {
                        ++newNameCount;
                        newNameClasses.add(ic.getClazz() + ".<" + ic.getAttributes().getEmbeddedId().getName() + '>');
                    }
                }
            }
        }

        for (JavaClassWidget<JavaClass> javaClassWidget : javaClassList) {
            JavaClass javaClass = javaClassWidget.getBaseElementSpec();

            if (javaClass.getClazz().equals(previousName)) {
                if (++previousNameCount > 1) {
                    javaClassWidget.getSignalManager().fire(ERROR, ClassValidator.CLASS_NAME_USED_BY_DEFAULT_CLASS, previousName, previousNameClasses.toString());
                } else if (!javaClassWidget.getSignalManager().getSignalList(ERROR).isEmpty()) {
                    javaClassWidget.getSignalManager().clear(ERROR, ClassValidator.CLASS_NAME_USED_BY_DEFAULT_CLASS);
                }
            }

            if (javaClass.getClazz().equals(newName)) {
                if (++newNameCount > 1) {
                    javaClassWidget.getSignalManager().fire(ERROR, ClassValidator.CLASS_NAME_USED_BY_DEFAULT_CLASS, newName, newNameClasses.toString());
                } else if (!javaClassWidget.getSignalManager().getSignalList(ERROR).isEmpty()) {
                    javaClassWidget.getSignalManager().clear(ERROR, ClassValidator.CLASS_NAME_USED_BY_DEFAULT_CLASS);
                }
            }
        }
    }

    public String getNextAttributeName() {
        return getNextAttributeName(null);
    }

    public String getNextAttributeName(String attrName) {
        return getNextAttributeName(attrName, false);
    }

    public String getNextAttributeName(String attrName, boolean multi) {
        if (attrName == null || attrName.trim().isEmpty()) {
            attrName = "attribute";
        }
        attrName = StringHelper.firstLower(attrName);
        if (multi) {
            attrName = English.plural(attrName);
        }
        JavaClass javaClass = this.getBaseElementSpec();
        if (javaClass.getAttributes() == null) {
            return attrName;
        }
        return getNext(attrName, nextAttrName -> javaClass.getAttributes().isAttributeExist(nextAttrName));
    }

    public List<AttributeWidget<? extends Attribute>> getAllAttributeWidgets() {
        return getAllAttributeWidgets(true);
    }

    public List<AttributeWidget<? extends Attribute>> getAllSortedAttributeWidgets() {
        List<AttributeWidget<? extends Attribute>> attributeWidgets = getAllAttributeWidgets(true);
        AttributeLocationComparator attributeLocationComparator = new AttributeLocationComparator();
        attributeWidgets.sort((a1, a2) -> attributeLocationComparator.compare(a1.getBaseElementSpec(), a2.getBaseElementSpec()));
        return attributeWidgets;
    }

    public void scanDuplicateAttributes(String previousName, String newName) {
        int previousNameCount = 0, newNameCount = 0;
        List<AttributeWidget<? extends Attribute>> attributeWidgets = this.getAllAttributeWidgets(true);
        JavaClass javaClass = this.getBaseElementSpec();

        List<Attribute> hiddenAttributes = new ArrayList<>(javaClass.getAttributes().getAllAttribute(true));
        hiddenAttributes.removeAll(
                attributeWidgets.stream()
                        .map(aw -> (Attribute) aw.getBaseElementSpec())
                        .collect(toList())
        );
        for (Attribute attribute : hiddenAttributes) {
            if (attribute.getName().equals(previousName)) {
                ++previousNameCount;
            }
            if (attribute.getName().equals(newName)) {
                ++newNameCount;
            }
        }

        for (AttributeWidget<? extends Attribute> attributeWidget : attributeWidgets) {
            Attribute attribute = attributeWidget.getBaseElementSpec();

            if (attribute.getName().equals(previousName)) {
                if (++previousNameCount > 1) {
                    attributeWidget.getSignalManager().fire(ERROR, AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
                } else if (!attributeWidget.getSignalManager().getSignalList(ERROR).isEmpty()) {
                    attributeWidget.getSignalManager().clear(ERROR, AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
                }
            }

            if (attribute.getName().equals(newName)) {
                if (++newNameCount > 1) {
                    attributeWidget.getSignalManager().fire(ERROR, AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
                } else if (!attributeWidget.getSignalManager().getSignalList(ERROR).isEmpty()) {
                    attributeWidget.getSignalManager().clear(ERROR, AttributeValidator.NON_UNIQUE_ATTRIBUTE_NAME);
                }
            }
        }
    }

    public abstract List<AttributeWidget<? extends Attribute>> getAllAttributeWidgets(boolean includeParentClassAttibute);

    public abstract E createBaseElementSpec();

    public abstract void createPinWidget(String docId);
}
