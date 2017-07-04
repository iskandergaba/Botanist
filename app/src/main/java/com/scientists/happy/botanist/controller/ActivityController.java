package com.scientists.happy.botanist.controller;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.scientists.happy.botanist.data.DatabaseManager;

import java.util.ArrayList;

import za.co.riggaroo.materialhelptutorial.TutorialItem;

public abstract class ActivityController {

    private final DatabaseManager mDatabase = DatabaseManager.getInstance();
    private final AppCompatActivity mActivity;
    private ProgressDialog mProgressDialog;

    ActivityController(AppCompatActivity activity) {
        mActivity = activity;
    }

    /**
     * Show load the controller
     */
    public abstract void load();

    /**
     * Fetch assets for the tutorial
     * @return - Returns the list of tutorial items (null by default)
     */
    protected abstract ArrayList<TutorialItem> loadTutorialItems();

    final AppCompatActivity getActivity() {
        return mActivity;
    }

    final DatabaseManager getDatabaseManager() {
        return mDatabase;
    }

    /**
     * Show the tutorial
     */
    public final void showTutorial(boolean isForceShow) {
        ArrayList<TutorialItem> items = loadTutorialItems();
        if (items != null) {
            mDatabase.showTutorial(mActivity, loadTutorialItems(), isForceShow);
        }
    }

    /**
     * Show the loading progress
     */
    final void showProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    /**
     * Dismiss the loading progress
     */
    final void hideProgressDialog() {
        if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
