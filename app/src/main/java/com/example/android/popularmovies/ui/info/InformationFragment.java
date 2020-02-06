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

package com.example.android.popularmovies.ui.info;

import android.app.Activity;
import androidx.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.FragmentInfoBinding;
import com.example.android.popularmovies.model.Cast;
import com.example.android.popularmovies.model.Credits;
import com.example.android.popularmovies.model.Crew;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieDetails;
import com.example.android.popularmovies.utilities.FormatUtils;
import com.example.android.popularmovies.utilities.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;

/**
 * The InformationFragment displays information for the selected movie.
 */
public class InformationFragment extends Fragment {

    /** This field is used for data binding */
    private FragmentInfoBinding mInfoBinding;

    /** Define a new interface OnInfoSelectedListener that triggers a Callback in the host activity.
     *  The callback is a method named onInformationSelected(MovieDetails movieDetails) that contains
     *  information about the MovieDetails */
    OnInfoSelectedListener mCallback;

    public interface OnInfoSelectedListener {
        void onInformationSelected(MovieDetails movieDetails);
    }

    /**
     * Define a new interface OnViewAllSelectedListener that triggers a Callback in the host activity.
     * The callback is a method named onViewAllSelected() that is triggered when the user clicks
     * "VIEW ALL" TextView
     */
    OnViewAllSelectedListener mViewAllCallback;

    /** OnViewAllSelectedListener interface, calls a method in the host activity named onViewAllSelected */
    public interface OnViewAllSelectedListener {
        void onViewAllSelected();
    }

    /** Tag for logging */
    public static final String TAG = InformationFragment.class.getSimpleName();

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
        mInfoViewModel = new ViewModelProvider(this, factory).get(InfoViewModel.class);

        // Retrieve live data object using the getMovieDetails() method from the ViewModel
        mInfoViewModel.getMovieDetails().observe(getViewLifecycleOwner(), new Observer<MovieDetails>() {
            @Override
            public void onChanged(@Nullable MovieDetails movieDetails) {
                if (movieDetails != null) {
                    // Trigger the callback onInformationSelected
                    mCallback.onInformationSelected(movieDetails);

                    // Display vote count, budget, revenue, status of the movie
                    loadMovieDetailInfo(movieDetails);

                    // Display cast and crew of the movie
                    loadCastCrew(movieDetails);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Instantiate mInfoBinding using DataBindingUtil
        mInfoBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_info, container, false);
        View rootView = mInfoBinding.getRoot();

        // Switch to CastFragment in a ViewPager when "VIEW ALL" TextView is clicked
        mInfoBinding.tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger the callback onViewAllSelected
                mViewAllCallback.onViewAllSelected();
            }
        });

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
     * Display cast and crew of the movie
     */
    private void loadCastCrew(MovieDetails movieDetails) {
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
            mInfoBinding.tvCast.setText(castStr);

            // Display director of the movie
            List<Crew> crewList = credits.getCrew();
            for (int i = 0; i < crewList.size(); i++) {
                Crew crew = crewList.get(i);
                // if job is "director", set the director's name to the TextView
                if (crew.getJob().equals(getString(R.string.director))) {
                    mInfoBinding.tvDirector.setText(crew.getName());
                    break;
                }
            }
        }
    }

    /**
     * Display vote count, budget, revenue, status of the movie
     */
    private void loadMovieDetailInfo(MovieDetails movieDetails) {
        // Get the  vote count, budget, revenue, status
        int voteCount = movieDetails.getVoteCount();
        long budget = movieDetails.getBudget();
        long revenue = movieDetails.getRevenue();
        String status = movieDetails.getStatus();

        // Display vote count, budget, revenue, status of the movie. Use FormatUtils class
        // to format the integer number
        mInfoBinding.tvVoteCount.setText(FormatUtils.formatNumber(voteCount));
        mInfoBinding.tvBudget.setText(FormatUtils.formatCurrency(budget));
        mInfoBinding.tvRevenue.setText(FormatUtils.formatCurrency(revenue));
        mInfoBinding.tvStatus.setText(status);
    }

    /**
     * Get the detail information from the Movie object, then set them to the TextView to display the
     * overview, vote average, release date of the movie.
     */
    private void loadDetails() {
        // Display the overview of the movie
        mInfoBinding.tvOverview.setText(mMovie.getOverview());
        // Display the vote average of the movie
        mInfoBinding.tvVoteAverage.setText(String.valueOf(mMovie.getVoteAverage()));
        // Display the original title of the movie
        mInfoBinding.tvOriginalTitle.setText(mMovie.getOriginalTitle());
        // Display the release date of the movie
        mInfoBinding.tvReleaseDate.setText(FormatUtils.formatDate(mMovie.getReleaseDate()));
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

        try {
            mViewAllCallback = (OnViewAllSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnViewAllSelectedListener");
        }
    }
}
