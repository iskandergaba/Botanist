// Singleton Database manager for Firebase
// @author: Christopher Besser, Antonio Muscarella, and Iskander Gaba
package com.scientists.happy.botanist.data;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.services.BirthdayReceiver;
import com.scientists.happy.botanist.services.FertilizerReceiver;
import com.scientists.happy.botanist.services.HeightMeasureReceiver;
import com.scientists.happy.botanist.services.UpdatePhotoReceiver;
import com.scientists.happy.botanist.services.WaterReceiver;
import com.scientists.happy.botanist.ui.ProfileActivity;
import com.scientists.happy.botanist.ui.SettingsActivity;
import com.scientists.happy.botanist.utils.GifSequenceWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.content.Context.ALARM_SERVICE;
import static android.os.Environment.getExternalStoragePublicDirectory;
public class DatabaseManager {
    private static final int HEIGHT_MEASURE_RECEIVER_ID_OFFSET = 1000;
    private static final int FERTILIZER_RECEIVER_ID_OFFSET = 2000;
    private static final int UPDATE_PHOTO_RECEIVER_ID_OFFSET = 3000;
    private static final int BIRTHDAY_RECEIVER_ID_OFFSET = 4000;
    private long mPlantsAdded, mPlantsDeleted, mPlantsNumber;
    private long mWaterCount, mMeasureCount, mPhotoCount;
    private long mBotanistSince;
    private double mRating;
    private ProgressDialog mProgressDialog;
    private Map<String, String> mAutoCompleteCache;
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
                        mAutoCompleteCache.put(commonName, sciName);
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

    private class LoadImages extends AsyncTask<Void, Void, Boolean> {
        int numPhotos;
        Activity parent;
        String userId;
        String plantId;
        File outFile;

        /**
         * Prepare to launch task
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TextView location = (TextView) parent.findViewById(R.id.gif_location);
            location.setText(parent.getString(R.string.gif_fmt, parent.getString(R.string.gif_loading)));
        }

        /**
         * Create the background process for loading images
         * @param numPhotos - number of images taken of the plant
         * @param activity - calling activity
         * @param plantId - id of plant to make gif of
         */
        private LoadImages(int numPhotos, Activity activity, String plantId) {
            parent = activity;
            this.numPhotos = numPhotos;
            userId = getUserId();
            this.plantId = plantId;
        }

        /**
         * Background asynchronous update
         * @param params - process parameters
         * @return Returns nothing
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GifSequenceWriter gifWriter = new GifSequenceWriter();
            gifWriter.start(out);
            // 500 ms frame
            gifWriter.setDelay(500);
            for (int i = 0; i <= numPhotos; i++) {
                StorageReference storageReference = mStorage.child(userId).child(plantId + "_" + i + ".jpg");
                try {
                    Bitmap bmp = Glide.with(parent).using(new FirebaseImageLoader()).load(storageReference).asBitmap().into(-1, -1).get();
                    gifWriter.addFrame(bmp);
                } catch (InterruptedException | ExecutionException e) {
                    return false;
                }
            }
            gifWriter.finish();
            try {
                // created gif files are written to pictures public external storage
                File outputDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                // Apparently, the external storage public directory only sometimes exists?
                if (!outputDir.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    outputDir.mkdir();
                }
                outFile = new File(outputDir, plantId + ".gif");
                FileOutputStream output = new FileOutputStream(outFile);
                output.write(out.toByteArray());
                output.flush();
                output.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        /**
         * Determine if execution succeeded
         * @param success - true if doInBackground succeeded.
         */
        @Override
        protected void onPostExecute(Boolean success) {
            TextView location = (TextView) parent.findViewById(R.id.gif_location);
            String text = "Failure making GIF";
            if (success) {
                text = outFile.getAbsolutePath();
            }
            location.setText(parent.getString(R.string.gif_fmt, text));
            mDatabase.child("users").child(userId).child("plants").child(plantId).child("gifLocation").setValue(text);
        }
    }

    /**
     * Singleton DatabaseManager constructor
     */
    private DatabaseManager() {
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAutoCompleteCache = new HashMap<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mPlantsNumber = getPlantsNumber();
        mBotanistSince = getBotanistSince();
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
    }

    /**
     * Delete a user from the database
     * @param userId - The user id
     */
    public void deleteUserRecords(String userId) {
        if (userId != null) {
            mStorage.child(userId).delete();
            mDatabase.child("users").child(userId).removeValue();
        }
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
    public void addPlant(Context context, String name, String species, long birthday, double height, final Bitmap bmp) {
        showProgressDialog(context);
        final Plant plant;
        // reject plant addition if species is null
        if ((species == null) || species.equals("")) {
            return;
        }
        else if (mAutoCompleteCache.containsKey(species)) {
            // If the user typed a common name, fetch the scientific name
            plant = new Plant(name, mAutoCompleteCache.get(species), birthday, height);
        }
        else if (mAutoCompleteCache.containsValue(species)) {
            // The user must have entered the correct scientific name
            plant = new Plant(name, species, birthday, height);
        }
        else {
            return;
        }
        final String plantId = plant.getId();
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plants").child(plantId).addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change to the data asynchronously
                 * @param snapshot - the current database state
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        mDatabase.child("users").child(userId).child("plants").child(plantId).setValue(plant);
                        setPlantsNumber(++mPlantsNumber);
                        updatePlantImage(0, plantId, bmp);
                        setAddedNumber(getAddedNumber() + 1);
                        updateUserRating();
                    }
                }

                /**
                 * Do nothing when update is cancelled
                 * @param databaseError - ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        hideProgressDialog();
    }

    /**
     * Update a plant's image
     * @param photoNum - suffix of image path
     * @param plantId - id of the plant whose image needs to update
     * @param bmp - new image
     */
    public void updatePlantImage(int photoNum, String plantId, Bitmap bmp) {
        String userId = getUserId();
        if ((userId != null) && (bmp != null)) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();
            StorageReference filepath = mStorage.child(userId).child(plantId + "_" + photoNum + ".jpg");
            filepath.putBytes(data);
            mDatabase.child("users").child(userId).child("plants").child(plantId).child("photoNum").setValue(photoNum);
            updateNotificationTime(plantId, "lastPhotoNotification");
            setPhotoCount(getPhotoCount() + 1);
            updateUserRating();
        }
    }

    /**
     * Remove plant from the database
     * @param context - the current app context
     * @param plantId - the id of the plant
     * @param photoNum - the number of pictures that plant has
     */
    public void deletePlant(final Context context, final String plantId, final int photoNum) {
        final String userId = getUserId();
        deleteAllReminders(context);
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plants").child(plantId).addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle asynchronous change to database
                 * @param snapshot - the current database contents
                 */
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mDatabase.child("users").child(userId).child("plants").child(plantId).removeValue();
                        setPlantsNumber(--mPlantsNumber);
                        for (int i = 0; i <= photoNum; i++) {
                            mStorage.child(userId).child(plantId + "_" + i + ".jpg").delete();
                        }
                        setDeletedNumber(getDeletedNumber() + 1);
                        updateUserRating();
                    }
                    File gif = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), plantId + ".gif");
                    if (gif.exists() && gif.delete()) {
                        updateGallery(context);
                    }
                }

                /**
                 * Do nothing when the event is cancelled.
                 * @param databaseError - ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    /**
     * Delete gif reference from the Android Gallery
     * @param context - app context
     */
    private void updateGallery(Context context) {
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
     * Get heights with their recording time in millis
     * @param plantId - plant unique id
     * @param graph - height graph
     */
    public void populateHeightsGraph(String plantId, final GraphView graph) {

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
                        List<DataPoint> data = new ArrayList<>();
                        for (DataSnapshot record : snapshot.getChildren()) {
                            long date = Long.parseLong(record.getKey());
                            double height = record.getValue(Double.class);
                            data.add(new DataPoint(date, height));
                        }
                        DataPoint[] points = data.toArray(new DataPoint[data.size()]);
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
                        graph.addSeries(series);
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
     * Update plant's height
     * @param context - the current app context
     * @param plantId - the id of the plant (species_name)
     * @param heightInInches - the height of the plant
     */
    public void updatePlantHeight(final Context context, final String plantId, double heightInInches) {
        showProgressDialog(context);
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
                        setMeasureCount(getMeasureCount() + 1);
                        updateUserRating();
                    }
                }
            });
            mDatabase.child("users").child(userId).child("plants").child(plantId).child("height").setValue(heightInInches)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        /**
                         * Update current plant height
                         * @param task - update task
                         */
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(context, "Height update failed, try again", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                updateNotificationTime(plantId, "lastMeasureNotification");
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
        showProgressDialog(context);
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
     * Get a plant adapter
     * @param activity - the current activity
     * @return Returns an adapter for the plants
     */
    public FirebaseListAdapter<Plant> getPlantsAdapter(final Activity activity) {
        final String userId = getUserId();
        if (userId != null) {
            DatabaseReference databaseRef = mDatabase.child("users").child(userId).child("plants");
            return new FirebaseListAdapter<Plant>(activity, Plant.class, R.layout.grid_item_view, databaseRef) {
                /**
                 * Show images in glide
                 * @param view - the current view
                 * @param plant - the plant to display
                 * @param position - the position in the menu
                 */
                @Override
                protected void populateView(final View view, final Plant plant, final int position) {
                    StorageReference storageReference = mStorage.child(userId).child(plant.getId() + "_" + plant.getPhotoNum() + ".jpg");
                    ((TextView) view.findViewById(R.id.grid_item_nickname)).setText(plant.getName());
                    ((TextView) view.findViewById(R.id.grid_item_species)).setText(plant.getSpecies());
                    ImageView picture = (ImageView) view.findViewById(R.id.grid_item_image_view);
                    Glide.with(activity).using(new FirebaseImageLoader()).load(storageReference).placeholder(R.drawable.flowey).into(picture);
                    view.setOnClickListener(new View.OnClickListener() {
                        /**
                         * User clicked a plant
                         * @param v - the current view
                         */
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(activity.getApplicationContext(), ProfileActivity.class);
                            i.putExtra("plant_id", plant.getId());
                            i.putExtra("name", plant.getName());
                            i.putExtra("species", plant.getSpecies());
                            i.putExtra("height", plant.getHeight());
                            i.putExtra("photo_num", plant.getPhotoNum());
                            i.putExtra("gif_location", plant.getGifLocation());
                            i.putExtra("birthday", plant.getBirthday());
                            i.putExtra("last_watered", plant.getLastWaterNotification());
                            i.putExtra("last_fertilized", plant.getLastFertilizerNotification());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                View sharedImageView = view.findViewById(R.id.grid_item_image_view);
                                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(activity, sharedImageView, "image_main_to_profile_transition").toBundle();
                                activity.startActivity(i, bundle);
                            }
                            else {
                                activity.startActivity(i);
                            }
                        }
                    });
                    setReminders(activity, plant, position, new WaterReceiver());
                    setReminders(activity, plant, position + HEIGHT_MEASURE_RECEIVER_ID_OFFSET, new HeightMeasureReceiver());
                    setReminders(activity, plant, position + FERTILIZER_RECEIVER_ID_OFFSET, new FertilizerReceiver());
                    setReminders(activity, plant, position + UPDATE_PHOTO_RECEIVER_ID_OFFSET, new UpdatePhotoReceiver());
                    setBirthdayReminder(activity, plant, position + BIRTHDAY_RECEIVER_ID_OFFSET);
                }
            };
        }
        return null;
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
            return new FirebaseListAdapter<String>(activity, String.class, android.R.layout.simple_list_item_1, databaseRef) {
                /**
                 * Show images in glide
                 * @param view - the current view
                 * @param plant - the plant to display
                 * @param position - the position in the menu
                 */
                @Override
                protected void populateView(final View view, final String plant, final int position) {
                    if (!plant.equals(species)) {
                        ((TextView) view.findViewById(android.R.id.text1)).setText(plant);
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
     * Get a plant adapter
     * @param view - the current activity
     * @param name of the plant to fetch
     */
    public void editProfile(final View view, String name) {
        if (name != null) {
            DatabaseReference ref = mDatabase.child("PlantsData").child(name);
            ref.addValueEventListener(new ValueEventListener() {
                /**
                 * Read the data from the plant entry
                 * @param dataSnapshot - the entry contents
                 */
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    PlantEntry entry = dataSnapshot.getValue(PlantEntry.class);
                    ((TextView) view.findViewById(R.id.group_holder)).setText(entry.getGroup());
                    ((TextView) view.findViewById(R.id.care_tips)).setText(entry.generateCareTips());
                    TextView toxicWarningTextView = (TextView) view.findViewById(R.id.toxic_warning);
                    if (entry.isToxic()) {
                        toxicWarningTextView.setVisibility(View.VISIBLE);
                    }
                    else {
                        toxicWarningTextView.setVisibility(View.GONE);
                    }
                    TextView noxiousWarningTextView = (TextView) view.findViewById(R.id.noxious_warning);
                    List<String> noxious = entry.getNoxious();
                    if (noxious != null) {
                        noxiousWarningTextView.setVisibility(View.VISIBLE);
                        if (noxious.contains("Noxious")) {
                            noxiousWarningTextView.setText(R.string.noxious_warning);
                        }
                        if (noxious.contains("Quarantine")) {
                            noxiousWarningTextView.setText(noxiousWarningTextView.getText() + " \n\n" + view.getContext().getString(R.string.quarantine_warning));
                        }
                        if (noxious.contains("Regulated")) {
                            noxiousWarningTextView.setText(noxiousWarningTextView.getText() + " \n\n" + view.getContext().getString(R.string.regulated_warning));
                        }
                        if (noxious.contains("Banned")) {
                            noxiousWarningTextView.setText(noxiousWarningTextView.getText() + " \n\n" + view.getContext().getString(R.string.banned_warning));
                        }
                    }
                }

                /**
                 * Do nothing when cancelled
                 * @param databaseError - ignored error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    /**
     * Show the autocomplete for AddPlantActivity add species
     * @param context - the current app context
     * @param autoCompleteTextView - the autocomplete view
     */
    public void setSpeciesAutoComplete(Context context, AutoCompleteTextView autoCompleteTextView) {
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        autoComplete.addAll(mAutoCompleteCache.keySet());
        autoComplete.addAll(mAutoCompleteCache.values());
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
    public String getUserId() {
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
     * Create a gif of the plant
     * @param activity - calling activity
     * @param plantId - the id of the plant to form a gif of
     * @param photoNum - the number of pictures of the plant that were taken
     */
    public void makePlantGif(final Activity activity, String plantId, int photoNum) {
        final String userId = getUserId();
        if ((photoNum > 1) && (userId != null)) {
            new LoadImages(photoNum, activity, plantId).execute();
        } else {
            Toast.makeText(activity, "Must take at least 2 pictures for a .gif", Toast.LENGTH_SHORT).show();
        }
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

    private void updateUserRating() {
        String userId = getUserId();
        long added = getAddedNumber();
        long deleted = getDeletedNumber();


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
    public long getWaterCount() {
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
    public void setWaterCount(long count) {
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
     * @return Returns the total number of photos uploaded by the user
     */
    private long getPhotoCount() {
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
     * @param count - the new total number of photos uploaded by the user
     */
    private void setPhotoCount(long count) {
        String userId = getUserId();
        mMeasureCount = count;
        if (userId != null) {
            mDatabase.child("users").child(userId).child("photoCount").setValue(count);
        }
    }

    /**
     * Get how long the user has been a botanist
     * @return Returns the total number of added plants
     */
    private long getAddedNumber() {
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
    private long getDeletedNumber() {
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
    private void setDeletedNumber(long count) {
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
    private void setPlantsNumber(long count) {
        String userId = getUserId();
        mPlantsNumber = count;
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plantsNumber").setValue(count);
        }
    }

    /**
     * Update the name of a plant
     * @param plantId - the ID of the plant
     * @param newName - the new name for the plant
     */
    public void setPlantName(String plantId, String newName) {
        String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plants").child(plantId).child("name").setValue(newName);
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
            // photo updates happen bi-daily, others update daily.
            if (receiver instanceof UpdatePhotoReceiver) {
                nextMeasure.add(Calendar.DAY_OF_MONTH, 2);
            }
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
    private void showProgressDialog(Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    /**
     * Hide the loading progress
     */
    private void hideProgressDialog() {
        if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}