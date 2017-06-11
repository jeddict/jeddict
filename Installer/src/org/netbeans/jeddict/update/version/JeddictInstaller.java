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
package org.netbeans.jeddict.update.version;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.autoupdate.UpdateElement;
import org.netbeans.api.autoupdate.UpdateManager;
import org.netbeans.api.autoupdate.UpdateUnit;
import org.openide.*;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.OnShowing;
import org.openide.windows.WindowManager;

@OnShowing
public class JeddictInstaller extends ModuleInstall implements Runnable {
    public static final String CATEGORY = "Jeddict";
    public static final String IDE_NAME = "NetBeans";
    public static final String CONFIG = ".jeddict.cfg";
    public static final Logger log = Logger.getLogger("jeddict");

    public static String VERSION = "Unknown";
    public static String IDE_VERSION = "Unknown";
    public static Boolean DEBUG = false;

    public static Boolean READY = false;
    public static String lastFile = null;
    public static long lastTime = 0;

    @Override
    public void run() {
        JeddictInstaller.VERSION = JeddictInstaller.getPluginVersion();
        JeddictInstaller.IDE_VERSION = System.getProperty("netbeans.buildnumber");
        JeddictInstaller.log.log(Level.INFO, "Initializing Jeddict v{0} (https://jeddict.github.io/)", JeddictInstaller.VERSION);

        JeddictInstaller.info("Finished initializing Jeddict...");
        lookupUpdates();
    }

    static boolean LOOKUP_BOOTSTRAP_UPDATE = true;

    /**
     * Generic method can be called either on IDE startup or modeler file
     * startup
     */
    public static void lookupUpdates() {
        if (LOOKUP_BOOTSTRAP_UPDATE) {
        // install update checker when UI is ready (main window shown)
        WindowManager.getDefault().invokeWhenUIReady(() -> {
            RequestProcessor.getDefault().post(() -> {
                UpdateHandler.checkAndHandleUpdates();
            }, getUpdateStartTime());
            });
        }
    }

    private static int getUpdateStartTime() {
        String s = NbBundle.getBundle("org.netbeans.jeddict.update.version.Bundle").getString("UpdateHandler.StartTime");
        return Integer.parseInt(s);
    }

    public static void info(String msg) {
        log.log(Level.INFO, msg);
    }

    public static void warn(String msg) {
        log.log(Level.WARNING, msg);
    }

    public static void error(String msg) {
        log.log(Level.SEVERE, msg);
    }

    public static void errorDialog(String msg) {
        int msgType = NotifyDescriptor.ERROR_MESSAGE;
        NotifyDescriptor d = new NotifyDescriptor.Message(msg, msgType);
        DialogDisplayer.getDefault().notify(d);
    }

    public static void debug(String msg) {
        log.log(Level.CONFIG, msg);
    }

    public static String getPluginVersion() {
        for (UpdateUnit updateUnit : UpdateManager.getDefault().getUpdateUnits()) {
            UpdateElement updateElement = updateUnit.getInstalled();
            if (updateElement != null)
                if (JeddictInstaller.CATEGORY.equals(updateElement.getCodeName()))
                    return updateElement.getSpecificationVersion();
        }
        return "Unknown";
    }
}
