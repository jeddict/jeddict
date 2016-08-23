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
@OptionsPanelController.ContainerRegistration(id = "JPAModeler", categoryName = "#OptionsCategory_Name_JPAModeler", 
        iconBase = "org/netbeans/jpa/modeler/settings/icon.png", 
        keywords = "#OptionsCategory_Keywords_JPAModeler", keywordsCategory = "JPAModeler")
@NbBundle.Messages(value = {"OptionsCategory_Name_JPAModeler=JPA Modeler", "OptionsCategory_Keywords_JPAModeler=JPA Modeler"})
package org.netbeans.jpa.modeler.settings;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
