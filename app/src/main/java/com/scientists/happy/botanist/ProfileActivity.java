// Plant profile
// @author: Cactus
package com.scientists.happy.botanist;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {
    private static final String PLANT_KEY = "plant";
    private static final String DELIMETER = "\t";
    private static final int IMAGE_WIDTH = 300;
    private static final int IMAGE_HEIGHT = 500;
    private Bitmap img;
    private String[] plantData;
    private String nickname;
    private String species;
    private String photoPath;

    /**
     * Launch the activity
     * @param savedInstanceState - current view state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //store individual plant information from the extras passed through the intent
        plantData = ((String) getIntent().getExtras().get(PLANT_KEY)).split(DELIMETER);

        nickname = plantData[0];
        species = plantData[1];
        photoPath = plantData[2];


        setTitle(nickname + "\'s Profile");

        TextView nicknameTextView = (TextView)findViewById(R.id.plant_nickname);
        nicknameTextView.setText(nickname);

        TextView speciesTextView = (TextView)findViewById(R.id.plant_species);
        speciesTextView.setText(species);

        //update the ImageView on the app screen to match the stored image
        //if stored image doesn't exist, put in a default image

        ImageView image = (ImageView)findViewById(R.id.plant_picture);

        if(photoPath != null) {
            File f = new File(photoPath);
            if(f.exists()) {
                System.out.println("image.getWidth() = " + image.getWidth());
                System.out.println("image.getHeight() = " + image.getHeight());
                img = ImageUtils.loadScaledImage(photoPath, IMAGE_WIDTH, IMAGE_HEIGHT);
                image.setImageBitmap(img);
            }
        } else {
            image.setImageResource(R.drawable.flowey);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    /**
     * Delete plant from PlantArray when delete button is pressed
     * @param view - current app view
     */
    protected void onPressDelete(View view)
    {
        AlertDialog d = buildDeleteDialog();
        d.show();
    }

    private AlertDialog buildDeleteDialog() {
        //Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.delete_message).setTitle(R.string.delete_dialog_title);

        // Add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PlantArray pa = PlantArray.getInstance();
                pa.remove(nickname);
                Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        //Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        return dialog;
    }

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
