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
package io.github.jeddict.orm.generator.compiler.constraints;

import io.github.jeddict.bv.constraints.AssertFalse;
import io.github.jeddict.bv.constraints.AssertTrue;
import io.github.jeddict.bv.constraints.Constraint;
import io.github.jeddict.bv.constraints.DecimalMax;
import io.github.jeddict.bv.constraints.DecimalMin;
import io.github.jeddict.bv.constraints.Digits;
import io.github.jeddict.bv.constraints.Email;
import io.github.jeddict.bv.constraints.Future;
import io.github.jeddict.bv.constraints.FutureOrPresent;
import io.github.jeddict.bv.constraints.Max;
import io.github.jeddict.bv.constraints.Min;
import io.github.jeddict.bv.constraints.Negative;
import io.github.jeddict.bv.constraints.NegativeOrZero;
import io.github.jeddict.bv.constraints.NotBlank;
import io.github.jeddict.bv.constraints.NotEmpty;
import io.github.jeddict.bv.constraints.NotNull;
import io.github.jeddict.bv.constraints.Null;
import io.github.jeddict.bv.constraints.Past;
import io.github.jeddict.bv.constraints.PastOrPresent;
import io.github.jeddict.bv.constraints.Pattern;
import io.github.jeddict.bv.constraints.Positive;
import io.github.jeddict.bv.constraints.PositiveOrZero;
import io.github.jeddict.bv.constraints.Size;
import io.github.jeddict.bv.constraints.Valid;

/**
 *
 * @author Gaurav Gupta
 */
public class ConstraintSnippetFactory {

    public static ConstraintSnippet getInstance(Constraint constraint) {
        if (constraint instanceof Valid) {
            return new ValidSnippet((Valid) constraint);
        } else if (constraint instanceof NotNull) {
            return new NotNullSnippet((NotNull) constraint);
        } else if (constraint instanceof Null) {
            return new NullSnippet((Null) constraint);
        } else if (constraint instanceof NotBlank) {
            return new NotBlankSnippet((NotBlank) constraint);
        } else if (constraint instanceof NotEmpty) {
            return new NotEmptySnippet((NotEmpty) constraint);
        } else if (constraint instanceof AssertFalse) {
            return new AssertFalseSnippet((AssertFalse) constraint);
        } else if (constraint instanceof AssertTrue) {
            return new AssertTrueSnippet((AssertTrue) constraint);
        } else if (constraint instanceof Past) {
            return new PastSnippet((Past) constraint);
        } else if (constraint instanceof PastOrPresent) {
            return new PastOrPresentSnippet((PastOrPresent) constraint);
        } else if (constraint instanceof Future) {
            return new FutureSnippet((Future) constraint);
        } else if (constraint instanceof FutureOrPresent) {
            return new FutureOrPresentSnippet((FutureOrPresent) constraint);
        } else if (constraint instanceof Size) {
            return new SizeSnippet((Size) constraint);
        } else if (constraint instanceof Pattern) {
            return new PatternSnippet((Pattern) constraint);
        } else if (constraint instanceof Email) {
            return new EmailSnippet((Email) constraint);
        } else if (constraint instanceof DecimalMin) {
            return new DecimalMinSnippet((DecimalMin) constraint);
        } else if (constraint instanceof DecimalMax) {
            return new DecimalMaxSnippet((DecimalMax) constraint);
        } else if (constraint instanceof Min) {
            return new MinSnippet((Min) constraint);
        } else if (constraint instanceof Max) {
            return new MaxSnippet((Max) constraint);
        } else if (constraint instanceof Digits) {
            return new DigitsSnippet((Digits) constraint);
        } else if (constraint instanceof Negative) {
            return new NegativeSnippet((Negative) constraint);
        } else if (constraint instanceof NegativeOrZero) {
            return new NegativeOrZeroSnippet((NegativeOrZero) constraint);
        } else if (constraint instanceof Positive) {
            return new PositiveSnippet((Positive) constraint);
        } else if (constraint instanceof PositiveOrZero) {
            return new PositiveOrZeroSnippet((PositiveOrZero) constraint);
        }

        throw new IllegalStateException(constraint.getClass().getName() + " not found");

    }
}
