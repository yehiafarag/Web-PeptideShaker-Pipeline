package com.uib.web.peptideshaker.galaxy.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.jettison.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * This class will include required API connections that is no longer supported
 * in blind4Jn
 *
 * @author Yehia Farag
 */
public class GalaxyAPIInteractiveLayer {

    public List<Map<String, Object>> getDatasetIdList(String galaxyUrl, String apiKey) {
       

        try {

            URL website = new URL(galaxyUrl + "/api/datasets/?key=" + apiKey);
            URLConnection conn = website.openConnection();
            conn.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01;text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

            String stringToParse;
            try (BufferedReader bin = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                stringToParse = bin.readLine();
            }
            JSONParser parser = new JSONParser();
            JSONArray json = (JSONArray) parser.parse(stringToParse);
            List<Object> dataList = toList(json);
            filterDatasets(dataList);
            
            return filterDatasets(dataList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
    public String getUserMemoryUsage(String galaxyUrl,String userId, String apiKey) {
       

        try {

            URL website = new URL(galaxyUrl + "/api/users/"+userId+"?key=" + apiKey);
            URLConnection conn = website.openConnection();
            conn.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01;text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

            String stringToParse;
            try (BufferedReader bin = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                stringToParse = bin.readLine();
            }
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(stringToParse);
            Map<String,Object>dataList = jsonToMap(json);
            return dataList.get("nice_total_disk_usage")+"";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Not Available";
    }

    private List<Map<String, Object>> filterDatasets(List<Object> dataList) {
        List<Map<String, Object>>convertedList =new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> datasetMap = (Map<String, Object>) dataList.get(i);
            if (datasetMap.get("purged").toString().equalsIgnoreCase("true") || datasetMap.get("deleted").toString().equalsIgnoreCase("true")) {
                dataList.remove(i);
                continue;
            }
            convertedList.add(datasetMap);

        }
        return convertedList;

    }

    /**
     * Convert JSON object to Java readable map
     *
     * @param object JSON object to be converted
     * @return Java Hash map has all the data
     * @throws JSONException in case of error in reading JSON file
     */
    private Map<String, Object> jsonToMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keySet().iterator();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * Convert JSON object to Java readable list
     *
     * @param object JSON object to be converted
     * @return Java List has all the data
     * @throws JSONException in case of error in reading JSON file
     */
    private List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

}
