package com.example.kevin.eventapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventsTab.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventsTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsTab extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public RecyclerView eventListView;
    public RecyclerView.Adapter eventListViewAdaptor;
    private OnFragmentInteractionListener mListener;
    public List<Event> myEvents;
    private String userid;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();


    public EventsTab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventsTab.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsTab newInstance(String param1, String param2) {
        EventsTab fragment = new EventsTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_events_tab2, container, false);

        eventListView = (RecyclerView)view.findViewById(R.id.elist);
        eventListView.setHasFixedSize(true);
        eventListView.setLayoutManager(new LinearLayoutManager(getActivity()));


        CollectionReference eventsRef = db.collection("Events");
        //final List<Event> events = new ArrayList<Event>();
        Query query = eventsRef.whereEqualTo("organiserId", userid);//.whereEqualTo("name", name);//.whereEqualTo("tags", tags);//.whereEqualTo("date", date);
        myEvents = new ArrayList<>();
        Event event = new Event();

                        event.setTags("gg");
                            event.setName("gg");
                            event.setEventId("gg");
                            event.setOrganiserId("gg");
                        myEvents.add(event);

        Event event2 = new Event();
        event2.setTags("kk");
        event2.setName("kk");
        event2.setEventId("kk");
        event2.setOrganiserId("kk");
        myEvents.add(event2);
        final  CountDownLatch countDownLatch = new CountDownLatch(1);
        Log.d("Activity1", "reached getevents");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Activity1", document.getId() + " => " + document.getData());
                        Map<String, Object> docs = document.getData();
                        //Event event = (Event) doc.get(document.getId());
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

                        myEvents.add(event);

                        //events.add(event);
                    }
                    eventListViewAdaptor = new EventAdaptor(myEvents,getActivity());
                    eventListView.setAdapter(eventListViewAdaptor);
                    countDownLatch.countDown();
                } else {
                    Log.d("Activity1", "Error getting documents: ", task.getException());
                }
            }

        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
