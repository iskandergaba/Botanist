// Plant profile
// @author: Antonio Muscarella and Christopher Besser
package com.scientists.happy.botanist.ui;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
import java.util.Calendar;
public class ProfileActivity extends AppCompatActivity {
    private static final String ID_KEY = "plant_id";
    private static final String NAME_KEY = "name";
    private static final String SPECIES_KEY = "species";
    private static final String HEIGHT_KEY = "height";
    private static final String PHOTO_KEY = "photo_num";
    private static final String GIF_LOCATION_KEY = "gif_location";
    private static final String WATER_KEY = "last_watered";
    private static final String FERTILIZER_KEY = "last_fertilized";
    private String name, plantId;
    private double height;
    private int photoNum;
    private Bitmap mBitmap;
    private DatabaseManager mDatabase;
    private TextView mHeightTextView, mGroup;
    private ImageView mPicture;
    private String changeNameText = "";
    private long lastWatered, lastFertilized;
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
        String species = i.getExtras().getString(SPECIES_KEY);
        height = i.getExtras().getDouble(HEIGHT_KEY);
        photoNum = i.getExtras().getInt(PHOTO_KEY);
        lastWatered = i.getExtras().getLong(WATER_KEY);
        lastFertilized = i.getExtras().getLong(FERTILIZER_KEY);
        String mGifLocation = i.getExtras().getString(GIF_LOCATION_KEY);
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
                        photoNum++;
                    }
                }).show(getSupportFragmentManager());
            }
        });
        TextView fertilizationLink = (TextView)findViewById(R.id.fertilization_link);
        fertilizationLink.setMovementMethod(LinkMovementMethod.getInstance());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
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
        TextView gifTextView = (TextView) findViewById(R.id.gif_location);
        gifTextView.setText(getString(R.string.gif_fmt, mGifLocation));
        mHeightTextView = (TextView) findViewById(R.id.plant_height);
        mHeightTextView.setText(getString(R.string.height_fmt, height));
        mGroup = (TextView) findViewById(R.id.group_holder);
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
        Button deleteButton = (Button) findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Delete plant from PlantArray when delete button is pressed
             * @param v - the button view
             */
            @Override
            public void onClick(View v) {
                AlertDialog dialog = buildDeleteDialog();
                dialog.show();
            }
        });
        Button calendarButton = (Button) findViewById(R.id.calendar_button);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Delete plant from PlantArray when delete button is pressed
             * @param v - the button view
             */
            @Override
            public void onClick(View v) {
                AlertDialog dialog = buildCalendarDialog();
                dialog.show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
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
            i.putExtra("group", mGroup.getText().toString());
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_diseases) {
            Intent i = new Intent(this, DiseaseActivity.class);
            i.putExtra("group", mGroup.getText().toString());
            startActivity(i);
        }
        else if (id == R.id.action_create_gif) {
            mDatabase.makePlantGif(this, plantId, photoNum);
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
                Context context = getApplicationContext();
                Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show();
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
                Context context = getApplicationContext();
                Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show();
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
        builder.setView(R.layout.height_input_dialog).setTitle("Record New Height")
                .setPositiveButton(R.string.mdtp_ok, new DialogInterface.OnClickListener() {
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
                Context context = getApplicationContext();
                Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton(R.string.mdtp_cancel, new DialogInterface.OnClickListener() {
            /**
             * User clicked cancel
             * @param dialog - current dialog
             * @param which - selected option
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel
                dialog.cancel();
            }
        });
        return builder.create();
    }

    /**
     * Allow the user to change the name of the plant
     * @return - returns the alert window
     */
    private AlertDialog buildChangeNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.name_input_dialog).setTitle("Change Plant Name");
        // Set up the buttons
        builder.setPositiveButton(R.string.mdtp_ok, new DialogInterface.OnClickListener() {
            /**
             * User clicked submit
             * @param dialog - warning dialog
             * @param which - user selected option
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText inputEditText = (EditText) ((AlertDialog) dialog).findViewById(R.id.name_edit_text);
                if (inputEditText != null) {
                    changeNameText = inputEditText.getText().toString();
                    mDatabase.setPlantName(plantId, changeNameText);
                    name = changeNameText;
                    TextView nameTextView = (TextView) findViewById(R.id.plant_name);
                    nameTextView.setText(getString(R.string.name_fmt, name));
                    setTitle(name + "\'s Profile");
                }
            }
        });
        builder.setNegativeButton(R.string.mdtp_cancel, new DialogInterface.OnClickListener() {
            /**
             * User cancelled name update
             * @param dialog - warning dialog
             * @param which - user selected option
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
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
                mDatabase.deletePlant(ProfileActivity.this, plantId, photoNum);
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
     * Ask the user which reminder they want to add to their calendar
     * @return - returns the alert window
     */
    private AlertDialog buildCalendarDialog() {
        // Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.calendar_dialog_text).setTitle(R.string.calendar_sync_text);
        // Add the buttons
        builder.setPositiveButton("Watering", new DialogInterface.OnClickListener() {
            /**
             * User clicked confirm
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                updateCalendar(getApplicationContext(), "Water " + name, WATER_KEY);
            }
        });
        builder.setNegativeButton("Fertilizing", new DialogInterface.OnClickListener() {
            /**
             * User clicked cancel
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                updateCalendar(getApplicationContext(), "Fertilize " + name, FERTILIZER_KEY);
            }
        });
        // Get the AlertDialog from create()
        return builder.create();
    }

    /**
     * write to phone's calendar
     * @param context - the context from which this is called
     * @param title - the title of the event to add
     */
    private void updateCalendar(Context context, String title, String type) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int hour = preferences.getInt("water_hour", 9);
        int minute = preferences.getInt("water_minute", 0);
        int reminderSetting = 0;
        Calendar cal = Calendar.getInstance();
        if (type.equals(WATER_KEY)) {
            reminderSetting = Integer.parseInt(preferences.getString(SettingsActivity.WATER_REMINDER_KEY, "1"));
            long interval = mDatabase.getReminderIntervalInMillis(reminderSetting);
            cal.setTimeInMillis(lastWatered + interval);
        }
        else if (type.equals(FERTILIZER_KEY)) {
            reminderSetting = Integer.parseInt(preferences.getString(SettingsActivity.FERTILIZER_REMINDER_KEY, "2"));
            long interval = mDatabase.getReminderIntervalInMillis(reminderSetting);
            cal.setTimeInMillis(lastFertilized + interval);
        }
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.MINUTE, minute);
        Intent calendarIntent = new Intent(Intent.ACTION_EDIT);
        calendarIntent.setType("vnd.android.cursor.item/event");
        calendarIntent.putExtra(CalendarContract.Events.TITLE, title);
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis() );
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis() + 36000);
        calendarIntent.putExtra(CalendarContract.Events.ALL_DAY, false);
        calendarIntent.putExtra(CalendarContract.Events.DESCRIPTION, title);
        context.startActivity(calendarIntent);
    }
}