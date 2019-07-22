package com.example.skillshop.NavigationFragments;

import android.app.usage.UsageEvents;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.applikeysolutions.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.example.skillshop.ClassDetailsActivity;
import com.example.skillshop.DaysEventsActivity;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.skyhope.eventcalenderlibrary.CalenderEvent;
import com.skyhope.eventcalenderlibrary.listener.CalenderDayClickListener;
import com.skyhope.eventcalenderlibrary.model.DayContainerModel;
import com.skyhope.eventcalenderlibrary.model.Event;

import org.parceler.Parcels;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CalendarFragment extends Fragment {

    CalenderEvent calendarView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_calendar),container,false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = view.findViewById(R.id.calendarView);

        populateCalendarClassesTaking();

        calendarView.initCalderItemClickCallback(new CalenderDayClickListener() {
            @Override
            public void onGetDay(DayContainerModel dayContainerModel) {

                Intent eventsToday = new Intent(getContext(), DaysEventsActivity.class);
                getContext().startActivity(eventsToday);

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
                        Event event = new Event(date.getTime(), "TA",Color.RED);
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

                        Event event = new Event(date.getTime(), "TE",Color.BLUE);
                        calendarView.addEvent(event);

                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }





}
