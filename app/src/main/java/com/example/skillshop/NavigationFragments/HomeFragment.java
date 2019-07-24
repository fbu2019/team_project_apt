package com.example.skillshop.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.skillshop.ClassAdapter;
import com.example.skillshop.MapActivity;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.Models.Query;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.ParseException;
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
    Spinner spinFilters;
    ImageButton btnMap;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_home), container, false);
    }




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        spinSorters = view.findViewById(R.id.spinSorters);
        spinFilters = view.findViewById(R.id.spinFilters);
        setupMapButton(view);
        populateHomeFeed();
        connectRecyclerView(view);
    }

    private void setupMapButton(View view) {
        btnMap = view.findViewById(R.id.btnMap);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMapActivity = new Intent(getContext(), MapActivity.class);
                startActivity(openMapActivity);
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        setSpinners();
    }

    public void  setSpinners()
    {
        setSorters();
        setFilters();



    }

    private void setSorters() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sorters, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinSorters.setAdapter(sortAdapter);

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


    private void setFilters() {
        final ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categoryFilters, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinFilters.setAdapter(filterAdapter);

        //set listener for selected spinner item
        spinFilters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                       filterByCategory("Culinary");
                        break;
                    }
                    case (2):{
                        filterByCategory("Education");
                        break;
                    }
                    case (3):{
                        filterByCategory("Fitness");
                        break;
                    }
                    case (4):{
                        filterByCategory("Arts/Crafts");
                        break;
                    }
                    case (5):{
                        filterByCategory("Other");
                        break;
                    }
                    default:
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                    int i = 0;
            }
        });


    }

    private void filterByCategory(String category) {
        mWorkshops.clear();
        classAdapter.notifyDataSetChanged();
        Query parseQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        parseQuery.getAllClasses().withItems().byCategory(category).getClassesNotTaking();

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


