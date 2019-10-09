package io.virtualan.mapson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.*;

public class Mapson {
    
    public static String buildMapAsJsonString(Map<String, String> jsonStructMap) {
        Map<String, Object> params = new HashMap<>();
        for (Map.Entry<String, String> mapsonEntry : jsonStructMap.entrySet()) {
            String key = mapsonEntry.getKey();
            if (key.indexOf(".") != -1) {
                params = buildChildJson(params, key.split("\\."), mapsonEntry.getValue());
            } else if (key.contains("[") && key.contains("]")) {
                String elementAt = key.substring(0, key.indexOf("["));
                params.put(key, buildObjectList(params, key, mapsonEntry.getValue()));
            } else {
                params.put(key, mapsonEntry.getValue());
            }
        }
        String json = buildJsonString(params).toString();
        return json;
    }
    
    public static String buildMapAsJsonString(Map<String, String> jsonStructMap, Map<String, Object> contextObject ) {
        Map<String, Object> params = new HashMap<>();
        for (Map.Entry<String, String> mapsonEntry : jsonStructMap.entrySet()) {
            String key = mapsonEntry.getKey();
            if (key.indexOf(".") != -1) {
                params = buildChildJson(params, key.split("\\."), mapsonEntry.getValue());
            } else if (key.contains("[") && key.contains("]")) {
                String elementAt = key.substring(0, key.indexOf("["));
                params.put(key, buildObjectList(params, key, mapsonEntry.getValue()));
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
    
    private static String getPrefixType(JSONObject jsonObject1, String keey) {
        String prefix = "";
        if(jsonObject1.get(keey) instanceof Double ) {
            prefix = "d~";
        } else if (jsonObject1.get(keey) instanceof Integer) {
            prefix = "i~";
        } else if (jsonObject1.get(keey) instanceof Boolean) {
            prefix = "b~";
        } else if (jsonObject1.get(keey) instanceof Long) {
            prefix = "l~";
        } else if (jsonObject1.get(keey) instanceof Float) {
            prefix = "f~";
        }
        return prefix;
    }
    
    private static Map<String, Object> buildChildJson(Map<String, Object> jsonStructMap, String[] key, Object value) {
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
        return jsonStructMap;
    }
    
    private static void buildMapAsJson(Map<String, Object> jsonStructMap, String[] key, Object value, String elementAt) {
        Map<String, Object> obj = extractDirectMap(jsonStructMap, elementAt);
        populateKeyPath(key, value, obj);
        jsonStructMap.put(elementAt, obj);
    }
    
    private static void populateKeyPath(String[] key, Object value, Map<String, Object> obj) {
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
    
    private static void buildArrayOfObject(Map<String, Object> jsonStructMap, String[] key, Object value, String elementAt) {
        String elementAtArray = elementAt.substring(0, elementAt.indexOf("["));
        int index = Integer.parseInt(elementAt.substring(elementAt.indexOf("[") + 1, elementAt.indexOf("]")));
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
    
    
    public static Map<String, String> buildMapson(String json) {
        JSONTokener jsonTokener = new JSONTokener(json);
        JSONObject jsonObject = new JSONObject(jsonTokener);
        Map<String, String>  mapsonMap = new LinkedHashMap<>();
        getJSONPath(jsonObject, null , mapsonMap);
        return mapsonMap;
    }
    
    public static void getJSONPath(Object jsonObject, String key, Map<String, String>  mapsonMap) {
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
                    String prefix = getPrefixType(jsonObject1, keey);
                    mapsonMap.put(keeey, prefix + jsonObject1.get(keey));
                }
            }
        } else  if (jsonObject instanceof JSONArray) {
            JSONArray jsonArray = ((JSONArray) jsonObject);
            int index = 0;
            for (Iterator<Object> iterator = (jsonArray).iterator(); iterator.hasNext(); ) {
                Object obj = iterator.next();
                getJSONPath(obj, key +"["+ index++ +"]", mapsonMap);
            }
        }
        return;
    }
    

    
}