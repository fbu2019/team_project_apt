package com.example.skillshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.skillshop.Adapters.ClassAdapter;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.User;
import com.example.skillshop.Models.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private RecyclerView rvClasses;
    protected ArrayList<Workshop> mWorkshops;
    protected ClassAdapter classAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        connectRecyclerView();
        populateHomeFeed();
    }

    private void connectRecyclerView() {
        //find the RecyclerView
        rvClasses = (RecyclerView) findViewById(R.id.rvClasses);
        //init the arraylist (data source)
        mWorkshops = new ArrayList<>();
        //construct the adapter from this datasource
        classAdapter = new ClassAdapter(mWorkshops, UserProfileActivity.this);
        //RecyclerView setup (layout manager, use adapter)
        rvClasses.setLayoutManager(new LinearLayoutManager(UserProfileActivity.this));
        //set the adapter
        rvClasses.setAdapter(classAdapter);


        // add dividers on posts
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvClasses.getContext(),
                new LinearLayoutManager(UserProfileActivity.this).getOrientation());
        rvClasses.addItemDecoration(dividerItemDecoration);
    }
    public void populateHomeFeed() {

        mWorkshops.clear();
        classAdapter.notifyDataSetChanged();

        ArrayList<String> classes = (ArrayList<String>) ParseUser.getCurrentUser().get("classesTaking");
        Query userClassesQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        userClassesQuery.getAllClasses().withItems().hasClasses(classes);

        userClassesQuery.findInBackground(new FindCallback<Workshop>() {
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
