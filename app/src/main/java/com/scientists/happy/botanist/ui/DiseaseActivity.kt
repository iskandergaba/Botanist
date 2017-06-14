// View diseases a plant can get
// @author: Antonio Muscarella & Iskander Gaba
package com.scientists.happy.botanist.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import com.scientists.happy.botanist.R
import com.scientists.happy.botanist.data.DatabaseManager

class DiseaseActivity : AppCompatActivity() {
    private var mDatabase: DatabaseManager? = null
    /**
     * The activity is launched
     * @param savedInstanceState - current app state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease)
        mDatabase = DatabaseManager.getInstance()
        val list = findViewById(R.id.diseases) as ListView
        list.emptyView = findViewById(R.id.empty_list_view)
        val group = intent.extras.get("group") as String
        val adapter = mDatabase!!.getDiseases(this, group)
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