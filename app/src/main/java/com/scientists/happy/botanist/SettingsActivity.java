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

package com.scientists.happy.botanist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    public static final String WATER_HOUR_KEY = "water_hour";
    public static final String WATER_MINUTE_KEY = "water_minute";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference btnDateFilter = findPreference("water_plants_time");
            btnDateFilter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showTimePicker();
                    return false;
                }
            });
        }

        private void showTimePicker(){

            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final Calendar c = Calendar.getInstance();
            int hour = preferences.getInt("water_hour", c.get(Calendar.HOUR_OF_DAY));
            int minute = preferences.getInt("water_minute", c.get(Calendar.MINUTE));

            TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(WATER_HOUR_KEY, hourOfDay);
                    editor.putInt(WATER_MINUTE_KEY, minute);
                    editor.apply();
                }
            };

            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(listener, hour, minute, false);
            timePickerDialog.setTitle(getActivity().getString(R.string.set_time));
            timePickerDialog.vibrate(false);
            timePickerDialog.dismissOnPause(true);
            timePickerDialog.show(getFragmentManager(), "time_picker");
        }
    }
}