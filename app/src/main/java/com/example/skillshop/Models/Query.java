package com.example.skillshop.Models;



import com.parse.ParseQuery;

public class Query extends ParseQuery<Class> {

    public Query(){
        super(Class.class);
    }

    public Query getAllClasses() {
        return this;
    }


}

