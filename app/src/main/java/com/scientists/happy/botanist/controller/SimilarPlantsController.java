package com.scientists.happy.botanist.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
import com.scientists.happy.botanist.ui.NewPlantActivity;

public class SimilarPlantsController {
    private AppCompatActivity mActivity;
    private final String mGroup, mSpecies;
    private final DatabaseManager mDatabase = DatabaseManager.getInstance();

    public SimilarPlantsController(AppCompatActivity activity) {
        mActivity = activity;
        Bundle extras = mActivity.getIntent().getExtras();
        mSpecies = extras.getString("species");
        mGroup = extras.getString("group");
    }

    public void load() {
        ListView list = (ListView) mActivity.findViewById(R.id.similar_plants);
        list.setEmptyView(mActivity.findViewById(R.id.empty_list_view));
        populateSimilarPlantsGrid(list);
    }

    private void populateSimilarPlantsGrid(final ListView list) {
        DatabaseReference databaseRef = mDatabase.getGroupPlantsReference(mGroup);
        FirebaseListAdapter<String> adapter = new FirebaseListAdapter<String>(mActivity, String.class, R.layout.list_item_text_button, databaseRef) {
            /**
             * Show images in glide
             * @param view - the current view
             * @param plant - the plant to display
             * @param position - the position in the menu
             */
            @Override
            protected void populateView(final View view, final String plant, final int position) {
                if (!plant.equals(mSpecies)) {
                    ((TextView) view.findViewById(R.id.text)).setText(plant);
                    Button addPlantButton = (Button) view.findViewById(R.id.button);
                    addPlantButton.setText(R.string.add_plant);
                    addPlantButton.setOnClickListener(new View.OnClickListener() {
                        /**
                         * User clicked buy now
                         * @param v - current view
                         */
                        @Override
                        public void onClick(View v) {
                            Intent addIntent = new Intent(mActivity, NewPlantActivity.class);
                            addIntent.putExtra("species", plant);
                            mActivity.startActivity(addIntent);
                        }
                    });
                }
            }
        };
        list.setAdapter(adapter);
    }
}
