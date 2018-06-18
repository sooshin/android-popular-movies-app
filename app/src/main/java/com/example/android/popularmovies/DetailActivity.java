package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    // Extra for the movie to be received in the intent
    public static final String EXTRA_MOVIE = "movie";

    /** The base image URL to build the complete url that is necessary for fetching the image */
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";

    /** The image file size to build the complete url that is necessary for fetching the image*/
    private static final String BACKDROP_FILE_SIZE ="w500";

    /** ImageView for the backdrop image */
    @BindView(R.id.iv_backdrop)
    ImageView mBackdropImageView;

    /** Get a reference to the ViewPager that will allow the user to swipe between fragments */
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    /** Get a reference to the TabLayout */
    @BindView(R.id.sliding_tabs)
    TabLayout mTabLayout;

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

    /** Movie object */
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        // Give the TabLayout the ViewPager
        mTabLayout.setupWithViewPager(mViewPager);
        // Set gravity for the TabLayout
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Create an adapter that knows which fragment should be shown on each page
        DetailPagerAdapter pagerAdapter = new DetailPagerAdapter(
                this, getSupportFragmentManager());
        // Set the adapter onto the ViewPager
        mViewPager.setAdapter(pagerAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                mMovie = intent.getParcelableExtra(EXTRA_MOVIE);
            }
        }

        // Show the title in the app bar when a CollapsingToolbarLayout is fully collapsed
        setCollapsingToolbarTitle();
        loadBackdropImage();
        setOriginalTitle();
    }

    private void loadBackdropImage() {
        String backdropPath = mMovie.getBackdropPath();
        String backdrop = IMAGE_BASE_URL + BACKDROP_FILE_SIZE + backdropPath;
        Picasso.with(this)
                .load(backdrop)
                .placeholder(R.drawable.photo)
                .into(mBackdropImageView);
    }

    private void setOriginalTitle() {
        String title = mMovie.getTitle();
        mTitleTextView.setText(title);
    }

    /**
     * Show the title in the app bar when a CollapsingToolbarLayout is fully collapsed, otherwise hide the title.
     *
     * reference [https://stackoverflow.com/questions/31662416/show-collapsingtoolbarlayout-title-only-when-collapsed]
     */
    private void setCollapsingToolbarTitle() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbar.setTitle(mMovie.getTitle());
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
}
