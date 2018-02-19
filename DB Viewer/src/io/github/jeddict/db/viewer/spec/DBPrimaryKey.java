/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jeddict.db.viewer.spec;

import org.netbeans.modules.db.metadata.model.api.Column;

/**
 *
 * @author Gaurav Gupta
 */
public class DBPrimaryKey extends DBColumn {


    public DBPrimaryKey(String name, Column column) {
        super(name, column);
    }

}
