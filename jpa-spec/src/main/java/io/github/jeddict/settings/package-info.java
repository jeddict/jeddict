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
@OptionsPanelController.ContainerRegistration(
        id = "Jeddict", 
        categoryName = "#OptionsCategory_Name_Jeddict", 
        iconBase = "io/github/jeddict/settings/icon.png", 
        keywords = "#OptionsCategory_Keywords_Jeddict", 
        keywordsCategory = "Jeddict"
)
@NbBundle.Messages({
    "OptionsCategory_Name_Jeddict=Jeddict", 
    "OptionsCategory_Keywords_Jeddict=Jeddict"
})
package io.github.jeddict.settings;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
