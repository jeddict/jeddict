/**
 * Copyright [2013] Gaurav Gupta
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
@TemplateRegistration(folder = "Persistence", content = "JPADiagram.jpa"
, position=1, displayName="#template_name", category="persistence",description="TemplateHelp.html", requireProject=false)
@Messages("template_name=JPA Modeler")

//@TemplateRegistration(folder = "Persistence", position = 2, 
//        displayName = "#DBImportWizardDescriptor_displayName",
//        iconBase = "org/netbeans/jpa/modeler/reveng/database/resource/JPA_FILE_ICON.png",
//        description = "resource/JPA_DB_IMPORT_DESC.html")

package org.netbeans.jpa.modeler.specification.model.file;

import org.netbeans.api.templates.TemplateRegistration;
import org.openide.util.NbBundle.Messages;
//@TemplateRegistration(
//    folder=UIUtil.TEMPLATE_FOLDER,
//    id="newHTML",
//    position=191,
//    displayName="#template_html",
//    iconBase="org/netbeans/modules/apisupport/project/ui/wizard/html/newHTML.png",
//    description="newHTML.html",
//    category=UIUtil.TEMPLATE_CATEGORY
//)
//@Messages("template_html=Portable HTML UI")

//@TemplateRegistrations({
//    @TemplateRegistration(folder="Other", position=100, displayName="#Templates/Other/html.html", content="templates/html.html", 
//scriptEngine="freemarker", category="simple-files", description="TemplateHelp.html", requireProject=false),
//    @TemplateRegistration(folder="Other", position=200, displayName="#Templates/Other/xhtml.xhtml", content="templates/xhtml.xhtml", scriptEngine="freemarker", category="simple-files", description="XhtmlTemplateHelp.html", requireProject=false)
//})

    //    @NbBundle.Messages({
    //        "LBL_CordovaApp=Cordova Application"
    //    })
    //    @TemplateRegistration(folder = "Project/ClientSide",
    //            displayName = "#LBL_CordovaApp",
    //            description = "../resources/CordovaProjectDescription.html", // NOI18N
    //            iconBase = "org/netbeans/modules/cordova/resources/project.png", // NOI18N
    //            position = 400)