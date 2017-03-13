// Add a plant to the database
// @author: Chia George Washington
package com.scientists.happy.botanist.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.scientists.happy.botanist.data.DatabaseManager;
import com.scientists.happy.botanist.utils.ImageUtils;
import com.scientists.happy.botanist.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddPlantActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;

    protected ImageView picture;
    protected TextView pictureHint;
    protected EditText nameEditText;
    protected EditText birthdayEditText;
    protected AutoCompleteTextView speciesAutoCompleteText;
    protected Button addPlantButton;

    DatabaseManager mDatabase;
    protected GregorianCalendar birthday;

    protected Uri photoUri;

    /**
     * Launch the add plant screen
     * @param savedInstanceState - Current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        mDatabase = DatabaseManager.getInstance();

        picture = (ImageView) findViewById(R.id.picture);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        });
        pictureHint = (TextView) findViewById(R.id.picture_hint);
        speciesAutoCompleteText = (AutoCompleteTextView) findViewById(R.id.species_edit_text);
        mDatabase.setSpeciesAutoComplete(this, speciesAutoCompleteText);
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        birthdayEditText = (EditText) findViewById(R.id.birthday_edit_text);
        birthdayEditText.setOnClickListener(new View.OnClickListener() {
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
                        birthday.getTimeInMillis(), photoUri);
                finish();
            }
        });

        birthday = new GregorianCalendar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            photoUri = data.getData();
            Bitmap bmp = ImageUtils.correctRotation(this, photoUri);
            picture.setImageBitmap(bmp);

            //TODO: delete image after upload
        }
    }

    /**
     * Show the date picker
     */
    private void showDatePicker() {

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                birthday.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                birthdayEditText.setText(dateFormat.format(birthday.getTime()));
            }

        };
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(listener,
                birthday.get(Calendar.YEAR),
                birthday.get(Calendar.MONTH),
                birthday.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setTitle(getString(R.string.set_birthday));
        datePickerDialog.vibrate(false);
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setOnDateSetListener(listener);
        datePickerDialog.show(getFragmentManager(), "date_picker");
    }
}