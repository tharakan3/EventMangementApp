package com.example.kevin.eventapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class InviteAdaptor extends RecyclerView.Adapter<InviteAdaptor.EventViewholder> {


    public List<Event> listitem;
    public Context cont;
    private String userid;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static class EventViewholder extends RecyclerView.ViewHolder{
        public Button acceptButton;
        public Button rejectButton;
        public TextView ename;

        public EventViewholder(View itemView) {
            super(itemView);
            acceptButton = (Button)itemView.findViewById(R.id.accept);
            ename = (TextView)itemView.findViewById(R.id.eventName_invite);
            rejectButton = (Button)itemView.findViewById(R.id.reject);

        }
    }

    public InviteAdaptor(List<Event> listitem, Context cont) {
        this.listitem = listitem;
        this.cont = cont;
    }


    @Override
    public InviteAdaptor.EventViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_event_item,parent,false);
        return new EventViewholder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull EventViewholder holder, int position) {
        Log.d("Activity1", " "+ position);
        final Event eve = listitem.get(position);
        holder.ename.setText(eve.getName());
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("Events").document(eve.getEventId()).update("users", FieldValue.arrayUnion(userid)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Acitivity1", "DocumentSnapshot successfully updated!");
                        db.collection("Events").document(eve.getEventId()).update("invitees", FieldValue.arrayRemove(userid)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Acitivity1", "DocumentSnapshot successfully updated!");
                                //Intent profile = new Intent(getApplicationContext(), Profile.class);
                                //profile.putExtra("userId", userid);
                                //UpdateEvent.this.startActivity(profile);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("", "Error updating document", e);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("", "Error updating document", e);
                            }
                        });
                Intent intent = new Intent(cont, UpdateEvent.class);
                intent.putExtra("eventId", eve.getEventId());
                intent.putExtra("userId", eve.getOrganiserId());
                cont.startActivity(intent);
            }
        });

        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(cont, UpdateEvent.class);
                intent.putExtra("eventId", eve.getEventId());
                intent.putExtra("userId", eve.getOrganiserId());
                cont.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listitem.size();
    }


}
