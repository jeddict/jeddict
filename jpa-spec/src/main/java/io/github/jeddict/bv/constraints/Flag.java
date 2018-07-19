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
package io.github.jeddict.bv.constraints;

import javax.xml.bind.annotation.XmlEnum;
import org.netbeans.modeler.properties.type.Enumy;

/**
 *
 * @author jGauravGupta
 */
@XmlEnum
public enum Flag implements Enumy {

    /**
     * Enables Unix lines mode.
     *
     * @see java.util.regex.Pattern#UNIX_LINES
     */
    UNIX_LINES(java.util.regex.Pattern.UNIX_LINES),
    /**
     * Enables case-insensitive matching.
     *
     * @see java.util.regex.Pattern#CASE_INSENSITIVE
     */
    CASE_INSENSITIVE(java.util.regex.Pattern.CASE_INSENSITIVE),
    /**
     * Permits whitespace and comments in pattern.
     *
     * @see java.util.regex.Pattern#COMMENTS
     */
    COMMENTS(java.util.regex.Pattern.COMMENTS),
    /**
     * Enables multiline mode.
     *
     * @see java.util.regex.Pattern#MULTILINE
     */
    MULTILINE(java.util.regex.Pattern.MULTILINE),
    /**
     * Enables dotall mode.
     *
     * @see java.util.regex.Pattern#DOTALL
     */
    DOTALL(java.util.regex.Pattern.DOTALL),
    /**
     * Enables Unicode-aware case folding.
     *
     * @see java.util.regex.Pattern#UNICODE_CASE
     */
    UNICODE_CASE(java.util.regex.Pattern.UNICODE_CASE),
    /**
     * Enables canonical equivalence.
     *
     * @see java.util.regex.Pattern#CANON_EQ
     */
    CANON_EQ(java.util.regex.Pattern.CANON_EQ);

    //JDK flag value
    private final int value;

    private Flag(int value) {
        this.value = value;
    }

    /**
     * @return flag value as defined in {@link java.util.regex.Pattern}
     */
    public int getValue() {
        return value;
    }

    public static Flag fromValue(String v) {
        try {
            return valueOf(v);
        } catch (java.lang.IllegalArgumentException ex) {
            return null;
        }
    }

    @Override
    public String getDisplay() {
        return name();
    }

    @Override
    public Enumy getDefault() {
        return null;
    }

}
