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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.skillshop.LoginActivity;
import com.example.skillshop.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

public class ProfileFragment extends Fragment {

    public String photoFileName = "photo.jpg";
    File photoFile;

    TextView nameViewText;
    ImageView ivProfilePic;
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
        ivProfilePic = view.findViewById(R.id.profilePicture);

        //  only attempts to display profile image if user has one
        ParseFile profileImageFile = user.getParseFile("profilePicture");
        if (profileImageFile != null) {
            Glide.with(getContext()).load(profileImageFile.getUrl()).into(ivProfilePic);
            Log.i("Profile Frag", "There is a profile image");
        } else {
            ivProfilePic.setImageBitmap(null);
            Log.i("Profile Frag", "No profile image");
        }

        nameViewText.setText("Hello "+username+". Your current zipcode is "+zipCode+".");

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


