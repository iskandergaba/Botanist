package com.scientists.happy.botanist.controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.scientists.happy.botanist.R
import com.scientists.happy.botanist.utils.DateAxisValueFormatter
import com.scientists.happy.botanist.utils.DayAxisValueFormatter
import za.co.riggaroo.materialhelptutorial.TutorialItem
import java.util.*

class StatsController(activity: AppCompatActivity) : ActivityController(activity) {

    private var mPlantId : String = activity.intent.extras.getString("plant_id")

    override fun load() {
        populateHeightChart()
        populateWaterChart()
    }

    override fun loadTutorialItems(): ArrayList<TutorialItem>? {
        return null
    }

    private fun populateHeightChart() {
        val chart : LineChart = activity.findViewById(R.id.height_chart) as LineChart
        chart.setTouchEnabled(false)
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = DateAxisValueFormatter()
        // Separate labels by almost one month
        xAxis.granularity = 259200000f
        xAxis.labelCount = 4
        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
        chart.description.isEnabled = false

        val heightsRef : DatabaseReference? = databaseManager.getPlantHeightsReference(mPlantId)
        heightsRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val entries = ArrayList<Entry>()
                    for (record in snapshot.children) {
                        val time = java.lang.Long.parseLong(record.key)
                        val height = record.getValue(Float::class.java)!!
                        entries.add(Entry(time.toFloat(), height))
                    }
                    if (!entries.isEmpty()) {
                        val dataSet = LineDataSet(entries, "Height in inches")
                        dataSet.lineWidth = 1.5f
                        dataSet.setColors(Color.RED)
                        val lineData = LineData(dataSet)
                        lineData.setValueTextSize(7f)
                        chart.data = lineData
                        chart.invalidate()
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun populateWaterChart() {
        val chart : BarChart = activity.findViewById(R.id.water_chart) as BarChart
        chart.setTouchEnabled(false)
        val xAxis = chart.xAxis
        // Separate labels by one day
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = DayAxisValueFormatter(activity)
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 11f
        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
        // Y axis can only have integer values
        chart.axisLeft.granularity = 1f
        chart.description.isEnabled = false

        val wateringRef : DatabaseReference? = databaseManager.getPlantWateringReference(mPlantId)
        wateringRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            internal var watering: MutableMap<Long, Int> = LinkedHashMap()
            /**
             * Handle a change in the user data
             * @param snapshot - the current database contents
             */
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val today = Calendar.getInstance()
                    watering.put(today.timeInMillis, 0)
                    for (i in 1..6) {
                        val day = Calendar.getInstance()
                        day.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR) - i)
                        watering.put(day.timeInMillis, 0)
                    }
                    for (record in snapshot.children) {
                        processTime(java.lang.Long.parseLong(record.getValue(String::class.java)))
                    }
                    val entries = ArrayList<BarEntry>()
                    val diff = 7 - today.get(Calendar.DAY_OF_WEEK)
                    for (timeStamp in watering.keys) {
                        val date = Calendar.getInstance()
                        date.timeInMillis = timeStamp
                        // Just to ensure that today appears always as the latest bar
                        var day = date.get(Calendar.DAY_OF_WEEK) + diff
                        if (day > 7) day %= 7
                        entries.add(BarEntry(day.toFloat(), watering[timeStamp]!!.toFloat()))
                    }
                    val dataSet = BarDataSet(entries, "Times Watered")
                    val barData = BarData(dataSet)
                    barData.barWidth = 0.9f
                    barData.setValueTextSize(10f)
                    chart.data = barData
                    chart.invalidate()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}

            private fun processTime(time: Long) {
                val date = Calendar.getInstance()
                date.timeInMillis = time
                for (timeStamp in watering.keys) {
                    val day = Calendar.getInstance()
                    day.timeInMillis = timeStamp
                    if (date.get(Calendar.YEAR) == day.get(Calendar.YEAR) && date.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR)) {
                        val count : Int? = watering.remove(timeStamp)
                        watering.put(timeStamp, count!!.plus(1))
                        break
                    }
                }
            }
        })
    }
}
