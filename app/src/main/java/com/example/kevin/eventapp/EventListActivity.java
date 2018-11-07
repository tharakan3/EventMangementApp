package com.example.kevin.eventapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;




import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventListActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.Adapter recyclerViewAdaptor;
    public List<Event> events ;
    public String[] ev = new String[]{"fefa", "efe"};
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final double MIN_LAT = Math.toRadians(-90d);  // -PI/2
    private static final double MAX_LAT = Math.toRadians(90d);   //  PI/2
    private static final double MIN_LON = Math.toRadians(-180d); // -PI
    private static final double MAX_LON = Math.toRadians(180d);  //  PI
    //boolean isdone = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        //final ListView listview = (ListView) findViewById(R.id.listview);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        events = new ArrayList<>();

            //le.add(e1);
        String tags = getIntent().getStringExtra("tags");
        String name = getIntent().getStringExtra("name");
        String userid = getIntent().getStringExtra("userId");
        String dateStr = getIntent().getStringExtra("date");
        SimpleDateFormat sdf = new SimpleDateFormat("yyy/mm/dd");
        Date date  = null;
        try {
            if(dateStr != null && !"".equals(dateStr))
                date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final LatLng coordinates = LatLng.newBuilder().setLatitude(13.0891).setLongitude(80.2096).build();
        final Double rangeinKm = 15d;
        CollectionReference eventsRef = db.collection("Events");
            //final List<Event> events = new ArrayList<Event>();
        Query query = eventsRef.whereArrayContains("users", userid);//.whereEqualTo("name", name);//.whereEqualTo("tags", tags);//.whereEqualTo("date", date);

        if(tags != null && !"".equals(tags)){
            query = query.whereEqualTo("tags",tags);
        }

        if(name != null && !"".equals(name)){
            query = query.whereEqualTo("name",name);
        }

        if(date != null ){
            query = query.whereGreaterThan("date",date);
        }

        if(rangeinKm != null && coordinates != null){
            double [] range = getMinMaxLat(coordinates, rangeinKm);
            query.whereGreaterThan("location", new GeoPoint(range[0], range[1]));
            query.whereLessThan("location", new GeoPoint(range[2], range[3]));
        }
            Log.d("Activity1", "reached getevents");
            Log.d("Activity1", dateStr);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Activity1", document.getId() + " => " + document.getData());
                            Map<String, Object> docs = document.getData();
                            //Event event = (Event) doc.get(document.getId());
                            if(docs.get("location") != null){
                                GeoPoint gp = (GeoPoint) docs.get("location");
                                Double dist = distanceTo(coordinates.getLatitude(),gp.getLatitude(),coordinates.getLongitude(), gp.getLongitude());
                                Log.d("Activity1", dist.toString());
                                if( (rangeinKm != null && coordinates != null && dist <= rangeinKm) || (rangeinKm == null || coordinates == null)){
                                    Log.d("Activity1", (String)docs.get("tags"));
                                    Event event = new Event();
                                    event.setTags((String)docs.get("tags"));
                                    event.setName((String)docs.get("name"));
                                    event.setEventId((String)docs.get("eventId"));
                                    event.setOrganiserId((String)docs.get("organiserId"));
                                    events.add(event);
                                }
                            }



                            //events.add(event);
                        }
                        recyclerViewAdaptor = new EventAdaptor(events,EventListActivity.this);
                        recyclerView.setAdapter(recyclerViewAdaptor);
                    } else {
                        Log.d("Activity1", "Error getting documents: ", task.getException());
                    }
                }
            });


        //while(!isdone);
        //Log.d("Activity1", ""+events.size());
        /*Event event = new Event();
        event.setTags("ts,ts1");
        event.setName("test1");
        //event.setDate(new Date());
        events.add(event);
        Log.d("Activity1", ""+events.size());
        recyclerViewAdaptor = new EventAdaptor(ev,this);
        recyclerView.setAdapter(recyclerViewAdaptor);*/



    }

    public double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    public double radiansToDegrees(double radians) {
        return radians * 180 / Math.PI;
    }

    public double distanceTo(double lat1, double lat2, double lng1, double lng2) {
        /*return Math.acos(Math.sin(radLat) * Math.sin(location.radLat) +
                Math.cos(radLat) * Math.cos(location.radLat) *
                        Math.cos(radLon - location.radLon)) * radius;*/
        double earthRadius = 6371d; // in miles, change to 6371 for kilometer output
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }


    public double[] getMinMaxLat(LatLng ln, double distance){
        double res = 0.0, earthRadiusKm = 6371;
        double lat = degreesToRadians(ln.getLatitude());
        double lon = degreesToRadians(ln.getLongitude());
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
