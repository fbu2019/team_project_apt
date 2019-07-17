package com.example.skillshop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class NewClassActivity extends AppCompatActivity {


    TextView etClassname;
    TextView etDate;
    TextView etLocation;
    TextView etDescription;
    TextView etCategory;
    TextView etCost;

    String classname;
    String date;
    String location;
    String description;
    String category;
    String cost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);
        findAllViews();
        setAllViews();
    }

    private void setAllViews() {
        String classname = etClassname.getText().toString();
        String date;
        String location;
        String description = etDescription.getText().toString();
        String category;
        String cost;


    }

    private void findAllViews() {

        etClassname = findViewById(R.id.etClassname);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        etCategory = findViewById(R.id.etCategory);
        etCost = findViewById(R.id.etCost);
;
    }

    //TODO fix type issues
    //TODO send to parse


}
