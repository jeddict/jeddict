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

import java.util.HashMap;
import org.netbeans.jpa.modeler.rules.entity.EntityValidator;
import org.netbeans.modeler.widget.node.IWidget;
import org.netbeans.modeler.widget.node.WidgetStateHandler;
import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
public class ErrorHandler {

    private final IWidget widget;

    public ErrorHandler(IWidget widget) {
        this.widget = widget;
    }

    private final java.util.Map<String, String> errorList = new HashMap<>();

    public void throwError(String key) {
        errorList.put(key, NbBundle.getMessage(EntityValidator.class, key));
        printError();
    }

    public void clearError(String key) {
        errorList.remove(key);
        printError();
    }

    public void printError() {
        StringBuilder errorMessage = new StringBuilder();
        errorList.keySet().stream().forEach((errorKey) -> {
            errorMessage.append(errorList.get(errorKey));
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

//    private Image icon;
//    private Image errorIcon;

//    public void createErrorIcon(Image icon) {
//        this.icon = icon;
//        int iconWidth =  icon.getWidth(null) + 3;
//        int iconHeight =  icon.getHeight(null) + 3;
//        int errorIconWidth =  ERROR_ICON.getWidth(null);
//        int errorIconHeight =  ERROR_ICON.getHeight(null);
//
//        BufferedImage combined = new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_ARGB);
//        Graphics g = combined.getGraphics();
//        g.drawImage(icon, 0, 0, null);
//        g.drawImage(ERROR_ICON, iconWidth - errorIconWidth, iconHeight - errorIconHeight, null);
//        errorIcon = combined;
//    }
}
