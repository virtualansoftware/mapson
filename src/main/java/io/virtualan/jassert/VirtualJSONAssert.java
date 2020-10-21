/*
 * Copyright (c) 2020  Virtualan Software Contributors (https://virtualan.io)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 *  file except  in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 *  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific language governing permissions
 *  and limitations under the License.
 */

package io.virtualan.jassert;

/*
 *   Copyright (c) 2020.  Virtualan Software Contributors (https://virtualan.io)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 *   file except  in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software distributed under the
 *   License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *   either express or implied. See the License for the specific language governing permissions
 *   and limitations under the License.
 */

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

/**
 * The type Virtual json assert.
 */
@Slf4j
public class VirtualJSONAssert {

  /**
   * J assert object boolean.
   *
   * @param expected the expected
   * @param actual   the actual
   * @param mode     the mode
   * @return the boolean
   */
  public static boolean jAssertObject(JSONObject expected, JSONObject actual, JSONCompareMode mode) {
    JSONCompareResult result = JSONCompare.compareJSON(
        actual, expected, mode);
    logger(result);
    return result.passed();
  }

  /**
   * J assert array boolean.
   *
   * @param expected the expected
   * @param actual   the actual
   * @param mode     the mode
   * @return the boolean
   */
  public static boolean jAssertArray(JSONArray expected, JSONArray actual, JSONCompareMode mode) {
    JSONCompareResult result = JSONCompare.compareJSON(
        actual, expected, mode);
    logger(result);
    return result.passed();
  }

  private static void logger(JSONCompareResult result) {
    if(result.passed()) {
      log.info(result.getMessage());
    } else if(result.failed()) {
      log.error(result.getMessage());
    }
  }
}
