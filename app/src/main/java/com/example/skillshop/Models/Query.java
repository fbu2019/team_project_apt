package com.example.skillshop.Models;


import com.parse.ParseQuery;

public class Query extends ParseQuery<Class> {

    public Query(){
        super(Class.class);
    }

    public Query getAllClasses() {
        return this;
    }

    public Query withTeacher() {
        include("mentor");
        return this;
    }

    // TODO login as user with facebook login as parselogin
//    public Query getClassesTeaching(){
//        whereEqualTo("teacher", ParseUser.getCurrentUser());
//        return this;
//    }
//
//    public Query getClassesTaking(){
//        whereContainedIn('attendees',)
//        return this;
//    }




}

