// Shop for botanist pots, tools, accessories and gifts
// @author: Wendy Zhang and Iskander Gaba
package com.scientists.happy.botanist.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.scientists.happy.botanist.R

class ShopActivity : AppCompatActivity() {
    /**
     * Launch the activity
     * @param savedInstanceState - current app state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        val potsButton = findViewById<Button>(R.id.shop_pots_button)
        potsButton.setOnClickListener{ openWebPage(SHOP_POTS_URL) }
        val toolsButton = findViewById<Button>(R.id.shop_tools_button)
        toolsButton.setOnClickListener{ openWebPage(SHOP_TOOLS_URL) }
        val giftsButton = findViewById<Button>(R.id.shop_gifts_button)
        giftsButton.setOnClickListener{ openWebPage(SHOP_GIFTS_URL) }
    }

    /**
     * User pressed the back button
     * @return Returns true
     */
    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    /**
     * Launch Web browser
     * @param url - the url to view
     */
    private fun openWebPage(url: String) {
        val viewIntent = Intent("android.intent.action.VIEW", Uri.parse(url))
        startActivity(viewIntent)
    }

    companion object {
        private val SHOP_POTS_URL = "http://www.crocus.co.uk/pots/"
        private val SHOP_TOOLS_URL = "http://www.crocus.co.uk/tools/"
        private val SHOP_GIFTS_URL = "http://www.crocus.co.uk/gifts-and-home/"
    }
}