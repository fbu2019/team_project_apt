package com.example.skillshop.Models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;


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


    public  String getName() {
        return getString(KEY_NAME);
    }
    public  String getDescription() {
        return getString(KEY_DESCRIPTION);
    }
    public  String getDate() {
        return getDate(KEY_DATE).toString();
    }
    public ParseGeoPoint getLocation() {return getParseGeoPoint(KEY_LOCATION); }
    public  String getLocationName() {
        return getString(KEY_LOCATION_NAME);
    }
    public Date getJavaDate(){
        return getDate(KEY_DATE);
    }
    public void setDate(Date date) {
        put(KEY_DATE,date);
    }
    public  Double getCost() {
        return getDouble(KEY_COST);
    }

    public  void setCost(Double cost) {
         put(KEY_COST,cost);
    }

    public void setName(String name) {
        put(KEY_NAME,name);
    }
    public void setDescription(String description) {
        put(KEY_DESCRIPTION,description);
    }
    public void setLocationName(String locationName) {
        put(KEY_LOCATION_NAME,locationName);
    }
    public void setLocation(ParseGeoPoint location)  {
        put(KEY_LOCATION,location);
    }
    public ParseUser getTeacher() {
        return getParseUser(KEY_TEACHER);
    }

    public void setTeacher(ParseUser user) {
        put(KEY_TEACHER,user);
    }

    public String getCategory() {
        return getString(KEY_CATEGORY);
    }

    public void setCategory(String category) {
        put(KEY_CATEGORY,category);
    }

    public Boolean isTeacher(){return (ParseUser.getCurrentUser() == getParseUser(KEY_TEACHER));}


    public Object getStudents() {
        return get(KEY_STUDENTS);
    }

    public void setStudents(ArrayList<String> students) {
        put(KEY_STUDENTS,students);
    }
}

