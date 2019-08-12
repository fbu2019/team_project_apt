package com.example.skillshop.NavigationFragments.Profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.skillshop.Adapters.ClassAdapterCard;
import com.example.skillshop.ClassDescription.ClassDetailsActivity;
import com.example.skillshop.FollowingListActivity;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Ratings;
import com.example.skillshop.Models.User;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private RecyclerView rvClasses;
    protected ArrayList<Workshop> mWorkshops;
    protected ClassAdapterCard classAdapter;

    private int numberOfFollowers = 0;
    private ParseUser currentUser;
    private String profilePhotoUrl;
    private TextView tvNameView;
    private TextView tvNumberFollowers;
    private TextView tvNumberFollowing;
    private TextView tvPreferences;
    private ImageView ivProfileImage;
    private Button btnFollow;

    private RatingBar rbInstructorAverage;


    private float currentRatingAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        currentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        profilePhotoUrl = currentUser.getString("profilePicUrl");

        tvNameView = findViewById(R.id.tvUsername);
        tvNameView.setText(currentUser.get("firstName") + " " + currentUser.get("lastName"));
        ivProfileImage = findViewById(R.id.profileImage);
        loadProfilePicture();

        tvNumberFollowers = findViewById(R.id.numberOfFollowers);
        setTvNumberFollowers();
        tvNumberFollowing = findViewById(R.id.numberFollowing);
        ArrayList<String> friends = (ArrayList<String>) currentUser.get("friends");
        tvNumberFollowing.setText(friends.size() + "");


        tvPreferences = findViewById(R.id.tvPreferences);
        ArrayList<String> preferences = (ArrayList<String>) currentUser.get("preferences");
        String preferenceString = "";
        if (preferences != null && preferences.size() > 0) {
            for (int i = 0; i < preferences.size(); i++) {

                if (i == preferences.size() - 1) {
                    preferenceString += preferences.get(i);
                } else {
                    preferenceString += preferences.get(i) + " | ";
                }
            }
        } else {
            preferenceString += "No current preferences.";
        }
        tvPreferences.setText(preferenceString);



        connectRecyclerView();

        tvNumberFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this, UserFollowersActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(currentUser));
                startActivity(i);
            }
        });

        tvNumberFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this, FollowingListActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(currentUser));
                startActivity(i);
            }
        });

        setUpNavBar();

        setUpRating();

        initFollowButton();

        initializeAverageRating(currentUser.getObjectId());


    }

    private void initFollowButton() {

        btnFollow = findViewById(R.id.btnFollow);

        if (ParseUser.getCurrentUser().getObjectId().equals(currentUser.getObjectId())) {
            btnFollow.setVisibility(View.GONE);
        } else {

            ArrayList<String> myFollowing = (ArrayList<String>) ParseUser.getCurrentUser().get("friends");
            Boolean isFollowing = myFollowing.contains(currentUser.getObjectId());

            if (isFollowing) {
                btnFollow.setText("Unfollow");
                btnFollow.setTextColor(getResources().getColor((R.color.color_palette_dark_grey)));
                btnFollow.setBackgroundColor(getResources().getColor(R.color.light_gray));
            }
            else
            {
                btnFollow.setText("Follow");
                btnFollow.setTextColor(getResources().getColor((R.color.quantum_white_100)));
                btnFollow.setBackgroundColor(getResources().getColor(R.color.color_palette_dark_grey));
            }

            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Gets the list of users being followed by the current user. This has to be done
                    //each time the follow button is clicked because the list may change if the
                    //current user clicks multiple times.
                    ArrayList<String> currentlyFollowing = (ArrayList<String>) ParseUser.getCurrentUser().get("friends");
                    Boolean isCurrentlyFollowing = currentlyFollowing.contains(currentUser.getObjectId());

                    if (!isCurrentlyFollowing) {
                        followInstructor(currentlyFollowing, currentUser.getObjectId(), currentUser, ParseUser.getCurrentUser());
                    } else {
                        unfollowInstructor(currentlyFollowing, currentUser.getObjectId(), currentUser, ParseUser.getCurrentUser());
                    }

                }
            });
        }
    }

    private void initializeAverageRating(String instructorID) {

        rbInstructorAverage = findViewById(R.id.rbInstructorAverage);

        Ratings.Query ratingParseQuery = new Ratings.Query();
        ratingParseQuery.getAllRatings().whereEqualTo("user", currentUser);

        ratingParseQuery.findInBackground(new FindCallback<Ratings>() {

            @Override
            public void done(List<Ratings> objects, ParseException e) {
                if (e == null) {

                    if (objects.size() > 0) {
                        Ratings currentRating = objects.get(0);

                        float avgRating = 0;
                        if (currentRating.getNumRatings() > 0) {
                            avgRating = currentRating.getSumRatings() / currentRating.getNumRatings();
                        }

                        currentRating.setAverageRating((int) avgRating);
                        currentRatingAverage = (float) avgRating;

                        rbInstructorAverage.setRating(currentRatingAverage);
                    }


                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void unfollowInstructor(ArrayList<String> currentlyFollowing, String instructorId, ParseUser instructor, ParseUser currentUser) {

        //Removes the attendee from the current user's following list and saves it to parse
        currentlyFollowing.remove(instructorId);
        ParseUser.getCurrentUser().put("friends", currentlyFollowing);

        login(ParseUser.getCurrentUser().getUsername(), ParseUser.getCurrentUser().getUsername());
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(UserProfileActivity.this, "You are no longer following " + instructor.get("firstName"), Toast.LENGTH_LONG).show();
                } else {
                    Log.e("InstructorDetails", "error saving");
                    e.printStackTrace();
                }
            }
        });

        btnFollow.setText("Follow");
        btnFollow.setTextColor(getResources().getColor((R.color.quantum_white_100)));
        btnFollow.setBackgroundColor(getResources().getColor(R.color.color_palette_dark_grey));
        numberOfFollowers--;
        tvNumberFollowers.setText("" + numberOfFollowers);
    }

    private void followInstructor(ArrayList<String> currentlyFollowing, String instructorId, ParseUser instructor, ParseUser currentUser) {

        //Adds the attendee to the current user's following list and saves it to parse
        currentlyFollowing.add(instructorId);
        ParseUser.getCurrentUser().put("friends", currentlyFollowing);

        login(ParseUser.getCurrentUser().getUsername(), ParseUser.getCurrentUser().getUsername());
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(UserProfileActivity.this, "You are now following " + instructor.get("firstName"), Toast.LENGTH_LONG).show();
                } else {
                    Log.e("InstructorDetails", "error saving");
                    e.printStackTrace();
                }
            }
        });

        //Resets the following button
        btnFollow.setText("Unfollow");
        btnFollow.setTextColor(getResources().getColor((R.color.color_palette_dark_grey)));
        btnFollow.setBackgroundColor(getResources().getColor(R.color.light_gray));
        numberOfFollowers++;
        tvNumberFollowers.setText(numberOfFollowers + "");
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
                    e.printStackTrace();
                }
            }
        });
    }


    public void setUpRating()
    {
        Button btnRate = findViewById(R.id.btnRate);
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this, InstructorDetailsActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(currentUser));
                startActivity(i);
            }
        });
    }
    private void setUpNavBar() {
        BottomNavigationView topNavigationBar = findViewById(R.id.top_navigation);
        topNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.taking:
                        populateTaking();
                        break;
                    case R.id.teaching:
                        populateTeaching();
                        break;
                    default: break;
                }
                return true;
            }
        });
        // default fragment in home fragment
        topNavigationBar.setSelectedItemId(R.id.taking);
    }





    private void loadProfilePicture() {

        Log.i("UserProfileACTIVITY", profilePhotoUrl);

        if (profilePhotoUrl != null) {
            Glide.with(UserProfileActivity.this).load(profilePhotoUrl).apply(new RequestOptions().circleCrop()).into(ivProfileImage);
        } else {
            ivProfileImage.setImageBitmap(null);
            Log.i("Instructor Details", "No profile image");
        }
    }

    private void connectRecyclerView() {

        Log.i("UseProfileActivity", "reached connect method ");
        //find the RecyclerView
        rvClasses = (RecyclerView) findViewById(R.id.rvClasses);
        //init the arraylist (data source)
        mWorkshops = new ArrayList<>();
        //construct the adapter from this datasource
        classAdapter = new ClassAdapterCard(mWorkshops, UserProfileActivity.this);
        //RecyclerView setup (layout manager, use adapter)
        rvClasses.setLayoutManager(new LinearLayoutManager(UserProfileActivity.this));
        //set the adapter
        rvClasses.setAdapter(classAdapter);

        // add dividers on posts
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvClasses.getContext(),
                new LinearLayoutManager(UserProfileActivity.this).getOrientation());
        rvClasses.addItemDecoration(dividerItemDecoration);
    }

    public void populateTeaching() {

        mWorkshops.clear();
        classAdapter.notifyDataSetChanged();

        Query userClassesQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        userClassesQuery.getAllClasses().withItems();

        userClassesQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        if (workshopItem.getTeacher().getUsername().equals(currentUser.getUsername())) {
                            mWorkshops.add(workshopItem);
                            classAdapter.notifyItemInserted(mWorkshops.size() - 1);
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void populateTaking() {

        mWorkshops.clear();
        classAdapter.notifyDataSetChanged();

        Query userClassesQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        userClassesQuery.getAllClasses().withItems();

        userClassesQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {
                    for(Workshop workshop : objects)

                        for(String id : (ArrayList<String>) workshop.getStudents()) {

                            if (id.equals(currentUser.getObjectId())) {
                                mWorkshops.add(workshop);
                                classAdapter.notifyItemInserted(mWorkshops.size() - 1);
                            }

                        }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setTvNumberFollowers() {

        Log.e("UserProfileActivity", "REACHED HERE");

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> allUsers, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < allUsers.size(); i++) {
                        ParseUser userItem = allUsers.get(i);

                        if (!(userItem.getUsername().equals(currentUser.getUsername()))) {

                            Log.i("UserProfileActivity", "reached another user ");
                            ArrayList<String> usersFollowing = (ArrayList<String>) userItem.get("friends");
                            for (int j = 0; j < usersFollowing.size(); j++) {
                                if (usersFollowing.get(j).equals(currentUser.getObjectId())) {
                                    Log.i("UserProfileActivity", "num followers " + numberOfFollowers);
                                    numberOfFollowers++;
                                }
                            }
                        }
                    }

                    tvNumberFollowers.setText(numberOfFollowers + "");
                    Log.i("UserProfileActivity", "num followers " + numberOfFollowers);

                } else {
                    e.printStackTrace();
                    Log.e("UserProfileActivity", "ERROR ");
                }
            }
        });
    }








}
