package com.scientists.happy.botanist.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddPlantActivity extends AppCompatActivity {

    protected ImageView mPicture;
    protected TextView mPictureHint;
    protected EditText mNameEditText;
    protected EditText mBirthdayEditText;
    protected AutoCompleteTextView mSpeciesAutoCompleteText;
    protected EditText mHeightEditText;
    protected EditText mWaterEditText;
    protected Button mAddPlantButton;

    protected DatabaseManager mDatabase;
    protected Bitmap mBitmap;
    protected GregorianCalendar mBirthday;

    int mWaterHour, mWaterMin;

    /**
     * Launch the add plant screen
     * @param savedInstanceState - Current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        mDatabase = DatabaseManager.getInstance();

        mPictureHint = (TextView) findViewById(R.id.picture_hint);
        mSpeciesAutoCompleteText = (AutoCompleteTextView) findViewById(R.id.species_edit_text);
        mDatabase.setSpeciesAutoComplete(this, mSpeciesAutoCompleteText);
        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
        mBirthdayEditText = (EditText) findViewById(R.id.birthday_edit_text);
        mBirthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        mBirthdayEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showDatePicker();
            }
        });
        mHeightEditText = (EditText) findViewById(R.id.height_edit_text);
        mWaterEditText = (EditText) findViewById(R.id.water_edit_text);
        mAddPlantButton = (Button) findViewById(R.id.add_plant_button);
        mAddPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String height = mHeightEditText.getText().toString();
                mDatabase.addPlant(AddPlantActivity.this,
                        mNameEditText.getText().toString(),
                        mSpeciesAutoCompleteText.getText().toString(),
                        mBirthday.getTimeInMillis(),
                        height.equals("") ? 0 : Double.parseDouble(height),
                        mBitmap);
                finish();
            }
        });

        mPicture = (ImageView) findViewById(R.id.picture);
        mPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PickSetup setup = new PickSetup()
                        .setSystemDialog(true);
                PickImageDialog.build(setup)
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                mBitmap = r.getBitmap();
                                mPicture.setImageBitmap(mBitmap);
                            }
                        })
                        .show(getSupportFragmentManager());
            }
        });

        mBirthday = new GregorianCalendar();
        mWaterHour = mBirthday.get(Calendar.HOUR);
        mWaterMin = mBirthday.get(Calendar.MINUTE);
    }

    /**
     * Show the date picker
     */
    private void showDatePicker() {

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                mBirthday.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                mBirthdayEditText.setText(dateFormat.format(mBirthday.getTime()));
            }

        };
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(listener,
                mBirthday.get(Calendar.YEAR),
                mBirthday.get(Calendar.MONTH),
                mBirthday.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setTitle(getString(R.string.set_birthday));
        datePickerDialog.vibrate(false);
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setOnDateSetListener(listener);
        datePickerDialog.show(getFragmentManager(), "date_picker");
    }

    /**
     * Show the clock
     * later on will be implemented, ignore for now
     */
    private void showTimePicker() {

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
                mWaterHour = hourOfDay;
                mWaterMin = minute;
                mWaterEditText.setText(mWaterHour < 13 ? mWaterHour + "" : "");
            }
        };

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(listener, mWaterHour, mWaterMin, false);
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);
        //timePickerDialog.setTitle(this.getString(R.string.set_time));
        timePickerDialog.vibrate(false);
        timePickerDialog.dismissOnPause(true);
        timePickerDialog.show(getFragmentManager(), "time_picker");
    }
}