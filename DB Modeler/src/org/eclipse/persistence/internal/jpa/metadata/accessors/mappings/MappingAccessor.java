/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     08/27/2008-1.1 Guy Pelletier 
 *       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/03/2009-2.0 Guy Pelletier
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     06/25/2009-2.0 Michael O'Brien 
 *       - 266912: change MappedSuperclass handling in stage2 to pre process accessors
 *          in support of the custom descriptors holding mappings required by the Metamodel. 
 *          We handle undefined parameterized generic types for a MappedSuperclass defined
 *          Map field by returning Void in this case.
 *     09/29/2009-2.0 Guy Pelletier 
 *       - 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 *     01/22/2010-2.0.1 Guy Pelletier 
 *       - 294361: incorrect generated table for element collection attribute overrides
 *     01/26/2010-2.0.1 Guy Pelletier 
 *       - 299893: @MapKeyClass does not work with ElementCollection
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 *     04/09/2010-2.1 Guy Pelletier 
 *       - 307050: Add defaults for access methods of a VIRTUAL access type
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     05/19/2010-2.1 Guy Pelletier 
 *       - 313574: Lower case primary key column association does not work when upper casing flag is set to true
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     07/05/2010-2.1.1 Guy Pelletier 
 *       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
 *     08/20/2010-2.2 Guy Pelletier 
 *       - 323252: Canonical model generator throws NPE on virtual 1-1 or M-1 mapping
 *     09/03/2010-2.2 Guy Pelletier 
 *       - 317286: DB column lenght not in sync between @Column and @JoinColumn
 *     10/27/2010-2.2 Guy Pelletier 
 *       - 328114: @AttributeOverride does not work with nested embeddables having attributes of the same name
 *     12/01/2010-2.2 Guy Pelletier 
 *       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification 
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 *      *     30/05/2012-2.4 Guy Pelletier    
 *       - 354678: Temp classloader is still being used during metadata processing
 *     10/09/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     10/25/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     10/30/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     11/19/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 *     11/28/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     12/07/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 *     02/20/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 *     07/16/2013-2.5.1 Guy Pelletier 
 *       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
 *     06/20/2014-2.5.2 Rick Curtis 
 *       - 437760: AttributeOverride with no column name defined doesn't work.       
 *     07/01/2014-2.5.3 Rick Curtis 
 *       - 375101: Date and Calendar should not require @Temporal.
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.eclipse.persistence.annotations.Properties;
import org.eclipse.persistence.annotations.Property;
import org.eclipse.persistence.annotations.ReturnInsert;
import org.eclipse.persistence.annotations.ReturnUpdate;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.descriptors.VirtualAttributeAccessor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy;
import org.eclipse.persistence.internal.indirection.WeavedObjectBasicIndirectionPolicy;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.ForeignKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.AbstractConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ClassInstanceMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.EnumeratedMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.JSONMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.KryoMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.LobMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.SerializedMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TemporalMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.XMLMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.MapKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.mappings.converters.AttributeNamePrefix;
import org.eclipse.persistence.internal.mappings.converters.AttributeNameTokenizer;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.ContainerMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.EmbeddableMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.mappings.foundation.MapComponentMapping;
import org.eclipse.persistence.mappings.foundation.MapKeyMapping;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.EL_ACCESS_VIRTUAL;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ACCESS_FIELD;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ACCESS_PROPERTY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_CONVERT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_CONVERTS;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_FETCH_EAGER;

/**
 * INTERNAL:
 * An abstract mapping accessor. Holds common metadata for all mappings.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be handled in the merge
 *   method. (merging is done at the accessor/mapping level)
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject  method.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public abstract class MappingAccessor extends MetadataAccessor {

    // Used for looking up attribute overrides for a map accessor.
    /** Dot notation key prefix. */
    protected static final String KEY_DOT_NOTATION
            = AttributeNamePrefix.KEY.getName() + AttributeNameTokenizer.SEPARATOR;
    /** Dot notation value prefix. */
    protected static final String VALUE_DOT_NOTATION
            = AttributeNamePrefix.VALUE.getName() + AttributeNameTokenizer.SEPARATOR;

    private final static String DEFAULT_MAP_KEY_COLUMN_SUFFIX = "_KEY";

    private ClassAccessor m_classAccessor;
    private DatabaseMapping m_mapping;
    private DatabaseMapping m_overrideMapping;
    private Map<String, PropertyMetadata> m_properties = new HashMap<String, PropertyMetadata>();
    private String m_attributeType;
    protected ColumnMetadata m_field;
    
    /**
     * INTERNAL:
     */
    protected MappingAccessor(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor.getDescriptor(), classAccessor.getProject());
        
        // We must keep a reference to the class accessors where this
        // mapping accessor is defined. We need it to determine access types.
        m_classAccessor = classAccessor;
        
        // Once the class accessor is initialized, we can look for an explicit 
        // access type specification.
        initAccess();

        // Any mapping type may have a field for EIS/NoSQL data.
        if (isAnnotationPresent("org.eclipse.persistence.nosql.annotations.Field")) {
            m_field = new ColumnMetadata(getAnnotation("org.eclipse.persistence.nosql.annotations.Field"), this);
        }
        
        // Set the converts if some are present. 
        // Process all the converts first.
        if (isAnnotationPresent(JPA_CONVERTS)) {
            for (Object convert : getAnnotation(JPA_CONVERTS).getAttributeArray("value")) {
                addConvertMetadata(new ConvertMetadata((MetadataAnnotation) convert, this));
            }
        }
        
        // Process the single convert second.
        if (isAnnotationPresent(JPA_CONVERT)) {
            addConvertMetadata(new ConvertMetadata(getAnnotation(JPA_CONVERT), this));
        }
    }
    
    /**
     * INTERNAL:
     */
    protected MappingAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     * Subclasses that support converts need to override this method otherwise
     * an exception will be thrown from those accessors that do not support them
     * when a user has defined them on that accessor.
     */
    protected void addConvert(ConvertMetadata convert) {
        throw ValidationException.invalidMappingForConvert(getJavaClassName(), getAttributeName());
    }
    
    /**
     * INTERNAL:
     * Add a JPA convert annotation to the converts list. If it is a map key 
     * convert, pass it on to the map key converts list.
     */
    protected void addConvertMetadata(ConvertMetadata convert) {
        if (convert.isForMapKey()) {
            // This isForMapKey call will remove the key prefix when there is one.
            addMapKeyConvert(convert);
        } else {
            addConvert(convert);
        }
    }
    
    /**
     * INTERNAL:
     * Process an attribute override for either an embedded object mapping, or
     * an element collection mapping containing embeddable objects.
     */
    protected void addFieldNameTranslation(EmbeddableMapping embeddableMapping, String overrideName, DatabaseField overrideField, MappingAccessor aggregatesAccessor) {
        DatabaseMapping aggregatesMapping = aggregatesAccessor.getMapping();
        DatabaseField aggregatesMappingField = aggregatesMapping.getField();
        
        // If we are specifying an attribute override to an id field that is
        // within the embeddable we must update the primary key field on the
        // owning descriptor.
        if (aggregatesAccessor.isId()) {
            updatePrimaryKeyField(aggregatesAccessor, overrideField);
        }
        
        if (overrideName.indexOf(".") > -1) {
            // Set the nested field name translation on the mapping. Nested 
            // (dot notation) overrides are initialized slightly different then
            // core field name translations which are based on column names. In
            // JPA we need to rely and differentiate based on the override
            // (attribute) name.
            embeddableMapping.addNestedFieldTranslation(overrideName, overrideField, aggregatesMappingField.getName());
        } else {
            // Set the field name translation on the mapping.
            embeddableMapping.addFieldTranslation(overrideField, aggregatesMappingField.getName());
        }
    }
    
    /**
     * INTERNAL:
     * Subclasses that support converts need to override this method otherwise
     * an exception will be thrown from those accessors that do not support them
     * when a user has defined them on that accessor.
     */
    protected void addMapKeyConvert(ConvertMetadata convert) {
        throw ValidationException.invalidMappingForMapKeyConvert(getJavaClassName(), getAttributeName());
    }
    
    /**
     * INTERNAL:
     * Return true is this accessor is a derived id accessor.
     * @see ObjectAccessor
     */
    public boolean derivesId() {
        return false;
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof MappingAccessor) {
            MappingAccessor mappingAccessor = (MappingAccessor) objectToCompare;
            
            // For extra safety compare that the owning class accessors of these
            // mapping accessors are the same.
            if (! valuesMatch(getClassAccessor(), mappingAccessor.getClassAccessor())) {
                return false;
            }

            if (! valuesMatch(m_field, mappingAccessor.getField())) {
                return false;
            }
            
            return valuesMatch(getAttributeType(), mappingAccessor.getAttributeType());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Return the annotation if it exists.
     */
    @Override
    protected MetadataAnnotation getAnnotation(String annotation) {
        return getAccessibleObject().getAnnotation(annotation, getClassAccessor());
    }
    
    /**
     * INTERNAL:
     * Process the list of association overrides into a map, merging and 
     * overriding any association overrides where necessary with descriptor
     * level association overrides.
     */
    protected Map<String, AssociationOverrideMetadata> getAssociationOverrides(List<AssociationOverrideMetadata> associationOverrides) {
        // TODO: Be nice to look for duplicates within the same list.
        Map<String, AssociationOverrideMetadata> associationOverridesMap = new HashMap<String, AssociationOverrideMetadata>();
        
        for (AssociationOverrideMetadata associationOverride : associationOverrides) {
            String name = associationOverride.getName();
            
            // An association override from a sub-entity class will name its
            // association override slightly different in that it will have 
            // one extra dot notation at the front. E.G. A mapped superclass 
            // that defines an embedded attribute named 'record' can define 
            // association overrides directly on the mapping, that is, 
            // 'date'. Whereas from an entity class to override 'date' on 
            // 'record', the attribute name will be 'record.date'
            String dotNotationName = getAttributeName() + "." + name;
            if (getClassAccessor().isMappedSuperclass() && getDescriptor().hasAssociationOverrideFor(dotNotationName)) {
                getLogger().logConfigMessage(getLogger().IGNORE_ASSOCIATION_OVERRIDE, name, getAttributeName(), getClassAccessor().getJavaClassName(), getJavaClassName());
                associationOverridesMap.put(name, getDescriptor().getAssociationOverrideFor(dotNotationName));
            } else {
                associationOverridesMap.put(name, associationOverride);
            }
        }
        
        // Now add every other descriptor association override that didn't 
        // override a mapping level one (if we are processing a mapping from
        // a mapped superclass level). We'll check the attribute names match
        // and rip off the extra qualifying when adding it to the override map.
        if (getClassAccessor().isMappedSuperclass()) {
            for (AssociationOverrideMetadata associationOverride : getDescriptor().getAssociationOverrides()) {
                String name = associationOverride.getName();
                String attributeName = name;
                String overrideName = name;
                int indexOfFirstDot = name.indexOf(".");                

                if (indexOfFirstDot > -1) {
                    attributeName = name.substring(0, indexOfFirstDot);
                    overrideName = name.substring(indexOfFirstDot + 1);
                }
                 
                if (attributeName.equals(getAttributeName()) && ! associationOverridesMap.containsKey(attributeName)) {
                    associationOverridesMap.put(overrideName, associationOverride);
                }
            }
        }
        
        return associationOverridesMap;
    }
    
    /**
     * INTERNAL:
     * Return the attribute name for this accessor. This is typically the
     * attribute name on the accessible object (i.e., field or property name),
     * however, if access-methods have been specified, use the name attribute
     * that was specified in XML. (e.g. basic name="sin") and not the property
     * name of the get method from the access-methods specification.
     */
    @Override
    public String getAttributeName() {
        if (hasAccessMethods()) {
            return getName();
        } else {
            return getAccessibleObject().getAttributeName();
        }
    }

    /**
     * INTERNAL:
     * Return the attribute override for this accessor.
     */
    protected AttributeOverrideMetadata getAttributeOverride(String loggingCtx) {
        if (loggingCtx.equals(MetadataLogger.MAP_KEY_COLUMN)) {
            return getDescriptor().getAttributeOverrideFor(KEY_DOT_NOTATION + getAttributeName());
        } else if (loggingCtx.equals(MetadataLogger.VALUE_COLUMN)) {
            if (getDescriptor().hasAttributeOverrideFor(VALUE_DOT_NOTATION + getAttributeName())) {
                return getDescriptor().getAttributeOverrideFor(VALUE_DOT_NOTATION + getAttributeName());
            }
        }
        return getDescriptor().getAttributeOverrideFor(getAttributeName());
    }

    /**
     * INTERNAL:
     * Process the list of attribute overrides into a map, merging and 
     * overriding any attribute overrides where necessary with descriptor
     * level attribute overrides.
     */
    protected Map<String, AttributeOverrideMetadata> getAttributeOverrides(List<AttributeOverrideMetadata> attributeOverrides) {
        // TODO: Be nice to look for duplicates within the same list.
        HashMap<String, AttributeOverrideMetadata> attributeOverridesMap = new HashMap<String, AttributeOverrideMetadata>();
        
        for (AttributeOverrideMetadata attributeOverride : attributeOverrides) {
            String name = attributeOverride.getName();
            
            // An attribute override from a sub-entity class will name its
            // attribute override slightly different in that it will have one
            // extra dot notation at the front. E.G. A mapped superclass that
            // defines an embedded attribute named 'record' can define attribute
            // overrides directly on the mapping, that is, 'date'. Whereas
            // from an entity class to override 'date' on 'record', the
            // attribute name will be 'record.date'
            String dotNotationName = getAttributeName() + "." + name;
            if (getClassAccessor().isMappedSuperclass() && getDescriptor().hasAttributeOverrideFor(dotNotationName)) {
                getLogger().logConfigMessage(getLogger().IGNORE_ATTRIBUTE_OVERRIDE, name, getAttributeName(), getClassAccessor().getJavaClassName(), getJavaClassName());
                attributeOverridesMap.put(name, getDescriptor().getAttributeOverrideFor(dotNotationName));
            } else {
                attributeOverridesMap.put(name, attributeOverride);
            }
        }
        
        // Now add every other descriptor association override that didn't 
        // override a mapping level one (if we are processing a mapping from
        // a mapped superclass level). We'll check the attribute names match
        // and rip off the extra qualifying when adding it to the override map.
        if (getClassAccessor().isMappedSuperclass()) {
            for (AttributeOverrideMetadata attributeOverride : getDescriptor().getAttributeOverrides()) {
                String name = attributeOverride.getName();
                String attributeName = name;
                String overrideName = name;
                int indexOfFirstDot = name.indexOf(".");                

                if (indexOfFirstDot > -1) {
                    attributeName = name.substring(0, indexOfFirstDot);
                    overrideName = name.substring(indexOfFirstDot + 1);
                }
                 
                if (attributeName.equals(getAttributeName()) && ! attributeOverridesMap.containsKey(attributeName)) {
                    attributeOverridesMap.put(overrideName, attributeOverride);
                }
            }
        }
        
        return attributeOverridesMap;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping. Those accessors that do not require a separate 
     * attribute-type specification for VIRTUAL accessors should override this 
     * method. For example, one-to-one and many-to-one will its target-entity. 
     * variable-one-to-one will use its target-interface.
     */
    public String getAttributeType() {
        return m_attributeType;
    }
    
    /**
     * INTERNAL:
     * Returns the class accessor on which this mapping was defined.
     */
    public ClassAccessor getClassAccessor(){
        return m_classAccessor;
    }
    
    /**
     * INTERNAL:
     * Subclasses should override this method to return the appropriate
     * column for their mapping.
     * @see BasicAccessor
     * @see BasicCollectionAccessor
     * @see BasicMapAccessor
     * @see ElementCollectionAccessor
     * @see CollectionAccessor
     */
    protected ColumnMetadata getColumn(String loggingCtx) {
        return m_field == null ? new ColumnMetadata(this) : m_field;
    }

    /**
     * INTERNAL:
     * Given the potential converts return them for processing unless there
     * are overrides available from the descriptor.
     */
    protected List<ConvertMetadata> getConverts(List<ConvertMetadata> potentialConverts) {
        if (getDescriptor().hasConverts(getAttributeName())) {
            return getDescriptor().getConverts(getAttributeName());
        } else {
            return potentialConverts;
        }
    }
    
    /**
     * INTERNAL:
     * Process column metadata details into a database field. This will set 
     * correct metadata and log defaulting messages to the user. It also looks 
     * for an attribute override.
     * 
     * This method will call getColumn() which assumes the subclasses will
     * return the appropriate ColumnMetadata to process based on the context
     * provided.
     * 
     * @See BasicCollectionAccessor and BasicMapAccessor.
     */
    protected DatabaseField getDatabaseField(DatabaseTable defaultTable, String loggingCtx) {
        // Check if we have an attribute override first, otherwise process for a column
        ColumnMetadata column  = hasAttributeOverride(loggingCtx) ? getAttributeOverride(loggingCtx).getColumn() : getColumn(loggingCtx);

        // Get the actual database field and apply any defaults.
        DatabaseField field = column.getDatabaseField();
           
        // Make sure there is a table name on the field.
        if (! field.hasTableName()) {
            field.setTable(defaultTable);
        }
        
        // We must check the flag before blindly setting it on the table since
        // global flag may be false where this fields table may explicitly use
        // delimiters.
        if (getProject().useDelimitedIdentifier()) {
            field.getTable().setUseDelimiters(true);
        }
        
        // Set the correct field name, defaulting and logging when necessary.
        String defaultName = getDefaultAttributeName();
           
        // If this is for a map key column, append a suffix.
        if (loggingCtx.equals(MetadataLogger.MAP_KEY_COLUMN)) {
            defaultName += DEFAULT_MAP_KEY_COLUMN_SUFFIX;
        }
        
        setFieldName(field, defaultName, loggingCtx);

        // Store all the fields for this descriptor. This will allow re-use
        // and easy lookup of referenced column names.
        getDescriptor().addField(field);
        
        return field;
    }
    
    /**
     * INTERNAL:
     */
    protected String getDefaultFetchType() {
        return JPA_FETCH_EAGER; 
    }
    
    /**
     * INTERNAL:
     * Return the default table to hold the foreign key of a MapKey when
     * and Entity is used as the MapKey
     */
    protected DatabaseTable getDefaultTableForEntityMapKey(){
        return getReferenceDescriptor().getPrimaryTable();
    }
    
    /**
     * INTERNAL:
     * Return the enumerated metadata for this accessor.
     * @see DirectAccessor
     * @see ElementCollectionAccessor
     * @see CollectionAccessor
     */
    public EnumeratedMetadata getEnumerated(boolean isForMapKey) {
        return null;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ColumnMetadata getField() {
        return m_field;
    }
    
    /**
     * INTERNAL:
     * Return the foreign key to use with this mapping accessor. This method 
     * will look for association overrides and use the foreign key from it 
     * instead (do as the association override says).
     */
    protected ForeignKeyMetadata getForeignKey(ForeignKeyMetadata potentialForeignKey, MetadataDescriptor descriptor) {
        if (getDescriptor().hasAssociationOverrideFor(getAttributeName())) {
            ForeignKeyMetadata foreignKey = getDescriptor().getAssociationOverrideFor(getAttributeName()).getForeignKey();
            
            // Have to init the foreign key with items that could potentially
            // be required during processing here.
            if (foreignKey != null) {
                foreignKey.setProject(descriptor.getProject());
            }
            
            return foreignKey;
        } else {
            return potentialForeignKey;
        }
    }
    
    /**
     * INTERNAL:
     * Returns the get method name of a method accessor. Note, this method
     * should not be called when processing field access.
     */
    public String getGetMethodName() {
        return hasAccessMethods() ? getAccessMethods().getGetMethodName() : getAccessibleObjectName(); 
    }
    
    /**
     * INTERNAL:
     * Return the join columns to use with this mapping accessor. This method 
     * will look for association overrides and use those instead if some are 
     * available. This method will validate the join columns and default
     * any where necessary.
     */
    protected List<JoinColumnMetadata> getJoinColumns(List<JoinColumnMetadata> potentialJoinColumns, MetadataDescriptor descriptor) {
        if (getDescriptor().hasAssociationOverrideFor(getAttributeName())) {
            return getJoinColumnsAndValidate(getDescriptor().getAssociationOverrideFor(getAttributeName()).getJoinColumns(), descriptor);
        } else {
            return getJoinColumnsAndValidate(potentialJoinColumns, descriptor);
        }
    }
    
    /**
     * INTERNAL:
     * This method will validate the join columns and default any where 
     * necessary.
     */    
    protected List<JoinColumnMetadata> getJoinColumnsAndValidate(List<JoinColumnMetadata> joinColumns, MetadataDescriptor referenceDescriptor) {
        if (joinColumns.isEmpty()) {
            if (referenceDescriptor.hasCompositePrimaryKey()) {
                // Add a default one for each part of the composite primary
                // key. Foreign and primary key to have the same name.
                for (DatabaseField primaryKeyField : referenceDescriptor.getPrimaryKeyFields()) {
                    // Multitenant primary key fields will be dealt with below so avoid adding here.
                    if (! primaryKeyField.isPrimaryKey()) {
                        JoinColumnMetadata joinColumn = new JoinColumnMetadata();
                        joinColumn.setReferencedColumnName(primaryKeyField.getName());
                        joinColumn.setName(primaryKeyField.getName());
                        joinColumn.setProject(referenceDescriptor.getProject());
                        joinColumns.add(joinColumn);
                    }
                }
            } else {
                // Add a default one for the single case, not setting any
                // foreign and primary key names. They will default based
                // on which accessor is using them.
                JoinColumnMetadata jcm = new JoinColumnMetadata();
                jcm.setProject(referenceDescriptor.getProject());
                joinColumns.add(jcm);
            }
        } else {
            // Need to update any join columns that use a foreign key name
            // for the primary key name. E.G. User specifies the renamed id
            // field name from a primary key join column as the primary key in
            // an inheritance subclass.
            for (JoinColumnMetadata joinColumn : joinColumns) {
                // Doing this could potentially change a value entered in XML.
                // However, in this case I think that is ok since in theory we 
                // are writing out the correct value that EclipseLink needs to 
                // form valid queries.
                String referencedColumnName = joinColumn.getReferencedColumnName();
                // The referenced column name in a variable one to one case is a 
                // query key name and not a column name so bypass any of this
                // code.
                if (referencedColumnName != null && !isVariableOneToOne()) {
                    DatabaseField referencedField = new DatabaseField();
                    setFieldName(referencedField, referencedColumnName);
                    joinColumn.setReferencedColumnName(referenceDescriptor.getPrimaryKeyJoinColumnAssociation(referencedField).getName());
                }
            }
        }
        
        // Multitenant entities. Go through and add any multitenant primary key 
        // fields that need to be added. The user may or may not specify them
        // in metadata. 
        if (referenceDescriptor.hasSingleTableMultitenant() && joinColumns.size() != referenceDescriptor.getPrimaryKeyFields().size()) {
            Map<String, List<DatabaseField>> referenceTenantFields = referenceDescriptor.getSingleTableMultitenantFields();
                
            // If we are multitenant then we can sync up on relationship fields
            // using the context property.
            Map<String, List<DatabaseField>> tenantFields = getDescriptor().hasSingleTableMultitenant() ? getDescriptor().getSingleTableMultitenantFields() : null;
                    
            for (String contextProperty : referenceTenantFields.keySet()) {
                List<DatabaseField> referenceFields = referenceTenantFields.get(contextProperty);

                for (DatabaseField referenceField : referenceFields) {
                    // Only if it is a primary key field, otherwise we don't
                    // care about it.
                    if (referenceField.isPrimaryKey()) {
                        JoinColumnMetadata jcm = new JoinColumnMetadata();
                        // This join column must be read only.
                        jcm.setInsertable(false);
                        jcm.setUpdatable(false);
                        
                        // If we have a related context property, look up a 
                        // field that matches from it.
                        if (tenantFields != null && tenantFields.containsKey(contextProperty)) {
                            // This is going to return a list just pick the first
                            // field (they populate the same value so doesn't really
                            // matter which one we pick.
                            jcm.setName(tenantFields.get(contextProperty).get(0).getName());
                        } else {
                            // We don't have a match, use the same name.
                            jcm.setName(referenceField.getName());
                        }
                            
                        jcm.setReferencedColumnName(referenceField.getName());
                        jcm.setProject(referenceDescriptor.getProject());
                        joinColumns.add(jcm);
                    }
                }
            } 
        }
        
        // Now run some validation.
        if (referenceDescriptor.hasCompositePrimaryKey()) {
            // The number of join columns should equal the number of primary key fields.
            if (joinColumns.size() != referenceDescriptor.getPrimaryKeyFields().size()) {
                throw ValidationException.incompleteJoinColumnsSpecified(getAnnotatedElement(), getJavaClass());
            }
            
            // All the primary and foreign key field names should be specified.
            for (JoinColumnMetadata joinColumn : joinColumns) {
                if (joinColumn.isPrimaryKeyFieldNotSpecified() || joinColumn.isForeignKeyFieldNotSpecified()) {
                    throw ValidationException.incompleteJoinColumnsSpecified(getAnnotatedElement(), getJavaClass());
                }
            }
        }
        
        return joinColumns;
    }
    
    /**
     * INTERNAL:
     * Return the lob metadata for this accessor.
     * @see DirectAccessor
     */
    public LobMetadata getLob(boolean isForMapKey) {
        return null;
    }
    
    /**
     * INTERNAL:
     * Return the mapping that this accessor is associated to.
     */
    public DatabaseMapping getMapping(){
        return (m_overrideMapping == null) ? m_mapping : m_overrideMapping;
    }
    
    /**
     * INTERNAL:
     * Return the owning descriptor of this accessor.
     */
    public MetadataDescriptor getOwningDescriptor() {
        return getClassAccessor().getOwningDescriptor();
    }
    
    /**
     * INTERNAL:
     * Return the owning descriptors of this accessor. In most cases this is
     * a single descriptor. Multiples can only exist when dealing with 
     * accessors for an embeddable that is shared.
     */
    public List<MetadataDescriptor> getOwningDescriptors() {
        return getClassAccessor().getOwningDescriptors();
    }
    
    /**
     * INTERNAL:
     * Return the map key if this mapping accessor employs one. Those accessors
     * that support it should override this method.
     * @see CollectionAccessor
     * @see ElementCollectionAccessor
     */
    public MapKeyMetadata getMapKey() {
        return null;
    }
    
    /**
     * INTERNAL:
     * Given the potential converts return them for processing unless there
     * are overrides available from the descriptor.
     */
    protected List<ConvertMetadata> getMapKeyConverts(List<ConvertMetadata> potentialMapKeyConverts) {
        if (getDescriptor().hasMapKeyConverts(getAttributeName())) {
            return getDescriptor().getMapKeyConverts(getAttributeName());
        } else {
            return potentialMapKeyConverts;
        }
    }
    
    /**
     * INTERNAL:
     * Return the map key reference class for this accessor if applicable. It 
     * will try to extract a reference class from a generic specification.
     * Parameterized generic keys on a MappedSuperclass will return void.class.  
     * If no generics are used, then it will return void.class. This avoids NPE's 
     * when processing JPA converters that can default (Enumerated and Temporal) 
     * based on the reference class.
     */
    public MetadataClass getMapKeyReferenceClass() {
        // First check if we are a mapped key map accessor and return its map
        // key class if specified. Otherwise continue on to extract it from
        // a generic specification. We do this to avoid going to the class
        // with is needed for dynamic persistence.
        if (isMappedKeyMapAccessor()) {
            MetadataClass mapKeyClass = ((MappedKeyMapAccessor) this).getMapKeyClass();
            if (mapKeyClass != null && ! mapKeyClass.isVoid()) {
                return mapKeyClass;
            }
        }
        
        if (isMapAccessor()) {
            MetadataClass referenceClass = getAccessibleObject().getMapKeyClass(getDescriptor());
        
            if (referenceClass == null) {
                throw ValidationException.unableToDetermineMapKeyClass(getAttributeName(), getJavaClass());
            }        
        
            // 266912:  Use of parameterized generic types like Map<X,Y> 
            // inherits from class<T> in a MappedSuperclass field will cause 
            // referencing issues - as in we are unable to determine the correct 
            // type for T. A workaround for this is to detect when we are in 
            // this state and return a standard top level class. An invalid 
            // class will be of the form MetadataClass.m_name="T" 
            if (getDescriptor().isMappedSuperclass()) {
                // Determine whether we are directly referencing a class or 
                // using a parameterized generic reference by trying to load the 
                // class and catching any validationException. If we do not get 
                // an exception on getClass then the referenceClass.m_name is 
                // valid and should be directly returned
                try {
                    MetadataHelper.getClassForName(referenceClass.getName(), getMetadataFactory().getLoader());
                } catch (ValidationException exception) {
                    // Default to Void for parameterized types
                    // Ideally we would need a MetadataClass.isParameterized() to inform us instead.
                    return getMetadataClass(Void.class);
                }                          
            }
            
            return referenceClass;
        } else {
            return getMetadataClass(void.class);
        }
    }
    
    /**
     * INTERNAL:
     * Return the map key reference class name
     */
    public String getMapKeyReferenceClassName() {
        return getMapKeyReferenceClass().getName();
    }
    
    /**
     * INTERNAL:
     * Return the map key reference class for this accessor if applicable. It 
     * will try to extract a reference class from a generic specification.  
     * If no generics are used, then it will return void.class. This avoids NPE's 
     * when processing JPA converters that can default (Enumerated and Temporal) 
     * based on the reference class.
     * 
     * Future: this method is where we would provide a more explicit reference
     * class to support an auto-apply jpa converter. Per the spec auto-apply
     * converters are applied against basics only.
     */
    public MetadataClass getMapKeyReferenceClassWithGenerics() {
        return getMapKeyReferenceClass();
    }
    
    /**
     * INTERNAL:
     * Return the raw class for this accessor. 
     * E.g. For an accessor with a type of java.util.Collection<Employee>, this 
     * method will return java.util.Collection. To check for the attribute
     * type we must go through the method calls since some accessors define
     * the attribute type through a target entity specification. Do not access
     * the m_attributeType variable directly in this method.
     */
    public MetadataClass getRawClass() {
        if (hasAttributeType()) {
            // If the class doesn't exist the factory we'll just return a
            // generic MetadataClass
            return getMetadataClass(getAttributeType());
        } else {
            return getAccessibleObject().getRawClass(getDescriptor());
        }
    }
    
    /**
     * INTERNAL:
     * Return the raw class with any generic specifications for this accessor. 
     * E.g. For an accessor with a type of java.util.Collection<Employee>, this 
     * method will return java.util.CollectionEmployee. To check for the 
     * attribute type we must go through the method calls since some accessors 
     * define the attribute type through a target entity specification. Do not 
     * access the m_attributeType variable directly in this method.
     */
    public MetadataClass getRawClassWithGenerics() {
        if (hasAttributeType()) {
            // If the class doesn't exist the factory we'll just return a
            // generic MetadataClass
            return getMetadataClass(getAttributeType());
        } else {
            return getAccessibleObject().getRawClassWithGenerics(getDescriptor());
        }
    }
    
    /**
     * INTERNAL:
     * Return the mapping accessors associated with the reference descriptor.
     */
    public Collection<MappingAccessor> getReferenceAccessors() {
        return getReferenceDescriptor().getMappingAccessors();
    }
    
    /**
     * INTERNAL: 
     * Return the reference class for this accessor. By default the reference
     * class is the raw class. Some accessors may need to override this
     * method to drill down further. That is, try to extract a reference class
     * from generics.
     */
    public MetadataClass getReferenceClass() {
        return getRawClass();
    }
    
    /**
     * INTERNAL: 
     * Return the reference class for this accessor. By default the reference
     * class is the raw class. Some accessors may need to override this
     * method to drill down further. That is, try to extract a reference class
     * from generics.
     */
    public MetadataClass getReferenceClassWithGenerics() {
        return getRawClassWithGenerics();
    }
    
    /**
     * INTERNAL:
     * Attempts to return a reference class from a generic specification. Note,
     * this method may return null.
     */
    public MetadataClass getReferenceClassFromGeneric() {
        return getAccessibleObject().getReferenceClassFromGeneric(getDescriptor());
    }

    /**
     * INTERNAL:
     * Return the reference class name for this accessor.
     */
    public String getReferenceClassName() {
        return getReferenceClass().getName();
    }
    
    /**
     * INTERNAL:
     * Return the reference descriptors table. By default it is the primary
     * key table off the reference descriptor. Subclasses that care to return
     * a different class should override this method.
     * @see DirectCollectionAccessor
     * @see ManyToManyAccessor
     */
    protected DatabaseTable getReferenceDatabaseTable() {
        return getReferenceDescriptor().getPrimaryKeyTable();
    }
    
    /**
     * INTERNAL:
     * Return the reference metadata descriptor for this accessor.
     */
    public MetadataDescriptor getReferenceDescriptor() {
        ClassAccessor accessor = getProject().getAccessor(getReferenceClassName());
        
        if (accessor == null) {
            throw ValidationException.classNotListedInPersistenceUnit(getReferenceClassName());
        }
        
        return accessor.getDescriptor();
    }

    /**
     * INTERNAL:
     * Returns the set method name of a method accessor. Note, this method
     * should not be called when processing field access.
     */
    public String getSetMethodName() {
        return hasAccessMethods() ? getAccessMethods().getSetMethodName() : ((MetadataMethod) getAccessibleObject()).getSetMethodName();
    }
    
    /**
     * INTERNAL:
     * Return the temporal metadata for this accessor.
     * @see DirectAccessor
     * @see CollectionAccessor
     */
    public TemporalMetadata getTemporal(boolean isForMapKey) {
        return null;
    }
    
    /**
     * INTERNAL: Set the temporal metadata for this accessor.
     * 
     * @see DirectAccessor
     * @see CollectionAccessor
     */
    protected void setTemporal(TemporalMetadata metadata, boolean isForMapKey) {

    }
    
    /**
     * INTERNAL:
     * Return true if we have an attribute override for this accessor.
     */
    protected boolean hasAttributeOverride(String loggingCtx) {
        if (loggingCtx.equals(MetadataLogger.MAP_KEY_COLUMN)) {
            return getDescriptor().hasAttributeOverrideFor(KEY_DOT_NOTATION + getAttributeName());
        } else if (loggingCtx.equals(MetadataLogger.VALUE_COLUMN)) {
            if (getDescriptor().hasAttributeOverrideFor(VALUE_DOT_NOTATION + getAttributeName())) {
                return true;
            }
        } 
            
        return getDescriptor().hasAttributeOverrideFor(getAttributeName());
    }
    
    /**
     * INTERNAL:
     * Those accessors that do not require a separate attribute-type 
     * specification for VIRTUAL accessors should override this method. For
     * example, one-to-one and many-to-one will its target-entity. 
     * variable-one-to-one will use its target-interface. 
     */
    public boolean hasAttributeType() {
        return m_attributeType != null;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has temporal metadata.
     * @see DirectAccessor
     * @see ElementCollectionAccessor
     * @see CollectionAccessor
     */
    protected boolean hasEnumerated(boolean isForMapKey) {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has lob metadata.
     * @see DirectAccessor
     * @see ElementCollectionAccessor
     * @see CollectionAccessor
     */
    protected boolean hasLob(boolean isForMapKey) {
        return false;
    }
    
    /**
     * INTERNAL:
     * Method should be overridden by those accessors that accept and use a map 
     * key.
     * @see CollectionAccessor
     * @see ElementCollectionAccessor
     * @see BasicMapAccessor
     */
    public boolean hasMapKey() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Method to check if this accessor has a ReturnInsert annotation.
     */
    protected boolean hasReturnInsert() {
        return isAnnotationPresent(ReturnInsert.class);
    }
    
    /**
     * INTERNAL:
     * Method to check if this accessor has a ReturnUpdate annotation.
     */
    protected boolean hasReturnUpdate() {
        return isAnnotationPresent(ReturnUpdate.class);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has temporal metadata.
     * @see DirectAccessor
     * @see ElementCollectionAccessor
     * @see CollectionAccessor
     */
    public boolean hasTemporal(boolean isForMapKey) {
        return false;
    }
    
    /**
     * INTERNAL: 
     * Init an xml mapping accessor with its necessary components. 
     */
    public void initXMLMappingAccessor(ClassAccessor classAccessor) {
        m_classAccessor = classAccessor;
        setEntityMappings(classAccessor.getEntityMappings());
        initXMLAccessor(classAccessor.getDescriptor(), classAccessor.getProject());   
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        initXMLObject(m_field, accessibleObject);
    }
    
    /** 
     * INTERNAL:
     * Indicates whether the specified annotation is present on the annotated
     * element for this accessor. Method checks against the metadata complete
     * flag.
     */
    @Override
    public boolean isAnnotationPresent(String annotation) {
        return getAccessibleObject().isAnnotationPresent(annotation, getClassAccessor());
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a basic mapping.
     */
    public boolean isBasic() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a basic collection mapping.
     */
    public boolean isBasicCollection() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a basic map mapping.
     */
    public boolean isBasicMap() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor is a derived id class accessor.
     */
    public boolean isDerivedIdClass(){
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a direct collection mapping, 
     * which include basic collection, basic map and element collection 
     * accessors.
     */
    public boolean isDirectCollection() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents an element collection that
     * contains embeddable objects.
     */
    public boolean isDirectEmbeddableCollection() {
        return false;
    }
    
    /** 
     * INTERNAL:
     * Return true if this accessor represents a collection accessor.
     */
    public boolean isCollectionAccessor() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents an aggregate mapping.
     */
    public boolean isEmbedded() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents an aggregate id mapping.
     */
    public boolean isEmbeddedId() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this represents an enum type mapping. Will return true
     * if the accessor's reference class is an enum or if enumerated metadata
     * exists.
     */
    protected boolean isEnumerated(MetadataClass referenceClass, boolean isForMapKey) {
        return hasEnumerated(isForMapKey) || EnumeratedMetadata.isValidEnumeratedType(referenceClass);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor is part of the id.
     */
    public boolean isId(){
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a BLOB/CLOB mapping.
     */
    protected boolean isLob(MetadataClass referenceClass, boolean isForMapKey) {
        return hasLob(isForMapKey);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a m-m relationship.
     */
    public boolean isManyToMany() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a m-1 relationship.
     */
    public boolean isManyToOne() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor uses a Map.
     */
    public boolean isMapAccessor() {
        return getAccessibleObject().isSupportedMapClass(getRawClass());
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor is a mapped key map accessor. It is a
     * map key accessor for two reasons, it's a map and it does not have 
     * a map key specified. NOTE: we can't check for a map key class since
     * one may not have been explicitly specified. In this case, a generic 
     * value must be set and we check for one when adding accessors (and in 
     * turn set the map key class at that point)
     */
    public boolean isMappedKeyMapAccessor() {
        return MappedKeyMapAccessor.class.isAssignableFrom(getClass()) && isMapAccessor() && ! hasMapKey();
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor is a multitenant id mapping.
     */
    public boolean isMultitenantId() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a 1-m relationship.
     */
    public boolean isOneToMany() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a 1-1 relationship.
     */
    public boolean isOneToOne() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Returns true is the given class is primitive wrapper type.
     */
    protected boolean isPrimitiveWrapperClass(MetadataClass cls) {
        return cls.extendsClass(Number.class) ||
            cls.equals(Boolean.class) ||
            cls.equals(Character.class) ||
            cls.equals(String.class) ||
            cls.extendsClass(java.math.BigInteger.class) ||
            cls.extendsClass(java.math.BigDecimal.class) ||
            cls.extendsClass(java.util.Date.class) ||
            cls.extendsClass(java.util.Calendar.class);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has been processed. If there is a mapping
     * set, we have processed this accessor.
     */
    @Override
    public boolean isProcessed() {
        return m_mapping != null;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor method represents a relationship.
     */
    public boolean isRelationship() {
        return isManyToOne() || isManyToMany() || isOneToMany() || isOneToOne() || isVariableOneToOne();
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a serialized mapping.
     */
    public boolean isSerialized(MetadataClass referenceClass, boolean isForMapKey) {
        return isValidSerializedType(referenceClass);
    }
    
    /**
     * INTERNAL:
     * Return true if this represents a temporal type mapping.
     */
    protected boolean isTemporal(MetadataClass referenceClass, boolean isForMapKey) {
        return hasTemporal(isForMapKey) || TemporalMetadata.isValidTemporalType(referenceClass);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a transient mapping.
     */
    public boolean isTransient() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Returns true if the given class is valid for SerializedObjectMapping.
     */
    protected boolean isValidSerializedType(MetadataClass cls) {
        if (cls.isPrimitive()) {
            return false;
        }
        
        if (isPrimitiveWrapperClass(cls)) {    
            return false;
        }   
        
        if (LobMetadata.isValidLobType(cls)) {
            return false;
        }
        
        if (TemporalMetadata.isValidTemporalType(cls)) {
            return false;
        }
     
        return true;   
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a variable one to one mapping.
     */
    public boolean isVariableOneToOne() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Process an association override for either an embedded object mapping, 
     * or a map mapping (element-collection, 1-M and M-M) containing an
     * embeddable object as the value or key.
     * This method should be implemented in those accessors that support 
     * association overrides. An exception is thrown otherwise the association
     * is called against an unsupported accessor/relationship. 
     */
    protected void processAssociationOverride(AssociationOverrideMetadata associationOverride, EmbeddableMapping embeddableMapping, MetadataDescriptor owningDescriptor) {
        throw ValidationException.invalidEmbeddableAttributeForAssociationOverride(getJavaClass(), getAttributeName(), associationOverride.getName(), associationOverride.getLocation()); 
    }
    
    /**
     * INTERNAL:
     * Process the association overrides for the given embeddable mapping which
     * is either an embedded or element collection mapping. Association 
     * overrides are used to specify different keys to a shared mapping.
     */
    protected void processAssociationOverrides(List<AssociationOverrideMetadata> associationOverrides, EmbeddableMapping embeddableMapping, MetadataDescriptor embeddableDescriptor) {
        // Get the processible map of association overrides. This will take dot 
        // notation overrides into consideration (from a sub-entity to a mapped 
        // superclass accessor) and merge the lists. Once the map is returned, 
        // use the map keys as the attribute name and not the name from the 
        // individual association override since they could still contain dot 
        // notation names meaning you will not find their respective mapping 
        // accessor on the embeddable descriptor.
        Map<String, AssociationOverrideMetadata> mergedAssociationOverrides = getAssociationOverrides(associationOverrides);
        
        for (String attributeName : mergedAssociationOverrides.keySet()) {
            AssociationOverrideMetadata associationOverride = mergedAssociationOverrides.get(attributeName);
            // The getAccessorFor call will take care of any sub dot notation 
            // attribute names when looking for the mapping. It will traverse 
            // the embeddable chain. 
            MappingAccessor mappingAccessor = embeddableDescriptor.getMappingAccessor(attributeName);
            
            if (mappingAccessor == null) {
                throw ValidationException.embeddableAssociationOverrideNotFound(embeddableDescriptor.getJavaClass(), attributeName, getJavaClass(), getAttributeName());
            } else {
                mappingAccessor.processAssociationOverride(associationOverride, embeddableMapping, getOwningDescriptor());
            }  
        }
    }
    
    /**
     * INTERNAL:
     * Process the attribute overrides for the given embedded mapping. Attribute 
     * overrides are used to apply the correct field name translations of direct 
     * fields. Note an embedded object mapping may be supported as the map key
     * to an element-collection, 1-M and M-M mapping.
     */
    protected void processAttributeOverrides(List<AttributeOverrideMetadata> attributeOverrides, AggregateObjectMapping aggregateObjectMapping, MetadataDescriptor embeddableDescriptor) {
        // Get the processible map of attribute overrides. This will take dot 
        // notation overrides into consideration (from a sub-entity to a mapped 
        // superclass accessor) and merge the lists. Once the map is returned, 
        // use the map keys as the attribute name and not the name from the 
        // individual attribute override since they could still contain dot 
        // notation names meaning you will not find their respective mapping 
        // accessor on the embeddable descriptor.
        Map<String, AttributeOverrideMetadata> mergedAttributeOverrides = getAttributeOverrides(attributeOverrides);
        
        for (String attributeName : mergedAttributeOverrides.keySet()) {
            AttributeOverrideMetadata attributeOverride = mergedAttributeOverrides.get(attributeName);
            // The getMappingForAttributeName call will take care of any sub dot 
            // notation attribute names when looking for the mapping. It will
            // traverse the embeddable chain. 
            MappingAccessor mappingAccessor = embeddableDescriptor.getMappingAccessor(attributeName);

            String colName = attributeOverride.getColumn().getName();
            if (colName == null || colName.isEmpty()) {
                String prevName = mappingAccessor.getDefaultAttributeName();
                attributeOverride.getColumn().setName(prevName);
            }
            
            if (mappingAccessor == null) {
                throw ValidationException.embeddableAttributeOverrideNotFound(embeddableDescriptor.getJavaClass(), attributeName, getJavaClass(), getAttributeName());
            } else if (! mappingAccessor.isBasic()) {
                throw ValidationException.invalidEmbeddableAttributeForAttributeOverride(embeddableDescriptor.getJavaClass(), attributeName, getJavaClass(), getAttributeName());
            } else {
                // Get databasefield() takes care of any delimited/uppercasing on the column.
                addFieldNameTranslation(aggregateObjectMapping, attributeName, attributeOverride.getColumn().getDatabaseField(), mappingAccessor);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the map metadata if this is a valid map accessor. Will return 
     * the map key method name that should be use, null otherwise.
     * @see CollectionAccessor
     * @see ElementCollectionAccessor
     */
    protected void processContainerPolicyAndIndirection(ContainerMapping mapping) {
        if (isMappedKeyMapAccessor()) {
            // If we are a map key map accessor then the following is true:
            // 1 - we implement the mapped key map accessor interface
            // 2 - we are a map accessor
            // 3 - there is no map key metadata specified
            processMapKeyClass(mapping, (MappedKeyMapAccessor) this);
        } else if (isMapAccessor()) {
            // If we are not a mapped key map accessor, but a map accessor,
            // we need a map key metadata object to process. Default one if
            // one is not provided.
            MapKeyMetadata mapKey = getMapKey();
            if (mapKey == null) {
                setIndirectionPolicy(mapping, new MapKeyMetadata().process(mapping, this), usesIndirection());
            } else {
                setIndirectionPolicy(mapping, mapKey.process(mapping, this), usesIndirection());
            }
        } else {
            // Set the indirection policy on the mapping.
            setIndirectionPolicy(mapping, null, usesIndirection());
        }
    } 
    
    /**
     * INTERNAL:
     * Process a Convert annotation or convert element to apply to specified 
     * EclipseLink converter (Converter, TypeConverter, ObjectTypeConverter) 
     * to the given mapping.
     */
    protected void processConvert(DatabaseMapping mapping, String converterName, MetadataClass referenceClass, boolean isForMapKey, boolean hasConverts) {
        if (converterName.equals(Convert.SERIALIZED)) {
            processSerialized(mapping, referenceClass, isForMapKey);
        } else if (converterName.equals(Convert.CLASS_INSTANCE)){
            new ClassInstanceMetadata().process(mapping, this, referenceClass, isForMapKey);
        } else if (converterName.equals(Convert.XML)){
            new XMLMetadata().process(mapping, this, referenceClass, isForMapKey);
        } else if (converterName.equals(Convert.JSON)){
            new JSONMetadata().process(mapping, this, referenceClass, isForMapKey);
        } else if (converterName.equals(Convert.KRYO)){
            new KryoMetadata().process(mapping, this, referenceClass, isForMapKey);
        } else {
            AbstractConverterMetadata converter = getProject().getConverter(converterName);
                
            if (converter == null) {
                throw ValidationException.converterNotFound(getJavaClass(), converterName, getAnnotatedElement());
            } else {
                // Process the converter for this mapping.
                converter.process(mapping, this, referenceClass, isForMapKey);
            }
        }
        
        // This was an old requirement from PM, that if an EclipseLink
        // convert was specified with a JPA converter that we log a warning
        // message that we're overriding the JPA converter.
        
        if (hasEnumerated(isForMapKey)) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_ENUMERATED, getJavaClass(), getAnnotatedElement());
        }
        
        if (hasLob(isForMapKey)) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_LOB, getJavaClass(), getAnnotatedElement());
        }
        
        if (hasTemporal(isForMapKey)) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_TEMPORAL, getJavaClass(), getAnnotatedElement());
        }
        
        if (isValidSerializedType(referenceClass)) {
            getLogger().logConfigMessage(MetadataLogger.IGNORE_SERIALIZED, getJavaClass(), getAnnotatedElement());
        }
        
        // Check for the new JPA 2.1 convert metadata.
        if (hasConverts) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_CONVERTS, getJavaClass(), getAnnotatedElement());
        }
        
        if (getProject().hasAutoApplyConverter(referenceClass)) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_AUTO_APPLY_CONVERTER, getJavaClass(), getAnnotatedElement());
        }
    }
    
    /**
     * INTERNAL:
     * Process the JPA defined convert(s)
     */
    protected void processConverts(List<ConvertMetadata> converts, DatabaseMapping mapping, MetadataClass referenceClass, boolean isForMapKey) {
        if (converts != null) {
            for (ConvertMetadata convert : converts) {
                convert.process(mapping, referenceClass, getClassAccessor(), isForMapKey);
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected AbstractDirectMapping processDirectMapKeyClass(MappedKeyMapAccessor mappedKeyMapAccessor) {
        AbstractDirectMapping keyMapping = new DirectToFieldMapping();

        // Get the map key field, defaulting and looking for attribute 
        // overrides. Set the field before applying a converter.
        DatabaseField mapKeyField = getDatabaseField(getReferenceDatabaseTable(), MetadataLogger.MAP_KEY_COLUMN);
        keyMapping.setField(mapKeyField);
        keyMapping.setIsReadOnly(mapKeyField.isReadOnly());
        keyMapping.setAttributeClassificationName(mappedKeyMapAccessor.getMapKeyClass().getName());
        keyMapping.setDescriptor(getDescriptor().getClassDescriptor());
        
        // Process a convert key or jpa converter for the map key if specified.
        processMappingKeyConverter(keyMapping, mappedKeyMapAccessor.getMapKeyConvert(), mappedKeyMapAccessor.getMapKeyConverts(), mappedKeyMapAccessor.getMapKeyClass(), mappedKeyMapAccessor.getMapKeyClassWithGenerics());
        
        return keyMapping;
    }
    
    /**
     * INTERNAL:
     */
    protected AggregateObjectMapping processEmbeddableMapKeyClass(MappedKeyMapAccessor mappedKeyMapAccessor) {
        AggregateObjectMapping keyMapping = new AggregateObjectMapping();
        MetadataClass mapKeyClass = mappedKeyMapAccessor.getMapKeyClass();
        keyMapping.setReferenceClassName(mapKeyClass.getName());
        
        // The embeddable accessor must be processed by now. If it is not then
        // we are in trouble since by the time we get here, we are too late in
        // the cycle to process embeddable classes and their accessors. See
        // MetadataProject processStage3(), processEmbeddableMappingAccessors. 
        // At this stage all class accessors (embeddable, entity and mapped 
        // superclass) have to have been processed to gather all their 
        // relational and embedded mappings. 
        EmbeddableAccessor mapKeyAccessor = getProject().getEmbeddableAccessor(mapKeyClass);
        
        // Ensure the reference descriptor is marked as an embeddable collection.
        mapKeyAccessor.getDescriptor().setIsEmbeddable();
        
        // Process the attribute overrides for this may key embeddable.
        processAttributeOverrides(mappedKeyMapAccessor.getMapKeyAttributeOverrides(), keyMapping, mapKeyAccessor.getDescriptor());
        
        // Process the association overrides for this may key embeddable.
        processAssociationOverrides(mappedKeyMapAccessor.getMapKeyAssociationOverrides(), keyMapping, mapKeyAccessor.getDescriptor());
        
        // Process a convert key or jpa converter for the map key if specified.
        processConverts(getMapKeyConverts(mappedKeyMapAccessor.getMapKeyConverts()), keyMapping, mappedKeyMapAccessor.getMapKeyClass(), true);
        
        keyMapping.setDescriptor(getDescriptor().getClassDescriptor());
        
        return keyMapping;
    }
    
    /**
     * INTERNAL:
     * Process the map key to be an entity class.
     */
    protected OneToOneMapping processEntityMapKeyClass(MappedKeyMapAccessor mappedKeyMapAccessor) {
        String mapKeyClassName = mappedKeyMapAccessor.getMapKeyClass().getName();
        
        // Create the one to one map key mapping.
        OneToOneMapping keyMapping = new OneToOneMapping();
        keyMapping.setReferenceClassName(mapKeyClassName);
        keyMapping.dontUseIndirection();
        keyMapping.setDescriptor(getDescriptor().getClassDescriptor());
        
        // Process the map key join columns.
        EntityAccessor mapKeyAccessor = getProject().getEntityAccessor(mapKeyClassName);
        MetadataDescriptor mapKeyClassDescriptor = mapKeyAccessor.getDescriptor();
        
        // If the fk field (name) is not specified, it defaults to the 
        // concatenation of the following: the name of the referencing 
        // relationship property or field of the referencing entity or 
        // embeddable; "_"; "KEY"
        String defaultFKFieldName = getAttributeName() + DEFAULT_MAP_KEY_COLUMN_SUFFIX;

        // Get the join columns (directly or through an association override), 
        // init them and validate.
        List<JoinColumnMetadata> joinColumns = getJoinColumns(mappedKeyMapAccessor.getMapKeyJoinColumns(), mapKeyClassDescriptor);
        
        // Get the foreign key (directly or through an association override) and
        // make sure it is initialized for processing.
        ForeignKeyMetadata foreignKey = getForeignKey(mappedKeyMapAccessor.getMapKeyForeignKey(), mapKeyClassDescriptor);
        
        // Now process the foreign key relationship metadata.
        processForeignKeyRelationship(keyMapping, joinColumns, foreignKey, mapKeyClassDescriptor, defaultFKFieldName, getDefaultTableForEntityMapKey());

        return keyMapping;
    }
    
    /**
     * INTERNAL:
     * Process an Enumerated setting. The method may still be called if no 
     * Enumerated metadata has been specified but the accessor's reference 
     * class is a valid enumerated type.
     */
    protected void processEnumerated(EnumeratedMetadata enumerated, DatabaseMapping mapping, MetadataClass referenceClass, boolean isForMapKey) {
        if (enumerated == null) {
            enumerated = new EnumeratedMetadata(this);
        }
        
        enumerated.process(mapping, this, referenceClass, isForMapKey);
    }
    
    /**
     * INTERNAL:
     * Process the indirection (aka fetch type)
     */
    protected void processIndirection(ForeignReferenceMapping mapping) {
        boolean usesIndirection = usesIndirection();
        
        // Lazy is not disabled until descriptor initialization (OneToOneMapping preInitialize),
        // as it cannot be known if weaving occurred until then.
        String actualAttributeType = getAttributeType();
        if (getAccessibleObject() != null){
            actualAttributeType = getAccessibleObject().getType();
        }
        
        if (usesIndirection && usesPropertyAccess()) {
            mapping.setIndirectionPolicy(new WeavedObjectBasicIndirectionPolicy(getGetMethodName(), getSetMethodName(), actualAttributeType, true));
        } else if (usesIndirection && usesFieldAccess()) {
            mapping.setIndirectionPolicy(new WeavedObjectBasicIndirectionPolicy(Helper.getWeavedGetMethodName(mapping.getAttributeName()), Helper.getWeavedSetMethodName(mapping.getAttributeName()), actualAttributeType, false));
        } else {
            mapping.setUsesIndirection(usesIndirection);
        }
    }
    
    /**
     * INTERNAL:
     * Return the mapping join fetch type.
     */
    protected void processJoinFetch(String joinFetch, ForeignReferenceMapping mapping) {
        if (joinFetch == null) {
            mapping.setJoinFetch(ForeignReferenceMapping.NONE);
        } else if (joinFetch.equals(JoinFetchType.INNER.name())) {
            mapping.setJoinFetch(ForeignReferenceMapping.INNER_JOIN);
        } else {
            mapping.setJoinFetch(ForeignReferenceMapping.OUTER_JOIN);
        }
    }
    
    /**
     * INTERNAL:
     * Process a lob specification. The lob must be specified to process and 
     * create a lob type mapping.
     */
    protected void processLob(LobMetadata lob, DatabaseMapping mapping, MetadataClass referenceClass, boolean isForMapKey) {
        lob.process(mapping, this, referenceClass, isForMapKey);
    }
    
    /**
     * INTERNAL:
     * Process a map key class for the given map key map accessor.
     */
    protected void processMapKeyClass(ContainerMapping mapping, MappedKeyMapAccessor mappedKeyMapAccessor) {
        MapKeyMapping keyMapping;
        MetadataClass mapKeyClass = mappedKeyMapAccessor.getMapKeyClass();
        
        if (getProject().hasEntity(mapKeyClass)) {
            keyMapping = processEntityMapKeyClass(mappedKeyMapAccessor);
        } else if (getProject().hasEmbeddable(mapKeyClass)) {
            keyMapping = processEmbeddableMapKeyClass(mappedKeyMapAccessor);
        } else {
            keyMapping = processDirectMapKeyClass(mappedKeyMapAccessor);
        }
          
        Class containerClass;
        if (mapping instanceof ForeignReferenceMapping) {
            if (usesIndirection()) {
                containerClass = ClassConstants.IndirectMap_Class;
                ((ForeignReferenceMapping) mapping).setIndirectionPolicy(new TransparentIndirectionPolicy());
            } else {
                containerClass = java.util.Hashtable.class;
                ((ForeignReferenceMapping) mapping).dontUseIndirection();
            }
        } else {
            containerClass = java.util.Hashtable.class;
        }

        MappedKeyMapContainerPolicy policy = new MappedKeyMapContainerPolicy(containerClass);
        policy.setKeyMapping(keyMapping);
        policy.setValueMapping((MapComponentMapping) mapping);
        mapping.setContainerPolicy(policy);
    }
    
    /**
     * INTERNAL:
     * Process a convert value which specifies the name of an EclipseLink
     * converter to process with this accessor's mapping.     
     */
    protected void processMappingConverter(DatabaseMapping mapping, String convertValue, List<ConvertMetadata> converts, MetadataClass referenceClass, MetadataClass referenceClassWithGenerics, boolean isForMapKey) {
        boolean hasConverts = (converts != null && ! converts.isEmpty());
        
        // A convert value is an EclipseLink extension and it takes precedence
        // over all JPA converters so check for it first.
        if (convertValue != null && ! convertValue.equals(Convert.NONE)) {
            processConvert(mapping, convertValue, referenceClass, isForMapKey, hasConverts);
        } else if (hasConverts) {
            // If we have JPA converts, apply them.
            processConverts(converts, mapping, referenceClass, isForMapKey);
        } else if (getProject().hasAutoApplyConverter(referenceClassWithGenerics)) {
            // If no convert is specified and there exist an auto-apply
            // converter for the reference class, apply it.
            getProject().getAutoApplyConverter(referenceClassWithGenerics).process(mapping, isForMapKey, null);
        } else {
            // Check for original JPA converters. Check for an enum first since 
            // it will fall into a serializable mapping otherwise since enums 
            // are serialized.
            if (isEnumerated(referenceClass, isForMapKey)) {
                processEnumerated(getEnumerated(isForMapKey), mapping, referenceClass, isForMapKey);
            } else if (isLob(referenceClass, isForMapKey)) {
                processLob(getLob(isForMapKey), mapping, referenceClass, isForMapKey);
            } else if (isTemporal(referenceClass, isForMapKey)) {
                processTemporal(getTemporal(isForMapKey), mapping, referenceClass, isForMapKey);
            } else if (isSerialized(referenceClass, isForMapKey)) {
                processSerialized(mapping, referenceClass, isForMapKey);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process a mapping key converter either from an EclipseLink convert
     * specification or a JPA converter specification (map-key-convert, 
     * map-key-temporal, map-key-enumerated) to be applied to the given mapping.
     */
    protected void processMappingKeyConverter(DatabaseMapping mapping, String convertValue, List<ConvertMetadata> converts, MetadataClass referenceClass, MetadataClass referenceClassWithGenerics) {
        processMappingConverter(mapping, convertValue, getMapKeyConverts(converts), referenceClass, referenceClassWithGenerics, true);
    }
    
    /**
     * INTERNAL:
     * Process a convert value which specifies the name of an EclipseLink
     * converter to process with this accessor's mapping.
     */
    protected void processMappingValueConverter(DatabaseMapping mapping, String convertValue, List<ConvertMetadata> converts, MetadataClass referenceClass, MetadataClass referenceClassWithGenerics) {
        processMappingConverter(mapping, convertValue, getConverts(converts), referenceClass, referenceClassWithGenerics, false);
    }

    /**
     * INTERNAL:
     * Process the join columns for the owning side of a one to one mapping.
     * The default pk and fk field names are used only with single primary key 
     * entities. The processor should never get as far as to use them with 
     * entities that have a composite primary key (validation exception will be 
     * thrown).
     */
    protected void processForeignKeyRelationship(ForeignReferenceMapping mapping, List<JoinColumnMetadata> joinColumns, ForeignKeyMetadata foreignKey, MetadataDescriptor referenceDescriptor, String defaultFKFieldName, DatabaseTable defaultFKTable) {
        // We need to know if all the mappings are read-only so we can determine 
        // if we use target foreign keys to represent read-only parts of the 
        // join, or if we simply set the whole mapping as read-only
        boolean allReadOnly = true;
        Map<DatabaseField, DatabaseField> fields = new HashMap<DatabaseField, DatabaseField>();
        
        // Build our fk->pk associations.
        for (JoinColumnMetadata joinColumn : joinColumns) {
            // Look up the primary key field from the referenced column name.
            DatabaseField pkField = getReferencedField(joinColumn.getReferencedColumnName(), referenceDescriptor, MetadataLogger.PK_COLUMN);
            
            // The foreign key should be built using the primary key field 
            // since it will contain extra metadata that can not be specified
            // in the join column. This will keep the pk and fk fields in sync.
            DatabaseField fkField = joinColumn.getForeignKeyField(pkField);
            setFieldName(fkField, defaultFKFieldName, MetadataLogger.FK_COLUMN);
            
            // Set the table name if one is not already set.
            if (! fkField.hasTableName()) {
                fkField.setTable(defaultFKTable);
            }
            
            fields.put(fkField, pkField);
            allReadOnly = allReadOnly && fkField.isReadOnly();
        }
        
        DatabaseTable foreignKeyTable = null;
        
        // Apply the fields to the mapping based on what we found.
        for (DatabaseField fkField : fields.keySet()) {
            DatabaseField pkField = fields.get(fkField);
            
            if (allReadOnly || ! fkField.isReadOnly()) {
                // Add a source foreign key to the mapping.
                mapping.addForeignKeyField(fkField, pkField);
            } else {
                // This is a read-only join column that is part of a set of join 
                // columns that are not all read only - hence this is not a 
                // read-only mapping, but instead uses a target foreign key 
                // field to enable the read-only functionality
                mapping.addTargetForeignKeyField(pkField, fkField);
            }
            
            // Set the foreign key table to the first fk's table.
            if (foreignKeyTable == null) {
                foreignKeyTable = fkField.getTable();
            }
        }
        
        // If all the join columns are read-only then set the mapping as read only.
        mapping.setIsReadOnly(allReadOnly);
        
        // Process the foreign key if one is provided. Right now this assumes
        // and supports the foreign keys all being on the same table. It covers
        // the spec case (on the source table) and our extended support when 
        // the fk's are on the target table as well.
        if (foreignKey != null) {
            foreignKey.process(foreignKeyTable);
        }
    }
    
    /**
     * INTERNAL:
     * Adds properties to the mapping.
     */
    protected void processProperties(DatabaseMapping mapping) {
        // If we were loaded from XML use the properties loaded from there
        // only. Otherwise look for annotations.
        if (loadedFromXML()) {
            for (PropertyMetadata property : getProperties()) {
                processProperty(mapping, property);
            }
        } else {
            // Look for annotations.
            MetadataAnnotation properties = getAnnotation(Properties.class);
            if (properties != null) {
                for (Object property : properties.getAttributeArray("value")) {
                    processProperty(mapping, new PropertyMetadata((MetadataAnnotation) property, this));
                }
            }
            
            MetadataAnnotation property = getAnnotation(Property.class);
            if (property != null) {
                processProperty(mapping, new PropertyMetadata(property, this));
            }    
        }
    }
    
    /**
     * INTERNAL:
     * Adds properties to the mapping. They can only come from one place,
     * therefore if we add the same one twice we know to throw an exception.
     */
    protected void processProperty(DatabaseMapping mapping, PropertyMetadata property) {
        if (property.shouldOverride(m_properties.get(property.getName()))) {
            m_properties.put(property.getName(), property); 
            mapping.addUnconvertedProperty(property.getName(), property.getValue(), getJavaClassName(property.getValueType()));
        }
    }
    
    /**
     * INTERNAL:
     * Subclasses should call this method if they want the warning message or
     * override the method if they want/support different behavior.
     * @see BasicAccessor
     */
    protected void processReturnInsert() {
        if (hasReturnInsert()) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_RETURN_INSERT_ANNOTATION, getAnnotatedElement());
        }
    }    
    
    /**
     * INTERNAL:
     * Subclasses should call this method if they want the warning message.
     */
    protected void processReturnInsertAndUpdate() {
        processReturnInsert();
        processReturnUpdate();
    }
    
    /**
     * INTERNAL:
     * Subclasses should call this method if they want the warning message or
     * override the method if they want/support different behavior.
     * @see BasicAccessor
     */
    protected void processReturnUpdate() {
        if (hasReturnUpdate()) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_RETURN_UPDATE_ANNOTATION, getAnnotatedElement());
        }
    }
    
    /**
     * INTERNAL:
     * Process a potential serializable attribute. If the class implements 
     * the Serializable interface then set a SerializedObjectConverter on 
     * the mapping.
     */
    protected void processSerialized(DatabaseMapping mapping, MetadataClass referenceClass, boolean isForMapKey) {        
        new SerializedMetadata(this).process(mapping, this, referenceClass, isForMapKey);
    }
    
    /**
     * INTERNAL:
     * Process a potential serializable attribute. If the class implements 
     * the Serializable interface then set a SerializedObjectConverter on 
     * the mapping.
     */
    protected void processSerialized(DatabaseMapping mapping, MetadataClass referenceClass, MetadataClass classification, boolean isForMapKey) {
        new SerializedMetadata(this).process(mapping, this, referenceClass, classification, isForMapKey);
    }
    
    /**
     * INTERNAL:
     * Process a temporal type accessor.
     */
    protected void processTemporal(TemporalMetadata temporal, DatabaseMapping mapping, MetadataClass referenceClass, boolean isForMapKey) {
        if (temporal == null) {
            // We need to have a temporal type on either a basic mapping or the key to a collection
            // mapping. Since the temporal type was not specified, per the JPA spec we *should* throw an
            // exception.. but lets be a little nice to our users and default to timestamp.
            MetadataAnnotation annotation = new MetadataAnnotation();
            annotation.setName("javax.persistence.Temporal");
            annotation.addAttribute("value", "TIMESTAMP");
            temporal = new TemporalMetadata(annotation, this);

            // This call handles both @Temporal and @MapKeyTemporal
            setTemporal(temporal, isForMapKey);
        }
        
        temporal.process(mapping, this, referenceClass, isForMapKey);
    }
    
    /**
     * INTERNAL:
     * Set the getter and setter access methods for this accessor.
     */
    protected void setAccessorMethods(DatabaseMapping mapping) {
        if (usesPropertyAccess() || usesVirtualAccess()) {
            if (usesVirtualAccess()) {
                mapping.setAttributeAccessor(new VirtualAttributeAccessor());
            }
            
            mapping.setGetMethodName(getGetMethodName());
            mapping.setSetMethodName(getSetMethodName());
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAttributeType(String attributeType) {
        m_attributeType = attributeType;
    }
    
    /**
     * INTERNAL:
     * Sets the class accessor for this mapping accessor.
     */
    public void setClassAccessor(ClassAccessor classAccessor) {
        m_classAccessor = classAccessor;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setField(ColumnMetadata column) {
        m_field = column;
    }
    
    /** 
     * INTERNAL:
     * Set the correct indirection policy on a collection mapping. Method
     * assume that the reference class has been set on the mapping before
     * calling this method.
     */
    protected void setIndirectionPolicy(ContainerMapping mapping, String mapKey, boolean usesIndirection) {
        MetadataClass rawClass = getRawClass();
        boolean containerPolicySet = false;
        if (usesIndirection && (mapping instanceof ForeignReferenceMapping)) {
            containerPolicySet = true;
            CollectionMapping collectionMapping = (CollectionMapping)mapping;
            if (rawClass.equals(Map.class)) {
                if (collectionMapping.isDirectMapMapping()) {
                    ((DirectMapMapping) mapping).useTransparentMap();
                } else {
                    collectionMapping.useTransparentMap(mapKey);
                }
            } else if (rawClass.equals(List.class)) {
                collectionMapping.useTransparentList();
            } else if (rawClass.equals(Collection.class)) {
                collectionMapping.useTransparentCollection();
            } else if (rawClass.equals(Set.class)) {
                collectionMapping.useTransparentSet();
            } else {
                getLogger().logWarningMessage(MetadataLogger.WARNING_INVALID_COLLECTION_USED_ON_LAZY_RELATION, getJavaClass(), getAnnotatedElement(), rawClass);
                processIndirection((ForeignReferenceMapping)mapping);
                containerPolicySet = false;
            }
        } else {
            if (mapping instanceof CollectionMapping) {
                ((CollectionMapping)mapping).dontUseIndirection();
            }
        }
        if (!containerPolicySet) {            
            if (rawClass.equals(Map.class)) {
                if (mapping instanceof DirectMapMapping) {
                    ((DirectMapMapping) mapping).useMapClass(java.util.Hashtable.class);
                } else {
                    mapping.useMapClass(java.util.Hashtable.class, mapKey);
                }
            } else if (rawClass.equals(Set.class)) {
                // This will cause it to use a CollectionContainerPolicy type
                mapping.useCollectionClass(java.util.HashSet.class);
            } else if (rawClass.equals(List.class)) {
                // This will cause a ListContainerPolicy type to be used or 
                // OrderedListContainerPolicy if ordering is specified.
                mapping.useCollectionClass(java.util.Vector.class);
            } else if (rawClass.equals(Collection.class)) {
                // Force CollectionContainerPolicy type to be used with a 
                // collection implementation.
                mapping.setContainerPolicy(new CollectionContainerPolicy(java.util.Vector.class));
            } else {
                // Use the supplied collection class type if its not an interface
                if (mapKey == null || mapKey.equals("")){
                    if (rawClass.isList()) {
                        mapping.useListClassName(rawClass.getName());
                    } else {
                        mapping.useCollectionClassName(rawClass.getName());
                    }
                } else {
                    mapping.useMapClassName(rawClass.getName(), mapKey);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * This will do three things:
     * 1 - process any common level metadata for all mappings.
     * 2 - add the mapping to the internal descriptor.
     * 3 - store the actual database mapping associated with this accessor.
     * 
     * Calling this method is a must for all mapping accessors since it will 
     * help to:
     * 1 - determine if the accessor has been processed, and
     * 2 - sub processing will may need access to the mapping to set its 
     *     metadata.
     */
    protected void setMapping(DatabaseMapping mapping) {
        if (! isMultitenantId()) {
            // Before adding the mapping to the descriptor, process the 
            // properties for this mapping (if any). Avoid this is we are
            // multitenant id accessor which is derived from primary key
            // tenant discriminator columns on the class where any properties
            // defined there do not apply to this mapping.
            processProperties(mapping);
        }
        
        // Add the mapping to the class descriptor.
        getDescriptor().getClassDescriptor().addMapping(mapping);
        
        // Keep a reference back to this mapping for quick look up.
        m_mapping = mapping;  
    }
    
    /**
     * INTERNAL:
     * An override mapping is created when an association override is specified
     * to a relationship accessor on an embeddable class. For any non-owning 
     * relationship accessor referring to this accessor will need its override
     * mapping and not the original mapping from the embeddable so that it can
     * populate the right metadata.
     */
    protected void setOverrideMapping(DatabaseMapping mapping) {
        m_overrideMapping = mapping;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public String toString() {
        return getAnnotatedElementName();
    }
    
    /**
     * INTERNAL:
     * Update the primary key field on the owning descriptor the override field
     * given.
     */
    protected void updatePrimaryKeyField(MappingAccessor idAccessor, DatabaseField overrideField) {
        getOwningDescriptor().removePrimaryKeyField(idAccessor.getMapping().getField());
        getOwningDescriptor().addPrimaryKeyField(overrideField, idAccessor);
    }
    
    /**
     * INTERNAL:
     * @see RelationshipAccessor
     * @see DirectAccessor
     */
    protected boolean usesIndirection() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Returns true if this mapping or class uses property access. In an 
     * inheritance hierarchy, the subclasses inherit their access type from 
     * the parent (unless there is an explicit access setting).
     */
    public boolean usesPropertyAccess() {
        if (hasAccess()) {
            return getAccess().equals(JPA_ACCESS_PROPERTY);
        } else {
            return hasAccessMethods() ? !usesVirtualAccess() : m_classAccessor.usesPropertyAccess();
        }
    }
    
    /**
     * INTERNAL:
     * Returns true if this mapping or class uses virtual access. In an 
     * inheritance hierarchy, the subclasses inherit their access type from 
     * the parent (unless there is an explicit access setting).
     */
    public boolean usesVirtualAccess() {
        if (hasAccess()) {
            return getAccess().equals(EL_ACCESS_VIRTUAL);
        } else {
            return m_classAccessor.usesVirtualAccess();
        }
    }

    /**
     * INTERNAL:
     * Returns true if this mapping or class uses property field. In an 
     * inheritance hierarchy, the subclasses inherit their access type from 
     * the parent (unless there is an explicit access setting).
     */
    public boolean usesFieldAccess() {
        if (hasAccess()) {
            return getAccess().equals(JPA_ACCESS_FIELD);
        } else {
            return m_classAccessor.usesFieldAccess();
        }
    }
}
