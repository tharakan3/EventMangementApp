package com.example.kevin.eventapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    // UI references.
    private EditText mPasswordView;
    private EditText mUserNameView;
    private Button mSigninButton;
    private Button mRegisterButton;
    public static Session session;
    private List<Event> events;
    Intent int1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        session = new Session(getApplicationContext());
        mPasswordView = (EditText) findViewById(R.id.passwordLogin);
        mUserNameView = (EditText) findViewById(R.id.usernameLogin);

        mSigninButton = (Button) findViewById(R.id.sign_in_button);

        mRegisterButton = (Button)findViewById(R.id.register);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int1 = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(int1);
            }
        });
        events = new ArrayList<>();
        mSigninButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = db.collection("usersnew").whereEqualTo("name", mUserNameView.getText().toString()).whereEqualTo("password", mPasswordView.getText().toString());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Activity1", document.getId() + " => " + document.getData());
                                Map<String, Object> docs = document.getData();
                                String name = (String)docs.get("name");
                                String password = (String)docs.get("password");
                                if(password.equals(mPasswordView.getText().toString()))
                                {

                                    session.setuserId(document.getId());

                                    CollectionReference eventsRef = db.collection("Events");
                                    //final List<Event> events = new ArrayList<Event>();
                                    Query query = eventsRef.whereArrayContains("users", document.getId());

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

                                                    // }

                                                    //}



                                                    //events.add(event);
                                                }
                                                Intent intent = new Intent(getApplicationContext(), MapScreen.class);
                                                //Serializable eventList = (Serializable)events;
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("eventlist", (Serializable) events);
                                                intent.putExtras(bundle);
                                                startActivity(intent);


                                            } else {
                                                Log.d("Activity1", "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });

                                    /*int1 = new Intent(getApplicationContext(),MapScreen.class);
                                    startActivity(int1);



                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("eventlist", (Serializable) events);
                                    intent.putExtras(bundle);
                                    startActivity(intent);*/

//                                    int1 = new Intent(getApplicationContext(),EventActivity.class);
//                                    startActivity(int1);

                                    /*int1 = new Intent(getApplicationContext(),SearchEvent.class);
                                    startActivity(int1);*/


//                                    int1 = new Intent(getApplicationContext(),Profile.class);
//                                    //int1.putExtra("userId",(String)docs.get("userId"));
//                                    startActivity(int1);

                                   /* int1 = new Intent(getApplicationContext(),EventActivity.class);
                                    int1.putExtra("userId",(String)docs.get("userId"));
                                    startActivity(int1);*/
                                    //startActivity(new Intent(getApplicationContext(),EventInviteActivity.class));
                                }

                                //Event event = (Event) doc.get(document.getId());
                                //Log.d("Activity1", (String) docs.get("tags"));
                                //events.add(event);
                            }
                        } else {
                            Log.d("Activity1", "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });


    }
}

