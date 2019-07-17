package com.example.skillshop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skillshop.Models.Class;

import org.parceler.Parcels;

public class ClassDetailsActivity extends AppCompatActivity {

    private Class detailedClass;
    private ImageView ivClassIcon;
    private TextView tvClassName;
    private TextView tvInstructor;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvLocation;
    private TextView tvCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        detailedClass = Parcels.unwrap(getIntent().getParcelableExtra(Class.class.getSimpleName()));

        //perform findViewById lookups by id in the xml file
        tvClassName = findViewById(R.id.tvClassName);
        tvInstructor = findViewById(R.id.tvInstructor);
        tvDate = findViewById(R.id.tvDate);
        tvTime =findViewById(R.id.tvTime);
        tvLocation =  findViewById(R.id.tvLocation);
        tvCost =  findViewById(R.id.tvCost);

        populateFields();

    }

    private void populateFields() {

        tvClassName.setText(detailedClass.getName());
        tvInstructor.setText(detailedClass.getTeacher().getUsername());
        String date = detailedClass.getDate();
        tvDate.setText(date.substring(0,11));
        tvTime.setText(date.substring(11,16));
        tvLocation.setText("Location");
        tvCost.setText("Cost");


    }


}
