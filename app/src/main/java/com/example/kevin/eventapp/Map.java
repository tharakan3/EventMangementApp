package com.example.kevin.eventapp;


import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class Map extends android.support.v4.app.Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    LocationManager locationManager;
    String Home[] = {"Fisher College of Business", "Caldwell Laboratory"};
    Geocoder geo;
    List<Address> addressList;
    Address uevent;
    LatLng le;
    MarkerOptions eventMarker;
    double markerlat, markerlong;
    LatLng l2;
    Location l1;
    double m1,m2,n1,n2;

    public Map() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geo = new Geocoder(getActivity());
        eventMarker = new MarkerOptions();
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                l2 = new LatLng(location.getLatitude(), location.getLongitude());
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
            return;

        }
        l1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        l2 = new LatLng(13.545, 60.23);
        googleMap.setMyLocationEnabled(true);

        //googleMap.addMarker(new MarkerOptions().position(new LatLng(40.689247,-74.044502))).setTitle("Bingo");
        CameraPosition Liberty = CameraPosition.builder().target(l2).zoom(15).bearing(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
        try {
            addressList = geo.getFromLocationName(Home[0], 3);
            if (addressList != null) {
                uevent = addressList.get(0);
                le = new LatLng(uevent.getLatitude(), uevent.getLongitude());
                eventMarker.position(le);
                eventMarker.title("Home");
                eventMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                googleMap.addMarker(eventMarker);


            }

            addressList = geo.getFromLocationName(Home[1], 3);
            if (addressList != null) {
                uevent = addressList.get(0);
                le = new LatLng(uevent.getLatitude(), uevent.getLongitude());
                eventMarker.position(le);
                eventMarker.title("Home");
                eventMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                googleMap.addMarker(eventMarker);


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        googleMap.setOnMarkerClickListener(this);


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setTitle("Event_name");
        LatLng Marker = marker.getPosition();
        m1 = Marker.latitude;
        m2 = Marker.longitude;
        n1 = l2.latitude;
        n2 = l2.longitude;
        float results[] = new float[10];

        Object[] datatransfer = new Object[4];
        String url = getDirectionsUrl();
        GetDirections gd = new GetDirections();
        datatransfer[0] = mGoogleMap;
        datatransfer[1] = url;
        datatransfer[2] = new LatLng(m1,m2);
        datatransfer[3] = eventMarker;
        gd.execute(datatransfer);

        return false;
    }


    private String getDirectionsUrl()
    {
        StringBuilder googleDirectionsURL = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsURL.append("origin="+ n1 +"," + n2);
        googleDirectionsURL.append("&destination=" + m1 + "," + m2);
        googleDirectionsURL.append("&key=" + "AIzaSyBHZw_xa93uLUYG9u63D00m1KUXSv7-Pvc");
        return  (googleDirectionsURL.toString());

    }


}