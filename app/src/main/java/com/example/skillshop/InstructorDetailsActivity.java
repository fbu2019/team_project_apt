package com.example.skillshop;

import org.parceler.Parcels;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.skillshop.LoginActivities.SignupActivity;
import com.example.skillshop.Models.Ratings;
import com.example.skillshop.Models.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
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

        tvNumRatings = findViewById(R.id.numRatings);
        tvNotYetRated = findViewById(R.id.notRated);
        tvUserProvidedRating = findViewById(R.id.userProvideRating);

        int numTimesRated = (int) detailedWorkshop.getTeacher().get("numRatings");

        if (numTimesRated == 0) {

            rbInstructorAverage.setEnabled(false);
            tvNotYetRated = findViewById(R.id.notRated);
            tvNotYetRated.setText("This instructor has not been rated");

        } else if (numTimesRated == 1) {

            int avg = (int) detailedWorkshop.getTeacher().get("sumRatings") / (int) detailedWorkshop.getTeacher().get("numRatings");
            rbInstructorAverage.setRating((int) detailedWorkshop.getTeacher().get("instructorRating"));
            tvNumRatings.setText(detailedWorkshop.getTeacher().get("firstName") + " has been rated " + numTimesRated + " time.");

        } else {

            rbInstructorAverage.setRating((int) detailedWorkshop.getTeacher().get("instructorRating"));
            tvNumRatings.setText(detailedWorkshop.getTeacher().get("firstName") + " has been rated " + numTimesRated + " times.");
        }


        if (true) {
            //TODO - DETERMINE RELATIONSHIP BETWEEN USER AND IF THEY'VE RATeD A USER
            tvUserProvidedRating.setText("You have not yet rated this instructor");
        }

        rbUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                updateRating(rating, detailedWorkshop.getTeacher().getUsername());
                tvUserProvidedRating.setText("You have provided " + detailedWorkshop.getTeacher().get("firstName") + " with a rating of " + rbUserRating.getRating());
                rbInstructorAverage.setRating(currentRatingAverage);
                tvNotYetRated.setText(" ");
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
                    if( usersWhoRated.get(instructorID) != null){

                        int formerRating = usersWhoRated.get(instructorID);
                        usersWhoRated.put(instructorID, (int) ratingValue);
                        currentRating.put("userRatings", usersWhoRated);

                        currentRating.setSumRatings(currentSumOfRatings - formerRating + (int) ratingValue);

                        int avgRating = currentRating.getSumRatings()/currentRating.getNumRatings();
                        currentRating.setAverageRating(avgRating);
                        currentRatingAverage = (float) avgRating;

                    } else {

                        usersWhoRated.put(instructorID, (int) ratingValue);
                        currentRating.put("userRatings", usersWhoRated);

                        currentRating.setNumRatings(currentNumberOfRatings+1);
                        currentRating.setSumRatings(currentSumOfRatings+ (int) ratingValue);

                        int avgRating = currentRating.getSumRatings()/currentRating.getNumRatings();
                        currentRating.setAverageRating(avgRating);
                        currentRatingAverage = (float) avgRating;
                    }


                    currentRating.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(InstructorDetailsActivity.this, "Your rating has been recorded", Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(InstructorDetailsActivity.this, "Error saving changes", Toast.LENGTH_SHORT).show();
                                Log.e("InstructorActivity", "CHANGES NOT SAVED");
                            }

                        }
                    });
                } else {
                    e.printStackTrace();
                    Log.e("not Nice", "not Nice");
                }
            }
        });

    }


    private void refreshDetailsPage(Workshop editedWorkshop) {
        Intent data = new Intent();
        data.putExtra("updated", Parcels.wrap(editedWorkshop));
        setResult(RESULT_OK, data);
    }


}
