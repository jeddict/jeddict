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
package org.netbeans.jpa.modeler.network.social.linkedin;

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
public class LinkedInSocialNetwork {

    public static Icon LINKEDIN;
    private static final String INTENT = "https://www.linkedin.com/shareArticle?";
    private static String LINK;
    private static LinkedInSocialNetwork instance;

    private LinkedInSocialNetwork() {
        if (LINK == null) {
            try {
                LINK = new StringBuilder(INTENT)
                        .append("url=").append(URLEncoder.encode("http://jpamodeler.blogspot.in", "UTF-8"))
                        .append("&title=").append(URLEncoder.encode("JPA Modeler - your jpa assistant", "UTF-8"))
                        .append("&summary=").append(URLEncoder.encode("JPA Modeler assists to create, design and edit java persistence application business model and DB visually as graphical diagram. It automates JPA code generation, enable to import database into diagram or modify database from diagram visually and also supports existing JPA Classes Reverse Engineering. It is open-source and free. Check it out at http://jpamodeler.blogspot.in", "UTF-8"))
                        .append("&source=").append(URLEncoder.encode("http://jpamodeler.blogspot.in", "UTF-8")).toString();
            } catch (UnsupportedEncodingException ex) {
                ExceptionUtils.printStackTrace(ex);
            }
            ClassLoader cl = LinkedInSocialNetwork.class.getClassLoader();
            LINKEDIN = new ImageIcon(cl.getResource("org/netbeans/jpa/modeler/collaborate/resource/image/socialnetwork/linkedin.png"));
        }
    }

    public static LinkedInSocialNetwork getInstance() {
        if (instance == null) {
            synchronized (LinkedInSocialNetwork.class) {
                if (instance == null) {
                    instance = new LinkedInSocialNetwork();
                }
            }
        }
        return instance;
    }

    public JMenuItem getComponent() {
        JMenuItem twitterShare = new JMenuItem("Linked In", LINKEDIN);
        twitterShare.addActionListener((ActionEvent e) -> {
            SharingHelper.openWebpage(LINK);
        });
        return twitterShare;
    }

}
