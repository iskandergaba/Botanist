package com.scientists.happy.botanist;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {
    private static final String NICKNAME_KEY = "nickname";
    private static final String SPECIES_KEY = "species";
    private static final String IMAGE_KEY = "photoPath";

    protected String nickname;
    protected String species;
    protected String photoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //store individual plant information from the extras passed through the intent
        nickname = (String) getIntent().getExtras().get(NICKNAME_KEY);
        species = (String) getIntent().getExtras().get(SPECIES_KEY);
        photoPath = (String) getIntent().getExtras().get(IMAGE_KEY);

        setTitle(nickname + "\'s Profile");

        TextView nicknameTextView = (TextView)findViewById(R.id.plant_nickname);
        nicknameTextView.setText(nickname);

        TextView speciesTextView = (TextView)findViewById(R.id.plant_species);
        speciesTextView.setText(species);

        //update the ImageView on the app screen to match the stored image
        //if stored image doesn't exist, put in a default image

        File f = new File(photoPath);

        ImageView image = (ImageView)findViewById(R.id.plant_picture);

        if(f.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
            image.setImageBitmap(bmp);
        }
    }
}
