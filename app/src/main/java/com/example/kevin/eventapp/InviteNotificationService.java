package com.example.kevin.eventapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

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
import java.util.HashSet;
import java.util.Map;

public class InviteNotificationService extends Service {

    private Handler mHandler;
    private static int id = 0;
    // default interval for syncing data
    private NotificationCompat.Builder mBuilder;
    private static HashSet<String> invites = new HashSet<>();
    public static final long DEFAULT_SYNC_INTERVAL = 30 * 1000;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // task to be run here
    private Runnable runnableService = new Runnable() {
        @Override
        public void run() {
            pollFirestore();
            // Repeat this runnable code block again every ... min
            mHandler.postDelayed(runnableService, 60000);
        }
    };

    @Override
    public int onStartCommand(Intent intent1, int flags, int startId) {

        // Create the Handler object
        mHandler = new Handler();
        // Execute a runnable task as soon as possible
        mHandler.post(runnableService);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private synchronized void pollFirestore() {
        CollectionReference eventsRef = db.collection("Events");
        //final List<Event> events = new ArrayList<Event>();
        Query query = eventsRef.whereArrayContains("invitees", LoginActivity.session.getuserId());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Activity1", document.getId() + " => " + document.getData());
                        Map<String, Object> docs = document.getData();
                        //Event event = (Event) doc.get(document.getId());
                        //if(docs.get("location") != null){
                        //GeoPoint gp = (GeoPoint) docs.get("location");
                        //Double dist = distanceTo(coordinates.latitude,gp.getLatitude(),coordinates.longitude, gp.getLongitude());
                        //Log.d("Activity1", dist.toString());
                        //if( (rangeinKm != null && coordinates != null && dist <= rangeinKm) || (rangeinKm == null || coordinates == null)){
                        //Log.d("Activity1", (String)docs.get("tags"));
                        ArrayList<String> invitees = null;
                        String name = "";
                        if(docs.get("invitees") != null){
                            invitees = (ArrayList)docs.get("invitees");
                        }

                        if(docs.get("name") != null){
                            name = (String)docs.get("name");
                        }
                            if(!invites.contains(document.getId())){
                                invites.add(document.getId());
                                showNotifcation(name);
                            }


                    }



                } else {
                    Log.d("Activity1", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    private void showNotifcation(String title){

        Intent intent = new Intent(this, Profile.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder = new NotificationCompat.Builder(this, "2")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Event Invitation")
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(id++, mBuilder.build());



    }


}