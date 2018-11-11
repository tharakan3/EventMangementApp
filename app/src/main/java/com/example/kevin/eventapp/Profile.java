package com.example.kevin.eventapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Profile extends AppCompatActivity {
    private TextView user;
    private Button search;
    private Button create;
    String name;
    String ID;
    Intent intpro;
    public RecyclerView eventListView;
    public RecyclerView.Adapter eventListViewAdaptor;
    public RecyclerView invitesListView;
    public RecyclerView.Adapter inviteesListViewAdaptor;
    public List<Event> myEvents ;
    public List<Event> invites;
    private String userid;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Activity2", "onCreate: Activity Created");
        setContentView(R.layout.activity_profile);
        user = (TextView)findViewById(R.id.user_name);
        //name = getIntent().getStringExtra("username");
        //user.setText(name);
        Session session = new Session(getApplicationContext());
        userid = LoginActivity.session.getuserId();
        Log.d("Activity1", "userid " + userid);
        /*search = (Button)findViewById(R.id.search);
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
        });*/

        eventListView = (RecyclerView)findViewById(R.id.eventListView);
        eventListView.setHasFixedSize(true);
        eventListView.setLayoutManager(new LinearLayoutManager(this));

        CollectionReference eventsRef = db.collection("Events");
        //final List<Event> events = new ArrayList<Event>();
        Query query = eventsRef.whereEqualTo("organiserId", userid);//.whereEqualTo("name", name);//.whereEqualTo("tags", tags);//.whereEqualTo("date", date);
        myEvents = new ArrayList<>();
        Log.d("Activity1", "reached getevents");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Activity1", document.getId() + " => " + document.getData());
                        Map<String, Object> docs = document.getData();
                        //Event event = (Event) doc.get(document.getId());
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

                        myEvents.add(event);

                        //events.add(event);
                    }
                    eventListViewAdaptor = new EventAdaptor(myEvents,Profile.this);
                    eventListView.setAdapter(eventListViewAdaptor);
                } else {
                    Log.d("Activity1", "Error getting documents: ", task.getException());
                }
            }
        });


        invitesListView = (RecyclerView)findViewById(R.id.InviteesListView);
        invitesListView.setHasFixedSize(true);
        invitesListView.setLayoutManager(new LinearLayoutManager(this));

        CollectionReference eventInviteRef = db.collection("Events");
        //final List<Event> events = new ArrayList<Event>();
        Query queryInvitees = eventsRef.whereArrayContains("invitees", userid);//.whereEqualTo("name", name);//.whereEqualTo("tags", tags);//.whereEqualTo("date", date);
        invites = new ArrayList<>();
        Log.d("Activity1", "reached getevents");
        queryInvitees.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Activity1", document.getId() + " => " + document.getData());
                        Map<String, Object> docs = document.getData();
                        //Event event = (Event) doc.get(document.getId());
                        Log.d("Activity1", (String)docs.get("tags"));
                        Event event = new Event();
                        event.setTags((String)docs.get("tags"));
                        event.setName((String)docs.get("name"));
                        event.setEventId((String)docs.get("eventId"));
                        event.setOrganiserId((String)docs.get("organiserId"));

                        invites.add(event);

                        //events.add(event);
                    }
                    inviteesListViewAdaptor = new InviteAdaptor(invites,Profile.this);
                    invitesListView.setAdapter(inviteesListViewAdaptor);
                } else {
                    Log.d("Activity1", "Error getting documents: ", task.getException());
                }
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
