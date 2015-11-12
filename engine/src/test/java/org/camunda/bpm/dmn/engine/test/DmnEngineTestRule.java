/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.dmn.engine.test;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.junit.runner.Description;

/**
 * JUnit test rule for internal unit tests. Uses The
 * {@link DecisionResource} annotation to load decisions
 * before tests.
 */
public class DmnEngineTestRule extends DmnEngineRule {

  public static final String DMN_SUFFIX = "dmn";

  protected DmnDecision decision;

  public DmnEngineTestRule() {
    super();
  }

  public DmnEngineTestRule(DmnEngineConfiguration dmnEngineConfiguration) {
    super(dmnEngineConfiguration);
  }

  public DmnDecision getDecision() {
    return decision;
  }

  @Override
  protected void starting(Description description) {
    super.starting(description);

    decision = loadDecision(description);
  }

  protected DmnDecision loadDecision(Description description) {
    DecisionResource decisionResource = description.getAnnotation(DecisionResource.class);

    if(decisionResource != null) {

      String resourcePath = decisionResource.resource();

      resourcePath = expandResourcePath(description, resourcePath);

      String decisionKey = decisionResource.decisionKey();

      if (decisionKey == null || decisionKey.isEmpty()) {
        return dmnEngine.parseFirstDecision(resourcePath);
      } else {
        return dmnEngine.parseDecision(decisionKey, resourcePath);
      }
    }
    else {
      return null;
    }
  }

  protected String expandResourcePath(Description description, String resourcePath) {
    if (resourcePath.contains("/")) {
      // already expanded path
      return resourcePath;
    }
    else {
      Class<?> testClass = description.getTestClass();
      if (resourcePath.isEmpty()) {
        // use test class and method name as resource file name
        return testClass.getName().replace(".", "/") + "." + description.getMethodName() + "." + DMN_SUFFIX;
      }
      else {
        // use test class location as resource location
        return testClass.getPackage().getName().replace(".", "/") + "/" + resourcePath;
      }
    }
  }

}