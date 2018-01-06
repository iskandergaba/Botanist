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
import com.scientists.happy.botanist.ui.NewPlantActivity;

import java.util.ArrayList;

import za.co.riggaroo.materialhelptutorial.TutorialItem;

public class SimilarPlantsController extends ActivityController {

    private final String mGroup, mSpecies;

    public SimilarPlantsController(AppCompatActivity activity) {
        super(activity);
        Bundle extras = getActivity().getIntent().getExtras();
        mSpecies = extras.getString("species");
        mGroup = extras.getString("group");
    }

    @Override
    public void load() {
        ListView list = getActivity().findViewById(R.id.similar_plants);
        list.setEmptyView(getActivity().findViewById(R.id.empty_list_view));
        populateSimilarPlantsList(list);
    }

    @Override
    protected ArrayList<TutorialItem> loadTutorialItems() {
        return null;
    }

    private void populateSimilarPlantsList(final ListView list) {
        DatabaseReference databaseRef = getDatabaseManager().getGroupPlantsReference(mGroup);
        FirebaseListAdapter<String> adapter = new FirebaseListAdapter<String>(getActivity(), String.class, R.layout.list_item_text_button, databaseRef) {
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
                    Button addPlantButton = view.findViewById(R.id.button);
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
