package com.example.skillshop.NavigationFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.skillshop.NavigationFragments.CalendarActivities.CalendarFragment;
import com.example.skillshop.NavigationFragments.ClassesActivities.ClassesFragmentManager;
import com.example.skillshop.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class FragmentHandler extends AppCompatActivity {
    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_handler);

        fragmentManager = getSupportFragmentManager();

        // define manager to decide which fragment to display
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new HomeFragment();

                // depending on which button is pressed launch the corresponding fragment
                switch (item.getItemId()) {
                    case R.id.home_fragment:
                        fragment = new HomeFragment();
                        break;
                    case R.id.calendar_fragment:
                        fragment = new CalendarFragment();
                        break;

                    case R.id.classes_fragment:
                        fragment = new ClassesFragmentManager();
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
//        updateFirebaseToken();



    }

    @Override
    public void onBackPressed() {

        // allows the back button to always take user to the home fragment
        Fragment fragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        bottomNavigationView.setSelectedItemId(R.id.home_fragment);
    }


    public void updateFirebaseToken()
    {
        ParseUser currentUser = ParseUser.getCurrentUser();


        currentUser.put("firebaseToken",FirebaseInstanceId.getInstance().getToken());
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });

    }


}
