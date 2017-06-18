// View diseases a plant can get
// @author: Iskander Gaba
package com.scientists.happy.botanist.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.GridView
import com.scientists.happy.botanist.R
import com.scientists.happy.botanist.data.DatabaseManager

class EditProfileActivity : AppCompatActivity() {
    private var mDatabase: DatabaseManager? = null
    /**
     * The activity is launched
     * @param savedInstanceState - current app state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        mDatabase = DatabaseManager.getInstance()
        val grid = findViewById(R.id.photo_grid_view) as GridView
        grid.emptyView = findViewById(R.id.empty_list_view)
        val plantId = intent.extras.get("plant_id") as String
        mDatabase!!.populatePhotoGrid(this, grid, plantId)
    }

    /**
     * User navigated up from the activity
     * @return returns true
     */
    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }
}