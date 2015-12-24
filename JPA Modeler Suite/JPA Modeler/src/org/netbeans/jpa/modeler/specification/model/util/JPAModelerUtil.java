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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.widget.Widget;
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
import org.netbeans.jpa.modeler.navigator.entitygraph.NamedEntityGraphPanel;
import org.netbeans.jpa.modeler.properties.joincolumn.JoinColumnPanel;
import org.netbeans.jpa.modeler.properties.named.nativequery.NamedNativeQueryPanel;
import org.netbeans.jpa.modeler.properties.named.query.NamedQueryPanel;
import org.netbeans.jpa.modeler.properties.named.resultsetmapping.ResultSetMappingsPanel;
import org.netbeans.jpa.modeler.properties.named.storedprocedurequery.NamedStoredProcedureQueryPanel;
import org.netbeans.jpa.modeler.spec.AccessType;
import static org.netbeans.jpa.modeler.spec.AccessType.FIELD;
import static org.netbeans.jpa.modeler.spec.AccessType.PROPERTY;
import org.netbeans.jpa.modeler.spec.DefaultAttribute;
import org.netbeans.jpa.modeler.spec.DefaultClass;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.FetchType;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.NamedEntityGraph;
import org.netbeans.jpa.modeler.spec.NamedNativeQuery;
import org.netbeans.jpa.modeler.spec.NamedQuery;
import org.netbeans.jpa.modeler.spec.NamedStoredProcedureQuery;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.SqlResultSetMapping;
import org.netbeans.jpa.modeler.spec.design.Bounds;
import org.netbeans.jpa.modeler.spec.design.Diagram;
import org.netbeans.jpa.modeler.spec.design.DiagramElement;
import org.netbeans.jpa.modeler.spec.design.Edge;
import org.netbeans.jpa.modeler.spec.design.Plane;
import org.netbeans.jpa.modeler.spec.design.Shape;
import org.netbeans.jpa.modeler.spec.extend.AccessTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.CollectionTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.CompositePrimaryKeyType;
import org.netbeans.jpa.modeler.spec.extend.CompositionAttribute;
import org.netbeans.jpa.modeler.spec.extend.FetchTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.FlowNode;
import org.netbeans.jpa.modeler.spec.extend.IPersistenceAttributes;
import org.netbeans.jpa.modeler.spec.extend.InheritenceHandler;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.PrimaryKeyContainer;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;
import static org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType.XML_ELEMENT;
import static org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType.XML_TRANSIENT;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableTypeHandler;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlAttribute;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlElement;
import org.netbeans.jpa.modeler.specification.model.file.JPAFileDataObject;
import org.netbeans.jpa.modeler.specification.model.file.action.JPAFileActionListener;
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
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.ComboBoxValue;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.listener.ActionHandler;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.listener.ComboBoxListener;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.support.ComboBoxPropertySupport;
import org.netbeans.modeler.properties.nentity.Column;
import org.netbeans.modeler.properties.nentity.NAttributeEntity;
import org.netbeans.modeler.properties.nentity.NEntityDataListener;
import org.netbeans.modeler.properties.nentity.NEntityPropertySupport;
import org.netbeans.modeler.scene.vmd.AbstractPModelerScene;
import org.netbeans.modeler.shape.ShapeDesign;
import org.netbeans.modeler.specification.model.ModelerDiagramSpecification;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.specification.model.document.IPModelerScene;
import org.netbeans.modeler.specification.model.document.core.IFlowNode;
import org.netbeans.modeler.specification.model.document.property.ElementPropertySet;
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
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.PropertySupport;
import org.openide.util.Exceptions;

public class JPAModelerUtil implements PModelerUtil {

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

    private static JAXBContext MODELER_CONTEXT;
    public static Unmarshaller MODELER_UNMARSHALLER;
    public static Marshaller MODELER_MARSHALLER;
//    private final static InputOutput IO;

    static {

        try {
            MODELER_CONTEXT = JAXBContext.newInstance(new Class<?>[]{EntityMappings.class}); // unmarshaller will be always init before marshaller
        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        }
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
            System.out.println("IUtil Total time : " + (new Date().getTime() - st) + " sec");
        }
    }

    public static EntityMappings getEntityMapping(File file) {
        EntityMappings definition_Load = null;
        try {
//            if (MODELER_CONTEXT == null) {
//               
//            }

            if (MODELER_UNMARSHALLER == null) {
                MODELER_UNMARSHALLER = MODELER_CONTEXT.createUnmarshaller();
//            MODELER_UNMARSHALLER.setEventHandler(new ValidateJAXB());

            }
            definition_Load = MODELER_UNMARSHALLER.unmarshal(new StreamSource(file), EntityMappings.class).getValue();
        } catch (JAXBException ex) {
//            IO.getOut().println("Exception: " + ex.toString());
            ex.printStackTrace();
        }
        return definition_Load;
    }

    @Override
    public void loadModelerFile(ModelerFile file) {
        try {
            IModelerScene scene = file.getModelerScene();
            File savedFile = file.getFile();
            EntityMappings entityMappings = getEntityMapping(savedFile);
            if (entityMappings == null) {
                ElementConfigFactory elementConfigFactory = file.getVendorSpecification().getElementConfigFactory();
                entityMappings = EntityMappings.getNewInstance();
                elementConfigFactory.initializeObjectValue(entityMappings);
            }

            Diagram diagram = entityMappings.getJPADiagram();
            ModelerDiagramSpecification modelerDiagram = file.getModelerDiagramModel();
            modelerDiagram.setDefinitionElement(entityMappings);
            modelerDiagram.setRootElement(entityMappings);
            modelerDiagram.setDiagramElement(diagram);
            scene.setRootElementSpec(entityMappings);
            long st = new Date().getTime();

            ((AbstractPModelerScene) scene).startSceneGeneration();
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

            ((AbstractPModelerScene) scene).commitSceneGeneration();
        } catch (IllegalStateException ex) {
//            IO.getOut().println("Exception: " + ex.toString());
            ex.printStackTrace();
        }
    }

    private void loadFlowNode(IModelerScene scene, Widget parentWidget, IFlowNode flowElement) {
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
                Exceptions.printStackTrace(ex);
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
            if (flowElement instanceof JavaClass) {
                JavaClass _class = (JavaClass) flowElement;
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
                        if (elementCollection.getConnectedClassId() != null) {
                            if (elementCollection.isVisibile()) {
                                entityWidget.addNewMultiValueEmbeddedAttribute(elementCollection.getName(), elementCollection);
                            }
                        } else {
                            entityWidget.addNewBasicCollectionAttribute(elementCollection.getName(), elementCollection);
                        }
                    });
                    _class.getAttributes().getOneToOne().stream().filter(OneToOne::isVisibile).forEach((oneToOne) -> {
                        OTORelationAttributeWidget relationAttributeWidget = entityWidget.addNewOneToOneRelationAttribute(oneToOne.getName(), oneToOne);
                        if (oneToOne.getMappedBy() == null) {
                            relationAttributeWidget.setOwner(true);
                        }
                    });
                    _class.getAttributes().getOneToMany().stream().filter(OneToMany::isVisibile).forEach((oneToMany) -> {
                        OTMRelationAttributeWidget relationAttributeWidget = entityWidget.addNewOneToManyRelationAttribute(oneToMany.getName(), oneToMany);
                        if (oneToMany.getMappedBy() == null) {
                            relationAttributeWidget.setOwner(true);
                        }
                    });
                    _class.getAttributes().getManyToOne().stream().filter(ManyToOne::isVisibile).map((manyToOne) -> entityWidget.addNewManyToOneRelationAttribute(manyToOne.getName(), manyToOne)).forEach((relationAttributeWidget) -> {
                        relationAttributeWidget.setOwner(true);//always
                    });
                    _class.getAttributes().getManyToMany().stream().filter(ManyToMany::isVisibile).forEach((manyToMany) -> {
                        MTMRelationAttributeWidget relationAttributeWidget = entityWidget.addNewManyToManyRelationAttribute(manyToMany.getName(), manyToMany);
                        if (manyToMany.getMappedBy() == null) {
                            relationAttributeWidget.setOwner(true);
                        }
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

    private void loadFlowEdge(IModelerScene scene) {
        IPModelerScene modelerScene = (IPModelerScene) scene;
        scene.getBaseElements().stream().filter((baseElementWidget) -> (baseElementWidget instanceof JavaClassWidget)).forEach((baseElementWidget) -> {
            JavaClassWidget javaClassWidget = (JavaClassWidget) baseElementWidget;
            loadGeneralization(modelerScene, javaClassWidget);
            if (baseElementWidget instanceof PersistenceClassWidget) {
                PersistenceClassWidget sourcePersistenceClassWidget = (PersistenceClassWidget) baseElementWidget;
                for (SingleValueEmbeddedAttributeWidget embeddedAttributeWidget : sourcePersistenceClassWidget.getSingleValueEmbeddedAttributeWidgets()) {
                    loadEmbeddedEdge(modelerScene, "SINGLE_EMBEDDABLE_RELATION", sourcePersistenceClassWidget, embeddedAttributeWidget);
                }
                for (MultiValueEmbeddedAttributeWidget embeddedAttributeWidget : sourcePersistenceClassWidget.getMultiValueEmbeddedAttributeWidgets()) {
                    loadEmbeddedEdge(modelerScene, "MULTI_EMBEDDABLE_RELATION", sourcePersistenceClassWidget, embeddedAttributeWidget);
                }

                for (OTORelationAttributeWidget sourceRelationAttributeWidget : sourcePersistenceClassWidget.getOneToOneRelationAttributeWidgets()) {
                    loadRelationEdge(modelerScene, "OTO_RELATION", sourcePersistenceClassWidget, sourceRelationAttributeWidget, OTORelationAttributeWidget.class);
                }
                for (OTMRelationAttributeWidget sourceRelationAttributeWidget : sourcePersistenceClassWidget.getOneToManyRelationAttributeWidgets()) {
                    loadRelationEdge(modelerScene, "OTM_RELATION", sourcePersistenceClassWidget, sourceRelationAttributeWidget, OTMRelationAttributeWidget.class);
                }
                for (MTORelationAttributeWidget sourceRelationAttributeWidget : sourcePersistenceClassWidget.getManyToOneRelationAttributeWidgets()) {
                    loadRelationEdge(modelerScene, "MTO_RELATION", sourcePersistenceClassWidget, sourceRelationAttributeWidget, OTMRelationAttributeWidget.class);
                }
                for (MTMRelationAttributeWidget sourceRelationAttributeWidget : sourcePersistenceClassWidget.getManyToManyRelationAttributeWidgets()) {
                    loadRelationEdge(modelerScene, "MTM_RELATION", sourcePersistenceClassWidget, sourceRelationAttributeWidget, MTMRelationAttributeWidget.class);
                }
            }
        });
    }

    private void loadEmbeddedEdge(IPModelerScene scene, String contextToolId, PersistenceClassWidget sourcePersistenceClassWidget, EmbeddedAttributeWidget sourceAttributeWidget) {
        CompositionAttribute sourceEmbeddedAttribute = (CompositionAttribute) sourceAttributeWidget.getBaseElementSpec();
        EmbeddableWidget targetEntityWidget = (EmbeddableWidget) scene.findBaseElement(sourceEmbeddedAttribute.getConnectedClassId());
        EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo();
        edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
        edgeInfo.setSource(sourcePersistenceClassWidget.getNodeWidgetInfo().getId());
        edgeInfo.setTarget(targetEntityWidget.getNodeWidgetInfo().getId());
        edgeInfo.setType(NBModelerUtil.getEdgeType(sourcePersistenceClassWidget, targetEntityWidget, contextToolId));
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);
        scene.setEdgeWidgetSource(edgeInfo, getEdgeSourcePinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, sourceAttributeWidget));
        scene.setEdgeWidgetTarget(edgeInfo, getEdgeTargetPinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, null));

    }

    private void loadRelationEdge(IPModelerScene scene, String abstractTool, PersistenceClassWidget sourcePersistenceClassWidget, RelationAttributeWidget sourceRelationAttributeWidget, Class<? extends RelationAttributeWidget>... targetRelationAttributeWidgetClass) {
        if (!sourceRelationAttributeWidget.isOwner()) {  //Only Owner will draw edge because in any case uni/bi owner is always exist
            return;
        }
        RelationAttribute sourceRelationAttribute = (RelationAttribute) sourceRelationAttributeWidget.getBaseElementSpec();
        EntityWidget targetEntityWidget = (EntityWidget) scene.findBaseElement(sourceRelationAttribute.getConnectedEntityId());
//                    Entity targetEntity = (Entity) targetEntityWidget.getBaseElementSpec();
        RelationAttributeWidget targetRelationAttributeWidget = null;
//                    RelationAttribute targetRelationAttribute;

        EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo();
        edgeInfo.setId(NBModelerUtil.getAutoGeneratedStringId());
        edgeInfo.setSource(sourcePersistenceClassWidget.getNodeWidgetInfo().getId());
        edgeInfo.setTarget(targetEntityWidget.getNodeWidgetInfo().getId());
        String contextToolId;
        if (sourceRelationAttribute.getConnectedAttributeId() != null) {
            targetRelationAttributeWidget = targetEntityWidget.findRelationAttributeWidget(sourceRelationAttribute.getConnectedAttributeId(), targetRelationAttributeWidgetClass);
//                        targetRelationAttribute = (RelationAttribute) targetRelationAttributeWidget.getBaseElementSpec();
            contextToolId = "B" + abstractTool;//OTM_RELATION";
        } else {
            contextToolId = "U" + abstractTool;
        }
        edgeInfo.setType(NBModelerUtil.getEdgeType(sourcePersistenceClassWidget, targetEntityWidget, contextToolId));
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);

        scene.setEdgeWidgetSource(edgeInfo, getEdgeSourcePinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, sourceRelationAttributeWidget));
        scene.setEdgeWidgetTarget(edgeInfo, getEdgeTargetPinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, targetRelationAttributeWidget));

    }

    private void loadGeneralization(IPModelerScene scene, JavaClassWidget javaClassWidget) {

        JavaClass javaClass = (JavaClass) javaClassWidget.getBaseElementSpec();
        if (javaClass.getSuperclass() != null) {
            JavaClassWidget subJavaClassWidget = javaClassWidget;
            JavaClassWidget superJavaClassWidget = (JavaClassWidget) scene.findBaseElement(javaClass.getSuperclass().getId());
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

    private void loadDiagram(IModelerScene scene, Diagram diagram, DiagramElement diagramElement) {
        if (diagramElement instanceof Shape) {
            Shape shape = (Shape) diagramElement;
            Bounds bounds = shape.getBounds();
            Widget widget = (Widget) scene.findBaseElement(shape.getElementRef());
            if (widget != null) {
                if (widget instanceof INodeWidget) { //reverse ref
                    INodeWidget nodeWidget = (INodeWidget) widget;
//                  nodeWidget.setPreferredSize(new Dimension((int) bounds.getWidth(), (int) bounds.getHeight()));
                    Point location = new Point((int) bounds.getX(), (int) bounds.getY());
                    nodeWidget.setPreferredLocation(location);
//                    nodeWidget.setActiveStatus(false);//Active Status is used to prevent reloading SVGDocument until complete document is loaded
//                    nodeWidget.setActiveStatus(true);
                } else {
                    throw new InvalidElmentException("Invalid JPA Element : " + widget);
                }
            }
        } else if (diagramElement instanceof Edge) {
//            JPAEdge edge = (JPAEdge) diagramElement;
//            Widget widget = (Widget) scene.getBaseElement(edge.getJPAElement());
//            if (widget != null && widget instanceof EdgeWidget) {
//                if (widget instanceof SequenceFlowWidget) {
//                    SequenceFlowWidget sequenceFlowWidget = (SequenceFlowWidget) widget;
//                    sequenceFlowWidget.setControlPoints(edge.getWaypointCollection(), true);
//                    if (edge.getJPALabel() != null) {
//                        Bounds bound = edge.getJPALabel().getBounds();
////                        sequenceFlowWidget.getLabelManager().getLabelWidget().getParentWidget().setPreferredLocation(bound.toPoint());
//                        sequenceFlowWidget.getLabelManager().getLabelWidget().getParentWidget().setPreferredLocation(
//                                sequenceFlowWidget.getLabelManager().getLabelWidget().convertSceneToLocal(bound.toPoint()));
//                    }
//                } else if (widget instanceof AssociationWidget) {
//                    AssociationWidget associationWidget = (AssociationWidget) widget;
//                    associationWidget.setControlPoints(edge.getWaypointCollection(), true);
//                } else {
//                    throw new InvalidElmentException("Invalid JPA Element");
//                }
////                EdgeWidget edgeWidget = (EdgeWidget)widget;
////                edgeWidget.manageControlPoint();
//
//            }
//
        }
    }

    /*---------------------------------Load File End---------------------------------*/
    /*---------------------------------Save File Satrt---------------------------------*/
//      public static void saveJPA(final JPAFile file) {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                saveJPAImpl(file);
//            }
//        };
//        final RequestProcessor.Task theTask = RP.create(runnable);
//        final ProgressHandle ph = ProgressHandleFactory.createHandle("Saving JPA File...", theTask);
//        theTask.addTaskListener(new TaskListener() {
//            @Override
//            public void taskFinished(org.openide.util.Task task) {
//                ph.finish();
//            }
//        });
//        ph.start();
//        theTask.schedule(0);
//    }
//
    @Override
    public void saveModelerFile(ModelerFile file) {
        updateJPADiagram(file);

        IModelerScene scene = file.getModelerScene();
        EntityMappings entityMappings = (EntityMappings) file.getDefinitionElement();

        entityMappings.getDefaultClass().clear();

        for (IBaseElementWidget baseElementWidget : scene.getBaseElements()) {
            if (baseElementWidget instanceof FlowNodeWidget) {
                FlowNodeWidget flowNodeWidget = (FlowNodeWidget) baseElementWidget;
                FlowNode flowNode = (FlowNode) flowNodeWidget.getBaseElementSpec();
                flowNode.setMinimized(flowNodeWidget.isMinimized());
                if (baseElementWidget instanceof JavaClassWidget) {
                    if (baseElementWidget instanceof PersistenceClassWidget) {
                        PersistenceClassWidget persistenceClassWidget = (PersistenceClassWidget) baseElementWidget;
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
//Start : IDCLASS,EMBEDDEDID
                        if (persistenceClassWidget.getBaseElementSpec() instanceof PrimaryKeyContainer) {
                            PrimaryKeyContainer pkContainerSpec = (PrimaryKeyContainer) persistenceClassWidget.getBaseElementSpec();
                            CompositePKProperty compositePKProperty = persistenceClassWidget.isCompositePKPropertyAllow();

                            if (compositePKProperty == CompositePKProperty.NONE
                                    && (pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID
                                    || pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS)) {
                                pkContainerSpec.setCompositePrimaryKeyClass(null);
                                pkContainerSpec.setCompositePrimaryKeyType(null);
                            } else if (compositePKProperty != CompositePKProperty.NONE
                                    && (pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID
                                    || pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS)) {
                                if (compositePKProperty == CompositePKProperty.AUTO_CLASS) {
                                    RelationAttributeWidget relationAttributeWidget = persistenceClassWidget.getDerivedRelationAttributeWidgets().get(0);
                                    RelationAttribute relationAttribute = (RelationAttribute) relationAttributeWidget.getBaseElementSpec();
                                    IFlowElementWidget targetElementWidget = (EntityWidget) relationAttributeWidget.getRelationFlowWidget().getTargetWidget();
                                    EntityWidget targetEntityWidget = null;
                                    if (targetElementWidget instanceof EntityWidget) {
                                        targetEntityWidget = (EntityWidget) targetElementWidget;
                                    } else if (targetElementWidget instanceof RelationAttributeWidget) {
                                        RelationAttributeWidget targetRelationAttributeWidget = (RelationAttributeWidget) targetElementWidget;
                                        targetEntityWidget = (EntityWidget) targetRelationAttributeWidget.getClassWidget();//target can be only Entity
                                    }
                                    Entity targetPKConatinerSpec = (Entity) targetEntityWidget.getBaseElementSpec();
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
                                        } else if (relationAttribute instanceof ManyToOne) {
                                            ((ManyToOne) relationAttribute).setMapsId("");
                                        }
                                    } else if (pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS
                                            && targetPKConatinerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID) {
                                        if (relationAttribute instanceof OneToOne) {
                                            ((OneToOne) relationAttribute).setMapsId(null);
                                        } else if (relationAttribute instanceof ManyToOne) {
                                            ((ManyToOne) relationAttribute).setMapsId(null);
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
                                    } else if (pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS) {
                                        idAttributeWidgets = persistenceClassWidget.getAllIdAttributeWidgets();
                                    }

                                    for (IdAttributeWidget idAttributeWidget : idAttributeWidgets) {
                                        Id idSpec = (Id) idAttributeWidget.getBaseElementSpec();
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
                                        } else if (targetElementWidget instanceof RelationAttributeWidget) {
                                            RelationAttributeWidget targetRelationAttributeWidget = (RelationAttributeWidget) targetElementWidget;
                                            targetEntityWidget = (EntityWidget) targetRelationAttributeWidget.getClassWidget();//target can be only Entity
                                        }
                                        targetEntitySpec = (Entity) targetEntityWidget.getBaseElementSpec();
                                        List<IdAttributeWidget> targetIdAttributeWidgets = targetEntityWidget.getAllIdAttributeWidgets();
                                        DefaultAttribute attribute = new DefaultAttribute();
                                        if (targetIdAttributeWidgets.size() == 1) {
                                            Id idSpec = (Id) targetIdAttributeWidgets.get(0).getBaseElementSpec();
                                            attribute.setAttributeType(idSpec.getAttributeType());
                                            attribute.setName(relationAttributeSpec.getName());// matches name of @Id Relation attribute
                                        } else {
                                            attribute.setAttributeType(targetEntitySpec.getCompositePrimaryKeyClass());
                                            attribute.setName(relationAttributeSpec.getName());// matches name of @Id Relation attribute

                                            if (targetEntitySpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS) {
                                            } else if (targetEntitySpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID) {
                                            } else {
                                                throw new UnsupportedOperationException("Not Supported Currently");
                                            }
                                        }
                                        _class.addAttribute(attribute);
                                        //Start : if dependent class is Embedded that add @MapsId to Derived PK
                                        if (pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.IDCLASS) {
                                            if (relationAttributeSpec instanceof OneToOne) {
                                                ((OneToOne) relationAttributeSpec).setMapsId(null);
                                            } else if (relationAttributeSpec instanceof ManyToOne) {
                                                ((ManyToOne) relationAttributeSpec).setMapsId(null);
                                            }
                                        } else if (pkContainerSpec.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID) {
                                            if (relationAttributeSpec instanceof OneToOne) {
                                                ((OneToOne) relationAttributeSpec).setMapsId(attribute.getName());
                                            } else if (relationAttributeSpec instanceof ManyToOne) {
                                                ((ManyToOne) relationAttributeSpec).setMapsId(attribute.getName());
                                            }
                                        }
                                        //End : if dependent class is Embedded that add @MapsId to Derived PK

                                    }
                                }
                            }
                        }
//End : IDCLASS,EMBEDDEDID

                        PersistenceClassWidget entityWidget = (PersistenceClassWidget) baseElementWidget;
                        //Entity entity = (Entity) entityWidget.getBaseElementSpec();
                        entityWidget.getSingleValueEmbeddedAttributeWidgets().stream().map((embeddedAttributeWidget) -> (Embedded) embeddedAttributeWidget.getBaseElementSpec()).forEach((embedded) -> {
                            EmbeddableWidget connectedEmbeddableWidget = (EmbeddableWidget) scene.findBaseElement(embedded.getConnectedClassId());
                            JavaClass connectedEmbeddable = (JavaClass) connectedEmbeddableWidget.getBaseElementSpec();
                            embedded.setAttributeType(connectedEmbeddable.getClazz());
                        });
                        entityWidget.getMultiValueEmbeddedAttributeWidgets().stream().map((embeddedAttributeWidget) -> (ElementCollection) embeddedAttributeWidget.getBaseElementSpec()).forEach((elementCollection) -> {
                            EmbeddableWidget connectedEmbeddableWidget = (EmbeddableWidget) scene.findBaseElement(elementCollection.getConnectedClassId());
                            JavaClass connectedEmbeddable = (JavaClass) connectedEmbeddableWidget.getBaseElementSpec();
                            elementCollection.setAttributeType(connectedEmbeddable.getClazz());
                        });
                        entityWidget.getOneToOneRelationAttributeWidgets().stream().forEach((otoRelationAttributeWidget) -> {
                            OneToOne oneToOne = (OneToOne) otoRelationAttributeWidget.getBaseElementSpec();
                            PersistenceClassWidget connectedEntityWidget = (PersistenceClassWidget) scene.findBaseElement(oneToOne.getConnectedEntityId());
                            JavaClass connectedEntity = (JavaClass) connectedEntityWidget.getBaseElementSpec();
                            oneToOne.setTargetEntity(connectedEntity.getClazz());
                            String connectedAttributeId = oneToOne.getConnectedAttributeId();
                            if (!otoRelationAttributeWidget.isOwner() && connectedAttributeId != null) {
                                RelationAttributeWidget connectedAttributeWidget = connectedEntityWidget.findRelationAttributeWidget(connectedAttributeId, OTORelationAttributeWidget.class);
                                RelationAttribute relationAttribute = (RelationAttribute) connectedAttributeWidget.getBaseElementSpec();
                                oneToOne.setMappedBy(relationAttribute.getName());
                            }
                        });
                        entityWidget.getOneToManyRelationAttributeWidgets().stream().forEach((otmRelationAttributeWidget) -> {
                            OneToMany oneToMany = (OneToMany) otmRelationAttributeWidget.getBaseElementSpec();
                            PersistenceClassWidget connectedEntityWidget = (PersistenceClassWidget) scene.findBaseElement(oneToMany.getConnectedEntityId());
                            JavaClass connectedEntity = (JavaClass) connectedEntityWidget.getBaseElementSpec();
                            oneToMany.setTargetEntity(connectedEntity.getClazz());
                            String connectedAttributeId = oneToMany.getConnectedAttributeId();
                            if (!otmRelationAttributeWidget.isOwner() && connectedAttributeId != null) {
                                RelationAttributeWidget connectedAttributeWidget = connectedEntityWidget.findRelationAttributeWidget(connectedAttributeId, MTORelationAttributeWidget.class);
                                RelationAttribute relationAttribute = (RelationAttribute) connectedAttributeWidget.getBaseElementSpec();
                                oneToMany.setMappedBy(relationAttribute.getName());
                            }
                        });
                        entityWidget.getManyToOneRelationAttributeWidgets().stream().map((mtoRelationAttributeWidget) -> (ManyToOne) mtoRelationAttributeWidget.getBaseElementSpec()).forEach((manyToOne) -> {
                            PersistenceClassWidget connectedEntityWidget = (PersistenceClassWidget) scene.findBaseElement(manyToOne.getConnectedEntityId());
                            JavaClass connectedEntity = (JavaClass) connectedEntityWidget.getBaseElementSpec();
                            manyToOne.setTargetEntity(connectedEntity.getClazz());
                            //Always Owner no need to set mappedBy
                        });
                        entityWidget.getManyToManyRelationAttributeWidgets().stream().forEach((mtmRelationAttributeWidget) -> {
                            ManyToMany manyToMany = (ManyToMany) mtmRelationAttributeWidget.getBaseElementSpec();
                            PersistenceClassWidget connectedEntityWidget = (PersistenceClassWidget) scene.findBaseElement(manyToMany.getConnectedEntityId());
                            JavaClass connectedEntity = (JavaClass) connectedEntityWidget.getBaseElementSpec();
                            manyToMany.setTargetEntity(connectedEntity.getClazz());
                            String connectedAttributeId = manyToMany.getConnectedAttributeId();
                            if (!mtmRelationAttributeWidget.isOwner() && connectedAttributeId != null) {
                                RelationAttributeWidget connectedAttributeWidget = connectedEntityWidget.findRelationAttributeWidget(connectedAttributeId, MTMRelationAttributeWidget.class);
                                RelationAttribute relationAttribute = (RelationAttribute) connectedAttributeWidget.getBaseElementSpec();
                                manyToMany.setMappedBy(relationAttribute.getName());
                            }
                        });
                    }
                }
            }

        }
        saveFile(entityMappings, file.getFile());
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
            StringWriter sw = new StringWriter();
            MODELER_MARSHALLER.marshal(entityMappings, sw);

            System.out.println("sw :");
        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
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

    public static void createNewModelerFile(EntityMappings entityMappingsSpec, FileObject parentFileObject, String fileName, boolean autoOpen) {
        File jpaFile = null;
        try {
            jpaFile = new File(parentFileObject.getPath() + File.separator + getFileName(fileName, null, parentFileObject) + ".jpa");
            if (!jpaFile.exists()) {
                jpaFile.createNewFile();
            }
            saveFile(entityMappingsSpec, jpaFile);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (autoOpen) {
            FileObject jpaFileObject = FileUtil.toFileObject(jpaFile);
            try {
                JPAFileActionListener actionListener = new JPAFileActionListener((JPAFileDataObject) DataObject.find(jpaFileObject));
                actionListener.actionPerformed(null);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
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
            shape.setId(((IBaseElementWidget) nodeWidget).getId() + "_gui");
//            if (nodeWidget.getLabelManager() != null && nodeWidget.getLabelManager().isVisible() && nodeWidget.getLabelManager().getLabel() != null && !nodeWidget.getLabelManager().getLabel().trim().isEmpty()) {
//                Rectangle bound = nodeWidget.getLabelManager().getLabelWidget().getParentWidget().getPreferredBounds();
//                bound = nodeWidget.getLabelManager().getLabelWidget().getParentWidget().convertLocalToScene(bound);
//
//                Rectangle rec_label = new Rectangle(bound.x, bound.y, (int) bound.getWidth(), (int) bound.getHeight());
//
////                JPALabel label = new JPALabel();
////                label.setBounds(new Bounds(rec_label));
////                shape.setJPALabel(label);
//            }
            plane.addDiagramElement(shape);

//            ShapeDesign shapeDesign = null;// JPAShapeDesign XML Location Change Here
//            if (nodeWidget instanceof FlowNodeWidget) {
//                TFlowNode flowNode = (TFlowNode) ((FlowNodeWidget) nodeWidget).getBaseElementSpec();
//                if (flowNode.getExtensionElements() == null) {
//                    flowNode.setExtensionElements(new TExtensionElements());
//                }
//                TExtensionElements extensionElements = flowNode.getExtensionElements();
//                for (Object obj : extensionElements.getAny()) {
//                    if (obj instanceof Element) { //first time save
//                        Element element = (Element) obj;
//                        if ("ShapeDesign".equals(element.getNodeName())) {
//                            shapeDesign = getJPAShapeDesign(nodeWidget);
//                            extensionElements.getAny().remove(obj);
//                            extensionElements.getAny().add(shapeDesign);
//                            break;
//                        }
//                    } else if (obj instanceof ShapeDesign) {
//                        shapeDesign = getJPAShapeDesign(nodeWidget);
//                        extensionElements.getAny().remove(obj);
//                        extensionElements.getAny().add(shapeDesign);
//                        break;
//                    }
//                }
//            }
//            if (shapeDesign == null) {
//                if (nodeWidget instanceof FlowNodeWidget) {
//                    TFlowNode flowNode = (TFlowNode) ((FlowNodeWidget) nodeWidget).getBaseElementSpec();
//                    TExtensionElements extensionElements = flowNode.getExtensionElements();
//                    shapeDesign = getJPAShapeDesign(nodeWidget);
//                    extensionElements.getAny().add(shapeDesign);
//                }
//            }
//            shape.setShapeDesign(getJPAShapeDesign(nodeWidget));
//            if (nodeWidget instanceof SubProcessWidget) {
//                SubProcessWidget subProcessWidget = (SubProcessWidget) nodeWidget;
//                for (FlowElementWidget flowElementChildrenWidget : subProcessWidget.getFlowElements()) {
//                    updateDiagramFlowElement(plane, (Widget) flowElementChildrenWidget);
//                }
//            }
        } else if (widget instanceof EdgeWidget) {
//            EdgeWidget edgeWidget = (EdgeWidget) widget;
//            JPAEdge edge = new JPAEdge();
//            for (java.awt.Point point : edgeWidget.getControlPoints()) {
//                edge.addWaypoint(point);
//            }
//            edge.setJPAElement(((BaseElementWidget) edgeWidget).getId());
//            edge.setId(((BaseElementWidget) edgeWidget).getId() + "_gui");
//
//            if (widget instanceof SequenceFlowWidget) {
//                if (edgeWidget.getLabelManager() != null && edgeWidget.getLabelManager().isVisible() && edgeWidget.getLabelManager().getLabel() != null && !edgeWidget.getLabelManager().getLabel().trim().isEmpty()) {
//                    Rectangle bound = edgeWidget.getLabelManager().getLabelWidget().getParentWidget().getPreferredBounds();
//                    bound = edgeWidget.getLabelManager().getLabelWidget().getParentWidget().convertLocalToScene(bound);
//
//                    Rectangle rec = new Rectangle(bound.x, bound.y, (int) bound.getWidth(), (int) bound.getHeight());
//
//                    JPALabel label = new JPALabel();
//                    label.setBounds(new Bounds(rec));
//                    edge.setJPALabel(label);
//                }
//            }
//            plane.addDiagramElement(edge);

        } else {
            throw new InvalidElmentException("Invalid JPA Element");
        }

    }

    public static void updateJPADiagram(ModelerFile file) {
        Plane plane = ((Diagram) file.getDiagramElement()).getJPAPlane();
        plane.getDiagramElement().clear();
        IModelerScene processScene = file.getModelerScene();
        processScene.getBaseElements().stream().forEach((flowElementWidget) -> {
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
    public IPinWidget attachPinWidget(IModelerScene scene, INodeWidget nodeWidget, PinWidgetInfo widgetInfo) {
        IPinWidget widget = null;
        if (widgetInfo.getDocumentId().equals(IdAttributeWidget.class.getSimpleName())) {
            widget = new IdAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(EmbeddedIdAttributeWidget.class.getSimpleName())) {
            widget = new EmbeddedIdAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(BasicAttributeWidget.class.getSimpleName())) {
            widget = new BasicAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(BasicCollectionAttributeWidget.class.getSimpleName())) {
            widget = new BasicCollectionAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(TransientAttributeWidget.class.getSimpleName())) {
            widget = new TransientAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(VersionAttributeWidget.class.getSimpleName())) {
            widget = new VersionAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(OTORelationAttributeWidget.class.getSimpleName())) {
            widget = new OTORelationAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(OTMRelationAttributeWidget.class.getSimpleName())) {
            widget = new OTMRelationAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(MTORelationAttributeWidget.class.getSimpleName())) {
            widget = new MTORelationAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(MTMRelationAttributeWidget.class.getSimpleName())) {
            widget = new MTMRelationAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(SingleValueEmbeddedAttributeWidget.class.getSimpleName())) {
            widget = new SingleValueEmbeddedAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals(MultiValueEmbeddedAttributeWidget.class.getSimpleName())) {
            widget = new MultiValueEmbeddedAttributeWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else if (widgetInfo.getDocumentId().equals("INTERNAL")) {
            widget = null; //widget = new PinWidget(scene, (IPNodeWidget) nodeWidget, widgetInfo);
        } else {
            throw new InvalidElmentException("Invalid JPA Pin Element");
        }
//        ((PNodeWidget) scene.findWidget(nodeWidgetInfo)).attachPinWidget(widget);
        return widget;
    }

    @Override
    public void dettachEdgeSourceAnchor(IModelerScene scene, IEdgeWidget edgeWidget, IPinWidget sourcePinWidget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dettachEdgeTargetAnchor(IModelerScene scene, IEdgeWidget edgeWidget, IPinWidget targetPinWidget) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void attachEdgeSourceAnchor(IModelerScene scene, IEdgeWidget edgeWidget, IPinWidget sourcePinWidget) {
        edgeWidget.setSourceAnchor(((IPModelerScene) scene).getPinAnchor(sourcePinWidget));

    }

    @Override
    public void attachEdgeSourceAnchor(IModelerScene scene, IEdgeWidget edgeWidget, INodeWidget sourceNodeWidget) { //BUG : Remove this method
        edgeWidget.setSourceAnchor(((IPNodeWidget) sourceNodeWidget).getNodeAnchor());
    }

    @Override
    public void attachEdgeTargetAnchor(IModelerScene scene, IEdgeWidget edgeWidget, IPinWidget targetPinWidget) {
        edgeWidget.setTargetAnchor(((IPModelerScene) scene).getPinAnchor(targetPinWidget));
    }

    @Override
    public void attachEdgeTargetAnchor(IModelerScene scene, IEdgeWidget edgeWidget, INodeWidget targetNodeWidget) { //BUG : Remove this method
        edgeWidget.setTargetAnchor(((IPNodeWidget) targetNodeWidget).getNodeAnchor());
    }

    @Override
    public IEdgeWidget attachEdgeWidget(IModelerScene scene, EdgeWidgetInfo widgetInfo) {
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
    public INodeWidget attachNodeWidget(IModelerScene scene, NodeWidgetInfo widgetInfo) {
        IFlowNodeWidget widget = null;
        IModelerDocument modelerDocument = widgetInfo.getModelerDocument();
        switch (modelerDocument.getId()) {
            case "Entity":
                widget = new EntityWidget(scene, widgetInfo);
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

    public IEdgeWidget getEdgeWidget(IModelerScene scene, EdgeWidgetInfo edgeWidgetInfo) {
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
            RelationAttributeWidget relationAttributeWidget = null;
            if (relationFlowWidget instanceof OTORelationFlowWidget) {
                OTORelationFlowWidget otoRelationFlowWidget = (OTORelationFlowWidget) relationFlowWidget;
                OTORelationAttributeWidget otoRelationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    otoRelationAttributeWidget = sourcePersistenceWidget.addNewOneToOneRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()));
                } else {
                    otoRelationAttributeWidget = (OTORelationAttributeWidget) sourceAttributeWidget;
                }
                if (otoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKUOTO_RELATION") || otoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKBOTO_RELATION")) {
                    ((OneToOne) otoRelationAttributeWidget.getBaseElementSpec()).setPrimaryKey(Boolean.TRUE);
                }
                otoRelationAttributeWidget.setOneToOneRelationFlowWidget(otoRelationFlowWidget);
                relationAttributeWidget = otoRelationAttributeWidget;
            } else if (relationFlowWidget instanceof OTMRelationFlowWidget) {
                OTMRelationAttributeWidget otmRelationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    otmRelationAttributeWidget = sourcePersistenceWidget.addNewOneToManyRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()));
                } else {
                    otmRelationAttributeWidget = (OTMRelationAttributeWidget) sourceAttributeWidget;
                }
                otmRelationAttributeWidget.setHierarchicalRelationFlowWidget((OTMRelationFlowWidget) relationFlowWidget);
                relationAttributeWidget = otmRelationAttributeWidget;
            } else if (relationFlowWidget instanceof MTORelationFlowWidget) {
                MTORelationFlowWidget mtoRelationFlowWidget = (MTORelationFlowWidget) relationFlowWidget;
                MTORelationAttributeWidget mtoRelationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    mtoRelationAttributeWidget = sourcePersistenceWidget.addNewManyToOneRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()));
                } else {
                    mtoRelationAttributeWidget = (MTORelationAttributeWidget) sourceAttributeWidget;
                }
                if (mtoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKUMTO_RELATION") || mtoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKBMTO_RELATION")) {
                    ((ManyToOne) mtoRelationAttributeWidget.getBaseElementSpec()).setPrimaryKey(Boolean.TRUE);
                }
                mtoRelationAttributeWidget.setManyToOneRelationFlowWidget(mtoRelationFlowWidget);
                relationAttributeWidget = mtoRelationAttributeWidget;

            } else if (relationFlowWidget instanceof MTMRelationFlowWidget) {
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

            relationFlowWidget.setSourceRelationAttributeWidget(relationAttributeWidget);
            relationAttributeWidget.setOwner(true);
            return relationAttributeWidget.getPinWidgetInfo();

        } else if (edgeWidget instanceof GeneralizationFlowWidget) {
            JavaClassWidget sourceJavaClassWidget = (JavaClassWidget) sourceNodeWidget;
            JavaClass sourceJavaClass = (JavaClass) sourceJavaClassWidget.getBaseElementSpec();
            JavaClassWidget targetJavaClassWidget = (JavaClassWidget) targetNodeWidget;
            JavaClass targetJavaClass = (JavaClass) targetJavaClassWidget.getBaseElementSpec();
            GeneralizationFlowWidget generalizationFlowWidget = (GeneralizationFlowWidget) edgeWidget;
            sourceJavaClass.addSuperclass(targetJavaClass);
            generalizationFlowWidget.setSubclassWidget(sourceJavaClassWidget);
            generalizationFlowWidget.setSuperclassWidget(targetJavaClassWidget);
            return sourceJavaClassWidget.getInternalPinWidgetInfo();
        } else if (edgeWidget instanceof EmbeddableFlowWidget) {
            PersistenceClassWidget sourcePersistenceWidget = (PersistenceClassWidget) sourceNodeWidget;
//            JavaClass sourceJavaClass = (JavaClass) sourcePersistenceWidget.getBaseElementSpec();
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
            } else if (edgeWidget instanceof MultiValueEmbeddableFlowWidget) {
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
            embeddableFlowWidget.setSourceEmbeddedAttributeWidget(embeddedAttributeWidget);
            return embeddedAttributeWidget.getPinWidgetInfo();
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
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
            } else if (edgeWidget instanceof Bidirectional) {
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

                } else if (edgeWidget instanceof BMTORelationFlowWidget) {
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
                } else if (edgeWidget instanceof BMTMRelationFlowWidget) {
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
            } else {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        } else if (edgeWidget instanceof Direction && (sourceNodeWidget instanceof MappedSuperclassWidget || sourceNodeWidget instanceof EmbeddableWidget) && targetNodeWidget instanceof EntityWidget) {
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
        } else if (edgeWidget instanceof GeneralizationFlowWidget && sourceNodeWidget instanceof JavaClassWidget && targetNodeWidget instanceof JavaClassWidget) {
            JavaClassWidget targetJavaClassWidget = (JavaClassWidget) targetNodeWidget;
            return targetJavaClassWidget.getInternalPinWidgetInfo();
        } else if (edgeWidget instanceof EmbeddableFlowWidget && sourceNodeWidget instanceof PersistenceClassWidget && targetNodeWidget instanceof EmbeddableWidget) {
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

    public static ComboBoxPropertySupport getAccessTypeProperty(IModelerScene modelerScene, final AccessTypeHandler accessTypeHandlerSpec) {
        ComboBoxListener<AccessType> comboBoxListener = new ComboBoxListener<AccessType>() {
            @Override
            public void setItem(ComboBoxValue<AccessType> value) {
                accessTypeHandlerSpec.setAccess(value.getValue());
            }

            @Override
            public ComboBoxValue<AccessType> getItem() {
                if (accessTypeHandlerSpec.getAccess() != null) {
                    return new ComboBoxValue<AccessType>(accessTypeHandlerSpec.getAccess(), accessTypeHandlerSpec.getAccess().value());
                } else {
                    return new ComboBoxValue<AccessType>(AccessType.getDefault(), AccessType.getDefault().value());
                }
            }

            @Override
            public List<ComboBoxValue<AccessType>> getItemList() {
                ComboBoxValue<AccessType>[] values;
                values = new ComboBoxValue[]{
                    new ComboBoxValue<AccessType>(FIELD, "Field"),
                    new ComboBoxValue<AccessType>(PROPERTY, "Property")};
                return Arrays.asList(values);
            }

            @Override
            public String getDefaultText() {
                return "Field";
            }
        };
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), "accessType", "Access Type", "", comboBoxListener);
    }

    public static ComboBoxPropertySupport getCollectionTypeProperty(IModelerScene modelerScene, final CollectionTypeHandler colSpec) {
        EntityMappings em = (EntityMappings) modelerScene.getBaseElementSpec();
        ModelerFile modelerFile = modelerScene.getModelerFile();
        ComboBoxListener<String> comboBoxListener = new ComboBoxListener<String>() {
            @Override
            public void setItem(ComboBoxValue<String> value) {
                colSpec.setCollectionType(value.getValue());
                em.getCache().addCollectionClass(value.getValue());
            }

            @Override
            public ComboBoxValue<String> getItem() {
                return new ComboBoxValue(colSpec.getCollectionType(), colSpec.getCollectionType().substring(colSpec.getCollectionType().lastIndexOf('.') + 1));
            }

            @Override
            public List<ComboBoxValue<String>> getItemList() {
                List<ComboBoxValue<String>> comboBoxValues = new ArrayList<>();
                em.getCache().getCollectionClasses().stream().forEach((collection) -> {
                    Class _class;
                    try {
                        _class = Class.forName(collection);
                        comboBoxValues.add(new ComboBoxValue(_class.getName(), _class.getSimpleName()));
                    } catch (ClassNotFoundException ex) {
                         comboBoxValues.add(new ComboBoxValue(collection, collection + "(Not Exist)"));
                    }
                });
                return comboBoxValues;
            }

            @Override
            public String getDefaultText() {
                return "";
            }

            @Override
            public ActionHandler getActionHandler() {
                return ActionHandler.getInstance(() -> {
                    String collectionType = NBModelerUtil.browseClass(modelerFile);
                    return new ComboBoxValue<String>(collectionType, collectionType.substring(collectionType.lastIndexOf('.') + 1));
                })
                        .afterCreation(e ->  em.getCache().addCollectionClass(e.getValue()))
                        .afterDeletion(e -> em.getCache().getCollectionClasses().remove(e.getValue()))
                        .beforeDeletion(() -> JOptionPane.showConfirmDialog(null, "Are you sue you want to delete this collection class ?", "Delete Collection Class", JOptionPane.OK_CANCEL_OPTION));
            }
        };
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), "collectionType", "Collection Type", "", comboBoxListener);
    }

    public static ComboBoxPropertySupport getFetchTypeProperty(IModelerScene modelerScene, final FetchTypeHandler fetchTypeHandlerSpec) {
        ComboBoxListener comboBoxListener = new ComboBoxListener() {
            @Override
            public void setItem(ComboBoxValue value) {
                fetchTypeHandlerSpec.setFetch((FetchType) value.getValue());
            }

            @Override
            public ComboBoxValue getItem() {
                if (fetchTypeHandlerSpec.getFetch() == FetchType.EAGER) {
                    return new ComboBoxValue(FetchType.EAGER, "Eager");
                } else if (fetchTypeHandlerSpec.getFetch() == FetchType.LAZY) {
                    return new ComboBoxValue(FetchType.LAZY, "Lazy");
                } else {
                    return new ComboBoxValue(null, "Default(Eager)");
                }
            }

            @Override
            public List<ComboBoxValue> getItemList() {
                ComboBoxValue[] values = new ComboBoxValue[]{
                    new ComboBoxValue(null, "Default(Eager)"),
                    new ComboBoxValue(FetchType.EAGER, "Eager"),
                    new ComboBoxValue(FetchType.LAZY, "Lazy")};
                return Arrays.asList(values);
            }

            @Override
            public String getDefaultText() {
                return "";
            }

            @Override
            public ActionHandler getActionHandler() {
                return null;
            }
        };
        return new ComboBoxPropertySupport(modelerScene.getModelerFile(), "fetchType", "Fetch Type", "", comboBoxListener);
    }

    public static PropertySupport getJoinColumnsProperty(String id, String name, String desc, IModelerScene modelerScene, final List<JoinColumn> joinColumnsSpec) {
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No JoinColumns exist", "One JoinColumn exist", "JoinColumns exist"});

        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Column Name", false, String.class));
        columns.add(new Column("Referenced Column Name", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new JoinColumnPanel());

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = joinColumnsSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<JoinColumn> joinColumns = joinColumnsSpec;
                List<Object[]> data_local = new LinkedList<Object[]>();
                Iterator<JoinColumn> itr = joinColumns.iterator();
                while (itr.hasNext()) {
                    JoinColumn joinColumn = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = joinColumn;
                    row[1] = joinColumn.getName();
                    row[2] = joinColumn.getReferencedColumnName();
                    data_local.add(row);
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                joinColumnsSpec.clear();
                for (Object[] row : data) {
                    joinColumnsSpec.add((JoinColumn) row[0]);
                }
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getResultSetMappingsProperty(String id, String name, String desc, IModelerScene modelerScene, final Entity entity) {
        final Set<SqlResultSetMapping> sqlResultSetMappingSpec = entity.getSqlResultSetMapping();
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);

        attributeEntity.setCountDisplay(new String[]{"No ResultSet Mappings", "One ResultSet Mapping", " ResultSet Mappings"});
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("ResultSet Name", true, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new ResultSetMappingsPanel(modelerScene.getModelerFile(), entity));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data = new LinkedList<Object[]>();
            int count;

            @Override
            public void initCount() {
                count = sqlResultSetMappingSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<Object[]> data_local = new LinkedList<Object[]>();
                for (SqlResultSetMapping resultSetMapping : sqlResultSetMappingSpec) {
                    Object[] row = new Object[2];
                    row[0] = resultSetMapping;
                    row[1] = resultSetMapping.getName();
                    data_local.add(row);
                }
//                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                sqlResultSetMappingSpec.clear();
                for (Object[] row : data) {
                    SqlResultSetMapping resultSetMapping = (SqlResultSetMapping) row[0];
                    resultSetMapping.setEntity(entity);
                    sqlResultSetMappingSpec.add(resultSetMapping);
                }
                initData();
            }
        });
        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getNamedStoredProcedureQueryProperty(String id, String name, String desc, IModelerScene modelerScene, Entity entity) {
        final List<NamedStoredProcedureQuery> namedStoredProcedureQueriesSpec = entity.getNamedStoredProcedureQuery();
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No NamedStoredProcedureQueries exist", "One NamedStoredProcedureQuery exist", "NamedStoredProcedureQueries exist"});

        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("ProcedureName", false, String.class));
        columns.add(new Column("Parameters", false, Integer.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedStoredProcedureQueryPanel(modelerScene.getModelerFile(), entity));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = namedStoredProcedureQueriesSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<NamedStoredProcedureQuery> joinColumns = namedStoredProcedureQueriesSpec;
                List<Object[]> data_local = new LinkedList<Object[]>();
                Iterator<NamedStoredProcedureQuery> itr = joinColumns.iterator();
                while (itr.hasNext()) {
                    NamedStoredProcedureQuery namedStoredProcedureQuery = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = namedStoredProcedureQuery;
                    row[1] = namedStoredProcedureQuery.getName();
                    row[2] = namedStoredProcedureQuery.getProcedureName();
                    row[3] = namedStoredProcedureQuery.getParameter().size();
                    data_local.add(row);
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                namedStoredProcedureQueriesSpec.clear();
                for (Object[] row : data) {
                    namedStoredProcedureQueriesSpec.add((NamedStoredProcedureQuery) row[0]);
                }
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getNamedQueryProperty(String id, String name, String desc, IModelerScene modelerScene, final List<NamedQuery> namedQueriesSpec) {
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No NamedQueries exist", "One NamedQuery exist", "NamedQueries exist"});

        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Query", false, String.class));
        columns.add(new Column("Lock Mode Type", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedQueryPanel());

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = namedQueriesSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<NamedQuery> joinColumns = namedQueriesSpec;
                List<Object[]> data_local = new LinkedList<Object[]>();
                Iterator<NamedQuery> itr = joinColumns.iterator();
                while (itr.hasNext()) {
                    NamedQuery namedQuery = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = namedQuery;
                    row[1] = namedQuery.getName();
                    row[2] = namedQuery.getQuery();
                    row[3] = namedQuery.getLockMode();
                    data_local.add(row);
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                namedQueriesSpec.clear();
                for (Object[] row : data) {
                    namedQueriesSpec.add((NamedQuery) row[0]);
                }
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getNamedEntityGraphProperty(String id, String name, String desc, final EntityWidget entityWidget) {
        IModelerScene modelerScene = entityWidget.getModelerScene();
        final List<NamedEntityGraph> entityGraphsSpec = ((Entity) entityWidget.getBaseElementSpec()).getNamedEntityGraph();
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No EntityGraphs exist", "One EntityGraph exist", "EntityGraphs exist"});

        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedEntityGraphPanel(entityWidget));

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = entityGraphsSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<NamedEntityGraph> entityGraphList = entityGraphsSpec;
                List<Object[]> data_local = new LinkedList<Object[]>();
                Iterator<NamedEntityGraph> itr = entityGraphList.iterator();
                while (itr.hasNext()) {
                    NamedEntityGraph entityGraph = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = entityGraph;
                    row[1] = entityGraph.getName();
                    data_local.add(row);
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                entityGraphsSpec.clear();
                for (Object[] row : data) {
                    entityGraphsSpec.add((NamedEntityGraph) row[0]);
                }
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static PropertySupport getNamedNativeQueryProperty(String id, String name, String desc, IModelerScene modelerScene, final List<NamedNativeQuery> namedNativeQueriesSpec) {
        final NAttributeEntity attributeEntity = new NAttributeEntity(id, name, desc);
        attributeEntity.setCountDisplay(new String[]{"No Named Native Queries exist", "One Named Native Query exist", "Named Native Queries exist"});

        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("OBJECT", false, true, Object.class));
        columns.add(new Column("Name", false, String.class));
        columns.add(new Column("Query", false, String.class));
        columns.add(new Column("Result Class", false, String.class));
        columns.add(new Column("ResultSet Mapping", false, String.class));
        attributeEntity.setColumns(columns);
        attributeEntity.setCustomDialog(new NamedNativeQueryPanel());

        attributeEntity.setTableDataListener(new NEntityDataListener() {
            List<Object[]> data;
            int count;

            @Override
            public void initCount() {
                count = namedNativeQueriesSpec.size();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public void initData() {
                List<NamedNativeQuery> namedNativeQueries = namedNativeQueriesSpec;
                List<Object[]> data_local = new LinkedList<Object[]>();
                Iterator<NamedNativeQuery> itr = namedNativeQueries.iterator();
                while (itr.hasNext()) {
                    NamedNativeQuery namedNativeQuery = itr.next();
                    Object[] row = new Object[attributeEntity.getColumns().size()];
                    row[0] = namedNativeQuery;
                    row[1] = namedNativeQuery.getName();
                    row[2] = namedNativeQuery.getQuery();
                    row[3] = namedNativeQuery.getResultClass();
                    row[4] = namedNativeQuery.getResultSetMapping();
                    data_local.add(row);
                }
                this.data = data_local;
            }

            @Override
            public List<Object[]> getData() {
                return data;
            }

            @Override
            public void setData(List<Object[]> data) {
                namedNativeQueriesSpec.clear();
                for (Object[] row : data) {
                    namedNativeQueriesSpec.add((NamedNativeQuery) row[0]);
                }
                this.data = data;
            }

        });

        return new NEntityPropertySupport(modelerScene.getModelerFile(), attributeEntity);
    }

    public static void getJaxbVarTypeProperty(final ElementPropertySet set, final AttributeWidget attributeWidget, final JaxbVariableTypeHandler varHandlerSpec) {

        final List<JaxbVariableType> jaxbVariableList = varHandlerSpec.getJaxbVariableList();

        ComboBoxListener comboBoxListener = new ComboBoxListener<JaxbVariableType>() {
            @Override
            public void setItem(ComboBoxValue<JaxbVariableType> value) {
                varHandlerSpec.setJaxbVariableType(value.getValue());
                varHandlerSpec.setJaxbXmlAttribute(null);
                varHandlerSpec.setJaxbXmlElement(null);
                varHandlerSpec.setJaxbXmlElementList(null);
                if (value.getValue() == JaxbVariableType.XML_ATTRIBUTE || value.getValue() == JaxbVariableType.XML_LIST_ATTRIBUTE) {
                    varHandlerSpec.setJaxbXmlAttribute(new JaxbXmlAttribute());
                    set.replacePropertySet(attributeWidget, varHandlerSpec.getJaxbXmlAttribute(), attributeWidget.getPropertyChangeListeners());
                } else if (value.getValue() == JaxbVariableType.XML_ELEMENT || value.getValue() == JaxbVariableType.XML_LIST_ELEMENT) {
                    varHandlerSpec.setJaxbXmlElement(new JaxbXmlElement());
                    set.replacePropertySet(attributeWidget, varHandlerSpec.getJaxbXmlElement(), attributeWidget.getPropertyChangeListeners());
                } else if (value.getValue() == JaxbVariableType.XML_ELEMENTS) {
                    varHandlerSpec.setJaxbXmlElementList(new ArrayList<JaxbXmlElement>());
//                     set.createPropertySet( attributeWidget , varHandlerSpec.get(), attributeWidget.getPropertyChangeListeners());
                }
                attributeWidget.refreshProperties();
            }

            @Override
            public ComboBoxValue<JaxbVariableType> getItem() {
                if (varHandlerSpec.getJaxbVariableType() == null) {
                    if (jaxbVariableList != null) {
                        return new ComboBoxValue<JaxbVariableType>(XML_ELEMENT, "Default(Element)");
                    } else {
                        return new ComboBoxValue<JaxbVariableType>(XML_TRANSIENT, XML_TRANSIENT.getDisplayText());
                    }
                } else {
                    return new ComboBoxValue<JaxbVariableType>(varHandlerSpec.getJaxbVariableType(), varHandlerSpec.getJaxbVariableType().getDisplayText());
                }
            }

            @Override
            public List<ComboBoxValue<JaxbVariableType>> getItemList() {
                List<ComboBoxValue<JaxbVariableType>> values = new ArrayList<ComboBoxValue<JaxbVariableType>>();
                if (jaxbVariableList != null) {
                    values.add(new ComboBoxValue<JaxbVariableType>(XML_ELEMENT, "Default(Element)"));
                    jaxbVariableList.stream().forEach((variableType) -> {
                        values.add(new ComboBoxValue<JaxbVariableType>(variableType, variableType.getDisplayText()));
                    });
                } else {
                    values.add(new ComboBoxValue<JaxbVariableType>(XML_TRANSIENT, XML_TRANSIENT.getDisplayText()));
                }
                return values;
            }

            @Override
            public String getDefaultText() {
                if (jaxbVariableList != null) {
                    return "Default(Element)";
                } else {
                    return JaxbVariableType.XML_TRANSIENT.getDisplayText();
                }
            }

            @Override
            public ActionHandler getActionHandler() {
                return null;
            }
        };
        if (varHandlerSpec.getJaxbVariableType() == JaxbVariableType.XML_ATTRIBUTE) {
            set.replacePropertySet(attributeWidget, varHandlerSpec.getJaxbXmlAttribute(), attributeWidget.getPropertyChangeListeners());
        } else if (varHandlerSpec.getJaxbVariableType() == JaxbVariableType.XML_ELEMENT) {
            set.replacePropertySet(attributeWidget, varHandlerSpec.getJaxbXmlElement(), attributeWidget.getPropertyChangeListeners());
        }
        set.put("JAXB_PROP", new ComboBoxPropertySupport(attributeWidget.getModelerScene().getModelerFile(), "jaxbVariableType", "Variable Type", "", comboBoxListener, "root.jaxbSupport==true", varHandlerSpec));

    }

    public static void initEntityModel(javax.swing.JComboBox entity_ComboBox, EntityMappings entityMappings) {
        entity_ComboBox.removeAllItems();
        entity_ComboBox.addItem(new ComboBoxValue(null, ""));
        entityMappings.getEntity().stream().forEach((entity) -> {
            entity_ComboBox.addItem(new ComboBoxValue(entity, entity.getClazz()));
        });
    }

}
