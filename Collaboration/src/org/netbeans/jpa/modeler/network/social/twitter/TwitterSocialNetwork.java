/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.jpa.modeler.network.social.twitter;

import java.awt.event.ActionEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import org.netbeans.jpa.modeler.collaborate.issues.ExceptionUtils;
import org.netbeans.jpa.modeler.network.social.SharingHelper;

/**
 *
 * @author Gaurav Gupta
 */
public class TwitterSocialNetwork {

    private static final String INTENT = "https://twitter.com/intent/tweet?source=webclient&text=";
    private static final String MESSAGE = "Check out #JPAModeler, to generate & reverse engineering #JPA code and visualize(modify) the DB and ER diagram (http://jpamodeler.blogspot.in)";
    private static String LINK;
    public static Icon TWITTER;

    private static TwitterSocialNetwork instance;

    private TwitterSocialNetwork() {
        if (LINK == null) {
            try {
                LINK = INTENT + URLEncoder.encode(MESSAGE, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                ExceptionUtils.printStackTrace(ex);
            }
            ClassLoader cl = TwitterSocialNetwork.class.getClassLoader();
            TWITTER = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/collaborate/resource/image/socialnetwork/twitter.png"));
        }
    }

    public static TwitterSocialNetwork getInstance() {
        if (instance == null) {
            synchronized (TwitterSocialNetwork.class) {
                if (instance == null) {
                    instance = new TwitterSocialNetwork();
                }
            }
        }
        return instance;
    }

    public JMenuItem getComponent() {
        JMenuItem twitterShare = new JMenuItem("Twitter", TWITTER);
        twitterShare.addActionListener((ActionEvent e) -> {
            SharingHelper.openWebpage(LINK);
        });
        return twitterShare;
    }

}
