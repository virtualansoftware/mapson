package io.virtualan.jassert;


import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

public class VirtualJSONAssert {
  private final static Logger log = Logger.getLogger(VirtualJSONAssert.class.getName());

  public static boolean jAssertObject(JSONObject expected, JSONObject actual, JSONCompareMode mode) {
    JSONCompareResult result = JSONCompare.compareJSON(
        actual, expected, mode);
    logger(result);
    return result.passed();
  }
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
      log.warning(result.getMessage());
    }
  }
}
