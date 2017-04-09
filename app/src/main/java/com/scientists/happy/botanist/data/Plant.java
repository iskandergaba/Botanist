// Plant object
// @author: Antonio Muscarella and Iskander Gaba
package com.scientists.happy.botanist.data;

import java.util.LinkedHashMap;
import java.util.Map;
@SuppressWarnings({"WeakerAccess", "unused"})
public class Plant {
    private static final String DELIMITER = "\t";
    private int photoNum;
    private long birthday, lastWaterNotification, lastMeasureNotification, lastFertilizerNotification, lastPhotoNotification;
    private double height;
    private String name, species, id, gifLocation;
    private Map<String, String> watering;
    private Map<String, Double> heights;
    /**
     * Required by Firebase, this useless constructor must remain
     */

    private Plant() {
    }

    /**
     * Create a new plant
     * @param name - the name of the plant
     * @param species - the plant's species
     * @param birthday - the plant's birthday
     * @param height - the plant's height
     */
    Plant(String name, String species, long birthday, double height) {
        this.id = species + "_" + name;
        this.name = name;
        this.species = species;
        this.birthday = birthday;
        this.height = height;
        this.watering = new LinkedHashMap<>();
        this.heights = new LinkedHashMap<>();
        this.lastMeasureNotification = System.currentTimeMillis();
        this.lastFertilizerNotification = lastMeasureNotification;
        this.lastWaterNotification = lastMeasureNotification;
        this.lastPhotoNotification = lastMeasureNotification;
        this.heights.put(Long.toString(lastMeasureNotification), height);
        photoNum = -1;
        gifLocation = "No Gif made (yet!)";
    }

    /**
     * Get the plant's name
     * @return Returns the plant's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the plant's species
     * @return Returns the plant's species
     */
    public String getSpecies() {
        return species;
    }

    /**
     * Get the plant's id
     * @return Returns the plant's id
     */
    public String getId() {
        return id;
    }

    /**
     * Get the plant's birthday
     * @return Returns the plant's birthday
     */
    public long getBirthday() {
        return birthday;
    }

    /**
     * Get the plant heights
     * @return Returns a list of watering operations times in millis
     */
    public Map<String, String> getWatering() {
        return watering;
    }

    /**
     * Get the plant heights
     * @return Returns all of the plant's recorded heights
     */
    public Map<String, Double> getHeights() {
        return heights;
    }

    /**
     * Get the current plant height
     * @return Returns the latest plant height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Get the last time the plant was watered
     * @return Returns the last time the plant water notification was issued
     */
    public long getLastWaterNotification() {
        return lastWaterNotification;
    }

    /**
     * Get the last time the plant was watered in millis
     * @return Returns the last time the plant was watered in millis
     */
    public long getLastWatered() {
        long lastWatered = birthday;
        for (String key : watering.keySet()) {
            lastWatered = Math.max(lastWatered, Long.parseLong(watering.get(key)));
        }
        return lastWatered;
    }

    /**
     * retrieve the last time the user measured the plant
     * @return Returns the last time the plant height notification was issued
     */
    public long getLastMeasureNotification() {
        return lastMeasureNotification;
    }

    /**
     * Get last time plant was fertilized
     * @return Returns last time plant was fertilized
     */
    public long getLastFertilizerNotification() {
        return lastFertilizerNotification;
    }

    /**
     * Get last time user was prompted to take a picture
     * @return Returns the last time the user was prompted to take a picture
     */
    public long getLastPhotoNotification() {
        return lastPhotoNotification;
    }

    /**
     * Get the number of the most recent photo
     * @return Returns the number of the most recent photo
     */
    public int getPhotoNum() {
        return photoNum;
    }

    /**
     * Get gif location
     * @return Returns the location of the plant gif
     */
    public String getGifLocation() {
        return gifLocation;
    }

    /**
     * Represent the plant as a string
     * @return Returns a string representation of the plant
     */
    @Override
    public String toString() {
        return name + DELIMITER + species + DELIMITER;
    }

}
