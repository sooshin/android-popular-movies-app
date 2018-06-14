package com.example.android.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    /** Member variable for the list of {@link Movie}s */
    private List<Movie> mMovies;

    /** An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final MovieAdapterOnClickHandler mOnClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onItemClick(Movie movie);
    }

    /**
     * Constructor for MovieAdapter that accepts a list of movies to display
     *
     * @param movies List of {@link Movie}
     * @param onClickHandler The on-click handler for this adapter. This single handler is called
     *                       when an item is clicked.
     */
    public MovieAdapter(List<Movie> movies, MovieAdapterOnClickHandler onClickHandler) {
        mMovies = movies;
        mOnClickHandler = onClickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout.
     * @return A new NumberViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_list_item, viewGroup, false);
        return new MovieViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     *
     * @param holder  The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MovieViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        String title = movie.getTitle();
        String thumbnail = movie.getThumbnail();
        double voteAvg = movie.getVoteAverage();
        holder.titleTextView.setText(title);
        Picasso.with(holder.itemView.getContext())
                .load(thumbnail)
                .into(holder.thumbnailImageView);
        holder.voteAvgTextView.setText(String.valueOf(voteAvg));
    }

    /**
     * This method simply return the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of movies
     */
    @Override
    public int getItemCount() {
        if (null == mMovies) return 0;
        return mMovies.size();
    }

    public void setMovies(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    /**
     * This method is to clear all data, a list of {@link Movie} objects
     */
    public void clearAll() {
        mMovies.clear();
        notifyDataSetChanged();
    }

    /**
     * This method is to add a list of {@link Movie}s
     *
     * @param movies movies is the data source of the adapter
     */
    public void addAll(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a movie list item.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iv_thumbnail) ImageView thumbnailImageView;
        @BindView(R.id.tv_title) TextView titleTextView;
        @BindView(R.id.tv_vote_average) TextView voteAvgTextView;

        /**
         * Constructor for our ViewHolder.
         * @param itemView The View that you inflated in {@link MovieAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public MovieViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            // Call setOnClickListener on  the View passed into the constructor
            itemView.setOnClickListener(this);
        }

        /**
         * Called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMovies.get(adapterPosition);
            mOnClickHandler.onItemClick(movie);
        }
    }
}
