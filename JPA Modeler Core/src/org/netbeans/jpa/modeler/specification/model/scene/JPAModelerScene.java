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

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.netbeans.api.project.Project;
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
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.specification.model.event.JPAEventListener;
import org.netbeans.jpa.modeler.specification.model.file.JPAFileDataObject;
import org.netbeans.jpa.modeler.specification.model.file.action.JPAFileActionListener;
import org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.GENERATE_SRC;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.PERSISTENCE_UNIT;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.SOCIAL_NETWORK_SHARING;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.VIEW_DB;
import org.netbeans.jpa.modeler.visiblity.javaclass.ClassWidgetVisibilityController;
import org.netbeans.modeler.actions.IEventListener;
import org.netbeans.modeler.config.element.ElementConfigFactory;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.core.scene.vmd.DefaultPModelerScene;
import org.netbeans.modeler.specification.model.document.IColorScheme;
import org.netbeans.modeler.specification.model.document.IRootElement;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
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
import org.openide.windows.WindowManager;

public class JPAModelerScene extends DefaultPModelerScene<EntityMappings> {

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
                PersistenceClassWidget<ManagedClass> p = ((PersistenceClassWidget<ManagedClass>) e);
                if (!p.getErrorHandler().getErrorList().isEmpty()) {
                    errorMessage.append(p.getName()).append(':').append('\n');
                    p.getErrorHandler().getErrorList().values().forEach(v -> {
                        errorMessage.append('\t').append(v).append('\n');
                    });
                    failure = true;
                }
                for (AttributeWidget attributeWidget : p.getAllAttributeWidgets()) {
                    if (!attributeWidget.getErrorHandler().getErrorList().isEmpty()) {
                        errorMessage.append('\t').append(p.getName()).append('.').append(attributeWidget.getName()).append(':').append('\n');
                        attributeWidget.getErrorHandler().getErrorList().values().forEach(v -> {
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
            if (baseElementWidget instanceof FlowNodeWidget) { //reverse refactorRelationSynchronously
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
                        PersistenceClassWidget<ManagedClass> persistenceClassWidget = (PersistenceClassWidget) baseElementWidget;
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
            if (baseElementWidget instanceof IFlowNodeWidget) { //reverse refactorRelationSynchronously
                ((FlowNodeWidget) baseElementWidget).setFlowElementsContainer(this);
                baseElementId = ((FlowNodeWidget) baseElementWidget).getId();
                isExist = ((FlowNodeWidget) baseElementWidget).getNodeWidgetInfo().isExist();
            } else if (baseElementWidget instanceof IFlowEdgeWidget) { //reverse refactorRelationSynchronously
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
        //To check env
//        PersistenceEnvironment pe = project!=null ? project.getLookup().lookup(PersistenceEnvironment.class) : null;
//                    if( pe != null ) {
//                        return true;//!Util.isSupportedJavaEEVersion(project);//so far support only non-container managed projects
//                    }
//        SwingUtilities.invokeLater(() -> { //Activiation of OverrideView window (Don't delete)
//            OverrideViewNavigatorComponent window = OverrideViewNavigatorComponent.getInstance();
//            if (!window.isOpened()) {
//                window.open();
//            }
//            window.requestActive();
//        });
        
        //After installation of new version, auto save file 
        ModelerFile file = this.getModelerFile();
        EntityMappings entityMappings = (EntityMappings) file.getDefinitionElement();
        if (SoftwareVersion.getInstance(entityMappings.getVersion()).compareTo(file.getCurrentVersion()) < 0) {
            file.getModelerUtil().saveModelerFile(file);
        }
    }

    @Override
    public void destroy() {
//        SwingUtilities.invokeLater(() -> {
//            OverrideViewNavigatorComponent window = OverrideViewNavigatorComponent.getInstance();
//            if (ModelerCore.getModelerFiles().size() == 1) {
//                window.close();
//            }
//        });
    }

    @NbBundle.Messages({
        "GENERATE_SRC=Generate Source Code",
        "ENTITY_VISIBILTY=Manage Entity Visibility",
        "VIS_DB=Visualize DB",
        "PERSISTENCE_UNIT=Persistence.xml",
        "RYN_JPQL=Run JPQL Query",
        "SHARE=Share"
    })
    @Override
    protected List<JMenuItem> getPopupMenuItemList() {
        List<JMenuItem> menuList = super.getPopupMenuItemList();
        JMenuItem generateCode = new JMenuItem(Bundle.GENERATE_SRC(), GENERATE_SRC);
        generateCode.setAccelerator(KeyStroke.getKeyStroke(Character.valueOf('G'), InputEvent.CTRL_DOWN_MASK));
        generateCode.addActionListener((ActionEvent e) -> {
            JPAModelerUtil.generateSourceCode(JPAModelerScene.this.getModelerFile());
        });

        JMenuItem manageVisibility = new JMenuItem(Bundle.ENTITY_VISIBILTY());
        manageVisibility.addActionListener((ActionEvent e) -> {
            fireEntityVisibilityAction(getModelerFile());
        });

        JMenuItem visDB = new JMenuItem(Bundle.VIS_DB(), VIEW_DB);
        visDB.setAccelerator(KeyStroke.getKeyStroke(Character.valueOf('D'), InputEvent.CTRL_DOWN_MASK));
        visDB.addActionListener((ActionEvent e) -> {
            JPAModelerUtil.openDBViewer(this.getModelerFile(), this.getBaseElementSpec());
        });
        
        JMenuItem openPUXML = new JMenuItem(Bundle.PERSISTENCE_UNIT(), PERSISTENCE_UNIT);
        openPUXML.addActionListener((ActionEvent e) -> {
             Project project = JPAModelerScene.this.getModelerFile().getProject();
            try {
                PUDataObject pud = ProviderUtil.getPUDataObject(project);
                org.netbeans.modules.openfile.OpenFile.open(pud.getPrimaryFile(), -1);
            } catch (InvalidPersistenceXmlException ex) {
                JPAModelerScene.this.getModelerFile().handleException(ex);
            }
           
        });
        
       JMenuItem openJPQLPanel = new JMenuItem(Bundle.RYN_JPQL());//,ImageUtilities.loadImage(JPQL_ICON_PATH, true));
        openJPQLPanel.addActionListener((ActionEvent e) -> {
               new JPQLExternalEditorController().init(JPAModelerScene.this.getModelerFile());
        });

        JMenu shareModeler = new JMenu(Bundle.SHARE());
        shareModeler.setIcon(SOCIAL_NETWORK_SHARING);
        shareModeler.add(TwitterSocialNetwork.getInstance().getComponent());
        shareModeler.add(LinkedInSocialNetwork.getInstance().getComponent());

        menuList.add(0, generateCode);
        menuList.add(1, visDB);
        menuList.add(2, openPUXML);
        menuList.add(3, openJPQLPanel);
        menuList.add(4, null);
        menuList.add(5, manageVisibility);
        menuList.add(6, shareModeler);
        menuList.add(7, EnhancementRequestHandler.getInstance().getComponent());
        
        return menuList;
    }

    public static void fireEntityVisibilityAction(ModelerFile file) {
        ClassWidgetVisibilityController dialog = new ClassWidgetVisibilityController((EntityMappings) file.getDefinitionElement());
        dialog.setVisible(true);
        if (dialog.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {
            file.getModelerPanelTopComponent().changePersistenceState(false);
            file.save();
            int option = JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), "Are you want to reload diagram now ?", "Reload Diagram", JOptionPane.YES_NO_OPTION);
            if (option == javax.swing.JOptionPane.OK_OPTION) {
                file.getModelerPanelTopComponent().close();
                JPAFileActionListener fileListener = new JPAFileActionListener((JPAFileDataObject) file.getModelerFileDataObject());
                fileListener.actionPerformed(null);
            }
        }
    }

    public String getNextClassName(String className) {
        int index = 0;
        if (className == null || className.trim().isEmpty()) {
            className = "class";
        }
        className = Character.toUpperCase(className.charAt(0)) + (className.length() > 1 ? className.substring(1) : "");
        String nextClassName = className + ++index;
        EntityMappings entityMappings = this.getBaseElementSpec();

        boolean isExist = true;
        while (isExist) {
            if (entityMappings.isClassExist(nextClassName)) {
                isExist = true;
                nextClassName = className + ++index;
            } else {
                return nextClassName;
            }
        }
        return nextClassName;
    }

    @Override
    public IColorScheme getColorScheme() {
        EntityMappings entityMappings = this.getBaseElementSpec();
        if (PFactory.getNetBeans60Scheme().getSimpleName().equals(entityMappings.getTheme())) {
            return PFactory.getColorScheme(PFactory.getNetBeans60Scheme());
        } else if (PFactory.getMetroScheme().getSimpleName().equals(entityMappings.getTheme())) {
            return PFactory.getColorScheme(PFactory.getMetroScheme());
       } else if (PFactory.getDarkScheme().getSimpleName().equals(entityMappings.getTheme())) {
            return PFactory.getColorScheme(PFactory.getDarkScheme());
//        } else if (PFactory.getWoodScheme().getSimpleName().equals(entityMappings.getTheme())) {
//            return PFactory.getColorScheme(PFactory.getWoodScheme());
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
}
