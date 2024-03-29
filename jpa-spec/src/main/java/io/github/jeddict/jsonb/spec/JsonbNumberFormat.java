/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jsonb.spec;

import static io.github.jeddict.jcode.util.AttributeType.BIGDECIMAL;
import static io.github.jeddict.jcode.util.AttributeType.BIGINTEGER;
import static io.github.jeddict.jcode.util.AttributeType.DOUBLE;
import static io.github.jeddict.jcode.util.AttributeType.DOUBLE_WRAPPER;
import static io.github.jeddict.jcode.util.AttributeType.FLOAT;
import static io.github.jeddict.jcode.util.AttributeType.FLOAT_WRAPPER;
import static io.github.jeddict.jcode.util.AttributeType.INT;
import static io.github.jeddict.jcode.util.AttributeType.INT_WRAPPER;
import static io.github.jeddict.jcode.util.AttributeType.LONG;
import static io.github.jeddict.jcode.util.AttributeType.LONG_WRAPPER;
import static io.github.jeddict.jcode.util.AttributeType.SHORT;
import static io.github.jeddict.jcode.util.AttributeType.SHORT_WRAPPER;
import io.github.jeddict.jpa.spec.validator.JsonbNumberFormatValidator;
import io.github.jeddict.source.AnnotatedMember;
import io.github.jeddict.source.AnnotationExplorer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author jGauravGupta
 */
@XmlJavaTypeAdapter(value = JsonbNumberFormatValidator.class)
public class JsonbNumberFormat extends JsonbFormat {

    private static final Set<String> SUPPORTED_TYPE = new HashSet<>(Arrays.asList(SHORT, INT, LONG, FLOAT, DOUBLE,
            SHORT_WRAPPER, INT_WRAPPER, LONG_WRAPPER, FLOAT_WRAPPER, DOUBLE_WRAPPER, BIGINTEGER, BIGDECIMAL));

    @Override
    public boolean isSupportedFormat(String type) {
        return SUPPORTED_TYPE.contains(type);
    }

    public static JsonbNumberFormat load(AnnotatedMember member) {
        JsonbNumberFormat jsonbNumberFormat = null;
        Optional<AnnotationExplorer> annotationOpt = member.getAnnotation(jakarta.json.bind.annotation.JsonbNumberFormat.class);
        if (annotationOpt.isPresent()) {
            AnnotationExplorer annotation = annotationOpt.get();
            jsonbNumberFormat = new JsonbNumberFormat();
            annotation.getString("value").ifPresent(jsonbNumberFormat::setValue);
            annotation.getString("locale").ifPresent(jsonbNumberFormat::setLocale);
        }
        return jsonbNumberFormat;
    }

}
