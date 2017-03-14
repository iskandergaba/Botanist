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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddPlantActivity extends AppCompatActivity {

    protected ImageView picture;
    protected TextView pictureHint;
    protected EditText nameEditText;
    protected EditText mBirthdayEditText;
    protected AutoCompleteTextView speciesAutoCompleteText;
    protected Button addPlantButton;

    protected DatabaseManager mDatabase;
    protected Bitmap mBitmap;
    protected GregorianCalendar mBirthday;

    /**
     * Launch the add plant screen
     * @param savedInstanceState - Current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        mDatabase = DatabaseManager.getInstance();

        pictureHint = (TextView) findViewById(R.id.picture_hint);
        speciesAutoCompleteText = (AutoCompleteTextView) findViewById(R.id.species_edit_text);
        mDatabase.setSpeciesAutoComplete(this, speciesAutoCompleteText);
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        mBirthdayEditText = (EditText) findViewById(R.id.birthday_edit_text);
        mBirthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        addPlantButton = (Button) findViewById(R.id.add_plant_button);
        addPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: changes here
                mDatabase.addPlant(AddPlantActivity.this, nameEditText.getText().toString(), speciesAutoCompleteText.getText().toString(),
                        mBirthday.getTimeInMillis(), mBitmap);
                finish();
            }
        });

        picture = (ImageView) findViewById(R.id.picture);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PickSetup setup = new PickSetup()
                        .setSystemDialog(true);
                PickImageDialog.build(setup)
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                mBitmap = r.getBitmap();
                                picture.setImageBitmap(mBitmap);
                            }
                        })
                        .show(getSupportFragmentManager());
            }
        });

        mBirthday = new GregorianCalendar();
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
}