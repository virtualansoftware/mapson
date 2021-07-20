package io.virtualan.util;
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

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Helper {

  public static Object getActualValue(Object object, Map<String, String> contextObject) {
    if(object != null) {
      String key = object.toString();
      if (key.indexOf('[') != -1) {
        String idkey = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
        if (contextObject.containsKey(idkey)) {
          return key.replaceAll("\\[" + key.substring(key.indexOf('[') + 1, key.indexOf(']') + 1),
              contextObject.get(idkey).replace("$","\\$"));
        }
      }
    }
    return object;
  }

  public static Object getActualValueForAll(Object object, Map<String, String> contextObject) {
    String key = object.toString();
    if (key.indexOf('[') != -1 && key.indexOf(']') != -1) {
      String idkey = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
      if (contextObject.containsKey(idkey)) {
        String replaceValue =  key.replaceAll("\\[" + key.substring(key.indexOf('[') + 1, key.indexOf(']') + 1),
            contextObject.get(idkey));
        if (key.indexOf('[') != -1 && key.indexOf(']') != -1) {
          return getActualValue(replaceValue, contextObject);
        }
      } else {
        log.error("id key :" + idkey);
      }
    }
    return object;
  }

}
