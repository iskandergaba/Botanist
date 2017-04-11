// Format date from millis
// @author: Iskander Gaba
package com.scientists.happy.botanist.utils;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
public class DateAxisValueFormatter implements IAxisValueFormatter {
    private SimpleDateFormat mFormat;

    /**
     * Create a formatter
     */
    public DateAxisValueFormatter() {
        mFormat = new SimpleDateFormat("MM/yyyy", Locale.US);
    }

    /**
     * Convert the float into a date
     * @param value - float to convert
     * @param axis - axis to fit to
     * @return Returns the date formatted
     */
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis((long) value);
        return mFormat.format(date.getTime());
    }
}
