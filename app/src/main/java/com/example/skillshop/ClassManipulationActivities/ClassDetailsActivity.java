package com.example.skillshop.ClassManipulationActivities;

import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.skillshop.ChatActivity;
import com.example.skillshop.ClassAttendeesActivity;
import com.example.skillshop.InstructorDetailsActivity;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.NavigationFragments.FragmentHandler;
import com.example.skillshop.R;
import com.google.android.gms.wallet.PaymentsClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassDetailsActivity extends AppCompatActivity {

    private Workshop detailedWorkshop;
    private ImageView ivClassPicture;
    private ImageView ivInstructorProfile;
    private TextView tvClassName;
    private TextView tvInstructor;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvLocation;
    private TextView tvCost;
    private TextView tvClassDescription;
    private Button btnClassOptions;
    private Button btnViewAttendees;
    private Button btnChat;

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
        setUpInstructor(detailedWorkshop);

        setUpClassOptions();
        setUpViewAttendees();
    }

    private void setUpViewAttendees() {
        btnViewAttendees = findViewById(R.id.btnViewAttendees);
        btnViewAttendees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openAttendeesView = new Intent(ClassDetailsActivity.this, ClassAttendeesActivity.class);
                openAttendeesView.putExtra(Workshop.class.getSimpleName(), Parcels.wrap(detailedWorkshop));
                startActivity(openAttendeesView);
            }
        });

    }

    private void setUpClassOptions() {

        ParseUser teacher = detailedWorkshop.getTeacher();


        // if user is teacher
        if (teacher.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
            setUpTeacherSettings();
            setUpChat();
        } else {


            ArrayList<String> students = (ArrayList<String>) detailedWorkshop.getStudents();

            boolean enrolled = students.contains(ParseUser.getCurrentUser().getObjectId());
            toggleClassSignUp(enrolled);
            if(enrolled)
            {
                setUpChat();
            }



        }
    }

    private void setUpChat()
    {
        btnChat = findViewById(R.id.btnChat);
        btnChat.setVisibility(View.VISIBLE);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chatIntent = new Intent(ClassDetailsActivity.this, ChatActivity.class);


                //pass in class that was selected
                chatIntent.putExtra(Workshop.class.getSimpleName(), Parcels.wrap(detailedWorkshop));

                startActivity(chatIntent);
            }
        });
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
                setUpInstructor(updatedWorkshop);
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
                setStatusTaking(enrolled);
            }
        });

    }

    private void setStatusTaking(boolean enrolled) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ArrayList<String> classesTaking = (ArrayList<String>) currentUser.get("classesTaking");

        String workshopObjectId = detailedWorkshop.getObjectId();

        if (enrolled && (classesTaking.contains(workshopObjectId))){ //TODO double check

                classesTaking.remove(workshopObjectId);
        }
        else
        {
            if (!classesTaking.contains(workshopObjectId))
            classesTaking.add(workshopObjectId);
        }

        currentUser.put("classesTaking", classesTaking);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i("ClassDetailsActivity", "Class was not successfully added to classesTaking array");
                }
            }
        });
    }

    private void populateFields(Workshop workshop) {

        tvClassName.setText(workshop.getName());

        if(workshop.getTeacher()!= null){
            tvInstructor.setText(workshop.getTeacher().getString("firstName")+" "+workshop.getTeacher().getString("lastName"));
        }

        // get date and format it for the views
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

    private void setUpInstructor(Workshop workshop) {
        String profilePhotoUrl = workshop.getTeacher().getString("profilePicUrl");

        ivInstructorProfile = findViewById(R.id.ivProfile);
        if (profilePhotoUrl != null) {
            Glide.with(ClassDetailsActivity.this)
                    .load(profilePhotoUrl)
                    .error(R.drawable.profile)
                    .placeholder(R.drawable.profile)
                    .apply(new RequestOptions().circleCrop())
                    .into(ivInstructorProfile);

            Log.i("ClassDetails", profilePhotoUrl);
        } else {
            ivInstructorProfile.setImageBitmap(null);
            Log.i("ClassDetails", "No profile image");
        }


        ivInstructorProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ClassDetailsActivity.this, InstructorDetailsActivity.class);
                i.putExtra(Workshop.class.getSimpleName(), Parcels.wrap(workshop));
                startActivity(i);
                Log.e("ClassDetails", "Starting Activity");
            }
        });
    }

    public void setStatusWorkshop(boolean enroll) {


        ArrayList<String> students = (ArrayList<String>) detailedWorkshop.getStudents();


        String objectId = ParseUser.getCurrentUser().getObjectId();

        if (enroll){
            students.remove(objectId);
        }
        else
        {
            students.add(objectId);
        }

        detailedWorkshop.setStudents(students);
        detailedWorkshop.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    if(enroll) {
                        Toast.makeText(ClassDetailsActivity.this, "You dropped this class", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ClassDetailsActivity.this, FragmentHandler.class);
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(ClassDetailsActivity.this, "You signed up for this class", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ClassDetailsActivity.this, FragmentHandler.class);
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(ClassDetailsActivity.this, "You weren't able to drop this class", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}