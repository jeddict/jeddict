/**
 * Copyright [2017] Gaurav Gupta
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

import static java.lang.Boolean.TRUE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang.StringUtils;
import org.jcode.infra.JavaEEVersion;
import static org.jcode.infra.JavaEEVersion.JAVA_EE_8;
import org.netbeans.jpa.modeler.settings.code.CodePanel;
import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.AttributeOverride;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.CascadeType;
import org.netbeans.jpa.modeler.spec.CollectionTable;
import org.netbeans.jpa.modeler.spec.Column;
import org.netbeans.jpa.modeler.spec.ColumnResult;
import org.netbeans.jpa.modeler.spec.ConstructorResult;
import org.netbeans.jpa.modeler.spec.Convert;
import org.netbeans.jpa.modeler.spec.DefaultClass;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.EmptyType;
import org.netbeans.jpa.modeler.spec.Entity;
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
import org.netbeans.jpa.modeler.spec.extend.AttributeSnippet;
import org.netbeans.jpa.modeler.spec.extend.ClassMembers;
import org.netbeans.jpa.modeler.spec.extend.ClassSnippet;
import org.netbeans.jpa.modeler.spec.extend.Constructor;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.extend.JoinColumnHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyHandler;
import org.netbeans.jpa.modeler.spec.extend.MapKeyType;
import org.netbeans.jpa.modeler.spec.extend.ReferenceClass;
import org.netbeans.jpa.modeler.spec.extend.Snippet;
import org.netbeans.jpa.modeler.spec.extend.annotation.Annotation;
import org.netbeans.bean.validation.constraints.Constraint;
import org.netbeans.jpa.modeler.spec.extend.AnnotationLocation;
import org.netbeans.jpa.modeler.spec.extend.AttributeSnippetLocationType;
import org.netbeans.jpa.modeler.spec.extend.ClassSnippetLocationType;
import org.netbeans.jpa.modeler.spec.extend.IAttributes;
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
import org.netbeans.orm.converter.compiler.NamedQuerySnippet;
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
import org.netbeans.jpa.modeler.spec.extend.SnippetLocation;
import org.netbeans.jpa.modeler.spec.validator.ConvertValidator;
import org.netbeans.jsonb.converter.compiler.DateFormatSnippet;
import org.netbeans.jsonb.converter.compiler.NillableSnippet;
import org.netbeans.jsonb.converter.compiler.NumberFormatSnippet;
import org.netbeans.jsonb.converter.compiler.PropertyOrderSnippet;
import org.netbeans.jsonb.converter.compiler.PropertySnippet;
import org.netbeans.jsonb.converter.compiler.TransientSnippet;
import org.netbeans.jsonb.converter.compiler.TypeAdapterSnippet;
import org.netbeans.jsonb.converter.compiler.TypeDeserializerSnippet;
import org.netbeans.jsonb.converter.compiler.TypeSerializerSnippet;
import org.netbeans.jsonb.converter.compiler.VisibilitySnippet;
import org.netbeans.orm.converter.compiler.ConvertSnippet;
import org.netbeans.orm.converter.compiler.ConvertsSnippet;
import org.netbeans.orm.converter.compiler.OrderColumnSnippet;

public abstract class ClassGenerator<T extends ClassDefSnippet> {

    private static final Logger logger = ORMConvLogger.getLogger(ClassGenerator.class);

    protected String rootPackageName;
    protected String packageName;
    protected T classDef;
    protected Map<String, VariableDefSnippet> variables = new LinkedHashMap<>();
    protected JavaEEVersion javaEEVersion;
    protected boolean repeatable;

    public ClassGenerator(T classDef, JavaEEVersion javaEEVersion) {
        this.classDef = classDef;
        this.javaEEVersion = javaEEVersion;
        this.repeatable = javaEEVersion.getVersion()<JAVA_EE_8.getVersion();
    }

    public abstract T getClassDef();

    protected T initClassDef(String packageName, JavaClass javaClass) {
        ClassHelper classHelper = new ClassHelper(javaClass.getClazz());
        classHelper.setPackageName(packageName);
        classDef.setClassName(classHelper.getFQClassName());
//        classDef.setPackageName(classHelper.getPackageName());
        classDef.setAbstractClass(javaClass.getAbstract());

        if (!(javaClass instanceof DefaultClass)) { // custom interface support skiped for IdClass/EmbeddedId
            Set<ReferenceClass> interfaces = new LinkedHashSet<>(javaClass.getRootElement().getInterfaces());
            interfaces.addAll(javaClass.getInterfaces());
            classDef.setInterfaces(interfaces.stream().filter(ReferenceClass::isEnable).map(ReferenceClass::getName).collect(toList()));
        }
        
        classDef.setJSONBSnippets(getJSONBClassSnippet(javaClass));
        classDef.setAnnotation(getAnnotationSnippet(javaClass.getAnnotation()));
        classDef.getAnnotation().putAll(getAnnotationSnippet(javaClass.getRuntimeAnnotation()));
        
        List<ClassSnippet> snippets = new ArrayList<>(javaClass.getRootElement().getSnippets());
        snippets.addAll(javaClass.getSnippets());
        snippets.addAll(javaClass.getRuntimeSnippets());
        classDef.setCustomSnippet(getCustomSnippet(snippets));

        classDef.setVariableDefs(new ArrayList<>(variables.values()));

        classDef.setConstructors(getConstructorSnippets(javaClass));
        classDef.setHashcodeMethod(getHashcodeMethodSnippet(javaClass, getClassMembers(javaClass, javaClass.getHashCodeMethod())));
        classDef.setEqualsMethod(getEqualsMethodSnippet(javaClass, getClassMembers(javaClass, javaClass.getEqualsMethod())));
        classDef.setToStringMethod(getToStringMethodSnippet(javaClass, getClassMembers(javaClass, javaClass.getToStringMethod())));

        if (javaClass.getSuperclass() != null) {
            classDef.setSuperClassName(javaClass.getSuperclass().getFQN());
        } else if (javaClass.getSuperclassRef() != null) {
            classDef.setSuperClassName(javaClass.getSuperclassRef().getName());
        }
        return classDef;
    }

    private ClassMembers getClassMembers(JavaClass javaClass, ClassMembers classMembers) {
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

    protected <T extends AnnotationLocation> Map<T, List<AnnotationSnippet>> getAnnotationSnippet(List<? extends Annotation<T>> annotations) {
        Map<T, List<AnnotationSnippet>> snippetsMap = new HashMap<>();
        for (Annotation<T> annotation : annotations) {
            if (annotation.isEnable()) {
                if (snippetsMap.get(annotation.getLocationType()) == null) {
                    snippetsMap.put(annotation.getLocationType(), new ArrayList<>());
                }
                AnnotationSnippet snippet = new AnnotationSnippet();
                snippet.setName(annotation.getName());
                snippetsMap.get(annotation.getLocationType()).add(snippet);
            }
        }
        return snippetsMap;
    }

    protected <T extends SnippetLocation> Map<T, List<String>> getCustomSnippet(List<? extends Snippet<T>> snippets) {
        Map<T, List<String>> snippetsMap = new HashMap<>();
        for (Snippet<T> snippet : snippets) {
            if (snippet.isEnable()) {
                if (snippetsMap.get(snippet.getLocationType()) == null) {
                    snippetsMap.put(snippet.getLocationType(), new ArrayList<>());
                }
                String value = snippet.getValue();
                if(snippet.getLocationType() == ClassSnippetLocationType.IMPORT ||
                        snippet.getLocationType() == AttributeSnippetLocationType.IMPORT){
                    if(!value.startsWith("import")){
                        value = "import " + value;
                    }
                    if(!value.endsWith(";")){
                        value = value + ";";
                    }
                }
                snippetsMap.get(snippet.getLocationType()).add(value);
            }
        }
        return snippetsMap;
    }

    protected HashcodeMethodSnippet getHashcodeMethodSnippet(JavaClass javaClass, ClassMembers classMembers) {
        if (classMembers.getAttributes().isEmpty()
                && StringUtils.isBlank(classMembers.getPreCode())
                && StringUtils.isBlank(classMembers.getPostCode())) {
            return null;
        }
        return new HashcodeMethodSnippet(javaClass.getClazz(), classMembers);
    }

    protected EqualsMethodSnippet getEqualsMethodSnippet(JavaClass javaClass, ClassMembers classMembers) {
        if (classMembers.getAttributes().isEmpty()
                && StringUtils.isBlank(classMembers.getPreCode())
                && StringUtils.isBlank(classMembers.getPostCode())) {
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
        List<Constructor> constructors = javaClass.getConstructors();
        if (javaClass instanceof DefaultClass && constructors.isEmpty()) { //for EmbeddedId and IdClass
            constructors.add(Constructor.getNoArgsInstance());
            Constructor constructor = new Constructor();
            constructor.setAttributes(getClassMembers(javaClass, null).getAttributes());
            constructors.add(constructor);
        }

        constructors.stream().filter(Constructor::isEnable).map(constructor -> {
            String className = javaClass.getClazz();
            List<VariableDefSnippet> parentVariableSnippets = constructor.getAttributes()
                    .stream()
                    .filter(attr -> attr.getJavaClass() != javaClass)
                    .map(attr -> getVariableDef(attr))
                    .collect(toList());
            List<VariableDefSnippet> localVariableSnippets = constructor.getAttributes()
                    .stream()
                    .filter(attr -> attr.getJavaClass() == javaClass)
                    .map(attr -> getVariableDef(attr))
                    .collect(toList());
            ConstructorSnippet snippet = new ConstructorSnippet(className, constructor, parentVariableSnippets, localVariableSnippets);
            return snippet;
        }).forEach(constructorSnippets::add);
        return constructorSnippets;
    }

    protected List<ConstraintSnippet> getConstraintSnippet(Set<Constraint> constraints) {
        List<ConstraintSnippet> snippets = new ArrayList<>();
        for (Constraint constraint : constraints) {
            if (!constraint.getSelected() || constraint.isEmpty()) {
                continue;
            }
            ConstraintSnippet snippet = ConstraintSnippetFactory.getInstance(constraint);
            if (snippet != null) {
                snippets.add(snippet);
            }
        }
        return snippets;
    }
    
    protected List<org.netbeans.orm.converter.compiler.Snippet> getJSONBAttributeSnippet(Attribute attribute) {
        List<org.netbeans.orm.converter.compiler.Snippet> snippets = new ArrayList<>();
        if (attribute.getJsonbTransient()) {
            snippets.add(new TransientSnippet());
        } else {
            if (StringUtils.isNotBlank(attribute.getJsonbProperty()) || attribute.getJsonbNillable()) {
                snippets.add(new PropertySnippet(attribute.getJsonbProperty(), attribute.getJsonbNillable()));
            }
            if (attribute.getJsonbDateFormat() != null
                    && (StringUtils.isNotBlank(attribute.getJsonbDateFormat().getValue())
                    || StringUtils.isNotBlank(attribute.getJsonbDateFormat().getLocale()))) {
                snippets.add(new DateFormatSnippet(attribute.getJsonbDateFormat()));
            }
            if (attribute.getJsonbNumberFormat() != null
                    && (StringUtils.isNotBlank(attribute.getJsonbNumberFormat().getValue())
                    || StringUtils.isNotBlank(attribute.getJsonbNumberFormat().getLocale()))) {
                snippets.add(new NumberFormatSnippet(attribute.getJsonbNumberFormat()));
            }
            if (attribute.getJsonbTypeAdapter() != null
                    && attribute.getJsonbTypeAdapter().isEnable()
                    && !StringUtils.isBlank(attribute.getJsonbTypeAdapter().getName())) {
                snippets.add(new TypeAdapterSnippet(attribute.getJsonbTypeAdapter()));
            }
            if (attribute.getJsonbTypeDeserializer() != null
                    && attribute.getJsonbTypeDeserializer().isEnable()
                    && !StringUtils.isBlank(attribute.getJsonbTypeDeserializer().getName())) {
                snippets.add(new TypeDeserializerSnippet(attribute.getJsonbTypeDeserializer()));
            }
            if (attribute.getJsonbTypeSerializer() != null
                    && attribute.getJsonbTypeSerializer().isEnable()
                    && !StringUtils.isBlank(attribute.getJsonbTypeSerializer().getName())) {
                snippets.add(new TypeSerializerSnippet(attribute.getJsonbTypeSerializer()));
            }
        }
        return snippets;
    }

    protected List<org.netbeans.orm.converter.compiler.Snippet> getJSONBClassSnippet(JavaClass<IAttributes> javaClass) {
        List<org.netbeans.orm.converter.compiler.Snippet> snippets = new ArrayList<>();
        if(javaClass.getJsonbNillable()){
            snippets.add(new NillableSnippet(javaClass.getJsonbNillable()));
        }
        if (!javaClass.getJsonbPropertyOrder().isEmpty()) {
            snippets.add(new PropertyOrderSnippet(javaClass.getJsonbPropertyOrder()
                    .stream()
                    .map(Attribute::getName)
                    .collect(toList())
                )
            );
        }
        if (javaClass.getJsonbDateFormat() != null && !javaClass.getJsonbDateFormat().isEmpty()) {
            snippets.add(new DateFormatSnippet(javaClass.getJsonbDateFormat()));
        }
        if (javaClass.getJsonbNumberFormat() != null && !javaClass.getJsonbNumberFormat().isEmpty()) {
            snippets.add(new NumberFormatSnippet(javaClass.getJsonbNumberFormat()));
        }
        if(javaClass.getJsonbTypeAdapter()!=null 
                && javaClass.getJsonbTypeAdapter().isEnable() 
                && !StringUtils.isBlank(javaClass.getJsonbTypeAdapter().getName())){
            snippets.add(new TypeAdapterSnippet(javaClass.getJsonbTypeAdapter()));
        }
        if(javaClass.getJsonbTypeDeserializer()!=null 
                && javaClass.getJsonbTypeDeserializer().isEnable() 
                && !StringUtils.isBlank(javaClass.getJsonbTypeDeserializer().getName())){
            snippets.add(new TypeDeserializerSnippet(javaClass.getJsonbTypeDeserializer()));
        }
        if(javaClass.getJsonbTypeSerializer()!=null 
                && javaClass.getJsonbTypeSerializer().isEnable() 
                && !StringUtils.isBlank(javaClass.getJsonbTypeSerializer().getName())){
            snippets.add(new TypeSerializerSnippet(javaClass.getJsonbTypeSerializer()));
        }
        if(javaClass.getJsonbVisibility()!=null 
                && javaClass.getJsonbVisibility().isEnable() 
                && !StringUtils.isBlank(javaClass.getJsonbVisibility().getName())){
            snippets.add(new VisibilitySnippet(javaClass.getJsonbVisibility()));
        }
        return snippets;
    }

    protected List<org.netbeans.orm.converter.compiler.Snippet> getJSONBCPackageSnippet(EntityMappings entityMappings) {
        List<org.netbeans.orm.converter.compiler.Snippet> snippets = new ArrayList<>();
        if(entityMappings.getJsonbNillable()){
            snippets.add(new NillableSnippet(entityMappings.getJsonbNillable()));
        }
        if (entityMappings.getJsonbDateFormat() != null
                && (StringUtils.isNotBlank(entityMappings.getJsonbDateFormat().getValue())
                || StringUtils.isNotBlank(entityMappings.getJsonbDateFormat().getLocale()))) {
            snippets.add(new DateFormatSnippet(entityMappings.getJsonbDateFormat()));
        }
        if (entityMappings.getJsonbNumberFormat() != null
                && (StringUtils.isNotBlank(entityMappings.getJsonbNumberFormat().getValue())
                || StringUtils.isNotBlank(entityMappings.getJsonbNumberFormat().getLocale()))) {
            snippets.add(new NumberFormatSnippet(entityMappings.getJsonbNumberFormat()));
        }
        if (entityMappings.getJsonbVisibility() != null 
                && entityMappings.getJsonbVisibility().isEnable() 
                && !StringUtils.isBlank(entityMappings.getJsonbVisibility().getName())){
            snippets.add(new VisibilitySnippet(entityMappings.getJsonbVisibility()));
        }
        return snippets;
    }

    protected VariableDefSnippet getVariableDef(Attribute attr) {
        VariableDefSnippet variableDef = variables.get(attr.getName());
        if (variableDef == null) {
            variableDef = new VariableDefSnippet(attr);
            variableDef.setAccessModifier(attr.getAccessModifier());
            variableDef.setName(attr.getName());
            variableDef.setDefaultValue(attr.getDefaultValue());
            variableDef.setDescription(attr.getDescription());
            if (CodePanel.isJavaSESupportEnable()) {
                variableDef.setPropertyChangeSupport(TRUE.equals(attr.getPropertyChangeSupport()));
                variableDef.setVetoableChangeSupport(TRUE.equals(attr.getVetoableChangeSupport()));
                if (TRUE.equals(attr.getPropertyChangeSupport())) {
                    classDef.setPropertyChangeSupport(true);
                }
                if (TRUE.equals(attr.getVetoableChangeSupport())) {
                    classDef.setVetoableChangeSupport(true);
                }
            }

            variableDef.setAttributeConstraints(getConstraintSnippet(attr.getAttributeConstraints()));
            variableDef.setKeyConstraints(getConstraintSnippet(attr.getKeyConstraints()));
            variableDef.setValueConstraints(getConstraintSnippet(attr.getValueConstraints()));
            variableDef.setJSONBSnippets(getJSONBAttributeSnippet(attr));

            List<AttributeSnippet> snippets = new ArrayList<>();//todo global attribute snippet at class level ; javaClass.getAttrSnippets());
            snippets.addAll(attr.getSnippets());
            snippets.addAll(attr.getRuntimeSnippets());
            variableDef.setCustomSnippet(getCustomSnippet(snippets));
            variableDef.setAnnotation(getAnnotationSnippet(attr.getAnnotation()));
            variableDef.getAnnotation().putAll(getAnnotationSnippet(attr.getRuntimeAnnotation()));
            
            variableDef.setJaxbVariableType(attr.getJaxbVariableType());
            variableDef.setJaxbWrapperMetadata(attr.getJaxbWrapperMetadata());
            variableDef.setJaxbMetadata(attr.getJaxbMetadata());
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
            variableDef.setFunctionalType(parsedBasic.isOptionalReturnType());
            variableDef.setConverts(processConverts(Collections.singletonList(parsedBasic.getConvert())));
            variableDef.setLob(parsedLob != null);
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
            elementCollection.setCollectionImplType(parsedElementCollection.getCollectionImplType());
            elementCollection.setMapKeySnippet(updateMapKeyAttributeSnippet(parsedElementCollection));
            elementCollection.setTargetClass(parsedElementCollection.getAttributeType());
            if (parsedElementCollection.getConnectedClass() != null) {
                elementCollection.setTargetClassPackage(parsedElementCollection.getConnectedClass().getAbsolutePackage(rootPackageName));
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
            } else if (parsedElementCollection.getOrderColumn() != null) {
                variableDef.setOrderColumn(new OrderColumnSnippet(parsedElementCollection.getOrderColumn()));
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

            List<Convert> converts = new ArrayList<>();
            converts.addAll(parsedElementCollection.getMapKeyConverts());
            converts.addAll(parsedElementCollection.getConverts());
            variableDef.setConverts(processConverts(converts));
        }
    }

    protected void processTransient(List<Transient> parsedTransients) {

        for (Transient parsedTransient : parsedTransients) {
            VariableDefSnippet variableDef = getVariableDef(parsedTransient);
            variableDef.setType(parsedTransient.getAttributeType());
            variableDef.setTranzient(true);
            variableDef.setFunctionalType(parsedTransient.isOptionalReturnType());
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

        classDef.setAssociationOverrides(new AssociationOverridesSnippet(repeatable));

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

            classDef.getAssociationOverrides().add(associationOverride);
        }
        if (classDef.getAssociationOverrides() != null && classDef.getAssociationOverrides().get().isEmpty()) {
            classDef.setAssociationOverrides(null);
        }
    }

    protected AttributeOverridesSnippet processAttributeOverrides(
            Set<AttributeOverride> parsedAttributeOverrides) {

        if (parsedAttributeOverrides == null || parsedAttributeOverrides.isEmpty()) {
            return null;
        }
        AttributeOverridesSnippet attributeOverridesSnippet = new AttributeOverridesSnippet(repeatable);

        for (AttributeOverride parsedAttributeOverride : parsedAttributeOverrides) {

            ColumnDefSnippet columnDef = getColumnDef(parsedAttributeOverride.getColumn());
            if (columnDef == null) {
                continue;
            }
            AttributeOverrideSnippet attributeOverride = new AttributeOverrideSnippet();

            attributeOverride.setColumnDef(columnDef);
            attributeOverride.setName(parsedAttributeOverride.getName());

            attributeOverridesSnippet.add(attributeOverride);
        }

        if (attributeOverridesSnippet.get().isEmpty()) {
            return null;
        }

        return attributeOverridesSnippet;
    }

    protected void processEntityListeners(EntityListeners parsedEntityListeners) {

        if (parsedEntityListeners == null) {
            return;
        }

        Set<ReferenceClass> parsedEntityListenersList = parsedEntityListeners.getEntityListener();

        List<EntityListenerSnippet> entityListeners = GeneratorUtil.processEntityListeners(parsedEntityListenersList, packageName);

        if (entityListeners.isEmpty()) {
            return;
        }

        classDef.setEntityListeners(new EntityListenersSnippet());
        classDef.getEntityListeners().setEntityListeners(entityListeners);
    }

    protected ConvertsSnippet processConverts(List<Convert> parsedConverts) {

        if (parsedConverts == null || parsedConverts.isEmpty()) {
            return null;
        }

        ConvertsSnippet convertsSnippet = new ConvertsSnippet(repeatable);

        for (Convert parsedConvert : parsedConverts) {
            if (parsedConvert != null && ConvertValidator.isNotEmpty(parsedConvert)) {
                ConvertSnippet convertSnippet = new ConvertSnippet(parsedConvert);
                convertsSnippet.add(convertSnippet);
            }

        }
        if (!convertsSnippet.get().isEmpty()) {
            return convertsSnippet;
        }

        return null;
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

        NamedEntityGraphsSnippet namedEntityGraphs = new NamedEntityGraphsSnippet(repeatable);

        for (NamedEntityGraph parsedNamedEntityGraph : parsedNamedEntityGraphs) {
            if (parsedNamedEntityGraph.isEnable()) {
                NamedEntityGraphSnippet namedEntityGraph = new NamedEntityGraphSnippet();
                namedEntityGraph.setName(parsedNamedEntityGraph.getName());
                namedEntityGraph.setIncludeAllAttributes(parsedNamedEntityGraph.isIncludeAllAttributes());
                namedEntityGraph.setNamedAttributeNodes(getNamedAttributeNodes(parsedNamedEntityGraph.getNamedAttributeNode()));
                namedEntityGraph.setSubgraphs(getNamedSubgraphs(parsedNamedEntityGraph.getSubgraph()));
                namedEntityGraph.setSubclassSubgraphs(getNamedSubgraphs(parsedNamedEntityGraph.getSubclassSubgraph()));

                namedEntityGraphs.add(namedEntityGraph);
            }

        }
        if (!namedEntityGraphs.isEmpty()) {
            classDef.setNamedEntityGraphs(namedEntityGraphs);
        }
    }

    protected void processNamedStoredProcedureQueries(EntityMappings entityMappings, List<NamedStoredProcedureQuery> parsedNamedStoredProcedureQueries) {

        if (parsedNamedStoredProcedureQueries == null || parsedNamedStoredProcedureQueries.isEmpty()) {
            return;
        }

        NamedStoredProcedureQueriesSnippet namedStoredProcedureQueries = new NamedStoredProcedureQueriesSnippet(repeatable);

        for (NamedStoredProcedureQuery parsedNamedStoredProcedureQuery : parsedNamedStoredProcedureQueries) {
            if (parsedNamedStoredProcedureQuery.isEnable()) {
                NamedStoredProcedureQuerySnippet namedStoredProcedureQuery = new NamedStoredProcedureQuerySnippet();
                namedStoredProcedureQuery.setName(parsedNamedStoredProcedureQuery.getName());
                namedStoredProcedureQuery.setProcedureName(parsedNamedStoredProcedureQuery.getProcedureName());
                namedStoredProcedureQuery.setQueryHints(getQueryHints(parsedNamedStoredProcedureQuery.getHint()));
                namedStoredProcedureQuery.setResultClasses(getResultClasses(entityMappings, parsedNamedStoredProcedureQuery.getResultClass()));
                namedStoredProcedureQuery.setResultSetMappings(parsedNamedStoredProcedureQuery.getResultSetMapping());
                namedStoredProcedureQuery.setParameters(getStoredProcedureParameters(parsedNamedStoredProcedureQuery.getParameter()));

                namedStoredProcedureQueries.add(namedStoredProcedureQuery);
            }
        }
        if (!namedStoredProcedureQueries.isEmpty()) {
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

        NamedNativeQueriesSnippet namedNativeQueries = new NamedNativeQueriesSnippet(repeatable);

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
                namedNativeQueries.add(namedNativeQuery);
            }
        }

        if (!namedNativeQueries.isEmpty()) {
            classDef.setNamedNativeQueries(namedNativeQueries);
        }
    }

    protected void processNamedQueries(
            List<NamedQuery> parsedNamedQueries) {

        if (parsedNamedQueries == null || parsedNamedQueries.isEmpty()) {
            return;
        }

        NamedQueriesSnippet namedQueries = new NamedQueriesSnippet(repeatable);

        for (NamedQuery parsedNamedQuery : parsedNamedQueries) {

            if (parsedNamedQuery.isEnable()) {
                List<QueryHintSnippet> queryHints = getQueryHints(parsedNamedQuery.getHint());

                NamedQuerySnippet namedQuery = new NamedQuerySnippet();
                namedQuery.setName(parsedNamedQuery.getName());
                namedQuery.setQuery(parsedNamedQuery.getQuery());
                //  namedQuery.setAttributeType(parsedNamedQuery.getAttributeType());
                namedQuery.setQueryHints(queryHints);
                namedQuery.setLockMode(parsedNamedQuery.getLockMode());

                namedQueries.add(namedQuery);
            }
        }

        if (!namedQueries.isEmpty()) {
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
            variableDef.setType(parsedEmbeded.getConnectedClass().getAbsolutePackage(rootPackageName) + ORMConverterUtil.DOT
                    + parsedEmbeded.getAttributeType());
            variableDef.setFunctionalType(parsedEmbeded.isOptionalReturnType());
            variableDef.setConverts(processConverts(parsedEmbeded.getConverts()));
            processInternalAttributeOverride(variableDef, parsedEmbeded.getAttributeOverride());
            processInternalAssociationOverride(variableDef, parsedEmbeded.getAssociationOverride());
        }
    }

    private void processInternalAttributeOverride(AttributeOverridesHandler attrHandler, Set<AttributeOverride> attributeOverrrides) {
        if (attributeOverrrides != null && !attributeOverrrides.isEmpty()
                && attrHandler.getAttributeOverrides() == null) {
            attrHandler.setAttributeOverrides(new AttributeOverridesSnippet(repeatable));
        }
        for (AttributeOverride parsedAttributeOverride : attributeOverrrides) {
            AttributeOverrideSnippet attributeOverride = new AttributeOverrideSnippet();
            ColumnDefSnippet columnDef = getColumnDef(parsedAttributeOverride.getColumn());
            if (columnDef == null) {
                continue;
            }
            attributeOverride.setColumnDef(columnDef);
            attributeOverride.setName(parsedAttributeOverride.getName());
            attrHandler.getAttributeOverrides().add(attributeOverride);
        }
        if (attrHandler.getAttributeOverrides() != null && attrHandler.getAttributeOverrides().get().isEmpty()) {
            attrHandler.setAttributeOverrides(null);
        }
    }

    private void processInternalAssociationOverride(AssociationOverridesHandler assoHandler, Set<AssociationOverride> associationOverrrides) {

        if (associationOverrrides != null && !associationOverrrides.isEmpty()
                && assoHandler.getAssociationOverrides() == null) {
            assoHandler.setAssociationOverrides(new AssociationOverridesSnippet(repeatable));
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

            assoHandler.getAssociationOverrides().add(associationOverride);
        }
        if (assoHandler.getAssociationOverrides() != null && assoHandler.getAssociationOverrides().get().isEmpty()) {
            assoHandler.setAssociationOverrides(null);
        }
    }

    protected void processEmbeddedId(IdentifiableClass identifiableClass, EmbeddedId parsedEmbeddedId) {
        if (parsedEmbeddedId == null || !identifiableClass.isEmbeddedIdType()) {
            return;
        }

        VariableDefSnippet variableDef = getVariableDef(parsedEmbeddedId);
        variableDef.setEmbeddedId(true);
        /**
         * Filter if Embeddable class is used in case of derived entities. Refer
         * : JPA Spec 2.4.1.3 Example 5(b)
         */
        if (identifiableClass.isEmbeddedIdType() && parsedEmbeddedId.getConnectedClass() == null) {
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
            variableDef.setFunctionalType(parsedId.isOptionalReturnType());
            variableDef.setPrimaryKey(true);

            Column parsedColumn = parsedId.getColumn();

            if (parsedColumn != null) {
                ColumnDefSnippet columnDef = getColumnDef(parsedColumn);
                variableDef.setColumnDef(columnDef);
            }
            GeneratedValue parsedGeneratedValue = parsedId.getGeneratedValue();
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
        for (ManyToMany parsedManyToMany : parsedManyToManys) {
            List<String> cascadeTypes = getCascadeTypes(parsedManyToMany.getCascade());

            JoinTableSnippet joinTable = getJoinTable(parsedManyToMany.getJoinTable());

            ManyToManySnippet manyToMany = new ManyToManySnippet();

            manyToMany.setCollectionType(parsedManyToMany.getCollectionType());
            manyToMany.setCollectionImplType(parsedManyToMany.getCollectionImplType());
            manyToMany.setMapKeySnippet(updateMapKeyAttributeSnippet(parsedManyToMany));
            manyToMany.setMappedBy(parsedManyToMany.getMappedBy());
            manyToMany.setTargetEntity(parsedManyToMany.getTargetEntity());
            manyToMany.setTargetEntityPackage(parsedManyToMany.getConnectedEntity().getAbsolutePackage(rootPackageName));
            manyToMany.setTargetField(parsedManyToMany.getConnectedAttributeName());
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
            } else if (parsedManyToMany.getOrderColumn() != null) {
                variableDef.setOrderColumn(new OrderColumnSnippet(parsedManyToMany.getOrderColumn()));
            }

//            variableDef.setType(parsedManyToMany.getAttributeType());
            if (parsedManyToMany.getMapKey() != null) {
                variableDef.setMapKey(parsedManyToMany.getMapKey().getName());
            }
            variableDef.setConverts(processConverts(parsedManyToMany.getMapKeyConverts()));
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
                joinColumns = new JoinColumnsSnippet(repeatable, true);
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
            manyToOne.setTargetEntityPackage(parsedManyToOne.getConnectedEntity().getAbsolutePackage(rootPackageName));
            manyToOne.setTargetField(parsedManyToOne.getConnectedAttributeName());
            manyToOne.setCascadeTypes(cascadeTypes);

            if (parsedManyToOne.getOptional() != null) {
                manyToOne.setOptional(parsedManyToOne.getOptional());
            }

            if (parsedManyToOne.getFetch() != null) {
                manyToOne.setFetchType(parsedManyToOne.getFetch().value());
            } else if(CodePanel.isLazyDefaultTypeForSingleAssociation()){
                manyToOne.setFetchType(FetchType.LAZY.value());
            }
            
            manyToOne.setPrimaryKey(parsedManyToOne.isPrimaryKey());
            manyToOne.setMapsId(parsedManyToOne.getMapsId());

            VariableDefSnippet variableDef = getVariableDef(parsedManyToOne);

            variableDef.setRelationDef(manyToOne);
            variableDef.setJoinTable(joinTable);
            variableDef.setJoinColumns(getJoinColumnsSnippet(parsedManyToOne, false));
            variableDef.setFunctionalType(parsedManyToOne.isOptionalReturnType());
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
            oneToMany.setTargetEntityPackage(parsedOneToMany.getConnectedEntity().getAbsolutePackage(rootPackageName));
            oneToMany.setTargetField(parsedOneToMany.getConnectedAttributeName());
            oneToMany.setMappedBy(parsedOneToMany.getMappedBy());
            oneToMany.setCollectionType(parsedOneToMany.getCollectionType());
            oneToMany.setCollectionImplType(parsedOneToMany.getCollectionImplType());
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
            } else if (parsedOneToMany.getOrderColumn() != null) {
                variableDef.setOrderColumn(new OrderColumnSnippet(parsedOneToMany.getOrderColumn()));
            }

            if (parsedOneToMany.getMapKey() != null) {
                variableDef.setMapKey(parsedOneToMany.getMapKey().getName());
            }
            variableDef.setConverts(processConverts(parsedOneToMany.getMapKeyConverts()));
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
            oneToOne.setTargetEntityPackage(parsedOneToOne.getConnectedEntity().getAbsolutePackage(rootPackageName));
            oneToOne.setTargetField(parsedOneToOne.getConnectedAttributeName());
            oneToOne.setMappedBy(parsedOneToOne.getMappedBy());
            if (parsedOneToOne.getOptional() != null) {
                oneToOne.setOptional(parsedOneToOne.getOptional());
            }

            if (parsedOneToOne.getFetch() != null) {
                oneToOne.setFetchType(parsedOneToOne.getFetch().value());
            } else if(CodePanel.isLazyDefaultTypeForSingleAssociation()){
                oneToOne.setFetchType(FetchType.LAZY.value());
            }
            
            oneToOne.setOrphanRemoval(parsedOneToOne.getOrphanRemoval());

            oneToOne.setPrimaryKey(parsedOneToOne.isPrimaryKey());
            oneToOne.setMapsId(parsedOneToOne.getMapsId());

            VariableDefSnippet variableDef = getVariableDef(parsedOneToOne);

            variableDef.setRelationDef(oneToOne);
            variableDef.setJoinTable(joinTable);
            variableDef.setJoinColumns(getJoinColumnsSnippet(parsedOneToOne, false));
            variableDef.setFunctionalType(parsedOneToOne.isOptionalReturnType());
        }
    }

    private JoinColumnsSnippet getJoinColumnsSnippet(JoinColumnHandler joinColumnHandler, boolean mapKey) {
        List<JoinColumnSnippet> joinColumnsList = getJoinColumns(joinColumnHandler.getJoinColumn(), mapKey);
        JoinColumnsSnippet joinColumns = null;
        if (!joinColumnsList.isEmpty()) {
            joinColumns = new JoinColumnsSnippet(repeatable, mapKey);
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
            variableDef.setFunctionalType(parsedVersion.isOptionalReturnType());
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
        classDef.setPrimaryKeyJoinColumns(new PrimaryKeyJoinColumnsSnippet(repeatable));
        classDef.getPrimaryKeyJoinColumns().setPrimaryKeyJoinColumns(primaryKeyJoinColumns);
        classDef.getPrimaryKeyJoinColumns().setForeignKey(primaryKeyForeignKey);
    }

    protected void processSecondaryTable(
            List<SecondaryTable> parsedSecondaryTables) {

        if (parsedSecondaryTables == null || parsedSecondaryTables.isEmpty()) {
            return;
        }

        classDef.setSecondaryTables(new SecondaryTablesSnippet(repeatable));

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

            classDef.getSecondaryTables().add(secondaryTable);
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

        classDef.setSQLResultSetMappings(new SQLResultSetMappingsSnippet(repeatable));

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

            classDef.getSQLResultSetMappings().add(sqlResultSetMapping);
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

        if (cacheable == null) { //Implicit Disable (!Force Disable)
            return;
        }
        CacheableDefSnippet snippet = new CacheableDefSnippet(cacheable);
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
