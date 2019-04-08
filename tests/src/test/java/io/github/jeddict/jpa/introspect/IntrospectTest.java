/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jpa.introspect;

import io.github.jeddict.settings.generate.GenerateSettings;
import io.github.jeddict.test.BaseModelTest;
import org.junit.jupiter.api.*;

/**
 *
 * @author jGauravGupta
 */
public class IntrospectTest extends BaseModelTest {

    @BeforeAll
    static void setup() {
        GenerateSettings.setIntrospectionEnabled(true);
    }

    @Test
    void testGenerator() throws Exception {
        testModelerFile("IntrospectModel.jpa");
    }

    @Test
    void testReveng() throws Exception {
        reverseEngineeringTest(
                "Student"
        );
    }

    @AfterAll
    static void tear() {
        GenerateSettings.setIntrospectionEnabled(false);
    }
}
