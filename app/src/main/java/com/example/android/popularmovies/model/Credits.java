package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Credits {

    @SerializedName("cast")
    private List<Cast> mCast = null;

    @SerializedName("crew")
    private List<Crew> mCrew = null;

    public void setCast(List<Cast> cast) {
        mCast = cast;
    }

    public List<Cast> getCast() {
        return mCast;
    }

    public void setCrew(List<Crew> crew) {
        mCrew = crew;
    }

    public List<Crew> getCrew() {
        return mCrew;
    }
}
