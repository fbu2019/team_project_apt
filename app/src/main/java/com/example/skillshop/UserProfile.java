package com.example.skillshop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class UserProfile  extends AppCompatActivity {

    TextView nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        nameView = (TextView) findViewById(R.id.nameView);

    }

}