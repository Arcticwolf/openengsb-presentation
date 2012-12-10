/**
 * Licensed to the Austrian Association for Software Tool Integration (AASTI)
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. The AASTI licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openengsb.presentation;

import org.openengsb.core.api.model.ModelDescription;
import org.openengsb.domain.issue.Issue;
import org.openengsb.domain.requirement.Requirement;
import org.openengsb.presentation.model.ExternalIssue;
import org.openengsb.presentation.model.ExternalRequirement;
import org.openengsb.presentation.model.SimpleModelA;
import org.openengsb.presentation.model.SimpleModelB;
import org.openengsb.presentation.model.SimpleModelC;
import org.openengsb.presentation.model.SimpleModelD;

public class ModelDefinitions {
    public static ModelDescription REQUIREMENT = new ModelDescription(Requirement.class, "1.0.0.SNAPSHOT");
    public static ModelDescription EXT_REQUIREMENT = new ModelDescription(ExternalRequirement.class, "3.0.0.SNAPSHOT");
    public static ModelDescription ISSUE = new ModelDescription(Issue.class, "3.0.0.SNAPSHOT");
    public static ModelDescription EXT_ISSUE = new ModelDescription(ExternalIssue.class, "3.0.0.SNAPSHOT");
    public static ModelDescription SIMPLEA = new ModelDescription(SimpleModelA.class, "3.0.0.SNAPSHOT");
    public static ModelDescription SIMPLEB = new ModelDescription(SimpleModelB.class, "3.0.0.SNAPSHOT");
    public static ModelDescription SIMPLEC = new ModelDescription(SimpleModelC.class, "3.0.0.SNAPSHOT");
    public static ModelDescription SIMPLED = new ModelDescription(SimpleModelD.class, "3.0.0.SNAPSHOT");
}
