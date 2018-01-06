// Plant profile
// @author: Antonio Muscarella, Christopher Besser, and Iskander Gaba
package com.scientists.happy.botanist.ui;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.controller.PlantController;
public class PlantActivity extends AppCompatActivity implements View.OnClickListener {
    private int mToxicRotationAngle, mNoxiousRotationAngle, mTipsRotationAngle;
    private boolean mToxicExpanded, mNoxiousExpanded, mTipsExpanded;

    private PlantController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mController = new PlantController(this);
        ActivityCompat.postponeEnterTransition(this);
        // Setting OnClickListeners
        findViewById(R.id.camera_fab).setOnClickListener(this);
        findViewById(R.id.height_button).setOnClickListener(this);
        findViewById(R.id.poop_button).setOnClickListener(this);
        findViewById(R.id.water_button).setOnClickListener(this);
        findViewById(R.id.calendar_button).setOnClickListener(this);
        findViewById(R.id.care_tips_expand_collapse).setOnClickListener(this);
        findViewById(R.id.toxic_warning_expand_collapse).setOnClickListener(this);
        findViewById(R.id.noxious_warning_expand_collapse).setOnClickListener(this);
        overridePendingTransition(R.anim.slide_up, R.anim.hold);
    }

    @Override
    protected void onStart() {
        mController.load();
        mController.showTutorial(false);
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.slide_down);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit_profile:
                mController.startEditPlantActivity();
                return true;
            case R.id.action_share:
                mController.sharePlant();
                return true;
            case R.id.action_delete:
                mController.showDeleteDialog();
                return true;
            case R.id.action_export_gif:
                mController.exportGif();
                return true;
            case R.id.action_stats:
                mController.startStatsActivity();
                return true;
            case R.id.action_similar_plants:
                mController.startSimilarPlantsActivity();
                return true;
            case R.id.action_diseases:
                mController.startDiseaseActivity();
                return true;
            case R.id.action_help:
                mController.showTutorial(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera_fab:
                mController.uploadPhoto();
                break;
            case R.id.water_button:
                mController.showWaterDialog();
                break;
            case R.id.height_button:
                mController.showHeightInputDialog();
                break;
            case R.id.poop_button:
                mController.showFertilizationDialog();
                break;
            case R.id.calendar_button:
                mController.showCalendarDialog();
                break;
            case R.id.care_tips_expand_collapse:
                View careTips = findViewById(R.id.care_tips_box);
                mTipsRotationAngle = rotateImage(view, mTipsRotationAngle);
                if (mTipsExpanded) {
                    collapse(careTips);
                } else {
                    expand(careTips);
                }
                mTipsExpanded = !mTipsExpanded;
                break;
            case R.id.toxic_warning_expand_collapse:
                View toxicWarning = findViewById(R.id.toxic_warning_box);
                mToxicRotationAngle = rotateImage(view, mToxicRotationAngle);
                if (mToxicExpanded) {
                    collapse(toxicWarning);
                } else {
                    expand(toxicWarning);
                }
                mToxicExpanded = !mToxicExpanded;
                break;
            case R.id.noxious_warning_expand_collapse:
                View noxiousWarning = findViewById(R.id.noxious_warning_box);
                mNoxiousRotationAngle = rotateImage(view, mNoxiousRotationAngle);
                if (mNoxiousExpanded) {
                    collapse(noxiousWarning);
                } else {
                    expand(noxiousWarning);
                }
                mNoxiousExpanded = !mNoxiousExpanded;
                break;
            default:
                break;
        }
    }

    /**
     * Expand a hidden view
     * @param v - current view
     */
    private static void expand(final View v) {
        v.measure(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        Animation anim = new Animation() {
            /**
             * Transform a view
             * @param interpolatedTime - time to transition
             * @param t - transition to apply
             */
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? CoordinatorLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            /**
             * Returns if the bounds will change
             * @return Returns true
             */
            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 3dp/ms
        anim.setDuration(((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density)) * 3);
        v.startAnimation(anim);
    }

    /**
     * Collapse a visible view
     * @param v - current view
     */
    private static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            /**
             * Transform the view
             * @param interpolatedTime - time to transition
             * @param t - animation to apply
             */
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                v.requestLayout();
            }

            /**
             * Returns whether the bounds will change
             * @return Returns true
             */
            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 2dp/ms
        a.setDuration(((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density)) * 2);
        v.startAnimation(a);
    }

    /**
     * Rotate an image
     * @param v - current view
     * @param rotationAngle - angle to rotate image
     * @return Returns the rotation angle
     */
    private int rotateImage(View v, int rotationAngle) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "rotation", rotationAngle, rotationAngle + 180);
        anim.setDuration(500);
        anim.start();
        rotationAngle += 180;
        rotationAngle %= 360;
        return rotationAngle;
    }
}