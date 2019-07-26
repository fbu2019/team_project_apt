package com.example.skillshop.DatabaseCommunication;

import android.app.Application;

import com.example.skillshop.Models.Message;
import com.example.skillshop.Models.Ratings;
import com.example.skillshop.Models.Workshop;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Workshop.class);
        ParseObject.registerSubclass(Ratings.class);
        ParseObject.registerSubclass(Message.class);


        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("skillshop")
                .clientKey("skillshop")
                .server("http://skillshop-fbu.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);

    }
}
