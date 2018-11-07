package com.example.kevin.eventapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class DataParser {
    private HashMap<String,String>getPlace(JSONObject googlePlaceJson){
        HashMap<String, String>googlePlaceMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longtitude = "";
        String reference = "";
        try {
            if (!googlePlaceJson.isNull("name")) {

                placeName = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {

                vicinity = googlePlaceJson.getString("vicinity");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longtitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference  = googlePlaceJson.getString("reference");

            googlePlaceMap.put("place_name",placeName);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",latitude);
            googlePlaceMap.put("lng",longtitude);
            googlePlaceMap.put("reference",reference);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }

    public HashMap<String,String>parseDirections(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getDuration(jsonArray);

    }


    private HashMap<String,String> getDuration(JSONArray googleDirectionsJson) {
        HashMap<String,String> googleDirectionsMap = new HashMap<>();
        String duration = "";
        String distance = "";
        try {
            duration = googleDirectionsJson.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = googleDirectionsJson.getJSONObject(0).getJSONObject("duration").getString("text");
            googleDirectionsMap.put("duration",duration);
            googleDirectionsMap.put("distance",distance);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googleDirectionsMap;
    }

}
