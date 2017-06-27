// Show users similar plants
// @author: Antonio Muscarella, Christopher Besser and Iskander Gaba
package com.scientists.happy.botanist.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import com.scientists.happy.botanist.R
import com.scientists.happy.botanist.data.DatabaseManager

class SimilarPlantsActivity : AppCompatActivity() {
    /**
     * The activity is launched
     * @param savedInstanceState - current app state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_similar_plants)
        val list:ListView = findViewById(R.id.similar_plants) as ListView
        list.emptyView = findViewById(R.id.empty_list_view)
        val group = intent.extras.get("group") as String
        val species = intent.extras.get("species") as String
        val database = DatabaseManager.getInstance()
        val adapter = database.getSimilarPlants(this, group, species)
        list.adapter = adapter
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