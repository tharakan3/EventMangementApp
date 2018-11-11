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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    String tag;
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        //userid = getIntent().getStringExtra("userId");
        userid = LoginActivity.session.getuserId();
        address = findViewById(R.id.addr);


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
        Spinner dropdown = findViewById(R.id.tagList);
//create a list of items for the spinner.
        String[] tags = new String[]{"tag1", "tag2", "tag3"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tags);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        tag = dropdown.getSelectedItem().toString();
        mAddEventButton = (Button) findViewById(R.id.add_event);

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
                userids.add(userid);
                event.setOrganiserId(userid);
                event.setUsers(userids);
                event.setInvitees(invitees);
                event.setDate(new Date(date.getTimeInMillis()));
                event.setLocation(new GeoPoint(13.1067,80.0970));
                event.setAddress(address.getText().toString());
                event.setLocation(new GeoPoint(e2.latitude, e2.longitude));
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
                                        Intent second = new Intent(getApplicationContext(),Profile.class);
                                        second.putExtra("userId",userid);
                                        startActivityForResult(second,0);
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
