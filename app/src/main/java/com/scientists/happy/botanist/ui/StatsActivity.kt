// View plant statistics and diseases
// @author: Antonio Muscarella and Iskander Gaba
package com.scientists.happy.botanist.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.scientists.happy.botanist.R
import com.scientists.happy.botanist.controller.StatsController

class StatsActivity : AppCompatActivity() {
    private var mController : StatsController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
    }

    override fun onStart() {
        super.onStart()
        mController = StatsController(this)
    }

    override fun onResume() {
        super.onResume()
        mController?.load()
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }
}