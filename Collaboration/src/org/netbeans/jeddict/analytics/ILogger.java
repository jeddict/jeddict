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
package org.netbeans.jeddict.analytics;


import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jcode.layer.TechContext;
import com.brsanthu.googleanalytics.EventHit;
import com.brsanthu.googleanalytics.GoogleAnalytics;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.openide.util.NbBundle;

/**
 * Insight program to collect usage data event for developer experience
 * improvement. It does not intend to collect information that can uniquely
 * identify developer as an individual.
 *
 * @author jGauravGupta
 */
public class ILogger {

    private static GoogleAnalytics ANALYTICS;
    private static boolean ENABLE = false;

    private final static String GENERATE_CATEGORY = "SOURCE_GENERATED";
    private final static String DOMAIN = "Domain";
    private final static String ACTION = "Action";
    private final static String WORKSPACE = "WorkSpace";
    private final static String JPA_MODELER = "JPA Modeler";
    private final static String JPA = "JPA";
    private final static String DB = "DB";
    private final static String JPA_CLASS_COUNT = "JPA Class Count";
    private final static String OPEN_MODELER_FILE = "Open Modeler File";
    private final static String CREATE_MODELER_FILE = "Create Modeler File";
    private final static String WORKSPACE_ITEM_COUNT = "ItemCount";

    private final static String CREATE = "Create";
    private final static String DELETE = "Delete";
    private final static String DELETE_ALL = "DeleteAll";
    private final static String UPDATE = "Update";
    private final static String OPEN = "Open";

    static {
        if (ENABLE = Boolean.valueOf(NbBundle.getMessage(ILogger.class, "TRACKING_ENABLE"))) {
            ANALYTICS = new GoogleAnalytics(NbBundle.getMessage(ILogger.class, "TRACKING_ID"));
        }
    }

    private static final TemporalMap<String, Integer> data = new TemporalMap<String, Integer>(5, TimeUnit.MINUTES,
            (key, value) -> {
                String token[] = key.split("#");
                String category = token[0], action = token[1], label = token[2];
                EventHit eventHit = new EventHit(category, action, label, value);
                System.out.println("ANALYTICS category[" + category + "] action[" + action + "] label[" + label + "] value[" + value + "]");
                ANALYTICS.postAsync(eventHit);
            });

    public static void logEvent(String category, String action) {
        logEvent(category, action, null, 1);
    }

    public static void logEvent(String category, String action, String label) {
        logEvent(category, action, label, 1);
    }

    public static void logEvent(String category, String action, String label, int value) {
        if (ENABLE && StringUtils.isNotBlank(category) && StringUtils.isNotBlank(action) && StringUtils.isNotBlank(label)) {
            String key = category+"#"+action+"#"+label;
            Integer existingValue = data.get(key);
            if (existingValue != null) {
                data.update(key, value + existingValue);
            } else {
                data.save(key, value);
            }
        }
    }

    public static void logEvent(ApplicationConfigData applicationConfigData) {
        logEvent(GENERATE_CATEGORY, DOMAIN, JPA);
        logEvent(GENERATE_CATEGORY, DOMAIN, JPA_CLASS_COUNT, applicationConfigData.getEntityMappings().getJavaClass().size());
        logSourceGenerationEvent(applicationConfigData.getBussinesTechContext());
        logSourceGenerationEvent(applicationConfigData.getControllerTechContext());
        logSourceGenerationEvent(applicationConfigData.getViewerTechContext());
    }

    private static void logSourceGenerationEvent(TechContext context) {
        if (context != null) {
            logEvent(GENERATE_CATEGORY,
                    context.getTechnology().type().getDisplayLabel(),
                    context.getTechnology().label());
            context.getPanel().getConfigData().getUsageDetails()
                    .stream()
                    .forEach(data -> logEvent(GENERATE_CATEGORY,
                    context.getTechnology().type().getDisplayLabel(), (String) data));
            context.getSiblingTechContext()
                    .stream()
                    .forEach(childContext -> logSourceGenerationEvent(childContext));
        }
    }

    public static void createModelerFile(String type) {
        logEvent(ACTION, CREATE_MODELER_FILE, type);
    }

    public static void openModelerFile(String type) {
        logEvent(ACTION, OPEN_MODELER_FILE, type );
    }

    public static void createWorkSpace() {
        logEvent(ACTION, WORKSPACE, WORKSPACE+"-"+CREATE);
    }

    public static void deleteWorkSpace() {
        logEvent(ACTION, WORKSPACE, WORKSPACE+"-"+DELETE);
    }

    public static void deleteAllWorkSpace() {
        logEvent(ACTION, WORKSPACE, WORKSPACE+"-"+DELETE_ALL);
    }

    public static void openWorkSpace(int count) {
        logEvent(ACTION, WORKSPACE, WORKSPACE+"-"+OPEN);
        logEvent(ACTION, WORKSPACE, WORKSPACE+"-"+WORKSPACE_ITEM_COUNT, count);
    }

    public static void updateWorkSpace() {
        logEvent(ACTION, WORKSPACE, WORKSPACE+"-"+UPDATE);
    }

    public static void recordDBAction(String type) {
        logEvent(ACTION, DB, type);
    }

    public static void recordJPACreateAction(String type) {
        logEvent(JPA_MODELER, type, type+"-"+CREATE);
    }

    public static void recordAction(String type) {
        logEvent(ACTION, "Util", type);
    }

}
