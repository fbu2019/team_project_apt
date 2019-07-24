package com.example.skillshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

public class AddUserPreferences extends AppCompatActivity {

    Button submitButton;
    CheckBox culinaryBox;
    CheckBox educationBox;
    CheckBox fitnessBox;
    CheckBox artsCraftsBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_preferences);

        culinaryBox = findViewById(R.id.culinaryBox);
        educationBox = findViewById(R.id.educationBox);
        fitnessBox = findViewById(R.id.fitnessBox);
        artsCraftsBox = findViewById(R.id.artsCraftsBox);

        checkCurrentPreferences();

        submitButton = findViewById(R.id.continueButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrievePreferences();
                finish();
            }
        });
    }

    private void checkCurrentPreferences() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser.get("preferences") != null ) {

            ArrayList<String> preferences = (ArrayList<String>) currentUser.get("preferences");

            for (int i = 0; i < preferences.size(); i++) {
                if (preferences.get(i).equals("Culinary")) {
                    culinaryBox.setChecked(true);
                }
                if (preferences.get(i).equals("Education")) {
                    educationBox.setChecked(true);
                }
                if (preferences.get(i).equals("Fitness")) {
                    fitnessBox.setChecked(true);
                }
                if (preferences.get(i).equals("Arts and Crafts")) {
                    artsCraftsBox.setChecked(true);
                }
            }

        }

    }

    //TODO: potentiall make each preference an individual boolean column
    private void retrievePreferences() {
        ArrayList <String> preferences = new ArrayList<String>();

        if(culinaryBox.isChecked()){
            preferences.add("Culinary");
        }
        if(educationBox.isChecked()){
            preferences.add("Education");
        }
        if(fitnessBox.isChecked()){
            preferences.add("Fitness");
        }
        if(artsCraftsBox.isChecked()){
            preferences.add("Arts and Crafts");
        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("preferences", preferences);

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
            }
        });
    }
}