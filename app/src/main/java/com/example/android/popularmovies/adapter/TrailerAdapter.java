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

package com.example.android.popularmovies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popularmovies.utilities.Constant.YOUTUBE_THUMBNAIL_BASE_URL;
import static com.example.android.popularmovies.utilities.Constant.YOUTUBE_THUMBNAIL_URL_JPG;

/**
 * {@link TrailerAdapter} exposes a list of trailers to a {@link android.support.v7.widget.RecyclerView}
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    /** Member variable for the list of {@link Video}s*/
    private List<Video> mVideos;

    /**
     * Constructor for TrailerAdapter that accepts a list of trailers to display
     *
     * @param videos List of {@link Video}s
     */
    public TrailerAdapter(List<Video> videos) {
        mVideos = videos;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout.
     * @return A new TrailerViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.trailer_list_item, viewGroup, false);
        return new TrailerViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Video video = mVideos.get(position);
        holder.bind(video);
    }

    /**
     * This method simply return the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of trailers
     */
    @Override
    public int getItemCount() {
        if (null == mVideos) return 0;
        return mVideos.size();
    }

    /**
     * This method is to add a list of {@link Video}s
     *
     * @param videos The videos is the data source fo the adapter
     */
    public void addAll(List<Video> videos) {
        mVideos.clear();
        mVideos.addAll(videos);
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a trailer list item.
     */
    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        /** Get a reference to the ImageView to display trailer thumbnail */
        @BindView(R.id.iv_trailer_thumbnail)
        ImageView mTrailerThumbnailImageView;

        /** Get a reference to the */
        @BindView(R.id.tv_trailer_name)
        TextView mTrailerNameTextView;

        /**
         * Constructor for our ViewHolder
         *
         * @param itemView The View that you inflated in {@link TrailerAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public TrailerViewHolder(View itemView) {
            super(itemView);

            // Bind the view using ButterKnife
            ButterKnife.bind(this, itemView);
        }

        void bind(Video video) {
            // Get the video ID
            String videoKey = video.getKey();
            // Get the complete the trailer thumbnail url
            String trailerThumbnailUrl = YOUTUBE_THUMBNAIL_BASE_URL + videoKey +
                    YOUTUBE_THUMBNAIL_URL_JPG;

            // Load trailer thumbnail with Picasso library
            Picasso.with(itemView.getContext())
                    .load(trailerThumbnailUrl)
                    .into(mTrailerThumbnailImageView);

            // Get the video name and set name to the TextView to display the trailer name
            String videoName = video.getName();
            mTrailerNameTextView.setText(videoName);
        }
    }
}
