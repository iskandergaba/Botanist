package com.scientists.happy.botanist;

public class User {
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
