package com.scientists.happy.botanist.data;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.services.BirthdayReceiver;
import com.scientists.happy.botanist.ui.ProfileActivity;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;

public class DatabaseManager {

    //private static final String TAG = "DatabaseManager";

    private long mPlantsNumber;
    private long mBotanistSince;

    private ProgressDialog mProgressDialog;

    private Map<String, String> mAutoCompleteCache;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private static DatabaseManager mDatabaseManager;

    private class PrepareAutocompleteTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
                    for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                        //Get the suggestion by childing the key of the string you want to get.
                        String commonName = suggestionSnapshot.getKey();
                        String sciName = suggestionSnapshot.getValue(String.class);
                        //Add the retrieved string to the list
                        mAutoCompleteCache.put(commonName, sciName);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            //Child the root before all the push() keys are found and add a ValueEventListener()
            mDatabase.child("Lookup").addValueEventListener(listener);
            return null;
        }
    }

    private DatabaseManager() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAutoCompleteCache = new HashMap<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mPlantsNumber = getPlantsNumber();
        mBotanistSince = getBotanistSince();
        new PrepareAutocompleteTask().execute();
    }

    public static DatabaseManager getInstance() {
        if (mDatabaseManager == null) {
            mDatabaseManager = new DatabaseManager();
        }
        return mDatabaseManager;
    }

    public void addUserRecords(final String userId, final String name, final String email) {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    User user = new User(userId, name, email, 0);
                    mDatabase.child("users").child(userId).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteUserRecords(String userId) {
        if (userId != null) {
            mStorage.child(userId).delete();
            mDatabase.child("users").child(userId).removeValue();
        }
    }

    public void addPlant(Context context, String name, String species, long birthday, double height, final Bitmap bmp) {
        showProgressDialog(context);
        final Plant plant;
        if (mAutoCompleteCache.containsKey(species)) {
            // If the user typed a common name, fetch the scientific name
            plant = new Plant(name, mAutoCompleteCache.get(species), birthday, height);
        } else {
            // The user must have entered either the correct scientific name or a random name, either way, add it
            plant = new Plant(name, species, birthday, height);
        }
        final String plantId = plant.getId();
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plants").child(plantId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                mDatabase.child("users").child(userId).child("plants").child(plantId).setValue(plant);
                                setPlantsNumber(++mPlantsNumber);
                                if (bmp != null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                    byte[] data = stream.toByteArray();
                                    StorageReference filepath = mStorage.child(userId).child(plant.getId() + ".jpg");
                                    filepath.putBytes(data);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
        hideProgressDialog();
    }

    public void deletePlant(Context context, String name, String species) {
        final String userId = getUserId();
        final String plantId = species + "_" + name;
        deleteAllBirthdayReminders(context);
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plants").child(plantId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                mDatabase.child("users").child(userId).child("plants").child(plantId).removeValue();
                                setPlantsNumber(--mPlantsNumber);
                                mStorage.child(userId).child(plantId + ".jpg").delete();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    public FirebaseListAdapter<Plant> getPlantsAdapter(final Activity activity) {
        final String userId = getUserId();
        if (userId != null) {
            DatabaseReference databaseRef = mDatabase.child("users").child(userId).child("plants");
            return new FirebaseListAdapter<Plant>(activity, Plant.class, R.layout.grid_item_view, databaseRef) {
                @Override
                protected void populateView(final View view, final Plant plant, final int position) {
                    StorageReference storageReference = mStorage.child(userId).child(plant.getId() + ".jpg");
                    ((TextView)view.findViewById(R.id.grid_item_nickname)).setText(plant.getName());
                    ((TextView)view.findViewById(R.id.grid_item_species)).setText(plant.getSpecies());
                    ImageView picture = (ImageView) view.findViewById(R.id.grid_item_image_view);
                    Glide.with(activity)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .placeholder(R.drawable.flowey)
                            .into(picture);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(activity.getApplicationContext(), ProfileActivity.class);
                            i.putExtra("id", position);
                            i.putExtra("name", plant.getName());
                            i.putExtra("species", plant.getSpecies());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                View sharedImageView = view.findViewById(R.id.grid_item_image_view);
                                Bundle bundle = ActivityOptions
                                        .makeSceneTransitionAnimation(activity, sharedImageView, "image_main_to_profile_transition")
                                        .toBundle();
                                activity.startActivity(i, bundle);
                            } else {
                                activity.startActivity(i);
                            }
                        }
                    });
                    setBirthdayReminder(activity, plant, position);
                }
            };
        }
        return null;
    }

    public void setSpeciesAutoComplete(Context context, AutoCompleteTextView autoCompleteTextView) {
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        autoComplete.addAll(mAutoCompleteCache.keySet());
        autoComplete.addAll(mAutoCompleteCache.values());
        autoCompleteTextView.setAdapter(autoComplete);
    }

    public long getPlantsNumber() {
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plantsNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mPlantsNumber = (long) snapshot.getValue();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return mPlantsNumber;
        }
        return -1;
    }

    public String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public long getBotanistSince() {
        final String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("botanistSince").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mBotanistSince = (long) snapshot.getValue();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return mBotanistSince;
        }
        return -1;
    }

    public void deleteAllBirthdayReminders(Context context) {
        AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        for (int i = 0; i < mPlantsNumber; i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, new Intent(context, BirthdayReceiver.class), 0);
            am.cancel(pendingIntent);
        }
    }

    private void setPlantsNumber(long count) {
        String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plantsNumber").setValue(count);
        }
    }

    private void setBirthdayReminder(Context context, Plant plant, int id) {
        Intent intent = new Intent(context, BirthdayReceiver.class);
        intent.putExtra("name", plant.getName());
        intent.putExtra("species", plant.getSpecies());
        intent.putExtra("birthday", plant.getBirthday());
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);

        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());

        Calendar birthday = Calendar.getInstance();
        birthday.setTimeInMillis(plant.getBirthday());

        birthday.set(Calendar.YEAR, now.get((Calendar.YEAR)));
        if (birthday.getTimeInMillis() < now.getTimeInMillis()) {
            birthday.set(Calendar.YEAR, now.get((Calendar.YEAR)) + 1);
        }

        Log.v("fuck", birthday.toString());

        AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, birthday.getTimeInMillis(), pendingIntent);
    }

    private void showProgressDialog(Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
