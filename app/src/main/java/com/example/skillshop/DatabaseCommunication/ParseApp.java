package com.example.skillshop.DatabaseCommunication;

import android.app.Application;

import com.example.skillshop.Models.Workshop;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Workshop.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("skillshop")
                .clientKey("skillshop")
                .server("http://skillshop2019.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);

    }
}
