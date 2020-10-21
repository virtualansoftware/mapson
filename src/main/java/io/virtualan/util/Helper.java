package io.virtualan.util;

import java.util.Map;
import java.util.logging.Logger;

public class Helper {

  private final static Logger LOGGER = Logger.getLogger(Helper.class.getName());

  public static Object getActualValue(Object object, Map<String, String> contextObject) {
    String key = object.toString();
    if (key.indexOf('[') != -1) {
      String idkey = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
      if (contextObject.containsKey(idkey)) {
        return key.replaceAll("\\[" + key.substring(key.indexOf('[') + 1, key.indexOf(']') + 1),
            contextObject.get(idkey));
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
        LOGGER.warning("id key :" + idkey);
      }
    }
    return object;
  }

}
