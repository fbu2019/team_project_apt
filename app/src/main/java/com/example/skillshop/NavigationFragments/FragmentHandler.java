package com.example.skillshop.NavigationFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.skillshop.R;


public class FragmentHandler extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // define manager to decide which fragment to display
        final FragmentManager fragmentManager = getSupportFragmentManager();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = new HomeFragment();
                switch (item.getItemId()) {

                    case R.id.home_fragment:
                        fragment = new HomeFragment();
                        break;

                    case R.id.calendar_fragment:
                        fragment = new CalendarFragment();
                        break;

                    case R.id.profile_fragment:
                        fragment = new ProfileFragment();
                        break;

                    case R.id.classes_fragment:
                        fragment = new ClassesFragment();
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
}
