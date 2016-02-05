/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     11/23/2009-2.0 Guy Pelletier 
 *       - 295790: JPA 2.0 adding @MapsId to one entity causes initialization errors in other entities
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     10/09/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     10/25/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     11/19/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 *     11/28/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.EmbeddableMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ATTRIBUTE_OVERRIDE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ATTRIBUTE_OVERRIDES;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ASSOCIATION_OVERRIDE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ASSOCIATION_OVERRIDES;

/**
 * An embedded relationship accessor. It may define all the same attributes
 * as an entity, therefore, it also must handle nesting embedded's to the nth
 * level. An embedded owning descriptor is a reference back to the actual
 * owning entity's descriptor where the first embedded was discovered.
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
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class EmbeddedAccessor extends MappingAccessor {
    private List<ConvertMetadata> m_converts;
    private List<AssociationOverrideMetadata> m_associationOverrides = new ArrayList<AssociationOverrideMetadata>();
    private List<AttributeOverrideMetadata> m_attributeOverrides = new ArrayList<AttributeOverrideMetadata>();

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public EmbeddedAccessor() {
        super("<embedded>");
    }
    
    /**
     * INTERNAL:
     */
    protected EmbeddedAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    public EmbeddedAccessor(MetadataAnnotation embedded, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(embedded, accessibleObject, classAccessor);
        
        // Set the attribute overrides if some are present.
        // Process the attribute overrides first.
        if (isAnnotationPresent(JPA_ATTRIBUTE_OVERRIDES)) {
            for (Object attributeOverride : getAnnotation(JPA_ATTRIBUTE_OVERRIDES).getAttributeArray("value")) {
                m_attributeOverrides.add(new AttributeOverrideMetadata((MetadataAnnotation) attributeOverride, this));
            }
        }
        
        // Process the single attribute override second.  
        if (isAnnotationPresent(JPA_ATTRIBUTE_OVERRIDE)) {
            m_attributeOverrides.add(new AttributeOverrideMetadata(getAnnotation(JPA_ATTRIBUTE_OVERRIDE), this));
        }
        
        // Set the association overrides if some are present.
        // Process the attribute overrides first.
        if (isAnnotationPresent(JPA_ASSOCIATION_OVERRIDES)) {
            for (Object associationOverride : (Object[]) getAnnotation(JPA_ASSOCIATION_OVERRIDES).getAttributeArray("value")) {
                m_associationOverrides.add(new AssociationOverrideMetadata((MetadataAnnotation) associationOverride, this));
            }
        }
        
        // Process the single attribute override second.
        if (isAnnotationPresent(JPA_ASSOCIATION_OVERRIDE)) {
            m_associationOverrides.add(new AssociationOverrideMetadata(getAnnotation(JPA_ASSOCIATION_OVERRIDE), this));
        }
    }
    
    /**
     * INTERNAL:
     * Subclasses that support key converts need to override this method.
     */
    @Override
    protected void addConvert(ConvertMetadata convert) {
        if (m_converts == null) {
            m_converts = new ArrayList<ConvertMetadata>();
        }
        
        m_converts.add(convert);
    }
    
    /**
     * INTERNAL:
     */
    public void addMapsIdAccessor(MappingAccessor mapsIdAccessor) {
        ((AggregateObjectMapping) getMapping()).addMapsIdMapping(mapsIdAccessor.getMapping());
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof EmbeddedAccessor) {
            EmbeddedAccessor embeddedAccessor = (EmbeddedAccessor) objectToCompare;
            
            if (! valuesMatch(m_associationOverrides, embeddedAccessor.getAssociationOverrides())) {
                return false;
            }
            
            if (! valuesMatch(m_attributeOverrides, embeddedAccessor.getAttributeOverrides())) {
                return false;
            }
            
            return valuesMatch(m_converts, embeddedAccessor.getConverts());
        }
        
        return false;
    }
    
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<AssociationOverrideMetadata> getAssociationOverrides() {
        return m_associationOverrides;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<AttributeOverrideMetadata> getAttributeOverrides() {
        return m_attributeOverrides;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ConvertMetadata> getConverts() {
        return m_converts;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
    
        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_attributeOverrides, accessibleObject);
        initXMLObjects(m_associationOverrides, accessibleObject);
        initXMLObjects(m_converts, accessibleObject);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isEmbedded() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process an embedded.
     */    
    public void process() {
        // Build and aggregate object mapping and add it to the descriptor.
        AggregateMapping mapping = getOwningDescriptor().getClassDescriptor().newAggregateMapping();
        setMapping(mapping);
        
        mapping.setReferenceClassName(getReferenceClassName());
        mapping.setAttributeName(getAttributeName());    
        
        // Will check for PROPERTY access
        setAccessorMethods(mapping);

        // EIS and ORDT mappings may not be aggregate object mappings.
        if (mapping.isAggregateObjectMapping()) {
            AggregateObjectMapping aggregateMapping = (AggregateObjectMapping) mapping;
            aggregateMapping.setIsNullAllowed(true);
            
            // Process attribute overrides.
            processAttributeOverrides(m_attributeOverrides, aggregateMapping, getReferenceDescriptor());
           
            // Process association overrides.
            processAssociationOverrides(m_associationOverrides, aggregateMapping, getReferenceDescriptor());
            
            // Process converts.
            processConverts(getConverts(m_converts), aggregateMapping, getReferenceClass(), false);
        } else if (mapping.isAbstractCompositeObjectMapping()) {
            ((AbstractCompositeObjectMapping)mapping).setField(getDatabaseField(getDescriptor().getPrimaryTable(), MetadataLogger.COLUMN));
        }
        
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAssociationOverrides(List<AssociationOverrideMetadata> associationOverrides) {
        m_associationOverrides = associationOverrides;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAttributeOverrides(List<AttributeOverrideMetadata> attributeOverrides) {
        m_attributeOverrides = attributeOverrides;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConverts(List<ConvertMetadata> converts) {
        m_converts = converts;
    }
    
    /**
     * INTERNAL:
     * Called when process the mapsId metadata. The id fields for this owning
     * descriptor must have it's id fields update to those from the one to one
     * accessor that maps them. We process embedded and embedded id mappings
     * first, so by default they get mapped and processed as they normally
     * would. When we go through the relationship accessors and discover a
     * mapsId we then need to make some updates to our list of primary key
     * fields.
     */
    protected void updateDerivedIdField(EmbeddableMapping embeddableMapping, String overrideName, DatabaseField overrideField, MappingAccessor mappingAccessor) {
       addFieldNameTranslation(embeddableMapping, overrideName, overrideField, mappingAccessor);
       
       // Update the primary key field.
       updatePrimaryKeyField(mappingAccessor, overrideField);
    }
}
