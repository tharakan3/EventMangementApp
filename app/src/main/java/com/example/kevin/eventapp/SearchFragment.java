package com.example.kevin.eventapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static com.example.kevin.eventapp.Constants.CONNECTIVITY_ERROR_MESSAGE;



public class SearchFragment extends Fragment{

    private String userid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText sname;
    private EditText stag;
    private Button search;
    private static EditText sDate;
    private static  Calendar date;
    private Button msetDateButton;
    public List<Event> events ;
    int year, month, day;
    Geocoder geo;
    LocationManager l3;
    private static final double MIN_LAT = Math.toRadians(-90d);  // -PI/2
    private static final double MAX_LAT = Math.toRadians(90d);   //  PI/2
    private static final double MIN_LON = Math.toRadians(-180d); // -PI
    private static final double MAX_LON = Math.toRadians(180d);  //  PI
    public LatLng l2;
    public LatLng coordinates;
    private SeekBar sek;
    private TextView sektxt;
    Date datefield ;
    Double rangeinKm = 15d;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.activity_search_event, container, false);
        //MapScreen mapScreen = (MapScreen) getActivity();

        events = new ArrayList<>();
        sname = (EditText)view.findViewById(R.id.sname);
        stag = (EditText)view.findViewById(R.id.stags);
        search = (Button)view.findViewById(R.id.searchEvent);
        date = Calendar.getInstance();
        sDate = (EditText)view.findViewById(R.id.sdate_input);
        msetDateButton = (Button)view.findViewById((R.id.scal));

        Session session = new Session(getContext());
        //userid = LoginActivity.session.getuserId();
        userid = session.getuserId();

        msetDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });


        events = new ArrayList<>();

        sek = (SeekBar)view.findViewById(R.id.seekBar2);
        sektxt = (TextView)view.findViewById(R.id.slidertext);
        sek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                String text = String.valueOf(i) + " Miles";
                sektxt.setText(text);
                rangeinKm = (double)i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        l3 = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return view;
        }


        Location lx = l3.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(lx!=null){
            l2 = new LatLng(lx.getLatitude(),lx.getLongitude());

        }

        else
        {
            lx = l3.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(lx!=null){
                l2 = new LatLng(lx.getLatitude(),lx.getLongitude());
            }
        }
        coordinates = l2;
        l3.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                l2 = new LatLng(location.getLatitude(), location.getLongitude());
                coordinates = l2;
                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Utils.isNetworkConnected(getContext())){
                            String dateStr = sDate.getText().toString();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyy/mm/dd");
                            datefield  = null;
                            try {
                                if(dateStr != null && !"".equals(dateStr))
                                    datefield = sdf.parse(dateStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            final String tags = stag.getText().toString();
                            final String name = sname.getText().toString();
                            CollectionReference eventsRef = db.collection("Events");
                            //final List<Event> events = new ArrayList<Event>();
                            Query query = eventsRef.whereArrayContains("users", userid);//.whereEqualTo("name", name);//.whereEqualTo("tags", tags);//.whereEqualTo("date", date);

                            if(tags != null && !"".equals(tags)){
                                query = query.whereEqualTo("tags",tags);
                            }



                            if(datefield != null ){
                                query = query.whereGreaterThan("date",datefield);
                            }

                            Log.d("Activity1", "reached getevents");
                            //Log.d("Activity1", dateStr);
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("Activity1", document.getId() + " => " + document.getData());
                                            java.util.Map<String, Object> docs = document.getData();
                                            //Event event = (Event) doc.get(document.getId());
                                            //if(docs.get("location") != null){
                                            GeoPoint gp = (GeoPoint) docs.get("location");
                                            Double dist = distanceTo(coordinates.latitude,gp.getLatitude(),coordinates.longitude, gp.getLongitude());

                                            Log.d("Activity1", dist.toString());
                                            if( (rangeinKm != null && coordinates != null && dist <= rangeinKm && name != null && !"".equals(sname.getText().toString()) && ((String)docs.get("name")).contains(name)) || (rangeinKm == null || coordinates == null || "".equals(sname.getText().toString())) ){
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

                                                if((docs.get("location") != null)) {
                                                    GeoPoint gp1 = (GeoPoint) docs.get("location");
                                                    event.setLat(gp1.getLatitude());
                                                    event.setLng(gp1.getLongitude());
                                                }
                                                events.add(event);
                                            }

                                        }
                                        /*Intent intent = new Intent(getContext(), MapScreen.class);
                                        //Serializable eventList = (Serializable)events;
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("eventlist", (Serializable) events);
                                        intent.putExtras(bundle);
                                        startActivity(intent);*/
                                        MapScreen.events = events;

                                        FragmentManager Fm  = getFragmentManager();
                                        //Fragment frag = new Map();


                                        List<Fragment> fragments = Fm.getFragments();
                                        for(Fragment fragment : fragments){
                                            if(fragment instanceof com.example.kevin.eventapp.Map ){
                                                ((com.example.kevin.eventapp.Map) fragment).setMarkersAfterSearch();
                                                Fm.beginTransaction().show(fragment).commit();
                                            }
                                            else if(fragment instanceof SearchFragment){
                                                Fm.beginTransaction().remove(fragment).commit();
                                            }
                                        }

                                    } else {
                                        Log.d("Activity1", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(getContext(), CONNECTIVITY_ERROR_MESSAGE, LENGTH_LONG).show();
                        }


                    }
                });



            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isNetworkConnected(getContext())){
                    String dateStr = sDate.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyy/mm/dd");
                    datefield  = null;
                    try {
                        if(dateStr != null && !"".equals(dateStr))
                            datefield = sdf.parse(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    final String tags = stag.getText().toString();
                    final String name = sname.getText().toString();
                    CollectionReference eventsRef = db.collection("Events");
                    //final List<Event> events = new ArrayList<Event>();
                    Query query = eventsRef.whereArrayContains("users", userid);//.whereEqualTo("name", name);//.whereEqualTo("tags", tags);//.whereEqualTo("date", date);

                    if(tags != null && !"".equals(tags)){
                        query = query.whereEqualTo("tags",tags);
                    }

//                        if(name != null && !"".equals(sname.getText().toString())){
//                            query = query.whereEqualTo("name",sname.getText().toString());
//                        }

                    if(datefield != null ){
                        query = query.whereGreaterThan("date",datefield);
                    }


                    Log.d("Activity1", "reached getevents");
                    //Log.d("Activity1", dateStr);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("Activity1", document.getId() + " => " + document.getData());
                                    Map<String, Object> docs = document.getData();
                                    //Event event = (Event) doc.get(document.getId());
                                    //if(docs.get("location") != null){
                                    GeoPoint gp = (GeoPoint) docs.get("location");
                                    Double dist = distanceTo(coordinates.latitude,gp.getLatitude(),coordinates.longitude, gp.getLongitude());

                                    Log.d("Activity1", dist.toString());
                                    if( (rangeinKm != null && coordinates != null && dist <= rangeinKm && name != null && !"".equals(sname.getText().toString()) && ((String)docs.get("name")).contains(name)) || (rangeinKm == null || coordinates == null || "".equals(sname.getText().toString())) ){
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

                                        if((docs.get("location") != null)) {
                                            GeoPoint gp1 = (GeoPoint) docs.get("location");
                                            event.setLat(gp1.getLatitude());
                                            event.setLng(gp1.getLongitude());
                                        }
                                        events.add(event);
                                    }


                                    // }

                                    //}



                                    //events.add(event);
                                }
                                Intent intent = new Intent(getContext(), MapScreen.class);
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
                else{
                    Toast.makeText(getContext(), CONNECTIVITY_ERROR_MESSAGE, LENGTH_LONG).show();
                }


            }
        });



        return view;
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new SearchEvent.DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
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

    public double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    public double radiansToDegrees(double radians) {
        return radians * 180 / Math.PI;
    }

    public double distanceTo(double lat1, double lat2, double lng1, double lng2) {
        float results[] = new float[10];
        Location.distanceBetween(lat1,lng1,lat2,lng2,results);
        return results[0]/1000d;
    }


    public double[] getMinMaxLat(LatLng ln, double distance){
        double res = 0.0, earthRadiusKm = 6371;
        double lat = degreesToRadians(ln.latitude);
        double lon = degreesToRadians(ln.longitude);
        double angle = distance/earthRadiusKm;
        double minLat = lat - angle;
        double maxLat = lat + angle;

        double minLon, maxLon;
        if (minLat > MIN_LAT && maxLat < MAX_LAT) {
            double deltaLon = Math.asin(Math.sin(angle) /
                    Math.cos(lat));
            minLon = lon - deltaLon;
            if (minLon < MIN_LON) minLon += 2d * Math.PI;
            maxLon = lon + deltaLon;
            if (maxLon > MAX_LON) maxLon -= 2d * Math.PI;
        } else {
            // a pole is within the distance
            minLat = Math.max(minLat, MIN_LAT);
            maxLat = Math.min(maxLat, MAX_LAT);
            minLon = MIN_LON;
            maxLon = MAX_LON;
        }


        return new double[]{radiansToDegrees(minLat),radiansToDegrees(minLon),radiansToDegrees(maxLat),radiansToDegrees(maxLon)};
    }
}
