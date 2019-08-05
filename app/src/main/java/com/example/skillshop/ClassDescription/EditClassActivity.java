package com.example.skillshop.ClassDescription;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.example.skillshop.NavigationFragments.FragmentHandler;
import com.example.skillshop.NavigationFragments.Home.AllCategoryFragment;
import com.example.skillshop.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class EditClassActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

    public static final String TAG = "EditClassActivity";

    TextView etClassname;
    Button btnDate;
    Button btnTime;
    Button btLocation;
    TextView etDescription;
    Spinner spinCategory;
    TextView etCost;
    ImageView ivClassImage;
    Button btSubmit;
    Workshop currentWorkshop;
    ImageView ivTrash;
    Date currentDate;


    ParseGeoPoint location;
    String locationName;
    ArrayAdapter<CharSequence> adapter;
    HashMap<String, Integer> dateMap;

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;
    public final static int AUTOCOMPLETE_REQUEST_CODE = 42;
    public final static int YEAR_OFFSET = 1900;
    public final static int HOUR_OFFSET = 1;

    private String apiKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiKey = this.getResources().getString(R.string.places_api_key);
        setContentView(R.layout.activity_edit_class_new);
        currentWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));
        currentDate = currentWorkshop.getJavaDate();

        findAllViews();
        setupPlacesApi();
        setupDatePicker();
        setCurrentDetails();
        setSubmitListener();
        setTrashListener();
    }

    private void refreshDetailsPage(Workshop editedWorkshop) {
        Intent data = new Intent();
        data.putExtra("updated", Parcels.wrap(editedWorkshop));
        setResult(RESULT_OK, data);
    }



    private void setupDatePicker() {

        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, EditClassActivity.this, currentDate.getYear()+YEAR_OFFSET, currentDate.getMonth(), currentDate.getDate());

        final TimePickerDialog timePickerDialog = new TimePickerDialog(this,EditClassActivity.this,currentDate.getHours(),currentDate.getMinutes(),true);

        dateMap = new HashMap<>();

        ivClassImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });


        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
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
        etClassname.setText(currentWorkshop.getName());
        etDescription.setText(currentWorkshop.getDescription());
        btLocation.setText(currentWorkshop.getLocationName());
        etCost.setText(currentWorkshop.getCost().toString());
        Integer categoryPosition = adapter.getPosition(currentWorkshop.getCategory());
        spinCategory.setSelection(categoryPosition);


        LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        //TODO figure out year offset
        int year  = localDateTime.getYear();
        int month = localDateTime.getMonthValue();
        int day   = localDateTime.getDayOfMonth();
        int hour = localDateTime.getHour() - HOUR_OFFSET;
        int minute = localDateTime.getMinute();

        SimpleDateFormat dateString = new SimpleDateFormat("MM/dd/YYYY");

        btnDate.setText(dateString.format(currentDate));


        dateMap.put("year",year);
        dateMap.put("month",month);
        dateMap.put("dayOfMonth",day);

        SimpleDateFormat timeString = new SimpleDateFormat("HH:mm");
        btnTime.setText(timeString.format(currentDate));

        dateMap.put("hourOfDay",hour);
        dateMap.put("minute",minute);

        location = currentWorkshop.getLocation();
        locationName = currentWorkshop.getLocationName();

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

    private void setTrashListener() {

        ivTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    removeWorkshop();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        dateMap.put("year",year);
        dateMap.put("month",month);
        dateMap.put("dayOfMonth",dayOfMonth);
        btnDate.setText(String.format("%d/%d/%d",month,dayOfMonth,year));

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        dateMap.put("hourOfDay",hourOfDay);
        dateMap.put("minute",minute);
        btnTime.setText(String.format("%d:%d",hourOfDay,minute));


    }

    private void postWorkshop() {
        currentWorkshop.setName(etClassname.getText().toString());
        currentWorkshop.setDescription( etDescription.getText().toString());
        currentWorkshop.setLocation(location);
        currentWorkshop.setLocationName(locationName);
        currentWorkshop.setCost(Double.parseDouble(etCost.getText().toString()));
        currentWorkshop.setCategory(spinCategory.getSelectedItem().toString());
        Date date = new Date(dateMap.get("year") - YEAR_OFFSET ,dateMap.get("month"),dateMap.get("dayOfMonth"),dateMap.get("hourOfDay") ,dateMap.get("minute"));
        currentWorkshop.setDate(date);

        currentWorkshop.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    Toast.makeText(EditClassActivity.this, "Changes have been saved (changes may take a while to be reflected in the app)", Toast.LENGTH_SHORT).show();
                    Workshop editedWorkshop = currentWorkshop;

                    sendNotifications();
                    refreshDetailsPage(editedWorkshop);
                    finish();
                }
                else
                {
                    Toast.makeText(EditClassActivity.this, "Error saving changes", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void removeWorkshop() throws ParseException {
        List<Workshop> objects = new ArrayList<>();
        objects.add(currentWorkshop);
        ParseObject.deleteAll(objects);
        Intent i = new Intent(this, FragmentHandler.class);
        startActivity(i);
    }


    public void sendNotifications()
    {
        AsyncHttpClient client = new AsyncHttpClient();

        // set the request parameters
        RequestParams params = new RequestParams();
        // api key param put in
        params.add("classId",currentWorkshop.getObjectId());

        // do get request to this server to send notifications to everyone involved with this class
        client.get("https://agile-caverns-23612.herokuapp.com/",params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                Toast.makeText(EditClassActivity.this, responseString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(EditClassActivity.this, responseString, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToHomeFragment(){

        // create new fragment to use
        Fragment home = new AllCategoryFragment();
        // transaction on current activity
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContainer, home);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    private void findAllViews() {

        etClassname = findViewById(R.id.etClassname);
        btnDate = findViewById(R.id.btnDate);
        btLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        spinCategory = findViewById(R.id.categoryPicker);
        etCost = findViewById(R.id.etCost);
        btSubmit = findViewById(R.id.btSubmit);
        ivClassImage = findViewById(R.id.ivClassImage);
        btnTime = findViewById(R.id.btnTime);
        ivTrash =findViewById(R.id.ivTrash);

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
        if ((data != null) && (requestCode == AUTOCOMPLETE_REQUEST_CODE)  && (resultCode == RESULT_OK)){
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


