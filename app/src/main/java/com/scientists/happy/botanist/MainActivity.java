package com.scientists.happy.botanist;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private static final String DELIMETER = "\t";
    /**
     * Launch app
     * @param savedInstanceState - app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final PlantArray plants = PlantArray.getInstance();
        if (getIntent().getExtras() != null) {

        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Handle action button click
             * @param view - current view
             */
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(MainActivity.this, AddPlantActivity.class);
                startActivity(i);
            }
        });

        final Activity activity = MainActivity.this;
        final GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, getIntent()));
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                Plant plant = plants.get(position);
                if (plant != null) {
                    i.putExtra("plant", plant.toString());
                } else {
                    i.putExtra("plant", "Flowey\tUndertalus asrielus\t");
                }

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    View sharedView = gridview.getChildAt(position - gridview.getFirstVisiblePosition());
                    sharedView.setTransitionName("main_to_profile_transition");
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(activity, sharedView, sharedView.getTransitionName()).toBundle();
                    startActivity(i, bundle);
                } else {
                   startActivity(i);
                }
            }
        });
    }

    /**
     * Handle options menu
     * @param menu - options menu
     * @return Returns success code
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handle selected option
     * @param item - selected option
     * @return Returns success code
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}