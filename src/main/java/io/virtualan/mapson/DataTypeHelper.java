/*
 *   Copyright (c) 2019.  Virtualan Software Contributors (https://virtualan.io)
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

package io.virtualan.mapson;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The MAPson Data type helper.
 */
@Slf4j
public class DataTypeHelper {

  private DataTypeHelper() {

  }

  /**
   * Sets object.
   *
   * @param jsonObject the json object
   * @param key        the key
   * @param value      the value
   */
  public static void setObject(JSONObject jsonObject, String key, Object value) {
    if (value instanceof String) {
      String newValue = (String) value;
      if (newValue.indexOf('~') == 1) {
        setObjectByType(jsonObject, key, value, newValue);
        return;
      }
    }
    jsonObject.put(key, value);
  }

  /**
   * Sets object by type.
   *
   * @param jsonArray the json array
   * @param value     the value
   */
  public static void setObjectByType(JSONArray jsonArray, Object value) {
    String[] arrayValue = value.toString().split("~");
    if ("i".equals(arrayValue[0])) {
      jsonArray.put(Integer.parseInt(arrayValue[1]));
    } else if ("b".equals(arrayValue[0])) {
      jsonArray.put(Boolean.parseBoolean(arrayValue[1]));
    } else if ("d".equals(arrayValue[0])) {
      jsonArray.put(Double.parseDouble(arrayValue[1]));
    } else if ("l".equals(arrayValue[0])) {
      jsonArray.put(Long.parseLong(arrayValue[1]));
    } else if ("f".equals(arrayValue[0])) {
      jsonArray.put(Float.parseFloat(arrayValue[1]));
    } else {
      jsonArray.put(value.toString());
    }
  }

  /**
   * Sets object by type.
   *
   * @param jsonObject the json object
   * @param key        the key
   * @param value      the value
   * @param newValue   the new value
   */
  public static void setObjectByType(JSONObject jsonObject, String key, Object value,
      String newValue) {
    String[] arrayValue = newValue.split("~");
    if ("i".equals(arrayValue[0])) {
      jsonObject.put(key, Integer.parseInt(arrayValue[1]));
    } else if ("b".equals(arrayValue[0])) {
      jsonObject.put(key, Boolean.parseBoolean(arrayValue[1]));
    } else if ("d".equals(arrayValue[0])) {
      jsonObject.put(key, Double.parseDouble(arrayValue[1]));
    } else if ("l".equals(arrayValue[0])) {
      jsonObject.put(key, Long.parseLong(arrayValue[1]));
    } else if ("f".equals(arrayValue[0])) {
      jsonObject.put(key, Float.parseFloat(arrayValue[1]));
    } else {
      jsonObject.put(key, value);
    }
  }

  /**
   * Gets prefix type.
   *
   * @param object the object
   * @return the prefix type
   */
  public static String getPrefixType(Object object) {
    String prefix = "";
    if (object instanceof Double) {
      prefix = "d~";
    } else if (object instanceof Integer) {
      prefix = "i~";
    } else if (object instanceof Boolean) {
      prefix = "b~";
    } else if (object instanceof Long) {
      prefix = "l~";
    } else if (object instanceof Float) {
      prefix = "f~";
    }
    return prefix;
  }
}
