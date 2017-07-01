// View diseases a plant can get
// @author: Iskander Gaba
package com.scientists.happy.botanist.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.scientists.happy.botanist.R
import com.scientists.happy.botanist.controller.EditPlantController

class EditPlantActivity : AppCompatActivity() {
    private var mController : EditPlantController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
    }

    override fun onStart() {
        super.onStart()
        mController = EditPlantController(this)
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