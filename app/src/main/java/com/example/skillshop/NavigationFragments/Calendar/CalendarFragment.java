package com.example.skillshop.NavigationFragments.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarFragment extends Fragment {

    CalendarView calendarView;

    ArrayList<Workshop> taking;
    ArrayList<Workshop> teaching;
    ArrayList<Workshop> allClasses;




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



        populateAllClasses();



        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {


                if(eventDay.getImageDrawable() != null) {
                    Intent eventsToday = new Intent(getContext(), DayFragmentHandler.class);
                    Long time = eventDay.getCalendar().getTimeInMillis();
                    eventsToday.putExtra("Date", time);
                    startActivity(eventsToday);
                }


            }
        });



    }
    public void populateAllClasses()
    {
        Query parseQuery = new Query();
        parseQuery.getAllClasses().withItems().byTimeOfClass();
        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {
                    allClasses = (ArrayList<Workshop>) objects;
                    populateCalendarClassesTaking();

                } else {
                    e.printStackTrace();
                }
            }
        });
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