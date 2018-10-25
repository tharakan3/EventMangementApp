package com.example.kevin.eventapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    // UI references.
    private EditText mEventName;
    private EditText mTags;
    private View mEventView;
    private View mLoginFormView;
    private EditText mUserName;

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView startDate;
    private int year, month, day;

    private Button mAddEventButton;

    private String userid;

    private Event event;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        userid = getIntent().getStringExtra("userId");

        mEventName = (EditText) findViewById(R.id.eventName);

        mTags = (EditText) findViewById(R.id.tagNames);

        startDate = (TextView) findViewById(R.id.startDate);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        mAddEventButton = (Button) findViewById(R.id.add_event);

        mAddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event = new Event();
                event.setName(mEventName.getText().toString());
                event.setTags(mTags.getText().toString());
                List<String> userids = new ArrayList<String>();
                userids.add(userid);
                event.setOrganiserId(userid);
                event.setUsers(userids);
                db.collection("Events")
                        .add(event)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("Activity1", "DocumentSnapshot added with ID: " + documentReference.getId());
                                String eventid = documentReference.getId().toString();
                                event.setEventId(eventid);
                                db.collection("Events").document(eventid).set(event);
                                Intent second = new Intent(getApplicationContext(),SearchEvent.class);
                                second.putExtra("userId",userid);
                                startActivityForResult(second,0);
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
        startDate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

}
