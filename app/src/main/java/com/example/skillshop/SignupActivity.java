package com.example.skillshop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Arrays;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";

    private final String apiKey = "AIzaSyARv5bJ1b1bnym8eUwPZlGm_7HN__WsbFE";
    public final static int AUTOCOMPLETE_REQUEST_CODE = 42;

    TextView signupMessage;
    TextView userLocation;
    Button submitButton;
    Button launchMapButton;

    ParseGeoPoint location;
    String locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Profile profile = Profile.getCurrentProfile();

        launchMapButton = findViewById(R.id.launchMap);
        submitButton = findViewById(R.id.submit);
        signupMessage = findViewById(R.id.signUpMessage);
        userLocation = findViewById(R.id.userLocation);
        userLocation.setText("Hello "+profile.getFirstName()+". Please add and submit your current location.");

        // Initialize Places.
        if (!Places.isInitialized()){
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

        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ParseUser user = new ParseUser();
                Profile profile = Profile.getCurrentProfile();

                String firstName = profile.getFirstName();
                String lastName = profile.getLastName();
                String fbID = profile.getId();
                final String username = fbID;
                final String password = fbID;

                    user.setUsername(fbID);
                    user.setPassword(fbID);
                    user.put("userLocation", location);
                    user.put("locationName", locationName);
                    user.put("firstName", firstName);
                    user.put("lastName", lastName);

                    String image_url = "https://graph.facebook.com/"+fbID+"/picture?type=large";
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
                }   else {
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

        if ((data != null) && (requestCode == AUTOCOMPLETE_REQUEST_CODE)){
            Place place = Autocomplete.getPlaceFromIntent(data);
            locationName = place.getName();
            LatLng latLng = place.getLatLng();
            location = new ParseGeoPoint(latLng.latitude, latLng.longitude);
            userLocation.setText("You are located at "+locationName);
        }
    }
}


