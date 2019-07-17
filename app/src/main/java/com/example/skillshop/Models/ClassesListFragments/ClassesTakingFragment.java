package com.example.skillshop.Models.ClassesListFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.skillshop.ClassAdapter;
import com.example.skillshop.Models.Class;
import com.example.skillshop.Models.Query;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class ClassesTakingFragment extends Fragment {

    private RecyclerView rvClasses;
    protected ArrayList<Class> mClasses;
    protected ClassAdapter classAdapter;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_classes_taking), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        getAllClasses();
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


        // add dividers on posts
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvClasses.getContext(),
                new LinearLayoutManager(getContext()).getOrientation());
        rvClasses.addItemDecoration(dividerItemDecoration);
    }



    public void getAllClasses() {

        Query parseQuery = new Query();
        parseQuery.getAllClasses().withTeacher().byTimeOfClass();

        parseQuery.findInBackground(new FindCallback<Class>() {
            @Override
            public void done(List<Class> objects, ParseException e) {
                if (e == null) {

                    for (int i = 0; i < objects.size(); i++) {
                        Log.d("HOME", "Class: " + objects.get(i).getName()+" "+objects.get(i).getDescription()+" "+objects.get(i).getDate());
                        Class classItem = objects.get(i);
                        mClasses.add(classItem);
                        classAdapter.notifyItemInserted(mClasses.size()-1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}


