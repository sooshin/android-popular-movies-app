package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.popularmovies.fragment.CastFragment;
import com.example.android.popularmovies.fragment.InformationFragment;

public class DetailPagerAdapter extends FragmentPagerAdapter {

    /** Constant value for each fragment */
    private static final int INFORMATION = 0;
    private static final int CAST = 1;

    /** Context of the app */
    private Context mContext;



    private static final String[] TAP_TITLE = new String[] {
            "Info", "Cast"
    };

    private static final int PAGE_COUNT = TAP_TITLE.length;


    /**
     * Creates a new {@link DetailPagerAdapter} object
     *
     * @param context The context of the app
     * @param fm The fragment manager that will keep each fragment's state in the adapter across swipes
     */
    public DetailPagerAdapter(Context context, FragmentManager fm){
        super(fm);
        mContext = context;
    }

    /**
     * Return the {@link Fragment} that should be displayed for the given page number
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case INFORMATION:
                return new InformationFragment();
            case CAST:
                return new CastFragment();
        }
        return null;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    /**
     * Return a title string to describe the specified page.
     *
     * @param position The position of the title requested
     * @return A title of the requested page
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAP_TITLE[position % PAGE_COUNT].toUpperCase();
    }
}
