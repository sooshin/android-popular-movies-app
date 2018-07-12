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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popularmovies.utilities.Constant.IMAGE_BASE_URL;
import static com.example.android.popularmovies.utilities.Constant.IMAGE_FILE_SIZE;

/**
 * Exposes a list of favorite movies from a list of {@link MovieEntry} to a {@link RecyclerView}
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    /** Member variable for the list of MovieEntries that holds movie data */
    private List<MovieEntry> mMovieEntries;

    /** Context we use to utility methods, app resources and layout inflaters */
    private Context mContext;

    /** An on-click handler that we've defined to make it easy for a Activity to interface with
     * our RecyclerView
     */
    private final FavoriteAdapterOnClickHandler mOnClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface FavoriteAdapterOnClickHandler {
        void onFavItemClick(MovieEntry movieEntry);
    }

    /**
     * Constructor for the FavoriteAdapter
     */
    public FavoriteAdapter(Context context, FavoriteAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mOnClickHandler = onClickHandler;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView
     *
     * @return A new FavoriteViewHolder that holds the view for each movie
     */
    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_list_item, parent, false);
        return new FavoriteViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display the data at the specified position.
     *
     * @param holder  The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        MovieEntry movieEntry = mMovieEntries.get(position);
        holder.bind(movieEntry);
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mMovieEntries == null) return 0;
        return mMovieEntries.size();
    }

    /**
     * When data changes, updates the list of movieEntries
     * and notifies the adapter to use the new values on it
     */
    public void setMovies(List<MovieEntry> movieEntries) {
        mMovieEntries = movieEntries;
        notifyDataSetChanged();
    }

    /**
     * Returns the list of MovieEntries
     */
    public List<MovieEntry> getMovies() {
        return mMovieEntries;
    }

    /**
     * Cache of the children views for favorite movie list item.
     */
    public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        /** Get a reference to the ImageView for showing thumbnail image */
        @BindView(R.id.iv_thumbnail)
        ImageView mThumbnailImageView;

        /** Get a reference to the TextView for showing the movie title */
        @BindView(R.id.tv_title)
        TextView mTitleTextView;

        /**
         * Constructor for FavoriteViewHolder
         */
        public FavoriteViewHolder(View itemView) {
            super(itemView);

            // Bind the view using ButterKnife
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(MovieEntry movieEntry) {
            // Get the complete thumbnail path
            String thumbnail = IMAGE_BASE_URL + IMAGE_FILE_SIZE + movieEntry.getPosterPath();

            // Load thumbnail with Picasso library
            Picasso.with(itemView.getContext())
                    .load(thumbnail)
                    .into(mThumbnailImageView);

            mTitleTextView.setText(movieEntry.getTitle());
        }

        /**
         * Called whenever a user clicks on an movie in the list
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieEntry movieEntry = mMovieEntries.get(adapterPosition);

            mOnClickHandler.onFavItemClick(movieEntry);
        }
    }
}
