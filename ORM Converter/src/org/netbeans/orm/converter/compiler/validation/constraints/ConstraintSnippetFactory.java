/**
 * Copyright [2016] Gaurav Gupta
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
package org.netbeans.orm.converter.compiler.validation.constraints;

import org.netbeans.bean.validation.constraints.AssertFalse;
import org.netbeans.bean.validation.constraints.AssertTrue;
import org.netbeans.bean.validation.constraints.Constraint;
import org.netbeans.bean.validation.constraints.DecimalMax;
import org.netbeans.bean.validation.constraints.DecimalMin;
import org.netbeans.bean.validation.constraints.Digits;
import org.netbeans.bean.validation.constraints.Email;
import org.netbeans.bean.validation.constraints.Future;
import org.netbeans.bean.validation.constraints.FutureOrPresent;
import org.netbeans.bean.validation.constraints.Max;
import org.netbeans.bean.validation.constraints.Min;
import org.netbeans.bean.validation.constraints.Negative;
import org.netbeans.bean.validation.constraints.NegativeOrZero;
import org.netbeans.bean.validation.constraints.NotBlank;
import org.netbeans.bean.validation.constraints.NotEmpty;
import org.netbeans.bean.validation.constraints.NotNull;
import org.netbeans.bean.validation.constraints.Null;
import org.netbeans.bean.validation.constraints.Past;
import org.netbeans.bean.validation.constraints.PastOrPresent;
import org.netbeans.bean.validation.constraints.Pattern;
import org.netbeans.bean.validation.constraints.Positive;
import org.netbeans.bean.validation.constraints.PositiveOrZero;
import org.netbeans.bean.validation.constraints.Size;

/**
 *
 * @author Gaurav Gupta
 */
public class ConstraintSnippetFactory {

    public static ConstraintSnippet getInstance(Constraint constraint) {
        if (constraint instanceof NotNull) {
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
