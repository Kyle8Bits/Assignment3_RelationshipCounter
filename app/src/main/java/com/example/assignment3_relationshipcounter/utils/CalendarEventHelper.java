package com.example.assignment3_relationshipcounter.utils;

import static java.util.Locale.*;

import android.graphics.Color;

import com.applandeo.materialcalendarview.EventDay;
import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.service.models.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarEventHelper {

    public static List<EventDay> generateEventDays(List<Event> events) {
        List<EventDay> eventDays = new ArrayList<>();
        for (Event event : events) {
            try {
                // Parse event date (assuming "yyyy-MM-dd" format)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", getDefault());
                Date date = sdf.parse(event.getDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                // Add EventDay with a dot (or replace drawable with your custom resource)
                eventDays.add(new EventDay(calendar, R.drawable.ic_dot, Color.RED));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return eventDays;
    }
}

