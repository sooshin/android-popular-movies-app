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

package com.example.android.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import com.example.android.popularmovies.R;

public class MoviePreferences {

    /**
     * Returns the sort criteria currently set in Preferences. The default sort criteria this method
     * will return is "popular".
     *
     * @param context Context used to get the SharedPreferences
     * @return Sort Criteria The current user has set in SharedPreferences. Will default to
     * "popular" if SharedPreferences have not been implemented yet.
     */
    public static String getPreferredSortCriteria(Context context) {
        // Get all of the values from shared preferences to set it up
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // String for the key
        String keyForSortBy = context.getString(R.string.pref_sort_by_key);
        // String for the default value
        String defaultSortBy = context.getString(R.string.pref_sort_by_default);
        return prefs.getString(keyForSortBy, defaultSortBy);
    }

}
