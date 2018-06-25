package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Crew implements Parcelable {

    @SerializedName("job")
    private String mJob;

    @SerializedName("name")
    private String mName;

    private Crew(Parcel in) {
        mJob = in.readString();
        mName = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Creator<Crew> CREATOR = new Creator<Crew>() {
        @Override
        public Crew createFromParcel(Parcel in) {
            return new Crew(in);
        }

        @Override
        public Crew[] newArray(int size) {
            return new Crew[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mJob);
        dest.writeString(mName);
    }
}
