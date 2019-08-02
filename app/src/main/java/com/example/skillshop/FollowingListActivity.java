package com.example.skillshop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.skillshop.Adapters.UserAdapter;
import com.example.skillshop.LoginActivities.LoginActivity;
import com.example.skillshop.LoginActivities.SignupActivity;
import com.example.skillshop.NavigationFragments.FragmentHandler;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class FollowingListActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    protected ArrayList<ParseUser> mUsers;
    protected UserAdapter userAdapter;
    public int nullIndex;

    //TODO IF A USER IS NULL REMOVE IT FROM ARRAYLIST

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_list);

        loginAndRefresh(ParseUser.getCurrentUser().getUsername(), ParseUser.getCurrentUser().getUsername());

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

    // Log in user again to work around retrieval issues
    private void loginAndRefresh(String username, String password) {

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login successful");
                    ArrayList<String> following = (ArrayList<String>) ParseUser.getCurrentUser().get("friends");
                    getStudentArray();

                } else {
                    Log.e("LoginActivity", "Login failure");
                    e.printStackTrace();
                }
            }
        });
    }

    private void getStudentArray() {
        ArrayList<String> following = (ArrayList<String>) ParseUser.getCurrentUser().get("friends");
        ArrayList<String> validUserIDs = new ArrayList<>();

        for (int i = 0; i < following.size(); i++) {
            nullIndex = i;
            final ParseQuery<ParseUser> userQuery = ParseUser.getQuery().whereMatches("objectId", following.get(i));
            userQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> attendees, ParseException e) {
                    if (e == null) {

                        Log.i("FollowingListActivity", "Number of ParseUsers: " + attendees.size());

                        for (int i = 0; i < attendees.size(); i++) {
                            ParseUser userItem = attendees.get(i);

                            if (userItem != ParseUser.getCurrentUser() && userItem != null) {
                                mUsers.add(userItem);
                                userAdapter.notifyItemInserted(mUsers.size() - 1);
                            }

                            validUserIDs.add(userItem.getObjectId());
                            replaceUsersFollowing(validUserIDs);
                        }
                    } else {

                        Log.i("FollowingList", "ParseException encountered");
                        e.printStackTrace();
                    }
                }

            });

        }

    }

    private void replaceUsersFollowing(ArrayList <String> validUserIDs) {

        for (int i = 0; i < validUserIDs.size(); i++) {
            Log.i("FollowingList", validUserIDs.get(i));
        }

        ParseUser.getCurrentUser().put("friends", validUserIDs);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e("FollowingList", "Deleted user(s) has been removed from following list");

                } else {
                    Log.e("FollowingList", "error saving");
                    e.printStackTrace();
                }
            }
        });
    }
}
