// Plant object
// @author: Antonio Muscarella and Iskander Gaba
package com.scientists.happy.botanist.data;
import java.util.ArrayList;
import java.util.List;
public class Plant {
    private static final String DELIMITER = "\t";
    private String name, species, id;
    private List<Double> heights;
    private long birthday, lastWatered;
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
        this.heights = new ArrayList<>();
        this.heights.add(height);
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
    public List<Double> getHeights() {
        return heights;
    }

    /**
     * Get the last time the plant was watered
     * @return Returns the last time the plant was watered
     */
    public long getLastWatered() {
        return lastWatered;
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