package com.example.skillshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseUser;

public class UserSettings extends AppCompatActivity {

    ImageView ivProfileImage;
    Button btnModifyLocation;
    Button btnLogout;
    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);


        ivProfileImage = findViewById(R.id.ivUserProfileImage);
        ParseUser user = ParseUser.getCurrentUser();
        String profilePhotoUrl = user.getString("profilePicUrl");
        if (profilePhotoUrl != null) {
            Glide.with(this).load(profilePhotoUrl).apply(new RequestOptions().circleCrop()).into(ivProfileImage);
            Log.i("ProfileFragment", profilePhotoUrl);
        } else {
            ivProfileImage.setImageBitmap(null);
            Log.i("Profile Frag", "No profile image");
        }
    }
}
