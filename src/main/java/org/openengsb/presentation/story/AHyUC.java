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

import org.openengsb.core.api.context.ContextHolder;
import org.openengsb.core.ekb.api.EKBCommit;
import org.openengsb.core.ekb.api.PersistInterface;
import org.openengsb.core.ekb.api.QueryInterface;
import org.openengsb.presentation.model.Signal;
import org.openengsb.presentation.model.Zuli;
import org.openengsb.presentation.model.Eplan;
import org.openengsb.presentation.model.Logicad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AHyUC {
    private static Logger LOGGER = LoggerFactory.getLogger(AHyUC.class);
    private PersistInterface persistInterface;
    private QueryInterface queryInterface;
    private String contextId;
    private int index;
    private String instanceId;

    public AHyUC(PersistInterface persistInterface, QueryInterface queryInterface) {
        this.persistInterface = persistInterface;
        this.queryInterface = queryInterface;
        this.index = 0;
    }
    
    private Zuli createZuliInstance() {
        Zuli zuliInstance = new Zuli();
        zuliInstance.setId("zuli-" + index);
        zuliInstance.setZuli_ok(true);
        zuliInstance.setZuli_comment("All fine for me");
        return zuliInstance;
    }
    
    private void insertInstances() {
        Zuli zuliInstance = new Zuli();
        zuliInstance.setId("zuli-" + index);
        Eplan eplanInstance = new Eplan();
        eplanInstance.setId("eplan-" + index);
        Logicad logicadInstance = new Logicad();
        logicadInstance.setId("logic-" + index);
        EKBCommit commit = createCommitObject();
        commit.addInsert(zuliInstance).addInsert(eplanInstance).addInsert(logicadInstance);
        persistInterface.commit(commit);
        LOGGER.info("Inserted model instances");
        // The corresponding signal model is created by the SignalCreationHook
    }

    public void runUC() {
        long start = System.currentTimeMillis();
        this.contextId = ContextHolder.get().getCurrentContextId();
        LOGGER.info("Starting the Andritz Hydro Use-Case");
        index++;
        insertInstances();
        
        Zuli zuliInstance = createZuliInstance();
        EKBCommit commit = createCommitObject();
        commit.addUpdate(zuliInstance);
        persistInterface.commit(commit);
        LOGGER.info("Persisted the Zuli model instance");
        
        Eplan eplanInstance = new Eplan();
        eplanInstance.setId("eplan-" + index);
        eplanInstance.setEplan_ok(false);
        eplanInstance.setEplan_comment("There is an error in the hardware configuration");
        commit = createCommitObject();
        commit.addUpdate(eplanInstance);
        persistInterface.commit(commit);
        
        LOGGER.info("Persisted the EPlan model instance with the error message for the Zuli model");
        zuliInstance = queryInterface.getModel(Zuli.class, contextId + "/zuli-" + index);
        if (zuliInstance.getEplan_ok() != false) {
            LOGGER.error("The eplan_ok property of the Zuli model was not updated");
            LOGGER.error("The use case ran not through successfully!");
        }
        // do some modifications on the zuliInstance, not part of the conceptual prove of context
        zuliInstance.setZuli_comment("I've fixed the problem, have a look at the change");
        commit = createCommitObject();
        commit.addUpdate(zuliInstance);
        persistInterface.commit(commit);
        LOGGER.info("Persisted the updated Zuli model instance");
        
        eplanInstance = queryInterface.getModel(Eplan.class, contextId + "/eplan-" + index);
        if (!eplanInstance.getZuli_comment().equals("I've fixed the problem, have a look at the change")) {
            LOGGER.error("The zuli_comment property of the Eplan model was not updated");
            LOGGER.error("The use case ran not through successfully!");
        }
        // Everything is fine, the electrical engineer finishes his work
        eplanInstance.setEplan_ok(true);
        eplanInstance.setEplan_comment("Everything fine for me");
        commit = createCommitObject();
        commit.addUpdate(eplanInstance);
        persistInterface.commit(commit);
        LOGGER.info("Persisted the updated Eplan model instance");
        
        Logicad logicadInstance = queryInterface.getModel(Logicad.class, contextId + "/logic-" + index);
        if (logicadInstance.getEplan_ok() != true) {
            LOGGER.error("The logicad instance was not updated");
            LOGGER.error("The use case ran not through successfully!");
        }
        
        logicadInstance.setEplan_ok(false);
        logicadInstance.setEplan_comment("I need an additional int variable");
        commit = createCommitObject();
        commit.addUpdate(logicadInstance);
        persistInterface.commit(commit);
        LOGGER.info("Persisted the updated Logicad model instance");
        
        eplanInstance = queryInterface.getModel(Eplan.class, contextId + "/eplan-" + index);
        if (eplanInstance.getEplan_ok() != false) {
            LOGGER.error("The eplan_ok property was not updated");
            LOGGER.error("The use case ran not through successfully!");
        }
        eplanInstance.setEplan_ok(true);
        eplanInstance.setEplan_comment("Did add another circuit on the board");
        commit = createCommitObject();
        commit.addUpdate(eplanInstance);
        persistInterface.commit(commit);
        LOGGER.info("Persisted the updated eplan model instance");
        
        logicadInstance = queryInterface.getModel(Logicad.class, contextId + "/logic-" + index);
        if (logicadInstance.getEplan_ok() != true) {
            LOGGER.error("The eplan_ok property was not updated");
            LOGGER.error("The use case ran not through successfully!");
        }
        logicadInstance.setLogic_ok(true);
        logicadInstance.setEplan_comment("Everything fine now");
        commit = createCommitObject();
        commit.addUpdate(logicadInstance);
        persistInterface.commit(commit);
        LOGGER.info("Persisted the updated logicad model instance");
        
        Signal signalInstance = queryInterface.getModel(Signal.class, contextId + "/signal-" + index);
        if (!signalInstance.getEplan_ok() || !signalInstance.getLogic_ok() || !signalInstance.getZuli_ok()) {
            LOGGER.error("Not all domains are ok");
            LOGGER.error("The use case ran not through successfully!");
        }
        // everything is fine, part can be constructed
        long duration = System.currentTimeMillis() - start;
        LOGGER.info("The use case ran through successfully! Duration: " + duration + " ms");
    }

    private EKBCommit createCommitObject() {
        EKBCommit commit = new EKBCommit();
        commit.setDomainId("presentation-domain");
        commit.setConnectorId("presentation-connector");
        commit.setInstanceId(instanceId);
        return commit;
    }
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
