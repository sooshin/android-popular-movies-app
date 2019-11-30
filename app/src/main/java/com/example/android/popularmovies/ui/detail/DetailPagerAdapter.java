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

package com.example.android.popularmovies.ui.detail;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.android.popularmovies.ui.cast.CastFragment;
import com.example.android.popularmovies.ui.info.InformationFragment;
import com.example.android.popularmovies.ui.review.ReviewFragment;
import com.example.android.popularmovies.ui.trailer.TrailerFragment;
import com.example.android.popularmovies.utilities.Constant;

import static com.example.android.popularmovies.utilities.Constant.CAST;
import static com.example.android.popularmovies.utilities.Constant.INFORMATION;
import static com.example.android.popularmovies.utilities.Constant.REVIEWS;
import static com.example.android.popularmovies.utilities.Constant.TRAILERS;

/**
 * The DetailPagerAdapter provides the appropriate {@link Fragment} for a view pager.
 */
public class DetailPagerAdapter extends FragmentPagerAdapter {

    /** Context of the app */
    private Context mContext;

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
            case TRAILERS:
                return new TrailerFragment();
            case CAST:
                return new CastFragment();
            case REVIEWS:
                return new ReviewFragment();
        }
        return null;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return Constant.PAGE_COUNT;
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
        return Constant.TAP_TITLE[position % Constant.PAGE_COUNT].toUpperCase();
    }
}
