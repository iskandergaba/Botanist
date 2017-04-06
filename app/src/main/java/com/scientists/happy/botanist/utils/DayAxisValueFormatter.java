package com.scientists.happy.botanist.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Calendar;

public class DayAxisValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        // Getting the real day of the week from the offset value
        int diff = 7 - Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int dayOfTheWeek = ((int)value - diff);
        if(dayOfTheWeek < 1) dayOfTheWeek += 7;

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(System.currentTimeMillis());

        if (dayOfTheWeek == Calendar.MONDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) ? "Today" : "Monday";
        } else if (dayOfTheWeek == Calendar.TUESDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) ? "Today" : "Tuesday";
        } else if (dayOfTheWeek == Calendar.WEDNESDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) ? "Today" : "Wednesday";
        } else if (dayOfTheWeek == Calendar.THURSDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) ? "Today" : "Thursday";
        } else if (dayOfTheWeek == Calendar.FRIDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) ? "Today" : "Friday";
        }  else if (dayOfTheWeek == Calendar.SATURDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) ? "Today" : "Saturday";
        } else if (dayOfTheWeek == Calendar.SUNDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) ? "Today" : "Sunday";
        }

        return "";
    }
}