/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.reveng.doc;

import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.getEntityMapping;
import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.getModelerFileVersion;
import io.github.jeddict.jpa.spec.Basic;
import io.github.jeddict.jpa.spec.ElementCollection;
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.Embedded;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.ManyToMany;
import io.github.jeddict.jpa.spec.ManyToOne;
import io.github.jeddict.jpa.spec.MappedSuperclass;
import io.github.jeddict.jpa.spec.OneToMany;
import io.github.jeddict.jpa.spec.OneToOne;
import io.github.jeddict.jpa.spec.Transient;
import io.github.jeddict.jpa.spec.Version;
import io.github.jeddict.jpa.spec.bean.BeanAttribute;
import io.github.jeddict.jpa.spec.bean.BeanAttributes;
import io.github.jeddict.jpa.spec.bean.BeanClass;
import io.github.jeddict.jpa.spec.bean.BeanCollectionAttribute;
import io.github.jeddict.jpa.spec.bean.ManyToManyAssociation;
import io.github.jeddict.jpa.spec.bean.ManyToOneAssociation;
import io.github.jeddict.jpa.spec.bean.OneToManyAssociation;
import io.github.jeddict.jpa.spec.bean.OneToOneAssociation;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.IPersistenceAttributes;
import io.github.jeddict.jpa.spec.extend.IPrimaryKeyAttributes;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import io.github.jeddict.jpa.spec.extend.PersistenceAttributes;
import io.github.jeddict.jpa.spec.workspace.WorkSpace;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import java.util.function.Consumer;
import javax.xml.bind.JAXBException;
import org.netbeans.modeler.core.exception.ProcessInterruptedException;
import org.openide.util.Exceptions;
import static org.openide.util.NbBundle.getMessage;

/**
 *
 * @author jGauravGupta
 */
public class ModelerParser extends DocParser {

    public ModelerParser(Consumer<String> reporter, boolean jpaSupport, boolean jsonbSupport, boolean jaxbSupport) {
        super(reporter, jpaSupport, jsonbSupport, jaxbSupport);
    }

    @Override
    public EntityMappings generateModel(EntityMappings targetEM, Reader reader) throws IOException, ProcessInterruptedException {
        try {
            String progressMsg = getMessage(DocWizardDescriptor.class, "MSG_Progress_Class_Diagram_Pre"); //NOI18N;
            reporter.accept(progressMsg);
            String version = getModelerFileVersion();

            if (targetEM == null) {
                targetEM = EntityMappings.getNewInstance(version);
                targetEM.setGenerated();
            }
            EntityMappings sourceEM = getEntityMapping(reader);

            for (JavaClass clazz : sourceEM.getAllJavaClass()) {
                clazz.setRootElement(targetEM);
            }

            for (Entity sourceEntity : sourceEM.getEntity()) {
                Optional<Entity> targetEntityOpt = targetEM.findEntity(sourceEntity.getClazz());
                if (!targetEntityOpt.isPresent()) {
                    targetEM.addEntity(sourceEntity);
                } else {
                    for (Attribute sourceAttribute : sourceEntity.getAttributes().getAllAttribute()) {
                        IPrimaryKeyAttributes attributes = targetEntityOpt.get().getAttributes();
                        addAttribute(sourceAttribute, attributes);
                    }
                }
            }

            for (MappedSuperclass sourceMappedSuperclass : sourceEM.getMappedSuperclass()) {
                Optional<MappedSuperclass> targetMappedSuperclassOpt = targetEM.findMappedSuperclass(sourceMappedSuperclass.getClazz());
                if (!targetMappedSuperclassOpt.isPresent()) {
                    targetEM.addMappedSuperclass(sourceMappedSuperclass);
                } else {
                    for (Attribute sourceAttribute : sourceMappedSuperclass.getAttributes().getAllAttribute()) {
                        IPrimaryKeyAttributes attributes = targetMappedSuperclassOpt.get().getAttributes();
                        addAttribute(sourceAttribute, attributes);
                    }
                }
            }

            for (Embeddable sourceEmbeddable : sourceEM.getEmbeddable()) {
                Optional<Embeddable> targetEmbeddableOpt = targetEM.findEmbeddable(sourceEmbeddable.getClazz());
                if (!targetEmbeddableOpt.isPresent()) {
                    targetEM.addEmbeddable(sourceEmbeddable);
                } else {
                    for (Attribute sourceAttribute : sourceEmbeddable.getAttributes().getAllAttribute()) {
                        PersistenceAttributes attributes = targetEmbeddableOpt.get().getAttributes();
                        addAttribute(sourceAttribute, attributes);
                    }
                }
            }

            for (BeanClass sourceBeanClass : sourceEM.getBeanClass()) {
                Optional<BeanClass> targetBeanClassOpt = targetEM.findBeanClass(sourceBeanClass.getClazz());
                if (!targetBeanClassOpt.isPresent()) {
                    targetEM.addBeanClass(sourceBeanClass);
                } else {
                    for (Attribute sourceAttribute : sourceBeanClass.getAttributes().getAllAttribute()) {
                        BeanAttributes attributes = targetBeanClassOpt.get().getAttributes();
                        addAttribute(sourceAttribute, attributes);
                    }
                }
            }

            targetEM.getSequenceGenerator().addAll(sourceEM.getSequenceGenerator());
            targetEM.getTableGenerator().addAll(sourceEM.getTableGenerator());
            targetEM.getNamedQuery().addAll(sourceEM.getNamedQuery());
            targetEM.getNamedNativeQuery().addAll(sourceEM.getNamedNativeQuery());
            targetEM.getNamedStoredProcedureQuery().addAll(sourceEM.getNamedStoredProcedureQuery());
            targetEM.getSqlResultSetMapping().addAll(sourceEM.getSqlResultSetMapping());
            targetEM.getConverter().addAll(sourceEM.getConverter());
            targetEM.getSnippets().addAll(sourceEM.getSnippets());
            targetEM.getInterfaces().addAll(sourceEM.getInterfaces());

            for (WorkSpace targetWS : targetEM.getWorkSpaces()) {
                sourceEM.findWorkSpaceByName(targetWS.getName())
                        .ifPresent(sourceWS -> targetWS.getItems().addAll(sourceWS.getItems()));
            }

        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        }
        return targetEM;
    }

    private void addAttribute(Attribute sourceAttribute, IPersistenceAttributes attributes) {
        if (attributes.findAllAttribute(sourceAttribute.getName()).isEmpty()) {
            if (sourceAttribute instanceof Id && attributes instanceof IPrimaryKeyAttributes) {
                ((IPrimaryKeyAttributes) attributes).addId((Id) sourceAttribute);
            } else if (sourceAttribute instanceof Version && attributes instanceof IPrimaryKeyAttributes) {
                ((IPrimaryKeyAttributes) attributes).addVersion((Version) sourceAttribute);
            } else if (sourceAttribute instanceof Basic) {
                attributes.addBasic((Basic) sourceAttribute);
            } else if (sourceAttribute instanceof ElementCollection) {
                attributes.addElementCollection((ElementCollection) sourceAttribute);
            } else if (sourceAttribute instanceof Embedded) {
                attributes.addEmbedded((Embedded) sourceAttribute);
            } else if (sourceAttribute instanceof Transient) {
                attributes.addTransient((Transient) sourceAttribute);
            } else if (sourceAttribute instanceof OneToOne) {
                attributes.addOneToOne((OneToOne) sourceAttribute);
            } else if (sourceAttribute instanceof OneToMany) {
                attributes.addOneToMany((OneToMany) sourceAttribute);
            } else if (sourceAttribute instanceof ManyToOne) {
                attributes.addManyToOne((ManyToOne) sourceAttribute);
            } else if (sourceAttribute instanceof ManyToMany) {
                attributes.addManyToMany((ManyToMany) sourceAttribute);
            }
        }
    }

    private void addAttribute(Attribute sourceAttribute, BeanAttributes attributes) {
        if (attributes.findAllAttribute(sourceAttribute.getName()).isEmpty()) {
            if (sourceAttribute instanceof BeanAttribute) {
                attributes.addBasic((BeanAttribute) sourceAttribute);
            } else if (sourceAttribute instanceof BeanCollectionAttribute) {
                attributes.addElementCollection((BeanCollectionAttribute) sourceAttribute);
            } else if (sourceAttribute instanceof Transient) {
                attributes.addTransient((Transient) sourceAttribute);
            } else if (sourceAttribute instanceof OneToOneAssociation) {
                attributes.addOneToOne((OneToOneAssociation) sourceAttribute);
            } else if (sourceAttribute instanceof OneToManyAssociation) {
                attributes.addOneToMany((OneToManyAssociation) sourceAttribute);
            } else if (sourceAttribute instanceof ManyToOneAssociation) {
                attributes.addManyToOne((ManyToOneAssociation) sourceAttribute);
            } else if (sourceAttribute instanceof ManyToManyAssociation) {
                attributes.addManyToMany((ManyToManyAssociation) sourceAttribute);
            }
        }
    }

}
