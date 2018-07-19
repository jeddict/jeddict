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
package io.github.jeddict.jcode;

import io.github.jeddict.jcode.task.progress.ProgressHandler;

public abstract class AbstractGenerator {

    private ProgressHandler handler;
    private int totalWorkUnits;
    private int workUnits; 

    public AbstractGenerator() {
    }

    public abstract void initialize(ApplicationConfigData applicationConfigData, ProgressHandler progressHandler);
    public abstract void preGeneration();
    public abstract void generate();
    public abstract void postGeneration();

    protected void initProgressReporting(ProgressHandler handler) {
        initProgressReporting(handler, true);
    }

    protected void initProgressReporting(ProgressHandler handler, boolean start) {
        this.handler = handler;
        this.totalWorkUnits = getTotalWorkUnits();
        this.workUnits = 0;

        if (start) {
            if (totalWorkUnits > 0) {
                handler.start(totalWorkUnits);
            } else {
                handler.start();
            }
        }
    }

    protected void reportProgress(String message) {
        if (handler != null) {
            if (totalWorkUnits > 0) {
                handler.progress(message, ++workUnits);
            } else {
                handler.progress(message);
            }
        }
    }

    protected void finishProgressReporting() {
        if (handler != null) {
            handler.finish();
        }
    }

    protected int getTotalWorkUnits() {
        return 100;
    }

    protected ProgressHandler getProgressHandle() {
        return handler;
    }
}
