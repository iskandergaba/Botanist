// Format the day of week
// @author: Iskander Gaba
package com.scientists.happy.botanist.utils;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import android.content.Context;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.scientists.happy.botanist.R;
import java.util.Calendar;
public class DayAxisValueFormatter implements IAxisValueFormatter {
    private Context mContext;
    /**
     * Create a new day axis formatter
     * @param context - current app context
     */
    public DayAxisValueFormatter(Context context) {
        this.mContext = context;
    }
  
    /**
     * Format millis to a day of week
     * @param value - millis to format
     * @param axis - axis to format to
     * @return Returns the day of week
     */
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        String[] days = mContext.getResources().getStringArray(R.array.day_x_axis_labels);
        // Getting the real day of the week from the offset value
        int diff = 7 - Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int dayOfTheWeek = ((int) value - diff);
        if(dayOfTheWeek < 1) {
            dayOfTheWeek += 7;
        }
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(System.currentTimeMillis());

        if (dayOfTheWeek == Calendar.SUNDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) ? days[0] : days[1];
        }else if (dayOfTheWeek == Calendar.MONDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) ? days[0] : days[2];
        } else if (dayOfTheWeek == Calendar.TUESDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) ? days[0] : days[3];
        } else if (dayOfTheWeek == Calendar.WEDNESDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) ? days[0] : days[4];
        } else if (dayOfTheWeek == Calendar.THURSDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) ? days[0] : days[5];
        } else if (dayOfTheWeek == Calendar.FRIDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) ? days[0] : days[6];
        }  else if (dayOfTheWeek == Calendar.SATURDAY) {
            return (date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) ? days[0] : days[7];
        }
        return "";
    }
}