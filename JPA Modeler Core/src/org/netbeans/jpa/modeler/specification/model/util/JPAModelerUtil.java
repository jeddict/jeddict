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
package org.netbeans.jpa.modeler.specification.model.util;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.db.modeler.manager.DBModelerRequestManager;
import org.netbeans.jpa.modeler._import.javaclass.JCREProcessor;
import org.netbeans.jpa.modeler.collaborate.issues.ExceptionUtils;
import org.netbeans.jpa.modeler.core.widget.CompositePKProperty;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.FlowNodeWidget;
import org.netbeans.jpa.modeler.core.widget.InheritenceStateType;
import static org.netbeans.jpa.modeler.core.widget.InheritenceStateType.LEAF;
import static org.netbeans.jpa.modeler.core.widget.InheritenceStateType.ROOT;
import static org.netbeans.jpa.modeler.core.widget.InheritenceStateType.SINGLETON;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.MappedSuperclassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.AttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.BasicCollectionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.EmbeddedIdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.IdAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.MultiValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.SingleValueEmbeddedAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.TransientAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.base.VersionAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.MTMRelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.MTORelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.OTMRelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.OTORelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.attribute.relation.RelationAttributeWidget;
import org.netbeans.jpa.modeler.core.widget.flow.EmbeddableFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.GeneralizationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.MultiValueEmbeddableFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.SingleValueEmbeddableFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.BMTMRelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.BMTORelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.BOTORelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.MTMRelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.MTORelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.OTMRelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.OTORelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.RelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.UMTMRelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.UMTORelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.UOTMRelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.flow.relation.UOTORelationFlowWidget;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Bidirectional;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Direction;
import org.netbeans.jpa.modeler.core.widget.relation.flow.direction.Unidirectional;
import org.netbeans.jpa.modeler.source.generator.task.SourceCodeGeneratorTask;
import org.netbeans.jpa.modeler.source.generator.ui.GenerateCodeDialog;
import org.netbeans.jpa.modeler.spec.Attributes;
import org.netbeans.jpa.modeler.spec.DefaultAttribute;
import org.netbeans.jpa.modeler.spec.DefaultClass;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.EmbeddableAttributes;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.design.Bounds;
import org.netbeans.jpa.modeler.spec.design.Diagram;
import org.netbeans.jpa.modeler.spec.design.DiagramElement;
import org.netbeans.jpa.modeler.spec.design.Plane;
import org.netbeans.jpa.modeler.spec.design.Shape;
import org.netbeans.jpa.modeler.spec.extend.BaseAttributes;
import org.netbeans.jpa.modeler.spec.extend.CompositePrimaryKeyType;
import org.netbeans.jpa.modeler.spec.extend.CompositionAttribute;
import org.netbeans.jpa.modeler.spec.extend.FlowNode;
import org.netbeans.jpa.modeler.spec.extend.IPersistenceAttributes;
import org.netbeans.jpa.modeler.spec.extend.InheritenceHandler;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.PrimaryKeyContainer;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.specification.model.file.JPAFileDataObject;
import org.netbeans.jpa.modeler.specification.model.file.action.JPAFileActionListener;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.anchors.CustomRectangularAnchor;
import org.netbeans.modeler.border.ResizeBorder;
import org.netbeans.modeler.config.document.IModelerDocument;
import org.netbeans.modeler.config.document.ModelerDocumentFactory;
import org.netbeans.modeler.config.element.ElementConfigFactory;
import org.netbeans.modeler.config.palette.SubCategoryNodeConfig;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.core.exception.ModelerException;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.ComboBoxValue;
import org.netbeans.modeler.shape.ShapeDesign;
import org.netbeans.modeler.specification.annotaton.DiagramModel;
import org.netbeans.modeler.specification.model.ModelerDiagramSpecification;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.core.IFlowNode;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.specification.model.util.PModelerUtil;
import org.netbeans.modeler.validation.jaxb.ValidateJAXB;
import org.netbeans.modeler.widget.edge.EdgeWidget;
import org.netbeans.modeler.widget.edge.IEdgeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.node.NodeWidget;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.node.vmd.PNodeWidget;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.netbeans.modeler.widget.pin.info.PinWidgetInfo;
import org.openide.awt.NotificationDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import static org.openide.util.NbBundle.getMessage;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;
import static org.openide.util.NbBundle.getMessage;

public class JPAModelerUtil implements PModelerUtil<JPAModelerScene> {

    public static String ENTITY_ICON_PATH;
    public static String ABSTRACT_ENTITY_ICON_PATH;
    public static String ID_ATTRIBUTE_ICON_PATH;
    public static String BASIC_ATTRIBUTE_ICON_PATH;
    public static String BASIC_COLLECTION_ATTRIBUTE_ICON_PATH;
    public static String EMBEDDED_ATTRIBUTE_ICON_PATH;
    public static String EMBEDDED_ID_ATTRIBUTE_ICON_PATH;
    public static String MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH;
    public static String SINGLE_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH;
    public static String TRANSIENT_ATTRIBUTE_ICON_PATH;
    public static String VERSION_ATTRIBUTE_ICON_PATH;
    public static String MULTIVALUE_EMBEDDED_ATTRIBUTE_ICON_PATH;
    public static String UMTM_ATTRIBUTE_ICON_PATH;
    public static String BMTM_ATTRIBUTE_ICON_PATH;
    public static String UMTO_ATTRIBUTE_ICON_PATH;
    public static String BMTO_ATTRIBUTE_ICON_PATH;
    public static String PK_UMTO_ATTRIBUTE_ICON_PATH;
    public static String PK_BMTO_ATTRIBUTE_ICON_PATH;
    public static String UOTM_ATTRIBUTE_ICON_PATH;
    public static String BOTM_ATTRIBUTE_ICON_PATH;
    public static String UOTO_ATTRIBUTE_ICON_PATH;
    public static String BOTO_ATTRIBUTE_ICON_PATH;
    public static String PK_UOTO_ATTRIBUTE_ICON_PATH;
    public static String PK_BOTO_ATTRIBUTE_ICON_PATH;
    public static String GENERALIZATION_ICON_PATH;
    public static Image ID_ATTRIBUTE;
    public static Image BASIC_ATTRIBUTE;
    public static Image BASIC_COLLECTION_ATTRIBUTE;
    public static Image EMBEDDED_ATTRIBUTE;
    public static Image EMBEDDED_ID_ATTRIBUTE;
    public static Image MULTI_VALUE_EMBEDDED_ATTRIBUTE;
    public static Image SINGLE_VALUE_EMBEDDED_ATTRIBUTE;
    public static Image TRANSIENT_ATTRIBUTE;
    public static Image VERSION_ATTRIBUTE;
    public static Image MULTIVALUE_EMBEDDED_ATTRIBUTE;
    public static Image UMTM_ATTRIBUTE;
    public static Image BMTM_ATTRIBUTE;
    public static Image UMTO_ATTRIBUTE;
    public static Image BMTO_ATTRIBUTE;
    public static Image PK_UMTO_ATTRIBUTE;
    public static Image PK_BMTO_ATTRIBUTE;
    public static Image UOTM_ATTRIBUTE;
    public static Image BOTM_ATTRIBUTE;
    public static Image UOTO_ATTRIBUTE;
    public static Image BOTO_ATTRIBUTE;
    public static Image PK_UOTO_ATTRIBUTE;
    public static Image PK_BOTO_ATTRIBUTE;
    public static Image GENERALIZATION;
    public static Image OTOR_SOURCE_ANCHOR_SHAPE;
    public static Image OTOR_TARGET_ANCHOR_SHAPE;
    public static Image OTMR_SOURCE_ANCHOR_SHAPE;
    public static Image OTMR_TARGET_ANCHOR_SHAPE;
    public static Image MTOR_SOURCE_ANCHOR_SHAPE;
    public static Image MTOR_TARGET_ANCHOR_SHAPE;
    public static Image MTMR_SOURCE_ANCHOR_SHAPE;
    public static Image MTMR_TARGET_ANCHOR_SHAPE;
    public static Image ABSTRACT_ENTITY;
    public static Image ENTITY;

    public static Icon GENERATE_SRC;
    public static Icon ENTITY_VISIBILITY;
    public static Icon SOCIAL_NETWORK_SHARING;
    public static Icon VIEW_DB;
    public static Icon MICRO_DB;
    public static Icon NANO_DB;

    private static JAXBContext MODELER_CONTEXT;
    public static Unmarshaller MODELER_UNMARSHALLER;
    public static Marshaller MODELER_MARSHALLER;
//    private final static InputOutput IO;

    static {

        try {
            MODELER_CONTEXT = JAXBContext.newInstance(new Class<?>[]{EntityMappings.class}); // unmarshaller will be always init before marshaller
        } catch (JAXBException ex) {
            ExceptionUtils.printStackTrace(ex);
        }

        ClassLoader cl = JPAModelerUtil.class.getClassLoader();//Eager Initialization
        GENERATE_SRC = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/popup/generate-src.png"));
        ENTITY_VISIBILITY = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/popup/entity-visibility.png"));
        VIEW_DB = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/popup/db.png"));
        MICRO_DB = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/popup/micro-db.png"));
        NANO_DB = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/popup/nano-db.png"));
        SOCIAL_NETWORK_SHARING = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/popup/share.png"));

//        IO = IOProvider.getDefault().getIO("JPA Modeler", false);
    }

    @Override
    public void init() {
        long st = new Date().getTime();
        if (ENTITY_ICON_PATH == null) {
            ENTITY_ICON_PATH = "org/netbeans/jpa/modeler/resource/element/java/ENTITY.png";
            ABSTRACT_ENTITY_ICON_PATH = "org/netbeans/jpa/modeler/resource/element/java/ABSTRACT_ENTITY.png";
            ID_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/id-attribute.png";
            BASIC_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/basic-attribute.png";
            BASIC_COLLECTION_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/basic-collection-attribute.png";
            EMBEDDED_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/embedded-attribute.gif";
            EMBEDDED_ID_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/embedded-id-attribute.png";
            MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/multi-value-embedded.gif";
            SINGLE_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/single-value-embedded.gif";
            TRANSIENT_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/transient-attribute.png";
            VERSION_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/version-attribute.png";
            MULTIVALUE_EMBEDDED_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/multi-value-embedded.gif";
            UMTM_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/umtm-attribute.png";
            BMTM_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/bmtm-attribute.png";
            UMTO_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/umto-attribute.png";
            BMTO_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/bmto-attribute.png";
            PK_UMTO_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/pk-umto-attribute.png";
            PK_BMTO_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/pk-bmto-attribute.png";
            UOTM_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/uotm-attribute.png";
            BOTM_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/botm-attribute.png";
            UOTO_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/uoto-attribute.png";
            BOTO_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/boto-attribute.png";
            PK_UOTO_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/pk-uoto-attribute.png";
            PK_BOTO_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/pk-boto-attribute.png";
            GENERALIZATION_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/generalization.png";

            ClassLoader cl = JPAModelerUtil.class.getClassLoader();

            UMTM_ATTRIBUTE = new ImageIcon(cl.getResource(UMTM_ATTRIBUTE_ICON_PATH)).getImage();
            BMTM_ATTRIBUTE = new ImageIcon(cl.getResource(BMTM_ATTRIBUTE_ICON_PATH)).getImage();
            UMTO_ATTRIBUTE = new ImageIcon(cl.getResource(UMTO_ATTRIBUTE_ICON_PATH)).getImage();
            BMTO_ATTRIBUTE = new ImageIcon(cl.getResource(BMTO_ATTRIBUTE_ICON_PATH)).getImage();
            PK_UMTO_ATTRIBUTE = new ImageIcon(cl.getResource(PK_UMTO_ATTRIBUTE_ICON_PATH)).getImage();
            PK_BMTO_ATTRIBUTE = new ImageIcon(cl.getResource(PK_BMTO_ATTRIBUTE_ICON_PATH)).getImage();
            UOTM_ATTRIBUTE = new ImageIcon(cl.getResource(UOTM_ATTRIBUTE_ICON_PATH)).getImage();
            BOTM_ATTRIBUTE = new ImageIcon(cl.getResource(BOTM_ATTRIBUTE_ICON_PATH)).getImage();
            UOTO_ATTRIBUTE = new ImageIcon(cl.getResource(UOTO_ATTRIBUTE_ICON_PATH)).getImage();
            BOTO_ATTRIBUTE = new ImageIcon(cl.getResource(BOTO_ATTRIBUTE_ICON_PATH)).getImage();
            PK_UOTO_ATTRIBUTE = new ImageIcon(cl.getResource(PK_UOTO_ATTRIBUTE_ICON_PATH)).getImage();
            PK_BOTO_ATTRIBUTE = new ImageIcon(cl.getResource(PK_BOTO_ATTRIBUTE_ICON_PATH)).getImage();
            GENERALIZATION = new ImageIcon(cl.getResource(GENERALIZATION_ICON_PATH)).getImage();
            OTOR_SOURCE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/one-to-one.gif")).getImage();
            OTOR_TARGET_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/one-to-one-arrow.png")).getImage();
            OTMR_SOURCE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/one-to-one.gif")).getImage();
            OTMR_TARGET_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/one-to-many-arrow.png")).getImage();
            MTOR_SOURCE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/many-to-one.gif")).getImage();
            MTOR_TARGET_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/one-to-one-arrow.png")).getImage();
            MTMR_SOURCE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/many-to-many.gif")).getImage();
            MTMR_TARGET_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/many-to-many-arrow.png")).getImage();

            ABSTRACT_ENTITY = new ImageIcon(cl.getResource(JPAModelerUtil.ABSTRACT_ENTITY_ICON_PATH)).getImage();
            ENTITY = new ImageIcon(cl.getResource(JPAModelerUtil.ENTITY_ICON_PATH)).getImage();
            ID_ATTRIBUTE = new ImageIcon(cl.getResource(ID_ATTRIBUTE_ICON_PATH)).getImage();
            BASIC_ATTRIBUTE = new ImageIcon(cl.getResource(BASIC_ATTRIBUTE_ICON_PATH)).getImage();
            BASIC_COLLECTION_ATTRIBUTE = new ImageIcon(cl.getResource(BASIC_COLLECTION_ATTRIBUTE_ICON_PATH)).getImage();
            EMBEDDED_ATTRIBUTE = new ImageIcon(cl.getResource(EMBEDDED_ATTRIBUTE_ICON_PATH)).getImage();
            EMBEDDED_ID_ATTRIBUTE = new ImageIcon(cl.getResource(EMBEDDED_ID_ATTRIBUTE_ICON_PATH)).getImage();
            MULTI_VALUE_EMBEDDED_ATTRIBUTE = new ImageIcon(cl.getResource(MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH)).getImage();
            SINGLE_VALUE_EMBEDDED_ATTRIBUTE = new ImageIcon(cl.getResource(SINGLE_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH)).getImage();
            TRANSIENT_ATTRIBUTE = new ImageIcon(cl.getResource(TRANSIENT_ATTRIBUTE_ICON_PATH)).getImage();
            VERSION_ATTRIBUTE = new ImageIcon(cl.getResource(VERSION_ATTRIBUTE_ICON_PATH)).getImage();
            MULTIVALUE_EMBEDDED_ATTRIBUTE = new ImageIcon(cl.getResource(MULTIVALUE_EMBEDDED_ATTRIBUTE_ICON_PATH)).getImage();

        }
    }

    public static EntityMappings getEntityMapping(File file) throws JAXBException {
        EntityMappings definition_Load = null;
        if (MODELER_UNMARSHALLER == null) {
            MODELER_UNMARSHALLER = MODELER_CONTEXT.createUnmarshaller();
//            MODELER_UNMARSHALLER.setEventHandler(new ValidateJAXB());
        }
        definition_Load = MODELER_UNMARSHALLER.unmarshal(new StreamSource(file), EntityMappings.class).getValue();
        return definition_Load;
    }

    @Override
    public void loadModelerFile(final ModelerFile file) throws ProcessInterruptedException {

        try {
            JPAModelerScene scene = (JPAModelerScene) file.getModelerScene();
            File savedFile = file.getFile();
            EntityMappings entityMappings = getEntityMapping(savedFile);
            if (entityMappings == null) {
                ElementConfigFactory elementConfigFactory = file.getVendorSpecification().getElementConfigFactory();
                entityMappings = EntityMappings.getNewInstance(file.getCurrentVersion());
                elementConfigFactory.initializeObjectValue(entityMappings);
            } else {
                if (entityMappings.getVersion() < file.getCurrentVersion()) {
                    int reply = javax.swing.JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                            getMessage(JPAModelerUtil.class, "Notification.JCRE_SUGGESION.text", file.getCurrentVersion()),
                            getMessage(JPAModelerUtil.class, "Notification.JCRE_SUGGESION.title"), JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        file.getModelerPanelTopComponent().close();
                        JCREProcessor processor = Lookup.getDefault().lookup(JCREProcessor.class);
                        file.getModelerDiagramModel().setDefinitionElement(entityMappings);
                        processor.process(file);
                    } else {
                        entityMappings.setVersion(file.getCurrentVersion());
                        NotificationDisplayer.getDefault().notify(getMessage(JPAModelerUtil.class, "Notification.SVC_WARNING.title"),
                                ImageUtilities.image2Icon(file.getIcon()),
                                getMessage(JPAModelerUtil.class, "Notification.SVC_WARNING.text"), null,
                                NotificationDisplayer.Priority.HIGH, NotificationDisplayer.Category.INFO);
                    }
                }
            }

            Diagram diagram = entityMappings.getJPADiagram();
            ModelerDiagramSpecification modelerDiagram = file.getModelerDiagramModel();
            modelerDiagram.setDefinitionElement(entityMappings);
            scene.setBaseElementSpec(entityMappings);
            long st = new Date().getTime();

            scene.startSceneGeneration();
            entityMappings.getEntity().stream().
                    forEach(node -> loadFlowNode(scene, (Widget) scene, node));
            entityMappings.getMappedSuperclass().stream().
                    forEach(node -> loadFlowNode(scene, (Widget) scene, node));
            entityMappings.getEmbeddable().stream().
                    forEach(node -> loadFlowNode(scene, (Widget) scene, node));
            System.out.println("EM PS Total time : " + (new Date().getTime() - st) + " sec");

            entityMappings.initJavaInheritenceMapping();
            loadFlowEdge(scene);
            diagram.getJPAPlane().getDiagramElement().stream().
                    forEach((diagramElement_Tmp) -> loadDiagram(scene, diagram, diagramElement_Tmp));

            if (entityMappings.isGenerated() || (entityMappings.getEntity().size() + entityMappings.getMappedSuperclass().size()
                    + entityMappings.getEmbeddable().size() != entityMappings.getJPADiagram().getJPAPlane().getDiagramElement().size())) {
                scene.autoLayout();
                entityMappings.setStatus(null);
            }

            scene.commitSceneGeneration();
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void loadFlowNode(JPAModelerScene scene, Widget parentWidget, IFlowNode flowElement) {
        IModelerDocument document = null;
        ModelerDocumentFactory modelerDocumentFactory = scene.getModelerFile().getVendorSpecification().getModelerDocumentFactory();
        if (flowElement instanceof FlowNode) {
            FlowNode flowNode = (FlowNode) flowElement;
            if (flowElement instanceof JavaClass) { //skip class creation in case of hidden visibility
                JavaClass _class = (JavaClass) flowElement;
                if (!_class.isVisibile()) {
                    return;
                }
            }
            try {
                document = modelerDocumentFactory.getModelerDocument(flowElement);
            } catch (ModelerException ex) {
                scene.getModelerFile().handleException(ex);
            }
            SubCategoryNodeConfig subCategoryNodeConfig = scene.getModelerFile().getVendorSpecification().getPaletteConfig().findSubCategoryNodeConfig(document);
            NodeWidgetInfo nodeWidgetInfo = new NodeWidgetInfo(flowElement.getId(), subCategoryNodeConfig, new Point(0, 0));
            nodeWidgetInfo.setName(flowElement.getName());
            nodeWidgetInfo.setExist(Boolean.TRUE);//to Load JPA
            nodeWidgetInfo.setBaseElementSpec(flowElement);//to Load JPA
            INodeWidget nodeWidget = scene.createNodeWidget(nodeWidgetInfo);
            if (flowElement.getName() != null) {
                nodeWidget.setLabel(flowElement.getName());
            }
            if (flowNode.isMinimized()) {
                ((PNodeWidget) nodeWidget).setMinimized(true);
            }
            if (flowElement instanceof ManagedClass) {
                ManagedClass _class = (ManagedClass) flowElement;
                PersistenceClassWidget entityWidget = (PersistenceClassWidget) nodeWidget;
                if (_class.getAttributes() != null) {
                    if (_class.getAttributes() instanceof IPersistenceAttributes) {
                        ((IPersistenceAttributes) _class.getAttributes()).getId().stream().
                                forEach((id) -> entityWidget.addNewIdAttribute(id.getName(), id));
                        EmbeddedId embeddedId = ((IPersistenceAttributes) _class.getAttributes()).getEmbeddedId();
                        if (embeddedId != null && embeddedId.isVisibile()) {
                            entityWidget.addNewEmbeddedIdAttribute(embeddedId.getName(), embeddedId);
                        }

                        ((IPersistenceAttributes) _class.getAttributes()).getVersion().stream().
                                forEach((version) -> entityWidget.addNewVersionAttribute(version.getName(), version));
                    }
                    _class.getAttributes().getBasic().stream().forEach((basic) -> {
                        entityWidget.addNewBasicAttribute(basic.getName(), basic);
                    });
                    _class.getAttributes().getTransient().stream().forEach((_transient) -> {
                        entityWidget.addNewTransientAttribute(_transient.getName(), _transient);
                    });

                    _class.getAttributes().getEmbedded().stream().filter(Embedded::isVisibile).forEach((embedded) -> {
                        entityWidget.addNewSingleValueEmbeddedAttribute(embedded.getName(), embedded);
                    });
                    _class.getAttributes().getElementCollection().stream().forEach((elementCollection) -> {
                        if (elementCollection.getConnectedClass() != null) {
                            if (elementCollection.isVisibile()) {
                                entityWidget.addNewMultiValueEmbeddedAttribute(elementCollection.getName(), elementCollection);
                            }
                        } else {
                            entityWidget.addNewBasicCollectionAttribute(elementCollection.getName(), elementCollection);
                        }
                    });
                    _class.getAttributes().getOneToOne().stream().filter(OneToOne::isVisibile).forEach((oneToOne) -> {
                        OTORelationAttributeWidget relationAttributeWidget = entityWidget.addNewOneToOneRelationAttribute(oneToOne.getName(), oneToOne);
//                        if (oneToOne.getMappedBy() == null) {
//                            oneToOne.setOwner(true);
//                        }
                    });
                    _class.getAttributes().getOneToMany().stream().filter(OneToMany::isVisibile).forEach((oneToMany) -> {
                        OTMRelationAttributeWidget relationAttributeWidget = entityWidget.addNewOneToManyRelationAttribute(oneToMany.getName(), oneToMany);
//                        if (oneToMany.getMappedBy() == null) {
//                            oneToMany.setOwner(true);
//                        }
                    });
                    _class.getAttributes().getManyToOne().stream().filter(ManyToOne::isVisibile).forEach((manyToOne) -> {
                        MTORelationAttributeWidget relationAttributeWidget = entityWidget.addNewManyToOneRelationAttribute(manyToOne.getName(), manyToOne);
//                        manyToOne.setOwner(true);//always
                    });
                    _class.getAttributes().getManyToMany().stream().filter(ManyToMany::isVisibile).forEach((manyToMany) -> {
                        MTMRelationAttributeWidget relationAttributeWidget = entityWidget.addNewManyToManyRelationAttribute(manyToMany.getName(), manyToMany);
//                        if (manyToMany.getMappedBy() == null) {
//                            manyToMany.setOwner(true);
//                        }
                    });
                    entityWidget.sortAttributes();
                }

            }
//            nodeWidget.i
            //clear incomming & outgoing it will added on sequenceflow auto connection
//            ((FlowNode) flowElement).getIncoming().clear();
//            ((FlowNode) flowElement).getOutgoing().clear();

        }
    }

    private void loadFlowEdge(JPAModelerScene scene) {

        scene.getBaseElements().stream().filter((baseElementWidget) -> (baseElementWidget instanceof JavaClassWidget)).forEach((baseElementWidget) -> {
            JavaClassWidget javaClassWidget = (JavaClassWidget) baseElementWidget;
            loadGeneralization(scene, javaClassWidget);
            if (baseElementWidget instanceof PersistenceClassWidget) {
                PersistenceClassWidget<? extends ManagedClass> sourcePersistenceClassWidget = (PersistenceClassWidget) baseElementWidget;
                for (SingleValueEmbeddedAttributeWidget embeddedAttributeWidget : sourcePersistenceClassWidget.getSingleValueEmbeddedAttributeWidgets()) {
                    loadEmbeddedEdge(scene, "SINGLE_EMBEDDABLE_RELATION", sourcePersistenceClassWidget, embeddedAttributeWidget);
                }
                for (MultiValueEmbeddedAttributeWidget embeddedAttributeWidget : sourcePersistenceClassWidget.getMultiValueEmbeddedAttributeWidgets()) {
                    loadEmbeddedEdge(scene, "MULTI_EMBEDDABLE_RELATION", sourcePersistenceClassWidget, embeddedAttributeWidget);
                }

                for (OTORelationAttributeWidget sourceRelationAttributeWidget : sourcePersistenceClassWidget.getOneToOneRelationAttributeWidgets()) {
                    loadRelationEdge(scene, "OTO_RELATION", sourcePersistenceClassWidget, sourceRelationAttributeWidget, OTORelationAttributeWidget.class);
                }
                for (OTMRelationAttributeWidget sourceRelationAttributeWidget : sourcePersistenceClassWidget.getOneToManyRelationAttributeWidgets()) {
                    loadRelationEdge(scene, "OTM_RELATION", sourcePersistenceClassWidget, sourceRelationAttributeWidget, OTMRelationAttributeWidget.class);
                }
                for (MTORelationAttributeWidget sourceRelationAttributeWidget : sourcePersistenceClassWidget.getManyToOneRelationAttributeWidgets()) {
                    loadRelationEdge(scene, "MTO_RELATION", sourcePersistenceClassWidget, sourceRelationAttributeWidget, OTMRelationAttributeWidget.class);
                }
                for (MTMRelationAttributeWidget sourceRelationAttributeWidget : sourcePersistenceClassWidget.getManyToManyRelationAttributeWidgets()) {
                    loadRelationEdge(scene, "MTM_RELATION", sourcePersistenceClassWidget, sourceRelationAttributeWidget, MTMRelationAttributeWidget.class);
                }
            }
        });
    }

    private void loadEmbeddedEdge(JPAModelerScene scene, String contextToolId, PersistenceClassWidget sourcePersistenceClassWidget, EmbeddedAttributeWidget sourceAttributeWidget) {
        CompositionAttribute sourceEmbeddedAttribute = (CompositionAttribute) sourceAttributeWidget.getBaseElementSpec();
        EmbeddableWidget targetEntityWidget = (EmbeddableWidget) scene.getBaseElement(sourceEmbeddedAttribute.getConnectedClass().getId());
        EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo();
        edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
        edgeInfo.setSource(sourcePersistenceClassWidget.getNodeWidgetInfo().getId());
        edgeInfo.setTarget(targetEntityWidget.getNodeWidgetInfo().getId());
        edgeInfo.setType(NBModelerUtil.getEdgeType(sourcePersistenceClassWidget, targetEntityWidget, contextToolId));
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);
        scene.setEdgeWidgetSource(edgeInfo, getEdgeSourcePinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, sourceAttributeWidget));
        scene.setEdgeWidgetTarget(edgeInfo, getEdgeTargetPinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, null));

    }

    private void loadRelationEdge(JPAModelerScene scene, String abstractTool, PersistenceClassWidget sourcePersistenceClassWidget, RelationAttributeWidget sourceRelationAttributeWidget, Class<? extends RelationAttributeWidget>... targetRelationAttributeWidgetClass) {

        RelationAttribute sourceRelationAttribute = (RelationAttribute) sourceRelationAttributeWidget.getBaseElementSpec();

        if (!sourceRelationAttribute.isOwner()) {  //Only Owner will draw edge because in any case uni/bi owner is always exist
            return;
        }

        EntityWidget targetEntityWidget = (EntityWidget) scene.getBaseElement(sourceRelationAttribute.getConnectedEntity().getId());
        RelationAttributeWidget targetRelationAttributeWidget = null;

        EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo();
        edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
        edgeInfo.setSource(sourcePersistenceClassWidget.getNodeWidgetInfo().getId());
        edgeInfo.setTarget(targetEntityWidget.getNodeWidgetInfo().getId());
        String contextToolId;
        if (sourceRelationAttribute.getConnectedAttribute() != null) {
            targetRelationAttributeWidget = targetEntityWidget.findRelationAttributeWidget(sourceRelationAttribute.getConnectedAttribute().getId(), targetRelationAttributeWidgetClass);
            contextToolId = "B" + abstractTool;//OTM_RELATION";
        } else {
            contextToolId = "U" + abstractTool;
        }
        edgeInfo.setType(NBModelerUtil.getEdgeType(sourcePersistenceClassWidget, targetEntityWidget, contextToolId));
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);

        scene.setEdgeWidgetSource(edgeInfo, getEdgeSourcePinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, sourceRelationAttributeWidget));
        scene.setEdgeWidgetTarget(edgeInfo, getEdgeTargetPinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, targetRelationAttributeWidget));

    }

    private void loadGeneralization(JPAModelerScene scene, JavaClassWidget javaClassWidget) {

        JavaClass javaClass = (JavaClass) javaClassWidget.getBaseElementSpec();
        if (javaClass.getSuperclass() != null) {
            JavaClassWidget subJavaClassWidget = javaClassWidget;
            JavaClassWidget superJavaClassWidget = (JavaClassWidget) scene.getBaseElement(javaClass.getSuperclass().getId());
            EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo();
            edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
            edgeInfo.setSource(subJavaClassWidget.getNodeWidgetInfo().getId());
            edgeInfo.setTarget(superJavaClassWidget.getNodeWidgetInfo().getId());

            edgeInfo.setType(NBModelerUtil.getEdgeType(subJavaClassWidget, superJavaClassWidget, "GENERALIZATION"));
            IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);

            scene.setEdgeWidgetSource(edgeInfo, getEdgeSourcePinWidget(subJavaClassWidget, superJavaClassWidget, edgeWidget, null));
            scene.setEdgeWidgetTarget(edgeInfo, getEdgeTargetPinWidget(subJavaClassWidget, superJavaClassWidget, edgeWidget, null));

        }

    }

    private void loadDiagram(JPAModelerScene scene, Diagram diagram, DiagramElement diagramElement) {
        if (diagramElement instanceof Shape) {
            Shape shape = (Shape) diagramElement;
            Bounds bounds = shape.getBounds();
            Widget widget = (Widget) scene.getBaseElement(shape.getElementRef());
            if (widget != null) {
                if (widget instanceof INodeWidget) { //reverse ref
                    INodeWidget nodeWidget = (INodeWidget) widget;
                    Point location = new Point((int) bounds.getX(), (int) bounds.getY());
                    nodeWidget.setPreferredLocation(location);
//                    nodeWidget.setActiveStatus(true);
                } else {
                    throw new InvalidElmentException("Invalid JPA Element : " + widget);
                }
            }
        }
    }

    @Override
    public void saveModelerFile(ModelerFile file) {
        JPAModelerScene scene = (JPAModelerScene) file.getModelerScene();
        EntityMappings entityMappings = (EntityMappings) file.getDefinitionElement();
        updateJPADiagram(scene, entityMappings.getJPADiagram());
        entityMappings.getDefaultClass().clear();

        for (IBaseElementWidget baseElementWidget : scene.getBaseElements()) {
            if (baseElementWidget instanceof FlowNodeWidget) {
                FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                FlowNode flowNode = (FlowNode) flowNodeWidget.getBaseElementSpec();
                flowNode.setMinimized(flowNodeWidget.isMinimized());
                if (baseElementWidget instanceof JavaClassWidget) {
                    if (baseElementWidget instanceof PersistenceClassWidget) {
                        PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget = (PersistenceClassWidget) baseElementWidget;
                        if (persistenceClassWidget instanceof EntityWidget) {
                            EntityWidget entityWidget = (EntityWidget) persistenceClassWidget;
                            InheritenceHandler classSpec = (InheritenceHandler) entityWidget.getBaseElementSpec();
                            InheritenceStateType inheritenceState = entityWidget.getInheritenceState();
                            switch (inheritenceState) {
                                case LEAF:
                                case SINGLETON:
                                    classSpec.setDiscriminatorColumn(null);
                                    classSpec.setInheritance(null);
                                    break;
                                case ROOT:
                                    classSpec.setDiscriminatorValue(null);
                                    break;
                            }
                        }

                    }
                }
            }

        }

        executionCompositePrimaryKeyEvaluation(scene.getBaseElements(), entityMappings);

        saveFile(entityMappings, file.getFile());
    }

    private void executionCompositePrimaryKeyEvaluation(List<IBaseElementWidget> baseElements, EntityMappings entityMappings) {
        List<IBaseElementWidget> baseElementWidgetPending = new ArrayList<>();
        for (IBaseElementWidget baseElementWidget : baseElements) {
            if (baseElementWidget instanceof PersistenceClassWidget) {
                PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget = (PersistenceClassWidget) baseElementWidget;
                if (!manageCompositePrimaryKey(persistenceClassWidget, entityMappings)) {
                    baseElementWidgetPending.add(persistenceClassWidget);
                }
            }
        }
        if (!baseElementWidgetPending.isEmpty()) {
            executionCompositePrimaryKeyEvaluation(baseElementWidgetPending, entityMappings);
        }
    }

    private boolean manageCompositePrimaryKey(PersistenceClassWidget<? extends ManagedClass> persistenceClassWidget, EntityMappings entityMappings) {
        //Start : IDCLASS,EMBEDDEDID
        if (persistenceClassWidget.getBaseElementSpec() instanceof PrimaryKeyContainer) {
            PrimaryKeyContainer pkContainerSpec = (PrimaryKeyContainer) persistenceClassWidget.getBaseElementSpec();
            CompositePKProperty compositePKProperty = persistenceClassWidget.isCompositePKPropertyAllow();

            if (compositePKProperty == CompositePKProperty.NONE) {
                pkContainerSpec.clearCompositePrimaryKey();
            } else {

                if (pkContainerSpec.getCompositePrimaryKeyType() == null) {
                    pkContainerSpec.setCompositePrimaryKeyType(CompositePrimaryKeyType.IDCLASS);
                }
                    if (compositePKProperty == CompositePKProperty.AUTO_CLASS) {
                        RelationAttributeWidget relationAttributeWidget = persistenceClassWidget.getDerivedRelationAttributeWidgets().get(0);
                        RelationAttribute relationAttribute = (RelationAttribute) relationAttributeWidget.getBaseElementSpec();
                        IFlowElementWidget targetElementWidget = (EntityWidget) relationAttributeWidget.getRelationFlowWidget().getTargetWidget();
                        EntityWidget targetEntityWidget = null;
                        if (targetElementWidget instanceof EntityWidget) {
                            targetEntityWidget = (EntityWidget) targetElementWidget;
                        } else {
                            if (targetElementWidget instanceof RelationAttributeWidget) {
                                RelationAttributeWidget targetRelationAttributeWidget = (RelationAttributeWidget) targetElementWidget;
                                targetEntityWidget = (EntityWidget) targetRelationAttributeWidget.getClassWidget();//target can be only Entity
                            }
                        }
                        Entity targetPKConatinerSpec = targetEntityWidget.getBaseElementSpec();
                        if (StringUtils.isBlank(targetPKConatinerSpec.getCompositePrimaryKeyClass())) {
                            return false;//send to next execution, its parents are required to evalaute first
                        }
                        if (pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID
                                && targetPKConatinerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS) {
                            // when Enity E1 class use IdClass IC1 and
                            //another Enity E2 class use EmbeddedId is also IC1
                            //then register IdClass name here to append @Embeddable annotation
                            DefaultClass _class = entityMappings.addDefaultClass(targetPKConatinerSpec.getCompositePrimaryKeyClass());
                            _class.setEmbeddable(true);
//                                            _class.setAttributes(null);//attribute will be added in parent Entity DefaultClass creation process
                            if (relationAttribute instanceof OneToOne) {
                                ((OneToOne) relationAttribute).setMapsId("");
                            } else {
                                if (relationAttribute instanceof ManyToOne) {
                                    ((ManyToOne) relationAttribute).setMapsId("");
                                }
                            }
                        } else {
                            if (pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS
                                    && targetPKConatinerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID) {
                                if (relationAttribute instanceof OneToOne) {
                                    ((OneToOne) relationAttribute).setMapsId(null);
                                } else {
                                    if (relationAttribute instanceof ManyToOne) {
                                        ((ManyToOne) relationAttribute).setMapsId(null);
                                    }
                                }
                            }
                        }
                        //set derived entity IdClass/EmbeddedId class type same as of parent entity IdClass/EmbeddedId class type

                        pkContainerSpec.setCompositePrimaryKeyClass(targetPKConatinerSpec.getCompositePrimaryKeyClass());
                    } else { //not CompositePKProperty.NONE
                        List<IdAttributeWidget> idAttributeWidgets = null;
                        DefaultClass _class = entityMappings.addDefaultClass(pkContainerSpec.getCompositePrimaryKeyClass());
                        if (pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID) {
                            idAttributeWidgets = persistenceClassWidget.getIdAttributeWidgets();
                            _class.setEmbeddable(true);
                        } else {
                            if (pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS) {
                                idAttributeWidgets = persistenceClassWidget.getAllIdAttributeWidgets();
                            }
                        }

                        for (IdAttributeWidget idAttributeWidget : idAttributeWidgets) {
                            Id idSpec = idAttributeWidget.getBaseElementSpec();
                            DefaultAttribute attribute = new DefaultAttribute();
                            attribute.setAttributeType(idSpec.getAttributeType());
                            attribute.setName(idSpec.getName());
                            _class.addAttribute(attribute);
                        }
                        for (RelationAttributeWidget relationAttributeWidget : persistenceClassWidget.getDerivedRelationAttributeWidgets()) {
                            RelationAttribute relationAttributeSpec = (RelationAttribute) relationAttributeWidget.getBaseElementSpec();
                            Entity targetEntitySpec;
                            IFlowElementWidget targetElementWidget = relationAttributeWidget.getRelationFlowWidget().getTargetWidget();
                            EntityWidget targetEntityWidget = null;
                            if (targetElementWidget instanceof EntityWidget) {
                                targetEntityWidget = (EntityWidget) targetElementWidget;
                            } else {
                                if (targetElementWidget instanceof RelationAttributeWidget) {
                                    RelationAttributeWidget targetRelationAttributeWidget = (RelationAttributeWidget) targetElementWidget;
                                    targetEntityWidget = (EntityWidget) targetRelationAttributeWidget.getClassWidget();//target can be only Entity
                                }
                            }
                            targetEntitySpec = targetEntityWidget.getBaseElementSpec();
                            List<IdAttributeWidget> targetIdAttributeWidgets = targetEntityWidget.getAllIdAttributeWidgets();
                            DefaultAttribute attribute = new DefaultAttribute();
                            if (targetIdAttributeWidgets.size() == 1) {
                                Id idSpec = targetIdAttributeWidgets.get(0).getBaseElementSpec();
                                attribute.setAttributeType(idSpec.getAttributeType());
                                attribute.setName(relationAttributeSpec.getName());// matches name of @Id Relation attribute
                            } else {
                                attribute.setAttributeType(targetEntitySpec.getCompositePrimaryKeyClass());
                                attribute.setName(relationAttributeSpec.getName());// matches name of @Id Relation attribute

                                if (null != targetEntitySpec.getCompositePrimaryKeyType()) {
                                    switch (targetEntitySpec.getCompositePrimaryKeyType()) {
                                        case IDCLASS:
                                            break;
                                        case EMBEDDEDID:
                                            break;
                                        default:
                                            throw new UnsupportedOperationException("Not Supported Currently");
                                    }
                                }
                            }
                            _class.addAttribute(attribute);
                            //Start : if dependent class is Embedded that add @MapsId to Derived PK
                            if (pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS) {
                                if (relationAttributeSpec instanceof OneToOne) {
                                    ((OneToOne) relationAttributeSpec).setMapsId(null);
                                } else {
                                    if (relationAttributeSpec instanceof ManyToOne) {
                                        ((ManyToOne) relationAttributeSpec).setMapsId(null);
                                    }
                                }
                            } else {
                                if (pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID) {
                                    if (relationAttributeSpec instanceof OneToOne) {
                                        ((OneToOne) relationAttributeSpec).setMapsId(attribute.getName());
                                    } else {
                                        if (relationAttributeSpec instanceof ManyToOne) {
                                            ((ManyToOne) relationAttributeSpec).setMapsId(attribute.getName());
                                        }
                                    }
                                }
                            }
                            //End : if dependent class is Embedded that add @MapsId to Derived PK

                        }
                    }

            }
        }
        return true;
//End : IDCLASS,EMBEDDEDID
    }

    public static void saveFile(EntityMappings entityMappings, File file) {
        try {
            if (MODELER_MARSHALLER == null) {
                MODELER_MARSHALLER = MODELER_CONTEXT.createMarshaller();
                MODELER_MARSHALLER.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                MODELER_MARSHALLER.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://java.sun.com/xml/ns/persistence/orm orm_2_1.xsd");
                MODELER_MARSHALLER.setEventHandler(new ValidateJAXB());
            }
            MODELER_MARSHALLER.marshal(entityMappings, file);
        } catch (JAXBException ex) {
            ExceptionUtils.printStackTrace(ex);
        }
    }

    public static void createNewModelerFile(EntityMappings entityMappingsSpec, FileObject parentFileObject, String fileName, boolean autoOpen) {
        createNewModelerFile(entityMappingsSpec, parentFileObject, fileName, true, autoOpen);
    }

    /**
     *
     * @param softWrite if true and file already exist with fileName then it
     * will create another file with next serial number
     * @param autoOpen if true then open modeler file in netbeans
     */
    public static void createNewModelerFile(EntityMappings entityMappingsSpec, FileObject parentFileObject, String fileName, boolean softWrite, boolean autoOpen) {
        File jpaFile = null;
        try {
            if (softWrite) {
                jpaFile = new File(parentFileObject.getPath() + File.separator + getFileName(fileName, null, parentFileObject) + ".jpa");
            } else {
                jpaFile = new File(parentFileObject.getPath() + File.separator + fileName + ".jpa");
            }
            if (!jpaFile.exists()) {
                jpaFile.createNewFile();
            }
            saveFile(entityMappingsSpec, jpaFile);
        } catch (IOException ex) {
            ExceptionUtils.printStackTrace(ex);
        }
        if (autoOpen) {
            FileObject jpaFileObject = FileUtil.toFileObject(jpaFile);
            try {
                JPAFileActionListener actionListener = new JPAFileActionListener((JPAFileDataObject) DataObject.find(jpaFileObject));
                actionListener.actionPerformed(null);
            } catch (DataObjectNotFoundException ex) {
                ExceptionUtils.printStackTrace(ex);
            }
        }

    }

    private static String getFileName(String fileName, Integer index, FileObject parentFileObject) {
        File jpaFile;
        if (index == null) {
            jpaFile = new File(parentFileObject.getPath() + File.separator + fileName + ".jpa");
        } else {
            jpaFile = new File(parentFileObject.getPath() + File.separator + fileName + index + ".jpa");
        }
        if (jpaFile.exists()) {
            if (index == null) {
                index = 0;
            }
            return getFileName(fileName, ++index, parentFileObject);
        } else {
            if (index == null) {
                return fileName;
            } else {
                return fileName + index;
            }
        }
    }

    @Override
    public INodeWidget updateNodeWidgetDesign(ShapeDesign shapeDesign, INodeWidget inodeWidget) {
        PNodeWidget nodeWidget = (PNodeWidget) inodeWidget;
        //ELEMENT_UPGRADE
//        if (shapeDesign != null) {
//            if (shapeDesign.getOuterShapeContext() != null) {
//                if (shapeDesign.getOuterShapeContext().getBackground() != null) {
//                    nodeWidget.setOuterElementStartBackgroundColor(shapeDesign.getOuterShapeContext().getBackground().getStartColor());
//                    nodeWidget.setOuterElementEndBackgroundColor(shapeDesign.getOuterShapeContext().getBackground().getEndColor());
//                }
//                if (shapeDesign.getOuterShapeContext().getBorder() != null) {
//                    nodeWidget.setOuterElementBorderColor(shapeDesign.getOuterShapeContext().getBorder().getColor());
//                    nodeWidget.setOuterElementBorderWidth(shapeDesign.getOuterShapeContext().getBorder().getWidth());
//                }
//            }
//            if (shapeDesign.getInnerShapeContext() != null) {
//                if (shapeDesign.getInnerShapeContext().getBackground() != null) {
//                    nodeWidget.setInnerElementStartBackgroundColor(shapeDesign.getInnerShapeContext().getBackground().getStartColor());
//                    nodeWidget.setInnerElementEndBackgroundColor(shapeDesign.getInnerShapeContext().getBackground().getEndColor());
//                }
//                if (shapeDesign.getInnerShapeContext().getBorder() != null) {
//                    nodeWidget.setInnerElementBorderColor(shapeDesign.getInnerShapeContext().getBorder().getColor());
//                    nodeWidget.setInnerElementBorderWidth(shapeDesign.getInnerShapeContext().getBorder().getWidth());
//                }
//            }
//        }

        return (INodeWidget) nodeWidget;
    }

    public static void updateDiagramFlowElement(Plane plane, Widget widget) {
        //Diagram Model
        if (widget instanceof INodeWidget) { //reverse ref
            INodeWidget nodeWidget = (INodeWidget) widget;

            Rectangle rec = nodeWidget.getSceneViewBound();

            Shape shape = new Shape();
            shape.setBounds(new Bounds(rec));//(new Bounds(flowNodeWidget.getBounds()));
            shape.setElementRef(((IBaseElementWidget) nodeWidget).getId());
//            shape.setId(((IBaseElementWidget) nodeWidget).getId() + "_gui");
            plane.addDiagramElement(shape);

        } else {
            if (widget instanceof EdgeWidget) {

            } else {
                throw new InvalidElmentException("Invalid JPA Element");
            }
        }

    }

    public static void updateJPADiagram(JPAModelerScene scene, Diagram diagram) {
        Plane plane = diagram.getJPAPlane();
        plane.getDiagramElement().clear();
//        JPAModelerScene processScene = (JPAModelerScene)file.getModelerScene();
        scene.getBaseElements().stream().forEach((flowElementWidget) -> {
            updateDiagramFlowElement(plane, (Widget) flowElementWidget);
        });
    }

    /*---------------------------------Save File End---------------------------------*/
    @Override
    public Anchor getAnchor(INodeWidget inodeWidget) {
        INodeWidget nodeWidget = inodeWidget;
        Anchor sourceAnchor;
//        NodeWidgetInfo nodeWidgetInfo = nodeWidget.getNodeWidgetInfo();
        if (nodeWidget instanceof IFlowNodeWidget) {
            sourceAnchor = new CustomRectangularAnchor(nodeWidget, 0, true);
        } else {
            throw new InvalidElmentException("Invalid JPA Process Element : " + nodeWidget);
        }
        return sourceAnchor;
    }

    @Override
    public void transformNode(IFlowNodeWidget flowNodeWidget, IModelerDocument document) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IPinWidget attachPinWidget(JPAModelerScene scene, INodeWidget nodeWidget, PinWidgetInfo widgetInfo) {
        IPinWidget widget = null;
        if (widgetInfo.getDocumentId().equals(IdAttributeWidget.class.getSimpleName())) {
            widget = new IdAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else {
            if (widgetInfo.getDocumentId().equals(EmbeddedIdAttributeWidget.class.getSimpleName())) {
                widget = new EmbeddedIdAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
            } else {
                if (widgetInfo.getDocumentId().equals(BasicAttributeWidget.class.getSimpleName())) {
                    widget = new BasicAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
                } else {
                    if (widgetInfo.getDocumentId().equals(BasicCollectionAttributeWidget.class.getSimpleName())) {
                        widget = new BasicCollectionAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
                    } else {
                        if (widgetInfo.getDocumentId().equals(TransientAttributeWidget.class.getSimpleName())) {
                            widget = new TransientAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
                        } else {
                            if (widgetInfo.getDocumentId().equals(VersionAttributeWidget.class.getSimpleName())) {
                                widget = new VersionAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
                            } else {
                                if (widgetInfo.getDocumentId().equals(OTORelationAttributeWidget.class.getSimpleName())) {
                                    widget = new OTORelationAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
                                } else {
                                    if (widgetInfo.getDocumentId().equals(OTMRelationAttributeWidget.class.getSimpleName())) {
                                        widget = new OTMRelationAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
                                    } else {
                                        if (widgetInfo.getDocumentId().equals(MTORelationAttributeWidget.class.getSimpleName())) {
                                            widget = new MTORelationAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
                                        } else {
                                            if (widgetInfo.getDocumentId().equals(MTMRelationAttributeWidget.class.getSimpleName())) {
                                                widget = new MTMRelationAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
                                            } else {
                                                if (widgetInfo.getDocumentId().equals(SingleValueEmbeddedAttributeWidget.class.getSimpleName())) {
                                                    widget = new SingleValueEmbeddedAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
                                                } else {
                                                    if (widgetInfo.getDocumentId().equals(MultiValueEmbeddedAttributeWidget.class.getSimpleName())) {
                                                        widget = new MultiValueEmbeddedAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
                                                    } else {
                                                        throw new InvalidElmentException("Invalid JPA Pin Element");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//        ((PNodeWidget) scene.findWidget(nodeWidgetInfo)).attachPinWidget(widget);
        return widget;
    }

    @Override
    public void dettachEdgeSourceAnchor(JPAModelerScene scene, IEdgeWidget edgeWidget, IPinWidget sourcePinWidget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dettachEdgeTargetAnchor(JPAModelerScene scene, IEdgeWidget edgeWidget, IPinWidget targetPinWidget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void attachEdgeSourceAnchor(JPAModelerScene scene, IEdgeWidget edgeWidget, IPinWidget sourcePinWidget) {
        edgeWidget.setSourceAnchor(sourcePinWidget.createAnchor());

    }

    @Override
    public void attachEdgeSourceAnchor(JPAModelerScene scene, IEdgeWidget edgeWidget, INodeWidget sourceNodeWidget) { //BUG : Remove this method
        edgeWidget.setSourceAnchor(((IPNodeWidget) sourceNodeWidget).getNodeAnchor());
    }

    @Override
    public void attachEdgeTargetAnchor(JPAModelerScene scene, IEdgeWidget edgeWidget, IPinWidget targetPinWidget) {
        edgeWidget.setTargetAnchor(targetPinWidget.createAnchor());
    }

    @Override
    public void attachEdgeTargetAnchor(JPAModelerScene scene, IEdgeWidget edgeWidget, INodeWidget targetNodeWidget) { //BUG : Remove this method
        edgeWidget.setTargetAnchor(((IPNodeWidget) targetNodeWidget).getNodeAnchor());
    }

    @Override
    public IEdgeWidget attachEdgeWidget(JPAModelerScene scene, EdgeWidgetInfo widgetInfo) {
        IEdgeWidget edgeWidget = getEdgeWidget(scene, widgetInfo);
        edgeWidget.setEndPointShape(PointShape.SQUARE_FILLED_SMALL);
        edgeWidget.setControlPointShape(PointShape.SQUARE_FILLED_SMALL);
        edgeWidget.setRouter(scene.getRouter());
        ((IFlowEdgeWidget) edgeWidget).setName(widgetInfo.getName());

        return edgeWidget;
    }

    @Override
    public ResizeBorder getNodeBorder(INodeWidget nodeWidget) {
//        if (nodeWidget instanceof EntityWidget) {
        nodeWidget.setWidgetBorder(NodeWidget.RECTANGLE_RESIZE_BORDER);
        return PNodeWidget.RECTANGLE_RESIZE_BORDER;
//        }
//        else {
//            nodeWidget.setWidgetBorder(NodeWidget.CIRCLE_RESIZE_BORDER);
//            return PNodeWidget.CIRCLE_RESIZE_BORDER;
//        }
    }

    @Override
    public INodeWidget attachNodeWidget(JPAModelerScene scene, NodeWidgetInfo widgetInfo) {
        IFlowNodeWidget widget = null;
        IModelerDocument modelerDocument = widgetInfo.getModelerDocument();
        switch (modelerDocument.getId()) {
            case "Entity":
                widget = new EntityWidget(scene, widgetInfo);
                break;
            case "AbstractEntity":
                widget = new EntityWidget(scene, widgetInfo);
                ((EntityWidget) widget).setAbstractEntity(true);
                break;
            case "MappedSuperclass":
                widget = new MappedSuperclassWidget(scene, widgetInfo);
                break;
            case "Embeddable":
                widget = new EmbeddableWidget(scene, widgetInfo);
                break;
            default:
                throw new InvalidElmentException("Invalid JPA Element");
        }

        return (INodeWidget) widget;
    }

    private IEdgeWidget getEdgeWidget(JPAModelerScene scene, EdgeWidgetInfo edgeWidgetInfo) {
        IEdgeWidget edgeWidget = null;
        switch (edgeWidgetInfo.getType()) {
            case "UOTO_RELATION":
            case "PKUOTO_RELATION":
                edgeWidget = new UOTORelationFlowWidget(scene, edgeWidgetInfo);
                break;
            case "BOTO_RELATION":
            case "PKBOTO_RELATION":
                edgeWidget = new BOTORelationFlowWidget(scene, edgeWidgetInfo);
                break;
            case "UOTM_RELATION":
                edgeWidget = new UOTMRelationFlowWidget(scene, edgeWidgetInfo);
                break;
            case "UMTO_RELATION":
            case "PKUMTO_RELATION":
                edgeWidget = new UMTORelationFlowWidget(scene, edgeWidgetInfo);
                break;
            case "BMTO_RELATION":
            case "PKBMTO_RELATION":
                edgeWidget = new BMTORelationFlowWidget(scene, edgeWidgetInfo);
                break;
            case "UMTM_RELATION":
                edgeWidget = new UMTMRelationFlowWidget(scene, edgeWidgetInfo);
                break;
            case "BMTM_RELATION":
                edgeWidget = new BMTMRelationFlowWidget(scene, edgeWidgetInfo);
                break;
            case "GENERALIZATION":
                edgeWidget = new GeneralizationFlowWidget(scene, edgeWidgetInfo);
                break;
            case "SINGLE_EMBEDDABLE_RELATION":
                edgeWidget = new SingleValueEmbeddableFlowWidget(scene, edgeWidgetInfo);
                break;
            case "MULTI_EMBEDDABLE_RELATION":
                edgeWidget = new MultiValueEmbeddableFlowWidget(scene, edgeWidgetInfo);
                break;
        }
//        else if (edgeWidgetInfo.getType().equals("ENTITY_OTM_RELATION")) {
//            edgeWidget = new OTMRelationFlowWidget(scene, edgeWidgetInfo);
//        }
        return edgeWidget;
    }

    @Override
    public String getEdgeType(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, String connectionContextToolId) {
        String edgeType = null;
//        if (sourceNodeWidget instanceof EntityWidget && targetNodeWidget instanceof EntityWidget && connectionContextToolId.equals("OTO_RELATION")) {
//            edgeType = "ENTITY_UOTO_RELATION";
//        } else if (sourceNodeWidget instanceof EntityWidget && targetNodeWidget instanceof EntityWidget && connectionContextToolId.equals("OTM_RELATION")) {
//            edgeType = "ENTITY_UOTM_RELATION";
//        } else if (sourceNodeWidget instanceof EntityWidget && targetNodeWidget instanceof EntityWidget && connectionContextToolId.equals("MTO_RELATION")) {
//            edgeType = "ENTITY_UMTO_RELATION";
//        } else if (sourceNodeWidget instanceof EntityWidget && targetNodeWidget instanceof EntityWidget && connectionContextToolId.equals("MTM_RELATION")) {
//            edgeType = "ENTITY_UMTM_RELATION";
//        }
        edgeType = connectionContextToolId;
        return edgeType;
    }

    @Override
    public PinWidgetInfo getEdgeSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget) {
        return getEdgeSourcePinWidget(sourceNodeWidget, targetNodeWidget, edgeWidget, null);
    }

    public PinWidgetInfo getEdgeSourcePinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget, AttributeWidget sourceAttributeWidget) {
        if (sourceNodeWidget instanceof PersistenceClassWidget && targetNodeWidget instanceof EntityWidget && edgeWidget instanceof RelationFlowWidget) {
            PersistenceClassWidget sourcePersistenceWidget = (PersistenceClassWidget) sourceNodeWidget;
            EntityWidget targetEntityWidget = (EntityWidget) targetNodeWidget;
            RelationFlowWidget relationFlowWidget = (RelationFlowWidget) edgeWidget;
            RelationAttributeWidget<? extends RelationAttribute> relationAttributeWidget = null;
            if (relationFlowWidget instanceof OTORelationFlowWidget) {
                OTORelationFlowWidget otoRelationFlowWidget = (OTORelationFlowWidget) relationFlowWidget;
                OTORelationAttributeWidget otoRelationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    otoRelationAttributeWidget = sourcePersistenceWidget.addNewOneToOneRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()));
                } else {
                    otoRelationAttributeWidget = (OTORelationAttributeWidget) sourceAttributeWidget;
                }
                if (otoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKUOTO_RELATION") || otoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKBOTO_RELATION")) {
                    otoRelationAttributeWidget.getBaseElementSpec().setPrimaryKey(Boolean.TRUE);
                }
                otoRelationAttributeWidget.setOneToOneRelationFlowWidget(otoRelationFlowWidget);
                relationAttributeWidget = otoRelationAttributeWidget;
            } else {
                if (relationFlowWidget instanceof OTMRelationFlowWidget) {
                    OTMRelationAttributeWidget otmRelationAttributeWidget;
                    if (sourceAttributeWidget == null) {
                        otmRelationAttributeWidget = sourcePersistenceWidget.addNewOneToManyRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()));
                    } else {
                        otmRelationAttributeWidget = (OTMRelationAttributeWidget) sourceAttributeWidget;
                    }
                    otmRelationAttributeWidget.setHierarchicalRelationFlowWidget((OTMRelationFlowWidget) relationFlowWidget);
                    relationAttributeWidget = otmRelationAttributeWidget;
                } else {
                    if (relationFlowWidget instanceof MTORelationFlowWidget) {
                        MTORelationFlowWidget mtoRelationFlowWidget = (MTORelationFlowWidget) relationFlowWidget;
                        MTORelationAttributeWidget mtoRelationAttributeWidget;
                        if (sourceAttributeWidget == null) {
                            mtoRelationAttributeWidget = sourcePersistenceWidget.addNewManyToOneRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()));
                        } else {
                            mtoRelationAttributeWidget = (MTORelationAttributeWidget) sourceAttributeWidget;
                        }
                        if (mtoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKUMTO_RELATION") || mtoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKBMTO_RELATION")) {
                            mtoRelationAttributeWidget.getBaseElementSpec().setPrimaryKey(Boolean.TRUE);
                        }
                        mtoRelationAttributeWidget.setManyToOneRelationFlowWidget(mtoRelationFlowWidget);
                        relationAttributeWidget = mtoRelationAttributeWidget;

                    } else {
                        if (relationFlowWidget instanceof MTMRelationFlowWidget) {
                            MTMRelationAttributeWidget mtmRelationAttributeWidget;
                            if (sourceAttributeWidget == null) {
                                mtmRelationAttributeWidget = sourcePersistenceWidget.addNewManyToManyRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()));
                            } else {
                                mtmRelationAttributeWidget = (MTMRelationAttributeWidget) sourceAttributeWidget;
                            }
                            mtmRelationAttributeWidget.setManyToManyRelationFlowWidget((MTMRelationFlowWidget) relationFlowWidget);
                            relationAttributeWidget = mtmRelationAttributeWidget;
                        } else {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }
                    }
                }
            }

            relationFlowWidget.setSourceRelationAttributeWidget(relationAttributeWidget);
            relationAttributeWidget.getBaseElementSpec().setOwner(true);
            return relationAttributeWidget.getPinWidgetInfo();

        } else {
            if (edgeWidget instanceof GeneralizationFlowWidget) {
                JavaClassWidget sourceJavaClassWidget = (JavaClassWidget) sourceNodeWidget;
                JavaClass sourceJavaClass = (JavaClass) sourceJavaClassWidget.getBaseElementSpec();
                JavaClassWidget targetJavaClassWidget = (JavaClassWidget) targetNodeWidget;
                JavaClass targetJavaClass = (JavaClass) targetJavaClassWidget.getBaseElementSpec();
                GeneralizationFlowWidget generalizationFlowWidget = (GeneralizationFlowWidget) edgeWidget;
                sourceJavaClass.addSuperclass(targetJavaClass);
                generalizationFlowWidget.setSubclassWidget(sourceJavaClassWidget);
                generalizationFlowWidget.setSuperclassWidget(targetJavaClassWidget);
                return sourceJavaClassWidget.getInternalPinWidgetInfo();
            } else {
                if (edgeWidget instanceof EmbeddableFlowWidget) {
                    PersistenceClassWidget sourcePersistenceWidget = (PersistenceClassWidget) sourceNodeWidget;
//            JavaClass sourceJavaClass = sourcePersistenceWidget.getBaseElementSpec();
                    EmbeddableWidget targetEmbeddableWidget = (EmbeddableWidget) targetNodeWidget;
                    EmbeddableFlowWidget embeddableFlowWidget = (EmbeddableFlowWidget) edgeWidget;
                    EmbeddedAttributeWidget embeddedAttributeWidget = null;
                    if (edgeWidget instanceof SingleValueEmbeddableFlowWidget) {
                        SingleValueEmbeddedAttributeWidget singleValueEmbeddedAttributeWidget;
                        if (sourceAttributeWidget == null) {
                            singleValueEmbeddedAttributeWidget = sourcePersistenceWidget.addNewSingleValueEmbeddedAttribute(sourcePersistenceWidget.getNextAttributeName(targetEmbeddableWidget.getName()));
                        } else {
                            singleValueEmbeddedAttributeWidget = (SingleValueEmbeddedAttributeWidget) sourceAttributeWidget;
                        }
                        singleValueEmbeddedAttributeWidget.setEmbeddableFlowWidget(embeddableFlowWidget);
                        embeddedAttributeWidget = singleValueEmbeddedAttributeWidget;
                    } else {
                        if (edgeWidget instanceof MultiValueEmbeddableFlowWidget) {
                            MultiValueEmbeddedAttributeWidget multiValueEmbeddedAttributeWidget;
                            if (sourceAttributeWidget == null) {
                                multiValueEmbeddedAttributeWidget = sourcePersistenceWidget.addNewMultiValueEmbeddedAttribute(sourcePersistenceWidget.getNextAttributeName(targetEmbeddableWidget.getName()));
                            } else {
                                multiValueEmbeddedAttributeWidget = (MultiValueEmbeddedAttributeWidget) sourceAttributeWidget;
                            }
                            multiValueEmbeddedAttributeWidget.setEmbeddableFlowWidget(embeddableFlowWidget);
                            embeddedAttributeWidget = multiValueEmbeddedAttributeWidget;
                        } else {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }
                    }
                    embeddableFlowWidget.setSourceEmbeddedAttributeWidget(embeddedAttributeWidget);
                    return embeddedAttributeWidget.getPinWidgetInfo();
                } else {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            }
        }

    }

    @Override
    public PinWidgetInfo getEdgeTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget) {
        return getEdgeTargetPinWidget(sourceNodeWidget, targetNodeWidget, edgeWidget, null);
    }

    public PinWidgetInfo getEdgeTargetPinWidget(INodeWidget sourceNodeWidget, INodeWidget targetNodeWidget, IEdgeWidget edgeWidget, RelationAttributeWidget targetRelationAttributeWidget) {
        if (edgeWidget instanceof Direction && edgeWidget instanceof RelationFlowWidget && sourceNodeWidget instanceof EntityWidget && targetNodeWidget instanceof EntityWidget) {
            EntityWidget targetEntityWidget = (EntityWidget) targetNodeWidget;
            EntityWidget sourceEntityWidget = (EntityWidget) sourceNodeWidget;
            if (edgeWidget instanceof Unidirectional) {
                Unidirectional uRelationFlowWidget = (Unidirectional) edgeWidget;
                uRelationFlowWidget.setTargetEntityWidget(targetEntityWidget);
                targetEntityWidget.addInverseSideRelationFlowWidget((RelationFlowWidget) uRelationFlowWidget);
                if (targetRelationAttributeWidget == null) { //called for new added not for already exist widget from loaded document
                    RelationAttributeWidget sourceRelationAttributeWidget = uRelationFlowWidget.getSourceRelationAttributeWidget();
                    sourceRelationAttributeWidget.setConnectedSibling(targetEntityWidget);
                }
                return targetEntityWidget.getInternalPinWidgetInfo();
            } else {
                if (edgeWidget instanceof Bidirectional) {
                    if (edgeWidget instanceof BOTORelationFlowWidget) {
                        BOTORelationFlowWidget botoRelationFlowWidget = (BOTORelationFlowWidget) edgeWidget;
                        OTORelationAttributeWidget targetOTORelationAttributeWidget;
                        if (targetRelationAttributeWidget == null) {
                            targetOTORelationAttributeWidget = targetEntityWidget.addNewOneToOneRelationAttribute(targetEntityWidget.getNextAttributeName(sourceEntityWidget.getName()));
                            RelationAttributeWidget sourceOTORelationAttributeWidget = botoRelationFlowWidget.getSourceRelationAttributeWidget();
                            sourceOTORelationAttributeWidget.setConnectedSibling(targetEntityWidget, targetOTORelationAttributeWidget);
                            targetOTORelationAttributeWidget.setConnectedSibling(sourceEntityWidget, sourceOTORelationAttributeWidget);
                        } else {
                            targetOTORelationAttributeWidget = (OTORelationAttributeWidget) targetRelationAttributeWidget;
                        }
                        targetOTORelationAttributeWidget.setOneToOneRelationFlowWidget(botoRelationFlowWidget);
                        botoRelationFlowWidget.setTargetRelationAttributeWidget(targetOTORelationAttributeWidget);

                        return targetOTORelationAttributeWidget.getPinWidgetInfo();

                    } else {
                        if (edgeWidget instanceof BMTORelationFlowWidget) {
                            BMTORelationFlowWidget bmtoRelationFlowWidget = (BMTORelationFlowWidget) edgeWidget;
                            OTMRelationAttributeWidget targetMTORelationAttributeWidget;
                            if (targetRelationAttributeWidget == null) {
                                targetMTORelationAttributeWidget = targetEntityWidget.addNewOneToManyRelationAttribute(targetEntityWidget.getNextAttributeName(sourceEntityWidget.getName()));
                                RelationAttributeWidget sourceMTORelationAttributeWidget = bmtoRelationFlowWidget.getSourceRelationAttributeWidget();
                                sourceMTORelationAttributeWidget.setConnectedSibling(targetEntityWidget, targetMTORelationAttributeWidget);
                                targetMTORelationAttributeWidget.setConnectedSibling(sourceEntityWidget, sourceMTORelationAttributeWidget);
                            } else {
                                targetMTORelationAttributeWidget = (OTMRelationAttributeWidget) targetRelationAttributeWidget;
                            }
                            targetMTORelationAttributeWidget.setHierarchicalRelationFlowWidget(bmtoRelationFlowWidget);
                            bmtoRelationFlowWidget.setTargetRelationAttributeWidget(targetMTORelationAttributeWidget);
                            return targetMTORelationAttributeWidget.getPinWidgetInfo();
                        } else {
                            if (edgeWidget instanceof BMTMRelationFlowWidget) {
                                BMTMRelationFlowWidget bmtmRelationFlowWidget = (BMTMRelationFlowWidget) edgeWidget;
                                MTMRelationAttributeWidget targetMTMRelationAttributeWidget;
                                if (targetRelationAttributeWidget == null) {
                                    targetMTMRelationAttributeWidget = targetEntityWidget.addNewManyToManyRelationAttribute(targetEntityWidget.getNextAttributeName(sourceEntityWidget.getName()));
                                    RelationAttributeWidget sourceMTMRelationAttributeWidget = bmtmRelationFlowWidget.getSourceRelationAttributeWidget();
                                    sourceMTMRelationAttributeWidget.setConnectedSibling(targetEntityWidget, targetMTMRelationAttributeWidget);
                                    targetMTMRelationAttributeWidget.setConnectedSibling(sourceEntityWidget, sourceMTMRelationAttributeWidget);

                                } else {
                                    targetMTMRelationAttributeWidget = (MTMRelationAttributeWidget) targetRelationAttributeWidget;
                                }
                                targetMTMRelationAttributeWidget.setManyToManyRelationFlowWidget(bmtmRelationFlowWidget);
                                bmtmRelationFlowWidget.setTargetRelationAttributeWidget(targetMTMRelationAttributeWidget);
                                return targetMTMRelationAttributeWidget.getPinWidgetInfo();
                            } else {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }
                        }
                    }
                } else {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            }

        } else {
            if (edgeWidget instanceof Direction && (sourceNodeWidget instanceof MappedSuperclassWidget || sourceNodeWidget instanceof EmbeddableWidget) && targetNodeWidget instanceof EntityWidget) {
                EntityWidget targetEntityWidget = (EntityWidget) targetNodeWidget;
                if (edgeWidget instanceof Unidirectional) {
                    Unidirectional uRelationFlowWidget = (Unidirectional) edgeWidget;
                    uRelationFlowWidget.setTargetEntityWidget(targetEntityWidget);
                    if (targetRelationAttributeWidget == null) { //called for new added not for already exist widget from loaded document
                        RelationAttributeWidget sourceRelationAttributeWidget = uRelationFlowWidget.getSourceRelationAttributeWidget();
                        sourceRelationAttributeWidget.setConnectedSibling(targetEntityWidget);
                    }
                    return targetEntityWidget.getInternalPinWidgetInfo();
                } else {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            } else {
                if (edgeWidget instanceof GeneralizationFlowWidget && sourceNodeWidget instanceof JavaClassWidget && targetNodeWidget instanceof JavaClassWidget) {
                    JavaClassWidget targetJavaClassWidget = (JavaClassWidget) targetNodeWidget;
                    return targetJavaClassWidget.getInternalPinWidgetInfo();
                } else {
                    if (edgeWidget instanceof EmbeddableFlowWidget && sourceNodeWidget instanceof PersistenceClassWidget && targetNodeWidget instanceof EmbeddableWidget) {
                        EmbeddableWidget targetEmbeddableWidget = (EmbeddableWidget) targetNodeWidget;
                        ((EmbeddableFlowWidget) edgeWidget).setTargetEmbeddableWidget(targetEmbeddableWidget);
                        targetEmbeddableWidget.addIncomingEmbeddableFlowWidget((EmbeddableFlowWidget) edgeWidget);
                        EmbeddedAttributeWidget sourceEmbeddedAttributeWidget = ((EmbeddableFlowWidget) edgeWidget).getSourceEmbeddedAttributeWidget();
                        sourceEmbeddedAttributeWidget.setConnectedSibling(targetEmbeddableWidget);
                        return targetEmbeddableWidget.getInternalPinWidgetInfo();
                    } else {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                }
            }
        }

    }

    public static void initEntityModel(javax.swing.JComboBox entityComboBox, EntityMappings entityMappings) {
        entityComboBox.removeAllItems();
        entityComboBox.addItem(new ComboBoxValue(null, ""));
        entityMappings.getEntity().stream().forEach((entity) -> {
            entityComboBox.addItem(new ComboBoxValue(entity, entity.getClazz()));
        });
    }

    public static void initReferencedColumnModel(javax.swing.JComboBox columnComboBox, Entity entity, Id selectedColumn) {
        columnComboBox.setEditable(true);
        columnComboBox.removeAllItems();
        columnComboBox.addItem(new ComboBoxValue(null, ""));
        if (entity != null) {
//            entity.getAttributes().getBasic().stream().forEach((basic) -> {
//                if (basic.getColumn() != null && org.apache.commons.lang.StringUtils.isNotBlank(basic.getColumn().getName())) {
//                    columnComboBox.addItem(new ComboBoxValue(basic, basic.getColumn().getName()));
//                } else {
//                    columnComboBox.addItem(new ComboBoxValue(basic, basic.getName()));
//                }
//            });
            int i = 0;
            int selectedItemIndex = -1;
            for (Id id : entity.getAttributes().getId()) {
                String columnName = id.getReferenceColumnName();
                columnComboBox.addItem(new ComboBoxValue(id, columnName));
                if (id == selectedColumn) {
                    selectedItemIndex = ++i;
                }
            }
            columnComboBox.setSelectedIndex(selectedItemIndex);
        }
    }

    /**
     * Micro DB filter
     *
     * @param mappings The graph
     * @param entity The master node
     * @return
     */
    public static EntityMappings isolateEntityMapping(EntityMappings mappings, Entity entity) {

        EntityMappings mappingClone = cloneObject(mappings);
        Entity entityClone = mappingClone.getEntity(entity.getId());

        Set<Entity> relationClasses = getRelationClass(entityClone.getAttributes());

        mappingClone.getEntity().stream().filter(e -> e.getAttributes().getRelationAttributes().stream().anyMatch(r -> r.getConnectedEntity() == entityClone)).forEach(relationClasses::add);
        relationClasses.remove(entityClone);
        relationClasses.stream().map(e -> e.getAttributes()).forEach(attr -> {
            attr.getManyToMany().removeIf(r -> r.getConnectedEntity() != entityClone);
            attr.getManyToOne().removeIf(r -> r.getConnectedEntity() != entityClone);
            attr.getOneToMany().removeIf(r -> r.getConnectedEntity() != entityClone);
            attr.getOneToOne().removeIf(r -> r.getConnectedEntity() != entityClone);
            attr.setEmbedded(null);
        });
        relationClasses.add(entityClone);
        mappingClone.setEntity(new ArrayList<>());
        relationClasses.stream().forEach(mappingClone::addEntity);
        mapToOrignalObject(mappings, mappingClone);
        return mappingClone;
    }

    private static Set<Entity> getRelationClass(BaseAttributes attributes) {
        Set<Entity> relationAttributeconnected = attributes.getRelationAttributes().stream().map(RelationAttribute::getConnectedEntity).collect(toSet());
        relationAttributeconnected.addAll(attributes.getEmbedded().stream().map(e -> e.getConnectedClass()).flatMap(c -> getRelationClass(c.getAttributes()).stream()).collect(toSet()));
        if (attributes instanceof Attributes && ((Attributes) attributes).getEmbeddedId() != null) {
            relationAttributeconnected.addAll(getRelationClass(((Attributes) attributes).getEmbeddedId().getConnectedClass().getAttributes()));
        }
        return relationAttributeconnected;
    }

    public static EntityMappings cloneObject(EntityMappings entityMappings) {
        EntityMappings definition_Load = null;
        try {
            if (MODELER_MARSHALLER == null) {
                MODELER_MARSHALLER = MODELER_CONTEXT.createMarshaller();
                MODELER_MARSHALLER.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                MODELER_MARSHALLER.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://java.sun.com/xml/ns/persistence/orm orm_2_1.xsd");
                MODELER_MARSHALLER.setEventHandler(new ValidateJAXB());
            }
            StringWriter sw = new StringWriter();
            MODELER_MARSHALLER.marshal(entityMappings, sw);

            if (MODELER_UNMARSHALLER == null) {
                MODELER_UNMARSHALLER = MODELER_CONTEXT.createUnmarshaller();
            }
            StringReader reader = new StringReader(sw.toString());
            definition_Load = MODELER_UNMARSHALLER.unmarshal(new StreamSource(reader), EntityMappings.class).getValue();
        } catch (JAXBException ex) {
            ExceptionUtils.printStackTrace(ex);
        }
        return definition_Load;
    }

    public static void generateSourceCode(ModelerFile modelerFile) {
        EntityMappings mappings = (EntityMappings) modelerFile.getDefinitionElement();
        GenerateCodeDialog dialog = new GenerateCodeDialog(modelerFile.getFileObject());
        dialog.setVisible(true);
        if (dialog.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {
//            if (mappings.getPreviousVersion() < mappings.getVersion()) {
//                int reply = javax.swing.JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
//                        org.openide.util.NbBundle.getMessage(JPAModelerUtil.class, "Notification.GEN_SRC_WITHOUT_JCRE.text"),
//                        org.openide.util.NbBundle.getMessage(JPAModelerUtil.class, "Notification.GEN_SRC_WITHOUT_JCRE.title"),
//                        JOptionPane.YES_NO_OPTION);
//                if (reply == JOptionPane.NO_OPTION) {
//                    return;
//                }
//            }

            RequestProcessor processor = new RequestProcessor("jpa/ExportCode"); // NOI18N
            SourceCodeGeneratorTask task = new SourceCodeGeneratorTask(modelerFile, dialog.getTargetPoject(), dialog.getSourceGroup());
            processor.post(task);
        }
    }

    private static void makeSiblingOrphan(Entity entity, RelationAttribute relationAttribute, Entity siblingEntity, RelationAttribute siblingRelationAttribute) {
        Attributes attr = entity.getAttributes();
        if (relationAttribute != null) {
            attr.getManyToMany().removeIf(r -> r != relationAttribute);
            attr.getManyToOne().removeIf(r -> r != relationAttribute);
            attr.getOneToMany().removeIf(r -> r != relationAttribute);
            attr.getOneToOne().removeIf(r -> r != relationAttribute);
        } else {
            attr.getManyToMany().removeIf(r -> r.getConnectedAttribute() != siblingRelationAttribute);
            attr.getManyToOne().removeIf(r -> r.getConnectedAttribute() != siblingRelationAttribute);
            attr.getOneToMany().removeIf(r -> r.getConnectedAttribute() != siblingRelationAttribute);
            attr.getOneToOne().removeIf(r -> r.getConnectedAttribute() != siblingRelationAttribute);
        }
        attr.setElementCollection(null);
    }

    private static void makeSiblingOrphan(Embeddable embeddable) {
        EmbeddableAttributes attr = embeddable.getAttributes();
        attr.setManyToMany(null);
        attr.setManyToOne(null);
        attr.setOneToMany(null);
        attr.setOneToOne(null);
        attr.setEmbedded(null);
        attr.setElementCollection(null);
    }

    private static void makeSiblingOrphan(MappedSuperclass mappedSuperclass) {
        Attributes attr = mappedSuperclass.getAttributes();
        attr.setManyToMany(null);
        attr.setManyToOne(null);
        attr.setOneToMany(null);
        attr.setOneToOne(null);
        attr.setEmbedded(null);
        attr.setElementCollection(null);
    }

    private static void mapToOrignalObject(EntityMappings orignalMappings, EntityMappings clonedMappings) {
        clonedMappings.getEntity().forEach(class_ -> {
            Entity orignalEntity = orignalMappings.getEntity(class_.getId());
            class_.setOrignalObject(orignalEntity);
            mapToOrignalObject(orignalEntity.getAttributes(), class_.getAttributes());
        });
        clonedMappings.getEmbeddable().forEach(class_ -> {
            Embeddable orignalEmbeddable = orignalMappings.getEmbeddable(class_.getId());
            class_.setOrignalObject(orignalEmbeddable);
            mapToOrignalObject(orignalEmbeddable.getAttributes(), class_.getAttributes());
        });
        clonedMappings.getMappedSuperclass().forEach(e -> {
            MappedSuperclass orignalMappedSuperclass = orignalMappings.getMappedSuperclass(e.getId());
            e.setOrignalObject(orignalMappedSuperclass);
            mapToOrignalObject(orignalMappedSuperclass.getAttributes(), e.getAttributes());
        });

    }

    private static void mapToOrignalObject(BaseAttributes orignalAttributes, BaseAttributes clonedAttributes) {

        clonedAttributes.getBasic().forEach(a -> {
            a.setOrignalObject(orignalAttributes.getBasic(a.getId()).get());
        });
        clonedAttributes.getElementCollection().forEach(a -> {
            a.setOrignalObject(orignalAttributes.getElementCollection(a.getId()).get());
        });
        clonedAttributes.getEmbedded().forEach(a -> {
            a.setOrignalObject(orignalAttributes.getEmbedded(a.getId()).get());
        });

        clonedAttributes.getManyToMany().forEach(a -> {
            a.setOrignalObject(orignalAttributes.getManyToMany(a.getId()).get());
        });
        clonedAttributes.getManyToOne().forEach(a -> {
            a.setOrignalObject(orignalAttributes.getManyToOne(a.getId()).get());
        });
        clonedAttributes.getOneToMany().forEach(a -> {
            a.setOrignalObject(orignalAttributes.getOneToMany(a.getId()).get());
        });
        clonedAttributes.getOneToOne().forEach(a -> {
            a.setOrignalObject(orignalAttributes.getOneToOne(a.getId()).get());
        });
        clonedAttributes.getTransient().forEach(a -> {
            a.setOrignalObject(orignalAttributes.getTransient(a.getId()).get());
        });
        if (clonedAttributes instanceof Attributes) {
            ((Attributes) clonedAttributes).getId().forEach(a -> {
                a.setOrignalObject(((Attributes) orignalAttributes).getId(a.getId()).get());
            });
            ((Attributes) clonedAttributes).getVersion().forEach(a -> {
                a.setOrignalObject(((Attributes) orignalAttributes).getVersion(a.getId()).get());
            });
        }

    }

    public static EntityMappings isolateEntityMapping(EntityMappings mappings, Entity javaClass, RelationAttribute relationAttribute) {

        EntityMappings mappingClone = cloneObject(mappings);
        Entity entityClone = mappingClone.getEntity(javaClass.getId());
        RelationAttribute relationAttributeClone = entityClone.getAttributes().getRelationAttribute(relationAttribute.getId()).get();

        Entity mappedEntityClone = relationAttributeClone.getConnectedEntity();
        RelationAttribute mappedRelationAttributeClone = relationAttributeClone.getConnectedAttribute();

        makeSiblingOrphan(entityClone, relationAttributeClone, mappedEntityClone, mappedRelationAttributeClone);
        makeSiblingOrphan(mappedEntityClone, mappedRelationAttributeClone, entityClone, relationAttributeClone);

        mappingClone.getEmbeddable().stream().forEach((embeddable) -> makeSiblingOrphan(embeddable));
        mappingClone.getMappedSuperclass().stream().forEach((mappedSuperclass) -> makeSiblingOrphan(mappedSuperclass));

        Set<Entity> relationClasses = new HashSet<>();
        relationClasses.add(entityClone);
        relationClasses.add(mappedEntityClone);
        mappingClone.setEntity(new ArrayList<>());
        relationClasses.stream().forEach(mappingClone::addEntity);
        mapToOrignalObject(mappings, mappingClone);
        return mappingClone;
    }

    public static void openDBViewer(ModelerFile file) {
        EntityMappings entityMappings = (EntityMappings) file.getModelerScene().getBaseElementSpec();
        openDBViewer(file, entityMappings);
    }

    public static void openDBViewer(ModelerFile file, EntityMappings entityMappings) {
        DBModelerRequestManager dbModelerRequestManager = Lookup.getDefault().lookup(DBModelerRequestManager.class);//new DefaultSourceCodeGeneratorFactory();//SourceGeneratorFactoryProvider.getInstance();//
        Optional<ModelerFile> dbChildModelerFile = file.getChildrenFile("DB");

        dbModelerRequestManager.init(file, entityMappings);
        if (dbChildModelerFile.isPresent()) {
            ModelerFile childModelerFile = dbChildModelerFile.get();
            IModelerScene scene = childModelerFile.getModelerScene();
            scene.getBaseElements().stream().filter(element -> element instanceof INodeWidget).forEach(element -> {
                ((INodeWidget) element).remove(false);
            });
            childModelerFile.unload();
            try {
                childModelerFile.getModelerUtil().loadModelerFile(childModelerFile);
            } catch (Exception ex) {
                file.handleException(ex);
            }
            childModelerFile.loaded();
        }
    }

    /**
     * This method is used, when modeler file is not created and version is
     * required.
     *
     * @return
     */
    public static float getModelerFileVersion() {
        Class _class = JPAFileActionListener.class;//.get
        Annotation[] annotations = _class.getAnnotations();

        for (Annotation annotation : annotations) {
            if (annotation instanceof DiagramModel) {
                DiagramModel diagramModel = (DiagramModel) annotation;
                return diagramModel.version();
            }
        }
        return 0.0f;
    }

}
