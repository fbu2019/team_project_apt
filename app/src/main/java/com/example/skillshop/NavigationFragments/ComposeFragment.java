package com.example.skillshop.NavigationFragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ComposeFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    public static final String TAG = "NewClassActivity";
    public final String APP_TAG = "MyCustomApp";

    TextView etClassname;
    Button btnDate;
    Button btnTime;
    TextView etLocation;
    TextView etDescription;
    Spinner spinCategory;
    TextView etCost;
    ImageView ivClassImage;
    Button btSubmit;
    Workshop newClass;
    String[] categoryArray;

    ParseGeoPoint location;
    String locationName;
    private File photoFile;
    NumberPicker categoryPicker;
    NumberPicker subCategoryPicker;
    Uri photoUri;

    Date today;

    View v;
    HashMap<String, Integer> dateMap;

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;
    public final static int AUTOCOMPLETE_REQUEST_CODE = 42;
    public final static int YEAR_OFFSET = 1900;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;


    public String photoFileName = "photo.jpg";
    private String apiKey;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_new_compose), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Integer> skillsData = new ArrayList<Integer>(Collections.nCopies(10, 0));
        ParseUser curr = ParseUser.getCurrentUser();
        curr.put("skillsData", skillsData);
        curr.saveInBackground();
        apiKey = this.getResources().getString(R.string.places_api_key);
        findAllViews(view);
        setSubmitListener();

        v = view;

        newClass = new Workshop();

        // dates todays date
        Calendar cal = Calendar.getInstance();
        today = cal.getTime();


        // Initialize Places.
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }


        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIntent();
            }
        });


        setOnPictureUploadButton();
        setTimeAndDateListeners();
        setCategoryPicker();
        setSubCategoryPicker(0);


    }

    private void setCategoryPicker() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        categoryPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                setSubCategoryPicker(newVal);
            }
        });
        categoryArray = getResources().getStringArray(R.array.categories);

        categoryPicker.setMinValue(0);
        categoryPicker.setMaxValue(categoryArray.length-1);
        categoryPicker.setDisplayedValues(categoryArray);
    }

    private void setSubCategoryPicker(int i) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        String [] subCategoryArray;
        switch(i){
            case 0:{
                subCategoryArray = getResources().getStringArray(R.array.subCategoriesArtsCrafts);
                break;
            }
            case 1:{
                subCategoryArray = getResources().getStringArray(R.array.subCategoriesCulinary);
                break;
            }
            case 2:{
                subCategoryArray = getResources().getStringArray(R.array.subCategoriesEducation);
                break;
            }
            case 3:{
                subCategoryArray = getResources().getStringArray(R.array.subCategoriesFitness);
                break;
            }
            case 4:{
                subCategoryArray = getResources().getStringArray(R.array.subCategoriesOther);
                break;
            }
            default:{
                subCategoryArray = getResources().getStringArray(R.array.subCategoriesArtsCrafts);
                break;
            }

        }
        subCategoryPicker.setDisplayedValues(null);
        subCategoryPicker.setMinValue(0);
        subCategoryPicker.setMaxValue(subCategoryArray.length-1);
        subCategoryPicker.setDisplayedValues(subCategoryArray);
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




        // initializes date picker with today's date
        final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),ComposeFragment.this,today.getYear() + YEAR_OFFSET, today.getMonth(), today.getDay());

        // initializes date picker with the current time
        final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), ComposeFragment.this, today.getHours(), today.getMinutes(), true);

        // initialize map to remember date values for class
        dateMap = new HashMap<>();
        // date text box when clicked launches date dialog
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();



            }
        });


        // time text box when clicked launches time dialog
        btnTime.setOnClickListener(new View.OnClickListener() {
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

        SimpleDateFormat dateString = new SimpleDateFormat("MM/dd/YYYY");
        Date tempDate = new Date(year-YEAR_OFFSET,month,dayOfMonth);


        btnDate.setText(dateString.format(tempDate));


    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        // adds time values to map
        dateMap.put("hourOfDay", hourOfDay);
        dateMap.put("minute", minute);

        SimpleDateFormat dateString = new SimpleDateFormat("HH:mm");
        Date tempDate = new Date(0,0,0,hourOfDay,minute);

        btnTime.setText(dateString.format(tempDate));
        ParseUser user = ParseUser.getCurrentUser();


    }

    private void postWorkshop() {
        String category = spinCategory.getSelectedItem().toString();

       Integer categorySelectedIndex = categoryPicker.getValue();
       categoryArray = getResources().getStringArray(R.array.categories);
       String categorySelected =  categoryArray[categorySelectedIndex];
        try {

            newClass.setDescription(etDescription.getText().toString());

            newClass.setName(etClassname.getText().toString());

            // creates new date instance with values form map to post
            Date date = new Date(dateMap.get("year") - YEAR_OFFSET, dateMap.get("month"), dateMap.get("dayOfMonth"), dateMap.get("hourOfDay"), dateMap.get("minute"));

            if(date.compareTo(today) < 0)
            {
                throw new EmptyStackException();
            }
            newClass.setDate(date);

            newClass.setCost(Double.parseDouble(etCost.getText().toString()));

            newClass.setCategory(categorySelected);

            newClass.setTeacher(ParseUser.getCurrentUser());

            newClass.setLocationName(locationName);

            newClass.setLocation(location);


            ArrayList<String> students = new ArrayList<>();
            newClass.setStudents(students);


            newClass.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getActivity(), "Class was made", Toast.LENGTH_SHORT).show();
                        getAndSetSkillsArray(category);
                        // create new fragment to use
                        Fragment home = new HomeFragment();
                        // transaction on current activity
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.flContainer, home);
                        transaction.addToBackStack(null);
                        // Commit the transaction
                        transaction.commit();
                    } else {

                        Toast.makeText(getActivity(), "Class wasn't made", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
        catch (EmptyStackException e) {
            Toast.makeText(getActivity(), "The class you want to post is in the past", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(getActivity(), "One or more items were not filled in for this class to be made", Toast.LENGTH_SHORT).show();
        }

    }

    private void getAndSetSkillsArray(String category) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ArrayList<Integer> skillsData = (ArrayList<Integer>) currentUser.get("skillsData");
        skillsData = updateSkillsArray(skillsData, category);

        currentUser.put("skillsData", skillsData);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.i("NewClassActivity", "SkillsData array successfully saved");
                }else{
                    e.printStackTrace();
                }
            }
        });
    }


    private void findAllViews(View v) {

        etClassname = v.findViewById(R.id.etClassname);
        btnDate = v.findViewById(R.id.btnDate);
        etLocation = v.findViewById(R.id.etLocation);
        etDescription = v.findViewById(R.id.etDescription);
      //  spinCategory = v.findViewById(R.id.categoryPicker);
        etCost = v.findViewById(R.id.etCost);
        btSubmit = v.findViewById(R.id.btSubmit);
        ivClassImage = v.findViewById(R.id.ivClassImage);
        btnTime = v.findViewById(R.id.btnTime);
        categoryPicker = (NumberPicker) v.findViewById(R.id.categoryPicker);
        subCategoryPicker = (NumberPicker) v.findViewById(R.id.subCategoryPicker);

    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data != null) && (requestCode == PICK_PHOTO_CODE)) {
            if (resultCode == RESULT_OK) {
                photoUri = data.getData();
                //  photoFile = getPhotoFileUri(photoFileName);
                // Do something with the photo based on Uri

               /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri fileProvider = FileProvider.getUriForFile(NewClassActivity.this, "com.codepath.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                // So as long as the result is not null, it's safe to use the intent.
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Start the image capture intent to take photo
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }*/


                Bitmap selectedImage = null;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Load the selected image into a preview
                ImageView ivPreview = v.findViewById(R.id.ivClassImage);
                ivPreview.setImageBitmap(selectedImage);
            } else {
                // Result was a failure
                Toast.makeText(getActivity(), "No picture was selected.", Toast.LENGTH_SHORT).show();
            }


        }
        if ((data != null) && (requestCode == AUTOCOMPLETE_REQUEST_CODE) && (resultCode == RESULT_OK)) {

            Place place = Autocomplete.getPlaceFromIntent(data);
            locationName = place.getName();
            etLocation.setText(locationName);
            LatLng latLng = place.getLatLng();
            location = new ParseGeoPoint(latLng.latitude, latLng.longitude);
        }
    }

    private File getPhotoFileUri(String photoFileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);
        return file;
    }

    private void launchIntent() {
        // Specify the types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(getActivity());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }


    private ArrayList<Integer> updateSkillsArray(ArrayList<Integer> skillsData, String category) {
        switch (category){
            case ("Culinary"): {
                skillsData.set(0, skillsData.get(0) + 1);
                break;
            }
            case ("Education"): {
                skillsData.set(1, skillsData.get(1) + 1);
                break;
            }
            case ("Fitness"): {
                skillsData.set(2, skillsData.get(2) + 1);
                break;
            }
            case ("Arts/Crafts"): {
                skillsData.set(3, skillsData.get(3) + 1);
                break;
            }
            case ("Other"): {
                skillsData.set(4, skillsData.get(4) + 1);
                break;
            }
            default: {
                break;
            }
        }
        return skillsData;
    }

}
