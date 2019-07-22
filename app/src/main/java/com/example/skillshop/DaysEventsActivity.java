package com.example.skillshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DaysEventsActivity extends AppCompatActivity {

    private TextView tvDate;
    private RecyclerView rvClasses;
    protected ArrayList<Workshop> mWorkshops;
    protected ClassAdapter classAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_events);

        tvDate = findViewById(R.id.tvDate);

        Long dateLong = getIntent().getLongExtra("Date",0);
        Date date = new Date(dateLong);
        SimpleDateFormat format = new SimpleDateFormat("E MMM dd YYYY");
        tvDate.setText(format.format(date));

        connectRecyclerView();
        populateHomeFeed(dateLong);

    }

    private void connectRecyclerView() {
        //find the RecyclerView
        rvClasses = (RecyclerView) findViewById(R.id.rvClasses);
        //init the arraylist (data source)
        mWorkshops = new ArrayList<>();
        //construct the adapter from this datasource
        classAdapter = new ClassAdapter(mWorkshops, this);
        //RecyclerView setup (layout manager, use adapter)
        rvClasses.setLayoutManager(new LinearLayoutManager(this));
        //set the adapter
        rvClasses.setAdapter(classAdapter);


        // add dividers on posts
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvClasses.getContext(),
                new LinearLayoutManager(this).getOrientation());
        rvClasses.addItemDecoration(dividerItemDecoration);
    }

    public void populateHomeFeed(Long date) {

        Query parseQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        parseQuery.getAllClasses().withItems().byTimeOfClass().onDate(date);

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
