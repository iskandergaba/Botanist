// Plant profile
// @author: Antonio Muscarella and Christopher Besser
package com.scientists.happy.botanist.ui;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
public class ProfileActivity extends AppCompatActivity {
    private static final String ID_KEY = "plant_id";
    private static final String NAME_KEY = "name";
    private static final String SPECIES_KEY = "species";
    private static final String HEIGHT_KEY = "height";
    private String name, species, plantId;
    private double height;
    private DatabaseManager mDatabase;
    private TextView mHeightTextView;
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
        plantId = i.getExtras().getString(ID_KEY);
        name = i.getExtras().getString(NAME_KEY);
        species = i.getExtras().getString(SPECIES_KEY);
        height = i.getExtras().getDouble(HEIGHT_KEY);
        setTitle(name + "\'s Profile");
        ImageView picture = (ImageView) findViewById(R.id.plant_picture);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                // TODO: clean a bit
                .child(mDatabase.getUserId()).child(plantId+ ".jpg");
        Glide.with(this).using(new FirebaseImageLoader()).load(storageReference).placeholder(R.drawable.flowey).into(picture);
        TextView nameTextView = (TextView)findViewById(R.id.plant_name);
        nameTextView.setText(getString(R.string.name_fmt, name));
        TextView speciesTextView = (TextView)findViewById(R.id.plant_species);
        speciesTextView.setText(getString(R.string.species_fmt, species));
        mHeightTextView = (TextView)findViewById(R.id.plant_height);
        mHeightTextView.setText(getString(R.string.height_fmt, height));
        Button heightButton = (Button) findViewById(R.id.height_button);
        heightButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked update height
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                buildHeightInputDialog().show();
            }
        });
        Button poopButton = (Button) findViewById(R.id.poop_button);
        poopButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked update height
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                buildFertilizedDialog().show();
            }
        });
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
     * User fertilized plant
     * @return Returns new height
     */
    private AlertDialog buildFertilizedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_message).setTitle(R.string.confirm_message);
        // Add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            /**
             * User clicked confirm
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                mDatabase.updateLastFertilizerNotification(ProfileActivity.this, plantId);
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

    /**
     * Input height window
     * @return Returns new height dialog
     */
    private AlertDialog buildHeightInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.height_input_dialog).setTitle("Record new height").setPositiveButton(R.string.mdtp_ok, new DialogInterface.OnClickListener() {
            /**
             * User clicked submit
             * @param dialog - current dialog
             * @param which - selected option
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText inputEditText = (EditText) ((AlertDialog) dialog).findViewById(R.id.height_edit_text);
                double newHeight = Double.parseDouble(inputEditText != null ? inputEditText.getText().toString() : "-1");
                if (height < newHeight) {
                    height = newHeight;
                    mDatabase.updatePlantHeight(ProfileActivity.this, plantId, height);
                    mHeightTextView.setText(getString(R.string.height_fmt, height));
                }
            }
        }).setNegativeButton(R.string.mdtp_cancel, new DialogInterface.OnClickListener() {
            /**
             * User clicked cancel
             * @param dialog - current dialog
             * @param which - selected option
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing on cancel
            }
        });
        return builder.create();
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