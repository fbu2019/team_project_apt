package com.example.skillshop.NavigationFragments;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.skillshop.ClassAdapter;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.Models.Query;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    private static final String CHANNEL_ID = "CHANNEL_ID";
    private RecyclerView rvClasses;
    protected ArrayList<Workshop> mWorkshops;
    protected ClassAdapter classAdapter;


    Spinner spinSorters;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_home), container, false);
    }




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        spinSorters = view.findViewById(R.id.spinSorters);

        populateHomeFeed();
        connectRecyclerView(view);
        //setSpinner();

        // ceates channel for notifications
        createNotificationChannel();
        // call this function with the title and body and any unique id you want
        notification("Welcome to the home page!","Here are classes you can sign up for",0);




    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Skillshop";
            String description = "notification channel for class updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void notification(String headline, String body,int notificationId)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(),CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_skill_note)
                .setContentTitle(headline)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());

    }

    @Override
    public void onStart() {
        super.onStart();
        setSpinner();
    }

    public void  setSpinner()
    {
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sorters, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinSorters.setAdapter(adapter);

        //set listener for selected spinner item
        spinSorters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //     classAdapter.notifyDataSetChanged();
                switch(position){

                    case (0):{
                        mWorkshops.clear();
                        classAdapter.notifyDataSetChanged();
                        populateHomeFeed();
                        break;
                    }
                    case (1):{
                        populateByCost();
                        break;
                    }
                    case(2):{
                        //TODO multilevel drop down list
                    }
                    case(3): {
                        final ParseQuery<ParseUser> userQuery = new ParseQuery<ParseUser>(ParseUser.class);
                        userQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                        userQuery.findInBackground(new FindCallback<ParseUser>() {

                            @Override
                            public void done(List<ParseUser> singletonUserList, ParseException e) {
                                ParseGeoPoint userLocation = singletonUserList.get(0).getParseGeoPoint("userLocation");
                                populateByLocation(userLocation);
                            }
                        });
                    }
                    default:
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void populateByLocation(ParseGeoPoint userLocation) {
        mWorkshops.clear();
        classAdapter.notifyDataSetChanged();
        Query parseQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        parseQuery.getAllClasses().withItems().byLocation(userLocation).getClassesNotTaking();

        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                //
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        mWorkshops.add(workshopItem);
                        classAdapter.notifyItemInserted(mWorkshops.size()-1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

    }

    private void populateByCost() {
        mWorkshops.clear();
        classAdapter.notifyDataSetChanged();
        Query parseQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        parseQuery.getAllClasses().withItems().byCost().getClassesNotTaking();

        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                //
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        mWorkshops.add(workshopItem);
                        classAdapter.notifyItemInserted(mWorkshops.size()-1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }



    private void connectRecyclerView(View view) {
        //find the RecyclerView
        rvClasses = (RecyclerView) view.findViewById(R.id.rvClasses);
        //init the arraylist (data source)
        mWorkshops = new ArrayList<>();
        //construct the adapter from this datasource
        classAdapter = new ClassAdapter(mWorkshops, getContext());
        //RecyclerView setup (layout manager, use adapter)
        rvClasses.setLayoutManager(new LinearLayoutManager(getContext()));
        //set the adapter
        rvClasses.setAdapter(classAdapter);


        // add dividers on posts
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvClasses.getContext(),
                new LinearLayoutManager(getContext()).getOrientation());
        rvClasses.addItemDecoration(dividerItemDecoration);
    }



    public void populateHomeFeed() {


        Query parseQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        parseQuery.getAllClasses().withItems().byTimeOfClass().getClassesNotTaking();

        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        mWorkshops.add(workshopItem);
                        classAdapter.notifyItemInserted(mWorkshops.size()-1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}



