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

package com.example.android.popularmovies.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmovies.EndlessRecyclerViewScrollListener;
import com.example.android.popularmovies.GridSpacingItemDecoration;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.FavoriteAdapter;
import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.adapter.MovieAdapter.MovieAdapterOnClickHandler;
import com.example.android.popularmovies.data.MovieEntry;
import com.example.android.popularmovies.data.MoviePreferences;
import com.example.android.popularmovies.databinding.ActivityMainBinding;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieResponse;
import com.example.android.popularmovies.utilities.InjectorUtils;
import com.example.android.popularmovies.viewmodel.MainActivityViewModel;
import com.example.android.popularmovies.viewmodel.MainViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;
import static com.example.android.popularmovies.utilities.Constant.GRID_INCLUDE_EDGE;
import static com.example.android.popularmovies.utilities.Constant.GRID_SPACING;
import static com.example.android.popularmovies.utilities.Constant.GRID_SPAN_COUNT;
import static com.example.android.popularmovies.utilities.Constant.LAYOUT_MANAGER_STATE;
import static com.example.android.popularmovies.utilities.Constant.REQUEST_CODE_DIALOG;

/**
 * The MainActivity displays the list of movies that appear as a grid of images
 */
public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler,
        FavoriteAdapter.FavoriteAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /** Tag for a log message */
    private static final String TAG = MainActivity.class.getSimpleName();

    /** Reference to MovieAdapter*/
    private MovieAdapter mMovieAdapter;

    /** Exposes a list of favorite movies from a list of MovieEntry to a RecyclerView */
    private FavoriteAdapter mFavoriteAdapter;

    /** String for the sort criteria("most popular and highest rated") */
    private String mSortCriteria;

    /** Member variable for restoring list items positions on device rotation */
    private Parcelable mSavedLayoutState;

    /** ViewModel for MainActivity */
    private MainActivityViewModel mMainViewModel;

    /** Member variable for the list of movies */
    private List<Movie> mMovies;

    /** This field is used for data binding */
    private ActivityMainBinding mMainBinding;

    /** Member variable for the EndlessRecyclerViewScrollListener */
    private EndlessRecyclerViewScrollListener mScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // A GridLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a grid layout.
        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);
        // Set the layout manager to the RecyclerView
        mMainBinding.rvMovie.setLayoutManager(layoutManager);

        // Use this setting to improve performance if you know that changes in content do not
        // change the child layout size in the RecyclerView
        mMainBinding.rvMovie.setHasFixedSize(true);

        // Create an empty array list
        List<Movie> movies = new ArrayList<>();
        // Create MovieAdapter that is responsible for linking our movie data with the Views
        mMovieAdapter = new MovieAdapter(movies, this);
        // Create FavoriteAdapter that is responsible for linking favorite movies with the Views
        mFavoriteAdapter = new FavoriteAdapter(this, this);

        // Show a dialog when there is no internet connection
        showNetworkDialog(isOnline());

        // Get the sort criteria currently set in Preferences
        mSortCriteria = MoviePreferences.getPreferredSortCriteria(this);

        // Get the ViewModel from the factory
        setupViewModel(mSortCriteria);
        // Update the UI depending on the sort order
        updateUI(mSortCriteria);

        // Load automatically more movies as the user scrolls through the movies
        setEndlessScrolling(layoutManager);

        // Register MainActivity as an OnPreferenceChangedListener to receive a callback when a
        // SharedPreference has changed. Please note that we must unregister MainActivity as an
        // OnSharedPreferenceChanged listener in onDestroy to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        // Set the color scheme of the SwipeRefreshLayout and setup OnRefreshListener
        setSwipeRefreshLayout();

        // Set column spacing to make each column have the same spacing
        setColumnSpacing();

        if (savedInstanceState != null) {
            // Get the scroll position
            mSavedLayoutState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE);
            // Restore the scroll position
            mMainBinding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
        }
    }

    /**
     * Load automatically more movies as the user scrolls through the movies
     *
     * @param layoutManager
     */
    private void setEndlessScrolling(GridLayoutManager layoutManager) {
        // Retain an instance so that you can call `resetState()` for fresh searches
        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Load next page of movies
                loadNextMovieData(page, mSortCriteria);
            }
        };
        // Associate RecyclerView with the EndlessRecyclerViewScrollListener to enable endless pagination
        mMainBinding.rvMovie.addOnScrollListener(mScrollListener);
    }

    /**
     *
     * @param offset
     * @param sortBy
     */
    private void loadNextMovieData(int offset, String sortBy) {
        if (sortBy.equals(getString(R.string.pref_sort_by_favorites))) {
            mMainViewModel.setFavoriteMovies();
            // Set the FavoriteAdapter to the RecyclerView
            mMainBinding.rvMovie.setAdapter(mFavoriteAdapter);
            // Update the list of MovieEntries from LiveData in MainActivityViewModel
            observeFavoriteMovies();
        } else {
            for (offset = 2; offset < 30; offset++) {
                mMainViewModel.setNextMovieResponse(sortBy, offset);

                // Set the MovieAdapter to the RecyclerView
                mMainBinding.rvMovie.setAdapter(mMovieAdapter);
                mMainViewModel.getNextMovieResponse().observe(this, new Observer<MovieResponse>() {
                    @Override
                    public void onChanged(@Nullable MovieResponse movieResponse) {
                        if (movieResponse != null) {
                            // Get the list of next movies
                            List<Movie> moviesNext = movieResponse.getMovieResults();

                            // Append the list of next movies to the existing set of movies
                            mMovies.addAll(moviesNext);
                            // Add a list of Movies to the movie adapter
                            mMovieAdapter.addAll(mMovies);

                            mScrollListener.resetState();

                            // Restore the scroll position after setting up the adapter with the list of movies
                            mMainBinding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
                        }
                    }
                });
            }
        }
    }

    /**
     * Get the MainActivityViewModel from the factory
     */
    private void setupViewModel(String sortCriteria) {
        MainViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(
                MainActivity.this, sortCriteria);
        mMainViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);
    }

    /**
     * Update the UI depending on the sort criteria
     */
    private void updateUI(String sortCriteria) {
        // Set a new value for the MovieResponse
        mMainViewModel.setMovieResponse(sortCriteria);
        // Set a new value for the list of MovieEntries
        mMainViewModel.setFavoriteMovies();

        // If the sortCriteria is equal to "favorites", set the FavoriteAdapter to the RecyclerView
        // and observe the list of MovieEntry and update UI to display favorite movies
        if (sortCriteria.equals(getString(R.string.pref_sort_by_favorites))) {
            mMainBinding.rvMovie.setAdapter(mFavoriteAdapter);
            observeFavoriteMovies();
        } else {
            // Otherwise, set the MovieAdapter to the RecyclerView and observe the MovieResponse
            // and update the UI to display movies
            mMainBinding.rvMovie.setAdapter(mMovieAdapter);
            observeMovieResponse();
        }
    }

    /**
     * Update the list of MovieEntries from LiveData in MainActivityViewModel
     */
    private void observeFavoriteMovies() {
        mMainViewModel.getFavoriteMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                // Set the list of MovieEntries to display favorite movies
                mFavoriteAdapter.setMovies(movieEntries);

                // Restore the scroll position after setting up the adapter with the list of favorite movies
                mMainBinding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);

                if (movieEntries == null) {
                    // Display Empty view
                    // ToDo:
                } else if(!isOnline()) {
                    showMovieDataView();
                }
            }
        });
    }

    /**
     * Update the MovieResponse from LiveData in MainActivityViewModel
     */
    private void observeMovieResponse() {
        mMainViewModel.getMovieResponse().observe(this, new Observer<MovieResponse>() {
            @Override
            public void onChanged(@Nullable MovieResponse movieResponse) {
                if (movieResponse != null) {
                    // Get the list of movies
                    mMovies = movieResponse.getMovieResults();
                    // Add a list of Movies
                    mMovieAdapter.addAll(mMovies);
                    // Restore the scroll position after setting up the adapter with the list of movies
                    mMainBinding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
                }

                // Show the movie list or the loading screen based on whether the movie data exists
                // and is loaded.
                if (movieResponse != null && !movieResponse.getMovieResults().isEmpty()) {
                    hideLoadingAndRefresh();
                    // Hide offline message and show movie data
                    showMovieDataView();
                } else if (!isOnline()) {
                    // When offline, show a message displaying that it is offline
                    hideLoadingAndRefresh();
                    showOfflineMessage();
                } else {
                    showLoading();
                }
            }
        });
    }

    /**
     * When preferences have been changed, make a network request again.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // If the key is equal to sort_by, update the sort by preference
        if (key.equals(getString(R.string.pref_sort_by_key))) {
            mSortCriteria = sharedPreferences.getString(key, getString(R.string.pref_sort_by_default));
        }

        // When SharedPreference changes, observe the data and update the UI
        updateUI(mSortCriteria);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item clicks.
     *
     * @param movie The movie that was clicked
     */
    @Override
    public void onItemClick(Movie movie) {
        // Wrap the parcelable into a bundle
        // Reference: @see "https://stackoverflow.com/questions/28589509/android-e-parcel-
        // class-not-found-when-unmarshalling-only-on-samsung-tab3"
        Bundle b = new Bundle();
        b.putParcelable(EXTRA_MOVIE, movie);

        // Create the Intent the will start the DetailActivity
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        // Pass the bundle through Intent
        intent.putExtra(EXTRA_MOVIE, b);
        // Once the Intent has been created, start the DetailActivity
        startActivity(intent);
    }

    @Override
    public void onFavItemClick(MovieEntry movieEntry) {

        int movieId = movieEntry.getMovieId();
        String originalTitle = movieEntry.getOriginalTitle();
        String title = movieEntry.getTitle();
        String posterPath = movieEntry.getPosterPath();
        String overview = movieEntry.getOverview();
        double voteAverage = movieEntry.getVoteAverage();
        String releaseDate = movieEntry.getReleaseDate();
        String backdropPath = movieEntry.getBackdropPath();

        Movie movie = new Movie(movieId, originalTitle, title, posterPath, overview,
                voteAverage, releaseDate, backdropPath);

        // Wrap the parcelable into a bundle
        Bundle b = new Bundle();
        b.putParcelable(EXTRA_MOVIE, movie);

        // Create the Intent the will start the DetailActivity
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        // Pass the bundle through Intent
        intent.putExtra(EXTRA_MOVIE, b);
        // Once the Intent has been created, start the DetailActivity
        startActivity(intent);
    }

    /**
     *  Set the SwipeRefreshLayout triggered by a swipe gesture.
     */
    private void setSwipeRefreshLayout() {
        // Set the colors used in the progress animation
        mMainBinding.swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        // Set the listener to be notified when a refresh is triggered
        mMainBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            /**
             * Called when a swipe gesture triggers a refresh
             */
            @Override
            public void onRefresh() {
                // Make movie data visible and hide error message
                showMovieDataView();

                // When refreshing, observe the data and update the UI
                updateUI(mSortCriteria);

                hideLoadingAndRefresh();
                // Show snack bar message
                Snackbar.make(mMainBinding.rvMovie, getString(R.string.snackbar_updated)
                        , Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Check if there is the network connectivity
     *
     * @return true if connected to the network
     */
    private boolean isOnline() {
        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * Show a dialog when there is no internet connection
     *
     * @param isOnline true if connected to the network
     */
    private void showNetworkDialog(final boolean isOnline) {
        if (!isOnline) {
            // Create an AlertDialog.Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
            // Set an Icon and title, and message
            builder.setIcon(R.drawable.ic_warning);
            builder.setTitle(getString(R.string.no_network_title));
            builder.setMessage(getString(R.string.no_network_message));
            builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), REQUEST_CODE_DIALOG);
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), null);

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the offline message.
     */
    private void showMovieDataView() {
        // First, make sure the offline message or error message is invisible
        mMainBinding.tvOffline.setVisibility(View.INVISIBLE);
        mMainBinding.tvError.setVisibility(View.INVISIBLE);
        // Then, make sure the movie data is visible
        mMainBinding.rvMovie.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the offline message visible and hide the movie View
     */
    private void showOfflineMessage() {
        // First, hide the currently visible data
        mMainBinding.rvMovie.setVisibility(View.INVISIBLE);
        // Then, show the offline message
        mMainBinding.tvOffline.setVisibility(View.VISIBLE);
    }

    /**
     * When an error occurred, display error message
     */
    private void showErrorMessage() {
        // First, hide the currently visible data
        mMainBinding.rvMovie.setVisibility(View.INVISIBLE);
        // Then, show an error message
        mMainBinding.tvError.setVisibility(View.VISIBLE);
        mMainBinding.tvError.setText(getString(R.string.error_message_failed));
    }

    /**
     * Set column spacing to make each column have the same spacing.
     */
    private void setColumnSpacing() {
        GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(
                GRID_SPAN_COUNT, GRID_SPACING, GRID_INCLUDE_EDGE);
        mMainBinding.rvMovie.addItemDecoration(decoration);
    }

    /**
     * Methods for setting up the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                // Launch SettingsActivity when the Settings option is clicked
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method for persisting data across Activity recreation
     *
     * Reference: @see "https://stackoverflow.com/questions/27816217/how-to-save-recyclerviews-scroll
     * -position-using-recyclerview-state"
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Store the scroll position in our bundle
        outState.putParcelable(LAYOUT_MANAGER_STATE,
                mMainBinding.rvMovie.getLayoutManager().onSaveInstanceState());
    }

    private void showLoading() {
        // First, hide the movie data
        mMainBinding.rvMovie.setVisibility(View.INVISIBLE);
        // Then, show the loading indicator
        mMainBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingAndRefresh() {
        // First, hide the loading indicator
        mMainBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        // Hide refresh progress
        mMainBinding.swipeRefresh.setRefreshing(false);
        // Then, make sure the movie data is visible
        mMainBinding.rvMovie.setVisibility(View.VISIBLE);
    }
}
