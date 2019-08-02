package com.example.skillshop.Models;

import android.media.Rating;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;


@ParseClassName("Workshop")
public class Workshop extends ParseObject {

    public final static String KEY_NAME = "name";
    public final static String KEY_DESCRIPTION = "description";
    public final static String KEY_DATE = "date";
    public final static String KEY_LOCATION_NAME = "locationName";
    public final static String KEY_LOCATION = "location";
    public final static String KEY_TEACHER = "teacher";
    public final static String KEY_CREATED_AT = "createdAt";
    public final static String KEY_COST = "cost";
    public final static String KEY_CATEGORY = "category";
    public static final String KEY_STUDENTS = "students";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_INSTRUCTOR_RATING = "instructorRating";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_SUB_CATEGORY = "subCategory";

    //  Name Methods
    public  String getName() {
        return getString(KEY_NAME);
    }
    public void setName(String name) {
        put(KEY_NAME,name);
    }

    //  Date Methods
    public  String getDate() {
        return getDate(KEY_DATE).toString();
    }
    public void setDate(Date date) {
        put(KEY_DATE,date);
    }

    public Date getJavaDate(){
        return getDate(KEY_DATE);
    }


    //  Location Methods
    public  String getLocationName() {
        return getString(KEY_LOCATION_NAME);
    }
    public void setLocationName(String locationName) {
        put(KEY_LOCATION_NAME,locationName);
    }

    public ParseGeoPoint getLocation() {return getParseGeoPoint(KEY_LOCATION); }
    public void setLocation(ParseGeoPoint location)  {
        put(KEY_LOCATION,location);
    }

    //  Cost Methods
    public  Double getCost() {
        return getDouble(KEY_COST);
    }
    public  void setCost(Double cost) {
        put(KEY_COST,cost);
    }

    //  Description Methods
    public  String getDescription() {
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String description) {
        put(KEY_DESCRIPTION,description);
    }

    //  Teacher Methods
    public ParseUser getTeacher() {
        return getParseUser(KEY_TEACHER);
    }
    public void setTeacher(ParseUser user) {
        put(KEY_TEACHER,user);
    }
    public Boolean isTeacher(){return (ParseUser.getCurrentUser().getObjectId().equals(getParseUser(KEY_TEACHER).getObjectId()));}

    //  Rating Methods
    public void setInstructorRating(Ratings instructorRating) {put(KEY_INSTRUCTOR_RATING, instructorRating); }

    //  Student Methods
    public Object getStudents() {
        return get(KEY_STUDENTS);
    }
    public void setStudents(ArrayList<String> students) {
        put(KEY_STUDENTS,students);
    }

    //  Category Methods
    public String getCategory() {
        return getString(KEY_CATEGORY);
    }
    public void setCategory(String category) {
        put(KEY_CATEGORY,category);
    }

    // Image Methods
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile image) {
        put(KEY_IMAGE,image);
    }

    //  Subcategory Methods
    public String getSubcategory() {
        return getString(KEY_SUB_CATEGORY);
    }
    public void setSubCategory(String subCategory) {
        put(KEY_CATEGORY,subCategory);
    }

}

