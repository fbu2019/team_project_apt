package com.example.skillshop.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;


import java.util.Date;


@ParseClassName("Workshop")
public class Workshop extends ParseObject {

    public final static String KEY_NAME = "name";
    public final static String KEY_DESCRIPTION = "description";
    public final static String KEY_DATE = "date";
    public final static String KEY_LOCATION = "location";
    public final static String KEY_MENTOR = "mentor";
    public final static String KEY_CREATED_AT = "createdAt";
    public final static String KEY_COST = "cost";


    public  String getName() {
        return getString(KEY_NAME);
    }
    public  String getDescription() {
        return getString(KEY_DESCRIPTION);
    }
    public  String getDate() {
        return getDate(KEY_DATE).toString();
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

    public ParseUser getTeacher() {
        return getParseUser("mentor");
    }

    public void setTeacher(ParseUser user) {
         put("mentor",user);
    }

}

