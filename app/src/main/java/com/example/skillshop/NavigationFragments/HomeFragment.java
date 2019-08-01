package com.example.skillshop.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.skillshop.Adapters.ClassAdapterCard;
import com.example.skillshop.FollowingListActivity;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.Models.Query;
import com.example.skillshop.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    private static final String CHANNEL_ID = "CHANNEL_ID";
    private RecyclerView rvClasses;
    protected ArrayList<Workshop> mWorkshops;
    protected ClassAdapterCard classAdapter;


    Spinner spinSorters;
    Spinner spinFilters;
    Button btnMap;
    Button btnPreferenceFilter;
    Button btnFollowing;
    private SwipeRefreshLayout swipeContainer;

    Boolean firstLoad = true;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_home), container, false);
    }




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        spinSorters = view.findViewById(R.id.spinSorters);
        spinFilters = view.findViewById(R.id.spinFilters);

        setupPreferenceFilterButton(view);
        setupFollowingListButton(view);

        connectRecyclerView(view);
      //  populateHomeFeed();

        updateToken();
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateHomeFeed();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }




    private void setupFollowingListButton(View view) {
        btnFollowing = view.findViewById(R.id.btnFollowing);
        btnFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openFollowerListActivity = new Intent (getContext(), FollowingListActivity.class);
                startActivity(openFollowerListActivity);
            }
        });
    }

    public void updateToken()
    {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("firebaseToken", FirebaseInstanceId.getInstance().getToken());
        currentUser.saveInBackground();
    }

    private void setupPreferenceFilterButton(View view) {
        btnPreferenceFilter = view.findViewById(R.id.btnPreferenceFilter);

        btnPreferenceFilter.setOnClickListener(new View.OnClickListener() {
            ArrayList<String> preferenceList = new ArrayList<String>();
            @Override
            public void onClick(View v) {
                JSONArray preferenceArray = ParseUser.getCurrentUser().getJSONArray("preferences");
                for (int i = 0; i < preferenceArray.length(); i++){
                    try {
                        preferenceList.add(preferenceArray.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                filterByCategory(preferenceList);

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

    @Override
    public void onResume() {
        super.onResume();
        firstLoad = true;
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

                        populateHomeFeed();
                        break;
                    }
                    case (1):{
                        populateByCost(true);
                        break;
                    }
                    case (2):{
                        populateByCost(false);
                        break;
                    }
                    case(3):{

                        final ParseQuery<ParseUser> userQuery = new ParseQuery<ParseUser>(ParseUser.class);
                        userQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                        userQuery.findInBackground(new FindCallback<ParseUser>() {

                            @Override
                            public void done(List<ParseUser> singletonUserList, ParseException e) {
                                ParseGeoPoint userLocation = singletonUserList.get(0).getParseGeoPoint("userLocation");
                                populateByLocation(userLocation);
                            }
                        });
                        break;
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

                ArrayList<String> singletonCategory = new ArrayList<String>();
                //     classAdapter.notifyDataSetChanged();
                switch(position){

                    case (0):{
                        //case zero is called when the activity is created so we do not need to call populateHomeFeed in onCreate
                        if (firstLoad){
                            firstLoad = false;
                        }else{
                            populateHomeFeed();
                        }
                        break;
                    }
                    case (1):{

                        singletonCategory.add("Culinary");
                       filterByCategory(singletonCategory);
                        break;
                    }
                    case (2):{
                        singletonCategory.add("Education");
                        filterByCategory(singletonCategory);
                        break;
                    }
                    case (3):{
                        singletonCategory.add("Fitness");
                        filterByCategory(singletonCategory);
                        break;
                    }
                    case (4):{
                        singletonCategory.add("Arts/Crafts");
                        filterByCategory(singletonCategory);
                        break;
                    }
                    case (5):{
                        singletonCategory.add("Other");
                        filterByCategory(singletonCategory);
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

    private void filterByCategory(ArrayList<String> categories) {
        mWorkshops.clear();
        classAdapter.notifyDataSetChanged();
        Query parseQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        parseQuery.getAllClasses().withItems().byCategory(categories).getClassesNotTaking();

        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                //
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        mWorkshops.add(workshopItem);
                        classAdapter.notifyItemInserted(mWorkshops.size() - 1);
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

    private void populateByCost(boolean ascending) {
        mWorkshops.clear();
        classAdapter.notifyDataSetChanged();
        Query parseQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        if (ascending) {
            parseQuery.getAllClasses().withItems().byCostAscending().getClassesNotTaking();
        }else{
            parseQuery.getAllClasses().withItems().byCostDescending().getClassesNotTaking();
        }


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
        classAdapter = new ClassAdapterCard(mWorkshops, getContext());

        final GridLayoutManager layout = new GridLayoutManager(getActivity(), 1);
        //RecyclerView setup (layout manager, use adapter)
        rvClasses.setLayoutManager(layout);
        //set the adapter
        rvClasses.setAdapter(classAdapter);


    }



    public void populateHomeFeed() {

        mWorkshops.clear();
        classAdapter.notifyDataSetChanged();

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
                swipeContainer.setRefreshing(false);
            }
        });
    }
}


