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
package io.github.jeddict.jcode.console;

import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
public enum Console {

    NORMAL("0"), 
    
    BOLD("1"), UNDERLINE("4"), BLINK("5"), INVERSE("7"),
    
    FG_RED("31;1"),
    FG_GREEN("32;1"),
    FG_YELLOW("33;1"),
    FG_BLUE("34;1"),
    FG_MAGENTA("35;1"),
    FG_CYAN("36;1"),
    
    FG_GREY("30;1"),
    FG_LIGHT_GREY("37"),
    FG_BLACK("30"),
    
    FG_DARK_RED("31"),
    FG_DARK_GREEN("32"),
    FG_DARK_YELLOW("33"),
    FG_DARK_BLUE("34"),
    FG_DARK_MAGENTA("35"),
    FG_DARK_CYAN("36"),
    
    BG_RED("41;1"),
    BG_GREEN("42;1"),
    BG_YELLOW("43;1"),
    BG_BLUE("44;1"),
    BG_MAGENTA("45;1"),
    BG_CYAN("46;1"),
    BG_GREY("47;1");

    private final String code;

    private Console(String code) {
        this.code = code;
    }

    private final static String END = "\u001B[0m";
    private final static String TAG_START = "\u001B[";
    private final static String TAG_END = "m";

    public static String wrap(String text, Console... consoles) {
        StringBuilder sb = new StringBuilder(TAG_START);
        if (consoles != null) {

            for (int i = 0; i < consoles.length; i++) {
                Console console = consoles[i];
                sb.append(console.code);
                if (i < consoles.length - 1) {
                    sb.append(';');
                }
            }

        } else {
            sb.append(NORMAL.code);
        }
        sb.append(TAG_END).append(text).append(END);
        return sb.toString();
    }

    public static String wrap(Class _class, String text, Console... consoles) {
        return wrap(NbBundle.getMessage(_class, text), consoles);
    }

    public static String wrap(Class _class, String text, String param, Console... consoles) {
        return wrap(NbBundle.getMessage(_class, text, param), consoles);
    }

}
