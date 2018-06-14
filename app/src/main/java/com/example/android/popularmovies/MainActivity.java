package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmovies.MovieAdapter.MovieAdapterOnClickHandler;
import com.example.android.popularmovies.data.MoviePreferences;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler {

    /** Tag for a log message */
    private static final String TAG = MainActivity.class.getSimpleName();

    /** Reference to RecyclerView */
    @BindView(R.id.rv_movie) RecyclerView mRecyclerView;

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
        // Once all of our views are setup, load the movie data
        loadMovieData();
    }

    /**
     * This method will get the user's preferred sort criteria for movie, and then tell some
     * background method to get the movie data in the background.
     */
    private void loadMovieData() {
        String sort = MoviePreferences.getPreferredSortCriteria(this);
        new FetchMovieTask().execute(sort);
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


    public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {
        /**
         * Perform the network requests
         */
        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String url = params[0];
            URL movieRequestUrl = NetworkUtils.buildMovieUrl(url);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                List<Movie> jsonMovieData = JsonUtils.parseMovieJson(jsonMovieResponse);

                return jsonMovieData;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(TAG, "Problem retrieving the Movie Database JSON results.");
            }
            return null;
        }

        /**
         * Display the result fo the network request
         */
        @Override
        protected void onPostExecute(List<Movie> movies) {
            // Clear the adapter of the previous movie data
            mMovieAdapter.clearAll();

            // Add the movie data
            if (movies != null && !movies.isEmpty()) {
                mMovieAdapter.addAll(movies);
            }
        }
    }
}
