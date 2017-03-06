package com.scientists.happy.botanist;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";

    private long mPlantsNumber;

    private List<String> mAutoComplete;
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
                        String suggestion = suggestionSnapshot.getValue(String.class);
                        //Add the retrieved string to the list
                        mAutoComplete.add(suggestion);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            //Child the root before all the push() keys are found and add a ValueEventListener()
            //mDatabase.child("CommonNames").addValueEventListener(listener);
            mDatabase.child("SpeciesNames").addValueEventListener(listener);
            return null;
        }
    }

    private DatabaseManager() {
        mAutoComplete = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPlantsNumber = getPlantsNumber();
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
            mDatabase.child("users").child(userId).removeValue();
        }
    }

    public void addPlant(String name, String species, String photoPath, long birthday) {
        final Plant plant = new Plant(name, species, photoPath, birthday);
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
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    public void deletePlant(String name, String species) {
        final String userId = getUserId();
        final String plantId = species + "_" + name;
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plants").child(plantId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                mDatabase.child("users").child(userId).child("plants").child(plantId).removeValue();
                                setPlantsNumber(--mPlantsNumber);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    public FirebaseListAdapter<Plant> getPlantsAdapter(final Activity activity) {
        String userId = getUserId();
        if (userId != null) {
            DatabaseReference databaseRef = mDatabase.child("users").child(userId).child("plants");
            return new FirebaseListAdapter<Plant>(activity, Plant.class, R.layout.grid_item_view, databaseRef) {
                @Override
                protected void populateView(final View view, final Plant plant, int position) {
                    ((TextView)view.findViewById(R.id.grid_item_nickname)).setText(plant.getName());
                    ((TextView)view.findViewById(R.id.grid_item_species)).setText(plant.getSpecies());
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(activity.getApplicationContext(), ProfileActivity.class);
                            i.putExtra("plant", plant.toString());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                View sharedImageView = view.findViewById(R.id.grid_item_image_view);
                                View sharedNicknameView = view.findViewById(R.id.grid_item_nickname);
                                Pair<View, String> p1 = Pair.create(sharedImageView, "image_main_to_profile_transition");
                                Pair<View, String> p2 = Pair.create(sharedNicknameView, "nickname_main_to_profile_transition");
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, p1, p2);
                                activity.startActivity(i, options.toBundle());
                            } else {
                                activity.startActivity(i);
                            }
                        }
                    });
                }
            };
        }
        return null;
    }

    public void setSpeciesAutoComplete(Context context, AutoCompleteTextView autoCompleteTextView) {
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        autoComplete.addAll(mAutoComplete);
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

    private String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    private void setPlantsNumber(long count) {
        String userId = getUserId();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("plantsNumber").setValue(count);
        }
    }
}
