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

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EventAdaptor extends RecyclerView.Adapter<EventAdaptor.EventViewholder> {


    public List<Event> listitem;
    public Context cont;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static class EventViewholder extends RecyclerView.ViewHolder{
        public TextView ename;
        public Button edel;
        public Button eview;

        public EventViewholder(View itemView) {
            super(itemView);
            ename = (TextView)itemView.findViewById(R.id.ename);
            eview = (Button)itemView.findViewById(R.id.eview);

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

        holder.eview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.session.setEventId(eve.getEventId());
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
