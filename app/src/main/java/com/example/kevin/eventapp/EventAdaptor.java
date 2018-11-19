package com.example.kevin.eventapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Map;

public class EventAdaptor extends RecyclerView.Adapter<EventAdaptor.EventViewholder> {


    public List<Event> listitem;
    public Context cont;
    private String userid;
    private List<Event> events;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static class EventViewholder extends RecyclerView.ViewHolder{
        public TextView ename;
        public Button edel;
        public Button eview;

        public EventViewholder(View itemView) {
            super(itemView);
            edel = (Button)itemView.findViewById(R.id.del);
            eview = (Button)itemView.findViewById(R.id.eview);
            ename = (TextView) itemView.findViewById(R.id.ename);

        }
    }

    public EventAdaptor(List<Event> listitem, Context cont) {
        this.listitem = listitem;
        this.cont = cont;
    }


    @Override
    public EventAdaptor.EventViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,parent,false);
        return new EventViewholder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull EventViewholder holder, int position) {
        Log.d("Activity1", " "+ position);
        final Event eve = listitem.get(position);
        holder.ename.setText(eve.getName());
        userid = LoginActivity.session.getuserId();
        events = new ArrayList<>();
        holder.eview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.session.setEventId(eve.getEventId());
                Intent intent = new Intent(cont, EventActivity.class);
                intent.putExtra("eventId", eve.getEventId());
                intent.putExtra("userId", eve.getOrganiserId());
                cont.startActivity(intent);
            }
        });

        holder.edel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.session.setEventId(eve.getEventId());
                db.collection("Events").document(eve.getEventId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Activity1", "DocumentSnapshot successfully deleted!");
                                CollectionReference eventsRef = db.collection("Events");
                                //final List<Event> events = new ArrayList<Event>();
                                Query query = eventsRef.whereArrayContains("users", userid);
                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("Activity1", document.getId() + " => " + document.getData());
                                                Map<String, Object> docs = document.getData();
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

                                            }
                                            Intent intent = new Intent(cont, MapScreen.class);
                                            //Serializable eventList = (Serializable)events;
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("eventlist", (Serializable) events);
                                            intent.putExtras(bundle);
                                            cont.startActivity(intent);


                                        } else {
                                            Log.d("Activity1", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("", "Error deleting document", e);
                            }
                        });



            }
        });

    }

    @Override
    public int getItemCount() {
        return listitem.size();
    }


}
