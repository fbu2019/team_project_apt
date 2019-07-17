package com.example.skillshop.Models;


import com.parse.ParseQuery;

import static com.example.skillshop.Models.Class.KEY_CREATED_AT;
import static com.example.skillshop.Models.Class.KEY_DATE;
import static com.example.skillshop.Models.Class.KEY_MENTOR;

public class Query extends ParseQuery<Class> {

    public Query(){
        super(Class.class);
    }

    public Query getAllClasses() {
        return this;
    }

    public Query withTeacher() {
        include(KEY_MENTOR);
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

