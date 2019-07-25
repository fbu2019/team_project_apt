package com.example.skillshop;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.skillshop.ClassManipulationActivities.EditClassActivity;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Ratings;
import com.example.skillshop.Models.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

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
    private RatingBar rbInstructorAverage;
    private RatingBar rbUserRating;

    private String profilePhotoUrl;

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

        if ( true ){
            //TODO - DETERMINE RELATIONSHIP BETWEEN USER AND IF THEY'VE RATD A USER
            tvUserProvidedRating.setText("You have not yet rated this instructor");
        }
        rbUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                updateRating(rating);
                tvUserProvidedRating.setText("You have provided "+detailedWorkshop.getTeacher().get("firstName")+" with a rating of "+rbUserRating.getRating());
            }
        });
    }

    private void updateRating(float ratingValue) {

        int numTimesRated = (int) detailedWorkshop.getTeacher().get("numRatings") + 1;

       Ratings instructorRating = (Ratings) detailedWorkshop.get("instructorRating");
       if(instructorRating!=null) {
           Log.e("Instructor details", String.valueOf((instructorRating.getNumRatings())));
       }

       /*
        HashMap<String, Integer> usersWhoRated = (HashMap<String, Integer>) instructorRating.get("userRatings");
        if(usersWhoRated.get(detailedWorkshop.getTeacher().getUsername())!=null){
            usersWhoRated.put(detailedWorkshop.getTeacher().getUsername(), (int) ratingValue);
            //TODO - update message
        } else {
            usersWhoRated.put(detailedWorkshop.getTeacher().getUsername(), (int) ratingValue);
        }
        */
       instructorRating.setNumRatings(33);

        instructorRating.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Toast.makeText(InstructorDetailsActivity.this, "Changes have been saved (changes may take a while to be reflected in the app)", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(InstructorDetailsActivity.this, "Error saving changes", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void refreshDetailsPage(Workshop editedWorkshop) {
        Intent data = new Intent();
        data.putExtra("updated", Parcels.wrap(editedWorkshop));
        setResult(RESULT_OK, data);
    }

    private void getRating() {


    }
}
