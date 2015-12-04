/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.orm.converter.compiler;

public class CallbackSnippet {

    public static enum CallbackType {

        PrePersist,
        PostPersist,
        PreRemove,
        PostRemove,
        PreUpdate,
        PostUpdate,
        PostLoad
    }

    private CallbackType callbackType = null;
    private String methodName = null;

    public CallbackSnippet() {
        super();
    }

    public CallbackSnippet(CallbackType callbackType, String methodName) {
        this.callbackType = callbackType;
        this.methodName = methodName;
    }

    public CallbackType getCallbackType() {
        return callbackType;
    }

    public void setCallbackType(CallbackType callbackType) {
        this.callbackType = callbackType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof CallbackSnippet)) {
            return false;
        }

        CallbackSnippet callback = (CallbackSnippet) object;

        if (callback.getCallbackType().equals(callbackType)
                && callback.getMethodName().equals(methodName)) {

            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (callbackType != null ? callbackType.hashCode() : 0);
        hash = 37 * hash + (methodName != null ? methodName.hashCode() : 0);
        return hash;
    }
}
