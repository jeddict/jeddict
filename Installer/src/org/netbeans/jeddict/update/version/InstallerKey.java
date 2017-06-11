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
package org.netbeans.jeddict.update.version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import org.openide.*;
import org.openide.util.NbPreferences;

public final class InstallerKey {
    
    public static String getApiKey() {
        String apiKey = NbPreferences.forModule(JeddictInstaller.class).get("API Key", "");
        if (apiKey.equals("")) {
            apiKey = getApiKeyFromConfigFile();
        }
        return apiKey;
    }
    
    public static String getApiKeyFromConfigFile() {
        String apiKey = "";
        File userHome = new File(System.getProperty("user.home"));
        File configFile = new File(userHome, JeddictInstaller.CONFIG);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(configFile.getAbsolutePath()));
        } catch (FileNotFoundException e1) {}
        if (br != null) {
            try {
                String line = br.readLine();
                while (line != null) {
                    String[] parts = line.split("=");
                    if (parts.length == 2 && parts[0].trim().equals("api_key")) {
                        apiKey = parts[1].trim();
                    }
                    line = br.readLine();
                }
            } catch (IOException e) {
                JeddictInstaller.error(e.toString());
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    JeddictInstaller.error(e.toString());
                    e.printStackTrace();
                }
            }
        }
        return apiKey;
    }
    
    public static void saveApiKey(String apiKey) {
        NbPreferences.forModule(JeddictInstaller.class).put("API Key", apiKey);
        saveApiKeyToConfigFile(apiKey);
    }
    
    public static void saveApiKeyToConfigFile(String apiKey) {
        File userHome = new File(System.getProperty("user.home"));
        File configFile = new File(userHome, JeddictInstaller.CONFIG);
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        try {
            br = new BufferedReader(new FileReader(configFile.getAbsolutePath()));
        } catch (FileNotFoundException e1) {
        }
        if (br != null) {
            try {
                String line = br.readLine();
                while (line != null) {
                    String[] parts = line.split("=");
                    if (parts.length == 2 && parts[0].trim().equals("api_key")) {
                        found = true;
                        sb.append("api_key = " + apiKey + "\n");
                    } else {
                        sb.append(line + "\n");
                    }
                    line = br.readLine();
                }
            } catch (IOException e) {
                JeddictInstaller.error(e.toString());
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    JeddictInstaller.error(e.toString());
                    e.printStackTrace();
                }
            }
        }
        if (!found) {
            sb = new StringBuilder();
            sb.append("[settings]\n");
            sb.append("api_key = " + apiKey + "\n");
            sb.append("debug = false\n");
        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(configFile.getAbsolutePath(), "UTF-8");
        } catch (FileNotFoundException e) {
            JeddictInstaller.error(e.toString());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            JeddictInstaller.error(e.toString());
            e.printStackTrace();
        }
        if (writer != null) {
            writer.print(sb.toString());
            writer.close();
        }
    }
    
    public static String promptForApiKey() {
        String apiKey = getApiKey();
        NotifyDescriptor.InputLine question = new NotifyDescriptor.InputLine(
            "API Key:",
                "Enter your JPAModelerInstaller API Key",
                NotifyDescriptor.OK_CANCEL_OPTION,
            NotifyDescriptor.QUESTION_MESSAGE
        );
        question.setInputText(apiKey);
 
        if (DialogDisplayer.getDefault().notify(question) == NotifyDescriptor.OK_OPTION) {
            apiKey = question.getInputText();
            if (InstallerKey.isValidApiKey(apiKey)) {
                return apiKey;
            }
        }
        
        return null;
    }
    
    public static boolean isValidApiKey(String apiKey) {
        try {
            UUID.fromString(apiKey);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
