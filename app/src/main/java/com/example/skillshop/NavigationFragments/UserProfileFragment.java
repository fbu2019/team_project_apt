package com.example.skillshop.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.skillshop.AddUserPreferences;
import com.example.skillshop.DeleteAccountActivity;
import com.example.skillshop.LoginActivities.LoginActivity;
import com.example.skillshop.Models.Ratings;
import com.example.skillshop.NavigationFragments.ClassesActivities.ClassesTakingFragment;
import com.example.skillshop.NavigationFragments.ClassesActivities.ClassesTeachingFragment;
import com.example.skillshop.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends Fragment {
    public static final String TAG = "UserProfileFragment";
    public final static int AUTOCOMPLETE_REQUEST_CODE = 42;
    private final String apiKey = "AIzaSyARv5bJ1b1bnym8eUwPZlGm_7HN__WsbFE";

    TextView tvUserName;
    TextView tvRatingMessage;
    ImageView ivProfilePic;
    Button submitNewLocationButton;
    Button addPreferencesButton;
    Button mySkillsButton;
    Button logoutButton;
    Button deleteAccountButton;
    RatingBar rbUserRating;

    ParseGeoPoint location;
    String locationName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_user_profile), container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        tvUserName = view.findViewById(R.id.nameView);
        tvRatingMessage = view.findViewById(R.id.ratingMessage);
        tvRatingMessage = view.findViewById(R.id.ratingMessage);
        ivProfilePic = view.findViewById(R.id.profilePicture);
        rbUserRating = view.findViewById(R.id.instructorAverage);
        mySkillsButton = view.findViewById(R.id.btnMySkills);
        rbUserRating.setIsIndicator(true);
        rbUserRating.setNumStars(5);


        ParseUser user = ParseUser.getCurrentUser();

        if(user!=null) {
            String locationName = (user.getString("locationName"));
            String profilePhotoUrl = user.getString("profilePicUrl");

            displayUserInfo(view, locationName, profilePhotoUrl);

            if (!Places.isInitialized()) {
                Places.initialize(getContext(), apiKey); // Initializes places
            }
        }

        submitNewLocationButton = view.findViewById(R.id.modifyLocationButton);
        submitNewLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIntent();
            }
        });

        addPreferencesButton = view.findViewById(R.id.addPreferences);
        addPreferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPreferences();
            }
        });

        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        deleteAccountButton = view.findViewById(R.id.deleteAccount);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        mySkillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //      Intent startMySkillsActivity = new Intent(Use)
            }
        });

        setupFragments(view);






    }

    private void setupFragments(View view) {
        // define manager to decide which fragment to display
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        BottomNavigationView topNavigationBar = view.findViewById(R.id.bottom_navigation);
        topNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new HomeFragment();
                // depending on which button the user presses the classes will be displayed
                switch (item.getItemId()) {
                    case R.id.taking:
                        fragment = new ClassesTakingFragment();
                        break;
                    case R.id.teaching:
                        fragment = new ClassesTeachingFragment();
                        break;
                    default: break;
                }
                // switch to selected fragment
                fragmentManager.beginTransaction().replace(R.id.classes_take_teach, fragment).commit();
                return true;
            }
        });
        // default fragment in home fragment
        topNavigationBar.setSelectedItemId(R.id.taking);
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
                    if (objects != null && objects.size()>0){

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

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(getContext());
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
                    tvUserName.setText("You are now located at " + locationName);
                }
            });
        }
    }

    private void deleteAccount() {

        Intent i = new Intent(getContext(), DeleteAccountActivity.class);
        startActivity(i);
    }
}
