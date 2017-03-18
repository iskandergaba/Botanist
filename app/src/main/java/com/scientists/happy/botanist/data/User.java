package com.scientists.happy.botanist.data;

class User {
    private String userName, email, userId;
    private long botanistSince;
    private int plantsNumber;

    User() {}

    User(String userId, String userName, String email, int plantsNumber) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.plantsNumber = plantsNumber;
        this.botanistSince = System.currentTimeMillis();
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

    public long getBotanistSince() {
        return botanistSince;
    }
}
