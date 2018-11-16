package com.example.kevin.eventapp;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MapScreen extends FragmentActivity {
    Button bcreate,bprofile,bsearch;
    Intent ip,ic,ise;
    public static List<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle eventsBundle = getIntent().getExtras();
        if(eventsBundle != null){
            events = (ArrayList<Event>)getIntent().getSerializableExtra("eventlist");
            //Log.d("Activity1", events.get(0).getName());
        }


        setContentView(R.layout.activity_map_screen);
        FragmentManager Fm  = getSupportFragmentManager();
        Fragment frag = Fm.findFragmentById(R.id.fc);
        if(frag==null){
            frag = new Map();
            Fm.beginTransaction().add(R.id.fc,frag).commit();

        }
        bcreate = (Button)findViewById(R.id.bcreate);
        bprofile = (Button)findViewById(R.id.bprofile);
        bsearch = (Button)findViewById(R.id.bsearch);
        bsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ise = new Intent(getApplicationContext(),SearchEvent.class);
                startActivity(ise);

            }
        });

        bcreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ic = new Intent(getApplicationContext(),EventActivity.class);
                startActivity(ic);
            }
        });

        bprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ip = new Intent(getApplicationContext(),Profile.class);
                startActivity(ip);

            }
        });


    }

    public List<Event> Events( ){
      return events;

    }

}
