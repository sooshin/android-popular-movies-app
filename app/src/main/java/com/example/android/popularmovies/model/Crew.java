package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

public class Crew {

    @SerializedName("job")
    private String mJob;

    @SerializedName("name")
    private String mName;

    public void setJob(String job) {
        mJob = job;
    }

    public String getJob() {
        return mJob;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
