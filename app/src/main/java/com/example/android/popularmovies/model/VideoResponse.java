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

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * The {@link VideoResponse} object includes information related to the movie trailers
 */
public class VideoResponse {

    @SerializedName("id")
    private int mId;

    @SerializedName("results")
    private List<Video> mVideoResults = null;

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setVideoResults(List<Video> videoResults) {
        mVideoResults = videoResults;
    }

    public List<Video> getVideoResults() {
        return mVideoResults;
    }
}
