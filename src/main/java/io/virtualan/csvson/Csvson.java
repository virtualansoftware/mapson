package io.virtualan.csvson;

/*
 *   Copyright (c) 2020.  Virtualan Software Contributors (https://virtualan.io)
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


import io.virtualan.mapson.Mapson;
import io.virtualan.mapson.exception.BadInputDataException;
import io.virtualan.util.Helper;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;

public class Csvson {


  public static JSONArray buildCSVson(List<String> csvline, Map<String, String> contextObject)  throws BadInputDataException {
    String heading = csvline.get(0);
    List<String> list = csvline.subList(1, csvline.size());
    List<JSONObject> listJSONArray = list.stream().map(s -> {
      try {
        return new JSONObject(splitRow(heading, Helper.getActualValue(s, contextObject).toString()));
      } catch (BadInputDataException e) {
        return null;
      }
    }).collect(Collectors.toList());

    JSONArray array = new JSONArray();
    listJSONArray.forEach(it -> array.put(it));
    return array;
  }


  private static String splitRow(String heading, String row) throws BadInputDataException {
    String[] headings = heading.split(",");
    String[] rows = row.split(",");

    Map<String, String> rowMap =
        IntStream.range(0, headings.length)
            .mapToObj(i -> new AbstractMap.SimpleEntry<>(headings[i], rows[i]))
            .collect(Collectors.toMap(k -> k.getKey().trim(), v -> v.getValue().trim()));

    List<SimpleEntry<String, String>> mapsonList = rowMap.entrySet().stream()
        .map(Csvson::buildKeyValuePairBase).flatMap(List::stream).collect(
            Collectors.toList());

    Map<String, String> mapson = mapsonList.stream()
        .collect(LinkedHashMap::new, (m,v)->m.put(v.getKey(), v.getValue()), LinkedHashMap::putAll);
      return Mapson.buildMAPsonAsJson(mapson);
    }

  private static List<SimpleEntry<String, String>> buildKeyValuePairBase(Entry<String, String> entry) {
    String[] subElementParent = entry.getKey().split("/");
    String prefix = subElementParent[0];
    String[] subElement = indexExists(subElementParent, 1)?  subElementParent[1].split(":") : null;
    String[] subArrayElementValue = entry.getValue().split("\\|");
    String[] subElementValue = entry.getValue().split(":");

    Stream<List<SimpleEntry<String, String>>>
        streamArrayElement  = null;
    if(subElement != null && subArrayElementValue != null ) {
      if (subArrayElementValue.length > 1) {
        streamArrayElement = IntStream.range(0, subArrayElementValue.length)
            .mapToObj(i ->
                getArrayElementList(prefix, subElement, subArrayElementValue[i].split(":"), i));
      } else {
        streamArrayElement = IntStream.range(0, 1)
            .mapToObj(i ->
                getElementList(prefix, subElement, subElementValue));

      }
      List subElementMap = streamArrayElement.flatMap(List::stream)
          .collect(Collectors.toList());

      System.out.println(subElementMap);
      return subElementMap;
    }
    else {
      if(subArrayElementValue != null && subArrayElementValue.length > 1) {
        return IntStream.range(0, subArrayElementValue.length).mapToObj(i -> (new SimpleEntry<String, String>(prefix +"[" +i+"]",subArrayElementValue[i])))
            .collect(Collectors.toList());
      } else {
        SimpleEntry<String, String> singleEntry = new SimpleEntry<String, String>(entry.getKey(),
            entry.getValue());

        List<SimpleEntry<String, String>> list = new ArrayList<>();
        list.add(singleEntry);
        return list;
      }
    }
  }


  private static List<SimpleEntry<String, String>> buildKeyValuePair(Entry<String, String> entry) {
    String[] subElementParent = entry.getKey().split("/");
    String prefix = subElementParent[0];

    String[] subElement = subElementParent[1].split(":");
    String[] subArrayElementValue = entry.getValue().split("\\|");
    String[] subElementValue = entry.getValue().split(":");

    Stream<List<SimpleEntry<String, String>>>
        streamArrayElement  = null;
    if(subElement != null && subArrayElementValue != null ) {
      if (subArrayElementValue.length > 1) {
        streamArrayElement = IntStream.range(0, subArrayElementValue.length)
            .mapToObj(i ->
                getArrayElementList(prefix, subElement, subArrayElementValue[i].split(":"), i));
      } else {
        streamArrayElement = IntStream.range(0, 1)
            .mapToObj(i ->
                getElementList(prefix, subElement, subElementValue));

      }
      List subElementMap = streamArrayElement.flatMap(List::stream)
          .collect(Collectors.toList());

      System.out.println(subElementMap);
      return subElementMap;
    }
    else {
      if(subArrayElementValue != null) {
        return IntStream.range(0, subArrayElementValue.length).mapToObj(i -> (new SimpleEntry<String, String>(prefix +"[" +i+"]",subArrayElementValue[i])))
            .collect(Collectors.toList());
      } else {
        SimpleEntry<String, String> singleEntry = new SimpleEntry<String, String>(entry.getKey(),
            entry.getValue());

        List<SimpleEntry<String, String>> list = new ArrayList<>();
        list.add(singleEntry);
        return list;
      }
    }
  }

  private static List<SimpleEntry<String, String>> getElementList(String prefix, String[] subElement,
      String[] valueElement) {
    IntPredicate valueIsNotNull = i -> indexExists(valueElement, i) && valueElement[i] != null && !valueElement[i].equalsIgnoreCase("");
    return IntStream.range(0, subElement.length)
        .filter(valueIsNotNull).mapToObj(i -> (new SimpleEntry<String, String>(prefix +"." + subElement[i],
            valueElement[i])))
        .collect(Collectors.toList());
  }

  private static List<SimpleEntry<String, String>> getArrayElementList(String prefix, String[] subElement,
      String[] valueElement, int index) {
    IntPredicate valueIsNotNull = i ->  indexExists(valueElement, i) && valueElement[i] != null && !valueElement[i].equalsIgnoreCase("");
    return IntStream.range(0, subElement.length).filter(valueIsNotNull)
        .mapToObj(i -> (new SimpleEntry<String, String>(prefix + "[" + index + "]." + subElement[i],
             (indexExists(valueElement, i)) ? valueElement[i] : null)))
        .collect(Collectors.toList());
  }

  private static boolean indexExists(String[] array,int index){
    if (array != null && index >= 0 && index < array.length) {
      return array[index] != null ? true : false;
    } else {
      return false;
    }
  }

}