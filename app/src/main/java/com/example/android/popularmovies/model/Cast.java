package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * A {@link Cast} object includes information related to movie cast.
 * This class implements Parcelable interface to allow {@link Cast} object to be sent as a Parcel
 */
public class Cast implements Parcelable {

    @SerializedName("cast_id")
    private int mCastId;

    @SerializedName("character")
    private String mCharacter;

    @SerializedName("id")
    private int mPersonId;

    @SerializedName("name")
    private String mName;

    @SerializedName("profile_path")
    private String mProfilePath;

    private Cast(Parcel in) {
        mCastId = in.readInt();
        mCharacter = in.readString();
        mPersonId = in.readInt();
        mName = in.readString();
        mProfilePath = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Creator<Cast> CREATOR = new Creator<Cast>() {
        @Override
        public Cast createFromParcel(Parcel in) {
            return new Cast(in);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };

    public void setCharacter(String character) {
        mCharacter = character;
    }

    public String getCharacter() {
        return mCharacter;
    }

    public void setPersonId(int personId) {
        mPersonId = personId;
    }

    public int getPersonId() {
        return mPersonId;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setProfilePath(String profilePath) {
        mProfilePath = profilePath;
    }

    public String getProfilePath() {
        return mProfilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mCastId);
        dest.writeString(mCharacter);
        dest.writeInt(mPersonId);
        dest.writeString(mName);
        dest.writeString(mProfilePath);
    }
}
