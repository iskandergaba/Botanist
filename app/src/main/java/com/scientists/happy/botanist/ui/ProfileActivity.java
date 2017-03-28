// Plant profile
// @author: Antonio Muscarella and Christopher Besser
package com.scientists.happy.botanist.ui;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
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
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
public class ProfileActivity extends AppCompatActivity {
    private static final String ID_KEY = "plant_id";
    private static final String NAME_KEY = "name";
    private static final String SPECIES_KEY = "species";
    private static final String HEIGHT_KEY = "height";
    private static final String PHOTO_KEY = "photoNum";
    private String name, species, plantId;
    private int photoNum;
    private Bitmap mBitmap;
    private double height;
    private DatabaseManager mDatabase;
    private TextView mHeightTextView, mGroup;
    private ImageView mPicture;
    private String changeNameText = "";
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
        photoNum = i.getExtras().getInt(PHOTO_KEY);
        setTitle(name + "\'s Profile");
        mPicture = (ImageView) findViewById(R.id.plant_picture);
        mPicture.setOnClickListener(new View.OnClickListener() {
            /**
             * Handle click event in the picture
             * @param v - the current view
             */
            @Override
            public void onClick(View v) {
                final PickSetup setup = new PickSetup().setSystemDialog(true);
                PickImageDialog.build(setup).setOnPickResult(new IPickResult() {
                    /**
                     * Handle the selected result
                     * @param r - the selected result
                     */
                    @Override
                    public void onPickResult(PickResult r) {
                        mBitmap = r.getBitmap();
                        mPicture.setImageBitmap(mBitmap);
                        mDatabase.updatePlantImage(photoNum + 1, plantId, mBitmap);
                    }
                }).show(getSupportFragmentManager());
            }
        });

        TextView fertilizationLink = (TextView)findViewById(R.id.fertilization_link);
        fertilizationLink.setMovementMethod(LinkMovementMethod.getInstance());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                // TODO: clean a bit
                .child(mDatabase.getUserId()).child(plantId + "_" + photoNum + ".jpg");
        Glide.with(this).using(new FirebaseImageLoader()).load(storageReference).placeholder(R.drawable.flowey).into(mPicture);
        TextView nameTextView = (TextView) findViewById(R.id.plant_name);
        Button changeNameButton = (Button) findViewById(R.id.change_name_button);
        changeNameButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked change name
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                buildChangeNameDialog().show();
            }
        });
        nameTextView.setText(getString(R.string.name_fmt, name));
        TextView speciesTextView = (TextView) findViewById(R.id.plant_species);
        speciesTextView.setText(getString(R.string.species_fmt, species));
        mHeightTextView = (TextView) findViewById(R.id.plant_height);
        mHeightTextView.setText(getString(R.string.height_fmt, height));
        Button heightButton = (Button) findViewById(R.id.height_button);
        mGroup = (TextView) findViewById(R.id.invisible_man);
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
        Button waterButton = (Button) findViewById(R.id.water_button);
        waterButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked update height
             * @param v - current view
             */
            @Override
            public void onClick(View v) {
                buildWateredDialog().show();
            }
        });
        mDatabase.editProfile(this.findViewById(android.R.id.content), species);
    }

    /**
     * Create Action Overflow menu
     * @param menu - actions
     * @return Returns success code
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    /**
     * Action overflow menu
     * @param item - selected item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_similar_plants) {
            Intent i = new Intent(this, SimilarPlantsActivity.class);
            i.putExtra("species", plantId);
            System.out.println("Species: " + species);
            i.putExtra("group", mGroup.getText().toString());
            System.out.println("Group: " + mGroup.getText().toString());
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
     * @return Returns alert window
     */
    private AlertDialog buildFertilizedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_message).setTitle(R.string.confirm_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            /**
             * User clicked confirm
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                mDatabase.updateNotificationTime(plantId, "lastFertilizerNotification");
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            /**
             * User clicked cancel
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }

    /**
     * User watered plant
     * @return Returns warning screen
     */
    private AlertDialog buildWateredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_message).setTitle(R.string.confirm_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            /**
             * User clicked confirm
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                mDatabase.updateNotificationTime(plantId, "lastWaterNotification");
                mDatabase.setWaterCount(mDatabase.getWaterCount() + 1);
                mDatabase.updateUserRating();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            /**
             * User clicked cancel
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }

    /**
     * Input height window
     * @return Returns warning screen
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
                mDatabase.deletePlant(ProfileActivity.this, name, species, photoNum);
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
     * Allow the user to change the name of the plant
     * @return - returns the alert window
     */
    private AlertDialog buildChangeNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Plant Name");

        // Set up the input
        final EditText input = new EditText(this);

        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeNameText = input.getText().toString();
                mDatabase.setPlantName(plantId, changeNameText);
                name = changeNameText;
                TextView nameTextView = (TextView) findViewById(R.id.plant_name);
                nameTextView.setText(name);
                setTitle(name + "\'s Profile");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }
}