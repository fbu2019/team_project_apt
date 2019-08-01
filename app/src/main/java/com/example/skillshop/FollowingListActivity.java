package com.example.skillshop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.skillshop.Adapters.UserAdapter;
import com.example.skillshop.LoginActivities.LoginActivity;
import com.example.skillshop.LoginActivities.SignupActivity;
import com.example.skillshop.NavigationFragments.FragmentHandler;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FollowingListActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    protected ArrayList<ParseUser> mUsers;
    protected UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_list);

        login(ParseUser.getCurrentUser().getUsername(), ParseUser.getCurrentUser().getUsername());

        getStudentArray();
        //find the RecyclerView
        rvUsers = (RecyclerView) findViewById(R.id.rvUsers);
        //init the arraylist (data source)
        mUsers = new ArrayList<>();
        //construct the adapter from this datasource
        userAdapter = new UserAdapter(mUsers, FollowingListActivity.this);
        //RecyclerView setup (layout manager, use adapter)
        rvUsers.setLayoutManager(new LinearLayoutManager(FollowingListActivity.this));
        //set the adapter
        rvUsers.setAdapter(userAdapter);
        // add dividers to list of attendees
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvUsers.getContext(),
                new LinearLayoutManager(this).getOrientation());
        rvUsers.addItemDecoration(dividerItemDecoration);
    }

    private void getStudentArray() {

        ArrayList<String> following = (ArrayList<String>) ParseUser.getCurrentUser().get("friends");
        Log.i("FollowingList", "Number of friends "+following.size());
        for (int i = 0; i < following.size(); i++) {
            final ParseQuery<ParseUser> userQuery = ParseUser.getQuery().whereMatches("objectId", following.get(i));
            Log.i("FollowingList", "made parseQuery");
            userQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> attendees, ParseException e) {
                    if (e == null) {
                        Log.i("FollowingList", "Reached before loop");
                        for (int i = 0; i < attendees.size(); i++) {
                            ParseUser userItem = attendees.get(i);
                            Log.i("FollowingList", String.valueOf(attendees.get(i).get("firstName")));
                            if (userItem != ParseUser.getCurrentUser() && userItem != null) {
                                mUsers.add(userItem);
                                userAdapter.notifyItemInserted(mUsers.size() - 1);
                            }
                        }
                    } else {

                        Log.i("FollowingList", "ParseException encountered");
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void login(String username, String password) {

        Log.i("LoginActivity", "Reached login method");
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login successful");
                } else {
                    Log.e("LoginActivity", "Login failure");
                }
            }
        });
    }

}
