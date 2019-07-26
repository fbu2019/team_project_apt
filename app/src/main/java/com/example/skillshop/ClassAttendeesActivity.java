package com.example.skillshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.skillshop.ClassManipulationActivities.ClassDetailsActivity;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ClassAttendeesActivity extends AppCompatActivity {

    Workshop currentWorkshop;

    private RecyclerView rvUsers;
    protected ArrayList<ParseUser> mUsers;
    protected UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_attendees);

        getStudentArray();
        //find the RecyclerView
        rvUsers = (RecyclerView) findViewById(R.id.rvUsers);
        //init the arraylist (data source)
        mUsers = new ArrayList<>();
        //construct the adapter from this datasource
        userAdapter = new UserAdapter(mUsers, ClassAttendeesActivity.this);
        //RecyclerView setup (layout manager, use adapter)
        rvUsers.setLayoutManager(new LinearLayoutManager(ClassAttendeesActivity.this));
        //set the adapter
        rvUsers.setAdapter(userAdapter);
        // add dividers to list of attendees
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvUsers.getContext(),
                new LinearLayoutManager(this).getOrientation());
        rvUsers.addItemDecoration(dividerItemDecoration);




    }

    private void getStudentArray() {

        currentWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));
        ArrayList<String> students = (ArrayList<String>) currentWorkshop.getStudents();

        for (int i = 0; i < students.size(); i++ ){
            final ParseQuery<ParseUser> userQuery = ParseUser.getQuery().whereMatches("objectId", students.get(i));
            userQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> attendees, ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < attendees.size(); i++) {
                            ParseUser userItem = attendees.get(i);
                            mUsers.add(userItem);
                            userAdapter.notifyItemInserted(mUsers.size()-1);
                        }
                    } else {
                        e.printStackTrace();
                    }

                }
            });
        }

    }
}
