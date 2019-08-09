package com.example.skillshop.NavigationFragments.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.skillshop.FollowingListActivity;
import com.example.skillshop.LoginActivities.LoginActivity;
import com.example.skillshop.Models.Ratings;
import com.example.skillshop.Models.User;
import com.example.skillshop.NavigationFragments.Home.AllCategoryFragment;
import com.example.skillshop.NavigationFragments.Profile.ClassesActivities.ClassesInvolvedFragment;
import com.example.skillshop.R;
import com.example.skillshop.NavigationFragments.Profile.ClassesActivities.SkillVisualizationActivity;
import com.example.skillshop.NavigationFragments.Profile.Settings.UserSettings;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends Fragment {
    public static final String TAG = "UserProfileFragment";
    public final static int AUTOCOMPLETE_REQUEST_CODE = 42;
    private final String apiKey = "AIzaSyARv5bJ1b1bnym8eUwPZlGm_7HN__WsbFE";

    private TextView tvUserName;
    private TextView tvRatingMessage;
    private TextView tvNumberOfFollowers;
    private TextView tvNumberFollowing;
    private TextView tvSkillsAnalysis;
    private ImageView ivProfilePic;
    private ImageView ivUserSettings;
    private RatingBar rbUserRating;

    private ParseGeoPoint location;
    private String locationName;
    private int numberOfFollowers = 0;
    public boolean firstLoad;
    public DrawerLayout drawer;

    public NavigationView nvSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_user_profile), container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        tvUserName = view.findViewById(R.id.tvUsername);
        tvNumberOfFollowers = view.findViewById(R.id.numberOfFollowers);
        tvSkillsAnalysis = view.findViewById(R.id.tvSkillAnalysis);
        setNumFollowers(); //   sets view within method
        tvNumberFollowing = view.findViewById(R.id.numberFollowing);
        setNumFollowing();
        drawer = view.findViewById(R.id.drawer);
        ivProfilePic = view.findViewById(R.id.profilePicture);
        ivUserSettings = view.findViewById(R.id.userSettings);
        rbUserRating = view.findViewById(R.id.instructorAverage);
        rbUserRating.setIsIndicator(true);
        rbUserRating.setNumStars(5);

        firstLoad = true;


        setUpDrawer(view);

        ParseUser user = ParseUser.getCurrentUser();

        if (user != null) {
            String locationName = (user.getString("locationName"));
            String profilePhotoUrl = user.getString("profilePicUrl");

            displayUserInfo(view, locationName, profilePhotoUrl);

            if (!Places.isInitialized()) {
                Places.initialize(getContext(), apiKey); // Initializes places
            }
        }

        tvNumberOfFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), UserFollowersActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(ParseUser.getCurrentUser()));
                startActivity(i);
            }
        });

        tvNumberFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), FollowingListActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(ParseUser.getCurrentUser()));
                startActivity(i);
            }
        });

        tvSkillsAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMySkillsActivity = new Intent(getContext(), SkillVisualizationActivity.class);
                startActivity(startMySkillsActivity);
            }
        });

        ivUserSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.RIGHT);

            }
        });

        rbUserRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setupFragments(view);

    }


    public void setUpDrawer(View view)
    {

        nvSettings = view.findViewById(R.id.nvSettings);

        nvSettings.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                switch (menuItem.getItemId())
                {
                    case R.id.general:
                        Intent settings = new Intent(getContext(), UserSettings.class);
                        startActivity(settings);
                        getActivity().finish();
                        break;

                    case R.id.logout:
                        ParseUser.logOut(); //  logs out ParseUser
                        LoginManager.getInstance().logOut();    //  logs out Facebook user
                        Intent logout = new Intent(getContext(), LoginActivity.class);
                        startActivity(logout);
                        break;

                    default:break;
                }



                return true;
            }
        });


    }


    private void setupFragments(View view) {
        // define manager to decide which fragment to display
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        BottomNavigationView topNavigationBar = view.findViewById(R.id.top_navigation);
        topNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new AllCategoryFragment();
                // depending on which button the user presses the classes will be displayed
                Bundle bundle = new Bundle();

                switch (item.getItemId()) {
                    case R.id.taking:
                        fragment = new ClassesInvolvedFragment();
                        bundle.putBoolean("taking", true);
                        fragment.setArguments(bundle);
                        // switch to selected fragment
                        if(firstLoad){
                            fragmentManager.beginTransaction().setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left).replace(R.id.classes_today, fragment).commit();
                            firstLoad = false;
                        }
                        else {
                            fragmentManager.beginTransaction().setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right).replace(R.id.classes_today, fragment).commit();
                        }
                        break;

                    case R.id.teaching:
                        fragment = new ClassesInvolvedFragment();
                        bundle.putBoolean("taking", false);
                        fragment.setArguments(bundle);
                        // switch to selected fragment
                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left).replace(R.id.classes_today, fragment).commit();
                        break;
                    default: break;
                }


                return true;
            }
        });
        // default fragment in home fragment
        topNavigationBar.setSelectedItemId(R.id.taking);
    }

    private void displayUserInfo(View view, String locationName, String profilePhotoUrl) {

        ParseUser user = ParseUser.getCurrentUser();
        if (locationName != null && user.getString("firstName") != null) {
            tvUserName.setText(user.getString("firstName")+" "+user.getString("lastName"));
        }

        if (profilePhotoUrl != null) {
            profilePhotoUrl= (String) ParseUser.getCurrentUser().get("profilePicUrl");
            Glide.with(getContext()).load(profilePhotoUrl).apply(new RequestOptions().circleCrop()).into(ivProfilePic);
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
                    if (objects != null && objects.size() > 0) {

                        Ratings userRating = objects.get(0);

                        if (userRating.getNumRatings() == 0) {

                            rbUserRating.setVisibility(View.INVISIBLE);

                        } else if (userRating.getNumRatings() == 1) {

                            rbUserRating.setRating((int) userRating.getAverageRating());
                        } else {

                            rbUserRating.setRating((int) userRating.getAverageRating());
                        }

                        rbUserRating.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(userRating.getNumRatings() == 0) {

                                    Toast.makeText(getContext(), "You have not been rated as an instructor", Toast.LENGTH_LONG).show();
                                } else if (userRating.getNumRatings() == 1 ){

                                    Toast.makeText(getContext(), "You have been rated 1 time", Toast.LENGTH_LONG).show();
                                } else {

                                    Toast.makeText(getContext(), "You have been rated "+userRating.getAverageRating()+" times.", Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                    }

                } else {
                    e.printStackTrace();
                }
            }
        });
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

    private void setNumFollowers() {

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> allUsers, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < allUsers.size(); i++) {
                        ParseUser userItem = allUsers.get(i);

                        if (!(userItem.getUsername().equals(ParseUser.getCurrentUser().getUsername()))) {

                            ArrayList<String> usersFollowing = (ArrayList<String>) userItem.get("friends");
                            for (int j = 0; j < usersFollowing.size(); j++) {
                                if (usersFollowing.get(j).equals(ParseUser.getCurrentUser().getObjectId())) {
                                    numberOfFollowers++;
                                }
                            }
                        }
                    }

                    if (numberOfFollowers == 1) {
                        tvNumberOfFollowers.setText("1 Follower");

                    } else {
                        tvNumberOfFollowers.setText(numberOfFollowers + " Followers");
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setNumFollowing() {

        ParseUser user = ParseUser.getCurrentUser();
        ArrayList <String> following = (ArrayList<String>) user.get("friends");
        for(int i = 0; i<following.size(); i++){
            if(following.get(i).equals(ParseUser.getCurrentUser().getObjectId())){
                following.remove(i);
            }
        }
        int numberFollowing = following.size();
        tvNumberFollowing.setText(numberFollowing+" Following");

    }
}
