package com.example.skillshop.NavigationFragments.Profile.Settings;

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
import com.example.skillshop.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserSettings extends AppCompatActivity {

    public static final String TAG = "UserSettings";
    public final static int AUTOCOMPLETE_REQUEST_CODE = 42;
    private final String apiKey = "AIzaSyARv5bJ1b1bnym8eUwPZlGm_7HN__WsbFE";
    private ParseGeoPoint location;
    private String locationName;

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

        tvCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIntent();
            }
        });

        tvCurrentLocationCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIntent();
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
        tvCurrentPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserSettings.this, AddUserPreferences.class);
                startActivity(i);
            }
        });

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


    private void launchIntent() {
        Log.i(TAG, "placelookuplaunched");
        // Specify the types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(UserSettings.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((data != null) && (requestCode == AUTOCOMPLETE_REQUEST_CODE) && (resultCode == RESULT_OK)) {

            Place place = Autocomplete.getPlaceFromIntent(data);
            locationName = place.getName();
            LatLng latLng = place.getLatLng();
            location = new ParseGeoPoint(latLng.latitude, latLng.longitude);

            ParseUser user = ParseUser.getCurrentUser();
            user.put("userLocation", location);
            user.put("locationName", locationName);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.d(TAG, "ERROR WHILE SAVING");
                        e.printStackTrace();
                        return;
                    }
                    Log.e(TAG, "Successfully changed location");
                    tvCurrentLocation.setText(locationName);
                    String strLat = String.valueOf(latLng.latitude);
                    String strLng = String.valueOf(latLng.longitude);
                    tvCurrentLocationCoordinates.setText("Lat: "+strLat+" Lng: "+strLng);
                }
            });
        }
    }

}
