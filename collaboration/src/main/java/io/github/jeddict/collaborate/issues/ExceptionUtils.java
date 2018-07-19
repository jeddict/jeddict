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
package io.github.jeddict.collaborate.issues;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.modeler.core.IExceptionHandler;
import org.netbeans.modeler.core.ModelerFile;

/**
 * Useful utility and methods to work with exceptions. Allows to annotate
 * exceptions with messages, extract such messages and provides a common utility
 * method to report an exception.
 *
 *
 * @since 1.4
 */
public final class ExceptionUtils implements IExceptionHandler {

    public static final String ISSUES_URL = "https://github.com/jeddict/jeddict/issues/new";

    static final Logger LOG = Logger.getLogger(ExceptionUtils.class.getName());

    /**
     * Notifies an exception with a severe level. Such exception is going to be
     * printed to log file and possibly also notified to alarm the user somehow.
     *
     * @param t the exception to notify
     */
    public static void printStackTrace(Throwable t) {
        printStackTrace(t, null);
    }

    public static void printStackTrace(Throwable t, ModelerFile file) {
        printStackTrace(null, t, file);
    }

    public static void printStackTrace(String errorMessage, final Throwable t, final ModelerFile file) {
        t.printStackTrace();
        if (StringUtils.isBlank(errorMessage)) {
            errorMessage = t.getMessage();

            if (StringUtils.isBlank(errorMessage)) {
                if (t.getCause() != null && StringUtils.isNotBlank(t.getCause().getMessage())) {
                    errorMessage = t.getCause().getMessage();
                } else if (t.getStackTrace().length > 0) {
                    errorMessage = t.getStackTrace()[0].toString();
                }
            }
        }
        final String message = errorMessage;
        LOG.log(Level.ALL, errorMessage, t);
        String content = file!=null?file.getContent():"";
        SwingUtilities.invokeLater(() -> {
            ExceptionReporterPanel exceptionReporterPanel = new ExceptionReporterPanel(message, t, content);
            exceptionReporterPanel.setVisible(true);
        });
    }

    @Override
    public void handle(Throwable throwable, ModelerFile modelerFile) {
        printStackTrace(throwable, modelerFile);
    }
}
