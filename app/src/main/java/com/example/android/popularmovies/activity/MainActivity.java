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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.GridSpacingItemDecoration;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.adapter.MovieAdapter.MovieAdapterOnClickHandler;
import com.example.android.popularmovies.data.MoviePreferences;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieResponse;
import com.example.android.popularmovies.utilities.Controller;
import com.example.android.popularmovies.utilities.TheMovieApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * The MainActivity displays the list of movies that appear as a grid of images
 */
public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler,
        Callback<MovieResponse>, SharedPreferences.OnSharedPreferenceChangeListener {

    /** Tag for a log message */
    private static final String TAG = MainActivity.class.getSimpleName();

    /** API Status code for invalid API key or Authentication failed */
    private static final int RESPONSE_CODE_API_STATUS = 401;

    /** A numeric constant for request code */
    private static final int REQUEST_CODE_DIALOG = 0;

    /** Constants that are used to request the network call */
    public static final String API_KEY = BuildConfig.API_KEY;
    public static final String LANGUAGE = "en-US";
    public static final int PAGE = 1;
    public static final String CREDITS = "credits";

    /** Constant for the span count in the grid layout manager */
    private static final int GRID_SPAN_COUNT = 3;
    /** Constant for the grid spacing (px)*/
    private static final int GRID_SPACING = 8;
    /** True when including edge */
    private static final boolean GRID_INCLUDE_EDGE = true;

    /** Reference to RecyclerView */
    @BindView(R.id.rv_movie) RecyclerView mRecyclerView;

    /** Reference to Offline TextView*/
    @BindView(R.id.tv_offline) TextView mOfflineTextView;

    /** Reference to the error message TextView */
    @BindView(R.id.tv_error) TextView mErrorTextView;

    /** ProgressBar that will indicate to the user that we are loading the data */
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;

    /** SwipeRefreshLayout that is used whenever the user can refresh the contents of a view*/
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout mSwipeRefreshLayout;

    /** Member variable for TheMovieApi interface */
    private TheMovieApi mMovieApi;

    /** MovieResponse callback that communicates responses from a server */
    Callback<MovieResponse> mMovieResponseCallback;

    /** Reference to MovieAdapter*/
    private MovieAdapter mMovieAdapter;

    /** String for the sort criteria("most popular and highest rated") */
    private String mSortCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the view using ButterKnife
        ButterKnife.bind(this);

        // A GridLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a grid layout.
        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);
        // Set the layout manager to the RecyclerView
        mRecyclerView.setLayoutManager(layoutManager);

        // Use this setting to improve performance if you know that changes in content do not
        // change the child layout size in the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Create an empty array list
        List<Movie> movies = new ArrayList<>();
        // Create MovieAdapter that is responsible for linking our movie data with the Views
        mMovieAdapter = new MovieAdapter(movies, this);
        // Set the MovieAdapter to the RecyclerView
        mRecyclerView.setAdapter(mMovieAdapter);

        // Show a dialog when there is no internet connection
        showNetworkDialog(isOnline());

        // The Retrofit class generates an implementation of the TheMovieApi interface.
        Retrofit retrofit = Controller.getClient();
        mMovieApi = retrofit.create(TheMovieApi.class);
        // Make a network request by calling enqueue
        callMovieResponse();

        // Register MainActivity as an OnPreferenceChangedListener to receive a callback when a
        // SharedPreference has changed. Please note that we must unregister MainActivity as an
        // OnSharedPreferenceChanged listener in onDestroy to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        // The MainActivity has implemented the callback interface
        mMovieResponseCallback = this;
        // Set the color scheme of the SwipeRefreshLayout and setup OnRefreshListener
        setSwipeRefreshLayout();

        // Set column spacing to make each column have the same spacing
        setColumnSpacing();
    }

    /**
     * Makes a network request by calling enqueue
     */
    private void callMovieResponse() {
        // Get the sort criteria currently set in Preferences
        mSortCriteria = MoviePreferences.getPreferredSortCriteria(this);

        // Each call from the created TheMovieApi can make a synchronous or asynchronous HTTP request
        // to the remote web server. Send Request:
        // https://api.themoviedb.org/3/movie/{sort_criteria}?api_key={API_KEY}&language=en-US&page=1
        Call<MovieResponse> call = mMovieApi.getMovies(mSortCriteria, API_KEY, LANGUAGE, PAGE);

        // Show the loading indicator before calls are executed
        mLoadingIndicator.setVisibility(View.VISIBLE);

        // Calls are executed with asynchronously with enqueue and notify callback of its response
        call.enqueue(this);
    }

    /**
     * Invoked for a received HTTP response.
     */
    @Override
    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
        // Hide the loading indicator
        mLoadingIndicator.setVisibility(View.GONE);

        // Hide refresh progress
        mSwipeRefreshLayout.setRefreshing(false);

        if (response.isSuccessful()) {
            // Make movie data visible and hide error message
            showMovieDataView();

            MovieResponse movieResponse = response.body();
            if (movieResponse != null) {
                // Get the list of movies
                List<Movie> movies = movieResponse.getMovieResults();
                //  Add a list of Movies
                mMovieAdapter.addAll(movies);
            }
        } else if (response.code() == RESPONSE_CODE_API_STATUS) {
            // Display error message when API status code is equal to 401
            Log.e(TAG, "Invalid Api key. Response code: " + response.code());
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText(getString(R.string.error_message_api_key));
        } else {
            Log.e(TAG, "Response Code: " + response.code());
        }
    }

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected exception
     * occurred creating the request or processing the response.
     */
    @Override
    public void onFailure(Call<MovieResponse> call, Throwable t) {
        // Hide the loading indicator
        mLoadingIndicator.setVisibility(View.GONE);

        // Hide refresh progress
        mSwipeRefreshLayout.setRefreshing(false);

        if (!isOnline()) {
            // When there is no internet connection, display offline message
            showOfflineMessage();
            Log.e(TAG, "onFailure, offline: " + t.getMessage());
        } else {
            // When an error occurred, display error message
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText(getString(R.string.error_message_failed));
            Log.e(TAG, "onFailure: " + t.getMessage());
        }
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

        // When SharedPreference changes, make a network request again
        Call<MovieResponse> call = mMovieApi.getMovies(mSortCriteria, API_KEY, LANGUAGE, PAGE);
        // Calls are executed with asynchronously with enqueue and notify callback of its response
        call.enqueue(this);
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
        b.putParcelable(DetailActivity.EXTRA_MOVIE, movie);

        // Create the Intent the will start the DetailActivity
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        // Pass the bundle through Intent
        intent.putExtra(DetailActivity.EXTRA_MOVIE, b);
        // Once the Intent has been created, start the DetailActivity
        startActivity(intent);
    }

    /**
     *  Set the SwipeRefreshLayout triggered by a swipe gesture.
     */
    private void setSwipeRefreshLayout() {
        // Set the colors used in the progress animation
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        // Set the listener to be notified when a refresh is triggered
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            /**
             * Called when a swipe gesture triggers a refresh
             */
            @Override
            public void onRefresh() {
                // Make movie data visible and hide error message
                showMovieDataView();

                // When refreshing, make a network request again
                Call<MovieResponse> call = mMovieApi.getMovies(mSortCriteria, API_KEY, LANGUAGE, PAGE);
                call.enqueue(mMovieResponseCallback);

                // Show snack bar message
                Snackbar.make(mRecyclerView, getString(R.string.snackbar_updated), Snackbar.LENGTH_SHORT).show();
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
        mOfflineTextView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        // Then, make sure the movie data is visible
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the offline message visible and hide the movie View
     */
    private void showOfflineMessage() {
        // First, hide the currently visible data
        mRecyclerView.setVisibility(View.INVISIBLE);
        // Then, show the offline message
        mOfflineTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Set column spacing to make each column have the same spacing.
     */
    private void setColumnSpacing() {
        GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(
                GRID_SPAN_COUNT, GRID_SPACING, GRID_INCLUDE_EDGE);
        mRecyclerView.addItemDecoration(decoration);
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
}
