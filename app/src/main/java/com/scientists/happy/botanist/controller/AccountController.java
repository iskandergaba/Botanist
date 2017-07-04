package com.scientists.happy.botanist.controller;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import za.co.riggaroo.materialhelptutorial.TutorialItem;

public class AccountController extends ActivityController {

    private User mUser;

    public AccountController(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void load() {
        showProgressDialog(getActivity().getString(R.string.loading_text));
        getDatabaseManager().getUserReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUser = dataSnapshot.getValue(User.class);
                    populateUserInfo();
                    populateUserStatsChart();
                    loadBadge();
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    @Override
    protected ArrayList<TutorialItem> loadTutorialItems() {
        TutorialItem tutorialItem0 = new TutorialItem(getActivity().getString(R.string.account_tutorial_title_0), getActivity().getString(R.string.account_tutorial_contents_0),
                R.color.colorAccent, R.drawable.account_tutorial_0,  R.drawable.account_tutorial_0);
        TutorialItem tutorialItem1 = new TutorialItem(getActivity().getString(R.string.account_tutorial_title_1), getActivity().getString(R.string.account_tutorial_contents_1),
                R.color.colorAccent, R.drawable.account_tutorial_1,  R.drawable.account_tutorial_1);
        TutorialItem tutorialItem2 = new TutorialItem(getActivity().getString(R.string.account_tutorial_title_2), getActivity().getString(R.string.account_tutorial_contents_2),
                R.color.colorAccent, R.drawable.account_tutorial_2,  R.drawable.account_tutorial_2);
        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem0);
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        return tutorialItems;
    }

    /**
     * Delete a user from the database
     */
    public void deleteUserFromDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            getDatabaseManager().deleteUserRecords(getActivity(), userId);
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                /**
                 * Delete the user task completed
                 * @param task - the completed task
                 */
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mUser = null;
                    }
                }
            });
            resetDatabaseManager();
        }
    }

    public void resetDatabaseManager() {
        getDatabaseManager().resetMemberData();
        getDatabaseManager().deleteAllReminders(getActivity());
    }

    private void populateUserInfo() {
        TextView nameTextView = (TextView) getActivity().findViewById(R.id.name);
        TextView emailTextView = (TextView) getActivity().findViewById(R.id.email);
        TextView botanistSinceTextView = (TextView) getActivity().findViewById(R.id.botanist_since);
        TextView plantsNumberTextView = (TextView) getActivity().findViewById(R.id.plants_number);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        nameTextView.setText(mUser.getUserName());
        emailTextView.setText(getActivity().getString(R.string.email_fmt, mUser.getEmail()));
        botanistSinceTextView.setText(getActivity().getString(R.string.botanist_since_fmt, dateFormat.format(mUser.getBotanistSince())));
        plantsNumberTextView.setText(getActivity().getString(R.string.plants_number_fmt, mUser.getPlantsNumber()));
    }

    /**
     * Show user stats graph
     */
    private void populateUserStatsChart() {
        final String[] userStatsChartXAxisLabel = getActivity().getResources().getStringArray(R.array.user_stats_x_axis_labels);
        final BarChart chart = (BarChart) getActivity().findViewById(R.id.user_stats_chart);
        chart.setTouchEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            /**
             * Format the value
             * @param value - value to fit to the axis
             * @param axis - the axis to fit to
             * @return Returns the formatted value
             */
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int v = (int) value;
                return userStatsChartXAxisLabel[v];
            }
        });
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(11f);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        chart.getAxisLeft().setGranularity(1);
        chart.getDescription().setEnabled(false);

        if (mUser != null) {
            int[] colors = getActivity().getResources().getIntArray(R.array.user_stats_chart_colors);
            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0f, mUser.getPlantsAdded()));
            entries.add(new BarEntry(1f, mUser.getPlantsDeleted()));
            entries.add(new BarEntry(2f, mUser.getWaterCount()));
            entries.add(new BarEntry(3f, mUser.getMeasureCount()));
            entries.add(new BarEntry(4f, mUser.getPhotoCount()));

            BarDataSet barDataSet = new BarDataSet(entries, "Plant Operations");
            barDataSet.setColors(ColorTemplate.createColors(colors));
            barDataSet.setValueTextSize(11f);

            BarData data = new BarData(barDataSet);
            data.setBarWidth(0.9f); // set custom bar width
            chart.setData(data);
            chart.invalidate(); // refresh
        }
    }

    private void loadBadge() {
        TextView levelTextView = (TextView) getActivity().findViewById(R.id.level_text_view);
        ImageView badgeImageView = (ImageView) getActivity().findViewById(R.id.user_badge);
        ProgressBar levelProgressBar = (ProgressBar) getActivity().findViewById(R.id.level_progress_bar);
        double rating = mUser.getRating();
        if (rating < 0) {
            badgeImageView.setImageResource(R.drawable.badge_level_0);
            levelTextView.setText(getActivity().getString(R.string.level_0));
            levelProgressBar.setProgress(0);
        }
        else if (rating < 0.35) {
            badgeImageView.setImageResource(R.drawable.badge_level_1);
            levelTextView.setText(getActivity().getString(R.string.level_1));
            levelProgressBar.setProgress(35);
        }
        else if (rating < 0.75) {
            badgeImageView.setImageResource(R.drawable.badge_level_2);
            levelTextView.setText(getActivity().getString(R.string.level_2));
            levelProgressBar.setProgress(75);
        }
        else {
            badgeImageView.setImageResource(R.drawable.badge_level_3);
            levelTextView.setText(getActivity().getString(R.string.level_3));
            levelProgressBar.setProgress(100);
        }
        populateUserStatsChart();
    }
}
