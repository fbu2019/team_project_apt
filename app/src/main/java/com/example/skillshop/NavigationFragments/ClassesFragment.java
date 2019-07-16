package com.example.skillshop.NavigationFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.skillshop.ClassAdapter;
import com.example.skillshop.R;

import java.util.ArrayList;

public class ClassesFragment extends Fragment {

    private RecyclerView rvClasses;
    protected ArrayList<Class> mClasses;
    protected ClassAdapter classAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_classes),container,false);
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        connectRecyclerView(view);
    }

    private void connectRecyclerView(View view) {
        //find the RecyclerView
        rvClasses = (RecyclerView) view.findViewById(R.id.rvClasses);
        //init the arraylist (data source)
        mClasses = new ArrayList<>();
        //construct the adapter from this datasource
        classAdapter = new ClassAdapter(mClasses, getContext());
        //RecyclerView setup (layout manager, use adapter)
        rvClasses.setLayoutManager(new LinearLayoutManager(getContext()));
        //set the adapter
        rvClasses.setAdapter(classAdapter);
    }


}
