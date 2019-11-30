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

package com.example.android.popularmovies.ui.cast;

import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.CastListItemBinding;
import com.example.android.popularmovies.model.Cast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.popularmovies.utilities.Constant.IMAGE_BASE_URL;
import static com.example.android.popularmovies.utilities.Constant.IMAGE_FILE_SIZE;

/**
 * {@link CastAdapter} exposes a list of casts to a {@link RecyclerView}
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    /** Member variable for the list of {@link Cast}s */
    private List<Cast> mCasts;

    /**
     * Constructor for CastAdapter that accepts a list of casts to display
     *
     * @param casts The list of {@link Cast}s
     */
    public CastAdapter(List<Cast> casts) {
        mCasts = casts;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout.
     * @return A new CastViewHolder that holds the CastListItemBinding
     */
    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        CastListItemBinding castItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.cast_list_item, viewGroup, false);
        return new CastViewHolder(castItemBinding);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Cast cast = mCasts.get(position);
        holder.bind(cast);
    }

    /**
     * This method simply return the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of casts
     */
    @Override
    public int getItemCount() {
        if (null == mCasts) return 0;
        return mCasts.size();
    }

    /**
     * This method is to add a list of {@link Cast}s
     *
     * @param casts the data source of the adapter
     */
    public void addAll(List<Cast> casts) {
        mCasts.clear();
        mCasts.addAll(casts);
        notifyDataSetChanged();
    }


    /**
     * Cache of the children views for a cast list item.
     */
    public class CastViewHolder extends RecyclerView.ViewHolder {
        /** This field is used for data binding */
        CastListItemBinding mCastItemBinding;

        /**
         * Constructor for CastViewHolder.
         *
         * @param castItemBinding Used to access the layout's variables and views
         */
        CastViewHolder(CastListItemBinding castItemBinding) {
            super(castItemBinding.getRoot());
            mCastItemBinding = castItemBinding;
        }

        /**
         * This method will take a Cast object as input and use that cast to display the appropriate
         * text and an image within a list item.
         *
         * @param cast The cast object
         */
         void bind(Cast cast) {
            // The complete profile image url
            String profile = IMAGE_BASE_URL + IMAGE_FILE_SIZE + cast.getProfilePath();
            // Load image with Picasso library
            Picasso.with(itemView.getContext())
                    .load(profile)
                    // Create circular avatars
                    // Reference: @see "https://stackoverflow.com/questions/26112150/android-create
                    // -circular-image-with-picasso"
                    .into(mCastItemBinding.ivCast, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) mCastItemBinding.ivCast.getDrawable())
                                    .getBitmap();
                            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(
                                    itemView.getContext().getResources(), // to determine density
                                    imageBitmap); // image to round
                            drawable.setCircular(true);
                            mCastItemBinding.ivCast.setImageDrawable(drawable);
                        }

                        @Override
                        public void onError() {
                            mCastItemBinding.ivCast.setImageResource(R.drawable.account_circle);
                        }
                    });

            // Set the cast name and character name to the TextViews
            mCastItemBinding.setCast(cast);
        }
    }
}
