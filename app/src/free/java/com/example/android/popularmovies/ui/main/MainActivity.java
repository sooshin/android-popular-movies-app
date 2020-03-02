
/*
 *  Copyright 2020 Soojeong Shin
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

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies.GridSpacingItemDecoration;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.ui.detail.DetailActivity;
import com.example.android.popularmovies.settings.SettingsActivity;
import com.example.android.popularmovies.data.MovieEntry;
import com.example.android.popularmovies.data.MoviePreferences;
import com.example.android.popularmovies.databinding.ActivityMainBinding;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.InjectorUtils;

import java.util.List;

import static com.example.android.popularmovies.utilities.Constant.DRAWABLES_ZERO;
import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;
import static com.example.android.popularmovies.utilities.Constant.GRID_INCLUDE_EDGE;
import static com.example.android.popularmovies.utilities.Constant.GRID_SPACING;
import static com.example.android.popularmovies.utilities.Constant.GRID_SPAN_COUNT;
import static com.example.android.popularmovies.utilities.Constant.LAYOUT_MANAGER_STATE;
import static com.example.android.popularmovies.utilities.Constant.REQUEST_CODE_DIALOG;

/**
 * The MainActivity displays the list of movies that appear as a grid of images
 */
public class MainActivity extends AppCompatActivity implements
        FavoriteAdapter.FavoriteAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MoviePagedListAdapter.MoviePagedListAdapterOnClickHandler {

    /** Tag for a log message */
    private static final String TAG = MainActivity.class.getSimpleName();

    /** MoviePagedListAdapter enables for data to be loaded in chunks */
    private MoviePagedListAdapter mMoviePagedListAdapter;

    /** Exposes a list of favorite movies from a list of MovieEntry to a RecyclerView */
    private FavoriteAdapter mFavoriteAdapter;

    /** String for the sort criteria("most popular and highest rated") */
    private String mSortCriteria;

    /** Member variable for restoring list items positions on device rotation */
    private Parcelable mSavedLayoutState;

    /** ViewModel for MainActivity */
    private MainActivityViewModel mMainViewModel;

    /** This field is used for data binding */
    private ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Set the LayoutManager to the RecyclerView and create MoviePagedListAdapter and FavoriteAdapter
        initAdapter();

        // Check if savedInstance is null not to recreate a dialog when rotating
        if (savedInstanceState == null) {
            // Show a dialog when there is no internet connection
            showNetworkDialog(isOnline());
        }

        // Get the sort criteria currently set in Preferences
        mSortCriteria = MoviePreferences.getPreferredSortCriteria(this);

        // Get the ViewModel from the factory
        setupViewModel(mSortCriteria);
        // Update the UI depending on the sort order
        updateUI(mSortCriteria);

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

        // Initialize the Mobile Ads SDK and enable test ads
        setupTestAds();
    }

    /**
     * Set the LayoutManager to the RecyclerView and create MoviePagedListAdapter and FavoriteAdapter
     */
    private void initAdapter() {
        // A GridLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a grid layout.
        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);
        // Set the layout manager to the RecyclerView
        mMainBinding.rvMovie.setLayoutManager(layoutManager);

        // Use this setting to improve performance if you know that changes in content do not
        // change the child layout size in the RecyclerView
        mMainBinding.rvMovie.setHasFixedSize(true);

        // Create MoviePagedListAdapter
        mMoviePagedListAdapter = new MoviePagedListAdapter(this);
        // Create FavoriteAdapter that is responsible for linking favorite movies with the Views
        mFavoriteAdapter = new FavoriteAdapter(this, this);
    }

    /**
     * Get the MainActivityViewModel from the factory
     */
    private void setupViewModel(String sortCriteria) {
        MainViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(
                MainActivity.this, sortCriteria);
        mMainViewModel = new ViewModelProvider(this, factory).get(MainActivityViewModel.class);
    }

    /**
     * Update the UI depending on the sort criteria
     */
    private void updateUI(String sortCriteria) {
        // Set a new value for the list of MovieEntries
        mMainViewModel.setFavoriteMovies();

        // If the sortCriteria is equal to "favorites", set the FavoriteAdapter to the RecyclerView
        // and observe the list of MovieEntry and update UI to display favorite movies
        if (sortCriteria.equals(getString(R.string.pref_sort_by_favorites))) {
            mMainBinding.rvMovie.setAdapter(mFavoriteAdapter);
            observeFavoriteMovies();
        } else {
            // Otherwise, set the MoviePagedListAdapter to the RecyclerView and observe the MoviePagedList
            // and update the UI to display movies
            mMainBinding.rvMovie.setAdapter(mMoviePagedListAdapter);
            observeMoviePagedList();
        }
    }

    /**
     * Update the MoviePagedList from LiveData in MainActivityViewModel
     */
    private void observeMoviePagedList() {
        mMainViewModel.getMoviePagedList().observe(this, new Observer<PagedList<Movie>>() {
            @Override
            public void onChanged(@Nullable PagedList<Movie> pagedList) {
                showMovieDataView();
                if (pagedList != null) {
                    mMoviePagedListAdapter.submitList(pagedList);

                    // Restore the scroll position after setting up the adapter with the list of movies
                    mMainBinding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
                }

                // When offline, make the movie data view visible and show a snackbar message
                if (!isOnline()) {
                    showMovieDataView();
                    showSnackbarOffline();
                }
            }
        });
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

                if (movieEntries == null || movieEntries.size() == 0) {
                    // When there are no favorite movies, display an empty view
                    showEmptyView();
                } else if(!isOnline()) {
                    // When offline, make the movie data view visible
                    showMovieDataView();
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
        // Set a new value for the PagedList of movies to clear old list and reload. Needs to call it
        // when the SharedPreferences is changed because at that time it's okay to overwrite everything.
        mMainViewModel.setMoviePagedList(mSortCriteria);
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
        // Get the movie data from the MovieEntry
        int movieId = movieEntry.getMovieId();
        String originalTitle = movieEntry.getOriginalTitle();
        String title = movieEntry.getTitle();
        String posterPath = movieEntry.getPosterPath();
        String overview = movieEntry.getOverview();
        double voteAverage = movieEntry.getVoteAverage();
        String releaseDate = movieEntry.getReleaseDate();
        String backdropPath = movieEntry.getBackdropPath();

        // Create a movie object based on the MovieEntry data
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
                // Make the movie data visible and hide an empty message
                showMovieDataView();

                // When refreshing, observe the data and update the UI
                updateUI(mSortCriteria);

                // Hide refresh progress
                hideRefresh();

                // When online, show a snack bar message notifying updated
                showSnackbarRefresh(isOnline());
            }
        });
    }

    /**
     * When online, show a snack bar message notifying updated
     *
     * @param isOnline True if connected to the network
     */
    private void showSnackbarRefresh(boolean isOnline) {
        if (isOnline) {
            // Show snack bar message
            Snackbar.make(mMainBinding.rvMovie, getString(R.string.snackbar_updated)
                    , Snackbar.LENGTH_SHORT).show();
        }
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
        return networkInfo != null && networkInfo.isConnected();
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
     * This method will make the View for the movie data visible
     */
    private void showMovieDataView() {
        // First, hide an empty view
        mMainBinding.tvEmpty.setVisibility(View.INVISIBLE);
        // Then, make sure the movie data is visible
        mMainBinding.rvMovie.setVisibility(View.VISIBLE);
    }

    /**
     * When there are no favorite movies, display an empty view
     */
    private void showEmptyView() {
        mMainBinding.tvEmpty.setVisibility(View.VISIBLE);
        mMainBinding.tvEmpty.setText(getString(R.string.message_empty_favorites));
        mMainBinding.tvEmpty.setCompoundDrawablesWithIntrinsicBounds(DRAWABLES_ZERO,
                R.drawable.film, DRAWABLES_ZERO, DRAWABLES_ZERO);
        mMainBinding.tvEmpty.setTextColor(Color.WHITE);
    }

    /**
     * When offline, show a snackbar message
     */
    private void showSnackbarOffline() {
        Snackbar snackbar = Snackbar.make(
                mMainBinding.frameMain, R.string.snackbar_offline, Snackbar.LENGTH_LONG);
        // Set background color of the snackbar
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.WHITE);
        // Set background color of the snackbar
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
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

    /**
     * Hide refresh progress
     */
    private void hideRefresh() {
        mMainBinding.swipeRefresh.setRefreshing(false);
    }

    /**
     * Initializes the Mobile Ads SDK and enables test ads.
     */
    private void setupTestAds() {
        MobileAds.initialize(this, getString(R.string.ad_app_id));

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mMainBinding.adView.loadAd(adRequest);
    }
}


