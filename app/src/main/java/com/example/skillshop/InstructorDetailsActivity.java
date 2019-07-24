package com.example.skillshop;

import org.parceler.Parcels;

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
import com.example.skillshop.Models.Workshop;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class InstructorDetailsActivity extends AppCompatActivity {

    private Workshop detailedWorkshop;
    private ImageView ivInstructorProfile;
    private TextView tvInstructorName;
    private TextView tvNotYetRated;
    private TextView tvNumRatings;
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

    private void loadProfilePicture(){

        ivInstructorProfile = findViewById(R.id.instructorProfile);
        profilePhotoUrl = detailedWorkshop.getTeacher().getString("profilePicUrl");

        if (profilePhotoUrl != null) {
            Glide.with(InstructorDetailsActivity.this).load(profilePhotoUrl).into(ivInstructorProfile);
        } else {
            ivInstructorProfile.setImageBitmap(null);
            Log.i("Instructor Details", "No profile image");
        }

    }

    private void initRatingBar(){

        //  rbUserRating = findViewById(R.id)
        rbInstructorAverage = findViewById(R.id.instructorAverage);
        rbInstructorAverage.setNumStars(5);

        tvNumRatings = findViewById(R.id.numRatings);
        tvNotYetRated = findViewById(R.id.notRated);


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

        rbInstructorAverage.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                updateRating();

            }
        });
    }

    private void updateRating() {

        int numTimesRated = (int) detailedWorkshop.getTeacher().get("numRatings");
        detailedWorkshop.getTeacher().put("numRatings", (int) numTimesRated+1);

        detailedWorkshop.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    Toast.makeText(InstructorDetailsActivity.this, "Changes have been saved (changes may take a while to be reflected in the app)", Toast.LENGTH_SHORT).show();
                    Workshop editedWorkshop = detailedWorkshop;
                    refreshDetailsPage(editedWorkshop);
                    finish();
                }
                else
                {
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

}
