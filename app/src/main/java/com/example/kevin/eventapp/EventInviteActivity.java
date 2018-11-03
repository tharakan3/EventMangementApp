package com.example.kevin.eventapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventInviteActivity extends AppCompatActivity {

    MultiAutoCompleteTextView autocomplete;

//    String[] arr = { "Paries,France", "PA,United States","Parana,Brazil",
//            "Padua,Italy", "Pasadena,CA,United States"};
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId = "2idIs87BNKbFtgwh8VWL";
    private String eventId = "42gRgKp0tcNRWtFw84UQ";
    private Button mInviteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_invite);
        mInviteButton = (Button)findViewById(R.id.invite);
        CollectionReference userRef = db.collection("usersnew");
        userRef.document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Activity1", "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> docs = document.getData();
                        ArrayList<String> friends = (ArrayList)docs.get("friends");
                        ArrayList<String> friendNames = (ArrayList)docs.get("friendNames");
                        final HashMap<String, String> nameIdMap = new HashMap<String, String>();
                        for(int i = 0; i<friends.size();i++){
                            nameIdMap.put(friendNames.get(i), friends.get(i));
                        }

                        autocomplete = findViewById(R.id.autoCompleteTextView1);

                        ArrayList<String> names = new ArrayList<>();

                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                (EventInviteActivity.this,android.R.layout.select_dialog_item, friendNames);


//+        autocomplete.setThreshold(2);
                        autocomplete.setAdapter(adapter);
                        autocomplete.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

                        mInviteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String [] names = autocomplete.getText().toString().split(", ");
                                String[] id = new String[nameIdMap.size()];

                                for(int i = 0 ; i < names.length ;i++)
                                {
                                    id[i]=  nameIdMap.get(names[i]);
                                }
                                db.collection("Events").document(eventId).update("users", FieldValue.arrayUnion(id)).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                        });
                    } else {
                        Log.d("Activity1", "No such document");
                    }
                } else {
                    Log.d("Activity1", "get failed with ", task.getException());
                }
            }
        });

    }
}
