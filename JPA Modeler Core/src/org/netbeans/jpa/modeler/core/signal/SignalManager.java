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
package org.netbeans.jpa.modeler.core.signal;

import java.util.Arrays;
import static java.util.Comparator.comparingInt;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.modeler.specification.model.document.widget.IFlowElementWidget;
import org.netbeans.modeler.widget.node.IWidgetStateHandler;
import org.netbeans.modeler.widget.node.IWidgetStateHandler.StateType;

/**
 *
 * @author jGauravGupta
 */
public final class SignalManager {

    private final Map<IWidgetStateHandler.StateType, SignalHandler> handler = new HashMap<>();
    private final IFlowElementWidget widget;

    public SignalManager(IFlowElementWidget widget) {
        this.widget = widget;
        Arrays.stream(StateType.values()).forEach(stateType -> createSignalHandler(stateType));
    }

    void createSignalHandler(IWidgetStateHandler.StateType stateType) {
        SignalHandler signalHandler = new SignalHandler(widget, stateType, this);
        handler.put(stateType, signalHandler);
    }

    public void fire(IWidgetStateHandler.StateType stateType, String key, Object... param) {
        handler.get(stateType).fire(key, param);
    }

    public void fire(IWidgetStateHandler.StateType stateType, String key) {
        handler.get(stateType).fire(key);
    }

    public void clear(IWidgetStateHandler.StateType stateType, String key) {
        handler.get(stateType).clear(key);
    }

    public Map<String, String> getSignalList(IWidgetStateHandler.StateType stateType) {
        return handler.get(stateType).getSignalList();
    }
    
    public void signalNext(){
        handler.values().stream()
               .filter(sh -> !sh.getSignalList().isEmpty())
               .sorted(comparingInt(sh -> sh.getStateType().getPriority()))
               .findFirst().ifPresent(SignalHandler::print);
    }

}
