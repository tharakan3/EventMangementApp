package com.example.kevin.eventapp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventsDBUtil {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void updateEvent(String eventId, Event event){
        //update using event id
    }

    public static void deleteEvent(String eventId){

    }


    public void inviteFriends(String eventId, String userId){

        //final String eventid =
        db.collection("Events").document(eventId).update("invitees", FieldValue.arrayUnion(userId)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Acitivity1", "DocumentSnapshot successfully updated!");

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("", "Error updating document", e);
                    }
                });
    }

    public void acceptInvitation(String eventId, String userId){

        db.collection("Events").document(eventId).update("invitees", FieldValue.arrayRemove(userId)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Acitivity1", "DocumentSnapshot successfully updated!");

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("", "Error updating document", e);
                    }
                });

    }

    public void addFriend(String userId, String friendId){
        db.collection("Events").document(userId).update("invitees", FieldValue.arrayUnion(friendId)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Acitivity1", "DocumentSnapshot successfully updated!");

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("", "Error updating document", e);
                    }
                });
    }
}
