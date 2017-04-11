// View plant statistics and diseases
// @author: Antonio Muscarella and Iskander Gaba
package com.scientists.happy.botanist.ui;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
import com.scientists.happy.botanist.utils.DateAxisValueFormatter;
import com.scientists.happy.botanist.utils.DayAxisValueFormatter;
import java.util.ArrayList;
import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialActivity;
public class StatsActivity extends AppCompatActivity {
    private DatabaseManager mDatabase;
    private static final int REQUEST_CODE = 1234;
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

    /**
     * Make the height chart
     * @param plantId - plant to chart
     */
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
        chart.getDescription().setEnabled(false);
        mDatabase.populateHeightChart(plantId, chart);
    }

    /**
     * Make the water chart
     * @param plantId - plant to chart
     */
    private void populateWaterChart(String plantId) {
        BarChart chart = (BarChart) findViewById(R.id.water_chart);
        chart.setTouchEnabled(false);
        XAxis xAxis = chart.getXAxis();
        // Separate labels by one day
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DayAxisValueFormatter());
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(11f);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        // Y axis can only have integer values
        chart.getAxisLeft().setGranularity(1);
        chart.getDescription().setEnabled(false);
        mDatabase.populateWaterChart(plantId, chart);
    }

    /**
     * Handle options menu
     * @param menu - options menu
     * @return Returns success code
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    /**
     * Handle selected option
     * @param item - selected option
     * @return Returns success code
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_help) {
            loadTutorial();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Load the tutorial
     */
    public void loadTutorial() {
        Intent mainAct = new Intent(this, MaterialTutorialActivity.class);
        mainAct.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, getTutorialItems(this));
        startActivityForResult(mainAct, REQUEST_CODE);
    }

    /**
     * Fetch assets for the tutorial
     * @param context - current app context
     * @return - Returns the list of tutorial items
     */
    private ArrayList<TutorialItem> getTutorialItems(Context context) {
        TutorialItem tutorialItem1 = new TutorialItem(context.getString(R.string.tutorial_title_0), context.getString(R.string.tutorial_contents_0),
                R.color.colorPrimary, R.drawable.tutorial_0,  R.drawable.tutorial_0);
        TutorialItem tutorialItem2 = new TutorialItem(context.getString(R.string.tutorial_title_1), context.getString(R.string.tutorial_contents_1),
                R.color.colorPrimary, R.drawable.tutorial_1,  R.drawable.tutorial_1);
        TutorialItem tutorialItem3 = new TutorialItem(context.getString(R.string.tutorial_title_2), context.getString(R.string.tutorial_contents_2),
                R.color.colorPrimary, R.drawable.tutorial_2,  R.drawable.tutorial_2);
        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        return tutorialItems;
    }
}