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

package com.example.android.popularmovies.ui.trailer;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.TrailerListItemBinding;
import com.example.android.popularmovies.model.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.popularmovies.utilities.Constant.YOUTUBE_BASE_URL;
import static com.example.android.popularmovies.utilities.Constant.YOUTUBE_THUMBNAIL_BASE_URL;
import static com.example.android.popularmovies.utilities.Constant.YOUTUBE_THUMBNAIL_URL_JPG;

/**
 * {@link TrailerAdapter} exposes a list of trailers to a {@link RecyclerView}
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    /** Member variable for the list of {@link Video}s*/
    private List<Video> mVideos;

    /** An on-click handler that we've defined to make it easy for a Fragment to interface with
     * our RecyclerView
     */
    private final TrailerAdapterOnClickHandler mOnClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailerAdapterOnClickHandler {
        void onItemClick(String videoUrl);
    }

    /**
     * Constructor for TrailerAdapter that accepts a list of trailers to display
     *
     * @param videos List of {@link Video}s
     * @param onClickHandler The on-click handler for this adapter. This single handler is called
     *                       when an item is clicked.
     */
    public TrailerAdapter(List<Video> videos, TrailerAdapterOnClickHandler onClickHandler) {
        mVideos = videos;
        mOnClickHandler = onClickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout.
     * @return A new TrailerViewHolder that holds the TrailerListItemBinding
     */
    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        TrailerListItemBinding trailerItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.trailer_list_item, viewGroup, false);
        return new TrailerViewHolder(trailerItemBinding);
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
    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /** This field is used for data binding */
        TrailerListItemBinding mTrailerItemBinding;

        /**
         * Constructor for our ViewHolder
         *
         * @param trailerItemBinding Used to access the layout's variables and views
         */
        public TrailerViewHolder(TrailerListItemBinding trailerItemBinding) {
            super(trailerItemBinding.getRoot());
            mTrailerItemBinding = trailerItemBinding;

            // Call setOnClickListener on the trailer thumbnail ImageView
            mTrailerItemBinding.ivTrailerThumbnail.setOnClickListener(this);
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
                    .into(mTrailerItemBinding.ivTrailerThumbnail);

            // Get the video name and set name to the TextView to display the trailer name
            String videoName = video.getName();
            mTrailerItemBinding.tvTrailerName.setText(videoName);
        }

        /**
         * Called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Video video = mVideos.get(adapterPosition);
            // Get the video key
            String videoKey = video.getKey();
            // Get the complete YouTube video url to display a trailer video
            String videoUrl = YOUTUBE_BASE_URL + videoKey;
            mOnClickHandler.onItemClick(videoUrl);
        }
    }
}
