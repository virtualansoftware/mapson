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

package io.virtualan.mapson;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		plugin = {"progress",
				"html:target/report/html",
				"junit:target/report/junit/cucumber-report.xml",
				"json:target/report/json/cucumber-report.json"
		},
		features={"classpath:features/"},
		glue = { "io.virtualan.mapson.step" }
		)
public class MapsonRunner {
	
	@Test
	public void test()
	{
		Assert.assertTrue("This will succeed.", true);
	}
}