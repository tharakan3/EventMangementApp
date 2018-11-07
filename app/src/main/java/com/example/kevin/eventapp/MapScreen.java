package com.example.kevin.eventapp;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class MapScreen extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);
        FragmentManager Fm  = getSupportFragmentManager();
        Fragment frag = Fm.findFragmentById(R.id.fc);
        if(frag==null){
            frag = new Map();
            Fm.beginTransaction().add(R.id.fc,frag).commit();

        }
    }

}
