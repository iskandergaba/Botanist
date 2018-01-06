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

import java.util.ArrayList;

import za.co.riggaroo.materialhelptutorial.TutorialItem;

public class DiseaseController extends ActivityController {

    private final String mGroup;

    public DiseaseController(AppCompatActivity activity) {
        super(activity);
        mGroup = getActivity().getIntent().getExtras().getString("group");
    }

    @Override
    public void load() {
        ListView listView = getActivity().findViewById(R.id.diseases);
        listView.setEmptyView(getActivity().findViewById(R.id.empty_list_view));
        populateDiseasesList(listView);
    }

    @Override
    protected ArrayList<TutorialItem> loadTutorialItems() {
        return null;
    }

    /**
     * Populate plant diseases list
     */
    private void populateDiseasesList(final ListView list) {

        DatabaseReference databaseRef = getDatabaseManager().getGroupDiseasesReference(mGroup);
        FirebaseListAdapter<String> listAdapter = new FirebaseListAdapter<String>(getActivity(), String.class, R.layout.list_item_text_button, databaseRef) {
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
                getDatabaseManager().getDiseaseUrlReference(disease).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Button learnMoreButton = view.findViewById(R.id.button);
                learnMoreButton.setText(getActivity().getString(R.string.learn_more));
                learnMoreButton.setOnClickListener(new View.OnClickListener() {
                    /**
                     * User pressed a disease
                     * @param v - current app view
                     */
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(diseaseUrl));
                        getActivity().startActivity(browserIntent);
                    }
                });
            }
        };
        list.setAdapter(listAdapter);
    }
}
