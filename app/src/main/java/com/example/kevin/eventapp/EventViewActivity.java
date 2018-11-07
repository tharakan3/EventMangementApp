package com.example.kevin.eventapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class EventViewActivity extends AppCompatActivity {

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
    private static int hour, minute;

    private Button mAddEventButton, msetTimeButton, mInviteButton;

    private String userid;

    private Event event;

    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        userid = getIntent().getStringExtra("userId");

        mEventName = (EditText) findViewById(R.id.eventName);

        //mTags = (EditText) findViewById(R.id.tagNames);

        startDate = (TextView) findViewById(R.id.startDate);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        mInviteButton = (Button) findViewById(R.id.invite);

        mInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),EventInviteActivity.class));
            }
        });

        msetTimeButton = (Button) findViewById((R.id.add_time));

        msetTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mAddEventButton = (Button) findViewById(R.id.add_event);








    }














}
