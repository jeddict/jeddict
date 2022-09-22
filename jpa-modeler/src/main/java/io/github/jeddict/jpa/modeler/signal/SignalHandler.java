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
package io.github.jeddict.jpa.modeler.signal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import org.netbeans.api.visual.widget.ImageWidget;
import io.github.jeddict.jpa.modeler.rules.attribute.AttributeValidator;
import io.github.jeddict.jpa.modeler.rules.entity.ClassValidator;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.modeler.widget.node.INodeWidget;
import org.netbeans.modeler.widget.node.IPNodeWidget;
import org.netbeans.modeler.widget.node.IWidgetStateHandler;
import org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType;
import org.netbeans.modeler.widget.pin.IPinWidget;
import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
public class SignalHandler {

    private final IFlowElementWidget widget;
    private final ResourceBundleManager bundleManager = new ResourceBundleManager();
    private final StateType stateType;
    private final SignalManager manager;

    public SignalHandler(IFlowElementWidget widget, StateType stateType, SignalManager manager) {
        this.stateType = stateType;
        this.widget = widget;
        this.manager= manager;
    }

    private final Map<String, String> signalList = new HashMap<>();

    public void fire(String key, Object... param) {
        signalList.put(key, bundleManager.get(key, param));
        print();
    }
    
    public void fire(String key) {
        signalList.put(key, bundleManager.get(key, widget.getName()));
        print();
    }

    public void clear(String key) {
        signalList.remove(key);
        print();
    }

    public Map<String, String> getSignalList() {
        return signalList;
    }

    public void print() {
        StringBuilder message = new StringBuilder();
        signalList.keySet().forEach((signalKey) -> {
            message.append(signalList.get(signalKey)).append(". ").append('\n');
        });
        IWidgetStateHandler handler = widget instanceof INodeWidget ? ((IPNodeWidget) widget).getWidgetStateHandler() : (widget instanceof IPinWidget ? ((IPinWidget) widget).getWidgetStateHandler() : null);
        ImageWidget imageWidget = widget instanceof INodeWidget ? ((IPNodeWidget) widget).getImageWidget() : (widget instanceof IPinWidget ? ((IPinWidget) widget).getImageWidget() : null);

        if (message.length() != 0) {
            if (imageWidget != null) {
                imageWidget.setToolTipText(message.toString());
            }
            if (handler != null) {
                handler.applyState(getStateType());
            }
        } else {
                if (imageWidget != null) {
                    imageWidget.setToolTipText(null);
                }
                if (handler != null) {
                    handler.clearState(getStateType());
                }
            manager.signalNext();
        }
    }

    /**
     * @return the stateType
     */
    public StateType getStateType() {
        return stateType;
    }

    private class ResourceBundleManager {

        private final Map<String, String> ERRORS = new HashMap<>();
        private final Class[] VALIDATORS = {ClassValidator.class, AttributeValidator.class};

        private String get(String key, Object... param) {
            String value = ERRORS.get(key);
            if (value != null && param.length == 0) {
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
