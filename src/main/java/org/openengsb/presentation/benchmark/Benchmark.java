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

package org.openengsb.presentation.benchmark;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openengsb.core.common.transformations.TransformationUtils;
import org.openengsb.core.ekb.api.TransformationEngine;
import org.openengsb.core.ekb.api.transformation.TransformationDescription;
import org.openengsb.presentation.ModelDefinitions;
import org.openengsb.presentation.model.ExternalRequirement;
import org.openengsb.presentation.model.SimpleModelA;
import org.openengsb.presentation.model.Worker;

public class Benchmark {
    private TransformationEngine transformationEngine;

    public Benchmark(TransformationEngine transformationEngine) {
        this.transformationEngine = transformationEngine;
    }

    private void initialize() {
        loadTransformations("META-INF/transformations/simpleTransformations.transformation");
        loadTransformations("META-INF/transformations/requirementTransformations.transformation");
        loadTransformations("META-INF/transformations/issueTransformations.transformation");
        loadTransformations("META-INF/transformations/reqissueTransformations.transformation");
    }

    private void loadTransformations(String name) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(name);
        List<TransformationDescription> descriptions =
            TransformationUtils.getDescriptionsFromXMLInputStream(stream);
        transformationEngine.saveDescriptions(descriptions);
    }

    public void runBenchmark() throws Exception {
        System.out.println("initializing");
        initialize();

        for (int i = 0; i < 30; i++) {
            if (transformationEngine.isTransformationPossible(ModelDefinitions.EXT_REQUIREMENT,
                ModelDefinitions.REQUIREMENT)) {
                break;
            }
            System.out.println("still tryin. try nr. " + i);
            Thread.sleep(3000);
        }
        System.out.println("finished initializing");

        performMeasurement(BenchmarkType.SIMPLE, 1, 5);
        performMeasurement(BenchmarkType.SIMPLE, 10, 5);
        performMeasurement(BenchmarkType.SIMPLE, 100, 5);
        performMeasurement(BenchmarkType.SIMPLE, 1000, 5);

        performMeasurement(BenchmarkType.COMPLEX, 1, 5);
        performMeasurement(BenchmarkType.COMPLEX, 10, 5);
        performMeasurement(BenchmarkType.COMPLEX, 100, 5);
        performMeasurement(BenchmarkType.COMPLEX, 1000, 5);

        performMeasurement(BenchmarkType.SIMPLE_PATH, 1, 5);
        performMeasurement(BenchmarkType.SIMPLE_PATH, 10, 5);
        performMeasurement(BenchmarkType.SIMPLE_PATH, 100, 5);
        performMeasurement(BenchmarkType.SIMPLE_PATH, 1000, 5);

        performMeasurement(BenchmarkType.COMPLEX_PATH, 1, 5);
        performMeasurement(BenchmarkType.COMPLEX_PATH, 10, 5);
        performMeasurement(BenchmarkType.COMPLEX_PATH, 100, 5);
        performMeasurement(BenchmarkType.COMPLEX_PATH, 1000, 5);
        System.out.println("finished benchmark");
        
        System.out.println("check if it is working:");
        System.out.println("simple path result:");
        System.out.println(new SimplePathBenchmarkAction(transformationEngine, createTestSimpleModelA()).perform().toString());
        System.out.println("complex path result:");
        System.out.println(new ComplexPathBenchmarkAction(transformationEngine, createTestExtRequirement()).perform().toString());
    }

    private void performMeasurement(BenchmarkType type, int count, int iterations) {
        long duration = 0;
        switch (type) {
            case SIMPLE:
                System.out.println("perform " + count + " simple transformation(s) " + iterations + " times");
                duration = performTransformation(
                    new SimpleBenchmarkAction(transformationEngine, createTestSimpleModelA()), count, iterations);
                System.out.println("it took " + duration + " ms");
                break;
            case COMPLEX:
                System.out.println("perform " + count + " complex transformation(s) " + iterations + " times");
                duration = performTransformation(
                    new ComplexBenchmarkAction(transformationEngine, createTestExtRequirement()), count, iterations);
                System.out.println("it took " + duration + " ms");
                break;
            case SIMPLE_PATH:
                System.out.println("perform " + count + " simple path transformation(s) " + iterations + " times");
                duration = performTransformation(
                    new SimplePathBenchmarkAction(transformationEngine, createTestSimpleModelA()), count, iterations);
                System.out.println("it took " + duration + " ms");
                break;
            case COMPLEX_PATH:
                System.out.println("perform " + count + " complex path transformation(s) " + iterations + " times");
                duration =
                    performTransformation(
                        new ComplexPathBenchmarkAction(transformationEngine, createTestExtRequirement()), count,
                        iterations);
                System.out.println("it took " + duration + " ms");
                break;
        }
    }

    private long performTransformation(BenchmarkAction action, int count, int iterations) {
        // initialize for more realistic benchmark results
        action.perform();
        List<Long> durations = new ArrayList<Long>();
        for (int i = 0; i < iterations; i++) {
            long before = System.currentTimeMillis();
            for (int j = 0; j < count; j++) {
                action.perform();
            }
            long duration = System.currentTimeMillis() - before;
            durations.add(duration);
        }
        long duration = 0;
        for (Long value : durations) {
            duration += value;
        }
        return duration / durations.size();
    }

    private SimpleModelA createTestSimpleModelA() {
        SimpleModelA simple = new SimpleModelA();
        simple.setNameA("name");
        simple.setNumberA("number");
        simple.setDescriptionA("description");
        simple.setCommentA1("comment1");
        simple.setCommentA2("comment2");
        return simple;
    }

    private ExternalRequirement createTestExtRequirement() {
        ExternalRequirement requirement = new ExternalRequirement();
        Worker worker = new Worker();
        worker.setEmail("email");
        worker.setName("name");
        worker.setNickname("nickname");
        requirement.setAssigned(worker);
        requirement.setChangedBy(worker);
        requirement.setChanged(new Date());
        requirement.setComments("comment");
        requirement.setInformalDescription("description");
        requirement.setInternal_id("externalRequirement-1");
        requirement.setName("name");
        requirement.setPriority("LOW");
        requirement.setSummary("summary");
        requirement.setTechnicalDescription("technical description");
        requirement.setTimeEstimation("20 hours");
        requirement.setType("type");
        return requirement;
    }

}
