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
package org.netbeans.orm.converter.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.AttributeOverride;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.CascadeType;
import org.netbeans.jpa.modeler.spec.CollectionTable;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.ColumnResult;
import org.netbeans.jpa.modeler.spec.ConstructorResult;
import org.netbeans.jpa.modeler.spec.DefaultClass;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.EmptyType;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.EntityListener;
import org.netbeans.jpa.modeler.spec.EntityListeners;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.EntityResult;
import org.netbeans.jpa.modeler.spec.EnumType;
import org.netbeans.jpa.modeler.spec.FetchType;
import org.netbeans.jpa.modeler.spec.FieldResult;
import org.netbeans.jpa.modeler.spec.ForeignKey;
import org.netbeans.jpa.modeler.spec.GeneratedValue;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.IdClass;
import org.netbeans.jpa.modeler.spec.IdentifiableClass;
import org.netbeans.jpa.modeler.spec.Index;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.JoinTable;
import org.netbeans.jpa.modeler.spec.Lob;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.NamedAttributeNode;
import org.netbeans.jpa.modeler.spec.NamedEntityGraph;
import org.netbeans.jpa.modeler.spec.NamedNativeQuery;
import org.netbeans.jpa.modeler.spec.NamedQuery;
import org.netbeans.jpa.modeler.spec.NamedStoredProcedureQuery;
import org.netbeans.jpa.modeler.spec.NamedSubgraph;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.PrimaryKeyJoinColumn;
import org.netbeans.jpa.modeler.spec.QueryHint;
import org.netbeans.jpa.modeler.spec.SecondaryTable;
import org.netbeans.jpa.modeler.spec.SequenceGenerator;
import org.netbeans.jpa.modeler.spec.SqlResultSetMapping;
import org.netbeans.jpa.modeler.spec.StoredProcedureParameter;
import org.netbeans.jpa.modeler.spec.Table;
import org.netbeans.jpa.modeler.spec.TableGenerator;
import org.netbeans.jpa.modeler.spec.TemporalType;
import org.netbeans.jpa.modeler.spec.Transient;
import org.netbeans.jpa.modeler.spec.UniqueConstraint;
import org.netbeans.jpa.modeler.spec.Version;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.ClassMembers;
import org.netbeans.jpa.modeler.spec.extend.CompositePrimaryKeyType;
import org.netbeans.jpa.modeler.spec.extend.Constructor;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.JoinColumnHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyType;
import org.netbeans.jpa.modeler.spec.extend.Snippet;
import org.netbeans.jpa.modeler.spec.extend.SnippetLocationType;
import org.netbeans.jpa.modeler.spec.extend.annotation.Annotation;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;
import org.netbeans.jpa.modeler.spec.validation.constraints.Constraint;
import org.netbeans.jpa.modeler.spec.validator.SequenceGeneratorValidator;
import org.netbeans.jpa.modeler.spec.validator.TableGeneratorValidator;
import org.netbeans.jpa.modeler.spec.validator.column.ForeignKeyValidator;
import org.netbeans.jpa.modeler.spec.validator.column.JoinColumnValidator;
import org.netbeans.jpa.modeler.spec.validator.table.CollectionTableValidator;
import org.netbeans.jpa.modeler.spec.validator.table.JoinTableValidator;
import org.netbeans.jpa.modeler.spec.validator.table.TableValidator;
import org.netbeans.orm.converter.compiler.AnnotationSnippet;
import org.netbeans.orm.converter.compiler.AssociationOverrideSnippet;
import org.netbeans.orm.converter.compiler.AssociationOverridesSnippet;
import org.netbeans.orm.converter.compiler.AttributeOverrideSnippet;
import org.netbeans.orm.converter.compiler.AttributeOverridesSnippet;
import org.netbeans.orm.converter.compiler.BasicSnippet;
import org.netbeans.orm.converter.compiler.CacheableDefSnippet;
import org.netbeans.orm.converter.compiler.ClassDefSnippet;
import org.netbeans.orm.converter.compiler.CollectionTableSnippet;
import org.netbeans.orm.converter.compiler.ColumnDefSnippet;
import org.netbeans.orm.converter.compiler.ColumnResultSnippet;
import org.netbeans.orm.converter.compiler.validation.constraints.ConstraintSnippet;
import org.netbeans.orm.converter.compiler.validation.constraints.ConstraintSnippetFactory;
import org.netbeans.orm.converter.compiler.ConstructorResultSnippet;
import org.netbeans.orm.converter.compiler.ConstructorSnippet;
import org.netbeans.orm.converter.compiler.ElementCollectionSnippet;
import org.netbeans.orm.converter.compiler.EntityListenerSnippet;
import org.netbeans.orm.converter.compiler.EntityListenersSnippet;
import org.netbeans.orm.converter.compiler.EntityResultSnippet;
import org.netbeans.orm.converter.compiler.EnumeratedSnippet;
import org.netbeans.orm.converter.compiler.EqualsMethodSnippet;
import org.netbeans.orm.converter.compiler.FieldResultSnippet;
import org.netbeans.orm.converter.compiler.ForeignKeySnippet;
import org.netbeans.orm.converter.compiler.GeneratedValueSnippet;
import org.netbeans.orm.converter.compiler.HashcodeMethodSnippet;
import org.netbeans.orm.converter.compiler.IdClassSnippet;
import org.netbeans.orm.converter.compiler.IndexSnippet;
import org.netbeans.orm.converter.compiler.JoinColumnSnippet;
import org.netbeans.orm.converter.compiler.JoinColumnsSnippet;
import org.netbeans.orm.converter.compiler.JoinTableSnippet;
import org.netbeans.orm.converter.compiler.ManyToManySnippet;
import org.netbeans.orm.converter.compiler.ManyToOneSnippet;
import org.netbeans.orm.converter.compiler.MapKeySnippet;
import org.netbeans.orm.converter.compiler.NamedAttributeNodeSnippet;
import org.netbeans.orm.converter.compiler.NamedEntityGraphSnippet;
import org.netbeans.orm.converter.compiler.NamedEntityGraphsSnippet;
import org.netbeans.orm.converter.compiler.NamedNativeQueriesSnippet;
import org.netbeans.orm.converter.compiler.NamedNativeQuerySnippet;
import org.netbeans.orm.converter.compiler.NamedQueriesSnippet;
import org.netbeans.orm.converter.compiler.NamedQueryDefSnippet;
import org.netbeans.orm.converter.compiler.NamedStoredProcedureQueriesSnippet;
import org.netbeans.orm.converter.compiler.NamedStoredProcedureQuerySnippet;
import org.netbeans.orm.converter.compiler.NamedSubgraphSnippet;
import org.netbeans.orm.converter.compiler.OneToManySnippet;
import org.netbeans.orm.converter.compiler.OneToOneSnippet;
import org.netbeans.orm.converter.compiler.OrderBySnippet;
import org.netbeans.orm.converter.compiler.PrimaryKeyJoinColumnSnippet;
import org.netbeans.orm.converter.compiler.PrimaryKeyJoinColumnsSnippet;
import org.netbeans.orm.converter.compiler.QueryHintSnippet;
import org.netbeans.orm.converter.compiler.SQLResultSetMappingSnippet;
import org.netbeans.orm.converter.compiler.SQLResultSetMappingsSnippet;
import org.netbeans.orm.converter.compiler.SecondaryTableSnippet;
import org.netbeans.orm.converter.compiler.SecondaryTablesSnippet;
import org.netbeans.orm.converter.compiler.SequenceGeneratorSnippet;
import org.netbeans.orm.converter.compiler.StoredProcedureParameterSnippet;
import org.netbeans.orm.converter.compiler.TableDefSnippet;
import org.netbeans.orm.converter.compiler.TableGeneratorSnippet;
import org.netbeans.orm.converter.compiler.TemporalSnippet;
import org.netbeans.orm.converter.compiler.ToStringMethodSnippet;
import org.netbeans.orm.converter.compiler.UniqueConstraintSnippet;
import org.netbeans.orm.converter.compiler.VariableDefSnippet;
import org.netbeans.orm.converter.compiler.extend.AssociationOverridesHandler;
import org.netbeans.orm.converter.compiler.extend.AttributeOverridesHandler;
import org.netbeans.orm.converter.util.ClassHelper;
import org.netbeans.orm.converter.util.ORMConvLogger;
import org.netbeans.orm.converter.util.ORMConverterUtil;

public abstract class ClassGenerator<T extends ClassDefSnippet> {

    private static final Logger logger = ORMConvLogger.getLogger(ClassGenerator.class);

    protected String rootPackageName;
    protected String packageName;
    protected T classDef;
    protected Map<String, VariableDefSnippet> variables = new LinkedHashMap<>();

    public ClassGenerator(T classDef) {
        this.classDef = classDef;
    }

    public abstract T getClassDef();

    protected T initClassDef(String packageName, JavaClass javaClass) {
        ClassHelper classHelper = new ClassHelper(javaClass.getClazz());
        classHelper.setPackageName(packageName);
        classDef.setClassName(classHelper.getFQClassName());
//        classDef.setPackageName(classHelper.getPackageName());
        classDef.setAbstractClass(javaClass.getAbstract());
        classDef.setInterfaces(javaClass.getInterfaces());
        classDef.setAnnotation(getAnnotationSnippet(javaClass.getAnnotation()));
        
        List<Snippet> snippets = new ArrayList<>(javaClass.getRootElement().getSnippets());
        snippets.addAll(javaClass.getSnippets());
        classDef.setCustomSnippet(getCustomSnippet(snippets));
        
        classDef.setVariableDefs(new ArrayList<>(variables.values()));

        classDef.setConstructors(getConstructorSnippets(javaClass));
        classDef.setHashcodeMethod(getHashcodeMethodSnippet(javaClass, getClassMembers(javaClass, javaClass.getHashCodeMethod())));
        classDef.setEqualsMethod(getEqualsMethodSnippet(javaClass, getClassMembers(javaClass, javaClass.getEqualsMethod())));
        classDef.setToStringMethod(getToStringMethodSnippet(javaClass, getClassMembers(javaClass, javaClass.getToStringMethod())));
        
        
        if (javaClass.getSuperclass() != null) {
            ClassHelper superClassHelper = new ClassHelper(javaClass.getSuperclass().getClazz());
            superClassHelper.setPackageName(javaClass.getSuperclass().getPackage(rootPackageName));
            classDef.setSuperClassName(superClassHelper.getFQClassName());
        }
        return classDef;
    }
    
    
    private ClassMembers getClassMembers(JavaClass javaClass, ClassMembers classMembers){
        if (javaClass instanceof DefaultClass) {
            classMembers = new ClassMembers();
            for (VariableDefSnippet variableDefSnippet : variables.values()) {
                if (variableDefSnippet.getAttribute() != null) {
                    classMembers.addAttribute(variableDefSnippet.getAttribute());
                }
            }
        }
        return classMembers;
    }

    protected ColumnDefSnippet getColumnDef(Column column) {
        return getColumnDef(column, false);
    }

    protected ColumnDefSnippet getColumnDef(Column column, boolean mapKey) {

        if (column == null) {
            return null;
        }

        ColumnDefSnippet columnDef = new ColumnDefSnippet(mapKey);

        columnDef.setColumnDefinition(column.getColumnDefinition());
        columnDef.setName(column.getName());
        columnDef.setTable(column.getTable());
        columnDef.setInsertable(column.getInsertable());
        columnDef.setNullable(column.getNullable());
        columnDef.setUnique(column.getUnique());
        columnDef.setUpdatable(column.getUpdatable());

        if (column.getLength() != null) {
            columnDef.setLength(column.getLength());
        }

        if (column.getPrecision() != null) {
            columnDef.setPrecision(column.getPrecision());
        }

        if (column.getScale() != null) {
            columnDef.setScale(column.getScale());
        }
        if (columnDef.isEmptyObject()) {
            columnDef = null;
        }

        return columnDef;
    }

    protected List<AnnotationSnippet> getAnnotationSnippet(List<Annotation> annotations) {
        List<AnnotationSnippet> snippets = new ArrayList<>();
        for (Annotation annotation : annotations) {
            if(annotation.isEnable()){
                AnnotationSnippet snippet = new AnnotationSnippet();
                snippet.setName(annotation.getName());
                snippets.add(snippet);
            }
        }
        return snippets;
    }
    protected Map<SnippetLocationType,List<String>> getCustomSnippet(List<Snippet> snippets) {
        Map<SnippetLocationType,List<String>> snippetsMap = new HashMap<>();
        for (Snippet snippet : snippets) {
            if(snippet.isEnable()){
                if(snippetsMap.get(snippet.getLocationType())==null){
                   snippetsMap.put(snippet.getLocationType(), new ArrayList<>());
                }
                snippetsMap.get(snippet.getLocationType()).add(snippet.getValue());
            }
        }
        return snippetsMap;
    }

    protected HashcodeMethodSnippet getHashcodeMethodSnippet(JavaClass javaClass, ClassMembers classMembers) {
        if (classMembers.getAttributes().isEmpty() && 
                StringUtils.isBlank(classMembers.getPreCode()) &&
                StringUtils.isBlank(classMembers.getPostCode())) {
            return null;
        }
        return new HashcodeMethodSnippet(javaClass.getClazz(), classMembers);
    }

    protected EqualsMethodSnippet getEqualsMethodSnippet(JavaClass javaClass, ClassMembers classMembers) {
        if (classMembers.getAttributes().isEmpty() && 
                StringUtils.isBlank(classMembers.getPreCode()) &&
                StringUtils.isBlank(classMembers.getPostCode())) {
            return null;
        }
        return new EqualsMethodSnippet(javaClass.getClazz(), classMembers);
    }

    protected ToStringMethodSnippet getToStringMethodSnippet(JavaClass javaClass, ClassMembers classMembers) {
        if (classMembers.getAttributes().isEmpty()) {
            return null;
        }
        ToStringMethodSnippet snippet = new ToStringMethodSnippet(javaClass.getClazz());
        snippet.setAttributes(classMembers.getAttributeNames());
        return snippet;
    }

    protected List<ConstructorSnippet> getConstructorSnippets(JavaClass javaClass) {
        List<ConstructorSnippet> constructorSnippets = new ArrayList<>();
        Function<Attribute, VariableDefSnippet> buildVarDef = attr -> {
                            VariableDefSnippet variableDefSnippet = new VariableDefSnippet(attr);
                            variableDefSnippet.setName(attr.getName());
                            variableDefSnippet.setType(attr.getDataTypeLabel());
                            return variableDefSnippet;
                        };
        List<Constructor> constructors = javaClass.getConstructors();
        if(javaClass instanceof DefaultClass && constructors.isEmpty()){ //for EmbeddedId and IdClass
            constructors.add(Constructor.getNoArgsInstance());
            Constructor constructor = new Constructor();
            constructor.setAttributes(getClassMembers(javaClass, null).getAttributes());
            constructors.add(constructor);
        }
        
        constructors.stream().filter((constructor) -> (constructor.isEnable())).map((constructor) -> {
            String className = javaClass.getClazz();
            List<VariableDefSnippet> parentVariableSnippets = constructor.getAttributes().stream()
                    .filter(attr -> attr.getJavaClass() != javaClass).map(buildVarDef).collect(toList());
            List<VariableDefSnippet> localVariableSnippets = constructor.getAttributes().stream()
                    .filter(attr -> attr.getJavaClass() == javaClass).map(buildVarDef).collect(toList());
            ConstructorSnippet snippet = new ConstructorSnippet(className, constructor, parentVariableSnippets, localVariableSnippets);
            return snippet;
        }).forEachOrdered((snippet) -> {
            constructorSnippets.add(snippet);
        });
        return constructorSnippets;
    }

    protected List<ConstraintSnippet> getConstraintSnippet(Set<Constraint> constraints) {
        List<ConstraintSnippet> snippets = new ArrayList<>();
        for (Constraint constraint : constraints) {
            if (!constraint.getSelected()) {
                continue;
            }
            ConstraintSnippet snippet = ConstraintSnippetFactory.getInstance(constraint);
            if (snippet != null) {
                snippets.add(snippet);
            }
        }
        return snippets;
    }

    protected VariableDefSnippet getVariableDef(Attribute attr) {
        VariableDefSnippet variableDef = variables.get(attr.getName());
        if (variableDef == null) {
            variableDef = new VariableDefSnippet(attr);
            variableDef.setName(attr.getName());
            variableDef.setDescription(attr.getDescription());
            variableDef.setAnnotation(getAnnotationSnippet(attr.getAnnotation()));
            if (attr instanceof BaseAttribute) {
                variableDef.setConstraints(getConstraintSnippet(((BaseAttribute) attr).getConstraints()));
            }

            variableDef.setJaxbVariableType(attr.getJaxbVariableType());
            if (attr.getJaxbVariableType() == JaxbVariableType.XML_ATTRIBUTE || attr.getJaxbVariableType() == JaxbVariableType.XML_LIST_ATTRIBUTE) {
                variableDef.setJaxbXmlAttribute(attr.getJaxbXmlAttribute());
            } else if (attr.getJaxbVariableType() == JaxbVariableType.XML_ELEMENT || attr.getJaxbVariableType() == JaxbVariableType.XML_LIST_ELEMENT) {
                variableDef.setJaxbXmlElement(attr.getJaxbXmlElement());
            } else if (attr.getJaxbVariableType() == JaxbVariableType.XML_ELEMENTS) {
//            variableDef.setJaxbXmlAttribute(attr.getJaxbXmlAttribute());
            } else if (attr.getJaxbVariableType() == JaxbVariableType.XML_VALUE || attr.getJaxbVariableType() == JaxbVariableType.XML_LIST_VALUE) {
//        variableDef.setJaxbXmlAttribute(attr.getJaxbXmlAttribute());
            }
            variables.put(attr.getName(), variableDef);
        }
        return variableDef;
    }

    protected void processBasic(List<Basic> parsedBasics) {
        if (parsedBasics == null) {
            return;
        }
        for (Basic parsedBasic : parsedBasics) {
            ColumnDefSnippet columnDef = getColumnDef(parsedBasic.getColumn());

            EnumType parsedEnumType = parsedBasic.getEnumerated();
            EnumeratedSnippet enumerated = null;
            if (parsedEnumType != null) {
                enumerated = new EnumeratedSnippet();
                enumerated.setValue(parsedEnumType);
            }

            TemporalType parsedTemporalType = parsedBasic.getTemporal();
            TemporalSnippet temporal = null;
            if (parsedTemporalType != null) {
                temporal = new TemporalSnippet();
                temporal.setValue(parsedTemporalType);
            }

            FetchType parsedFetchType = parsedBasic.getFetch();
            BasicSnippet basic = new BasicSnippet();

            if (parsedFetchType != null) {
                basic.setFetchType(parsedFetchType.value());
            }
            if (parsedBasic.getOptional() != null) {
                basic.setOptional(parsedBasic.getOptional());
            }

            Lob parsedLob = parsedBasic.getLob();

            VariableDefSnippet variableDef = getVariableDef(parsedBasic);

            variableDef.setBasic(basic);
            variableDef.setColumnDef(columnDef);
            variableDef.setEnumerated(enumerated);
            variableDef.setTemporal(temporal);
            variableDef.setType(parsedBasic.getAttributeType());
            if (parsedBasic.getFunctionalType() != null) {
                variableDef.setFunctionalType(parsedBasic.getFunctionalType());
            }

            if (parsedLob != null) {
                variableDef.setLob(true);
            }
        }
    }

    protected void processElementCollection(List<ElementCollection> parsedElementCollections) {
        if (parsedElementCollections == null) {
            return;
        }
        for (ElementCollection parsedElementCollection : parsedElementCollections) {

            CollectionTableSnippet collectionTable = getCollectionTable(parsedElementCollection.getCollectionTable());

            FetchType parsedFetchType = parsedElementCollection.getFetch();
            ElementCollectionSnippet elementCollection = new ElementCollectionSnippet();
            elementCollection.setCollectionType(parsedElementCollection.getCollectionType());
            elementCollection.setMapKeySnippet(updateMapKeyAttributeSnippet(parsedElementCollection));
            elementCollection.setTargetClass(parsedElementCollection.getAttributeType());
            if(parsedElementCollection.getConnectedClass()!=null){
                elementCollection.setTargetClassPackage(parsedElementCollection.getConnectedClass().getPackage(rootPackageName));
            }
            
            if (parsedFetchType != null) {
                elementCollection.setFetchType(parsedFetchType.value());
            }
            Lob parsedLob = parsedElementCollection.getLob();

            VariableDefSnippet variableDef = getVariableDef(parsedElementCollection);
            variableDef.setElementCollection(elementCollection);
            variableDef.setCollectionTable(collectionTable);

            if (parsedElementCollection.getOrderBy() != null) {
                variableDef.setOrderBy(new OrderBySnippet(parsedElementCollection.getOrderBy()));
            }

            if (parsedLob != null) {
                variableDef.setLob(true);
            }

            if (parsedElementCollection.getConnectedClass() == null) {//if not embeddable

                EnumType parsedEnumType = parsedElementCollection.getEnumerated();
                EnumeratedSnippet enumerated = null;
                if (parsedEnumType != null) {
                    enumerated = new EnumeratedSnippet();
                    enumerated.setValue(parsedEnumType);
                }

                TemporalType parsedTemporalType = parsedElementCollection.getTemporal();
                TemporalSnippet temporal = null;
                if (parsedTemporalType != null) {
                    temporal = new TemporalSnippet();
                    temporal.setValue(parsedTemporalType);
                }
                variableDef.setEnumerated(enumerated);
                variableDef.setTemporal(temporal);
                variableDef.setColumnDef(getColumnDef(parsedElementCollection.getColumn()));
            } else {
                processInternalAttributeOverride(variableDef, parsedElementCollection.getAttributeOverride());
                processInternalAssociationOverride(variableDef, parsedElementCollection.getAssociationOverride());
            }
        }
    }

    protected void processTransient(List<Transient> parsedTransients) {

        for (Transient parsedTransient : parsedTransients) {
            VariableDefSnippet variableDef = getVariableDef(parsedTransient);
            variableDef.setType(parsedTransient.getAttributeType());
            variableDef.setTranzient(true);
            if (parsedTransient.getFunctionalType() != null) {
                variableDef.setFunctionalType(parsedTransient.getFunctionalType());
            }
        }
    }

    protected List<String> getCascadeTypes(CascadeType cascadeType) {

        if (cascadeType == null) {
            return Collections.EMPTY_LIST;
        }

        List<String> cascadeTypes = new ArrayList<String>();

        EmptyType cascadeAll = cascadeType.getCascadeAll();
        EmptyType cascadeMerge = cascadeType.getCascadeMerge();
        EmptyType cascadePersist = cascadeType.getCascadePersist();
        EmptyType cascadeRefresh = cascadeType.getCascadeRefresh();
        EmptyType cascadeRemove = cascadeType.getCascadeRemove();

        if (cascadeAll != null) {
            cascadeTypes.add(ManyToManySnippet.CASCADE_ALL);
        }

        if (cascadeMerge != null) {
            cascadeTypes.add(ManyToManySnippet.CASCADE_MERGE);
        }

        if (cascadePersist != null) {
            cascadeTypes.add(ManyToManySnippet.CASCADE_PERSIST);
        }

        if (cascadeRefresh != null) {
            cascadeTypes.add(ManyToManySnippet.CASCADE_REFRESH);
        }

        if (cascadeRemove != null) {
            cascadeTypes.add(ManyToManySnippet.CASCADE_REMOVE);
        }

        return cascadeTypes;
    }

    protected List<ColumnResultSnippet> getColumnResults(
            List<ColumnResult> parsedColumnResults) {

        if (parsedColumnResults == null || parsedColumnResults.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<ColumnResultSnippet> columnResults = new ArrayList<ColumnResultSnippet>();

        for (ColumnResult parsedColumnResult : parsedColumnResults) {
            ColumnResultSnippet columnResult = new ColumnResultSnippet();

            columnResult.setName(parsedColumnResult.getName());
            columnResult.setType(parsedColumnResult.getClazz());
            columnResults.add(columnResult);
        }

        return columnResults;
    }

    protected List<ConstructorResultSnippet> getConstructorResults(
            List<ConstructorResult> parsedConstructorResults) {

        if (parsedConstructorResults == null || parsedConstructorResults.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<ConstructorResultSnippet> constructorResults = new ArrayList<ConstructorResultSnippet>();

        for (ConstructorResult parsedConstructorResult : parsedConstructorResults) {
            ConstructorResultSnippet constructorResult = new ConstructorResultSnippet();
            List<ColumnResultSnippet> columnResults = getColumnResults(parsedConstructorResult.getColumn());
            constructorResult.setColumnResults(columnResults);
            constructorResult.setTargetClass(parsedConstructorResult.getTargetClass());
            constructorResult.setPackageName(packageName);
            constructorResults.add(constructorResult);
        }

        return constructorResults;
    }

    protected List<EntityResultSnippet> getEntityResults(
            List<EntityResult> parsedEntityResults) {

        if (parsedEntityResults == null || parsedEntityResults.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<EntityResultSnippet> entityResults = new ArrayList<EntityResultSnippet>();

        for (EntityResult parsedEntityResult : parsedEntityResults) {

            List<FieldResultSnippet> fieldResults = getFieldResults(
                    parsedEntityResult.getFieldResult());

            EntityResultSnippet entityResult = new EntityResultSnippet();

            entityResult.setDiscriminatorColumn(
                    parsedEntityResult.getDiscriminatorColumn());
            entityResult.setEntityClass(
                    parsedEntityResult.getEntityClass());
            entityResult.setPackageName(packageName);
            entityResult.setFieldResults(fieldResults);

            entityResults.add(entityResult);
        }

        return entityResults;
    }

    protected List<FieldResultSnippet> getFieldResults(
            List<FieldResult> parsedFieldResults) {

        if (parsedFieldResults == null || parsedFieldResults.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<FieldResultSnippet> fieldResults = new ArrayList<FieldResultSnippet>();

        for (FieldResult parsedFieldResult : parsedFieldResults) {
            FieldResultSnippet fieldResult = new FieldResultSnippet();

            fieldResult.setColumn(parsedFieldResult.getColumn());
            fieldResult.setName(parsedFieldResult.getName());

            fieldResults.add(fieldResult);
        }
        return fieldResults;
    }

    protected List<JoinColumnSnippet> getJoinColumns(List<? extends JoinColumn> parsedJoinColumns, boolean mapKey) {

        List<JoinColumnSnippet> joinColumns = new ArrayList<>();

        parsedJoinColumns.stream().filter(JoinColumnValidator::isNotEmpty).forEach(parsedJoinColumn -> {

            JoinColumnSnippet joinColumn = new JoinColumnSnippet(mapKey);

            joinColumn.setColumnDefinition(
                    parsedJoinColumn.getColumnDefinition());

            if (parsedJoinColumn.getInsertable() != null) {
                joinColumn.setInsertable(parsedJoinColumn.getInsertable());
            }

            if (parsedJoinColumn.getUnique() != null) {
                joinColumn.setUnique(parsedJoinColumn.getUnique());
            }

            if (parsedJoinColumn.getNullable() != null) {
                joinColumn.setNullable(parsedJoinColumn.getNullable());
            }

            if (parsedJoinColumn.getUpdatable() != null) {
                joinColumn.setUpdatable(parsedJoinColumn.getUpdatable());
            }

            joinColumn.setName(parsedJoinColumn.getName());
            joinColumn.setReferencedColumnName(parsedJoinColumn.getReferencedColumnName());
            joinColumn.setTable(parsedJoinColumn.getTable());
            joinColumn.setForeignKey(getForeignKey(parsedJoinColumn.getForeignKey()));

            joinColumns.add(joinColumn);
        });

        return joinColumns;
    }

    protected CollectionTableSnippet getCollectionTable(CollectionTable parsedCollectionTable) {
        if (parsedCollectionTable == null || CollectionTableValidator.isEmpty(parsedCollectionTable)) {
            return null;
        }

        List<JoinColumnSnippet> joinColumns = getJoinColumns(
                parsedCollectionTable.getJoinColumn(), false);

        Set<UniqueConstraint> parsedUniqueConstraints = parsedCollectionTable.getUniqueConstraint();

        List<UniqueConstraintSnippet> uniqueConstraints = getUniqueConstraints(parsedUniqueConstraints);

        CollectionTableSnippet collectionTable = new CollectionTableSnippet();

        collectionTable.setCatalog(parsedCollectionTable.getCatalog());
        collectionTable.setName(parsedCollectionTable.getName());
        collectionTable.setSchema(parsedCollectionTable.getSchema());
        collectionTable.setJoinColumns(joinColumns);
        collectionTable.setUniqueConstraints(uniqueConstraints);
        collectionTable.setIndices(getIndexes(parsedCollectionTable.getIndex()));

        collectionTable.setForeignKey(getForeignKey(parsedCollectionTable.getForeignKey()));

        return collectionTable;
    }

    protected JoinTableSnippet getJoinTable(JoinTable parsedJoinTable) {
        if (parsedJoinTable == null || JoinTableValidator.isEmpty(parsedJoinTable)) {
            return null;
        }

        List<JoinColumnSnippet> inverseJoinColumns = getJoinColumns(
                parsedJoinTable.getInverseJoinColumn(), false);

        List<JoinColumnSnippet> joinColumns = getJoinColumns(
                parsedJoinTable.getJoinColumn(), false);

        Set<UniqueConstraint> parsedUniqueConstraints
                = parsedJoinTable.getUniqueConstraint();

        List<UniqueConstraintSnippet> uniqueConstraints = getUniqueConstraints(parsedUniqueConstraints);

        JoinTableSnippet joinTable = new JoinTableSnippet();

        joinTable.setCatalog(parsedJoinTable.getCatalog());
        joinTable.setName(parsedJoinTable.getName());
        joinTable.setSchema(parsedJoinTable.getSchema());
        joinTable.setJoinColumns(joinColumns);
        joinTable.setInverseJoinColumns(inverseJoinColumns);
        joinTable.setUniqueConstraints(uniqueConstraints);
        joinTable.setIndices(getIndexes(parsedJoinTable.getIndex()));

        joinTable.setForeignKey(getForeignKey(parsedJoinTable.getForeignKey()));
        joinTable.setInverseForeignKey(getForeignKey(parsedJoinTable.getInverseForeignKey()));

        return joinTable;
    }

    protected ForeignKeySnippet getForeignKey(ForeignKey parsedForeignKey) {
        if (parsedForeignKey == null || ForeignKeyValidator.isEmpty(parsedForeignKey)) {
            return null;
        }

        ForeignKeySnippet foreignKey = new ForeignKeySnippet();

        foreignKey.setName(parsedForeignKey.getName());
        foreignKey.setDescription(parsedForeignKey.getDescription());
        foreignKey.setForeignKeyDefinition(parsedForeignKey.getForeignKeyDefinition());
        if (parsedForeignKey.getConstraintMode() != null) {
            foreignKey.setConstraintMode(parsedForeignKey.getConstraintMode().name());
        }

        return foreignKey;
    }

    protected List<PrimaryKeyJoinColumnSnippet> getPrimaryKeyJoinColumns(
            List<PrimaryKeyJoinColumn> parsedPrimaryKeyJoinColumns) {

        if (parsedPrimaryKeyJoinColumns == null || parsedPrimaryKeyJoinColumns.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns = new ArrayList<>();

        for (PrimaryKeyJoinColumn parsedPrimaryKeyJoinColumn : parsedPrimaryKeyJoinColumns) {
            PrimaryKeyJoinColumnSnippet primaryKeyJoinColumn = new PrimaryKeyJoinColumnSnippet();
            primaryKeyJoinColumn.setColumnDefinition(parsedPrimaryKeyJoinColumn.getColumnDefinition());
            primaryKeyJoinColumn.setName(parsedPrimaryKeyJoinColumn.getName());
            primaryKeyJoinColumn.setReferencedColumnName(parsedPrimaryKeyJoinColumn.getReferencedColumnName());
            primaryKeyJoinColumn.setForeignKey(getForeignKey(parsedPrimaryKeyJoinColumn.getForeignKey()));
            primaryKeyJoinColumns.add(primaryKeyJoinColumn);
        }

        return primaryKeyJoinColumns;
    }

    protected List<NamedAttributeNodeSnippet> getNamedAttributeNodes(
            List<NamedAttributeNode> parsedNamedAttributeNodes) {

        if (parsedNamedAttributeNodes == null || parsedNamedAttributeNodes.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<NamedAttributeNodeSnippet> namedAttributeNodes = new ArrayList<NamedAttributeNodeSnippet>();
        for (NamedAttributeNode parsedNamedAttributeNode : parsedNamedAttributeNodes) {
            NamedAttributeNodeSnippet namedAttributeNode = new NamedAttributeNodeSnippet();
            namedAttributeNode.setName(parsedNamedAttributeNode.getName());
            namedAttributeNode.setSubgraph(parsedNamedAttributeNode.getSubgraph());
            namedAttributeNode.setKeySubgraph(parsedNamedAttributeNode.getKeySubgraph());
            namedAttributeNodes.add(namedAttributeNode);
        }
        return namedAttributeNodes;
    }

    protected List<NamedSubgraphSnippet> getNamedSubgraphs(
            List<NamedSubgraph> parsedNamedSubgraphs) {

        if (parsedNamedSubgraphs == null || parsedNamedSubgraphs.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<NamedSubgraphSnippet> namedSubgraphs = new ArrayList<>();
        for (NamedSubgraph parsedNamedSubgraph : parsedNamedSubgraphs) {
            NamedSubgraphSnippet namedSubgraph = new NamedSubgraphSnippet();
            namedSubgraph.setName(parsedNamedSubgraph.getName());
            namedSubgraph.setNamedAttributeNode(getNamedAttributeNodes(parsedNamedSubgraph.getNamedAttributeNode()));
            namedSubgraph.setType(parsedNamedSubgraph.getClazz());
            namedSubgraphs.add(namedSubgraph);
        }
        return namedSubgraphs;
    }

    protected List<QueryHintSnippet> getQueryHints(
            List<QueryHint> parsedQueryHints) {

        if (parsedQueryHints == null || parsedQueryHints.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<QueryHintSnippet> queryHints = new ArrayList<>();

        for (QueryHint parsedQueryHint : parsedQueryHints) {
            QueryHintSnippet queryHint = new QueryHintSnippet();

            queryHint.setName(parsedQueryHint.getName());
            queryHint.setValue(parsedQueryHint.getValue());

            queryHints.add(queryHint);
        }
        return queryHints;
    }

    protected List<UniqueConstraintSnippet> getUniqueConstraints(Set<UniqueConstraint> parsedUniqueConstraints) {
        if (parsedUniqueConstraints == null || parsedUniqueConstraints.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return parsedUniqueConstraints.stream().map(c -> new UniqueConstraintSnippet(c)).collect(toList());
    }

    protected List<IndexSnippet> getIndexes(List<Index> parsedIndexes) {
        if (parsedIndexes == null || parsedIndexes.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return parsedIndexes.stream().filter(index -> !index.getColumnList().isEmpty())
                .map(index -> new IndexSnippet(index)).collect(toList());
    }

    protected void processAssociationOverrides(
            Set<AssociationOverride> parsedAssociationOverrides) {

        if (parsedAssociationOverrides == null
                || parsedAssociationOverrides.isEmpty()) {
            return;
        }

        classDef.setAssociationOverrides(new AssociationOverridesSnippet());

        for (AssociationOverride parsedAssociationOverride : parsedAssociationOverrides) {

            List<JoinColumnSnippet> joinColumnsList = getJoinColumns(parsedAssociationOverride.getJoinColumn(), false);
            JoinTableSnippet joinTable = getJoinTable(parsedAssociationOverride.getJoinTable());

            if ((joinTable == null || joinTable.isEmpty()) && joinColumnsList.isEmpty()) {
                continue;
            }
            AssociationOverrideSnippet associationOverride = new AssociationOverrideSnippet();
            associationOverride.setName(parsedAssociationOverride.getName());
            associationOverride.setJoinColumns(joinColumnsList);
            associationOverride.setJoinTable(joinTable);

            classDef.getAssociationOverrides().addAssociationOverride(
                    associationOverride);
        }
        if (classDef.getAssociationOverrides() != null && classDef.getAssociationOverrides().getAssociationOverrides().isEmpty()) {
            classDef.setAssociationOverrides(null);
        }
    }

    protected AttributeOverridesSnippet processAttributeOverrides(
            Set<AttributeOverride> parsedAttributeOverrides) {

        if (parsedAttributeOverrides == null || parsedAttributeOverrides.isEmpty()) {
            return null;
        }
        AttributeOverridesSnippet attributeOverridesSnippet = new AttributeOverridesSnippet();

        for (AttributeOverride parsedAttributeOverride : parsedAttributeOverrides) {

            ColumnDefSnippet columnDef = getColumnDef(parsedAttributeOverride.getColumn());
            if (columnDef == null) {
                continue;
            }
            AttributeOverrideSnippet attributeOverride = new AttributeOverrideSnippet();

            attributeOverride.setColumnDef(columnDef);
            attributeOverride.setName(parsedAttributeOverride.getName());

            attributeOverridesSnippet.addAttributeOverrides(attributeOverride);
        }

        if (attributeOverridesSnippet.getAttributeOverrides().isEmpty()) {
            return null;
        }

        return attributeOverridesSnippet;
    }

    protected void processEntityListeners(EntityListeners parsedEntityListeners) {

        if (parsedEntityListeners == null) {
            return;
        }

        List<EntityListener> parsedEntityListenersList
                = parsedEntityListeners.getEntityListener();

        List<EntityListenerSnippet> entityListeners
                = GeneratorUtil.processEntityListeners(
                        parsedEntityListenersList, packageName);

        if (entityListeners.isEmpty()) {
            return;
        }

        classDef.setEntityListeners(new EntityListenersSnippet());
        classDef.getEntityListeners().setEntityListeners(entityListeners);
    }

    protected void processDefaultExcludeListeners(
            EmptyType parsedEmptyType) {

        if (parsedEmptyType != null) {
            classDef.setDefaultExcludeListener(true);
        }
    }

    protected void processExcludeSuperclassListeners(
            EmptyType parsedEmptyType) {

        if (parsedEmptyType != null) {
            classDef.setExcludeSuperClassListener(true);
        }
    }

    protected void processIdClass(IdClass parsedIdClass) {

        if (parsedIdClass == null) {
            return;
        }

        IdClassSnippet idClass = new IdClassSnippet();

        idClass.setValue(parsedIdClass.getClazz());
        idClass.setPackageName(packageName);

        classDef.setIdClass(idClass);
    }

    protected void processNamedEntityGraphs(List<NamedEntityGraph> parsedNamedEntityGraphs) {

        if (parsedNamedEntityGraphs == null || parsedNamedEntityGraphs.isEmpty()) {
            return;
        }

        NamedEntityGraphsSnippet namedEntityGraphs = new NamedEntityGraphsSnippet();

        for (NamedEntityGraph parsedNamedEntityGraph : parsedNamedEntityGraphs) {
            if (parsedNamedEntityGraph.isEnable()) {
                NamedEntityGraphSnippet namedEntityGraph = new NamedEntityGraphSnippet();
                namedEntityGraph.setName(parsedNamedEntityGraph.getName());
                namedEntityGraph.setIncludeAllAttributes(parsedNamedEntityGraph.isIncludeAllAttributes());
                namedEntityGraph.setNamedAttributeNodes(getNamedAttributeNodes(parsedNamedEntityGraph.getNamedAttributeNode()));
                namedEntityGraph.setSubgraphs(getNamedSubgraphs(parsedNamedEntityGraph.getSubgraph()));
                namedEntityGraph.setSubclassSubgraphs(getNamedSubgraphs(parsedNamedEntityGraph.getSubclassSubgraph()));

                namedEntityGraphs.addNamedEntityGraph(namedEntityGraph);
            }

        }
        if (!namedEntityGraphs.getNamedEntityGraphs().isEmpty()) {
            classDef.setNamedEntityGraphs(namedEntityGraphs);
        }
    }

    protected void processNamedStoredProcedureQueries(EntityMappings entityMappings, List<NamedStoredProcedureQuery> parsedNamedStoredProcedureQueries) {

        if (parsedNamedStoredProcedureQueries == null || parsedNamedStoredProcedureQueries.isEmpty()) {
            return;
        }

        NamedStoredProcedureQueriesSnippet namedStoredProcedureQueries = new NamedStoredProcedureQueriesSnippet();

        for (NamedStoredProcedureQuery parsedNamedStoredProcedureQuery : parsedNamedStoredProcedureQueries) {
            if (parsedNamedStoredProcedureQuery.isEnable()) {
                NamedStoredProcedureQuerySnippet namedStoredProcedureQuery = new NamedStoredProcedureQuerySnippet();
                namedStoredProcedureQuery.setName(parsedNamedStoredProcedureQuery.getName());
                namedStoredProcedureQuery.setProcedureName(parsedNamedStoredProcedureQuery.getProcedureName());
                namedStoredProcedureQuery.setQueryHints(getQueryHints(parsedNamedStoredProcedureQuery.getHint()));
                namedStoredProcedureQuery.setResultClasses(getResultClasses(entityMappings, parsedNamedStoredProcedureQuery.getResultClass()));
                namedStoredProcedureQuery.setResultSetMappings(parsedNamedStoredProcedureQuery.getResultSetMapping());
                namedStoredProcedureQuery.setParameters(getStoredProcedureParameters(parsedNamedStoredProcedureQuery.getParameter()));

                namedStoredProcedureQueries.addNamedStoredProcedureQuery(namedStoredProcedureQuery);
            }
        }
        if (!namedStoredProcedureQueries.getNamedStoredProcedureQueries().isEmpty()) {
            classDef.setNamedStoredProcedureQueries(namedStoredProcedureQueries);
        }
    }

    protected List<String> getResultClasses(EntityMappings entityMappings, List<String> parsedgetResultClasses) {
        List<String> newParsedgetResultClasses = new ArrayList<String>();

        for (String resultClass : parsedgetResultClasses) {
            if (resultClass.charAt(0) == '{' && resultClass.charAt(resultClass.length() - 1) == '}') {
                String id = resultClass.substring(1, resultClass.length() - 1);
                Entity entity = entityMappings.getEntity(id);
                if (entityMappings.getPackage() == null || entityMappings.getPackage().isEmpty()) {
                    newParsedgetResultClasses.add(entity.getClazz());
                } else {
                    newParsedgetResultClasses.add(entityMappings.getPackage() + "." + entity.getClazz());
                }
            } else {
                newParsedgetResultClasses.add(resultClass);
            }
        }

        return newParsedgetResultClasses;
    }

    protected List<StoredProcedureParameterSnippet> getStoredProcedureParameters(List<StoredProcedureParameter> parsedStoredProcedureParameters) {

        if (parsedStoredProcedureParameters == null || parsedStoredProcedureParameters.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<StoredProcedureParameterSnippet> storedProcedureParameters = new ArrayList<StoredProcedureParameterSnippet>();

        for (StoredProcedureParameter parsedStoredProcedureParameter : parsedStoredProcedureParameters) {
            StoredProcedureParameterSnippet storedProcedureParameter = new StoredProcedureParameterSnippet();
            storedProcedureParameter.setName(parsedStoredProcedureParameter.getName());
            storedProcedureParameter.setType(parsedStoredProcedureParameter.getClazz());
            if (parsedStoredProcedureParameter.getMode() != null) {
                storedProcedureParameter.setMode(parsedStoredProcedureParameter.getMode().value());
            }
            storedProcedureParameters.add(storedProcedureParameter);
        }
        return storedProcedureParameters;
    }

    protected void processNamedNativeQueries(List<NamedNativeQuery> parsedNamedNativeQueries) {

        if (parsedNamedNativeQueries == null || parsedNamedNativeQueries.isEmpty()) {
            return;
        }

        NamedNativeQueriesSnippet namedNativeQueries = new NamedNativeQueriesSnippet();

        for (NamedNativeQuery parsedNamedNativeQuery : parsedNamedNativeQueries) {
            if (parsedNamedNativeQuery.isEnable()) {
                List<QueryHintSnippet> queryHints = getQueryHints(parsedNamedNativeQuery.getHint());

                NamedNativeQuerySnippet namedNativeQuery = new NamedNativeQuerySnippet();
                namedNativeQuery.setName(parsedNamedNativeQuery.getName());
                namedNativeQuery.setQuery(parsedNamedNativeQuery.getQuery());
                namedNativeQuery.setResultClass(parsedNamedNativeQuery.getResultClass());
                namedNativeQuery.setPackageName(packageName);
                namedNativeQuery.setResultSetMapping(parsedNamedNativeQuery.getResultSetMapping());
                namedNativeQuery.setQueryHints(queryHints);
//            namedNativeQuery.setAttributeType(parsedNamedNativeQuery.getAttributeType());
                namedNativeQueries.addNamedQuery(namedNativeQuery);
            }
        }

        if (!namedNativeQueries.getNamedQueries().isEmpty()) {
            classDef.setNamedNativeQueries(namedNativeQueries);
        }
    }

    protected void processNamedQueries(
            List<NamedQuery> parsedNamedQueries) {

        if (parsedNamedQueries == null || parsedNamedQueries.isEmpty()) {
            return;
        }

        NamedQueriesSnippet namedQueries = new NamedQueriesSnippet();

        for (NamedQuery parsedNamedQuery : parsedNamedQueries) {

            if (parsedNamedQuery.isEnable()) {
                List<QueryHintSnippet> queryHints = getQueryHints(parsedNamedQuery.getHint());

                NamedQueryDefSnippet namedQuery = new NamedQueryDefSnippet();
                namedQuery.setName(parsedNamedQuery.getName());
                namedQuery.setQuery(parsedNamedQuery.getQuery());
                //  namedQuery.setAttributeType(parsedNamedQuery.getAttributeType());
                namedQuery.setQueryHints(queryHints);
                namedQuery.setLockMode(parsedNamedQuery.getLockMode());

                namedQueries.addNamedQuery(namedQuery);
            }
        }

        if (!namedQueries.getNamedQueries().isEmpty()) {
            classDef.setNamedQueries(namedQueries);
        }
    }

    protected void processEmbedded(List<Embedded> parsedEmbeddeds) {

        if (parsedEmbeddeds == null) {
            return;
        }
        for (Embedded parsedEmbeded : parsedEmbeddeds) {
            VariableDefSnippet variableDef = getVariableDef(parsedEmbeded);

            variableDef.setEmbedded(true);
            variableDef.setType(parsedEmbeded.getConnectedClass().getPackage(rootPackageName) + ORMConverterUtil.DOT +
                    parsedEmbeded.getAttributeType());
            if (parsedEmbeded.getFunctionalType() != null) {
                variableDef.setFunctionalType(parsedEmbeded.getFunctionalType());
            }

            processInternalAttributeOverride(variableDef, parsedEmbeded.getAttributeOverride());
            processInternalAssociationOverride(variableDef, parsedEmbeded.getAssociationOverride());
        }
    }

    private void processInternalAttributeOverride(AttributeOverridesHandler attrHandler, Set<AttributeOverride> attributeOverrrides) {
        if (attributeOverrrides != null && !attributeOverrrides.isEmpty()
                && attrHandler.getAttributeOverrides() == null) {
            attrHandler.setAttributeOverrides(new AttributeOverridesSnippet());
        }
        for (AttributeOverride parsedAttributeOverride : attributeOverrrides) {
            AttributeOverrideSnippet attributeOverride = new AttributeOverrideSnippet();
            ColumnDefSnippet columnDef = getColumnDef(parsedAttributeOverride.getColumn());
            if (columnDef == null) {
                continue;
            }
            attributeOverride.setColumnDef(columnDef);
            attributeOverride.setName(parsedAttributeOverride.getName());
            attrHandler.getAttributeOverrides().addAttributeOverrides(attributeOverride);
        }
        if (attrHandler.getAttributeOverrides() != null && attrHandler.getAttributeOverrides().getAttributeOverrides().isEmpty()) {
            attrHandler.setAttributeOverrides(null);
        }
    }

    private void processInternalAssociationOverride(AssociationOverridesHandler assoHandler, Set<AssociationOverride> associationOverrrides) {

        if (associationOverrrides != null && !associationOverrrides.isEmpty()
                && assoHandler.getAssociationOverrides() == null) {
            assoHandler.setAssociationOverrides(new AssociationOverridesSnippet());
        }

        for (AssociationOverride parsedAssociationOverride : associationOverrrides) {

            List<JoinColumnSnippet> joinColumnsList = getJoinColumns(parsedAssociationOverride.getJoinColumn(), false);
            JoinTableSnippet joinTable = getJoinTable(parsedAssociationOverride.getJoinTable());

            if (joinTable.isEmpty() && joinColumnsList.isEmpty()) {
                continue;
            }
            AssociationOverrideSnippet associationOverride = new AssociationOverrideSnippet();
            associationOverride.setName(parsedAssociationOverride.getName());
            associationOverride.setJoinColumns(joinColumnsList);
            associationOverride.setJoinTable(joinTable);

            associationOverride.setForeignKey(getForeignKey(parsedAssociationOverride.getForeignKey()));

            assoHandler.getAssociationOverrides().addAssociationOverride(
                    associationOverride);
        }
        if (assoHandler.getAssociationOverrides() != null && assoHandler.getAssociationOverrides().getAssociationOverrides().isEmpty()) {
            assoHandler.setAssociationOverrides(null);
        }
    }

    protected void processEmbeddedId(IdentifiableClass identifiableClass, EmbeddedId parsedEmbeddedId) {
        if (parsedEmbeddedId == null
                || identifiableClass.getCompositePrimaryKeyType() != CompositePrimaryKeyType.EMBEDDEDID) {
            return;
        }

        VariableDefSnippet variableDef = getVariableDef(parsedEmbeddedId);
        variableDef.setEmbeddedId(true);
        /**
         * Filter if Embeddable class is used in case of derived entities. Refer
         * : JPA Spec 2.4.1.3 Example 5(b)
         */
        if (identifiableClass.getCompositePrimaryKeyType() == CompositePrimaryKeyType.EMBEDDEDID && parsedEmbeddedId.getConnectedClass() == null) {
            variableDef.setType(identifiableClass.getCompositePrimaryKeyClass());
        } else {
            variableDef.setType(parsedEmbeddedId.getAttributeType());
        }

        processInternalAttributeOverride(variableDef, parsedEmbeddedId.getAttributeOverride());
    }

    protected void processId(List<Id> parsedIds) {

//        if (parsedAttributes == null) {
//            return;
//        }
//
//        List<ParsedId> parsedIds = parsedAttributes.getId();
        for (Id parsedId : parsedIds) {
            VariableDefSnippet variableDef = getVariableDef(parsedId);
            variableDef.setType(parsedId.getAttributeType());
            if (parsedId.getFunctionalType() != null) {
                variableDef.setFunctionalType(parsedId.getFunctionalType());
            }
            variableDef.setPrimaryKey(true);

            Column parsedColumn = parsedId.getColumn();

            if (parsedColumn != null) {
                ColumnDefSnippet columnDef = getColumnDef(parsedColumn);
                variableDef.setColumnDef(columnDef);
            }

            GeneratedValue parsedGeneratedValue
                    = parsedId.getGeneratedValue();

            if (parsedGeneratedValue != null && parsedGeneratedValue.getStrategy() != null) {
                GeneratedValueSnippet generatedValue = new GeneratedValueSnippet();

                generatedValue.setGenerator(parsedGeneratedValue.getGenerator());
                generatedValue.setStrategy("GenerationType." + parsedGeneratedValue.getStrategy().value());

                variableDef.setGeneratedValue(generatedValue);

                SequenceGenerator parsedSequenceGenerator
                        = parsedId.getSequenceGenerator();

                if (parsedSequenceGenerator != null) {
                    SequenceGeneratorSnippet sequenceGenerator = processSequenceGenerator(parsedSequenceGenerator);
                    variableDef.setSequenceGenerator(sequenceGenerator);
                }

                TableGenerator parsedTableGenerator = parsedId.getTableGenerator();
                if (parsedTableGenerator != null) {
                    variableDef.setTableGenerator(processTableGenerator(parsedTableGenerator));
                }
            }

            TemporalType parsedTemporalType = parsedId.getTemporal();
            TemporalSnippet temporal = null;
            if (parsedTemporalType != null) {
                temporal = new TemporalSnippet();
                temporal.setValue(parsedTemporalType);
            }
            variableDef.setTemporal(temporal);
        }
    }

    protected void processManyToMany(List<ManyToMany> parsedManyToManys) {

        if (parsedManyToManys == null) {
            return;
        }
//
//        List<ParsedManyToMany> parsedManyToManys
//                = parsedAttributes.getManyToMany();
        for (ManyToMany parsedManyToMany : parsedManyToManys) {
            List<String> cascadeTypes = getCascadeTypes(
                    parsedManyToMany.getCascade());

            JoinTableSnippet joinTable = getJoinTable(parsedManyToMany.getJoinTable());

            ManyToManySnippet manyToMany = new ManyToManySnippet();

            manyToMany.setCollectionType(parsedManyToMany.getCollectionType());
            manyToMany.setMapKeySnippet(updateMapKeyAttributeSnippet(parsedManyToMany));
            manyToMany.setMappedBy(parsedManyToMany.getMappedBy());
            manyToMany.setTargetEntity(parsedManyToMany.getTargetEntity());
            manyToMany.setTargetEntityPackage(parsedManyToMany.getConnectedEntity().getPackage(rootPackageName));
            
            manyToMany.setCascadeTypes(cascadeTypes);

            if (parsedManyToMany.getFetch() != null) {
                manyToMany.setFetchType(parsedManyToMany.getFetch().value());
            }
            //TODO: Checked this error - The ORM.xsd has this but NOT the
            //http://www.oracle.com/technology/products/ias/toplink/jpa/resources/toplink-jpa-annotations.html
//            manyToMany.setOrderBy(parsedManyToMany.getOrderBy());

            VariableDefSnippet variableDef = getVariableDef(parsedManyToMany);

            variableDef.setRelationDef(manyToMany);
            variableDef.setJoinTable(joinTable);
            if (parsedManyToMany.getOrderBy() != null) {
                variableDef.setOrderBy(new OrderBySnippet(parsedManyToMany.getOrderBy()));
            }
//            variableDef.setType(parsedManyToMany.getAttributeType());

            if (parsedManyToMany.getMapKey() != null) {
                variableDef.setMapKey(parsedManyToMany.getMapKey().getName());
            }
        }
    }

    private MapKeySnippet updateMapKeyAttributeSnippet(MapKeyHandler mapKeyHandler) {
        if (mapKeyHandler.getMapKeyType() == null || mapKeyHandler.getValidatedMapKeyType() == null) {
            return null;
        }
        MapKeySnippet snippet = new MapKeySnippet();
        if (mapKeyHandler.getMapKeyType() == MapKeyType.EXT && mapKeyHandler.getValidatedMapKeyType() == MapKeyType.EXT) {
            snippet.setMapKeyAttribute(mapKeyHandler.getMapKeyAttribute());
            snippet.setMapKeyAttributeType(mapKeyHandler.getMapKeyAttribute().getDataTypeLabel());
        } else if (mapKeyHandler.getMapKeyEntity() != null) {
            List<JoinColumnSnippet> joinColumnsList = getJoinColumns(mapKeyHandler.getMapKeyJoinColumn(), true);
            JoinColumnsSnippet joinColumns = null;
            if (!joinColumnsList.isEmpty()) {
                joinColumns = new JoinColumnsSnippet(true);
                joinColumns.setJoinColumns(joinColumnsList);
                joinColumns.setForeignKey(getForeignKey(mapKeyHandler.getMapKeyForeignKey()));
            }
            snippet.setJoinColumnsSnippet(joinColumns);
            snippet.setMapKeyAttributeType(mapKeyHandler.getMapKeyEntity().getClazz());
        } else if (mapKeyHandler.getMapKeyEmbeddable() != null) {//TODO attr override
            snippet.setAttributeOverrideSnippet(processAttributeOverrides(mapKeyHandler.getMapKeyAttributeOverride()));
            snippet.setMapKeyAttributeType(mapKeyHandler.getMapKeyEmbeddable().getClazz());
        } else {
            if (mapKeyHandler.getMapKeyEnumerated() != null) {
                EnumeratedSnippet enumeratedSnippet = new EnumeratedSnippet(true);
                enumeratedSnippet.setValue(mapKeyHandler.getMapKeyEnumerated());
                snippet.setEnumeratedSnippet(enumeratedSnippet);
            } else if (mapKeyHandler.getMapKeyTemporal() != null) {
                TemporalSnippet temporalSnippet = new TemporalSnippet(true);
                temporalSnippet.setValue(mapKeyHandler.getMapKeyTemporal());
                snippet.setTemporalSnippet(temporalSnippet);
            }
            snippet.setColumnSnippet(getColumnDef(mapKeyHandler.getMapKeyColumn(), true));
            snippet.setMapKeyAttributeType(mapKeyHandler.getMapKeyAttributeType());
        }
        return snippet;
    }

    protected void processManyToOne(List<ManyToOne> parsedManyToOnes) {

        if (parsedManyToOnes == null) {
            return;
        }
//
//        List<ParsedManyToOne> parsedManyToOnes
//                = parsedAttributes.getManyToOne();
        for (ManyToOne parsedManyToOne : parsedManyToOnes) {
            List<String> cascadeTypes = getCascadeTypes(
                    parsedManyToOne.getCascade());

            JoinTableSnippet joinTable = getJoinTable(parsedManyToOne.getJoinTable());

            ManyToOneSnippet manyToOne = new ManyToOneSnippet();

            manyToOne.setTargetEntity(parsedManyToOne.getTargetEntity());
            manyToOne.setTargetEntityPackage(parsedManyToOne.getConnectedEntity().getPackage(rootPackageName));
            
            manyToOne.setCascadeTypes(cascadeTypes);

            if (parsedManyToOne.getOptional() != null) {
                manyToOne.setOptional(parsedManyToOne.getOptional());
            }

            if (parsedManyToOne.getFetch() != null) {
                manyToOne.setFetchType(parsedManyToOne.getFetch().value());
            }
            manyToOne.setPrimaryKey(parsedManyToOne.isPrimaryKey());
            manyToOne.setMapsId(parsedManyToOne.getMapsId());

            VariableDefSnippet variableDef = getVariableDef(parsedManyToOne);

            variableDef.setRelationDef(manyToOne);
            variableDef.setJoinTable(joinTable);
            variableDef.setJoinColumns(getJoinColumnsSnippet(parsedManyToOne, false));
            if (parsedManyToOne.getFunctionalType() != null) {
                variableDef.setFunctionalType(parsedManyToOne.getFunctionalType());
            }
        }
    }

    protected void processOneToMany(List<OneToMany> parsedOneToManys) {

        if (parsedOneToManys == null) {
            return;
        }
//
//        List<ParsedOneToMany> parsedOneToManys = parsedAttributes.getOneToMany();
        for (OneToMany parsedOneToMany : parsedOneToManys) {
            List<String> cascadeTypes = getCascadeTypes(
                    parsedOneToMany.getCascade());

            JoinTableSnippet joinTable = getJoinTable(parsedOneToMany.getJoinTable());

            OneToManySnippet oneToMany = new OneToManySnippet();

            oneToMany.setCascadeTypes(cascadeTypes);
            oneToMany.setTargetEntity(parsedOneToMany.getTargetEntity());
            oneToMany.setTargetEntityPackage(parsedOneToMany.getConnectedEntity().getPackage(rootPackageName));
            
            oneToMany.setMappedBy(parsedOneToMany.getMappedBy());
            oneToMany.setCollectionType(parsedOneToMany.getCollectionType());
            oneToMany.setMapKeySnippet(updateMapKeyAttributeSnippet(parsedOneToMany));
            if (parsedOneToMany.getFetch() != null) {
                oneToMany.setFetchType(parsedOneToMany.getFetch().value());
            }
            oneToMany.setOrphanRemoval(parsedOneToMany.getOrphanRemoval());

            VariableDefSnippet variableDef = getVariableDef(parsedOneToMany);

            variableDef.setRelationDef(oneToMany);
            variableDef.setJoinTable(joinTable);
            variableDef.setJoinColumns(getJoinColumnsSnippet(parsedOneToMany, false));
            if (parsedOneToMany.getOrderBy() != null) {
                variableDef.setOrderBy(new OrderBySnippet(parsedOneToMany.getOrderBy()));
            }

            if (parsedOneToMany.getMapKey() != null) {
                variableDef.setMapKey(parsedOneToMany.getMapKey().getName());
            }
        }
    }

    protected void processOneToOne(List<OneToOne> parsedOneToOnes) {

        if (parsedOneToOnes == null) {
            return;
        }
//
//        List<ParsedOneToOne> parsedOneToOnes = parsedAttributes.getOneToOne();
        for (OneToOne parsedOneToOne : parsedOneToOnes) {
            List<String> cascadeTypes = getCascadeTypes(
                    parsedOneToOne.getCascade());

            JoinTableSnippet joinTable = getJoinTable(parsedOneToOne.getJoinTable());

            OneToOneSnippet oneToOne = new OneToOneSnippet();

            oneToOne.setCascadeTypes(cascadeTypes);
            oneToOne.setTargetEntity(parsedOneToOne.getTargetEntity());
            oneToOne.setTargetEntityPackage(parsedOneToOne.getConnectedEntity().getPackage(rootPackageName));
            
            oneToOne.setMappedBy(parsedOneToOne.getMappedBy());
            if (parsedOneToOne.getOptional() != null) {
                oneToOne.setOptional(parsedOneToOne.getOptional());
            }

            if (parsedOneToOne.getFetch() != null) {
                oneToOne.setFetchType(parsedOneToOne.getFetch().value());
            }
            oneToOne.setOrphanRemoval(parsedOneToOne.getOrphanRemoval());

            oneToOne.setPrimaryKey(parsedOneToOne.isPrimaryKey());
            oneToOne.setMapsId(parsedOneToOne.getMapsId());

            VariableDefSnippet variableDef = getVariableDef(parsedOneToOne);

            variableDef.setRelationDef(oneToOne);
            variableDef.setJoinTable(joinTable);
            variableDef.setJoinColumns(getJoinColumnsSnippet(parsedOneToOne, false));
            if (parsedOneToOne.getFunctionalType() != null) {
                variableDef.setFunctionalType(parsedOneToOne.getFunctionalType());
            }
        }
    }

    private JoinColumnsSnippet getJoinColumnsSnippet(JoinColumnHandler joinColumnHandler, boolean mapKey) {
        List<JoinColumnSnippet> joinColumnsList = getJoinColumns(joinColumnHandler.getJoinColumn(), mapKey);
        JoinColumnsSnippet joinColumns = null;
        if (!joinColumnsList.isEmpty()) {
            joinColumns = new JoinColumnsSnippet(mapKey);
            joinColumns.setJoinColumns(joinColumnsList);
            joinColumns.setForeignKey(getForeignKey(joinColumnHandler.getForeignKey()));
        }
        return joinColumns;
    }

    protected void processVersion(List<Version> parsedVersions) {
        if (parsedVersions == null) {
            return;
        }
        for (Version parsedVersion : parsedVersions) {
            VariableDefSnippet variableDef = getVariableDef(parsedVersion);

            ColumnDefSnippet columnDef = getColumnDef(parsedVersion.getColumn());
            variableDef.setType(parsedVersion.getAttributeType());
            if (parsedVersion.getFunctionalType() != null) {
                variableDef.setFunctionalType(parsedVersion.getFunctionalType());
            }
            variableDef.setVersion(true);
            variableDef.setColumnDef(columnDef);

            TemporalType parsedTemporalType = parsedVersion.getTemporal();
            TemporalSnippet temporal = null;
            if (parsedTemporalType != null) {
                temporal = new TemporalSnippet();
                temporal.setValue(parsedTemporalType);
            }
            variableDef.setTemporal(temporal);
        }
    }

    protected void processPrimaryKeyJoinColumns(List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns, ForeignKeySnippet primaryKeyForeignKey) {
        if (primaryKeyJoinColumns == null || primaryKeyJoinColumns.isEmpty()) {
            return;
        }
        classDef.setPrimaryKeyJoinColumns(new PrimaryKeyJoinColumnsSnippet());
        classDef.getPrimaryKeyJoinColumns().setPrimaryKeyJoinColumns(primaryKeyJoinColumns);
        classDef.getPrimaryKeyJoinColumns().setForeignKey(primaryKeyForeignKey);
    }

    protected void processSecondaryTable(
            List<SecondaryTable> parsedSecondaryTables) {

        if (parsedSecondaryTables == null || parsedSecondaryTables.isEmpty()) {
            return;
        }

        classDef.setSecondaryTables(new SecondaryTablesSnippet());

        for (SecondaryTable parsedSecondaryTable : parsedSecondaryTables) {
            List<PrimaryKeyJoinColumnSnippet> primaryKeyJoinColumns
                    = getPrimaryKeyJoinColumns(parsedSecondaryTable.getPrimaryKeyJoinColumn());

            List<UniqueConstraintSnippet> uniqueConstraints = getUniqueConstraints(
                    parsedSecondaryTable.getUniqueConstraint());

            SecondaryTableSnippet secondaryTable = new SecondaryTableSnippet();

            secondaryTable.setCatalog(parsedSecondaryTable.getCatalog());
            secondaryTable.setName(parsedSecondaryTable.getName());
            secondaryTable.setSchema(parsedSecondaryTable.getSchema());
            secondaryTable.setUniqueConstraints(uniqueConstraints);
            secondaryTable.setIndices(getIndexes(parsedSecondaryTable.getIndex()));
            secondaryTable.setPrimaryKeyJoinColumns(primaryKeyJoinColumns);
            secondaryTable.setForeignKey(getForeignKey(parsedSecondaryTable.getForeignKey()));

            classDef.getSecondaryTables().addSecondaryTable(secondaryTable);
        }
    }

    protected SequenceGeneratorSnippet processSequenceGenerator(SequenceGenerator parsedSequenceGenerator) {

        if (parsedSequenceGenerator == null || SequenceGeneratorValidator.isEmpty(parsedSequenceGenerator)) {
            return null;
        }

        SequenceGeneratorSnippet sequenceGenerator = new SequenceGeneratorSnippet();

        sequenceGenerator.setCatalog(parsedSequenceGenerator.getCatalog());
        sequenceGenerator.setSchema(parsedSequenceGenerator.getSchema());
        sequenceGenerator.setName(parsedSequenceGenerator.getName());
        sequenceGenerator.setSequenceName(parsedSequenceGenerator.getSequenceName());

        if (parsedSequenceGenerator.getAllocationSize() != null) {
            sequenceGenerator.setAllocationSize(
                    parsedSequenceGenerator.getAllocationSize());
        }

        if (parsedSequenceGenerator.getInitialValue() != null) {
            sequenceGenerator.setInitialValue(
                    parsedSequenceGenerator.getInitialValue());
        }

        return sequenceGenerator;
    }

    protected void processSequenceGeneratorEntity(SequenceGenerator parsedSequenceGenerator) {

        SequenceGeneratorSnippet sequenceGenerator = processSequenceGenerator(parsedSequenceGenerator);

        if (sequenceGenerator == null) {
            return;
        }

        VariableDefSnippet variableDef = null;
        boolean found = false;
        //The name of the SequenceGenerator must match the generator name in a
        //GeneratedValue with its strategy set to SEQUENCE.
        for (Map.Entry<String, VariableDefSnippet> entry : variables.entrySet()) {
            variableDef = entry.getValue();

            if (variableDef.getGeneratedValue() != null
                    && variableDef.getGeneratedValue().getGenerator().equals(
                            sequenceGenerator.getName())) {

                found = true;
                break;
            }
        }
        if (found) {
            variableDef.setSequenceGenerator(sequenceGenerator);
        } else {
            logger.log(Level.WARNING, "Ignoring : Cannot find variable for "
                    + "Sequence generator :" + sequenceGenerator.getName());
        }
    }

    protected void processSqlResultSetMapping(
            Set<SqlResultSetMapping> parsedSqlResultSetMappings) {

        if (parsedSqlResultSetMappings == null
                || parsedSqlResultSetMappings.isEmpty()) {
            return;
        }

        classDef.setSQLResultSetMappings(new SQLResultSetMappingsSnippet());

        for (SqlResultSetMapping parsedSqlResultSetMapping : parsedSqlResultSetMappings) {
            SQLResultSetMappingSnippet sqlResultSetMapping = new SQLResultSetMappingSnippet();

            List<ColumnResultSnippet> columnResults = getColumnResults(
                    parsedSqlResultSetMapping.getColumnResult());

            List<EntityResultSnippet> entityResults = getEntityResults(
                    parsedSqlResultSetMapping.getEntityResult());

            List<ConstructorResultSnippet> constructorResults = getConstructorResults(
                    parsedSqlResultSetMapping.getConstructorResult());

            sqlResultSetMapping.setColumnResults(columnResults);
            sqlResultSetMapping.setEntityResults(entityResults);
            sqlResultSetMapping.setConstructorResults(constructorResults);
            sqlResultSetMapping.setName(parsedSqlResultSetMapping.getName());

            classDef.getSQLResultSetMappings().addSQLResultSetMapping(
                    sqlResultSetMapping);
        }
    }

    protected void processTable(Table parsedTable) {

        if (parsedTable == null || TableValidator.isEmpty(parsedTable)) {
            return;
        }

        TableDefSnippet table = new TableDefSnippet();

        table.setCatalog(parsedTable.getCatalog());
        table.setName(parsedTable.getName());
        table.setSchema(parsedTable.getSchema());
        table.setUniqueConstraints(getUniqueConstraints(parsedTable.getUniqueConstraint()));
        table.setIndices(getIndexes(parsedTable.getIndex()));

        classDef.setTableDef(table);
    }

    protected void processCacheable(Boolean cacheable) {

        if (cacheable == null || Objects.equals(cacheable, Boolean.FALSE)) {
            return;
        }
        CacheableDefSnippet snippet = new CacheableDefSnippet();
        classDef.setCacheableDef(snippet);
    }

    protected TableGeneratorSnippet processTableGenerator(TableGenerator parsedTableGenerator) {

        if (parsedTableGenerator == null || TableGeneratorValidator.isEmpty(parsedTableGenerator)) {
            return null;
        }

        TableGeneratorSnippet tableGenerator = new TableGeneratorSnippet();

        if (parsedTableGenerator.getAllocationSize() != null) {
            tableGenerator.setAllocationSize(
                    parsedTableGenerator.getAllocationSize());
        }

        if (parsedTableGenerator.getInitialValue() != null) {
            tableGenerator.setInitialValue(
                    parsedTableGenerator.getInitialValue());
        }

        tableGenerator.setCatalog(parsedTableGenerator.getCatalog());
        tableGenerator.setName(parsedTableGenerator.getName());
        tableGenerator.setPkColumnName(parsedTableGenerator.getPkColumnName());
        tableGenerator.setPkColumnValue(
                parsedTableGenerator.getPkColumnValue());
        tableGenerator.setSchema(parsedTableGenerator.getSchema());
        tableGenerator.setTable(parsedTableGenerator.getTable());
        tableGenerator.setValueColumnName(
                parsedTableGenerator.getValueColumnName());
        tableGenerator.setUniqueConstraints(getUniqueConstraints(
                parsedTableGenerator.getUniqueConstraint()));
        tableGenerator.setIndices(getIndexes(parsedTableGenerator.getIndex()));

        return tableGenerator;
    }

    protected void processTableGeneratorEntity(TableGenerator parsedTableGenerator) {

        TableGeneratorSnippet tableGenerator = processTableGenerator(parsedTableGenerator);
        if (tableGenerator == null) {
            return;
        }

        VariableDefSnippet variableDef = null;
        boolean found = false;
        //The name of the TableGenerator must match the generator name in a
        //GeneratedValue with its strategy set to TABLE. The scope of the
        //generator name is global to the persistence unit
        //(across all generator types).
        for (Map.Entry<String, VariableDefSnippet> entry : variables.entrySet()) {
            variableDef = entry.getValue();

            if (variableDef.getGeneratedValue() != null
                    && variableDef.getGeneratedValue().getGenerator().equals(
                            parsedTableGenerator.getName())) {
                found = true;
                break;
            }
        }

        if (found) {
            variableDef.setTableGenerator(tableGenerator);
        } else {
            logger.log(Level.WARNING, "Ignoring : Cannot find variable for Table generator :{0}", tableGenerator.getName());
        }
    }
}
