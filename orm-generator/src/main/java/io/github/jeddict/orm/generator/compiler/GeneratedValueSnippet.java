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
package io.github.jeddict.orm.generator.compiler;

import static io.github.jeddict.jcode.JPAConstants.GENERATED_VALUE;
import static io.github.jeddict.jcode.JPAConstants.GENERATED_VALUE_FQN;
import static io.github.jeddict.jcode.JPAConstants.GENERATION_TYPE_FQN;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.AT;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.CLOSE_PARANTHESES;
import static io.github.jeddict.orm.generator.util.ORMConverterUtil.OPEN_PARANTHESES;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class GeneratedValueSnippet implements Snippet {

    public static final String AUTO = "GenerationType.AUTO";
    public static final String IDENTITY = "GenerationType.IDENTITY";
    public static final String SEQUENCE = "GenerationType.SEQUENCE";
    public static final String TABLE = "GenerationType.TABLE";

    private static final Set<String> STRATEGY_TYPES = getStrategyTypes();

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

    @Override
    public String getSnippet() throws InvalidDataException {
        StringBuilder builder = new StringBuilder(AT);
        builder.append(GENERATED_VALUE);

        if (isBlank(generator) && isBlank(strategy)) {
            return builder.toString();
        }

        builder.append(OPEN_PARANTHESES)
                .append(buildString("generator", generator))
                .append(buildExp("strategy", strategy));

        return builder.substring(0, builder.length() - 1) + CLOSE_PARANTHESES;
    }

    @Override
    public Collection<String> getImportSnippets() throws InvalidDataException {
        Set<String> imports = new HashSet<>();
        if (isNotBlank(strategy)) {
            imports.add(GENERATION_TYPE_FQN);
        }
        imports.add(GENERATED_VALUE_FQN);
        return imports;
    }

    private static Set<String> getStrategyTypes() {
        Set<String> strategyTypes = new HashSet<>();
        strategyTypes.add(AUTO);
        strategyTypes.add(IDENTITY);
        strategyTypes.add(SEQUENCE);
        strategyTypes.add(TABLE);
        return strategyTypes;
    }
}
