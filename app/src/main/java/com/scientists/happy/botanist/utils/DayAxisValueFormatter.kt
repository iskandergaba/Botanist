// Format the day of week
// @author: Iskander Gaba
package com.scientists.happy.botanist.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import android.content.Context
import com.scientists.happy.botanist.R
import java.util.Calendar

class DayAxisValueFormatter
/**
 * Create a new day axis formatter
 * @param mContext - current app context
 */
(private val mContext: Context) : IAxisValueFormatter {

    /**
     * Format millis to a day of week
     * @param value - millis to format
     * *
     * @param axis - axis to format to
     * *
     * @return Returns the day of week
     */
    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val days = mContext.resources.getStringArray(R.array.day_x_axis_labels)
        // Getting the real day of the week from the offset value
        val diff = 7 - Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        var dayOfTheWeek = value.toInt() - diff
        if (dayOfTheWeek < 1) {
            dayOfTheWeek += 7
        }
        val date = Calendar.getInstance()
        date.timeInMillis = System.currentTimeMillis()

        if (dayOfTheWeek == Calendar.SUNDAY) {
            return if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) days[0] else days[1]
        } else if (dayOfTheWeek == Calendar.MONDAY) {
            return if (date.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) days[0] else days[2]
        } else if (dayOfTheWeek == Calendar.TUESDAY) {
            return if (date.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) days[0] else days[3]
        } else if (dayOfTheWeek == Calendar.WEDNESDAY) {
            return if (date.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) days[0] else days[4]
        } else if (dayOfTheWeek == Calendar.THURSDAY) {
            return if (date.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) days[0] else days[5]
        } else if (dayOfTheWeek == Calendar.FRIDAY) {
            return if (date.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) days[0] else days[6]
        } else if (dayOfTheWeek == Calendar.SATURDAY) {
            return if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) days[0] else days[7]
        }
        return ""
    }
}