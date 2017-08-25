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


import org.netbeans.jcode.layer.TechContext;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import static org.netbeans.modeler.analytics.ILogger.*;

/**
 * Insight program to collect usage data event for developer experience
 * improvement. It does not intend to collect information that can uniquely
 * identify developer as an individual.
 *
 * @author jGauravGupta
 */
public class JeddictLogger {

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


    public static void logGenerateEvent(ApplicationConfigData applicationConfigData) {
        logEvent(GENERATE_CATEGORY, DOMAIN, JPA);
        logEvent(GENERATE_CATEGORY, DOMAIN, JPA_CLASS_COUNT, applicationConfigData.getEntityMappings().getJavaClass().size());
        logSourceGenerationEvent(applicationConfigData.getBussinesTechContext(), applicationConfigData.isCompleteApplication());
        logSourceGenerationEvent(applicationConfigData.getControllerTechContext(), applicationConfigData.isCompleteApplication());
        logSourceGenerationEvent(applicationConfigData.getViewerTechContext(), applicationConfigData.isCompleteApplication());
    }

    private static void logSourceGenerationEvent(TechContext context, boolean completeApplication) {
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
                    .filter(tc -> completeApplication?true:tc.getTechnology().entityGenerator())
                    .forEach(childContext -> logSourceGenerationEvent(childContext, completeApplication));
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
