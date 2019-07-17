package com.example.skillshop;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skillshop.Models.Workshop;

import org.parceler.Parcels;

public class ClassDetailsActivity extends AppCompatActivity {

    private Workshop detailedWorkshop;
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

        detailedWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));

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

        tvClassName.setText(detailedWorkshop.getName());
        tvInstructor.setText(detailedWorkshop.getTeacher().getUsername());
        String date = detailedWorkshop.getDate();
        tvDate.setText(date.substring(0,11));
        tvTime.setText(date.substring(11,16));
        tvLocation.setText("Location");

        Double cost = detailedWorkshop.getCost();
        if(cost == 0)
        {
            tvCost.setText("Free");
            tvCost.setBackground(new ColorDrawable(Color.parseColor("#00FF00")));
        }
        else
        {
            tvCost.setText("$"+Double.toHexString(cost));
        }


    }


}
