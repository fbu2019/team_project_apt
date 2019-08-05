package com.example.skillshop.NavigationFragments.Profile.ClassesActivities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.skillshop.Adapters.ClassAdapterCard;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.Models.Query;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ClassesInvolvedFragment extends Fragment {

    private RecyclerView rvClasses;
    protected ArrayList<Workshop> mWorkshops;
    protected ClassAdapterCard classAdapter;
    private SwipeRefreshLayout swipeContainer;
    private boolean taking;


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_classes_list), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        taking = bundle.getBoolean("taking");




        connectRecyclerView(view);
        getClassesTaking();
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                getClassesTaking();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    private void connectRecyclerView(View view) {
        //find the RecyclerView
        rvClasses = (RecyclerView) view.findViewById(R.id.rvClasses);
        //init the arraylist (data source)
        mWorkshops = new ArrayList<>();
        //construct the adapter from this datasource
        classAdapter = new ClassAdapterCard(mWorkshops, getContext());
        //RecyclerView setup (layout manager, use adapter)
        rvClasses.setLayoutManager(new LinearLayoutManager(getContext()));
        //set the adapter
        rvClasses.setAdapter(classAdapter);


        // add dividers on posts
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvClasses.getContext(),
                new LinearLayoutManager(getContext()).getOrientation());
        rvClasses.addItemDecoration(dividerItemDecoration);



    }



    public void getClassesTaking() {

        mWorkshops.clear();
        classAdapter.notifyDataSetChanged();
        if(ParseUser.getCurrentUser().getUsername()!=null) {
            // get all the classes the user is taking and display them
            Query parseQuery = new Query();

            if(taking){
                parseQuery.getAllClasses().getClassesTaking().withItems().byTimeOfClass();
            }
            else
            {
                parseQuery.getAllClasses().getClassesTeaching().withItems().byTimeOfClass();
            }


            parseQuery.findInBackground(new FindCallback<Workshop>() {
                @Override
                public void done(List<Workshop> objects, ParseException e) {
                    if (e == null) {

                        for (int i = 0; i < objects.size(); i++) {
                            Workshop workshopItem = objects.get(i);
                            mWorkshops.add(workshopItem);
                            classAdapter.notifyItemInserted(mWorkshops.size() - 1);
                        }
                    } else {
                        e.printStackTrace();
                    }
                    swipeContainer.setRefreshing(false);

                }
            });
        }
    }
}


