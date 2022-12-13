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

import static io.github.jeddict.jcode.util.AttributeType.CALENDAR;
import static io.github.jeddict.jcode.util.AttributeType.DATE;
import static io.github.jeddict.jcode.util.AttributeType.DURATION;
import static io.github.jeddict.jcode.util.AttributeType.GREGORIAN_CALENDAR;
import static io.github.jeddict.jcode.util.AttributeType.INSTANT;
import static io.github.jeddict.jcode.util.AttributeType.LOCAL_DATE;
import static io.github.jeddict.jcode.util.AttributeType.LOCAL_DATE_TIME;
import static io.github.jeddict.jcode.util.AttributeType.LOCAL_TIME;
import static io.github.jeddict.jcode.util.AttributeType.OFFSET_DATE_TIME;
import static io.github.jeddict.jcode.util.AttributeType.OFFSET_TIME;
import static io.github.jeddict.jcode.util.AttributeType.PERIOD;
import static io.github.jeddict.jcode.util.AttributeType.SIMPLE_TIME_ZONE;
import static io.github.jeddict.jcode.util.AttributeType.TIME_ZONE;
import static io.github.jeddict.jcode.util.AttributeType.ZONED_DATE_TIME;
import static io.github.jeddict.jcode.util.AttributeType.ZONE_ID;
import static io.github.jeddict.jcode.util.AttributeType.ZONE_OFFSET;
import io.github.jeddict.jpa.spec.validator.JsonbDateFormatValidator;
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
@XmlJavaTypeAdapter(value = JsonbDateFormatValidator.class)
public class JsonbDateFormat extends JsonbFormat {
    
    private static final Set<String> SUPPORTED_TYPE = new HashSet<>(Arrays.asList(DATE,CALENDAR,GREGORIAN_CALENDAR,TIME_ZONE ,SIMPLE_TIME_ZONE,
            INSTANT,DURATION,PERIOD,LOCAL_DATE,LOCAL_TIME,LOCAL_DATE_TIME,
            ZONED_DATE_TIME,ZONE_ID,ZONE_OFFSET,OFFSET_DATE_TIME,OFFSET_TIME));
    
    @Override
    public boolean isSupportedFormat(String type) {
        return SUPPORTED_TYPE.contains(type);
    }

    public static JsonbDateFormat load(AnnotatedMember member) {
        JsonbDateFormat jsonbDateFormat = null;
        Optional<AnnotationExplorer> annotationOpt = member.getAnnotation(jakarta.json.bind.annotation.JsonbDateFormat.class);
        if (annotationOpt.isPresent()) {
            AnnotationExplorer annotation = annotationOpt.get();
            jsonbDateFormat = new JsonbDateFormat();
            annotation.getString("value").ifPresent(jsonbDateFormat::setValue);
            annotation.getString("locale").ifPresent(jsonbDateFormat::setLocale);
        }
        return jsonbDateFormat;
    }

    
}
