// Add a plant to the database
// @author: Chia George Washington
package com.scientists.happy.botanist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddPlantActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    protected ImageView picture;
    protected TextView pictureHint;
    protected AutoCompleteTextView speciesAutoCompleteText;
    protected EditText nameEditText;
    protected EditText birthdayEditText;
    protected Button addPlantButton;

    protected String mCurrentPhotoPath;
    protected String mPhotoPath;

    DatabaseManager mDatabase;
    protected GregorianCalendar birthday;
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
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(AddPlantActivity.this, "com.scientists.happy.botanist.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
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
                Plant p = new Plant(nameEditText.getText().toString(), speciesAutoCompleteText.getText().toString(),
                        mPhotoPath, birthday);
                PlantArray plantArray = PlantArray.getInstance();
                plantArray.add(p);
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        birthday = new GregorianCalendar();
        //new SetAutocompleteTask().execute();
    }

    /**
     * Update ImageButton
     * @param requestCode - code for the update request?
     * @param resultCode - camera result
     * @param data - camera data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            mPhotoPath = mCurrentPhotoPath;
            Bitmap bmp = ImageUtils.loadScaledImage(mPhotoPath, picture.getWidth(), picture.getHeight());
            picture.setImageBitmap(bmp);
            pictureHint.setText(R.string.update_picture);
        }
    }

    /**
     * Store the captured image
     * @return Returns the image
     * @throws IOException if the write fails
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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