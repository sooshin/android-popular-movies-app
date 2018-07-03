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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.DetailPagerAdapter;
import com.example.android.popularmovies.fragment.CastFragment;
import com.example.android.popularmovies.fragment.InformationFragment;
import com.example.android.popularmovies.model.Genre;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieDetails;
import com.example.android.popularmovies.model.Video;
import com.example.android.popularmovies.model.VideoResponse;
import com.example.android.popularmovies.utilities.Controller;
import com.example.android.popularmovies.utilities.FormatUtils;
import com.example.android.popularmovies.utilities.TheMovieApi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.android.popularmovies.utilities.Constant.API_KEY;
import static com.example.android.popularmovies.utilities.Constant.BACKDROP_FILE_SIZE;
import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;
import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE_DETAILS;
import static com.example.android.popularmovies.utilities.Constant.IMAGE_BASE_URL;
import static com.example.android.popularmovies.utilities.Constant.LANGUAGE;
import static com.example.android.popularmovies.utilities.Constant.RELEASE_YEAR_BEGIN_INDEX;
import static com.example.android.popularmovies.utilities.Constant.RELEASE_YEAR_END_INDEX;
import static com.example.android.popularmovies.utilities.Constant.RESULTS_GENRE;
import static com.example.android.popularmovies.utilities.Constant.RESULTS_RELEASE_YEAR;
import static com.example.android.popularmovies.utilities.Constant.RESULTS_RUNTIME;
import static com.example.android.popularmovies.utilities.Constant.SHARE_INTENT_TYPE_TEXT;
import static com.example.android.popularmovies.utilities.Constant.SHARE_URL;
import static com.example.android.popularmovies.utilities.Constant.YOUTUBE_BASE_URL;

/**
 * This activity is responsible for displaying the details for a selected movie.
 */
public class DetailActivity extends AppCompatActivity implements
        InformationFragment.OnInfoSelectedListener, Callback<VideoResponse> {

    /** Tag for logging */
    public static final String TAG = DetailActivity.class.getSimpleName();

    /** ImageView for the backdrop image */
    @BindView(R.id.iv_backdrop)
    ImageView mBackdropImageView;

    /** Get a reference to the ViewPager that will allow the user to swipe between fragments */
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    /** Get a reference to the TabLayout */
    @BindView(R.id.sliding_tabs)
    TabLayout mTabLayout;

    /** Get a reference to the TextView to display the title */
    @BindView(R.id.tv_detail_title)
    TextView mTitleTextView;

    /** AppBarLayout */
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    /** Collapsing Toolbar Layout */
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbar;

    /** Toolbar */
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    /** Get a reference to the TextView to display runtime */
    @BindView(R.id.tv_runtime)
    TextView mRuntimeTextView;
    /** Get a reference to the TextView to display release year */
    @BindView(R.id.tv_release_year)
    TextView mReleaseYearTextView;
    /** Get a reference to the TextView to display genres*/
    @BindView(R.id.tv_genre)
    TextView mGenreTextView;
    /** ProgressBar that will indicate to the user that we are loading the data */
    @BindView(R.id.pb_detail_loading_indicator)
    ProgressBar mDetailLoadingIndicator;

    @BindView(R.id.iv_play_circle)
    ImageView mPlayCircleImageView;

    /** Movie object */
    private Movie mMovie;

    /** Get a reference to the FragmentManager */
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Bind the view using ButterKnife
        ButterKnife.bind(this);

        // Get the movie data from the MainActivity. The movie data includes the movie id, original title,
        // title, poster path, overview, vote average, release date, and backdrop path.
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }

        // Setup the UI
        setupUI();
        callVideoResponse();

        if (savedInstanceState != null) {
            mDetailLoadingIndicator.setVisibility(View.GONE);

            String resultRuntime = savedInstanceState.getString(RESULTS_RUNTIME);
            String resultReleaseYear = savedInstanceState.getString(RESULTS_RELEASE_YEAR);
            String resultGenre = savedInstanceState.getString(RESULTS_GENRE);

            mRuntimeTextView.setText(resultRuntime);
            mReleaseYearTextView.setText(resultReleaseYear);
            mGenreTextView.setText(resultGenre);
        }
    }

    /**
     *  This method is called from onCreate to setup the UI
     */
    private void setupUI() {
        // Show back button in Collapsing Toolbar
        showBackButton();

        // Give the TabLayout the ViewPager
        mTabLayout.setupWithViewPager(mViewPager);
        // Set gravity for the TabLayout
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Create an adapter that knows which fragment should be shown on each page
        DetailPagerAdapter pagerAdapter = new DetailPagerAdapter(
                this, getSupportFragmentManager());
        // Set the adapter onto the ViewPager
        mViewPager.setAdapter(pagerAdapter);

        // Show the title in the app bar when a CollapsingToolbarLayout is fully collapsed
        setCollapsingToolbarTitle();
        // Display the backdrop image
        loadBackdropImage();
        // Display title
        setTitle();
        // Show loading indicator
        mDetailLoadingIndicator.setVisibility(View.VISIBLE);
        // Get the FragmentManager for interacting with fragments associated with DetailActivity
        mFragmentManager = getSupportFragmentManager();
    }

    /**
     * Makes a network request by calling enqueue
     */
    private void callVideoResponse() {
        // The Retrofit class generates an implementation of the TheMovieApi interface.
        Retrofit retrofit = Controller.getClient();
        TheMovieApi theMovieApi = retrofit.create(TheMovieApi.class);

        // Each call from the created TheMovieApi can make a synchronous or asynchronous HTTP request
        // to the remote web server. Send Request:
        // https://api.themoviedb.org/3/movie/{id}/videos?api_key={API_KEY}&language=en-US
        Call<VideoResponse> callVideoResponse = theMovieApi.getVideos(
                mMovie.getId(), API_KEY, LANGUAGE);

        // Calls are executed with asynchronously with enqueue and notify callback of its response
        callVideoResponse.enqueue(this);
    }

    /**
     * Invoked for a received HTTP response.
     */
    @Override
    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
        if (response.isSuccessful()) {
            VideoResponse videoResponse = response.body();
            if (videoResponse != null) {
                List<Video> videos = videoResponse.getVideoResults();
                videoResponse.setVideoResults(videos);

                if (videos.size() != 0) {
                    mPlayCircleImageView.setVisibility(View.VISIBLE);
                    final Video firstVideo = videos.get(0);
                    mPlayCircleImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            launchTrailer(firstVideo);
                        }
                    });
                }
            }
        }
    }

    /**
     * Use Intent to open a YouTube link in either the native app or a web browser of choice
     *
     * @param video The video object that contains YouTube url
     */
    private void launchTrailer(Video video) {
        String firstVideoKey = video.getKey();
        String firstVideoUrl = YOUTUBE_BASE_URL + firstVideoKey;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(firstVideoUrl));
        startActivity(intent);
    }

    @Override
    public void onFailure(Call<VideoResponse> call, Throwable t) {
        Log.e(TAG, "onFailure: " + t.getMessage());
    }

    /**
     * Show back button in Collapsing Toolbar
     */
    private void showBackButton() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Display the backdrop image
     */
    private void loadBackdropImage() {
        // Get the backdrop path
        String backdropPath = mMovie.getBackdropPath();
        // The complete backdrop image url
        String backdrop = IMAGE_BASE_URL + BACKDROP_FILE_SIZE + backdropPath;
        // Load image with Picasso library
        Picasso.with(this)
                .load(backdrop)
                .error(R.drawable.photo)
                .into(mBackdropImageView);
    }

    /**
     * The {@link Movie} object contains information, such as ID, original title, title, poster path,
     * vote average, release date, and backdrop path. Get the title from the {@link Movie} and
     * set the title to the TextViews
     */
    private void setTitle() {
        // Get title of the movie
        String title = mMovie.getTitle();
        // Set title to the TextView
        mTitleTextView.setText(title);
    }

    /**
     * Get the release date from the {@link Movie} and display the release year. This method is
     * called as soon as the loading indicator is gone.
     */
    private void showReleaseYear() {
        // Get the release date of the movie (e.g. "2018-06-20")
        String releaseDate = mMovie.getReleaseDate();
        // Get the release year (e.g. "2018")
        String releaseYear = releaseDate.substring(RELEASE_YEAR_BEGIN_INDEX, RELEASE_YEAR_END_INDEX);
        // Set the release year to the TextView
        mReleaseYearTextView.setText(releaseYear);
    }

    /**
     * Show the title in the app bar when a CollapsingToolbarLayout is fully collapsed, otherwise hide the title.
     *
     * Reference: @see "https://stackoverflow.com/questions/31662416/show-collapsingtoolbarlayout-title-only-when-collapsed"
     */
    private void setCollapsingToolbarTitle() {
        // Set onOffsetChangedListener to determine when CollapsingToolbar is collapsed
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    // Show title when a CollapsingToolbarLayout is fully collapse
                    mCollapsingToolbar.setTitle(mMovie.getTitle());
                    isShow = true;
                } else if (isShow) {
                    // Otherwise hide the title
                    mCollapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Define the behavior for onInformationSelected
     * @param movieDetails The movie details contains information, such as budget, genre, runtime,
     *                    revenue, status, vote count, credits.
     */
    @Override
    public void onInformationSelected(MovieDetails movieDetails) {
        // Hide the loading indicator
        mDetailLoadingIndicator.setVisibility(View.GONE);

        // As soon as the loading indicator is gone, show release year
        showReleaseYear();

        // Get the runtime of the movie from MovieDetails object
        int runtime = movieDetails.getRuntime();
        // Convert Minutes to Hours and Minutes (e.g. "118" -> "1h 58m") and set the runtime to the TextView
        mRuntimeTextView.setText(FormatUtils.formatTime(this, runtime));

        // Get the genre of the movie from MovieDetails
        List<Genre> genres = movieDetails.getGenres();
        // Create an empty arrayList
        List<String> genresStrList = new ArrayList<>();
        // Iterate through the list of genres, and add genre name to the list of strings
        for (int i = 0; i < genres.size(); i++) {
            Genre genre = genres.get(i);
            // Get the genre name from the genre at ith position
            String genreName = genre.getGenreName();
            // Add genre name to the list of strings
            genresStrList.add(genreName);
        }
        // Join a string using a delimiter
        String genreStr = TextUtils.join(getString(R.string.delimiter_comma), genresStrList);
        // Display the genre
        mGenreTextView.setText(genreStr);

        // Create a new CastFragment
        CastFragment castFragment = newInstance(movieDetails);

        // Add the fragment to its container using a FragmentManager and a Transaction
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.cast_container, castFragment);

        // Allows the commit to be executed after an activity's state is saved
        // to avoid IllegalStateException: Can not perform this action after onSaveInstanceState.
        //@see "https://stackoverflow.com/questions/7469082/getting-exception-illegalstateexception
        // -can-not-perform-this-action-after-onsa/10261438#10261438"
        transaction.commitAllowingStateLoss();
    }

    /**
     * Returns {@link CastFragment} to display all of the cast members for the selected movie.
     *
     * @param movieDetails The MovieDetails object that contains budget, genre, runtime,
     *                    revenue, status, vote count, credits of the movie.
     */
    public static CastFragment newInstance(MovieDetails movieDetails) {
        // Create a new CastFragment
        CastFragment castFragment = new CastFragment();

        // Pass MovieDetails object from DetailActivity to CastFragment
        Bundle args = new Bundle();
        castFragment.setArguments(args);
        args.putParcelable(EXTRA_MOVIE_DETAILS, movieDetails);

        return castFragment;
    }

    /**
     * When the arrow icon in the app bar is clicked, finishes DetailActivity.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        menu.findItem(R.id.action_share).setIntent(createShareIntent());
        return true;
    }

    /**
     * Uses the ShareCompat Intent builder to create our share intent for sharing.
     * Return the newly created intent.
     *
     * @return The Intent to use to start our share.
     */
    private Intent createShareIntent() {
        // Text message to share
        String shareText = mMovie.getTitle() + getString(R.string.new_line)
                + SHARE_URL + mMovie.getId();

        // Create share intent
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType(SHARE_INTENT_TYPE_TEXT)
                .setText(shareText)
                .setChooserTitle(getString(R.string.chooser_title))
                .createChooserIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String resultRuntime = mRuntimeTextView.getText().toString();
        outState.putString(RESULTS_RUNTIME, resultRuntime);

        String resultReleaseYear = mReleaseYearTextView.getText().toString();
        outState.putString(RESULTS_RELEASE_YEAR, resultReleaseYear);

        String resultGenre = mGenreTextView.getText().toString();
        outState.putString(RESULTS_GENRE, resultGenre);
    }
}
