/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.core.widget;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.rules.entity.EntityValidator;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.modeler.widget.node.WidgetStateHandler;
import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
public class ErrorHandler {

    private final IFlowElementWidget widget;
    private ResourceBundleManager bundleManager = new ResourceBundleManager();

    public ErrorHandler(IFlowElementWidget widget) {
        this.widget = widget;
    }

    private final Map<String, String> errorList = new HashMap<>();

    public void throwError(String key) {
//        if (widget instanceof IFlowElementWidget) {
//            errorList.put(key, ResourceBundleManager.get(key, ((IFlowElementWidget) widget).getName()));
//            printError();
//        } else {
//            errorList.put(key, ResourceBundleManager.get(key));
//            printError();
//        }
        errorList.put(key, bundleManager.get(key, widget.getName()));
        printError();
    }

    public void clearError(String key) {
        errorList.remove(key);
        printError();
    }

    public Map<String, String> getErrorList() {
        return errorList;
    }

    public void printError() {
        StringBuilder errorMessage = new StringBuilder();
        errorList.keySet().stream().forEach((errorKey) -> {
            errorMessage.append(errorList.get(errorKey)).append(". ").append('\n');
        });
        if (errorMessage.length() != 0) {
            widget.setToolTipText(errorMessage.toString());
            if (widget instanceof WidgetStateHandler) {
                ((WidgetStateHandler) widget).setErrorState(true);
            }
        } else {
            widget.setToolTipText(null);
            if (widget instanceof WidgetStateHandler) {
                ((WidgetStateHandler) widget).setErrorState(false);
            }
        }
    }

    private class ResourceBundleManager {

        private final Map<String, String> ERRORS = new HashMap<>();
        private final Class[] VALIDATORS = {EntityValidator.class, AttributeValidator.class};

        private String get(String key, Object... param) {
            String value = ERRORS.get(key);
            if (value != null) {
                return value;
            }
            for (Class validator : VALIDATORS) {
                try {
                    value = NbBundle.getMessage(validator, key, param);
                    ERRORS.put(key, value);
                    return value;
                } catch (MissingResourceException resourceException) {
                    //Ignore
                }
            }
            throw new MissingResourceException(key + " not found", Arrays.toString(VALIDATORS), key);
        }

    }
}
