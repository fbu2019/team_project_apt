package com.example.skillshop;

import org.parceler.Parcels;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;

public class InstructorDetailsActivity extends AppCompatActivity {

    private Workshop detailedWorkshop;
    private ImageView ivInstructorProfile;
    private TextView tvInstructorName;
    private TextView tvNotYetRated;
    private TextView tvNumRatings;
    private TextView tvUserProvidedRating;
    private RatingBar rbInstructorAverage;
    private RatingBar rbUserRating;

    private String profilePhotoUrl;
    float currentRatingAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_details);

        detailedWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));
        tvInstructorName = findViewById(R.id.instructorName);
        tvInstructorName.setText(detailedWorkshop.getTeacher().getString("firstName") + " " + detailedWorkshop.getTeacher().getString("lastName"));

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

    private void initRatingBar() {

        rbUserRating = findViewById(R.id.userRating);
        rbInstructorAverage = findViewById(R.id.instructorAverage);
        rbInstructorAverage.setNumStars(5);
        rbInstructorAverage.setIsIndicator(true);

        tvNumRatings = findViewById(R.id.numRatings);
        tvNotYetRated = findViewById(R.id.notRated);
        tvUserProvidedRating = findViewById(R.id.userProvideRating);

        if(ParseUser.getCurrentUser().getUsername().equals(detailedWorkshop.getTeacher().getUsername())){

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
                    initializeAverageRating(detailedWorkshop.getTeacher().getUsername());
                    tvNotYetRated.setText(" ");

            }
        });
    }

    private void initializeAverageRating(String instructorID){

        Ratings.Query ratingParseQuery = new Ratings.Query();
        ratingParseQuery.getAllRatings().whereEqualTo("user", detailedWorkshop.getTeacher());

        ratingParseQuery.findInBackground(new FindCallback<Ratings>() {

            @Override
            public void done(List<Ratings> objects, ParseException e) {
                if (e == null) {

                    Ratings currentRating = objects.get(0);

                    float avgRating = 0;
                    if(currentRating.getNumRatings() >0) {
                        avgRating = currentRating.getSumRatings() / currentRating.getNumRatings();
                    }

                    currentRating.setAverageRating( (int) avgRating);
                    currentRatingAverage = (float) avgRating;

                    int currentNumberOfRatings = currentRating.getNumRatings();

                    if (currentNumberOfRatings == 0) {

                        rbInstructorAverage.setEnabled(false);
                        tvNotYetRated = findViewById(R.id.notRated);
                        tvNotYetRated.setText("This instructor has not been rated.");

                    } else if (currentNumberOfRatings == 1) {

                        rbInstructorAverage.setRating(currentRatingAverage);
                        tvNumRatings.setText(detailedWorkshop.getTeacher().get("firstName") + " has been rated by one user.");

                    } else {

                        rbInstructorAverage.setRating(currentRatingAverage);
                        tvNumRatings.setText(detailedWorkshop.getTeacher().get("firstName") + " has been rated by " + currentNumberOfRatings + " users. Provide your own rating below.");
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

                    Ratings currentRating = objects.get(0);
                    int currentNumberOfRatings = currentRating.getNumRatings();
                    int currentSumOfRatings = currentRating.getSumRatings();

                    HashMap<String, Integer> usersWhoRated = (HashMap<String, Integer>) currentRating.get("userRatings");

                    if (usersWhoRated.get(instructorID) != null ) {

                        int formerRating = usersWhoRated.get(instructorID);
                        usersWhoRated.put(instructorID, (int) ratingValue);
                        currentRating.put("userRatings", usersWhoRated);

                        currentRating.setSumRatings(currentSumOfRatings - formerRating + (int) ratingValue);

                        int avgRating = currentRating.getSumRatings() / currentRating.getNumRatings();
                        currentRating.setAverageRating(avgRating);
                        currentRatingAverage = (float) avgRating;

                    } else {

                        usersWhoRated.put(instructorID, (int) ratingValue);
                        currentRating.put("userRatings", usersWhoRated);

                        currentRating.setNumRatings(currentNumberOfRatings + 1);
                        currentRating.setSumRatings(currentSumOfRatings + (int) ratingValue);

                        int avgRating = currentRating.getSumRatings() / currentRating.getNumRatings();
                        currentRating.setAverageRating(avgRating);
                        currentRatingAverage = (float) avgRating;
                    }


                    currentRating.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if(instructorID != ParseUser.getCurrentUser().getUsername()){
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

    private boolean checkIfRated(String userID){

        final Boolean[] answer = {false};

        Ratings.Query ratingParseQuery = new Ratings.Query();
        ratingParseQuery.getAllRatings().whereEqualTo("user", detailedWorkshop.getTeacher());

        ratingParseQuery.findInBackground(new FindCallback<Ratings>() {

            @Override
            public void done(List<Ratings> objects, ParseException e) {
                if (e == null) {

                    Ratings currentRating = objects.get(0);
                    HashMap<String, Integer> usersWhoRated = (HashMap<String, Integer>) currentRating.get("userRatings");

                    if(usersWhoRated.get(userID)!=null ){
                        answer[0] = true;
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });

        return answer[0];

    }

}
