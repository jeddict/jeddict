/**
 * Copyright [2017  ] Gaurav Gupta
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

import java.util.Date;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import org.netbeans.db.modeler.manager.DBModelerRequestManager;
import org.netbeans.jpa.modeler.spec.PrimaryKeyAttributes;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.EmbeddableAttributes;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.jpa.modeler.spec.extend.IPersistenceAttributes;
import org.netbeans.jpa.modeler.spec.extend.IPrimaryKeyAttributes;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpace;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.ERROR_ICON;
import org.netbeans.modeler.core.ModelerFile;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author jGauravGupta
 */
public class DBUtil {

    public static void openDBViewer(ModelerFile file) {
        EntityMappings entityMappings = (EntityMappings) file.getModelerScene().getBaseElementSpec();
        openDBViewer(file, entityMappings, entityMappings.getCurrentWorkSpace());
    }

    public static void openDBViewer(ModelerFile file, EntityMappings entityMappings, WorkSpace workSpace) {
        if (!((JPAModelerScene) file.getModelerScene()).compile()) {
            return;
        }
        WorkSpace paramWorkSpace = entityMappings.getRootWorkSpace() == workSpace ? null : workSpace;
        try {
            PreExecutionUtil.preExecution(file);
            DBModelerRequestManager dbModelerRequestManager = Lookup.getDefault().lookup(DBModelerRequestManager.class);

            if (dbModelerRequestManager == null) {
                JOptionPane.showMessageDialog(null,
                        NbBundle.getMessage(JSONBUtil.class, "Error.PLUGIN_INSTALLATION.text", "DB Modeler", file.getCurrentVersion()),
                        NbBundle.getMessage(JSONBUtil.class, "Error.PLUGIN_INSTALLATION.title"), ERROR_MESSAGE, ERROR_ICON);
            } else {
                //close diagram and reopen 
                long st = new Date().getTime();
                file.getChildrenFile("DB").ifPresent(modelerFile -> modelerFile.getModelerPanelTopComponent().close());
                System.out.println("openDBViewer close Total time : " + (new Date().getTime() - st) + " ms");
                dbModelerRequestManager.init(file, entityMappings, paramWorkSpace);
                System.out.println("openDBViewer Total time : " + (new Date().getTime() - st) + " ms");
            }
        } catch (Throwable t) {
            file.handleException(t);
        }
    }

    @Deprecated
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

    @Deprecated
    private static void mapToOrignalObject(IPersistenceAttributes orignalAttributes, IPersistenceAttributes clonedAttributes) {

        clonedAttributes.getBasic().forEach(a -> {
            orignalAttributes.getBasic(a.getId()).ifPresent(attr -> a.setOrignalObject(attr));
        });
        clonedAttributes.getElementCollection().forEach(a -> {
            orignalAttributes.getElementCollection(a.getId()).ifPresent(attr -> a.setOrignalObject(attr));
        });
        clonedAttributes.getEmbedded().forEach(a -> {
            orignalAttributes.getEmbedded(a.getId()).ifPresent(attr -> a.setOrignalObject(attr));
        });

        clonedAttributes.getManyToMany().forEach(a -> {
            orignalAttributes.getManyToMany(a.getId()).ifPresent(attr -> a.setOrignalObject(attr));
        });
        clonedAttributes.getManyToOne().forEach(a -> {
            orignalAttributes.getManyToOne(a.getId()).ifPresent(attr -> a.setOrignalObject(attr));
        });
        clonedAttributes.getOneToMany().forEach(a -> {
            orignalAttributes.getOneToMany(a.getId()).ifPresent(attr -> a.setOrignalObject(attr));
        });
        clonedAttributes.getOneToOne().forEach(a -> {
            orignalAttributes.getOneToOne(a.getId()).ifPresent(attr -> a.setOrignalObject(attr));
        });
        clonedAttributes.getTransient().forEach(a -> {
            orignalAttributes.getTransient(a.getId()).ifPresent(attr -> a.setOrignalObject(attr));
        });
        if (clonedAttributes instanceof PrimaryKeyAttributes) {
            ((PrimaryKeyAttributes) clonedAttributes).getId().forEach(a -> {
                ((PrimaryKeyAttributes) orignalAttributes).getId(a.getId()).ifPresent(attr -> a.setOrignalObject(attr));
            });
            ((PrimaryKeyAttributes) clonedAttributes).getVersion().forEach(a -> {
                ((PrimaryKeyAttributes) orignalAttributes).getVersion(a.getId()).ifPresent(attr -> a.setOrignalObject(attr));
            });
        }

    }

    @Deprecated
    private static void makeSiblingOrphan(Entity entity, RelationAttribute relationAttribute, Entity siblingEntity, RelationAttribute siblingRelationAttribute) {
        IPrimaryKeyAttributes attr = entity.getAttributes();
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

    @Deprecated
    private static void makeSiblingOrphan(Embeddable embeddable) {
        EmbeddableAttributes attr = embeddable.getAttributes();
        attr.setManyToMany(null);
        attr.setManyToOne(null);
        attr.setOneToMany(null);
        attr.setOneToOne(null);
        attr.setEmbedded(null);
        attr.setElementCollection(null);
    }

    @Deprecated
    private static void makeSiblingOrphan(MappedSuperclass mappedSuperclass) {
        IPrimaryKeyAttributes attr = mappedSuperclass.getAttributes();
        attr.setManyToMany(null);
        attr.setManyToOne(null);
        attr.setOneToMany(null);
        attr.setOneToOne(null);
        attr.setEmbedded(null);
        attr.setElementCollection(null);
    }

}
