// Format date from millis
// @author: Iskander Gaba
package com.scientists.happy.botanist.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateAxisValueFormatter : IAxisValueFormatter {
    private val mFormat: SimpleDateFormat = SimpleDateFormat("MM/yyyy", Locale.US)

    /**
     * Convert the float into a date
     * @param value - float to convert
     * *
     * @param axis - axis to fit to
     * *
     * @return Returns the date formatted
     */
    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val date = Calendar.getInstance()
        date.timeInMillis = value.toLong()
        return mFormat.format(date.time)
    }
}
