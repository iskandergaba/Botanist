// View diseases a plant can get
// @author: Antonio Muscarella
package com.scientists.happy.botanist.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
import com.scientists.happy.botanist.utils.DateAxisValueFormatter;
import com.scientists.happy.botanist.utils.DayAxisValueFormatter;

public class StatsActivity extends AppCompatActivity {
    private DatabaseManager mDatabase;
    /**
     * The activity is being created
     * @param savedInstanceState - current activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        mDatabase = DatabaseManager.getInstance();
        String plantId = (String) getIntent().getExtras().get("plant_id");
        populateHeightChart(plantId);
        populateWaterChart(plantId);
    }

    /**
     * User navigated up from the activity
     * @return returns true
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    private void populateHeightChart(String plantId) {
        LineChart chart = (LineChart) findViewById(R.id.height_chart);
        chart.setTouchEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DateAxisValueFormatter());
        // Separate labels by almost one month
        xAxis.setGranularity(259200000);
        xAxis.setLabelCount(4);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        chart.getDescription().setText("Plant Growth");
        chart.getDescription().setTextSize(11f);

        mDatabase.populateHeightChart(plantId, chart);
    }

    private void populateWaterChart(String plantId) {
        BarChart chart = (BarChart) findViewById(R.id.water_chart);
        chart.setTouchEnabled(false);
        XAxis xAxis = chart.getXAxis();
        // Separate labels by one day
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DayAxisValueFormatter());
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(9f);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        chart.getDescription().setText("Weekly Watering Summary");
        chart.getDescription().setTextSize(11f);
        mDatabase.populateWaterChart(plantId, chart);
    }
}