package com.example.skillshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseUser;

import java.util.ArrayList;

public class UserSettings extends AppCompatActivity {

    TextView tvLocationMessage;
    TextView tvCurrentLocation;
    TextView tvPreferencesMessage;
    TextView tvCurrentPreferences;
    TextView tvAccountCreatedMessage;
    TextView tvCurrentAccountCreated;
    ImageView ivProfileImage;
    Button btnModifyLocation;
    Button btnLogout;
    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        ParseUser user = ParseUser.getCurrentUser();

        tvLocationMessage = findViewById(R.id.currentlyLocatedAtMessage);
        tvCurrentLocation = findViewById(R.id.currentLocation);
        tvCurrentLocation.setText(user.get("locationName").toString());

        tvPreferencesMessage = findViewById(R.id.currentPreferencesMessage);
        tvCurrentPreferences = findViewById(R.id.currentPreferences);

        ArrayList<String> preferences = (ArrayList<String>) user.get("preferences");
        String preferenceString = "";
        if (preferences != null) {
            for (int i = 0; i < preferences.size(); i++) {

                    if(i==preferences.size()-1) {
                        preferenceString += preferences.get(i);
                    } else {
                        preferenceString += preferences.get(i) + " | ";
                    }
            }
        } else {
            preferenceString += "No preferences set.";
        }

        tvCurrentPreferences.setText(preferenceString);


//        tvAccountCreatedMessage = findViewById(R.id.accountCreatedMessage);
       // tvCurrentAccountCreated = findViewById(R.id.currentAccountCreated);
       // tvCurrentAccountCreated.setText(user.get("createdAt").toString());

        ivProfileImage = findViewById(R.id.ivUserProfileImage);
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
