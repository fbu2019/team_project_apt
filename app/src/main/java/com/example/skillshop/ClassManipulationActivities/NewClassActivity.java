package com.example.skillshop.ClassManipulationActivities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import com.example.skillshop.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class NewClassActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String TAG = "NewClassActivity";
    public final String APP_TAG = "MyCustomApp";

    TextView etClassname;
    TextView etDate;
    TextView etTime;
    Button btLocation;
    TextView etDescription;
    Spinner spinCategory;
    TextView etCost;
    ImageView ivClassImage;
    Button btSubmit;
    Workshop newClass;


    ParseGeoPoint location;
    String locationName;

    HashMap<String, Integer> dateMap;

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;
    public final static int AUTOCOMPLETE_REQUEST_CODE = 42;
    public final static int YEAR_OFFSET = 1900;

    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiKey = this.getResources().getString(R.string.places_api_key);
        setContentView(R.layout.activity_new_class);
        findAllViews();
        setSubmitListener();

        newClass = new Workshop();


        // Initialize Places.
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }


        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIntent();
            }
        });


        setOnPictureUploadButton();

        setTimeAndDateListeners();

        setSpinner();

    }

    public void setSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinCategory.setAdapter(adapter);
    }

    public void setOnPictureUploadButton() {
        // allow user to pick a picture from their gallery to set as image of the class
        ivClassImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });
    }

    public void setTimeAndDateListeners() {

        // dates todays date
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();

        // initializes date picker with today's date
        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, NewClassActivity.this, date.getYear() + YEAR_OFFSET, date.getMonth(), date.getDay());

        // initializes date picker with the current time
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, NewClassActivity.this, date.getHours(), date.getMinutes(), true);

        // initialize map to remember date values for class
        dateMap = new HashMap<>();
        // date text box when clicked launches date dialog
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });


        // time text box when clicked launches time dialog
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });
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

        // adds date values to map
        dateMap.put("year", year);
        dateMap.put("month", month);
        dateMap.put("dayOfMonth", dayOfMonth);
        etDate.setText(String.format("%d/%d/%d", month, dayOfMonth, year));

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        // adds time values to map
        dateMap.put("hourOfDay", hourOfDay);
        dateMap.put("minute", minute);
        etTime.setText(String.format("%d:%d", hourOfDay, minute));

    }

    private void postWorkshop() {


        newClass.setDescription(etDescription.getText().toString());

        newClass.setName(etClassname.getText().toString());

        // creates new date instance with values form map to post
        Date date = new Date(dateMap.get("year") - YEAR_OFFSET, dateMap.get("month") + 1, dateMap.get("dayOfMonth"), dateMap.get("hourOfDay"), dateMap.get("minute"));
        newClass.setDate(date);

        newClass.setCost(Double.parseDouble(etCost.getText().toString()));

        newClass.setCategory(spinCategory.getSelectedItem().toString());

        newClass.setTeacher(ParseUser.getCurrentUser());

        newClass.setLocationName(locationName);

        newClass.setLocation(location);


//        ParseFile imageFile = new ParseFile();
//
//
//        newClass.setImage(imageFile);

        ArrayList<String> students = new ArrayList<>();
        newClass.setStudents(students);


        newClass.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(NewClassActivity.this, "Class was made", Toast.LENGTH_SHORT).show();
                    // TODO go home and refresh home page
                    finish();

                } else {

                    Toast.makeText(NewClassActivity.this, "Class wasn't made", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void findAllViews() {

        etClassname = findViewById(R.id.etClassname);
        etDate = findViewById(R.id.etDate);
        btLocation = findViewById(R.id.btLocation);
        etDescription = findViewById(R.id.etDescription);
        spinCategory = findViewById(R.id.spinCategory);
        etCost = findViewById(R.id.etCost);
        btSubmit = findViewById(R.id.btSubmit);
        ivClassImage = findViewById(R.id.ivClassImage);
        etTime = findViewById(R.id.etTime);

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
        if ((data != null) && (requestCode == PICK_PHOTO_CODE)) {
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
        if ((data != null) && (requestCode == AUTOCOMPLETE_REQUEST_CODE)) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            locationName = place.getName();
            btLocation.setText(locationName);
            LatLng latLng = place.getLatLng();
            location = new ParseGeoPoint(latLng.latitude, latLng.longitude);
        }
    }

    private void launchIntent() {
        // Specify the types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }


}

