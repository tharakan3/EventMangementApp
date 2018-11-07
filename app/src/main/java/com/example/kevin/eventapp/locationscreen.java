package com.example.kevin.eventapp;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.EditText;

public class locationscreen extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditText editText = (EditText)findViewById(R.id.search);

        setContentView(R.layout.activity_locationscreen);
        FragmentManager Fm = getSupportFragmentManager();
        Fragment frag = Fm.findFragmentById(R.id.fcl);
        if (frag == null) {
            frag = new Map();
            Fm.beginTransaction().add(R.id.fcl, frag).commit();

        }
    }

}
