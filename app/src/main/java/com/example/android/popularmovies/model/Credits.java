/*
 *  Copyright 2018 Soojeong Shin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.utilities.Constant.BYTE;

/**
 * A {@link Credits} object includes information related to movie cast and crew.
 * This class implements Parcelable interface to allow {@link Credits} object to be sent as a Parcel
 */
public class Credits implements Parcelable {

    @SerializedName("cast")
    private List<Cast> mCast;

    @SerializedName("crew")
    private List<Crew> mCrew;

    private Credits(Parcel in) {
        if (in.readByte() == BYTE) {
            mCast = new ArrayList<>();
            in.readList(mCast, Cast.class.getClassLoader());
        } else {
            mCast = null;
        }
        if (in.readByte() == BYTE) {
            mCrew = new ArrayList<>();
            in.readList(mCrew, Crew.class.getClassLoader());
        } else {
            mCrew = null;
        }
    }

    @SuppressWarnings("unused")
    public static final Creator<Credits> CREATOR = new Creator<Credits>() {
        @Override
        public Credits createFromParcel(Parcel in) {
            return new Credits(in);
        }

        @Override
        public Credits[] newArray(int size) {
            return new Credits[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mCast == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mCast);
        }
        if (mCrew == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mCrew);
        }
    }
}
