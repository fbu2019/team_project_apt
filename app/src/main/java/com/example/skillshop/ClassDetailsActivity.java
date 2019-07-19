package com.example.skillshop;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ClassDetailsActivity extends AppCompatActivity {

    private Workshop detailedWorkshop;
    private ImageView ivClassPicture;
    private TextView tvClassName;
    private TextView tvInstructor;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvLocation;
    private TextView tvCost;
    private TextView tvClassDescription;
    private Button btnSignUp;
    Boolean isTeacher;
    private Button btnEditClass; //TODO add logic for showing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        detailedWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));
        isTeacher = Parcels.unwrap(getIntent().getParcelableExtra("isTeacher"));

        //perform findViewById lookups by id in the xml file
        tvClassName = findViewById(R.id.tvClassName);
        tvInstructor = findViewById(R.id.tvInstructor);
        tvDate = findViewById(R.id.tvDate);
        tvTime =findViewById(R.id.tvTime);
        tvLocation =  findViewById(R.id.tvLocation);
        tvCost =  findViewById(R.id.tvCost);
        tvClassDescription = findViewById(R.id.tvClassDescription);
        ivClassPicture = findViewById(R.id.ivClassPicture);
        populateFields();


        setUpClassOptions();




    }

    private void setUpClassOptions() {

        ParseUser teacher = detailedWorkshop.getTeacher();


        // if user is teacher
        if(teacher.getUsername().equals(ParseUser.getCurrentUser().getUsername()))
        {
            btnSignUp.setClickable(false);
            btnSignUp.setEnabled(false);
        }




        detailedWorkshop.getStudents().getQuery().findInBackground(new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {

            }
            @Override
            public void done(Object o, Throwable throwable) {

                boolean enrolled = false;


                for(int i = 0 ; i < ((ArrayList) o).size();i++)
                {
                    if(((ArrayList<ParseUser>) o).get(i).getUsername().equals(ParseUser.getCurrentUser().getUsername()))
                    {
                        enrolled = true;
                    }
                }
                toggleClassSignUp(enrolled);
            }
        });


    }

    private void toggleClassSignUp(final boolean enrolled)
    {

        if(enrolled)
        {
            btnSignUp.setText("Drop Class");
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropWorkshop();

                }
            });
        }
        else
        {
            btnSignUp.setText("Sign Up");
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signUpForWorkshop();

                }
            });
        }



    }



    private void populateFields() {

        tvClassName.setText(detailedWorkshop.getName());
        tvInstructor.setText(detailedWorkshop.getTeacher().getUsername());
        String date = detailedWorkshop.getDate();
        tvDate.setText(date.substring(0,11));
        tvTime.setText(date.substring(11,16));
        tvLocation.setText(detailedWorkshop.getLocationName());
        tvClassDescription.setText(detailedWorkshop.getDescription());
        btnSignUp = findViewById(R.id.btnSignUp);

        switch (detailedWorkshop.getCategory()) {

            case "Culinary":
                ivClassPicture.setImageResource(R.drawable.cooking);
                break;

            case "Education":
                ivClassPicture.setImageResource(R.drawable.education);
                break;
            case "Fitness":
                ivClassPicture.setImageResource(R.drawable.fitness);
                break;
            case "Arts/Crafts":
                ivClassPicture.setImageResource(R.drawable.arts);
                break;

            case "Other":
                ivClassPicture.setImageResource(R.drawable.misc);
                break;

            default: break;
        }

        Double cost = detailedWorkshop.getCost();
        if(cost == 0)
        {
            tvCost.setText("Free");
            tvCost.setBackground(new ColorDrawable(Color.parseColor("#00FF00")));
        }
        else
        {
            tvCost.setText("$"+cost);
        }

    }

    public void signUpForWorkshop()
    {
        ParseRelation<ParseUser> signedUpStudents = detailedWorkshop.getStudents();

        signedUpStudents.add(ParseUser.getCurrentUser());

        detailedWorkshop.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    Toast.makeText(ClassDetailsActivity.this, "You're signed up for this class!", Toast.LENGTH_SHORT).show();
                    // TODO go home and refresh home page
                    finish();
                }
                else
                {
                    Toast.makeText(ClassDetailsActivity.this, "You weren't able to sign up ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void dropWorkshop()
    {
        ParseRelation<ParseUser> signedUpStudents = detailedWorkshop.getStudents();

        signedUpStudents.remove(ParseUser.getCurrentUser());

        detailedWorkshop.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    Toast.makeText(ClassDetailsActivity.this, "You dropped this class", Toast.LENGTH_SHORT).show();
                    // TODO go home and refresh home page
                    finish();
                }
                else
                {
                    Toast.makeText(ClassDetailsActivity.this, "You weren't able to drop this class", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
