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

package org.openengsb.presentation.story;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openengsb.presentation.StoryException;
import org.openengsb.core.api.model.ModelDescription;
import org.openengsb.core.ekb.api.ModelRegistry;
import org.openengsb.core.ekb.api.TransformationEngine;
import org.openengsb.core.ekb.api.transformation.TransformationDescription;
import org.openengsb.domain.issue.Issue;
import org.openengsb.presentation.model.Worker;
import org.openengsb.domain.requirement.Person;
import org.openengsb.domain.requirement.Requirement;
import org.openengsb.presentation.ModelDefinitions;
import org.openengsb.presentation.model.ExternalIssue;
import org.openengsb.presentation.model.ExternalRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class TransformationUC {
    private static Logger LOGGER = LoggerFactory.getLogger(TransformationUC.class);
    private ModelRegistry registry;
    private TransformationEngine transformationEngine;

    public TransformationUC(ModelRegistry registry, TransformationEngine transformationEngine) {
        this.registry = registry;
        this.transformationEngine = transformationEngine;
    }

    private ExternalRequirement createExternalRequirement() {
        ExternalRequirement extreq = new ExternalRequirement();
        extreq.setInternal_id("ext1");
        extreq.setName("requirement1");
        extreq.setChanged(new Date());
        extreq.setInformalDescription("this is an informal description");
        extreq.setPriority("NORMAL");
        extreq.setSummary("this is a summary");
        extreq.setTechnicalDescription("this is a technical description");
        extreq.setType("TASK");
        extreq.setTimeEstimation("1 Day");
        Worker worker = new Worker();
        worker.setName("Joe Doe");
        worker.setEmail("test@test.com");
        worker.setNickname("joe");
        extreq.setAssigned(worker);
        extreq.setChangedBy(worker);
        return extreq;
    }

    private ExternalRequirement createExternalRequirement2() {
        ExternalRequirement extreq = new ExternalRequirement();
        extreq.setInternal_id("ext2");
        extreq.setName("requirement2");
        extreq.setChanged(new Date());
        extreq.setInformalDescription("this is an informal description");
        extreq.setPriority("NORMAL");
        extreq.setSummary("this is a summary");
        extreq.setTechnicalDescription("this is a technical description");
        extreq.setType("TASK");
        extreq.setTimeEstimation("1 Day");
        Worker worker = new Worker();
        worker.setName("Dana Rise");
        worker.setEmail("test@test.com");
        worker.setNickname("joe");
        extreq.setAssigned(worker);
        extreq.setChangedBy(worker);
        return extreq;
    }

    public void runUC() {
        long start = System.currentTimeMillis();
        // retrieve external requirement
        ExternalRequirement extreq = createExternalRequirement();
        Requirement req = transformExternalToInternalRequirement(extreq);
        if (req.getShortDescription() == null || !req.getShortDescription().equals("this is a summary")) {
            LOGGER.error("Transformation from ext requirement to the requirement model failed. Short description "
                    + "should be 'this is a summary' but was {}", req.getShortDescription());
        }
        Issue issue = transformInternalRequirementToIssue(req);
        if (issue.getOwner() == null || !issue.getOwner().equals("joe")) {
            LOGGER.error("Transformation from requirement to the issue model failed. Owner should be 'joe' but was {}"
                    ,issue.getOwner());
        }
        ExternalIssue extissue = transformInternalToExternalIssue(issue);
        if (extissue.getTitle() == null || !extissue.getTitle().equals("this is a summary")) {
            LOGGER.error("Transformaton from issue to ext issue model failed. Titel should be 'this is a summary' but "
                    + "was {}", extissue.getTitle()); 
        }
        sendExternalIssueAway(extissue);
        // finished point 1 to 3

        // start point 4
        extreq = createExternalRequirement2();
        registry.unregisterModel(ModelDefinitions.REQUIREMENT);
        transformationRepairSequence(extreq);
        registry.registerModel(ModelDefinitions.REQUIREMENT);
        long duration = System.currentTimeMillis() - start;
        LOGGER.info("The use case ran through successfully! Duration: " + duration + " ms.");
    }

    private void sendExternalIssueAway(ExternalIssue extissue) {
        // do something with the external issue
    }

    private ExternalIssue transformInternalToExternalIssue(Issue issueInstance) {
        ModelDescription sourceModel = ModelDefinitions.ISSUE;
        ModelDescription targetModel = ModelDefinitions.EXT_ISSUE;

        return (ExternalIssue) transformationEngine.performTransformation(
            sourceModel, targetModel, issueInstance);
    }

    private Requirement transformExternalToInternalRequirement(ExternalRequirement extRequirementInstance) {
        ModelDescription sourceModel = ModelDefinitions.EXT_REQUIREMENT;
        ModelDescription targetModel = ModelDefinitions.REQUIREMENT;

        return (Requirement) transformationEngine.performTransformation(
            sourceModel, targetModel, extRequirementInstance);
    }

    private Issue transformInternalRequirementToIssue(Requirement requirementInstance) {
        ModelDescription sourceModel = ModelDefinitions.REQUIREMENT;
        ModelDescription targetModel = ModelDefinitions.ISSUE;

        String identifier = null;
        if (isAlsoDeveloper(requirementInstance.getAssignedTo())) {
            identifier = "RequirementToIssueForDeveloper";
        } else {
            identifier = "RequirementToIssue";
        }

        return (Issue) transformationEngine.performTransformation(
            sourceModel, targetModel, requirementInstance, Arrays.asList(identifier));
    }

    private boolean isAlsoDeveloper(Person person) {
        return person.getName().equals("joe");
    }

    private void transformationRepairSequence(ExternalRequirement extRequirementInstance) {
        ModelDescription sourceModel = ModelDefinitions.EXT_REQUIREMENT;
        ModelDescription targetModel = ModelDefinitions.EXT_ISSUE;

        try {
            transformationEngine.performTransformation(sourceModel, targetModel, extRequirementInstance);
            LOGGER
                .error("UNWANTED - transformation from external requirement to external issue without reparation works");
            throw new StoryException(
                "UNWANTED - transformation from external requirement to external issue without reparation works");
        } catch (IllegalArgumentException e) {
            LOGGER
                .info("WANTED - Transformation from external requirement to external issue without reparation does not work");
            // comes here because the 'Requirement' model is gone
        }
        TransformationDescription desc = getNewTransformationDescription();
        transformationEngine.saveDescription(desc);
        try {
            transformationEngine.performTransformation(sourceModel, targetModel, extRequirementInstance);
            LOGGER.info("WANTED - Transformation from external requirement to external issue works after reparation");
            // comes here because the new description fills the "gap" between
            // requirements and issues
        } catch (IllegalArgumentException e) {
            LOGGER
                .error("UNWANTED - Transformation from external requirement to external issue does not work after reparation");
            throw new StoryException(
                "UNWANTED - Transformation from external requirement to external issue does not work after reparation");
        }
        transformationEngine.deleteDescription(desc);
    }

    private TransformationDescription getNewTransformationDescription() {
        ModelDescription sourceModel = ModelDefinitions.EXT_REQUIREMENT;
        ModelDescription targetModel = ModelDefinitions.ISSUE;
        TransformationDescription description =
            new TransformationDescription(sourceModel, targetModel, "testTransformation");
        description.addStep("prefixChange", Lists.newArrayList("internal_id"), "id",
            createParamMap("oldPrefix", "externalRequirement", "newPrefix", "issue"));
        description.forwardField("summary", "summary");
        description.forwardField("technicalDescription", "description");
        description.instantiateField("priority", "org.openengsb.domain.issue.Priority;3.0.0.SNAPSHOT",
            "valueOf", "priority");
        description.valueField("#status", "NEW");
        description.instantiateField("status", "org.openengsb.domain.issue.Status;3.0.0.SNAPSHOT",
            "valueOf", "#status");
        description.valueField("#type", "NEW_FEATURE");
        description.instantiateField("type", "org.openengsb.domain.issue.Type;3.0.0.SNAPSHOT",
            "valueOf", "type");
        return description;
    }

    private Map<String, String> createParamMap(String... params) {
        Map<String, String> result = new HashMap<>();
        int i = 0;
        while (i + 1 <= params.length) {
            result.put(params[i], params[i + 1]);
            i += 2;
        }
        return result;
    }
}
