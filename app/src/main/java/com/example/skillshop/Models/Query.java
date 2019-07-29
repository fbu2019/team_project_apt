package com.example.skillshop.Models;


import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.example.skillshop.Models.Workshop.KEY_CATEGORY;
import static com.example.skillshop.Models.Workshop.KEY_DATE;
import static com.example.skillshop.Models.Workshop.KEY_OBJECT_ID;
import static com.example.skillshop.Models.Workshop.KEY_STUDENTS;
import static com.example.skillshop.Models.Workshop.KEY_TEACHER;
import static com.example.skillshop.Models.Workshop.KEY_COST;
import static com.example.skillshop.Models.Workshop.KEY_LOCATION;


public class Query extends ParseQuery<Workshop> {

    public Query(){
        super(Workshop.class);
    }

    public Query getAllClasses() {
        return this;
    }

    public Query withItems() {
        include(KEY_TEACHER);
        return this;
    }

    public Query byLocation(ParseGeoPoint userLocation){
        whereNear(KEY_LOCATION, userLocation);
        return this;
    }


    public Query byCategory(ArrayList<String> categories) {
        whereContainedIn(KEY_CATEGORY, categories);
        return this;
    }

    public Query hasClasses(ArrayList<String> classes) {
        whereContainedIn(KEY_OBJECT_ID, classes);
        return this;
    }





    public Query byTimeOfClass() {
        addAscendingOrder(KEY_DATE);
        return this;
    }

    public Query byCost() {
        addAscendingOrder(KEY_COST);
        return this;
    }


    public Query getClassesTeaching(){
        whereEqualTo(KEY_TEACHER, ParseUser.getCurrentUser());
        return this;
    }


    public Query getClassesTaking(){
        List<String> list = Arrays.asList(ParseUser.getCurrentUser().getObjectId());

        whereContainedIn(KEY_STUDENTS,list);

        return this;
    }

    public Query getClassesNotTaking(){

        List<String> list = Arrays.asList(ParseUser.getCurrentUser().getObjectId());

        whereNotContainedIn(KEY_STUDENTS,list);

        return this;
    }

    public Query onDate(Long date){

        Date dateOfClass = new Date(date);

        Date lowerBound = new Date(dateOfClass.getYear(),dateOfClass.getMonth(),dateOfClass.getDate());
        Date upperBound = new Date(dateOfClass.getYear(),dateOfClass.getMonth(),dateOfClass.getDate()+1);

        whereGreaterThanOrEqualTo(KEY_DATE,lowerBound);
        whereLessThanOrEqualTo(KEY_DATE,upperBound);

        return this;
    }

}

