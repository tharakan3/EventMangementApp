package com.example.kevin.eventapp;

import com.google.firebase.firestore.FirebaseFirestore;

public class EventsDBUtil {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void updateEvent(String eventId, Event event){
        //update using event id
    }

    public static void deleteEvent(String eventId){

    }
}
