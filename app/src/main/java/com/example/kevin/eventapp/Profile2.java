package com.example.kevin.eventapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.support.v4.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Profile2 extends AppCompatActivity implements InvitesTab.OnFragmentInteractionListener,EventsTab.OnFragmentInteractionListener{

    private TextView user;
    private Button search;
    private Button create;
    String name;
    String ID;
    Intent intpro;
    public RecyclerView eventListView;
    public RecyclerView.Adapter eventListViewAdaptor;
    public RecyclerView invitesListView;
    public RecyclerView.Adapter inviteesListViewAdaptor;
    public List<Event> myEvents ;
    public List<Event> invites;
    private String userid;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Invites"));
        tabLayout.addTab(tabLayout.newTab().setText("My Events"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpage);
        final Pageadaptert adapter = new Pageadaptert(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }


    protected void onResume() {
        super.onResume();
        Log.d("Activity2", "onStart: Activity Resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Activity2","onPause: Acitvity Paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Activity2", "onStop: Activity Stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Activity2", "onDestroy: Activity Destroyed");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
