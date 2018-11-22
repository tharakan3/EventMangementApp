package com.example.kevin.eventapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class EventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // UI references.
    private EditText mEventName;
    private EditText mTags;
    private View mEventView;
    private View mLoginFormView;
    private EditText mUserName;
    private EditText address;
    Geocoder geo;
    private DatePicker datePicker;
    private Calendar calendar;
    static private Calendar date;
    private TextView startDate;
    private static EditText time;
    private int year, month, day;
    private static int hour, minute;
    List<Address> addressList;
    Address e1;
    private Button mAddEventButton, msetTimeButton, mAddImageButton;

    private String userid;

    private Event event;
    private String eventid = null;
    String tag;
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button mInviteButton;
    MultiAutoCompleteTextView autocomplete;
    private HashMap<String, String> nameIdMap;
    private HashMap<String, String> idNameMap;
    private Map<String, Object> eventMap;
    private static Map<String, Event> eventCache;
    private List<Event> events;
    String[] tags;
    Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        if(eventCache == null){
            eventCache = new HashMap<>();
        }
        eventid = getIntent().getStringExtra("eventId");
        //userid = getIntent().getStringExtra("userId");
        userid = LoginActivity.session.getuserId();
        address = findViewById(R.id.addr);
        mInviteButton = (Button)findViewById(R.id.invite);

        geo = new Geocoder(getApplicationContext());

        mEventName = (EditText) findViewById(R.id.eventName);

        //mTags = (EditText) findViewById(R.id.tagNames);

        startDate = (TextView) findViewById(R.id.startDate);
        calendar = Calendar.getInstance();
        date = calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        time = (EditText) findViewById(R.id.time_newevent);
        mAddImageButton = (Button) findViewById(R.id.add_image);

        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionsAndOpenFilePicker();
            }
        });

        msetTimeButton = (Button) findViewById((R.id.add_time));

        msetTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });

        //get the spinner from the xml.
        dropdown = findViewById(R.id.tagList);
//create a list of items for the spinner.
        tags = new String[]{"Social", "Work", "Clubs", "Sport", "Music", "Festivals"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tags);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        tag = dropdown.getSelectedItem().toString();
        mAddEventButton = (Button) findViewById(R.id.add_event);
        autocomplete = (MultiAutoCompleteTextView)findViewById(R.id.userInvite);
        db.collection("usersnew")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            nameIdMap = new HashMap<String, String>();
                            idNameMap = new HashMap<String, String>();
                            ArrayList<String> names = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("", document.getId() + " => " + document.getData());
                                nameIdMap.put(document.get("name").toString(), document.getId());
                                idNameMap.put(document.getId(), document.get("name").toString());
                                names.add(document.get("name").toString());
                            }


                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                    (EventActivity.this,android.R.layout.select_dialog_item, names);

                            autocomplete.setAdapter(adapter);
                            autocomplete.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

                            if(eventid != null){
                                if(eventCache.containsKey(eventid)){
                                    Event event = eventCache.get(eventid);
                                    if(event != null){
                                        if(event.getName() != null)
                                            mEventName.setText(event.getName());
                                        if(event.getAddress() != null)
                                            address.setText(event.getAddress());
                                        if(event.getDate() != null){
                                            Date existingDate = (Date)event.getDate();
                                            Calendar calender = Calendar.getInstance();
                                            calender.setTime(existingDate);
                                            startDate.setText(new StringBuilder().append(calender.get(Calendar.DAY_OF_MONTH)).append("/")
                                                    .append(calender.get(Calendar.MONTH)).append("/").append(calender.get(Calendar.YEAR)));
                                            time.setText("" + calender.get(Calendar.HOUR) +":" + calender.get(Calendar.MINUTE));
                                        }
                                        if(event.getInvitees() != null){
                                            List<String> invites = (List<String>) event.getInvitees();
                                            StringBuilder invts = new StringBuilder();
                                            for(String user : invites){
                                                if(!user.equals(userid))
                                                    invts.append(idNameMap.get(user)+ ", ");
                                            }
                                            autocomplete.setText(invts.toString());
                                        }
                                        if(event.getTags() != null){
                                            String tag = event.getTags();
                                            for(int i = 0; i< tags.length; i++){
                                                if(tags[i].equals(tag)){
                                                    dropdown.setSelection(i);
                                                }
                                            }
                                        }
                                        String [] dates = startDate.getText().toString().split("/");
                                        date.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dates[0]));
                                        date.set(Calendar.MONTH, Integer.valueOf(dates[1]));
                                        date.set(Calendar.YEAR, Integer.valueOf(dates[2]));
                                        String [] times = time.getText().toString().split(":");
                                        date.set(Calendar.MINUTE, Integer.valueOf(times[0]));
                                        date.set(Calendar.HOUR, Integer.valueOf(times[1]));
                                        mAddEventButton.setText("Update");
                                        mAddEventButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                userid = getIntent().getStringExtra("userId");

                                                String adr = address.getText().toString();
                                                try {
                                                    addressList = geo.getFromLocationName(adr,3);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                e1 = addressList.get(0);
                                                final LatLng e2 = new LatLng(e1.getLatitude(),e1.getLongitude());
                                                //List<String> userids = new ArrayList<String>();
                                                List<String> invitees = new ArrayList<String>();
                                                String [] names = autocomplete.getText().toString().split(", ");
                                                //String[] id = new String[nameIdMap.size()];

                                                for(String name : names){
                                                    String id = nameIdMap.get(name);
                                                    if(id != null)
                                                        invitees.add(id);
                                                }
                                                //final String eventid =
                                                String nameUpdate, addressUpdate, tagUpdate;
                                                nameUpdate =  mEventName.getText().toString();
                                                addressUpdate = address.getText().toString();
                                                tagUpdate = dropdown.getSelectedItem().toString();
                                                GeoPoint locationUpdate = new GeoPoint(e2.latitude, e2.longitude);
                                                Date dateUpdate = new Date(date.getTimeInMillis());
                                                Event eventToCache = new Event();
                                                eventToCache.setName(nameUpdate);
                                                eventToCache.setTags(tagUpdate);
                                                eventToCache.setAddress(addressUpdate);
                                                eventToCache.setLocation(locationUpdate);
                                                eventToCache.setDate(dateUpdate);
                                                eventToCache.setInvitees(invitees);
                                                eventCache.put(eventid, eventToCache);

                                                db.collection("Events").document(eventid).update("name", mEventName.getText().toString(), "address",address.getText().toString(),"date", new Date(date.getTimeInMillis()),"location", new GeoPoint(e2.latitude, e2.longitude),"tags", dropdown.getSelectedItem().toString(),"invitees", FieldValue.arrayUnion(invitees.toArray())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("Acitivity1", "DocumentSnapshot successfully updated!");
//                                                                Intent profile = new Intent(getApplicationContext(), Profile.class);
//                                                                profile.putExtra("userId", userid);
//                                                                EventActivity.this.startActivity(profile);

                                                        CollectionReference eventsRef = db.collection("Events");
                                                        //final List<Event> events = new ArrayList<Event>();
                                                        Query query = eventsRef.whereArrayContains("users", userid);
                                                        events = new ArrayList<>();
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
                                                })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w("", "Error updating document", e);
                                                            }
                                                        });

                                            }
                                        });

                                    }
                                }
                                else{
                                    db.collection("Events").document(eventid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    eventMap = document.getData();
                                                    //sname.setText(event.get("eventName").to);
                                                    if(eventMap.get("name") != null)
                                                        mEventName.setText(eventMap.get("name").toString());
                                                    if(eventMap.get("address") != null)
                                                        address.setText(eventMap.get("address").toString());
                                                    if(eventMap.get("date") != null){
                                                        Date existingDate = (Date)eventMap.get("date");
                                                        Calendar calender = Calendar.getInstance();
                                                        calender.setTime(existingDate);
                                                        startDate.setText(new StringBuilder().append(calender.get(Calendar.DAY_OF_MONTH)).append("/")
                                                                .append(calender.get(Calendar.MONTH)).append("/").append(calender.get(Calendar.YEAR)));
                                                        time.setText("" + calender.get(Calendar.HOUR) +":" + calender.get(Calendar.MINUTE));
                                                    }
                                                    if(eventMap.get("users") != null){
                                                        List<String> invites = (List<String>) eventMap.get("invitees");
                                                        StringBuilder invts = new StringBuilder();
                                                        for(String user : invites){
                                                            if(!user.equals(userid))
                                                                invts.append(idNameMap.get(user)+ ", ");
                                                        }
                                                        autocomplete.setText(invts.toString());
                                                    }
                                                    if(eventMap.get("tags") != null){
                                                        String tag = (String)eventMap.get("tags");
                                                        for(int i = 0; i< tags.length; i++){
                                                            if(tags[i].equals(tag)){
                                                                dropdown.setSelection(i);
                                                            }
                                                        }
                                                    }
                                                    String [] dates = startDate.getText().toString().split("/");
                                                    date.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dates[0]));
                                                    date.set(Calendar.MONTH, Integer.valueOf(dates[1]));
                                                    date.set(Calendar.YEAR, Integer.valueOf(dates[2]));
                                                    String [] times = time.getText().toString().split(":");
                                                    date.set(Calendar.MINUTE, Integer.valueOf(times[0]));
                                                    date.set(Calendar.HOUR, Integer.valueOf(times[1]));
                                                    mAddEventButton.setText("Update");
                                                    mAddEventButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {

                                                            userid = getIntent().getStringExtra("userId");

                                                            String adr = address.getText().toString();
                                                            try {
                                                                addressList = geo.getFromLocationName(adr,3);
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                            e1 = addressList.get(0);
                                                            final LatLng e2 = new LatLng(e1.getLatitude(),e1.getLongitude());
                                                            //List<String> userids = new ArrayList<String>();
                                                            List<String> invitees = new ArrayList<String>();
                                                            String [] names = autocomplete.getText().toString().split(", ");
                                                            //String[] id = new String[nameIdMap.size()];

                                                            for(String name : names){
                                                                String id = nameIdMap.get(name);
                                                                if(id != null)
                                                                    invitees.add(id);
                                                            }

                                                            String nameUpdate, addressUpdate, tagUpdate;
                                                            nameUpdate =  mEventName.getText().toString();
                                                            addressUpdate = address.getText().toString();
                                                            tagUpdate = dropdown.getSelectedItem().toString();
                                                            GeoPoint locationUpdate = new GeoPoint(e2.latitude, e2.longitude);
                                                            Date dateUpdate = new Date(date.getTimeInMillis());
                                                            Event eventToCache = new Event();
                                                            eventToCache.setName(nameUpdate);
                                                            eventToCache.setTags(tagUpdate);
                                                            eventToCache.setAddress(addressUpdate);
                                                            eventToCache.setLocation(locationUpdate);
                                                            eventToCache.setDate(dateUpdate);
                                                            eventToCache.setInvitees(invitees);
                                                            eventCache.put(eventid, eventToCache);

                                                            db.collection("Events").document(eventid).update("name", nameUpdate, "address",addressUpdate,"date", dateUpdate,"location", locationUpdate,"tags", tagUpdate,"invitees", FieldValue.arrayUnion(invitees.toArray())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d("Acitivity1", "DocumentSnapshot successfully updated!");
//                                                                Intent profile = new Intent(getApplicationContext(), Profile.class);
//                                                                profile.putExtra("userId", userid);
//                                                                EventActivity.this.startActivity(profile);

                                                                    CollectionReference eventsRef = db.collection("Events");
                                                                    //final List<Event> events = new ArrayList<Event>();
                                                                    Query query = eventsRef.whereArrayContains("users", userid);
                                                                    events = new ArrayList<>();
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

                                                    Log.d("", "DocumentSnapshot data: " + document.getData());
                                                } else {
                                                    Log.d("", "No such document");
                                                }
                                            } else {
                                                Log.d("", "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                }

                            }
                            else{
                                mAddEventButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        event = new Event();
                                        event.setName(mEventName.getText().toString());
                                        //event.setTags(mTags.getText().toString());
                                        String adr = address.getText().toString();
                                        try {
                                            addressList = geo.getFromLocationName(adr,3);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        e1 = addressList.get(0);
                                        final LatLng e2 = new LatLng(e1.getLatitude(),e1.getLongitude());
                                        //event.setDate();
                                        List<String> userids = new ArrayList<String>();
                                        List<String> invitees = new ArrayList<String>();
                                        String [] names = autocomplete.getText().toString().split(", ");
                                        //String[] id = new String[nameIdMap.size()];

                                        for(String name : names){
                                            String id = nameIdMap.get(name);
                                            if(id != null)
                                                invitees.add(id);
                                        }
                                        userids.add(userid);
                                        event.setOrganiserId(userid);
                                        event.setUsers(userids);
                                        event.setInvitees(invitees);
                                        event.setDate(new Date(date.getTimeInMillis()));
                                        //event.setLocation(new GeoPoint(13.1067,80.0970));
                                        event.setAddress(address.getText().toString());
                                        event.setLocation(new GeoPoint(e2.latitude, e2.longitude));
                                        event.setTags(dropdown.getSelectedItem().toString());

                                        //event.setTags();

                                        db.collection("Events")
                                                .add(event)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.d("Activity1", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                        String eventid = documentReference.getId().toString();
                                                        event.setEventId(eventid);
                                                        db.collection("Events").document(eventid).set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                Intent second = new Intent(getApplicationContext(),Profile.class);
//                                                                second.putExtra("userId",userid);
//                                                                startActivityForResult(second,0);
                                                                CollectionReference eventsRef = db.collection("Events");
                                                                //final List<Event> events = new ArrayList<Event>();
                                                                Query query = eventsRef.whereArrayContains("users", userid);
                                                                events = new ArrayList<>();
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
                                                            }
                                                        });

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("Activity1", "Error adding document", e);
                                                    }
                                                });



                                    }

                                });
                            }

                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });






    }

    public void setDate(View view) {
        showDialog(999);
        //Toast.makeText(getApplicationContext(), "ca",
          ///      Toast.LENGTH_SHORT)
             //   .show();
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        date.set(Calendar.DAY_OF_MONTH, day);
        date.set(Calendar.MONTH, month-1);
        date.set(Calendar.YEAR, year);
        startDate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);


            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
            // Do something with the time chosen by the user
            time.setText("" + hourOfDay +":" + minuteOfDay);
            hour = hourOfDay;
            minute = minuteOfDay;
            date.set(Calendar.HOUR, hour);
            date.set(Calendar.MINUTE, minuteOfDay);

        }


    }

    private void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showError();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            openFilePicker();
        }
    }

    private void showError() {
        Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFilePicker();
                } else {
                    showError();
                }
            }
        }
    }

    private void openFilePicker() {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(FILE_PICKER_REQUEST_CODE)
                .withFilter(Pattern.compile(".*\\.jpg$"))
                .withFilterDirectories(true)
                .withHiddenFiles(true)
                .withTitle("Sample title")
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            if (path != null) {
                Log.d("Path: ", path);
                Toast.makeText(this, "Picked file: " + path, Toast.LENGTH_LONG).show();
                StorageReference storageRef = storage.getReference();
                StorageReference spaceRef = storageRef.child(userid+mEventName.getText());


                String name = "testimage";

                StorageReference imgref = spaceRef.child(name);
                //upload the file to cloud storage

                Uri file = Uri.fromFile(new File(path));
                StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
                UploadTask uploadTask = imgref.putFile(file);

// Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        Log.d("Activity1: ", "Added file: " + taskSnapshot.getMetadata().getName().toString());

                    }
                });
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        tag = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }



}
