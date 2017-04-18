// Add a plant
// @author: Christopher Besser and Iskander Gaba
package com.scientists.happy.botanist.ui;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import za.co.riggaroo.materialhelptutorial.TutorialItem;
public class AddPlantActivity extends AppCompatActivity {
    protected ImageView mPicture;
    protected TextView mPictureHint;
    protected EditText mNameEditText;
    protected EditText mBirthdayEditText;
    protected AutoCompleteTextView mSpeciesAutoCompleteText;
    protected EditText mHeightEditText;
    protected Button mAddPlantButton;
    protected DatabaseManager mDatabase;
    protected Bitmap mBitmap;
    protected GregorianCalendar mBirthday;
    int mWaterHour, mWaterMin;
    /**
     * Launch the add plant screen
     * @param savedInstanceState - Current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        mDatabase = DatabaseManager.getInstance();
        mDatabase.showTutorial(this, loadTutorialItems(), false);
        mPictureHint = (TextView) findViewById(R.id.picture_hint);
        mSpeciesAutoCompleteText = (AutoCompleteTextView) findViewById(R.id.species_edit_text);
        mDatabase.setSpeciesAutoComplete(this, mSpeciesAutoCompleteText);
        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
        mBirthdayEditText = (EditText) findViewById(R.id.birthday_edit_text);
        mBirthdayEditText.setOnClickListener(new View.OnClickListener() {
            /**
             * Handle click event
             * @param v - the current view
             */
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        mBirthdayEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * Handle focus change between views
             * @param v - current view
             * @param hasFocus - whether the current view has focus
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker();
                }
            }
        });
        mHeightEditText = (EditText) findViewById(R.id.height_edit_text);
        mAddPlantButton = (Button) findViewById(R.id.add_plant_button);
        mAddPlantButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Handle click event for add plant button
             * @param v - the current view
             */
            @Override
            public void onClick(View v) {
                String height = mHeightEditText.getText().toString();
                mDatabase.addPlant(AddPlantActivity.this, mNameEditText.getText().toString(),
                        mSpeciesAutoCompleteText.getText().toString(), mBirthday.getTimeInMillis(),
                        height.equals("") ? 0 : Double.parseDouble(height), mBitmap);
                finish();
            }
        });
        mPicture = (ImageView) findViewById(R.id.picture);
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
                    }
                }).show(getSupportFragmentManager());
            }
        });
        mBirthday = new GregorianCalendar();
        mWaterHour = mBirthday.get(Calendar.HOUR);
        mWaterMin = mBirthday.get(Calendar.MINUTE);
        overridePendingTransition(R.anim.slide_up, R.anim.hold);
    }

    /**
     * The activity was paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.slide_down);
    }

    /**
     * Handle back button press
     * @return Returns a success code
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    /**
     * Show the date picker
     */
    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            /**
             * Handle selected time for birthday
             * @param view - the current view
             * @param year - the selected year
             * @param monthOfYear - the selected month
             * @param dayOfMonth - the selected day
             */
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                mBirthday.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                mBirthdayEditText.setText(dateFormat.format(mBirthday.getTime()));
            }
        };
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(listener,
                mBirthday.get(Calendar.YEAR), mBirthday.get(Calendar.MONTH),
                mBirthday.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setTitle(getString(R.string.set_birthday));
        datePickerDialog.vibrate(false);
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setOnDateSetListener(listener);
        datePickerDialog.show(getFragmentManager(), "date_picker");
    }

    /**
     * Handle options menu
     * @param menu - options menu
     * @return Returns success code
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    /**
     * Handle selected option
     * @param item - selected option
     * @return Returns success code
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_help) {
            mDatabase.showTutorial(this, loadTutorialItems(), true);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fetch assets for the tutorial
     * @return - Returns the list of tutorial items
     */
    private ArrayList<TutorialItem> loadTutorialItems() {
        TutorialItem tutorialItem1 = new TutorialItem(getString(R.string.tutorial_title_0), getString(R.string.tutorial_contents_0),
                R.color.colorPrimary, R.drawable.tutorial_0,  R.drawable.tutorial_0);
        TutorialItem tutorialItem2 = new TutorialItem(getString(R.string.tutorial_title_1), getString(R.string.tutorial_contents_1),
                R.color.colorPrimary, R.drawable.tutorial_1,  R.drawable.tutorial_1);
        TutorialItem tutorialItem3 = new TutorialItem(getString(R.string.tutorial_title_2), getString(R.string.tutorial_contents_2),
                R.color.colorPrimary, R.drawable.tutorial_2,  R.drawable.tutorial_2);
        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        return tutorialItems;
    }
}