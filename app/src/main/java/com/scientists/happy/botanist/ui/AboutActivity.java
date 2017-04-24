package com.scientists.happy.botanist.ui;
/*
Copyright 2016 Iskander Gaba

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        ((TextView)findViewById(R.id.version_name)).setText(getString(R.string.version_fmt, BuildConfig.VERSION_NAME));

        // Set click listeners for the buttons
        findViewById(R.id.cactus_github_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(CACTUS_GITHUB_URL);
            }
        });
        findViewById(R.id.cactus_linkedin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(CACTUS_LINKEDIN_URL);
            }
        });

        findViewById(R.id.chia_github_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(CHIA_GITHUB_URL);
            }
        });
        findViewById(R.id.chia_linkedin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(CHIA_LINKEDIN_URL);
            }
        });
        findViewById(R.id.flytrap_github_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(FLYTRAP_GITHUB_URL);
            }
        });
        findViewById(R.id.flytrap_linkedin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(FLYTRAP_LINKEDIN_URL);
            }
        });
        findViewById(R.id.marimo_github_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(MARIMO_GITHUB_URL);
            }
        });
        findViewById(R.id.marimo_linkedin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(MARIMO_LINKEDIN_URL);
            }
        });
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

    private void openWebPage(String url) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        startActivity(viewIntent);
    }
}