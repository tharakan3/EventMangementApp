package com.example.kevin.eventapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setuserId(String userid) {
        prefs.edit().putString("userid", userid).commit();
    }

    public String getuserId() {
        String usename = prefs.getString("userid","");
        return usename;
    }

    public void setEventId(String eventId){
        prefs.edit().putString("eventId", eventId).commit();
    }

    public String getEventId() {
        String eventId = prefs.getString("eventId","");
        return eventId;
    }

    public void setuserName(String username) {
        prefs.edit().putString("username", username).commit();
    }

    public String getuserName() {
        String usename = prefs.getString("username", "");
        return usename;
     };

    }