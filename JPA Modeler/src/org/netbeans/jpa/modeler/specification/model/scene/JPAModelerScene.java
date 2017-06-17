/**
 * Copyright [2013] Gaurav Gupta
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
package org.netbeans.jpa.modeler.specification.model.scene;

import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import org.netbeans.api.project.Project;
import org.netbeans.jcode.core.util.JavaSourceHelper;
import org.netbeans.jcode.core.util.StringHelper;
import static org.netbeans.jcode.core.util.StringHelper.getNext;
import org.netbeans.jpa.modeler.collaborate.enhancement.EnhancementRequestHandler;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.FlowNodeWidget;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.MappedSuperclassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.EmbeddableFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Bidirectional;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Direction;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Unidirectional;
import org.netbeans.jpa.modeler.external.jpqleditor.JPQLExternalEditorController;
import org.netbeans.jpa.modeler.network.social.linkedin.LinkedInSocialNetwork;
import org.netbeans.jpa.modeler.network.social.twitter.TwitterSocialNetwork;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getClassSnippet;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getConverterProperties;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.specification.model.event.JPAEventListener;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.GENERATE_SRC;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.PERSISTENCE_UNIT;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.SOCIAL_NETWORK_SHARING;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.VIEW_DB;
import org.netbeans.modeler.actions.IEventListener;
import org.netbeans.modeler.config.element.ElementConfigFactory;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.core.scene.vmd.DefaultPModelerScene;
import org.netbeans.modeler.specification.model.document.IColorScheme;
import org.netbeans.modeler.specification.model.document.IRootElement;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.specification.version.SoftwareVersion;
import org.netbeans.modeler.widget.edge.vmd.PEdgeWidget;
import org.netbeans.modeler.widget.node.IWidget;
import org.netbeans.modeler.widget.node.vmd.internal.PFactory;
import org.netbeans.modules.j2ee.persistence.provider.InvalidPersistenceXmlException;
import org.netbeans.modules.j2ee.persistence.provider.ProviderUtil;
import org.netbeans.modules.j2ee.persistence.unit.PUDataObject;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import static org.netbeans.jpa.modeler.properties.PropertiesHandler.getCustomArtifact;
import org.netbeans.jpa.modeler.spec.extend.IPersistenceAttributes;
import org.netbeans.jpa.modeler.specification.model.util.DBUtil;
import org.netbeans.jpa.modeler.specification.model.workspace.WorkSpaceManager;
import static org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType.ERROR;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.RUN_JPQL_ICON;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.SEARCH_ICON;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.VIEW_JSONB;
import org.netbeans.jpa.modeler.specification.model.util.JSONBUtil;
import org.openide.util.NbPreferences;

public class JPAModelerScene extends DefaultPModelerScene<EntityMappings> {

    private final WorkSpaceManager workSpaceManager;

    public JPAModelerScene() {
        workSpaceManager = new WorkSpaceManager(this);
    }
    
    @Override
    public void createPropertySet(ElementPropertySet set) {
        super.createPropertySet(set);
        EntityMappings entityMappings = this.getBaseElementSpec();
        set.put("BASIC_PROP", getConverterProperties(this, entityMappings.getConverter()));
        set.put("GLOBAL_CONFIG", getClassSnippet(this, entityMappings.getSnippets()));
        set.put("GLOBAL_CONFIG", getCustomArtifact(this, entityMappings.getInterfaces(), "Interface"));

    }
   
    public List<EntityWidget> getEntityWidgets() {
        List<EntityWidget> entityWidgets = new ArrayList<>();
        for (IBaseElementWidget baseElement : getBaseElements()) {
            if (baseElement instanceof EntityWidget) {
                entityWidgets.add((EntityWidget) baseElement);
            }
        }
        return entityWidgets;
    }

    public List<JavaClassWidget> getJavaClassWidges() {
        List<JavaClassWidget> classWidgets = new ArrayList<>();
        for (IBaseElementWidget baseElement : getBaseElements()) {
            if (baseElement instanceof JavaClassWidget) {
                classWidgets.add((JavaClassWidget) baseElement);
            }
        }
        return classWidgets;
    }

    public boolean compile() {
        boolean compiled = true;
        StringBuilder errorMessage = new StringBuilder();
        for (IBaseElementWidget e : getBaseElements()) {
            boolean failure = false;
            if (e instanceof PersistenceClassWidget) {
                PersistenceClassWidget<ManagedClass<IPersistenceAttributes>> p = ((PersistenceClassWidget<ManagedClass<IPersistenceAttributes>>) e);
                if (!p.getSignalManager().getSignalList(ERROR).isEmpty()) {
                    errorMessage.append(p.getName()).append(':').append('\n');
                    p.getSignalManager().getSignalList(ERROR).values().forEach(v -> {
                        errorMessage.append('\t').append(v).append('\n');
                    });
                    failure = true;
                }
                for (AttributeWidget attributeWidget : p.getAllAttributeWidgets(false)) {
                    if (!attributeWidget.getSignalManager().getSignalList(ERROR).isEmpty()) {
                        errorMessage.append('\t').append(p.getName()).append('.').append(attributeWidget.getName()).append(':').append('\n');
                        attributeWidget.getSignalManager().getSignalList(ERROR).values().forEach(v -> {
                            errorMessage.append('\t').append('\t').append(v).append('\n');
                        });
                        failure = true;
                    }
                }
            }
            if (failure) {
                compiled = false;
                errorMessage.append('\n');
            }
        }

        if (!compiled) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(errorMessage, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }

        return compiled;
    }

    @Override
    public void deleteBaseElement(IBaseElementWidget baseElementWidget) {
        EntityMappings entityMappingsSpec = (EntityMappings) this.getModelerFile().getModelerScene().getBaseElementSpec();
        if (baseElementWidget instanceof IFlowElementWidget) {
            if (baseElementWidget instanceof FlowNodeWidget) { //reverse refractorRelationSynchronously
                FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                IBaseElement baseElementSpec = flowNodeWidget.getBaseElementSpec();
                if (baseElementWidget instanceof JavaClassWidget) {
                    JavaClassWidget<JavaClass> javaClassWidget = (JavaClassWidget) baseElementWidget;
                    if (javaClassWidget.getOutgoingGeneralizationFlowWidget() != null) {
                        javaClassWidget.getOutgoingGeneralizationFlowWidget().remove();
                    }
                    for (GeneralizationFlowWidget generalizationFlowWidget : new CopyOnWriteArrayList<>(javaClassWidget.getIncomingGeneralizationFlowWidgets())) {
                        generalizationFlowWidget.remove();
                    }

                    if (baseElementWidget instanceof PersistenceClassWidget) {
                        PersistenceClassWidget<ManagedClass<IPersistenceAttributes>> persistenceClassWidget = (PersistenceClassWidget<ManagedClass<IPersistenceAttributes>>) baseElementWidget;
                        persistenceClassWidget.setLocked(true); //this method is used to prevent from reverse call( Recursion call) //  Source-flow-target any of deletion will delete each other so as deletion prcedd each element locked
                        for (RelationFlowWidget relationFlowWidget : new CopyOnWriteArrayList<>(persistenceClassWidget.getInverseSideRelationFlowWidgets())) {
                            relationFlowWidget.remove();
                        }
                        for (RelationAttributeWidget relationAttributeWidget : persistenceClassWidget.getRelationAttributeWidgets()) {
                            if (relationAttributeWidget.getRelationFlowWidget() != null) {//Bug : compatibility issue
                                relationAttributeWidget.getRelationFlowWidget().remove();
                            }
                        }
                        for (EmbeddedAttributeWidget embeddedAttributeWidget : persistenceClassWidget.getEmbeddedAttributeWidgets()) {
                            embeddedAttributeWidget.getEmbeddableFlowWidget().remove();
                        }

                        if (baseElementWidget instanceof EmbeddableWidget) {
                            EmbeddableWidget embeddableWidget = (EmbeddableWidget) baseElementWidget;
                            for (EmbeddableFlowWidget embeddableFlowWidget : new CopyOnWriteArrayList<>(embeddableWidget.getIncomingEmbeddableFlowWidgets())) {
                                embeddableFlowWidget.remove();
                            }
                        }

                        persistenceClassWidget.setLocked(false);
                    }

                }
                entityMappingsSpec.removeBaseElement(baseElementSpec);
                flowNodeWidget.setFlowElementsContainer(null);
                this.removeBaseElement(flowNodeWidget);
            } else if (baseElementWidget instanceof IFlowEdgeWidget) {
                if (baseElementWidget instanceof RelationFlowWidget) {
                    RelationFlowWidget relationFlowWidget = (RelationFlowWidget) baseElementWidget;
                    relationFlowWidget.setLocked(true);
                    RelationAttributeWidget sourceRelationAttributeWidget = relationFlowWidget.getSourceRelationAttributeWidget();
                    sourceRelationAttributeWidget.remove();

                    if (relationFlowWidget instanceof Direction) {
                        if (relationFlowWidget instanceof Unidirectional) {
                            Unidirectional unidirectional = (Unidirectional) relationFlowWidget;
                            unidirectional.getTargetEntityWidget().removeInverseSideRelationFlowWidget(relationFlowWidget);
                        } else if (relationFlowWidget instanceof Bidirectional) {
                            Bidirectional bidirectional = (Bidirectional) relationFlowWidget;
                            RelationAttributeWidget targetRelationAttributeWidget = bidirectional.getTargetRelationAttributeWidget();
                            targetRelationAttributeWidget.remove();
                        }
                    }
                    relationFlowWidget.setLocked(false);

                    relationFlowWidget.setFlowElementsContainer(null);
                    this.removeBaseElement(relationFlowWidget);
                } else if (baseElementWidget instanceof GeneralizationFlowWidget) {
                    GeneralizationFlowWidget generalizationFlowWidget = (GeneralizationFlowWidget) baseElementWidget;

                    generalizationFlowWidget.getSubclassWidget().setOutgoingGeneralizationFlowWidget(null);
                    generalizationFlowWidget.getSuperclassWidget().removeIncomingGeneralizationFlowWidget(generalizationFlowWidget);
                    JavaClass javaSubclass = (JavaClass) generalizationFlowWidget.getSubclassWidget().getBaseElementSpec();
                    JavaClass javaSuperclass = (JavaClass) generalizationFlowWidget.getSuperclassWidget().getBaseElementSpec();
                    javaSubclass.removeSuperclass(javaSuperclass);

                    generalizationFlowWidget.setFlowElementsContainer(null);
                    this.removeBaseElement(generalizationFlowWidget);

                } else if (baseElementWidget instanceof EmbeddableFlowWidget) {
                    EmbeddableFlowWidget embeddableFlowWidget = (EmbeddableFlowWidget) baseElementWidget;
                    embeddableFlowWidget.setLocked(true);
                    EmbeddedAttributeWidget sourceEmbeddedAttributeWidget = embeddableFlowWidget.getSourceEmbeddedAttributeWidget();
                    sourceEmbeddedAttributeWidget.remove();
                    embeddableFlowWidget.getTargetEmbeddableWidget().removeIncomingEmbeddableFlowWidget(embeddableFlowWidget);

                    embeddableFlowWidget.setLocked(false);

                    embeddableFlowWidget.setFlowElementsContainer(null);
                    this.removeBaseElement(embeddableFlowWidget);
                } else {
                    throw new InvalidElmentException("Invalid JPA Element");
                }

            } else {
                throw new InvalidElmentException("Invalid JPA Element");
            }

        }
    }

    @Override
    public void createBaseElement(IBaseElementWidget baseElementWidget) {
        String baseElementId = "";
        Boolean isExist = false;
        if (baseElementWidget instanceof IFlowElementWidget) {
            this.addBaseElement((IFlowElementWidget) baseElementWidget);
            if (baseElementWidget instanceof IFlowNodeWidget) { //reverse refractorRelationSynchronously
                ((FlowNodeWidget) baseElementWidget).setFlowElementsContainer(this);
                baseElementId = ((FlowNodeWidget) baseElementWidget).getId();
                isExist = ((FlowNodeWidget) baseElementWidget).getNodeWidgetInfo().isExist();
            } else if (baseElementWidget instanceof IFlowEdgeWidget) { //reverse refractorRelationSynchronously
                ((IFlowEdgeWidget) baseElementWidget).setFlowElementsContainer(this);
                baseElementId = ((IFlowEdgeWidget) baseElementWidget).getId();
                isExist = ((PEdgeWidget) baseElementWidget).getEdgeWidgetInfo().isExist();
            } else {
                throw new InvalidElmentException("Invalid JPA FlowElement : " + baseElementWidget);
            }
        } else {
            throw new InvalidElmentException("Invalid JPA Element");
        }

        if (!isExist) {

            IRootElement rootElement = (IRootElement) this.getModelerFile().getModelerScene().getBaseElementSpec();
            IBaseElement baseElement = null;
            if (baseElementWidget instanceof IFlowElementWidget) {
                if (baseElementWidget instanceof IFlowNodeWidget) {
                    if (baseElementWidget instanceof EntityWidget) {
                        baseElement = new Entity();
                        Boolean isAbstract = ((EntityWidget) baseElementWidget).isAbstractEntity();
                        if (isAbstract != null) {
                            ((Entity) baseElement).setAbstract(isAbstract);
                        }
                    } else if (baseElementWidget instanceof MappedSuperclassWidget) {
                        baseElement = new MappedSuperclass();
                    } else if (baseElementWidget instanceof EmbeddableWidget) {
                        baseElement = new Embeddable();
                    } else {
                        throw new InvalidElmentException("Invalid JPA Task Element : " + baseElement);
                    }
                } else if (baseElementWidget instanceof IFlowEdgeWidget) {
                    // skip don't need to create spec RelationFlowWidget, GeneralizationFlowWidget,EmbeddableFlowWidget
                } else {
                    throw new InvalidElmentException("Invalid JPA Element");
                }
            } else {
                throw new InvalidElmentException("Invalid JPA Element");
            }
            if (baseElement != null) {
                baseElementWidget.setBaseElementSpec(baseElement);
                baseElement.setId(baseElementId);
                if (baseElement instanceof JavaClass) {
                    ((JavaClass) baseElement).setAuthor(JavaSourceHelper.getAuthor());
                }
                rootElement.addBaseElement(baseElement);
                ElementConfigFactory elementConfigFactory = this.getModelerFile().getModelerDiagramModel().getElementConfigFactory();
                elementConfigFactory.initializeObjectValue(baseElement);
            }

        } else if (baseElementWidget instanceof IFlowElementWidget) {
            if (baseElementWidget instanceof FlowNodeWidget) {
                FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                flowNodeWidget.setBaseElementSpec(flowNodeWidget.getNodeWidgetInfo().getBaseElementSpec());
            } else {
                throw new InvalidElmentException("Invalid JPA Element");
            }
        } else {
            throw new InvalidElmentException("Invalid JPA Element");
        }

    }

    @Override
    public void init() {
        super.init();

        //After installation of new version, auto save file 
        ModelerFile file = this.getModelerFile();
        EntityMappings entityMappings = (EntityMappings) file.getDefinitionElement();
        if (SoftwareVersion.getInstance(entityMappings.getVersion()).compareTo(file.getArchitectureVersion()) < 0) {
            file.getModelerUtil().saveModelerFile(file);
        }
        
        getWorkSpaceManager().loadWorkspaceUI();
    }

    @Override
    public void destroy() {
    }

    @NbBundle.Messages({
        "GENERATE_SRC=Generate Source Code",
        "VIS_DB=Visualize DB",
        "VIS_JSON=JSONB View (Beta)",
        "SEARCH=Search",
        "PERSISTENCE_UNIT=Persistence.xml",
        "RUN_JPQL=Run JPQL Query",
        "SHARE=Share"
    })
    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = super.getPopupMenuItemList();

        JMenuItem generateCode = new JMenuItem(Bundle.GENERATE_SRC(), GENERATE_SRC);
        generateCode.setAccelerator(KeyStroke.getKeyStroke(Character.valueOf('G'), InputEvent.CTRL_DOWN_MASK));
        generateCode.addActionListener(e -> {
            JPAModelerUtil.generateSourceCode(JPAModelerScene.this.getModelerFile());
        });

        JMenuItem visDB = new JMenuItem(Bundle.VIS_DB(), VIEW_DB);
        visDB.setAccelerator(KeyStroke.getKeyStroke(Character.valueOf('D'), InputEvent.CTRL_DOWN_MASK));
        visDB.addActionListener(e -> DBUtil.openDBViewer(this.getModelerFile()));
        
        JMenuItem visJSONB = new JMenuItem(Bundle.VIS_JSON(), VIEW_JSONB);
        visJSONB.setAccelerator(KeyStroke.getKeyStroke(Character.valueOf('B'), InputEvent.CTRL_DOWN_MASK));
        visJSONB.addActionListener(e -> JSONBUtil.openJSONBViewer(this.getModelerFile()));

        JMenuItem openPUXML = new JMenuItem(Bundle.PERSISTENCE_UNIT(), PERSISTENCE_UNIT);
        openPUXML.addActionListener(e -> {
            Project project = JPAModelerScene.this.getModelerFile().getProject();
            try {
                PUDataObject pud = ProviderUtil.getPUDataObject(project);
                org.netbeans.modules.openfile.OpenFile.open(pud.getPrimaryFile(), -1);
            } catch (InvalidPersistenceXmlException ex) {
                this.getModelerFile().handleException(ex);
            }
        });

        JMenuItem searchMenu = new JMenuItem(Bundle.SEARCH(), SEARCH_ICON);
        searchMenu.setAccelerator(KeyStroke.getKeyStroke(Character.valueOf('F'), InputEvent.CTRL_DOWN_MASK));
        searchMenu.addActionListener(e -> getModelerFile().getModelerDiagramEngine().searchWidget());

        JMenuItem openJPQLPanel = new JMenuItem(Bundle.RUN_JPQL(), RUN_JPQL_ICON);
        openJPQLPanel.addActionListener(e -> new JPQLExternalEditorController().init(JPAModelerScene.this.getModelerFile()));

        JMenu shareModeler = new JMenu(Bundle.SHARE());
        shareModeler.setIcon(SOCIAL_NETWORK_SHARING);
        shareModeler.add(TwitterSocialNetwork.getInstance().getComponent());
        shareModeler.add(LinkedInSocialNetwork.getInstance().getComponent());

        int index = 0;
        menuList.add(index++, getWorkSpaceManager().getWorkSpaceMenu());
        menuList.add(index++, generateCode);
        menuList.add(index++, visDB);
        menuList.add(index++, visJSONB);
        menuList.add(index++, searchMenu);
        menuList.add(index++, null);
        menuList.add(index++, openPUXML);
        menuList.add(index++, openJPQLPanel);
        menuList.add(index++, null);
        menuList.add(index++, shareModeler);
        menuList.add(index++, EnhancementRequestHandler.getInstance().getComponent());

        return menuList;
    }

    public String getNextClassName(String className) {
        if (className == null || className.trim().isEmpty()) {
            className = "class";
        }
        className = StringHelper.firstUpper(className);
        EntityMappings entityMappings = this.getBaseElementSpec();
        return getNext(className, nextClassName -> entityMappings.isClassExist(nextClassName), true);
    }

    @Override
    public IColorScheme getColorScheme(String defaultTheme) {
        EntityMappings entityMappings = this.getBaseElementSpec();
        String theme = entityMappings.getTheme() == null ? defaultTheme : entityMappings.getTheme();
        if (PFactory.getNetBeans60Scheme().getSimpleName().equals(theme)) {
            return PFactory.getColorScheme(PFactory.getNetBeans60Scheme());
        } else if (PFactory.getMetroScheme().getSimpleName().equals(theme)) {
            return PFactory.getColorScheme(PFactory.getMetroScheme());
        } else if (PFactory.getDarkScheme().getSimpleName().equals(theme)) {
            return PFactory.getColorScheme(PFactory.getDarkScheme());
        } else {
            return PFactory.getColorScheme(PFactory.getMacScheme());
        }
    }

    @Override
    public void setColorScheme(Class<? extends IColorScheme> scheme) {
        EntityMappings entityMappings = this.getBaseElementSpec();
        entityMappings.setTheme(scheme.getSimpleName());
    }

    @Override
    public Map<String, Class<? extends IColorScheme>> getColorSchemes() {
        Map<String, Class<? extends IColorScheme>> colorSchemes = new HashMap<>();
        colorSchemes.put("Classic", PFactory.getNetBeans60Scheme());
        colorSchemes.put("Metro", PFactory.getMetroScheme());
        colorSchemes.put("Mac", PFactory.getMacScheme());
        colorSchemes.put("Dark", PFactory.getDarkScheme());
//      colorSchemes.put("Wood", PFactory.getWoodScheme());
        return colorSchemes;
    }

    private IWidget highlightedWidget;

    /**
     * @return the highlightedWidget
     */
    public IWidget getHighlightedWidget() {
        return highlightedWidget;
    }

    /**
     * @param highlightedWidget the highlightedWidget to set
     */
    public void setHighlightedWidget(IWidget highlightedWidget) {
        this.highlightedWidget = highlightedWidget;
    }

    @Override
    protected IEventListener getEventListener() {
        return new JPAEventListener();
    }

    /**
     * @return the workSpaceManager
     */
    public WorkSpaceManager getWorkSpaceManager() {
        return workSpaceManager;
    }
}
