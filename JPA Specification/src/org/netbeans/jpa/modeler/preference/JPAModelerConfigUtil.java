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
package org.netbeans.jpa.modeler.preference;

import org.openide.util.NbPreferences;

public class JPAModelerConfigUtil {

    private Boolean JOIN_COLUMN_AUTOGEN_NOTIFICATION;

    /**
     * @return the JOIN_COLUMN_AUTOGEN_NOTIFICATION
     */
    public boolean isJoinColumnAutoGenNotification() {
        if (JOIN_COLUMN_AUTOGEN_NOTIFICATION == null) {
            JOIN_COLUMN_AUTOGEN_NOTIFICATION = NbPreferences.forModule(JPAModelerConfigUtil.class).getBoolean("JOIN_COLUMN_AUTOGEN_NOTIFICATION", true);
        }
        return JOIN_COLUMN_AUTOGEN_NOTIFICATION;
    }

    /**
     * @param JOIN_COLUMN_AUTOGEN_NOTIFICATION the
     * JOIN_COLUMN_AUTOGEN_NOTIFICATION to set
     */
    public void setJoinColumnAutoGenNotification(boolean JOIN_COLUMN_AUTOGEN_NOTIFICATION) {
        this.JOIN_COLUMN_AUTOGEN_NOTIFICATION = JOIN_COLUMN_AUTOGEN_NOTIFICATION;
        NbPreferences.forModule(JPAModelerConfigUtil.class).putBoolean("JOIN_COLUMN_AUTOGEN_NOTIFICATION", JOIN_COLUMN_AUTOGEN_NOTIFICATION);
    }

}
