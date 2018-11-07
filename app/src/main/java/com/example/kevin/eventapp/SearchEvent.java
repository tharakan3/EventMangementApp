package com.example.kevin.eventapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SearchEvent extends AppCompatActivity {


    private String userid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText sname;
    private EditText stag;
    private Button search;
    private static EditText sDate;
    private static  Calendar date;
    private Button msetDateButton;
    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_event);
        sname = (EditText)findViewById(R.id.sname);
        stag = (EditText)findViewById(R.id.stags);
        search = (Button)findViewById(R.id.search);
        date = Calendar.getInstance();
        sDate = (EditText) findViewById(R.id.sdate_input);
        msetDateButton = (Button) findViewById((R.id.scal));

        msetDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userid = getIntent().getStringExtra("userId");

                Intent second = new Intent(getApplicationContext(),EventListActivity.class);
                second.putExtra("userId",userid);
                second.putExtra("tags",stag.getText().toString());
                second.putExtra("name",sname.getText().toString());
                second.putExtra("date", sDate.getText().toString());
                //second.putExtra("date", new Date());
                //second.putExtra("tags", "f,s,s,s");
                startActivityForResult(second,0);
            }
        });
    }

    public List<Event> getEvents(Date date, String tags, String name) {
        CollectionReference eventsRef = db.collection("Events");
        final List<Event> events = new ArrayList<Event>();
        Query query = eventsRef.whereArrayContains("users", userid).whereEqualTo("name", name);//.whereEqualTo("tags", tags);//.whereEqualTo("date", date);
        if(date != null){
            query = query.whereGreaterThan("date",date);
        }
        /*if(tags != null){
            query = query.whereEqualTo("tags",tags);
        }*/
        Log.d("Activity1", "reached getevents");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Activity1", document.getId() + " => " + document.getData());
                        Map<String, Object> docs = document.getData();
                        //Event event = (Event) doc.get(document.getId());
                        Log.d("Activity1", (String)docs.get("tags"));
                        //events.add(event);
                    }
                } else {
                    Log.d("Activity1", "Error getting documents: ", task.getException());
                }
            }
        });

        //query.ad
        return events;
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new SearchEvent.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day  = c.get(Calendar.DAY_OF_MONTH);


            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month,
                    day);
        }

        public void onDateSet(DatePicker view, int day, int month, int year) {
            // Do something with the time chosen by the user
            sDate.setText("" + day +"/" + month + "/" + year);
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, month);
            date.set(Calendar.DAY_OF_MONTH, day);

        }


    }



}
