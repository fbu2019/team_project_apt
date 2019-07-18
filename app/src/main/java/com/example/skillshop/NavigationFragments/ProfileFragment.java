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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.skillshop.LoginActivity;
import com.example.skillshop.R;
import com.example.skillshop.SignupActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static java.security.AccessController.getContext;

public class ProfileFragment extends Fragment {

    TextView nameViewText;
    ImageView ivProfilePic;
    EditText zipcodeInput;
    Button submitZipcodeButton;
    Button logoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_profile),container,false);
    }
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        final ParseUser user = ParseUser.getCurrentUser();
        final String username = user.getUsername();
        final String zipCode = (user.getString("zipCode"));

        nameViewText = view.findViewById(R.id.nameView);
        nameViewText.setText("Hello "+username+". Your current zipcode is "+zipCode+".");

        //  only attempts to display profile image if user has one
        ivProfilePic = view.findViewById(R.id.profilePicture);
        ParseFile profileImageFile = user.getParseFile("profilePicture");
        if (profileImageFile != null) {
            Glide.with(getContext()).load(profileImageFile.getUrl()).into(ivProfilePic);
            Log.i("Profile Frag", "There is a profile image");
        } else {
            ivProfilePic.setImageBitmap(null);
            Log.i("Profile Frag", "No profile image");
        }

        zipcodeInput = view.findViewById(R.id.etZipcode);
        submitZipcodeButton = view.findViewById(R.id.zipCodeModify);

        submitZipcodeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                final String newZipCode = zipcodeInput.getText().toString();

                if(newZipCode.length()!=5){
                    Toast.makeText(getContext(), "Zipcode entered must be correct length, "+newZipCode+" is now "+newZipCode.length(), Toast.LENGTH_LONG).show();
                }
                else {
                    user.put("zipCode", newZipCode);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                e.printStackTrace();
                                return;
                            } else {
                                nameViewText.setText("Hello " + username + ". Your current zipcode is " + newZipCode + ".");
                            }
                        }
                    });
                }
            }
        });

        logoutButton = view.findViewById(R.id.logoutButton);
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