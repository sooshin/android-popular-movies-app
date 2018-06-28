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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.adapter.CastAdapter;
import com.example.android.popularmovies.activity.DetailActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Cast;
import com.example.android.popularmovies.model.Credits;
import com.example.android.popularmovies.model.MovieDetails;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * The CastFragment displays all of the cast members for the selected movie.
 */
public class CastFragment extends Fragment {

    /** Tag for a log message */
    public static final String TAG = CastFragment.class.getSimpleName();

    /** Member variable for the list of casts */
    private List<Cast> mCastList;

    /** Member variable for CastAdapter */
    private CastAdapter mCastAdapter;

    private Unbinder mUnbinder;

    /** Get a reference to RecyclerView */
    @BindView(R.id.rv_cast) RecyclerView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     */
    public CastFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cast, container, false);

        // Bind the view using ButterKnife
        mUnbinder = ButterKnife.bind(this, rootView);

        // A LinearLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a linear list.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Create an empty ArrayList
        mCastList = new ArrayList<>();

        // The CastAdapter is responsible for displaying each item in the list.
        mCastAdapter = new CastAdapter(mCastList);
        // Set Adapter on RecyclerView
        mRecyclerView.setAdapter(mCastAdapter);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get MovieDetails data from the DetailActivity
        Bundle args = getArguments();
        if (args != null) {
            MovieDetails movieDetails = args.getParcelable(DetailActivity.EXTRA_MOVIE_DETAILS);
            if (movieDetails != null) {
                // Get Credits from MovieDetails
                Credits credits = movieDetails.getCredits();
                // Get the list of casts
                mCastList = credits.getCast();
                // Set the list of casts
                credits.setCast(mCastList);
            }
        }
        // add a list of casts to CastAdapter
        mCastAdapter.addAll(mCastList);
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
