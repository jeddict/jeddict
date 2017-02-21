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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.netbeans.db.modeler.manager.DBModelerRequestManager;
import org.netbeans.jpa.modeler.spec.Attributes;
import org.netbeans.jpa.modeler.spec.Embeddable;
import org.netbeans.jpa.modeler.spec.EmbeddableAttributes;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.jpa.modeler.spec.extend.BaseAttributes;
import org.netbeans.jpa.modeler.spec.extend.IPersistenceAttributes;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpace;
import org.netbeans.jpa.modeler.specification.model.scene.JPAModelerScene;
import org.netbeans.modeler.core.ModelerFile;
import org.netbeans.modeler.specification.model.document.IModelerScene;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.openide.util.Lookup;
import static org.netbeans.jpa.modeler.specification.model.util.JPAModelerUtil.cloneEntityMapping;

/**
 *
 * @author jGauravGupta
 */
public class DBUtil {

    public static EntityMappings isolateEntityMapping(EntityMappings mappings, Entity javaClass, RelationAttribute relationAttribute) {

        EntityMappings mappingClone = cloneEntityMapping(mappings);
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
        if (!((JPAModelerScene) file.getModelerScene()).compile()) {
            return;
        }
        try {
            PreExecutionUtil.preExecution(file);
            DBModelerRequestManager dbModelerRequestManager = Lookup.getDefault().lookup(DBModelerRequestManager.class);//new DefaultSourceCodeGeneratorFactory();//SourceGeneratorFactoryProvider.getInstance();//
            Optional<ModelerFile> dbChildModelerFile = file.getChildrenFile("DB");
            dbModelerRequestManager.init(file, entityMappings);
            IModelerScene scene;
            if (dbChildModelerFile.isPresent()) {
                ModelerFile childModelerFile = dbChildModelerFile.get();
                scene = childModelerFile.getModelerScene();
                scene.getBaseElements().stream().filter(element -> element instanceof INodeWidget).forEach(element -> {
                    ((INodeWidget) element).remove(false);
                });
                childModelerFile.unload();
                try {
                    childModelerFile.getModelerUtil().loadModelerFile(childModelerFile);
                    scene.validate();
                } catch (Exception ex) {
                    file.handleException(ex);
                }
                childModelerFile.loaded();
            } else {
                dbChildModelerFile = file.getChildrenFile("DB");
                if (dbChildModelerFile.isPresent()) {
                    scene = dbChildModelerFile.get().getModelerScene();
                    scene.validate();//TODO remove it// should be called from framework
                }
            }
        } catch (Throwable t) {
            file.handleException(t);
        }
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

    /**
     * Micro DB filter
     *
     * @param mappings The graph
     * @param entity The master node
     * @return
     */
    public static EntityMappings isolateEntityMapping(EntityMappings mappings, Entity entity) {
        EntityMappings mappingClone = cloneEntityMapping(mappings);
        Entity entityClone = mappingClone.getEntity(entity.getId());

        Set<Entity> connectedEntities = new HashSet<>();
        connectedEntities.add(entityClone);

        //Owner
        connectedEntities.addAll(entityClone.getAttributes().getRelationConnectedClassRef());

        //Inverse Owner
        connectedEntities.addAll(mappingClone.getEntity()
                .stream()
                .filter(e -> e.getAttributes().getRelationAttributes().stream().anyMatch(r -> r.getConnectedEntity() == entityClone))
                .collect(toSet()));

        //Inheritance
        connectedEntities.addAll(connectedEntities
                .stream()
                .flatMap(ce -> ce.getAllSuperclass().stream().filter(sc -> sc instanceof Entity).map(sc -> (Entity) sc))
                .collect(toList()));

        connectedEntities.remove(entityClone);

        connectedEntities.stream()
                .map(e -> e.getAttributes())
                .forEach(attr -> {
                    attr.getManyToMany().removeIf(r -> r.getConnectedEntity() != entityClone);
                    attr.getManyToOne().removeIf(r -> r.getConnectedEntity() != entityClone);
                    attr.getOneToMany().removeIf(r -> r.getConnectedEntity() != entityClone);
                    attr.getOneToOne().removeIf(r -> r.getConnectedEntity() != entityClone);
                    attr.setEmbedded(null);
                });

        connectedEntities.add(entityClone);

        //setup Entity
        mappingClone.setEntity(connectedEntities.stream().collect(toList()));

        mapToOrignalObject(mappings, mappingClone);
        return mappingClone;
    }

    /**
     * WorkSpace DB filter
     *
     * @param mappings The graph
     * @param workSpace The master node
     * @return
     */
    public static EntityMappings isolateEntityMapping(EntityMappings mappings, WorkSpace workSpace) {
        EntityMappings mappingClone = cloneEntityMapping(mappings);
        WorkSpace workSpaceClone = mappingClone.getCurrentWorkSpace();
        
        mappingClone.setEntity(
                mappingClone.getEntity()
                        .stream()
                        .filter(jc -> workSpaceClone.hasItem(jc))
                        .collect(toList())
        );
        mappingClone.setMappedSuperclass(
                mappingClone.getMappedSuperclass()
                        .stream()
                        .filter(jc -> workSpaceClone.hasItem(jc))
                        .collect(toList())
        );
        mappingClone.setEmbeddable(
                mappingClone.getEmbeddable()
                        .stream()
                        .filter(jc -> workSpaceClone.hasItem(jc))
                        .collect(toList())
        );
        
        mappingClone.getManagedClass()
                .stream()
                .forEach(jc -> isolateClass(jc, workSpaceClone));
        
        mapToOrignalObject(mappings, mappingClone);
        return mappingClone;
    }
    
    private static void isolateClass(ManagedClass classSpec, WorkSpace workSpace){
//        if (classSpec.getAttributes() instanceof IPersistenceAttributes) {
//                    IPersistenceAttributes persistenceAttributes = (IPersistenceAttributes) classSpec.getAttributes();
//                    EmbeddedId embeddedId = persistenceAttributes.getEmbeddedId();
//                    if (embeddedId != null && !workSpace.hasItem(embeddedId.getConnectedClass())) {
//                        persistenceAttributes.set
//                    }
//                }
        classSpec.getAttributes().setEmbedded(
                classSpec.getAttributes().getEmbedded()
                        .stream()
                        .filter(embedded -> workSpace.hasItem(embedded.getConnectedClass()))
                        .collect(toList())
        );
        classSpec.getAttributes().setElementCollection(
                classSpec.getAttributes().getElementCollection()
                        .stream()
                        .filter(ec -> ec.getConnectedClass() != null)
                        .filter(ec -> workSpace.hasItem(ec.getConnectedClass()))
                        .collect(toList())
        );     
        classSpec.getAttributes().setOneToOne(
                classSpec.getAttributes().getOneToOne()
                        .stream()
                        .filter(oto -> workSpace.hasItem(oto.getConnectedEntity()))
                        .collect(toList())
        );
        classSpec.getAttributes().setOneToMany(
                classSpec.getAttributes().getOneToMany()
                        .stream()
                        .filter(otm -> workSpace.hasItem(otm.getConnectedEntity()))
                        .collect(toList())
        );
        classSpec.getAttributes().setManyToOne(
                classSpec.getAttributes().getManyToOne()
                        .stream()
                        .filter(mto -> workSpace.hasItem(mto.getConnectedEntity()))
                        .collect(toList())
        );
        classSpec.getAttributes().setManyToMany(
                classSpec.getAttributes().getManyToMany()
                        .stream()
                        .filter(mtm -> workSpace.hasItem(mtm.getConnectedEntity()))
                        .collect(toList())
        );
    }

}
