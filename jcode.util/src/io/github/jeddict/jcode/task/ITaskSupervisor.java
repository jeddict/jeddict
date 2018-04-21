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
package io.github.jeddict.jcode.task;

/**
 *
 * @author Gaurav Gupta
 */
public interface ITaskSupervisor {

    // message output levels
    public final static int SUMMARY = 0; // very high level messages
    public final static int TERSE = 1; // somewhat detailed messages
    public final static int VERBOSE = 2; // very detailed messages
    public final static int DEBUG = 3; // debug level information only

    public boolean start(int itemTotal);

    public boolean start(int contributor, int itemTotal);

    /**
     * Increment the item counter by one.
     *
     * @return the current count after incrementing by one
     */
    public int increment();

    /**
     * Increment the item counter by the amount of the step parameter.
     *
     * @param step the amount to increment the counter.
     *
     * @return the current count after incrementing by the amount of the step
     * parameter.
     */
    public int increment(int step);

    /**
     * Called by task subclass to check confirm that the task hasn't been
     * cancelled or failed. If there is a cancellation or failure, finish() is
     * called.
     *
     * @return true if the process hasn't failed or been canceled
     */
    public boolean proceed();

    /**
     * Called by task subclass to check confirm that the task hasn't been
     * cancelled or failed. If there is a cancellation or failure, finish() is
     * called.
     *
     * @param step the amount to increment the counter by.
     *
     * @return true if the process hasn't failed or been canceled
     */
    public boolean proceed(int step);

    /**
     * Outputs a blank line
     */
    public void log();

    public void log(int level);

    /**
     * Outputs a message with and appends newline by default
     *
     * @param msg the message to be output
     */
    public void log(String msg);

    public void log(int level, String msg);

    /**
     * Outputs a message
     *
     * @param msg the message to be output
     * @param newline if true, appends newline
     */
    public void log(String msg, boolean newline);
    
    public void log(String msg, int padding);

    public void log(int level, String msg, boolean newline);

    /**
     * Call this method when a the task is instructed to be canceled and it will
     * set the cancelled flag to false. The next time proceed() is called, it
     * will invoke finish() return false.
     */
    public boolean cancel();

    /**
     * Call this method when a failure in your task is detected and it will set
     * the success flag to false. The next time proceed() is called, it will
     * invoke finish() return false.
     */
    public void fail();

}
