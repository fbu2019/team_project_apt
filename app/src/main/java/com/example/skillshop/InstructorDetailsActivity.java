package com.example.skillshop;

import org.parceler.Parcels;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.skillshop.Models.Workshop;

public class InstructorDetailsActivity extends AppCompatActivity {

    private Workshop detailedWorkshop;
    private ImageView ivInstructorProfile;
    private TextView tvInstructorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_details);

        Log.e("InstructorDetails", "Starting Activity");

        detailedWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));

        tvInstructorName = findViewById(R.id.instructorName);
        tvInstructorName.setText(detailedWorkshop.getTeacher().getString("firstName")+" "+detailedWorkshop.getTeacher().getString("lastName"));

        ivInstructorProfile = findViewById(R.id.instructorProfile);
        String profilePhotoUrl = detailedWorkshop.getTeacher().getString("profilePicUrl");
        Log.e("Instructor Details", profilePhotoUrl);

        if (profilePhotoUrl != null) {
            Log.e("Instructor Details 2", profilePhotoUrl);
            Glide.with(InstructorDetailsActivity.this).load(profilePhotoUrl).into(ivInstructorProfile);
            Log.i("ProfileFragment", profilePhotoUrl);
        } else {
            ivInstructorProfile.setImageBitmap(null);
            Log.i("Profile Frag", "No profile image");
        }

    }
}
