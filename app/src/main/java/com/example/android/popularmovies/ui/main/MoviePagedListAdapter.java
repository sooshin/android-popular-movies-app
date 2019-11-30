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

package com.example.android.popularmovies.ui.main;

import androidx.paging.PagedListAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.MovieListItemBinding;
import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import static com.example.android.popularmovies.utilities.Constant.IMAGE_BASE_URL;
import static com.example.android.popularmovies.utilities.Constant.IMAGE_FILE_SIZE;

/**
 * {@link MoviePagedListAdapter} is responsible for presenting movie data from PagedList in a RecyclerView.
 * The PagedListAdapter is notified when pages are loaded, and it uses DiffUtil to compute fine grain
 * updates as new data is received.
 */
public class MoviePagedListAdapter extends PagedListAdapter<Movie, MoviePagedListAdapter.MoviePagedViewHolder> {

    /** An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final MoviePagedListAdapter.MoviePagedListAdapterOnClickHandler mOnClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MoviePagedListAdapterOnClickHandler {
        void onItemClick(Movie movie);
    }

    /**
     * Tell MoviePagedListAdapter how to compute the differences between the two elements
     */
    private static DiffUtil.ItemCallback<Movie> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Movie>() {
                // The ID property identifies when items are the same
                @Override
                public boolean areItemsTheSame(Movie oldItem, Movie newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(Movie oldItem, Movie newItem) {
                    return oldItem.equals(newItem);
                }
            };

    /**
     * Constructor for MoviePagedListAdapter
     *
     * @param onClickHandler The on-click handler for this adapter. This single handler
     *                      is called when an item is clicked
     */
    public MoviePagedListAdapter(MoviePagedListAdapterOnClickHandler onClickHandler) {
        super(MoviePagedListAdapter.DIFF_CALLBACK);
        mOnClickHandler = onClickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param parent The ViewGroup that these ViewHolders are contained within.
     * @param viewType If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout.
     * @return A new MoviePagedViewHolder that holds the MovieListItemBinding
     */
    @NonNull
    @Override
    public MoviePagedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        MovieListItemBinding mMovieItemBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.movie_list_item, parent, false);

        return new MoviePagedViewHolder(mMovieItemBinding);
    }

    /**
     * Called by the RecyclerView to display the data at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MoviePagedViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    /**
     * Cache of the children views for a list item.
     */
    public class MoviePagedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /** This field is used for data binding */
        private MovieListItemBinding mMovieItemBinding;

        /**
         * Constructor for the MoviePagedViewHolder
         *
         * @param movieItemBinding Used to access the layout's variables and views
         */
        public MoviePagedViewHolder(MovieListItemBinding movieItemBinding) {
            super(movieItemBinding.getRoot());
            mMovieItemBinding = movieItemBinding;
            // Call setOnClickListener on the view
            itemView.setOnClickListener(this);
        }

        /**
         * This method will take a Movie object as input and use that movie to display the appropriate
         * text and an image within a list item.
         *
         * @param movie The movie object
         */
        void bind(Movie movie) {
            // Get the complete thumbnail path
            String thumbnail = IMAGE_BASE_URL + IMAGE_FILE_SIZE + movie.getPosterPath();

            // Load thumbnail with Picasso library
            Picasso.with(itemView.getContext())
                    .load(thumbnail)
                    .error(R.drawable.image)
                    .into(mMovieItemBinding.ivThumbnail);

            // Display the title
            mMovieItemBinding.tvTitle.setText(movie.getTitle());
        }

        /**
         * Called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = getItem(adapterPosition);
            mOnClickHandler.onItemClick(movie);
        }
    }
}
