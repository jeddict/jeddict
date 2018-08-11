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
package io.github.jeddict.jpa.modeler.initializer;

import io.github.jeddict.collaborate.enhancement.EnhancementRequestHandler;
import io.github.jeddict.jcode.util.JavaSourceHelper;
import io.github.jeddict.jcode.util.StringHelper;
import static io.github.jeddict.jcode.util.StringHelper.getNext;
import io.github.jeddict.jpa.modeler.external.jpqleditor.JPQLExternalEditorController;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.GENERATE_SRC;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.PERSISTENCE_UNIT;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.RUN_JPQL_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.SEARCH_ICON;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.SOCIAL_NETWORK_SHARING;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.VIEW_DB;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.VIEW_JSONB;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getClassSnippet;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getConverterProperties;
import static io.github.jeddict.jpa.modeler.properties.PropertiesHandler.getCustomArtifact;
import io.github.jeddict.jpa.modeler.specification.model.event.JPAEventListener;
import io.github.jeddict.jpa.modeler.specification.model.workspace.WorkSpaceManager;
import io.github.jeddict.jpa.modeler.widget.EmbeddableWidget;
import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.modeler.widget.FlowNodeWidget;
import io.github.jeddict.jpa.modeler.widget.JavaClassWidget;
import io.github.jeddict.jpa.modeler.widget.PersistenceClassWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.association.AssociationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.EmbeddedAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.RelationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.flow.EmbeddableFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.GeneralizationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.AssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.BidirectionalAssociation;
import io.github.jeddict.jpa.modeler.widget.flow.association.DirectionalAssociation;
import io.github.jeddict.jpa.modeler.widget.flow.association.UnidirectionalAssociation;
import io.github.jeddict.jpa.modeler.widget.flow.relation.BidirectionalRelation;
import io.github.jeddict.jpa.modeler.widget.flow.relation.DirectionalRelation;
import io.github.jeddict.jpa.modeler.widget.flow.relation.RelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.UnidirectionalRelation;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.extend.IPersistenceAttributes;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.network.social.LinkedInSocialNetwork;
import io.github.jeddict.network.social.TwitterSocialNetwork;
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
import static org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType.ERROR;
import org.netbeans.modeler.widget.node.vmd.internal.PFactory;
import org.netbeans.modules.j2ee.persistence.provider.InvalidPersistenceXmlException;
import org.netbeans.modules.j2ee.persistence.provider.ProviderUtil;
import org.netbeans.modules.j2ee.persistence.unit.PUDataObject;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

public class JPAModelerScene extends DefaultPModelerScene<EntityMappings> {

    private final WorkSpaceManager workSpaceManager;

    public JPAModelerScene() {
        workSpaceManager = new WorkSpaceManager(this);
        addWidgetDropListener(new WidgetDropListenerImpl());
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

                    if (relationFlowWidget instanceof DirectionalRelation) {
                        if (relationFlowWidget instanceof UnidirectionalRelation) {
                            UnidirectionalRelation unidirectional = (UnidirectionalRelation) relationFlowWidget;
                            unidirectional.getTargetEntityWidget().removeInverseSideRelationFlowWidget(relationFlowWidget);
                        } else if (relationFlowWidget instanceof BidirectionalRelation) {
                            BidirectionalRelation bidirectional = (BidirectionalRelation) relationFlowWidget;
                            RelationAttributeWidget targetRelationAttributeWidget = bidirectional.getTargetRelationAttributeWidget();
                            targetRelationAttributeWidget.remove();
                        }
                    }
                    relationFlowWidget.setLocked(false);
                    relationFlowWidget.setFlowElementsContainer(null);
                    this.removeBaseElement(relationFlowWidget);
                } else if (baseElementWidget instanceof AssociationFlowWidget) {
                    AssociationFlowWidget associationFlowWidget = (AssociationFlowWidget) baseElementWidget;
                    associationFlowWidget.setLocked(true);
                    AssociationAttributeWidget sourceAssociationAttributeWidget = associationFlowWidget.getSourceAssociationAttributeWidget();
                    sourceAssociationAttributeWidget.remove();

                    if (associationFlowWidget instanceof DirectionalAssociation) {
                        if (associationFlowWidget instanceof UnidirectionalAssociation) {
                            UnidirectionalAssociation unidirectional = (UnidirectionalAssociation) associationFlowWidget;
                            unidirectional.getTargetClassWidget().removeInverseSideAssociationFlowWidget(associationFlowWidget);
                        } else if (associationFlowWidget instanceof BidirectionalAssociation) {
                            BidirectionalAssociation bidirectional = (BidirectionalAssociation) associationFlowWidget;
                            AssociationAttributeWidget targetAssociationAttributeWidget = bidirectional.getTargetAssociationAttributeWidget();
                            targetAssociationAttributeWidget.remove();
                        }
                    }
                    associationFlowWidget.setLocked(false);
                    associationFlowWidget.setFlowElementsContainer(null);
                    this.removeBaseElement(associationFlowWidget);
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
                    if (baseElementWidget instanceof JavaClassWidget) {
                        baseElement = ((JavaClassWidget)baseElementWidget).createBaseElementSpec();
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
            file.save(true);
        }
        getWorkSpaceManager().loadWorkspaceUI();
    }

    @Override
    public void destroy() {
        this.getModelerFile().getChildrenFile("JSONB").ifPresent(ModelerFile::close);
        this.getModelerFile().getChildrenFile("DB").ifPresent(ModelerFile::close);
    }

    @NbBundle.Messages({
        "GENERATE_SRC=Generate Source Code",
        "VIS_DB=Visualize DB",
        "VIS_JSON=JSONB View",
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
        visDB.addActionListener(e -> DBUtil.openDBModeler(this.getModelerFile()));
        
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
