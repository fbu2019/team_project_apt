package com.example.skillshop.ClassDescription;

import org.parceler.Parcels;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.skillshop.FollowingListActivity;
import com.example.skillshop.Models.Ratings;
import com.example.skillshop.Models.User;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.NavigationFragments.Profile.UserFollowersActivity;
import com.example.skillshop.NavigationFragments.Profile.UserProfileActivity;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstructorDetailsActivity extends AppCompatActivity {

    private Workshop detailedWorkshop;
    private ImageView ivInstructorProfile;
    private TextView tvInstructorName;
    private TextView tvNotYetRated;
    private TextView tvNumRatings;
    private TextView tvUserProvidedRating;
    private TextView tvNumberOfFollowers;
    private TextView tvNumberOfFollowersMessage;
    private TextView tvNumberFollowing;
    private TextView tvNumberFollowingMessage;
    private Button followInstructorButton;
    private RatingBar rbInstructorAverage;
    private RatingBar rbUserRating;

    private String profilePhotoUrl;
    private float currentRatingAverage;
    private int numberOfFollowers = 0;
    boolean hasRatedBefore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_details);

        detailedWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));
        tvInstructorName = findViewById(R.id.instructorName);
        tvInstructorName.setText(detailedWorkshop.getTeacher().getString("firstName") + " " + detailedWorkshop.getTeacher().getString("lastName"));

        setNumFollowers();
        setNumFollowing();
        initFollowButton();
        loadProfilePicture();
        initRatingBar();
    }

    private void loadProfilePicture() {

        ivInstructorProfile = findViewById(R.id.instructorProfile);
        profilePhotoUrl = detailedWorkshop.getTeacher().getString("profilePicUrl");

        if (profilePhotoUrl != null) {
            Glide.with(InstructorDetailsActivity.this).load(profilePhotoUrl).apply(new RequestOptions().circleCrop()).into(ivInstructorProfile);
        } else {
            ivInstructorProfile.setImageBitmap(null);
            Log.i("Instructor Details", "No profile image");
        }
    }

    private void initFollowButton() {

        followInstructorButton = findViewById(R.id.followInstructor);

        if (ParseUser.getCurrentUser().getObjectId().equals(detailedWorkshop.getTeacher().getObjectId())) {
            followInstructorButton.setVisibility(View.GONE);
        } else {

            ArrayList<String> myFollowing = (ArrayList<String>) ParseUser.getCurrentUser().get("friends");
            Boolean isFollowing = myFollowing.contains(detailedWorkshop.getTeacher().getObjectId());

            if (isFollowing) {
                followInstructorButton.setText("UNFOLLOW INSTRUCTOR");
            }

            followInstructorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Gets the list of users being followed by the current user. This has to be done
                    //each time the follow button is clicked because the list may change if the
                    //current user clicks multiple times.
                    ArrayList<String> currentlyFollowing = (ArrayList<String>) ParseUser.getCurrentUser().get("friends");
                    Boolean isCurrentlyFollowing = currentlyFollowing.contains(detailedWorkshop.getTeacher().getObjectId());

                    if (!isCurrentlyFollowing) {
                        followInstructor(currentlyFollowing, detailedWorkshop.getTeacher().getObjectId(), detailedWorkshop.getTeacher(), ParseUser.getCurrentUser());
                    } else {
                        unfollowInstructor(currentlyFollowing, detailedWorkshop.getTeacher().getObjectId(), detailedWorkshop.getTeacher(), ParseUser.getCurrentUser());
                    }

                }
            });
        }
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
                    Toast.makeText(InstructorDetailsActivity.this, "You are no longer following " + instructor.get("firstName"), Toast.LENGTH_LONG).show();
                } else {
                    Log.e("InstructorDetails", "error saving");
                    e.printStackTrace();
                }
            }
        });

        followInstructorButton.setText("FOLLOW INSTRUCTOR");
        numberOfFollowers--;
        tvNumberOfFollowers.setText("" + numberOfFollowers);
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
                    Toast.makeText(InstructorDetailsActivity.this, "You are now following " + instructor.get("firstName"), Toast.LENGTH_LONG).show();
                } else {
                    Log.e("InstructorDetails", "error saving");
                    e.printStackTrace();
                }
            }
        });

        //Resets the following button
        followInstructorButton.setText("UNFOLLOW INSTRUCTOR");
        numberOfFollowers++;
        tvNumberOfFollowers.setText(numberOfFollowers + "");
    }

    private void initRatingBar() {

        rbUserRating = findViewById(R.id.userRating);
        rbInstructorAverage = findViewById(R.id.instructorAverage);
        rbInstructorAverage.setNumStars(5);
        rbInstructorAverage.setIsIndicator(true);

        tvNumRatings = findViewById(R.id.numRatings);
        tvNotYetRated = findViewById(R.id.notRated);
        tvUserProvidedRating = findViewById(R.id.userProvideRating);

        if (ParseUser.getCurrentUser().getUsername().equals(detailedWorkshop.getTeacher().getUsername())) {

            rbUserRating.setEnabled(false);
            tvUserProvidedRating.setText("Instructors cannot rate themselves");

        } else {

            checkIfRated(ParseUser.getCurrentUser().getUsername()); // Checks to see if current user has rated the instructor before
        }

        initializeAverageRating(detailedWorkshop.getTeacher().getUsername());
        checkIfRated(ParseUser.getCurrentUser().getUsername());


        rbUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                Log.e("InstructorDetails", "Current setting is " + hasRatedBefore);
                updateRating(hasRatedBefore, rating, detailedWorkshop.getTeacher().getUsername());
                tvUserProvidedRating.setText("You have provided " + detailedWorkshop.getTeacher().get("firstName") + " with a rating of " + rbUserRating.getRating());
                tvNotYetRated.setText(" ");
            }
        });
    }

    private void initializeAverageRating(String instructorID) {

        Ratings.Query ratingParseQuery = new Ratings.Query();
        ratingParseQuery.getAllRatings().whereEqualTo("user", detailedWorkshop.getTeacher());

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

                        int currentNumberOfRatings = currentRating.getNumRatings();

                        if (currentNumberOfRatings == 0) {

                            rbInstructorAverage.setEnabled(false);
                            tvNotYetRated = findViewById(R.id.notRated);
                            tvNotYetRated.setText("This instructor has not been rated.");

                        } else if (currentNumberOfRatings == 1) {

                            rbInstructorAverage.setRating(currentRatingAverage);
                            tvNumRatings.setText(detailedWorkshop.getTeacher().get("firstName") + " has been rated by one user.");

                        } else if (currentNumberOfRatings > 1) {

                            rbInstructorAverage.setRating(currentRatingAverage);
                            tvNumRatings.setText(detailedWorkshop.getTeacher().get("firstName") + " has been rated by " + currentNumberOfRatings + " users.");
                        }
                    } else {


                        rbInstructorAverage.setEnabled(false);
                        tvNotYetRated = findViewById(R.id.notRated);
                        tvNotYetRated.setText("This instructor has not been rated.");

                    }


                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateRating(Boolean userRatedBefore, float ratingValue, String instructorID) {

        Ratings.Query ratingParseQuery = new Ratings.Query();
        ratingParseQuery.getAllRatings().whereEqualTo("user", detailedWorkshop.getTeacher());

        ratingParseQuery.findInBackground(new FindCallback<Ratings>() {

            @Override
            public void done(List<Ratings> objects, ParseException e) {
                if (e == null) {

                    if (objects.size() > 0) {
                        Ratings currentRating = objects.get(0);
                        int currentNumberOfRatings = currentRating.getNumRatings();
                        int currentSumOfRatings = currentRating.getSumRatings();

                        HashMap<String, Integer> usersWhoRated = (HashMap<String, Integer>) currentRating.get("userRatings");

                        if (userRatedBefore) {

                            int formerRating = usersWhoRated.get(ParseUser.getCurrentUser().getUsername());
                            usersWhoRated.put(ParseUser.getCurrentUser().getUsername(), (int) ratingValue);
                            currentRating.put("userRatings", usersWhoRated);

                            currentRating.setSumRatings(currentSumOfRatings - formerRating + (int) ratingValue);

                            if (currentRating.getNumRatings() == 0) {
                                int avgRating = 0;
                                currentRating.setAverageRating(avgRating);
                                currentRatingAverage = (float) avgRating;
                                rbInstructorAverage.setRating(currentRatingAverage);

                            } else {
                                int avgRating = currentRating.getSumRatings() / currentRating.getNumRatings();
                                currentRating.setAverageRating(avgRating);
                                currentRatingAverage = (float) avgRating;
                                rbInstructorAverage.setRating(currentRatingAverage);
                            }

                            if (currentNumberOfRatings == 1) {

                                tvNumRatings.setText(detailedWorkshop.getTeacher().get("firstName") + " has been rated by one user.");

                            } else {
                                tvNumRatings.setText(detailedWorkshop.getTeacher().get("firstName") + " has been rated by " + currentNumberOfRatings + " users.");
                            }

                    } else {

                        usersWhoRated.put(ParseUser.getCurrentUser().getUsername(), (int) ratingValue);
                        currentRating.put("userRatings", usersWhoRated);

                        currentRating.setNumRatings(currentNumberOfRatings + 1);
                        currentNumberOfRatings++; //used to update text views before rating is savedInBackground
                        currentRating.setSumRatings(currentSumOfRatings + (int) ratingValue);

                        int avgRating = currentRating.getSumRatings() / currentRating.getNumRatings();
                        currentRating.setAverageRating(avgRating);
                        currentRatingAverage = (float) avgRating;

                        rbInstructorAverage.setRating(currentRatingAverage);

                        if (currentNumberOfRatings == 1) {

                            tvNumRatings.setText(detailedWorkshop.getTeacher().get("firstName") + " has been rated by one user."); //   todo - fix and do not allow users to rate multiple times

                        } else {
                            tvNumRatings.setText(detailedWorkshop.getTeacher().get("firstName") + " has been rated by " + currentNumberOfRatings + " users.");
                        }

                        hasRatedBefore = true;

                    }

                    currentRating.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if (instructorID != ParseUser.getCurrentUser().getUsername()) {
                                    Toast.makeText(InstructorDetailsActivity.this, "Your rating has been recorded", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(InstructorDetailsActivity.this, "Error saving changes", Toast.LENGTH_SHORT).show();
                                Log.e("InstructorActivity", "CHANGES NOT SAVED");
                            }

                        }
                    });
                } else {
                      Log.e("InstructorDetails", "Error with rating in database");
                    }
                } else {
                    e.printStackTrace();

                }
            }
        });

    }


    private void setNumFollowers() {

        tvNumberOfFollowers = findViewById(R.id.numFollowers);

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> allUsers, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < allUsers.size(); i++) {
                        ParseUser userItem = allUsers.get(i);

                        if (userItem != detailedWorkshop.getTeacher()) {

                            ArrayList<String> usersFollowing = (ArrayList<String>) userItem.get("friends");
                            for (int j = 0; j < usersFollowing.size(); j++) {
                                if (usersFollowing.get(j).equals(detailedWorkshop.getTeacher().getObjectId())) {
                                    numberOfFollowers++;
                                }
                            }
                        }
                    }

                    tvNumberOfFollowers.setText(numberOfFollowers + "");

                } else {
                    e.printStackTrace();
                }
            }
        });

        tvNumberOfFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InstructorDetailsActivity.this, UserFollowersActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(ParseUser.getCurrentUser()));
                startActivity(i);
            }
        });

        tvNumberOfFollowersMessage = findViewById(R.id.numberOfFollowers);
        tvNumberOfFollowersMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InstructorDetailsActivity.this, UserFollowersActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(ParseUser.getCurrentUser()));
                startActivity(i);
            }
        });
    }


    private void setNumFollowing() {

        tvNumberFollowing = findViewById(R.id.numFollowing);
        ArrayList<String> instructorFollowing = (ArrayList<String>) detailedWorkshop.getTeacher().get("friends");
        int numFollowing = instructorFollowing.size();
        tvNumberFollowing.setText(numFollowing + "");

        tvNumberFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InstructorDetailsActivity.this, FollowingListActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(ParseUser.getCurrentUser()));
                startActivity(i);
            }
        });

        tvNumberFollowingMessage = findViewById(R.id.numberFollowing);
        tvNumberFollowingMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InstructorDetailsActivity.this, FollowingListActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(ParseUser.getCurrentUser()));
                startActivity(i);
            }
        });


    }

    private void checkIfRated(String userID) {

        hasRatedBefore = false;

        Ratings.Query ratingParseQuery = new Ratings.Query();
        ratingParseQuery.getAllRatings().whereEqualTo("user", detailedWorkshop.getTeacher());

        ratingParseQuery.findInBackground(new FindCallback<Ratings>() {

            @Override
            public void done(List<Ratings> objects, ParseException e) {
                if (e == null) {

                    if (objects.size() > 0) {
                        Ratings currentRating = objects.get(0);
                        HashMap<String, Integer> usersWhoRated = (HashMap<String, Integer>) currentRating.get("userRatings");

                        if (usersWhoRated.get(userID) != null) {

                            Log.e("InstructorDetails", "User has rated before with rating " + usersWhoRated.get(userID));
                            hasRatedBefore = true;
                            tvUserProvidedRating.setText("You have previously rated this instructor. You may modify your rating above.");

                        }
                    } else {

                        tvUserProvidedRating.setText("Provide a rating for this instructor above");
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });
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
}
