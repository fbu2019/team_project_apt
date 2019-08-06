package com.example.skillshop.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.NavigationFragments.Calendar.CalendarDayViewFragment;
import com.example.skillshop.NavigationFragments.Calendar.CalendarFragment;
import com.example.skillshop.NavigationFragments.Calendar.DayFragmentHandler;
import com.example.skillshop.NavigationFragments.Compose.ComposeFragment;
import com.example.skillshop.NavigationFragments.Home.AllCategoryFragment;
import com.example.skillshop.NavigationFragments.Home.CategoryChooseFragment;
import com.example.skillshop.NavigationFragments.Maps.MapFragment;
import com.example.skillshop.NavigationFragments.Profile.UserProfileFragment;
import com.example.skillshop.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class FragmentHandler extends AppCompatActivity {
    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_handler);

        fragmentManager = getSupportFragmentManager();

        // define manager to decide which fragment to display
        bottomNavigationView = findViewById(R.id.top_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new AllCategoryFragment();

                // depending on which button is pressed launch the corresponding fragment
                switch (item.getItemId()) {
                    case R.id.home_fragment:
                        fragment = new CategoryChooseFragment();
                        break;
                    case R.id.calendar_fragment:
                        fragment = new Fragment();
                        populateClassesTaking();
                        break;
                    case R.id.compose_fragment:
                        fragment = new ComposeFragment();
                        break;
                    case R.id.maps_fragment:
                        fragment = new MapFragment();
                        break;
                    case R.id.user_profile_fragment:
                        fragment = new UserProfileFragment();

                        break;
                    default: break;
                }
                // switch to selected fragment
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // default fragment in home fragment
        bottomNavigationView.setSelectedItemId(R.id.home_fragment);


    }

    @Override
    public void onBackPressed() {

        if(!(getSupportFragmentManager().findFragmentById(R.id.flContainer) instanceof CategoryChooseFragment))
        {
            // create new fragment to use
            Fragment home = new CategoryChooseFragment();
            // transaction on current activity
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.setCustomAnimations(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);

            transaction.replace(R.id.flContainer, home);
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();
        }


    }


    public void populateClassesTaking() {
        Query parseQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        parseQuery.getAllClasses().withItems().byTimeOfClass();

        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {
                    startDay((ArrayList<Workshop>) objects);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startDay(ArrayList<Workshop> workshops)
    {
        Intent eventsToday = new Intent(this, DayFragmentHandler.class);
        eventsToday.putExtra("workshops", workshops);

        Bundle bundle = eventsToday.getExtras();

        Fragment fragment = new CalendarDayViewFragment();
        fragment.setArguments(bundle);

        // transaction on current activity
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContainer, fragment);

        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();



    }


}
