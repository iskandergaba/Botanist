// User object
// @author: Iskander Gaba
package com.scientists.happy.botanist.data

@Suppress("unused")
class User {
    /**
     * Get the user's name
     * @return Returns the user's name
     */
    var userName: String? = null
    /**
     * Get the user's email
     * @return Returns the user's email
     */
    var email: String? = null
    /**
     * Get the user's id
     * @return Returns the user's id
     */
    var userId: String? = null
    /**
     * Get the time since the user started botanist
     * @return Returns how long the user has used Botanist
     */
    var botanistSince: Long = 0
    /**
     * Get the number of plants the user owns
     * @return Returns the number of plants the user owns
     */
    var plantsNumber: Int = 0
    /**
     * Fetch a user's rating
     * @return Returns the user's botanist quality
     */
    var rating: Double = 0.0
    /**
     * Get the number of plants the user added
     * @return Returns the number of plants the user owns
     */
    var plantsAdded: Int = 0
    /**
     * Get the number of plants the user deleted
     * @return Returns the number of plants the user owns
     */
    var plantsDeleted: Int = 0
    /**
     * Fetch how many times the user watered their plants
     * @return Returns the number of times the user watered their plants
     */
    var waterCount: Int = 0
    /**
     * Fetch the number of times the user fertilized their plants
     * @return Returns the number of times the user fertilized their plants
     */
    var measureCount: Int = 0
    /**
     * Fetch the number of times the user took photos of their plants
     * @return Returns the number of times the user took photos of their plants
     */
    var photoCount: Int = 0
    /**
     * Fetch the number of times the user took photos of their plants
     * @return Returns photos taken
     */
    var photos: Map<String, Int>? = null
    /**
     * Fetch the number of times the user took photos of their plants
     * @return Returns tutorials shown
     */
    var tutorials: Map<String, Boolean>? = null

    /**
     * Required by Firebase, this useless constructor must remain
     */
    private constructor()

    /**
     * Create a new user
     * @param userId - the user's ID
     * *
     * @param userName - the user's name
     * *
     * @param email - the user's email
     * *
     * @param plantsNumber - the number of plants the user has
     */
    constructor(userId: String, userName: String, email: String, plantsNumber: Int) {
        this.userId = userId
        this.userName = userName
        this.email = email
        this.plantsNumber = plantsNumber
        this.botanistSince = System.currentTimeMillis()
    }
}