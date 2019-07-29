package com.example.skillshop.NavigationFragments.CalendarActivities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.example.skillshop.NavigationFragments.CalendarActivities.DaysEventsActivity;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.skyhope.eventcalenderlibrary.CalenderEvent;
import com.skyhope.eventcalenderlibrary.listener.CalenderDayClickListener;
import com.skyhope.eventcalenderlibrary.model.DayContainerModel;
import com.skyhope.eventcalenderlibrary.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarFragment extends Fragment {

    CalendarView calendarView;

    ArrayList<Workshop> taking;
    ArrayList<Workshop> teaching;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        calendarView = null;
        return inflater.inflate((R.layout.fragment_calendar),container,false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        calendarView = view.findViewById(R.id.calendarView);

        taking = new ArrayList<>();



        populateCalendarClassesTaking();



    }




    public void populateCalendarClassesTaking()
    {
        Query parseQuery = new Query();
        parseQuery.getAllClasses().withItems().byTimeOfClass().getClassesTaking();
        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {
                    taking = (ArrayList<Workshop>) objects;
                    populateCalendarClassesTeaching();

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void populateCalendarClassesTeaching()
    {
        Query parseQuery = new Query();
        parseQuery.getAllClasses().withItems().byTimeOfClass().getClassesTeaching();
        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {
                    teaching = (ArrayList<Workshop>) objects;
                    populateCalendarBoth();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public  void  populateCalendarBoth()
    {
        List<EventDay> events = new ArrayList<>();


        ArrayList<Date> overlap = new ArrayList<>();

        for(Workshop wTaking : taking)
        {
            Date takingDate = wTaking.getJavaDate();
            for(Workshop wTeaching : teaching) {
                Date teachingDate = wTeaching.getJavaDate();
                if(takingDate.getDate() == teachingDate.getDate() &&
                        takingDate.getDate() == teachingDate.getDate()&&
                            takingDate.getDate() == teachingDate.getDate())
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(takingDate);
                    events.add(new EventDay(calendar, R.drawable.ic_both));
                }

            }
        }


        for(Workshop wTaking : taking)
        {
            Date takingDate = wTaking.getJavaDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(takingDate);
            events.add(new EventDay(calendar, R.drawable.ic_taking_note));

        }

        for(Workshop wTeaching : teaching)
        {
            Date teachingDate = wTeaching.getJavaDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(teachingDate);
            events.add(new EventDay(calendar, R.drawable.ic_teaching_note));

        }
        calendarView.setEvents(events);

    }


}
