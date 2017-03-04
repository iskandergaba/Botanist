package com.scientists.happy.botanist;

import java.util.GregorianCalendar;

public class Plant /*implements Parcelable*/ {
    private static final String DELIMETER = "\t";

    private String nickname;
    private String species;
    private String photoPath;
    private GregorianCalendar birthday;
    private GregorianCalendar lastWatered;
    private int id;

    private boolean deleted = false;

    public Plant(String nickname, String species, String photoPath, GregorianCalendar birthday) {
        this.nickname = nickname;
        this.species = species;
        this.photoPath = photoPath;
        this.birthday = birthday;
    }

    public String getNickname() {
        return nickname;
    }

    public String getSpecies() {
        return species;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void delete() {
        deleted = true;
    }

    public GregorianCalendar getBirthday() {
        return birthday;
    }

    public GregorianCalendar getLastWatered() {
        return lastWatered;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return nickname + DELIMETER + species + DELIMETER + photoPath;
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
