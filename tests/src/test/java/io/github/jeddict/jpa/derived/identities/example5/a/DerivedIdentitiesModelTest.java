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
package io.github.jeddict.jpa.derived.identities.example5.a;

import io.github.jeddict.test.BaseModelTest;
import org.junit.jupiter.api.*;

/**
 *
 * @author jGauravGupta
 */
public class DerivedIdentitiesModelTest extends BaseModelTest {

    @Test
    void testGenerator() throws Exception {
        testModelerFile("DerivedIdentitiesModel.jpa");
    }

    @Test
    void testReveng() throws Exception {
        reverseEngineeringTest(
                "Person",
                "PersonId",
                "MedicalHistory"
        );
    }

}