package com.example.kevin.eventapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.Map;

public class SearchEvent extends AppCompatActivity {


    private String userid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText sname;
    private EditText stag;
    private Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_event);
        sname = (EditText)findViewById(R.id.sname);
        stag = (EditText)findViewById(R.id.stags);
        search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userid = getIntent().getStringExtra("userId");

                Intent second = new Intent(getApplicationContext(),EventListActivity.class);
                second.putExtra("userId",userid);
                second.putExtra("tags",stag.getText().toString());
                second.putExtra("name",sname.getText().toString());
                //second.putExtra("date", new Date());
                //second.putExtra("tags", "f,s,s,s");
                startActivityForResult(second,0);
            }
        });
    }

    public List<Event> getEvents(Date date, String tags, String name) {
        CollectionReference eventsRef = db.collection("Events");
        final List<Event> events = new ArrayList<Event>();
        Query query = eventsRef.whereArrayContains("users", userid).whereEqualTo("name", name);//.whereEqualTo("tags", tags);//.whereEqualTo("date", date);
        if(date != null){
            query = query.whereEqualTo("date",date);
        }
        /*if(tags != null){
            query = query.whereEqualTo("tags",tags);
        }*/
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
                        //events.add(event);
                    }
                } else {
                    Log.d("Activity1", "Error getting documents: ", task.getException());
                }
            }
        });

        //query.ad
        return events;
    }
}
