// Plant object
// @author: Antonio Muscarella and Iskander Gaba
package com.scientists.happy.botanist.data;
import java.util.LinkedHashMap;
import java.util.Map;
class Plant {
    private static final String DELIMITER = "\t";
    private String name, species, id;
    private Map<String, Double> heights;
    private long birthday, lastWatered, lastMeasureNotification, lastFertilized;
    private double height;
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
    protected Plant(String name, String species, long birthday, double height) {
        this.id = species + "_" + name;
        this.name = name;
        this.species = species;
        this.birthday = birthday;
        this.height = height;
        this.heights = new LinkedHashMap<>();
        this.lastMeasureNotification = System.currentTimeMillis();
        this.lastFertilized = lastMeasureNotification;
        this.lastWatered = lastMeasureNotification;
        this.heights.put(Long.toString(lastMeasureNotification), height);
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
    String getSpecies() {
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
    long getBirthday() {
        return birthday;
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
     * @return Returns the last time the plant was watered
     */
    public long getLastWatered() {
        return lastWatered;
    }

    /**
     * retrieve the last time the user measured the plant
     * @return Returns when the plant was last measured
     */
    public long getLastMeasureNotification() {
        return lastMeasureNotification;
    }

    /**
     * Get last time plant was fertilized
     * @return Returns last time plant was fertilized
     */
    public long getLastFertilized() {
        return lastFertilized;
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
