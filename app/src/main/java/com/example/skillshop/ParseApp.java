package com.example.skillshop;

import android.app.Application;

import com.parse.Parse;

public class ParseApp extends Application {


    // link to the Post class



    @Override
    public void onCreate() {
        super.onCreate();
//       ParseObject.registerSubclass(Class.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("moisestrejo")
                .clientKey("2952216")
                .server("http://skillshop2019.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }



}
