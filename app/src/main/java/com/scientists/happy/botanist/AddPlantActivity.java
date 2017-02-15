// Add a plant to the database
// @author: Chia George Washington
package com.scientists.happy.botanist;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class AddPlantActivity extends AppCompatActivity
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    protected ImageButton iButton;
    protected EditText speciesBox;
    protected EditText nicknameBox;
    protected String photoPath;
    private Bitmap img;
    /**
     * Launch the add plant screen
     * @param savedInstanceState - Current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        iButton = (ImageButton) findViewById(R.id.imageButton);
        speciesBox = (EditText) findViewById(R.id.Species);
        nicknameBox = (EditText) findViewById(R.id.Nickname);
    }

    /**
     * Take a picture
     * @param view - current activity view
     */
    protected void onPressImageButton(View view)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                System.out.println(photoURI.getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Update ImageButton
     * @param requestCode - code for the update request?
     * @param resultCode - camera result
     * @param data - camera data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if ((requestCode == REQUEST_IMAGE_CAPTURE) && (resultCode == RESULT_OK))
        {
            img = ImageUtils.loadScaledImage(photoPath, iButton.getWidth(), iButton.getHeight());
            iButton.setImageBitmap(img);
        }
    }

    /**
     * Store the captured image
     * @return Returns the image
     * @throws IOException if the write fails
     */
    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        photoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Send information back to main page
     * @param view - current app view
     */
    protected void onPressSubmit(View view)
    {
        Intent i = new Intent(this, MainActivity.class);
        Plant p = new Plant(nicknameBox.getText().toString(), speciesBox.getText().toString(),
                photoPath);
        PlantArray pa = PlantArray.getInstance();
        pa.add(p);
        startActivity(i);
    }

    /**
     * Delete old image
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (img != null)
        {
            img.recycle();
        }
    }
}