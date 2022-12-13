/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a
 * workingCopy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.jeddict.jcode;

import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Gaurav Gupta
 */
public class BeanVaildationConstants {

    public static final String BV_CONSTRAINTS_PACKAGE = "jakarta.validation.constraints";
    public static final String CONSTRAINT_VIOLATION = "jakarta.validation.ConstraintViolation";
    public static final String VALIDATE_ON_EXECUTION = "jakarta.validation.executable.ValidateOnExecution";
    public static final String EXECUTABLE_TYPE = "jakarta.validation.executable.ExecutableType";
    public static final String BV_PACKAGE_PREFIX = "jakarta.validation.";

    public static final String ASSERT_FALSE = "AssertFalse";
    public static final String ASSERT_TRUE = "AssertTrue";
    public static final String DECIMAL_MAX = "DecimalMax";
    public static final String DECIMAL_MIN = "DecimalMin";
    public static final String DIGITS = "Digits";
    public static final String EMAIL = "Email";
    public static final String FUTURE = "Future";
    public static final String FUTURE_OR_PRESENT = "FutureOrPresent";
    public static final String MAX = "Max";
    public static final String MIN = "Min";
    public static final String NEGATIVE = "Negative";
    public static final String NEGATIVE_OR_ZERO = "NegativeOrZero";
    public static final String NOT_BLANK = "NotBlank";
    public static final String NOT_EMPTY = "NotEmpty";
    public static final String NOT_NULL = "NotNull";
    public static final String NULL = "Null";
    public static final String PAST = "Past";
    public static final String PAST_OR_PRESENT = "PastOrPresent";
    public static final String PATTERN = "Pattern";
    public static final String POSITIVE = "Positive";
    public static final String POSITIVE_OR_ZERO = "PositiveOrZero";
    public static final String SIZE = "Size";
    public static final String VALID = "Valid";
    public static final String VALID_FQN = BV_PACKAGE_PREFIX + VALID;


    public static final Set<String> BV_ANNOTATIONS = new HashSet<>(asList(
            ASSERT_FALSE,
            ASSERT_TRUE,
            DECIMAL_MAX,
            DECIMAL_MIN,
            DIGITS,
            EMAIL,
            FUTURE,
            FUTURE_OR_PRESENT,
            MAX,
            MIN,
            NEGATIVE,
            NEGATIVE_OR_ZERO,
            NOT_BLANK,
            NOT_EMPTY,
            NOT_NULL,
            NULL,
            PAST,
            PAST_OR_PRESENT,
            PATTERN,
            POSITIVE,
            POSITIVE_OR_ZERO,
            SIZE,
            VALID
    ));
}
