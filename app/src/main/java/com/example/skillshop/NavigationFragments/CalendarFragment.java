package com.example.skillshop.NavigationFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
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

import android.widget.Toast;


public class CalendarFragment extends Fragment {



    TextView textView;
    com.applandeo.materialcalendarview.CalendarView  calendarView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_calendar),container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = view.findViewById(R.id.textView);



        final List<EventDay> events = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, R.drawable.ic_add));

        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setEvents(events);

        final List<EventDay> mEventDays = new ArrayList<>();



        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                Toast.makeText(getContext(), eventDay.getCalendar().getTime().toString(), Toast.LENGTH_SHORT).show();

//                mEventDays.add(eventDay);
                mEventDays.add(new EventDay(eventDay.getCalendar()));

                calendarView.setEvents(mEventDays);



            }
        });



    }
    public void populateCalendarClassesTaking() {

        Query parseQuery = new Query();
        parseQuery.getAllClasses().withItems().byTimeOfClass().getClassesTaking();

        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        Date date = new Date(workshopItem.getDate());
                        textView.setText(textView.getText().toString()+date.toString());
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}
