package com.scientists.happy.botanist.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
import com.scientists.happy.botanist.data.Plant;
import com.scientists.happy.botanist.services.FertilizerReceiver;
import com.scientists.happy.botanist.services.HeightMeasureReceiver;
import com.scientists.happy.botanist.services.UpdatePhotoReceiver;
import com.scientists.happy.botanist.services.WaterReceiver;
import com.scientists.happy.botanist.ui.PlantActivity;

import java.util.ArrayList;
import java.util.Calendar;

import za.co.riggaroo.materialhelptutorial.TutorialItem;

public class MainController extends ActivityController {

    private final DatabaseReference mUserReference = getDatabaseManager().getUserReference();

    public MainController(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void load() {
        populatePlantGrid((GridView) getActivity().findViewById(R.id.plants_grid));
        generateDailyTip(getActivity().findViewById(R.id.daily_tip_cardview));
    }

    @Override
    public ArrayList<TutorialItem> loadTutorialItems() {
        TutorialItem tutorialItem0 = new TutorialItem(getActivity().getString(R.string.main_tutorial_title_0_0), getActivity().getString(R.string.main_tutorial_contents_0_0),
                R.color.colorAccent, R.drawable.main_tutorial_0_0,  R.drawable.main_tutorial_0_0);
        TutorialItem tutorialItem1 = new TutorialItem(getActivity().getString(R.string.main_tutorial_title_0_1), getActivity().getString(R.string.main_tutorial_contents_0_1),
                R.color.colorAccent, R.drawable.main_tutorial_0_1,  R.drawable.main_tutorial_0_1);
        TutorialItem tutorialItem2 = new TutorialItem(getActivity().getString(R.string.main_tutorial_title_0_2), getActivity().getString(R.string.main_tutorial_contents_0_2),
                R.color.colorAccent, R.drawable.main_tutorial_0_2,  R.drawable.main_tutorial_0_2);
        TutorialItem tutorialItem3 = new TutorialItem(getActivity().getString(R.string.main_tutorial_title_1), getActivity().getString(R.string.main_tutorial_contents_1),
                R.color.colorAccent, R.drawable.main_tutorial_1,  R.drawable.main_tutorial_1);
        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem0);
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        return tutorialItems;
    }

    /**
     * populate a grid with user plants
     * @param grid - the current grid
     */
    private void populatePlantGrid(final GridView grid) {
        final DatabaseReference plantsReference = getDatabaseManager().getAllPlantsReference();
        final TextView emptyGridView = getActivity().findViewById(R.id.empty_grid_view);
        final ProgressBar loadingProgressBar = getActivity().findViewById(R.id.loading_indicator);
        loadingProgressBar.setVisibility(View.VISIBLE);
        if (plantsReference != null) {
            final FirebaseListAdapter<Plant> adapter = new FirebaseListAdapter<Plant>(getActivity(), Plant.class, R.layout.grid_item_plant, plantsReference) {
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
                        // Backward support for earlier versions before profilePhoto property was added
                        long photoNum = plant.getPhotoNum();
                        profilePhoto = photoNum < 0 ? "default" : plant.getId() + "_" + photoNum + ".jpg";
                        plant.setProfilePhoto(profilePhoto);
                    }
                    StorageReference storageReference = getDatabaseManager().getUserStorage().child(profilePhoto);
                    ((TextView) view.findViewById(R.id.grid_item_nickname)).setText(plant.getName());
                    ((TextView) view.findViewById(R.id.grid_item_species)).setText(plant.getSpecies());
                    final ImageView picture = view.findViewById(R.id.grid_item_image_view);
                    Glide.with(getActivity()).using(new FirebaseImageLoader()).load(storageReference).dontAnimate()
                            .placeholder(R.drawable.flowey).into(picture);
                    // One day, before the progress bar becomes empty
                    long interval = getDatabaseManager().getReminderIntervalInMillis(1);
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
                            Intent i = new Intent(getActivity().getApplicationContext(), PlantActivity.class);
                            i.putExtra("plant_id", plant.getId());
                            getActivity().startActivity(i);
                        }
                    });
                    getDatabaseManager().setReminders(getActivity(), plant, position, new WaterReceiver());
                    getDatabaseManager().setReminders(getActivity(), plant, position + DatabaseManager.HEIGHT_MEASURE_RECEIVER_ID_OFFSET, new HeightMeasureReceiver());
                    getDatabaseManager().setReminders(getActivity(), plant, position + DatabaseManager.FERTILIZER_RECEIVER_ID_OFFSET, new FertilizerReceiver());
                    getDatabaseManager().setReminders(getActivity(), plant, position + DatabaseManager.UPDATE_PHOTO_RECEIVER_ID_OFFSET, new UpdatePhotoReceiver());
                    getDatabaseManager().setBirthdayReminder(getActivity(), plant, position + DatabaseManager.BIRTHDAY_RECEIVER_ID_OFFSET);
                }
            };

            // After deep digging, I discovered that Firebase keeps some local information in ".info"
            DatabaseReference connectedRef = getDatabaseManager().getUserConnectionReference();
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = (boolean) snapshot.getValue();
                    if (connected) {
                        emptyGridView.setText(R.string.loading_text);
                        loadingProgressBar.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), R.string.msg_network_error, Toast.LENGTH_SHORT).show();
                        emptyGridView.setText(R.string.msg_network_error);
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            plantsReference.addValueEventListener(new ValueEventListener() {
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
     * Get the array index of the last daily tip the user saw, and update the CardView on the
     * main activity if it was not today
     * @param tipView - the container view of the daily tip
     */
    private void generateDailyTip(final View tipView) {
        final String[] dailyTips = getActivity().getResources().getStringArray(R.array.daily_tips_values);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean displayTip = preferences.getBoolean("daily_tip", true);
        if (mUserReference != null && displayTip) {
            mUserReference.child("indexOfLastDailyTip").addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change in the user data
                 *
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
                        getDatabaseManager().setIndexOfLastDailyTip(dailyTipIndex);
                    } else {
                        int dailyTipIndex = (int) (Math.random() * dailyTips.length);
                        ((TextView) tipView.findViewById(R.id.daily_tip_text)).setText(dailyTips[dailyTipIndex]);
                        getDatabaseManager().setIndexOfLastDailyTip(dailyTipIndex);
                    }
                }

                /**
                 * Generate a random tip and hope it's not the same as last time if something wrong happens
                 *
                 * @param databaseError - database encountered an error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    int dailyTipIndex = (int) (Math.random() * dailyTips.length);
                    ((TextView) tipView.findViewById(R.id.daily_tip_text)).setText(dailyTips[dailyTipIndex]);
                    getDatabaseManager().setIndexOfLastDailyTip(dailyTipIndex);
                }
            });

            mUserReference.child("dateOfLastDailyTip").addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Handle a change in the user data
                 *
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
                            getDatabaseManager().setDateOfLastDailyTip(System.currentTimeMillis());
                        } else {
                            tipView.setVisibility(View.GONE);
                        }
                    } else {
                        tipView.setVisibility(View.VISIBLE);
                        getDatabaseManager().setDateOfLastDailyTip(System.currentTimeMillis());
                    }
                }

                /**
                 * Hide the tip view if cannot know whether it's been a day since the last tip or not
                 *
                 * @param databaseError - database encountered an error
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    tipView.setVisibility(View.GONE);
                }
            });
        }
    }
}
