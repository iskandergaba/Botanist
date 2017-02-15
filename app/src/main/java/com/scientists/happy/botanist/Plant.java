package com.scientists.happy.botanist;

/**
 * Created by amusc on 2/14/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class Plant implements Parcelable {
    private static final String DELIMETER = "\t";

    private String nickname;
    private String species;
    private String photoPath;
    private int mData;
    private boolean deleted = false;

    public Plant(String nickname, String species, String photoPath) {
        this.nickname = nickname;
        this.species = species;
        this.photoPath = photoPath;
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

    // 99.9% of the time you can just ignore this
    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Plant> CREATOR = new Parcelable.Creator<Plant>() {
        public Plant createFromParcel(Parcel in) {
            return new Plant(in);
        }

        public Plant[] newArray(int size) {
            return new Plant[size];
        }
    };

    public void delete() {
        deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Plant(Parcel in) {
        mData = in.readInt();
    }

    @Override
    public String toString() {
        return nickname + DELIMETER + species + DELIMETER + photoPath;
    }
}
