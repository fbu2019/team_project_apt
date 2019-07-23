package com.example.skillshop;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skillshop.Models.Workshop;
import com.google.android.gms.wallet.PaymentsClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.xml.sax.Parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private Button btnClassOptions;
    private PaymentsClient mPaymentsClient; //  client for interacting with the Google Pay API
    private View mGooglePayButton; //   Google Pay payment button presented to the viewer for interaction

    private static int REQUEST_CODE = 333;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        detailedWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));

        //perform findViewById lookups by id in the xml file
        tvClassName = findViewById(R.id.tvClassName);
        tvInstructor = findViewById(R.id.tvInstructor);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvLocation = findViewById(R.id.tvLocation);
        tvCost = findViewById(R.id.tvCost);
        tvClassDescription = findViewById(R.id.tvClassDescription);
        ivClassPicture = findViewById(R.id.ivClassPicture);
        populateFields(detailedWorkshop);



        setUpClassOptions();

    }

    private void setUpClassOptions() {

        ParseUser teacher = detailedWorkshop.getTeacher();

        // if user is teacher
        if (teacher.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
            setUpTeacherSettings();
        } else {

            detailedWorkshop.getStudents().getQuery().findInBackground(new FindCallback() {
                @Override
                public void done(List objects, ParseException e) {
                }

                @Override
                public void done(Object o, Throwable throwable) {
                    // go through all enrolled students and see if user is one of them
                    boolean enrolled = false;
                    for (int i = 0; i < ((ArrayList) o).size(); i++) {
                        if (((ArrayList<ParseUser>) o).get(i).getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                            enrolled = true;
                            break;
                        }
                    }
                    // pass the status of student and allow them to sign up or drop a class
                    toggleClassSignUp(enrolled);
                }
            });
        }
    }

    private void setUpTeacherSettings() {
        btnClassOptions.setText("Edit Class");
        btnClassOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent editClassIntent = new Intent(ClassDetailsActivity.this, EditClassActivity.class);
                //pass in class that was selected
                editClassIntent.putExtra(Workshop.class.getSimpleName(), Parcels.wrap(detailedWorkshop));
                ClassDetailsActivity.this.startActivityForResult(editClassIntent, REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if ((data != null) && (requestCode == REQUEST_CODE)){


                Workshop updatedWorkshop = Parcels.unwrap(data.getParcelableExtra("updated"));
                populateFields(updatedWorkshop);
                detailedWorkshop = updatedWorkshop;

        }
    }

    private void toggleClassSignUp(final boolean enrolled)
    {

        if (enrolled) {
            btnClassOptions.setText("Drop Class");

        } else {
            btnClassOptions.setText("Sign Up");
        }

        btnClassOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if enrolled giv option to un enroll and also the opposite
                setStatusWorkshop(enrolled);
                //TODO - IF CLASS COSTS SOMETHING -- CONTINUE TO PAYMENT ACTIVITY
                if(detailedWorkshop.getInt("cost")>0) {
                    Log.e("ClassDetails", "Workshop is not free part2");
                    Intent i =  new Intent(ClassDetailsActivity.this, PayActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });

    }




    private void populateFields(Workshop workshop) {

        tvClassName.setText(workshop.getName());
        tvInstructor.setText(workshop.getTeacher().getUsername());

        // get dat eand format it for the views
        Date date = new Date(workshop.getDate());
        DateFormat dateFormat = new SimpleDateFormat("E MMM dd YYYY");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        tvDate.setText(dateFormat.format(date));
        tvTime.setText(timeFormat.format(date));


        tvLocation.setText(workshop.getLocationName());
        tvClassDescription.setText(workshop.getDescription());
        btnClassOptions = findViewById(R.id.btnClassOptions);


        switch (workshop.getCategory()) {

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

            default:
                break;
        }


        Double cost = workshop.getCost();
        if (cost == 0) {
            tvCost.setText("Free");
            tvCost.setBackground(new ColorDrawable(Color.parseColor("#00FF00")));
        } else {
            tvCost.setText("$ " + cost);
        }

    }

    public void setStatusWorkshop(boolean enroll) {

        ParseRelation<ParseUser> signedUpStudents = detailedWorkshop.getStudents();

        if (enroll) {
            // add user from list of students taking class and post this
            signedUpStudents.add(ParseUser.getCurrentUser());
        } else {
            // remove user from list of students taking class and post this
            signedUpStudents.remove(ParseUser.getCurrentUser());
        }

        detailedWorkshop.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    if(enroll) {
                        Toast.makeText(ClassDetailsActivity.this, "You dropped this class", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ClassDetailsActivity.this, "You signed up for this class", Toast.LENGTH_SHORT).show();
                    }
                    // TODO go home and refresh home page
                    finish();
                } else {
                    Toast.makeText(ClassDetailsActivity.this, "You weren't able to drop this class", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject()
                .put("apiVersion", 2)
                .put("apiVersionMinor", 0);
    }
}