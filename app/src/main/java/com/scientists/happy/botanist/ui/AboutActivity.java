package com.scientists.happy.botanist.ui;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import com.scientists.happy.botanist.BuildConfig;
import com.scientists.happy.botanist.R;
public class AboutActivity extends AppCompatActivity {
    private static final String CACTUS_GITHUB_URL = "https://github.com/amuscarella";
    private static final String CACTUS_LINKEDIN_URL = "https://www.linkedin.com/in/antonio-muscarella-6a927a100";
    private static final String CHIA_GITHUB_URL = "https://github.com/CBesser";
    private static final String CHIA_LINKEDIN_URL = "https://www.linkedin.com/in/christopher-besser-352900103";
    private static final String FLYTRAP_GITHUB_URL = "https://github.com/iskandergaba";
    private static final String FLYTRAP_LINKEDIN_URL = "https://www.linkedin.com/in/iskandergaba";
    private static final String MARIMO_GITHUB_URL = "https://github.com/xwyzhx";
    private static final String MARIMO_LINKEDIN_URL = "https://www.linkedin.com/in/wendyzhang95";
    /**
     * The activity is launched
     * @param savedInstanceState - current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ((TextView) findViewById(R.id.version_name)).setText(BuildConfig.VERSION_NAME);
        // Set click listeners for the buttons
        findViewById(R.id.cactus_github_button).setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked Antonio's Github
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(CACTUS_GITHUB_URL);
            }
        });
        findViewById(R.id.cactus_linkedin_button).setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked Antonio's LinkedIn
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(CACTUS_LINKEDIN_URL);
            }
        });
        findViewById(R.id.chia_github_button).setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked Chris's GitHub
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(CHIA_GITHUB_URL);
            }
        });
        findViewById(R.id.chia_linkedin_button).setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked Chris's LinkedIn
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(CHIA_LINKEDIN_URL);
            }
        });
        findViewById(R.id.flytrap_github_button).setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked Iskander's GitHub
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(FLYTRAP_GITHUB_URL);
            }
        });
        findViewById(R.id.flytrap_linkedin_button).setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked Iskander's LinkedIn
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(FLYTRAP_LINKEDIN_URL);
            }
        });
        findViewById(R.id.marimo_github_button).setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked Wendy's GitHub
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(MARIMO_GITHUB_URL);
            }
        });
        findViewById(R.id.marimo_linkedin_button).setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked Wendy's LinkedIn
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                openWebPage(MARIMO_LINKEDIN_URL);
            }
        });
        TextView fertilizationLink = (TextView)findViewById(R.id.github_repo_link);
        fertilizationLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * User pressed back
     * @return Returns true
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    /**
     * Launch Google Chrome
     * @param url - URL to view
     */
    private void openWebPage(String url) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        startActivity(viewIntent);
    }
}