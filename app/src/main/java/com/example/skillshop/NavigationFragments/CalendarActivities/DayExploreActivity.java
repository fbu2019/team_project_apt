package com.example.skillshop.NavigationFragments.CalendarActivities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.skillshop.NavigationFragments.HomeFragment;
import com.example.skillshop.R;

public class DayExploreActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_day_classes);

        // define manager to decide which fragment to display
        final FragmentManager fragmentManager = getSupportFragmentManager();

        BottomNavigationView topNavigationBar = findViewById(R.id.top_navigation);
        topNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new HomeFragment();
                // depending on which button the user presses the classes will be displayed
                switch (item.getItemId()) {
                    case R.id.classesMe:
                        break;
                    case R.id.classesExplore:
                        break;
                    default: break;
                }
                // switch to selected fragment
                fragmentManager.beginTransaction().replace(R.id.classes_today, fragment).commit();
                return true;
            }
        });

        // default fragment in home fragment
        topNavigationBar.setSelectedItemId(R.id.classesMe);

    }


}
