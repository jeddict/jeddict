/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.github.jeddict.db.modeler.ddl;

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
