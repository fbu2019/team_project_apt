package com.example.skillshop.NavigationFragments.CalendarActivities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.skillshop.NavigationFragments.HomeFragment;
import com.example.skillshop.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DayFragmentHandler extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_fragment_handler);



        Long dateLong = getIntent().getLongExtra("Date",0);
        Date date = new Date(dateLong);
        SimpleDateFormat format = new SimpleDateFormat("E MMM dd YYYY");

        TextView tvDate = findViewById(R.id.tvDate);
        tvDate.setText(format.format(date));



        // define manager to decide which fragment to display
        final FragmentManager fragmentManager = getSupportFragmentManager();

        BottomNavigationView topNavigationBar = findViewById(R.id.top_navigation);
        topNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new TodaysClassesFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("explore", false);
                fragment.setArguments(bundle);

                // depending on which button the user presses the classes will be displayed
                switch (item.getItemId()) {
                    case R.id.classesMe:

                        fragment = new TodaysClassesFragment();
                        bundle = new Bundle();
                        bundle.putBoolean("explore", false);
                        bundle.putLong("time", dateLong);
                        fragment.setArguments(bundle);

                        break;
                    case R.id.classesExplore:
                        fragment = new TodaysClassesFragment();
                        bundle = new Bundle();
                        bundle.putBoolean("explore", true);
                        bundle.putLong("time", dateLong);
                        fragment.setArguments(bundle);
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
