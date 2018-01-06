package com.scientists.happy.botanist.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.ui.NewPlantDetailsFragment;
import com.scientists.happy.botanist.ui.NewPlantSpeciesFragment;

import java.util.ArrayList;

import za.co.riggaroo.materialhelptutorial.TutorialItem;

public class NewPlantController extends ActivityController {

    public NewPlantController(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void load() {
        final String speciesKey = "species";
        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(speciesKey)) {
            String species = intent.getStringExtra(speciesKey);
            Bundle bundle = new Bundle();
            bundle.putString(speciesKey, species);
            NewPlantDetailsFragment plantDetailsFragment = new NewPlantDetailsFragment();
            plantDetailsFragment.setArguments(bundle);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.root_layout, plantDetailsFragment);
            // transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();
        } else {
            NewPlantSpeciesFragment plantSpeciesFragment = new NewPlantSpeciesFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.root_layout, plantSpeciesFragment);
            // transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();
        }
    }

    @Override
    protected ArrayList<TutorialItem> loadTutorialItems() {
        return null;
    }
}
