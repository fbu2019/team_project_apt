package com.example.skillshop.NavigationFragments.Calendar;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.skillshop.ClassDescription.ClassDetailsActivity;
import com.example.skillshop.ClassDescription.EditClassActivity;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CalendarDayViewFragment extends Fragment implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {


    WeekView mWeekView;
    ArrayList<Workshop> workshops;
    HashMap<String,Workshop> map;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate((R.layout.fragment_calendar_day_view),container,false);


    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();

        workshops = (ArrayList<Workshop>) bundle.get("workshops");


        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) view.findViewById(R.id.weekView);

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        map = new HashMap<>();
        for(Workshop w : workshops)
        {
            map.put(w.getName(),w);
        }

        mWeekView.setNumberOfVisibleDays(2);

    }



    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        Calendar startTime;
        Calendar endTime;
        WeekViewEvent event;

        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        for(Workshop w : workshops)
        {
            Date d = new Date(w.getDate());
            if(newMonth-1 != d.getMonth() && newYear != d.getYear())
            {
                continue;
            }
            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, d.getHours());
            startTime.set(Calendar.DATE, d.getDate());
            startTime.set(Calendar.MINUTE, d.getMinutes());
            startTime.set(Calendar.MONTH, newMonth - 1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR, 1);
            endTime.set(Calendar.MONTH, newMonth - 1);

            SimpleDateFormat format = new SimpleDateFormat("h:mm a");

            Date end = new Date(w.getDate());
            end.setHours(end.getHours()+1);
            String display = String.format("\t%s\n",w.getName(),format.format(d),format.format(end));

            event = new WeekViewEvent(new Random().nextLong(), display, startTime, endTime);

            String lower = String.format("\t%s - %s",format.format(d),format.format(end));
            event.setLocation(lower);

            switch(w.getCategory())
            {
                case "Culinary":
                    event.setColor(getResources().getColor(R.color.calendar_culinary));
                    break;
                case "Fitness":
                    event.setColor(getResources().getColor(R.color.calendar_fitness));
                    break;
                case "Arts/Crafts":
                    event.setColor(getResources().getColor(R.color.calendar_arts));
                    break;
                case "Education":
                    event.setColor(getResources().getColor(R.color.calendar_education));
                    break;
                case "Other":
                    event.setColor(getResources().getColor(R.color.calendar_other));
                    break;

            }
            events.add(event);
        }

        return events;
    }



    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

        String display = event.getName();

        String name = display.substring(1,display.indexOf("\n"));

        Workshop target = map.get(name);
        final Intent editClassIntent = new Intent(getContext(), ClassDetailsActivity.class);
        //pass in class that was selected
        editClassIntent.putExtra(Workshop.class.getSimpleName(), Parcels.wrap(target));
        getActivity().startActivity(editClassIntent);
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

        String display = event.getName();

        String name = display.substring(1,display.indexOf("\n"));

        Workshop target = map.get(name);
        final Intent editClassIntent = new Intent(getContext(), ClassDetailsActivity.class);
        //pass in class that was selected
        editClassIntent.putExtra(Workshop.class.getSimpleName(), Parcels.wrap(target));
        getActivity().startActivity(editClassIntent);
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
    }



}
