package com.example.android.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.MovieAdapter.MovieAdapterOnClickHandler;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieResponse;
import com.example.android.popularmovies.utilities.Controller;
import com.example.android.popularmovies.utilities.NetworkError;
import com.example.android.popularmovies.utilities.TheMovieApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler, Callback<MovieResponse> {

    /** Tag for a log message */
    private static final String TAG = MainActivity.class.getSimpleName();

    /** Constants  */
    public static final String CATEGORY = "popular";
    public static final String API_KEY = BuildConfig.API_KEY;
    public static final String LANGUAGE = "en-US";
    public static final int PAGE = 1;
    public static final String CREDITS = "credits";

    /** Reference to RecyclerView */
    @BindView(R.id.rv_movie) RecyclerView mRecyclerView;

    /** Reference to Offline TextView*/
    @BindView(R.id.tv_offline) TextView mOfflineTextView;

    @BindView(R.id.tv_error) TextView mErrorTextView;

    /** Reference to MovieAdapter*/
    private MovieAdapter mMovieAdapter;

    /** Constants for the span count in the grid layout manager */
    private static final int GRID_SPAN_COUNT = 3;

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

        callMovieResponse();
    }

    /**
     * Makes a network request by calling enqueue
     */
    private void callMovieResponse() {
        Retrofit retrofit = Controller.getClient();
        TheMovieApi theMovieApi = retrofit.create(TheMovieApi.class);

        Call<MovieResponse> call = theMovieApi.getMovies(CATEGORY, API_KEY, LANGUAGE, PAGE);
        // Calls are executed with asynchronously with enqueue and notify callback of its response
        call.enqueue(this);
    }

    /**
     * Invoked for a received HTTP response.
     */
    @Override
    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
        if (response.isSuccessful()) {
            MovieResponse movieResponse = response.body();
            if (movieResponse != null) {
                List<Movie> movies = movieResponse.getMovieResults();
                mMovieAdapter.addAll(movies);
            }
        } else if (response.code() == 401) {
            Log.e(TAG, "Invalid Api key");
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText("Please make sure enter your api key.");
        } else {
            Log.e(TAG, "response Code: " + response.code());
        }
    }

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected exception
     * occurred creating the request or processing the response.
     */
    @Override
    public void onFailure(Call<MovieResponse> call, Throwable t) {
        NetworkError networkError = new NetworkError(t);
        String errorMessage = networkError.getAppErrorMessage();
        Log.e(TAG, "Network error message: " + errorMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        callMovieResponse();
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item clicks.
     *
     * @param movie The movie that was clicked
     */
    @Override
    public void onItemClick(Movie movie) {
        // Create the Intent the will start the DetailActivity
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        // Pass the selected Movie object through Intent
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);
        // Once the Intent has been created, start the DetailActivity
        startActivity(intent);

        Toast.makeText(this, "toast:"  + movie.getId(), Toast.LENGTH_SHORT).show();
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
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS),0);
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
        // First, make sure the offline message is invisible
        mOfflineTextView.setVisibility(View.INVISIBLE);
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
