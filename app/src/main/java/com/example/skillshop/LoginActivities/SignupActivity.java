package com.example.skillshop.LoginActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.skillshop.NavigationFragments.FragmentHandler;
import com.example.skillshop.R;
import com.facebook.Profile;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.iid.FirebaseInstanceId;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";

    private String apiKey;
    public final static int AUTOCOMPLETE_REQUEST_CODE = 42;

    TextView signupMessage;
    TextView userLocation;
    Button launchMapButton;

    ParseGeoPoint location;
    String locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiKey = this.getResources().getString(R.string.places_api_key);
        setContentView(R.layout.activity_signup);

        Profile profile = Profile.getCurrentProfile();

        launchMapButton = findViewById(R.id.launchMap);
        signupMessage = findViewById(R.id.signUpMessage);
        userLocation = findViewById(R.id.userLocation);
        userLocation.setText("Hello " + profile.getFirstName() + ". Please add your current location.");

        // Initialize Places.
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        //Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(SignupActivity.this);

        launchMapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                launchIntent();
            }
        });
    }

    private void login(String username, String password) {

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("SignUpActivity", "Login successful");
                    final Intent intent = new Intent(SignupActivity.this, FragmentHandler.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("SignUpActivity", "Login failure");
                    e.printStackTrace();
                    finish();
                }
            }
        });
    }

    private void launchIntent() {
        Log.i(TAG, "placelookuplaunched");
        // Specify the types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((data != null) && (requestCode == AUTOCOMPLETE_REQUEST_CODE)) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            locationName = place.getName();
            LatLng latLng = place.getLatLng();
            location = new ParseGeoPoint(latLng.latitude, latLng.longitude);
            userLocation.setText("You are located at " + locationName);

            ParseUser user = new ParseUser();
            Profile profile = Profile.getCurrentProfile();

            String firstName = profile.getFirstName();
            String lastName = profile.getLastName();
            String fbID = profile.getId();
            ArrayList<String> friends = new ArrayList<>();
            final String username = fbID;
            final String password = fbID;

            user.setUsername(fbID);
            user.setPassword(fbID);
            user.put("userLocation", location);
            user.put("locationName", locationName);
            user.put("firstName", firstName);
            user.put("lastName", lastName);
            user.put("firebaseToken", FirebaseInstanceId.getInstance().getToken());
            user.put("numRatings", 0);
            user.put("sumRatings", 0);
            user.put("sumRatings", 0);
            user.put("instructorRating", 0);
            user.put("friends", friends);
            
            String image_url = "https://graph.facebook.com/" + fbID + "/picture?type=large";
            user.put("profilePicUrl", image_url);

            login(fbID, fbID);

            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        login(username, password);
                    } else {
                        Log.d("SignUpActivity", "Sign up failed");
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}


