package com.example.skillshop.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;

import com.example.skillshop.Adapters.ClassAdapterCard;
import com.example.skillshop.FollowingListActivity;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {


    CardView cvAll;



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_category_choose), container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        cvAll = view.findViewById(R.id.cvAll);

        cvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create new fragment to use
                Fragment home = new HomeFragment();
                // transaction on current activity
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                transaction.setCustomAnimations(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);

                transaction.replace(R.id.flContainer, home);
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();


            }
        });




    }
}