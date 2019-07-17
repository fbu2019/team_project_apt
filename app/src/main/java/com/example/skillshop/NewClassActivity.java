package com.example.skillshop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.skillshop.Models.Workshop;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NewClassActivity extends AppCompatActivity {

    public static final String TAG = "NewClassActivity";

    TextView etClassname;
    TextView etDate;
    TextView etLocation;
    TextView etDescription;
    Spinner spinCategory;
    TextView etCost;
    ImageView ivClassImage;

    Button btSubmit;

    String classname;
    String date;
    String location;
    String description;
    String category;
    String cost;


    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);
        findAllViews();
        setSubmitListener();

        ivClassImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
    /*    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinCategory.setAdapter(adapter);
*/

    }

    private void setSubmitListener() {

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewContent();
                submitClass();
            }
        });
    }

    private void submitClass() {
        final Workshop newClass = new Workshop();

    }



    private void getViewContent() {
        classname = etClassname.getText().toString();
        String dateString = etDate.getText().toString();
      //  Toast.makeText(this, classname, Toast.LENGTH_LONG).show();
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy kk:mm").parse(dateString);
          //  String dateConverted = format(date);
           // Toast.makeText(this, dateConverted, Toast.LENGTH_LONG).show();

        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date.");
            e.printStackTrace();
        }

        location = etLocation.getText().toString();

        //   location;
        description = etDescription.getText().toString();
        //category = spinCategory.getText().toString();






        //  cost;


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
;
    }

    //TODO fix type issues
    //TODO send to parse




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
