package com.example.skillshop.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Class")
public class Class extends ParseObject {

    public final static String KEY_NAME = "name";
    public final static String KEY_DESCRIPTION = "description";

    public  String getName() {
        return getString(KEY_NAME);
    }
    public  String getDescription() {
        return getString(KEY_DESCRIPTION);
    }
    public void setName(String name) {
        put(KEY_NAME,name);
    }
    public void setDescription(String description) {
        put(KEY_DESCRIPTION,description);
    }


}
