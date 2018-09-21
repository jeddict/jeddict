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

import io.github.jeddict.collaborate.issues.ExceptionUtils;
import static io.github.jeddict.jcode.util.StringHelper.getNext;
import static io.github.jeddict.jpa.modeler.Constant.*;
import io.github.jeddict.jpa.modeler.source.generator.task.SourceCodeGeneratorTask;
import io.github.jeddict.jpa.modeler.source.generator.ui.GenerateCodeDialog;
import io.github.jeddict.jpa.modeler.specification.model.file.JPAFileDataObject;
import static io.github.jeddict.jpa.modeler.specification.model.workspace.WorkSpaceManager.WORK_SPACE;
import io.github.jeddict.jpa.modeler.widget.BeanClassWidget;
import io.github.jeddict.jpa.modeler.widget.EmbeddableWidget;
import io.github.jeddict.jpa.modeler.widget.EntityWidget;
import io.github.jeddict.jpa.modeler.widget.JavaClassWidget;
import io.github.jeddict.jpa.modeler.widget.PersistenceClassWidget;
import io.github.jeddict.jpa.modeler.widget.PrimaryKeyContainerWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.AttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.association.AssociationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.association.MTMAssociationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.association.MTOAssociationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.association.OTMAssociationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.association.OTOAssociationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.EmbeddedAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.MultiValueEmbeddedAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.base.SingleValueEmbeddedAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.MTMRelationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.MTORelationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.OTMRelationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.OTORelationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.attribute.relation.RelationAttributeWidget;
import io.github.jeddict.jpa.modeler.widget.flow.GeneralizationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.MultiValueEmbeddableFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.SingleValueEmbeddableFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.BMTMAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.BMTOAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.BOTOAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.UMTMAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.UMTOAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.UOTMAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.association.UOTOAssociationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.BMTMRelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.BMTORelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.BOTORelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.UMTMRelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.UMTORelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.UOTMRelationFlowWidget;
import io.github.jeddict.jpa.modeler.widget.flow.relation.UOTORelationFlowWidget;
import io.github.jeddict.jpa.spec.Basic;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.Embedded;
import io.github.jeddict.jpa.spec.EmbeddedId;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.IdentifiableClass;
import io.github.jeddict.jpa.spec.JoinColumn;
import io.github.jeddict.jpa.spec.ManagedClass;
import io.github.jeddict.jpa.spec.ManyToMany;
import io.github.jeddict.jpa.spec.ManyToOne;
import io.github.jeddict.jpa.spec.MappedSuperclass;
import io.github.jeddict.jpa.spec.OneToMany;
import io.github.jeddict.jpa.spec.OneToOne;
import io.github.jeddict.jpa.spec.Transient;
import io.github.jeddict.jpa.spec.Version;
import io.github.jeddict.jpa.spec.bean.AssociationAttribute;
import io.github.jeddict.jpa.spec.bean.BeanClass;
import io.github.jeddict.jpa.spec.design.Bounds;
import io.github.jeddict.jpa.spec.design.Diagram;
import io.github.jeddict.jpa.spec.design.DiagramElement;
import io.github.jeddict.jpa.spec.design.Shape;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.BaseElement;
import io.github.jeddict.jpa.spec.extend.CompositionAttribute;
import io.github.jeddict.jpa.spec.extend.ExtensionElements;
import io.github.jeddict.jpa.spec.extend.FlowNode;
import io.github.jeddict.jpa.spec.extend.IPersistenceAttributes;
import io.github.jeddict.jpa.spec.extend.IPrimaryKeyAttributes;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.spec.extend.MultiRelationAttribute;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import io.github.jeddict.jpa.spec.extend.SingleRelationAttribute;
import io.github.jeddict.jpa.spec.workspace.WorkSpace;
import io.github.jeddict.jpa.spec.workspace.WorkSpaceElement;
import io.github.jeddict.jpa.spec.workspace.WorkSpaceItem;
import io.github.jeddict.reveng.JCREProcessor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modeler.config.document.IModelerDocument;
import org.netbeans.modeler.config.document.ModelerDocumentFactory;
import org.netbeans.modeler.config.element.ElementConfigFactory;
import org.netbeans.modeler.config.palette.SubCategoryNodeConfig;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.core.exception.InvalidElmentException;
import org.netbeans.modeler.core.exception.ModelerException;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import org.netbeans.modeler.properties.spec.ComboBoxValue;
import org.netbeans.modeler.specification.annotaton.DiagramModel;
import org.netbeans.modeler.specification.model.ModelerDiagramSpecification;
import org.netbeans.modeler.specification.model.document.core.IBaseElement;
import org.netbeans.modeler.specification.model.document.widget.IBaseElementWidget;
import org.netbeans.modeler.specification.model.util.IModelerUtil;
import org.netbeans.modeler.specification.version.SoftwareVersion;
import org.netbeans.modeler.validation.jaxb.ValidateJAXB;
import org.netbeans.modeler.widget.edge.IEdgeWidget;
import org.netbeans.modeler.widget.edge.info.EdgeWidgetInfo;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.node.info.NodeWidgetInfo;
import org.netbeans.modeler.widget.node.vmd.PNodeWidget;
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

public class JPAModelerUtil implements IModelerUtil<JPAModelerScene> {

    public static String PACKAGE_ICON_PATH;
    public static String JAVA_CLASS_ICON_PATH;
    public static String ABSTRACT_JAVA_CLASS_ICON_PATH;
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
    public static String TABLE_ICON_PATH;
    
    public static Image ID_ATTRIBUTE_ICON;
    public static Image BASIC_ATTRIBUTE_ICON;
    public static Image BASIC_COLLECTION_ATTRIBUTE_ICON;
    public static Image EMBEDDED_ATTRIBUTE_ICON;
    public static Image EMBEDDED_ID_ATTRIBUTE_ICON;
    public static Image COMPOSITION_ATTRIBUTE_ICON;
    public static Image SINGLE_VALUE_EMBEDDED_ATTRIBUTE_ICON;
    public static Image MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON;
    public static final Image COMPOSITION_ANCHOR;
    public static final Image SINGLE_VALUE_ANCHOR_SHAPE;
    public static final Image MULTI_VALUE_ANCHOR_SHAPE;
    public static Image TRANSIENT_ATTRIBUTE_ICON;
    public static Image VERSION_ATTRIBUTE_ICON;  
    public static Image UOTO_ATTRIBUTE_ICON;
    public static Image BOTO_ATTRIBUTE_ICON;
    public static Image PK_UOTO_ATTRIBUTE_ICON;
    public static Image PK_BOTO_ATTRIBUTE_ICON;
    public static Image UOTM_ATTRIBUTE_ICON;
    public static Image BOTM_ATTRIBUTE_ICON;
    public static Image UMTO_ATTRIBUTE_ICON;
    public static Image BMTO_ATTRIBUTE_ICON;
    public static Image PK_UMTO_ATTRIBUTE_ICON;
    public static Image PK_BMTO_ATTRIBUTE_ICON;
    public static Image UMTM_ATTRIBUTE_ICON;
    public static Image BMTM_ATTRIBUTE_ICON;
    public static Image GENERALIZATION_ICON;
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
    public static Image UNI_DIRECTIONAL_ICON;
    public static Image BI_DIRECTIONAL_ICON;
    public static Image PK_UNI_DIRECTIONAL_ICON;
    public static Image PK_BI_DIRECTIONAL_ICON;
    public static Image ABSTRACT_ENTITY_ICON;
    public static Image ENTITY_ICON;
    public static Image ABSTRACT_JAVA_CLASS_ICON;
    public static Image JAVA_CLASS_ICON;
    public static Image MAPPED_SUPER_CLASS_ICON;
    public static Image EMBEDDABLE_ICON;
    public static Image PACKAGE_ICON;
    public static Image TABLE_ICON;

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
        GENERATE_SRC = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/generate-src.png"));
        RUN_JPQL_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/run-jpql.png"));
        ENTITY_VISIBILITY = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/entity-visibility.png"));
        VIEW_JSONB = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/jsonb.png"));
        SEARCH_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/search.png"));
        VIEW_DB = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/db.png"));
        MICRO_DB = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/micro-db.png"));
        NANO_DB = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/nano-db.png"));
        SOCIAL_NETWORK_SHARING = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/share.png"));
        PERSISTENCE_UNIT = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/persistence-unit.png"));
        WORKSPACE_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/workspace.png"));
        RESET_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/reset.png"));
        COMPOSITION_ANCHOR = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/composition-anchor.png")).getImage();
        SINGLE_VALUE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/single-value-anchor-shape.png")).getImage();
        MULTI_VALUE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/multi-value-anchor-shape.png")).getImage();

        IO = IOProvider.getDefault().getIO("Jeddict", false);
    }

    @Override
    public void init() {
        if (ENTITY_ICON_PATH == null) {
            PACKAGE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/java/PACKAGE.png";
            JAVA_CLASS_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/java/JAVA_CLASS.png";
            ABSTRACT_JAVA_CLASS_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/java/ABSTRACT_JAVA_CLASS.png";
            ENTITY_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/java/ENTITY.png";
            ABSTRACT_ENTITY_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/java/ABSTRACT_ENTITY.png";
            MAPPED_SUPER_CLASS_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/java/MAPPED_SUPER_CLASS.png";
            EMBEDDABLE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/java/EMBEDDABLE.png";
            ID_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/id-attribute.png";
            BASIC_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/basic-attribute.png";
            BASIC_COLLECTION_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/basic-collection-attribute.png";
            EMBEDDED_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/embedded-attribute.gif";
            EMBEDDED_ID_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/embedded-id-attribute.png";
            MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/multi-value-embedded.gif";
            SINGLE_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/single-value-embedded.gif";
            TRANSIENT_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/transient-attribute.png";
            VERSION_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/version-attribute.png";
            UMTM_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/umtm-attribute.png";
            BMTM_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/bmtm-attribute.png";
            UMTO_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/umto-attribute.png";
            BMTO_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/bmto-attribute.png";
            PK_UMTO_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/pk-umto-attribute.png";
            PK_BMTO_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/pk-bmto-attribute.png";
            UOTM_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/uotm-attribute.png";
            BOTM_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/botm-attribute.png";
            UOTO_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/uoto-attribute.png";
            BOTO_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/boto-attribute.png";
            PK_UOTO_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/pk-uoto-attribute.png";
            PK_BOTO_ATTRIBUTE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/pk-boto-attribute.png";
            TABLE_ICON_PATH = "io/github/jeddict/jpa/modeler/resource/image/db/TABLE.gif";

            BASE_ELEMENT_ICONS.put(JavaClass.class, JAVA_CLASS_ICON_PATH);
            BASE_ELEMENT_ICONS.put(Entity.class, ENTITY_ICON_PATH);
            BASE_ELEMENT_ICONS.put(MappedSuperclass.class, MAPPED_SUPER_CLASS_ICON_PATH);
            BASE_ELEMENT_ICONS.put(Embeddable.class, EMBEDDABLE_ICON_PATH);

            ClassLoader cl = JPAModelerUtil.class.getClassLoader();

            UMTM_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(UMTM_ATTRIBUTE_ICON_PATH)).getImage();
            BMTM_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(BMTM_ATTRIBUTE_ICON_PATH)).getImage();
            UMTO_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(UMTO_ATTRIBUTE_ICON_PATH)).getImage();
            BMTO_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(BMTO_ATTRIBUTE_ICON_PATH)).getImage();
            PK_UMTO_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(PK_UMTO_ATTRIBUTE_ICON_PATH)).getImage();
            PK_BMTO_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(PK_BMTO_ATTRIBUTE_ICON_PATH)).getImage();
            UOTM_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(UOTM_ATTRIBUTE_ICON_PATH)).getImage();
            BOTM_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(BOTM_ATTRIBUTE_ICON_PATH)).getImage();
            UOTO_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(UOTO_ATTRIBUTE_ICON_PATH)).getImage();
            BOTO_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(BOTO_ATTRIBUTE_ICON_PATH)).getImage();
            PK_UOTO_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(PK_UOTO_ATTRIBUTE_ICON_PATH)).getImage();
            PK_BOTO_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(PK_BOTO_ATTRIBUTE_ICON_PATH)).getImage();
            GENERALIZATION_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/generalization.png")).getImage();
            GENERALIZATION_ANCHOR = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/generalization-anchor.png")).getImage();

            OTOR_SOURCE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/one-to-one.gif")).getImage();
            OTOR_TARGET_ANCHOR_SHAPE = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/one-to-one-arrow.png")).getImage();
            OTMR_SOURCE_ANCHOR_SHAPE = OTOR_SOURCE_ANCHOR_SHAPE;
            OTMR_TARGET_ANCHOR_SHAPE = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/one-to-many-arrow.png")).getImage();
            MTOR_SOURCE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/many-to-one.gif")).getImage();
            MTOR_TARGET_ANCHOR_SHAPE = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/one-to-one-arrow.png")).getImage();
            MTMR_SOURCE_ANCHOR_SHAPE = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/many-to-many.gif")).getImage();
            MTMR_TARGET_ANCHOR_SHAPE = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/many-to-many-arrow.png")).getImage();

            OTOR_ICON = OTOR_SOURCE_ANCHOR_SHAPE;
            OTMR_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/one-to-many.gif")).getImage();
            MTOR_ICON = MTOR_SOURCE_ANCHOR_SHAPE;
            MTMR_ICON = MTMR_SOURCE_ANCHOR_SHAPE;

            UNI_DIRECTIONAL_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/uni.png")).getImage();
            BI_DIRECTIONAL_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/bi.png")).getImage();
            PK_UNI_DIRECTIONAL_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/pk-uni.png")).getImage();
            PK_BI_DIRECTIONAL_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/pk-bi.png")).getImage();
            COMPOSITION_ATTRIBUTE_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/composition.png")).getImage();

            JAVA_CLASS_ICON = new ImageIcon(cl.getResource(JAVA_CLASS_ICON_PATH)).getImage();
            ABSTRACT_JAVA_CLASS_ICON = new ImageIcon(cl.getResource(ABSTRACT_JAVA_CLASS_ICON_PATH)).getImage();
            ENTITY_ICON = new ImageIcon(cl.getResource(ENTITY_ICON_PATH)).getImage();
            ABSTRACT_ENTITY_ICON = new ImageIcon(cl.getResource(ABSTRACT_ENTITY_ICON_PATH)).getImage();
            MAPPED_SUPER_CLASS_ICON = new ImageIcon(cl.getResource(MAPPED_SUPER_CLASS_ICON_PATH)).getImage();
            EMBEDDABLE_ICON = new ImageIcon(cl.getResource(EMBEDDABLE_ICON_PATH)).getImage();
            PACKAGE_ICON = new ImageIcon(cl.getResource(PACKAGE_ICON_PATH)).getImage();
            ID_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(ID_ATTRIBUTE_ICON_PATH)).getImage();
            BASIC_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(BASIC_ATTRIBUTE_ICON_PATH)).getImage();
            BASIC_COLLECTION_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(BASIC_COLLECTION_ATTRIBUTE_ICON_PATH)).getImage();
            EMBEDDED_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(EMBEDDED_ATTRIBUTE_ICON_PATH)).getImage();
            EMBEDDED_ID_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(EMBEDDED_ID_ATTRIBUTE_ICON_PATH)).getImage();
            MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(MULTI_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH)).getImage();
            SINGLE_VALUE_EMBEDDED_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(SINGLE_VALUE_EMBEDDED_ATTRIBUTE_ICON_PATH)).getImage();
            TRANSIENT_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(TRANSIENT_ATTRIBUTE_ICON_PATH)).getImage();
            VERSION_ATTRIBUTE_ICON = new ImageIcon(cl.getResource(VERSION_ATTRIBUTE_ICON_PATH)).getImage();
            TABLE_ICON = new ImageIcon(cl.getResource(TABLE_ICON_PATH)).getImage();

            CREATE_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/add-element.png"));
            EDIT_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/edit-element.png"));
            DELETE_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/delete-element.png"));
            DELETE_ALL_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/delete-all-element.png"));
            PAINT_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/paint.png"));
            SUCCESS_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/success_16.png"));
            WARNING_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/warning_16.png"));
            ERROR_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/error_16.png"));
            HOME_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/home.png"));
            UP_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/anchor_up.png")).getImage();
            DOWN_ICON = new ImageIcon(cl.getResource("io/github/jeddict/jpa/modeler/resource/image/misc/anchor_down.png")).getImage();
        }
    }

    public static String getBaseElementIcon(Class<? extends BaseElement> baseElement) {
        String icon = BASE_ELEMENT_ICONS.get(baseElement);
        return icon == null ? JAVA_CLASS_ICON_PATH : icon;
    }

    public static EntityMappings getEntityMapping(Source source) throws JAXBException {
        EntityMappings definition_Load;
        if (MODELER_UNMARSHALLER == null) {
            MODELER_UNMARSHALLER = MODELER_CONTEXT.createUnmarshaller();
            MODELER_UNMARSHALLER.setEventHandler(new ValidateJAXB());
        }
        definition_Load = MODELER_UNMARSHALLER.unmarshal(source, EntityMappings.class).getValue();
        MODELER_UNMARSHALLER = null;//GC issue
//        cleanUnMarshaller();
        return definition_Load;
    }

    public static EntityMappings getEntityMapping(Reader reader) throws JAXBException {
        return getEntityMapping(new StreamSource(reader));
    }

    public static EntityMappings getEntityMapping(File file) throws JAXBException {
        return getEntityMapping(new StreamSource(file));
    }

    private static void cleanUnMarshaller() {
        try {
            String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><entity-mappings/>";
            MODELER_UNMARSHALLER.unmarshal(new StreamSource(new StringReader(xmlStr)));
        } catch (JAXBException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public void loadModelerFile(final ModelerFile modelerFile) throws ProcessInterruptedException {

        try {
            JPAModelerScene scene = (JPAModelerScene) modelerFile.getModelerScene();
            scene.startSceneGeneration();
            File savedFile = modelerFile.getFile();
            EntityMappings entityMappings = null;
            try {
                entityMappings = getEntityMapping(savedFile);
            } catch (JAXBException ex) {
                if (StringUtils.isBlank(modelerFile.getFileContent())) {
                    entityMappings = null;
                } else {
                    throw ex;
                }
            }
            if (entityMappings == null) {
                ElementConfigFactory elementConfigFactory = modelerFile.getModelerDiagramModel().getElementConfigFactory();
                entityMappings = EntityMappings.getNewInstance(modelerFile.getCurrentVersion().getValue());
                elementConfigFactory.initializeObjectValue(entityMappings);
            } else {
                if (SoftwareVersion.getInstance(entityMappings.getVersion()).compareTo(modelerFile.getArchitectureVersion()) < 0) {
                    int reply = showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                            getMessage(JPAModelerUtil.class, "Notification.JCRE_SUGGESION.text", modelerFile.getCurrentVersion()),
                            getMessage(JPAModelerUtil.class, "Notification.JCRE_SUGGESION.title"), YES_NO_OPTION);
                    if (reply == YES_OPTION) {
                        modelerFile.getModelerPanelTopComponent().close();
                        JCREProcessor processor = Lookup.getDefault().lookup(JCREProcessor.class);
                        modelerFile.getModelerDiagramModel().setDefinitionElement(entityMappings);
                        processor.syncExistingDiagram(modelerFile);
                        throw new ProcessInterruptedException("Reverse engineering initiated");
                    } else {
                        entityMappings.setVersion(modelerFile.getCurrentVersion());
                        NotificationDisplayer.getDefault().notify(getMessage(JPAModelerUtil.class, "Notification.SVC_WARNING.title"),
                                ImageUtilities.image2Icon(modelerFile.getIcon()),
                                getMessage(JPAModelerUtil.class, "Notification.SVC_WARNING.text"), null,
                                NotificationDisplayer.Priority.HIGH, NotificationDisplayer.Category.INFO);
                    }

                }

            }

            ModelerDiagramSpecification modelerDiagram = modelerFile.getModelerDiagramModel();
            modelerDiagram.setDefinitionElement(entityMappings);
            scene.setBaseElementSpec(entityMappings);

            entityMappings.repairDefinition(IO);

            scene.getWorkSpaceManager().reloadMainWorkSpace();
            Diagram diagram = entityMappings.getJPADiagram();
            WorkSpace workSpace;
            if (diagram != null) {
                entityMappings.getJavaClass().forEach(node -> loadFlowNode(scene, node));
            } else {
                workSpace = (WorkSpace) modelerFile.getAttribute(WORK_SPACE);
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
            scene.getJavaClassWidges().forEach(this::loadAttribute);
            scene.getJavaClassWidges().forEach(this::loadFlowEdge);
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

            updateWindowTitle(modelerFile, entityMappings);
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
            if (classSpec.getRootElement() != null && classSpec.getAttributes() != null) {
                WorkSpace workSpace = classSpec.getRootElement().getCurrentWorkSpace();
                if ((classSpec.getAttributes() instanceof IPrimaryKeyAttributes)
                        && (classWidget instanceof PrimaryKeyContainerWidget)) {
                    PrimaryKeyContainerWidget primaryKeyContainerWidget = (PrimaryKeyContainerWidget) classWidget;
                    IPrimaryKeyAttributes persistenceAttributes = (IPrimaryKeyAttributes) classSpec.getAttributes();
                    persistenceAttributes.getId()
                            .forEach((id) -> primaryKeyContainerWidget.addIdAttribute(id.getName(), id));
                    EmbeddedId embeddedId = persistenceAttributes.getEmbeddedId();
                    if (embeddedId != null) {// && workSpace.hasItem(embeddedId.getConnectedClass())) {
                        primaryKeyContainerWidget.addEmbeddedIdAttribute(embeddedId.getName(), embeddedId);
                    }
                    persistenceAttributes.getVersion()
                            .forEach(version -> primaryKeyContainerWidget.addVersionAttribute(version.getName(), version));
                }
                classSpec.getAttributes().getBasic()
                        .forEach(attr -> persistenceClassWidget.addBasicAttribute(attr.getName(), attr));
                classSpec.getAttributes().getTransient()
                        .forEach(attr -> persistenceClassWidget.addTransientAttribute(attr.getName(), attr));
                classSpec.getAttributes().getEmbedded()
                        .stream()
                        .filter(embedded -> workSpace.hasItem(embedded.getConnectedClass()))
                        .forEach((attr) -> {
                            persistenceClassWidget.addSingleValueEmbeddedAttribute(attr.getName(), attr);
                        });
                classSpec.getAttributes().getElementCollection()
                        .forEach((elementCollection) -> {
                            if (elementCollection.getConnectedClass() != null) {
                                if (workSpace.hasItem(elementCollection.getConnectedClass())) {
                                    persistenceClassWidget.addMultiValueEmbeddedAttribute(elementCollection.getName(), elementCollection);
                                }
                            } else {
                                persistenceClassWidget.addBasicCollectionAttribute(elementCollection.getName(), elementCollection);
                            }
                        });
                classSpec.getAttributes().getOneToOne()
                        .stream()
                        .filter(oto -> workSpace.hasItem(oto.getConnectedEntity()))
                        .forEach(oto -> persistenceClassWidget.addOneToOneRelationAttribute(oto.getName(), oto.isPrimaryKey(), oto));
                classSpec.getAttributes().getOneToMany()
                        .stream()
                        .filter(otm -> workSpace.hasItem(otm.getConnectedEntity()))
                        .forEach(otm -> persistenceClassWidget.addOneToManyRelationAttribute(otm.getName(), otm));
                classSpec.getAttributes().getManyToOne()
                        .stream()
                        .filter(mto -> workSpace.hasItem(mto.getConnectedEntity()))
                        .forEach(mto -> persistenceClassWidget.addManyToOneRelationAttribute(mto.getName(), mto.isPrimaryKey(), mto));
                classSpec.getAttributes().getManyToMany()
                        .stream()
                        .filter(mtm -> workSpace.hasItem(mtm.getConnectedEntity()))
                        .forEach(mtm -> persistenceClassWidget.addManyToManyRelationAttribute(mtm.getName(), mtm));
                persistenceClassWidget.sortAttributes();
            }
        } else if (classWidget.getBaseElementSpec() instanceof BeanClass) {
            BeanClass classSpec = (BeanClass) classWidget.getBaseElementSpec();
            WorkSpace workSpace = classSpec.getRootElement().getCurrentWorkSpace();
            BeanClassWidget beanClassWidget = (BeanClassWidget) classWidget;
            if (classSpec.getRootElement() != null && classSpec.getAttributes() != null) {
                classSpec.getAttributes().getBasic()
                        .forEach(attr -> beanClassWidget.addBeanAttribute(attr.getName(), attr));
                classSpec.getAttributes().getElementCollection()
                        .forEach(attr -> beanClassWidget.addBeanCollectionAttribute(attr.getName(), attr));
                classSpec.getAttributes().getTransient()
                        .forEach(attr -> beanClassWidget.addBeanTransientAttribute(attr.getName(), attr));
                classSpec.getAttributes().getOneToOne()
                        .stream()
                        .filter(oto -> workSpace.hasItem(oto.getConnectedClass()))
                        .forEach(oto -> beanClassWidget.addOneToOneAssociationAttribute(oto.getName(), oto));
                classSpec.getAttributes().getOneToMany()
                        .stream()
                        .filter(otm -> workSpace.hasItem(otm.getConnectedClass()))
                        .forEach(otm -> beanClassWidget.addOneToManyAssociationAttribute(otm.getName(), otm));
                classSpec.getAttributes().getManyToOne()
                        .stream()
                        .filter(mto -> workSpace.hasItem(mto.getConnectedClass()))
                        .forEach(mto -> beanClassWidget.addManyToOneAssociationAttribute(mto.getName(), mto));
                classSpec.getAttributes().getManyToMany()
                        .stream()
                        .filter(mtm -> workSpace.hasItem(mtm.getConnectedClass()))
                        .forEach(mtm -> beanClassWidget.addManyToManyAssociationAttribute(mtm.getName(), mtm));
                beanClassWidget.sortAttributes();
            }
        }
    }

    @Override
    public void loadBaseElement(IBaseElementWidget parentConatiner, Map<IBaseElement, Rectangle> elements) {
        if (parentConatiner instanceof JavaClassWidget) {
            ManagedClass<IPersistenceAttributes> classSpec = (ManagedClass) parentConatiner.getBaseElementSpec();
            PersistenceClassWidget persistenceClassWidget = (PersistenceClassWidget) parentConatiner;
            WorkSpace workSpace = classSpec.getRootElement().getCurrentWorkSpace();
            JPAModelerScene scene = (JPAModelerScene) persistenceClassWidget.getModelerScene();
            for (Map.Entry<IBaseElement, Rectangle> elementEntry : elements.entrySet()) {
                IBaseElement element = elementEntry.getKey();
                if (element instanceof Attribute) {
                    Attribute attribute = (Attribute) element;
                    attribute.setAttributes(classSpec.getAttributes());

                    if ((classSpec.getAttributes() instanceof IPrimaryKeyAttributes) && (parentConatiner instanceof PrimaryKeyContainerWidget)) {
                        PrimaryKeyContainerWidget primaryKeyContainerWidget = (PrimaryKeyContainerWidget) parentConatiner;
                        IPrimaryKeyAttributes persistenceAttributes = (IPrimaryKeyAttributes) classSpec.getAttributes();
                        if (element instanceof Id) {
                            Id id = (Id) element;
                            primaryKeyContainerWidget.addIdAttribute(id.getName(), id);
                            persistenceAttributes.addId(id);
                        }
                        if (element instanceof Version) {
                            Version version = (Version) element;
                            primaryKeyContainerWidget.addVersionAttribute(version.getName(), version);
                            persistenceAttributes.addVersion(version);
                        }
                    }
                    if (element instanceof Basic) {
                        Basic basic = (Basic) element;
                        persistenceClassWidget.addBasicAttribute(basic.getName(), basic);
                        classSpec.getAttributes().addBasic(basic);
                    }
                    if (element instanceof Transient) {
                        Transient _transient = (Transient) element;
                        persistenceClassWidget.addTransientAttribute(_transient.getName(), _transient);
                        classSpec.getAttributes().addTransient(_transient);
                    }
                    if (element instanceof Embedded && workSpace.hasItem(((Embedded) element).getConnectedClass())) {
                        Embedded embedded = (Embedded) element;
                        SingleValueEmbeddedAttributeWidget attributeWidget = persistenceClassWidget.addSingleValueEmbeddedAttribute(embedded.getName(), embedded);
                        classSpec.getAttributes().addEmbedded(embedded);
                        loadEmbeddedEdge(scene, SINGLE_EMBEDDABLE_RELATION,
                                e -> new SingleValueEmbeddableFlowWidget(scene, e),
                                persistenceClassWidget, attributeWidget);
                    }

                    if (element instanceof ElementCollection) {
                        ElementCollection elementCollection = (ElementCollection) element;
                        if (elementCollection.getConnectedClass() != null) {
                            if (workSpace.hasItem(elementCollection.getConnectedClass())) {
                                MultiValueEmbeddedAttributeWidget attributeWidget = persistenceClassWidget.addMultiValueEmbeddedAttribute(elementCollection.getName(), elementCollection);
                                loadEmbeddedEdge(scene, MULTI_EMBEDDABLE_RELATION,
                                        e -> new MultiValueEmbeddableFlowWidget(scene, e),
                                        persistenceClassWidget, attributeWidget);
                            }
                        } else {
                            persistenceClassWidget.addBasicCollectionAttribute(elementCollection.getName(), elementCollection);
                        }
                        classSpec.getAttributes().addElementCollection(elementCollection);
                    }

                    if (element instanceof OneToOne && workSpace.hasItem(((OneToOne) element).getConnectedEntity())) {
                        OneToOne oto = (OneToOne) element;
                        OTORelationAttributeWidget attributeWidget = persistenceClassWidget.addOneToOneRelationAttribute(oto.getName(), oto.isPrimaryKey(), oto);
                        classSpec.getAttributes().addOneToOne(oto);
                        loadRelationEdge(scene, OTO_RELATION,
                        attributeWidget.getBaseElementSpec().getConnectedAttribute()==null?
                                e -> new UOTORelationFlowWidget(scene, e) : e -> new BOTORelationFlowWidget(scene, e), persistenceClassWidget, attributeWidget, OTORelationAttributeWidget.class);
                    }
                    if (element instanceof OneToMany && workSpace.hasItem(((OneToMany) element).getConnectedEntity())) {
                        OneToMany otm = (OneToMany) element;
                        OTMRelationAttributeWidget attributeWidget = persistenceClassWidget.addOneToManyRelationAttribute(otm.getName(), otm);
                        classSpec.getAttributes().addOneToMany(otm);
                        loadRelationEdge(scene, OTM_RELATION,
                        attributeWidget.getBaseElementSpec().getConnectedAttribute()==null?
                                e -> new UOTMRelationFlowWidget(scene, e) : null, persistenceClassWidget, attributeWidget, OTMRelationAttributeWidget.class);
                    }
                    if (element instanceof ManyToOne && workSpace.hasItem(((ManyToOne) element).getConnectedEntity())) {
                        ManyToOne mto = (ManyToOne) element;
                        MTORelationAttributeWidget attributeWidget = persistenceClassWidget.addManyToOneRelationAttribute(mto.getName(), mto.isPrimaryKey(), mto);
                        classSpec.getAttributes().addManyToOne(mto);
                        loadRelationEdge(scene, MTO_RELATION,
                        attributeWidget.getBaseElementSpec().getConnectedAttribute()==null?
                                e -> new UMTORelationFlowWidget(scene, e) : e -> new BMTORelationFlowWidget(scene, e), persistenceClassWidget, attributeWidget, OTMRelationAttributeWidget.class);
                    }
                    if (element instanceof ManyToMany && workSpace.hasItem(((ManyToMany) element).getConnectedEntity())) {
                        ManyToMany mtm = (ManyToMany) element;
                        MTMRelationAttributeWidget attributeWidget = persistenceClassWidget.addManyToManyRelationAttribute(mtm.getName(), mtm);
                        classSpec.getAttributes().addManyToMany(mtm);
                        loadRelationEdge(scene, MTM_RELATION,
                        attributeWidget.getBaseElementSpec().getConnectedAttribute()==null?
                                e -> new UMTMRelationFlowWidget(scene, e) : e -> new BMTMRelationFlowWidget(scene, e), persistenceClassWidget, attributeWidget, MTMRelationAttributeWidget.class);
                    }

                }
            }
            persistenceClassWidget.sortAttributes();
        } else if (parentConatiner instanceof JPAModelerScene) {
            JPAModelerScene scene = (JPAModelerScene) parentConatiner;
            EntityMappings entityMappings = scene.getBaseElementSpec();
            List<JavaClassWidget> javaClassWidgets = new ArrayList<>();
            for (Map.Entry<IBaseElement, Rectangle> elementEntry : elements.entrySet()) {
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
        NodeWidgetInfo nodeWidgetInfo = new NodeWidgetInfo(subCategoryNodeConfig, new Point(0, 0));
        nodeWidgetInfo.setId(flowNode.getId());
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

    private void loadFlowEdge(JavaClassWidget<? extends JavaClass> javaClassWidget) {
        JPAModelerScene scene = javaClassWidget.getModelerScene();
        loadGeneralization(scene, javaClassWidget);
        if (javaClassWidget instanceof PersistenceClassWidget) {
            PersistenceClassWidget<? extends ManagedClass> sourcePersistenceClassWidget = (PersistenceClassWidget) javaClassWidget;
            for (SingleValueEmbeddedAttributeWidget embeddedAttributeWidget : sourcePersistenceClassWidget.getSingleValueEmbeddedAttributeWidgets()) {
                loadEmbeddedEdge(scene, SINGLE_EMBEDDABLE_RELATION,
                        e -> new SingleValueEmbeddableFlowWidget(scene, e),
                        sourcePersistenceClassWidget, embeddedAttributeWidget);
            }
            for (MultiValueEmbeddedAttributeWidget embeddedAttributeWidget : sourcePersistenceClassWidget.getMultiValueEmbeddedAttributeWidgets()) {
                loadEmbeddedEdge(scene, MULTI_EMBEDDABLE_RELATION,
                        e -> new MultiValueEmbeddableFlowWidget(scene, e),
                        sourcePersistenceClassWidget, embeddedAttributeWidget);
            }

            for (OTORelationAttributeWidget sourceAttrWidget : sourcePersistenceClassWidget.getOneToOneRelationAttributeWidgets()) {
                loadRelationEdge(scene, OTO_RELATION,
                        sourceAttrWidget.getBaseElementSpec().getConnectedAttribute()==null?
                                e -> new UOTORelationFlowWidget(scene, e) : e -> new BOTORelationFlowWidget(scene, e),
                        sourcePersistenceClassWidget, sourceAttrWidget, OTORelationAttributeWidget.class);
            }
            for (OTMRelationAttributeWidget sourceAttrWidget : sourcePersistenceClassWidget.getOneToManyRelationAttributeWidgets()) {
                loadRelationEdge(scene, OTM_RELATION,
                        sourceAttrWidget.getBaseElementSpec().getConnectedAttribute()==null?
                                e -> new UOTMRelationFlowWidget(scene, e) : null, sourcePersistenceClassWidget, sourceAttrWidget, OTMRelationAttributeWidget.class);
            }
            for (MTORelationAttributeWidget sourceAttrWidget : sourcePersistenceClassWidget.getManyToOneRelationAttributeWidgets()) {
                loadRelationEdge(scene, MTO_RELATION,
                        sourceAttrWidget.getBaseElementSpec().getConnectedAttribute()==null?
                                e -> new UMTORelationFlowWidget(scene, e) : e -> new BMTORelationFlowWidget(scene, e), sourcePersistenceClassWidget, sourceAttrWidget, OTMRelationAttributeWidget.class);
            }
            for (MTMRelationAttributeWidget sourceAttrWidget : sourcePersistenceClassWidget.getManyToManyRelationAttributeWidgets()) {
                loadRelationEdge(scene, MTM_RELATION,
                        sourceAttrWidget.getBaseElementSpec().getConnectedAttribute()==null?
                                e -> new UMTMRelationFlowWidget(scene, e) : e -> new BMTMRelationFlowWidget(scene, e), sourcePersistenceClassWidget, sourceAttrWidget, MTMRelationAttributeWidget.class);
            }
        } else if (javaClassWidget instanceof BeanClassWidget) {
            BeanClassWidget sourceClassWidget = (BeanClassWidget) javaClassWidget;

            for (OTOAssociationAttributeWidget sourceAttrWidget : sourceClassWidget.getOneToOneAssociationAttributeWidgets()) {
                loadAssociationEdge(scene, OTO_ASSOCIATION,
                        sourceAttrWidget.getBaseElementSpec().getConnectedAttribute()==null?
                                e -> new UOTOAssociationFlowWidget(scene, e) : e -> new BOTOAssociationFlowWidget(scene, e),
                        sourceClassWidget, sourceAttrWidget, OTOAssociationAttributeWidget.class);
            }
            for (OTMAssociationAttributeWidget sourceAttrWidget : sourceClassWidget.getOneToManyAssociationAttributeWidgets()) {
                loadAssociationEdge(scene, OTM_ASSOCIATION,
                        sourceAttrWidget.getBaseElementSpec().getConnectedAttribute()==null?
                                e -> new UOTMAssociationFlowWidget(scene, e) : null, sourceClassWidget, sourceAttrWidget, OTMAssociationAttributeWidget.class);
            }
            for (MTOAssociationAttributeWidget sourceAttrWidget : sourceClassWidget.getManyToOneAssociationAttributeWidgets()) {
                loadAssociationEdge(scene, MTO_ASSOCIATION,
                        sourceAttrWidget.getBaseElementSpec().getConnectedAttribute()==null?
                                e -> new UMTOAssociationFlowWidget(scene, e) : e -> new BMTOAssociationFlowWidget(scene, e), sourceClassWidget, sourceAttrWidget, OTMAssociationAttributeWidget.class);
            }
            for (MTMAssociationAttributeWidget sourceAttrWidget : sourceClassWidget.getManyToManyAssociationAttributeWidgets()) {
                loadAssociationEdge(scene, MTM_ASSOCIATION,
                        sourceAttrWidget.getBaseElementSpec().getConnectedAttribute()==null?
                                e -> new UMTMAssociationFlowWidget(scene, e) : e -> new BMTMAssociationFlowWidget(scene, e), sourceClassWidget, sourceAttrWidget, MTMAssociationAttributeWidget.class);
            }
        }
    }

    private void loadEmbeddedEdge(JPAModelerScene scene, 
            String contextToolId,
            Function<EdgeWidgetInfo, IEdgeWidget> edgeWidgetFunction,
            PersistenceClassWidget sourcePersistenceClassWidget,
            EmbeddedAttributeWidget sourceAttributeWidget) {
        CompositionAttribute sourceEmbeddedAttribute = (CompositionAttribute) sourceAttributeWidget.getBaseElementSpec();
        EmbeddableWidget targetEntityWidget = (EmbeddableWidget) scene.getBaseElement(sourceEmbeddedAttribute.getConnectedClass().getId());
        EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo(edgeWidgetFunction);
        edgeInfo.setSource(sourcePersistenceClassWidget.getNodeWidgetInfo().getId());
        edgeInfo.setTarget(targetEntityWidget.getNodeWidgetInfo().getId());
        edgeInfo.setType(contextToolId);
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);
        scene.setEdgeWidgetSource(edgeInfo, edgeWidget.getSourcePinWidget(sourcePersistenceClassWidget, targetEntityWidget, sourceAttributeWidget));
        scene.setEdgeWidgetTarget(edgeInfo, edgeWidget.getTargetPinWidget(sourcePersistenceClassWidget, targetEntityWidget, null));
    }

    private void loadRelationEdge(JPAModelerScene scene,
            String contextToolId, 
            Function<EdgeWidgetInfo, IEdgeWidget> edgeWidgetFunction,
            JavaClassWidget sourceClassWidget, 
            RelationAttributeWidget sourceRelationAttributeWidget, 
            Class<? extends RelationAttributeWidget>... targetRelationAttributeWidgetClass) {

        RelationAttribute sourceRelationAttribute = (RelationAttribute) sourceRelationAttributeWidget.getBaseElementSpec();

        if (!sourceRelationAttribute.isOwner()) {  //Only Owner will draw edge because in any case uni/bi owner is always exist
            return;
        }

        EntityWidget targetEntityWidget = (EntityWidget) scene.getBaseElement(sourceRelationAttribute.getConnectedEntity().getId());
        RelationAttributeWidget targetRelationAttributeWidget = null;

        EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo(edgeWidgetFunction);
        edgeInfo.setSource(sourceClassWidget.getNodeWidgetInfo().getId());
        edgeInfo.setTarget(targetEntityWidget.getNodeWidgetInfo().getId());
        boolean primaryKey = sourceRelationAttributeWidget.getBaseElementSpec() instanceof SingleRelationAttribute && ((SingleRelationAttribute) sourceRelationAttributeWidget.getBaseElementSpec()).isPrimaryKey();
        if (sourceRelationAttribute.getConnectedAttribute() != null) {
            targetRelationAttributeWidget = targetEntityWidget.findRelationAttributeWidget(sourceRelationAttribute.getConnectedAttribute().getId(), targetRelationAttributeWidgetClass);
            contextToolId = "B" + contextToolId;//OTM_RELATION";
        } else {
            contextToolId = "U" + contextToolId;
        }
        edgeInfo.setType(primaryKey ? "PK" + contextToolId : contextToolId);
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);

        scene.setEdgeWidgetSource(edgeInfo, edgeWidget.getSourcePinWidget(sourceClassWidget, targetEntityWidget, sourceRelationAttributeWidget));
        scene.setEdgeWidgetTarget(edgeInfo, edgeWidget.getTargetPinWidget(sourceClassWidget, targetEntityWidget, targetRelationAttributeWidget));
        ((IBaseElementWidget) edgeWidget.getSourceAnchor().getRelatedWidget()).onConnection();
        ((IBaseElementWidget) edgeWidget.getTargetAnchor().getRelatedWidget()).onConnection();
        ((IBaseElementWidget) edgeWidget).onConnection();
    }

    private void loadAssociationEdge(JPAModelerScene scene,
            String contextToolId, 
            Function<EdgeWidgetInfo, IEdgeWidget> edgeWidgetFunction,
            JavaClassWidget sourceClassWidget, 
            AssociationAttributeWidget sourceAssociationAttributeWidget, 
            Class<? extends AssociationAttributeWidget>... targetAssociationAttributeWidgetClass) {

        AssociationAttribute sourceAssociationAttribute = (AssociationAttribute) sourceAssociationAttributeWidget.getBaseElementSpec();

        if (edgeWidgetFunction == null) {  //Only Owner will draw edge because in any case uni/bi owner is always exist
            return;
        }

        BeanClassWidget targetClassWidget = (BeanClassWidget) scene.getBaseElement(sourceAssociationAttribute.getConnectedClass().getId());
        AssociationAttributeWidget targetAssociationAttributeWidget = null;

        EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo(edgeWidgetFunction);
        edgeInfo.setSource(sourceClassWidget.getNodeWidgetInfo().getId());
        edgeInfo.setTarget(targetClassWidget.getNodeWidgetInfo().getId());
        if (sourceAssociationAttribute.getConnectedAttribute() != null) {
            targetAssociationAttributeWidget = targetClassWidget.findAssociationAttributeWidget(sourceAssociationAttribute.getConnectedAttribute().getId(), targetAssociationAttributeWidgetClass);
            contextToolId = "B" + contextToolId;
        } else {
            contextToolId = "U" + contextToolId;
        }
        edgeInfo.setType(contextToolId);
        IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);

        scene.setEdgeWidgetSource(edgeInfo, edgeWidget.getSourcePinWidget(sourceClassWidget, targetClassWidget, sourceAssociationAttributeWidget));
        scene.setEdgeWidgetTarget(edgeInfo, edgeWidget.getTargetPinWidget(sourceClassWidget, targetClassWidget, targetAssociationAttributeWidget));
        ((IBaseElementWidget) edgeWidget.getSourceAnchor().getRelatedWidget()).onConnection();
        ((IBaseElementWidget) edgeWidget.getTargetAnchor().getRelatedWidget()).onConnection();
        ((IBaseElementWidget) edgeWidget).onConnection();
    }

        
    private void loadGeneralization(JPAModelerScene scene, JavaClassWidget javaClassWidget) {
        JavaClass javaClass = (JavaClass) javaClassWidget.getBaseElementSpec();
        if (javaClass.getSuperclass() != null) {
            JavaClassWidget subClassWidget = javaClassWidget;
            JavaClassWidget superClassWidget = (JavaClassWidget) scene.getBaseElement(javaClass.getSuperclass().getId());
            EdgeWidgetInfo edgeInfo = new EdgeWidgetInfo(e -> new GeneralizationFlowWidget(scene, e));
            edgeInfo.setSource(subClassWidget.getNodeWidgetInfo().getId());
            edgeInfo.setTarget(superClassWidget.getNodeWidgetInfo().getId());
            edgeInfo.setType("GENERALIZATION");

            IEdgeWidget edgeWidget = scene.createEdgeWidget(edgeInfo);
            scene.setEdgeWidgetSource(edgeInfo, edgeWidget.getSourcePinWidget(subClassWidget, superClassWidget));
            scene.setEdgeWidgetTarget(edgeInfo, edgeWidget.getTargetPinWidget(subClassWidget, superClassWidget));
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

    /**
     * For internal invocation, call ModelerFile.save(true) to save file
     *
     * @param file
     */
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

    private static void saveFile(EntityMappings entityMappings, File file) {
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

//    public IEdgeWidget attachEdgeWidget(JPAModelerScene scene, EdgeWidgetInfo widgetInfo) {
//        IEdgeWidget edgeWidget = getEdgeWidget(scene, widgetInfo);
//        edgeWidget.setEndPointShape(PointShape.SQUARE_FILLED_SMALL);
//        edgeWidget.setControlPointShape(PointShape.SQUARE_FILLED_SMALL);
//        edgeWidget.setRouter(scene.getRouter());
//        ((IFlowEdgeWidget) edgeWidget).setName(widgetInfo.getName());

    public static void initEntityModel(javax.swing.JComboBox entityComboBox, EntityMappings entityMappings) {
        entityComboBox.removeAllItems();
        entityComboBox.addItem(new ComboBoxValue(null, ""));
        entityMappings.getEntity().forEach((entity) -> {
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

    private <P extends Object, T extends Object> void copyRef(P parentElement, T element, P parentClonedElement, T clonedElement, List<T> clonedElements) {
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
            if (((JavaClass) clonedElement).getAttributes() instanceof IPersistenceAttributes) {
                IPersistenceAttributes persistenceAttributes = (IPersistenceAttributes) ((JavaClass) clonedElement).getAttributes();
                persistenceAttributes.removeNonOwnerAttribute(new HashSet<>((List<JavaClass>) clonedElements));
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
                if (((RelationAttribute) clonedElement).getConnectedEntity() == null) { //if not self
                    if (((RelationAttribute) clonedElement).getConnectedEntity() == null) {
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
