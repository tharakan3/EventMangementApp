package com.example.kevin.eventapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;
import static android.widget.Toast.LENGTH_LONG;
import static com.example.kevin.eventapp.Constants.CONNECTIVITY_ERROR_MESSAGE;
import static com.example.kevin.eventapp.Constants.EMPTY_FIELD_ERROR_MESSAGE;
import static com.example.kevin.eventapp.Constants.WRONG_PASSWORD_ERROR_MESSAGE;

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
    private Context context;
    private boolean isConnected;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo activeNetwork;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    /*FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build();
    db.setFirestoreSettings(settings);*/

    Intent int1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        session = new Session(getApplicationContext());
        context = getApplicationContext();

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
                boolean isNetworkCnted = Utils.isNetworkConnected(context);
                boolean isValidField = validateFields();
                if(isNetworkCnted && isValidField){
                    final String sha256hex = Hashing.sha256()
                            .hashString(mPasswordView.getText().toString(), StandardCharsets.UTF_8)
                            .toString();
                    Query query = db.collection("usersnew").whereEqualTo("name", mUserNameView.getText().toString()).whereEqualTo("password",sha256hex);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("Activity1", document.getId() + " => " + document.getData());
                                    Map<String, Object> docs = document.getData();
                                    String name = (String)docs.get("name");
                                    String password = (String)docs.get("password");

                                    if(password.equals(sha256hex))
                                    {
                                        Intent intent = new Intent(getApplicationContext(), InviteNotificationService.class);
                                        startService(intent);

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


                                    }
                                }
                                Toast.makeText(getApplicationContext(),WRONG_PASSWORD_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("Activity1", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                }
                else if(!isNetworkCnted){
                    Toast.makeText(context, CONNECTIVITY_ERROR_MESSAGE, LENGTH_LONG).show();
                }

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Activity1", "onResume: Login Activity Resumed");
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Invitee Notificaton";
            String description = "Channel to Show to Invitee Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("2", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public boolean validateFields(){

        if(Utils.isStringNullorEmpty(mUserNameView.getText().toString()) || Utils.isStringNullorEmpty(mPasswordView.getText().toString())){
            Toast.makeText(getApplicationContext(),EMPTY_FIELD_ERROR_MESSAGE, Toast.LENGTH_SHORT ).show();
            return false;
        }
        return true;
    }
}

