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
import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.jpa.modeler.rules.attribute.AttributeValidator;
import org.netbeans.jpa.modeler.rules.entity.EntityValidator;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.openide.util.NbBundle;
import org.netbeans.modeler.widget.node.IWidgetStateHandler;
import org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType;
import org.netbeans.modeler.widget.pin.IPinWidget;

/**
 *
 * @author Gaurav Gupta
 */
public class SignalHandler {

    private final IFlowElementWidget widget;
    private final ResourceBundleManager bundleManager = new ResourceBundleManager();
    private final StateType stateType;

    public SignalHandler(IFlowElementWidget widget, StateType stateType) {
        this.stateType = stateType;
        this.widget = widget;
    }

    private final Map<String, String> signalList = new HashMap<>();

    public void throwSignal(String key) {
        signalList.put(key, bundleManager.get(key, widget.getName()));
        print();
    }

    public void clearSignal(String key) {
        signalList.remove(key);
        print();
    }

    public Map<String, String> getSignalList() {
        return signalList;
    }

    public void print() {
        StringBuilder message = new StringBuilder();
        signalList.keySet().stream().forEach((signalKey) -> {
            message.append(signalList.get(signalKey)).append(". ").append('\n');
        });
        IWidgetStateHandler handler = widget instanceof INodeWidget ? ((IPNodeWidget) widget).getWidgetStateHandler() : (widget instanceof IPinWidget ? ((IPinWidget) widget).getWidgetStateHandler() : null);
        ImageWidget imageWidget = widget instanceof INodeWidget ? ((IPNodeWidget) widget).getImageWidget() : (widget instanceof IPinWidget ? ((IPinWidget) widget).getImageWidget() : null);

        if (message.length() != 0) {
            if (imageWidget != null) {
                imageWidget.setToolTipText(message.toString());
            }
            if (handler != null) {
                handler.applyState(stateType);
            }
        } else {
            if (imageWidget != null) {
                imageWidget.setToolTipText(null);
            }
            if (handler != null) {
                handler.clearState(stateType);
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
