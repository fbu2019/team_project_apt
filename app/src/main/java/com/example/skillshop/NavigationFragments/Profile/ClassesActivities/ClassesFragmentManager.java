package com.example.skillshop.NavigationFragments.Profile.ClassesActivities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.example.skillshop.NavigationFragments.Home.AllCategoryFragment;
import com.example.skillshop.R;


public class ClassesFragmentManager extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_classes),container,false);
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                        break;

                    case R.id.teaching:
                        fragment = new ClassesInvolvedFragment();
                        bundle.putBoolean("taking", false);
                        break;
                    default: break;
                }
                fragment.setArguments(bundle);
                // switch to selected fragment
                fragmentManager.beginTransaction().replace(R.id.classes_today, fragment).commit();
                return true;
            }
        });
        // default fragment in home fragment
        topNavigationBar.setSelectedItemId(R.id.taking);

    }

}
