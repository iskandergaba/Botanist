package com.scientists.happy.botanist.controller;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;

public class DiseaseController {

    private final DatabaseManager mDatabase = DatabaseManager.getInstance();
    private final AppCompatActivity mActivity;
    private final String mGroup;

    public DiseaseController(AppCompatActivity activity) {
        mActivity = activity;
        mGroup = mActivity.getIntent().getExtras().getString("group");
    }

    public void load() {
        ListView listView = (ListView) mActivity.findViewById(R.id.diseases);
        listView.setEmptyView(mActivity.findViewById(R.id.empty_list_view));
        populateDiseasesList(listView);
    }

    /**
     * Populate plant diseases list
     */
    private void populateDiseasesList(final ListView list) {

        DatabaseReference databaseRef = mDatabase.getGroupDiseasesReference(mGroup);
        FirebaseListAdapter<String> listAdapter = new FirebaseListAdapter<String>(mActivity, String.class, R.layout.list_item_text_button, databaseRef) {
            String diseaseUrl;
            /**
             * Show images in glide
             * @param view - the current view
             * @param disease - the disease to display
             * @param position - the position in the menu
             */
            @Override
            protected void populateView(final View view, final String disease, final int position) {
                ((TextView) view.findViewById(R.id.text)).setText(disease);
                mDatabase.getDiseaseUrlReference(disease).addListenerForSingleValueEvent(new ValueEventListener() {
                    /**
                     * Handle a change in the database contents
                     * @param snapshot - current database contents
                     */
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            diseaseUrl = (String) snapshot.getValue();
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
                Button learnMoreButton = (Button) view.findViewById(R.id.button);
                learnMoreButton.setText(mActivity.getString(R.string.learn_more));
                learnMoreButton.setOnClickListener(new View.OnClickListener() {
                    /**
                     * User pressed a disease
                     * @param v - current app view
                     */
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(diseaseUrl));
                        mActivity.startActivity(browserIntent);
                    }
                });
            }
        };
        list.setAdapter(listAdapter);
    }
}
