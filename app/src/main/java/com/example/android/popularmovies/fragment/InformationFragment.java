package com.example.android.popularmovies.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.MainActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Cast;
import com.example.android.popularmovies.model.Credits;
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

public class InformationFragment extends Fragment implements Callback<MovieDetails> {

    /** Define a new interface OnInfoSelectedListener that triggers a Callback in the host activity
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

    private Unbinder mUnbinder;

    /** Member variable for the Movie object */
    private Movie mMovie;

    public InformationFragment() {
        // Required empty public constructor
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
                // Receive the Movie object
                mMovie = intent.getParcelableExtra(EXTRA_MOVIE);
            }
        }
        callMovieDetails();
        loadDetails();
        return rootView;
    }

    /**
     * Makes a network request by calling enqueue
     */
    private void callMovieDetails() {
        Retrofit retrofit = Controller.getClient();
        TheMovieApi theMovieApi = retrofit.create(TheMovieApi.class);

        Call<MovieDetails> callDetails = theMovieApi.getDetails(
                mMovie.getId(), MainActivity.API_KEY, MainActivity.LANGUAGE, MainActivity.CREDITS);
        // Calls are executed with asynchronously with enqueue and notify callback of its response
        callDetails.enqueue(this);
    }

    private void loadDetails() {
        mOverviewTextView.setText(mMovie.getOverview());
        mVoteAverageTextView.setText(String.valueOf(mMovie.getVoteAverage()));
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
                int budget = movieDetails.getBudget();
                int runtime = movieDetails.getRuntime();
                int revenue = movieDetails.getRevenue();
                int voteCount = movieDetails.getVoteCount();
                String status = movieDetails.getStatus();

                // Get the cast and crew for a movie
                Credits credits = movieDetails.getCredits();
                List<Cast> castList = credits.getCast();
                List<String> castStrList = new ArrayList<>();
                for (int i = 0; i < castList.size(); i++) {
                    Cast cast = castList.get(i);
                    String castName = cast.getName();
                    castStrList.add(castName);
                }
                String castStr = TextUtils.join(getString(R.string.delimiter_comma), castStrList);
                mCastTextView.setText(castStr);

                mVoteCountTextView.setText(FormatUtils.formatNumber(voteCount));
                mBudgetTextView.setText(FormatUtils.formatCurrency(budget));
                mRevenueTextView.setText(FormatUtils.formatCurrency(revenue));
                mStatusTextView.setText(status);

                // Trigger the callback onInformationSelected
                mCallback.onInformationSelected(movieDetails);
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
