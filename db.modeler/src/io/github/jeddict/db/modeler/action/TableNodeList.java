/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jeddict.db.modeler.action;

import java.util.List;
import org.netbeans.api.db.explorer.node.BaseNode;

/**
 *
 * @author gaura
 */
public class TableNodeList {
    
    private final List<BaseNode> baseNodes;

    public TableNodeList(List<BaseNode> baseNodes) {
        this.baseNodes = baseNodes;
    }
    
    public List<BaseNode> getBaseNodes() {
        return baseNodes;
    }
    
}
