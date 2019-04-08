/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.installer.update.version;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import static java.util.logging.Level.INFO;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import org.netbeans.api.autoupdate.UpdateElement;
import org.netbeans.api.autoupdate.UpdateManager;
import org.netbeans.api.autoupdate.UpdateUnit;
import org.openide.*;
import org.openide.modules.ModuleInstall;
import static org.openide.util.NbBundle.getMessage;
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
    static boolean LOOKUP_BOOTSTRAP_UPDATE = true;

    @Override
    public void run() {
        Set<String> versions = JeddictInstaller.getPluginVersion();
        if (versions.isEmpty()) {
            // unknown
        } else if (versions.size() == 1) {
            JeddictInstaller.VERSION = versions.iterator().next();
            JeddictInstaller.IDE_VERSION = System.getProperty("netbeans.buildnumber");
            log.log(INFO, "Initializing Jeddict v{0} (https://jeddict.github.io/)", JeddictInstaller.VERSION);
            log.info("Finished initializing Jeddict...");
            lookupUpdates();
        } else {
            // multiple version found
            JOptionPane.showMessageDialog(
                    WindowManager.getDefault().getMainWindow(),
                    getMessage(JeddictInstaller.class, "MultipleVersion.text", versions),
                    getMessage(JeddictInstaller.class, "MultipleVersion.title"),
                    WARNING_MESSAGE
            );
        }
    }

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
        return Integer.parseInt(getMessage(JeddictInstaller.class, "UpdateHandler.StartTime"));
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

    public static Set<String> getPluginVersion() {
        Set<String> versions = new HashSet<>();
        for (UpdateUnit updateUnit : UpdateManager.getDefault().getUpdateUnits()) {
            UpdateElement updateElement = updateUnit.getInstalled();
            if (updateElement != null) {
                if (JeddictInstaller.CATEGORY.equals(updateElement.getCategory())) {
                    versions.add(updateElement.getSpecificationVersion());
                }
            }
        }
        return versions;
    }
}
