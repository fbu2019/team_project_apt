package com.example.skillshop.NavigationFragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.skillshop.LoginActivity;
import com.example.skillshop.NewClassActivity;
import com.example.skillshop.R;
import com.example.skillshop.SignupActivity;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;

import static java.security.AccessController.getContext;

public class ProfileFragment extends Fragment {

    TextView nameViewText;
    ImageView ivProfilePic;
    EditText zipcodeInput;
    Button submitZipcodeButton;
    Button uploadPhotoButton;
    Button logoutButton;

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;
    public final String APP_TAG = "ProfileFragment";
    public final static int AUTOCOMPLETE_REQUEST_CODE = 42;
    File photoFile;
    public String photoFileName = "photo.jpg";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_profile),container,false);
    }
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        final ParseUser user = ParseUser.getCurrentUser();
        final String username = user.getUsername();
        final String zipCode = (user.getString("zipCode"));

        nameViewText = view.findViewById(R.id.nameView);
        nameViewText.setText("Hello "+user.getString("firstName")+". Your current zipcode is "+zipCode+".");

        //  only attempts to display profile image if user has one
        ivProfilePic = view.findViewById(R.id.profilePicture);
        ParseFile profileImageFile = user.getParseFile("profilePicture");
        if (profileImageFile != null) {
            Glide.with(getContext()).load(profileImageFile.getUrl()).into(ivProfilePic);
            Log.i("Profile Frag", "There is a profile image");
        } else {
            ivProfilePic.setImageBitmap(null);
            Log.i("Profile Frag", "No profile image");
        }

        zipcodeInput = view.findViewById(R.id.etZipcode);
        submitZipcodeButton = view.findViewById(R.id.zipCodeModify);

        submitZipcodeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                final String newZipCode = zipcodeInput.getText().toString();

                if(newZipCode.length()!=5){
                    Toast.makeText(getContext(), "Zipcode entered must be correct length, "+newZipCode+" is now "+newZipCode.length(), Toast.LENGTH_LONG).show();
                }
                else {
                    user.put("zipCode", newZipCode);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                e.printStackTrace();
                                return;
                            } else {
                                nameViewText.setText("Hello " + username + ". Your current zipcode is " + newZipCode + ".");
                            }
                        }
                    });
                }
            }
        });

        uploadPhotoButton = view.findViewById(R.id.uploadPhoto);
        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
                //  user.put("profilePicture", photoFile);
            }
        });

        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Create a File reference to access to future access
        // photoFile = getPhotoFileUri(photoFileName);

        /*

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        */

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("ProfileFragment", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data != null) && (requestCode == PICK_PHOTO_CODE)){
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri

            Bitmap selectedImage = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Load the selected image into  preview
            ivProfilePic.setImageBitmap(selectedImage);

            //TODO: determine how to set image in database
            /*
            .saveInBackground(new SaveCallback() {
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

            */
        }

    }

}