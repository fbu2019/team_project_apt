package com.example.skillshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ClassAttendeesActivity extends AppCompatActivity {

    Workshop currentWorkshop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_attendees);

        getStudentArray();

        //TODO set up recycler view
        //TODO set up teacher adapter


    }

    private void getStudentArray() {

        currentWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));
        ArrayList<String> students = (ArrayList<String>) currentWorkshop.getStudents();

        for (int i = 0; i < students.size(); i++ ){
            final ParseQuery<ParseUser> userQuery = ParseUser.getQuery().whereMatches("objectId", students.get(i));
            userQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> attendee, ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < attendee.size(); i++) {
                            String firstName = attendee.get(i).get("firstName").toString();
                            Log.i("classAttendees", firstName);

                            // Workshop workshopItem = objects.get(i);
                          //  mWorkshops.add(workshopItem);
                           // classAdapter.notifyItemInserted(mWorkshops.size()-1);
                        }
                    } else {
                        e.printStackTrace();
                    }

                }
            });
        }

    }
}
