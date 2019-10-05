package io.virtualan.mapson.step;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.virtualan.mapson.Mapson;
import org.junit.Assert;
import java.util.Map;

public class MapsonStepDefinition {

	private String jsonActual;
	Map<String, String> mapson;
	
	@Given("Create a json with given jsonpath input$")
	public void createPetData(Map<String, String> petMap)  {
		mapson = petMap;
		jsonActual = Mapson.buildMapAsJsonString(petMap);
	}
	
	@Then("Check the Json is Valid")
	public void validateJson(Map<String, String> JsonExpected) {
		Assert.assertEquals(JsonExpected.get("key"), jsonActual);
	}

	@Then("Check the reverse way mapson is Valid")
	public void validateJson() {
		Map<String, String> firstMap = mapson;
		Map<String, String> secondMap = Mapson.buildMapson(jsonActual);
		for (Map.Entry<String, String> firstEntry : firstMap.entrySet()) {
			String firstMapValue = firstEntry.getValue();
			String secondMapValue = secondMap.get(firstEntry.getKey());
			if(!firstMapValue.equals(secondMapValue)){
				System.out.println(firstEntry.getKey() + " " + firstMapValue + " " + secondMapValue + " " + firstMapValue.equals(secondMapValue));
			}
		}
		Assert.assertTrue(areEqual(firstMap, secondMap));
	}
	
	private boolean areEqual(Map<String, String> first, Map<String, String> second) {
		if (first.size() != second.size()) {
			return false;
		}
		return first.entrySet().stream().allMatch(e -> e.getValue().equals(second.get(e.getKey())));
	}
}