// View diseases a plant can get
// @author: Antonio Muscarella & Iskander Gaba
package com.scientists.happy.botanist.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.scientists.happy.botanist.R
import com.scientists.happy.botanist.controller.DiseaseController

class DiseaseActivity : AppCompatActivity() {

    private var mController:DiseaseController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease)
        mController = DiseaseController(this)
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