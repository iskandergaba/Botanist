package com.scientists.happy.botanist.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
import com.scientists.happy.botanist.data.Plant;
import com.scientists.happy.botanist.data.PlantEntry;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProfileController {

    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private String mPlantId;

    private final DatabaseManager mDatabase = DatabaseManager.getInstance();
    private final StorageReference mUserStorage = mDatabase.getUserStorage();
    private final DatabaseReference mPlantReference;

    public ProfileController(Activity activity, String plantId) {
        this.mActivity = activity;
        this.mPlantId = plantId;
        mPlantReference = mDatabase.getPlantReference(plantId);
    }

    public void load() {
        showProgressDialog(mActivity.getString(R.string.loading_text));
        loadProfilePhoto();
        loadProfileInfo();
        hideProgressDialog();
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
                    String fileName = dataSnapshot.getValue(String.class);
                    ImageView profilePictureView = (ImageView) mActivity.findViewById(R.id.plant_picture);
                    int placeHolderResId = R.drawable.flowey;
                    if (fileName != null && !fileName.equals("default")) {
                        StorageReference photoFileReference = mUserStorage.child(fileName);
                        Glide.with(mActivity).using(new FirebaseImageLoader()).
                                load(photoFileReference).dontAnimate().placeholder(placeHolderResId).into(profilePictureView);
                    } else {
                        Glide.with(mActivity).load(placeHolderResId).dontAnimate().placeholder(placeHolderResId).into(profilePictureView);
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
                    Plant plant = dataSnapshot.getValue(Plant.class);
                    if (plant != null) {
                        String species = plant.getSpecies();
                        TextView speciesTextView = (TextView) mActivity.findViewById(R.id.plant_species);
                        speciesTextView.setText(mActivity.getString(R.string.species_fmt, species));
                        String name = plant.getName();
                        CollapsingToolbarLayout collapsingToolbarLayout =
                                (CollapsingToolbarLayout) mActivity.findViewById(R.id.toolbar_layout);
                        collapsingToolbarLayout.setTitle(name);
                        TextView HeightTextView = (TextView) mActivity.findViewById(R.id.plant_height);
                        HeightTextView.setText(mActivity.getString(R.string.height_fmt, plant.getHeight()));
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
            DatabaseReference plantEntryReference = mDatabase.getPlantEntryReference(species);
            plantEntryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Read the data from the plant entry
                 * @param dataSnapshot - the entry contents
                 */
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    PlantEntry entry = dataSnapshot.getValue(PlantEntry.class);
                    assert entry != null;
                    ((TextView) mActivity.findViewById(R.id.group_holder)).setText(entry.getGroup());
                    ((TextView) mActivity.findViewById(R.id.care_tips)).setText(entry.generateCareTips());
                    generateActiveGrowth(entry.getActive());
                    View toxicWarning = mActivity.findViewById(R.id.toxic_warning);
                    if (entry.isToxic()) {
                        toxicWarning.setVisibility(View.VISIBLE);
                    } else {
                        toxicWarning.setVisibility(View.GONE);
                    }
                    View noxiousWarning = mActivity.findViewById(R.id.noxious_warning);
                    List<String> noxious = entry.getNoxious();
                    if (noxious != null) {
                        noxiousWarning.setVisibility(View.VISIBLE);
                        TextView noxiousWarningTextView = (TextView) mActivity.findViewById(R.id.noxious_warning_box);
                        if (noxious.contains("Noxious")) {
                            noxiousWarningTextView.setText(R.string.noxious_warning_msg);
                        }
                        if (noxious.contains("Quarantine")) {
                            noxiousWarningTextView.setText(noxiousWarningTextView.getText() + " \n\n" + mActivity.getString(R.string.quarantine_warning));
                        }
                        if (noxious.contains("Regulated")) {
                            noxiousWarningTextView.setText(noxiousWarningTextView.getText() + " \n\n" + mActivity.getString(R.string.regulated_warning));
                        }
                        if (noxious.contains("Banned")) {
                            noxiousWarningTextView.setText(noxiousWarningTextView.getText() + " \n\n" + mActivity.getString(R.string.banned_warning));
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
            mActivity.findViewById(R.id.active_growth_period_view).setVisibility(View.GONE);
        } else {
            mActivity.findViewById(R.id.active_growth_period_view).setVisibility(View.VISIBLE);
            activeGrowthPeriod = activeGrowthPeriod.trim();
            if (!activeGrowthPeriod.equals("Year Round")) {
                ((ImageView) mActivity.findViewById(R.id.winter_image)).setImageResource(R.drawable.winter_grayscale);
                if (!activeGrowthPeriod.contains("Fall")) {
                    ((ImageView) mActivity.findViewById(R.id.winter_image)).setImageResource(R.drawable.autumn_grayscale);
                }
                if (!activeGrowthPeriod.contains("Spring")) {
                    ((ImageView) mActivity.findViewById(R.id.winter_image)).setImageResource(R.drawable.spring_grayscale);
                }
                if (!activeGrowthPeriod.contains("Summer")) {
                    ((ImageView) mActivity.findViewById(R.id.summer_image)).setImageResource(R.drawable.summer_grayscale);
                }
            }
        }
    }

    /**
     * Show the loading progress
     */
    private void showProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(mActivity);
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

}
