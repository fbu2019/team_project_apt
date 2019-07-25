package com.example.skillshop.Models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import static com.example.skillshop.Models.Workshop.KEY_TEACHER;

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

    //  public Ratings getParseRating(String key) { return getParseObject(key); }

    // public Ratings getByUser(String userFBiD){ return}

    //  Can return different things to feed depending on specifications
    public static class Query extends ParseQuery<Ratings> {
        public Query() {
            super(Ratings.class);
        }

        public Query getAllRatings() {
            return this;
        }
        

        public Query withUser() {
            include(KEY_USER);
            return this;
        }


    }

}
