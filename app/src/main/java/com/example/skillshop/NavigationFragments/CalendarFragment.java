package com.example.skillshop.NavigationFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.settings.appearance.ConnectedDayIconPosition;
import com.applikeysolutions.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CalendarFragment extends Fragment {

    CalendarView calendarView;
    ConnectedDays connectedDaysTaking;
    ConnectedDays connectedDaysTeaching;
    ConnectedDays connectedDaysOverlap;




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



        calendarView.setConnectedDayIconPosition(ConnectedDayIconPosition.TOP);

    }

    public void displayColorPatterns()
    {
        Set<Long> days = new TreeSet<>();
        connectedDaysOverlap = new ConnectedDays(days, Color.MAGENTA, Color.BLACK, Color.BLACK);

        Iterator<Long> dayTakingIterator = connectedDaysTaking.getDays().iterator();

        while(dayTakingIterator.hasNext())
        {

            Long day = dayTakingIterator.next();
            Date takingDay = new Date(day);

            Iterator<Long> dayTeachingIterator = connectedDaysTeaching.getDays().iterator();

            while(dayTeachingIterator.hasNext())
            {
                Date teachingDay = new Date(dayTeachingIterator.next());

                if(takingDay.getDate() == teachingDay.getDate() && takingDay.getMonth() == teachingDay.getMonth() && takingDay.getYear() == teachingDay.getYear())
                {
                    days.add(day);
                }
            }

        }

        connectedDaysOverlap = new ConnectedDays(days, Color.MAGENTA, Color.BLACK, Color.BLACK);
        //Connect days to calendar
        calendarView.addConnectedDays(connectedDaysOverlap);


    }



    public void populateCalendarClassesTaking()
    {
        Query parseQuery = new Query();
        parseQuery.getAllClasses().withItems().byTimeOfClass().getClassesTaking();

        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {


                    Set<Long> days = new TreeSet<>();

                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        Date date = new Date(workshopItem.getDate());
                        days.add(date.getTime());

                    }
                    connectedDaysTaking = new ConnectedDays(days, Color.BLUE, Color.BLACK, Color.BLACK);
                    //Connect days to calendar
                    calendarView.addConnectedDays(connectedDaysTaking);
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


                    Set<Long> days = new TreeSet<>();

                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        Date date = new Date(workshopItem.getDate());
                        days.add(date.getTime());

                    }


                    connectedDaysTeaching = new ConnectedDays(days, Color.RED, Color.BLACK, Color.BLACK);

                    //Connect days to calendar
                    calendarView.addConnectedDays(connectedDaysTeaching);

                    displayColorPatterns();

                } else {
                    e.printStackTrace();
                }
            }
        });
    }





}
