package com.example.skillshop;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.skillshop.Models.Workshop;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;


public class NewClassActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

    public static final String TAG = "NewClassActivity";

    TextView etClassname;
    TextView etDate;
    TextView etTime;
    TextView etLocation;
    TextView etDescription;
    Spinner spinCategory;
    TextView etCost;
    ImageView ivClassImage;
    Button btSubmit;

    HashMap<String, Integer> dateMap;


    //    private final String apiKey = getResources().getString(R.string.places_api_key);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);
        findAllViews();
        setSubmitListener();

//        final TextView txtVw = findViewById(R.id.placeName);

        // Initialize Places.
/*        Places.initialize(getApplicationContext(), apiKey);
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
               getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
           }
           @Override
            public void onError(Status status) {
               // TODO: Handle the error.
               Log.i(TAG, "An error occurred: " + status);
            }
        });*/

        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, NewClassActivity.this, 2019, 7, 1);

        final TimePickerDialog timePickerDialog = new TimePickerDialog(this,NewClassActivity.this,0,0,true);

        dateMap = new HashMap<>();

        ivClassImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });


        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinCategory.setAdapter(adapter);


    }

    private void setSubmitListener() {

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postWorkshop();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        dateMap.put("year",year);
        dateMap.put("month",month);
        dateMap.put("dayOfMonth",dayOfMonth);
        etDate.setText(String.format("%d/%d/%d",month,dayOfMonth,year));

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        dateMap.put("hourOfDay",hourOfDay);
        dateMap.put("minute",minute);
        etTime.setText(String.format("%d:%d",hourOfDay,minute));


    }

    private void postWorkshop() {

        final Workshop newClass = new Workshop();

        newClass.setDescription(etDescription.getText().toString());
        newClass.setName(etClassname.getText().toString());

        Date date = new Date(dateMap.get("year"),dateMap.get("month"),dateMap.get("dayOfMonth"),dateMap.get("hourOfDay"),dateMap.get("minute"));
        newClass.setDate(date);

        newClass.setCost(Double.parseDouble(etCost.getText().toString()));

        newClass.setCategory(spinCategory.getSelectedItem().toString());

        newClass.setTeacher(ParseUser.getCurrentUser());


        newClass.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    Toast.makeText(NewClassActivity.this, "Class was made", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(NewClassActivity.this, "Class wasn't made", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void findAllViews() {

        etClassname = findViewById(R.id.etClassname);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        spinCategory = findViewById(R.id.spinCategory);
        etCost = findViewById(R.id.etCost);
        btSubmit = findViewById(R.id.btSubmit);
        ivClassImage = findViewById(R.id.ivClassImage);
        etTime = findViewById(R.id.etTime);
        ;
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri

            Bitmap selectedImage = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Load the selected image into a preview
            ImageView ivPreview = findViewById(R.id.ivClassImage);
            ivPreview.setImageBitmap(selectedImage);
        }
    }



}

