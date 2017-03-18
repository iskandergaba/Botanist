// Plant profile
// @author: Antonio Muscarella and Christopher Besser
package com.scientists.happy.botanist.ui;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.scientists.happy.botanist.data.DatabaseManager;
import com.scientists.happy.botanist.R;
public class ProfileActivity extends AppCompatActivity {
    private static final String NAME_KEY = "name";
    private static final String SPECIES_KEY = "species";
    private String name;
    private String species;
    private DatabaseManager mDatabase;
    /**
     * Launch the activity
     * @param savedInstanceState - current view state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mDatabase = DatabaseManager.getInstance();
        // store individual plant information from the extras passed through the intent
        Intent i = getIntent();
        name = i.getExtras().getString(NAME_KEY);
        species = i.getExtras().getString(SPECIES_KEY);
        setTitle(name + "\'s Profile");
        TextView nameTextView = (TextView) findViewById(R.id.plant_name);
        nameTextView.setText(getString(R.string.name_fmt, name));
        TextView speciesTextView = (TextView) findViewById(R.id.plant_species);
        speciesTextView.setText(getString(R.string.species_fmt, species));
        ImageView picture = (ImageView) findViewById(R.id.plant_picture);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                // TODO: clean a bit
                .child(mDatabase.getUserId()).child(species + "_" + name + ".jpg");
        Glide.with(this).using(new FirebaseImageLoader()).load(storageReference).placeholder(R.drawable.flowey).into(picture);
        mDatabase.editProfile(this.findViewById(android.R.id.content), species);
    }

    /**
     * User navigated up from the activity
     * @return returns true
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    /**
     * Delete plant from PlantArray when delete button is pressed
     * @param view - current app view
     */
    protected void onPressDelete(View view) {
        AlertDialog dialog = buildDeleteDialog();
        dialog.show();
    }

    /**
     * Warn the user that the plant will be deleted
     * @return - returns the alert window
     */
    private AlertDialog buildDeleteDialog() {
        // Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.delete_message).setTitle(R.string.delete_dialog_title);
        // Add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            /**
             * User clicked confirm
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                mDatabase.deletePlant(ProfileActivity.this, name, species);
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            /**
             * User clicked cancel
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Get the AlertDialog from create()
        return builder.create();
    }
}