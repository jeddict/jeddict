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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.netbeans.api.autoupdate.InstallSupport;
import org.netbeans.api.autoupdate.InstallSupport.Installer;
import org.netbeans.api.autoupdate.InstallSupport.Validator;
import org.netbeans.api.autoupdate.OperationContainer;
import org.netbeans.api.autoupdate.OperationContainer.OperationInfo;
import org.netbeans.api.autoupdate.OperationException;
import org.netbeans.api.autoupdate.OperationSupport.Restarter;
import org.netbeans.api.autoupdate.UpdateElement;
import org.netbeans.api.autoupdate.UpdateManager;
import org.netbeans.api.autoupdate.UpdateUnit;
import org.netbeans.api.autoupdate.UpdateUnitProvider;
import org.netbeans.api.autoupdate.UpdateUnitProviderFactory;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;

public final class UpdateHandler {

    public static boolean timeToCheck() {
        // every startup
        return true;
    }

    public static class UpdateHandlerException extends Exception {
        public UpdateHandlerException(String msg) {
            super(msg);
        }
        public UpdateHandlerException(String msg, Throwable th) {
            super(msg, th);
        }
    }

    public static void checkAndHandleUpdates() {

        JeddictInstaller.info("Checking for updates to Jeddict plugin...");

        // refresh silent update center first
        refreshSilentUpdateProvider();

        Collection<UpdateElement> updates = findUpdates();
        Collection<UpdateElement> available = Collections.emptySet();
        if (installNewModules()) {
            available = findNewModules();
        }
        if (updates.isEmpty() && available.isEmpty()) {
            JeddictInstaller.LOOKUP_BOOTSTRAP_UPDATE = false;
            // none for install
            JeddictInstaller.info("Jeddict plugin is up to date.");
            return;
        }

        JeddictInstaller.info("Found new Jeddict plugin version, updating...");

        // create a container for install
        OperationContainer<InstallSupport> containerForInstall = feedContainer(available, false);
        if (containerForInstall != null) {
            try {
                handleInstall(containerForInstall);
                JeddictInstaller.info("Jeddict plugin installation finished.");
            } catch (UpdateHandlerException ex) {
                JeddictInstaller.error(ex.toString());

                // cancel progress bar
                InstallSupport support = containerForInstall.getSupport();
                try {
                    support.doCancel();
                } catch (OperationException ex1) {
                    JeddictInstaller.error(ex1.toString());
                }

                return;
            }
        }

        // create a container for update
        OperationContainer<InstallSupport> containerForUpdate = feedContainer(updates, true);
        if (containerForUpdate != null) {
            try {
                handleInstall(containerForUpdate);
                JeddictInstaller.info("Jeddict plugin update finished.");
            } catch (UpdateHandlerException ex) {
                JeddictInstaller.error(ex.toString());

                // cancel progress bar
                InstallSupport support = containerForUpdate.getSupport();
                try {
                    support.doCancel();
                } catch (OperationException ex1) {
                    JeddictInstaller.error(ex1.toString());
                }

                return;
            }
        }

    }

    public static boolean isLicenseApproved(String license) {
        // place your code there
        return true;
    }

    // package private methods
    static Collection<UpdateElement> findUpdates() {
        // check updates
        Collection<UpdateElement> elements4update = new HashSet<>();
        List<UpdateUnit> updateUnits = UpdateManager.getDefault().getUpdateUnits();
        for (UpdateUnit unit : updateUnits) {
            if (unit.getInstalled() != null) { // means the plugin already installed
                if (unit.getInstalled()!=null && JeddictInstaller.CATEGORY.equals(unit.getInstalled().getCategory())) { // this is our current plugin
                    if (!unit.getAvailableUpdates().isEmpty()) { // has updates
                        elements4update.add(unit.getAvailableUpdates().get(0)); // add plugin with highest version
                    }
                }
            }
        }
        return elements4update;
    }

    static void handleInstall(OperationContainer<InstallSupport> container) throws UpdateHandlerException {
        // check licenses
        if (!allLicensesApproved(container)) {
            // have a problem => cannot continue
            throw new UpdateHandlerException("Cannot continue because license approval is missing.");
        }
        
        InstallSupport support = container.getSupport();

        // download
        Validator v = null;
        try {
            v = doDownload(support);
        } catch (OperationException | NullPointerException ex) {
            throw new UpdateHandlerException("A problem caught while downloading, cause: ", ex);
        }
        if (v == null) {
            // have a problem => cannot continue
            throw new UpdateHandlerException("Missing Update Validator => cannot continue.");
        }

        // verify
        Installer i = null;
        try {
            i = doVerify(support, v);
        } catch (OperationException ex) {
            // caught a exception
            throw new UpdateHandlerException("A problem caught while verification of updates, cause: ", ex);
        }
        if (i == null) {
            // have a problem => cannot continue
            throw new UpdateHandlerException("Missing Update Installer => cannot continue.");
        }

        // install
        Restarter r = null;
        try {
            r = doInstall(support, i);
        } catch (OperationException ex) {
            // caught a exception
            throw new UpdateHandlerException("A problem caught while installation of updates, cause: ", ex);
        }

        JeddictInstaller.LOOKUP_BOOTSTRAP_UPDATE = false;
        // restart later
        support.doRestartLater(r);
    }

    static Collection<UpdateElement> findNewModules() {
        // check updates
        Collection<UpdateElement> elements4install = new HashSet<>();
        List<UpdateUnit> updateUnits = UpdateManager.getDefault().getUpdateUnits();
        for (UpdateUnit unit : updateUnits) {
            if (unit.getInstalled() == null) { // means the plugin is not installed yet
                if (unit.getCodeName().equals(JeddictInstaller.CATEGORY)) { // this is our current plugin
                    if (!unit.getAvailableUpdates().isEmpty()) { // is available
                        elements4install.add(unit.getAvailableUpdates().get(0)); // add plugin with highest version
                    }
                }
            }
        }
        return elements4install;
    }

    static void refreshSilentUpdateProvider() {
        UpdateUnitProvider silentUpdateProvider = getSilentUpdateProvider();
        if (silentUpdateProvider == null) {
            // have a problem => cannot continue
            JeddictInstaller.info("Missing Silent Update Provider => cannot continue.");
            return ;
        }
        try {
            final String displayName = "Checking for updates to Jeddict plugin...";
            silentUpdateProvider.refresh(
                ProgressHandleFactory.createHandle(displayName, () -> true),
                true
            );
        } catch (IOException ex) {
            // caught a exception
            JeddictInstaller.error("A problem caught while refreshing Update Centers, cause: " + ex.toString());
        }
    }

    static UpdateUnitProvider getSilentUpdateProvider() {
        List<UpdateUnitProvider> providers = UpdateUnitProviderFactory.getDefault().getUpdateUnitProviders(true);
        String oldCodename = "org.netbeans.jeddict";
        for (UpdateUnitProvider p : providers) {
            JeddictInstaller.info("Silent Update Provider " + p.getName());
            if (p.getName().equals(oldCodename) || p.getName().equals(JeddictInstaller.CATEGORY)) { // this is our current plugin
                try {
                    final String displayName = "Checking for updates to Jeddict plugin...";
                    p.refresh(
                        ProgressHandleFactory.createHandle(displayName, () -> true),
                        true
                    );
                } catch (IOException ex) {
                    // caught a exception
                    JeddictInstaller.error("A problem caught while refreshing Update Centers, cause: " + ex.toString());
                }
                return p;
            }
        }
        return null;
    }

    static OperationContainer<InstallSupport> feedContainer(Collection<UpdateElement> updates, boolean update) {
        if (updates == null || updates.isEmpty()) {
            return null;
        }
        // create a container for update
        OperationContainer<InstallSupport> container;
        if (update) {
            container = OperationContainer.createForUpdate();
        } else {
            container = OperationContainer.createForInstall();
        }

        // loop all updates and add to container for update
        for (UpdateElement ue : updates) {
            if (container.canBeAdded(ue.getUpdateUnit(), ue) && JeddictInstaller.CATEGORY.equals(ue.getCategory())) {
                JeddictInstaller.info("Update to Jeddict plugin found: " + ue);
                OperationInfo<InstallSupport> operationInfo = container.add(ue);
                if (operationInfo == null) {
                    continue;
                }
                container.add(operationInfo.getRequiredElements());
                if (!operationInfo.getBrokenDependencies().isEmpty()) {
                    // have a problem => cannot continue
                    JeddictInstaller.info("There are broken dependencies => cannot continue, broken deps: " + operationInfo.getBrokenDependencies());
                    return null;
                }
            }
        }
        return container;
    }

    static boolean allLicensesApproved(OperationContainer<InstallSupport> container) {
        if (!container.listInvalid().isEmpty()) {
            return false;
        }
        for (OperationInfo<InstallSupport> info : container.listAll()) {
            String license = info.getUpdateElement().getLicence();
            if (!isLicenseApproved(license)) {
                return false;
            }
        }
        return true;
    }

    static Validator doDownload(InstallSupport support) throws OperationException {
        final String displayName = "Downloading new version of Jeddict plugin...";
        ProgressHandle downloadHandle = ProgressHandleFactory.createHandle(displayName, () -> true);
        return support.doDownload(downloadHandle, true);
    }

    static Installer doVerify(InstallSupport support, Validator validator) throws OperationException {
        final String displayName = "Validating Jeddict plugin...";
        ProgressHandle validateHandle = ProgressHandleFactory.createHandle(displayName, () -> true);
        Installer installer = support.doValidate(validator, validateHandle);
        return installer;
    }

    static Restarter doInstall(InstallSupport support, Installer installer) throws OperationException {
        final String displayName = "Installing Jeddict plugin...";
        ProgressHandle installHandle = ProgressHandleFactory.createHandle(displayName, () -> true);
        return support.doInstall(installer, installHandle);
    }

    private static boolean installNewModules() {
        String s = NbBundle.getBundle("org.netbeans.jeddict.update.version.Bundle").getString("UpdateHandler.NewModules");
        return Boolean.parseBoolean(s);
    }
}
