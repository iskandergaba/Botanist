package com.scientists.happy.botanist.data;

import java.util.LinkedHashMap;
import java.util.Map;

class Plant {
    private static final String DELIMETER = "\t";

    private String name, species, id;

    private Map<String, Double> heights;

    private long birthday, lastWatered, lastMeasureNotification;

    //Required by Firebase, This useless constructor must remain
    private Plant() {}

    Plant(String name, String species, long birthday, double height) {
        this.id = species + "_" + name;
        this.name = name;
        this.species = species;
        this.birthday = birthday;
        this.heights = new LinkedHashMap<>();
        this.lastMeasureNotification = System.currentTimeMillis();
        this.lastWatered = lastMeasureNotification;
        this.heights.put(Long.toString(lastMeasureNotification), height);
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

    public Map<String, Double> getHeights() {
        return heights;
    }

    public long getLastWatered() {
        return lastWatered;
    }

    public long getLastMeasureNotification() {
        return lastMeasureNotification;
    }

    @Override
    public String toString() {
        return name + DELIMETER + species + DELIMETER + "";
    }
}
