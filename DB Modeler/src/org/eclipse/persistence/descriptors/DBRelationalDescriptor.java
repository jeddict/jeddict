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
 *     
 ******************************************************************************/  
package org.eclipse.persistence.descriptors;

import java.util.*;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;

/**
 * <p><b>Purpose</b>: EclipseLink has been designed to take advantage of the similarities between
 * relational databases and objects while accommodating for their differences, providing an object
 * oriented wrapper for relational databases. This is accomplished through the use of Descriptors.
 * A descriptor is a pure specification class with all its behavior deputized to DescriptorEventManager,
 * DescriptorQueryManager and ObjectBuilder. Look at the following variables for the list
 * of specification on the descriptor.
 * <p>
 * A Descriptor is a set of mappings that describe how an objects's data is to be represented in a
 * relational database. It contains mappings from the class instance variables to the table's fields,
 * as well as the transformation routines necessary for storing and retrieving attributes. As such
 * the descriptor acts as the link between the Java object and its database representation.
 * <p>
 * Every descriptor is initialized with the following information:
 * <ul>
 * <li> The Java class its describes, and the corresponding table(s) for storing instances of the class.
 * <li> The primary key of the table.
 * <li> A list of query keys for field names.
 * <li> A description of the objects's attributes and relationships. This information is stored in mappings.
 * <li> A set of user selectable properties for tailoring the behavior of the descriptor.
 * </ul>
 *
 * <p> This descriptor subclass should be used for object-relational mapping,
 * and allows for other datatype mappings to be done in the XML, EIS and OR sibling classes.
 *
 * @see DescriptorEventManager
 * @see DescriptorQueryManager
 * @see InheritancePolicy
 * @see InterfacePolicy
 */
public class DBRelationalDescriptor extends RelationalDescriptor {
    
  private ClassAccessor accessor;
    
    public DBRelationalDescriptor(ClassAccessor accessor) {
        super();
        this.accessor=accessor;
    }

    /**
     * @return the accessor
     */
    public ClassAccessor getAccessor() {
        return accessor;
    }

}
