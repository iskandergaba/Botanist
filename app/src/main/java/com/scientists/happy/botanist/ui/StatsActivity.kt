// View plant statistics and diseases
// @author: Antonio Muscarella and Iskander Gaba
package com.scientists.happy.botanist.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.scientists.happy.botanist.R
import com.scientists.happy.botanist.data.DatabaseManager
import com.scientists.happy.botanist.utils.DateAxisValueFormatter
import com.scientists.happy.botanist.utils.DayAxisValueFormatter

class StatsActivity : AppCompatActivity() {
    private var mDatabase: DatabaseManager? = null
    /**
     * The activity is being created
     * @param savedInstanceState - current activity state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        mDatabase = DatabaseManager.getInstance()
        val plantId = intent.extras.get("plant_id") as String
        populateHeightChart(plantId)
        populateWaterChart(plantId)
    }

    /**
     * User navigated up from the activity
     * @return returns true
     */
    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    /**
     * Make the height chart
     * @param plantId - plant to chart
     */
    private fun populateHeightChart(plantId: String) {
        val chart = findViewById(R.id.height_chart) as LineChart
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
        mDatabase!!.populateHeightChart(plantId, chart)
    }

    /**
     * Make the water chart
     * @param plantId - plant to chart
     */
    private fun populateWaterChart(plantId: String) {
        val chart = findViewById(R.id.water_chart) as BarChart
        chart.setTouchEnabled(false)
        val xAxis = chart.xAxis
        // Separate labels by one day
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = DayAxisValueFormatter(this)
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 11f
        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
        // Y axis can only have integer values
        chart.axisLeft.granularity = 1f
        chart.description.isEnabled = false
        mDatabase!!.populateWaterChart(plantId, chart)
    }
}