package com.example.skillshop.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Class")
public class Class extends ParseObject {

    public final static String KEY_NAME = "name";
    public final static String KEY_DESCRIPTION = "description";
    public final static String KEY_DATE = "date";
    public final static String KEY_LOCATION = "location";
    public final static String KEY_TEACHER = "teacher";


    public  String getName() {
        return getString(KEY_NAME);
    }
    public  String getDescription() {
        return getString(KEY_DESCRIPTION);
    }
    public  String getDate() {
        return getDate(KEY_DATE).toString();
    }

    public void setName(String name) {
        put(KEY_NAME,name);
    }
    public void setDescription(String description) {
        put(KEY_DESCRIPTION,description);
    }

    public ParseUser getTeacher() {
        return getParseUser("mentor");
    }
    
}

