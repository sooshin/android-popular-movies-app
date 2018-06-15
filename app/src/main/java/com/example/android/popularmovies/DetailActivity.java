package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class DetailActivity extends AppCompatActivity {

    // Extra for the movie to be received in the intent
    public static final String EXTRA_MOVIE = "movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get a reference to the ViewPager that will allow the user to swipe between fragments.
        // Use findViewById as ButterKnife causes an error.
        ViewPager viewPager = findViewById(R.id.viewpager);
        // Get a reference to the TabLayout
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);
        // Set gravity for the TabLayout
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Create an adapter that knows which fragment should be shown on each page
        DetailPagerAdapter pagerAdapter = new DetailPagerAdapter(
                this, getSupportFragmentManager());
        // Set the adapter onto the ViewPager
        viewPager.setAdapter(pagerAdapter);


    }

}
