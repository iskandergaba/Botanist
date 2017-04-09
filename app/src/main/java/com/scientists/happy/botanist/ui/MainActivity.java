// Main page
// @author: Christopher Besser, Iskander Gaba, Antonio Muscarella, and Wendy Zhang
package com.scientists.happy.botanist.ui;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
public class MainActivity extends AppCompatActivity {
    private static final int VIEW_ACCOUNT = 1;

    /**
     * Launch app
     * @param savedInstanceState - app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * Handle action button click
             * @param view - current view
             */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddPlantActivity.class));
            }
        });
        DatabaseManager database = DatabaseManager.getInstance();
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setEmptyView(findViewById(R.id.empty_grid_view));
        database.populatePlantGrid(this, gridView);
    }

    /**
     * Handle options menu
     * @param menu - options menu
     * @return Returns success code
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_account) {
            startActivityForResult(new Intent(MainActivity.this, AccountActivity.class), VIEW_ACCOUNT);
            return true;
        }
        else if (id == R.id.action_shop) {
            startActivity(new Intent(MainActivity.this, ShopActivity.class));
            return true;
        }
        else if (id == R.id.action_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Launched activity ended
     * @param requestCode - activity launch request code
     * @param resultCode - activity launch result
     * @param data - activity result data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == VIEW_ACCOUNT) && (resultCode == RESULT_OK)) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
}