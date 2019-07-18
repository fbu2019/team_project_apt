package com.example.skillshop.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.skillshop.LoginActivity;
import com.example.skillshop.R;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    TextView nameViewText;
    Button logoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_profile),container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ParseUser user = ParseUser.getCurrentUser();
        String username = user.getUsername();
        String zipCode = (user.getString("zipCode"));

        nameViewText = view.findViewById(R.id.nameView);
        logoutButton = view.findViewById(R.id.logoutButton);


        Log.i("Profile Frag", zipCode);
        nameViewText.setText("Hello "+username+". Your current zipcode is "+zipCode);

        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });

    }

}
