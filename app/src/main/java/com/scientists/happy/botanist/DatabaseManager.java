package com.scientists.happy.botanist;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";

    private DatabaseReference mDatabase;
    private static DatabaseManager mDatabaseManager;

    private DatabaseManager() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseManager getInstance() {
        if (mDatabaseManager == null) {
            mDatabaseManager = new DatabaseManager();
        }
        return mDatabaseManager;
    }

    public void addNewUserRecords(String userId, String name, String email) {
        User user = new User(userId, name, email, 0);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public void deleteUserRecords(String userId) {
        if (userId != null) {
            mDatabase.child("users").child(userId).removeValue();
            // TODO: add listeners
        }
    }

}
