package com.example.kevin.eventapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;




import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventListActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.Adapter recyclerViewAdaptor;
    public List<Event> events ;
    public String[] ev = new String[]{"fefa", "efe"};
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    //boolean isdone = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        //final ListView listview = (ListView) findViewById(R.id.listview);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        events = new ArrayList<>();

            //le.add(e1);
        String tags = getIntent().getStringExtra("tags");
        String name = getIntent().getStringExtra("name");
        String userid = getIntent().getStringExtra("userId");
        Date date = null;

            CollectionReference eventsRef = db.collection("Events");
            //final List<Event> events = new ArrayList<Event>();
            Query query = eventsRef.whereArrayContains("users", userid);//.whereEqualTo("name", name);//.whereEqualTo("tags", tags);//.whereEqualTo("date", date);

        if(tags != null && !"".equals(tags)){
            query = query.whereEqualTo("tags",tags);
        }

        if(name != null && !"".equals(name)){
            query = query.whereEqualTo("name",name);
        }
            Log.d("Activity1", "reached getevents");
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            events.add(event);

                            //events.add(event);
                        }
                        recyclerViewAdaptor = new EventAdaptor(events,EventListActivity.this);
                        recyclerView.setAdapter(recyclerViewAdaptor);
                    } else {
                        Log.d("Activity1", "Error getting documents: ", task.getException());
                    }
                }
            });


        //while(!isdone);
        //Log.d("Activity1", ""+events.size());
        /*Event event = new Event();
        event.setTags("ts,ts1");
        event.setName("test1");
        //event.setDate(new Date());
        events.add(event);
        Log.d("Activity1", ""+events.size());
        recyclerViewAdaptor = new EventAdaptor(ev,this);
        recyclerView.setAdapter(recyclerViewAdaptor);*/



    }


}
