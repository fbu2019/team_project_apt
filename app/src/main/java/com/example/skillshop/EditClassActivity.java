package com.example.skillshop;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class EditClassActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

    public static final String TAG = "EditClassActivity";

    TextView etClassname;
    TextView etDate;
    TextView etTime;
    Button btLocation;
    TextView etDescription;
    Spinner spinCategory;
    TextView etCost;
    ImageView ivClassImage;
    Button btSubmit;
    Workshop currentWorkshop;
    Workshop editedClass;


    ParseGeoPoint location;
    String locationName;
    ArrayAdapter<CharSequence> adapter;
    HashMap<String, Integer> dateMap;

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;
    public final static int AUTOCOMPLETE_REQUEST_CODE = 42;
    public final static int YEAR_OFFSET = 1900;
    public final static int HOUR_OFFSET = 1900;

    private final String apiKey = "AIzaSyARv5bJ1b1bnym8eUwPZlGm_7HN__WsbFE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);
        findAllViews();
        setupPlacesApi();
        setupDatePicker();
        setCurrentDetails();
        setSubmitListener();

    }

    private void setupDatePicker() {

        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, EditClassActivity.this, 2019, 7, 1);

        final TimePickerDialog timePickerDialog = new TimePickerDialog(this,EditClassActivity.this,0,0,true);

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
        adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinCategory.setAdapter(adapter);

    }



    @TargetApi(Build.VERSION_CODES.O)
    private void setCurrentDetails() {
        currentWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));
        etClassname.setText(currentWorkshop.getName());
        etDescription.setText(currentWorkshop.getDescription());
        //TODO set date
        btLocation.setText(currentWorkshop.getLocationName());
        etCost.setText(currentWorkshop.getCost().toString());
        Integer categoryPosition = adapter.getPosition(currentWorkshop.getCategory());
        spinCategory.setSelection(categoryPosition);
        Date currentDate = currentWorkshop.getJavaDate();

        LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        //TODO figure out year offset
        int year  = localDateTime.getYear() - YEAR_OFFSET;
        int month = localDateTime.getMonthValue();
        int day   = localDateTime.getDayOfMonth();
        int hour = localDateTime.getHour() - HOUR_OFFSET;
        int minute = localDateTime.getMinute();
        etDate.setText(String.format("%d/%d/%d",month,day,year));
        //dateMap.put("year",year);
        //dateMap.put("month",month);
        //dateMap.put("dayOfMonth",day);
        etTime.setText(String.format("%d:%d",hour,minute));
       // dateMap.put("hourOfDay",hour);
       // dateMap.put("minute",minute);



        int i = 0;

    }

    private void setupPlacesApi() {

        // Initialize Places.
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), apiKey);
        }

        //Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(EditClassActivity.this);


        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSelectPlaceIntent();
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

        final Workshop editedClass = new Workshop();
        editedClass.setDescription(etDescription.getText().toString());
        editedClass.setName(etClassname.getText().toString());
        Date date = new Date(dateMap.get("year"),dateMap.get("month"),dateMap.get("dayOfMonth"),dateMap.get("hourOfDay"),dateMap.get("minute"));
        editedClass.setDate(date);

        editedClass.setCost(Double.parseDouble(etCost.getText().toString()));

        editedClass.setCategory(spinCategory.getSelectedItem().toString());

        editedClass.setTeacher(ParseUser.getCurrentUser());

        editedClass.setLocationName(btLocation.getText().toString());
   //     newClass.setLocation(location);


        editedClass.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    Toast.makeText(EditClassActivity.this, "Class was made", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(EditClassActivity.this, "Class wasn't made", Toast.LENGTH_SHORT).show();
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
        if ((data != null) && (requestCode == PICK_PHOTO_CODE)){
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
        if ((data != null) && (requestCode == AUTOCOMPLETE_REQUEST_CODE)){
            Place place = Autocomplete.getPlaceFromIntent(data);
            locationName = place.getName();
            btLocation.setText(locationName);
            LatLng latLng = place.getLatLng();
            location = new ParseGeoPoint(latLng.latitude, latLng.longitude);



        }

    }

    private void launchSelectPlaceIntent() {
        // Specify the types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }


}


