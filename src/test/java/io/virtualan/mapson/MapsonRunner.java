package io.virtualan.mapson;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		features={"classpath:features/"},
		glue = { "io.virtualan.mapson" }
		)
public class MapsonRunner {


}