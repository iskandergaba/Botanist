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

    private class User {
        private String userName;
        private String email;
        private String userId;
        private int plantsNumber;

        User() {
            this(null, null, null, 0);
        }

        User(String userId, String userName, String email, int plantsNumber) {
            this.userId = userId;
            this.userName = userName;
            this.email = email;
            this.plantsNumber = plantsNumber;
        }

        public String getUserName() {
            return userName;
        }

        public String getEmail() {
            return email;
        }

        public String getUserId() {
            return userId;
        }

        public int getPlantsNumber() {
            return plantsNumber;
        }
    }

}
