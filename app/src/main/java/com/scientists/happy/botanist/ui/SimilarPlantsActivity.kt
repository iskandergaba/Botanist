// Show users similar plants
// @author: Antonio Muscarella, Christopher Besser and Iskander Gaba
package com.scientists.happy.botanist.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.scientists.happy.botanist.R
import com.scientists.happy.botanist.controller.ActivityController
import com.scientists.happy.botanist.controller.SimilarPlantsController

class SimilarPlantsActivity : AppCompatActivity() {

    private var mController: ActivityController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_similar_plants)
        mController = SimilarPlantsController(this)
    }

    override fun onStart() {
        mController!!.load()
        super.onStart()
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }
}