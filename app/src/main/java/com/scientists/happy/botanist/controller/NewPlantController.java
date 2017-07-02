package com.scientists.happy.botanist.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import za.co.riggaroo.materialhelptutorial.TutorialItem;

public class NewPlantController {

    private final AppCompatActivity mActivity;
    private final DatabaseManager mDatabase = DatabaseManager.getInstance();

    private GregorianCalendar mBirthday;
    private Bitmap mBitmap;

    public NewPlantController(AppCompatActivity activity) {
        mActivity = activity;
    }

    public void load() {
        final EditText nameEditText = (EditText) mActivity.findViewById(R.id.name_edit_text);
        final EditText heightEditText = (EditText) mActivity.findViewById(R.id.height_edit_text);
        final EditText birthdayEditText = (EditText) mActivity.findViewById(R.id.birthday_edit_text);
        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            /**
             * Handle click event
             * @param v - the current view
             */
            @Override
            public void onClick(View v) {
                showDatePicker(birthdayEditText);
            }
        });
        birthdayEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * Handle focus change between views
             * @param v - current view
             * @param hasFocus - whether the current view has focus
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker(birthdayEditText);
                }
            }
        });
        final AutoCompleteTextView speciesAutoCompleteText = (AutoCompleteTextView) mActivity.findViewById(R.id.species_edit_text);
        mDatabase.setSpeciesAutoComplete(mActivity, speciesAutoCompleteText);
        Intent intent = mActivity.getIntent();
        if (intent.hasExtra("species")) {
            speciesAutoCompleteText.setText(intent.getStringExtra("species"));
        }
        final ImageView picture = (ImageView) mActivity.findViewById(R.id.picture);
        picture.setOnClickListener(new View.OnClickListener() {
            /**
             * Handle click event in the picture
             * @param v - the current view
             */
            @Override
            public void onClick(View v) {
                final PickSetup setup = new PickSetup().setSystemDialog(true);
                PickImageDialog.build(setup).setOnPickResult(new IPickResult() {
                    /**
                     * Handle the selected result
                     * @param r - the selection result
                     */
                    @Override
                    public void onPickResult(PickResult r) {
                        mBitmap = r.getBitmap();
                        picture.setImageBitmap(mBitmap);
                    }
                }).show(mActivity.getSupportFragmentManager());
            }
        });
        mBirthday = new GregorianCalendar();

        final Button addPlantButton = (Button) mActivity.findViewById(R.id.add_plant_button);
        addPlantButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String height = heightEditText.getText().toString();
                boolean isAddSuccessful = mDatabase.addPlant(mActivity, nameEditText.getText().toString(),
                        speciesAutoCompleteText.getText().toString(), mBirthday.getTimeInMillis(),
                        height.equals("") ? 0 : Double.parseDouble(height), mBitmap);
                if (isAddSuccessful) {
                    mActivity.finish();
                }
            }
        });
    }

    private ArrayList<TutorialItem> loadTutorialItems() {
        TutorialItem tutorialItem2 = new TutorialItem(mActivity.getString(R.string.add_tutorial_title_0), mActivity.getString(R.string.add_tutorial_contents_0),
                R.color.colorAccent, R.drawable.add_tutorial_0,  R.drawable.add_tutorial_0);
        TutorialItem tutorialItem3 = new TutorialItem(mActivity.getString(R.string.add_tutorial_title_1), mActivity.getString(R.string.add_tutorial_contents_1),
                R.color.colorAccent, R.drawable.add_tutorial_1,  R.drawable.add_tutorial_1);
        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        return tutorialItems;
    }

    public void showTutorial(boolean isForceShow) {
        mDatabase.showTutorial(mActivity, loadTutorialItems(), isForceShow);
    }

    /**
     * Show the date picker
     */
    private void showDatePicker(final EditText birthdayEditText) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            /**
             * Handle selected time for birthday
             * @param view - the current view
             * @param year - the selected year
             * @param monthOfYear - the selected month
             * @param dayOfMonth - the selected day
             */
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                mBirthday.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                birthdayEditText.setText(dateFormat.format(mBirthday.getTime()));
            }
        };
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(listener,
                mBirthday.get(Calendar.YEAR), mBirthday.get(Calendar.MONTH),
                mBirthday.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setTitle(mActivity.getString(R.string.set_birthday));
        datePickerDialog.vibrate(false);
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setOnDateSetListener(listener);
        datePickerDialog.show(mActivity.getFragmentManager(), "date_picker");
    }
}
