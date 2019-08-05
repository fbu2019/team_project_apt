package com.example.skillshop;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.skillshop.Adapters.ClassAdapter;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.User;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.NavigationFragments.ClassesActivities.ClassesInvolvedFragment;
import com.example.skillshop.NavigationFragments.HomeFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private RecyclerView rvClasses;
    protected ArrayList<Workshop> mWorkshops;
    protected ClassAdapter classAdapter;

    private  ParseUser currentUser;
    private String profilePhotoUrl;
    private TextView tvNameView;
    private ImageView ivProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        currentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        profilePhotoUrl = currentUser.getString("profilePicUrl");

        tvNameView = findViewById(R.id.nameView);
        tvNameView.setText(currentUser.get("firstName")+" "+currentUser.get("lastName"));
        ivProfileImage = findViewById(R.id.profileImage);
        loadProfilePicture();

        connectRecyclerView();
        populateHomeFeed();

    }


    private void loadProfilePicture() {

        Log.i("UserProfileACTIVITY", profilePhotoUrl);

        if (profilePhotoUrl != null) {
            Glide.with(UserProfileActivity.this).load(profilePhotoUrl).apply(new RequestOptions().circleCrop()).into(ivProfileImage);
        } else {
            ivProfileImage.setImageBitmap(null);
            Log.i("Instructor Details", "No profile image");
        }
    }



    private void connectRecyclerView() {

        Log.i("UseProfileActivity", "reached connect method ");
        //find the RecyclerView
        rvClasses = (RecyclerView) findViewById(R.id.rvClasses);
        //init the arraylist (data source)
        mWorkshops = new ArrayList<>();
        //construct the adapter from this datasource
        classAdapter = new ClassAdapter(mWorkshops, UserProfileActivity.this);
        //RecyclerView setup (layout manager, use adapter)
        rvClasses.setLayoutManager(new LinearLayoutManager(UserProfileActivity.this));
        //set the adapter
        rvClasses.setAdapter(classAdapter);

        // add dividers on posts
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvClasses.getContext(),
                new LinearLayoutManager(UserProfileActivity.this).getOrientation());
        rvClasses.addItemDecoration(dividerItemDecoration);
    }

    public void populateHomeFeed() {

        mWorkshops.clear();
        classAdapter.notifyDataSetChanged();

        ArrayList<String> classes = (ArrayList<String>) ParseUser.getCurrentUser().get("classesTaking");
        Query userClassesQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        userClassesQuery.getAllClasses().withItems();

        userClassesQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        if(workshopItem.getTeacher().getUsername().equals(currentUser.getUsername())) {
                            mWorkshops.add(workshopItem);
                            classAdapter.notifyItemInserted(mWorkshops.size() - 1);
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


}
