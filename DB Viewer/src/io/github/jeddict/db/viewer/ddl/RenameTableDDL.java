/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 * Portions Copyright [2018] Gaurav Gupta
 */
package io.github.jeddict.db.viewer.ddl;

import org.netbeans.lib.ddl.CommandNotSupportedException;
import org.netbeans.lib.ddl.DDLException;
import org.netbeans.lib.ddl.impl.RenameTable;
import org.netbeans.lib.ddl.impl.Specification;
import org.netbeans.lib.ddl.util.CommandBuffer;
import org.openide.util.Exceptions;

/**
 * This class factors out the DDL logic from the CreateTableDialog
 * 
 * @author <a href="mailto:david@vancouvering.com>David Van Couvering</a>
 */
public class RenameTableDDL {
    private final Specification       spec;
    private final String              schema;
    private final String              tablename;
    private final String              newTablename;

    public RenameTableDDL (
            Specification spec, 
            String schema,
            String tablename,
            String newTablename) {
        this.spec       = spec;
        this.schema     = schema;
        this.tablename  = tablename;
        this.newTablename = newTablename;
    }
    
    /**
     * Execute the DDL to rename a table.  
     * 
     */
    public boolean execute() {
        CommandBuffer cbuff = new CommandBuffer();
        try {

            RenameTable cmd = spec.createCommandRenameTable(tablename, newTablename);
            cmd.setObjectOwner(schema);
            cbuff.add(cmd);

            //execute DDL command
            cbuff.execute();

        } catch (CommandNotSupportedException | DDLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return cbuff.wasException();
    }

}
