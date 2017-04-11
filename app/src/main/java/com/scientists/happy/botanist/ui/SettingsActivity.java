/*
 * Copyright 2016 Iskander Gaba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scientists.happy.botanist.ui;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.scientists.happy.botanist.R;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;

import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialActivity;

public class SettingsActivity extends AppCompatActivity {
    public static SharedPreferences mPreferences;
    public static final String WATER_HOUR_KEY = "water_hour";
    public static final String WATER_MINUTE_KEY = "water_minute";
    public static final String WATER_REMINDER_KEY = "water_reminder";
    public static final String HEIGHT_REMINDER_KEY = "height_record_reminder";
    public static final String PHOTO_REMINDER_KEY = "photo_reminder";
    public static final String FERTILIZER_REMINDER_KEY = "fertilize_reminder";
    private static final int REQUEST_CODE = 1234;
    /**
     * Upon launching the activity
     * @param savedInstanceState - current app state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{
        /**
         * Run when fragment is created
         * @param savedInstanceState - current fragment state
         */
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference timePicker = findPreference("water_time");
            timePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                /**
                 * User clicked an option
                 * @param preference - selected option
                 * @return Returns false
                 */
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showTimePicker();
                    return false;
                }
            });
            updatePref(WATER_REMINDER_KEY);
            updatePref(HEIGHT_REMINDER_KEY);
            updatePref(PHOTO_REMINDER_KEY);
            updatePref(FERTILIZER_REMINDER_KEY);
        }

        /**
         * Activity resumed
         */
        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        /**
         * Activity paused
         */
        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        /**
         * Shared preference changed
         * @param sharedPreferences - all shared preferences
         * @param key - which one changed
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(WATER_REMINDER_KEY) || key.equals(HEIGHT_REMINDER_KEY)
                    || key.equals(FERTILIZER_REMINDER_KEY)|| key.equals(PHOTO_REMINDER_KEY)) {
                updatePref(key);
            }
        }

        /**
         * Update a preference
         * @param key - which preference
         */
        private void updatePref(String key){
            ListPreference preference = (ListPreference) findPreference(key);
            CharSequence entry = preference.getEntry();
            String value = preference.getValue();
            preference.setSummary(entry);
            SharedPreferences.Editor editor = mPreferences.edit();
            // Fucking strangely, a string cannot be parsed to an integer
            editor.putString(key, value);
            editor.apply();
        }

        /**
         * Show the clock
         */
        private void showTimePicker() {
            // By default, it is 9:00 am
            int hour = mPreferences.getInt(WATER_HOUR_KEY, 9);
            int minute = mPreferences.getInt(WATER_MINUTE_KEY, 0);
            TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                /**
                 * Handle time set
                 * @param view - current app view
                 * @param hourOfDay - selected hour
                 * @param minute - selected minute
                 * @param second - selected second
                 */
                @Override
                public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putInt(WATER_HOUR_KEY, hourOfDay);
                    editor.putInt(WATER_MINUTE_KEY, minute);
                    editor.apply();
                }
            };
            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(listener, hour, minute, false);
            timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);
            timePickerDialog.setTitle(getActivity().getString(R.string.set_time));
            timePickerDialog.vibrate(false);
            timePickerDialog.dismissOnPause(true);
            timePickerDialog.show(getFragmentManager(), "time_picker");
        }
    }

    /**
     * Handle options menu
     * @param menu - options menu
     * @return Returns success code
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    /**
     * Handle selected option
     * @param item - selected option
     * @return Returns success code
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_help) {
            loadTutorial();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Load the tutorial
     */
    public void loadTutorial() {
        Intent mainAct = new Intent(this, MaterialTutorialActivity.class);
        mainAct.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, getTutorialItems(this));
        startActivityForResult(mainAct, REQUEST_CODE);
    }

    /**
     * Fetch assets for the tutorial
     * @param context - current app context
     * @return - Returns the list of tutorial items
     */
    private ArrayList<TutorialItem> getTutorialItems(Context context) {
        TutorialItem tutorialItem1 = new TutorialItem(context.getString(R.string.tutorial_title_0), context.getString(R.string.tutorial_contents_0),
                R.color.colorPrimary, R.drawable.tutorial_0,  R.drawable.tutorial_0);
        TutorialItem tutorialItem2 = new TutorialItem(context.getString(R.string.tutorial_title_1), context.getString(R.string.tutorial_contents_1),
                R.color.colorPrimary, R.drawable.tutorial_1,  R.drawable.tutorial_1);
        TutorialItem tutorialItem3 = new TutorialItem(context.getString(R.string.tutorial_title_2), context.getString(R.string.tutorial_contents_2),
                R.color.colorPrimary, R.drawable.tutorial_2,  R.drawable.tutorial_2);
        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        return tutorialItems;
    }
}