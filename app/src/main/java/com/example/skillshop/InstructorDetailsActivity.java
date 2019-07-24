package com.example.skillshop;

import org.parceler.Parcels;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.skillshop.Models.Workshop;

public class InstructorDetailsActivity extends AppCompatActivity {

    private Workshop detailedWorkshop;
    private ImageView ivInstructorProfile;
    private TextView tvInstructorName;
    private TextView tvNotYetRated;
    private TextView tvNumRatings;
    private RatingBar rbInstructorAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_details);

        detailedWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));

        tvInstructorName = findViewById(R.id.instructorName);
        tvInstructorName.setText(detailedWorkshop.getTeacher().getString("firstName")+" "+detailedWorkshop.getTeacher().getString("lastName"));

        ivInstructorProfile = findViewById(R.id.instructorProfile);
        String profilePhotoUrl = detailedWorkshop.getTeacher().getString("profilePicUrl");
        Log.e("Instructor Details", profilePhotoUrl);

        if (profilePhotoUrl != null) {
            Glide.with(InstructorDetailsActivity.this).load(profilePhotoUrl).into(ivInstructorProfile);
        } else {
            ivInstructorProfile.setImageBitmap(null);
            Log.i("Instructor Details", "No profile image");
        }

        rbInstructorAverage = findViewById(R.id.instructorAverage);

        if(detailedWorkshop.getTeacher().get("instructorRating")!=null){
            rbInstructorAverage.setNumStars(5);
            rbInstructorAverage.setRating((int)detailedWorkshop.getTeacher().get("instructorRating"));
        } else {
            rbInstructorAverage.setEnabled(false);
            tvNotYetRated = findViewById(R.id.notRated);
            tvNotYetRated.setText("This instructor has not been rated");
        }
    }
}
