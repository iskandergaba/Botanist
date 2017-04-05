package com.scientists.happy.botanist.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateAxisValueFormatter implements IAxisValueFormatter {

    private SimpleDateFormat mFormat;

    public DateAxisValueFormatter() {
        mFormat = new SimpleDateFormat("MM/yyyy", Locale.US);
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis((long) value);
        return mFormat.format(date.getTime());
    }
}
