/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jcode.task.progress;

import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
public interface ProgressHandler {

    void start();

    void start(int step);

    void progress(String message);
    
    void progress(FileObject fileObject);

    void progress(String message, int step);

    void append(String message);
    
    void error(String title, String message);
    
    void warning(String title, String message);
    
    void info(String title, String message);
    
    void help(String title, String message);
    
    void error(String title, String message, Project project);
    
    void warning(String title, String message, Project project);
    
    void info(String title, String message, Project project);
    
    void help(String title, String message, Project project);

    void finish();
    
    void addDynamicVariable(String key, Object value);
    
    void removeDynamicVariable(String key);

}
