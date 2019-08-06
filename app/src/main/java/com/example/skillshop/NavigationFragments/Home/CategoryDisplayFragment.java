package com.example.skillshop.NavigationFragments.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.skillshop.Adapters.ClassAdapterCard;
import com.example.skillshop.FollowingListActivity;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CategoryDisplayFragment extends Fragment {


    private RecyclerView rvClasses;
    protected ArrayList<Workshop> mWorkshops;
    protected ClassAdapterCard classAdapter;
    Spinner spinSorters;
    SearchView searchView;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<String> category;
    TextView tvDisplay;
    private String mainCategory;
    Boolean firstLoad = true;
    TextView tvNote;

    public boolean byLocation;
    public boolean byDate;
    public int byCost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_category_display), container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        spinSorters = view.findViewById(R.id.spinSorters);
        searchView = view.findViewById(R.id.searchView);
        tvNote = view.findViewById(R.id.tvNote);
        tvDisplay = view.findViewById(R.id.tvDisplay);


        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded();
            }
        });


        // establish the category for this class
        Bundle bundle = this.getArguments();
        mainCategory  = bundle.getString("Category");
        tvDisplay.setText(mainCategory);


        connectRecyclerView(view);

        byLocation = false;
        byCost = 0;
        byDate = true;


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
                filterFeed(category);
                swipeContainer.setRefreshing(false);
            }
        });
        setSorters();
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        setUpNavBar(getView());


    }

    public void setUpNavBar(View v)
    {
        BottomNavigationView topNavigationBar = v.findViewById(R.id.top_navigation);

        switch (mainCategory) {
            case "Culinary":
                topNavigationBar.inflateMenu(R.menu.menu_culinary);
                break;
            case "Education":
                topNavigationBar.inflateMenu(R.menu.menu_education);
                break;
            case "Fitness":
                topNavigationBar.inflateMenu(R.menu.menu_fitness);
                break;
            case "Arts/Crafts":
                topNavigationBar.inflateMenu(R.menu.menu_arts);
                break;
            case "Other":
                topNavigationBar.inflateMenu(R.menu.menu_other);
                break;
            default:
                topNavigationBar.inflateMenu(R.menu.menu_culinary);
                break;
        }

        topNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                category = new ArrayList<String>();

                switch(mainCategory)
                {
                    case "Education":
                        switch (item.getItemId()) {
                            case R.id.programming_category:
                                category.add("Programming");
                                break;
                            case R.id.science_category:
                                category.add("Sciences");
                                break;
                            case R.id.humanities_category:
                                category.add("Humanities");
                                break;
                            case R.id.languages_fragment:
                                category.add("Languages");
                                break;
                            case R.id.business_fragment:
                                category.add("Business");
                                break;
                            default:
                                category.add("Programming");
                                break;
                        }
                        break;
                    case "Arts/Crafts":
                        switch (item.getItemId()) {
                            case R.id.music_category:
                                category.add("Music");
                                break;
                            case R.id.paint_category:
                                category.add("Paint");
                                break;
                            case R.id.sculpting_category:
                                category.add("Sculpting");
                                break;
                            case R.id.crafts_fragment:
                                category.add("Craft Making");
                                break;
                            case R.id.graphic_fragment:
                                category.add("Graphic Design");
                                break;
                            default:
                                category.add("Music");
                                break;
                        }
                        break;

                    case "Culinary":
                        switch (item.getItemId()) {
                            case R.id.baking_category:
                                category.add("Baking");
                                break;
                            case R.id.cooking_category:
                                category.add("Cooking");
                                break;
                            case R.id.beverages_category:
                                category.add("Beverages");
                                break;
                            case R.id.grilling_fragment:
                                category.add("Grilling");
                                break;
                            case R.id.advanced_fragment:
                                category.add("Advanced");
                                break;
                            default:
                                category.add("Baking");
                                break;
                        }
                        break;
                    case "Fitness":
                        switch (item.getItemId()) {
                            case R.id.Outdoors_category:
                                category.add("Outdoors");
                                break;
                            case R.id.Gym_category:
                                category.add("Gym");
                                break;
                            case R.id.Sports_category:
                                category.add("Sports");
                                break;
                            case R.id.Dance_fragment:
                                category.add("Dance");
                                break;
                            case R.id.Martial_fragment:
                                category.add("Martial Arts");
                                break;
                            default:
                                break;
                        }
                        break;
                    case "Other":
                        category.add("General");
                        break;
                }

                filterFeed(category);
                return true;
            }
        });

        switch (mainCategory)
        {
            case "Education":
                topNavigationBar.setSelectedItemId(R.id.programming_category);
                break;
            case "Fitness":
                topNavigationBar.setSelectedItemId(R.id.Outdoors_category);
                break;
            case "Arts/Crafts":
                topNavigationBar.setSelectedItemId(R.id.music_category);
                break;
            case "Culinary":
                topNavigationBar.setSelectedItemId(R.id.baking_category);
                break;
            case "Other":
                topNavigationBar.setSelectedItemId(R.id.other_category);
                break;

        }




    }


    public void updateToken()
    {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("firebaseToken", FirebaseInstanceId.getInstance().getToken());
        currentUser.saveInBackground();
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

                switch(position){

                    case (0):{
                        // Date
                        if(!firstLoad) {
                            byCost = 0;
                            byLocation = false;
                            byDate = true;
                            filterFeed(category);
                        }
                        else{
                            firstLoad=!firstLoad;
                        }
                        break;
                    }
                    case (1):{
                        // cost increasing
                        byCost = 2;
                        byLocation = false;
                        byDate = false;
                        filterFeed(category);

                        break;
                    }
                    case (2):{
                        // cost decreasing
                        byCost = 1;
                        byLocation = false;
                        byDate = false;
                        filterFeed(category);
                        break;
                    }
                    case(3):{
                        // location
                        byCost = 0;
                        byLocation = true;
                        byDate = false;
                        filterFeed(category);
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


    private void filterFeed(ArrayList<String> categories) {
        mWorkshops.clear();
        classAdapter.notifyDataSetChanged();
        Query parseQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        parseQuery.getAllClasses().withItems().bySubCategory(categories);

        if(byDate)
        {
            parseQuery.byTimeOfClass();
        }
        else if(byCost == 1)
        {
            parseQuery.byCostDescending();
        }
        else if(byCost == 2)
        {
            parseQuery.byCostAscending();
        }
        else if(byLocation)
        {

            parseQuery.byLocation(ParseUser.getCurrentUser().getParseGeoPoint("userLocation"));
        }

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
                    // if there are no objects give the user an indication that there aren't any more class to discover

                    if(objects.size()==0)
                    {
                        tvNote.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        tvNote.setVisibility(View.INVISIBLE);
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




}


