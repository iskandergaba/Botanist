package com.scientists.happy.botanist.data;

public class Plant /*implements Parcelable*/ {
    private static final String DELIMETER = "\t";

    private String name;
    private String species;
    private long birthday;
    private long lastWatered;
    private String id;

    private boolean deleted = false;

    private Plant() {}

    public Plant(String name, String species, long birthday) {
        this.id = species + "_" + name;
        this.name = name;
        this.species = species;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public String getId() {
        return id;
    }

    public long getBirthday() {
        return birthday;
    }

    public long getLastWatered() {
        return lastWatered;
    }

    public void delete() {
        deleted = true;
    }

    @Override
    public String toString() {
        return name + DELIMETER + species + DELIMETER + "";
    }

//    // 99.9% of the time you can just ignore this
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    // write your object's data to the passed-in Parcel
//    @Override
//    public void writeToParcel(Parcel out, int flags) {
//        out.writeInt(mData);
//    }
//
//    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
//    public static final Parcelable.Creator<Plant> CREATOR = new Parcelable.Creator<Plant>() {
//        public Plant createFromParcel(Parcel in) {
//            return new Plant(in);
//        }
//
//        public Plant[] newArray(int size) {
//            return new Plant[size];
//        }
//    };
//
//
//    public boolean isDeleted() {
//        return deleted;
//    }
//
//    // example constructor that takes a Parcel and gives you an object populated with it's values
//    private Plant(Parcel in) {
//        mData = in.readInt();
//    }
}
