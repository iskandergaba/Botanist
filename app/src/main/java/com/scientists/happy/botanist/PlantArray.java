package com.scientists.happy.botanist;

import java.util.ArrayList;

public class PlantArray {
    private static PlantArray pa;
    private static ArrayList<Plant>  plants;

    private PlantArray() {
        plants = new ArrayList<Plant>();
    }

    public static PlantArray getInstance() {
        if (pa == null) {
            pa = new PlantArray();
        }
        return pa;
    }

    public void add(Plant p) {
        plants.add(p);
    }

    public Plant remove(Plant p) {
        int tgt = -1;
        for (int i = 0; i < plants.size(); i++) {
            if (plants.get(i) == p) {
                tgt = i;
                break;
            }
        }
        if (tgt != -1) {
            Plant rslt = plants.remove(tgt);
            rslt.delete();
            return rslt;
        } else {
            return null;
        }
    }

    public Plant remove(String nickname) {
        int tgt = -1;
        for (int i = 0; i < plants.size(); i++) {
            if (plants.get(i).getName().equals(nickname)) {
                tgt = i;
                break;
            }
        }
        if (tgt != -1) {
            Plant rslt = plants.remove(tgt);
            rslt.delete();
            return rslt;
        } else {
            return null;
        }
    }

    public Plant get(int idx) {
        return plants.get(idx);
    }

    public Plant get(String nickname) {
        for (int i = 0; i < plants.size(); i++) {
            if (plants.get(i).getName().equals(nickname)) {
                return plants.get(i);
            }
        }
        return null;
    }

    public int size() {
        return plants.size();
    }


}
