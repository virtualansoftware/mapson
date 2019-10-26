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


public class MAPson {
    
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
    
    public static String buildMAPsonAsJson(Map<String, String> jsonStructMap, Map<String, Object> contextObject ) throws BadInputDataException {
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
    
    private static JSONObject buildJsonString(Map<String, Object> jsonStructMap, Map<String, Object> contextObject) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> mapEntry : jsonStructMap.entrySet()) {
            if (mapEntry.getValue() instanceof Map) {
                setObject(jsonObject, mapEntry.getKey(), buildJsonString((Map<String, Object>) mapEntry.getValue(), contextObject));
            } else if (mapEntry.getValue() instanceof List) {
                JSONArray array = new JSONArray();
                for (Object object : (List<Object>) mapEntry.getValue()) {
                    if (object instanceof Map) {
                        JSONObject obj = buildJsonString((Map<String, Object>) object);
                        array.put(getActualValue(obj, contextObject));
                    }
                }
                setObject(jsonObject, mapEntry.getKey(), array);
            } else {
                setObject(jsonObject, mapEntry.getKey(), getActualValue(mapEntry.getValue(), contextObject));
            }
            
        }
        return jsonObject;
    }
    
    private static JSONObject buildJsonString(Map<String, Object> jsonStructMap) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> mapEntry : jsonStructMap.entrySet()) {
            if (mapEntry.getValue() instanceof Map) {
                setObject(jsonObject, mapEntry.getKey(), buildJsonString((Map<String, Object>) mapEntry.getValue()));
            } else if (mapEntry.getValue() instanceof List) {
                JSONArray array = new JSONArray();
                for (Object object : (List<Object>) mapEntry.getValue()) {
                    if (object instanceof Map) {
                        JSONObject obj = buildJsonString((Map<String, Object>) object);
                        array.put(obj);
                    } else  {
                        setObjectByType(array, object);
                    }
                }
                setObject(jsonObject, mapEntry.getKey(), array);
            } else {
                setObject(jsonObject, mapEntry.getKey(), mapEntry.getValue());
            }
            
        }
        return jsonObject;
    }
    
    private static Object getActualValue(Object object, Map<String, Object> contextObject) {
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
    private static void setObject(JSONObject jsonObject, String key, Object value) {
        if (value instanceof String) {
            String newValue = (String) value;
            if (newValue.indexOf("~") == 1) {
                setObjectByType(jsonObject, key, value, newValue);
                return;
            }
        }
        jsonObject.put(key, value);
    }
    
    private static void setObjectByType(JSONArray jsonArray, Object value) {
        String[] arrayValue = value.toString().split("~");
        if ("i".equals(arrayValue[0])) {
            jsonArray.put(Integer.parseInt(arrayValue[1]));
        } else if ("b".equals(arrayValue[0])) {
            jsonArray.put( Boolean.parseBoolean(arrayValue[1]));
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
    
    private static void setObjectByType(JSONObject jsonObject, String key, Object value, String newValue) {
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
    
    private static String getPrefixType(Object object) {
        String prefix = "";
        if(object instanceof Double ) {
            prefix = "d~";
        } else if (object  instanceof Integer) {
            prefix = "i~";
        } else if (object  instanceof Boolean) {
            prefix = "b~";
        } else if (object  instanceof Long) {
            prefix = "l~";
        } else if (object instanceof Float) {
            prefix = "f~";
        }
        return prefix;
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
        List<Map<String, Object>> elementList;
        if (jsonStructMap.containsKey(elementAtArray)) {
            Object objectList = jsonStructMap.get(elementAtArray);
            if (objectList == null) {
                elementList = new ArrayList<>();
            } else {
                elementList = (List<Map<String, Object>>) objectList;
            }
        } else {
            elementList = new ArrayList<>();
        }
        return elementList;
    }
    
    
    
    private static void getJSONPath(Object jsonObject, String key, Map<String, String>  mapsonMap) {
        if (jsonObject instanceof JSONObject) {
            JSONObject jsonObject1 = (JSONObject)jsonObject;
            Iterator<String> keys = jsonObject1.keys();
            while (keys.hasNext()) {
                String keey = keys.next();
                String keeey = key == null ? keey : key +"."+ keey;
                if (jsonObject1.optJSONArray(keey) != null) {
                    getJSONPath(jsonObject1.optJSONArray(keey), keeey, mapsonMap);
                } else if (jsonObject1.optJSONObject(keey) != null) {
                    getJSONPath((JSONObject) jsonObject1.get(keey), keeey, mapsonMap);
                } else {
                    String prefix = getPrefixType(jsonObject1.get(keey));
                    mapsonMap.put(keeey, prefix + jsonObject1.get(keey));
                }
            }
        } else  if (jsonObject instanceof JSONArray) {
            JSONArray jsonArray = ((JSONArray) jsonObject);
            int index = 0;
            for (Iterator<Object> iterator = (jsonArray).iterator(); iterator.hasNext(); ) {
                Object obj = iterator.next();
                if(obj instanceof  JSONObject || obj instanceof  JSONArray) {
                    getJSONPath(obj, key + "[" + index++ + "]", mapsonMap);
                } else {
                    String prefix = getPrefixType(obj);
                    mapsonMap.put(key + "[" + index++ + "]", prefix + obj);
                }
            }
        }
        return;
    }
    
}