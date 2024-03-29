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
package io.github.jeddict.collaborate.enhancement;

import java.awt.event.ActionEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import io.github.jeddict.collaborate.issues.ExceptionUtils;
import io.github.jeddict.network.social.SharingHelper;

/**
 *
 * @author Gaurav Gupta
 */
public class EnhancementRequestHandler {

    public static Icon ENHANCEMENT_ICON;
    private static EnhancementRequestHandler instance;

    private EnhancementRequestHandler() {
        ClassLoader cl = EnhancementRequestHandler.class.getClassLoader();
        ENHANCEMENT_ICON = new ImageIcon(cl.getResource("io/github/jeddict/collaborate/resource/image/ENHANCEMENT_ICON.png"));
    }

    public static EnhancementRequestHandler getInstance() {
        if (instance == null) {
            synchronized (EnhancementRequestHandler.class) {
                if (instance == null) {
                    instance = new EnhancementRequestHandler();
                }
            }
        }
        return instance;
    }

    public JMenuItem getComponent() {
        JMenuItem twitterShare = new JMenuItem("Enhancement/Bugs ?", ENHANCEMENT_ICON);
        twitterShare.addActionListener((ActionEvent e) -> {
            SharingHelper.openWebpage(ExceptionUtils.ISSUES_URL);
        });
        return twitterShare;
    }

}
