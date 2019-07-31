package com.example.skillshop;

import org.parceler.Parcels;

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
import com.example.skillshop.Models.Ratings;
import com.example.skillshop.Models.Workshop;
import com.parse.FindCallback;
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
    private Button followInstructorButton;
    private RatingBar rbInstructorAverage;
    private RatingBar rbUserRating;

    private String profilePhotoUrl;
    private float currentRatingAverage;
    private int numberOfFollowers = 0;


    //TODO - make sure user's rating only impacts once
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_details);

        detailedWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));
        tvInstructorName = findViewById(R.id.instructorName);
        tvInstructorName.setText(detailedWorkshop.getTeacher().getString("firstName") + " " + detailedWorkshop.getTeacher().getString("lastName"));

        setNumFollowers();
        initFollowButton();
        loadProfilePicture();
        initRatingBar();
    }

    private void loadProfilePicture() {

        ivInstructorProfile = findViewById(R.id.instructorProfile);
        profilePhotoUrl = detailedWorkshop.getTeacher().getString("profilePicUrl");

        if (profilePhotoUrl != null) {
            Glide.with(InstructorDetailsActivity.this).load(profilePhotoUrl).into(ivInstructorProfile);
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
                followInstructorButton.setText("UNFOLLOW USER");
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

        if(currentlyFollowing.size()>0) {
            Log.e("InstructorDetails", currentlyFollowing.get(0));
        }

        ParseUser.getCurrentUser().put("friends", currentlyFollowing);

        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(InstructorDetailsActivity.this, "You are no longer following " + instructor.get("firstName"), Toast.LENGTH_LONG).show();
                } else {
                    Log.e("InstructorDetails","error saving");
                    e.printStackTrace();
                }
            }
        });

        //Resets the following button
       followInstructorButton.setText("FOLLOW USER");
       numberOfFollowers--;
        if(numberOfFollowers==1){
            tvNumberOfFollowers.setText("1 follower");

        } else {
            tvNumberOfFollowers.setText(numberOfFollowers + " followers");
        }
    }

    private void followInstructor(ArrayList<String> currentlyFollowing, String instructorId, ParseUser instructor, ParseUser currentUser) {

        //Adds the attendee to the current user's following list and saves it to parse
        currentlyFollowing.add(instructorId);
        ParseUser.getCurrentUser().put("friends", currentlyFollowing);
        Log.e("InstructorDetails",currentlyFollowing.get(0));

        for(int i = 0; i<currentlyFollowing.size(); i++){

            Log.e("InstructorDetails","Index "+i+" "+currentlyFollowing.get(0));

        }

        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(InstructorDetailsActivity.this, "You are now following " + instructor.get("firstName"), Toast.LENGTH_LONG).show();
                } else {
                    Log.e("InstructorDetails","error saving");
                    e.printStackTrace();
                }
            }
        });

        //Resets the following button
       followInstructorButton.setText("UNFOLLOW USER");
        numberOfFollowers++;
        if(numberOfFollowers==1){
            tvNumberOfFollowers.setText("1 follower");

        } else {
            tvNumberOfFollowers.setText(numberOfFollowers + " followers");
        }
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

            tvUserProvidedRating.setText("Provide a rating for your instructor above");
        }

        initializeAverageRating(detailedWorkshop.getTeacher().getUsername());


        rbUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                updateRating(rating, detailedWorkshop.getTeacher().getUsername());
                tvUserProvidedRating.setText("You have provided " + detailedWorkshop.getTeacher().get("firstName") + " with a rating of " + rbUserRating.getRating());
                // initializeAverageRating(detailedWorkshop.getTeacher().getUsername());
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
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateRating(float ratingValue, String instructorID) {

        Ratings.Query ratingParseQuery = new Ratings.Query();
        ratingParseQuery.getAllRatings().whereEqualTo("user", detailedWorkshop.getTeacher());

        ratingParseQuery.findInBackground(new FindCallback<Ratings>() {

            @Override
            public void done(List<Ratings> objects, ParseException e) {
                if (e == null) {

                    //todo - add additional check here to make sure user cannot rate multiple times
                    Ratings currentRating = objects.get(0);
                    int currentNumberOfRatings = currentRating.getNumRatings();
                    int currentSumOfRatings = currentRating.getSumRatings();

                    HashMap<String, Integer> usersWhoRated = (HashMap<String, Integer>) currentRating.get("userRatings");

                   /*
                    if(usersWhoRated.size() > 0){
                        if(usersWhoRated.get(instructorID)>=0){
                            // first case - user has rated before and is looking to modify their rating - change their rating
                            // numRatings should NOT be incremented
                        } else {
                            // user has NOT rated this instructor before - add userId to instructorRatings

                        }



                    }
                    */

                    if (usersWhoRated.get(instructorID) != null) {

                        int formerRating = usersWhoRated.get(instructorID);
                        usersWhoRated.put(ParseUser.getCurrentUser().getUsername(), (int) ratingValue);
                        currentRating.put("userRatings", usersWhoRated);

                        currentRating.setSumRatings(currentSumOfRatings - formerRating + (int) ratingValue);

                        if(currentRating.getNumRatings()==0) {
                            currentRating.setNumRatings(1);
                        }

                        if(currentRating.getNumRatings()>0) {
                            int avgRating = currentRating.getSumRatings() / currentRating.getNumRatings();
                            currentRating.setAverageRating(avgRating);
                            currentRatingAverage = (float) avgRating;
                        } else {
                            currentRatingAverage = 0;
                        }

                        rbInstructorAverage.setRating(currentRatingAverage);

                        if (currentNumberOfRatings == 1) {

                            tvNumRatings.setText(detailedWorkshop.getTeacher().get("firstName") + " has been rated by one user.");

                        } else {
                            tvNumRatings.setText(detailedWorkshop.getTeacher().get("firstName") + " has been rated by " + currentNumberOfRatings + " users.");
                        }

                    } else {

                        usersWhoRated.put(ParseUser.getCurrentUser().getUsername(), (int) ratingValue);
                        currentRating.put("userRatings", usersWhoRated);

                        currentRating.setNumRatings(currentNumberOfRatings + 1);
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
                    e.printStackTrace();
                }
            }
        });

    }

    private boolean checkIfRated(String userID) { //    todo - see if necessary then delete if not

        final Boolean[] answer = {false};

        Ratings.Query ratingParseQuery = new Ratings.Query();
        ratingParseQuery.getAllRatings().whereEqualTo("user", detailedWorkshop.getTeacher());

        ratingParseQuery.findInBackground(new FindCallback<Ratings>() {

            @Override
            public void done(List<Ratings> objects, ParseException e) {
                if (e == null) {

                    Ratings currentRating = objects.get(0);
                    HashMap<String, Integer> usersWhoRated = (HashMap<String, Integer>) currentRating.get("userRatings");

                    if (usersWhoRated.get(userID) != null) {
                        answer[0] = true;
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });

        return answer[0];
    }

    private void setNumFollowers() {

        tvNumberOfFollowers = findViewById(R.id.numberOfFollowers);

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

                    if (numberOfFollowers == 1) {
                        tvNumberOfFollowers.setText("1 follower");

                    } else {
                        tvNumberOfFollowers.setText(numberOfFollowers + " followers");
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });

    }

}
