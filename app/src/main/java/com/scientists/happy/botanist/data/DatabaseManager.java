// Singleton Database manager for Firebase
// @author: Christopher Besser, Antonio Muscarella, and Iskander Gaba
package com.scientists.happy.botanist.data;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.services.BirthdayReceiver;
import com.scientists.happy.botanist.services.FertilizerReceiver;
import com.scientists.happy.botanist.services.HeightMeasureReceiver;
import com.scientists.happy.botanist.services.UpdatePhotoReceiver;
import com.scientists.happy.botanist.services.WaterReceiver;
import com.scientists.happy.botanist.ui.ProfileActivity;
import com.scientists.happy.botanist.ui.SettingsActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialActivity;

import static android.content.Context.ALARM_SERVICE;

public class DatabaseManager {
    private static final int HEIGHT_MEASURE_RECEIVER_ID_OFFSET = 1000;
    private static final int FERTILIZER_RECEIVER_ID_OFFSET = 2000;
    private static final int UPDATE_PHOTO_RECEIVER_ID_OFFSET = 3000;
    private static final int BIRTHDAY_RECEIVER_ID_OFFSET = 4000;
    private static final int TUTORIAL_REQUEST_CODE = 1234;
    private long mPlantsAdded, mPlantsDeleted, mPlantsNumber;
    private long mWaterCount, mMeasureCount, mPhotoCount;
    private long mBotanistSince;
    private double mRating;
    private ProgressDialog mProgressDialog;
    private Map<String, String> mAutocompleteCache;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private String mDiseaseUrl;
    private static DatabaseManager mDatabaseManager;
    private class PrepareAutocompleteTask extends AsyncTask<Void, Void, Void> {
        /**
         * Background asynchronous update
         * @param params - process parameters
         * @return Returns nothing
         */
        @Override
        protected Void doInBackground(Void... params) {
            // Child the root before all the push() keys are found and add a ValueEventListener()
            mDatabase.child("Lookup").addValueEventListener(new ValueEventListener() {
                /**
                 * Handle a change in the database contents
                 * @param dataSnapshot - the database state
                 */
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
                    for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                        // Get the suggestion by childing the key of the string you want to get.
                        String commonName = suggestionSnapshot.getKey();
                        String sciName = suggestionSnapshot.getValue(String.class);
                        // Add the retrieved string to the list
                        mAutocompleteCache.put(commonName, sciName);
                    }
                }

                /**
                 * Do nothing when the process is cancelled
                 * @param databaseError - Ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return null;
        }
    }
    /**
     * Singleton DatabaseManager constructor
     */
    private DatabaseManager() {
        // Just in case we want to add offline caching to the app
        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAutocompleteCache = new HashMap<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        new PrepareAutocompleteTask().execute();
    }

    /**
     * Singleton DatabaseManager instance retrieval
     * @return Returns a pointer to the singleton
     */
    public static DatabaseManager getInstance() {
        if (mDatabaseManager == null) {
            mDatabaseManager = new DatabaseManager();
        }
        return mDatabaseManager;
    }

    /**
     * Set member counters when the user signs in
     */
    private void setMemberData() {
        mBotanistSince = getBotanistSince();
        mPlantsNumber = getPlantsNumber();
        mPlantsAdded = getAddedCount();
        mPlantsDeleted = getDeletedCount();
        mWaterCount = getWaterCount();
        mMeasureCount = getMeasureCount();
        mPhotoCount = getPhotoCount();
        mRating = getUserRating();
    }

    /**
     * Reset member counters when the user signs out
     */
    public void resetMemberData() {
        mBotanistSince = 0;
        mPlantsNumber = 0;
        mPlantsAdded = 0;
        mPlantsDeleted = 0;
        mWaterCount = 0;
        mMeasureCount = 0;
        mPhotoCount = 0;
        mRating = 0;
    }

    /**
     * Add a new user
     * @param userId - the user's ID
     * @param name - the user's name
     * @param email - the user's email
     */
    public void addUserRecords(final String userId, final String name, final String email) {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * Handle data change event
             * @param snapshot - current database contents
             */
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    User user = new User(userId, name, email, 0);
                    mDatabase.child("users").child(userId).setValue(user);
                }
            }

            /**
             * Do nothing when the process is cancelled
             * @param databaseError - Ignored error
             */
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        setMemberData();
    }

    /**
     * Delete a user from the database
     * @param userId - The user id
     */
    public void deleteUserRecords(Context context, final String userId) {
        showProgressDialog(context, context.getString(R.string.loading_text));
        if (userId != null) {
            // Remove photos uploaded by the user
            mDatabase.child("users").child(userId).child("photos").addValueEventListener(new ValueEventListener() {
                /**
                 * Handle a change in the database contents
                 * @param dataSnapshot - a snapshot of data
                 */
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Deleting the user photos
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String photoFileName = snapshot.getValue(String.class);
                        if (photoFileName != null) {
                            mStorage.child(userId).child(photoFileName).delete();
                        }
                    }
                    // then, deleting all user records
                    mDatabase.child("users").child(userId).removeValue();
                }

                /**
                 * Do nothing when the process is cancelled
                 * @param databaseError - Ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        hideProgressDialog();
    }

    /**
     * Add a new plant to a user's plant list in the database
     * @param context - current app context
     * @param name - name of the plant
     * @param species - the plant's species
     * @param birthday - the plant's birthday
     * @param height - the plant's height
     * @param bmp - the plant's picture
     */
    public boolean addPlant(final Context context, String name, String species, long birthday, double height, final Bitmap bmp) {
        showProgressDialog(context, context.getString(R.string.loading_text));
        // reject plant addition if species is null
        if ((species == null) || species.equals("") || (name == null) || name.equals("")) {
            Toast.makeText(context, R.string.toast_invalid_plant_input, Toast.LENGTH_SHORT).show();
            hideProgressDialog();
            return false;
        }
        else if (getPlantsNumber() > 999) {
            Toast.makeText(context, R.string.toast_plant_limit_exceeded, Toast.LENGTH_SHORT).show();
            hideProgressDialog();
            return false;
        }
        else if (mAutocompleteCache.containsKey(species)) {
            // If the user typed a common name, fetch the scientific name
            species = mAutocompleteCache.get(species);
        }
        else if (!mAutocompleteCache.containsValue(species)) {
            // The user must have entered neither the scientific nor the common name
            hideProgressDialog();
            return false;
        }

        final String userId = getUserId();
        if (userId != null) {
            DatabaseReference plantRef = mDatabase.child("users").child(userId).child("plants").push();
            final String plantId = plantRef.getKey();
            final Plant plant = new Plant(plantId, name, species, birthday, height);
            plantRef.setValue(plant).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Plant add failed, try again", Toast.LENGTH_SHORT).show();
                    } else {
                        setPlantsNumber(++mPlantsNumber);
                        updatePlantImage(0, 0, plantId, bmp);
                        setAddedNumber(getAddedCount() + 1);
                        updateUserRating();
                    }
                }
            });
        }
        hideProgressDialog();
        return true;
    }

    /**
     * Update a plant's image
     * @param photoPointer - suffix of image path
     * @param plantId - id of the plant whose image needs to update
     * @param bmp - new image
     */
    public void updatePlantImage(int photoPointer, int photoNum, final String plantId, Bitmap bmp) {
        final String userId = getUserId();
        if ((userId != null) && (bmp != null)) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();
            String profilePhoto = plantId + "_" + photoPointer + ".jpg";
            StorageReference filepath = mStorage.child(userId).child(profilePhoto);
            filepath.putBytes(data);
            DatabaseReference plantRef = mDatabase.child("users").child(userId).child("plants").child(plantId);
            mDatabase.child("users").child(userId).child("photos").push().setValue(profilePhoto);
            plantRef.child("profilePhoto").setValue(profilePhoto);
            plantRef.child("photoPointer").setValue(photoPointer);
            plantRef.child("photoNum").setValue(photoNum);
            updateNotificationTime(plantId, "lastPhotoNotification");
            setPhotoCount(getPhotoCount() + 1);
            updateUserRating();
        }
    }

    /**
     * Delete gif reference from the Android Gallery
     * @param context - app context
     */
    public void updateGallery(Context context) {
        MediaScannerConnection.scanFile(context, new String[]{Environment.getExternalStorageDirectory().toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    /**
                     * Gallery scan completed
                     * @param path - path of the deleted image
                     * @param uri of the deleted image
                     */
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }

    /**
     * Populate height chart
     * @param plantId - plant unique id
     */
    public void populateHeightChart(String plantId, final LineChart chart) {
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plants").child(plantId)
                    .child("heights").addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change in the user data
                 * @param snapshot - the current database contents
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        List<Entry> entries = new ArrayList<>();
                        for (DataSnapshot record: snapshot.getChildren()) {
                            long time = Long.parseLong(record.getKey());
                            float height = record.getValue(Float.class);
                            entries.add(new Entry(time, height));
                        }
                        if (!entries.isEmpty()) {
                            LineDataSet dataSet = new LineDataSet(entries, "Height in inches");
                            dataSet.setLineWidth(1.5f);
                            dataSet.setColors(Color.RED);
                            LineData lineData = new LineData(dataSet);
                            lineData.setValueTextSize(7f);
                            chart.setData(lineData);
                            chart.invalidate();
                        }
                    }
                }

                /**
                 * Do nothing when the process cancels
                 * @param databaseError - Ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    /**
     * Populate water chart
     * @param plantId - plant unique id
     */
    public void populateWaterChart(String plantId, final BarChart chart) {
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plants").child(plantId)
                    .child("watering").addListenerForSingleValueEvent(new ValueEventListener() {
                Map<Long, Integer> watering = new LinkedHashMap<>();
                /**
                 * Handle a change in the user data
                 * @param snapshot - the current database contents
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Calendar today = Calendar.getInstance();
                        watering.put(today.getTimeInMillis(), 0);
                        for (int i = 1; i < 7; i++) {
                            Calendar day = Calendar.getInstance();
                            day.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR) - i);
                            watering.put(day.getTimeInMillis(), 0);
                        }
                        for (DataSnapshot record : snapshot.getChildren()) {
                            processTime(Long.parseLong(record.getValue(String.class)));
                        }
                        List<BarEntry> entries = new ArrayList<>();
                        int diff = 7 - today.get(Calendar.DAY_OF_WEEK);
                        for (long timeStamp : watering.keySet()) {
                            Calendar date = Calendar.getInstance();
                            date.setTimeInMillis(timeStamp);
                            // Just to ensure that today appears always as the latest bar
                            int day = date.get(Calendar.DAY_OF_WEEK) + diff;
                            if (day > 7) day %= 7;
                            entries.add(new BarEntry(day, watering.get(timeStamp)));
                        }
                        BarDataSet dataSet = new BarDataSet(entries, "Times Watered");
                        BarData barData = new BarData(dataSet);
                        barData.setBarWidth(0.9f);
                        barData.setValueTextSize(10f);
                        chart.setData(barData);
                        chart.invalidate();
                    }
                }

                /**
                 * Do nothing when the process cancels
                 * @param databaseError - Ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

                /**
                 * Process the current time
                 * @param time - the time to process
                 */
                private void processTime(long time) {
                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(time);
                    for (long timeStamp: watering.keySet()) {
                        Calendar day = Calendar.getInstance();
                        day.setTimeInMillis(timeStamp);
                        if (date.get(Calendar.YEAR) == day.get(Calendar.YEAR)
                                && date.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR)) {
                            int count = watering.remove(timeStamp);
                            watering.put(timeStamp, ++count);
                            break;
                        }
                    }
                }
            });
        }
    }

    /**
     * populate the user's account statistics
     * @param chart - the chart to populate
     */
    public void populateUserStatsChart(Context context, final BarChart chart) {
        String userId = getUserId();
        if (userId != null) {
            int[] colors = context.getResources().getIntArray(R.array.user_stats_chart_colors);
            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0f, getAddedCount()));
            entries.add(new BarEntry(1f, getDeletedCount()));
            entries.add(new BarEntry(2f, getWaterCount()));
            entries.add(new BarEntry(3f, getMeasureCount()));
            entries.add(new BarEntry(4f, getPhotoCount()));

            BarDataSet barDataSet = new BarDataSet(entries, "Plant Operations");
            barDataSet.setColors(ColorTemplate.createColors(colors));
            barDataSet.setValueTextSize(11f);

            BarData data = new BarData(barDataSet);
            data.setBarWidth(0.9f); // set custom bar width
            chart.setData(data);
            chart.invalidate(); // refresh
        }
    }

    /**
     * Update plant's height
     * @param context - the current app context
     * @param plantId - the id of the plant (species_name)
     * @param heightInInches - the height of the plant
     */
    public void updatePlantHeight(final Context context, final String plantId, double heightInInches) {
        showProgressDialog(context, context.getString(R.string.loading_text));
        final String userId = getUserId();
        String now = Long.toString(System.currentTimeMillis());
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plants").child(plantId).child("heights")
                    .child(now).setValue(heightInInches).addOnCompleteListener(new OnCompleteListener<Void>() {
                /**
                 * Update plant heights list
                 * @param task - update task
                 */
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Height update failed, try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        updateNotificationTime(plantId, "lastMeasureNotification");
                        setMeasureCount(getMeasureCount() + 1);
                        updateUserRating();
                    }
                }
            });
            mDatabase.child("users").child(userId).child("plants").child(plantId).child("height")
                    .setValue(heightInInches).addOnCompleteListener(new OnCompleteListener<Void>() {
                /**
                 * Update current plant height
                 * @param task - update task
                 */
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Height update failed, try again", Toast.LENGTH_SHORT).show();
                    }
                    hideProgressDialog();
                }
            });
        }
    }

    /**
     * Update plant's height
     * @param context - the current app context
     * @param plantId - the id of the plant (species_name)
     */
    public void updatePlantWatering(final Context context, final String plantId) {
        showProgressDialog(context, context.getString(R.string.loading_text));
        final String userId = getUserId();
        String now = Long.toString(System.currentTimeMillis());
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plants").child(plantId).child("watering").push()
                    .setValue(now).addOnCompleteListener(new OnCompleteListener<Void>() {
                /**
                 * Update plant heights list
                 * @param task - update task
                 */
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Update failed, try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        updateNotificationTime(plantId, "lastWaterNotification");
                        setWaterCount(getWaterCount() + 1);
                        updateUserRating();
                        Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show();
                    }
                    hideProgressDialog();
                }
            });
        }
    }

    /**
     * Update last watered
     * @param plantId - the id of the plant (species_name)
     * @param field - the field to update
     */
    public void updateNotificationTime(final String plantId, final String field) {
        final String userId = getUserId();
        if ((userId != null) && (plantId != null)) {
            mDatabase.child("users").child(userId).child("plants").child(plantId).child(field)
                    .setValue(System.currentTimeMillis()).addOnCompleteListener(new OnCompleteListener<Void>() {
                /**
                 * Update last fertilized time
                 * @param task - update task
                 */
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // nothing, yet
                }
            });
        }
    }

    /**
     * populate a grid with user plants
     * @param activity - the current activity
     * @param grid - the current grid
     */
    public void populatePlantGrid(final Activity activity, final GridView grid) {
        final String userId = getUserId();
        final TextView emptyGridView = (TextView) activity.findViewById(R.id.empty_grid_view);
        final ProgressBar loadingProgressBar = (ProgressBar) activity.findViewById(R.id.loading_indicator);
        loadingProgressBar.setVisibility(View.VISIBLE);
        if (userId != null) {
            DatabaseReference databaseRef = mDatabase.child("users").child(userId).child("plants");
            final FirebaseListAdapter<Plant> adapter = new FirebaseListAdapter<Plant>(activity, Plant.class, R.layout.plant_item_view, databaseRef) {
                /**
                 * Populate a grid item
                 * @param view - the current view
                 * @param plant - the plant to display
                 * @param position - the position in the menu
                 */
                @Override
                protected void populateView(final View view, final Plant plant, final int position) {
                    String profilePhoto = plant.getProfilePhoto();
                    if (profilePhoto == null) {
                        // For earlier versions before profilePhoto property was added
                        long photoNum = plant.getPhotoNum();
                        profilePhoto = photoNum < 0 ? "default" : plant.getId() + "_" + photoNum + ".jpg";
                        plant.setProfilePhoto(profilePhoto);
                    }
                    StorageReference storageReference = mStorage.child(userId).child(profilePhoto);
                    ((TextView) view.findViewById(R.id.grid_item_nickname)).setText(plant.getName());
                    ((TextView) view.findViewById(R.id.grid_item_species)).setText(plant.getSpecies());
                    final ImageView picture = (ImageView) view.findViewById(R.id.grid_item_image_view);
                    Glide.with(activity).using(new FirebaseImageLoader()).load(storageReference).dontAnimate()
                            .placeholder(R.drawable.flowey).into(picture);
                    // One day, before the progress bar becomes empty
                    long interval = getReminderIntervalInMillis(1);
                    long diff = System.currentTimeMillis() - plant.getLastWatered();
                    float progress = 100 - (float) (100.0 * diff / interval);
                    // The minimum value is one, just to make sure it's visible to the user
                    if (progress < 1) {
                        progress = 1;
                    }
                    ((ProgressBar) view.findViewById(R.id.progress)).setProgress(Math.round(progress));
                    Calendar now = Calendar.getInstance();
                    Calendar birthday = Calendar.getInstance();
                    birthday.setTimeInMillis(plant.getBirthday());
                    if (now.get(Calendar.MONTH) == birthday.get(Calendar.MONTH)
                            && now.get(Calendar.DAY_OF_MONTH) == birthday.get(Calendar.DAY_OF_MONTH)
                            && now.get(Calendar.YEAR) != birthday.get(Calendar.YEAR)) {
                        view.findViewById(R.id.birthday_image_view).setVisibility(View.VISIBLE);
                    }
                    view.setOnClickListener(new View.OnClickListener() {
                        /**
                         * User clicked a plant
                         * @param v - the current view
                         */
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(activity.getApplicationContext(), ProfileActivity.class);
                            i.putExtra("plant_id", plant.getId());
                            i.putExtra("height", plant.getHeight());
                            activity.startActivity(i);
                        }
                    });
                    setReminders(activity, plant, position, new WaterReceiver());
                    setReminders(activity, plant, position + HEIGHT_MEASURE_RECEIVER_ID_OFFSET, new HeightMeasureReceiver());
                    setReminders(activity, plant, position + FERTILIZER_RECEIVER_ID_OFFSET, new FertilizerReceiver());
                    setReminders(activity, plant, position + UPDATE_PHOTO_RECEIVER_ID_OFFSET, new UpdatePhotoReceiver());
                    setBirthdayReminder(activity, plant, position + BIRTHDAY_RECEIVER_ID_OFFSET);
                }
            };

            // After digging deep, I discovered that Firebase keeps some local information in ".info"
            DatabaseReference connectedRef = mDatabase.child(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = (boolean) snapshot.getValue();
                    if (connected) {
                        emptyGridView.setText(R.string.loading_text);
                        loadingProgressBar.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(activity, R.string.msg_network_error, Toast.LENGTH_SHORT).show();
                        emptyGridView.setText(R.string.msg_network_error);
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loadingProgressBar.setVisibility(View.GONE);
                    emptyGridView.setText(R.string.no_plants);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    emptyGridView.setText(R.string.msg_unexpected_error);
                    loadingProgressBar.setVisibility(View.GONE);
                }
            });
            grid.setAdapter(adapter);
        }
    }



    /**
     * Get a plant adapter
     * @param activity - the current activity
     * @param group - group the plant belongs to
     * @param species - plant's species
     * @return Returns an adapter for the plants
     */
    public FirebaseListAdapter<String> getSimilarPlants(final Activity activity, String group, final String species) {
        final String userId = getUserId();
        if (userId != null) {
            DatabaseReference databaseRef = mDatabase.child("Groups").child(group);
            return new FirebaseListAdapter<String>(activity, String.class, R.layout.similar_plant_view, databaseRef) {
                /**
                 * Show images in glide
                 * @param view - the current view
                 * @param plant - the plant to display
                 * @param position - the position in the menu
                 */
                @Override
                protected void populateView(final View view, final String plant, final int position) {
                    if (!plant.equals(species)) {
                        ((TextView) view.findViewById(R.id.plant_species)).setText(plant);
                        view.findViewById(R.id.shop_button).setOnClickListener(new View.OnClickListener() {
                            /**
                             * User clicked buy now
                             * @param v - current view
                             */
                            @Override
                            public void onClick(View v) {
                                String search = plant.replaceAll(" ", "-").toLowerCase();
                                String url = "http://www.crocus.co.uk/search/_/search." + search + "/sort.0/";
                                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                                activity.startActivity(viewIntent);
                            }
                        });
                    }
                }
            };
        }
        return null;
    }

    /**
     * Get a plant adapter for the diseases a plant can have
     * @param activity - the current activity
     * @param group - group the plant belongs to
     * @return Returns an adapter for the plants
     */
    public FirebaseListAdapter<String> getDiseases(final Activity activity, String group) {
        final String userId = getUserId();
        if (userId != null) {
            DatabaseReference databaseRef = mDatabase.child("Diseases").child(group);
            return new FirebaseListAdapter<String>(activity, String.class, android.R.layout.simple_list_item_1, databaseRef) {
                /**
                 * Show images in glide
                 * @param view - the current view
                 * @param disease - the disease to display
                 * @param position - the position in the menu
                 */
                @Override
                protected void populateView(final View view, final String disease, final int position) {
                    ((TextView) view.findViewById(android.R.id.text1)).setText(disease);
                    mDatabase.child("DiseaseUrls").child(disease).addListenerForSingleValueEvent(new ValueEventListener() {
                        /**
                         * Handle a change in the database contents
                         * @param snapshot - current database contents
                         */
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                mDiseaseUrl = (String) snapshot.getValue();
                            }
                        }

                        /**
                         * Do nothing when the process is cancelled
                         * @param databaseError - Ignored error
                         */
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    view.findViewById(android.R.id.text1).setOnClickListener(new View.OnClickListener() {
                        /**
                         * User pressed a disease
                         * @param v - current app view
                         */
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDiseaseUrl));
                            activity.startActivity(browserIntent);
                        }
                    });
                }
            };
        }
        return null;
    }

    /**
     * Show the autocomplete for AddPlantActivity add species
     * @param context - the current app context
     * @param autoCompleteTextView - the autocomplete view
     */
    public void setSpeciesAutoComplete(Context context, AutoCompleteTextView autoCompleteTextView) {
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        autoComplete.addAll(mAutocompleteCache.keySet());
        autoComplete.addAll(mAutocompleteCache.values());
        autoCompleteTextView.setAdapter(autoComplete);
    }

    /**
     * Get the number of plants a user owns
     * @return Returns the number of plants the user owns
     */
    public long getPlantsNumber() {
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plantsNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change in the user data
                 * @param snapshot - the current database contents
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mPlantsNumber = (long) snapshot.getValue();
                    }
                }

                /**
                 * Do nothing when the process cancels
                 * @param databaseError - Ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return mPlantsNumber;
        }
        return -1;
    }

    /**
     * Get the user's id number
     * @return Returns the user's id number
     */
    private String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    /**
     * Get how long the user has been a botanist
     * @return Returns how long the user has been a botanist
     */
    public long getBotanistSince() {
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("botanistSince").addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change in the database contents
                 * @param snapshot - current database contents
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mBotanistSince = (long) snapshot.getValue();
                    }
                }

                /**
                 * Do nothing when the process is cancelled
                 * @param databaseError - Ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return mBotanistSince;
        }
        return -1;
    }

    /**
     * Delete the birthday reminders
     * @param context - the current app context
     */
    public void deleteAllReminders(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        for (int i = 0; i < mPlantsNumber; i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, new Intent(context, BirthdayReceiver.class), 0);
            am.cancel(pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(context, i, new Intent(context, FertilizerReceiver.class), 0);
            am.cancel(pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(context, i, new Intent(context, HeightMeasureReceiver.class), 0);
            am.cancel(pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(context, i, new Intent(context, UpdatePhotoReceiver.class), 0);
            am.cancel(pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(context, i, new Intent(context, WaterReceiver.class), 0);
            am.cancel(pendingIntent);
        }
    }

    /**
     * Get the user rating
     * @return Returns the user rating
     */
    public double getUserRating() {
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change in the database contents
                 * @param snapshot - current database contents
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mRating = Double.valueOf(String.valueOf(snapshot.getValue()));
                    }
                }

                /**
                 * Do nothing when the process is cancelled
                 * @param databaseError - Ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        return mRating;
    }

    /**
     * Update the user's rating
     */
    public void updateUserRating() {
        String userId = getUserId();
        long added = getAddedCount();
        long deleted = getDeletedCount();
        mRating = ((1.3 * added - deleted / 100))
                * (getWaterCount() + getMeasureCount() + getPhotoCount() + 1) / (10 * (added + deleted + 1));
        if (userId != null) {
            mDatabase.child("users").child(userId).child("rating").setValue(mRating);
        }
    }

    /**
     * Get the total number of plants watering
     * @return Returns the total number of times the user watered plants
     */
    private long getWaterCount() {
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("waterCount").addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change in the database contents
                 * @param snapshot - current database contents
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mWaterCount = (long) snapshot.getValue();
                    }
                }

                /**
                 * Do nothing when the process is cancelled
                 * @param databaseError - Ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        return mWaterCount;
    }

    /**
     * Update the total number of plants watering
     * @param count - the new total number of time the user watered plants
     */
    private void setWaterCount(long count) {
        String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("waterCount").setValue(count);
        }
    }

    /**
     * Get the total number of height measurements
     * @return Returns the total number of times the user measured the height of plants
     */
    private long getMeasureCount() {
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("measureCount").addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change in the database contents
                 * @param snapshot - current database contents
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mMeasureCount = (long) snapshot.getValue();
                    }
                }

                /**
                 * Do nothing when the process is cancelled
                 * @param databaseError - Ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        return mMeasureCount;
    }

    /**
     * Update the total number of height measurements
     * @param count - the new total number of time the user measured the height of plants
     */
    private void setMeasureCount(long count) {
        String userId = getUserId();
        mWaterCount = count;
        if (userId != null) {
            mDatabase.child("users").child(userId).child("measureCount").setValue(count);
        }
    }

    /**
     * Get the total number of photos
     * @return Returns the total number of existing user photos
     */
    public long getPhotoCount() {
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("photoCount").addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change in the database contents
                 * @param snapshot - current database contents
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mPhotoCount = (long) snapshot.getValue();
                    }
                }

                /**
                 * Do nothing when the process is cancelled
                 * @param databaseError - Ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        return mPhotoCount;
    }

    /**
     * Update the total number of photos
     * @param count - the new total number of existing user photos
     */
    public void setPhotoCount(long count) {
        String userId = getUserId();
        mPhotoCount = count;
        if (userId != null) {
            mDatabase.child("users").child(userId).child("photoCount").setValue(count);
        }
    }

    /**
     * Get how long the user has been a botanist
     * @return Returns the total number of added plants
     */
    private long getAddedCount() {
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plantsAdded").addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change in the database contents
                 * @param snapshot - current database contents
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mPlantsAdded = (long) snapshot.getValue();
                    }
                }

                /**
                 * Do nothing when the process is cancelled
                 * @param databaseError - Ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        return mPlantsAdded;
    }

    /**
     * Update the number of plants
     * @param count - the new number of added plants
     */
    private void setAddedNumber(long count) {
        String userId = getUserId();
        mPlantsAdded = count;
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plantsAdded").setValue(count);
        }
    }

    /**
     * Get how long the user has been a botanist
     * @return Returns the total number of deleted plants
     */
    public long getDeletedCount() {
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plantsDeleted").addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change in the database contents
                 * @param snapshot - current database contents
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mPlantsDeleted = (long) snapshot.getValue();
                    }
                }

                /**
                 * Do nothing when the process is cancelled
                 * @param databaseError - Ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        return mPlantsDeleted;
    }

    /**
     * Update the number of plants
     * @param count - the new number of deleted plants
     */
    public void setDeletedNumber(long count) {
        String userId = getUserId();
        mPlantsDeleted = count;
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plantsDeleted").setValue(count);
        }
    }

    /**
     * Update the number of plants
     * @param count - the new number of plants
     */
    public void setPlantsNumber(long count) {
        String userId = getUserId();
        mPlantsNumber = count;
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plantsNumber").setValue(count);
        }
    }

    /**
     * Show tutorial for given activity if it has never been shown before
     * @param activity - the activity to show the tutorial
     * @param tutorialItems - the items of the tutorial to show
     * @param forceShow - Whether to check if tutorial was shown before or not
     */
    public void showTutorial(final Activity activity, final ArrayList<TutorialItem> tutorialItems, boolean forceShow) {
        final String activityName = activity.getClass().getSimpleName();
        if (forceShow) {
            Intent tutorial = new Intent(activity, MaterialTutorialActivity.class);
            tutorial.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, tutorialItems);
            activity.startActivityForResult(tutorial, TUTORIAL_REQUEST_CODE);
            setTutorialShown(activityName);
        }
        else {
            final String userId = getUserId();
            if (userId != null) {
                mDatabase.child("users").child(userId).child("tutorials").addListenerForSingleValueEvent(new ValueEventListener() {
                    /**
                     * Handle a change in the database contents
                     * @param dataSnapshot - current database contents
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Boolean> tutorials = new HashMap<>();
                        // Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            String name = snapshot.getKey();
                            boolean isTutorialShown = (boolean) snapshot.getValue();
                            tutorials.put(name, isTutorialShown);
                        }
                        if (!tutorials.containsKey(activityName) || !tutorials.get(activityName)) {
                            Intent tutorial = new Intent(activity, MaterialTutorialActivity.class);
                            tutorial.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, tutorialItems);
                            activity.startActivityForResult(tutorial, TUTORIAL_REQUEST_CODE);
                            setTutorialShown(activityName);
                        }
                    }

                    /**
                     * Do nothing when the process is cancelled
                     * @param databaseError - Ignored error
                     */
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    /**
     * Set tutorial is shown for an activity
     * @param activityName - name of the tutorial to update
     */
    private void setTutorialShown(String activityName) {
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("tutorials").child(activityName).setValue(true);
        }
    }

    /**
     * Set when to remind about birthdays
     * @param context - the current app context
     * @param plant - the plant whose birthday is reminded of
     * @param id - the id of the plant
     */
    private void setBirthdayReminder(Context context, Plant plant, int id) {
        Intent intent = new Intent(context, BirthdayReceiver.class);
        intent.putExtra("name", plant.getName());
        intent.putExtra("species", plant.getSpecies());
        intent.putExtra("birthday", plant.getBirthday());
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        Calendar birthday = Calendar.getInstance();
        birthday.setTimeInMillis(plant.getBirthday());
        birthday.set(Calendar.YEAR, now.get((Calendar.YEAR)));
        if (birthday.getTimeInMillis() < now.getTimeInMillis()) {
            birthday.set(Calendar.YEAR, now.get((Calendar.YEAR)) + 1);
        }
        AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, birthday.getTimeInMillis(), pendingIntent);
    }

    /**
     * Create non-birthday push notifications
     * @param context - current app context
     * @param plant - plant to remind about
     * @param id - notification id
     * @param receiver - the type of reminder to set
     */
    private void setReminders(Context context, Plant plant, int id, BroadcastReceiver receiver) {
        Intent intent = new Intent(context, receiver.getClass());
        intent.putExtra("name", plant.getName());
        intent.putExtra("plant_id", plant.getId());
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int hour = preferences.getInt("water_hour", 9);
        int minute = preferences.getInt("water_minute", 0);
        int reminderSetting = 0;
        Calendar calendar = Calendar.getInstance();
        if (receiver instanceof WaterReceiver) {
            reminderSetting = Integer.parseInt(preferences.getString(SettingsActivity.WATER_REMINDER_KEY, "1"));
            calendar.setTimeInMillis(plant.getLastWaterNotification());
        }
        else if (receiver instanceof HeightMeasureReceiver) {
            reminderSetting = Integer.parseInt(preferences.getString(SettingsActivity.HEIGHT_REMINDER_KEY, "2"));
            calendar.setTimeInMillis(plant.getLastMeasureNotification());
        }
        else if (receiver instanceof UpdatePhotoReceiver) {
            reminderSetting = Integer.parseInt(preferences.getString(SettingsActivity.PHOTO_REMINDER_KEY, "2"));
            calendar.setTimeInMillis(plant.getLastPhotoNotification());
        }
        else if (receiver instanceof FertilizerReceiver) {
            reminderSetting = Integer.parseInt(preferences.getString(SettingsActivity.FERTILIZER_REMINDER_KEY, "3"));
            calendar.setTimeInMillis(plant.getLastFertilizerNotification());
        }
        if (reminderSetting != 0) {
            Calendar nextMeasure = Calendar.getInstance();
            long interval = getReminderIntervalInMillis(reminderSetting);
            nextMeasure.setTimeInMillis(calendar.getTimeInMillis() + interval);
            nextMeasure.set(Calendar.HOUR, hour);
            nextMeasure.set(Calendar.MINUTE, minute);
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, nextMeasure.getTimeInMillis(), interval, pendingIntent);
        }
    }

    /**
     * Convert reminder notification delay to milliseconds
     * @param setting - user selected delay
     * @return Returns the millisecond denomination of the delay
     */
    public long getReminderIntervalInMillis(int setting) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        if (setting == 1) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        else if (setting == 2) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }
        else if (setting == 3) {
            calendar.add(Calendar.DAY_OF_YEAR, 30);
        }
        return calendar.getTimeInMillis();
    }

    /**
     * Show the loading progress
     * @param context - the current app context
     */
    private void showProgressDialog(Context context, String message) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    /**
     * Dismiss the loading progress
     */
    private void hideProgressDialog() {
        if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * Get the array index of the last daily tip the user saw, and update the cardview on the
     * main activity if it was not today
     * @param context - the activity this method was called from
     * @param tipView - the container view of the daily tip
     */
    public void generateDailyTip(final Context context, final View tipView) {
        final String userId = getUserId();
        final String[] dailyTips = context.getResources().getStringArray(R.array.daily_tips_values);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean displayTip = preferences.getBoolean("daily_tip", true);
        if (displayTip && userId != null) {
            mDatabase.child("users").child(userId).child("indexOfLastDailyTip").addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change in the user data
                 * @param snapshot - the current database contents
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        long indexOfLastDailyTip = (long) snapshot.getValue();
                        int dailyTipIndex = (int) (Math.random() * dailyTips.length);
                        while (dailyTipIndex == indexOfLastDailyTip) {
                            dailyTipIndex = (int) (Math.random() * dailyTips.length);
                        }
                        ((TextView) tipView.findViewById(R.id.daily_tip_text)).setText(dailyTips[dailyTipIndex]);
                        setIndexOfLastDailyTip(dailyTipIndex);
                    } else {
                        int dailyTipIndex = (int) (Math.random() * dailyTips.length);
                        ((TextView) tipView.findViewById(R.id.daily_tip_text)).setText(dailyTips[dailyTipIndex]);
                        setIndexOfLastDailyTip(dailyTipIndex);
                    }
                }

                /**
                 * Generate a random tip and hope it's not the same as last time if something wrong happens
                 * @param databaseError - database encountered an error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    int dailyTipIndex = (int) (Math.random() * dailyTips.length);
                    ((TextView) tipView.findViewById(R.id.daily_tip_text)).setText(dailyTips[dailyTipIndex]);
                    setIndexOfLastDailyTip(dailyTipIndex);
                }
            });

            mDatabase.child("users").child(userId).child("dateOfLastDailyTip").addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change in the user data
                 * @param snapshot - the current database contents
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Calendar today = Calendar.getInstance();
                        Calendar lastTime = Calendar.getInstance();
                        lastTime.setTimeInMillis((long) snapshot.getValue());
                        if (lastTime.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)) {
                            tipView.setVisibility(View.VISIBLE);
                            setDateOfLastDailyTip(System.currentTimeMillis());
                        } else {
                            tipView.setVisibility(View.GONE);
                        }
                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        setDateOfLastDailyTip(System.currentTimeMillis());
                    }
                }

                /**
                 * Hide the tip view if cannot know whether it's been a day since the last tip or not
                 * @param databaseError - database encountered an error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    tipView.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * Update the array index of the last daily tip the user saw
     * @param index - the array index of the daily tip the user just saw today
     */
    private void setIndexOfLastDailyTip(int index) {
        String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("indexOfLastDailyTip").setValue(index);
        }
    }

    /**
     * Update the date of the last daily tip the user saw (so user doesn't see two daily tips in one day)
     * @param date - the date of the day the user last saw a daily tip
     */
    private void setDateOfLastDailyTip(long date) {
        String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("dateOfLastDailyTip").setValue(date);
        }
    }

    public DatabaseReference getPlantReference(String plantId) {
        String userId = getUserId();
        return userId == null ? null : mDatabase.child("users").child(userId).child("plants").child(plantId);
    }

    public DatabaseReference getPlantEntryReference(String species) {
        String userId = getUserId();
        return userId == null ? null : mDatabase.child("PlantsData").child(species);
    }

    public DatabaseReference getUserPhotosReference() {
        String userId = getUserId();
        return userId == null ? null : mDatabase.child("users").child(userId).child("photos");
    }

    public DatabaseReference getUserConnectionReference() {
        return mDatabase.child(".info/connected");
    }

    public StorageReference getUserStorage() {
        String userId = getUserId();
        return userId == null ? null : mStorage.child(userId);
    }
}