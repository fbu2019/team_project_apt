package com.example.skillshop.NavigationFragments.Calendar;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewLoader;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class CalendarFragment extends Fragment {

    CalendarView calendarView;

    ArrayList<Workshop> taking;
    ArrayList<Workshop> teaching;
    ArrayList<Workshop> allClasses;

    WeekView mWeekView;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        calendarView = null;
        return inflater.inflate((R.layout.activity_calendar_fragment_handler),container,false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        calendarView = view.findViewById(R.id.calendarView);
        taking = new ArrayList<>();
        populateAllClasses();
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {


                if(eventDay.getImageDrawable() != null) {

                    populateClassesTaking();
                }


            }
        });

    }

    public void populateClassesTaking() {


        Query parseQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        parseQuery.getAllClasses().withItems().byTimeOfClass();

        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {
                    startDay((ArrayList<Workshop>) objects);
                } else {
                    e.printStackTrace();
                }
            }
        });


    }

    public void startDay(ArrayList<Workshop> workshops)
    {
        Intent eventsToday = new Intent(getContext(), DayFragmentHandler.class);
        eventsToday.putExtra("workshops", workshops);
        startActivity(eventsToday);
    }



    public List<WeekViewEvent> populateAllClasses()
    {
        List<WeekViewEvent> events = new ArrayList<>();
        Query parseQuery = new Query();
        parseQuery.getAllClasses().withItems().byTimeOfClass();
        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {

                    for(int i  = 0; i < objects.size();i++)
                    {
                        Workshop workshop = objects.get(i);

                        WeekViewEvent event = new WeekViewEvent();
                        event.setId(new Random().nextLong());

                        Date date = new Date(workshop.getDate());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        event.setStartTime(cal);
                        allClasses = (ArrayList<Workshop>) objects;
                        populateCalendarClassesTaking();
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });
        return events;

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

        for(Workshop wAll : allClasses)
        {
            Date teachingDate = wAll.getJavaDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(teachingDate);
            events.add(new EventDay(calendar, R.drawable.ic_new_class));

        }


        calendarView.setEvents(events);

    }


}
