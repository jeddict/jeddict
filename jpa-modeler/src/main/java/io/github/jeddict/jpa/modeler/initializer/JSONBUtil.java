/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.modeler.initializer;

import static io.github.jeddict.jpa.modeler.initializer.JPAModelerUtil.ERROR_ICON;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.workspace.WorkSpace;
import io.github.jeddict.jsonb.modeler.JSONBModeler;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import org.netbeans.modeler.core.ModelerFile;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author jGauravGupta
 */
public class JSONBUtil {

    public static void openJSONBViewer(ModelerFile file) {
        EntityMappings entityMappings = (EntityMappings) file.getModelerScene().getBaseElementSpec();
        openJSONBViewer(file, entityMappings, entityMappings.getCurrentWorkSpace());
    }

    public static void openJSONBViewer(ModelerFile file, EntityMappings entityMappings, WorkSpace workSpace) {
        if (!((JPAModelerScene) file.getModelerScene()).compile()) {
            return;
        }
        WorkSpace paramWorkSpace = entityMappings.getRootWorkSpace() == workSpace ? null : workSpace;
        try {
            PreExecutionUtil.preExecution(file);
            JSONBModeler jsonbModelerRequestManager = Lookup.getDefault().lookup(JSONBModeler.class);

            if (jsonbModelerRequestManager == null) {
                JOptionPane.showMessageDialog(null,
                        NbBundle.getMessage(JSONBUtil.class, "Error.PLUGIN_INSTALLATION.text", "JSONB Modeler", file.getCurrentVersion()),
                        NbBundle.getMessage(JSONBUtil.class, "Error.PLUGIN_INSTALLATION.title"), ERROR_MESSAGE, ERROR_ICON);
            } else {
                //close diagram and reopen 
//                long st = new Date().getTime();
                file.getChildrenFile("JSONB").ifPresent(ModelerFile::close);
//                System.out.println("openJSONBViewer close Total time : " + (new Date().getTime() - st) + " ms");
                jsonbModelerRequestManager.init(file, entityMappings, paramWorkSpace);
//                System.out.println("openJSONBViewer Total time : " + (new Date().getTime() - st) + " ms");
            }
        } catch (Throwable t) {
            file.handleException(t);
        }
    }

}

   