package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

public class Cast {

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
}
