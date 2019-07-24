package com.example.skillshop.NavigationFragments.CalendarActivities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Date;
import java.util.List;

public class CalendarFragment extends Fragment {

    CalenderEvent calendarView;
    ArrayList<String> teachingDays;
    ArrayList<Date> teachingDates;
    ArrayList<String> takingDays;
    ArrayList<Date> overlapDays;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        calendarView = null;
        return inflater.inflate((R.layout.fragment_calendar),container,false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = null;
        calendarView = view.findViewById(R.id.calendarView);


         calendarView.clearDisappearingChildren();


        teachingDays = new ArrayList<>();
        teachingDates = new ArrayList<>();
        takingDays = new ArrayList<>();
        overlapDays = new ArrayList<>();


        //TODO figure out how to clear calendar every time it is opened




        populateCalendarClassesTaking();


        calendarView.initCalderItemClickCallback(new CalenderDayClickListener() {
            @Override
            public void onGetDay(DayContainerModel dayContainerModel) {

                if(dayContainerModel.getEvent() != null) {

                    Intent eventsToday = new Intent(getContext(), DaysEventsActivity.class);
                    Long time = dayContainerModel.getTimeInMillisecond();
                    eventsToday.putExtra("Date",time);
                    getContext().startActivity(eventsToday);
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
                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        Date date = new Date(workshopItem.getDate());
                        Event event = new Event(date.getTime(), "S",Color.RED);

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        takingDays.add(format.format(date));

                        calendarView.addEvent(event);

                    }

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
                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        Date date = new Date(workshopItem.getDate());

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        teachingDays.add(format.format(date));
                        teachingDates.add(date);

                        Event event = new Event(date.getTime(), "M",Color.BLUE);
                        calendarView.addEvent(event);

                        populateCalendarBoth();

                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public  void  populateCalendarBoth()
    {

        for(String takingDay : takingDays)
        {
            for(int i = 0; i < teachingDays.size();i++)
            {
                String teachingDay = teachingDays.get(i);
                if(teachingDay.equals(takingDay))
                {
                    Event event = new Event(teachingDates.get(i).getTime(), "SM",Color.MAGENTA);
                    calendarView.addEvent(event);
                }
            }
        }
    }





}
