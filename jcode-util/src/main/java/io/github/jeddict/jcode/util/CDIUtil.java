/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jcode.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.netbeans.api.project.Project;
import static io.github.jeddict.jcode.util.ProjectHelper.getProjectWebInf;
import static java.util.Collections.singleton;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Gaurav Gupta
 */
public class CDIUtil {

    private static final String RESOURCE_FOLDER = "/io/github/jeddict/cdi/resource/"; //NOI18N

    public static Set<DataObject> createDD(Project project) throws IOException {
        FileObject webInf = getProjectWebInf(project);
        if (webInf != null) {
            FileObject fo = FileUtil.copyFile(RESOURCE_FOLDER + "beans.xml", webInf, "beans.xml");
            if (fo != null) {
                return singleton(DataObject.find(fo));
            }
        }
        return Collections.EMPTY_SET;
    }


}
