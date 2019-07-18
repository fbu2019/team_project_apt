package com.example.skillshop.Models;


import com.parse.ParseQuery;
import com.parse.ParseUser;

import static com.example.skillshop.Models.Workshop.KEY_CREATED_AT;
import static com.example.skillshop.Models.Workshop.KEY_DATE;
import static com.example.skillshop.Models.Workshop.KEY_MENTOR;
import static com.example.skillshop.Models.Workshop.KEY_STUDENTS;

public class Query extends ParseQuery<Workshop> {

    public Query(){
        super(Workshop.class);
    }

    public Query getAllClasses() {
        return this;
    }

    public Query withItems() {
        include(KEY_MENTOR);
        include(KEY_STUDENTS);
        return this;
    }

    public Query byTimeMade() {
        addDescendingOrder(KEY_CREATED_AT);
        return this;
    }

    public Query byTimeOfClass() {
        addDescendingOrder(KEY_DATE);
        return this;
    }

    // TODO login as user with facebook login as parselogin
    public Query getClassesTeaching(){
        whereEqualTo("mentor", ParseUser.getCurrentUser());
        return this;
    }

// TODO be able to sign up for classes
//    public Query getClassesTaking(){
//        whereContainedIn('attendees',)
//        return this;
//    }




}

