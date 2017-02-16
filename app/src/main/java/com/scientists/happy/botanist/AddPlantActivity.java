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
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPlantActivity extends AppCompatActivity
{
    static final int REQUEST_TAKE_PHOTO = 1;
    protected ImageButton imageButton;
    protected EditText speciesBox;
    protected EditText nicknameBox;
    protected String mCurrentPhotoPath;
    /**
     * Launch the add plant screen
     * @param savedInstanceState - Current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        speciesBox = (EditText) findViewById(R.id.Species);
        nicknameBox = (EditText) findViewById(R.id.Nickname);
    }

    /**
     * Take a picture
     * @param view - current activity view
     */
    protected void onPressImageButton(View view) {
        dispatchTakePictureIntent();
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
            Bitmap bmp = ImageUtils.loadScaledImage(mCurrentPhotoPath, imageButton.getWidth(), imageButton.getHeight());
            imageButton.setImageBitmap(bmp);
        }
    }

    private void dispatchTakePictureIntent() {
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
                Uri photoURI = FileProvider.getUriForFile(this, "com.scientists.happy.botanist.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
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
     * Send information back to main page
     * @param view - current app view
     */
    protected void onPressSubmit(View view) {
        Plant p = new Plant(nicknameBox.getText().toString(), speciesBox.getText().toString(),
                mCurrentPhotoPath);
        PlantArray plantArray = PlantArray.getInstance();
        plantArray.add(p);
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}