package com.scientists.happy.botanist.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri

import com.scientists.happy.botanist.R

object AppRater {
    private val APP_PACKAGE_NAME = "com.scientists.happy.botanist"// Package Name

    private val DAYS_UNTIL_PROMPT = 3 //Min number of days
    private val LAUNCHES_UNTIL_PROMPT = 3 //Min number of launches

    fun appLaunched(context: Context) {
        val prefs = context.getSharedPreferences("app_rater", 0)
        if (prefs.getBoolean("dont_show_again", false)) {
            return
        }

        val editor = prefs.edit()

        // Increment launch counter
        val launchCount = prefs.getLong("launch_count", 0) + 1
        editor.putLong("launch_count", launchCount)

        // Get date of first launch
        var firstLaunchDate: Long? = prefs.getLong("first_launch_date", 0)
        if (firstLaunchDate == 0.toLong()) {
            firstLaunchDate = System.currentTimeMillis()
            editor.putLong("first_launch_date", firstLaunchDate)
        }

        // Wait at least n days before opening
        if (launchCount >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= firstLaunchDate!! + DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000) {
                showRateDialog(context, editor)
            }
        }

        editor.apply()
    }

    private fun showRateDialog(context: Context, editor: SharedPreferences.Editor) {
        val dialog = Dialog(context)

        dialog.setContentView(R.layout.rate_app_dialog)
        dialog.findViewById(R.id.btn_rate).setOnClickListener {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PACKAGE_NAME)))
            editor.putBoolean("dont_show_again", true)
            editor.apply()
            dialog.dismiss()
        }
        dialog.findViewById(R.id.btn_later).setOnClickListener { dialog.dismiss() }
        dialog.findViewById(R.id.btn_no).setOnClickListener {
            editor.putBoolean("dont_show_again", true)
            editor.apply()
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }
}
