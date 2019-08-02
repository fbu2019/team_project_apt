package com.example.skillshop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.skillshop.LoginActivities.LoginActivity;
import com.facebook.login.LoginManager;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;

public class UserSettings extends AppCompatActivity {

    TextView tvLocationMessage;
    TextView tvCurrentLocation;
    TextView tvLocationCoordinatesMessage;
    TextView tvCurrentLocationCoordinates;
    TextView tvPreferencesMessage;
    TextView tvCurrentPreferences;
    TextView tvNumberRatingsMessage;
    TextView tvCurrentNumberRatings;
    ImageView ivProfileImage;
    Button btnLogout;
    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        ParseUser user = ParseUser.getCurrentUser();

        tvLocationMessage = findViewById(R.id.currentLocationMessage);
        tvCurrentLocation = findViewById(R.id.currentLocation);
        tvCurrentLocation.setText(user.get("locationName").toString());

        tvLocationCoordinatesMessage = findViewById(R.id.currentLocationCoordinateMessage);
        tvCurrentLocationCoordinates = findViewById(R.id.locationCoordinates);
        ParseGeoPoint markerGP = (ParseGeoPoint) user.get("userLocation");
        double lat = markerGP.getLatitude();
        double lng = markerGP.getLongitude();
        String strLat = String.valueOf((lat));
        String strLng = String.valueOf(lng);
        tvCurrentLocationCoordinates.setText("Lat: "+strLat+" Lng: "+strLng);

        initRatingNumber(user);
        initPreferences(user);
        initProfileImage(user);

        btnLogout = findViewById(R.id.logoutButton);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut(); //  logs out ParseUser
                LoginManager.getInstance().logOut();    //  logs out Facebook user
                Intent i = new Intent(UserSettings.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btnDelete = findViewById(R.id.deleteAccountButton);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserSettings.this, DeleteAccountActivity.class);
                startActivity(i);
            }
        });
    }


    private void initPreferences(ParseUser user){

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

    }

    private void initProfileImage(ParseUser user){

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

    private void initRatingNumber(ParseUser user) {

        tvNumberRatingsMessage = findViewById(R.id.usersWhoRatedMessage);
        tvCurrentNumberRatings = findViewById(R.id.currentNumberRatings);
        //  tvCurrentNumberRatings.setText(user.get); //todo - new parse query

    }

}
