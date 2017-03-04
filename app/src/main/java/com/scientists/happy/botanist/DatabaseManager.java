package com.scientists.happy.botanist;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";

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
            mDatabase.child("CommonNames").addValueEventListener(listener);
            mDatabase.child("SpeciesNames").addValueEventListener(listener);
            return null;
        }
    }

    private DatabaseManager() {
        mAutoComplete = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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

    public void setSpeciesAutoComplete(Context context, AutoCompleteTextView autoCompleteTextView) {
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        autoComplete.addAll(mAutoComplete);
        autoCompleteTextView.setAdapter(autoComplete);
    }
}
