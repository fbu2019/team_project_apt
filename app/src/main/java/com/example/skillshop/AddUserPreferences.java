package com.example.skillshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;

public class AddUserPreferences extends AppCompatActivity {

    Button temporaryContinue;
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

        temporaryContinue = findViewById(R.id.continueButton);
        temporaryContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
