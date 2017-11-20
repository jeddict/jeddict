/**
 * Copyright [2016] Gaurav Gupta
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.widget.Widget;
import static org.netbeans.jcode.core.util.StringHelper.getNext;
import org.netbeans.jpa.modeler._import.javaclass.JCREProcessor;
import org.netbeans.jpa.modeler.collaborate.issues.ExceptionUtils;
import org.netbeans.jpa.modeler.core.widget.EmbeddableWidget;
import org.netbeans.jpa.modeler.core.widget.EntityWidget;
import org.netbeans.jpa.modeler.core.widget.JavaClassWidget;
import org.netbeans.jpa.modeler.core.widget.MappedSuperclassWidget;
import org.netbeans.jpa.modeler.core.widget.PersistenceClassWidget;
import org.netbeans.jpa.modeler.core.widget.PlainClassWidget;
import org.netbeans.jpa.modeler.core.widget.PrimaryKeyContainerWidget;
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
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.IdentifiableClass;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.Transient;
import org.netbeans.jpa.modeler.spec.Version;
import org.netbeans.jpa.modeler.spec.design.Bounds;
import org.netbeans.jpa.modeler.spec.design.Diagram;
import org.netbeans.jpa.modeler.spec.design.DiagramElement;
import org.netbeans.jpa.modeler.spec.design.Shape;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.BaseElement;
import org.netbeans.jpa.modeler.spec.extend.CompositionAttribute;
import org.netbeans.jpa.modeler.spec.extend.ExtensionElements;
import org.netbeans.jpa.modeler.spec.extend.FlowNode;
import org.netbeans.jpa.modeler.spec.extend.IPersistenceAttributes;
import org.netbeans.jpa.modeler.spec.extend.IPrimaryKeyAttributes;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.MultiRelationAttribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.spec.extend.SingleRelationAttribute;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpace;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpaceElement;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpaceItem;
import org.netbeans.jpa.modeler.specification.model.file.JPAFileDataObject;
import org.netbeans.jpa.modeler.specification.model.file.action.JPAFileActionListener;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import static org.netbeans.jpa.modeler.specification.model.workspace.WorkSpaceManager.WORK_SPACE;
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
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowEdgeWidget;
import org.netbeans.modeler.specification.model.document.widget.IFlowNodeWidget;
import org.netbeans.modeler.specification.model.util.PModelerUtil;
import org.netbeans.modeler.specification.version.SoftwareVersion;
import org.netbeans.modeler.validation.jaxb.ValidateJAXB;
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
import static org.openide.util.NbBundle.getMessage;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.WindowManager;

public class JPAModelerUtil implements PModelerUtil<JPAModelerScene> {

    public static String PACKAGE_ICON_PATH;
    public static String JAVA_CLASS_ICON_PATH;
    public static String ENTITY_ICON_PATH;
    public static String MAPPED_SUPER_CLASS_ICON_PATH;
    public static String EMBEDDABLE_ICON_PATH;
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
    public static Image ID_ATTRIBUTE;
    public static Image BASIC_ATTRIBUTE;
    public static Image BASIC_COLLECTION_ATTRIBUTE;
    public static Image EMBEDDED_ATTRIBUTE;
    public static Image EMBEDDED_ID_ATTRIBUTE;
    public static Image COMPOSITION_ATTRIBUTE;
    public static Image SINGLE_VALUE_EMBEDDED_ATTRIBUTE;
    public static Image MULTI_VALUE_EMBEDDED_ATTRIBUTE;
    public static final Image COMPOSITION_ANCHOR;
    public static final Image SINGLE_VALUE_ANCHOR_SHAPE;
    public static final Image MULTI_VALUE_ANCHOR_SHAPE;
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
    public static Image GENERALIZATION_ANCHOR;
    public static Image OTOR_SOURCE_ANCHOR_SHAPE;
    public static Image OTOR_TARGET_ANCHOR_SHAPE;
    public static Image OTMR_SOURCE_ANCHOR_SHAPE;
    public static Image OTMR_TARGET_ANCHOR_SHAPE;
    public static Image MTOR_SOURCE_ANCHOR_SHAPE;
    public static Image MTOR_TARGET_ANCHOR_SHAPE;
    public static Image MTMR_SOURCE_ANCHOR_SHAPE;
    public static Image MTMR_TARGET_ANCHOR_SHAPE;
    public static Image OTOR_ICON;
    public static Image OTMR_ICON;
    public static Image MTOR_ICON;
    public static Image MTMR_ICON;
    public static Image UNI_DIRECTIONAL;
    public static Image BI_DIRECTIONAL;
    public static Image PK_UNI_DIRECTIONAL;
    public static Image PK_BI_DIRECTIONAL;
    public static Image ABSTRACT_ENTITY;
    public static Image ENTITY;
    public static Image JAVA_CLASS;
    public static Image MAPPED_SUPER_CLASS;
    public static Image EMBEDDABLE;

    public static ImageIcon CREATE_ICON;
    public static ImageIcon EDIT_ICON;
    public static ImageIcon DELETE_ICON;
    public static ImageIcon DELETE_ALL_ICON;
    public static ImageIcon PAINT_ICON;
    public static ImageIcon SUCCESS_ICON;
    public static ImageIcon WARNING_ICON;
    public static ImageIcon ERROR_ICON;
    public static ImageIcon GENERATE_SRC;
    public static ImageIcon ENTITY_VISIBILITY;
    public static ImageIcon SOCIAL_NETWORK_SHARING;
    public static ImageIcon VIEW_JSONB;
    public static ImageIcon VIEW_DB;
    public static ImageIcon MICRO_DB;
    public static ImageIcon NANO_DB;
    public static ImageIcon PERSISTENCE_UNIT;
    public static ImageIcon RUN_JPQL_ICON;
    public static ImageIcon HOME_ICON;
    public static ImageIcon SEARCH_ICON;
    public static ImageIcon WORKSPACE_ICON;
    public static ImageIcon RESET_ICON;

    public static Image UP_ICON;
    public static Image DOWN_ICON;

    private final static Map<Class<? extends BaseElement>, String> BASE_ELEMENT_ICONS = new HashMap<>();

    private static JAXBContext MODELER_CONTEXT;
    public static Unmarshaller MODELER_UNMARSHALLER;
    public static Marshaller MODELER_MARSHALLER;
    public final static InputOutput IO;
    public final static String JPA_FILE_TYPE = "text/jpa+xml";

    static {

        try {
            MODELER_CONTEXT = JAXBContext.newInstance(new Class<?>[]{EntityMappings.class, Entity.class, Attribute.class}); // unmarshaller will be always init before marshaller
        } catch (JAXBException ex) {
            ExceptionUtils.printStackTrace(ex);
        }

        ClassLoader cl = JPAModelerUtil.class.getClassLoader();//Eager Initialization
        GENERATE_SRC = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/generate-src.png"));
        RUN_JPQL_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/run-jpql.png"));
        ENTITY_VISIBILITY = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/entity-visibility.png"));
        VIEW_JSONB = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/jsonb.png"));
        SEARCH_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/search.png"));
        VIEW_DB = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/db.png"));
        MICRO_DB = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/micro-db.png"));
        NANO_DB = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/nano-db.png"));
        SOCIAL_NETWORK_SHARING = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/share.png"));
        PERSISTENCE_UNIT = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/persistence-unit.png"));
        WORKSPACE_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/workspace.png"));
        RESET_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/reset.png"));
        COMPOSITION_ANCHOR = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/composition-anchor.png")).getImage();
        SINGLE_VALUE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/single-value-anchor-shape.png")).getImage();
        MULTI_VALUE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/multi-value-anchor-shape.png")).getImage();

        IO = IOProvider.getDefault().getIO("Jeddict", false);
    }

    @Override
    public void init() {
        if (ENTITY_ICON_PATH == null) {
            PACKAGE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/java/PACKAGE.png";
            JAVA_CLASS_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/java/JAVA_CLASS.png";
            ENTITY_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/java/ENTITY.png";
            MAPPED_SUPER_CLASS_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/java/MAPPED_SUPER_CLASS.png";
            EMBEDDABLE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/java/EMBEDDABLE.png";
            ABSTRACT_ENTITY_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/java/ABSTRACT_ENTITY.png";
            ID_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/id-attribute.png";
            BASIC_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/basic-attribute.png";
            BASIC_COLLECTION_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/basic-collection-attribute.png";
            EMBEDDED_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/embedded-attribute.gif";
            EMBEDDED_ID_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/embedded-id-attribute.png";
            MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/multi-value-embedded.gif";
            SINGLE_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/single-value-embedded.gif";
            TRANSIENT_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/transient-attribute.png";
            VERSION_ATTRIBUTE_ICON_PATH = "org/netbeans/jpa/modeler/resource/image/version-attribute.png";
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

            BASE_ELEMENT_ICONS.put(JavaClass.class, JAVA_CLASS_ICON_PATH);
            BASE_ELEMENT_ICONS.put(Entity.class, ENTITY_ICON_PATH);
            BASE_ELEMENT_ICONS.put(MappedSuperclass.class, MAPPED_SUPER_CLASS_ICON_PATH);
            BASE_ELEMENT_ICONS.put(Embeddable.class, EMBEDDABLE_ICON_PATH);

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
            GENERALIZATION = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/generalization.png")).getImage();
            GENERALIZATION_ANCHOR = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/generalization-anchor.png")).getImage();
            
            OTOR_SOURCE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/one-to-one.gif")).getImage();
            OTOR_TARGET_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/one-to-one-arrow.png")).getImage();
            OTMR_SOURCE_ANCHOR_SHAPE = OTOR_SOURCE_ANCHOR_SHAPE;
            OTMR_TARGET_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/one-to-many-arrow.png")).getImage();
            MTOR_SOURCE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/many-to-one.gif")).getImage();
            MTOR_TARGET_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/one-to-one-arrow.png")).getImage();
            MTMR_SOURCE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/many-to-many.gif")).getImage();
            MTMR_TARGET_ANCHOR_SHAPE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/many-to-many-arrow.png")).getImage();
            
            OTOR_ICON = OTOR_SOURCE_ANCHOR_SHAPE;
            OTMR_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/one-to-many.gif")).getImage();
            MTOR_ICON = MTOR_SOURCE_ANCHOR_SHAPE;
            MTMR_ICON = MTMR_SOURCE_ANCHOR_SHAPE;

            UNI_DIRECTIONAL = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/uni.png")).getImage();
            BI_DIRECTIONAL = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/bi.png")).getImage();
            PK_UNI_DIRECTIONAL = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/pk-uni.png")).getImage();
            PK_BI_DIRECTIONAL = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/pk-bi.png")).getImage();
            COMPOSITION_ATTRIBUTE = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/composition.png")).getImage();

            JAVA_CLASS = new ImageIcon(cl.getResource(JPAModelerUtil.JAVA_CLASS_ICON_PATH)).getImage();
            ABSTRACT_ENTITY = new ImageIcon(cl.getResource(JPAModelerUtil.ABSTRACT_ENTITY_ICON_PATH)).getImage();
            ENTITY = new ImageIcon(cl.getResource(JPAModelerUtil.ENTITY_ICON_PATH)).getImage();
            MAPPED_SUPER_CLASS = new ImageIcon(cl.getResource(JPAModelerUtil.MAPPED_SUPER_CLASS_ICON_PATH)).getImage();
            EMBEDDABLE = new ImageIcon(cl.getResource(JPAModelerUtil.EMBEDDABLE_ICON_PATH)).getImage();
            ID_ATTRIBUTE = new ImageIcon(cl.getResource(ID_ATTRIBUTE_ICON_PATH)).getImage();
            BASIC_ATTRIBUTE = new ImageIcon(cl.getResource(BASIC_ATTRIBUTE_ICON_PATH)).getImage();
            BASIC_COLLECTION_ATTRIBUTE = new ImageIcon(cl.getResource(BASIC_COLLECTION_ATTRIBUTE_ICON_PATH)).getImage();
            EMBEDDED_ATTRIBUTE = new ImageIcon(cl.getResource(EMBEDDED_ATTRIBUTE_ICON_PATH)).getImage();
            EMBEDDED_ID_ATTRIBUTE = new ImageIcon(cl.getResource(EMBEDDED_ID_ATTRIBUTE_ICON_PATH)).getImage();
            MULTI_VALUE_EMBEDDED_ATTRIBUTE = new ImageIcon(cl.getResource(MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH)).getImage();
            SINGLE_VALUE_EMBEDDED_ATTRIBUTE = new ImageIcon(cl.getResource(SINGLE_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH)).getImage();
            TRANSIENT_ATTRIBUTE = new ImageIcon(cl.getResource(TRANSIENT_ATTRIBUTE_ICON_PATH)).getImage();
            VERSION_ATTRIBUTE = new ImageIcon(cl.getResource(VERSION_ATTRIBUTE_ICON_PATH)).getImage();

            CREATE_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/add-element.png"));
            EDIT_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/edit-element.png"));
            DELETE_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/delete-element.png"));
            DELETE_ALL_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/delete-all-element.png"));
            PAINT_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/paint.png"));
            SUCCESS_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/success_16.png"));
            WARNING_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/warning_16.png"));
            ERROR_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/error_16.png"));
            HOME_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/home.png"));
            UP_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/anchor_up.png")).getImage();
            DOWN_ICON = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/resource/image/misc/anchor_down.png")).getImage();
        }
    }

    public static String getBaseElementIcon(Class<? extends BaseElement> baseElement) {
        String icon = BASE_ELEMENT_ICONS.get(baseElement);
        return icon == null ? JAVA_CLASS_ICON_PATH : icon;
    }

    public static EntityMappings getEntityMapping(File file) throws JAXBException {
        EntityMappings definition_Load;
        if (MODELER_UNMARSHALLER == null) {
            MODELER_UNMARSHALLER = MODELER_CONTEXT.createUnmarshaller();
            MODELER_UNMARSHALLER.setEventHandler(new ValidateJAXB());
        }
//         content = FileUtils.readFileToString(file);
        definition_Load = MODELER_UNMARSHALLER.unmarshal(new StreamSource(file), EntityMappings.class).getValue();
        MODELER_UNMARSHALLER = null;//GC issue
//        cleanUnMarshaller();
        return definition_Load;
    }

    private static void cleanUnMarshaller() {
        try {
            String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><entity-mappings/>";
            MODELER_UNMARSHALLER.unmarshal(new StreamSource(new StringReader(xmlStr)));
        } catch (JAXBException ex) {
//            Exceptions.printStackTrace(ex);
            System.out.println(ex);
        }
    }

    @Override
    public void loadModelerFile(final ModelerFile file) throws ProcessInterruptedException {

        try {
            JPAModelerScene scene = (JPAModelerScene) file.getModelerScene();
            scene.startSceneGeneration();
            File savedFile = file.getFile();
            EntityMappings entityMappings = null;
            try {
                entityMappings = getEntityMapping(savedFile);
            } catch (JAXBException ex) {
                if (StringUtils.isBlank(file.getFileContent())) {
                    entityMappings = null;
                } else {
                    throw ex;
                }
            }
            if (entityMappings == null) {
                ElementConfigFactory elementConfigFactory = file.getModelerDiagramModel().getElementConfigFactory();
                entityMappings = EntityMappings.getNewInstance(file.getCurrentVersion().getValue());
                elementConfigFactory.initializeObjectValue(entityMappings);
            } else {
                if (SoftwareVersion.getInstance(entityMappings.getVersion()).compareTo(file.getArchitectureVersion()) < 0) {
                    int reply = showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                            getMessage(JPAModelerUtil.class, "Notification.JCRE_SUGGESION.text", file.getCurrentVersion()),
                            getMessage(JPAModelerUtil.class, "Notification.JCRE_SUGGESION.title"), YES_NO_OPTION);
                    if (reply == YES_OPTION) {
                        file.getModelerPanelTopComponent().close();
                        JCREProcessor processor = Lookup.getDefault().lookup(JCREProcessor.class);
                        file.getModelerDiagramModel().setDefinitionElement(entityMappings);
                        processor.process(file);
                        throw new ProcessInterruptedException("Reverse engineering initiated");
                    } else {
                        entityMappings.setVersion(file.getCurrentVersion());
                        NotificationDisplayer.getDefault().notify(getMessage(JPAModelerUtil.class, "Notification.SVC_WARNING.title"),
                                ImageUtilities.image2Icon(file.getIcon()),
                                getMessage(JPAModelerUtil.class, "Notification.SVC_WARNING.text"), null,
                                NotificationDisplayer.Priority.HIGH, NotificationDisplayer.Category.INFO);
                    }

                }

            }

            ModelerDiagramSpecification modelerDiagram = file.getModelerDiagramModel();
            modelerDiagram.setDefinitionElement(entityMappings);
            scene.setBaseElementSpec(entityMappings);

            entityMappings.repairDefinition(IO);

            scene.getWorkSpaceManager().reloadMainWorkSpace();
            Diagram diagram = entityMappings.getJPADiagram();
            WorkSpace workSpace;
            if (diagram != null) {
                entityMappings.getJavaClass().forEach(node -> loadFlowNode(scene, node));
            } else {
                workSpace = (WorkSpace) file.getAttribute(WORK_SPACE);
                if (workSpace != null) {
                    entityMappings.setCurrentWorkSpace(workSpace.getId());
                }
                if (entityMappings.getPreviousWorkSpace() != entityMappings.getCurrentWorkSpace() && !entityMappings.isRootWorkSpace()) {
                    scene.getWorkSpaceManager().loadDependentItems(entityMappings.getCurrentWorkSpace());
                }
                entityMappings.getCurrentWorkSpace().getItems()
                        .stream()
                        .map(item -> item.getJavaClass())
                        .forEach(node -> loadFlowNode(scene, node));
            }
            scene.getJavaClassWidges().forEach(javaClassWidget -> loadAttribute(javaClassWidget));
            scene.getJavaClassWidges().forEach(javaClassWidget -> loadFlowEdge(javaClassWidget));
            entityMappings.initJavaInheritanceMapping();

            int itemSize;
            long drawItemSize;

            if (diagram != null && !diagram.getJPAPlane().getDiagramElement().isEmpty()) {
                diagram.getJPAPlane().getDiagramElement()
                        .forEach(diagramElement -> loadDiagram(scene, diagramElement));
                itemSize = entityMappings.getJPADiagram().getJPAPlane().getDiagramElement().size();
                drawItemSize = itemSize;
            } else {
                drawItemSize = entityMappings.getCurrentWorkSpace().getItems()
                        .stream()
                        .peek(item -> loadDiagram(scene, item))
                        .filter(item -> item.getLocation() != null)
                        .count();
                itemSize = entityMappings.getCurrentWorkSpace().getItems().size();
            }
            if (entityMappings.isGenerated() || drawItemSize != itemSize) {
                scene.autoLayout();
                entityMappings.setStatus(null);
            }

            updateWindowTitle(file, entityMappings);
            scene.commitSceneGeneration();
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void updateWindowTitle(ModelerFile file, EntityMappings entityMappings) {
        if (!entityMappings.isRootWorkSpace()) {
            String windowName = file.getModelerFileDataObject().getPrimaryFile().getName() + " > " + entityMappings.getCurrentWorkSpace().getName();
            file.setName(windowName);
            file.getModelerPanelTopComponent().setName(windowName);
            file.getModelerPanelTopComponent().setToolTipText(windowName);
        }
    }

    private void loadAttribute(JavaClassWidget classWidget) {
        if (classWidget.getBaseElementSpec() instanceof ManagedClass) {
            ManagedClass<IPersistenceAttributes> classSpec = (ManagedClass) classWidget.getBaseElementSpec();
            PersistenceClassWidget persistenceClassWidget = (PersistenceClassWidget) classWidget;
            if (classSpec.getRootElement()!=null && classSpec.getAttributes() != null) {
                WorkSpace workSpace = classSpec.getRootElement().getCurrentWorkSpace();
                if ((classSpec.getAttributes() instanceof IPrimaryKeyAttributes)
                        && (classWidget instanceof PrimaryKeyContainerWidget)) {
                    PrimaryKeyContainerWidget primaryKeyContainerWidget = (PrimaryKeyContainerWidget) classWidget;
                    IPrimaryKeyAttributes persistenceAttributes = (IPrimaryKeyAttributes) classSpec.getAttributes();
                    persistenceAttributes.getId()
                            .stream()
                            .forEach((id) -> primaryKeyContainerWidget.addNewIdAttribute(id.getName(), id));
                    EmbeddedId embeddedId = persistenceAttributes.getEmbeddedId();
                    if (embeddedId != null) {// && workSpace.hasItem(embeddedId.getConnectedClass())) {
                        primaryKeyContainerWidget.addNewEmbeddedIdAttribute(embeddedId.getName(), embeddedId);
                    }
                    persistenceAttributes.getVersion()
                            .stream()
                            .forEach(version -> primaryKeyContainerWidget.addNewVersionAttribute(version.getName(), version));
                }
                classSpec.getAttributes().getBasic()
                        .stream()
                        .forEach(basic -> persistenceClassWidget.addNewBasicAttribute(basic.getName(), basic));
                classSpec.getAttributes().getTransient()
                        .stream()
                        .forEach(_transient -> persistenceClassWidget.addNewTransientAttribute(_transient.getName(), _transient));
                classSpec.getAttributes().getEmbedded()
                        .stream()
                        .filter(embedded -> workSpace.hasItem(embedded.getConnectedClass()))
                        .forEach((embedded) -> {
                            persistenceClassWidget.addNewSingleValueEmbeddedAttribute(embedded.getName(), embedded);
                        });
                classSpec.getAttributes().getElementCollection()
                        .stream()
                        .forEach((elementCollection) -> {
                            if (elementCollection.getConnectedClass() != null) {
                                if (workSpace.hasItem(elementCollection.getConnectedClass())) {
                                    persistenceClassWidget.addNewMultiValueEmbeddedAttribute(elementCollection.getName(), elementCollection);
                                }
                            } else {
                                persistenceClassWidget.addNewBasicCollectionAttribute(elementCollection.getName(), elementCollection);
                            }
                        });
                classSpec.getAttributes().getOneToOne()
                        .stream()
                        .filter(oto -> workSpace.hasItem(oto.getConnectedEntity()))
                        .forEach(oto -> persistenceClassWidget.addNewOneToOneRelationAttribute(oto.getName(), oto.isPrimaryKey(), oto));
                classSpec.getAttributes().getOneToMany()
                        .stream()
                        .filter(otm -> workSpace.hasItem(otm.getConnectedEntity()))
                        .forEach(otm -> persistenceClassWidget.addNewOneToManyRelationAttribute(otm.getName(), otm));
                classSpec.getAttributes().getManyToOne()
                        .stream()
                        .filter(mto -> workSpace.hasItem(mto.getConnectedEntity()))
                        .forEach(mto -> persistenceClassWidget.addNewManyToOneRelationAttribute(mto.getName(), mto.isPrimaryKey(), mto));
                classSpec.getAttributes().getManyToMany()
                        .stream()
                        .filter(mtm -> workSpace.hasItem(mtm.getConnectedEntity()))
                        .forEach(mtm -> persistenceClassWidget.addNewManyToManyRelationAttribute(mtm.getName(), mtm));
                persistenceClassWidget.sortAttributes();
            }
        }
    }

    @Override
    public void loadBaseElement(IBaseElementWidget parentConatiner, Map<IBaseElement,Rectangle> elements) {
        if (parentConatiner instanceof JavaClassWidget) {
            ManagedClass<IPersistenceAttributes> classSpec = (ManagedClass) parentConatiner.getBaseElementSpec();
            PersistenceClassWidget persistenceClassWidget = (PersistenceClassWidget) parentConatiner;
            WorkSpace workSpace = classSpec.getRootElement().getCurrentWorkSpace();
            JPAModelerScene scene = (JPAModelerScene) persistenceClassWidget.getModelerScene();
            for (Map.Entry<IBaseElement,Rectangle> elementEntry : elements.entrySet()) {
                IBaseElement element = elementEntry.getKey();
                if (element instanceof Attribute) {
                    Attribute attribute = (Attribute) element;
                    attribute.setAttributes(classSpec.getAttributes());

                    if ((classSpec.getAttributes() instanceof IPrimaryKeyAttributes) && (parentConatiner instanceof PrimaryKeyContainerWidget)) {
                        PrimaryKeyContainerWidget primaryKeyContainerWidget = (PrimaryKeyContainerWidget) parentConatiner;
                        IPrimaryKeyAttributes persistenceAttributes = (IPrimaryKeyAttributes) classSpec.getAttributes();
                        if (element instanceof Id) {
                            Id id = (Id) element;
                            primaryKeyContainerWidget.addNewIdAttribute(id.getName(), id);
                            persistenceAttributes.addId(id);
                        }
                        if (element instanceof Version) {
                            Version version = (Version) element;
                            primaryKeyContainerWidget.addNewVersionAttribute(version.getName(), version);
                            persistenceAttributes.addVersion(version);
                        }
                    }
                    if (element instanceof Basic) {
                        Basic basic = (Basic) element;
                        persistenceClassWidget.addNewBasicAttribute(basic.getName(), basic);
                        classSpec.getAttributes().addBasic(basic);
                    }
                    if (element instanceof Transient) {
                        Transient _transient = (Transient) element;
                        persistenceClassWidget.addNewTransientAttribute(_transient.getName(), _transient);
                        classSpec.getAttributes().addTransient(_transient);
                    }
                    if (element instanceof Embedded && workSpace.hasItem(((Embedded) element).getConnectedClass())) {
                        Embedded embedded = (Embedded) element;
                        SingleValueEmbeddedAttributeWidget attributeWidget = persistenceClassWidget.addNewSingleValueEmbeddedAttribute(embedded.getName(), embedded);
                        classSpec.getAttributes().addEmbedded(embedded);
                        loadEmbeddedEdge(scene, "SINGLE_EMBEDDABLE_RELATION", persistenceClassWidget, attributeWidget);
                    }

                    if (element instanceof ElementCollection) {
                        ElementCollection elementCollection = (ElementCollection) element;
                        if (elementCollection.getConnectedClass() != null) {
                            if (workSpace.hasItem(elementCollection.getConnectedClass())) {
                                MultiValueEmbeddedAttributeWidget attributeWidget = persistenceClassWidget.addNewMultiValueEmbeddedAttribute(elementCollection.getName(), elementCollection);
                                loadEmbeddedEdge(scene, "MULTI_EMBEDDABLE_RELATION", persistenceClassWidget, attributeWidget);
                            }
                        } else {
                            persistenceClassWidget.addNewBasicCollectionAttribute(elementCollection.getName(), elementCollection);
                        }
                        classSpec.getAttributes().addElementCollection(elementCollection);
                    }

                    if (element instanceof OneToOne && workSpace.hasItem(((OneToOne) element).getConnectedEntity())) {
                        OneToOne oto = (OneToOne) element;
                        OTORelationAttributeWidget attributeWidget = persistenceClassWidget.addNewOneToOneRelationAttribute(oto.getName(), oto.isPrimaryKey(), oto);
                        classSpec.getAttributes().addOneToOne(oto);
                        loadRelationEdge(scene, "OTO_RELATION", persistenceClassWidget, attributeWidget, OTORelationAttributeWidget.class);
                    }
                    if (element instanceof OneToMany && workSpace.hasItem(((OneToMany) element).getConnectedEntity())) {
                        OneToMany otm = (OneToMany) element;
                        OTMRelationAttributeWidget attributeWidget = persistenceClassWidget.addNewOneToManyRelationAttribute(otm.getName(), otm);
                        classSpec.getAttributes().addOneToMany(otm);
                        loadRelationEdge(scene, "OTM_RELATION", persistenceClassWidget, attributeWidget, OTMRelationAttributeWidget.class);
                    }
                    if (element instanceof ManyToOne && workSpace.hasItem(((ManyToOne) element).getConnectedEntity())) {
                        ManyToOne mto = (ManyToOne) element;
                        MTORelationAttributeWidget attributeWidget = persistenceClassWidget.addNewManyToOneRelationAttribute(mto.getName(), mto.isPrimaryKey(), mto);
                        classSpec.getAttributes().addManyToOne(mto);
                        loadRelationEdge(scene, "MTO_RELATION", persistenceClassWidget, attributeWidget, OTMRelationAttributeWidget.class);
                    }
                    if (element instanceof ManyToMany && workSpace.hasItem(((ManyToMany) element).getConnectedEntity())) {
                        ManyToMany mtm = (ManyToMany) element;
                        MTMRelationAttributeWidget attributeWidget = persistenceClassWidget.addNewManyToManyRelationAttribute(mtm.getName(), mtm);
                        classSpec.getAttributes().addManyToMany(mtm);
                        loadRelationEdge(scene, "MTM_RELATION", persistenceClassWidget, attributeWidget, MTMRelationAttributeWidget.class);
                    }

                }
            }
            persistenceClassWidget.sortAttributes();
        } else if (parentConatiner instanceof JPAModelerScene) {
            JPAModelerScene scene = (JPAModelerScene) parentConatiner;
            EntityMappings entityMappings = scene.getBaseElementSpec();
            List<JavaClassWidget> javaClassWidgets = new ArrayList<>();
            for (Map.Entry<IBaseElement,Rectangle> elementEntry : elements.entrySet()) {
                IBaseElement element = elementEntry.getKey();
                if (element instanceof JavaClass) {
                    JavaClass javaClass = (JavaClass) element;
                    entityMappings.addBaseElement(javaClass);
                    JavaClassWidget javaClassWidget = (JavaClassWidget) loadFlowNode(scene, javaClass);
                    javaClassWidgets.add(javaClassWidget);
                    Rectangle widgetLocation = elementEntry.getValue();
                    Rectangle currentLocation = scene.getView().getVisibleRect();
                    WorkSpaceItem workSpaceItem = new WorkSpaceItem(javaClass, currentLocation.x + widgetLocation.x + 150, currentLocation.y + widgetLocation.y + 50);
                    loadDiagram(scene, workSpaceItem);
                }
            }
            javaClassWidgets.forEach(jcw -> loadAttribute(jcw));
            javaClassWidgets.forEach(jcw -> loadFlowEdge(jcw));
            entityMappings.initJavaInheritanceMapping();
        }
    }

    private INodeWidget loadFlowNode(JPAModelerScene scene, FlowNode flowNode) {
        INodeWidget nodeWidget;
        IModelerDocument document = null;
        ModelerDocumentFactory modelerDocumentFactory = scene.getModelerFile().getModelerDiagramModel().getModelerDocumentFactory();
        try {
            document = modelerDocumentFactory.getModelerDocument(flowNode);
        } catch (ModelerException ex) {
            scene.getModelerFile().handleException(ex);
        }
        SubCategoryNodeConfig subCategoryNodeConfig = scene.getModelerFile().getModelerDiagramModel().getPaletteConfig().findSubCategoryNodeConfig(document);
        NodeWidgetInfo nodeWidgetInfo = new NodeWidgetInfo(flowNode.getId(), subCategoryNodeConfig, new Point(0, 0));
        nodeWidgetInfo.setName(flowNode.getName());
        nodeWidgetInfo.setExist(Boolean.TRUE);
        nodeWidgetInfo.setBaseElementSpec(flowNode);
        nodeWidget = scene.createNodeWidget(nodeWidgetInfo);
        if (flowNode.getName() != null) {
            nodeWidget.setLabel(flowNode.getName());
        }
        ((PNodeWidget) nodeWidget).setMinimized(flowNode.isMinimized());
        return nodeWidget;
    }

    private void loadFlowEdge(JavaClassWidget<JavaClass> javaClassWidget) {
        JPAModelerScene scene = javaClassWidget.getModelerScene();
        loadGeneralization(scene, javaClassWidget);
        if (javaClassWidget instanceof PersistenceClassWidget) {
            PersistenceClassWidget<? extends ManagedClass> sourcePersistenceClassWidget = (PersistenceClassWidget) javaClassWidget;
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
        boolean primaryKey = sourceRelationAttributeWidget.getBaseElementSpec() instanceof SingleRelationAttribute && ((SingleRelationAttribute) sourceRelationAttributeWidget.getBaseElementSpec()).isPrimaryKey();
        if (sourceRelationAttribute.getConnectedAttribute() != null) {
            targetRelationAttributeWidget = targetEntityWidget.findRelationAttributeWidget(sourceRelationAttribute.getConnectedAttribute().getId(), targetRelationAttributeWidgetClass);
            contextToolId = "B" + abstractTool;//OTM_RELATION";
        } else {
            contextToolId = "U" + abstractTool;
        }
        edgeInfo.setType(NBModelerUtil.getEdgeType(sourcePersistenceClassWidget, targetEntityWidget, primaryKey ? "PK" + contextToolId : contextToolId));
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);

        scene.setEdgeWidgetSource(edgeInfo, getEdgeSourcePinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, sourceRelationAttributeWidget));
        scene.setEdgeWidgetTarget(edgeInfo, getEdgeTargetPinWidget(sourcePersistenceClassWidget, targetEntityWidget, edgeWidget, targetRelationAttributeWidget));
        ((IBaseElementWidget) edgeWidget.getSourceAnchor().getRelatedWidget()).onConnection();
        ((IBaseElementWidget) edgeWidget.getTargetAnchor().getRelatedWidget()).onConnection();
        ((IBaseElementWidget) edgeWidget).onConnection();
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

    @Deprecated
    private void loadDiagram(JPAModelerScene scene, DiagramElement diagramElement) {
        if (diagramElement instanceof Shape) {
            Shape shape = (Shape) diagramElement;
            Bounds bounds = shape.getBounds();
            Widget widget = (Widget) scene.getBaseElement(shape.getElementRef());
            if (widget != null) {
                if (widget instanceof INodeWidget) {
                    INodeWidget nodeWidget = (INodeWidget) widget;
                    Point location = new Point((int) bounds.getX(), (int) bounds.getY());
                    nodeWidget.setPreferredLocation(location);
                    scene.reinstallColorScheme(nodeWidget);
                } else {
                    throw new InvalidElmentException("Invalid JPA Element : " + widget);
                }
            }
        }
    }

    private void loadDiagram(JPAModelerScene scene, WorkSpaceItem workSpaceItem) {
        JavaClassWidget<JavaClass> classWidget = (JavaClassWidget<JavaClass>) scene.getBaseElement(workSpaceItem.getJavaClass().getId());
        if (classWidget != null) {
            classWidget.setPreferredLocation(workSpaceItem.getLocation());
            classWidget.setTextDesign(workSpaceItem.getTextDesign());
            for (AttributeWidget<? extends Attribute> attrWidget : classWidget.getAllAttributeWidgets(false)) {
                WorkSpaceElement workSpaceElement = workSpaceItem.getWorkSpaceElementMap().get(attrWidget.getBaseElementSpec());
                if (workSpaceElement != null) {
                    attrWidget.setTextDesign(workSpaceElement.getTextDesign());
                }
            }
            scene.reinstallColorScheme(classWidget);
        } else {
            throw new InvalidElmentException("Invalid JPA Element : " + classWidget);
        }
    }

    @Override
    public void saveModelerFile(ModelerFile file) {
        EntityMappings entityMappings = (EntityMappings) file.getDefinitionElement();
        JPAModelerScene scene = (JPAModelerScene) file.getModelerScene();
        scene.getWorkSpaceManager().updateWorkSpace();
        PreExecutionUtil.preExecution(file);
        saveFile(entityMappings, file.getFile());
    }

    @Override
    public String getContent(ModelerFile file) {
        EntityMappings entityMappings = (EntityMappings) file.getDefinitionElement();
//        if(entityMappings==null){
//            return "*** Corrupted Document found ***";//if broken xml file opened
//        }
        JPAModelerScene scene = (JPAModelerScene) file.getModelerScene();
        scene.getWorkSpaceManager().updateWorkSpace();
        PreExecutionUtil.preExecution(file);
        return getContent(entityMappings);
    }

    public static void removeDefaultJoinColumn(IdentifiableClass identifiableClass, String attributeName) {
        for (SingleRelationAttribute relationAttribute : identifiableClass.getAttributes().getDerivedRelationAttributes()) {
            if (!relationAttribute.isOwner()) {  //Only Owner will draw edge because in any case uni/bi owner is always exist
                continue;
            }
            if (!relationAttribute.getName().equals(attributeName)) {
                continue;
            }
            relationAttribute.getJoinColumn().clear();
        }
    }

    //Issue fix : https://github.com/jeddict/jeddict/issues/8 #Same Column name in CompositePK
    public static void addDefaultJoinColumnForCompositePK(IdentifiableClass identifiableClass,
            Attribute attribute, Set<String> allFields, List<JoinColumn> joinColumns) {
        if (attribute instanceof SingleRelationAttribute) {
            SingleRelationAttribute relationAttribute = (SingleRelationAttribute) attribute;
            if (!relationAttribute.isOwner()) {  //Only Owner will draw edge because in any case uni/bi owner is always exist
                return;
            }

            //check is it composite key
            Entity targetEntity = relationAttribute.getConnectedEntity();
            relationAttribute.getJoinColumn().clear();
            if (joinColumns == null || joinColumns.isEmpty()) {
                //unused snippet
                for (Attribute targetAttribute : targetEntity.getAttributes().getPrimaryKeyAttributes()) {
                    JoinColumn joinColumn = new JoinColumn();
                    String joinColumnName = (targetEntity.getClazz() + '_' + targetAttribute.getName()).toUpperCase();
                    joinColumnName = getNext(joinColumnName, nextJoinColumnName -> allFields.contains(nextJoinColumnName));
                    joinColumn.setName(joinColumnName);
                    if (targetAttribute instanceof RelationAttribute) {
                        Entity connectedEntity = ((RelationAttribute) targetAttribute).getConnectedEntity();
                        if (connectedEntity.getCompositePrimaryKeyType() != null) {
                            //TODO  
                        } else {
                            Id id = connectedEntity.getAttributes().getId().get(0);
                            joinColumn.setReferencedColumnName(targetAttribute.getName() + "_" + id.getDefaultColumnName());
                        }
                    } else {
                        joinColumn.setReferencedColumnName(targetAttribute.getName());
                    }
                    relationAttribute.getJoinColumn().add(joinColumn);
                }
            } else {
                //called from db exception handeler
                relationAttribute.getJoinColumn().addAll(joinColumns);
            }
        }
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

    public String getContent(EntityMappings entityMappings) {
        StringWriter sw = new StringWriter();
        try {
            if (MODELER_MARSHALLER == null) {
                MODELER_MARSHALLER = MODELER_CONTEXT.createMarshaller();
                MODELER_MARSHALLER.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                MODELER_MARSHALLER.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://java.sun.com/xml/ns/persistence/orm orm_2_1.xsd");
                MODELER_MARSHALLER.setEventHandler(new ValidateJAXB());
            }
            MODELER_MARSHALLER.marshal(entityMappings, sw);

        } catch (JAXBException ex) {
            ExceptionUtils.printStackTrace(ex);
        }
        return sw.toString();
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
        return inodeWidget;
    }

    @Override
    public Anchor getAnchor(INodeWidget inodeWidget) {
        INodeWidget nodeWidget = inodeWidget;
        Anchor sourceAnchor;
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
        } else {
            throw new InvalidElmentException("Invalid JPA Pin Element");
        }
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
            case "JavaClass":
                widget = new PlainClassWidget(scene, widgetInfo);
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
        if (sourceNodeWidget instanceof PersistenceClassWidget
                && targetNodeWidget instanceof EntityWidget
                && edgeWidget instanceof RelationFlowWidget) {
            PersistenceClassWidget sourcePersistenceWidget = (PersistenceClassWidget) sourceNodeWidget;
            EntityWidget targetEntityWidget = (EntityWidget) targetNodeWidget;
            RelationFlowWidget relationFlowWidget = (RelationFlowWidget) edgeWidget;
            RelationAttributeWidget<? extends RelationAttribute> relationAttributeWidget = null;
            if (relationFlowWidget instanceof OTORelationFlowWidget) {
                OTORelationFlowWidget otoRelationFlowWidget = (OTORelationFlowWidget) relationFlowWidget;
                OTORelationAttributeWidget otoRelationAttributeWidget;
                boolean primaryKey = otoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKUOTO_RELATION") || otoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKBOTO_RELATION");
                if (sourceAttributeWidget == null) {
                    otoRelationAttributeWidget = sourcePersistenceWidget.addNewOneToOneRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()), primaryKey);
                } else {
                    otoRelationAttributeWidget = (OTORelationAttributeWidget) sourceAttributeWidget;
                }

                if (otoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKUOTO_RELATION") || otoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKBOTO_RELATION")) {
                    otoRelationAttributeWidget.getBaseElementSpec().setPrimaryKey(Boolean.TRUE);
                }
                otoRelationAttributeWidget.setOneToOneRelationFlowWidget(otoRelationFlowWidget);
                relationAttributeWidget = otoRelationAttributeWidget;
            } else if (relationFlowWidget instanceof OTMRelationFlowWidget) {
                OTMRelationAttributeWidget otmRelationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    otmRelationAttributeWidget = sourcePersistenceWidget.addNewOneToManyRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName(), true));
                } else {
                    otmRelationAttributeWidget = (OTMRelationAttributeWidget) sourceAttributeWidget;
                }
                otmRelationAttributeWidget.setHierarchicalRelationFlowWidget((OTMRelationFlowWidget) relationFlowWidget);
                relationAttributeWidget = otmRelationAttributeWidget;
            } else if (relationFlowWidget instanceof MTORelationFlowWidget) {
                MTORelationFlowWidget mtoRelationFlowWidget = (MTORelationFlowWidget) relationFlowWidget;
                MTORelationAttributeWidget mtoRelationAttributeWidget;
                boolean primaryKey = mtoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKUMTO_RELATION") || mtoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKBMTO_RELATION");
                if (sourceAttributeWidget == null) {
                    mtoRelationAttributeWidget = sourcePersistenceWidget.addNewManyToOneRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName()), primaryKey);
                } else {
                    mtoRelationAttributeWidget = (MTORelationAttributeWidget) sourceAttributeWidget;
                }

                if (mtoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKUMTO_RELATION") || mtoRelationFlowWidget.getEdgeWidgetInfo().getType().equals("PKBMTO_RELATION")) {
                    mtoRelationAttributeWidget.getBaseElementSpec().setPrimaryKey(Boolean.TRUE);
                }
                mtoRelationAttributeWidget.setManyToOneRelationFlowWidget(mtoRelationFlowWidget);
                relationAttributeWidget = mtoRelationAttributeWidget;

            } else if (relationFlowWidget instanceof MTMRelationFlowWidget) {
                MTMRelationAttributeWidget mtmRelationAttributeWidget;
                if (sourceAttributeWidget == null) {
                    mtmRelationAttributeWidget = sourcePersistenceWidget.addNewManyToManyRelationAttribute(sourcePersistenceWidget.getNextAttributeName(targetEntityWidget.getName(), true));
                } else {
                    mtmRelationAttributeWidget = (MTMRelationAttributeWidget) sourceAttributeWidget;
                }
                mtmRelationAttributeWidget.setManyToManyRelationFlowWidget((MTMRelationFlowWidget) relationFlowWidget);
                relationAttributeWidget = mtmRelationAttributeWidget;
            } else {
                throw new UnsupportedOperationException("Not supported yet.");
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
                                multiValueEmbeddedAttributeWidget = sourcePersistenceWidget.addNewMultiValueEmbeddedAttribute(sourcePersistenceWidget.getNextAttributeName(targetEmbeddableWidget.getName(), true));
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
                            targetOTORelationAttributeWidget = targetEntityWidget.addNewOneToOneRelationAttribute(targetEntityWidget.getNextAttributeName(sourceEntityWidget.getName()), false);
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
                                targetMTORelationAttributeWidget = targetEntityWidget.addNewOneToManyRelationAttribute(targetEntityWidget.getNextAttributeName(sourceEntityWidget.getName(), true));
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
                                    targetMTMRelationAttributeWidget = targetEntityWidget.addNewManyToManyRelationAttribute(targetEntityWidget.getNextAttributeName(sourceEntityWidget.getName(), true));
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

    @Override
    public List<IBaseElement> clone(List<IBaseElement> elements) {
        List<IBaseElement> clonedElements = cloneElement(new ExtensionElements(elements)).getAny();
        for (int i = 0; i < clonedElements.size(); i++) {
            copyRef(null, elements.get(i), null, clonedElements.get(i), clonedElements);
        }
        return clonedElements;
    }

    public <T extends Object> T cloneElement(T element) {
        T clonedElement = null;
        try {
            if (MODELER_MARSHALLER == null) {
                MODELER_MARSHALLER = MODELER_CONTEXT.createMarshaller();
                MODELER_MARSHALLER.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                MODELER_MARSHALLER.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://java.sun.com/xml/ns/persistence/orm orm_2_1.xsd");
                MODELER_MARSHALLER.setEventHandler(new ValidateJAXB());
            }
            StringWriter sw = new StringWriter();
            QName qName = new QName(element.getClass().getSimpleName());
            JAXBElement<T> root = new JAXBElement<>(qName, (Class<T>) element.getClass(), (T) element);
            MODELER_MARSHALLER.marshal(root, sw);

            if (MODELER_UNMARSHALLER == null) {
                MODELER_UNMARSHALLER = MODELER_CONTEXT.createUnmarshaller();
            }
            StringReader reader = new StringReader(sw.toString());
            clonedElement = (T) MODELER_UNMARSHALLER.unmarshal(new StreamSource(reader), (Class<T>) element.getClass()).getValue();
            MODELER_UNMARSHALLER = null;
        } catch (JAXBException ex) {
            ExceptionUtils.printStackTrace(ex);
        }
        return clonedElement;
    }

    private <P extends Object,T extends Object> void copyRef(P parentElement, T element, P parentClonedElement, T clonedElement, List<T> clonedElements) {
        if (element instanceof BaseElement) {
            ((BaseElement) clonedElement).setRootElement(((BaseElement) element).getRootElement());
            ((BaseElement) clonedElement).setId(NBModelerUtil.getAutoGeneratedStringId());
        }
        if (element instanceof JavaClass) {
            ((JavaClass) clonedElement).setSuperclassRef(((JavaClass) element).getSuperclassRef());
            if (element instanceof Entity) {
//               skip LabelAttribute => child attribute is not required to set
            }
            List<Attribute> attributes = ((JavaClass) element).getAttributes().getAllAttribute();
            List<Attribute> clonedAttributes = ((JavaClass) clonedElement).getAttributes().getAllAttribute();
            for (int i = 0; i < attributes.size(); i++) {
                copyRef(element, attributes.get(i), clonedElement, clonedAttributes.get(i), clonedAttributes);
            }
            if(((JavaClass) clonedElement).getAttributes() instanceof IPersistenceAttributes){
                IPersistenceAttributes persistenceAttributes = (IPersistenceAttributes)((JavaClass) clonedElement).getAttributes();
                persistenceAttributes.removeNonOwnerAttribute(new HashSet<>((List<JavaClass>)clonedElements));
            }
            
        } else if (element instanceof Attribute) {

            if (element instanceof CompositionAttribute) {
                if (((CompositionAttribute) clonedElement).getConnectedClass() == null) {
                    ((CompositionAttribute) clonedElement).setConnectedClass(((CompositionAttribute) element).getConnectedClass());
                }
                if (element instanceof ElementCollection) {
                    if (((ElementCollection) clonedElement).getMapKeyEntity() == null) {
                        ((ElementCollection) clonedElement).setMapKeyEntity(((ElementCollection) element).getMapKeyEntity());
                    }
                    if (((ElementCollection) clonedElement).getMapKeyEmbeddable() == null) {
                        ((ElementCollection) clonedElement).setMapKeyEmbeddable(((ElementCollection) element).getMapKeyEmbeddable());
                    }//skip mapKeyAttribute
                }
            } else if (element instanceof RelationAttribute) {
                if(((RelationAttribute) clonedElement).getConnectedEntity() == null){ //if not self
                    if(((RelationAttribute) clonedElement).getConnectedEntity() == null){
                        ((RelationAttribute) clonedElement).setConnectedEntity(((RelationAttribute) element).getConnectedEntity());
                    }
                }
                // ConnectedAttribute => convert bi-directional to uni-directional
                if (element instanceof MultiRelationAttribute) {
                    if (((MultiRelationAttribute) clonedElement).getMapKeyEntity() == null) {
                        ((MultiRelationAttribute) clonedElement).setMapKeyEntity(((MultiRelationAttribute) element).getMapKeyEntity());
                    }
                    if (((MultiRelationAttribute) clonedElement).getMapKeyEntity() == null) {
                        ((MultiRelationAttribute) clonedElement).setMapKeyEmbeddable(((MultiRelationAttribute) element).getMapKeyEmbeddable());
                    }
                    //skip mapKeyAttribute
                }
            }
        }
    }

    public static void generateSourceCode(ModelerFile modelerFile) {
        generateSourceCode(modelerFile, null);
    }

    public static void generateSourceCode(ModelerFile modelerFile, Runnable afterExecution) {
        if (!((JPAModelerScene) modelerFile.getModelerScene()).compile()) {
            return;
        }
        GenerateCodeDialog dialog = new GenerateCodeDialog(modelerFile);
        dialog.setVisible(true);
        if (dialog.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {
            RequestProcessor processor = new RequestProcessor("jpa/ExportCode"); // NOI18N
            SourceCodeGeneratorTask task = new SourceCodeGeneratorTask(modelerFile, dialog.getConfigData(), afterExecution);
            processor.post(task);
        }
    }

    /**
     * This method is used, when modeler file is not created and version is
     * required.
     *
     * @return
     */
    public static String getModelerFileVersion() {
        Class _class = JPAFileActionListener.class;
        Annotation[] annotations = _class.getAnnotations();

        for (Annotation annotation : annotations) {
            if (annotation instanceof DiagramModel) {
                DiagramModel diagramModel = (DiagramModel) annotation;
                return diagramModel.version();
            }
        }
        return "0.0";
    }

}
