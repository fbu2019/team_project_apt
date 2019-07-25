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
    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public int getAverageRating() {return getInt(KEY_AVERAGE_RATING); }
    public void setAverageRating(int i) { put(KEY_AVERAGE_RATING,i); }

    public int getNumRatings() {return getInt(KEY_NUM_RATINGS); }
    public void setNumRatings(int i) { put(KEY_NUM_RATINGS, i); }

    //TODO - complete rest
    // public HashMap<String, Integer> getUsersWhoRated() {return get(KEY_USER_RATINGS); }

}
