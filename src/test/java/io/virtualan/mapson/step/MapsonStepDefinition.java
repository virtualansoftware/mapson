package io.virtualan.mapson.step;



import static io.restassured.RestAssured.given;

import java.util.Map;

import com.sun.org.apache.xpath.internal.operations.Equals;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.virtualan.mapson.Mapson;
import org.junit.Assert;


public class MapsonStepDefinition {

	
	private String PET_URL = "http://localhost:8080/api/pets";
	private String jsonActual;
	@Given("create a pet with given input$")
	public void createPetData(Map<String, String> petMap)  {
		jsonActual = Mapson.buildMapAsJsonString(petMap);
		System.out.println(jsonActual);
	}
	
	@Then("Check the Json is Valid")
	public void validateJson(Map<String, String> JsonExpected) {
		Assert.assertEquals(JsonExpected.get("key"), jsonActual);
	}
	
	
}