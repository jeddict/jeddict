
package org.netbeans.db.modeler.specification.model.panel;

import org.netbeans.modeler.component.ModelerPanelTopComponent;

/**
 * Remove this class in future when save file support is provided for DB file
 *
 */
public class DBPanel extends ModelerPanelTopComponent{
    
    @Override
    public void componentShowing() { //this function is added to handle multiple topcompoent for single file
        //Ignore
    }
    public void changePersistenceState(boolean state){
        //Ignore
    }
            
}
