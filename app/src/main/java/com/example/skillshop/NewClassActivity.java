package com.example.skillshop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static okhttp3.internal.http.HttpDate.format;


public class NewClassActivity extends AppCompatActivity {

    public static final String TAG = "NewClassActivity";

    TextView etClassname;
    TextView etDate;
    TextView etLocation;
    TextView etDescription;
    TextView etCategory;
    TextView etCost;

    Button btSubmit;

    String classname;
    String date;
    String location;
    String description;
    String category;
    String cost;
    String dateString;
    String dateConverted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);
        findAllViews();
        setSubmitListener();
    }

    private void setSubmitListener() {

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllViews();
            }
        });
    }

    private void setAllViews() {
        classname = etClassname.getText().toString();
        dateString = etDate.getText().toString();
        Toast.makeText(this, classname, Toast.LENGTH_LONG);
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dateString);
            dateConverted = format(date);

        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date.");
            e.printStackTrace();
        }

     //   location;
        description = etDescription.getText().toString();

      //  category;
      //  cost;


    }

    private void findAllViews() {

        etClassname = findViewById(R.id.etClassname);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        etCategory = findViewById(R.id.etCategory);
        etCost = findViewById(R.id.etCost);
        btSubmit = findViewById(R.id.btSubmit);
;
    }

    //TODO fix type issues
    //TODO send to parse


}
