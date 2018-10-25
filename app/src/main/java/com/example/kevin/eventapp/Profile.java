package com.example.kevin.eventapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Profile extends AppCompatActivity {
    private TextView user;
    private Button search;
    private Button create;
    String name;
    String ID;
    Intent intpro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Activity2", "onCreate: Activity Created");
        setContentView(R.layout.activity_profile);
        user = (TextView)findViewById(R.id.user_name);
        name = getIntent().getStringExtra("username");
        user.setText(name);
        search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intpro = new Intent(getApplicationContext(),SearchEvent.class);
                ID = getIntent().getStringExtra("userId");
                intpro.putExtra("userId",ID);
                startActivity(intpro);
            }

        });

        create = (Button)findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intpro = new Intent(getApplicationContext(),EventActivity.class);
                ID = getIntent().getStringExtra("userId");
                intpro.putExtra("userId",ID);
                startActivity(intpro);
            }
        });

    }


    protected void onResume() {
        super.onResume();
        Log.d("Activity2", "onStart: Activity Resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Activity2","onPause: Acitvity Paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Activity2", "onStop: Activity Stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Activity2", "onDestroy: Activity Destroyed");
    }
}
