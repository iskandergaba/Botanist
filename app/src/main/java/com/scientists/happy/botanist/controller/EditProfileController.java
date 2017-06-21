package com.scientists.happy.botanist.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
import com.scientists.happy.botanist.data.Plant;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

public class EditProfileController {

    private AppCompatActivity mActivity;
    private ProgressDialog mProgressDialog;
    private String mPlantId;
    private int mPhotoNum, mPhotoPointer = -1;

    private final DatabaseManager mDatabase = DatabaseManager.getInstance();
    private final StorageReference mUserStorage = mDatabase.getUserStorage();
    private final DatabaseReference mUserPhotosReference = mDatabase.getUserPhotosReference();

    public EditProfileController(AppCompatActivity activity, String plantId) {
        this.mActivity = activity;
        this.mPlantId = plantId;
        mDatabase.getPlantReference(mPlantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Plant plant = dataSnapshot.getValue(Plant.class);
                    if (plant != null) {
                        mPhotoNum = plant.getPhotoNum();
                        mPhotoPointer = plant.getPhotoPointer();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void load() {
        showProgressDialog(mActivity.getString(R.string.loading_text));
        populatePhotoGrid(mActivity);
        hideProgressDialog();
    }

    public void uploadPhoto() {
        final PickSetup setup = new PickSetup().setSystemDialog(true);
        PickImageDialog.build(setup).setOnPickResult(new IPickResult() {
            /**
             * Handle the selected result
             * @param r - the selected result
             */
            @Override
            public void onPickResult(PickResult r) {
                Bitmap bitmap = r.getBitmap();
                mDatabase.updatePlantImage(++mPhotoPointer, ++mPhotoNum, mPlantId, bitmap);
            }
        }).show(mActivity.getSupportFragmentManager());
    }

    /**
     * populate a grid with plant photos
     * @param activity - the current activity
     */
    private void populatePhotoGrid(final Activity activity) {
        final GridView grid = (GridView) mActivity.findViewById(R.id.photo_grid_view);
        final TextView emptyGridView = (TextView) activity.findViewById(R.id.empty_grid_view);
        final ProgressBar loadingProgressBar = (ProgressBar) activity.findViewById(R.id.loading_indicator);
        loadingProgressBar.setVisibility(View.VISIBLE);
        if (mUserPhotosReference != null) {
            final DatabaseReference plantRef = mDatabase.getPlantReference(mPlantId);
            // An SQL-like hack to retrieve only data with values that matches the query: "plantId*"
            // This is needed to query only images that correspond to the specific plant being edited
            Query query = mUserPhotosReference.orderByValue().startAt(mPlantId).endAt(mPlantId + "\uf8ff");
            final FirebaseListAdapter<String> adapter =
                    new FirebaseListAdapter<String>(activity, String.class, R.layout.photo_item_view, query) {
                        /**
                         * Populate a photo grid item
                         * @param view - the current view
                         * @param photoName - the photo to display
                         * @param position - the position
                         */
                        @Override
                        protected void populateView(final View view, final String photoName, final int position) {
                            final DatabaseReference profilePhotoRef = plantRef.child("profilePhoto");
                            final StorageReference storageReference = mUserStorage.child(photoName);
                            final ImageView picture = (ImageView) view.findViewById(R.id.photo_image_view);
                            final boolean[] isProfilePicture = new boolean[1];
                            Glide.with(activity).using(new FirebaseImageLoader()).load(storageReference).dontAnimate()
                                    .placeholder(R.drawable.flowey).into(picture);
                            final View setButton = view.findViewById(R.id.set_photo_btn);
                            setButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    profilePhotoRef.setValue(photoName);
                                    notifyDataSetChanged();
                                }
                            });
                            view.findViewById(R.id.delete_photo_btn).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    buildDeletePhotoDialog(storageReference,
                                            profilePhotoRef, position, isProfilePicture[0]).show();
                                }
                            });
                            profilePhotoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String profilePhoto = (String) dataSnapshot.getValue();
                                    View isSetIndicator = view.findViewById(R.id.is_set_indicator);
                                    isProfilePicture[0] = profilePhoto != null && profilePhoto.equals(photoName);
                                    if (isProfilePicture[0]) {
                                        setButton.setVisibility(View.GONE);
                                        isSetIndicator.setVisibility(View.VISIBLE);
                                    } else {
                                        setButton.setVisibility(View.VISIBLE);
                                        isSetIndicator.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onDataChanged() {
                            super.onDataChanged();
                            final DatabaseReference photoNumRef = plantRef.child("photoNum");
                            // Keep photoNum up to date. (photoNum is zero-based)
                            photoNumRef.setValue(getCount() - 1);
                        }

                        /**
                         * Delete Photo dialog
                         * @return Return the dialog window warning user of photo removal
                         */
                        private AlertDialog buildDeletePhotoDialog(final StorageReference storageReference,
                                                                   final DatabaseReference profilePhotoRef, final int position,
                                                                   final boolean isProfilePicture) {
                            //Instantiate an AlertDialog.Builder with its constructor
                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            //Chain together various setter methods to set the dialog characteristics
                            builder.setTitle(R.string.dialog_delete_photo_title).setMessage(R.string.dialog_delete_photo_text);
                            // Add the buttons
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                /**
                                 * User clicked confirm
                                 * @param dialog - the revoke dialog
                                 * @param id - the user's id
                                 */
                                public void onClick(DialogInterface dialog, int id) {
                                    storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                getRef(position).removeValue();
                                                if (isProfilePicture) {
                                                    profilePhotoRef.setValue("default");
                                                }
                                                // Keep photoCount up-to-date
                                                mDatabase.setPhotoCount(mDatabase.getPhotoCount() - 1);
                                            }
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                /**
                                 * User clicked cancel
                                 * @param dialog - the dialog
                                 * @param id - id
                                 */
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            // Get the AlertDialog from create()
                            return builder.create();
                        }
                    };

            // After digging deep, I discovered that Firebase keeps some local information in ".info"
            DatabaseReference connectedRef = mDatabase.getUserConnectionReference();
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = (boolean) snapshot.getValue();
                    if (connected) {
                        emptyGridView.setText(R.string.loading_text);
                        loadingProgressBar.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(activity, R.string.msg_network_error, Toast.LENGTH_SHORT).show();
                        emptyGridView.setText(R.string.msg_network_error);
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            mUserPhotosReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loadingProgressBar.setVisibility(View.GONE);
                    emptyGridView.setText(R.string.grid_photos_empty_text);
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
