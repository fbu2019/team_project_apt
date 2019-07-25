package com.example.skillshop.Models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.parse.ParseClassName;

@ParseClassName("Ratings")
public class Ratings extends ParseObject {

    public final static String KEY_USER = "user";
    public final static String KEY_AVERAGE_RATING = "averageRating";
    public final static String KEY_NUM_RATINGS = "numRatings";
    public final static String KEY_USER_RATINGS = "userRatings";

    public String getUser() {return getString(KEY_USER); }

}
