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

package com.example.android.popularmovies.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activity.DetailActivity;
import com.example.android.popularmovies.model.Cast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link CastAdapter} exposes a list of casts to a {@link android.support.v7.widget.RecyclerView}
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    /** The image file size to build the complete url that is necessary for fetching the image*/
    private static final String IMAGE_FILE_SIZE = "w185";

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
     * @return A new CastViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cast_list_item, viewGroup, false);
        return new CastViewHolder(view);
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
        /** Get a reference to the ImageView for showing profile image */
        @BindView(R.id.iv_cast) ImageView mCastImageView;
        /** Get a reference to the TextView for showing the cast name */
        @BindView(R.id.tv_cast_name) TextView mCastNameTextView;
        /** Get a reference to the TextView for showing the character name */
        @BindView(R.id.tv_cast_character) TextView mCharacterTextView;

        /**
         * Constructor for CastViewHolder.
         *
         * @param itemView The View that you inflated in {@link CastAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        CastViewHolder(View itemView) {
            super(itemView);

            // Bind the view using ButterKnife
            ButterKnife.bind(this, itemView);
        }

        /**
         * This method will take an Cast object as input and use that cast to display the appropriate
         * text within a list item.
         *
         * @param cast The cast object
         */
         void bind(Cast cast) {
            // The complete profile image url
            String profile = DetailActivity.IMAGE_BASE_URL + IMAGE_FILE_SIZE + cast.getProfilePath();
            // Load image with Picasso library
            Picasso.with(itemView.getContext())
                    .load(profile)
                    // Create circular avatars
                    // Reference: @see "https://stackoverflow.com/questions/26112150/android-create
                    // -circular-image-with-picasso"
                    .into(mCastImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) mCastImageView.getDrawable())
                                    .getBitmap();
                            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(
                                    itemView.getContext().getResources(), // to determine density
                                    imageBitmap); // image to round
                            drawable.setCircular(true);
                            mCastImageView.setImageDrawable(drawable);
                        }

                        @Override
                        public void onError() {
                            mCastImageView.setImageResource(R.drawable.account_circle);
                        }
                    });
            // Set the cast name to the TextView
            mCastNameTextView.setText(cast.getName());
            // Set the character name to the TextView
            mCharacterTextView.setText(cast.getCharacter());

        }
    }
}
