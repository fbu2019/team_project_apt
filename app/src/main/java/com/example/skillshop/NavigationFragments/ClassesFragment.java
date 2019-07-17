package com.example.skillshop.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.example.skillshop.Models.ClassesListFragments.ClassesTakingFragment;
import com.example.skillshop.Models.ClassesListFragments.ClassesTeachingFragment;
import com.example.skillshop.R;


public class ClassesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_classes),container,false);
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // define manager to decide which fragment to display
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        BottomNavigationView topNavigationBar = view.findViewById(R.id.bottom_navigation);

        topNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = new HomeFragment();
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



}
