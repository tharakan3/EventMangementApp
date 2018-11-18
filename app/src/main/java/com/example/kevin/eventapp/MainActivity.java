package com.example.kevin.eventapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


/**
     * A login screen that offers login via email/password.
     */
    public class MainActivity extends AppCompatActivity {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private static final int REQUEST_READ_CONTACTS = 0;
        public Intent second;


        /**
         * A dummy authentication store containing known user names and passwords.
         * TODO: remove after connecting to a real authentication system.
         */
        private static final String[] DUMMY_CREDENTIALS = new String[]{
                "foo@example.com:hello", "bar@example.com:world"
        };

        /**
         * Validate Emails using this Pattern.
         */
        private static final Pattern VALID_EMAIL
                = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                Pattern.CASE_INSENSITIVE);

        /**
         * Keep track of the login task to ensure we can cancel it if requested.
         */
        private UserLoginTask mAuthTask = null;


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // UI references.
        private EditText mEmailView;
        private EditText mPasswordView;
        private View mProgressView;
        private View mLoginFormView;
        private EditText mUserName;
        private List<Event> events;
        // Google Login.
//    private FirebaseAuth mAuth;

        // Date of birth.
        private DatePicker datePicker;
        private Calendar calendar;
        private TextView date;
        private int year, month, day;


        private User mNewUser;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d("Activity1", "onCreate: Activity Created");
            setContentView(R.layout.activity_main);
            // Set up the login form.
            mEmailView = (EditText) findViewById(R.id.email);
            //populateAutoComplete();

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });
            mUserName = (EditText)findViewById(R.id.username);
            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //attemptLogin();
                    mNewUser = new User();
                    mNewUser.setUserId("");
                    mNewUser.setEmail(mEmailView.getText().toString());
                    mNewUser.setName(mUserName.getText().toString());
                    mNewUser.setPassword(mPasswordView.getText().toString());
                    //mNewUser.set
                    Log.d("Activity1", "User Object created");
                    //db.collection("test").document("newTest").set(mNewUser);
                    Map<String, Object> data = new HashMap<>();
                    data.put("name", "Tokyo");
                    data.put("country", "Japan");

                    db.collection("usersnew")
                            .add(mNewUser)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    String  userid = documentReference.getId().toString();
                                    Log.d("Activity1", "DocumentSnapshot added with ID: " + documentReference.getId());
                                    mNewUser.setUserId(userid);
                                    events = new ArrayList<>();
                                    db.collection("usersnew").document(userid).set(mNewUser);


                                    LoginActivity.session.setuserId(userid);

                                    CollectionReference eventsRef = db.collection("Events");
                                    //final List<Event> events = new ArrayList<Event>();
                                    Query query = eventsRef.whereArrayContains("users", userid);

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
//                                    second = new Intent(getApplicationContext(),Profile.class);
//                                    second.putExtra("username",mUserName.getText().toString());
//                                    second.putExtra("userid",userid);
//                                    startActivityForResult(second,0);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Activity1", "Error adding document", e);
                                }
                            });


                    Log.d("Activity1", "Done");
/*
                    db.collection("users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("Activity1", document.getId() + " => " + document.getData());
                                        }
                                    } else {
                                        Log.w("Activity1", "Error getting documents.", task.getException());
                                    }
                                }
                            });*/



                }
            });

            date = (TextView) findViewById(R.id.dateofbirth);
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);

      /*  mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);*/
        }

        @Override
        public void onStart() {
            super.onStart();
            Log.d("Activity1", "onStart: Activity Started");
            // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
            // updateUI(currentUser);
        }

        /*private void populateAutoComplete() {

            getLoaderManager().initLoader(0, null, this);
        }
*/

        @Override
        protected void onResume() {
            super.onResume();
            Log.d("Activity1", "onStart: Activity Resumed");
        }

        @Override
        protected void onPause() {
            super.onPause();
            Log.d("Activity1","onPause: Acitvity Paused");
        }

        @Override
        protected void onStop() {
            super.onStop();
            Log.d("Activity1", "onStop: Activity Stopped");
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            Log.d("Activity1", "onDestroy: Activity Destroyed");
        }

        public void setDate(View view) {
            showDialog(999);
            Toast.makeText(getApplicationContext(), "ca",
                    Toast.LENGTH_SHORT)
                    .show();
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
            date.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));
        }
/*
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }*//*
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });*//*
    } else {
        requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
        }*/

        /**
         * Callback received when a permissions request has been completed.
         */
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            if (requestCode == REQUEST_READ_CONTACTS) {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //populateAutoComplete();
                }
            }
        }


        /**
         * Attempts to sign in or register the account specified by the login form.
         * If there are form errors (invalid email, missing fields, etc.), the
         * errors are presented and no actual login attempt is made.
         */
        private void attemptLogin() {
            if (mAuthTask != null) {
                return;
            }

            // Reset errors.
            mEmailView.setError(null);
            mPasswordView.setError(null);

            // Store values at the time of the login attempt.
            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.
            if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                //showProgress(true);
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
            }
        }

        private boolean isEmailValid(String email) {
            Matcher matcher = VALID_EMAIL.matcher(email);
            return matcher.find();
        }

        private boolean isPasswordValid(String password) {
            //TODO: Replace this with your own logic
            return password.length() > 4;
        }

        /**
         * Shows the progress UI and hides the login form.
         */
   /* @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }*/

        /*@Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return new CursorLoader(this,
                    // Retrieve data rows for the device user's 'profile' contact.
                    Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                            ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                    // Select only email addresses.
                    ContactsContract.Contacts.Data.MIMETYPE +
                            " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                    .CONTENT_ITEM_TYPE},

                    // Show primary email addresses first. Note that there won't be
                    // a primary email address if the user hasn't specified one.
                    ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            List<String> emails = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                emails.add(cursor.getString(ProfileQuery.ADDRESS));
                cursor.moveToNext();
            }

            addEmailsToAutoComplete(emails);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {

        }

        private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
            //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_dropdown_item_1line, emailAddressCollection);


        }


        private interface ProfileQuery {
            String[] PROJECTION = {
                    ContactsContract.CommonDataKinds.Email.ADDRESS,
                    ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
            };

            int ADDRESS = 0;
            int IS_PRIMARY = 1;
        }*/

        /**
         * Represents an asynchronous login/registration task used to authenticate
         * the user.
         */
        public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

            private final String mEmail;
            private final String mPassword;

            UserLoginTask(String email, String password) {
                mEmail = email;
                mPassword = password;
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                // TODO: attempt authentication against a network service.

                try {
                    // Simulate network access.
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    return false;
                }

                for (String credential : DUMMY_CREDENTIALS) {
                    String[] pieces = credential.split(":");
                    if (pieces[0].equals(mEmail)) {
                        // Account exists, return true if the password matches.
                        if (pieces[1].equals(mPassword)) {
                            // nextActivity();
                        }
                    }
                }

                // TODO: register the new account here.
                //nextActivity();
                return true;
            }

            private void nextActivity(){
                //Intent intent = new Intent(LoginActivity.this, BlankActivity.class);
                //startActivity(intent);
            }

            @Override
            protected void onPostExecute(final Boolean success) {
                mAuthTask = null;
                /*showProgress(false);*/

                if (success) {
                    finish();
                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            }

            @Override
            protected void onCancelled() {
                mAuthTask = null;
                //showProgress(false);
            }
        }
    }
