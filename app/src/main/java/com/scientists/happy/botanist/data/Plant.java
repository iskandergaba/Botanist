package com.scientists.happy.botanist.data;

import java.util.ArrayList;
import java.util.List;

public class Plant {
    private static final String DELIMETER = "\t";

    private String name, species, id;

    private List<Double> heights;

    private long birthday, lastWatered;

    //Required by Firebase, This useless constructor must remain
    private Plant() {}

    Plant(String name, String species, long birthday, double height) {
        this.id = species + "_" + name;
        this.name = name;
        this.species = species;
        this.birthday = birthday;
        this.heights = new ArrayList<>();
        this.heights.add(height);
    }

    public String getName() {
        return name;
    }

    String getSpecies() {
        return species;
    }

    public String getId() {
        return id;
    }

    long getBirthday() {
        return birthday;
    }

    public List<Double> getHeights() {
        return heights;
    }

    public long getLastWatered() {
        return lastWatered;
    }

    @Override
    public String toString() {
        return name + DELIMETER + species + DELIMETER + "";
    }

}
