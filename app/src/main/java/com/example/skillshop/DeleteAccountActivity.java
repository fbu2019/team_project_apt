package com.example.skillshop;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skillshop.ClassManipulationActivities.EditClassActivity;
import com.example.skillshop.LoginActivities.LoginActivity;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Ratings;
import com.example.skillshop.Models.Workshop;
import com.facebook.login.LoginManager;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class DeleteAccountActivity extends AppCompatActivity {

    private TextView tvWarning;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        tvWarning = findViewById(R.id.warningMessage);
        continueButton = findViewById(R.id.continueTODELETE);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //retrieve and delete classes user is teaching , must remove all classes from each user
               // deleteRating();
                Log.i("DeleteAccountActivity", "Reached onClick");
                deleteClassesTeaching();
                Log.i("DeleteAccountActivity", "Below");
                removeFromClassesTaking();
                Log.i("DeleteAccountActivity", "Below here");

                try {
                    removeUser();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void deleteRating() {

        Ratings.Query ratingParseQuery = new Ratings.Query();
        ratingParseQuery.getAllRatings().whereEqualTo("user", ParseUser.getCurrentUser());
        ratingParseQuery.findInBackground(new FindCallback<Ratings>() {
            @Override
            public void done(List<Ratings> objects, ParseException e) {

                if (e == null) {
                  Ratings currentUserRating = objects.get(0);
                    currentUserRating.setUser(null);

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void deleteClassesTeaching() {

        Query parseQuery = new Query();
        parseQuery.getAllClasses().withItems().byTimeOfClass().getClassesTeaching();

        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {

                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        try {
                            workshopItem.delete();
                        } catch (ParseException e1) {
                            e1.printStackTrace(); // attempts to delete
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void removeFromClassesTaking() {

        Query parseQuery = new Query();
        parseQuery.getAllClasses().getClassesTaking().withItems().byTimeOfClass();

        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {

                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        ArrayList<String> students = (ArrayList<String>) workshopItem.getStudents();

                        //iterate through, check for current user, and remove user
                        for(int j = 0; j<students.size(); j++){

                            Log.e("DeleteAccount", "Student id "+students.get(j));
                            Log.e("DeleteAccount", "User id "+ParseUser.getCurrentUser().getObjectId());

                            if(students.get(j).equals(ParseUser.getCurrentUser().getObjectId())){
                                students.remove(j);
                                workshopItem.setStudents(students);
                            }
                        }

                        workshopItem.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null)
                                {
                                    Log.e("DeleteAccount", "successful update");
                                    Toast.makeText(DeleteAccountActivity.this, "Changes have been saved (changes may take a while to be reflected in the app)", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else
                                {
                                    Log.e("DeleteAccount", "failure update");
                                    Toast.makeText(DeleteAccountActivity.this, "Error saving changes", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void removeUser() throws ParseException {
        Log.e("DeleteAccount", "reached removeUser");

        ParseUser user = ParseUser.getCurrentUser();
        user.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Log.e("DeleteAccount", "Deleted user");
                    //user deleted
                } else {
                    Log.e("DeleteAccount", "Failed to delete user");
                    e.printStackTrace();
                }
            }
        });

        Log.i("DeleteAccount", ParseUser.getCurrentUser().getUsername());

        ParseUser.logOutInBackground();

        LoginManager.getInstance().logOut(); // logs user out of facebook TODO - look into actually deleting fb user info
        // Deletes user only the first time

        Intent i = new Intent(DeleteAccountActivity.this, LoginActivity.class);
        startActivity(i);

    }

}
