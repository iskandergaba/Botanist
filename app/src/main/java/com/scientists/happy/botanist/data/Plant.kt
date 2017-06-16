// Plant object
// @author: Iskander Gaba
package com.scientists.happy.botanist.data

import java.util.LinkedHashMap

@Suppress("unused")
class Plant {
    /**
     * Get the number of the most recent photo
     * @return Returns the number of the most recent photo
     */
    var photoNum: Int = -1
    /**
     * Get the plant's birthday
     * @return Returns the plant's birthday
     */
    var birthday: Long = 0
    /**
     * Get the last time the plant was watered
     * @return Returns the last time the plant water notification was issued
     */
    var lastWaterNotification: Long = 0
    /**
     * retrieve the last time the user measured the plant
     * @return Returns the last time the plant height notification was issued
     */
    var lastMeasureNotification: Long = 0
    /**
     * Get last time plant was fertilized
     * @return Returns last time plant was fertilized
     */
    var lastFertilizerNotification: Long = 0
    /**
     * Get last time user was prompted to take a picture
     * @return Returns the last time the user was prompted to take a picture
     */
    var lastPhotoNotification: Long = 0
    /**
     * Get the current plant height
     * @return Returns the latest plant height
     */
    var height: Double = 0.0
    /**
     * Get the plant's name
     * @return Returns the plant's name
     */
    var name: String? = null
    /**
     * Get the plant's species
     * @return Returns the plant's species
     */
    var species: String? = null
    /**
     * Get the plant's id
     * @return Returns the plant's id
     */
    var id: String? = null
    /**
     * Get gif location
     * @return Returns the location of the plant gif
     */
    var gifLocation: String? = null
    /**
     * Get the plant heights
     * @return Returns a map of times in millis to their recorded heights
     */
    var heights: Map<String, Double>? = null
    /**
     * Get the plant waterings
     * @return Returns a list of watering operations times in millis
     */
    var watering: Map<String, String>? = null

    /**
     * Required by Firebase, this useless constructor must remain
     */
    private constructor()

    /**
     * Create a new plant
     * @param id - the unique id of the plant
     * *
     * @param name - the name of the plant
     * *
     * @param species - the plant's species
     * *
     * @param birthday - the plant's birthday
     * *
     * @param height - the plant's height
     */
    constructor(id: String, name: String, species: String, birthday: Long, height: Double) {
        this.id = id
        this.name = name
        this.species = species
        this.birthday = birthday
        this.height = height
        this.watering = LinkedHashMap<String, String>()
        this.heights = LinkedHashMap<String, Double>()
        this.lastMeasureNotification = System.currentTimeMillis()
        this.lastFertilizerNotification = lastMeasureNotification
        this.lastWaterNotification = lastMeasureNotification
        this.lastPhotoNotification = lastMeasureNotification
        (heights as LinkedHashMap<String, Double>).put(java.lang.Long.toString(lastMeasureNotification), height)
        photoNum = -1
        gifLocation = "No Gif made (yet!)"
    }

    /**
     * Get the last time the plant was watered in millis
     * @return Returns the last time the plant was watered in millis
     */
    val lastWatered: Long
        get() {
            var lastWatered = birthday
            if (watering != null) {
                for (key in watering?.keys!!) {
                    lastWatered = Math.max(lastWatered, java.lang.Long.parseLong(watering!![key]))
                }
            }
            return lastWatered
        }
}
