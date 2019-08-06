package com.example.skillshop.NavigationFragments.Calendar;

import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DayFragmentHandler extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener  {



    WeekView mWeekView;
    ArrayList <Workshop> workshops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_fragment_handler);


        Bundle bundle = getIntent().getExtras();

        workshops = (ArrayList<Workshop>) bundle.get("workshops");




        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

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
            event = new WeekViewEvent(new Random().nextLong(), w.getName(), startTime, endTime);

            switch(w.getCategory())
            {
                case "Culinary":
                    event.setColor(getResources().getColor(R.color.color_palette_green));
                    break;
                case "Fitness":
                    event.setColor(getResources().getColor(R.color.color_palette_yellow));
                    break;
                case "Arts/Crafts":
                    event.setColor(getResources().getColor(R.color.color_palette_orange));
                    break;
                case "Education":
                    event.setColor(getResources().getColor(R.color.color_palette_light_blue2));
                    break;
                case "Other":
                    event.setColor(getResources().getColor(R.color.color_palette_light_grey));
                    break;

            }
            events.add(event);
        }

        return events;
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    public WeekView getWeekView() {
        return mWeekView;
    }
}
