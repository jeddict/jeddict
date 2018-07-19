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
package io.github.jeddict.jcode.util;

import java.util.function.Predicate;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public final class StringHelper {

    //(\\s+) space
    //(?<=[a-z])(?=[A-Z]) => eclipseRCPExt -> eclipse / RCPExt
    //(?<=[A-Z])(?=[A-Z][a-z]) => eclipseRCPExt -> eclipse / RCP / Ext
    public final static String NATURAL_TEXT_SPLITTER = "(\\s+)|(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])";

    public static String firstLower(String string) {
        boolean makeFirstLower = string.length() < 2 || (!Character.isUpperCase(string.charAt(1)));
        return makeFirstLower ? string.substring(0, 1).toLowerCase() + string.substring(1) : string;
    }

    public static String firstUpper(String string) {
        return string.length() > 1 ? string.substring(0, 1).toUpperCase() + string.substring(1) : string.toUpperCase();
    }

    /**
     * Converts `string` to [start case]
     *
     * @param content
     * @return
     * @example
     *
     * startCase('--foo-bar--') => 'Foo Bar' startCase('fooBar') => 'Foo Bar'
     * startCase('__FOO_BAR__') => 'FOO BAR'
     */
    public static String startCase(String content) {
        StringBuilder result = new StringBuilder();
        content = content.replaceFirst("[^a-zA-Z0-9]+", EMPTY);
        for (String word : content.replaceAll("[^a-zA-Z0-9]", " ").split(NATURAL_TEXT_SPLITTER)) {
            result.append(firstUpper(word)).append(" ");
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }

    /**
     * Converts `string` to [snake case]
     *
     * @param content
     * @return
     * @example
     *
     * Foo Bar > 'foo_bar', fooBar > 'foo_bar', --FOO-BAR-- > 'foo_bar'
     */
    public static String snakeCase(String content) {
        StringBuilder result = new StringBuilder();
        content = content.replaceFirst("[^a-zA-Z0-9]+", EMPTY);
        for (String word : content.replaceAll("[^a-zA-Z0-9]", " ").split(NATURAL_TEXT_SPLITTER)) {
            result.append(word.toLowerCase()).append("_");
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }

    /**
     * Converts `string` to [kebab case]
     *
     * @param content
     * @return
     * @example
     *
     * 'Foo Bar > 'foo-bar', 'fooBar' > 'foo-bar', '__FOO_BAR__' > 'foo-bar'
     */
    public static String kebabCase(String content) {
        StringBuilder result = new StringBuilder();
        content = content.replaceFirst("[^a-zA-Z0-9]+", EMPTY);
        for (String word : content.replaceAll("[^a-zA-Z0-9]", " ").split(NATURAL_TEXT_SPLITTER)) {
            result.append(word.toLowerCase()).append("-");
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }

    /**
     * Removes leading and trailing whitespace or specified characters from
     * `string`.
     *
     * @param content
     * @param trimmer
     * @return
     * @example
     *
     * _.trim(' abc ', ' '); // => 'abc'
     *
     * _.trim('_abc_', '_'); // => 'abc'
     *
     */
    public static String trim(String content, char trimmer) {
        char value[] = content.toCharArray();
        int len = value.length;
        int st = 0;
        char[] val = value;
        /* avoid getfield opcode */

        while ((st < len) && (val[st] == trimmer)) {
            st++;
        }
        while ((st < len) && (val[len - 1] == trimmer)) {
            len--;
        }
        return ((st > 0) || (len < value.length)) ? content.substring(st, len) : content;
    }

    /**
     * Converts `string` to [camel case]
     *
     * @param content
     * @return
     * @example
     *
     * 'Foo Bar > 'fooBar', '--foo-bar--' > 'fooBar', '__FOO_BAR__ > 'fooBar'
     */
    public static String camelCase(String content) {
        StringBuilder result = new StringBuilder();
//        content = content.replaceFirst("[^a-zA-Z0-9]+", EMPTY);//issue job-history => jobhistory
        int i = 0;
        for (String word : content.replaceAll("[^a-zA-Z0-9]", " ").split(NATURAL_TEXT_SPLITTER)) {
            word = word.toLowerCase();
            if (i == 0) {
                result.append(word);
            } else {
                result.append(firstUpper(word));
            }
            i++;
        }
        return result.toString();
    }
    
    /**
     * @return the a derived methodName from variableName Eg nickname Nickname
     */
    public static String getMethodName(String name) {
        if (name.charAt(0) == '_') {
            char ch = Character.toUpperCase(name.charAt(1));
            return Character.toString(ch) + name.substring(2);
        } else {
            char ch = Character.toUpperCase(name.charAt(0));
            return Character.toString(ch) + name.substring(1);
        }
    }

    public static String removeBeanMethodPrefix(String methodName) {
        if (methodName.startsWith("get")) {  //NOI18N
            methodName = methodName.replaceFirst("get", EMPTY);
        }
        if (methodName.startsWith("set")) {  //NOI18N
            methodName = methodName.replaceFirst("set", EMPTY);
        }
        if (methodName.startsWith("is")) {  //NOI18N
            methodName = methodName.replaceFirst("is", EMPTY);
        }
        return methodName;
    }

    public static boolean isGetterMethod(String methodName) {
        return methodName.startsWith("get") || methodName.startsWith("is");
    }

    /**
     *
     * @param input
     * @return
     * @example
     *
     * BankAccount => Bank Account Bank_Account => Bank_Account
     */
    public static String toNatural(String input) {
        String natural = EMPTY;
        Character lastChar = null;
        for (Character curChar : input.toCharArray()) {
            if (lastChar == null) {
                // First character
                lastChar = Character.toUpperCase(curChar);
                natural = natural + lastChar;

            } else {
                if (Character.isLowerCase(lastChar)
                        && (Character.isUpperCase(curChar)) || Character.isDigit(curChar)) {
                    natural = natural + " " + curChar;
                } else {
                    natural = natural + curChar;
                }
                lastChar = curChar;
            }

        }
        return natural;
    }

    /**
     *
     * @param input
     * @return
     * @example
     *
     * BankAccount => BANK_ACCOUNT Bank_Account => BANK_ACCOUNT
     */
    public static String toConstant(String input) {
        String constant = EMPTY;
        Character lastChar = null;
        for (Character curChar : input.toCharArray()) {
            if (lastChar == null) {
                // First character
                lastChar = Character.toUpperCase(curChar);
                constant = constant + lastChar;

            } else {
                if (Character.isLowerCase(lastChar)
                        && (Character.isUpperCase(curChar) || Character.isDigit(curChar))) {
                    constant = constant + '_' + curChar;
                } else {
                    constant = constant + Character.toUpperCase(curChar);
                }
                lastChar = curChar;
            }

        }
        return constant;
    }
    
    public static String getNext(String name, Predicate<String> checkExist){
        return getNext(name, checkExist, false);
    }
    
    public static String getNext(String name, Predicate<String> checkExist, boolean increment){
        int index = 0;
        String nextName;
        if(increment){
            nextName = name + ++index;
        } else {
            nextName = name;
        }
        boolean isExist = true;
        while (isExist) {
            if (checkExist.test(nextName)) {
                isExist = true;
                nextName = name + ++index;
            } else {
                return nextName;
            }
        }
        return nextName;
    }

    public static String singularize(String name) {
        return Inflector.getInstance().singularize(name);
    }
    
    
    public static String pluralize(String name) {
        String pluralName = Inflector.getInstance().pluralize(name);

        if (name.equals(pluralName)) {
            return name + Constants.COLLECTION;
        } else {
            return pluralName;
        }
    }
    
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }
}
