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

import org.netbeans.orm.converter.util.ORMConverterUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GeneratedValueSnippet implements Snippet {

    public static final String AUTO = "GenerationType.AUTO";
    public static final String IDENTITY = "GenerationType.IDENTITY";
    public static final String SEQUENCE = "GenerationType.SEQUENCE";
    public static final String TABLE = "GenerationType.TABLE";

    private static final List<String> STRATEGY_TYPES = getStrategyTypes();

    private String generator = null;
    private String strategy = null;

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String startegy) {

        if (!STRATEGY_TYPES.contains(startegy)) {
            throw new IllegalArgumentException("Given type :" + startegy
                    + "Valid strategy types :" + STRATEGY_TYPES);
        }

        this.strategy = startegy;
    }

    public String getSnippet() throws InvalidDataException {

        if (generator == null && strategy == null) {
            return "@GeneratedValue";
        }

        StringBuilder builder = new StringBuilder();

        builder.append("@GeneratedValue(");

        if (generator != null) {
            builder.append("generator=\"");
            builder.append(generator);
            builder.append(ORMConverterUtil.QUOTE);
            builder.append(ORMConverterUtil.COMMA);
        }

        if (strategy != null) {
            builder.append("strategy=");
            builder.append(strategy);
            builder.append(ORMConverterUtil.COMMA);
        }

        return builder.substring(0, builder.length() - 1)
                + ORMConverterUtil.CLOSE_PARANTHESES;
    }

    public Collection<String> getImportSnippets() throws InvalidDataException {
        List<String> importSnippets = new ArrayList<String>();

        importSnippets.add("javax.persistence.GenerationType");
        importSnippets.add("javax.persistence.GeneratedValue");

        return importSnippets;
    }

    private static List<String> getStrategyTypes() {
        List<String> strategyTypes = new ArrayList<String>();

        strategyTypes.add(AUTO);
        strategyTypes.add(IDENTITY);
        strategyTypes.add(SEQUENCE);
        strategyTypes.add(TABLE);

        return strategyTypes;
    }
}
