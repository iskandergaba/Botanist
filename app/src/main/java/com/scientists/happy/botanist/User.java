package com.scientists.happy.botanist;

public class User {
    private String userName;
    private String email;
    private String userId;
    private int plantsNumber;
    private long botanistSince;

    User() {
        this(null, null, null, 0);
    }

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
