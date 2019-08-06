package com.example.skillshop.NavigationFragments.Profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.skillshop.Adapters.UserAdapter;
<<<<<<< HEAD:app/src/main/java/com/example/skillshop/UserFollowersActivity.java
import com.example.skillshop.Models.User;
=======
import com.example.skillshop.R;
>>>>>>> 16fb0e258b9bb10d7ade37d5947fa82b8f72760b:app/src/main/java/com/example/skillshop/NavigationFragments/Profile/UserFollowersActivity.java
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class UserFollowersActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    protected ArrayList<ParseUser> mUsers;
    protected UserAdapter userAdapter;
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_followers);

        currentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        getFollowersArray(currentUser);

        rvUsers = (RecyclerView) findViewById(R.id.rvFollowers);
        //init data source
        mUsers = new ArrayList<>();
        //construct adapter from this data source
        userAdapter = new UserAdapter(mUsers, UserFollowersActivity.this);
        // RecyclerView setup (layout manager, user adapter)
        rvUsers.setLayoutManager(new LinearLayoutManager(UserFollowersActivity.this));
        //set the adapter
        rvUsers.setAdapter(userAdapter);
        // add dividers to list of followers
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvUsers.getContext(),
                new LinearLayoutManager(this).getOrientation());
        rvUsers.addItemDecoration(dividerItemDecoration);

    }

    private void getFollowersArray(ParseUser currentUser) {

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> allUsers, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < allUsers.size(); i++) {
                        ParseUser userItem = allUsers.get(i);

                        if (!userItem.equals(currentUser)) {

                            ArrayList<String> usersFollowing = (ArrayList<String>) userItem.get("friends");
                            for (int j = 0; j < usersFollowing.size(); j++) {
                                if (usersFollowing.get(j).equals(currentUser.getObjectId())) {

                                    mUsers.add(userItem);
                                    userAdapter.notifyItemInserted(mUsers.size() - 1);
                                }
                            }
                        }
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });

    }
}
