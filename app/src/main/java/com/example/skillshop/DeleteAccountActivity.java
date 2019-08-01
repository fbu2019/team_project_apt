package com.example.skillshop;

import android.app.Activity;
import android.content.Intent;
import android.media.Rating;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skillshop.ClassManipulationActivities.EditClassActivity;
import com.example.skillshop.LoginActivities.LoginActivity;
import com.example.skillshop.LoginActivities.SignupActivity;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Ratings;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.NavigationFragments.ClassesActivities.ClassesTakingFragment;
import com.example.skillshop.NavigationFragments.FragmentHandler;
import com.example.skillshop.NavigationFragments.HomeFragment;
import com.facebook.login.LoginManager;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeleteAccountActivity extends AppCompatActivity {

    private TextView tvWarning;
    private Button continueButton;

    FragmentManager fragmentManager;
    private ParseUser currentUser;
    private String fbID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        tvWarning = findViewById(R.id.warningMessage);
        continueButton = findViewById(R.id.continueTODELETE);


        fragmentManager = getSupportFragmentManager();

        currentUser = ParseUser.getCurrentUser();
        fbID = currentUser.getUsername();

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentManager.beginTransaction().addToBackStack(null).commit();

                removeRatings();

            }
        });

    }

    private void removeRatings() {

        //TODO - query to retrieve all ratings with user ratings
        Ratings.Query ratingParseQuery = new Ratings.Query();
        ratingParseQuery.getAllRatings().withUserRatings();

        ratingParseQuery.findInBackground(new FindCallback<Ratings>() {
            @Override
            public void done(List<Ratings> objects, ParseException e) {

                if (e == null) {
                    if (objects.size() > 0) {
                        for (int i = 0; i < objects.size(); i++) {

                            Ratings currentRating = objects.get(i);
                            HashMap<String, Integer> userRatings = currentRating.getUserRatings();

                            if (userRatings.size() > 0) {

                                String userKey = fbID;

                                if (userRatings.containsKey(userKey) && userRatings.get(userKey) != null) {
                                    Log.e("DeleteAccount", "User ratings " + userRatings.get(userKey));
                                    if (userRatings.get(userKey) > 0) {
                                        int rating = userRatings.get(userKey);

                                        //TODO - fix this error
                                        currentRating.setSumRatings(currentRating.getSumRatings() - rating);
                                        currentRating.setNumRatings(currentRating.getNumRatings() - 1);

                                        if(currentRating.getNumRatings()>0) {
                                            currentRating.setAverageRating(currentRating.getSumRatings() / currentRating.getNumRatings());
                                        } else {
                                            currentRating.setAverageRating(0);
                                        }

                                        userRatings.remove(currentUser.getUsername());
                                    }

                                    currentRating.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Log.e("DeleteAccount", "Rating successfully updated");
                                                // Toast.makeText(DeleteAccountActivity.this, "Changes have been saved (changes may take a while to be reflected in the app)", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Log.e("DeleteAccount", "Failure to update rating");
                                                Toast.makeText(DeleteAccountActivity.this, "Error saving changes", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            }
                        }

                        deleteRating();
                        removeFromClassesTaking();
                    }
                } else {
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

                    if (objects.size() > 0) {
                        Ratings currentUserRating = objects.get(0);
                        try {
                            currentUserRating.delete();
                            Log.i("DeleteAccount", "User's rating has been deleted");
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
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
                    try {
                        removeUser();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
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
                        for (int j = 0; j < students.size(); j++) {

                            Log.e("DeleteAccount", "Student id " + students.get(j));
                            Log.e("DeleteAccount", "User id " + ParseUser.getCurrentUser().getObjectId());

                            if (students.get(j).equals(ParseUser.getCurrentUser().getObjectId())) {
                                students.remove(j);
                                workshopItem.setStudents(students);
                            }
                        }

                        workshopItem.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.e("DeleteAccount", "successful update");
                                    Toast.makeText(DeleteAccountActivity.this, "Changes have been saved (changes may take a while to be reflected in the app)", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Log.e("DeleteAccount", "failure update");
                                    Toast.makeText(DeleteAccountActivity.this, "Error saving changes", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    deleteClassesTeaching();

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void removeUser() throws ParseException {
        Log.e("DeleteAccount", "reached removeUser");

        ParseUser user = ParseUser.logIn(fbID,fbID);
        Log.i("DeleteAccount", ParseUser.getCurrentUser().getUsername());

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
        // AUTHENTICATION ERROR

        Intent i = new Intent(DeleteAccountActivity.this, LoginActivity.class);
        startActivity(i);

    }


    private void login(String username, String password) {

        Log.i("LoginActivity", "Reached login method");
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("DeleteActivity", "Login successful");
                    final Intent intent = new Intent(DeleteAccountActivity.this, FragmentHandler.class);
                    Log.i("DeleteActivity", "Reached login success");
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("LoginActivity", "Login failure");
                    e.printStackTrace();

                    //  continues to sign up activity if does not recognize facebook user
                    Intent main = new Intent(DeleteAccountActivity.this, SignupActivity.class);
                    startActivity(main);
                    finish();
                }
            }
        });
    }
}

//TODO - let ruth-ann / moises know that when a user is deleted it cannot modify who another user is following... should add checks for if null in displaying who ur following