package com.example.android.popularmovies.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.MainActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Cast;
import com.example.android.popularmovies.model.Credits;
import com.example.android.popularmovies.model.Crew;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieDetails;
import com.example.android.popularmovies.utilities.Controller;
import com.example.android.popularmovies.utilities.FormatUtils;
import com.example.android.popularmovies.utilities.TheMovieApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.android.popularmovies.DetailActivity.EXTRA_MOVIE;

/**
 * The InformationFragment displays information for the selected movie.
 */
public class InformationFragment extends Fragment implements Callback<MovieDetails> {

    /** Define a new interface OnInfoSelectedListener that triggers a Callback in the host activity.
     *  The callback is a method named onInformationSelected(MovieDetails movieDetails) that contains
     *  information about the MovieDetails */
    OnInfoSelectedListener mCallback;

    public interface OnInfoSelectedListener {
        void onInformationSelected(MovieDetails movieDetails);
    }

    /** Tag for logging */
    public static final String TAG = InformationFragment.class.getSimpleName();

    /** Automatically finds each field by the specified ID.
     *  Get a reference to the Overview TextView */
    @BindView(R.id.tv_overview) TextView mOverviewTextView;
    /** Get a reference to the Vote Average TextView */
    @BindView(R.id.tv_vote_average) TextView mVoteAverageTextView;
    /** Get a reference to the Release Date TextView*/
    @BindView(R.id.tv_release_date) TextView mReleaseDateTextView;

    /** Get a reference to the Vote Count TextView*/
    @BindView(R.id.tv_vote_count) TextView mVoteCountTextView;
    /** Get a reference to the Revenue TextView */
    @BindView(R.id.tv_revenue) TextView mRevenueTextView;
    /** Get a reference to the Budget TextView */
    @BindView(R.id.tv_budget) TextView mBudgetTextView;
    /** Get a reference to the Status TextView*/
    @BindView(R.id.tv_status) TextView mStatusTextView;
    /** Get a reference to the TextView for displaying the Cast */
    @BindView(R.id.tv_cast) TextView mCastTextView;
    /** Get a reference to the TextView for displaying the director */
    @BindView(R.id.tv_director) TextView mDirectorTextView;

    private Unbinder mUnbinder;

    /** Member variable for the Movie object */
    private Movie mMovie;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     */
    public InformationFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        // Bind the view using ButterKnife
        mUnbinder = ButterKnife.bind(this, rootView);

        // Store the Intent
        Intent intent = getActivity().getIntent();
        // Check if the Intent is not null, and has the extra we passed from MainActivity
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                // Receive the Movie object which contains information, such as ID, original title,
                // poster path, overview, vote average, release date, backdrop path.
                mMovie = intent.getParcelableExtra(EXTRA_MOVIE);
            }
        }
        // Makes a network request
        callMovieDetails();
        // display the overview, vote average, release date of the movie.
        loadDetails();
        return rootView;
    }

    /**
     * Makes a network request by calling enqueue
     */
    private void callMovieDetails() {
        // The Retrofit class generates an implementation of the TheMovieApi interface.
        Retrofit retrofit = Controller.getClient();
        TheMovieApi theMovieApi = retrofit.create(TheMovieApi.class);

        // Each call from the created TheMovieApi can make a synchronous or asynchronous HTTP request
        // to the remote web server. Send Request:
        // https://api.themoviedb.org/3/movie/{movie_id}?api_key={API_KEY}&language=en-US&append_to_response=credits
        Call<MovieDetails> callDetails = theMovieApi.getDetails(
                mMovie.getId(), MainActivity.API_KEY, MainActivity.LANGUAGE, MainActivity.CREDITS);

        // Calls are executed with asynchronously with enqueue and notify callback of its response
        callDetails.enqueue(this);
    }

    /**
     * Get the detail information from the Movie object, then set them to the TextView to display the
     * overview, vote average, release date of the movie.
     */
    private void loadDetails() {
        // Display the overview of the movie
        mOverviewTextView.setText(mMovie.getOverview());
        // Display the vote average of the movie
        mVoteAverageTextView.setText(String.valueOf(mMovie.getVoteAverage()));
        // Display the release date of the movie
        mReleaseDateTextView.setText(FormatUtils.formatDate(mMovie.getReleaseDate()));
    }

    /**
     * Invoked for a received HTTP response.
     */
    @Override
    public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
        if (response.isSuccessful()) {
            MovieDetails movieDetails = response.body();
            if (movieDetails != null) {
                // Trigger the callback onInformationSelected
                mCallback.onInformationSelected(movieDetails);

                // Get the budget, revenue, vote count, status
                long budget = movieDetails.getBudget();
                long revenue = movieDetails.getRevenue();
                int voteCount = movieDetails.getVoteCount();
                String status = movieDetails.getStatus();

                // Get the cast for a movie
                Credits credits = movieDetails.getCredits();
                List<Cast> castList = credits.getCast();
                // Create an empty ArrayList
                List<String> castStrList = new ArrayList<>();
                // Go through all the casts, and add the cast name to the list of strings
                for (int i = 0; i < castList.size(); i++) {
                    Cast cast = castList.get(i);
                    // Get the cast name
                    String castName = cast.getName();
                    // Add the cast name to the list of strings
                    castStrList.add(castName);
                }
                // Join a string using a delimiter
                String castStr = TextUtils.join(getString(R.string.delimiter_comma), castStrList);
                // Display the list of cast name
                mCastTextView.setText(castStr);

                // Display director of the movie
                List<Crew> crewList = credits.getCrew();
                for (int i = 0; i < crewList.size(); i++) {
                    Crew crew = crewList.get(i);
                    // if job is "director", set the director's name to the TextView
                    if (crew.getJob().equals(getString(R.string.director))) {
                        mDirectorTextView.setText(crew.getName());
                        break;
                    }
                }

                // Display vote count, budget, revenue, status of the movie. Use FormatUtils class
                // to format the integer number
                mVoteCountTextView.setText(FormatUtils.formatNumber(voteCount));
                mBudgetTextView.setText(FormatUtils.formatCurrency(budget));
                mRevenueTextView.setText(FormatUtils.formatCurrency(revenue));
                mStatusTextView.setText(status);
            }
        }
    }

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected exception
     * occurred creating the request or processing the response.
     */
    @Override
    public void onFailure(Call<MovieDetails> call, Throwable t) {
        t.printStackTrace();
        Log.e(TAG, "failure: " + t.getMessage());
    }

    /**
     * Override onAttach to make sure that the container activity has implemented the callback
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnInfoSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnInfoSelectedListener");
        }
    }

    /**
     * When binding a fragment in onCreateView, set the views to null in onDestroyView.
     * Butter Knife returns an Unbinder instance when calling bind
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
