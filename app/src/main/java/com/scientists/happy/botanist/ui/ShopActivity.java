// Shop for botanist accessories
// @author: Wendy Zhang and Iskander Gaba
package com.scientists.happy.botanist.ui;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.scientists.happy.botanist.R;

public class ShopActivity extends AppCompatActivity {
    private static final String SHOP_FERTILIZER_URL = "https://www.amazon.com/Best-Sellers-Patio-Lawn-Garden-Fertilizers/zgbs/lawn-garden/3752891";
    private static final String SHOP_ACCESSORIES_URL = "https://www.amazon.com/Best-Sellers-Patio-Lawn-Garden-Plant-Container-Accessories/zgbs/lawn-garden/3480695011";
    private static final String SHOP_POT_URL = "https://www.amazon.com/Best-Sellers-Patio-Lawn-Garden-Gardening-Pots-Planters-Accessories/zgbs/lawn-garden/3480694011";
    /**
     * Launch the activity
     * @param savedInstanceState - current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        View fertilizeButton = findViewById(R.id.shop_fertilizer_button);
        fertilizeButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked buy now
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(SHOP_FERTILIZER_URL);
            }
        });
        View accessoryButton = findViewById(R.id.shop_accessories_button);
        accessoryButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked buy now
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(SHOP_ACCESSORIES_URL);
            }
        });
        View potButton = findViewById(R.id.shop_pot_button);
        potButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked buy now
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(SHOP_POT_URL);
            }
        });
    }

    /**
     * User pressed the back button
     * @return Returns true
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    /**
     * Launch Web browser
     * @param url - the url to view
     */
    private void openWebPage(String url) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        startActivity(viewIntent);
    }
}