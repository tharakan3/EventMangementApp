package com.example.kevin.eventapp;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapScreen extends FragmentActivity {
    Button bcreate,bprofile,bsearch;
    Intent ip,ic,ise;
    ImageButton refreshButton ;
    public static List<Event> events = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle eventsBundle = getIntent().getExtras();
        if(eventsBundle != null){
            events = (ArrayList<Event>)getIntent().getSerializableExtra("eventlist");
            //Log.d("Activity1", events.get(0).getName());
        }


        setContentView(R.layout.activity_map_screen);
        refreshButton = (ImageButton) findViewById(R.id.refresh1);
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

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CollectionReference eventsRef = db.collection("Events");
                //final List<Event> events = new ArrayList<Event>();
                Query query = eventsRef.whereArrayContains("users", LoginActivity.session.getuserId());

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Activity1", document.getId() + " => " + document.getData());
                                java.util.Map<String, Object> docs = document.getData();
                                //Event event = (Event) doc.get(document.getId());
                                //if(docs.get("location") != null){
                                //GeoPoint gp = (GeoPoint) docs.get("location");
                                //Double dist = distanceTo(coordinates.latitude,gp.getLatitude(),coordinates.longitude, gp.getLongitude());
                                //Log.d("Activity1", dist.toString());
                                //if( (rangeinKm != null && coordinates != null && dist <= rangeinKm) || (rangeinKm == null || coordinates == null)){
                                //Log.d("Activity1", (String)docs.get("tags"));
                                Event event = new Event();
                                if(docs.get("tags") != null)
                                    event.setTags((String)docs.get("tags"));
                                if(docs.get("name") != null)
                                    event.setName((String)docs.get("name"));
                                if(docs.get("eventId") != null)
                                    event.setEventId((String)docs.get("eventId"));
                                if(docs.get("organiserId") != null)
                                    event.setOrganiserId((String)docs.get("organiserId"));

                                if((docs.get("location") != null)){
                                    GeoPoint gp1 = (GeoPoint)docs.get("location");
                                    event.setLat(gp1.getLatitude());
                                    event.setLng(gp1.getLongitude());
                                }
                                events.add(event);

                                // }

                                //}



                                //events.add(event);
                            }
                            Intent intent = new Intent(getApplicationContext(), MapScreen.class);
                            //Serializable eventList = (Serializable)events;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("eventlist", (Serializable) events);
                            intent.putExtras(bundle);
                            startActivity(intent);


                        } else {
                            Log.d("Activity1", "Error getting documents: ", task.getException());
                        }
                    }
                });

            }
        });



    }

    public List<Event> Events( ){
      return events;

    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent intent = new Intent(getApplicationContext(), InviteNotificationService.class);
        stopService(intent);
        Intent setIntent = new Intent(getApplicationContext(),LoginActivity.class);

        startActivity(setIntent);
    }

}
