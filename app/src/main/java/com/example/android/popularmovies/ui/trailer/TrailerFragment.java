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

package com.example.android.popularmovies.ui.trailer;

import androidx.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.FragmentTrailerBinding;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Video;
import com.example.android.popularmovies.model.VideoResponse;
import com.example.android.popularmovies.utilities.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;

public class TrailerFragment extends Fragment implements TrailerAdapter.TrailerAdapterOnClickHandler {

    /** Define a new interface OnTrailerSelectedListener that triggers a Callback in the host activity.
     *  The callback is a method named onTrailerSelected(Video video) that contains the first trailer
     */
    TrailerFragment.OnTrailerSelectedListener mCallback;

    public interface OnTrailerSelectedListener {
        void onTrailerSelected(Video video);
    }

    /** Tag for a log message */
    private static final String TAG = TrailerFragment.class.getSimpleName();

    /** Member variable for the list of trailers */
    private List<Video> mVideos;

    /** Member variable for TrailerAdapter */
    private TrailerAdapter mTrailerAdapter;

    private Movie mMovie;

    /** This field is used for data binding */
    private FragmentTrailerBinding mTrailerBinding;

    /** ViewModel for TrailerFragment */
    private TrailerViewModel mTrailerViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     */
    public TrailerFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get movie data from the MainActivity
        mMovie = getMovieData();

        // Observe the data and update the UI
        setupViewModel(this.getActivity(), mMovie.getId());
    }

    /**
     * Every time the user data is updated, the onChanged callback will be invoked and update the UI
     */
    private void setupViewModel(Context context, int movieId) {
        TrailerViewModelFactory factory = InjectorUtils.provideTrailerViewModelFactory(context, movieId);
        mTrailerViewModel = new ViewModelProvider(this, factory).get(TrailerViewModel.class);

        // Retrieve live data object using the getVideoResponse() method from the ViewModel
        mTrailerViewModel.getVideoResponse().observe(getViewLifecycleOwner(), new Observer<VideoResponse>() {
            @Override
            public void onChanged(@Nullable VideoResponse videoResponse) {
                if (videoResponse != null) {
                    mVideos = videoResponse.getVideoResults();
                    videoResponse.setVideoResults(mVideos);

                    // Check if there is a trailer
                    if (!mVideos.isEmpty()) {
                        // Trigger the callback onTrailerSelected
                        // to transfer the first trailer from TrailerFragment to DetailActivity
                        mCallback.onTrailerSelected(mVideos.get(0));

                        mTrailerAdapter.addAll(mVideos);
                    } else {
                        // If there are no trailers, show a message that says no trailers found
                        showNoTrailersMessage();
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTrailerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_trailer, container, false);
        View rootView = mTrailerBinding.getRoot();

        // A LinearLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a linear list.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mTrailerBinding.rvTrailer.setLayoutManager(layoutManager);
        mTrailerBinding.rvTrailer.setHasFixedSize(true);

        // Create an empty ArrayList
        mVideos = new ArrayList<>();

        // The TrailerAdapter is responsible for displaying each item in the list.
        mTrailerAdapter = new TrailerAdapter(mVideos, this);
        // Set TrailerAdapter on RecyclerView
        mTrailerBinding.rvTrailer.setAdapter(mTrailerAdapter);

        // Show a message when offline
        showOfflineMessage(isOnline());

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
     * Override onAttach to make sure that the container activity has implemented the callback
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnTrailerSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnTrailerSelectedListener");
        }
    }

    /**
     * When a movie trailer selected, use an Intent to open a YouTube link
     *
     * @param videoUrl YouTube video url to display a trailer video
     */
    @Override
    public void onItemClick(String videoUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
        /*
         * This is a check we perform with every implicit Intent that we launch. In some cases,
         * the device where this code is running might not have an Activity to perform the action
         * with the data we've specified. Without this check, in those cases your app would crash.
         */
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * This method will make the message that says no trailers found visible and
     * hide the View for the trailers data
     */
    private void showNoTrailersMessage() {
        // First, hide the currently visible data
        mTrailerBinding.rvTrailer.setVisibility(View.INVISIBLE);
        // Then, show a message that says no trailers found
        mTrailerBinding.tvNoTrailers.setVisibility(View.VISIBLE);
    }

    /**
     * Make the offline message visible and hide the trailer View when offline
     *
     * @param isOnline True when connected to the network
     */
    private void showOfflineMessage(boolean isOnline) {
        if (isOnline) {
            // First, hide the offline message
            mTrailerBinding.tvOffline.setVisibility(View.INVISIBLE);
            // Then, make sure the trailer data is visible
            mTrailerBinding.rvTrailer.setVisibility(View.VISIBLE);
        } else {
            // First, hide the currently visible data
            mTrailerBinding.rvTrailer.setVisibility(View.INVISIBLE);
            // Then, show an offline message
            mTrailerBinding.tvOffline.setVisibility(View.VISIBLE);
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
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
