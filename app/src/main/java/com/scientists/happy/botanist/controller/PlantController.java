package com.scientists.happy.botanist.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.Plant;
import com.scientists.happy.botanist.data.PlantEntry;
import com.scientists.happy.botanist.ui.DiseaseActivity;
import com.scientists.happy.botanist.ui.EditPlantActivity;
import com.scientists.happy.botanist.ui.SettingsActivity;
import com.scientists.happy.botanist.ui.SimilarPlantsActivity;
import com.scientists.happy.botanist.ui.StatsActivity;
import com.scientists.happy.botanist.utils.ExecutorValueEventListener;
import com.scientists.happy.botanist.utils.GifSequenceWriter;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import za.co.riggaroo.materialhelptutorial.TutorialItem;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class PlantController extends ActivityController {

    private final String WATER_KEY = "last_watered";
    private final String FERTILIZER_KEY = "last_fertilized";

    private Plant mPlant;
    private String mPlantGroup;

    private final StorageReference mUserStorage = getDatabaseManager().getUserStorage();
    private final DatabaseReference mPlantReference;

    public PlantController(AppCompatActivity activity) {
        super(activity);
        String plantIdKey = "plant_id";
        String plantId = getActivity().getIntent().getExtras().getString(plantIdKey);
        mPlantReference = getDatabaseManager().getPlantReference(plantId);
    }

    @Override
    public void load() {
        showProgressDialog(getActivity().getString(R.string.loading_text));
        loadProfilePhoto();
        loadProfileInfo();
        hideProgressDialog();
    }

    @Override
    protected ArrayList<TutorialItem> loadTutorialItems() {
        TutorialItem tutorialItem0 = new TutorialItem(getActivity().getString(R.string.profile_tutorial_title_0), getActivity().getString(R.string.profile_tutorial_contents_0),
                R.color.colorAccent, R.drawable.profile_tutorial_0, R.drawable.profile_tutorial_0);
        TutorialItem tutorialItem1 = new TutorialItem(getActivity().getString(R.string.profile_tutorial_title_1), getActivity().getString(R.string.profile_tutorial_contents_1),
                R.color.colorAccent, R.drawable.profile_tutorial_1, R.drawable.profile_tutorial_1);
        TutorialItem tutorialItem2 = new TutorialItem(getActivity().getString(R.string.profile_tutorial_title_2), getActivity().getString(R.string.profile_tutorial_contents_2),
                R.color.colorAccent, R.drawable.profile_tutorial_2, R.drawable.profile_tutorial_2);
        TutorialItem tutorialItem3 = new TutorialItem(getActivity().getString(R.string.profile_tutorial_title_3), getActivity().getString(R.string.profile_tutorial_contents_3),
                R.color.colorAccent, R.drawable.profile_tutorial_3, R.drawable.profile_tutorial_3);
        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem0);
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        return tutorialItems;
    }

    public void startEditPlantActivity() {
        Intent i = new Intent(getActivity(), EditPlantActivity.class);
        i.putExtra("plant_id", mPlant.getId());
        getActivity().startActivity(i);
    }

    public void startStatsActivity() {
        Intent i = new Intent(getActivity(), StatsActivity.class);
        i.putExtra("plant_id", mPlant.getId());
        getActivity().startActivity(i);
    }

    public void startDiseaseActivity() {
        if (mPlantGroup != null) {
            Intent i = new Intent(getActivity(), DiseaseActivity.class);
            i.putExtra("group", mPlantGroup);
            getActivity().startActivity(i);
        } else {
            Toast.makeText(getActivity(), "Not finished loading yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void startSimilarPlantsActivity() {
        if (mPlantGroup != null) {
            Intent i = new Intent(getActivity(), SimilarPlantsActivity.class);
            i.putExtra("species", mPlant.getSpecies());
            i.putExtra("group", mPlantGroup);
            getActivity().startActivity(i);
        } else {
            Toast.makeText(getActivity(), "Not finished loading yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadPhoto() {
        final PickSetup setup = new PickSetup().setSystemDialog(true);
        PickImageDialog.build(setup).setOnPickResult(new IPickResult() {
            /**
             * Handle the selected result
             * @param r - the selected result
             */
            @Override
            public void onPickResult(PickResult r) {
                StorageTask<UploadTask.TaskSnapshot> uploadTask = getDatabaseManager().updatePlantImage(mPlant.getPhotoPointer() + 1,
                        mPlant.getPhotoNum() + 1, mPlant.getId(), r.getBitmap());
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            loadProfilePhoto();
                        }
                    }
                });
            }
        }).show(getActivity().getSupportFragmentManager());
    }

    /**
     * Show watering dialog
     */
    public void showWaterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_message).setTitle(R.string.water_plant);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            /**
             * User clicked confirm
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                getDatabaseManager().updatePlantWatering(getActivity(), mPlant.getId());
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
        builder.create().show();
    }

    /**
     * Show input height dialog
     */
    public void showHeightInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_height_input).setTitle(R.string.record_new_height)
                .setPositiveButton(R.string.mdtp_ok, new DialogInterface.OnClickListener() {
                    /**
                     * User clicked submit
                     * @param dialog - current dialog
                     * @param which - selected option
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText inputEditText = ((AlertDialog) dialog).findViewById(R.id.height_edit_text);
                        double newHeight = Double.parseDouble(inputEditText != null ? inputEditText.getText().toString() : "-1");
                        if (mPlant.getHeight() < newHeight) {
                            getDatabaseManager().updatePlantHeight(getActivity(), mPlant.getId(), newHeight);
                        }
                        Toast.makeText(getActivity(), "Update successful", Toast.LENGTH_SHORT).show();
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
        builder.create().show();
    }

    /**
     * Show fertilization dialog
     */
    public void showFertilizationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_message).setTitle(R.string.fertilize_plant);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            /**
             * User clicked confirm
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                getDatabaseManager().updateNotificationTime(mPlant.getId(), "lastFertilizerNotification");
                Toast.makeText(getActivity(), R.string.update_success, Toast.LENGTH_SHORT).show();
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
        builder.create().show();
    }

    /**
     * Ask the user which reminder they want to add to their calendar
     */
    public void showCalendarDialog() {
        // Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.calendar_dialog_text).setTitle(R.string.calendar_sync_text);
        // Add the buttons
        builder.setPositiveButton(R.string.watering, new DialogInterface.OnClickListener() {
            /**
             * User clicked confirm
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                updateCalendar("Water " + mPlant.getName(), WATER_KEY);
            }
        });
        builder.setNegativeButton(R.string.fertilizing, new DialogInterface.OnClickListener() {
            /**
             * User clicked cancel
             * @param dialog - the warning window
             * @param id - the user id
             */
            public void onClick(DialogInterface dialog, int id) {
                updateCalendar("Fertilize " + mPlant.getName(), FERTILIZER_KEY);
            }
        });
        // Get the AlertDialog from create()
        builder.create().show();
    }

    /**
     * Warn the user that the plant will be deleted
     */
    public void showDeleteDialog() {
        // Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                deletePlant(mPlant.getId(), mPlant.getPhotoNum());
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
        builder.create().show();
    }

    /**
     * Remove plant from the database
     * @param plantId - the id of the plant
     * @param photoNum - the number of pictures that plant has
     */
    private void deletePlant(final String plantId, final int photoNum) {
        getDatabaseManager().deleteAllReminders(getActivity());
        if (mPlantReference != null) {
            mPlantReference.removeValue();
            final DatabaseReference userPhotosRef = getDatabaseManager().getUserPhotosReference();
            userPhotosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        String photoFileName = snapshot.getValue(String.class);
                        if (photoFileName != null && photoFileName.contains(plantId)) {
                            userPhotosRef.child(key).removeValue();
                            mUserStorage.child(photoFileName).delete();
                        }
                    }
                    getDatabaseManager().setPlantsNumber(getDatabaseManager().getPlantsNumber() - 1);
                    getDatabaseManager().setDeletedNumber(getDatabaseManager().getDeletedCount() + 1);
                    getDatabaseManager().setPhotoCount(getDatabaseManager().getPhotoCount() - (photoNum + 1));
                    getDatabaseManager().updateUserRating();
                    Intent resultIntent = new Intent();
                    getActivity().setResult(RESULT_OK, resultIntent);
                    getActivity().finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * Trigger the share intent
     */
    public void sharePlant() {
        if (mPlant != null) {
            String name = mPlant.getName();
            String gifLocation = mPlant.getGifLocation();
            String title = "Meet my plant: " + name + "!";
            String text = "Name: " + name + "\nSpecies: " + mPlant.getSpecies() + "\nFamily: " + mPlantGroup
                    + "\nAge: " + String.format(Locale.US, "%.2f", getAgeInDays(mPlant.getBirthday())) + " days"
                    + "\nHeight: " + mPlant.getHeight() + " inches\nShared via: Botanist";
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE, title);
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (gifLocation != null && gifLocation.equals("No Gif made (yet!)")) {
                shareIntent.setType("plain/text");
            } else {
                Uri gifUri = Uri.parse("file://" + gifLocation);
                shareIntent.putExtra(Intent.EXTRA_STREAM, gifUri);
                shareIntent.setType("image/*");
            }
            getActivity().startActivity(Intent.createChooser(shareIntent, getActivity().getString(R.string.share_dialog_title)));
        }
    }

    /**
     * Create a gif of the plant
     */
    public void exportGif() {
        // Iskander updated this because the photo counting is zero-index based
        // I changed it to zero so that if the user uploaded 2 picture only, we will still generate a GIF for them
        int photoNum = mPlant.getPhotoNum();
        if (photoNum < 1) {
            Toast.makeText(getActivity(), "You must take at least 2 pictures to make a GIF", Toast.LENGTH_SHORT).show();
        } else {
            createGif(mPlant.getId(), mPlant.getName(), mPlant.getSpecies());
        }
    }

    private void createGif(final String plantId, final String name, final String species) {
        showProgressDialog(getActivity().getString(R.string.gif_loading));
        DatabaseReference userPhotosReference = getDatabaseManager().getUserPhotosReference();
        if (userPhotosReference != null) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final GifSequenceWriter gifWriter = new GifSequenceWriter();
            gifWriter.start(out);
            // 500 ms frame
            gifWriter.setDelay(500);
            // An SQL-like hack to retrieve only data with values that matches the query: "plantId*"
            // This is needed to query only images that correspond to the specific plant being edited
            Query query = userPhotosReference.orderByValue().startAt(plantId).endAt(plantId + "\uf8ff");
            query.addValueEventListener(new ExecutorValueEventListener(Executors.newSingleThreadExecutor()) {
                String mResult;

                @Override
                protected void onDataChangeExecutor(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String photoName = snapshot.getValue(String.class);
                        if (photoName != null) {
                            StorageReference storageReference = mUserStorage.child(photoName);
                            try {
                                Bitmap bmp = Glide.with(getActivity()).using(new FirebaseImageLoader()).load(storageReference)
                                        .asBitmap().skipMemoryCache(true).into(-1, -1).get();
                                gifWriter.addFrame(bmp);
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    gifWriter.finish();
                    try {
                        // created gif files are written to pictures public external storage
                        File outputDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Botanist");
                        // Apparently, the external storage public directory only sometimes exists?
                        if (!outputDir.exists()) {
                            //noinspection ResultOfMethodCallIgnored
                            outputDir.mkdir();
                        }
                        // Iskander was here, we want to save gifs with the newest plant name which the plant id might not contain
                        File outFile = new File(outputDir, name + "_" + species + ".gif");
                        FileOutputStream output = new FileOutputStream(outFile);
                        output.write(out.toByteArray());
                        output.flush();
                        output.close();
                        String path = outFile.getAbsolutePath();
                        mPlantReference.child("gifLocation").setValue(path);
                        mResult = "GIF saved in: " + path;
                        notifyGallery(path);
                    } catch (IOException e) {
                        mResult = "Failed to make GIF";
                    } finally {
                        hideProgressDialog();
                        makeToastResult(getActivity());
                    }
                }

                @Override
                protected void onCancelledExecutor(@NonNull DatabaseError databaseError) {
                    mResult = "Failed to make GIF";
                    hideProgressDialog();
                    makeToastResult(getActivity());
                }

                private void makeToastResult(final Context context) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, mResult, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }

    private void notifyGallery(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        //change mCurrentPhotoPath for your imagepath
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    /**
     * Returns the plant's age in years
     * @param birthday - the plant's birthday
     * @return Returns age in days
     */
    private double getAgeInDays(long birthday) {
        return (System.currentTimeMillis() - birthday) / 86400000.0;
    }

    /**
     * write to phone's calendar
     * @param title - the title of the event to add
     */
    private void updateCalendar(String title, String type) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int hour = preferences.getInt("water_hour", 9);
        int minute = preferences.getInt("water_minute", 0);
        int reminderSetting;
        Calendar cal = Calendar.getInstance();
        if (type.equals(WATER_KEY)) {
            reminderSetting = Integer.parseInt(preferences.getString(SettingsActivity.WATER_REMINDER_KEY, "1"));
            long interval = getDatabaseManager().getReminderIntervalInMillis(reminderSetting);
            cal.setTimeInMillis(mPlant.getLastWatered() + interval);
        }
        else if (type.equals(FERTILIZER_KEY)) {
            reminderSetting = Integer.parseInt(preferences.getString(SettingsActivity.FERTILIZER_REMINDER_KEY, "2"));
            long interval = getDatabaseManager().getReminderIntervalInMillis(reminderSetting);
            cal.setTimeInMillis(mPlant.getLastFertilizerNotification() + interval);
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
        getActivity().startActivity(calendarIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    /**
     * Load profile photo of the plant
     */
    private void loadProfilePhoto() {
        if (mPlantReference != null) {
            final DatabaseReference photoFileNameReference = mPlantReference.child("profilePhoto");
            photoFileNameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String fileName = dataSnapshot.getValue(String.class);
                        ImageView profilePictureView = getActivity().findViewById(R.id.plant_picture);
                        int placeHolderResId = R.drawable.flowey;
                        if (fileName != null && !fileName.equals("default")) {
                            StorageReference photoFileReference = mUserStorage.child(fileName);
                            Glide.with(getActivity()).using(new FirebaseImageLoader()).
                                    load(photoFileReference).dontAnimate().placeholder(placeHolderResId).into(profilePictureView);
                        } else {
                            Glide.with(getActivity()).load(placeHolderResId).dontAnimate().placeholder(placeHolderResId).into(profilePictureView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * Load info of the plant
     */
    private void loadProfileInfo() {
        if (mPlantReference != null) {
            mPlantReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    mPlant = dataSnapshot.getValue(Plant.class);
                    if (mPlant != null) {
                        String species = mPlant.getSpecies();
                        TextView speciesTextView = getActivity().findViewById(R.id.plant_species);
                        speciesTextView.setText(getActivity().getString(R.string.species_fmt, species));
                        CollapsingToolbarLayout collapsingToolbarLayout = getActivity().findViewById(R.id.toolbar_layout);
                        collapsingToolbarLayout.setTitle(mPlant.getName());
                        TextView HeightTextView = getActivity().findViewById(R.id.plant_height);
                        HeightTextView.setText(getActivity().getString(R.string.height_fmt, mPlant.getHeight()));
                        loadCareTips(species);
                    }
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void loadCareTips(String species) {
        if (species != null) {
            DatabaseReference plantEntryReference = getDatabaseManager().getPlantEntryReference(species);
            plantEntryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Read the data from the plant entry
                 * @param dataSnapshot - the entry contents
                 */
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    PlantEntry entry = dataSnapshot.getValue(PlantEntry.class);
                    if (entry != null) {
                        mPlantGroup = entry.getGroup();
                        ((TextView) getActivity().findViewById(R.id.care_tips)).setText(entry.generateCareTips());
                        generateActiveGrowth(entry.getActive());
                        View toxicWarning = getActivity().findViewById(R.id.toxic_warning);
                        if (entry.isToxic()) {
                            toxicWarning.setVisibility(View.VISIBLE);
                        } else {
                            toxicWarning.setVisibility(View.GONE);
                        }
                        View noxiousWarning = getActivity().findViewById(R.id.noxious_warning);
                        List<String> noxious = entry.getNoxious();
                        if (noxious != null) {
                            noxiousWarning.setVisibility(View.VISIBLE);
                            TextView noxiousWarningTextView = getActivity().findViewById(R.id.noxious_warning_box);
                            if (noxious.contains("Noxious")) {
                                noxiousWarningTextView.setText(R.string.noxious_warning_msg);
                            }
                            if (noxious.contains("Quarantine")) {
                                noxiousWarningTextView.setText(String.format("%s \n\n%s", noxiousWarningTextView.getText(), getActivity().getString(R.string.quarantine_warning)));
                            }
                            if (noxious.contains("Regulated")) {
                                noxiousWarningTextView.setText(String.format("%s \n\n%s", noxiousWarningTextView.getText(), getActivity().getString(R.string.regulated_warning)));
                            }
                            if (noxious.contains("Banned")) {
                                noxiousWarningTextView.setText(String.format("%s \n\n%s", noxiousWarningTextView.getText(), getActivity().getString(R.string.banned_warning)));
                            }
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
     * Color the appropriate icons in the Active Growth Period box on the Care Tips in Profile
     */
    private void generateActiveGrowth(String activeGrowthPeriod) {
        if (activeGrowthPeriod == null || activeGrowthPeriod.trim().isEmpty() || activeGrowthPeriod.trim().equals("NA")) {
            getActivity().findViewById(R.id.active_growth_period_view).setVisibility(View.GONE);
        } else {
            getActivity().findViewById(R.id.active_growth_period_view).setVisibility(View.VISIBLE);
            activeGrowthPeriod = activeGrowthPeriod.trim();
            if (!activeGrowthPeriod.equals("Year Round")) {
                ((ImageView) getActivity().findViewById(R.id.winter_image)).setImageResource(R.drawable.winter_grayscale);
                if (!activeGrowthPeriod.contains("Fall")) {
                    ((ImageView) getActivity().findViewById(R.id.winter_image)).setImageResource(R.drawable.autumn_grayscale);
                }
                if (!activeGrowthPeriod.contains("Spring")) {
                    ((ImageView) getActivity().findViewById(R.id.winter_image)).setImageResource(R.drawable.spring_grayscale);
                }
                if (!activeGrowthPeriod.contains("Summer")) {
                    ((ImageView) getActivity().findViewById(R.id.summer_image)).setImageResource(R.drawable.summer_grayscale);
                }
            }
        }
    }

}
