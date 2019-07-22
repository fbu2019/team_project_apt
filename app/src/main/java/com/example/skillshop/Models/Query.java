package com.example.skillshop.Models;


import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

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


    public Query getClassesTeaching(){
        whereEqualTo(KEY_MENTOR, ParseUser.getCurrentUser());
        return this;
    }


    public Query getClassesTaking(){

        whereEqualTo(KEY_STUDENTS,ParseUser.getCurrentUser());

        return this;
    }

    public Query getClassesNotTaking(){

        whereNotEqualTo(KEY_STUDENTS,ParseUser.getCurrentUser());

        return this;
    }

    public Query onDate(Long date){

        Date dateOfClass = new Date(date);
        whereEqualTo(KEY_DATE,dateOfClass);

        return this;
    }




}

