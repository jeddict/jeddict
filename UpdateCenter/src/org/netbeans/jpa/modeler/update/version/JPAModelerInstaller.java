package org.netbeans.jpa.modeler.update.version;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.autoupdate.UpdateElement;
import org.netbeans.api.autoupdate.UpdateManager;
import org.netbeans.api.autoupdate.UpdateUnit;
import org.openide.*;
import org.openide.modules.ModuleInstall;
import org.openide.util.RequestProcessor;
import org.openide.windows.OnShowing;
import org.openide.windows.WindowManager;

@OnShowing
public class JPAModelerInstaller extends ModuleInstall implements Runnable {
    public static final String CODENAME = "org.netbeans.jpa.modeler";
    public static final String IDE_NAME = "NetBeans";
    public static final String CONFIG = ".jpamodeler.cfg";
    public static final short FREQUENCY = 2; // minutes between pings
    public static final Logger log = Logger.getLogger("JPAModeler");

    public static String VERSION = "Unknown";
    public static String IDE_VERSION = "Unknown";
    public static Boolean DEBUG = false;

    public static Boolean READY = false;
    public static String lastFile = null;
    public static long lastTime = 0;

    @Override
    public void run() {
        JPAModelerInstaller.VERSION = JPAModelerInstaller.getPluginVersion();
        JPAModelerInstaller.IDE_VERSION = System.getProperty("netbeans.buildnumber");
        JPAModelerInstaller.log.log(Level.INFO, "Initializing JPA Modeler v{0} (https://jpamodeler.github.io/)", JPAModelerInstaller.VERSION);

        JPAModelerInstaller.info("Finished initializing JPA Modeler...");

        // install update checker when UI is ready (main window shown)
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                RequestProcessor.getDefault().post(() -> {
                    try {
                        UpdateHandler.checkAndHandleUpdates();
                    } catch (NullPointerException e) {
                        JPAModelerInstaller.error(e.toString());
                    }
                }, 1000);

            }
        });
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
                if (JPAModelerInstaller.CODENAME.equals(updateElement.getCodeName()))
                    return updateElement.getSpecificationVersion();
        }
        return "Unknown";
    }
}
