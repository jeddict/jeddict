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
package io.github.jeddict.jpa.modeler.initializer;

import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.EmbeddableAttributes;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.MappedSuperclass;
import io.github.jeddict.jpa.spec.PrimaryKeyAttributes;
import io.github.jeddict.jpa.spec.extend.IPersistenceAttributes;
import io.github.jeddict.jpa.spec.extend.IPrimaryKeyAttributes;
import io.github.jeddict.jpa.spec.extend.RelationAttribute;
import io.github.jeddict.jpa.spec.workspace.WorkSpace;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.ERROR_ICON;
import org.netbeans.modeler.core.ModelerFile;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import io.github.jeddict.relation.mapper.RelationMapper;

/**
 *
 * @author jGauravGupta
 */
public class DBUtil {

    public static void openDBModeler(ModelerFile file) {
        EntityMappings entityMappings = (EntityMappings) file.getModelerScene().getBaseElementSpec();
        openDBModeler(file, entityMappings, entityMappings.getCurrentWorkSpace());
    }

    public static void openDBModeler(ModelerFile file, EntityMappings entityMappings, WorkSpace workSpace) {
        if (!((JPAModelerScene) file.getModelerScene()).compile()) {
            return;
        }
        WorkSpace paramWorkSpace = entityMappings.getRootWorkSpace() == workSpace ? null : workSpace;
        try {
            PreExecutionUtil.preExecution(file);
            RelationMapper relationMapper = Lookup.getDefault().lookup(RelationMapper.class);

            if (relationMapper == null) {
                JOptionPane.showMessageDialog(null,
                        NbBundle.getMessage(JSONBUtil.class, "Error.PLUGIN_INSTALLATION.text", "Relation Mapper", file.getCurrentVersion()),
                        NbBundle.getMessage(JSONBUtil.class, "Error.PLUGIN_INSTALLATION.title"), ERROR_MESSAGE, ERROR_ICON);
            } else {
                //close diagram and reopen 
                file.getChildrenFile("DB").ifPresent(ModelerFile::close);
                relationMapper.init(file, entityMappings, paramWorkSpace);
            }
        } catch (Throwable t) {
            file.handleException(t);
        }
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
