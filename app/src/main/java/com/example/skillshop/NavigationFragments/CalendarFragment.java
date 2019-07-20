package com.example.skillshop.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.skillshop.NewClassActivity;
import com.example.skillshop.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_calendar),container,false);
    }


    CalendarView calendarView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = view.findViewById(R.id.calendarView);
        final List<Calendar> calendars = new ArrayList<>();

        calendarView.setOnDayClickListener(new OnDayClickListener() {

            @Override
            public void onDayClick(EventDay eventDay) {

                Toast.makeText(getContext(), eventDay.getCalendar().getTime().toString(), Toast.LENGTH_SHORT).show();
                calendars.add(eventDay.getCalendar());
                calendarView.setDisabledDays(calendars);


            }
        });





    }

}
