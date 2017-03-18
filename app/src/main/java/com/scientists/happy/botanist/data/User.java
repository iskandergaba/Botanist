// User object
// @author: Iskander Gaba
package com.scientists.happy.botanist.data;
public class User {
    private String userName, email, userId;
    private long botanistSince;
    private int plantsNumber;
    /**
     * Create a new user
     */
    protected User() {
    }

    /**
     * Create a new user
     * @param userId - the user's ID
     * @param userName - the user's name
     * @param email - the user's email
     * @param plantsNumber - the number of plants the user has
     */
    protected User(String userId, String userName, String email, int plantsNumber) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.plantsNumber = plantsNumber;
        this.botanistSince = System.currentTimeMillis();
    }

    /**
     * Get the user's name
     * @return Returns the user's name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Get the user's email
     * @return Returns the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the user's id
     * @return Returns the user's id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Get the number of plants the user owns
     * @return Returns the number of plants the user owns
     */
    public int getPlantsNumber() {
        return plantsNumber;
    }

    /**
     * Get the time since the user started botanist
     * @return Returns how long the user has used Botanist
     */
    public long getBotanistSince() {
        return botanistSince;
    }
}