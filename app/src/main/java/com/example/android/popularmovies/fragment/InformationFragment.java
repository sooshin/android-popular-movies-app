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

package com.example.android.popularmovies.fragment;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Cast;
import com.example.android.popularmovies.model.Credits;
import com.example.android.popularmovies.model.Crew;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieDetails;
import com.example.android.popularmovies.utilities.FormatUtils;
import com.example.android.popularmovies.utilities.InjectorUtils;
import com.example.android.popularmovies.viewmodel.InfoViewModel;
import com.example.android.popularmovies.viewmodel.InfoViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;

/**
 * The InformationFragment displays information for the selected movie.
 */
public class InformationFragment extends Fragment {

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

    /** Get a reference to the Vote Count TextView */
    @BindView(R.id.tv_vote_count) TextView mVoteCountTextView;
    /** Get a reference to the Original Title TextView */
    @BindView(R.id.tv_original_title) TextView mOriginalTitleTextView;
    /** Get a reference to the Revenue TextView */
    @BindView(R.id.tv_revenue) TextView mRevenueTextView;
    /** Get a reference to the Budget TextView */
    @BindView(R.id.tv_budget) TextView mBudgetTextView;
    /** Get a reference to the Status TextView */
    @BindView(R.id.tv_status) TextView mStatusTextView;
    /** Get a reference to the TextView for displaying the Cast */
    @BindView(R.id.tv_cast) TextView mCastTextView;
    /** Get a reference to the TextView for displaying the director */
    @BindView(R.id.tv_director) TextView mDirectorTextView;

    private Unbinder mUnbinder;

    /** Member variable for the Movie object */
    private Movie mMovie;

    /** ViewModel for InformationFragment */
    private InfoViewModel mInfoViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     */
    public InformationFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get movie data from the MainActivity
        mMovie = getMovieData();

        // Observe the data and update the UI
        setupViewModel(this.getActivity(), mMovie.getId());

        // display the overview, vote average, release date of the movie.
        loadDetails();
    }

    /**
     * Every time the user data is updated, the onChanged callback will be invoked and update the UI
     */
    private void setupViewModel(Context context, int movieId) {
        InfoViewModelFactory factory = InjectorUtils.provideInfoViewModelFactory(context, movieId);
        mInfoViewModel = ViewModelProviders.of(this, factory).get(InfoViewModel.class);

        // Retrieve live data object using the getMovieDetails() method from the ViewModel
        mInfoViewModel.getMovieDetails().observe(this, new Observer<MovieDetails>() {
            @Override
            public void onChanged(@Nullable MovieDetails movieDetails) {
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

                    // Get the Activity the InformationFragment is currently associated with
                    Activity activity = getActivity();
                    // Check if the Activity is not null to avoid IllegalStateException: InformationFragment
                    // not attached to a context.
                    // @see "https://stackoverflow.com/questions/28672883/java-lang-illegalstateexception-
                    // fragment-not-attached-to-activity"
                    if (activity != null) {
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
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        // Bind the view using ButterKnife
        mUnbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    /**
     * Gets movie data from the MainActivity.
     */
    private Movie getMovieData() {
        // Store the Intent
        Intent intent = getActivity().getIntent();
        // Check if the Intent is not null, and has the extra we passed from MainActivity
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                // Receive the Movie object which contains information, such as ID, original title,
                // poster path, overview, vote average, release date, backdrop path.
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }
        return mMovie;
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
        // Display the original title of the movie
        mOriginalTitleTextView.setText(mMovie.getOriginalTitle());
        // Display the release date of the movie
        mReleaseDateTextView.setText(FormatUtils.formatDate(mMovie.getReleaseDate()));
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
     * Butter Knife returns an Unbinder instance when calling bind.
     * In InformationFragment, it seems the call to onDestroyView() is really quick, so
     * override onDestroy() method to delay it.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
