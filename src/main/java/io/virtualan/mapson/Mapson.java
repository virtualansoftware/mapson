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

import io.virtualan.mapson.exception.BadInputDataException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.*;


public class Mapson {
    
    public static String buildMAPsonAsJson(Map<String, String> jsonStructMap) throws BadInputDataException {
        Map<String, Object> params = new HashMap<>();
        for (Map.Entry<String, String> mapsonEntry : jsonStructMap.entrySet()) {
            String key = mapsonEntry.getKey();
            if (key.indexOf(".") != -1) {
                params = buildChildJson(params, key.split("\\."), mapsonEntry.getValue());
            } else if (key.contains("[") && key.contains("]")) {
                String elementAt = key.substring(0, key.indexOf("["));
                params.put(key,buildObjectList(params, key, mapsonEntry.getValue()));
            } else {
                params.put(key, mapsonEntry.getValue());
            }
        }
        String json = buildJsonString(params).toString();
        return json;
    }
    
    public static Map<String, String> buildMAPsonFromJson(String json) {
        JSONTokener jsonTokener = new JSONTokener(json);
        JSONObject jsonObject = new JSONObject(jsonTokener);
        Map<String, String>  mapsonMap = new LinkedHashMap<>();
        getJSONPath(jsonObject, null , mapsonMap);
        return mapsonMap;
    }
    
    public static String buildMAPsonAsJson(Map<String, String> jsonStructMap, Map<String, String> contextObject ) throws BadInputDataException {
        Map<String, Object> params = new HashMap<>();
        for (Map.Entry<String, String> mapsonEntry : jsonStructMap.entrySet()) {
            String key = mapsonEntry.getKey();
            if (key.indexOf(".") != -1) {
                params = buildChildJson(params, key.split("\\."), mapsonEntry.getValue());
            } else if (key.contains("[") && key.contains("]")) {
                String elementAt = key.substring(0, key.indexOf("["));
                params.put(elementAt, buildObjectList(params, key, mapsonEntry.getValue()));
            } else {
                params.put(key, getActualValue(mapsonEntry.getValue(),contextObject));
            }
        }
        String json = buildJsonString(params).toString();
        return json;
    }
    
    private static void populateList(int index, List<Object> elementList, Object object) {
        try {
            elementList.set(index, object);
        } catch (IndexOutOfBoundsException E) {
            elementList.add(index, object);
        }
    }
    
    private static List<Object> extractList(String key, Map<String, Object> jsonStructMap) {
        if (jsonStructMap.containsKey(key)) {
            return (List<Object>) jsonStructMap.get(key);
        } else {
            return new ArrayList<>();
        }
    }
    
    
    private static List<Object> buildObjectList(Map<String, Object> jsonStructMap, String elementAt, Object value) {
        String elementAtArray = elementAt.substring(0, elementAt.indexOf("["));
        int index = Integer.parseInt(elementAt.substring(elementAt.indexOf("[") + 1, elementAt.indexOf("]")));
        List<Object> elementList = extractList(elementAtArray, jsonStructMap);
        populateList(index, elementList, value);
        return elementList;
    }
    
    private static JSONArray buildJsonArray(List<Object> jsonStructList) {
        JSONArray array = new JSONArray();
        for (Iterator<Object> it = jsonStructList.iterator(); it.hasNext(); ) {
            Object ObIterator = it.next();
            array.put(ObIterator);
        }
        return array;
    }
    
    private static JSONObject buildJsonString(Map<String, Object> jsonStructMap, Map<String, String> contextObject) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> mapEntry : jsonStructMap.entrySet()) {
            if (mapEntry.getValue() instanceof Map) {
                DataTypeHelper.setObject(jsonObject, mapEntry.getKey(), buildJsonString((Map<String, Object>) mapEntry.getValue(), contextObject));
            } else if (mapEntry.getValue() instanceof List) {
                JSONArray array = new JSONArray();
                for (Object object : (List<Object>) mapEntry.getValue()) {
                    if (object instanceof Map) {
                        JSONObject obj = buildJsonString((Map<String, Object>) object);
                        array.put(getActualValue(obj, contextObject));
                    }
                }
                DataTypeHelper.setObject(jsonObject, mapEntry.getKey(), array);
            } else {
                DataTypeHelper.setObject(jsonObject, mapEntry.getKey(), getActualValue(mapEntry.getValue(), contextObject));
            }
            
        }
        return jsonObject;
    }
    
    private static JSONObject buildJsonString(Map<String, Object> jsonStructMap) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> mapEntry : jsonStructMap.entrySet()) {
            if (mapEntry.getValue() instanceof Map) {
                DataTypeHelper.setObject(jsonObject, mapEntry.getKey(), buildJsonString((Map<String, Object>) mapEntry.getValue()));
            } else if (mapEntry.getValue() instanceof List) {
                JSONArray array = new JSONArray();
                for (Object object : (List<Object>) mapEntry.getValue()) {
                    if (object instanceof Map) {
                        JSONObject obj = buildJsonString((Map<String, Object>) object);
                        array.put(obj);
                    } else  {
                        DataTypeHelper.setObjectByType(array, object);
                    }
                }
                DataTypeHelper.setObject(jsonObject, mapEntry.getKey(), array);
            } else {
                DataTypeHelper.setObject(jsonObject, mapEntry.getKey(), mapEntry.getValue());
            }
            
        }
        return jsonObject;
    }
    
    private static Object getActualValue(Object object, Map<String, String> contextObject) {
        String key = object.toString();
        if(key.indexOf("[") != -1){
            String idkey = key.substring(key.indexOf("[")+1, key.indexOf("]"));
            if(contextObject.containsKey(idkey) ){
                System.out.println(key.substring(key.indexOf("["), key.indexOf("]")+1));
                return key.replaceAll("\\["+key.substring(key.indexOf("[")+1, key.indexOf("]")+1),  contextObject.get(idkey).toString());
            }
        }
        return object;
    }
    
    
    private static Map<String, Object> buildChildJson(Map<String, Object> jsonStructMap, String[] key, Object value) throws BadInputDataException {
       try {
           String elementAt = key[0];
           if (elementAt.contains("[") && elementAt.contains("]")) {
               buildArrayOfObject(jsonStructMap, key, value, elementAt);
           } else {
               if (key.length == 1) {
                   jsonStructMap.put(elementAt, value);
                   return jsonStructMap;
               }
               buildMapAsJson(jsonStructMap, key, value, elementAt);
           }
       }catch (Exception e){
            throw new BadInputDataException("Bad input exception :" + Arrays.toString(key));
       }
        return jsonStructMap;
    }
    
    private static void buildMapAsJson(Map<String, Object> jsonStructMap, String[] key, Object value, String elementAt) throws BadInputDataException {
        Map<String, Object> obj = extractDirectMap(jsonStructMap, elementAt);
        populateKeyPath(key, value, obj);
        jsonStructMap.put(elementAt, obj);
    }
    
    private static void populateKeyPath(String[] key, Object value, Map<String, Object> obj) throws BadInputDataException {
        String[] subKey = new String[key.length - 1];
        System.arraycopy(key, 1, subKey, 0, subKey.length);
        buildChildJson(obj, subKey, value);
    }
    
    private static Map<String, Object> extractDirectMap(Map<String, Object> jsonStructMap, String elementAt) {
        if (jsonStructMap.get(elementAt) != null) {
            return (Map<String, Object>) jsonStructMap.get(elementAt);
        } else {
            return new HashMap<String, Object>();
        }
    }
    
    private static void buildArrayOfObject(Map<String, Object> jsonStructMap, String[] key, Object value, String elementAt) throws BadInputDataException {
        String elementAtArray = elementAt.substring(0, elementAt.indexOf("["));
        String indexStr = elementAt.substring(elementAt.indexOf("[") + 1, elementAt.indexOf("]"));
        if(indexStr.length() ==0){
            throw new BadInputDataException("Index missing for the json path :" + elementAt );
        }
        int index = Integer.parseInt(indexStr);
        List<Map<String, Object>> elementList = extractElementList(jsonStructMap, elementAtArray);
        Map<String, Object> objListMap = extractMap(index, elementList);
        populateKeyPath(key, value, objListMap);
        populateList(index, elementList, objListMap);
        jsonStructMap.put(elementAtArray, elementList);
    }
    
    private static void populateList(int index, List<Map<String, Object>> elementList, Map<String, Object> objListMap) {
        try {
            elementList.set(index, objListMap);
        } catch (IndexOutOfBoundsException E) {
            elementList.add(index, objListMap);
        }
    }
    
    private static Map<String, Object> extractMap(int index, List<Map<String, Object>> elementList) {
        if (elementList.size() > index) {
            return (Map<String, Object>) elementList.get(index);
        } else {
            return new HashMap<String, Object>();
        }
    }
    
    private static List<Map<String, Object>> extractElementList(Map<String, Object> jsonStructMap, String elementAtArray) {
        List<Map<String, Object>> elementList = new ArrayList<>();;
        if (jsonStructMap.containsKey(elementAtArray)) {
            Object objectList = jsonStructMap.get(elementAtArray);
            if (objectList != null) {
                elementList = (List<Map<String, Object>>) objectList;
            }
        }
        return elementList;
    }
    
    
    
    private static void getJSONPath(Object jsonObject, String key, Map<String, String>  mapsonMap) {
        if (jsonObject instanceof JSONObject) {
            getSubJson((JSONObject) jsonObject, key, mapsonMap);
        } else  if (jsonObject instanceof JSONArray) {
            JSONArray jsonArray = ((JSONArray) jsonObject);
            int index = 0;
            getSubArrayPath(key, mapsonMap, jsonArray, index);
        }
        return;
    }
    
    private static void getSubJson(JSONObject jsonObject, String key, Map<String, String> mapsonMap) {
        JSONObject jsonObject1 = jsonObject;
        Iterator<String> keys = jsonObject1.keys();
        while (keys.hasNext()) {
            String keey = keys.next();
            String keeey = key == null ? keey : key +"."+ keey;
            getSubPath(mapsonMap, jsonObject1, keey, keeey);
        }
    }
    
    private static void getSubArrayPath(String key, Map<String, String> mapsonMap, JSONArray jsonArray, int index) {
        for (Iterator<Object> iterator = (jsonArray).iterator(); iterator.hasNext(); ) {
            Object obj = iterator.next();
            if(obj instanceof JSONObject || obj instanceof  JSONArray) {
                getJSONPath(obj, key + "[" + index++ + "]", mapsonMap);
            } else {
                String prefix = DataTypeHelper.getPrefixType(obj);
                mapsonMap.put(key + "[" + index++ + "]", prefix + obj);
            }
        }
    }
    
    private static void getSubPath(Map<String, String> mapsonMap, JSONObject jsonObject1, String keey, String keeey) {
        if (jsonObject1.optJSONArray(keey) != null) {
            getJSONPath(jsonObject1.optJSONArray(keey), keeey, mapsonMap);
        } else if (jsonObject1.optJSONObject(keey) != null) {
            getJSONPath((JSONObject) jsonObject1.get(keey), keeey, mapsonMap);
        } else {
            String prefix = DataTypeHelper.getPrefixType(jsonObject1.get(keey));
            mapsonMap.put(keeey, prefix + jsonObject1.get(keey));
        }
    }
    
}