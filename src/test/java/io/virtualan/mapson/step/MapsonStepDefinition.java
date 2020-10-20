/*
 *   Copyright (c) 2019.  Virtualan Software Contributors (https://virtualan.io)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software distributed under the License
 *   is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *   or implied. See the License for the specific language governing permissions and limitations under
 *   the License.
 */

package io.virtualan.mapson.step;


import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.virtualan.csvson.Csvson;
import io.virtualan.mapson.Mapson;
import io.virtualan.mapson.exception.BadInputDataException;
import java.util.List;
import org.json.JSONArray;
import org.junit.Assert;
import java.util.Map;
import java.util.logging.Logger;

public class MapsonStepDefinition {
	private final static Logger LOGGER = Logger.getLogger(MapsonStepDefinition.class.getName());
	
	private String jsonActual;
	Map<String, String> mapson;
	Map<String, String> contextObject;
	
	
	@Given("create a Json with given MAPson input$")
	public void createJson(Map<String, String> inputJsonPathMap)  {
		mapson = inputJsonPathMap;
	}
	
	@And("build context object$")
	public void createContextObject(Map<String, String> inputMap)  {
		contextObject = inputMap;
	}


	@Then("validate csvson rows$")
	public void validateJson(List<String> csvline) throws BadInputDataException {
		JSONArray jsonArray = Csvson.buildCSVson(csvline);
		jsonActual = Mapson.buildMAPsonAsJson(mapson, contextObject);
		Assert.assertEquals(jsonArray.get(0).toString(), jsonActual);
	}

	@Then("validate the Json is as Expected$")
	public void validateJson(Map<String, String> JsonExpected) throws BadInputDataException {
		jsonActual = Mapson.buildMAPsonAsJson(mapson);
		Assert.assertEquals(JsonExpected.get("key"), jsonActual);
	}
	
	@Then("check the Json with context value is Valid$")
	public void validateJsonWithContext(Map<String, String> JsonExpected) throws BadInputDataException {
		jsonActual = Mapson.buildMAPsonAsJson(mapson, contextObject);
		Assert.assertEquals(JsonExpected.get("key"), jsonActual);
	}
	
	@Then("check the reverse way able to to create the MAPson successfully")
	public void validateJson() {
		Map<String, String> firstMap = mapson;
		Map<String, String> secondMap = Mapson.buildMAPsonFromJson(jsonActual);
		for (Map.Entry<String, String> firstEntry : firstMap.entrySet()) {
			String firstMapValue = firstEntry.getValue();
			String secondMapValue = secondMap.get(firstEntry.getKey());
			if(!firstMapValue.equals(secondMapValue)){
				LOGGER.warning(firstEntry.getKey() + " " + firstMapValue + " " + secondMapValue + " " + firstMapValue.equals(secondMapValue));
			}
		}
		Assert.assertTrue(areEqual(firstMap, secondMap));
	}
	
	@Then("check the reverse way MAPson is Invalid")
	public void inValidateJson() {
		Map<String, String> firstMap = mapson;
		Map<String, String> secondMap = Mapson.buildMAPsonFromJson(jsonActual);
		for (Map.Entry<String, String> firstEntry : firstMap.entrySet()) {
			String firstMapValue = firstEntry.getValue();
			String secondMapValue = secondMap.get(firstEntry.getKey());
			if(!firstMapValue.equals(secondMapValue)){
				LOGGER.warning(firstEntry.getKey() + " " + firstMapValue + " " + secondMapValue + " " + firstMapValue.equals(secondMapValue));
			}
		}
		Assert.assertFalse(areEqual(firstMap, secondMap));
	}
	
	private boolean areEqual(Map<String, String> first, Map<String, String> second) {
		if (first.size() != second.size()) {
			return false;
		}
		return first.entrySet().stream().allMatch(e -> e.getValue().equals(second.get(e.getKey())));
	}
}