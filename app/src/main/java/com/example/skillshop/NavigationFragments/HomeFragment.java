package com.example.skillshop.NavigationFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.skillshop.Models.Class;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class HomeFragment extends Fragment {


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_home),container,false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getAllClasses();

    }



    public void getAllClasses()
    {
        ParseQuery.getQuery(Class.class).findInBackground(new FindCallback<Class>() {
            @Override
            public void done(List<Class> objects, ParseException e) {
                if(e==null)
                {

                    for(int i = 0 ; i < objects.size();i++)
                    {
                        Log.d("HOME","Class: "+objects.get(i).getName());
                    }

                }
                else
                {

                    e.printStackTrace();
                }
            }

        });
    }

}
