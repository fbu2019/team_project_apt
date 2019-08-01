package com.example.skillshop.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.provider.MediaStore;
=======
>>>>>>> 13b774e9f5db9d9413b00c08532ec21b27b5ce0d
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
<<<<<<< HEAD
import com.example.skillshop.LoginActivity;
import com.example.skillshop.R;
import com.example.skillshop.SignupActivity;
=======
import com.example.skillshop.AddUserPreferences;
import com.example.skillshop.DeleteAccountActivity;
import com.example.skillshop.LoginActivities.LoginActivity;
import com.example.skillshop.Models.Ratings;
import com.example.skillshop.R;
import com.facebook.login.LoginManager;
>>>>>>> 13b774e9f5db9d9413b00c08532ec21b27b5ce0d
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
<<<<<<< HEAD
=======
import com.parse.FindCallback;
>>>>>>> 13b774e9f5db9d9413b00c08532ec21b27b5ce0d
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public final static int AUTOCOMPLETE_REQUEST_CODE = 42;
    private final String apiKey = "AIzaSyARv5bJ1b1bnym8eUwPZlGm_7HN__WsbFE";

<<<<<<< HEAD
    TextView nameViewText;
    ImageView ivProfilePic;
    Button submitNewLocationButton;
=======
    TextView tvUserName;
    TextView tvRatingMessage;
    ImageView ivProfilePic;
    Button submitNewLocationButton;
    Button addPreferencesButton;
>>>>>>> 13b774e9f5db9d9413b00c08532ec21b27b5ce0d
    Button logoutButton;
    Button deleteAccountButton;
    RatingBar rbUserRating;

    ParseGeoPoint location;
    String locationName;
<<<<<<< HEAD

=======
>>>>>>> 13b774e9f5db9d9413b00c08532ec21b27b5ce0d

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_profile), container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

<<<<<<< HEAD
        final ParseUser user = ParseUser.getCurrentUser();
        final String locationName = (user.getString("locationName"));
        final String profilePhotoUrl = user.getString("profilePicUrl");

        nameViewText = view.findViewById(R.id.nameView);
        nameViewText.setText("Hello "+user.getString("firstName")+". You are currently located at "+locationName+".");

        ivProfilePic = view.findViewById(R.id.profilePicture);
        if (profilePhotoUrl != null) {
            Log.i("ProfileFragment", profilePhotoUrl);
            Glide.with(getContext()).load(profilePhotoUrl).into(ivProfilePic);
        } else {
            ivProfilePic.setImageBitmap(null);
            Log.i("Profile Frag", "No profile image");
        }

        // Initialize Places.
        if (!Places.isInitialized()){
            Places.initialize(getContext(), apiKey);
        }

        //Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getContext());
        submitNewLocationButton = view.findViewById(R.id.modifyLocationButton);
        submitNewLocationButton.setOnClickListener(new View.OnClickListener(){
=======
        tvUserName = view.findViewById(R.id.nameView);
        tvRatingMessage = view.findViewById(R.id.ratingMessage);
        tvRatingMessage = view.findViewById(R.id.ratingMessage);
        ivProfilePic = view.findViewById(R.id.profilePicture);
        rbUserRating = view.findViewById(R.id.instructorAverage);
        rbUserRating.setIsIndicator(true);
        rbUserRating.setNumStars(5);


        ParseUser user = ParseUser.getCurrentUser();
        if(user!=null) {
            String locationName = (user.getString("locationName")); // todo: reinstate after error solved
            String profilePhotoUrl = user.getString("profilePicUrl");
            displayUserInfo(view, locationName, profilePhotoUrl);
        } else {
            Toast.makeText(getContext(), "user profile is null", Toast.LENGTH_SHORT).show();
        }


        if (!Places.isInitialized()) {
            Places.initialize(getContext(), apiKey); // Initializes places
        }
>>>>>>> 13b774e9f5db9d9413b00c08532ec21b27b5ce0d

        submitNewLocationButton = view.findViewById(R.id.modifyLocationButton);
        submitNewLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
<<<<<<< HEAD
                    launchIntent();
=======
                launchIntent();
            }
        });

        addPreferencesButton = view.findViewById(R.id.addPreferences);
        addPreferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPreferences();
>>>>>>> 13b774e9f5db9d9413b00c08532ec21b27b5ce0d
            }
        });

        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

<<<<<<< HEAD
    private void launchIntent() {
        Log.i(TAG, "placelookuplaunched");
        // Specify the types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

=======
        deleteAccountButton = view.findViewById(R.id.deleteAccountBUTTON);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
    }

    private void createPreferences() {
        Intent i = new Intent(getContext(), AddUserPreferences.class);
        startActivity(i);
    }

    private void displayUserInfo(View view, String locationName, String profilePhotoUrl) {

        ParseUser user = ParseUser.getCurrentUser();
        if (locationName != null && user.getString("firstName") != null) {
            tvUserName.setText("Hello " + user.getString("firstName") + ". You are currently located at " + locationName + ".");
        }

        if (profilePhotoUrl != null) {
            Glide.with(getContext()).load(profilePhotoUrl).into(ivProfilePic);
            Log.i("ProfileFragment", profilePhotoUrl);
        } else {
            ivProfilePic.setImageBitmap(null);
            Log.i("Profile Frag", "No profile image");
        }


        Ratings.Query ratingParseQuery = new Ratings.Query();
        ratingParseQuery.getAllRatings().whereEqualTo("user", ParseUser.getCurrentUser());


        ratingParseQuery.findInBackground(new FindCallback<Ratings>() {
            @Override
            public void done(List<Ratings> objects, ParseException e) {

                if (e == null) {

                    if(objects != null && objects.size()>0) {
                        Ratings userRating = objects.get(0);

                        if (userRating.getNumRatings() == 0) {
                            tvRatingMessage.setText("You have not been rated as an instructor.");
                        } else if (userRating.getNumRatings() == 1) {
                            tvRatingMessage.setText("You have been rated 1 time");
                            rbUserRating.setRating((int) userRating.getAverageRating());
                        } else {
                            tvRatingMessage.setText("You have been rated " + userRating.getAverageRating() + "times.");
                            rbUserRating.setRating((int) userRating.getAverageRating());
                        }

                    }

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void logout() {
        ParseUser.logOut(); //  logs out ParseUser
        LoginManager.getInstance().logOut();    //  logs out Facebook user
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
    }

    private void launchIntent() {
        Log.i(TAG, "placelookuplaunched");
        // Specify the types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

>>>>>>> 13b774e9f5db9d9413b00c08532ec21b27b5ce0d
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

<<<<<<< HEAD
        if ((data != null) && (requestCode == AUTOCOMPLETE_REQUEST_CODE)){

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
                    if(e != null){
=======
        if ((data != null) && (requestCode == AUTOCOMPLETE_REQUEST_CODE)) {

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
>>>>>>> 13b774e9f5db9d9413b00c08532ec21b27b5ce0d
                        Log.d(TAG, "ERROR WHILE SAVING");
                        e.printStackTrace();
                        return;
                    }
                    Log.e(TAG, "Successfully changed location");
<<<<<<< HEAD
                    nameViewText.setText("You are located at "+locationName);
                }
            });
        }
=======
                    tvUserName.setText("You are now located at " + locationName);
                }
            });
        }
    }

    private void deleteAccount() {

        Intent i = new Intent(getContext(), DeleteAccountActivity.class);
        startActivity(i);

>>>>>>> 13b774e9f5db9d9413b00c08532ec21b27b5ce0d
    }
}